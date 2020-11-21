package androidx.leanback.widget;

import android.os.Build;

/* access modifiers changed from: package-private */
public final class ShadowHelper {
    static boolean supportsDynamicShadow() {
        return Build.VERSION.SDK_INT >= 21;
    }

    static void setShadowFocusLevel(Object obj, float f) {
        if (Build.VERSION.SDK_INT >= 21) {
            ShadowHelperApi21.setShadowFocusLevel(obj, f);
        }
    }
}
