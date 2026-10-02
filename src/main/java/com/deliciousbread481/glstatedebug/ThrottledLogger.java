package com.deliciousbread481.glstatedebug;

import java.util.concurrent.ConcurrentHashMap;

public final class ThrottledLogger {

    private static final ConcurrentHashMap<String, Long> LAST = new ConcurrentHashMap<>();
    private static final long INTERVAL_MS = 1000;

    public static boolean allow(String key) {
        long now = System.currentTimeMillis();
        Long last = LAST.get(key);
        if (last == null || now - last >= INTERVAL_MS) {
            LAST.put(key, now);
            return true;
        }
        return false;
    }

    private ThrottledLogger() {}
}