package com.korybyrne.hlml.groovy.language

import com.korybyrne.hlml.groovy.voicing.Voicing

class EarlyTerminationException extends RuntimeException {
    Voicing voicing

    EarlyTerminationException(Voicing voicing) {
        this.voicing = voicing
    }
}
