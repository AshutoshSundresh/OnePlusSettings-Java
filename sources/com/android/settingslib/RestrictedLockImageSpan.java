package com.android.settingslib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

public class RestrictedLockImageSpan extends ImageSpan {
    private Context mContext;
    private final float mExtraPadding;
    private final Drawable mRestrictedPadlock = RestrictedLockUtilsInternal.getRestrictedPadlock(this.mContext);

    public RestrictedLockImageSpan(Context context) {
        super((Drawable) null);
        this.mContext = context;
        this.mExtraPadding = (float) context.getResources().getDimensionPixelSize(R$dimen.restricted_icon_padding);
    }

    public Drawable getDrawable() {
        return this.mRestrictedPadlock;
    }

    public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
        Drawable drawable = getDrawable();
        canvas.save();
        canvas.translate(f + this.mExtraPadding, ((float) (i5 - drawable.getBounds().bottom)) / 2.0f);
        drawable.draw(canvas);
        canvas.restore();
    }

    public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
        return (int) (((float) super.getSize(paint, charSequence, i, i2, fontMetricsInt)) + (this.mExtraPadding * 2.0f));
    }
}
