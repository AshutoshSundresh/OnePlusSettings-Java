package com.google.protobuf;

/* access modifiers changed from: package-private */
public final class Android {
    private static final boolean IS_ROBOLECTRIC = (getClassForName("org.robolectric.Robolectric") != null);
    private static final Class<?> MEMORY_CLASS = getClassForName("libcore.io.Memory");

    static boolean isOnAndroidDevice() {
        return MEMORY_CLASS != null && !IS_ROBOLECTRIC;
    }

    private static <T> Class<T> getClassForName(String str) {
        try {
            return (Class<T>) Class.forName(str);
        } catch (Throwable unused) {
            return null;
        }
    }
}
