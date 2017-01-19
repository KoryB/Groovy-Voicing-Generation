package com.korybyrne.hlml.chord;

import static com.korybyrne.hlml.Globals.*;

public class ProgressionGenerator {

    public final static int TONIC = 0;
    public final static int PRE_DOMINANT = 1;
    public final static int DOMINANT = 2;

    public final static Chord[][] MAJOR = {
            {new Chord(0, Chord.MAJOR), new Chord(9, Chord.MINOR)},
            {new Chord(2, Chord.MINOR), new Chord(5, Chord.MAJOR)},
            {new Chord(7, Chord.MAJOR), new Chord(11, Chord.DIMINISHED)}
    };

    public final static Chord[][] MINOR = {
            {new Chord(0, Chord.MINOR), new Chord(8, Chord.MAJOR)},
            {new Chord(2, Chord.DIMINISHED), new Chord(5, Chord.MINOR)},
            {new Chord(7, Chord.MAJOR), new Chord(11, Chord.DIMINISHED)}
    };

    private final static int[] FROM_TONIC = {TONIC, PRE_DOMINANT, DOMINANT};
    private final static int[] FROM_PRE_DOMINANT = {PRE_DOMINANT, DOMINANT};
    private final static int[] FROM_DOMINANT = {TONIC};

    private Chord[][] mCategories;
    private Progression mProgression;
    private int mCurrentCategory = TONIC;

    public ProgressionGenerator() {
        this(MAJOR);
    }

    public ProgressionGenerator(Chord[][] categories) {
        mCategories = categories;
    }

    public Progression createProgression(int length) {
        mProgression = new Progression();
        mCurrentCategory = TONIC;

        for (int i = 0; i < length; i++) {
            mProgression.appendRight(nextChord(length - i));
        }

        return mProgression;
    }

    private Chord nextChord(int numLeft) {
        Chord[] options = mCategories[mCurrentCategory];
        int choice = RANDOM.nextInt(options.length);

        Chord nextChord = options[choice];

        if (numLeft == 3) {
            mCurrentCategory = DOMINANT;
        } else {
            switch(mCurrentCategory) {
                case TONIC:
                    choice = RANDOM.nextInt(FROM_TONIC.length);
                    mCurrentCategory = FROM_TONIC[choice];
                    break;

                case PRE_DOMINANT:
                    choice = RANDOM.nextInt(FROM_PRE_DOMINANT.length);
                    mCurrentCategory = FROM_PRE_DOMINANT[choice];
                    break;

                case DOMINANT:
                default:
                    choice = RANDOM.nextInt(FROM_DOMINANT.length);
                    mCurrentCategory = FROM_DOMINANT[choice];
                    break;
            }
        }

        return nextChord;
    }
}
