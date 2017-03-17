package com.korybyrne.hlml.lilyrendering;

import java.util.ArrayList;
import java.util.List;

public class LilypondStaff {
    private String mStaffType;

    private String mHeader;
    private String mFooter;

    private String mClef;

    private List<LilypondVoice> mVoices;

    public LilypondStaff(String clef, String staffType) {
        mClef = clef;
        mStaffType = staffType;

        mHeader = "\\new " + mStaffType + "<<\n";
        mFooter = ">>\n";

        mVoices = new ArrayList<>();
    }

    public LilypondVoice getVoice(int idx) {
        return mVoices.get(idx);
    }

    public LilypondVoice addVoice(int id) {
        LilypondVoice voice = new LilypondVoice(id, mClef);
        mVoices.add(voice);
        return voice;
    }

    @Override
    public String toString() {
        StringBuilder rv = new StringBuilder();
        rv.append(mHeader);

        for (LilypondVoice voice : mVoices) {
            rv.append(voice.toString());
        }

        rv.append(mFooter);

        return rv.toString();
    }
}
