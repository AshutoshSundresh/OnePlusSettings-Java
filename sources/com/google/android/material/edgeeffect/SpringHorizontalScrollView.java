package com.google.android.material.edgeeffect;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.EdgeEffect;
import android.widget.HorizontalScrollView;
import com.google.android.material.edgeeffect.SpringRelativeLayout;

public class SpringHorizontalScrollView extends HorizontalScrollView {
    private int mDispatchScrollCounter;
    private SpringRelativeLayout.SEdgeEffectFactory mEdgeEffectFactory;
    private boolean mGlowing = false;
    private int mLastTouchX;
    private float mLastX;
    private float mLastXVel;
    private float mLastY;
    private EdgeEffect mLeftGlow;
    private int mMaxFlingVelocity;
    private int[] mNestedOffsets;
    boolean mOverScrollNested = false;
    float mPullGrowLeft = 0.1f;
    float mPullGrowRight = 0.9f;
    private EdgeEffect mRightGlow;
    private int[] mScrollOffset;
    private int mScrollPointerId;
    private int mScrollState;
    int[] mScrollStepConsumed;
    private SpringRelativeLayout mSpringLayout = null;
    private int mTouchSlop;
    private VelocityTracker mVelocityTracker;

    public void onScrolled(int i, int i2) {
    }

    public SpringHorizontalScrollView(Context context) {
        super(context);
        init();
    }

