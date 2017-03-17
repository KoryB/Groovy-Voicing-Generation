package com.korybyrne.hlml.groovy.note;

//TODO: Look into filing groovy bug report, this being a Groovy class causes VerifyError
public class Note extends jm.music.data.Note {
    static final int OCTAVE_LENGTH = 12;

    public Note(int pitch) {
        super(pitch, DEFAULT_RHYTHM_VALUE);
    }

    public int getPitchClass() {
        return getPitch() % OCTAVE_LENGTH;
    }

    public int getpc() {
        return getPitchClass();
    }

    public int getScaleDegree(int[] scale) {
        for (int i = 0; i < scale.length; ++i) {
            if (getPitchClass() == scale[i]) {
                return i;
            }
        }

        return -1;
    }

    /////// GROOVY OPERATORS /////////
    public Note plus(Integer rhs) {
        return new Note(getPitch() + rhs);
    }

    public boolean equals(int other) {
        return getPitch() == other;
    }

    public int asType(Class<Integer> c) {
        return getPitch();
    }
}
