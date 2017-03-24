package com.korybyrne.hlml.groovy.language

class ScaleDegree {
    int degree

    String toString() {
        return "${degree}d"
    }

    int toScale(scale) {
        return scale[degree]
    }
}

@Category(Number)
class VoicingCategory {


    ScaleDegree getSd() {
        return getScaleDegree()
    }

    ScaleDegree getScaleDegree() {
        return new ScaleDegree(degree: this - 1)
    }
}
