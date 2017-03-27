package com.korybyrne.hlml.groovy

import com.korybyrne.hlml.groovy.chord.Chord
import com.korybyrne.hlml.groovy.chord.Chords
import com.korybyrne.hlml.groovy.chord.ChordProgression
import com.korybyrne.hlml.groovy.chord.ChordProgressionGenerator
import com.korybyrne.hlml.groovy.voicing.Voicing
import com.korybyrne.hlml.groovy.language.LanguageRuleHandler
import com.korybyrne.hlml.lilyrendering.LilypondHandler
import inst.FractalInst
import inst.PluckInst
import inst.PulsewaveInst
import inst.SineInst
import inst.SuperSawInst
import inst.TextInst
import inst.VibesInst
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

//        ChordProgression p = new ChordProgressionGenerator().createProgression(16)
        ChordProgression p =
                new Chord(0, Chords.MINOR) +
                new Chord(2, Chords.DIMINISHED) +
                new Chord(7, Chords.MAJOR) +
//                new Chord(0, Chords.MAJOR)
//                new Chord(5, Chords.MAJOR) +
//                new Chord(2, Chords.MAJOR) +
                new Chord(0, Chords.MINOR)
//                new Chord(11, Chords.DIMINISHED) +
//                new Chord(0, Chords.MAJOR)

        LanguageRuleHandler.instance.init(p)
        def voicingProgression = LanguageRuleHandler.instance.voice("Test.hlml")

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
        Instrument sineWave = new SineInst(44100)
        Write.au(score, "Test.au", sineWave)
    }
}
