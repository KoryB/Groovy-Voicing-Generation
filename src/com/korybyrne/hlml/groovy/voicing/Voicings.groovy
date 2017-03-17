package com.korybyrne.hlml.groovy.voicing

import com.korybyrne.hlml.groovy.chord.Chords
import jm.constants.Pitches

interface Voicings extends Chords, Pitches {
    int BASS = 0
    int TENOR = 1
    int ALTO = 2
    int SOPRANO = 3

    Map ABBREVIATED = [
            b: BASS,
            t: TENOR,
            a: ALTO,
            s: SOPRANO
    ]

    int[][] DEFAULT_NOTES = [
        [C3, CS3, D3, DS3, E3, F2, FS2, G2, GS2, A2, AS2, B2],
        [C3, CS3, D3, DS3, E3, F3, FS3, G3, GS3, A3, AS3, B3],
        [C4, CS4, D4, DS4, E4, F3, FS3, G3, GS3, A3, AS3, B3],
        [C4, CS4, D4, DS4, E4, F4, FS4, G4, GS4, A4, AS4, B4],
    ]
}