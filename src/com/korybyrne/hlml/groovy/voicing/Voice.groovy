package com.korybyrne.hlml.groovy.voicing

import com.korybyrne.hlml.groovy.note.Note

class Voice extends Note {
    int part
    boolean locked;

    Voice(int part, int pitch) {
        super(pitch)
        this.part = part
    }

    @Override
    void setPitch(int pitch) {
        if (!this.locked) {
            super.setPitch(pitch)
            this.locked = true

            println "Locking: $part, $pitch"
        } else {
            println "Can't change $part to $pitch, I'm locked"
        }
    }

    /////////// OPERATORS ///////////
    Voice plus(Integer rhs) {
        return new Voice(this.part, getPitch() + rhs)
    }
}
