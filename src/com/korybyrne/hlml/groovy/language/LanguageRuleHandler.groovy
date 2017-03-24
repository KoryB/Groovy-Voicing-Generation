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

    Voicing prevVoicing, workingVoicing, currVoicing
    Binding binding
    CompilerConfiguration compilerConfiguration
    GroovyShell shell

    private List voiceMotion
    private List scanners
    private ChordProgression chordProgression
    private VoicingProgression voicingProgression
    private int currentChord

    /////////// SINGLETON ///////////////

    private LanguageRuleHandler() {
        binding = new Binding([
                ruleHandler:    this,

                intervals:      this.intervals,
                motion:         this.motion,
                intervalMotion: this.intervalMotion,
                inversion:      this.inversion,
                members:         this.members
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
            ON: { parts->
                parts.each {
                    workingVoicing[it] = low + Globals.RANDOM.nextInt(high - low + 1)
                }

                doScan()
            }
        ]
    }

    def MOVE(Voice voice) {
        return [
            TO: {note ->
                voice.setPitch(note as Integer)

                doScan()
            }
        ]
    }

    def MOVE(note) {
        return [
            TO: { outNote ->
                prevVoicing.getVoices().eachWithIndex { Voice entry, int i ->
                    if (entry.getPitch() == (note as Integer)) {
                        workingVoicing[i] = outNote
                    }
                }

                doScan()
            }
        ]
    }

    //NOTE: If it can't be in the range it will go low.
    def RESOLVE(Voice voice) {
        return [
            TO: { target ->
                return [
                    WITHIN: { low, high ->
                        Voice prevVoice = prevVoicing[voice.part]
                        int offset = Voicing.getPitchClassDistance(prevVoice as Integer, target as Integer)
                        int newPitch = prevVoice.pitch + offset

                        while (newPitch < low) {
                            newPitch += 12
                        }

                        while (newPitch > high) {
                            newPitch -= 12
                        }

                        voice.pitch = newPitch

                        doScan()
                    }
                ]
            }
        ]
    }

    def RESOLVE(ScaleDegree sd) {
        return [
            TO: { target ->
                target = (target in ScaleDegree)? target.toScale(Scales.MAJOR_SCALE) : target as Integer
                this.RESOLVE sd.toScale(Scales.MAJOR_SCALE) TO target
            }
        ]
    }

    def RESOLVE(pc) {
        return [
            TO: { target ->
                prevVoicing.getVoices().eachWithIndex { Voice voice, int i ->
//                    println "$pc, $voice.pitch, ${voice.pitch % 12}"
                    if (voice.getPitchClass() == (pc as Integer)) {
                        int offset = Voicing.getPitchClassDistance(pc as Integer, target as Integer)
                        workingVoicing[i] = voice.getPitch() + offset
                    }
                }

                doScan()
            }
        ]
    }

    // TODO: Add any index to index of next chord
    def ROTATE(int direction) {
        def newIndices = prevVoicing.getIntervalIndicesFromRoot().collect({
            Math.floorMod(it + direction, prevVoicing.voices.size()-1)
        })[1..-1]

        newIndices.eachWithIndex { int index, int i ->
            workingVoicing[i+1] = prevVoicing[i+1] + Voicing.getPitchClassDistance(
                    prevVoicing[i+1] as Integer,
                    workingVoicing.root + workingVoicing.intervals[index]
            )
        }

        doScan()
    }

    def DISTANCE(int nth = 0) {
        prevVoicing.getVoices().eachWithIndex { Voice voice, int i ->
            workingVoicing[i] = voice.getPitch() +
                workingVoicing.getPitchClasses(12).collect({
                    Voicing.getPitchClassDistance(voice.getPitchClass(), it)
                }).sort({Math.abs(it)})[nth]
        }

        doScan()
    }

    /////////// SCANNERS ///////////////

    private List calculateMotion() {
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

    private boolean doScan() {
        def confirmations

        for (Closure scanner : scanners) {
            confirmations = scanner.call()
            println confirmations
            confirmations.eachWithIndex { boolean entry, int i ->
                if (!entry && !currVoicing[i].locked) {
                    workingVoicing[i].unlock()
                    workingVoicing[i] = currVoicing[i].getPitch()
                    workingVoicing[i].unlock()
                }
            }
        }

        currVoicing = new Voicing(workingVoicing)

//        if (confirm) {
//            currVoicing = new Voicing(workingVoicing)
//        } else {
////            println "Failed! Before: ${workingVoicing.voices.collect {[it.pitch, it.locked]} }"
//            workingVoicing = new Voicing(currVoicing)
////            println "Failed!  After: ${workingVoicing.voices.collect {[it.pitch, it.locked]} }"
//        }

        return finalConfirmations
    }

    //TODO: Make this input good qualities like the other scanners
    Closure<List<Boolean>> motion = {List qualities, List parts ->
        if (parts.size() <= 1) {
            print parts
            println " is bad!"
            return false
        }

        def confirmations = [true] * workingVoicing.voices.size()
        calculateMotion()

        parts.each {
            def qualityPair = [voiceMotion[parts[0]], voiceMotion[it]]
            if ( qualityPair in qualities ) {
                confirmations[it] = false
            }
        }

        return confirmations
    }

    Closure<List<Boolean>> intervals = {List intervals, List parts ->
        def intervalsFromRoot = workingVoicing.calculateIntervals()
        def confirmations = [true] * workingVoicing.voices.size()

        parts.each {
            if ( workingVoicing[it].pitch != 0 && !(intervalsFromRoot[it] in intervals) ) {
                confirmations[it] = false
            }
        }

        return confirmations
    }

    Closure<List<Boolean>> intervalMotion = {List qualities, List parts ->
        if (parts.size() <= 1) {
            print parts
            println " is bad!"
            return false
        }

        def confirmations = [true] * workingVoicing.voices.size()
        def oldIntervals = []
        def newIntervals = []

        for (int i = 1; i < parts.size(); ++i) {
            def prevPart = parts[i-1]
            def part = parts[i]
            oldIntervals.add(Voicing.getPositivePitchClassDistance(
                    prevVoicing[prevPart].pitch, prevVoicing[part].pitch
            ))

            newIntervals.add(Voicing.getPositivePitchClassDistance(
                    workingVoicing[prevPart].pitch, workingVoicing[part].pitch
            ))
        }

        for (int i = 0; i < oldIntervals.size(); ++i) {
            if ([oldIntervals[i], newIntervals[i]] in qualities) {
                confirmations[parts[0]] = false
                confirmations[parts[i+1]] = false
            }
        }

        return confirmations
    }

    //TODO: Better name than inversion
    Closure<List<Boolean>> inversion = {List inversions, List parts ->
        def inversion = workingVoicing.getIntervalIndicesFromRoot()
        def confirmations = [true] * workingVoicing.voices.size()

        if (! (inversion in inversions)) {
            parts.each {
                confirmations[it] = false
            }
        }

        return confirmations
    }

    Closure<List<Boolean>> members = {List members, List parts ->
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
                    if (workingMemberNumbers[member] < memberNumbers[member]) {
                        workingMemberNumbers[member] = (workingMemberNumbers[member] ?: 0) + 1
                    } else {
                        confirmations[it] = false
                    }
                }
            }
        }

        println workingMemberNumbers
        return confirmations
    }

    // scan <action> for <quality> on <voices> (could be parts, perhaps other things? ex: no thirds of elements should move in oblique?)
    Map SCAN(scanner) {
        return [
            FOR: {quality ->
                return [
                    ON: {parts ->
                        scanners.add( {return scanner(quality, parts)} )
                    }
                ]
            }
        ]
    }

    ////////// HELPERS /////////////////

    int getRootMotion() {
        return Voicing.getPositivePitchClassDistance(workingVoicing.root, prevVoicing.root)
    }

    List<Integer> getCurrentIntervals() {
        return workingVoicing.getIntervals()
    }

    int getCurrentRoot() {
        return workingVoicing.root
    }

    ////////// WORKFLOWS ///////////////

    def init(ChordProgression progression) {
        voiceMotion = []
        scanners = []
        chordProgression = progression
        prevVoicing = new Voicing(chordProgression[currentChord]).setNumVoices(4).voiceRandom()
        voicingProgression = new VoicingProgression() + prevVoicing
    }

    VoicingProgression voice(String rulesFilename) {
        File rulesFile = new File(rulesFilename)

        println '//////////////// FILE START ///////////////'
        println rulesFile.text
        println '//////////////// FILE END /////////////////'

        chordProgression.elements[1..-1].eachWithIndex {Chord chord, int index ->
            workingVoicing = new Voicing(chord).setNumVoices(4)
            currVoicing = new Voicing(workingVoicing)
            shell.evaluate(
                    rulesFile
            )
            voicingProgression + confirm()
        }

        return voicingProgression
    }

    Voicing confirm() {
        currVoicing = new Voicing(workingVoicing)
        prevVoicing = new Voicing(currVoicing)
        workingVoicing = null
        voiceMotion = []
        scanners = []

        return currVoicing
    }

    //////// GROOVY GOODNESS /////////

    Voice getSoprano() {
        return workingVoicing.soprano
    }

    Voice getAlto() {
        return workingVoicing.alto
    }

    Voice getTenor() {
        return workingVoicing.tenor
    }

    Voice getBass() {
        return workingVoicing.bass
    }

    def propertyMissing(String name) {
        switch (name) {
            default:
                return name.collect {Voicings.ABBREVIATED[it.toLowerCase()] ?: 0}
        }
    }
}
