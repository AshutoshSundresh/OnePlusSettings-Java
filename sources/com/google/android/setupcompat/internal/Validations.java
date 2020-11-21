package com.google.android.setupcompat.internal;

public final class Validations {
    public static void assertLengthInRange(int i, String str, int i2, int i3) {
        Preconditions.checkArgument(i <= i3 && i >= i2, String.format("Length of %s should be in the range [%s-%s]", str, Integer.valueOf(i2), Integer.valueOf(i3)));
    }

    public static void assertLengthInRange(String str, String str2, int i, int i2) {
        Preconditions.checkNotNull(str, String.format("%s cannot be null.", str2));
        assertLengthInRange(str.length(), str2, i, i2);
    }
}
