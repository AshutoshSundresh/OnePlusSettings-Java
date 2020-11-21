package com.google.android.material.edgeeffect;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EdgeEffect;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import com.google.android.material.edgeeffect.SpringRelativeLayout;

public class SpringExpandableListView extends ExpandableListView {
    private EdgeEffect mBottomGlow;
    private int mDispatchScrollCounter;
    private SpringRelativeLayout.SEdgeEffectFactory mEdgeEffectFactory;
    AbsListView.OnScrollListener mGivenOnScrollListener;
    private boolean mGlowing = false;
    private int mInitialTouchY;
    private int mLastTouchY;
    private float mLastX;
    private float mLastY;
    private float mLastYVel = 0.0f;
    private int mMaxFlingVelocity;
    private int[] mNestedOffsets;
    OnScrollListenerWrapper mOnScrollListenerWrapper = new OnScrollListenerWrapper();
    boolean mOverScrollNested = false;
    float mPullGrowBottom = 0.9f;
    float mPullGrowTop = 0.1f;
    private int[] mScrollOffset;
    private int mScrollPointerId;
    private int mScrollState = 0;
    int[] mScrollStepConsumed;
    private SpringRelativeLayout mSpringLayout = null;
    private EdgeEffect mTopGlow;
    private int mTouchSlop;
    private VelocityTracker mVelocityTracker;

    public void onScrolled(int i, int i2) {
    }

    public SpringExpandableListView(Context context) {
        super(context);
        init();
    }

    public SpringExpandableListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public SpringExpandableListView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private void init() {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        this.mTouchSlop = viewConfiguration.getScaledTouchSlop();
        this.mMaxFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        this.mScrollStepConsumed = new int[2];
        this.mScrollOffset = new int[2];
        this.mNestedOffsets = new int[2];
        setOnScrollListener(this.mOnScrollListenerWrapper);
    }

