package androidx.leanback.widget;

import android.os.Build;
import android.view.View;
import androidx.leanback.R$dimen;

final class RoundedRectHelper {
    static void setClipToRoundedOutline(View view, boolean z) {
        if (Build.VERSION.SDK_INT >= 21) {
            RoundedRectHelperApi21.setClipToRoundedOutline(view, z, view.getResources().getDimensionPixelSize(R$dimen.lb_rounded_rect_corner_radius));
        }
    }
}
