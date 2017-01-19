package com.korybyrne.hlml.voicing;

import com.korybyrne.hlml.chord.Chord;
import com.korybyrne.hlml.chord.Progression;
import jm.JMC;
import jm.music.data.CPhrase;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class VoicingProgression implements JMC {
    private List<Voicing> mVoicings = new ArrayList<>();
    private int mKey = C4;
    private double mChordTime = MINIM;

    public VoicingProgression() {
        this(null);
    }

    public VoicingProgression(Progression progression) {
        if (progression != null) {
            for (Chord chord : progression.getChords()) {
                appendRightVoicing(new Voicing(chord.getRoot(), chord.getQuality()));
            }
        }
    }

    @Override
    public String toString() {
        String rv = "";

        for (Voicing voicing : mVoicings) {
            rv += voicing + "\n";
        }

        if (rv.endsWith("\n")) {
            rv = rv.substring(0, rv.length() - 1);
        }

        return rv;
    }

    private void appendRightVoicing(Voicing voicing) {
        mVoicings.add(voicing);
    }

    public VoicingProgression appendRight(Voicing voicing) {
        appendRightVoicing(voicing);

        return this;
    }

    public VoicingProgression appendRight(VoicingProgression progression) {
        for (Voicing voicing : progression.mVoicings) {
            appendRightVoicing(voicing);
        }

        return this;
    }

    public VoicingProgression voice(int numVoices) {
        int i = 0;
        ListIterator<Voicing> voicingIterator = mVoicings.listIterator(mVoicings.size());
        Voicing current = null, next = null;

        while (voicingIterator.hasPrevious()) {
            current = voicingIterator.previous();

            current.setNumVoices(numVoices);

            if (next == null) {
                current.voiceRandom();
            } else {
                current.voiceSmallest(next);
            }

            next = current;
        }

        return this;
    }

    public VoicingProgression render(CPhrase phrase) {
        for (Voicing voicing : mVoicings) {
            phrase.addChord(voicing.getVoices(mKey), mChordTime);
        }

        return this;
    }
}
