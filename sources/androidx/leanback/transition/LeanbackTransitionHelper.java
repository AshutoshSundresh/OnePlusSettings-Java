package androidx.leanback.transition;

import android.content.Context;
import android.os.Build;
import android.view.animation.AnimationUtils;
import androidx.leanback.R$anim;
import androidx.leanback.R$id;
import androidx.leanback.R$transition;

public class LeanbackTransitionHelper {
    public static Object loadTitleInTransition(Context context) {
        int i = Build.VERSION.SDK_INT;
        if (i < 19 || i >= 21) {
            return TransitionHelper.loadTransition(context, R$transition.lb_title_in);
        }
        SlideKitkat slideKitkat = new SlideKitkat();
        slideKitkat.setSlideEdge(48);
        slideKitkat.setInterpolator(AnimationUtils.loadInterpolator(context, 17432582));
        slideKitkat.addTarget(R$id.browse_title_group);
        return slideKitkat;
    }

    public static Object loadTitleOutTransition(Context context) {
        int i = Build.VERSION.SDK_INT;
        if (i < 19 || i >= 21) {
            return TransitionHelper.loadTransition(context, R$transition.lb_title_out);
        }
        SlideKitkat slideKitkat = new SlideKitkat();
        slideKitkat.setSlideEdge(48);
        slideKitkat.setInterpolator(AnimationUtils.loadInterpolator(context, R$anim.lb_decelerator_4));
        slideKitkat.addTarget(R$id.browse_title_group);
        return slideKitkat;
    }
}
