package com.google.android.material.edgeeffect;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.EdgeEffect;
import android.widget.ScrollView;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

public class SpringScrollView2 extends ScrollView {
    private static final FloatPropertyCompat<SpringScrollView2> DAMPED_SCROLL = new FloatPropertyCompat<SpringScrollView2>("value") {
        /* class com.google.android.material.edgeeffect.SpringScrollView2.AnonymousClass1 */

        public float getValue(SpringScrollView2 springScrollView2) {
            return springScrollView2.mDampedScrollShift;
        }

        public void setValue(SpringScrollView2 springScrollView2, float f) {
            springScrollView2.setDampedScrollShift(f);
        }
    };
    private SpringEdgeEffect mActiveEdge;
    private EdgeEffect mBottomGlow;
    private float mDampedScrollShift = 0.0f;
    private boolean mDisableEffectBottom = false;
    private boolean mDisableEffectTop = false;
    private int mDispatchScrollCounter;
    private float mDistance = 0.0f;
    private SEdgeEffectFactory mEdgeEffectFactory;
    private boolean mGlowingBottom = false;
    private boolean mGlowingTop = false;
    private int mLastTouchY;
    private float mLastX;
    private float mLastY;
    private float mLastYVel;
    private int mMaxFlingVelocity;
    private int[] mNestedOffsets;
    boolean mOverScrollNested = false;
    private int mPullCount = 0;
    float mPullGrowBottom = 0.9f;
    float mPullGrowTop = 0.1f;
    private int[] mScrollOffset;
    private int mScrollPointerId;
    private int mScrollState;
    int[] mScrollStepConsumed;
    private SpringAnimation mSpring;
    private EdgeEffect mTopGlow;
    private int mTouchSlop;
    private VelocityTracker mVelocityTracker;
    private float mVelocity_multiplier = 0.3f;

    public void onScrolled(int i, int i2) {
    }

    static /* synthetic */ int access$508(SpringScrollView2 springScrollView2) {
        int i = springScrollView2.mPullCount;
        springScrollView2.mPullCount = i + 1;
        return i;
    }

    public SpringScrollView2(Context context) {
        super(context);
        init();
    }

