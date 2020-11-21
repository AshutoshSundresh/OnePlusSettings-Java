package com.oneplus.settings.edgeeffect;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.EdgeEffect;
import android.widget.ListAdapter;
import android.widget.ListView;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

public class SpringListView2 extends ListView {
    private static final FloatPropertyCompat<SpringListView2> DAMPED_SCROLL = new FloatPropertyCompat<SpringListView2>("value") {
        /* class com.oneplus.settings.edgeeffect.SpringListView2.AnonymousClass1 */

        public float getValue(SpringListView2 springListView2) {
            return springListView2.mDampedScrollShift;
        }

        public void setValue(SpringListView2 springListView2, float f) {
            springListView2.setDampedScrollShift(f);
        }
    };
    private SpringEdgeEffect mActiveEdge;
    private DynamicAnimation.OnAnimationEndListener mAnimationEndListener;
    private EdgeEffect mBottomGlow;
    private float mDampedScrollShift = 0.0f;
    private int mDispatchScrollCounter;
    private float mDistance = 0.0f;
    private SEdgeEffectFactory mEdgeEffectFactory;
    private int mFirstChildTop = 0;
    AbsListView.OnScrollListener mGivenOnScrollListener;
    private boolean mGlowing = false;
    private int mInitialTouchY;
    int mLastChildBottom;
    private int mLastTouchY;
    private float mLastX;
    private float mLastY;
    private float mLastYVel = 0.0f;
    private int mMaxFlingVelocity;
    private int[] mNestedOffsets;
    OnScrollListenerWrapper mOnScrollListenerWrapper = new OnScrollListenerWrapper();
    boolean mOverScrollNested = true;
    private int mPullCount = 0;
    float mPullGrowBottom = 0.9f;
    float mPullGrowTop = 0.1f;
    int[] mScrollConsumed;
    private int[] mScrollOffset;
    private int mScrollPointerId;
    private int mScrollState;
    int[] mScrollStepConsumed;
    private SpringAnimation mSpring;
    private EdgeEffect mTopGlow;
    private int mTouchSlop;
    private VelocityTracker mVelocityTracker;

    public void onScrolled(int i, int i2) {
    }

    static /* synthetic */ float access$316(SpringListView2 springListView2, float f) {
        float f2 = springListView2.mDistance + f;
        springListView2.mDistance = f2;
        return f2;
    }

    static /* synthetic */ int access$508(SpringListView2 springListView2) {
        int i = springListView2.mPullCount;
        springListView2.mPullCount = i + 1;
        return i;
    }

    public SpringListView2(Context context) {
        super(context);
        init();
    }

