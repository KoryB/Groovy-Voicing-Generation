package com.korybyrne.hlml.chord;

import java.util.HashMap;
import java.util.Map;

public class Chord {
    public final static String[] ROOT_TO_ROMAN = new String[]{
            "I", "N", "II", "III", "III", "IV", "TT", "V", "VI", "VI", "VII", "VII"
    };

    public final static int AUGMENTED = 0;
    public final static int MAJOR = 1;
    public final static int MINOR = 2;
    public final static int DIMINISHED = 3;

    public final static int ROOT = 0;
    public final static int THIRD = 1;
    public final static int FIFTH = 2;

    public final static int[][] VOICINGS = {
            {0, 4, 8},
            {0, 4, 7},
            {0, 3, 7},
            {0, 3, 6}
    };

    protected int mRoot, mQuality, mInversion;

    public Chord(int root, int quality) {
        this(root, quality, 0);
    }

    public Chord(int root, int quality, int inversion) {
        mRoot = root;
        mQuality = quality;
        mInversion = inversion;
    }

    @Override
    public String toString() {
        String rv = ROOT_TO_ROMAN[mRoot];

        switch(mQuality) {
            case AUGMENTED:
                rv = rv.toUpperCase() + "+";
                break;

            case MAJOR:
                rv = rv.toUpperCase();
                break;

            case MINOR:
                rv = rv.toLowerCase();
                break;

            case DIMINISHED:
            default:
                rv = rv.toLowerCase() + "°";
                break;
        }

        return rv;
    }

    public int[] getVoices() {
        int[] rv = new int[3];

        for (int i = 0; i < 3; i++) {
            rv[i] = mRoot + VOICINGS[mQuality][i];
        }

        return rv;
    }

    public int[] getVoices(int key) {
        int[] rv = new int[3];

        for (int i = 0; i < 3; i++) {
            rv[i] = mRoot + VOICINGS[mQuality][i] + key;
        }

        return rv;
    }

    public int getRoot() {
        return mRoot;
    }

    public int getQuality() {
        return mQuality;
    }
}
