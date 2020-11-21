package com.google.android.material.checkbox;

import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.animation.PathInterpolator;
import com.oneplus.libs.FlipDrawable;

public class CheckableFlipDrawable extends FlipDrawable implements ValueAnimator.AnimatorUpdateListener {
    private final ValueAnimator mCheckmarkAlphaAnimator;
    private final CheckmarkDrawable mCheckmarkDrawable = ((CheckmarkDrawable) this.mBack);
    private final FrontDrawable mFrontDrawable = ((FrontDrawable) this.mFront);

    public CheckableFlipDrawable(Drawable drawable, Resources resources, int i, int i2, int i3) {
        super(new FrontDrawable(drawable), new CheckmarkDrawable(resources, i, i2), i3, 0, 150);
        long j = this.mPreFlipDurationMs;
        long j2 = this.mFlipDurationMs;
        ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration((j2 / 2) + this.mPostFlipDurationMs);
        this.mCheckmarkAlphaAnimator = duration;
        duration.setStartDelay(j + (j2 / 2));
        this.mCheckmarkAlphaAnimator.setInterpolator(new PathInterpolator(0.4f, 0.0f, 0.2f, 1.0f));
        this.mCheckmarkAlphaAnimator.addUpdateListener(this);
    }

    public void setFront(Drawable drawable) {
        this.mFrontDrawable.setInnerDrawable(drawable);
        invalidateSelf();
    }

    public void setCheckMarkBackgroundColor(int i) {
        this.mCheckmarkDrawable.setBackgroundColor(i);
        invalidateSelf();
    }

    public void setCheckMarkColor(int i) {
        this.mCheckmarkDrawable.setCheckMarkColor(i);
        invalidateSelf();
    }

