package androidx.leanback.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import androidx.collection.CircularIntArray;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.leanback.widget.BaseGridView;
import androidx.leanback.widget.Grid;
import androidx.leanback.widget.ItemAlignmentFacet;
import androidx.leanback.widget.WindowAlignment;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* access modifiers changed from: package-private */
public final class GridLayoutManager extends RecyclerView.LayoutManager {
    private static final Rect sTempRect = new Rect();
    static int[] sTwoInts = new int[2];
    final BaseGridView mBaseGridView;
    OnChildLaidOutListener mChildLaidOutListener = null;
    private OnChildSelectedListener mChildSelectedListener = null;
    private ArrayList<OnChildViewHolderSelectedListener> mChildViewHolderSelectedListeners = null;
    int mChildVisibility;
    final ViewsStateBundle mChildrenStates = new ViewsStateBundle();
    GridLinearSmoothScroller mCurrentSmoothScroller;
    int[] mDisappearingPositions;
    private int mExtraLayoutSpace;
    int mExtraLayoutSpaceInPreLayout;
    private FacetProviderAdapter mFacetProviderAdapter;
    private int mFixedRowSizeSecondary;
    int mFlag = 221696;
    int mFocusPosition = -1;
    private int mFocusPositionOffset = 0;
    private int mFocusScrollStrategy = 0;
    private int mGravity = 8388659;
    Grid mGrid;
    private Grid.Provider mGridProvider = new Grid.Provider() {
        /* class androidx.leanback.widget.GridLayoutManager.AnonymousClass2 */

        @Override // androidx.leanback.widget.Grid.Provider
        public int getMinIndex() {
            return GridLayoutManager.this.mPositionDeltaInPreLayout;
        }

        @Override // androidx.leanback.widget.Grid.Provider
        public int getCount() {
            return GridLayoutManager.this.mState.getItemCount() + GridLayoutManager.this.mPositionDeltaInPreLayout;
        }

        @Override // androidx.leanback.widget.Grid.Provider
        public int createItem(int i, boolean z, Object[] objArr, boolean z2) {
            GridLayoutManager gridLayoutManager = GridLayoutManager.this;
            View viewForPosition = gridLayoutManager.getViewForPosition(i - gridLayoutManager.mPositionDeltaInPreLayout);
            if (!((LayoutParams) viewForPosition.getLayoutParams()).isItemRemoved()) {
                if (z2) {
                    if (z) {
                        GridLayoutManager.this.addDisappearingView(viewForPosition);
                    } else {
                        GridLayoutManager.this.addDisappearingView(viewForPosition, 0);
                    }
                } else if (z) {
                    GridLayoutManager.this.addView(viewForPosition);
                } else {
                    GridLayoutManager.this.addView(viewForPosition, 0);
                }
                int i2 = GridLayoutManager.this.mChildVisibility;
                if (i2 != -1) {
                    viewForPosition.setVisibility(i2);
                }
                PendingMoveSmoothScroller pendingMoveSmoothScroller = GridLayoutManager.this.mPendingMoveSmoothScroller;
                if (pendingMoveSmoothScroller != null) {
                    pendingMoveSmoothScroller.consumePendingMovesBeforeLayout();
                }
                int subPositionByView = GridLayoutManager.this.getSubPositionByView(viewForPosition, viewForPosition.findFocus());
                GridLayoutManager gridLayoutManager2 = GridLayoutManager.this;
                int i3 = gridLayoutManager2.mFlag;
                if ((i3 & 3) != 1) {
                    if (i == gridLayoutManager2.mFocusPosition && subPositionByView == gridLayoutManager2.mSubFocusPosition && gridLayoutManager2.mPendingMoveSmoothScroller == null) {
                        gridLayoutManager2.dispatchChildSelected();
                    }
                } else if ((i3 & 4) == 0) {
                    if ((i3 & 16) == 0 && i == gridLayoutManager2.mFocusPosition && subPositionByView == gridLayoutManager2.mSubFocusPosition) {
                        gridLayoutManager2.dispatchChildSelected();
                    } else {
                        GridLayoutManager gridLayoutManager3 = GridLayoutManager.this;
                        if ((gridLayoutManager3.mFlag & 16) != 0 && i >= gridLayoutManager3.mFocusPosition && viewForPosition.hasFocusable()) {
                            GridLayoutManager gridLayoutManager4 = GridLayoutManager.this;
                            gridLayoutManager4.mFocusPosition = i;
                            gridLayoutManager4.mSubFocusPosition = subPositionByView;
                            gridLayoutManager4.mFlag &= -17;
                            gridLayoutManager4.dispatchChildSelected();
                        }
                    }
                }
                GridLayoutManager.this.measureChild(viewForPosition);
            }
            objArr[0] = viewForPosition;
            GridLayoutManager gridLayoutManager5 = GridLayoutManager.this;
            if (gridLayoutManager5.mOrientation == 0) {
                return gridLayoutManager5.getDecoratedMeasuredWidthWithMargin(viewForPosition);
            }
            return gridLayoutManager5.getDecoratedMeasuredHeightWithMargin(viewForPosition);
        }

        @Override // androidx.leanback.widget.Grid.Provider
        public void addItem(Object obj, int i, int i2, int i3, int i4) {
            int i5;
            int i6;
            long j;
            PendingMoveSmoothScroller pendingMoveSmoothScroller;
            View view = (View) obj;
            if (i4 == Integer.MIN_VALUE || i4 == Integer.MAX_VALUE) {
                if (!GridLayoutManager.this.mGrid.isReversedFlow()) {
                    i4 = GridLayoutManager.this.mWindowAlignment.mainAxis().getPaddingMin();
                } else {
                    i4 = GridLayoutManager.this.mWindowAlignment.mainAxis().getSize() - GridLayoutManager.this.mWindowAlignment.mainAxis().getPaddingMax();
                }
            }
            if (!GridLayoutManager.this.mGrid.isReversedFlow()) {
                i5 = i2 + i4;
                i6 = i4;
            } else {
                i6 = i4 - i2;
                i5 = i4;
            }
            int rowStartSecondary = GridLayoutManager.this.getRowStartSecondary(i3) + GridLayoutManager.this.mWindowAlignment.secondAxis().getPaddingMin();
            GridLayoutManager gridLayoutManager = GridLayoutManager.this;
            int i7 = rowStartSecondary - gridLayoutManager.mScrollOffsetSecondary;
            gridLayoutManager.mChildrenStates.loadView(view, i);
            GridLayoutManager.this.layoutChild(i3, view, i6, i5, i7);
            if (!GridLayoutManager.this.mState.isPreLayout()) {
                GridLayoutManager.this.updateScrollLimits();
            }
            GridLayoutManager gridLayoutManager2 = GridLayoutManager.this;
            if (!((gridLayoutManager2.mFlag & 3) == 1 || (pendingMoveSmoothScroller = gridLayoutManager2.mPendingMoveSmoothScroller) == null)) {
                pendingMoveSmoothScroller.consumePendingMovesAfterLayout();
            }
            GridLayoutManager gridLayoutManager3 = GridLayoutManager.this;
            if (gridLayoutManager3.mChildLaidOutListener != null) {
                RecyclerView.ViewHolder childViewHolder = gridLayoutManager3.mBaseGridView.getChildViewHolder(view);
                GridLayoutManager gridLayoutManager4 = GridLayoutManager.this;
                OnChildLaidOutListener onChildLaidOutListener = gridLayoutManager4.mChildLaidOutListener;
                BaseGridView baseGridView = gridLayoutManager4.mBaseGridView;
                if (childViewHolder == null) {
                    j = -1;
                } else {
                    j = childViewHolder.getItemId();
                }
                onChildLaidOutListener.onChildLaidOut(baseGridView, view, i, j);
            }
        }

        @Override // androidx.leanback.widget.Grid.Provider
        public void removeItem(int i) {
            GridLayoutManager gridLayoutManager = GridLayoutManager.this;
            View findViewByPosition = gridLayoutManager.findViewByPosition(i - gridLayoutManager.mPositionDeltaInPreLayout);
            GridLayoutManager gridLayoutManager2 = GridLayoutManager.this;
            if ((gridLayoutManager2.mFlag & 3) == 1) {
                gridLayoutManager2.detachAndScrapView(findViewByPosition, gridLayoutManager2.mRecycler);
            } else {
                gridLayoutManager2.removeAndRecycleView(findViewByPosition, gridLayoutManager2.mRecycler);
            }
        }

        @Override // androidx.leanback.widget.Grid.Provider
        public int getEdge(int i) {
            GridLayoutManager gridLayoutManager = GridLayoutManager.this;
            View findViewByPosition = gridLayoutManager.findViewByPosition(i - gridLayoutManager.mPositionDeltaInPreLayout);
            GridLayoutManager gridLayoutManager2 = GridLayoutManager.this;
            return (gridLayoutManager2.mFlag & 262144) != 0 ? gridLayoutManager2.getViewMax(findViewByPosition) : gridLayoutManager2.getViewMin(findViewByPosition);
        }

        @Override // androidx.leanback.widget.Grid.Provider
        public int getSize(int i) {
            GridLayoutManager gridLayoutManager = GridLayoutManager.this;
            return gridLayoutManager.getViewPrimarySize(gridLayoutManager.findViewByPosition(i - gridLayoutManager.mPositionDeltaInPreLayout));
        }
    };
    private int mHorizontalSpacing;
    private final ItemAlignment mItemAlignment = new ItemAlignment();
    int mMaxPendingMoves = 10;
    private int mMaxSizeSecondary;
    private int[] mMeasuredDimension = new int[2];
    int mNumRows;
    private int mNumRowsRequested = 1;
    ArrayList<BaseGridView.OnLayoutCompletedListener> mOnLayoutCompletedListeners = null;
    int mOrientation = 0;
    private OrientationHelper mOrientationHelper = OrientationHelper.createHorizontalHelper(this);
    PendingMoveSmoothScroller mPendingMoveSmoothScroller;
    int mPositionDeltaInPreLayout;
    final SparseIntArray mPositionToRowInPostLayout = new SparseIntArray();
    private int mPrimaryScrollExtra;
    RecyclerView.Recycler mRecycler;
    private final Runnable mRequestLayoutRunnable = new Runnable() {
        /* class androidx.leanback.widget.GridLayoutManager.AnonymousClass1 */

        public void run() {
            GridLayoutManager.this.requestLayout();
        }
    };
    private int[] mRowSizeSecondary;
    private int mRowSizeSecondaryRequested;
    private int mSaveContextLevel;
    int mScrollOffsetSecondary;
    private int mSizePrimary;
    float mSmoothScrollSpeedFactor = 1.0f;
    private int mSpacingPrimary;
    private int mSpacingSecondary;
    RecyclerView.State mState;
    int mSubFocusPosition = 0;
    private int mVerticalSpacing;
    final WindowAlignment mWindowAlignment = new WindowAlignment();

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public boolean requestChildRectangleOnScreen(RecyclerView recyclerView, View view, Rect rect, boolean z) {
        return false;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public boolean supportsPredictiveItemAnimations() {
        return true;
    }

    /* access modifiers changed from: package-private */
    public static final class LayoutParams extends RecyclerView.LayoutParams {
        private int[] mAlignMultiple;
        private int mAlignX;
        private int mAlignY;
        private ItemAlignmentFacet mAlignmentFacet;
        int mBottomInset;
        int mLeftInset;
        int mRightInset;
        int mTopInset;

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        public LayoutParams(int i, int i2) {
            super(i, i2);
        }

        public LayoutParams(ViewGroup.MarginLayoutParams marginLayoutParams) {
            super(marginLayoutParams);
        }

        public LayoutParams(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
        }

        public LayoutParams(RecyclerView.LayoutParams layoutParams) {
            super(layoutParams);
        }

        public LayoutParams(LayoutParams layoutParams) {
            super((RecyclerView.LayoutParams) layoutParams);
        }

        /* access modifiers changed from: package-private */
        public int getAlignX() {
            return this.mAlignX;
        }

        /* access modifiers changed from: package-private */
        public int getAlignY() {
            return this.mAlignY;
        }

        /* access modifiers changed from: package-private */
        public int getOpticalLeft(View view) {
            return view.getLeft() + this.mLeftInset;
        }

        /* access modifiers changed from: package-private */
        public int getOpticalTop(View view) {
            return view.getTop() + this.mTopInset;
        }

        /* access modifiers changed from: package-private */
        public int getOpticalRight(View view) {
            return view.getRight() - this.mRightInset;
        }

        /* access modifiers changed from: package-private */
        public int getOpticalWidth(View view) {
            return (view.getWidth() - this.mLeftInset) - this.mRightInset;
        }

        /* access modifiers changed from: package-private */
        public int getOpticalHeight(View view) {
            return (view.getHeight() - this.mTopInset) - this.mBottomInset;
        }

        /* access modifiers changed from: package-private */
        public int getOpticalLeftInset() {
            return this.mLeftInset;
        }

        /* access modifiers changed from: package-private */
        public int getOpticalRightInset() {
            return this.mRightInset;
        }

        /* access modifiers changed from: package-private */
        public int getOpticalTopInset() {
            return this.mTopInset;
        }

        /* access modifiers changed from: package-private */
        public void setAlignX(int i) {
            this.mAlignX = i;
        }

        /* access modifiers changed from: package-private */
        public void setAlignY(int i) {
            this.mAlignY = i;
        }

        /* access modifiers changed from: package-private */
        public void setItemAlignmentFacet(ItemAlignmentFacet itemAlignmentFacet) {
            this.mAlignmentFacet = itemAlignmentFacet;
        }

        /* access modifiers changed from: package-private */
        public ItemAlignmentFacet getItemAlignmentFacet() {
            return this.mAlignmentFacet;
        }

        /* access modifiers changed from: package-private */
        public void calculateItemAlignments(int i, View view) {
            ItemAlignmentFacet.ItemAlignmentDef[] alignmentDefs = this.mAlignmentFacet.getAlignmentDefs();
            int[] iArr = this.mAlignMultiple;
            if (iArr == null || iArr.length != alignmentDefs.length) {
                this.mAlignMultiple = new int[alignmentDefs.length];
            }
            for (int i2 = 0; i2 < alignmentDefs.length; i2++) {
                this.mAlignMultiple[i2] = ItemAlignmentFacetHelper.getAlignmentPosition(view, alignmentDefs[i2], i);
            }
            if (i == 0) {
                this.mAlignX = this.mAlignMultiple[0];
            } else {
                this.mAlignY = this.mAlignMultiple[0];
            }
        }

        /* access modifiers changed from: package-private */
        public int[] getAlignMultiple() {
            return this.mAlignMultiple;
        }

        /* access modifiers changed from: package-private */
        public void setOpticalInsets(int i, int i2, int i3, int i4) {
            this.mLeftInset = i;
            this.mTopInset = i2;
            this.mRightInset = i3;
            this.mBottomInset = i4;
        }
    }

    /* access modifiers changed from: package-private */
    public abstract class GridLinearSmoothScroller extends LinearSmoothScroller {
        boolean mSkipOnStopInternal;

        GridLinearSmoothScroller() {
            super(GridLayoutManager.this.mBaseGridView.getContext());
        }

        /* access modifiers changed from: protected */
        @Override // androidx.recyclerview.widget.LinearSmoothScroller, androidx.recyclerview.widget.RecyclerView.SmoothScroller
        public void onStop() {
            super.onStop();
            if (!this.mSkipOnStopInternal) {
                onStopInternal();
            }
            GridLayoutManager gridLayoutManager = GridLayoutManager.this;
            if (gridLayoutManager.mCurrentSmoothScroller == this) {
                gridLayoutManager.mCurrentSmoothScroller = null;
            }
            GridLayoutManager gridLayoutManager2 = GridLayoutManager.this;
            if (gridLayoutManager2.mPendingMoveSmoothScroller == this) {
                gridLayoutManager2.mPendingMoveSmoothScroller = null;
            }
        }

        /* access modifiers changed from: protected */
        public void onStopInternal() {
            View findViewByPosition = findViewByPosition(getTargetPosition());
            if (findViewByPosition != null) {
                if (GridLayoutManager.this.mFocusPosition != getTargetPosition()) {
                    GridLayoutManager.this.mFocusPosition = getTargetPosition();
                }
                if (GridLayoutManager.this.hasFocus()) {
                    GridLayoutManager.this.mFlag |= 32;
                    findViewByPosition.requestFocus();
                    GridLayoutManager.this.mFlag &= -33;
                }
                GridLayoutManager.this.dispatchChildSelected();
                GridLayoutManager.this.dispatchChildSelectedAndPositioned();
            } else if (getTargetPosition() >= 0) {
                GridLayoutManager.this.scrollToSelection(getTargetPosition(), 0, false, 0);
            }
        }

        /* access modifiers changed from: protected */
        @Override // androidx.recyclerview.widget.LinearSmoothScroller
        public float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
            return super.calculateSpeedPerPixel(displayMetrics) * GridLayoutManager.this.mSmoothScrollSpeedFactor;
        }

        /* access modifiers changed from: protected */
        @Override // androidx.recyclerview.widget.LinearSmoothScroller
        public int calculateTimeForScrolling(int i) {
            int calculateTimeForScrolling = super.calculateTimeForScrolling(i);
            if (GridLayoutManager.this.mWindowAlignment.mainAxis().getSize() <= 0) {
                return calculateTimeForScrolling;
            }
            float size = (30.0f / ((float) GridLayoutManager.this.mWindowAlignment.mainAxis().getSize())) * ((float) i);
            return ((float) calculateTimeForScrolling) < size ? (int) size : calculateTimeForScrolling;
        }

        /* access modifiers changed from: protected */
        @Override // androidx.recyclerview.widget.LinearSmoothScroller, androidx.recyclerview.widget.RecyclerView.SmoothScroller
        public void onTargetFound(View view, RecyclerView.State state, RecyclerView.SmoothScroller.Action action) {
            int i;
            int i2;
            if (GridLayoutManager.this.getScrollPosition(view, null, GridLayoutManager.sTwoInts)) {
                if (GridLayoutManager.this.mOrientation == 0) {
                    int[] iArr = GridLayoutManager.sTwoInts;
                    i = iArr[0];
                    i2 = iArr[1];
                } else {
                    int[] iArr2 = GridLayoutManager.sTwoInts;
                    int i3 = iArr2[1];
                    i2 = iArr2[0];
                    i = i3;
                }
                action.update(i, i2, calculateTimeForDeceleration((int) Math.sqrt((double) ((i * i) + (i2 * i2)))), this.mDecelerateInterpolator);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public final class PendingMoveSmoothScroller extends GridLinearSmoothScroller {
        private int mPendingMoves;
        private final boolean mStaggeredGrid;

        PendingMoveSmoothScroller(int i, boolean z) {
            super();
            this.mPendingMoves = i;
            this.mStaggeredGrid = z;
            setTargetPosition(-2);
        }

        /* access modifiers changed from: package-private */
        public void increasePendingMoves() {
            int i = this.mPendingMoves;
            if (i < GridLayoutManager.this.mMaxPendingMoves) {
                this.mPendingMoves = i + 1;
            }
        }

        /* access modifiers changed from: package-private */
        public void decreasePendingMoves() {
            int i = this.mPendingMoves;
            if (i > (-GridLayoutManager.this.mMaxPendingMoves)) {
                this.mPendingMoves = i - 1;
            }
        }

        /* access modifiers changed from: package-private */
        /* JADX WARNING: Removed duplicated region for block: B:12:0x001f  */
        /* JADX WARNING: Removed duplicated region for block: B:26:0x0054  */
        /* JADX WARNING: Removed duplicated region for block: B:31:? A[ADDED_TO_REGION, RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void consumePendingMovesBeforeLayout() {
            /*
            // Method dump skipped, instructions count: 112
            */
            throw new UnsupportedOperationException("Method not decompiled: androidx.leanback.widget.GridLayoutManager.PendingMoveSmoothScroller.consumePendingMovesBeforeLayout():void");
        }

        /* access modifiers changed from: package-private */
        public void consumePendingMovesAfterLayout() {
            int i;
            if (this.mStaggeredGrid && (i = this.mPendingMoves) != 0) {
                this.mPendingMoves = GridLayoutManager.this.processSelectionMoves(true, i);
            }
            int i2 = this.mPendingMoves;
            if (i2 == 0 || ((i2 > 0 && GridLayoutManager.this.hasCreatedLastItem()) || (this.mPendingMoves < 0 && GridLayoutManager.this.hasCreatedFirstItem()))) {
                setTargetPosition(GridLayoutManager.this.mFocusPosition);
                stop();
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.SmoothScroller
        public PointF computeScrollVectorForPosition(int i) {
            int i2 = this.mPendingMoves;
            if (i2 == 0) {
                return null;
            }
            int i3 = ((GridLayoutManager.this.mFlag & 262144) == 0 ? i2 >= 0 : i2 <= 0) ? 1 : -1;
            if (GridLayoutManager.this.mOrientation == 0) {
                return new PointF((float) i3, 0.0f);
            }
            return new PointF(0.0f, (float) i3);
        }

        /* access modifiers changed from: protected */
        @Override // androidx.leanback.widget.GridLayoutManager.GridLinearSmoothScroller
        public void onStopInternal() {
            super.onStopInternal();
            this.mPendingMoves = 0;
            View findViewByPosition = findViewByPosition(getTargetPosition());
            if (findViewByPosition != null) {
                GridLayoutManager.this.scrollToView(findViewByPosition, true);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public String getTag() {
        return "GridLayoutManager:" + this.mBaseGridView.getId();
    }

    public GridLayoutManager(BaseGridView baseGridView) {
        this.mBaseGridView = baseGridView;
        this.mChildVisibility = -1;
        setItemPrefetchEnabled(false);
    }

    public void setOrientation(int i) {
        if (i == 0 || i == 1) {
            this.mOrientation = i;
            this.mOrientationHelper = OrientationHelper.createOrientationHelper(this, i);
            this.mWindowAlignment.setOrientation(i);
            this.mItemAlignment.setOrientation(i);
            this.mFlag |= 256;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x0019  */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x0018 A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onRtlPropertiesChanged(int r6) {
        /*
            r5 = this;
            int r0 = r5.mOrientation
            r1 = 0
            r2 = 1
            if (r0 != 0) goto L_0x000d
            if (r6 != r2) goto L_0x000b
            r0 = 262144(0x40000, float:3.67342E-40)
            goto L_0x0011
        L_0x000b:
            r0 = r1
            goto L_0x0011
        L_0x000d:
            if (r6 != r2) goto L_0x000b
            r0 = 524288(0x80000, float:7.34684E-40)
        L_0x0011:
            int r3 = r5.mFlag
            r4 = 786432(0xc0000, float:1.102026E-39)
            r4 = r4 & r3
            if (r4 != r0) goto L_0x0019
            return
        L_0x0019:
            r4 = -786433(0xfffffffffff3ffff, float:NaN)
            r3 = r3 & r4
            r0 = r0 | r3
            r5.mFlag = r0
            r0 = r0 | 256(0x100, float:3.59E-43)
            r5.mFlag = r0
            androidx.leanback.widget.WindowAlignment r5 = r5.mWindowAlignment
            androidx.leanback.widget.WindowAlignment$Axis r5 = r5.horizontal
            if (r6 != r2) goto L_0x002b
            r1 = r2
        L_0x002b:
            r5.setReversedFlow(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.leanback.widget.GridLayoutManager.onRtlPropertiesChanged(int):void");
    }

    public int getFocusScrollStrategy() {
        return this.mFocusScrollStrategy;
    }

    public void setFocusScrollStrategy(int i) {
        this.mFocusScrollStrategy = i;
    }

    public void setWindowAlignment(int i) {
        this.mWindowAlignment.mainAxis().setWindowAlignment(i);
    }

    public int getWindowAlignment() {
        return this.mWindowAlignment.mainAxis().getWindowAlignment();
    }

    public void setWindowAlignmentOffset(int i) {
        this.mWindowAlignment.mainAxis().setWindowAlignmentOffset(i);
    }

    public int getWindowAlignmentOffset() {
        return this.mWindowAlignment.mainAxis().getWindowAlignmentOffset();
    }

    public void setWindowAlignmentOffsetPercent(float f) {
        this.mWindowAlignment.mainAxis().setWindowAlignmentOffsetPercent(f);
    }

    public float getWindowAlignmentOffsetPercent() {
        return this.mWindowAlignment.mainAxis().getWindowAlignmentOffsetPercent();
    }

    public void setItemAlignmentOffset(int i) {
        this.mItemAlignment.mainAxis().setItemAlignmentOffset(i);
        updateChildAlignments();
    }

    public int getItemAlignmentOffset() {
        return this.mItemAlignment.mainAxis().getItemAlignmentOffset();
    }

    public void setItemAlignmentOffsetWithPadding(boolean z) {
        this.mItemAlignment.mainAxis().setItemAlignmentOffsetWithPadding(z);
        updateChildAlignments();
    }

    public void setItemAlignmentOffsetPercent(float f) {
        this.mItemAlignment.mainAxis().setItemAlignmentOffsetPercent(f);
        updateChildAlignments();
    }

    public float getItemAlignmentOffsetPercent() {
        return this.mItemAlignment.mainAxis().getItemAlignmentOffsetPercent();
    }

    public void setItemAlignmentViewId(int i) {
        this.mItemAlignment.mainAxis().setItemAlignmentViewId(i);
        updateChildAlignments();
    }

    public int getItemAlignmentViewId() {
        return this.mItemAlignment.mainAxis().getItemAlignmentViewId();
    }

    public void setFocusOutAllowed(boolean z, boolean z2) {
        int i = 0;
        int i2 = (z ? 2048 : 0) | (this.mFlag & -6145);
        if (z2) {
            i = 4096;
        }
        this.mFlag = i2 | i;
    }

    public void setFocusOutSideAllowed(boolean z, boolean z2) {
        int i = 0;
        int i2 = (z ? 8192 : 0) | (this.mFlag & -24577);
        if (z2) {
            i = 16384;
        }
        this.mFlag = i2 | i;
    }

    public void setNumRows(int i) {
        if (i >= 0) {
            this.mNumRowsRequested = i;
            return;
        }
        throw new IllegalArgumentException();
    }

    public void setRowHeight(int i) {
        if (i >= 0 || i == -2) {
            this.mRowSizeSecondaryRequested = i;
            return;
        }
        throw new IllegalArgumentException("Invalid row height: " + i);
    }

    public void setItemSpacing(int i) {
        this.mHorizontalSpacing = i;
        this.mVerticalSpacing = i;
        this.mSpacingSecondary = i;
        this.mSpacingPrimary = i;
    }

    public void setVerticalSpacing(int i) {
        if (this.mOrientation == 1) {
            this.mVerticalSpacing = i;
            this.mSpacingPrimary = i;
            return;
        }
        this.mVerticalSpacing = i;
        this.mSpacingSecondary = i;
    }

    public void setHorizontalSpacing(int i) {
        if (this.mOrientation == 0) {
            this.mHorizontalSpacing = i;
            this.mSpacingPrimary = i;
            return;
        }
        this.mHorizontalSpacing = i;
        this.mSpacingSecondary = i;
    }

    public int getVerticalSpacing() {
        return this.mVerticalSpacing;
    }

    public int getHorizontalSpacing() {
        return this.mHorizontalSpacing;
    }

    public void setGravity(int i) {
        this.mGravity = i;
    }

    /* access modifiers changed from: protected */
    public boolean hasDoneFirstLayout() {
        return this.mGrid != null;
    }

    public void setOnChildSelectedListener(OnChildSelectedListener onChildSelectedListener) {
        this.mChildSelectedListener = onChildSelectedListener;
    }

    public void setOnChildViewHolderSelectedListener(OnChildViewHolderSelectedListener onChildViewHolderSelectedListener) {
        if (onChildViewHolderSelectedListener == null) {
            this.mChildViewHolderSelectedListeners = null;
            return;
        }
        ArrayList<OnChildViewHolderSelectedListener> arrayList = this.mChildViewHolderSelectedListeners;
        if (arrayList == null) {
            this.mChildViewHolderSelectedListeners = new ArrayList<>();
        } else {
            arrayList.clear();
        }
        this.mChildViewHolderSelectedListeners.add(onChildViewHolderSelectedListener);
    }

    public void addOnChildViewHolderSelectedListener(OnChildViewHolderSelectedListener onChildViewHolderSelectedListener) {
        if (this.mChildViewHolderSelectedListeners == null) {
            this.mChildViewHolderSelectedListeners = new ArrayList<>();
        }
        this.mChildViewHolderSelectedListeners.add(onChildViewHolderSelectedListener);
    }

    public void removeOnChildViewHolderSelectedListener(OnChildViewHolderSelectedListener onChildViewHolderSelectedListener) {
        ArrayList<OnChildViewHolderSelectedListener> arrayList = this.mChildViewHolderSelectedListeners;
        if (arrayList != null) {
            arrayList.remove(onChildViewHolderSelectedListener);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean hasOnChildViewHolderSelectedListener() {
        ArrayList<OnChildViewHolderSelectedListener> arrayList = this.mChildViewHolderSelectedListeners;
        return arrayList != null && arrayList.size() > 0;
    }

    /* access modifiers changed from: package-private */
    public void fireOnChildViewHolderSelected(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int i, int i2) {
        ArrayList<OnChildViewHolderSelectedListener> arrayList = this.mChildViewHolderSelectedListeners;
        if (arrayList != null) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                this.mChildViewHolderSelectedListeners.get(size).onChildViewHolderSelected(recyclerView, viewHolder, i, i2);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void fireOnChildViewHolderSelectedAndPositioned(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int i, int i2) {
        ArrayList<OnChildViewHolderSelectedListener> arrayList = this.mChildViewHolderSelectedListeners;
        if (arrayList != null) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                this.mChildViewHolderSelectedListeners.get(size).onChildViewHolderSelectedAndPositioned(recyclerView, viewHolder, i, i2);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setOnChildLaidOutListener(OnChildLaidOutListener onChildLaidOutListener) {
        this.mChildLaidOutListener = onChildLaidOutListener;
    }

    private int getAdapterPositionByView(View view) {
        LayoutParams layoutParams;
        if (view == null || (layoutParams = (LayoutParams) view.getLayoutParams()) == null || layoutParams.isItemRemoved()) {
            return -1;
        }
        return layoutParams.getViewAdapterPosition();
    }

    /* access modifiers changed from: package-private */
    public int getSubPositionByView(View view, View view2) {
        ItemAlignmentFacet itemAlignmentFacet;
        if (!(view == null || view2 == null || (itemAlignmentFacet = ((LayoutParams) view.getLayoutParams()).getItemAlignmentFacet()) == null)) {
            ItemAlignmentFacet.ItemAlignmentDef[] alignmentDefs = itemAlignmentFacet.getAlignmentDefs();
            if (alignmentDefs.length > 1) {
                while (view2 != view) {
                    int id = view2.getId();
                    if (id != -1) {
                        for (int i = 1; i < alignmentDefs.length; i++) {
                            if (alignmentDefs[i].getItemAlignmentFocusViewId() == id) {
                                return i;
                            }
                        }
                        continue;
                    }
                    view2 = (View) view2.getParent();
                }
            }
        }
        return 0;
    }

    private int getAdapterPositionByIndex(int i) {
        return getAdapterPositionByView(getChildAt(i));
    }

    /* access modifiers changed from: package-private */
    public void dispatchChildSelected() {
        long j;
        if (this.mChildSelectedListener != null || hasOnChildViewHolderSelectedListener()) {
            int i = this.mFocusPosition;
            View findViewByPosition = i == -1 ? null : findViewByPosition(i);
            if (findViewByPosition != null) {
                RecyclerView.ViewHolder childViewHolder = this.mBaseGridView.getChildViewHolder(findViewByPosition);
                OnChildSelectedListener onChildSelectedListener = this.mChildSelectedListener;
                if (onChildSelectedListener != null) {
                    BaseGridView baseGridView = this.mBaseGridView;
                    int i2 = this.mFocusPosition;
                    if (childViewHolder == null) {
                        j = -1;
                    } else {
                        j = childViewHolder.getItemId();
                    }
                    onChildSelectedListener.onChildSelected(baseGridView, findViewByPosition, i2, j);
                }
                fireOnChildViewHolderSelected(this.mBaseGridView, childViewHolder, this.mFocusPosition, this.mSubFocusPosition);
            } else {
                OnChildSelectedListener onChildSelectedListener2 = this.mChildSelectedListener;
                if (onChildSelectedListener2 != null) {
                    onChildSelectedListener2.onChildSelected(this.mBaseGridView, null, -1, -1);
                }
                fireOnChildViewHolderSelected(this.mBaseGridView, null, -1, 0);
            }
            if (!((this.mFlag & 3) == 1 || this.mBaseGridView.isLayoutRequested())) {
                int childCount = getChildCount();
                for (int i3 = 0; i3 < childCount; i3++) {
                    if (getChildAt(i3).isLayoutRequested()) {
                        forceRequestLayout();
                        return;
                    }
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void dispatchChildSelectedAndPositioned() {
        if (hasOnChildViewHolderSelectedListener()) {
            int i = this.mFocusPosition;
            View findViewByPosition = i == -1 ? null : findViewByPosition(i);
            if (findViewByPosition != null) {
                fireOnChildViewHolderSelectedAndPositioned(this.mBaseGridView, this.mBaseGridView.getChildViewHolder(findViewByPosition), this.mFocusPosition, this.mSubFocusPosition);
                return;
            }
            OnChildSelectedListener onChildSelectedListener = this.mChildSelectedListener;
            if (onChildSelectedListener != null) {
                onChildSelectedListener.onChildSelected(this.mBaseGridView, null, -1, -1);
            }
            fireOnChildViewHolderSelectedAndPositioned(this.mBaseGridView, null, -1, 0);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public boolean canScrollHorizontally() {
        return this.mOrientation == 0 || this.mNumRows > 1;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public boolean canScrollVertically() {
        return this.mOrientation == 1 || this.mNumRows > 1;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public RecyclerView.LayoutParams generateLayoutParams(Context context, AttributeSet attributeSet) {
        return new LayoutParams(context, attributeSet);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        if (layoutParams instanceof LayoutParams) {
            return new LayoutParams((LayoutParams) layoutParams);
        }
        if (layoutParams instanceof RecyclerView.LayoutParams) {
            return new LayoutParams((RecyclerView.LayoutParams) layoutParams);
        }
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            return new LayoutParams((ViewGroup.MarginLayoutParams) layoutParams);
        }
        return new LayoutParams(layoutParams);
    }

    /* access modifiers changed from: protected */
    public View getViewForPosition(int i) {
        View viewForPosition = this.mRecycler.getViewForPosition(i);
        ((LayoutParams) viewForPosition.getLayoutParams()).setItemAlignmentFacet((ItemAlignmentFacet) getFacet(this.mBaseGridView.getChildViewHolder(viewForPosition), ItemAlignmentFacet.class));
        return viewForPosition;
    }

    /* access modifiers changed from: package-private */
    public final int getOpticalLeft(View view) {
        return ((LayoutParams) view.getLayoutParams()).getOpticalLeft(view);
    }

    /* access modifiers changed from: package-private */
    public final int getOpticalRight(View view) {
        return ((LayoutParams) view.getLayoutParams()).getOpticalRight(view);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public int getDecoratedLeft(View view) {
        return super.getDecoratedLeft(view) + ((LayoutParams) view.getLayoutParams()).mLeftInset;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public int getDecoratedTop(View view) {
        return super.getDecoratedTop(view) + ((LayoutParams) view.getLayoutParams()).mTopInset;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public int getDecoratedRight(View view) {
        return super.getDecoratedRight(view) - ((LayoutParams) view.getLayoutParams()).mRightInset;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public int getDecoratedBottom(View view) {
        return super.getDecoratedBottom(view) - ((LayoutParams) view.getLayoutParams()).mBottomInset;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void getDecoratedBoundsWithMargins(View view, Rect rect) {
        super.getDecoratedBoundsWithMargins(view, rect);
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        rect.left += layoutParams.mLeftInset;
        rect.top += layoutParams.mTopInset;
        rect.right -= layoutParams.mRightInset;
        rect.bottom -= layoutParams.mBottomInset;
    }

    /* access modifiers changed from: package-private */
    public int getViewMin(View view) {
        return this.mOrientationHelper.getDecoratedStart(view);
    }

    /* access modifiers changed from: package-private */
    public int getViewMax(View view) {
        return this.mOrientationHelper.getDecoratedEnd(view);
    }

    /* access modifiers changed from: package-private */
    public int getViewPrimarySize(View view) {
        getDecoratedBoundsWithMargins(view, sTempRect);
        return this.mOrientation == 0 ? sTempRect.width() : sTempRect.height();
    }

    private int getViewCenter(View view) {
        return this.mOrientation == 0 ? getViewCenterX(view) : getViewCenterY(view);
    }

    private int getViewCenterSecondary(View view) {
        return this.mOrientation == 0 ? getViewCenterY(view) : getViewCenterX(view);
    }

    private int getViewCenterX(View view) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        return layoutParams.getOpticalLeft(view) + layoutParams.getAlignX();
    }

    private int getViewCenterY(View view) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        return layoutParams.getOpticalTop(view) + layoutParams.getAlignY();
    }

    private void saveContext(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (this.mSaveContextLevel == 0) {
            this.mRecycler = recycler;
            this.mState = state;
            this.mPositionDeltaInPreLayout = 0;
            this.mExtraLayoutSpaceInPreLayout = 0;
        }
        this.mSaveContextLevel++;
    }

    private void leaveContext() {
        int i = this.mSaveContextLevel - 1;
        this.mSaveContextLevel = i;
        if (i == 0) {
            this.mRecycler = null;
            this.mState = null;
            this.mPositionDeltaInPreLayout = 0;
            this.mExtraLayoutSpaceInPreLayout = 0;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0074, code lost:
        if (((r5.mFlag & 262144) != 0) != r5.mGrid.isReversedFlow()) goto L_0x0076;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean layoutInit() {
        /*
        // Method dump skipped, instructions count: 185
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.leanback.widget.GridLayoutManager.layoutInit():boolean");
    }

    private int getRowSizeSecondary(int i) {
        int i2 = this.mFixedRowSizeSecondary;
        if (i2 != 0) {
            return i2;
        }
        int[] iArr = this.mRowSizeSecondary;
        if (iArr == null) {
            return 0;
        }
        return iArr[i];
    }

    /* access modifiers changed from: package-private */
    public int getRowStartSecondary(int i) {
        int i2 = 0;
        if ((this.mFlag & 524288) != 0) {
            for (int i3 = this.mNumRows - 1; i3 > i; i3--) {
                i2 += getRowSizeSecondary(i3) + this.mSpacingSecondary;
            }
            return i2;
        }
        int i4 = 0;
        while (i2 < i) {
            i4 += getRowSizeSecondary(i2) + this.mSpacingSecondary;
            i2++;
        }
        return i4;
    }

    private int getSizeSecondary() {
        int i = (this.mFlag & 524288) != 0 ? 0 : this.mNumRows - 1;
        return getRowStartSecondary(i) + getRowSizeSecondary(i);
    }

    /* access modifiers changed from: package-private */
    public int getDecoratedMeasuredWidthWithMargin(View view) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        return getDecoratedMeasuredWidth(view) + ((ViewGroup.MarginLayoutParams) layoutParams).leftMargin + ((ViewGroup.MarginLayoutParams) layoutParams).rightMargin;
    }

    /* access modifiers changed from: package-private */
    public int getDecoratedMeasuredHeightWithMargin(View view) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        return getDecoratedMeasuredHeight(view) + ((ViewGroup.MarginLayoutParams) layoutParams).topMargin + ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin;
    }

    private void measureScrapChild(int i, int i2, int i3, int[] iArr) {
        View viewForPosition = this.mRecycler.getViewForPosition(i);
        if (viewForPosition != null) {
            LayoutParams layoutParams = (LayoutParams) viewForPosition.getLayoutParams();
            calculateItemDecorationsForChild(viewForPosition, sTempRect);
            int i4 = ((ViewGroup.MarginLayoutParams) layoutParams).leftMargin + ((ViewGroup.MarginLayoutParams) layoutParams).rightMargin;
            Rect rect = sTempRect;
            viewForPosition.measure(ViewGroup.getChildMeasureSpec(i2, getPaddingLeft() + getPaddingRight() + i4 + rect.left + rect.right, ((ViewGroup.MarginLayoutParams) layoutParams).width), ViewGroup.getChildMeasureSpec(i3, getPaddingTop() + getPaddingBottom() + ((ViewGroup.MarginLayoutParams) layoutParams).topMargin + ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin + rect.top + rect.bottom, ((ViewGroup.MarginLayoutParams) layoutParams).height));
            iArr[0] = getDecoratedMeasuredWidthWithMargin(viewForPosition);
            iArr[1] = getDecoratedMeasuredHeightWithMargin(viewForPosition);
            this.mRecycler.recycleView(viewForPosition);
        }
    }

    private boolean processRowSizeSecondary(boolean z) {
        int i;
        if (this.mFixedRowSizeSecondary != 0 || this.mRowSizeSecondary == null) {
            return false;
        }
        Grid grid = this.mGrid;
        CircularIntArray[] itemPositionsInRows = grid == null ? null : grid.getItemPositionsInRows();
        boolean z2 = false;
        int i2 = -1;
        for (int i3 = 0; i3 < this.mNumRows; i3++) {
            CircularIntArray circularIntArray = itemPositionsInRows == null ? null : itemPositionsInRows[i3];
            int size = circularIntArray == null ? 0 : circularIntArray.size();
            int i4 = -1;
            for (int i5 = 0; i5 < size; i5 += 2) {
                int i6 = circularIntArray.get(i5 + 1);
                for (int i7 = circularIntArray.get(i5); i7 <= i6; i7++) {
                    View findViewByPosition = findViewByPosition(i7 - this.mPositionDeltaInPreLayout);
                    if (findViewByPosition != null) {
                        if (z) {
                            measureChild(findViewByPosition);
                        }
                        if (this.mOrientation == 0) {
                            i = getDecoratedMeasuredHeightWithMargin(findViewByPosition);
                        } else {
                            i = getDecoratedMeasuredWidthWithMargin(findViewByPosition);
                        }
                        if (i > i4) {
                            i4 = i;
                        }
                    }
                }
            }
            int itemCount = this.mState.getItemCount();
            if (!this.mBaseGridView.hasFixedSize() && z && i4 < 0 && itemCount > 0) {
                if (i2 < 0) {
                    int i8 = this.mFocusPosition;
                    if (i8 < 0) {
                        i8 = 0;
                    } else if (i8 >= itemCount) {
                        i8 = itemCount - 1;
                    }
                    if (getChildCount() > 0) {
                        int layoutPosition = this.mBaseGridView.getChildViewHolder(getChildAt(0)).getLayoutPosition();
                        int layoutPosition2 = this.mBaseGridView.getChildViewHolder(getChildAt(getChildCount() - 1)).getLayoutPosition();
                        if (i8 >= layoutPosition && i8 <= layoutPosition2) {
                            i8 = i8 - layoutPosition <= layoutPosition2 - i8 ? layoutPosition - 1 : layoutPosition2 + 1;
                            if (i8 < 0 && layoutPosition2 < itemCount - 1) {
                                i8 = layoutPosition2 + 1;
                            } else if (i8 >= itemCount && layoutPosition > 0) {
                                i8 = layoutPosition - 1;
                            }
                        }
                    }
                    if (i8 >= 0 && i8 < itemCount) {
                        measureScrapChild(i8, View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0), this.mMeasuredDimension);
                        if (this.mOrientation == 0) {
                            i2 = this.mMeasuredDimension[1];
                        } else {
                            i2 = this.mMeasuredDimension[0];
                        }
                    }
                }
                if (i2 >= 0) {
                    i4 = i2;
                }
            }
            if (i4 < 0) {
                i4 = 0;
            }
            int[] iArr = this.mRowSizeSecondary;
            if (iArr[i3] != i4) {
                iArr[i3] = i4;
                z2 = true;
            }
        }
        return z2;
    }

    private void updateRowSecondarySizeRefresh() {
        int i = this.mFlag & -1025;
        int i2 = 0;
        if (processRowSizeSecondary(false)) {
            i2 = 1024;
        }
        int i3 = i | i2;
        this.mFlag = i3;
        if ((i3 & 1024) != 0) {
            forceRequestLayout();
        }
    }

    private void forceRequestLayout() {
        ViewCompat.postOnAnimation(this.mBaseGridView, this.mRequestLayoutRunnable);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int i, int i2) {
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        saveContext(recycler, state);
        if (this.mOrientation == 0) {
            i6 = View.MeasureSpec.getSize(i);
            i5 = View.MeasureSpec.getSize(i2);
            i4 = View.MeasureSpec.getMode(i2);
            i3 = getPaddingTop();
            i7 = getPaddingBottom();
        } else {
            i5 = View.MeasureSpec.getSize(i);
            i6 = View.MeasureSpec.getSize(i2);
            i4 = View.MeasureSpec.getMode(i);
            i3 = getPaddingLeft();
            i7 = getPaddingRight();
        }
        int i8 = i3 + i7;
        this.mMaxSizeSecondary = i5;
        int i9 = this.mRowSizeSecondaryRequested;
        if (i9 == -2) {
            int i10 = this.mNumRowsRequested;
            if (i10 == 0) {
                i10 = 1;
            }
            this.mNumRows = i10;
            this.mFixedRowSizeSecondary = 0;
            int[] iArr = this.mRowSizeSecondary;
            if (iArr == null || iArr.length != i10) {
                this.mRowSizeSecondary = new int[this.mNumRows];
            }
            if (this.mState.isPreLayout()) {
                updatePositionDeltaInPreLayout();
            }
            processRowSizeSecondary(true);
            if (i4 == Integer.MIN_VALUE) {
                i5 = Math.min(getSizeSecondary() + i8, this.mMaxSizeSecondary);
            } else if (i4 == 0) {
                i5 = getSizeSecondary() + i8;
            } else if (i4 == 1073741824) {
                i5 = this.mMaxSizeSecondary;
            } else {
                throw new IllegalStateException("wrong spec");
            }
        } else {
            if (i4 != Integer.MIN_VALUE) {
                if (i4 == 0) {
                    if (i9 == 0) {
                        i9 = i5 - i8;
                    }
                    this.mFixedRowSizeSecondary = i9;
                    int i11 = this.mNumRowsRequested;
                    if (i11 == 0) {
                        i11 = 1;
                    }
                    this.mNumRows = i11;
                    i5 = (this.mFixedRowSizeSecondary * i11) + (this.mSpacingSecondary * (i11 - 1)) + i8;
                } else if (i4 != 1073741824) {
                    throw new IllegalStateException("wrong spec");
                }
            }
            if (this.mNumRowsRequested == 0 && this.mRowSizeSecondaryRequested == 0) {
                this.mNumRows = 1;
                this.mFixedRowSizeSecondary = i5 - i8;
            } else {
                int i12 = this.mNumRowsRequested;
                if (i12 == 0) {
                    int i13 = this.mRowSizeSecondaryRequested;
                    this.mFixedRowSizeSecondary = i13;
                    int i14 = this.mSpacingSecondary;
                    this.mNumRows = (i5 + i14) / (i13 + i14);
                } else {
                    int i15 = this.mRowSizeSecondaryRequested;
                    if (i15 == 0) {
                        this.mNumRows = i12;
                        this.mFixedRowSizeSecondary = ((i5 - i8) - (this.mSpacingSecondary * (i12 - 1))) / i12;
                    } else {
                        this.mNumRows = i12;
                        this.mFixedRowSizeSecondary = i15;
                    }
                }
            }
            if (i4 == Integer.MIN_VALUE) {
                int i16 = this.mFixedRowSizeSecondary;
                int i17 = this.mNumRows;
                int i18 = (i16 * i17) + (this.mSpacingSecondary * (i17 - 1)) + i8;
                if (i18 < i5) {
                    i5 = i18;
                }
            }
        }
        if (this.mOrientation == 0) {
            setMeasuredDimension(i6, i5);
        } else {
            setMeasuredDimension(i5, i6);
        }
        leaveContext();
    }

    /* access modifiers changed from: package-private */
    public void measureChild(View view) {
        int i;
        int i2;
        int i3;
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        calculateItemDecorationsForChild(view, sTempRect);
        int i4 = ((ViewGroup.MarginLayoutParams) layoutParams).leftMargin + ((ViewGroup.MarginLayoutParams) layoutParams).rightMargin;
        Rect rect = sTempRect;
        int i5 = i4 + rect.left + rect.right;
        int i6 = ((ViewGroup.MarginLayoutParams) layoutParams).topMargin + ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin + rect.top + rect.bottom;
        if (this.mRowSizeSecondaryRequested == -2) {
            i = View.MeasureSpec.makeMeasureSpec(0, 0);
        } else {
            i = View.MeasureSpec.makeMeasureSpec(this.mFixedRowSizeSecondary, 1073741824);
        }
        if (this.mOrientation == 0) {
            i2 = ViewGroup.getChildMeasureSpec(View.MeasureSpec.makeMeasureSpec(0, 0), i5, ((ViewGroup.MarginLayoutParams) layoutParams).width);
            i3 = ViewGroup.getChildMeasureSpec(i, i6, ((ViewGroup.MarginLayoutParams) layoutParams).height);
        } else {
            int childMeasureSpec = ViewGroup.getChildMeasureSpec(View.MeasureSpec.makeMeasureSpec(0, 0), i6, ((ViewGroup.MarginLayoutParams) layoutParams).height);
            int childMeasureSpec2 = ViewGroup.getChildMeasureSpec(i, i5, ((ViewGroup.MarginLayoutParams) layoutParams).width);
            i3 = childMeasureSpec;
            i2 = childMeasureSpec2;
        }
        view.measure(i2, i3);
    }

    /* access modifiers changed from: package-private */
    public <E> E getFacet(RecyclerView.ViewHolder viewHolder, Class<? extends E> cls) {
        FacetProviderAdapter facetProviderAdapter;
        FacetProvider facetProvider;
        E e = viewHolder instanceof FacetProvider ? (E) ((FacetProvider) viewHolder).getFacet(cls) : null;
        return (e != null || (facetProviderAdapter = this.mFacetProviderAdapter) == null || (facetProvider = facetProviderAdapter.getFacetProvider(viewHolder.getItemViewType())) == null) ? e : (E) facetProvider.getFacet(cls);
    }

    /* access modifiers changed from: package-private */
    public void layoutChild(int i, View view, int i2, int i3, int i4) {
        int i5;
        int i6;
        int i7;
        if (this.mOrientation == 0) {
            i5 = getDecoratedMeasuredHeightWithMargin(view);
        } else {
            i5 = getDecoratedMeasuredWidthWithMargin(view);
        }
        int i8 = this.mFixedRowSizeSecondary;
        if (i8 > 0) {
            i5 = Math.min(i5, i8);
        }
        int i9 = this.mGravity;
        int i10 = i9 & 112;
        int absoluteGravity = (this.mFlag & 786432) != 0 ? Gravity.getAbsoluteGravity(i9 & 8388615, 1) : i9 & 7;
        if (!((this.mOrientation == 0 && i10 == 48) || (this.mOrientation == 1 && absoluteGravity == 3))) {
            if ((this.mOrientation == 0 && i10 == 80) || (this.mOrientation == 1 && absoluteGravity == 5)) {
                i7 = getRowSizeSecondary(i) - i5;
            } else if ((this.mOrientation == 0 && i10 == 16) || (this.mOrientation == 1 && absoluteGravity == 1)) {
                i7 = (getRowSizeSecondary(i) - i5) / 2;
            }
            i4 += i7;
        }
        if (this.mOrientation == 0) {
            i6 = i5 + i4;
        } else {
            int i11 = i5 + i4;
            i4 = i2;
            i2 = i4;
            i6 = i3;
            i3 = i11;
        }
        layoutDecoratedWithMargins(view, i2, i4, i3, i6);
        super.getDecoratedBoundsWithMargins(view, sTempRect);
        Rect rect = sTempRect;
        ((LayoutParams) view.getLayoutParams()).setOpticalInsets(i2 - rect.left, i4 - rect.top, rect.right - i3, rect.bottom - i6);
        updateChildAlignments(view);
    }

    private void updateChildAlignments(View view) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        if (layoutParams.getItemAlignmentFacet() == null) {
            layoutParams.setAlignX(this.mItemAlignment.horizontal.getAlignmentPosition(view));
            layoutParams.setAlignY(this.mItemAlignment.vertical.getAlignmentPosition(view));
            return;
        }
        layoutParams.calculateItemAlignments(this.mOrientation, view);
        if (this.mOrientation == 0) {
            layoutParams.setAlignY(this.mItemAlignment.vertical.getAlignmentPosition(view));
        } else {
            layoutParams.setAlignX(this.mItemAlignment.horizontal.getAlignmentPosition(view));
        }
    }

    private void updateChildAlignments() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            updateChildAlignments(getChildAt(i));
        }
    }

    /* access modifiers changed from: package-private */
    public void setExtraLayoutSpace(int i) {
        int i2 = this.mExtraLayoutSpace;
        if (i2 != i) {
            if (i2 >= 0) {
                this.mExtraLayoutSpace = i;
                requestLayout();
                return;
            }
            throw new IllegalArgumentException("ExtraLayoutSpace must >= 0");
        }
    }

    /* access modifiers changed from: package-private */
    public int getExtraLayoutSpace() {
        return this.mExtraLayoutSpace;
    }

    private void removeInvisibleViewsAtEnd() {
        int i;
        int i2 = this.mFlag;
        if ((65600 & i2) == 65536) {
            Grid grid = this.mGrid;
            int i3 = this.mFocusPosition;
            if ((i2 & 262144) != 0) {
                i = -this.mExtraLayoutSpace;
            } else {
                i = this.mExtraLayoutSpace + this.mSizePrimary;
            }
            grid.removeInvisibleItemsAtEnd(i3, i);
        }
    }

    private void removeInvisibleViewsAtFront() {
        int i = this.mFlag;
        if ((65600 & i) == 65536) {
            this.mGrid.removeInvisibleItemsAtFront(this.mFocusPosition, (i & 262144) != 0 ? this.mSizePrimary + this.mExtraLayoutSpace : -this.mExtraLayoutSpace);
        }
    }

    private boolean appendOneColumnVisibleItems() {
        return this.mGrid.appendOneColumnVisibleItems();
    }

    /* access modifiers changed from: package-private */
    public void slideIn() {
        int i = this.mFlag;
        if ((i & 64) != 0) {
            int i2 = i & -65;
            this.mFlag = i2;
            int i3 = this.mFocusPosition;
            if (i3 >= 0) {
                scrollToSelection(i3, this.mSubFocusPosition, true, this.mPrimaryScrollExtra);
            } else {
                this.mFlag = i2 & -129;
                requestLayout();
            }
            int i4 = this.mFlag;
            if ((i4 & 128) != 0) {
                this.mFlag = i4 & -129;
                if (this.mBaseGridView.getScrollState() != 0 || isSmoothScrolling()) {
                    this.mBaseGridView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        /* class androidx.leanback.widget.GridLayoutManager.AnonymousClass3 */

                        @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                            if (i == 0) {
                                GridLayoutManager.this.mBaseGridView.removeOnScrollListener(this);
                                GridLayoutManager.this.requestLayout();
                            }
                        }
                    });
                } else {
                    requestLayout();
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public int getSlideOutDistance() {
        int i;
        int i2;
        int right;
        if (this.mOrientation == 1) {
            i2 = -getHeight();
            if (getChildCount() <= 0 || (i = getChildAt(0).getTop()) >= 0) {
                return i2;
            }
        } else if ((this.mFlag & 262144) != 0) {
            int width = getWidth();
            return (getChildCount() <= 0 || (right = getChildAt(0).getRight()) <= width) ? width : right;
        } else {
            i2 = -getWidth();
            if (getChildCount() <= 0 || (i = getChildAt(0).getLeft()) >= 0) {
                return i2;
            }
        }
        return i2 + i;
    }

    /* access modifiers changed from: package-private */
    public boolean isSlidingChildViews() {
        return (this.mFlag & 64) != 0;
    }

    /* access modifiers changed from: package-private */
    public void slideOut() {
        int i = this.mFlag;
        if ((i & 64) == 0) {
            this.mFlag = i | 64;
            if (getChildCount() != 0) {
                if (this.mOrientation == 1) {
                    this.mBaseGridView.smoothScrollBy(0, getSlideOutDistance(), new AccelerateDecelerateInterpolator());
                } else {
                    this.mBaseGridView.smoothScrollBy(getSlideOutDistance(), 0, new AccelerateDecelerateInterpolator());
                }
            }
        }
    }

    private boolean prependOneColumnVisibleItems() {
        return this.mGrid.prependOneColumnVisibleItems();
    }

    private void appendVisibleItems() {
        int i;
        Grid grid = this.mGrid;
        if ((this.mFlag & 262144) != 0) {
            i = (-this.mExtraLayoutSpace) - this.mExtraLayoutSpaceInPreLayout;
        } else {
            i = this.mSizePrimary + this.mExtraLayoutSpace + this.mExtraLayoutSpaceInPreLayout;
        }
        grid.appendVisibleItems(i);
    }

    private void prependVisibleItems() {
        int i;
        Grid grid = this.mGrid;
        if ((this.mFlag & 262144) != 0) {
            i = this.mSizePrimary + this.mExtraLayoutSpace + this.mExtraLayoutSpaceInPreLayout;
        } else {
            i = (-this.mExtraLayoutSpace) - this.mExtraLayoutSpaceInPreLayout;
        }
        grid.prependVisibleItems(i);
    }

    /* JADX WARNING: Removed duplicated region for block: B:30:0x00c3 A[LOOP:3: B:30:0x00c3->B:33:0x00d1, LOOP_START] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void fastRelayout() {
        /*
        // Method dump skipped, instructions count: 219
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.leanback.widget.GridLayoutManager.fastRelayout():void");
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void removeAndRecycleAllViews(RecyclerView.Recycler recycler) {
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            removeAndRecycleViewAt(childCount, recycler);
        }
    }

    private void focusToViewInLayout(boolean z, boolean z2, int i, int i2) {
        View findViewByPosition = findViewByPosition(this.mFocusPosition);
        if (findViewByPosition != null && z2) {
            scrollToView(findViewByPosition, false, i, i2);
        }
        if (findViewByPosition != null && z && !findViewByPosition.hasFocus()) {
            findViewByPosition.requestFocus();
        } else if (!z && !this.mBaseGridView.hasFocus()) {
            if (findViewByPosition == null || !findViewByPosition.hasFocusable()) {
                int childCount = getChildCount();
                int i3 = 0;
                while (true) {
                    if (i3 < childCount) {
                        findViewByPosition = getChildAt(i3);
                        if (findViewByPosition != null && findViewByPosition.hasFocusable()) {
                            this.mBaseGridView.focusableViewAvailable(findViewByPosition);
                            break;
                        }
                        i3++;
                    } else {
                        break;
                    }
                }
            } else {
                this.mBaseGridView.focusableViewAvailable(findViewByPosition);
            }
            if (z2 && findViewByPosition != null && findViewByPosition.hasFocus()) {
                scrollToView(findViewByPosition, false, i, i2);
            }
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onLayoutCompleted(RecyclerView.State state) {
        ArrayList<BaseGridView.OnLayoutCompletedListener> arrayList = this.mOnLayoutCompletedListeners;
        if (arrayList != null) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                this.mOnLayoutCompletedListeners.get(size).onLayoutCompleted(state);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void updatePositionToRowMapInPostLayout() {
        Grid.Location location;
        this.mPositionToRowInPostLayout.clear();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            int oldPosition = this.mBaseGridView.getChildViewHolder(getChildAt(i)).getOldPosition();
            if (oldPosition >= 0 && (location = this.mGrid.getLocation(oldPosition)) != null) {
                this.mPositionToRowInPostLayout.put(oldPosition, location.row);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void fillScrapViewsInPostLayout() {
        List<RecyclerView.ViewHolder> scrapList = this.mRecycler.getScrapList();
        int size = scrapList.size();
        if (size != 0) {
            int[] iArr = this.mDisappearingPositions;
            if (iArr == null || size > iArr.length) {
                int[] iArr2 = this.mDisappearingPositions;
                int length = iArr2 == null ? 16 : iArr2.length;
                while (length < size) {
                    length <<= 1;
                }
                this.mDisappearingPositions = new int[length];
            }
            int i = 0;
            for (int i2 = 0; i2 < size; i2++) {
                int absoluteAdapterPosition = scrapList.get(i2).getAbsoluteAdapterPosition();
                if (absoluteAdapterPosition >= 0) {
                    this.mDisappearingPositions[i] = absoluteAdapterPosition;
                    i++;
                }
            }
            if (i > 0) {
                Arrays.sort(this.mDisappearingPositions, 0, i);
                this.mGrid.fillDisappearingItems(this.mDisappearingPositions, i, this.mPositionToRowInPostLayout);
            }
            this.mPositionToRowInPostLayout.clear();
        }
    }

    /* access modifiers changed from: package-private */
    public void updatePositionDeltaInPreLayout() {
        if (getChildCount() > 0) {
            this.mPositionDeltaInPreLayout = this.mGrid.getFirstVisibleIndex() - ((LayoutParams) getChildAt(0).getLayoutParams()).getViewLayoutPosition();
        } else {
            this.mPositionDeltaInPreLayout = 0;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:86:0x0171 A[LOOP:1: B:86:0x0171->B:89:0x017b, LOOP_START] */
    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onLayoutChildren(androidx.recyclerview.widget.RecyclerView.Recycler r13, androidx.recyclerview.widget.RecyclerView.State r14) {
        /*
        // Method dump skipped, instructions count: 518
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.leanback.widget.GridLayoutManager.onLayoutChildren(androidx.recyclerview.widget.RecyclerView$Recycler, androidx.recyclerview.widget.RecyclerView$State):void");
    }

    private void offsetChildrenSecondary(int i) {
        int childCount = getChildCount();
        int i2 = 0;
        if (this.mOrientation == 0) {
            while (i2 < childCount) {
                getChildAt(i2).offsetTopAndBottom(i);
                i2++;
            }
            return;
        }
        while (i2 < childCount) {
            getChildAt(i2).offsetLeftAndRight(i);
            i2++;
        }
    }

    private void offsetChildrenPrimary(int i) {
        int childCount = getChildCount();
        int i2 = 0;
        if (this.mOrientation == 1) {
            while (i2 < childCount) {
                getChildAt(i2).offsetTopAndBottom(i);
                i2++;
            }
            return;
        }
        while (i2 < childCount) {
            getChildAt(i2).offsetLeftAndRight(i);
            i2++;
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public int scrollHorizontallyBy(int i, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int i2;
        if ((this.mFlag & 512) == 0 || !hasDoneFirstLayout()) {
            return 0;
        }
        saveContext(recycler, state);
        this.mFlag = (this.mFlag & -4) | 2;
        if (this.mOrientation == 0) {
            i2 = scrollDirectionPrimary(i);
        } else {
            i2 = scrollDirectionSecondary(i);
        }
        leaveContext();
        this.mFlag &= -4;
        return i2;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public int scrollVerticallyBy(int i, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int i2;
        if ((this.mFlag & 512) == 0 || !hasDoneFirstLayout()) {
            return 0;
        }
        this.mFlag = (this.mFlag & -4) | 2;
        saveContext(recycler, state);
        if (this.mOrientation == 1) {
            i2 = scrollDirectionPrimary(i);
        } else {
            i2 = scrollDirectionSecondary(i);
        }
        leaveContext();
        this.mFlag &= -4;
        return i2;
    }

    private int scrollDirectionPrimary(int i) {
        int i2;
        int i3 = this.mFlag;
        boolean z = true;
        if ((i3 & 64) == 0 && (i3 & 3) != 1 && (i <= 0 ? !(i >= 0 || this.mWindowAlignment.mainAxis().isMinUnknown() || i >= (i2 = this.mWindowAlignment.mainAxis().getMinScroll())) : !(this.mWindowAlignment.mainAxis().isMaxUnknown() || i <= (i2 = this.mWindowAlignment.mainAxis().getMaxScroll())))) {
            i = i2;
        }
        if (i == 0) {
            return 0;
        }
        offsetChildrenPrimary(-i);
        if ((this.mFlag & 3) == 1) {
            updateScrollLimits();
            return i;
        }
        int childCount = getChildCount();
        if ((this.mFlag & 262144) == 0 ? i >= 0 : i <= 0) {
            appendVisibleItems();
        } else {
            prependVisibleItems();
        }
        boolean z2 = getChildCount() > childCount;
        int childCount2 = getChildCount();
        if ((262144 & this.mFlag) == 0 ? i >= 0 : i <= 0) {
            removeInvisibleViewsAtFront();
        } else {
            removeInvisibleViewsAtEnd();
        }
        if (getChildCount() >= childCount2) {
            z = false;
        }
        if (z2 || z) {
            updateRowSecondarySizeRefresh();
        }
        this.mBaseGridView.invalidate();
        updateScrollLimits();
        return i;
    }

    private int scrollDirectionSecondary(int i) {
        if (i == 0) {
            return 0;
        }
        offsetChildrenSecondary(-i);
        this.mScrollOffsetSecondary += i;
        updateSecondaryScrollLimits();
        this.mBaseGridView.invalidate();
        return i;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void collectAdjacentPrefetchPositions(int i, int i2, RecyclerView.State state, RecyclerView.LayoutManager.LayoutPrefetchRegistry layoutPrefetchRegistry) {
        int i3;
        try {
            saveContext(null, state);
            if (this.mOrientation != 0) {
                i = i2;
            }
            if (getChildCount() != 0) {
                if (i != 0) {
                    if (i < 0) {
                        i3 = -this.mExtraLayoutSpace;
                    } else {
                        i3 = this.mSizePrimary + this.mExtraLayoutSpace;
                    }
                    this.mGrid.collectAdjacentPrefetchPositions(i3, i, layoutPrefetchRegistry);
                    leaveContext();
                }
            }
        } finally {
            leaveContext();
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void collectInitialPrefetchPositions(int i, RecyclerView.LayoutManager.LayoutPrefetchRegistry layoutPrefetchRegistry) {
        int i2 = this.mBaseGridView.mInitialPrefetchItemCount;
        if (i != 0 && i2 != 0) {
            int max = Math.max(0, Math.min(this.mFocusPosition - ((i2 - 1) / 2), i - i2));
            int i3 = max;
            while (i3 < i && i3 < max + i2) {
                layoutPrefetchRegistry.addPosition(i3, 0);
                i3++;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void updateScrollLimits() {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        if (this.mState.getItemCount() != 0) {
            if ((this.mFlag & 262144) == 0) {
                i4 = this.mGrid.getLastVisibleIndex();
                i3 = this.mState.getItemCount() - 1;
                i = this.mGrid.getFirstVisibleIndex();
                i2 = 0;
            } else {
                i4 = this.mGrid.getFirstVisibleIndex();
                i = this.mGrid.getLastVisibleIndex();
                i2 = this.mState.getItemCount() - 1;
                i3 = 0;
            }
            if (i4 >= 0 && i >= 0) {
                boolean z = i4 == i3;
                boolean z2 = i == i2;
                if (z || !this.mWindowAlignment.mainAxis().isMaxUnknown() || z2 || !this.mWindowAlignment.mainAxis().isMinUnknown()) {
                    int i7 = Integer.MAX_VALUE;
                    if (z) {
                        i7 = this.mGrid.findRowMax(true, sTwoInts);
                        View findViewByPosition = findViewByPosition(sTwoInts[1]);
                        i5 = getViewCenter(findViewByPosition);
                        int[] alignMultiple = ((LayoutParams) findViewByPosition.getLayoutParams()).getAlignMultiple();
                        if (alignMultiple != null && alignMultiple.length > 0) {
                            i5 += alignMultiple[alignMultiple.length - 1] - alignMultiple[0];
                        }
                    } else {
                        i5 = Integer.MAX_VALUE;
                    }
                    int i8 = Integer.MIN_VALUE;
                    if (z2) {
                        i8 = this.mGrid.findRowMin(false, sTwoInts);
                        i6 = getViewCenter(findViewByPosition(sTwoInts[1]));
                    } else {
                        i6 = Integer.MIN_VALUE;
                    }
                    this.mWindowAlignment.mainAxis().updateMinMax(i8, i7, i6, i5);
                }
            }
        }
    }

    private void updateSecondaryScrollLimits() {
        WindowAlignment.Axis secondAxis = this.mWindowAlignment.secondAxis();
        int paddingMin = secondAxis.getPaddingMin() - this.mScrollOffsetSecondary;
        int sizeSecondary = getSizeSecondary() + paddingMin;
        secondAxis.updateMinMax(paddingMin, sizeSecondary, paddingMin, sizeSecondary);
    }

    private void initScrollController() {
        this.mWindowAlignment.reset();
        this.mWindowAlignment.horizontal.setSize(getWidth());
        this.mWindowAlignment.vertical.setSize(getHeight());
        this.mWindowAlignment.horizontal.setPadding(getPaddingLeft(), getPaddingRight());
        this.mWindowAlignment.vertical.setPadding(getPaddingTop(), getPaddingBottom());
        this.mSizePrimary = this.mWindowAlignment.mainAxis().getSize();
        this.mScrollOffsetSecondary = 0;
    }

    private void updateScrollController() {
        this.mWindowAlignment.horizontal.setSize(getWidth());
        this.mWindowAlignment.vertical.setSize(getHeight());
        this.mWindowAlignment.horizontal.setPadding(getPaddingLeft(), getPaddingRight());
        this.mWindowAlignment.vertical.setPadding(getPaddingTop(), getPaddingBottom());
        this.mSizePrimary = this.mWindowAlignment.mainAxis().getSize();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void scrollToPosition(int i) {
        setSelection(i, 0, false, 0);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i) {
        setSelection(i, 0, true, 0);
    }

    public void setSelection(int i, int i2) {
        setSelection(i, 0, false, i2);
    }

    public void setSelectionSmooth(int i) {
        setSelection(i, 0, true, 0);
    }

    public void setSelectionWithSub(int i, int i2, int i3) {
        setSelection(i, i2, false, i3);
    }

    public int getSelection() {
        return this.mFocusPosition;
    }

    public int getSubSelection() {
        return this.mSubFocusPosition;
    }

    public void setSelection(int i, int i2, boolean z, int i3) {
        if ((this.mFocusPosition != i && i != -1) || i2 != this.mSubFocusPosition || i3 != this.mPrimaryScrollExtra) {
            scrollToSelection(i, i2, z, i3);
        }
    }

    /* access modifiers changed from: package-private */
    public void scrollToSelection(int i, int i2, boolean z, int i3) {
        this.mPrimaryScrollExtra = i3;
        View findViewByPosition = findViewByPosition(i);
        boolean z2 = !isSmoothScrolling();
        if (!z2 || this.mBaseGridView.isLayoutRequested() || findViewByPosition == null || getAdapterPositionByView(findViewByPosition) != i) {
            int i4 = this.mFlag;
            if ((i4 & 512) == 0 || (i4 & 64) != 0) {
                this.mFocusPosition = i;
                this.mSubFocusPosition = i2;
                this.mFocusPositionOffset = Integer.MIN_VALUE;
            } else if (!z || this.mBaseGridView.isLayoutRequested()) {
                if (!z2) {
                    skipSmoothScrollerOnStopInternal();
                    this.mBaseGridView.stopScroll();
                }
                if (this.mBaseGridView.isLayoutRequested() || findViewByPosition == null || getAdapterPositionByView(findViewByPosition) != i) {
                    this.mFocusPosition = i;
                    this.mSubFocusPosition = i2;
                    this.mFocusPositionOffset = Integer.MIN_VALUE;
                    this.mFlag |= 256;
                    requestLayout();
                    return;
                }
                this.mFlag |= 32;
                scrollToView(findViewByPosition, z);
                this.mFlag &= -33;
            } else {
                this.mFocusPosition = i;
                this.mSubFocusPosition = i2;
                this.mFocusPositionOffset = Integer.MIN_VALUE;
                if (!hasDoneFirstLayout()) {
                    Log.w(getTag(), "setSelectionSmooth should not be called before first layout pass");
                    return;
                }
                int startPositionSmoothScroller = startPositionSmoothScroller(i);
                if (startPositionSmoothScroller != this.mFocusPosition) {
                    this.mFocusPosition = startPositionSmoothScroller;
                    this.mSubFocusPosition = 0;
                }
            }
        } else {
            this.mFlag |= 32;
            scrollToView(findViewByPosition, z);
            this.mFlag &= -33;
        }
    }

    /* access modifiers changed from: package-private */
    public int startPositionSmoothScroller(int i) {
        AnonymousClass4 r0 = new GridLinearSmoothScroller() {
            /* class androidx.leanback.widget.GridLayoutManager.AnonymousClass4 */

            @Override // androidx.recyclerview.widget.RecyclerView.SmoothScroller
            public PointF computeScrollVectorForPosition(int i) {
                if (getChildCount() == 0) {
                    return null;
                }
                GridLayoutManager gridLayoutManager = GridLayoutManager.this;
                boolean z = false;
                int position = gridLayoutManager.getPosition(gridLayoutManager.getChildAt(0));
                int i2 = 1;
                if ((GridLayoutManager.this.mFlag & 262144) == 0 ? i < position : i > position) {
                    z = true;
                }
                if (z) {
                    i2 = -1;
                }
                if (GridLayoutManager.this.mOrientation == 0) {
                    return new PointF((float) i2, 0.0f);
                }
                return new PointF(0.0f, (float) i2);
            }
        };
        r0.setTargetPosition(i);
        startSmoothScroll(r0);
        return r0.getTargetPosition();
    }

    /* access modifiers changed from: package-private */
    public void skipSmoothScrollerOnStopInternal() {
        GridLinearSmoothScroller gridLinearSmoothScroller = this.mCurrentSmoothScroller;
        if (gridLinearSmoothScroller != null) {
            gridLinearSmoothScroller.mSkipOnStopInternal = true;
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void startSmoothScroll(RecyclerView.SmoothScroller smoothScroller) {
        skipSmoothScrollerOnStopInternal();
        super.startSmoothScroll(smoothScroller);
        if (!smoothScroller.isRunning() || !(smoothScroller instanceof GridLinearSmoothScroller)) {
            this.mCurrentSmoothScroller = null;
            this.mPendingMoveSmoothScroller = null;
            return;
        }
        GridLinearSmoothScroller gridLinearSmoothScroller = (GridLinearSmoothScroller) smoothScroller;
        this.mCurrentSmoothScroller = gridLinearSmoothScroller;
        if (gridLinearSmoothScroller instanceof PendingMoveSmoothScroller) {
            this.mPendingMoveSmoothScroller = (PendingMoveSmoothScroller) gridLinearSmoothScroller;
        } else {
            this.mPendingMoveSmoothScroller = null;
        }
    }

    /* access modifiers changed from: package-private */
    public void processPendingMovement(boolean z) {
        if (z) {
            if (hasCreatedLastItem()) {
                return;
            }
        } else if (hasCreatedFirstItem()) {
            return;
        }
        PendingMoveSmoothScroller pendingMoveSmoothScroller = this.mPendingMoveSmoothScroller;
        if (pendingMoveSmoothScroller == null) {
            boolean z2 = true;
            int i = z ? 1 : -1;
            if (this.mNumRows <= 1) {
                z2 = false;
            }
            PendingMoveSmoothScroller pendingMoveSmoothScroller2 = new PendingMoveSmoothScroller(i, z2);
            this.mFocusPositionOffset = 0;
            startSmoothScroll(pendingMoveSmoothScroller2);
        } else if (z) {
            pendingMoveSmoothScroller.increasePendingMoves();
        } else {
            pendingMoveSmoothScroller.decreasePendingMoves();
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onItemsAdded(RecyclerView recyclerView, int i, int i2) {
        Grid grid;
        int i3;
        if (!(this.mFocusPosition == -1 || (grid = this.mGrid) == null || grid.getFirstVisibleIndex() < 0 || (i3 = this.mFocusPositionOffset) == Integer.MIN_VALUE || i > this.mFocusPosition + i3)) {
            this.mFocusPositionOffset = i3 + i2;
        }
        this.mChildrenStates.clear();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onItemsChanged(RecyclerView recyclerView) {
        this.mFocusPositionOffset = 0;
        this.mChildrenStates.clear();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onItemsRemoved(RecyclerView recyclerView, int i, int i2) {
        Grid grid;
        int i3;
        int i4;
        int i5;
        if (!(this.mFocusPosition == -1 || (grid = this.mGrid) == null || grid.getFirstVisibleIndex() < 0 || (i3 = this.mFocusPositionOffset) == Integer.MIN_VALUE || i > (i5 = (i4 = this.mFocusPosition) + i3))) {
            if (i + i2 > i5) {
                int i6 = i3 + (i - i5);
                this.mFocusPositionOffset = i6;
                this.mFocusPosition = i4 + i6;
                this.mFocusPositionOffset = Integer.MIN_VALUE;
            } else {
                this.mFocusPositionOffset = i3 - i2;
            }
        }
        this.mChildrenStates.clear();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onItemsMoved(RecyclerView recyclerView, int i, int i2, int i3) {
        int i4;
        int i5 = this.mFocusPosition;
        if (!(i5 == -1 || (i4 = this.mFocusPositionOffset) == Integer.MIN_VALUE)) {
            int i6 = i5 + i4;
            if (i <= i6 && i6 < i + i3) {
                this.mFocusPositionOffset = i4 + (i2 - i);
            } else if (i < i6 && i2 > i6 - i3) {
                this.mFocusPositionOffset -= i3;
            } else if (i > i6 && i2 < i6) {
                this.mFocusPositionOffset += i3;
            }
        }
        this.mChildrenStates.clear();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onItemsUpdated(RecyclerView recyclerView, int i, int i2) {
        int i3 = i2 + i;
        while (i < i3) {
            this.mChildrenStates.remove(i);
            i++;
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public boolean onRequestChildFocus(RecyclerView recyclerView, View view, View view2) {
        if ((this.mFlag & 32768) == 0 && getAdapterPositionByView(view) != -1 && (this.mFlag & 35) == 0) {
            scrollToView(view, view2, true);
        }
        return true;
    }

    private int getPrimaryAlignedScrollDistance(View view) {
        return this.mWindowAlignment.mainAxis().getScroll(getViewCenter(view));
    }

    private int getAdjustedPrimaryAlignedScrollDistance(int i, View view, View view2) {
        int subPositionByView = getSubPositionByView(view, view2);
        if (subPositionByView == 0) {
            return i;
        }
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        return i + (layoutParams.getAlignMultiple()[subPositionByView] - layoutParams.getAlignMultiple()[0]);
    }

    private int getSecondaryScrollDistance(View view) {
        return this.mWindowAlignment.secondAxis().getScroll(getViewCenterSecondary(view));
    }

    /* access modifiers changed from: package-private */
    public void scrollToView(View view, boolean z) {
        scrollToView(view, view == null ? null : view.findFocus(), z);
    }

    /* access modifiers changed from: package-private */
    public void scrollToView(View view, boolean z, int i, int i2) {
        scrollToView(view, view == null ? null : view.findFocus(), z, i, i2);
    }

    private void scrollToView(View view, View view2, boolean z) {
        scrollToView(view, view2, z, 0, 0);
    }

    private void scrollToView(View view, View view2, boolean z, int i, int i2) {
        if ((this.mFlag & 64) == 0) {
            int adapterPositionByView = getAdapterPositionByView(view);
            int subPositionByView = getSubPositionByView(view, view2);
            if (!(adapterPositionByView == this.mFocusPosition && subPositionByView == this.mSubFocusPosition)) {
                this.mFocusPosition = adapterPositionByView;
                this.mSubFocusPosition = subPositionByView;
                this.mFocusPositionOffset = 0;
                if ((this.mFlag & 3) != 1) {
                    dispatchChildSelected();
                }
                if (this.mBaseGridView.isChildrenDrawingOrderEnabledInternal()) {
                    this.mBaseGridView.invalidate();
                }
            }
            if (view != null) {
                if (!view.hasFocus() && this.mBaseGridView.hasFocus()) {
                    view.requestFocus();
                }
                if ((this.mFlag & 131072) == 0 && z) {
                    return;
                }
                if (getScrollPosition(view, view2, sTwoInts) || i != 0 || i2 != 0) {
                    int[] iArr = sTwoInts;
                    scrollGrid(iArr[0] + i, iArr[1] + i2, z);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean getScrollPosition(View view, View view2, int[] iArr) {
        int i = this.mFocusScrollStrategy;
        if (i == 1 || i == 2) {
            return getNoneAlignedPosition(view, iArr);
        }
        return getAlignedPosition(view, view2, iArr);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:26:0x009f, code lost:
        if (r2 != null) goto L_0x00a5;
     */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00a7  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00ad  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00b8  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00ba  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00c3 A[ADDED_TO_REGION] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean getNoneAlignedPosition(android.view.View r13, int[] r14) {
        /*
        // Method dump skipped, instructions count: 204
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.leanback.widget.GridLayoutManager.getNoneAlignedPosition(android.view.View, int[]):boolean");
    }

    private boolean getAlignedPosition(View view, View view2, int[] iArr) {
        int primaryAlignedScrollDistance = getPrimaryAlignedScrollDistance(view);
        if (view2 != null) {
            primaryAlignedScrollDistance = getAdjustedPrimaryAlignedScrollDistance(primaryAlignedScrollDistance, view, view2);
        }
        int secondaryScrollDistance = getSecondaryScrollDistance(view);
        int i = primaryAlignedScrollDistance + this.mPrimaryScrollExtra;
        if (i == 0 && secondaryScrollDistance == 0) {
            iArr[0] = 0;
            iArr[1] = 0;
            return false;
        }
        iArr[0] = i;
        iArr[1] = secondaryScrollDistance;
        return true;
    }

    private void scrollGrid(int i, int i2, boolean z) {
        if ((this.mFlag & 3) == 1) {
            scrollDirectionPrimary(i);
            scrollDirectionSecondary(i2);
            return;
        }
        if (this.mOrientation != 0) {
            i2 = i;
            i = i2;
        }
        if (z) {
            this.mBaseGridView.smoothScrollBy(i, i2);
            return;
        }
        this.mBaseGridView.scrollBy(i, i2);
        dispatchChildSelectedAndPositioned();
    }

    public void setPruneChild(boolean z) {
        int i = 65536;
        if (((this.mFlag & 65536) != 0) != z) {
            int i2 = this.mFlag & -65537;
            if (!z) {
                i = 0;
            }
            this.mFlag = i2 | i;
            if (z) {
                requestLayout();
            }
        }
    }

    public void setScrollEnabled(boolean z) {
        int i;
        int i2 = 0;
        if (((this.mFlag & 131072) != 0) != z) {
            int i3 = this.mFlag & -131073;
            if (z) {
                i2 = 131072;
            }
            int i4 = i3 | i2;
            this.mFlag = i4;
            if ((i4 & 131072) != 0 && this.mFocusScrollStrategy == 0 && (i = this.mFocusPosition) != -1) {
                scrollToSelection(i, this.mSubFocusPosition, true, this.mPrimaryScrollExtra);
            }
        }
    }

    public boolean isScrollEnabled() {
        return (this.mFlag & 131072) != 0;
    }

    private int findImmediateChildIndex(View view) {
        View findContainingItemView;
        BaseGridView baseGridView = this.mBaseGridView;
        if (baseGridView == null || view == baseGridView || (findContainingItemView = findContainingItemView(view)) == null) {
            return -1;
        }
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (getChildAt(i) == findContainingItemView) {
                return i;
            }
        }
        return -1;
    }

    /* access modifiers changed from: package-private */
    public void onFocusChanged(boolean z, int i, Rect rect) {
        if (z) {
            int i2 = this.mFocusPosition;
            while (true) {
                View findViewByPosition = findViewByPosition(i2);
                if (findViewByPosition != null) {
                    if (findViewByPosition.getVisibility() != 0 || !findViewByPosition.hasFocusable()) {
                        i2++;
                    } else {
                        findViewByPosition.requestFocus();
                        return;
                    }
                } else {
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setFocusSearchDisabled(boolean z) {
        this.mFlag = (z ? 32768 : 0) | (this.mFlag & -32769);
    }

    /* JADX WARNING: Removed duplicated region for block: B:70:0x00ca A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x00cb  */
    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View onInterceptFocusSearch(android.view.View r8, int r9) {
        /*
        // Method dump skipped, instructions count: 222
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.leanback.widget.GridLayoutManager.onInterceptFocusSearch(android.view.View, int):android.view.View");
    }

    /* access modifiers changed from: package-private */
    public boolean hasPreviousViewInSameRow(int i) {
        Grid grid = this.mGrid;
        if (!(grid == null || i == -1 || grid.getFirstVisibleIndex() < 0)) {
            if (this.mGrid.getFirstVisibleIndex() > 0) {
                return true;
            }
            int i2 = this.mGrid.getLocation(i).row;
            for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
                int adapterPositionByIndex = getAdapterPositionByIndex(childCount);
                Grid.Location location = this.mGrid.getLocation(adapterPositionByIndex);
                if (location != null && location.row == i2 && adapterPositionByIndex < i) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public boolean onAddFocusables(RecyclerView recyclerView, ArrayList<View> arrayList, int i, int i2) {
        View view;
        int i3;
        int i4;
        if ((this.mFlag & 32768) != 0) {
            return true;
        }
        if (!recyclerView.hasFocus()) {
            int size = arrayList.size();
            if (this.mFocusScrollStrategy != 0) {
                int paddingMin = this.mWindowAlignment.mainAxis().getPaddingMin();
                int clientSize = this.mWindowAlignment.mainAxis().getClientSize() + paddingMin;
                int childCount = getChildCount();
                for (int i5 = 0; i5 < childCount; i5++) {
                    View childAt = getChildAt(i5);
                    if (childAt.getVisibility() == 0 && getViewMin(childAt) >= paddingMin && getViewMax(childAt) <= clientSize) {
                        childAt.addFocusables(arrayList, i, i2);
                    }
                }
                if (arrayList.size() == size) {
                    int childCount2 = getChildCount();
                    for (int i6 = 0; i6 < childCount2; i6++) {
                        View childAt2 = getChildAt(i6);
                        if (childAt2.getVisibility() == 0) {
                            childAt2.addFocusables(arrayList, i, i2);
                        }
                    }
                }
            } else {
                View findViewByPosition = findViewByPosition(this.mFocusPosition);
                if (findViewByPosition != null) {
                    findViewByPosition.addFocusables(arrayList, i, i2);
                }
            }
            if (arrayList.size() == size && recyclerView.isFocusable()) {
                arrayList.add(recyclerView);
            }
        } else if (this.mPendingMoveSmoothScroller != null) {
            return true;
        } else {
            int movement = getMovement(i);
            int findImmediateChildIndex = findImmediateChildIndex(recyclerView.findFocus());
            int adapterPositionByIndex = getAdapterPositionByIndex(findImmediateChildIndex);
            if (adapterPositionByIndex == -1) {
                view = null;
            } else {
                view = findViewByPosition(adapterPositionByIndex);
            }
            if (view != null) {
                view.addFocusables(arrayList, i, i2);
            }
            if (this.mGrid == null || getChildCount() == 0) {
                return true;
            }
            if ((movement == 3 || movement == 2) && this.mGrid.getNumRows() <= 1) {
                return true;
            }
            Grid grid = this.mGrid;
            int i7 = (grid == null || view == null) ? -1 : grid.getLocation(adapterPositionByIndex).row;
            int size2 = arrayList.size();
            int i8 = (movement == 1 || movement == 3) ? 1 : -1;
            int childCount3 = i8 > 0 ? getChildCount() - 1 : 0;
            if (findImmediateChildIndex == -1) {
                i3 = i8 > 0 ? 0 : getChildCount() - 1;
            } else {
                i3 = findImmediateChildIndex + i8;
            }
            int i9 = i3;
            while (true) {
                if (i8 > 0) {
                    if (i9 > childCount3) {
                        break;
                    }
                } else if (i9 < childCount3) {
                    break;
                }
                View childAt3 = getChildAt(i9);
                if (childAt3.getVisibility() == 0 && childAt3.hasFocusable()) {
                    if (view == null) {
                        childAt3.addFocusables(arrayList, i, i2);
                        if (arrayList.size() > size2) {
                            break;
                        }
                    } else {
                        int adapterPositionByIndex2 = getAdapterPositionByIndex(i9);
                        Grid.Location location = this.mGrid.getLocation(adapterPositionByIndex2);
                        if (location != null) {
                            if (movement == 1) {
                                if (location.row == i7 && adapterPositionByIndex2 > adapterPositionByIndex) {
                                    childAt3.addFocusables(arrayList, i, i2);
                                    if (arrayList.size() > size2) {
                                        break;
                                    }
                                }
                            } else if (movement == 0) {
                                if (location.row == i7 && adapterPositionByIndex2 < adapterPositionByIndex) {
                                    childAt3.addFocusables(arrayList, i, i2);
                                    if (arrayList.size() > size2) {
                                        break;
                                    }
                                }
                            } else if (movement == 3) {
                                int i10 = location.row;
                                if (i10 != i7) {
                                    if (i10 < i7) {
                                        break;
                                    }
                                    childAt3.addFocusables(arrayList, i, i2);
                                }
                            } else if (movement == 2 && (i4 = location.row) != i7) {
                                if (i4 > i7) {
                                    break;
                                }
                                childAt3.addFocusables(arrayList, i, i2);
                            }
                        }
                    }
                }
                i9 += i8;
            }
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean hasCreatedLastItem() {
        int itemCount = getItemCount();
        if (itemCount == 0 || this.mBaseGridView.findViewHolderForAdapterPosition(itemCount - 1) != null) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean hasCreatedFirstItem() {
        if (getItemCount() == 0 || this.mBaseGridView.findViewHolderForAdapterPosition(0) != null) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean isItemFullyVisible(int i) {
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.mBaseGridView.findViewHolderForAdapterPosition(i);
        if (findViewHolderForAdapterPosition != null && findViewHolderForAdapterPosition.itemView.getLeft() >= 0 && findViewHolderForAdapterPosition.itemView.getRight() <= this.mBaseGridView.getWidth() && findViewHolderForAdapterPosition.itemView.getTop() >= 0 && findViewHolderForAdapterPosition.itemView.getBottom() <= this.mBaseGridView.getHeight()) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean canScrollTo(View view) {
        return view.getVisibility() == 0 && (!hasFocus() || view.hasFocusable());
    }

    /* access modifiers changed from: package-private */
    public boolean gridOnRequestFocusInDescendants(RecyclerView recyclerView, int i, Rect rect) {
        int i2 = this.mFocusScrollStrategy;
        if (i2 == 1 || i2 == 2) {
            return gridOnRequestFocusInDescendantsUnaligned(i, rect);
        }
        return gridOnRequestFocusInDescendantsAligned(i, rect);
    }

    private boolean gridOnRequestFocusInDescendantsAligned(int i, Rect rect) {
        View findViewByPosition = findViewByPosition(this.mFocusPosition);
        if (findViewByPosition != null) {
            return findViewByPosition.requestFocus(i, rect);
        }
        return false;
    }

    private boolean gridOnRequestFocusInDescendantsUnaligned(int i, Rect rect) {
        int i2;
        int i3;
        int childCount = getChildCount();
        int i4 = -1;
        if ((i & 2) != 0) {
            i4 = childCount;
            i3 = 0;
            i2 = 1;
        } else {
            i3 = childCount - 1;
            i2 = -1;
        }
        int paddingMin = this.mWindowAlignment.mainAxis().getPaddingMin();
        int clientSize = this.mWindowAlignment.mainAxis().getClientSize() + paddingMin;
        while (i3 != i4) {
            View childAt = getChildAt(i3);
            if (childAt.getVisibility() == 0 && getViewMin(childAt) >= paddingMin && getViewMax(childAt) <= clientSize && childAt.requestFocus(i, rect)) {
                return true;
            }
            i3 += i2;
        }
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0035, code lost:
        if (r10 != 130) goto L_0x0046;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x003d, code lost:
        if ((r9.mFlag & 524288) == 0) goto L_0x001b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0043, code lost:
        if ((r9.mFlag & 524288) == 0) goto L_0x0023;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0018, code lost:
        if (r10 != 130) goto L_0x0046;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int getMovement(int r10) {
        /*
            r9 = this;
            int r0 = r9.mOrientation
            r1 = 130(0x82, float:1.82E-43)
            r2 = 66
            r3 = 33
            r4 = 0
            r5 = 3
            r6 = 2
            r7 = 17
            r8 = 1
            if (r0 != 0) goto L_0x002b
            r0 = 262144(0x40000, float:3.67342E-40)
            if (r10 == r7) goto L_0x0025
            if (r10 == r3) goto L_0x0023
            if (r10 == r2) goto L_0x001d
            if (r10 == r1) goto L_0x001b
            goto L_0x0046
        L_0x001b:
            r4 = r5
            goto L_0x0047
        L_0x001d:
            int r9 = r9.mFlag
            r9 = r9 & r0
            if (r9 != 0) goto L_0x0047
            goto L_0x0038
        L_0x0023:
            r4 = r6
            goto L_0x0047
        L_0x0025:
            int r9 = r9.mFlag
            r9 = r9 & r0
            if (r9 != 0) goto L_0x0038
            goto L_0x0047
        L_0x002b:
            if (r0 != r8) goto L_0x0046
            r0 = 524288(0x80000, float:7.34684E-40)
            if (r10 == r7) goto L_0x0040
            if (r10 == r3) goto L_0x0047
            if (r10 == r2) goto L_0x003a
            if (r10 == r1) goto L_0x0038
            goto L_0x0046
        L_0x0038:
            r4 = r8
            goto L_0x0047
        L_0x003a:
            int r9 = r9.mFlag
            r9 = r9 & r0
            if (r9 != 0) goto L_0x0023
            goto L_0x001b
        L_0x0040:
            int r9 = r9.mFlag
            r9 = r9 & r0
            if (r9 != 0) goto L_0x001b
            goto L_0x0023
        L_0x0046:
            r4 = r7
        L_0x0047:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.leanback.widget.GridLayoutManager.getMovement(int):int");
    }

    /* access modifiers changed from: package-private */
    public int getChildDrawingOrder(RecyclerView recyclerView, int i, int i2) {
        int indexOfChild;
        View findViewByPosition = findViewByPosition(this.mFocusPosition);
        if (findViewByPosition != null && i2 >= (indexOfChild = recyclerView.indexOfChild(findViewByPosition))) {
            return i2 < i + -1 ? ((indexOfChild + i) - 1) - i2 : indexOfChild;
        }
        return i2;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onAdapterChanged(RecyclerView.Adapter adapter, RecyclerView.Adapter adapter2) {
        if (adapter != null) {
            discardLayoutInfo();
            this.mFocusPosition = -1;
            this.mFocusPositionOffset = 0;
            this.mChildrenStates.clear();
        }
        if (adapter2 instanceof FacetProviderAdapter) {
            this.mFacetProviderAdapter = (FacetProviderAdapter) adapter2;
        } else {
            this.mFacetProviderAdapter = null;
        }
        super.onAdapterChanged(adapter, adapter2);
    }

    private void discardLayoutInfo() {
        this.mGrid = null;
        this.mRowSizeSecondary = null;
        this.mFlag &= -1025;
    }

    public void setLayoutEnabled(boolean z) {
        int i = 512;
        if (((this.mFlag & 512) != 0) != z) {
            int i2 = this.mFlag & -513;
            if (!z) {
                i = 0;
            }
            this.mFlag = i2 | i;
            requestLayout();
        }
    }

    /* access modifiers changed from: package-private */
    public void setChildrenVisibility(int i) {
        this.mChildVisibility = i;
        if (i != -1) {
            int childCount = getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                getChildAt(i2).setVisibility(this.mChildVisibility);
            }
        }
    }

    /* access modifiers changed from: package-private */
    @SuppressLint({"BanParcelableUsage"})
    public static final class SavedState implements Parcelable {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            /* class androidx.leanback.widget.GridLayoutManager.SavedState.AnonymousClass1 */

            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        Bundle childStates = Bundle.EMPTY;
        int index;

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.index);
            parcel.writeBundle(this.childStates);
        }

        SavedState(Parcel parcel) {
            this.index = parcel.readInt();
            this.childStates = parcel.readBundle(GridLayoutManager.class.getClassLoader());
        }

        SavedState() {
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState();
        savedState.index = getSelection();
        Bundle saveAsBundle = this.mChildrenStates.saveAsBundle();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            int adapterPositionByView = getAdapterPositionByView(childAt);
            if (adapterPositionByView != -1) {
                saveAsBundle = this.mChildrenStates.saveOnScreenView(saveAsBundle, childAt, adapterPositionByView);
            }
        }
        savedState.childStates = saveAsBundle;
        return savedState;
    }

    /* access modifiers changed from: package-private */
    public void onChildRecycled(RecyclerView.ViewHolder viewHolder) {
        int absoluteAdapterPosition = viewHolder.getAbsoluteAdapterPosition();
        if (absoluteAdapterPosition != -1) {
            this.mChildrenStates.saveOffscreenView(viewHolder.itemView, absoluteAdapterPosition);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable instanceof SavedState) {
            SavedState savedState = (SavedState) parcelable;
            this.mFocusPosition = savedState.index;
            this.mFocusPositionOffset = 0;
            this.mChildrenStates.loadFromBundle(savedState.childStates);
            this.mFlag |= 256;
            requestLayout();
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public int getRowCountForAccessibility(RecyclerView.Recycler recycler, RecyclerView.State state) {
        Grid grid;
        if (this.mOrientation != 0 || (grid = this.mGrid) == null) {
            return super.getRowCountForAccessibility(recycler, state);
        }
        return grid.getNumRows();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public int getColumnCountForAccessibility(RecyclerView.Recycler recycler, RecyclerView.State state) {
        Grid grid;
        if (this.mOrientation != 1 || (grid = this.mGrid) == null) {
            return super.getColumnCountForAccessibility(recycler, state);
        }
        return grid.getNumRows();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onInitializeAccessibilityNodeInfoForItem(RecyclerView.Recycler recycler, RecyclerView.State state, View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (this.mGrid != null && (layoutParams instanceof LayoutParams)) {
            int viewAdapterPosition = ((LayoutParams) layoutParams).getViewAdapterPosition();
            int rowIndex = viewAdapterPosition >= 0 ? this.mGrid.getRowIndex(viewAdapterPosition) : -1;
            if (rowIndex >= 0) {
                int numRows = viewAdapterPosition / this.mGrid.getNumRows();
                if (this.mOrientation == 0) {
                    accessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(rowIndex, 1, numRows, 1, false, false));
                } else {
                    accessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(numRows, 1, rowIndex, 1, false, false));
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x002c, code lost:
        if (r5 != false) goto L_0x002e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x003a, code lost:
        if (r5 != false) goto L_0x0030;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x004c, code lost:
        if (r7 == androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_DOWN.getId()) goto L_0x002e;
     */
    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean performAccessibilityAction(androidx.recyclerview.widget.RecyclerView.Recycler r5, androidx.recyclerview.widget.RecyclerView.State r6, int r7, android.os.Bundle r8) {
        /*
        // Method dump skipped, instructions count: 102
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.leanback.widget.GridLayoutManager.performAccessibilityAction(androidx.recyclerview.widget.RecyclerView$Recycler, androidx.recyclerview.widget.RecyclerView$State, int, android.os.Bundle):boolean");
    }

    /* access modifiers changed from: package-private */
    public int processSelectionMoves(boolean z, int i) {
        Grid grid = this.mGrid;
        if (grid == null) {
            return i;
        }
        int i2 = this.mFocusPosition;
        int rowIndex = i2 != -1 ? grid.getRowIndex(i2) : -1;
        View view = null;
        int childCount = getChildCount();
        for (int i3 = 0; i3 < childCount && i != 0; i3++) {
            int i4 = i > 0 ? i3 : (childCount - 1) - i3;
            View childAt = getChildAt(i4);
            if (canScrollTo(childAt)) {
                int adapterPositionByIndex = getAdapterPositionByIndex(i4);
                int rowIndex2 = this.mGrid.getRowIndex(adapterPositionByIndex);
                if (rowIndex == -1) {
                    i2 = adapterPositionByIndex;
                    view = childAt;
                    rowIndex = rowIndex2;
                } else if (rowIndex2 == rowIndex && ((i > 0 && adapterPositionByIndex > i2) || (i < 0 && adapterPositionByIndex < i2))) {
                    i = i > 0 ? i - 1 : i + 1;
                    i2 = adapterPositionByIndex;
                    view = childAt;
                }
            }
        }
        if (view != null) {
            if (z) {
                if (hasFocus()) {
                    this.mFlag |= 32;
                    view.requestFocus();
                    this.mFlag &= -33;
                }
                this.mFocusPosition = i2;
                this.mSubFocusPosition = 0;
            } else {
                scrollToView(view, true);
            }
        }
        return i;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onInitializeAccessibilityNodeInfo(RecyclerView.Recycler recycler, RecyclerView.State state, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
        AccessibilityNodeInfoCompat.AccessibilityActionCompat accessibilityActionCompat;
        AccessibilityNodeInfoCompat.AccessibilityActionCompat accessibilityActionCompat2;
        int i = Build.VERSION.SDK_INT;
        saveContext(recycler, state);
        int itemCount = state.getItemCount();
        boolean z = (this.mFlag & 262144) != 0;
        if (itemCount > 1 && !isItemFullyVisible(0)) {
            if (i < 23) {
                accessibilityNodeInfoCompat.addAction(8192);
            } else if (this.mOrientation == 0) {
                if (z) {
                    accessibilityActionCompat2 = AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_RIGHT;
                } else {
                    accessibilityActionCompat2 = AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_LEFT;
                }
                accessibilityNodeInfoCompat.addAction(accessibilityActionCompat2);
            } else {
                accessibilityNodeInfoCompat.addAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_UP);
            }
            accessibilityNodeInfoCompat.setScrollable(true);
        }
        if (itemCount > 1 && !isItemFullyVisible(itemCount - 1)) {
            if (i < 23) {
                accessibilityNodeInfoCompat.addAction(4096);
            } else if (this.mOrientation == 0) {
                if (z) {
                    accessibilityActionCompat = AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_LEFT;
                } else {
                    accessibilityActionCompat = AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_RIGHT;
                }
                accessibilityNodeInfoCompat.addAction(accessibilityActionCompat);
            } else {
                accessibilityNodeInfoCompat.addAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_DOWN);
            }
            accessibilityNodeInfoCompat.setScrollable(true);
        }
        accessibilityNodeInfoCompat.setCollectionInfo(AccessibilityNodeInfoCompat.CollectionInfoCompat.obtain(getRowCountForAccessibility(recycler, state), getColumnCountForAccessibility(recycler, state), isLayoutHierarchical(recycler, state), getSelectionModeForAccessibility(recycler, state)));
        leaveContext();
    }
}
