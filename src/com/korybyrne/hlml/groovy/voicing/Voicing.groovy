package com.korybyrne.hlml.groovy.voicing

import com.korybyrne.hlml.groovy.chord.Chord
import com.korybyrne.hlml.groovy.chord.ChordProgression
import com.korybyrne.hlml.groovy.note.Note

class Voicing extends Chord implements Voicings {
    static int getPitchClassDistance(int from, int to) {
        int dist = to - from
        while (dist <= -6) {
            dist += 12
        }
        while (dist > 6) {
            dist -= 12
        }

        return dist
    }

    static int getPositivePitchClassDistance(int from, int to) {
        return Math.floorMod(to - from, 12)
    }

    List<Voice> voices

    Voicing(int root, int quality) {
        super(root, quality)
    }

    Voicing(Chord chord) {
        super(chord.root, chord.quality)
    }

    Voicing(Voicing voicing) {
        super(voicing.root, voicing.quality)

        this.voices = voicing.voices.collect {
            def voice = new Voice(it.part, it.pitch)
            voice.locked = it.locked

            return voice
        }
    }

    @Override
    String toString() {
        String rv = super.toString() + "\n"

        if (this.voices != null) {
            for (Voice voice : this.voices[-1..0]) {
                rv += "\t${voice.getPitch()}\t${ROOT_TO_ENGLISH[voice.getPitchClass()]}\n"
            }
        }

        return rv
    }

    Voicing setNumVoices(int numVoices) {
        this.voices = [null] * 4

        for (int i = 0; i < 4; ++i) {
            this.voices[i] = new Voice(i, 0)
        }

        return this
    }

    Voicing voiceRandom() {
//        this.voices[BASS] = new Voice(BASS, DEFAULT_NOTES[BASS][this.root])
//        this.voices[TENOR] = new Voice(TENOR, DEFAULT_NOTES[BASS][this.root]+ 12)
//        this.voices[ALTO] = new Voice(ALTO, DEFAULT_NOTES[ALTO][this.root] + this.getIntervals()[FIFTH])
//        this.voices[SOPRANO] = new Voice(SOPRANO, DEFAULT_NOTES[ALTO][this.root] + getIntervals()[THIRD] + 12)

        this.voices[BASS] = new Voice(BASS, DEFAULT_NOTES[BASS][this.root])
        this.voices[TENOR] = new Voice(TENOR, DEFAULT_NOTES[BASS][this.root] + this.getIntervals()[FIFTH])
        this.voices[ALTO] = new Voice(ALTO, DEFAULT_NOTES[ALTO][this.root] + getIntervals()[THIRD])
        this.voices[SOPRANO] = new Voice(SOPRANO, DEFAULT_NOTES[ALTO][this.root] + 12)
        
        return this
    }

    int[] getIntervalIndicesFromRoot() {
        int[] parts = new int[this.voices.size()]

        for (int i = 0; i < this.voices.size(); ++i) {
            def dist = getPositivePitchClassDistance this.root, this.voices[i].pitch
            def index = getIntervals().findIndexOf({it == dist})
            parts[i] = (index == null)? -1:index
        }

        return parts
    }

    int[] getIntervalsFromBass() {
        int[] parts = new int[this.voices.size()-1]

        for (int i = 1; i < this.voices.size(); ++i) {
            parts[i-1] = getPositivePitchClassDistance this.voices[BASS].pitch, this.voices[i].pitch
        }

        return parts
    }

    List calculateIntervals() {
        return voices.collect {
            if (it) {
                return getPositivePitchClassDistance(this.root, it.pitchClass)
            } else {
                return null
            }
        }
    }

    int[] getPitches() {
        return this.voices*.getPitch()
    }

    int[] getPitches(int key) {
        return this.voices*.getPitch() //TODO: Transposition
    }

    Voice getBass() {
        return this.voices[BASS]
    }

    Voice getTenor() {
        return this.voices[TENOR]
    }

    Voice getAlto() {
        return this.voices[ALTO]
    }

    Voice getSoprano() {
        return this.voices[SOPRANO]
    }

    def unlock() {
        this.voices.each {it.locked = false}
    }

    ///////// OPERATORS /////////
    ChordProgression plus(Chord rhs) {
        // TODO: Automatically voice the chord and put it in a Voicing ChordProgression that way?
        throw new UnsupportedOperationException("Chord cannot be added to Voicing")
    }

    VoicingProgression plus(Voicing rhs) {
        return new VoicingProgression().appendRight(this).appendRight(rhs)
    }

    Voice getAt(int index) {
        return voices[index]
    }

    Voice putAt(int index, int pitch) {
        Voice atIndex = voices[index]

        if (atIndex == null) {
            atIndex = voices[index] = new Voice(index, pitch)
            atIndex.locked = true
        } else {
            atIndex.setPitch(pitch)
        }

        return atIndex
    }

    Voice putAt(int index, Note note) {
        voices[index].setPitch(note.getPitch())
        return voices[index]
    }

    // used for open-gl like accessors for satb. maybe could be extended for other initials
    def propertyMissing(String name) {
        def rv = []

        name.each { rv += voices[ABBREVIATED[it.toLowerCase()] ?: 0] }

        if (rv.size() == 1) {
            return rv[0]
        } else {
            return rv
        }
    }
}
