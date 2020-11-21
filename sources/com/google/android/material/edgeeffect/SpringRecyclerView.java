package com.google.android.material.edgeeffect;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.EdgeEffect;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.RecyclerView;

public class SpringRecyclerView extends RecyclerView {
    private static final FloatPropertyCompat<SpringRecyclerView> DAMPED_SCROLL = new FloatPropertyCompat<SpringRecyclerView>("value") {
        /* class com.google.android.material.edgeeffect.SpringRecyclerView.AnonymousClass1 */

        public float getValue(SpringRecyclerView springRecyclerView) {
            return springRecyclerView.mDampedScrollShift;
        }

        public void setValue(SpringRecyclerView springRecyclerView, float f) {
            springRecyclerView.setDampedScrollShift(f);
        }
    };
    static final String TAG = SpringRecyclerView.class.getSimpleName();
    private SpringEdgeEffect mActiveEdge;
    private EdgeEffect mBottomGlow;
    private float mDampedScrollShift = 0.0f;
    private float mDamping = 0.5f;
    private boolean mDisableEffectBottom = false;
    private boolean mDisableEffectTop = false;
    private float mDistance = 0.0f;
    private SpringEdgeEffectFactory mEdgeEffectFactory;
    private boolean mGlowing = false;
    private boolean mHandleTouch = true;
    private boolean mHorizontal = false;
    private int mLastTouchX;
    private int mLastTouchY;
    private float mLastX;
    private float mLastXVel = 0.0f;
    private float mLastY;
    private int mMaxFlingVelocity;
    private int[] mNestedOffsets;
    boolean mOverScrollNested = true;
    private int mPullCount = 0;
    float mPullGrowBottom = 0.9f;
    float mPullGrowTop = 0.1f;
    private int[] mScrollOffset;
    private int mScrollPointerId;
    private int mScrollState;
    private SpringAnimation mSpring;
    private float mStif = 590.0f;
    private EdgeEffect mTopGlow;
    private int mTouchSlop;
    private VelocityTracker mVelocityTracker;
    private float mVelocity_multiplier = 0.3f;

    static /* synthetic */ int access$1208(SpringRecyclerView springRecyclerView) {
        int i = springRecyclerView.mPullCount;
        springRecyclerView.mPullCount = i + 1;
        return i;
    }

    public SpringRecyclerView(Context context) {
        super(context);
        init();
    }

    public SpringRecyclerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public SpringRecyclerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private void init() {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        this.mTouchSlop = viewConfiguration.getScaledTouchSlop();
        this.mMaxFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        this.mScrollOffset = new int[2];
        this.mNestedOffsets = new int[2];
        SpringEdgeEffectFactory springEdgeEffectFactory = new SpringEdgeEffectFactory();
        this.mEdgeEffectFactory = springEdgeEffectFactory;
        setEdgeEffectFactory(springEdgeEffectFactory);
        SpringAnimation springAnimation = new SpringAnimation(this, DAMPED_SCROLL, 0.0f);
        this.mSpring = springAnimation;
        SpringForce springForce = new SpringForce(0.0f);
        springForce.setStiffness(590.0f);
        springForce.setDampingRatio(0.5f);
        springAnimation.setSpring(springForce);
    }

