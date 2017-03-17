package com.korybyrne.hlml.groovy.voicing

import com.korybyrne.hlml.groovy.chord.Chord
import com.korybyrne.hlml.groovy.chord.ChordProgression
import com.korybyrne.hlml.groovy.voicing.Voice
import com.korybyrne.hlml.groovy.voicing.Voicing
import com.korybyrne.hlml.groovy.note.Line
import jm.constants.Durations

class VoicingProgression extends ChordProgression implements Durations {
    List<Voicing> elements = []
    List<Line> partLines = [];

    VoicingProgression() {}

    VoicingProgression(ChordProgression chordProgression) {
        throw new UnsupportedOperationException("Cannot clone a VoicingProgression with a Chord Progression")
    }

    VoicingProgression(VoicingProgression progression) {
        this.elements.addAll(progression.elements)
        this.rootLine.addNoteList(progression.rootLine.getNoteArray())
    }

    @Override
    String toString() {
        String rv = ""

        for (Voicing voicing : this.elements) {
            rv += voicing.toString() + "\n"
        }

        return rv
    }

    private VoicingProgression appendChordRight(Chord chord) {
        //TODO: Auto-voice here?
        throw new UnsupportedOperationException("Appending chords is not implemented yet")
//
//        this.elements.add(chord)
//        this.rootLine.addNote(chord.root, chord.duration)
//        return this
    }

    private VoicingProgression appendVoicingRight(Voicing voicing) {
        this.elements.add(voicing)
        voicing.voices.eachWithIndex { Voice entry, int i ->
            if (this.partLines.size() < i+1) {
                this.partLines.add new Line()
            }

            this.partLines[i].addNote entry
        }

        return this
    }

    VoicingProgression appendRight(Voicing voicing) {
        return appendVoicingRight(voicing)
    }

    VoicingProgression appendRight(List<Voicing> voicings) {
        voicings.each {
            appendVoicingRight(it)
        }

        return this
    }

    VoicingProgression appendRight(VoicingProgression progression) {
        return appendRight(progression.elements)
    }

    ////////// GROOVY GOODNESS //////////////

    // Note: These write to the object itself, done for DSL
    // Perhaps make a seperate class for modify ones?
    VoicingProgression plus(List<Voicing> voicings) {
        return appendRight(voicings)
    }

    VoicingProgression plus(VoicingProgression progression) {
        return appendRight(progression)
    }

    Voicing getAt(int index) {
        return elements[index]
    }
}
