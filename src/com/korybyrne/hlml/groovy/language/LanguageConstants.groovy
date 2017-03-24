package com.korybyrne.hlml.groovy.language

import com.korybyrne.hlml.groovy.chord.Chords
import jm.constants.Pitches

interface LanguageConstants extends Pitches, Chords {
    List<List<Integer>> parallelOctaves = [[0, 0]]
    List<List<Integer>> parallelPerfects = [
            [0, 0],
                    [5, 5],
                            [7, 7]
    ]

    int R = ROOT
    int T = THIRD
    int F = FIFTH
}