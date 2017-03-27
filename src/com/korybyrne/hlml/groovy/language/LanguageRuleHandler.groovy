package com.korybyrne.hlml.groovy.language

import com.korybyrne.hlml.Globals
import com.korybyrne.hlml.groovy.chord.Chord
import com.korybyrne.hlml.groovy.chord.ChordProgression
import com.korybyrne.hlml.groovy.voicing.Voice
import com.korybyrne.hlml.groovy.voicing.Voicing
import com.korybyrne.hlml.groovy.voicing.VoicingProgression
import com.korybyrne.hlml.groovy.voicing.Voicings
import jm.constants.Scales
import org.codehaus.groovy.control.CompilerConfiguration

//TODO: Have this extend from different modules for better abstraction
//      Perhaps it would use traits?

class LanguageRuleHandler {
    private static LanguageRuleHandler instance

    Voicing prevVoicing, currVoicing

    private List<Voicing> workingVoicings = []
    private int frontierSize = 0

    private Binding binding
    private CompilerConfiguration compilerConfiguration
    private GroovyShell shell

    private List voiceMotion
    private List scanners
    private ChordProgression chordProgression
    private VoicingProgression voicingProgression
    private int currentChord
    private List falseBitmasks = [
            0x00000001,
            0x00000002,
            0x00000004,
            0x00000008
    ]

    /////////// SINGLETON ///////////////

    private LanguageRuleHandler() {
        binding = new Binding([
                ruleHandler   : this,

                intervals     : this.intervals,
                motion        : this.motion,
                intervalMotion: this.intervalMotion,
                inversion     : this.inversion,
                members       : this.members
        ])
        compilerConfiguration = new CompilerConfiguration()
        compilerConfiguration.scriptBaseClass = LanguageBaseScriptClass.name

        shell = new GroovyShell(binding, compilerConfiguration)
    }

    static LanguageRuleHandler getInstance() {
        if (instance == null) {
            instance = new LanguageRuleHandler()
        }

        return instance
    }

    /////////// GENERATORS //////////////

    def RANDOM(int low, int high) {
        return [
                ON: { parts ->
                    workingVoicings.each { Voicing workingVoicing ->
                        parts.each {
                            workingVoicing[it] = low + Globals.RANDOM.nextInt(high - low + 1)
                        }
                    }

                    doScan()
                }
        ]
    }

    def MOVE(Voice voice) {
        return [
                TO: { note ->
                    workingVoicings.each { Voicing workingVoicing ->
                        voice.setPitch(note as Integer)
                    }

                    doScan()
                }
        ]
    }

    def MOVE(note) {
        return [
                TO: { outNote ->
                    workingVoicings.each { Voicing workingVoicing ->
                        prevVoicing.getVoices().eachWithIndex { Voice entry, int i ->
                            if (entry.getPitch() == (note as Integer)) {
                                workingVoicing[i] = outNote
                            }
                        }
                    }

                    doScan()
                }
        ]
    }

    //NOTE: If it can't be in the range it will go low.
    def RESOLVE(Voicings.Part part) {
        return [
                TO: { target ->
                    return [
                            WITHIN: { low, high ->
                                workingVoicings.each { Voicing workingVoicing ->
                                    Voice prevVoice = prevVoicing[part.id]
                                    int offset = Voicing.getPitchClassDistance(prevVoice as Integer, target as Integer)
                                    int newPitch = prevVoice.pitch + offset

                                    while (newPitch < low) {
                                        newPitch += 12
                                    }

                                    while (newPitch > high) {
                                        newPitch -= 12
                                    }

                                    workingVoicing[part.id].pitch = newPitch
                                }

                                doScan()
                            }
                    ]
                }
        ]
    }

    def RESOLVE(ScaleDegree sd) {
        return [
                TO: { target ->
                    target = (target in ScaleDegree) ? target.toScale(Scales.MAJOR_SCALE) : target as Integer
                    this.RESOLVE sd.toScale(Scales.MAJOR_SCALE) TO target
                }
        ]
    }

    def RESOLVE(pc) {
        return [
                TO: { target ->
                    workingVoicings.each { Voicing workingVoicing ->
                        prevVoicing.getVoices().eachWithIndex { Voice voice, int i ->
//                    println "$pc, $voice.pitch, ${voice.pitch % 12}"
                            if (voice.getPitchClass() == (pc as Integer)) {
                                int offset = Voicing.getPitchClassDistance(pc as Integer, target as Integer)
                                workingVoicing[i] = voice.getPitch() + offset
                            }
                        }
                    }

                    doScan()
                }
        ]
    }

