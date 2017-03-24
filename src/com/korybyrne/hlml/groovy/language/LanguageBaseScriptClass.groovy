package com.korybyrne.hlml.groovy.language

import com.korybyrne.hlml.groovy.voicing.Voice

abstract class LanguageBaseScriptClass extends Script implements LanguageConstants {
    private static final List<String> BLACK_LIST = []

    //TODO: Figure out why this one is breaking when capitalized
    def random(Map lowHigh) {
        return this.binding.ruleHandler.RANDOM(lowHigh.low, lowHigh.high)
    }

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

    ////// GETTERS ///////

    //TODO: Fix this methodMissing
//    def methodMissing(String name, args) {
//        if (name in BLACK_LIST) {
//            return null
//        } else {
//            def temp = this.binding.ruleHandler."$name"
//            return temp
//        }
//    }

    def getTwoVoicePairs() {
        return [
            this.binding.ruleHandler.BT, this.binding.ruleHandler.BA, this.binding.ruleHandler.BS,
            this.binding.ruleHandler.TA, this.binding.ruleHandler.TS, this.binding.ruleHandler.AS
        ]
    }

    //TODO: Error handling
    def propertyMissing(String name) {
        if (name in BLACK_LIST) {
            return null
        } else {
            return this.binding.ruleHandler."$name"
        }
    }
}
