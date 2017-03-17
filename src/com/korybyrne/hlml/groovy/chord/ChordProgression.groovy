package com.korybyrne.hlml.groovy.chord

import com.korybyrne.hlml.groovy.note.Line
import com.korybyrne.hlml.groovy.voicing.Voicing
import jm.constants.Durations

class ChordProgression implements Durations {
    List<Chord> elements = []
    Line rootLine = new Line()

    ChordProgression() {}

    ChordProgression(ChordProgression progression) {
        this.elements.addAll(progression.elements)
        this.rootLine.addNoteList(progression.rootLine.getNoteArray())
    }

    @Override
    String toString() {
        String rv = ""

        for (Chord chord : this.elements) {
            rv += chord.toString() + "-"
        }

        if (rv.endsWith("-")) {
            rv = rv.substring 0, rv.length()-1
        }

        return rv
    }

    private ChordProgression appendChordRight(Chord chord) {
        this.elements.add(chord)
        this.rootLine.addNote(chord.root, chord.duration)
        return this
    }

    ChordProgression appendRight(Chord chord) {
        return this.appendChordRight(chord)
    }

    ChordProgression appendRight(List<Chord> chords) {
        chords.each {
            this.appendChordRight(it)
        }

        return this
    }

    ChordProgression appendRight(ChordProgression progression) {
        return this.appendRight(progression.elements)
    }

    int[] getRootContour() {
        return this.rootLine.getContour()
    }

    /////////// GROOVY GOODNESS///////////

    // Note: These write to the object itself, done for DSL
    // Perhaps make a seperate class for modify ones?
    ChordProgression plus(Chord chord) {
        return appendRight(chord)
    }

    ChordProgression plus(List<Chord> chords) {
        return appendRight(chords)
    }

    ChordProgression plus(ChordProgression progression) {
        return appendRight(progression)
    }

    Chord getAt(int index) {
        return elements[index]
    }
}
