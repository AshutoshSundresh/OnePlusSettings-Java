package androidx.slice;

import androidx.core.util.ObjectsCompat;
import java.lang.reflect.Array;

class ArrayUtils {
    public static <T> boolean contains(T[] tArr, T t) {
        for (T t2 : tArr) {
            if (ObjectsCompat.equals(t2, t)) {
                return true;
            }
        }
        return false;
    }

    public static <T> T[] appendElement(Class<T> cls, T[] tArr, T t) {
        T[] tArr2;
        int i = 0;
        if (tArr != null) {
            int length = tArr.length;
            tArr2 = (T[]) ((Object[]) Array.newInstance((Class<?>) cls, length + 1));
            System.arraycopy(tArr, 0, tArr2, 0, length);
            i = length;
        } else {
            tArr2 = (T[]) ((Object[]) Array.newInstance((Class<?>) cls, 1));
        }
        tArr2[i] = t;
        return tArr2;
    }

    public static <T> T[] removeElement(Class<T> cls, T[] tArr, T t) {
        if (tArr == null || !contains(tArr, t)) {
            return tArr;
        }
        int length = tArr.length;
        for (int i = 0; i < length; i++) {
            if (ObjectsCompat.equals(tArr[i], t)) {
                if (length == 1) {
                    return null;
                } else {
                    T[] tArr2 = (T[]) ((Object[]) Array.newInstance((Class<?>) cls, length - 1));
                    System.arraycopy(tArr, 0, tArr2, 0, i);
                    System.arraycopy(tArr, i + 1, tArr2, i, (length - i) - 1);
                    return tArr2;
                }
            }
        }
        return tArr;
    }
}