    // TODO: Add any index to index of next chord
    def ROTATE(int direction) {
        workingVoicings.each { Voicing workingVoicing ->
            def newIndices = prevVoicing.getIntervalIndicesFromRoot().collect({
                Math.floorMod(it + direction, prevVoicing.voices.size() - 1)
            })[1..-1]

            newIndices.eachWithIndex { int index, int i ->
                workingVoicing[i + 1] = prevVoicing[i + 1] + Voicing.getPitchClassDistance(
                        prevVoicing[i + 1] as Integer,
                        workingVoicing.root + workingVoicing.intervals[index]
                )
            }
        }

        doScan()
    }

    def DISTANCE(int nth = 0) {
        workingVoicings.each { Voicing workingVoicing ->
            prevVoicing.getVoices().eachWithIndex { Voice voice, int i ->
                workingVoicing[i] = voice.getPitch() +
                        workingVoicing.getPitchClasses(12).collect({
                            Voicing.getPitchClassDistance(voice.getPitchClass(), it)
                        }).sort({ Math.abs(it) })[nth]
            }
        }

        doScan()
    }

    /////////// SCANNERS ///////////////

    private List calculateMotion(Voicing workingVoicing) {
        voiceMotion = []

        workingVoicing.voices.eachWithIndex { Voice entry, int i ->
            if (entry) {
                voiceMotion.add(Voicing.getPitchClassDistance(prevVoicing[i] as Integer, entry as Integer))
            } else {
                voiceMotion.add(null)
            }
        }

        return voiceMotion
    }

    //TODO: Make this input good qualities like the other scanners
    Closure<List<Boolean>> motion = { List qualities, List parts, Voicing workingVoicing ->
        if (parts.size() <= 1) {
            print parts
            println " is bad!"
            return false
        }

        def confirmations = [true] * workingVoicing.voices.size()
        calculateMotion()

        parts.each {
            def qualityPair = [voiceMotion[parts[0]], voiceMotion[it]]
            if (qualityPair in qualities) {
                confirmations[it] = false
            }
        }

        return confirmations
    }

    Closure<List<Boolean>> intervals = { List intervals, List parts, Voicing workingVoicing ->
        def intervalsFromRoot = workingVoicing.calculateIntervals()
        def confirmations = [true] * workingVoicing.voices.size()

        parts.each {
            if (workingVoicing[it].pitch != 0 && !(intervalsFromRoot[it] in intervals)) {
                confirmations[it] = false
            }
        }

        return confirmations
    }

    Closure<List<Boolean>> intervalMotion = { List qualities, List parts, Voicing workingVoicing ->
        if (parts.size() <= 1) {
            print parts
            println " is bad!"
            return false
        }

        def confirmations = [true] * workingVoicing.voices.size()
        def oldIntervals = []
        def newIntervals = []

        for (int i = 1; i < parts.size(); ++i) {
            def prevPart = parts[i - 1]
            def part = parts[i]
            oldIntervals.add(Voicing.getPositivePitchClassDistance(
                    prevVoicing[prevPart].pitch, prevVoicing[part].pitch
            ))

            newIntervals.add(Voicing.getPositivePitchClassDistance(
                    workingVoicing[prevPart].pitch, workingVoicing[part].pitch
            ))
        }

        for (int i = 0; i < oldIntervals.size(); ++i) {
            def pair = [oldIntervals[i], newIntervals[i]]
            if (pair in qualities) {
                confirmations[parts[0]] = false
                confirmations[parts[i + 1]] = false
            }
        }

        return confirmations
    }

    //TODO: Better name than inversion
    Closure<List<Boolean>> inversion = { List inversions, List parts, Voicing workingVoicing ->
        def inversion = workingVoicing.getIntervalIndicesFromRoot()
        def confirmations = [true] * workingVoicing.voices.size()

        if (!(inversion in inversions)) {
            parts.each {
                confirmations[it] = false
            }
        }

        return confirmations
    }

    Closure<List<Boolean>> members = { List members, List parts, Voicing workingVoicing ->
        def confirmations = [true] * workingVoicing.voices.size()
        def inversion = workingVoicing.getIntervalIndicesFromRoot()
        def memberNumbers = [:]
        def workingMemberNumbers = [:]

        members.each {
            memberNumbers[it] = (memberNumbers[it] ?: 0) + 1
        }

        parts.each {
            if (workingVoicing[it]) {
                def member = inversion[it]

                if (workingVoicing[it] != 0) {
                    workingMemberNumbers[member] = (workingMemberNumbers[member] ?: 0) + 1
                }
            }
        }

        parts.each {
            if (workingVoicing[it]) {
                def member = inversion[it]

                if (workingVoicing[it] != 0) {
                    if (workingMemberNumbers[member] > memberNumbers[member]) {
                        confirmations[it] = false
                    }
                }
            }
        }

//        println workingMemberNumbers
//        println prevVoicing
//        println workingVoicing
//        println currVoicing
//        println confirmations
        return confirmations
    }

