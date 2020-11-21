package androidx.leanback.widget;

import androidx.collection.CircularArray;
import androidx.collection.CircularIntArray;
import androidx.leanback.widget.Grid;

/* access modifiers changed from: package-private */
public abstract class StaggeredGrid extends Grid {
    protected int mFirstIndex = -1;
    protected CircularArray<Location> mLocations = new CircularArray<>(64);
    protected Object mPendingItem;
    protected int mPendingItemSize;

    /* access modifiers changed from: protected */
    public abstract boolean appendVisibleItemsWithoutCache(int i, boolean z);

    /* access modifiers changed from: protected */
    public abstract boolean prependVisibleItemsWithoutCache(int i, boolean z);

    StaggeredGrid() {
    }

    public static class Location extends Grid.Location {
        public int offset;
        public int size;

        public Location(int i, int i2, int i3) {
            super(i);
            this.offset = i2;
            this.size = i3;
        }
    }

    public final int getFirstIndex() {
        return this.mFirstIndex;
    }

    public final int getLastIndex() {
        return (this.mFirstIndex + this.mLocations.size()) - 1;
    }

    @Override // androidx.leanback.widget.Grid
    public final Location getLocation(int i) {
        int i2 = i - this.mFirstIndex;
        if (i2 < 0 || i2 >= this.mLocations.size()) {
            return null;
        }
        return this.mLocations.get(i2);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.leanback.widget.Grid
    public final boolean prependVisibleItems(int i, boolean z) {
        boolean prependVisibleItemsWithoutCache;
        if (this.mProvider.getCount() == 0) {
            return false;
        }
        if (!z && checkPrependOverLimit(i)) {
            return false;
        }
        try {
            if (prependVisbleItemsWithCache(i, z)) {
                prependVisibleItemsWithoutCache = true;
                this.mTmpItem[0] = null;
            } else {
                prependVisibleItemsWithoutCache = prependVisibleItemsWithoutCache(i, z);
                this.mTmpItem[0] = null;
            }
            this.mPendingItem = null;
            return prependVisibleItemsWithoutCache;
        } catch (Throwable th) {
            this.mTmpItem[0] = null;
            this.mPendingItem = null;
            throw th;
        }
    }

    /* access modifiers changed from: protected */
    public final boolean prependVisbleItemsWithCache(int i, boolean z) {
        int i2;
        int i3;
        int i4;
        if (this.mLocations.size() == 0) {
            return false;
        }
        int i5 = this.mFirstVisibleIndex;
        if (i5 >= 0) {
            i4 = this.mProvider.getEdge(i5);
            i3 = getLocation(this.mFirstVisibleIndex).offset;
            i2 = this.mFirstVisibleIndex - 1;
        } else {
            i4 = Integer.MAX_VALUE;
            int i6 = this.mStartIndex;
            i2 = i6 != -1 ? i6 : 0;
            if (i2 > getLastIndex() || i2 < getFirstIndex() - 1) {
                this.mLocations.clear();
                return false;
            } else if (i2 < getFirstIndex()) {
                return false;
            } else {
                i3 = 0;
            }
        }
        int max = Math.max(this.mProvider.getMinIndex(), this.mFirstIndex);
        while (i2 >= max) {
            Location location = getLocation(i2);
            int i7 = location.row;
            int createItem = this.mProvider.createItem(i2, false, this.mTmpItem, false);
            if (createItem != location.size) {
                this.mLocations.removeFromStart((i2 + 1) - this.mFirstIndex);
                this.mFirstIndex = this.mFirstVisibleIndex;
                this.mPendingItem = this.mTmpItem[0];
                this.mPendingItemSize = createItem;
                return false;
            }
            this.mFirstVisibleIndex = i2;
            if (this.mLastVisibleIndex < 0) {
                this.mLastVisibleIndex = i2;
            }
            this.mProvider.addItem(this.mTmpItem[0], i2, createItem, i7, i4 - i3);
            if (!z && checkPrependOverLimit(i)) {
                return true;
            }
            i4 = this.mProvider.getEdge(i2);
            i3 = location.offset;
            if (i7 == 0 && z) {
                return true;
            }
            i2--;
        }
        return false;
    }

    private int calculateOffsetAfterLastItem(int i) {
        boolean z;
        int i2;
        int lastIndex = getLastIndex();
        while (true) {
            if (lastIndex < this.mFirstIndex) {
                z = false;
                break;
            } else if (getLocation(lastIndex).row == i) {
                z = true;
                break;
            } else {
                lastIndex--;
            }
        }
        if (!z) {
            lastIndex = getLastIndex();
        }
        if (isReversedFlow()) {
            i2 = (-getLocation(lastIndex).size) - this.mSpacing;
        } else {
            i2 = getLocation(lastIndex).size + this.mSpacing;
        }
        for (int i3 = lastIndex + 1; i3 <= getLastIndex(); i3++) {
            i2 -= getLocation(i3).offset;
        }
        return i2;
    }

    /* access modifiers changed from: protected */
    public final int prependVisibleItemToRow(int i, int i2, int i3) {
        int i4 = this.mFirstVisibleIndex;
        if (i4 < 0 || (i4 == getFirstIndex() && this.mFirstVisibleIndex == i + 1)) {
            int i5 = this.mFirstIndex;
            Location location = i5 >= 0 ? getLocation(i5) : null;
            int edge = this.mProvider.getEdge(this.mFirstIndex);
            Location location2 = new Location(i2, 0, 0);
            this.mLocations.addFirst(location2);
            Object obj = this.mPendingItem;
            if (obj != null) {
                location2.size = this.mPendingItemSize;
                this.mPendingItem = null;
            } else {
                location2.size = this.mProvider.createItem(i, false, this.mTmpItem, false);
                obj = this.mTmpItem[0];
            }
            this.mFirstVisibleIndex = i;
            this.mFirstIndex = i;
            if (this.mLastVisibleIndex < 0) {
                this.mLastVisibleIndex = i;
            }
            int i6 = !this.mReversedFlow ? i3 - location2.size : i3 + location2.size;
            if (location != null) {
                location.offset = edge - i6;
            }
            this.mProvider.addItem(obj, i, location2.size, i2, i6);
            return location2.size;
        }
        throw new IllegalStateException();
    }

    /* access modifiers changed from: protected */
    @Override // androidx.leanback.widget.Grid
    public final boolean appendVisibleItems(int i, boolean z) {
        boolean appendVisibleItemsWithoutCache;
        if (this.mProvider.getCount() == 0) {
            return false;
        }
        if (!z && checkAppendOverLimit(i)) {
            return false;
        }
        try {
            if (appendVisbleItemsWithCache(i, z)) {
                appendVisibleItemsWithoutCache = true;
                this.mTmpItem[0] = null;
            } else {
                appendVisibleItemsWithoutCache = appendVisibleItemsWithoutCache(i, z);
                this.mTmpItem[0] = null;
            }
            this.mPendingItem = null;
            return appendVisibleItemsWithoutCache;
        } catch (Throwable th) {
            this.mTmpItem[0] = null;
            this.mPendingItem = null;
            throw th;
        }
    }

    /* access modifiers changed from: protected */
    public final boolean appendVisbleItemsWithCache(int i, boolean z) {
        int i2;
        int i3;
        int i4;
        if (this.mLocations.size() == 0) {
            return false;
        }
        int count = this.mProvider.getCount();
        int i5 = this.mLastVisibleIndex;
        if (i5 >= 0) {
            i2 = i5 + 1;
            i3 = this.mProvider.getEdge(i5);
        } else {
            int i6 = this.mStartIndex;
            i2 = i6 != -1 ? i6 : 0;
            if (i2 > getLastIndex() + 1 || i2 < getFirstIndex()) {
                this.mLocations.clear();
                return false;
            } else if (i2 > getLastIndex()) {
                return false;
            } else {
                i3 = Integer.MAX_VALUE;
            }
        }
        int lastIndex = getLastIndex();
        int i7 = i2;
        while (i7 < count && i7 <= lastIndex) {
            Location location = getLocation(i7);
            if (i3 != Integer.MAX_VALUE) {
                i3 += location.offset;
            }
            int i8 = location.row;
            int createItem = this.mProvider.createItem(i7, true, this.mTmpItem, false);
            if (createItem != location.size) {
                location.size = createItem;
                this.mLocations.removeFromEnd(lastIndex - i7);
                i4 = i7;
            } else {
                i4 = lastIndex;
            }
            this.mLastVisibleIndex = i7;
            if (this.mFirstVisibleIndex < 0) {
                this.mFirstVisibleIndex = i7;
            }
            this.mProvider.addItem(this.mTmpItem[0], i7, createItem, i8, i3);
            if (!z && checkAppendOverLimit(i)) {
                return true;
            }
            if (i3 == Integer.MAX_VALUE) {
                i3 = this.mProvider.getEdge(i7);
            }
            if (i8 == this.mNumRows - 1 && z) {
                return true;
            }
            i7++;
            lastIndex = i4;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public final int appendVisibleItemToRow(int i, int i2, int i3) {
        int i4;
        int i5 = this.mLastVisibleIndex;
        if (i5 < 0 || (i5 == getLastIndex() && this.mLastVisibleIndex == i - 1)) {
            int i6 = this.mLastVisibleIndex;
            if (i6 < 0) {
                i4 = (this.mLocations.size() <= 0 || i != getLastIndex() + 1) ? 0 : calculateOffsetAfterLastItem(i2);
            } else {
                i4 = i3 - this.mProvider.getEdge(i6);
            }
            Location location = new Location(i2, i4, 0);
            this.mLocations.addLast(location);
            Object obj = this.mPendingItem;
            if (obj != null) {
                location.size = this.mPendingItemSize;
                this.mPendingItem = null;
            } else {
                location.size = this.mProvider.createItem(i, true, this.mTmpItem, false);
                obj = this.mTmpItem[0];
            }
            if (this.mLocations.size() == 1) {
                this.mLastVisibleIndex = i;
                this.mFirstVisibleIndex = i;
                this.mFirstIndex = i;
            } else {
                int i7 = this.mLastVisibleIndex;
                if (i7 < 0) {
                    this.mLastVisibleIndex = i;
                    this.mFirstVisibleIndex = i;
                } else {
                    this.mLastVisibleIndex = i7 + 1;
                }
            }
            this.mProvider.addItem(obj, i, location.size, i2, i3);
            return location.size;
        }
        throw new IllegalStateException();
    }

    @Override // androidx.leanback.widget.Grid
    public final CircularIntArray[] getItemPositionsInRows(int i, int i2) {
        for (int i3 = 0; i3 < this.mNumRows; i3++) {
            this.mTmpItemPositionsInRows[i3].clear();
        }
        if (i >= 0) {
            while (i <= i2) {
                CircularIntArray circularIntArray = this.mTmpItemPositionsInRows[getLocation(i).row];
                if (circularIntArray.size() <= 0 || circularIntArray.getLast() != i - 1) {
                    circularIntArray.addLast(i);
                    circularIntArray.addLast(i);
                } else {
                    circularIntArray.popLast();
                    circularIntArray.addLast(i);
                }
                i++;
            }
        }
        return this.mTmpItemPositionsInRows;
    }

    @Override // androidx.leanback.widget.Grid
    public void invalidateItemsAfter(int i) {
        super.invalidateItemsAfter(i);
        this.mLocations.removeFromEnd((getLastIndex() - i) + 1);
        if (this.mLocations.size() == 0) {
            this.mFirstIndex = -1;
        }
    }
}
