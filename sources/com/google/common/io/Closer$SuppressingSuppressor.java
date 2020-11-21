package com.google.common.io;

import java.lang.reflect.Method;

final class Closer$SuppressingSuppressor implements Closer$Suppressor {
    Closer$SuppressingSuppressor() {
    }

    static {
        new Closer$SuppressingSuppressor();
        getAddSuppressed();
    }

    private static Method getAddSuppressed() {
        try {
            return Throwable.class.getMethod("addSuppressed", Throwable.class);
        } catch (Throwable unused) {
            return null;
        }
    }
}
