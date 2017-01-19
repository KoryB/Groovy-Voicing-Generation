package com.korybyrne.hlml.voicing;

import com.korybyrne.hlml.Globals;
import com.korybyrne.hlml.chord.Chord;

import java.util.ArrayList;

public class Voicing extends Chord {

    private Voice[] mVoices;

    public Voicing(int root, int quality) {
        super(root, quality);
    }

    public String toString() {
        String rv = super.toString() + "\n\t";

        if (mVoices != null) {
            for (Voice voice : mVoices) {
                rv += voice.getOffset() + "\t" + voice.getPitchClass() + "\n\t";
            }
        }

        return rv;


    }

    public Voicing setNumVoices(int voices) {
        mVoices = new Voice[4];
        return this;
    }

    public Voicing voiceRandom() {
        mVoices[0] = new Voice(ROOT, -2);
        mVoices[1] = new Voice(FIFTH, -1);
        mVoices[2] = new Voice(THIRD, 0);
        mVoices[3] = new Voice(ROOT, 1);

        return this;
    }

    public Voicing voiceSmallest(Voicing other) {
        ArrayList<ArrayList<Voice>> potentials = new ArrayList<>();

        // Get potential voicings
        for (int i = 0; i < other.mVoices.length; i++) {
            Voice otherVoice = other.mVoices[i];
            potentials.add(new ArrayList<>());

            int smallest = Integer.MAX_VALUE;

            for (int interval : VOICINGS[mQuality]) {
                int dist = Math.floorMod(otherVoice.getPitchClass() - (mRoot + interval), 12);

                if (dist < smallest) {
                    smallest = dist;
                }
            }

            for (int j = 0; j < VOICINGS[mQuality].length; j++) {
                int interval = VOICINGS[mQuality][j];
                int raw = otherVoice.getPitchClass() - (mRoot + interval);
                int div = Math.floorDiv(raw, 12);
                int dist = Math.floorMod(raw, 12);

                if (dist == smallest) {
                    System.out.println(String.format("raw: %d, div: %d, dist: %d", raw, div, dist));
                    int signedMovement = dist * Integer.signum(raw);
                    int newPitch = mRoot + VOICINGS[mQuality][j];
                    int newOctave = Math.floorDiv((otherVoice.mOctave*12 + (otherVoice.getPitch() - newPitch) - signedMovement), 12);

                    potentials.get(i).add(new Voice(j, newOctave));
                }
            }
        }
        System.out.println();


        // Collapse voicings
        for (int i = 0; i < potentials.size(); i++) {
            ArrayList<Voice> potential = potentials.get(i);

            if (potential.size() == 1) {
                mVoices[i] = potential.get(0);
            } else {    // TODO: Logic here, tonality etc.
                mVoices[i] = potential.get(Globals.RANDOM.nextInt(potential.size()));
            }
        }

        return this;
    }

    public int[] getVoices() {
        int[] rv = new int[mVoices.length];

        for (int i = 0; i < mVoices.length; i++) {
            rv[i] = mVoices[i].getOffset();
        }

        return rv;
    }

    public int[] getVoices(int key) {
        int[] rv = new int[mVoices.length];

        for (int i = 0; i < mVoices.length; i++) {
            rv[i] = mVoices[i].getOffset() + key;
        }

        return rv;
    }

    private class Voice {
        private int mType = 0;
        private int mOctave = 0;

        private Voice(int type, int octave) {
            mType = type;
            mOctave = octave;
        }

        private int getPitch() {
            return mRoot + VOICINGS[mQuality][mType];
        }

        private int getPitchClass() {
            return Math.floorMod(mRoot + VOICINGS[mQuality][mType], 12);
        }

        private int getOffset() {
            return mRoot + mOctave*12 + VOICINGS[mQuality][mType];
        }
    }
}
