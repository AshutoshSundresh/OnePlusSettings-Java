package androidx.leanback.widget;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.leanback.R$id;

public final class ShadowOverlayHelper {
    public static void setNoneWrapperOverlayColor(View view, int i) {
        Drawable foreground = ForegroundHelper.getForeground(view);
        if (foreground instanceof ColorDrawable) {
            ((ColorDrawable) foreground).setColor(i);
        } else {
            ForegroundHelper.setForeground(view, new ColorDrawable(i));
        }
    }

    public static void setNoneWrapperShadowFocusLevel(View view, float f) {
        setShadowFocusLevel(getNoneWrapperDynamicShadowImpl(view), 3, f);
    }

    static Object getNoneWrapperDynamicShadowImpl(View view) {
        return view.getTag(R$id.lb_shadow_impl);
    }

    static void setShadowFocusLevel(Object obj, int i, float f) {
        if (obj != null) {
            if (f < 0.0f) {
                f = 0.0f;
            } else if (f > 1.0f) {
                f = 1.0f;
            }
            if (i == 2) {
                StaticShadowHelper.setShadowFocusLevel(obj, f);
            } else if (i == 3) {
                ShadowHelper.setShadowFocusLevel(obj, f);
            }
        }
    }
}
