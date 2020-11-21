package com.oneplus.libs;

import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class FlipDrawable extends Drawable implements Drawable.Callback {
    protected final Drawable mBack;
    private final ValueAnimator mFlipAnimator;
    protected final long mFlipDurationMs;
    private float mFlipFraction = 0.0f;
    private boolean mFlipToSide = true;
    protected Drawable mFront;
    protected final long mPostFlipDurationMs;
    protected final long mPreFlipDurationMs;

    public FlipDrawable(Drawable drawable, Drawable drawable2, int i, int i2, int i3) {
        if (drawable == null || drawable2 == null) {
            throw new IllegalArgumentException("Front and back drawables must not be null.");
        }
        this.mFront = drawable;
        this.mBack = drawable2;
        drawable.setCallback(this);
        this.mBack.setCallback(this);
        this.mFlipDurationMs = (long) i;
        this.mPreFlipDurationMs = (long) i2;
        this.mPostFlipDurationMs = (long) i3;
        ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 2.0f).setDuration(this.mPreFlipDurationMs + this.mFlipDurationMs + this.mPostFlipDurationMs);
        this.mFlipAnimator = duration;
        duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            /* class com.oneplus.libs.FlipDrawable.AnonymousClass1 */

            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float f = FlipDrawable.this.mFlipFraction;
                FlipDrawable.this.mFlipFraction = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                if (f != FlipDrawable.this.mFlipFraction) {
                    FlipDrawable.this.invalidateSelf();
                }
            }
        });
        reset();
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect rect) {
        super.onBoundsChange(rect);
        if (rect.isEmpty()) {
            this.mFront.setBounds(0, 0, 0, 0);
            this.mBack.setBounds(0, 0, 0, 0);
            return;
        }
        this.mFront.setBounds(rect);
        this.mBack.setBounds(rect);
    }

    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        if (isVisible() && !bounds.isEmpty()) {
            canvas.save();
            this.mFront.draw(canvas);
            this.mBack.draw(canvas);
            canvas.restore();
        }
    }

    public void setAlpha(int i) {
        this.mFront.setAlpha(i);
        this.mBack.setAlpha(i);
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.mFront.setColorFilter(colorFilter);
        this.mBack.setColorFilter(colorFilter);
    }

    public int getOpacity() {
        return Drawable.resolveOpacity(this.mFront.getOpacity(), this.mBack.getOpacity());
    }

    /* access modifiers changed from: protected */
    public boolean onLevelChange(int i) {
        return this.mFront.setLevel(i) || this.mBack.setLevel(i);
    }

    public void invalidateDrawable(Drawable drawable) {
        invalidateSelf();
    }

    public void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
        scheduleSelf(runnable, j);
    }

    public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
        unscheduleSelf(runnable);
    }

    public void reset() {
        float f = this.mFlipFraction;
        this.mFlipAnimator.cancel();
        float f2 = this.mFlipToSide ? 0.0f : 2.0f;
        this.mFlipFraction = f2;
        if (f2 != f) {
            invalidateSelf();
        }
    }

    public boolean getSideFlippingTowards() {
        return this.mFlipToSide;
    }

    public void flip() {
        this.mFlipToSide = !this.mFlipToSide;
        if (this.mFlipAnimator.isStarted()) {
            this.mFlipAnimator.reverse();
        } else if (!this.mFlipToSide) {
            this.mFlipAnimator.start();
        } else {
            this.mFlipAnimator.reverse();
        }
    }

    public void flipTo(boolean z) {
        if (this.mFlipToSide != z) {
            flip();
        }
    }

    public boolean isStateful() {
        return this.mFront.isStateful() || this.mBack.isStateful();
    }

    public boolean setState(int[] iArr) {
        return this.mFront.setState(iArr) || this.mBack.setState(iArr);
    }

    public void setTintMode(PorterDuff.Mode mode) {
        this.mFront.setTintMode(mode);
        this.mBack.setTintMode(mode);
    }

    public void setTintList(ColorStateList colorStateList) {
        this.mFront.setTintList(colorStateList);
        this.mBack.setTintList(colorStateList);
    }
}
