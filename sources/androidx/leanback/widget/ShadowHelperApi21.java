package androidx.leanback.widget;

import android.graphics.Outline;
import android.view.View;
import android.view.ViewOutlineProvider;

/* access modifiers changed from: package-private */
public class ShadowHelperApi21 {

    static class ShadowImpl {
        float mFocusedZ;
        float mNormalZ;
        View mShadowContainer;

        ShadowImpl() {
        }
    }

    static {
        new ViewOutlineProvider() {
            /* class androidx.leanback.widget.ShadowHelperApi21.AnonymousClass1 */

            public void getOutline(View view, Outline outline) {
                outline.setRect(0, 0, view.getWidth(), view.getHeight());
                outline.setAlpha(1.0f);
            }
        };
    }

    public static void setShadowFocusLevel(Object obj, float f) {
        ShadowImpl shadowImpl = (ShadowImpl) obj;
        View view = shadowImpl.mShadowContainer;
        float f2 = shadowImpl.mNormalZ;
        view.setZ(f2 + (f * (shadowImpl.mFocusedZ - f2)));
    }
}