    public SpringScrollView2(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public SpringScrollView2(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    public SpringScrollView2(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init();
    }

    private void init() {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        this.mTouchSlop = viewConfiguration.getScaledTouchSlop();
        this.mMaxFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        this.mScrollStepConsumed = new int[2];
        this.mScrollOffset = new int[2];
        this.mNestedOffsets = new int[2];
        ViewEdgeEffectFactory createViewEdgeEffectFactory = createViewEdgeEffectFactory();
        this.mEdgeEffectFactory = createViewEdgeEffectFactory;
        setEdgeEffectFactory(createViewEdgeEffectFactory);
        SpringAnimation springAnimation = new SpringAnimation(this, DAMPED_SCROLL, 0.0f);
        this.mSpring = springAnimation;
        SpringForce springForce = new SpringForce(0.0f);
        springForce.setStiffness(590.0f);
        springForce.setDampingRatio(0.5f);
        springAnimation.setSpring(springForce);
    }

    public void setEdgeEffectFactory(SEdgeEffectFactory sEdgeEffectFactory) {
        this.mEdgeEffectFactory = sEdgeEffectFactory;
        invalidateGlows();
    }

    /* access modifiers changed from: package-private */
    public void invalidateGlows() {
        this.mBottomGlow = null;
        this.mTopGlow = null;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        boolean z;
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(motionEvent);
        int actionMasked = motionEvent.getActionMasked();
        int actionIndex = motionEvent.getActionIndex();
        MotionEvent obtain = MotionEvent.obtain(motionEvent);
        if (actionMasked == 0) {
            this.mScrollPointerId = motionEvent.getPointerId(0);
            this.mLastTouchY = (int) (motionEvent.getY() + 0.5f);
            if (this.mScrollState == 2) {
                getParent().requestDisallowInterceptTouchEvent(true);
                setScrollState(1);
            }
            int[] iArr = this.mNestedOffsets;
            iArr[1] = 0;
            iArr[0] = 0;
        } else if (actionMasked == 1) {
            this.mVelocityTracker.addMovement(obtain);
            this.mVelocityTracker.computeCurrentVelocity(1000, (float) this.mMaxFlingVelocity);
            if ((-this.mVelocityTracker.getYVelocity(this.mScrollPointerId)) == 0.0f) {
                setScrollState(0);
            }
            resetTouch();
        } else if (actionMasked == 2) {
            int findPointerIndex = motionEvent.findPointerIndex(this.mScrollPointerId);
            if (findPointerIndex < 0) {
                Log.e("SpringScrollView", "Error processing scroll; pointer index for id " + this.mScrollPointerId + " not found. Did any MotionEvents get skipped?");
                return false;
            }
            motionEvent.getX(findPointerIndex);
            int y = (int) (motionEvent.getY(findPointerIndex) + 0.5f);
            int i = this.mLastTouchY - y;
            if (this.mScrollState != 1) {
                int abs = Math.abs(i);
                int i2 = this.mTouchSlop;
                if (abs > i2) {
                    i = i > 0 ? i - i2 : i + i2;
                    z = true;
                } else {
                    z = false;
                }
                if (z) {
                    setScrollState(1);
                }
            }
            if (this.mScrollState == 1) {
                this.mLastTouchY = y - this.mScrollOffset[1];
                if (scrollByInternal(0, i, obtain)) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
            }
        } else if (actionMasked == 3) {
            cancelTouch();
        } else if (actionMasked == 5) {
            this.mScrollPointerId = motionEvent.getPointerId(actionIndex);
            this.mLastTouchY = (int) (motionEvent.getY(actionIndex) + 0.5f);
        } else if (actionMasked == 6) {
            onPointerUp(motionEvent);
        }
        this.mLastX = motionEvent.getX();
        this.mLastY = motionEvent.getY();
        return super.onInterceptTouchEvent(motionEvent);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean z;
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        MotionEvent obtain = MotionEvent.obtain(motionEvent);
        int actionMasked = motionEvent.getActionMasked();
        int actionIndex = motionEvent.getActionIndex();
        boolean z2 = false;
        if (actionMasked == 0) {
            int[] iArr = this.mNestedOffsets;
            iArr[1] = 0;
            iArr[0] = 0;
        }
        int[] iArr2 = this.mNestedOffsets;
        obtain.offsetLocation((float) iArr2[0], (float) iArr2[1]);
        if (actionMasked == 0) {
            this.mScrollPointerId = motionEvent.getPointerId(0);
            this.mLastTouchY = (int) (motionEvent.getY() + 0.5f);
        } else if (actionMasked == 1) {
            this.mVelocityTracker.addMovement(obtain);
            this.mVelocityTracker.computeCurrentVelocity(1000, (float) this.mMaxFlingVelocity);
            float f = -this.mVelocityTracker.getYVelocity(this.mScrollPointerId);
            if (f == 0.0f) {
                setScrollState(0);
            } else {
                this.mLastYVel = f;
            }
            resetTouch();
            z2 = true;
        } else if (actionMasked == 2) {
            int findPointerIndex = motionEvent.findPointerIndex(this.mScrollPointerId);
            if (findPointerIndex < 0) {
                Log.e("SpringScrollView", "Error processing scroll; pointer index for id " + this.mScrollPointerId + " not found. Did any MotionEvents get skipped?");
                return false;
            }
            motionEvent.getX(findPointerIndex);
            int y = (int) (motionEvent.getY(findPointerIndex) + 0.5f);
            int i = this.mLastTouchY - y;
            if (this.mScrollState != 1) {
                int abs = Math.abs(i);
                int i2 = this.mTouchSlop;
                if (abs > i2) {
                    i = i > 0 ? i - i2 : i + i2;
                    z = true;
                } else {
                    z = false;
                }
                if (z) {
                    setScrollState(1);
                }
            }
            if (this.mScrollState == 1) {
                this.mLastTouchY = y - this.mScrollOffset[1];
                if (scrollByInternal(0, i, obtain)) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
            }
        } else if (actionMasked == 3) {
            cancelTouch();
        } else if (actionMasked == 5) {
            this.mScrollPointerId = motionEvent.getPointerId(actionIndex);
            this.mLastTouchY = (int) (motionEvent.getY(actionIndex) + 0.5f);
        } else if (actionMasked == 6) {
            onPointerUp(motionEvent);
        }
        if (!z2) {
            this.mVelocityTracker.addMovement(obtain);
        }
        obtain.recycle();
        this.mLastX = motionEvent.getX();
        this.mLastY = motionEvent.getY();
        return super.onTouchEvent(motionEvent);
    }

    /* access modifiers changed from: package-private */
    public void ensureTopGlow() {
        SEdgeEffectFactory sEdgeEffectFactory = this.mEdgeEffectFactory;
        if (sEdgeEffectFactory == null) {
            Log.e("SpringScrollView2", "setEdgeEffectFactory first, please!");
        } else if (this.mTopGlow == null) {
            this.mTopGlow = sEdgeEffectFactory.createEdgeEffect(this, 1);
            if (getClipToPadding()) {
                this.mTopGlow.setSize((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight(), (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom());
            } else {
                this.mTopGlow.setSize(getMeasuredWidth(), getMeasuredHeight());
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void ensureBottomGlow() {
        SEdgeEffectFactory sEdgeEffectFactory = this.mEdgeEffectFactory;
        if (sEdgeEffectFactory == null) {
            Log.e("SpringScrollView2", "setEdgeEffectFactory first, please!");
        } else if (this.mBottomGlow == null) {
            this.mBottomGlow = sEdgeEffectFactory.createEdgeEffect(this, 3);
            if (getClipToPadding()) {
                this.mBottomGlow.setSize((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight(), (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom());
            } else {
                this.mBottomGlow.setSize(getMeasuredWidth(), getMeasuredHeight());
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:24:0x0070  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void pullGlows(float r6, float r7, float r8, float r9) {
        /*
        // Method dump skipped, instructions count: 124
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.material.edgeeffect.SpringScrollView2.pullGlows(float, float, float, float):void");
    }

    /* access modifiers changed from: package-private */
    public void setScrollState(int i) {
        if (i != this.mScrollState) {
            this.mScrollState = i;
        }
    }

    private void resetTouch() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.clear();
        }
        releaseGlows();
    }

    private void releaseGlows() {
        boolean z;
        EdgeEffect edgeEffect = this.mTopGlow;
        if (edgeEffect != null) {
            edgeEffect.onRelease();
            this.mGlowingTop = false;
            z = this.mTopGlow.isFinished() | false;
        } else {
            z = false;
        }
        EdgeEffect edgeEffect2 = this.mBottomGlow;
        if (edgeEffect2 != null) {
            edgeEffect2.onRelease();
            this.mGlowingBottom = false;
            z |= this.mBottomGlow.isFinished();
        }
        if (z) {
            postInvalidateOnAnimation();
        }
    }

    private void cancelTouch() {
        resetTouch();
        setScrollState(0);
    }

    private void onPointerUp(MotionEvent motionEvent) {
        int actionIndex = motionEvent.getActionIndex();
        if (motionEvent.getPointerId(actionIndex) == this.mScrollPointerId) {
            int i = actionIndex == 0 ? 1 : 0;
            this.mScrollPointerId = motionEvent.getPointerId(i);
            this.mLastTouchY = (int) (motionEvent.getY(i) + 0.5f);
        }
    }

    /* access modifiers changed from: package-private */
    public void dispatchOnScrolled(int i, int i2) {
        this.mDispatchScrollCounter++;
        int scrollX = getScrollX();
        int scrollY = getScrollY();
        onScrollChanged(scrollX, scrollY, scrollX, scrollY);
        onScrolled(i, i2);
        this.mDispatchScrollCounter--;
    }

    /* access modifiers changed from: package-private */
    public boolean scrollByInternal(int i, int i2, MotionEvent motionEvent) {
        int i3;
        int i4;
        int i5;
        int i6;
        if (!isReadyToOverScroll(i2 < 0)) {
            return false;
        }
        if (getChildCount() >= 0) {
            scrollStep(i, i2, this.mScrollStepConsumed);
            int[] iArr = this.mScrollStepConsumed;
            i5 = iArr[0];
            i6 = iArr[1];
            i4 = i - i5;
            i3 = i2 - i6;
        } else {
            i6 = 0;
            i5 = 0;
            i4 = 0;
            i3 = 0;
        }
        invalidate();
        if (getOverScrollMode() != 2) {
            if (motionEvent != null && !motionEvent.isFromSource(8194)) {
                pullGlows(motionEvent.getX(), (float) i4, motionEvent.getY(), (float) i3);
            }
            considerReleasingGlowsOnScroll(i, i2);
        }
        if (!(i5 == 0 && i6 == 0)) {
            dispatchOnScrolled(i5, i6);
        }
        if (!awakenScrollBars()) {
            invalidate();
        }
        return (i5 == 0 && i6 == 0) ? false : true;
    }

    /* access modifiers changed from: package-private */
    public void scrollStep(int i, int i2, int[] iArr) {
        if (iArr != null) {
            iArr[1] = 0;
        }
    }

    private boolean isReadyToOverScroll(boolean z) {
        if (getChildCount() <= 0) {
            return false;
        }
        if (z) {
            return !canScrollVertically(-1);
        }
        if (!z) {
            return !canScrollVertically(1);
        }
        return false;
    }

    /* access modifiers changed from: package-private */
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

    public void setOverScrollNested(boolean z) {
        this.mOverScrollNested = z;
    }

    /* access modifiers changed from: protected */
    public boolean overScrollBy(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, boolean z) {
        return super.overScrollBy(0, i2, 0, i4, 0, i6, 0, 0, z);
    }

    /* access modifiers changed from: protected */
    public void onScrollChanged(int i, int i2, int i3, int i4) {
        super.onScrollChanged(i, i2, i3, i4);
        if (this.mGlowingTop && canScrollVertically(-1) && i2 > i4) {
            onRecyclerViewScrolled();
        }
        if (this.mGlowingBottom && canScrollVertically(1) && i2 < i4) {
            onRecyclerViewScrolled();
        }
        if (!this.mGlowingTop && !canScrollVertically(-1) && i2 < i4) {
            float f = this.mLastYVel;
            if (f >= 0.0f) {
                f = computeVelocity();
            }
            float f2 = f / 20.0f;
            pullGlows(this.mLastX, 0.0f, this.mLastY, f2);
            EdgeEffect edgeEffect = this.mTopGlow;
            if (edgeEffect != null) {
                edgeEffect.onAbsorb((int) f2);
            }
        }
        if (!this.mGlowingBottom && !canScrollVertically(1) && i2 > i4) {
            float f3 = this.mLastYVel;
            if (f3 <= 0.0f) {
                f3 = computeVelocity();
            }
            float f4 = f3 / 20.0f;
            pullGlows(this.mLastX, 0.0f, this.mLastY, f4);
            EdgeEffect edgeEffect2 = this.mBottomGlow;
            if (edgeEffect2 != null) {
                edgeEffect2.onAbsorb((int) f4);
            }
        }
        super.onScrollChanged(i, i2, i3, i4);
    }

    /* access modifiers changed from: package-private */
    public void onRecyclerViewScrolled() {
        if (this.mPullCount != 1 && !this.mSpring.isRunning()) {
            this.mDistance = 0.0f;
            this.mPullCount = 0;
            finishScrollWithVelocity(0.0f);
        }
    }

    /* access modifiers changed from: private */
    public class ViewEdgeEffectFactory extends SEdgeEffectFactory {
        private ViewEdgeEffectFactory() {
        }

        /* access modifiers changed from: protected */
        @Override // com.google.android.material.edgeeffect.SpringScrollView2.SEdgeEffectFactory
        public EdgeEffect createEdgeEffect(View view, int i) {
            if (i == 0 || i == 1) {
                SpringScrollView2 springScrollView2 = SpringScrollView2.this;
                return new SpringEdgeEffect(springScrollView2.getContext(), SpringScrollView2.this.mVelocity_multiplier);
            } else if (i != 2 && i != 3) {
                return super.createEdgeEffect(view, i);
            } else {
                SpringScrollView2 springScrollView22 = SpringScrollView2.this;
                return new SpringEdgeEffect(springScrollView22.getContext(), -SpringScrollView2.this.mVelocity_multiplier);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public static class SEdgeEffectFactory {
        /* access modifiers changed from: protected */
        public EdgeEffect createEdgeEffect(View view, int i) {
            return new EdgeEffect(view.getContext());
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
            SpringScrollView2.this.finishScrollWithVelocity(((float) i) * this.mVelocityMultiplier);
            SpringScrollView2.this.mDistance = 0.0f;
        }

        public void onPull(float f, float f2) {
            if (SpringScrollView2.this.mSpring.isRunning()) {
                SpringScrollView2.this.mSpring.cancel();
            }
            SpringScrollView2.access$508(SpringScrollView2.this);
            SpringScrollView2.this.setActiveEdge(this);
            SpringScrollView2.this.mDistance += f * (this.mVelocityMultiplier / 3.0f);
            if (SpringScrollView2.this.mDistance > 0.0f && SpringScrollView2.this.mDisableEffectTop) {
                SpringScrollView2.this.mDistance = 0.0f;
            } else if (SpringScrollView2.this.mDistance < 0.0f && SpringScrollView2.this.mDisableEffectBottom) {
                SpringScrollView2.this.mDistance = 0.0f;
            }
            SpringScrollView2 springScrollView2 = SpringScrollView2.this;
            springScrollView2.setDampedScrollShift(springScrollView2.mDistance * ((float) SpringScrollView2.this.getHeight()));
            this.mReleased = false;
        }

        public void onRelease() {
            if (!this.mReleased) {
                SpringScrollView2.this.mDistance = 0.0f;
                SpringScrollView2.this.mPullCount = 0;
                SpringScrollView2.this.finishScrollWithVelocity(0.0f);
                this.mReleased = true;
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void finishScrollWithVelocity(float f) {
        float f2 = this.mDampedScrollShift;
        if (f2 > Float.MAX_VALUE || f2 < -3.4028235E38f) {
            Log.e("SpringScrollView2", "animation parameter out of range!");
        } else if (f > 0.0f && this.mDisableEffectTop) {
        } else {
            if (f >= 0.0f || !this.mDisableEffectBottom) {
                this.mSpring.setStartVelocity(f);
                this.mSpring.setStartValue(this.mDampedScrollShift);
                this.mSpring.start();
            }
        }
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

    public ViewEdgeEffectFactory createViewEdgeEffectFactory() {
        return new ViewEdgeEffectFactory();
    }

    public void draw(Canvas canvas) {
        if (this.mDampedScrollShift != 0.0f) {
            int save = canvas.save();
            canvas.translate(0.0f, this.mDampedScrollShift);
            super.draw(canvas);
            canvas.restoreToCount(save);
            return;
        }
        super.draw(canvas);
    }

    /* access modifiers changed from: package-private */
    public float computeVelocity() {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.computeCurrentVelocity(1000, (float) this.mMaxFlingVelocity);
        return -this.mVelocityTracker.getYVelocity(this.mScrollPointerId);
    }

    public void setVelocityMultiplier(float f) {
        this.mVelocity_multiplier = f;
    }

    public void setEdgeEffectDisable(int i) {
        if ((i & 1) != 0) {
            this.mDisableEffectTop = true;
        }
        if ((i & 2) != 0) {
            this.mDisableEffectBottom = true;
        }
    }
}