    @Override // com.oneplus.libs.FlipDrawable
    public void reset() {
        super.reset();
        ValueAnimator valueAnimator = this.mCheckmarkAlphaAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.mCheckmarkDrawable.setAlphaAnimatorValue(getSideFlippingTowards() ? 0.0f : 1.0f);
        }
    }

    @Override // com.oneplus.libs.FlipDrawable
    public void flip() {
        super.flip();
        if (this.mCheckmarkAlphaAnimator.isStarted()) {
            this.mCheckmarkAlphaAnimator.reverse();
        } else if (!getSideFlippingTowards()) {
            this.mCheckmarkAlphaAnimator.start();
        } else {
            this.mCheckmarkAlphaAnimator.reverse();
        }
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        if (valueAnimator == this.mCheckmarkAlphaAnimator) {
            this.mCheckmarkDrawable.setAlphaAnimatorValue(floatValue);
        }
    }

    private static class FrontDrawable extends Drawable implements Drawable.Callback {
        private Drawable mDrawable;

        public FrontDrawable(Drawable drawable) {
            this.mDrawable = drawable;
            drawable.setCallback(this);
        }

        public void setInnerDrawable(Drawable drawable) {
            this.mDrawable.setCallback(null);
            this.mDrawable = drawable;
            drawable.setCallback(this);
            assignDrawableBounds(getBounds());
            invalidateSelf();
        }

        public void setTintMode(PorterDuff.Mode mode) {
            this.mDrawable.setTintMode(mode);
        }

        public void setTintList(ColorStateList colorStateList) {
            this.mDrawable.setTintList(colorStateList);
        }

        public boolean setState(int[] iArr) {
            return this.mDrawable.setState(iArr);
        }

        /* access modifiers changed from: protected */
        public void onBoundsChange(Rect rect) {
            super.onBoundsChange(rect);
            assignDrawableBounds(rect);
        }

        private void assignDrawableBounds(Rect rect) {
            int intrinsicWidth = this.mDrawable.getIntrinsicWidth();
            int intrinsicHeight = this.mDrawable.getIntrinsicHeight();
            if (intrinsicWidth <= 0 || intrinsicHeight <= 0) {
                this.mDrawable.setBounds(rect);
            } else {
                this.mDrawable.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
            }
        }

        public void draw(Canvas canvas) {
            Rect bounds = getBounds();
            if (isVisible() && !bounds.isEmpty()) {
                int intrinsicWidth = this.mDrawable.getIntrinsicWidth();
                int intrinsicHeight = this.mDrawable.getIntrinsicHeight();
                if (intrinsicWidth <= 0 || intrinsicHeight <= 0) {
                    this.mDrawable.draw(canvas);
                    return;
                }
                float max = Math.max(((float) bounds.width()) / ((float) intrinsicWidth), ((float) bounds.height()) / ((float) intrinsicHeight));
                canvas.save();
                canvas.scale(max, max);
                canvas.translate((float) bounds.left, (float) bounds.top);
                this.mDrawable.draw(canvas);
                canvas.restore();
            }
        }

        public void setAlpha(int i) {
            this.mDrawable.setAlpha(i);
        }

        public void setColorFilter(ColorFilter colorFilter) {
            this.mDrawable.setColorFilter(colorFilter);
        }

        public int getOpacity() {
            return this.mDrawable.getOpacity();
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
    }

    private static class CheckmarkDrawable extends Drawable {
        private static final Matrix MATRIX = new Matrix();
        private float mAlphaFraction;
        private int mCheckMarkColor;
        private Path mCheckMarkPath = null;
        private int mCheckMarkhight = 0;
        private int mCheckMarkwidth = 0;
        private final Paint mPaint;

        public int getOpacity() {
            return -1;
        }

        public CheckmarkDrawable(Resources resources, int i, int i2) {
            Paint paint = new Paint();
            this.mPaint = paint;
            paint.setAntiAlias(true);
            this.mPaint.setFilterBitmap(true);
            this.mPaint.setColor(i);
            this.mCheckMarkColor = i2;
        }

        public void setBackgroundColor(int i) {
            this.mPaint.setColor(i);
        }

        public void setCheckMarkColor(int i) {
            this.mCheckMarkColor = i;
        }

        public void draw(Canvas canvas) {
            Rect bounds = getBounds();
            if (isVisible() && !bounds.isEmpty()) {
                int i = bounds.right - bounds.left;
                int i2 = bounds.bottom - bounds.top;
                if (!(this.mCheckMarkwidth == i && this.mCheckMarkhight == i2)) {
                    this.mCheckMarkwidth = i;
                    this.mCheckMarkhight = i2;
                    float f = ((float) (i > i2 ? i2 : i)) / 126.0f;
                    Path path = new Path();
                    this.mCheckMarkPath = path;
                    float f2 = 55.14f * f;
                    float f3 = 76.11f * f;
                    path.moveTo(f2, f3);
                    this.mCheckMarkPath.lineTo(85.25f * f, 46.0f * f);
                    this.mCheckMarkPath.lineTo(88.03f * f, 48.78f * f);
                    float f4 = f * 81.05f;
                    this.mCheckMarkPath.cubicTo(f * 55.77f, f4, f * 54.52f, f4, f * 53.07f, f * 80.28f);
                    this.mCheckMarkPath.lineTo(38.64f * f, 64.53f * f);
                    this.mCheckMarkPath.lineTo(40.78f * f, f * 61.75f);
                    this.mCheckMarkPath.lineTo(f2, f3);
                }
                if (this.mAlphaFraction > 0.0f) {
                    float min = (((float) Math.min(i, i2)) - 0.9f) / 2.0f;
                    float f5 = this.mAlphaFraction;
                    if (((double) f5) <= 0.4d) {
                        drawBounds(canvas, bounds, min);
                    } else if (((double) f5) <= 0.4d || ((double) f5) >= 0.6d) {
                        canvas.drawARGB(0, 0, 0, 0);
                        canvas.drawCircle(((float) i) / 2.0f, ((float) i2) / 2.0f, min, this.mPaint);
                        drawCheckMark(canvas, bounds);
                    } else {
                        drawBounds(canvas, bounds, min);
                        drawCheckMark(canvas, bounds);
                    }
                }
            }
        }

        private void drawBounds(Canvas canvas, Rect rect, float f) {
            int i = rect.right - rect.left;
            int i2 = rect.bottom - rect.top;
            float f2 = f - ((this.mAlphaFraction * f) / 0.6f);
            this.mPaint.setStyle(Paint.Style.STROKE);
            this.mPaint.setStrokeWidth(f - f2);
            canvas.drawCircle(((float) i) / 2.0f, ((float) i2) / 2.0f, (f2 + f) / 2.0f, this.mPaint);
            this.mPaint.setStyle(Paint.Style.FILL);
        }

        private void drawCheckMark(Canvas canvas, Rect rect) {
            float f = (this.mAlphaFraction - 0.4f) / 0.6f;
            MATRIX.reset();
            MATRIX.setScale(f, f, (float) (this.mCheckMarkwidth / 2), (float) (this.mCheckMarkhight / 2));
            MATRIX.postTranslate((float) (rect.centerX() - (this.mCheckMarkwidth / 2)), (float) (rect.centerY() - (this.mCheckMarkhight / 2)));
            int color = this.mPaint.getColor();
            int alpha = this.mPaint.getAlpha();
            this.mPaint.setColor(this.mCheckMarkColor);
            this.mPaint.setAlpha((int) (((float) alpha) * f));
            Path path = new Path();
            path.addPath(this.mCheckMarkPath, MATRIX);
            canvas.drawPath(path, this.mPaint);
            this.mPaint.setColor(color);
            this.mPaint.setAlpha(alpha);
        }

        public void setAlpha(int i) {
            this.mPaint.setAlpha(i);
        }

        public void setColorFilter(ColorFilter colorFilter) {
            this.mPaint.setColorFilter(colorFilter);
        }

        public void setAlphaAnimatorValue(float f) {
            float f2 = this.mAlphaFraction;
            this.mAlphaFraction = f;
            if (f2 != f) {
                invalidateSelf();
            }
        }
    }
}
