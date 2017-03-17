package com.korybyrne.hlml.lilyrendering;

public interface LilypondInterface {
    String[] NOTE_NAMES = {
            "c,,,,", "cis,,,,", "d,,,,", "dis,,,,", "e,,,,", "f,,,,", "fis,,,,", "g,,,,", "gis,,,,", "a,,,,", "ais,,,,", "b,,,,",
            "c,,,", "cis,,,", "d,,,", "dis,,,", "e,,,", "f,,,", "fis,,,", "g,,,", "gis,,,", "a,,,", "ais,,,", "b,,,",
            "c,,", "cis,,", "d,,", "dis,,", "e,,", "f,,", "fis,,", "g,,", "gis,,", "a,,", "ais,,", "b,,",
            "c,", "cis,", "d,", "dis,", "e,", "f,", "fis,", "g,", "gis,", "a,", "ais,", "b,",
            "c", "cis", "d", "dis", "e", "f", "fis", "g", "gis", "a", "ais", "b",
            "c'", "cis'", "d'", "dis'", "e'", "f'", "fis'", "g'", "gis'", "a'", "ais'", "b'",
            "c''", "cis''", "d''", "dis''", "e''", "f''", "fis''", "g''", "gis''", "a''", "ais''", "b''",
            "c'''", "cis'''", "d'''", "dis'''", "e'''", "f'''", "fis'''", "g'''", "gis'''", "a'''", "ais'''", "b'''",
            "c''''", "cis''''", "d''''", "dis''''", "e''''", "f''''", "fis''''", "g''''", "gis''''", "a''''", "ais''''", "b''''",
    };

    void addNote(int midinote);
}
