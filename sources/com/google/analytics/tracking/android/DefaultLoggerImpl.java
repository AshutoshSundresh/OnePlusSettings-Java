package com.google.analytics.tracking.android;

import android.util.Log;
import com.google.analytics.tracking.android.Logger;

class DefaultLoggerImpl implements Logger {
    static final String LOG_TAG = "GAV3";
    private Logger.LogLevel mLogLevel = Logger.LogLevel.INFO;

    DefaultLoggerImpl() {
    }

    @Override // com.google.analytics.tracking.android.Logger
    public void verbose(String str) {
        if (this.mLogLevel.ordinal() <= Logger.LogLevel.VERBOSE.ordinal()) {
            Log.v(LOG_TAG, formatMessage(str));
        }
    }

    @Override // com.google.analytics.tracking.android.Logger
    public void info(String str) {
        if (this.mLogLevel.ordinal() <= Logger.LogLevel.INFO.ordinal()) {
            Log.i(LOG_TAG, formatMessage(str));
        }
    }

    @Override // com.google.analytics.tracking.android.Logger
    public void warn(String str) {
        if (this.mLogLevel.ordinal() <= Logger.LogLevel.WARNING.ordinal()) {
            Log.w(LOG_TAG, formatMessage(str));
        }
    }

    @Override // com.google.analytics.tracking.android.Logger
    public void error(String str) {
        if (this.mLogLevel.ordinal() <= Logger.LogLevel.ERROR.ordinal()) {
            Log.e(LOG_TAG, formatMessage(str));
        }
    }

    @Override // com.google.analytics.tracking.android.Logger
    public void setLogLevel(Logger.LogLevel logLevel) {
        this.mLogLevel = logLevel;
    }

    @Override // com.google.analytics.tracking.android.Logger
    public Logger.LogLevel getLogLevel() {
        return this.mLogLevel;
    }

    private String formatMessage(String str) {
        return Thread.currentThread().toString() + ": " + str;
    }
}
