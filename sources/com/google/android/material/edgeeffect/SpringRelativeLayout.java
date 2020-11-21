package com.google.android.material.edgeeffect;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.EdgeEffect;
import android.widget.RelativeLayout;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

public class SpringRelativeLayout extends RelativeLayout {
    private static final FloatPropertyCompat<SpringRelativeLayout> DAMPED_SCROLL = new FloatPropertyCompat<SpringRelativeLayout>("value") {
        /* class com.google.android.material.edgeeffect.SpringRelativeLayout.AnonymousClass1 */

        public float getValue(SpringRelativeLayout springRelativeLayout) {
            return springRelativeLayout.mDampedScrollShift;
        }

        public void setValue(SpringRelativeLayout springRelativeLayout, float f) {
            springRelativeLayout.setDampedScrollShift(f);
        }
    };
    private SpringEdgeEffect mActiveEdge;
    private DynamicAnimation.OnAnimationEndListener mAnimationEndListener;
    private float mDampedScrollShift;
    private float mDamping;
    private boolean mDisableEffectBottom;
    private boolean mDisableEffectTop;
    private float mDistance;
    private boolean mHorizontal;
    private int mPullCount;
    private boolean mReadyToGo;
    private final SpringAnimation mSpring;
    protected final SparseBooleanArray mSpringViews;
    private float mStif;
    private float mVelocity_multiplier;

    public int getCanvasClipLeftForOverscroll() {
        return 0;
    }

    public int getCanvasClipTopForOverscroll() {
        return 0;
    }

    static /* synthetic */ int access$608(SpringRelativeLayout springRelativeLayout) {
        int i = springRelativeLayout.mPullCount;
        springRelativeLayout.mPullCount = i + 1;
        return i;
    }

    public SpringRelativeLayout(Context context) {
        this(context, null);
    }

