package com.korybyrne.hlml.groovy.chord

interface Chords {
    String[] ROOT_TO_ROMAN = ['I', 'N', 'II', 'III', 'III', 'IV', 'TT', 'V', 'VI', 'VI', 'VII', 'VII']

    String[] ROOT_TO_ENGLISH = ['C', 'C#', 'D', 'D#', 'E', 'F', 'F#', 'G', 'G#', 'A', 'A#', 'B']

    int ROOT = 0
    int THIRD = 1
    int FIFTH = 2

    // Indices for the INTERVALS array, order took from jm.constants.Chords
    int MAJOR =                         0
    int MINOR =                         1
    int AUGMENTED =                     2
    int DIMINISHED =                    3
    int SUSPENDED_FOURTH =              4
    int FLATTED_FIFTH =                 5
    int SIXTH =                         6
    int MINOR_SIXTH =                   7
    int SEVENTH =                       8
    int MINOR_SEVENTH =                 9
    int MAJOR_SEVENTH =                 10
    int SEVENTH_SHARP_FIFTH =           11
    int DIMINISHED_SEVENTH =            12
    int SEVENTH_FLAT_FIFTH =            13
    int MINOR_SEVENTH_FLAT_FIFTH =      14
    int SIXTH_ADDED_NINTH =             15
    int SEVENTH_SHARP_NINTH =           16
    int SEVENTH_FLAT_NINTH =            17
    int NINTH =                         18
    int MINOR_NINTH =                   19
    int ELEVENTH =                      20
    int MINOR_ELEVENTH =                21
    int THIRTEENTH =                    22

    // Intervals from jm.constants.Chords
    // Modified to have a leading zero in the list
    List<List> INTERVALS = [
        [0, 4, 7],
        [0, 3, 7],
        [0, 4, 8],
        [0, 3, 6],
        [0, 5, 7],
        [0, 4, 6],
        [0, 4, 7, 9],
        [0, 3, 7, 9],
        [0, 4, 7, 10],
        [0, 3, 7, 10],
        [0, 4, 7, 11],
        [0, 4, 8, 10],
        [0, 4, 6, 9],
        [0, 4, 6, 10],
        [0, 3, 6, 10],
        [0, 4, 7, 9, 14],
        [0, 4, 7, 10, 15],
        [0, 4, 7, 10, 13],
        [0, 4, 7, 10, 14],
        [0, 3, 7, 10, 14],
        [0, 7, 10, 14, 17],
        [0, 3, 7, 10, 14, 17],
        [0, 4, 7, 10, 14, 21]
    ]
    
}