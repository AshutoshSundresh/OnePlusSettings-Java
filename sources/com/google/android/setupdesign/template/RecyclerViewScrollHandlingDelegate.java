package com.google.android.setupdesign.template;

import android.util.Log;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.setupdesign.template.RequireScrollMixin;

public class RecyclerViewScrollHandlingDelegate implements RequireScrollMixin.ScrollHandlingDelegate {
    private final RecyclerView recyclerView;
    private final RequireScrollMixin requireScrollMixin;

    public RecyclerViewScrollHandlingDelegate(RequireScrollMixin requireScrollMixin2, RecyclerView recyclerView2) {
        this.requireScrollMixin = requireScrollMixin2;
        this.recyclerView = recyclerView2;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private boolean canScrollDown() {
        RecyclerView recyclerView2 = this.recyclerView;
        if (recyclerView2 == null) {
            return false;
        }
        int computeVerticalScrollOffset = recyclerView2.computeVerticalScrollOffset();
        int computeVerticalScrollRange = this.recyclerView.computeVerticalScrollRange() - this.recyclerView.computeVerticalScrollExtent();
        if (computeVerticalScrollRange == 0 || computeVerticalScrollOffset >= computeVerticalScrollRange - 1) {
            return false;
        }
        return true;
    }

    @Override // com.google.android.setupdesign.template.RequireScrollMixin.ScrollHandlingDelegate
    public void startListening() {
        RecyclerView recyclerView2 = this.recyclerView;
        if (recyclerView2 != null) {
            recyclerView2.addOnScrollListener(new RecyclerView.OnScrollListener() {
                /* class com.google.android.setupdesign.template.RecyclerViewScrollHandlingDelegate.AnonymousClass1 */

                @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                    RecyclerViewScrollHandlingDelegate.this.requireScrollMixin.notifyScrollabilityChange(RecyclerViewScrollHandlingDelegate.this.canScrollDown());
                }
            });
            if (canScrollDown()) {
                this.requireScrollMixin.notifyScrollabilityChange(true);
                return;
            }
            return;
        }
        Log.w("RVRequireScrollMixin", "Cannot require scroll. Recycler view is null.");
    }

    @Override // com.google.android.setupdesign.template.RequireScrollMixin.ScrollHandlingDelegate
    public void pageScrollDown() {
        RecyclerView recyclerView2 = this.recyclerView;
        if (recyclerView2 != null) {
            this.recyclerView.smoothScrollBy(0, recyclerView2.getHeight());
        }
    }
}