    public SpringListView2(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public SpringListView2(Context context, AttributeSet attributeSet, int i) {
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
        this.mScrollConsumed = new int[2];
        ViewEdgeEffectFactory createViewEdgeEffectFactory = createViewEdgeEffectFactory();
        this.mEdgeEffectFactory = createViewEdgeEffectFactory;
        setEdgeEffectFactory(createViewEdgeEffectFactory);
        SpringAnimation springAnimation = new SpringAnimation(this, DAMPED_SCROLL, 0.0f);
        this.mSpring = springAnimation;
        SpringForce springForce = new SpringForce(0.0f);
        springForce.setStiffness(590.0f);
        springForce.setDampingRatio(0.5f);
        springAnimation.setSpring(springForce);
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
                this.mLastChildBottom = getChildAt(childCount - 1).getBottom();
            } else {
                this.mLastChildBottom = 0;
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
                this.mLastX = motionEvent.getX();
                this.mLastY = motionEvent.getY();
            }
            resetScroll();
            z2 = true;
        } else if (actionMasked == 2) {
            int findPointerIndex = motionEvent.findPointerIndex(this.mScrollPointerId);
            if (findPointerIndex < 0) {
                Log.e("SpringListView", "Error processing scroll; pointer index for id " + this.mScrollPointerId + " not found. Did any MotionEvents get skipped?");
                return false;
            }
            motionEvent.getX(findPointerIndex);
            int y2 = (int) (motionEvent.getY(findPointerIndex) + 0.5f);
            int i = this.mLastTouchY - y2;
            if (dispatchNestedPreScroll(0, i, this.mScrollConsumed, this.mScrollOffset)) {
                i -= this.mScrollConsumed[1];
                int[] iArr3 = this.mScrollOffset;
                obtain.offsetLocation((float) iArr3[0], (float) iArr3[1]);
                int[] iArr4 = this.mNestedOffsets;
                int i2 = iArr4[0];
                int[] iArr5 = this.mScrollOffset;
                iArr4[0] = i2 + iArr5[0];
                iArr4[1] = iArr4[1] + iArr5[1];
            }
            if (this.mScrollState != 1) {
                int abs = Math.abs(i);
                int i3 = this.mTouchSlop;
                if (abs > i3) {
                    i = i > 0 ? i - i3 : i + i3;
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
        return super.onTouchEvent(motionEvent);
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
        boolean dispatchNestedScroll = dispatchNestedScroll(i5, i6, i4, i3, this.mScrollOffset);
        if (dispatchNestedScroll) {
            int i7 = this.mLastTouchY;
            int[] iArr2 = this.mScrollOffset;
            this.mLastTouchY = i7 - iArr2[1];
            if (motionEvent != null) {
                motionEvent.offsetLocation((float) iArr2[0], (float) iArr2[1]);
            }
            int[] iArr3 = this.mNestedOffsets;
            int i8 = iArr3[0];
            int[] iArr4 = this.mScrollOffset;
            iArr3[0] = i8 + iArr4[0];
            iArr3[1] = iArr3[1] + iArr4[1];
        }
        if ((!dispatchNestedScroll || this.mOverScrollNested) && getOverScrollMode() != 2) {
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

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void pullGlows(float f, float f2, float f3, float f4) {
        if (f3 <= ((float) getHeight()) && f3 >= 0.0f) {
            float height = f3 / ((float) getHeight());
            boolean z = true;
            if (f4 < 0.0f && height < this.mPullGrowBottom && height > this.mPullGrowTop) {
                ensureTopGlow();
                this.mTopGlow.onPull((-f4) / ((float) getHeight()), f / ((float) getWidth()));
                this.mGlowing = true;
            } else if (f4 <= 0.0f || height <= this.mPullGrowTop || height >= this.mPullGrowBottom) {
                z = false;
            } else {
                ensureBottomGlow();
                this.mBottomGlow.onPull(f4 / ((float) getHeight()), 1.0f - (f / ((float) getWidth())));
                this.mGlowing = true;
            }
            if (z || f2 != 0.0f || f4 != 0.0f) {
                postInvalidateOnAnimation();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void scrollStep(int i, int i2, int[] iArr) {
        if (iArr != null) {
            iArr[1] = 0;
        }
    }

    /* access modifiers changed from: package-private */
    public void ensureTopGlow() {
        SEdgeEffectFactory sEdgeEffectFactory = this.mEdgeEffectFactory;
        if (sEdgeEffectFactory == null) {
            throw new IllegalStateException("setEdgeEffectFactory first, please!");
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
            throw new IllegalStateException("setEdgeEffectFactory first, please!");
        } else if (this.mBottomGlow == null) {
            this.mBottomGlow = sEdgeEffectFactory.createEdgeEffect(this, 3);
            if (getClipToPadding()) {
                this.mBottomGlow.setSize((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight(), (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom());
            } else {
                this.mBottomGlow.setSize(getMeasuredWidth(), getMeasuredHeight());
            }
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

    /* access modifiers changed from: protected */
    public void setDampedScrollShift(float f) {
        if (f != this.mDampedScrollShift) {
            this.mDampedScrollShift = f;
            invalidate();
        }
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

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void finishScrollWithVelocity(float f) {
        DynamicAnimation.OnAnimationEndListener onAnimationEndListener = this.mAnimationEndListener;
        if (onAnimationEndListener != null) {
            this.mSpring.addEndListener(onAnimationEndListener);
        }
        this.mSpring.setStartVelocity(f);
        this.mSpring.setStartValue(this.mDampedScrollShift);
        this.mSpring.start();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setActiveEdge(SpringEdgeEffect springEdgeEffect) {
        SpringEdgeEffect springEdgeEffect2 = this.mActiveEdge;
        this.mActiveEdge = springEdgeEffect;
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
    public void setScrollState(int i) {
        if (i != this.mScrollState) {
            this.mScrollState = i;
        }
    }

    public static class SEdgeEffectFactory {
        /* access modifiers changed from: protected */
        public EdgeEffect createEdgeEffect(View view, int i) {
            return new EdgeEffect(view.getContext());
        }
    }

    public ViewEdgeEffectFactory createViewEdgeEffectFactory() {
        return new ViewEdgeEffectFactory();
    }

    /* access modifiers changed from: private */
    public class ViewEdgeEffectFactory extends SEdgeEffectFactory {
        private ViewEdgeEffectFactory() {
        }

        /* access modifiers changed from: protected */
        @Override // com.oneplus.settings.edgeeffect.SpringListView2.SEdgeEffectFactory
        public EdgeEffect createEdgeEffect(View view, int i) {
            if (i == 0 || i == 1) {
                SpringListView2 springListView2 = SpringListView2.this;
                return new SpringEdgeEffect(springListView2.getContext(), 0.3f);
            } else if (i != 2 && i != 3) {
                return super.createEdgeEffect(view, i);
            } else {
                SpringListView2 springListView22 = SpringListView2.this;
                return new SpringEdgeEffect(springListView22.getContext(), -0.3f);
            }
        }
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
            SpringListView2.this.finishScrollWithVelocity(((float) i) * this.mVelocityMultiplier);
            SpringListView2.this.mDistance = 0.0f;
        }

        public void onPull(float f, float f2) {
            if (SpringListView2.this.mSpring.isRunning()) {
                SpringListView2.this.mSpring.cancel();
            }
            SpringListView2.access$508(SpringListView2.this);
            SpringListView2.this.setActiveEdge(this);
            SpringListView2.access$316(SpringListView2.this, f * (this.mVelocityMultiplier / 3.0f));
            SpringListView2 springListView2 = SpringListView2.this;
            springListView2.setDampedScrollShift(springListView2.mDistance * ((float) SpringListView2.this.getHeight()));
        }

        public void onRelease() {
            SpringListView2.this.mDistance = 0.0f;
            SpringListView2.this.mPullCount = 0;
            SpringListView2.this.finishScrollWithVelocity(0.0f);
        }
    }

    public int getScrollState() {
        return this.mScrollState;
    }

    public void onRecyclerViewScrolled() {
        if (this.mPullCount != 1) {
            this.mDistance = 0.0f;
            this.mPullCount = 0;
            finishScrollWithVelocity(0.0f);
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
    public class OnScrollListenerWrapper implements AbsListView.OnScrollListener {
        int state = 0;

        OnScrollListenerWrapper() {
        }

        public void onScrollStateChanged(AbsListView absListView, int i) {
            this.state = i;
            AbsListView.OnScrollListener onScrollListener = SpringListView2.this.mGivenOnScrollListener;
            if (onScrollListener != null) {
                onScrollListener.onScrollStateChanged(absListView, i);
            }
        }

        public void onScroll(AbsListView absListView, int i, int i2, int i3) {
            AbsListView.OnScrollListener onScrollListener = SpringListView2.this.mGivenOnScrollListener;
            if (onScrollListener != null) {
                onScrollListener.onScroll(absListView, i, i2, i3);
            }
            if (this.state == 1) {
                SpringListView2.this.onRecyclerViewScrolled();
            }
            if (this.state == 2) {
                if (i == 0 && SpringListView2.this.getChildAt(0) != null) {
                    View childAt = SpringListView2.this.getChildAt(0);
                    if (childAt == null || childAt.getTop() != SpringListView2.this.getListPaddingTop()) {
                        if (childAt != null) {
                            SpringListView2.this.mFirstChildTop = childAt.getTop();
                        }
                    } else if ((childAt.getTop() > SpringListView2.this.mFirstChildTop || SpringListView2.this.getListPaddingTop() == SpringListView2.this.mFirstChildTop) && !SpringListView2.this.mGlowing) {
                        SpringListView2 springListView2 = SpringListView2.this;
                        springListView2.pullGlows(springListView2.mLastX, 0.0f, SpringListView2.this.mLastY, SpringListView2.this.mLastYVel / 20.0f);
                        if (SpringListView2.this.mTopGlow != null) {
                            SpringListView2.this.mTopGlow.onAbsorb((int) (SpringListView2.this.mLastYVel / 20.0f));
                        }
                    }
                } else if (SpringListView2.this.getAdapter() != null && i + i2 == i3) {
                    View childAt2 = SpringListView2.this.getChildAt(i2 - 1);
                    if (childAt2 == null || childAt2.getBottom() != SpringListView2.this.getHeight() - SpringListView2.this.getListPaddingBottom()) {
                        if (childAt2 != null) {
                            SpringListView2.this.mLastChildBottom = childAt2.getBottom();
                        }
                    } else if (!SpringListView2.this.mGlowing) {
                        int bottom = childAt2.getBottom();
                        SpringListView2 springListView22 = SpringListView2.this;
                        if (bottom < springListView22.mLastChildBottom || springListView22.getHeight() - SpringListView2.this.getListPaddingBottom() == SpringListView2.this.mLastChildBottom) {
                            SpringListView2 springListView23 = SpringListView2.this;
                            springListView23.pullGlows(springListView23.mLastX, 0.0f, SpringListView2.this.mLastY, SpringListView2.this.mLastYVel / 20.0f);
                            if (SpringListView2.this.mBottomGlow != null) {
                                SpringListView2.this.mBottomGlow.onAbsorb((int) (SpringListView2.this.mLastYVel / 20.0f));
                            }
                        }
                    }
                }
            }
        }
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
}
