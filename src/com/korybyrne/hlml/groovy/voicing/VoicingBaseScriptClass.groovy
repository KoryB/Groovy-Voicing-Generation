package com.korybyrne.hlml.groovy.voicing

abstract class VoicingBaseScriptClass extends Script {
    def MOVE(Voice voice) {
        return this.binding.ruleHandler.MOVE(voice)
    }

    def MOVE(note) {
        return this.binding.ruleHandler.MOVE(note)
    }

    def RESOLVE(Voice voice) {
        return this.binding.ruleHandler.RESOLVE(voice)
    }

    def RESOLVE(ScaleDegree sd) {
        return this.binding.ruleHandler.RESOLVE(sd)
    }

    def RESOLVE(pc) {
        return this.binding.ruleHandler.RESOLVE(pc)
    }

    def ROTATE(int direction) {
        return this.binding.ruleHandler.ROTATE(direction)
    }

    def DISTANCE(int nth = 0) {
        return this.binding.ruleHandler.DISTANCE(nth)
    }

    Map SCAN(scanner) {
        return this.binding.ruleHandler.SCAN(scanner)
    }

    ////// GROOVY GOODNESS ///////
    
    List<Integer> getCurrentIntervals() {
        return this.binding.ruleHandler.workingVoicing.getIntervals()
    }

    int getCurrentRoot() {
        return this.binding.ruleHandler.workingVoicing.root
    }

    Voice getSoprano() {
        return this.binding.ruleHandler.workingVoicing.soprano
    }

    Voice getAlto() {
        return this.binding.ruleHandler.workingVoicing.alto
    }

    Voice getTenor() {
        return this.binding.ruleHandler.workingVoicing.tenor
    }

    Voice getBass() {
        return this.binding.ruleHandler.workingVoicing.bass
    }

    def propertyMissing(String name) {
        return this.binding.ruleHandler.propertyMissing(name)
    }
}
