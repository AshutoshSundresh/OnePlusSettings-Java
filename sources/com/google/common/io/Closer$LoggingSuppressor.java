package com.google.common.io;

final class Closer$LoggingSuppressor implements Closer$Suppressor {
    Closer$LoggingSuppressor() {
    }

    static {
        new Closer$LoggingSuppressor();
    }
}
