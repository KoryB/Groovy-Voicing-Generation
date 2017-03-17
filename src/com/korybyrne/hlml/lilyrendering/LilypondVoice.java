package com.korybyrne.hlml.lilyrendering;

import jm.music.data.Note;

public class LilypondVoice implements LilypondInterface {
    private static final String[] ID_STRINGS = {
            "voiceOne",
            "voiceTwo",
            "voiceThree",
            "voiceFour"
    };

    private String mStringID;
    private int mID;

    private StringBuilder mNotes = new StringBuilder();
    private String mHeader;
    private String mFooter;

    public LilypondVoice(int id, String clef) {
        mID = id;
        mStringID = ID_STRINGS[id];

        mHeader = "\\new Voice = \"" + mStringID + "\" {\n\\" + mStringID + "\\clef " + clef + "\n";
        mFooter = "\n}\n";
    }

    @Override
    public String toString() {
        return mHeader + mNotes.toString() + mFooter;
    }

    public void addNotes(Note[] notes) {
        for (Note note : notes) {
            addNote(note.getPitch());
        }
    }

    public void addNotes(int[] midinotes) {
        for (int note : midinotes) {
            addNote(note);
        }
    }

    public void addNote(Note note) {
        addNote(note.getPitch());
    }

    @Override
    public void addNote(int midinote) {
        mNotes.append(NOTE_NAMES[midinote]).append(" ");
    }
}
