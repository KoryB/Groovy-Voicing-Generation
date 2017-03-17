package com.korybyrne.hlml.groovy.note

import jm.music.data.Phrase
import jm.music.tools.PhraseAnalysis

class Line extends Phrase {

    //TODO: Optimize this
    int[] getContour() {
        return PhraseAnalysis.pitchIntervals((getNoteArray()))
    }
}
