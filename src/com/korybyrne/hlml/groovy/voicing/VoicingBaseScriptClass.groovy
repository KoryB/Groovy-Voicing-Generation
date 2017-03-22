package com.korybyrne.hlml.groovy.voicing

abstract class VoicingBaseScriptClass extends Script {
    private static final List<String> BLACK_LIST = []

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

    //TODO: Error handling
    def propertyMissing(String name) {
        if (name in BLACK_LIST) {
            return null
        } else {
            return this.binding.ruleHandler."$name"
        }
    }
}