    // scan <action> for <quality> on <voices> (could be parts, perhaps other things? ex: no thirds of elements should move in oblique?)
    Map SCAN(scanner) {
        return [
                FOR: { quality ->
                    return [
                            ON: { parts ->
                                scanners.add({ Voicing workingVoicing ->
                                    return scanner(quality, parts, workingVoicing)
                                })
                            }
                    ]
                }
        ]
    }

    private boolean doScan() {
        def finalConfirmations = [true] * 4
        def confirmations = []
        frontierSize = workingVoicings.size()
        Voicing workingVoicing

        for (int i = 0; i < frontierSize; ++i) {
            def falseIndices = []
            def maxCount
            workingVoicing = workingVoicings.first()
            workingVoicings.removeAt(0)

            if (workingVoicing.isFinalized()) {
                println "Terminating: $workingVoicing"
                throw new EarlyTerminationException(workingVoicing)
            }

            for (Closure scanner : scanners) {
                confirmations = scanner.call(workingVoicing)
//            println confirmations
                confirmations.eachWithIndex { boolean entry, int idx ->
                    if (!entry && !currVoicing[idx].finalized) {
                        finalConfirmations[idx] = false
                    }
                }
            }

            finalConfirmations.eachWithIndex { boolean entry, int idx ->
                if (entry) {
                    if (workingVoicing[idx].pitch != 0) {
                        workingVoicing[idx].finalized = true
                    }
                } else {
                    falseIndices.add(idx)
                    workingVoicing[idx].unlock()
                    workingVoicing[idx] = 0
                    workingVoicing[idx].unlock()
                }
            }

            maxCount = 2 ** falseIndices.size() - 1
            println "Max: $maxCount"
            if (maxCount == 0) {
                workingVoicings.push(workingVoicing)
            } else {
                for (int j = 1; j <= maxCount; ++j) {
                    Voicing voicing = new Voicing(workingVoicing)

                    falseIndices.each {
                        if ( (j & falseBitmasks[it]) > 0) {
                            workingVoicing[it].unlock()
                            workingVoicing[it] = 0
                            workingVoicing[it].unlock()
                        }
                    }

                    workingVoicings.push(voicing)
                }
            }
        }

        println workingVoicings
        return confirmations
    }

    ////////// HELPERS /////////////////

    int getRootMotion() {
        return Voicing.getPositivePitchClassDistance(currVoicing.root, prevVoicing.root)
    }

    List<Integer> getCurrentIntervals() {
        return currVoicing.getIntervals()
    }

    int getCurrentRoot() {
        return currVoicing.root
    }

    ////////// WORKFLOWS ///////////////

    def init(ChordProgression progression) {
        voiceMotion = []
        scanners = []
        frontierSize = 0
        currentChord = 0

        chordProgression = progression
        prevVoicing = new Voicing(chordProgression[currentChord]).setNumVoices(4).voiceRandom()
        workingVoicings = []
        currVoicing = null
        voicingProgression = new VoicingProgression() + prevVoicing
    }

    VoicingProgression voice(String rulesFilename) {
        File rulesFile = new File(rulesFilename)

        println '//////////////// FILE START ///////////////'
        println rulesFile.text
        println '//////////////// FILE END /////////////////'

        for (Chord chord : chordProgression.elements[1..-1]) {
            currVoicing = new Voicing(chord).setNumVoices(4)
            workingVoicings = [new Voicing(currVoicing)]

            try {
                shell.evaluate rulesFile
                currVoicing = workingVoicings[0]

            } catch (EarlyTerminationException e) {
                println "early termination!"
                currVoicing = e.voicing
            }
            voicingProgression + confirm()
        }

        return voicingProgression
    }

    Voicing confirm() {
        prevVoicing = new Voicing(currVoicing)
        workingVoicings = []
        voiceMotion = []
        scanners = []

        return currVoicing
    }

    //////// GROOVY GOODNESS /////////

    Voicings.Part getSoprano() {
        return Voicings.Part.SOPRANO
    }

    Voicings.Part getAlto() {
        return Voicings.Part.ALTO
    }

    Voicings.Part getTenor() {
        return Voicings.Part.TENOR
    }

    Voicings.Part getBass() {
        return Voicings.Part.BASS
    }

    def propertyMissing(String name) {
        switch (name) {
            default:
                return name.collect { Voicings.ABBREVIATED[it.toLowerCase()] ?: 0 }
        }
    }
}
