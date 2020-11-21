package com.google.tagmanager;

public interface Logger {

    public enum LogLevel {
        VERBOSE,
        DEBUG,
        INFO,
        WARNING,
        ERROR,
        NONE
    }

    void e(String str);

    void w(String str);
}
