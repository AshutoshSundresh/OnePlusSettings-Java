package com.google.android.material.indicator.animation.type;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import androidx.animation.AnimatorUtils;
import com.google.android.material.indicator.animation.controller.ValueController;
import com.google.android.material.indicator.animation.data.WormAnimationValue;
import java.util.Iterator;

public class WormAnimation extends BaseAnimation<AnimatorSet> {
    private int coordinateEnd;
    private int coordinateStart;
    private boolean isRightSide;
    private int radius;
    private int rectLeftEdge;
    private int rectRightEdge;
    private WormAnimationValue value = new WormAnimationValue();

    public WormAnimation(ValueController.UpdateListener updateListener) {
        super(updateListener);
    }

    @Override // com.google.android.material.indicator.animation.type.BaseAnimation
    public AnimatorSet createAnimator() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        return animatorSet;
    }

    @Override // com.google.android.material.indicator.animation.type.BaseAnimation
    public WormAnimation duration(long j) {
        super.duration(j);
        return this;
    }

    public WormAnimation with(int i, int i2, int i3, boolean z) {
        if (hasChanges(i, i2, i3, z)) {
            this.animator = createAnimator();
            this.coordinateStart = i;
            this.coordinateEnd = i2;
            this.radius = i3;
            this.isRightSide = z;
            int i4 = i - i3;
            this.rectLeftEdge = i4;
            this.rectRightEdge = i + i3;
            this.value.setRectStart(i4);
            this.value.setRectEnd(this.rectRightEdge);
            RectValues createRectValues = createRectValues(z);
            boolean z2 = Math.abs(createRectValues.fromX - createRectValues.toX) > i3 * 10;
            ValueAnimator createWormAnimator = createWormAnimator(createRectValues.fromX, createRectValues.toX, false, this.value, z2, createRectValues.reverseToX);
            int i5 = createRectValues.reverseFromX;
            int i6 = createRectValues.reverseToX;
            ValueAnimator createWormAnimator2 = createWormAnimator(i5, i6, true, this.value, z2, i6);
            if (!z2) {
                ((AnimatorSet) this.animator).playSequentially(createWormAnimator, createWormAnimator2);
            } else {
                ((AnimatorSet) this.animator).play(createWormAnimator);
            }
        }
        return this;
    }

    @Override // com.google.android.material.indicator.animation.type.BaseAnimation
    public WormAnimation progress(float f) {
        T t = this.animator;
        if (t == null) {
            return this;
        }
        long j = (long) (f * ((float) this.animationDuration));
        Iterator<Animator> it = ((AnimatorSet) t).getChildAnimations().iterator();
        while (it.hasNext()) {
            ValueAnimator valueAnimator = (ValueAnimator) it.next();
            long duration = valueAnimator.getDuration();
            if (j <= duration) {
                duration = j;
            }
            valueAnimator.setCurrentPlayTime(duration);
            j -= duration;
        }
        return this;
    }

    private ValueAnimator createWormAnimator(int i, int i2, final boolean z, final WormAnimationValue wormAnimationValue, final boolean z2, final int i3) {
        ValueAnimator ofInt = ValueAnimator.ofInt(i, i2);
        ofInt.setInterpolator(AnimatorUtils.op_control_interpolator_fast_out_slow_in_auxiliary);
        if (z2) {
            ofInt.setDuration(225L);
        } else {
            ofInt.setDuration(125L);
        }
        ofInt.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            /* class com.google.android.material.indicator.animation.type.WormAnimation.AnonymousClass1 */

            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                WormAnimation.this.onAnimateUpdated(wormAnimationValue, valueAnimator, z, z2, i3);
            }
        });
        return ofInt;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void onAnimateUpdated(WormAnimationValue wormAnimationValue, ValueAnimator valueAnimator, boolean z, boolean z2, int i) {
        int intValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        float animatedFraction = valueAnimator.getAnimatedFraction();
        if (this.isRightSide) {
            if (z) {
                wormAnimationValue.setRectStart(intValue);
            } else if (!z2) {
                wormAnimationValue.setRectEnd(intValue);
            } else if (Math.abs(wormAnimationValue.getRectEnd() - wormAnimationValue.getRectStart()) <= (this.radius * 2) + 10) {
                wormAnimationValue.setRectEnd(intValue);
            } else if (animatedFraction < 0.6f) {
                wormAnimationValue.setRectStart(wormAnimationValue.getRectStart() + Math.abs(wormAnimationValue.getRectEnd() - intValue));
                wormAnimationValue.setRectEnd(intValue);
            } else if (animatedFraction < 0.9f) {
                wormAnimationValue.setRectStart(wormAnimationValue.getRectStart() + (Math.abs(wormAnimationValue.getRectEnd() - intValue) / 2));
                wormAnimationValue.setRectEnd(intValue);
            } else if (animatedFraction <= 1.0f) {
                wormAnimationValue.setRectStart(i);
                wormAnimationValue.setRectEnd(intValue);
            }
        } else if (!z) {
            if (!z2) {
                wormAnimationValue.setRectStart(intValue);
            } else if (Math.abs(wormAnimationValue.getRectEnd() - wormAnimationValue.getRectStart()) <= (this.radius * 2) + 10) {
                wormAnimationValue.setRectStart(intValue);
            } else if (animatedFraction < 0.6f) {
                wormAnimationValue.setRectEnd(wormAnimationValue.getRectEnd() - Math.abs(wormAnimationValue.getRectStart() - intValue));
                wormAnimationValue.setRectStart(intValue);
            } else if (animatedFraction < 0.9f) {
                wormAnimationValue.setRectEnd(wormAnimationValue.getRectEnd() - (Math.abs(wormAnimationValue.getRectStart() - intValue) * 2));
                wormAnimationValue.setRectStart(intValue);
            } else if (animatedFraction <= 1.0f) {
                wormAnimationValue.setRectStart(intValue);
                wormAnimationValue.setRectEnd(i);
            }
            wormAnimationValue.setRectStart(intValue);
        } else {
            wormAnimationValue.setRectEnd(intValue);
        }
        ValueController.UpdateListener updateListener = this.listener;
        if (updateListener != null) {
            updateListener.onValueUpdated(wormAnimationValue);
        }
    }

    private boolean hasChanges(int i, int i2, int i3, boolean z) {
        if (this.coordinateStart == i && this.coordinateEnd == i2 && this.radius == i3 && this.isRightSide == z) {
            return false;
        }
        return true;
    }

    private RectValues createRectValues(boolean z) {
        int i;
        int i2;
        int i3;
        int i4;
        if (z) {
            int i5 = this.coordinateStart;
            int i6 = this.radius;
            i4 = i5 + i6;
            int i7 = this.coordinateEnd;
            i3 = i7 + i6;
            i = i5 - i6;
            i2 = i7 - i6;
        } else {
            int i8 = this.coordinateStart;
            int i9 = this.radius;
            i4 = i8 - i9;
            int i10 = this.coordinateEnd;
            i3 = i10 - i9;
            i = i8 + i9;
            i2 = i10 + i9;
        }
        return new RectValues(i4, i3, i, i2);
    }

    /* access modifiers changed from: package-private */
    public static class RectValues {
        final int fromX;
        final int reverseFromX;
        final int reverseToX;
        final int toX;

        RectValues(int i, int i2, int i3, int i4) {
            this.fromX = i;
            this.toX = i2;
            this.reverseFromX = i3;
            this.reverseToX = i4;
        }
    }
}
