package com.korybyrne.hlml;

import java.util.Random;

public class Globals {
    public final static Random RANDOM = new Random();

    public static int trueMod(int a, int b) {
        return ((a % b) + b) % b;
    }
}
