package com.google.common.primitives;

import com.google.common.base.Preconditions;
import java.util.Arrays;

public final class Longs {
    public static int compare(long j, long j2) {
        int i = (j > j2 ? 1 : (j == j2 ? 0 : -1));
        if (i < 0) {
            return -1;
        }
        return i > 0 ? 1 : 0;
    }

    /* access modifiers changed from: package-private */
    public static final class AsciiDigits {
        private static final byte[] asciiDigits;

        static {
            byte[] bArr = new byte[128];
            Arrays.fill(bArr, (byte) -1);
            for (int i = 0; i <= 9; i++) {
                bArr[i + 48] = (byte) i;
            }
            for (int i2 = 0; i2 <= 26; i2++) {
                byte b = (byte) (i2 + 10);
                bArr[i2 + 65] = b;
                bArr[i2 + 97] = b;
            }
            asciiDigits = bArr;
        }

        static int digit(char c) {
            if (c < 128) {
                return asciiDigits[c];
            }
            return -1;
        }
    }

    public static Long tryParse(String str, int i) {
        Preconditions.checkNotNull(str);
        if (str.isEmpty()) {
            return null;
        }
        if (i < 2 || i > 36) {
            throw new IllegalArgumentException("radix must be between MIN_RADIX and MAX_RADIX but was " + i);
        }
        int i2 = 0;
        if (str.charAt(0) == '-') {
            i2 = 1;
        }
        if (i2 == str.length()) {
            return null;
        }
        int i3 = i2 + 1;
        int digit = AsciiDigits.digit(str.charAt(i2));
        if (digit < 0 || digit >= i) {
            return null;
        }
        long j = (long) (-digit);
        long j2 = (long) i;
        long j3 = Long.MIN_VALUE / j2;
        while (i3 < str.length()) {
            int i4 = i3 + 1;
            int digit2 = AsciiDigits.digit(str.charAt(i3));
            if (digit2 < 0 || digit2 >= i || j < j3) {
                return null;
            }
            long j4 = j * j2;
            long j5 = (long) digit2;
            if (j4 < j5 - Long.MIN_VALUE) {
                return null;
            }
            j = j4 - j5;
            i3 = i4;
        }
        if (i2 != 0) {
            return Long.valueOf(j);
        }
        if (j == Long.MIN_VALUE) {
            return null;
        }
        return Long.valueOf(-j);
    }
}
