package androidx.leanback.widget;

import androidx.collection.CircularIntArray;
import androidx.leanback.widget.Grid;
import androidx.recyclerview.widget.RecyclerView;

/* access modifiers changed from: package-private */
public class SingleRow extends Grid {
    private final Grid.Location mTmpLocation = new Grid.Location(0);

    SingleRow() {
        setNumRows(1);
    }

    @Override // androidx.leanback.widget.Grid
    public final Grid.Location getLocation(int i) {
        return this.mTmpLocation;
    }

    /* access modifiers changed from: package-private */
    public int getStartIndexForAppend() {
        int i = this.mLastVisibleIndex;
        if (i >= 0) {
            return i + 1;
        }
        int i2 = this.mStartIndex;
        if (i2 != -1) {
            return Math.min(i2, this.mProvider.getCount() - 1);
        }
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int getStartIndexForPrepend() {
        int i = this.mFirstVisibleIndex;
        if (i >= 0) {
            return i - 1;
        }
        int i2 = this.mStartIndex;
        if (i2 != -1) {
            return Math.min(i2, this.mProvider.getCount() - 1);
        }
        return this.mProvider.getCount() - 1;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.leanback.widget.Grid
    public final boolean prependVisibleItems(int i, boolean z) {
        int i2;
        if (this.mProvider.getCount() == 0) {
            return false;
        }
        if (!z && checkPrependOverLimit(i)) {
            return false;
        }
        int minIndex = this.mProvider.getMinIndex();
        int startIndexForPrepend = getStartIndexForPrepend();
        boolean z2 = false;
        while (startIndexForPrepend >= minIndex) {
            int createItem = this.mProvider.createItem(startIndexForPrepend, false, this.mTmpItem, false);
            if (this.mFirstVisibleIndex < 0 || this.mLastVisibleIndex < 0) {
                i2 = this.mReversedFlow ? Integer.MIN_VALUE : Integer.MAX_VALUE;
                this.mFirstVisibleIndex = startIndexForPrepend;
                this.mLastVisibleIndex = startIndexForPrepend;
            } else {
                if (this.mReversedFlow) {
                    i2 = this.mProvider.getEdge(startIndexForPrepend + 1) + this.mSpacing + createItem;
                } else {
                    i2 = (this.mProvider.getEdge(startIndexForPrepend + 1) - this.mSpacing) - createItem;
                }
                this.mFirstVisibleIndex = startIndexForPrepend;
            }
            this.mProvider.addItem(this.mTmpItem[0], startIndexForPrepend, createItem, 0, i2);
            if (z || checkPrependOverLimit(i)) {
                return true;
            }
            startIndexForPrepend--;
            z2 = true;
        }
        return z2;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.leanback.widget.Grid
    public final boolean appendVisibleItems(int i, boolean z) {
        int i2;
        if (this.mProvider.getCount() == 0) {
            return false;
        }
        if (!z && checkAppendOverLimit(i)) {
            return false;
        }
        int startIndexForAppend = getStartIndexForAppend();
        boolean z2 = false;
        while (startIndexForAppend < this.mProvider.getCount()) {
            int createItem = this.mProvider.createItem(startIndexForAppend, true, this.mTmpItem, false);
            if (this.mFirstVisibleIndex < 0 || this.mLastVisibleIndex < 0) {
                i2 = this.mReversedFlow ? Integer.MAX_VALUE : Integer.MIN_VALUE;
                this.mFirstVisibleIndex = startIndexForAppend;
                this.mLastVisibleIndex = startIndexForAppend;
            } else {
                if (this.mReversedFlow) {
                    int i3 = startIndexForAppend - 1;
                    i2 = (this.mProvider.getEdge(i3) - this.mProvider.getSize(i3)) - this.mSpacing;
                } else {
                    int i4 = startIndexForAppend - 1;
                    i2 = this.mProvider.getEdge(i4) + this.mProvider.getSize(i4) + this.mSpacing;
                }
                this.mLastVisibleIndex = startIndexForAppend;
            }
            this.mProvider.addItem(this.mTmpItem[0], startIndexForAppend, createItem, 0, i2);
            if (z || checkAppendOverLimit(i)) {
                return true;
            }
            startIndexForAppend++;
            z2 = true;
        }
        return z2;
    }

    @Override // androidx.leanback.widget.Grid
    public void collectAdjacentPrefetchPositions(int i, int i2, RecyclerView.LayoutManager.LayoutPrefetchRegistry layoutPrefetchRegistry) {
        int i3;
        int i4;
        if (!this.mReversedFlow ? i2 >= 0 : i2 <= 0) {
            if (getLastVisibleIndex() != this.mProvider.getCount() - 1) {
                i3 = getStartIndexForAppend();
                int size = this.mProvider.getSize(this.mLastVisibleIndex) + this.mSpacing;
                int edge = this.mProvider.getEdge(this.mLastVisibleIndex);
                if (this.mReversedFlow) {
                    size = -size;
                }
                i4 = size + edge;
            } else {
                return;
            }
        } else if (getFirstVisibleIndex() != 0) {
            i3 = getStartIndexForPrepend();
            int edge2 = this.mProvider.getEdge(this.mFirstVisibleIndex);
            boolean z = this.mReversedFlow;
            int i5 = this.mSpacing;
            if (!z) {
                i5 = -i5;
            }
            i4 = edge2 + i5;
        } else {
            return;
        }
        layoutPrefetchRegistry.addPosition(i3, Math.abs(i4 - i));
    }

    @Override // androidx.leanback.widget.Grid
    public final CircularIntArray[] getItemPositionsInRows(int i, int i2) {
        this.mTmpItemPositionsInRows[0].clear();
        this.mTmpItemPositionsInRows[0].addLast(i);
        this.mTmpItemPositionsInRows[0].addLast(i2);
        return this.mTmpItemPositionsInRows;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.leanback.widget.Grid
    public final int findRowMin(boolean z, int i, int[] iArr) {
        if (iArr != null) {
            iArr[0] = 0;
            iArr[1] = i;
        }
        if (this.mReversedFlow) {
            return this.mProvider.getEdge(i) - this.mProvider.getSize(i);
        }
        return this.mProvider.getEdge(i);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.leanback.widget.Grid
    public final int findRowMax(boolean z, int i, int[] iArr) {
        if (iArr != null) {
            iArr[0] = 0;
            iArr[1] = i;
        }
        if (this.mReversedFlow) {
            return this.mProvider.getEdge(i);
        }
        return this.mProvider.getSize(i) + this.mProvider.getEdge(i);
    }
}