    /* access modifiers changed from: protected */
    public boolean overScrollBy(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, boolean z) {
        return super.overScrollBy(0, i2, 0, i4, 0, i6, 0, 0, z);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(motionEvent);
        int actionMasked = motionEvent.getActionMasked();
        int actionIndex = motionEvent.getActionIndex();
        boolean z = false;
        if (actionMasked == 0) {
            int pointerId = motionEvent.getPointerId(0);
            this.mScrollPointerId = pointerId;
            if (motionEvent.findPointerIndex(pointerId) < 0) {
                return false;
            }
            if (!isReadyToOverScroll(!(getLastVisiblePosition() == getAdapter().getCount() - 1), 0)) {
                return super.onInterceptTouchEvent(motionEvent);
            }
            int y = (int) (motionEvent.getY() + 0.5f);
            this.mLastTouchY = y;
            this.mInitialTouchY = y;
            if (this.mScrollState == 2) {
                getParent().requestDisallowInterceptTouchEvent(true);
                setScrollState(1);
            }
            int[] iArr = this.mNestedOffsets;
            iArr[1] = 0;
            iArr[0] = 0;
            startNestedScroll(2);
        } else if (actionMasked == 1) {
            this.mVelocityTracker.clear();
            stopNestedScroll();
        } else if (actionMasked == 2) {
            int findPointerIndex = motionEvent.findPointerIndex(this.mScrollPointerId);
            if (findPointerIndex < 0) {
                return false;
            }
            motionEvent.getX(findPointerIndex);
            int y2 = (int) (motionEvent.getY(findPointerIndex) + 0.5f);
            if (this.mScrollState != 1) {
                if (Math.abs(y2 - this.mInitialTouchY) > this.mTouchSlop) {
                    this.mLastTouchY = y2;
                    z = true;
                }
                if (z) {
                    setScrollState(1);
                }
            }
        } else if (actionMasked == 3) {
            cancelScroll();
        } else if (actionMasked == 5) {
            this.mScrollPointerId = motionEvent.getPointerId(actionIndex);
            int y3 = (int) (motionEvent.getY(actionIndex) + 0.5f);
            this.mLastTouchY = y3;
            this.mInitialTouchY = y3;
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
            int y = (int) (motionEvent.getY() + 0.5f);
            this.mLastTouchY = y;
            this.mInitialTouchY = y;
            int childCount = getChildCount();
            if (childCount > 0) {
                getChildAt(childCount - 1).getBottom();
            }
            startNestedScroll(2);
        } else if (actionMasked == 1) {
            this.mVelocityTracker.addMovement(obtain);
            this.mVelocityTracker.computeCurrentVelocity(1000, (float) this.mMaxFlingVelocity);
            float f = -this.mVelocityTracker.getYVelocity(this.mScrollPointerId);
            if (f == 0.0f) {
                setScrollState(0);
            } else {
                this.mLastYVel = f;
            }
            resetScroll();
            z2 = true;
        } else if (actionMasked == 2) {
            int findPointerIndex = motionEvent.findPointerIndex(this.mScrollPointerId);
            if (findPointerIndex < 0) {
                Log.e("SpringListView", "Error processing scroll; pointer index for id " + this.mScrollPointerId + " not found. Did any MotionEvents get skipped?");
                obtain.recycle();
                return false;
            }
            motionEvent.getX(findPointerIndex);
            int y2 = (int) (motionEvent.getY(findPointerIndex) + 0.5f);
            int i = this.mLastTouchY - y2;
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
                this.mLastTouchY = y2 - this.mScrollOffset[1];
                if (scrollByInternal(0, i, obtain)) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
            }
        } else if (actionMasked == 3) {
            cancelScroll();
        } else if (actionMasked == 5) {
            this.mScrollPointerId = motionEvent.getPointerId(actionIndex);
            int y3 = (int) (motionEvent.getY(actionIndex) + 0.5f);
            this.mLastTouchY = y3;
            this.mInitialTouchY = y3;
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

    public void setEdgeEffectFactory(SpringRelativeLayout.SEdgeEffectFactory sEdgeEffectFactory) {
        this.mEdgeEffectFactory = sEdgeEffectFactory;
        invalidateGlows();
    }

    /* access modifiers changed from: package-private */
    public void invalidateGlows() {
        this.mBottomGlow = null;
        this.mTopGlow = null;
    }

    /* access modifiers changed from: package-private */
    public void ensureTopGlow() {
        SpringRelativeLayout.SEdgeEffectFactory sEdgeEffectFactory = this.mEdgeEffectFactory;
        if (sEdgeEffectFactory == null) {
            Log.e("SpringExpandableListView", "setEdgeEffectFactory first, please!");
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
        SpringRelativeLayout.SEdgeEffectFactory sEdgeEffectFactory = this.mEdgeEffectFactory;
        if (sEdgeEffectFactory == null) {
            Log.e("SpringExpandableListView", "setEdgeEffectFactory first, please!");
        } else if (this.mBottomGlow == null) {
            this.mBottomGlow = sEdgeEffectFactory.createEdgeEffect(this, 3);
            if (getClipToPadding()) {
                this.mBottomGlow.setSize((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight(), (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom());
            } else {
                this.mBottomGlow.setSize(getMeasuredWidth(), getMeasuredHeight());
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0070  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void pullGlows(float r6, float r7, float r8, float r9) {
        /*
        // Method dump skipped, instructions count: 124
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.material.edgeeffect.SpringExpandableListView.pullGlows(float, float, float, float):void");
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

    private void onPointerUp(MotionEvent motionEvent) {
        int actionIndex = motionEvent.getActionIndex();
        if (motionEvent.getPointerId(actionIndex) == this.mScrollPointerId) {
            int i = actionIndex == 0 ? 1 : 0;
            this.mScrollPointerId = motionEvent.getPointerId(i);
            int y = (int) (motionEvent.getY(i) + 0.5f);
            this.mLastTouchY = y;
            this.mInitialTouchY = y;
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
        if (!isReadyToOverScroll(i2 < 0, i2)) {
            return false;
        }
        if (getAdapter() != null) {
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

    private boolean isReadyToOverScroll(boolean z, int i) {
        View childAt;
        ListAdapter adapter = getAdapter();
        if ((adapter == null || adapter.isEmpty()) && getFooterViewsCount() == 0 && getHeaderViewsCount() == 0) {
            return false;
        }
        if (z && getFirstVisiblePosition() == 0) {
            View childAt2 = getChildAt(0);
            if (childAt2 == null || childAt2.getTop() < getListPaddingTop()) {
                return false;
            }
            return true;
        } else if (z || adapter == null || getLastVisiblePosition() != adapter.getCount() - 1 || (childAt = getChildAt(getChildCount() - 1)) == null || childAt.getBottom() > getHeight() - getListPaddingBottom()) {
            return false;
        } else {
            return true;
        }
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

    public int getScrollState() {
        return this.mScrollState;
    }

    private void resetScroll() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.clear();
        }
        stopNestedScroll();
        releaseGlows();
    }

    private void cancelScroll() {
        resetTouch();
        setScrollState(0);
    }

    /* access modifiers changed from: package-private */
    public class OnScrollListenerWrapper implements AbsListView.OnScrollListener {
        int state = 0;

        OnScrollListenerWrapper() {
        }

        public void onScrollStateChanged(AbsListView absListView, int i) {
            this.state = i;
            AbsListView.OnScrollListener onScrollListener = SpringExpandableListView.this.mGivenOnScrollListener;
            if (onScrollListener != null) {
                onScrollListener.onScrollStateChanged(absListView, i);
            }
        }

        public void onScroll(AbsListView absListView, int i, int i2, int i3) {
            AbsListView.OnScrollListener onScrollListener = SpringExpandableListView.this.mGivenOnScrollListener;
            if (onScrollListener != null) {
                onScrollListener.onScroll(absListView, i, i2, i3);
            }
            if (this.state == 1) {
                if (SpringExpandableListView.this.mSpringLayout == null) {
                    ViewGroup viewGroup = (ViewGroup) SpringExpandableListView.this.getParent();
                    if (viewGroup instanceof SpringRelativeLayout) {
                        SpringExpandableListView.this.mSpringLayout = (SpringRelativeLayout) viewGroup;
                    }
                }
                if (SpringExpandableListView.this.mSpringLayout != null) {
                    SpringExpandableListView.this.mSpringLayout.onRecyclerViewScrolled();
                }
            }
            int i4 = this.state;
            if (i4 == 2 || i4 == 1) {
                if (!SpringExpandableListView.this.canScrollVertically(-1) && !SpringExpandableListView.this.mGlowing) {
                    float f = SpringExpandableListView.this.mLastYVel;
                    if (f >= 0.0f) {
                        f = SpringExpandableListView.this.computeVelocity();
                    }
                    SpringExpandableListView springExpandableListView = SpringExpandableListView.this;
                    float f2 = f / 20.0f;
                    springExpandableListView.pullGlows(springExpandableListView.mLastX, 0.0f, SpringExpandableListView.this.mLastY, f2);
                    if (SpringExpandableListView.this.mTopGlow != null) {
                        SpringExpandableListView.this.mTopGlow.onAbsorb((int) f2);
                    }
                }
                if (!SpringExpandableListView.this.canScrollVertically(1) && !SpringExpandableListView.this.mGlowing) {
                    float f3 = SpringExpandableListView.this.mLastYVel;
                    if (f3 <= 0.0f) {
                        f3 = SpringExpandableListView.this.computeVelocity();
                    }
                    SpringExpandableListView springExpandableListView2 = SpringExpandableListView.this;
                    float f4 = f3 / 20.0f;
                    springExpandableListView2.pullGlows(springExpandableListView2.mLastX, 0.0f, SpringExpandableListView.this.mLastY, f4);
                    if (SpringExpandableListView.this.mBottomGlow != null) {
                        SpringExpandableListView.this.mBottomGlow.onAbsorb((int) f4);
                    }
                }
            }
        }
    }

    public void setOnScrollListener(AbsListView.OnScrollListener onScrollListener) {
        if (isUserOnScrollListener(onScrollListener)) {
            this.mGivenOnScrollListener = onScrollListener;
        } else {
            super.setOnScrollListener(onScrollListener);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isUserOnScrollListener(AbsListView.OnScrollListener onScrollListener) {
        return onScrollListener != this.mOnScrollListenerWrapper;
    }

    /* access modifiers changed from: package-private */
    public float computeVelocity() {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.computeCurrentVelocity(1000, (float) this.mMaxFlingVelocity);
        return -this.mVelocityTracker.getYVelocity(this.mScrollPointerId);
    }
}
