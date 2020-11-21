package androidx.core.os;

import android.os.Build;

public class BuildCompat {
    public static boolean isAtLeastR() {
        return Build.VERSION.SDK_INT >= 30 || Build.VERSION.CODENAME.equals("R");
    }
}
