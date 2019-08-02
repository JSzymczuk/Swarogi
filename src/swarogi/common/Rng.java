package swarogi.common;

import java.util.Random;

public class Rng {
    public static int getInt(int bound) {
        return generator.nextInt(bound);
    }

    public static int getInt(int min, int max) {
        return min + generator.nextInt(max - min + 1);
    }

    public static boolean getBoolean() { return generator.nextBoolean(); }

    private static final Random generator = new Random();
}
