package com.airbnb.lottie.utils;

import com.airbnb.lottie.LottieLogger;

public class Logger {
    private static LottieLogger INSTANCE = new LogcatLogger();

    public static void debug(String str) {
        INSTANCE.debug(str);
    }

    public static void warning(String str) {
        INSTANCE.warning(str);
    }

    public static void warning(String str, Throwable th) {
        INSTANCE.warning(str, th);
    }
}