    /* JADX WARNING: Removed duplicated region for block: B:31:0x008e  */
    @Override // androidx.recyclerview.widget.RecyclerView
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onInterceptTouchEvent(android.view.MotionEvent r10) {
        /*
        // Method dump skipped, instructions count: 264
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.material.edgeeffect.SpringRecyclerView.onInterceptTouchEvent(android.view.MotionEvent):boolean");
    }

    /* JADX WARNING: Removed duplicated region for block: B:42:0x00ba  */
    @Override // androidx.recyclerview.widget.RecyclerView
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r10) {
        /*
        // Method dump skipped, instructions count: 353
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.material.edgeeffect.SpringRecyclerView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    /* access modifiers changed from: protected */
    public void setDampedScrollShift(float f) {
        if (f != this.mDampedScrollShift) {
            this.mDampedScrollShift = f;
            invalidate();
        }
    }

    /* access modifiers changed from: package-private */
    public boolean scrollByInternal(int i, int i2, MotionEvent motionEvent) {
        boolean z = true;
        if (!this.mHorizontal ? i2 >= 0 : i >= 0) {
            z = false;
        }
        if (!isReadyToOverScroll(z)) {
            onRecyclerViewScrolled();
            return false;
        }
        if (this.mOverScrollNested && getOverScrollMode() != 2) {
            if (motionEvent != null && !motionEvent.isFromSource(8194)) {
                pullGlows(motionEvent.getX(), (float) i, motionEvent.getY(), (float) i2);
            }
            considerReleasingGlowsOnScroll(i, i2);
        }
        if (!awakenScrollBars()) {
            invalidate();
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    @Override // androidx.recyclerview.widget.RecyclerView
    public void considerReleasingGlowsOnScroll(int i, int i2) {
        EdgeEffect edgeEffect = this.mTopGlow;
        boolean z = false;
        if (edgeEffect != null && !edgeEffect.isFinished() && i2 > 0) {
            this.mTopGlow.onRelease();
            z = false | this.mTopGlow.isFinished();
        }
        EdgeEffect edgeEffect2 = this.mBottomGlow;
        if (edgeEffect2 != null && !edgeEffect2.isFinished() && i2 < 0) {
            this.mBottomGlow.onRelease();
            z |= this.mBottomGlow.isFinished();
        }
        if (z) {
            postInvalidateOnAnimation();
        }
    }

    private boolean isReadyToOverScroll(boolean z) {
        RecyclerView.Adapter adapter = getAdapter();
        if (adapter == null || adapter.getItemCount() <= 0) {
            return false;
        }
        if (this.mGlowing) {
            return true;
        }
        if (z) {
            if (this.mHorizontal) {
                if (canScrollHorizontally(-1)) {
                    return false;
                }
            } else if (canScrollVertically(-1)) {
                return false;
            }
            return true;
        }
        if (this.mHorizontal) {
            if (canScrollHorizontally(1)) {
                return false;
            }
        } else if (canScrollVertically(1)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    @Override // androidx.recyclerview.widget.RecyclerView
    public void setScrollState(int i) {
        if (i != this.mScrollState) {
            this.mScrollState = i;
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setActiveEdge(SpringEdgeEffect springEdgeEffect) {
        SpringEdgeEffect springEdgeEffect2 = this.mActiveEdge;
        this.mActiveEdge = springEdgeEffect;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void finishScrollWithVelocity(float f) {
        float f2 = this.mDampedScrollShift;
        if (f2 > Float.MAX_VALUE || f2 < -3.4028235E38f) {
            Log.e("SpringRecyclerView", "animation parameter out of range!");
        } else if (f > 0.0f && this.mDisableEffectTop) {
        } else {
            if (f >= 0.0f || !this.mDisableEffectBottom) {
                this.mSpring.setStartVelocity(f);
                this.mSpring.setStartValue(this.mDampedScrollShift);
                this.mSpring.start();
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:42:0x00bd  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void pullGlows(float r6, float r7, float r8, float r9) {
        /*
        // Method dump skipped, instructions count: 201
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.material.edgeeffect.SpringRecyclerView.pullGlows(float, float, float, float):void");
    }

    /* access modifiers changed from: package-private */
    @Override // androidx.recyclerview.widget.RecyclerView
    public void ensureBottomGlow() {
        SpringEdgeEffectFactory springEdgeEffectFactory = this.mEdgeEffectFactory;
        if (springEdgeEffectFactory == null) {
            Log.e("SpringRecyclerView", "setEdgeEffectFactory first, please!");
        } else if (this.mBottomGlow == null) {
            this.mBottomGlow = springEdgeEffectFactory.createEdgeEffect(this, 3);
            if (getClipToPadding()) {
                this.mBottomGlow.setSize((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight(), (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom());
            } else {
                this.mBottomGlow.setSize(getMeasuredWidth(), getMeasuredHeight());
            }
        }
    }

    /* access modifiers changed from: package-private */
    @Override // androidx.recyclerview.widget.RecyclerView
    public void ensureTopGlow() {
        SpringEdgeEffectFactory springEdgeEffectFactory = this.mEdgeEffectFactory;
        if (springEdgeEffectFactory == null) {
            Log.e("SpringRecyclerView", "setEdgeEffectFactory first, please!");
        } else if (this.mTopGlow == null) {
            this.mTopGlow = springEdgeEffectFactory.createEdgeEffect(this, 1);
            if (getClipToPadding()) {
                this.mTopGlow.setSize((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight(), (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom());
            } else {
                this.mTopGlow.setSize(getMeasuredWidth(), getMeasuredHeight());
            }
        }
    }

    private void resetScroll() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.clear();
        }
        stopNestedScroll();
        releaseGlows();
    }

    private void releaseGlows() {
        boolean z;
        EdgeEffect edgeEffect = this.mTopGlow;
        if (edgeEffect != null) {
            edgeEffect.onRelease();
            this.mGlowing = false;
            z = this.mTopGlow.isFinished() | false;
        } else {
            z = false;
        }
        EdgeEffect edgeEffect2 = this.mBottomGlow;
        if (edgeEffect2 != null) {
            edgeEffect2.onRelease();
            this.mGlowing = false;
            z |= this.mBottomGlow.isFinished();
        }
        if (z) {
            postInvalidateOnAnimation();
        }
    }

    /* access modifiers changed from: private */
    public class SpringEdgeEffectFactory extends RecyclerView.EdgeEffectFactory {
        private SpringEdgeEffectFactory() {
        }

        /* access modifiers changed from: protected */
        @Override // androidx.recyclerview.widget.RecyclerView.EdgeEffectFactory
        public EdgeEffect createEdgeEffect(RecyclerView recyclerView, int i) {
            if (i == 0 || i == 1) {
                SpringRecyclerView springRecyclerView = SpringRecyclerView.this;
                return new SpringEdgeEffect(springRecyclerView.getContext(), SpringRecyclerView.this.mVelocity_multiplier);
            } else if (i != 2 && i != 3) {
                return super.createEdgeEffect(recyclerView, i);
            } else {
                SpringRecyclerView springRecyclerView2 = SpringRecyclerView.this;
                return new SpringEdgeEffect(springRecyclerView2.getContext(), -SpringRecyclerView.this.mVelocity_multiplier);
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
            SpringRecyclerView.this.finishScrollWithVelocity(((float) i) * this.mVelocityMultiplier);
        }

        public void onPull(float f, float f2) {
            if (SpringRecyclerView.this.mSpring.isRunning()) {
                SpringRecyclerView.this.mSpring.cancel();
            }
            SpringRecyclerView.access$1208(SpringRecyclerView.this);
            SpringRecyclerView.this.setActiveEdge(this);
            SpringRecyclerView.this.mDistance += f * (this.mVelocityMultiplier / 3.0f);
            if (SpringRecyclerView.this.mDistance > 0.0f && SpringRecyclerView.this.mDisableEffectTop) {
                SpringRecyclerView.this.mDistance = 0.0f;
            } else if (SpringRecyclerView.this.mDistance < 0.0f && SpringRecyclerView.this.mDisableEffectBottom) {
                SpringRecyclerView.this.mDistance = 0.0f;
            } else if (SpringRecyclerView.this.mHorizontal) {
                SpringRecyclerView springRecyclerView = SpringRecyclerView.this;
                springRecyclerView.setDampedScrollShift(springRecyclerView.mDistance * ((float) SpringRecyclerView.this.getWidth()));
            } else {
                SpringRecyclerView springRecyclerView2 = SpringRecyclerView.this;
                springRecyclerView2.setDampedScrollShift(springRecyclerView2.mDistance * ((float) SpringRecyclerView.this.getHeight()));
            }
            this.mReleased = false;
        }

        public void onRelease() {
            if (!this.mReleased) {
                SpringRecyclerView.this.mDistance = 0.0f;
                SpringRecyclerView.this.mPullCount = 0;
                SpringRecyclerView.this.finishScrollWithVelocity(0.0f);
                this.mReleased = true;
            }
        }
    }

    private void cancelScroll() {
        resetTouch();
        setScrollState(0);
    }

    private void resetTouch() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.clear();
        }
        releaseGlows();
    }

    private void onPointerUp(MotionEvent motionEvent) {
        int actionIndex = motionEvent.getActionIndex();
        if (motionEvent.getPointerId(actionIndex) == this.mScrollPointerId) {
            int i = actionIndex == 0 ? 1 : 0;
            this.mScrollPointerId = motionEvent.getPointerId(i);
            this.mLastTouchY = (int) (motionEvent.getY(i) + 0.5f);
        }
    }

    public void onRecyclerViewScrolled() {
        if (this.mPullCount != 1 && !this.mSpring.isRunning()) {
            this.mDistance = 0.0f;
            this.mPullCount = 0;
            finishScrollWithVelocity(0.0f);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView
    public void draw(Canvas canvas) {
        if (this.mDampedScrollShift != 0.0f) {
            int save = canvas.save();
            if (this.mHorizontal) {
                canvas.translate(this.mDampedScrollShift, 0.0f);
            } else {
                canvas.translate(0.0f, this.mDampedScrollShift);
            }
            super.draw(canvas);
            canvas.restoreToCount(save);
            return;
        }
        super.draw(canvas);
    }

    public void setHandleTouch(boolean z) {
        this.mHandleTouch = z;
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
