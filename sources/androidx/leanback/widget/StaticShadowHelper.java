package androidx.leanback.widget;

import android.os.Build;
import android.view.View;

/* access modifiers changed from: package-private */
public final class StaticShadowHelper {
    static boolean supportsShadow() {
        return Build.VERSION.SDK_INT >= 21;
    }

    static void setShadowFocusLevel(Object obj, float f) {
        if (Build.VERSION.SDK_INT >= 21) {
            ShadowImpl shadowImpl = (ShadowImpl) obj;
            shadowImpl.mNormalShadow.setAlpha(1.0f - f);
            shadowImpl.mFocusShadow.setAlpha(f);
        }
    }

    /* access modifiers changed from: package-private */
    public static class ShadowImpl {
        View mFocusShadow;
        View mNormalShadow;

        ShadowImpl() {
        }
    }
}
