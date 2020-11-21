package androidx.leanback.app;

import androidx.leanback.widget.ObjectAdapter;
import androidx.leanback.widget.Row;

/* access modifiers changed from: package-private */
public class ListRowDataAdapter extends ObjectAdapter {
    private final ObjectAdapter mAdapter;
    final ObjectAdapter.DataObserver mDataObserver;
    int mLastVisibleRowIndex;

    public ListRowDataAdapter(ObjectAdapter objectAdapter) {
        super(objectAdapter.getPresenterSelector());
        this.mAdapter = objectAdapter;
        initialize();
        if (objectAdapter.isImmediateNotifySupported()) {
            this.mDataObserver = new SimpleDataObserver();
        } else {
            this.mDataObserver = new QueueBasedDataObserver();
        }
        attach();
    }

    /* access modifiers changed from: package-private */
    public void detach() {
        this.mAdapter.unregisterObserver(this.mDataObserver);
    }

    /* access modifiers changed from: package-private */
    public void attach() {
        initialize();
        this.mAdapter.registerObserver(this.mDataObserver);
    }

    /* access modifiers changed from: package-private */
    public void initialize() {
        this.mLastVisibleRowIndex = -1;
        for (int size = this.mAdapter.size() - 1; size >= 0; size--) {
            if (((Row) this.mAdapter.get(size)).isRenderedAsRowView()) {
                this.mLastVisibleRowIndex = size;
                return;
            }
        }
    }

    @Override // androidx.leanback.widget.ObjectAdapter
    public int size() {
        return this.mLastVisibleRowIndex + 1;
    }

    @Override // androidx.leanback.widget.ObjectAdapter
    public Object get(int i) {
        return this.mAdapter.get(i);
    }

    /* access modifiers changed from: package-private */
    public void doNotify(int i, int i2, int i3) {
        if (i == 2) {
            notifyItemRangeChanged(i2, i3);
        } else if (i == 4) {
            notifyItemRangeInserted(i2, i3);
        } else if (i == 8) {
            notifyItemRangeRemoved(i2, i3);
        } else if (i == 16) {
            notifyChanged();
        } else {
            throw new IllegalArgumentException("Invalid event type " + i);
        }
    }

    private class SimpleDataObserver extends ObjectAdapter.DataObserver {
        SimpleDataObserver() {
        }

        @Override // androidx.leanback.widget.ObjectAdapter.DataObserver
        public void onItemRangeChanged(int i, int i2) {
            int i3 = ListRowDataAdapter.this.mLastVisibleRowIndex;
            if (i <= i3) {
                onEventFired(2, i, Math.min(i2, (i3 - i) + 1));
            }
        }

        @Override // androidx.leanback.widget.ObjectAdapter.DataObserver
        public void onItemRangeInserted(int i, int i2) {
            ListRowDataAdapter listRowDataAdapter = ListRowDataAdapter.this;
            int i3 = listRowDataAdapter.mLastVisibleRowIndex;
            if (i <= i3) {
                listRowDataAdapter.mLastVisibleRowIndex = i3 + i2;
                onEventFired(4, i, i2);
                return;
            }
            listRowDataAdapter.initialize();
            int i4 = ListRowDataAdapter.this.mLastVisibleRowIndex;
            if (i4 > i3) {
                onEventFired(4, i3 + 1, i4 - i3);
            }
        }

        @Override // androidx.leanback.widget.ObjectAdapter.DataObserver
        public void onItemRangeRemoved(int i, int i2) {
            int i3 = (i + i2) - 1;
            ListRowDataAdapter listRowDataAdapter = ListRowDataAdapter.this;
            int i4 = listRowDataAdapter.mLastVisibleRowIndex;
            if (i3 < i4) {
                listRowDataAdapter.mLastVisibleRowIndex = i4 - i2;
                onEventFired(8, i, i2);
                return;
            }
            listRowDataAdapter.initialize();
            int i5 = ListRowDataAdapter.this.mLastVisibleRowIndex;
            int i6 = i4 - i5;
            if (i6 > 0) {
                onEventFired(8, Math.min(i5 + 1, i), i6);
            }
        }

        @Override // androidx.leanback.widget.ObjectAdapter.DataObserver
        public void onChanged() {
            ListRowDataAdapter.this.initialize();
            onEventFired(16, -1, -1);
        }

        /* access modifiers changed from: protected */
        public void onEventFired(int i, int i2, int i3) {
            ListRowDataAdapter.this.doNotify(i, i2, i3);
        }
    }

    private class QueueBasedDataObserver extends ObjectAdapter.DataObserver {
        QueueBasedDataObserver() {
        }

        @Override // androidx.leanback.widget.ObjectAdapter.DataObserver
        public void onChanged() {
            ListRowDataAdapter.this.initialize();
            ListRowDataAdapter.this.notifyChanged();
        }
    }
}
