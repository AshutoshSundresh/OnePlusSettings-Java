package com.google.analytics.tracking.android;

import com.google.analytics.tracking.android.Logger;

public class Log {
    private static GoogleAnalytics sGaInstance;

    public static void e(String str) {
        Logger logger = getLogger();
        if (logger != null) {
            logger.error(str);
        }
    }

    public static void i(String str) {
        Logger logger = getLogger();
        if (logger != null) {
            logger.info(str);
        }
    }

    public static void v(String str) {
        Logger logger = getLogger();
        if (logger != null) {
            logger.verbose(str);
        }
    }

    public static void w(String str) {
        Logger logger = getLogger();
        if (logger != null) {
            logger.warn(str);
        }
    }

    public static boolean isVerbose() {
        if (getLogger() != null) {
            return Logger.LogLevel.VERBOSE.equals(getLogger().getLogLevel());
        }
        return false;
    }

    private static Logger getLogger() {
        if (sGaInstance == null) {
            sGaInstance = GoogleAnalytics.getInstance();
        }
        GoogleAnalytics googleAnalytics = sGaInstance;
        if (googleAnalytics != null) {
            return googleAnalytics.getLogger();
        }
        return null;
    }

    static void clearGaInstance() {
        sGaInstance = null;
    }
}
