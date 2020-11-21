package androidx.leanback.graphics;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.leanback.R$color;
import androidx.leanback.R$fraction;
import androidx.leanback.R$styleable;

public final class ColorOverlayDimmer {
    private final float mActiveLevel;
    private int mAlpha;
    private float mAlphaFloat;
    private final float mDimmedLevel;
    private final Paint mPaint;

    public static ColorOverlayDimmer createDefault(Context context) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(R$styleable.LeanbackTheme);
        int color = obtainStyledAttributes.getColor(R$styleable.LeanbackTheme_overlayDimMaskColor, context.getResources().getColor(R$color.lb_view_dim_mask_color));
        float fraction = obtainStyledAttributes.getFraction(R$styleable.LeanbackTheme_overlayDimActiveLevel, 1, 1, context.getResources().getFraction(R$fraction.lb_view_active_level, 1, 0));
        float fraction2 = obtainStyledAttributes.getFraction(R$styleable.LeanbackTheme_overlayDimDimmedLevel, 1, 1, context.getResources().getFraction(R$fraction.lb_view_dimmed_level, 1, 1));
        obtainStyledAttributes.recycle();
        return new ColorOverlayDimmer(color, fraction, fraction2);
    }

    private ColorOverlayDimmer(int i, float f, float f2) {
        f = f > 1.0f ? 1.0f : f;
        float f3 = 0.0f;
        f = f < 0.0f ? 0.0f : f;
        f2 = f2 > 1.0f ? 1.0f : f2;
        f3 = f2 >= 0.0f ? f2 : f3;
        this.mPaint = new Paint();
        this.mPaint.setColor(Color.rgb(Color.red(i), Color.green(i), Color.blue(i)));
        this.mActiveLevel = f;
        this.mDimmedLevel = f3;
        setActiveLevel(1.0f);
    }

    public void setActiveLevel(float f) {
        float f2 = this.mDimmedLevel;
        float f3 = f2 + (f * (this.mActiveLevel - f2));
        this.mAlphaFloat = f3;
        int i = (int) (f3 * 255.0f);
        this.mAlpha = i;
        this.mPaint.setAlpha(i);
    }

    public Paint getPaint() {
        return this.mPaint;
    }
}
