package com.oneplus.settings.edgeeffect;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.EdgeEffect;
import android.widget.RelativeLayout;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.RecyclerView;

public class SpringRelativeLayout extends RelativeLayout {
    private static final FloatPropertyCompat<SpringRelativeLayout> DAMPED_SCROLL = new FloatPropertyCompat<SpringRelativeLayout>("value") {
        /* class com.oneplus.settings.edgeeffect.SpringRelativeLayout.AnonymousClass1 */

        public float getValue(SpringRelativeLayout springRelativeLayout) {
            return springRelativeLayout.mDampedScrollShift;
        }

        public void setValue(SpringRelativeLayout springRelativeLayout, float f) {
            springRelativeLayout.setDampedScrollShift(f);
        }
    };
    private SpringEdgeEffect mActiveEdge;
    private float mDampedScrollShift;
    private float mDistance;
    private boolean mHorizontal;
    private int mPullCount;
    private boolean mReadyToGo;
    private final SpringAnimation mSpring;
    protected final SparseBooleanArray mSpringViews;

    public int getCanvasClipLeftForOverscroll() {
        return 0;
    }

    public int getCanvasClipTopForOverscroll() {
        return 0;
    }

    static /* synthetic */ float access$316(SpringRelativeLayout springRelativeLayout, float f) {
        float f2 = springRelativeLayout.mDistance + f;
        springRelativeLayout.mDistance = f2;
        return f2;
    }

    static /* synthetic */ int access$508(SpringRelativeLayout springRelativeLayout) {
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
        if (this.mPullCount != 1) {
            this.mDistance = 0.0f;
            this.mPullCount = 0;
            finishScrollWithVelocity(0.0f);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void finishScrollWithVelocity(float f) {
        this.mSpring.setStartVelocity(f);
        this.mSpring.setStartValue(this.mDampedScrollShift);
        this.mSpring.start();
    }

    public RecyclerView.EdgeEffectFactory createEdgeEffectFactory() {
        return createEdgeEffectFactory(false);
    }

    /* access modifiers changed from: private */
    public class SpringEdgeEffectFactory extends RecyclerView.EdgeEffectFactory {
        private SpringEdgeEffectFactory() {
        }

        /* access modifiers changed from: protected */
        @Override // androidx.recyclerview.widget.RecyclerView.EdgeEffectFactory
        public EdgeEffect createEdgeEffect(RecyclerView recyclerView, int i) {
            if (i == 0 || i == 1) {
                SpringRelativeLayout springRelativeLayout = SpringRelativeLayout.this;
                return new SpringEdgeEffect(springRelativeLayout.getContext(), 0.3f);
            } else if (i != 2 && i != 3) {
                return super.createEdgeEffect(recyclerView, i);
            } else {
                SpringRelativeLayout springRelativeLayout2 = SpringRelativeLayout.this;
                return new SpringEdgeEffect(springRelativeLayout2.getContext(), -0.3f);
            }
        }
    }

    public RecyclerView.EdgeEffectFactory createEdgeEffectFactory(boolean z) {
        this.mHorizontal = z;
        return new SpringEdgeEffectFactory();
    }

    /* access modifiers changed from: private */
    public class SpringEdgeEffect extends EdgeEffect {
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
            SpringRelativeLayout.access$508(SpringRelativeLayout.this);
            SpringRelativeLayout.this.setActiveEdge(this);
            SpringRelativeLayout.access$316(SpringRelativeLayout.this, f * (this.mVelocityMultiplier / 3.0f));
            if (SpringRelativeLayout.this.mHorizontal) {
                SpringRelativeLayout springRelativeLayout = SpringRelativeLayout.this;
                springRelativeLayout.setDampedScrollShift(springRelativeLayout.mDistance * ((float) SpringRelativeLayout.this.getWidth()));
                return;
            }
            SpringRelativeLayout springRelativeLayout2 = SpringRelativeLayout.this;
            springRelativeLayout2.setDampedScrollShift(springRelativeLayout2.mDistance * ((float) SpringRelativeLayout.this.getHeight()));
        }

        public void onRelease() {
            SpringRelativeLayout.this.mDistance = 0.0f;
            SpringRelativeLayout.this.mPullCount = 0;
            if (((double) SpringRelativeLayout.this.mDampedScrollShift) != 0.0d) {
                SpringRelativeLayout.this.mReadyToGo = false;
            }
            SpringRelativeLayout.this.finishScrollWithVelocity(0.0f);
        }
    }

    /* access modifiers changed from: private */
    public class ViewEdgeEffectFactory extends SEdgeEffectFactory {
        private ViewEdgeEffectFactory() {
        }

        /* access modifiers changed from: protected */
        @Override // com.oneplus.settings.edgeeffect.SpringRelativeLayout.SEdgeEffectFactory
        public EdgeEffect createEdgeEffect(View view, int i) {
            if (i == 0 || i == 1) {
                SpringRelativeLayout springRelativeLayout = SpringRelativeLayout.this;
                return new SpringEdgeEffect(springRelativeLayout.getContext(), 0.3f);
            } else if (i != 2 && i != 3) {
                return super.createEdgeEffect(view, i);
            } else {
                SpringRelativeLayout springRelativeLayout2 = SpringRelativeLayout.this;
                return new SpringEdgeEffect(springRelativeLayout2.getContext(), -0.3f);
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
}