    public SpringRelativeLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public SpringRelativeLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mVelocity_multiplier = 0.3f;
        this.mStif = 590.0f;
        this.mDamping = 0.5f;
        this.mDisableEffectTop = false;
        this.mDisableEffectBottom = false;
        this.mSpringViews = new SparseBooleanArray();
        this.mDampedScrollShift = 0.0f;
        this.mHorizontal = false;
        this.mDistance = 0.0f;
        this.mPullCount = 0;
        SpringAnimation springAnimation = new SpringAnimation(this, DAMPED_SCROLL, 0.0f);
        this.mSpring = springAnimation;
        SpringForce springForce = new SpringForce(0.0f);
        springForce.setStiffness(590.0f);
        springForce.setDampingRatio(0.5f);
        springAnimation.setSpring(springForce);
    }

    public void addSpringView(int i) {
        this.mSpringViews.put(i, true);
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View view, long j) {
        if (this.mDampedScrollShift == 0.0f || !this.mSpringViews.get(view.getId())) {
            return super.drawChild(canvas, view, j);
        }
        int save = canvas.save();
        if (this.mHorizontal) {
            canvas.clipRect(getCanvasClipLeftForOverscroll(), 0, getWidth(), getHeight());
            canvas.translate(this.mDampedScrollShift, 0.0f);
        } else {
            canvas.clipRect(0, getCanvasClipTopForOverscroll(), getWidth(), getHeight());
            canvas.translate(0.0f, this.mDampedScrollShift);
        }
        boolean drawChild = super.drawChild(canvas, view, j);
        canvas.restoreToCount(save);
        return drawChild;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setActiveEdge(SpringEdgeEffect springEdgeEffect) {
        SpringEdgeEffect springEdgeEffect2 = this.mActiveEdge;
        this.mActiveEdge = springEdgeEffect;
    }

    /* access modifiers changed from: protected */
    public void setDampedScrollShift(float f) {
        if (f != this.mDampedScrollShift) {
            this.mDampedScrollShift = f;
            invalidate();
        }
    }

    public void onRecyclerViewScrolled() {
        if (this.mPullCount != 1 && !this.mSpring.isRunning()) {
            this.mDistance = 0.0f;
            this.mPullCount = 0;
            finishScrollWithVelocity(0.0f);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void finishScrollWithVelocity(float f) {
        float f2 = this.mDampedScrollShift;
        if (f2 > Float.MAX_VALUE || f2 < -3.4028235E38f) {
            Log.e("SpringRelativeLayout", "animation parameter out of range!");
        } else if (f > 0.0f && this.mDisableEffectTop) {
        } else {
            if (f >= 0.0f || !this.mDisableEffectBottom) {
                DynamicAnimation.OnAnimationEndListener onAnimationEndListener = this.mAnimationEndListener;
                if (onAnimationEndListener != null) {
                    this.mSpring.addEndListener(onAnimationEndListener);
                }
                this.mSpring.setStartVelocity(f);
                this.mSpring.setStartValue(this.mDampedScrollShift);
                this.mSpring.start();
            }
        }
    }

    /* access modifiers changed from: private */
    public class SpringEdgeEffect extends EdgeEffect {
        private boolean mReleased = true;
        private final float mVelocityMultiplier;

        public boolean draw(Canvas canvas) {
            return false;
        }

        public SpringEdgeEffect(Context context, float f) {
            super(context);
            this.mVelocityMultiplier = f;
        }

        public void onAbsorb(int i) {
            SpringRelativeLayout.this.finishScrollWithVelocity(((float) i) * this.mVelocityMultiplier);
            SpringRelativeLayout.this.mDistance = 0.0f;
        }

        public void onPull(float f, float f2) {
            if (SpringRelativeLayout.this.mSpring.isRunning()) {
                SpringRelativeLayout.this.mSpring.cancel();
            }
            SpringRelativeLayout.access$608(SpringRelativeLayout.this);
            SpringRelativeLayout.this.setActiveEdge(this);
            SpringRelativeLayout.this.mDistance += f * (this.mVelocityMultiplier / 3.0f);
            if (SpringRelativeLayout.this.mDistance > 0.0f && SpringRelativeLayout.this.mDisableEffectTop) {
                SpringRelativeLayout.this.mDistance = 0.0f;
            } else if (SpringRelativeLayout.this.mDistance < 0.0f && SpringRelativeLayout.this.mDisableEffectBottom) {
                SpringRelativeLayout.this.mDistance = 0.0f;
            } else if (SpringRelativeLayout.this.mHorizontal) {
                SpringRelativeLayout springRelativeLayout = SpringRelativeLayout.this;
                springRelativeLayout.setDampedScrollShift(springRelativeLayout.mDistance * ((float) SpringRelativeLayout.this.getWidth()));
            } else {
                SpringRelativeLayout springRelativeLayout2 = SpringRelativeLayout.this;
                springRelativeLayout2.setDampedScrollShift(springRelativeLayout2.mDistance * ((float) SpringRelativeLayout.this.getHeight()));
            }
            this.mReleased = false;
        }

        public void onRelease() {
            if (!this.mReleased) {
                SpringRelativeLayout.this.mDistance = 0.0f;
                SpringRelativeLayout.this.mPullCount = 0;
                if (((double) SpringRelativeLayout.this.mDampedScrollShift) != 0.0d) {
                    SpringRelativeLayout.this.mReadyToGo = false;
                }
                SpringRelativeLayout.this.finishScrollWithVelocity(0.0f);
                this.mReleased = true;
            }
        }
    }

    /* access modifiers changed from: private */
    public class ViewEdgeEffectFactory extends SEdgeEffectFactory {
        private ViewEdgeEffectFactory() {
        }

        /* access modifiers changed from: protected */
        @Override // com.google.android.material.edgeeffect.SpringRelativeLayout.SEdgeEffectFactory
        public EdgeEffect createEdgeEffect(View view, int i) {
            if (i == 0 || i == 1) {
                SpringRelativeLayout springRelativeLayout = SpringRelativeLayout.this;
                return new SpringEdgeEffect(springRelativeLayout.getContext(), SpringRelativeLayout.this.mVelocity_multiplier);
            } else if (i != 2 && i != 3) {
                return super.createEdgeEffect(view, i);
            } else {
                SpringRelativeLayout springRelativeLayout2 = SpringRelativeLayout.this;
                return new SpringEdgeEffect(springRelativeLayout2.getContext(), -SpringRelativeLayout.this.mVelocity_multiplier);
            }
        }
    }

    public ViewEdgeEffectFactory createViewEdgeEffectFactory() {
        return createViewEdgeEffectFactory(false);
    }

    public ViewEdgeEffectFactory createViewEdgeEffectFactory(boolean z) {
        this.mHorizontal = z;
        return new ViewEdgeEffectFactory();
    }

    public static class SEdgeEffectFactory {
        /* access modifiers changed from: protected */
        public EdgeEffect createEdgeEffect(View view, int i) {
            return new EdgeEffect(view.getContext());
        }
    }

    public void setAnimationEndListener(DynamicAnimation.OnAnimationEndListener onAnimationEndListener) {
        this.mAnimationEndListener = onAnimationEndListener;
    }

    public void setVelocityMultiplier(float f) {
        this.mVelocity_multiplier = f;
    }

    public void setStiffness(float f) {
        this.mStif = (1500.0f * f) + ((1.0f - f) * 200.0f);
        this.mSpring.getSpring().setStiffness(this.mStif);
    }

    public void setBouncy(float f) {
        this.mDamping = f;
        this.mSpring.getSpring().setDampingRatio(this.mDamping);
    }

    public void setEdgeEffectDisable(int i) {
        int i2;
        int i3;
        if (this.mHorizontal) {
            i3 = 4;
            i2 = 8;
        } else {
            i2 = 2;
            i3 = 1;
        }
        if ((i3 & i) != 0) {
            this.mDisableEffectTop = true;
        }
        if ((i & i2) != 0) {
            this.mDisableEffectBottom = true;
        }
    }
}
