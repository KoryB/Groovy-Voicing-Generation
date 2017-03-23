package com.korybyrne.hlml.groovy.voicing

import com.korybyrne.hlml.groovy.chord.Chord
import com.korybyrne.hlml.groovy.chord.ChordProgression
import com.korybyrne.hlml.groovy.note.Note
import jm.constants.Scales
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.runtime.DefaultGroovyMethods

//TODO: Have this extend from different modules for better abstraction
//      Perhaps it would use traits?

class VoicingRuleHandler {
    private static VoicingRuleHandler instance

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

    private VoicingRuleHandler() {
        binding = new Binding([
                ruleHandler:    this,

                intervals:      this.intervals,
                inversion:      this.inversion,
                motion:         this.motion,
        ])
        compilerConfiguration = new CompilerConfiguration()
        compilerConfiguration.scriptBaseClass = VoicingBaseScriptClass.name

        shell = new GroovyShell(binding, compilerConfiguration)
    }

    static VoicingRuleHandler getInstance() {
        if (instance == null) {
            instance = new VoicingRuleHandler()
        }

        return instance
    }

    /////////// GENERATORS //////////////

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
        def confirm = true

        for (Closure scanner : scanners) {
            if (!scanner.call()) {
                confirm = false
                break
            }
        }

        if (confirm) {
            currVoicing = new Voicing(workingVoicing)
        } else {
//            println "Failed! Before: ${workingVoicing.voices.collect {[it.pitch, it.locked]} }"
            workingVoicing = new Voicing(currVoicing)
//            println "Failed!  After: ${workingVoicing.voices.collect {[it.pitch, it.locked]} }"
        }

        return confirm
    }

    //TODO: Make this input good qualities like the other scanners
    Closure<Boolean> motion = {List qualities, List parts ->
        if (parts.size() <= 1) {
            print parts
            println " is bad!"
            return false
        }

        calculateMotion()

        for (def part : parts) {
            def qualityPair = [voiceMotion[parts[0]], voiceMotion[part]]
            if ( qualityPair in qualities ) {
                return false
            }
        }

        return true
    }

    Closure<Boolean> intervals = {List intervals, List parts ->
        def intervalsFromRoot = workingVoicing.calculateIntervals()

        for (def part : parts) {
            if ( workingVoicing[part].pitch != 0 && !(intervalsFromRoot[part] in intervals) ) {
                return false
            }
        }

        return true
    }

    //TODO: Better name than inversion
    Closure<Boolean> inversion = {List inversions, List parts ->
        def inversion = workingVoicing.getIntervalIndicesFromRoot()

        for (def part : parts) {
            if ( inversion[part] != inversions[part] ) {
                return false
            }
        }

        return true
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
