package com.korybyrne.hlml.chord;

import jm.JMC;
import jm.music.data.CPhrase;

import java.util.ArrayList;
import java.util.List;

import static jm.constants.Pitches.C4;

public class Progression implements JMC {

    private List<Chord> mChords = new ArrayList<>();
    private int mKey = C4;
    private double mChordLength = MINIM;

    public Progression() {

    }

    @Override
    public String toString() {
        String rv = "";

        for (Chord chord : mChords) {
            rv += chord + "-";
        }

        if (rv.endsWith("-")) {
            rv = rv.substring(0, rv.length() - 1);
        }

        return rv;
    }

    private void appendRightChord(Chord chord) {
        mChords.add(chord);
    }

    public Progression appendRight(Chord chord) {
        appendRightChord(chord);

        return this;
    }

    public Progression appendRight(List<Chord> chords) {
        for (Chord chord : chords) {
            appendRightChord(chord);
        }

        return this;
    }

    public Progression appendRight(Progression progression) {
        for (Chord chord : progression.mChords) {
            appendRightChord(chord);
        }

        return this;
    }

    public Progression render(CPhrase phrase) {
        for (Chord chord : mChords) {
            phrase.addChord(chord.getVoices(mKey), mChordLength);
        }

        return this;
    }

    public List<Chord> getChords() {
        return mChords;
    }
}
