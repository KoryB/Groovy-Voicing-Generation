package com.korybyrne.hlml.groovy.chord

import static com.korybyrne.hlml.Globals.*

class ChordProgressionGenerator {
    final static int TONIC = 0
    final static int PRE_DOMINANT = 1
    final static int DOMINANT = 2

    final static Chord[][] MAJOR = [
        [new Chord(0, Chord.MAJOR), new Chord(9, Chord.MINOR)],
        [new Chord(2, Chord.MINOR), new Chord(5, Chord.MAJOR)],
        [new Chord(7, Chord.MAJOR)]//, new Chord(11, Chord.DIMINISHED)]
    ]

    final static Chord[][] MINOR = [
        [new Chord(0, Chord.MINOR), new Chord(8, Chord.MAJOR)],
        [new Chord(2, Chord.DIMINISHED), new Chord(5, Chord.MINOR)],
        [new Chord(7, Chord.MAJOR), new Chord(11, Chord.DIMINISHED)]
    ]

    private final static int[] FROM_TONIC = [TONIC, PRE_DOMINANT, DOMINANT]
    private final static int[] FROM_PRE_DOMINANT = [PRE_DOMINANT, DOMINANT]
    private final static int[] FROM_DOMINANT = [TONIC]

    private Chord[][] categories
    private int currentCategory

    ChordProgressionGenerator() {
        this(MAJOR)
    }

    ChordProgressionGenerator(Chord[][] categories) {
        this.categories = categories
    }

    ChordProgression createProgression(int length) {
        ChordProgression progression = new ChordProgression()
        currentCategory = TONIC

        for (int i = 0; i < length; ++i) {
            progression += nextChord(length - i)
        }

        return progression;
    }

    private Chord nextChord(int numLeft) {
        Chord[] options = this.categories[this.currentCategory]
        Chord nextChord = null
        int choice

        while (nextChord == null) {
            choice = RANDOM.nextInt(options.length)
            
            nextChord = options[choice]
//            if (    mLastChord != null &&
//                    nextChord.getRoot() == mLastChord.getRoot() &&
//                    nextChord.getQuality() == mLastChord.getQuality()) {
//                nextChord = null;
//            }
        }
        
        if (numLeft == 3) {
            this.currentCategory = DOMINANT
        } else {
            switch(this.currentCategory) {
                case TONIC:
                    choice = RANDOM.nextInt(FROM_TONIC.length)
                    this.currentCategory = FROM_TONIC[choice]
                    break

                case PRE_DOMINANT:
                    choice = RANDOM.nextInt(FROM_PRE_DOMINANT.length)
                    this.currentCategory = FROM_PRE_DOMINANT[choice]
                    break

                case DOMINANT:
                default:
                    choice = RANDOM.nextInt(FROM_DOMINANT.length)
                    this.currentCategory = FROM_DOMINANT[choice]
                    break
            }
        }
        
        return nextChord
    }
}
