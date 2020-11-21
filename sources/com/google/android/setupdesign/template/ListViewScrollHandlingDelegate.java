package com.google.android.setupdesign.template;

import android.util.Log;
import android.widget.AbsListView;
import android.widget.ListView;
import com.google.android.setupdesign.template.RequireScrollMixin;

public class ListViewScrollHandlingDelegate implements RequireScrollMixin.ScrollHandlingDelegate, AbsListView.OnScrollListener {
    private final ListView listView;
    private final RequireScrollMixin requireScrollMixin;

    public void onScrollStateChanged(AbsListView absListView, int i) {
    }

    public ListViewScrollHandlingDelegate(RequireScrollMixin requireScrollMixin2, ListView listView2) {
        this.requireScrollMixin = requireScrollMixin2;
        this.listView = listView2;
    }

    @Override // com.google.android.setupdesign.template.RequireScrollMixin.ScrollHandlingDelegate
    public void startListening() {
        ListView listView2 = this.listView;
        if (listView2 != null) {
            listView2.setOnScrollListener(this);
            if (this.listView.getLastVisiblePosition() < this.listView.getAdapter().getCount()) {
                this.requireScrollMixin.notifyScrollabilityChange(true);
                return;
            }
            return;
        }
        Log.w("ListViewDelegate", "Cannot require scroll. List view is null");
    }

    @Override // com.google.android.setupdesign.template.RequireScrollMixin.ScrollHandlingDelegate
    public void pageScrollDown() {
        ListView listView2 = this.listView;
        if (listView2 != null) {
            this.listView.smoothScrollBy(listView2.getHeight(), 500);
        }
    }

    public void onScroll(AbsListView absListView, int i, int i2, int i3) {
        if (i + i2 >= i3) {
            this.requireScrollMixin.notifyScrollabilityChange(false);
        } else {
            this.requireScrollMixin.notifyScrollabilityChange(true);
        }
    }
}
