package com.iafenvoy.uranus.util;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandomHelper {
    public static int nextInt(int min, int max) {
        return nextInt(new Random(), min, max);
    }

    public static int nextInt(Random random, int min, int max) {
        return min >= max ? min : random.nextInt(max - min + 1) + min;
    }

    public static double nextDouble(double min, double max) {
        return nextDouble(new Random(), min, max);
    }

    public static double nextDouble(Random random, double min, double max) {
        return min >= max ? min : random.nextDouble() * (max - min) + min;
    }

    public static double randomize(double origin, double ratio) {
        double range = Math.abs(origin * ratio);
        return origin + nextDouble(-range, range);
    }

    public static <T> T randomOne(List<T> list) {
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }

    public static <T> T randomOne(T[] list) {
        return randomOne(List.of(list));
    }
}