    public SpringHorizontalScrollView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public SpringHorizontalScrollView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    public SpringHorizontalScrollView(Context context, AttributeSet attributeSet, int i, int i2) {
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
            this.mLastTouchX = (int) (motionEvent.getX() + 0.5f);
            if (this.mScrollState == 2) {
                getParent().requestDisallowInterceptTouchEvent(true);
                setScrollState(1);
            }
            int[] iArr = this.mNestedOffsets;
            iArr[1] = 0;
            iArr[0] = 0;
            startNestedScroll(2);
        } else if (actionMasked == 1) {
            this.mVelocityTracker.addMovement(obtain);
            this.mVelocityTracker.computeCurrentVelocity(1000, (float) this.mMaxFlingVelocity);
            if ((-this.mVelocityTracker.getXVelocity(this.mScrollPointerId)) == 0.0f) {
                setScrollState(0);
            }
            resetTouch();
        } else if (actionMasked == 2) {
            int findPointerIndex = motionEvent.findPointerIndex(this.mScrollPointerId);
            if (findPointerIndex < 0) {
                Log.e("SpringHorizontalScrollView", "Error processing scroll; pointer index for id " + this.mScrollPointerId + " not found. Did any MotionEvents get skipped?");
                obtain.recycle();
                return false;
            }
            int x = (int) (motionEvent.getX(findPointerIndex) + 0.5f);
            motionEvent.getY(findPointerIndex);
            int i = this.mLastTouchX - x;
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
                this.mLastTouchX = x - this.mScrollOffset[0];
                if (scrollByInternal(i, 0, obtain)) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
            }
        } else if (actionMasked == 3) {
            cancelTouch();
        } else if (actionMasked == 5) {
            this.mScrollPointerId = motionEvent.getPointerId(actionIndex);
            this.mLastTouchX = (int) (motionEvent.getX(actionIndex) + 0.5f);
        } else if (actionMasked == 6) {
            onPointerUp(motionEvent);
        }
        obtain.recycle();
        this.mLastX = motionEvent.getX();
        this.mLastY = motionEvent.getY();
        return super.onInterceptTouchEvent(motionEvent);
    }

    /* JADX WARNING: Removed duplicated region for block: B:47:0x00fb  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r8) {
        /*
        // Method dump skipped, instructions count: 276
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.material.edgeeffect.SpringHorizontalScrollView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public void setEdgeEffectFactory(SpringRelativeLayout.SEdgeEffectFactory sEdgeEffectFactory) {
        this.mEdgeEffectFactory = sEdgeEffectFactory;
        invalidateGlows();
    }

    /* access modifiers changed from: package-private */
    public void invalidateGlows() {
        this.mRightGlow = null;
        this.mLeftGlow = null;
    }

    private void releaseGlows() {
        boolean z;
        EdgeEffect edgeEffect = this.mLeftGlow;
        if (edgeEffect != null) {
            edgeEffect.onRelease();
            this.mGlowing = false;
            z = this.mLeftGlow.isFinished() | false;
        } else {
            z = false;
        }
        EdgeEffect edgeEffect2 = this.mRightGlow;
        if (edgeEffect2 != null) {
            edgeEffect2.onRelease();
            this.mGlowing = false;
            z |= this.mRightGlow.isFinished();
        }
        if (z) {
            postInvalidateOnAnimation();
        }
    }

    private void resetTouch() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.clear();
        }
        releaseGlows();
    }

    private void cancelTouch() {
        resetTouch();
        setScrollState(0);
    }

    /* access modifiers changed from: package-private */
    public void setScrollState(int i) {
        if (i != this.mScrollState) {
            this.mScrollState = i;
        }
    }

    private void onPointerUp(MotionEvent motionEvent) {
        int actionIndex = motionEvent.getActionIndex();
        if (motionEvent.getPointerId(actionIndex) == this.mScrollPointerId) {
            int i = actionIndex == 0 ? 1 : 0;
            this.mScrollPointerId = motionEvent.getPointerId(i);
            this.mLastTouchX = (int) (motionEvent.getX(i) + 0.5f);
        }
    }

    /* access modifiers changed from: protected */
    public boolean overScrollBy(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, boolean z) {
        return super.overScrollBy(i, 0, i3, 0, i5, 0, 0, 0, z);
    }

    /* access modifiers changed from: protected */
    public void onScrollChanged(int i, int i2, int i3, int i4) {
        super.onScrollChanged(i, i2, i3, i4);
        if (!canScrollHorizontally(-1) && i2 < i3 && !this.mGlowing) {
            float f = this.mLastXVel;
            if (f >= 0.0f) {
                f = computeVelocity();
            }
            float f2 = f / 20.0f;
            pullGlows(this.mLastX, f2, this.mLastY, 0.0f);
            EdgeEffect edgeEffect = this.mLeftGlow;
            if (edgeEffect != null) {
                edgeEffect.onAbsorb((int) f2);
            }
        }
        if (!canScrollHorizontally(1) && i > i3 && !this.mGlowing) {
            float f3 = this.mLastXVel;
            if (f3 <= 0.0f) {
                f3 = computeVelocity();
            }
            float f4 = f3 / 20.0f;
            pullGlows(this.mLastX, f4, this.mLastY, 0.0f);
            EdgeEffect edgeEffect2 = this.mRightGlow;
            if (edgeEffect2 != null) {
                edgeEffect2.onAbsorb((int) f4);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void considerReleasingGlowsOnScroll(int i, int i2) {
        EdgeEffect edgeEffect = this.mLeftGlow;
        boolean z = false;
        if (edgeEffect != null && !edgeEffect.isFinished() && i > 0) {
            this.mLeftGlow.onRelease();
            z = false | this.mLeftGlow.isFinished();
        }
        EdgeEffect edgeEffect2 = this.mRightGlow;
        if (edgeEffect2 != null && !edgeEffect2.isFinished() && i < 0) {
            this.mRightGlow.onRelease();
            z |= this.mRightGlow.isFinished();
        }
        if (z) {
            postInvalidateOnAnimation();
        }
    }

    private boolean isReadyToOverScroll(boolean z) {
        if (getChildCount() <= 0) {
            return false;
        }
        if (z) {
            return !canScrollHorizontally(-1);
        }
        if (!z) {
            return !canScrollHorizontally(1);
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void ensureLeftGlow() {
        SpringRelativeLayout.SEdgeEffectFactory sEdgeEffectFactory = this.mEdgeEffectFactory;
        if (sEdgeEffectFactory == null) {
            Log.e("SpringHorizontalScrollView", "setEdgeEffectFactory first, please!");
        } else if (this.mLeftGlow == null) {
            this.mLeftGlow = sEdgeEffectFactory.createEdgeEffect(this, 0);
            if (getClipToPadding()) {
                this.mLeftGlow.setSize((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight(), (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom());
            } else {
                this.mLeftGlow.setSize(getMeasuredWidth(), getMeasuredHeight());
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void ensureRightGlow() {
        SpringRelativeLayout.SEdgeEffectFactory sEdgeEffectFactory = this.mEdgeEffectFactory;
        if (sEdgeEffectFactory == null) {
            Log.e("SpringHorizontalScrollView", "setEdgeEffectFactory first, please!");
        } else if (this.mRightGlow == null) {
            this.mRightGlow = sEdgeEffectFactory.createEdgeEffect(this, 2);
            if (getClipToPadding()) {
                this.mRightGlow.setSize((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight(), (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom());
            } else {
                this.mRightGlow.setSize(getMeasuredWidth(), getMeasuredHeight());
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:24:0x0070  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void pullGlows(float r6, float r7, float r8, float r9) {
        /*
        // Method dump skipped, instructions count: 124
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.material.edgeeffect.SpringHorizontalScrollView.pullGlows(float, float, float, float):void");
    }

    /* access modifiers changed from: package-private */
    public boolean scrollByInternal(int i, int i2, MotionEvent motionEvent) {
        int i3;
        int i4;
        int i5;
        int i6;
        if (!isReadyToOverScroll(i < 0)) {
            if (this.mSpringLayout == null) {
                ViewGroup viewGroup = (ViewGroup) getParent();
                if (viewGroup instanceof SpringRelativeLayout) {
                    this.mSpringLayout = (SpringRelativeLayout) viewGroup;
                }
            }
            SpringRelativeLayout springRelativeLayout = this.mSpringLayout;
            if (!(springRelativeLayout == null || i == 0)) {
                springRelativeLayout.onRecyclerViewScrolled();
            }
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
    public void dispatchOnScrolled(int i, int i2) {
        this.mDispatchScrollCounter++;
        int scrollX = getScrollX();
        int scrollY = getScrollY();
        onScrollChanged(scrollX, scrollY, scrollX, scrollY);
        onScrolled(i, i2);
        this.mDispatchScrollCounter--;
    }

    /* access modifiers changed from: package-private */
    public void scrollStep(int i, int i2, int[] iArr) {
        if (iArr != null) {
            iArr[0] = 0;
        }
    }

    /* access modifiers changed from: package-private */
    public float computeVelocity() {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.computeCurrentVelocity(1000, (float) this.mMaxFlingVelocity);
        return -this.mVelocityTracker.getXVelocity(this.mScrollPointerId);
    }
}
