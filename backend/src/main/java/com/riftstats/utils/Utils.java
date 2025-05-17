package com.riftstats.utils;

public final class Utils {

    private Utils() {}

    public static void sleepSeconds(long seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
