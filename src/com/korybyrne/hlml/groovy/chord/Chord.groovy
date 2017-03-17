package com.korybyrne.hlml.groovy.chord

import jm.constants.Durations

class Chord implements Chords, Durations{
    int root, quality, inversion, duration

    Chord(int root, quality) {
        this.root = root
        this.quality = quality
        this.inversion = 0
        this.duration = MINIM
    }

    @Override
    String toString() {
        String rv = ROOT_TO_ROMAN[this.root]

        // Not sure why I need the Chords qualifier, but doesn't work without it
        switch (this.quality) {
            case AUGMENTED:
                rv = rv.toUpperCase() + "+"
                break

            case MAJOR:
                rv = rv.toUpperCase()
                break

            case MINOR:
                rv = rv.toLowerCase()
                break

            case DIMINISHED:
            default:
                rv = rv.toLowerCase() + "°"
                break
        }

        return rv
    }

    ArrayList<Integer> getPitchClasses(int modulo) {
        return INTERVALS[this.quality]*.plus(root)*.mod(modulo)
    }

    ArrayList<Integer> getIntervals() {
        return INTERVALS[this.quality]
    }

    ChordProgression plus(Chord rhs) {
        return new ChordProgression().appendRight(this).appendRight(rhs)
    }
}
