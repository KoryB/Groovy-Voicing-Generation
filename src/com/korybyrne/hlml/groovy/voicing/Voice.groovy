package com.korybyrne.hlml.groovy.voicing

import com.korybyrne.hlml.groovy.note.Note

class Voice extends Note {
    int part
    boolean locked, finalized;

    Voice(int part, int pitch) {
        super(pitch)
        this.part = part
    }

    @Override
    void setPitch(int pitch) {
        if (!this.locked) {
            super.setPitch(pitch)
            this.locked = true
        }
    }

    /////////// OPERATORS ///////////
    Voice plus(Integer rhs) {
        return new Voice(this.part, getPitch() + rhs)
    }

    boolean unlock() {
        def rv = locked
        locked = false

        return rv
    }
}
