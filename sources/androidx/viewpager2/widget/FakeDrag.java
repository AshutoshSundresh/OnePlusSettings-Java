package androidx.viewpager2.widget;

import androidx.recyclerview.widget.RecyclerView;

/* access modifiers changed from: package-private */
public final class FakeDrag {
    private final ScrollEventAdapter mScrollEventAdapter;

    FakeDrag(ViewPager2 viewPager2, ScrollEventAdapter scrollEventAdapter, RecyclerView recyclerView) {
        this.mScrollEventAdapter = scrollEventAdapter;
    }

    /* access modifiers changed from: package-private */
    public boolean isFakeDragging() {
        return this.mScrollEventAdapter.isFakeDragging();
    }
}
