package com.korybyrne.hlml.lilyrendering;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class LilypondHandler {
//    public static final String[] NOTE_NAMES = {
//            "C0", "Cis0", "D0", "Dis0", "E0", "F0", "Fis0", "G0", "Gis0", "A0", "Ais0", "B0",
//            "C1", "Cis1", "D1", "Dis1", "E1", "F1", "Fis1", "G1", "Gis1", "A1", "Ais1", "B1",
//            "C2", "Cis2", "D2", "Dis2", "E2", "F2", "Fis2", "G2", "Gis2", "A2", "Ais2", "B2",
//            "C3", "Cis3", "D3", "Dis3", "E3", "F3", "Fis3", "G3", "Gis3", "A3", "Ais3", "B3",
//            "C4", "Cis4", "D4", "Dis4", "E4", "F4", "Fis4", "G4", "Gis4", "A4", "Ais4", "B4",
//            "C5", "Cis5", "D5", "Dis5", "E5", "F5", "Fis5", "G5", "Gis5", "A5", "Ais5", "B5",
//            "C6", "Cis6", "D6", "Dis6", "E6", "F6", "Fis6", "G6", "Gis6", "A6", "Ais6", "B6",
//            "C7", "Cis7", "D7", "Dis7", "E7", "F7", "Fis7", "G7", "Gis7", "A7", "Ais7", "B7",
//            "C8", "Cis8", "D8", "Dis8", "E8", "F8", "Fis8", "G8", "Gis8", "A8", "Ais8", "B8",
//    };

    public final File LOG_FILE = new File("lilyhandler.txt");

    private final String mHeader = "\\new PianoStaff <<\n";
    private final String mFooter = ">>";

    private List<LilypondStaff> mStaffs = new ArrayList<>();

    public LilypondHandler() {

    }

    public LilypondStaff getStaff(int idx) {
        return mStaffs.get(idx);
    }

    public LilypondStaff addStaff(String clef, String staffType) {
        LilypondStaff staff = new LilypondStaff(clef,  staffType);
        mStaffs.add(staff);
        return staff;
    }

    @Override
    public String toString() {
        StringBuilder rv = new StringBuilder();
        rv.append(mHeader);

        for (LilypondStaff staff : mStaffs) {
            rv.append(staff.toString());
        }

        rv.append(mFooter);

        return rv.toString();
    }

    public boolean writeLily(String filename) {
        try (PrintWriter writer = new PrintWriter(filename+".ly")) {
            writer.println(this.toString());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean renderLily(String filename) {
        writeLily(filename);

        ProcessBuilder builder = new ProcessBuilder(
                "lilypond", filename+".ly"
        ).directory(new File("./"));

        builder.redirectErrorStream(true);
        builder.redirectOutput(
                ProcessBuilder.Redirect.appendTo(LOG_FILE)
        );

        try {
            builder.start();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
