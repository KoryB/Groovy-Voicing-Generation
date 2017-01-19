package com.korybyrne.hlml;

import com.korybyrne.hlml.chord.Chord;
import com.korybyrne.hlml.voicing.Voicing;
import com.korybyrne.hlml.voicing.VoicingProgression;
import inst.BreathyFluteInst;
import inst.SimpleSineInst;

import jm.music.data.*;
import jm.JMC;
import jm.audio.*;
import jm.util.*;

import com.korybyrne.hlml.chord.Progression;
import com.korybyrne.hlml.chord.ProgressionGenerator;

public class Main implements JMC {
    public static void main(String[] args) {
        CPhrase c = new CPhrase();

        ProgressionGenerator generator = new ProgressionGenerator();

        VoicingProgression vp = new VoicingProgression(
                generator.createProgression(8)
        );

//        VoicingProgression vp = new VoicingProgression()
//                .appendRight(new Voicing(7, Chord.MAJOR))
//                .appendRight(new Voicing(0, Chord.MAJOR));

        vp.voice(4);

        System.out.println(vp);

        vp.render(c);

        Score score = new Score(new Part(c));
        Instrument sineWave = new BreathyFluteInst(44100);
        Write.au(score, "Test.au", sineWave);
        View.notate(score);

        //TODO: USE THE GUIDO ENGINE LIBRARY (IN DOWNLOADS)
    }
}
