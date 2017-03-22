package com.korybyrne.hlml.groovy

import com.korybyrne.hlml.groovy.chord.Chords
import com.korybyrne.hlml.groovy.chord.ChordProgression
import com.korybyrne.hlml.groovy.chord.ChordProgressionGenerator
import com.korybyrne.hlml.groovy.voicing.Voicing
import com.korybyrne.hlml.groovy.voicing.VoicingCategory
import com.korybyrne.hlml.groovy.voicing.VoicingRuleHandler
import com.korybyrne.hlml.lilyrendering.LilypondHandler
import inst.PulsewaveInst
import jm.audio.Instrument
import jm.music.data.Part
import jm.music.data.Phrase
import jm.music.data.Score
import jm.util.Write

class Main {
    static void main(String[] args) {
        Voicing prevVoicing = new Voicing(2, Chords.MINOR).setNumVoices(4).voiceRandom()
        Voicing workingVoicing = new Voicing(5, Chords.MAJOR).setNumVoices(4)
        Voicing currVoicing

        ChordProgression p = new ChordProgressionGenerator().createProgression(32)

        VoicingRuleHandler.instance.init(p)
        def voicingProgression = VoicingRuleHandler.instance.voice("Test.hlml")

        println '//////// VOICINGS START /////////'

        println 'ChordProgression'
        println p
        
        println 'VoicingProgression'
        println voicingProgression

        LilypondHandler handler = new LilypondHandler();

        Phrase p1 = voicingProgression.partLines[3]
        Phrase p2 = voicingProgression.partLines[2]
        Phrase p3 = voicingProgression.partLines[1]
        Phrase p4 = voicingProgression.partLines[0]

        handler.addStaff("treble", "Staff").addVoice(0).addNotes(p1.getNoteArray())
        handler.getStaff(0).addVoice(1).addNotes(p2.getNoteArray())
        handler.addStaff("bass", "Staff").addVoice(2).addNotes(p3.getNoteArray())
        handler.getStaff(1).addVoice(3).addNotes(p4.getNoteArray())
        handler.renderLily("test")
//
        Score score = new Score()
        score.addPart(new Part(p1))
        score.addPart(new Part(p2))
        score.addPart(new Part(p3))
        score.addPart(new Part(p4))
        Instrument sineWave = new PulsewaveInst(44100)
        Write.au(score, "Test.au", sineWave)
    }
}
