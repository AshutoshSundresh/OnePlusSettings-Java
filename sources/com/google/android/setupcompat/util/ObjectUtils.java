package com.google.android.setupcompat.util;

import java.util.Arrays;

public final class ObjectUtils {
    public static int hashCode(Object... objArr) {
        return Arrays.hashCode(objArr);
    }

    public static boolean equals(Object obj, Object obj2) {
        return obj == obj2 || (obj != null && obj.equals(obj2));
    }
}
