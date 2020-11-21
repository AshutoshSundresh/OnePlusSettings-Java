package com.google.tagmanager;

/* access modifiers changed from: package-private */
public final class Log {
    static Logger sLogger = new DefaultLogger();

    public static void e(String str) {
        sLogger.e(str);
    }

    public static void w(String str) {
        sLogger.w(str);
    }
}
