package androidx.animation;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;

@SuppressLint({"NewApi"})
public class AnimatorUtils {
    public static final Interpolator FastOutLinearInInterpolator = new PathInterpolator(0.4f, 0.0f, 1.0f, 1.0f);
    public static final Interpolator FastOutLinearInInterpolatorSine = new PathInterpolator(0.4f, 0.0f, 0.6f, 1.0f);
    public static final Interpolator FastOutSlowInInterpolator = new PathInterpolator(0.4f, 0.0f, 0.2f, 1.0f);
    public static final Interpolator GRID_ITEM_ANIMATION_INTERPOLATOR = new PathInterpolator(0.4f, 0.0f, 0.3f, 1.0f);
    public static final Interpolator LinearOutSlowInInterpolator = new PathInterpolator(0.0f, 0.0f, 0.2f, 1.0f);
    public static final Interpolator op__control_interpolator_fast_out_linear_in = new PathInterpolator(0.4f, 0.0f, 1.0f, 1.0f);
    public static final Interpolator op_control_interpolator_fast_out_slow_in_auxiliary = new PathInterpolator(0.33f, 0.0f, 0.67f, 1.0f);
    public static final Interpolator op_control_interpolator_linear_out_slow_in = new PathInterpolator(0.0f, 0.0f, 0.3f, 1.0f);

    static {
        new PathInterpolator(0.4f, 0.0f, 0.3f, 1.0f);
        new PathInterpolator(0.7f, 0.0f, 0.6f, 1.0f);
        new Bundle();
    }
}
