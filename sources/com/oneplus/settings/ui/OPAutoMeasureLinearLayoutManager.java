package com.oneplus.settings.ui;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class OPAutoMeasureLinearLayoutManager extends LinearLayoutManager {
    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager, androidx.recyclerview.widget.LinearLayoutManager
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        Log.d("OPAutoMeasureLinearLayoutManager", "onLayoutChildren state:" + state.toString());
        super.onLayoutChildren(recycler, state);
        detachAndScrapAttachedViews(recycler);
        calculateChildrenSite(recycler);
    }

    private void calculateChildrenSite(RecyclerView.Recycler recycler) {
        int i = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < getItemCount(); i3++) {
            View viewForPosition = recycler.getViewForPosition(i3);
            addView(viewForPosition);
            measureChildWithMargins(viewForPosition, 0, 0);
            int decoratedMeasuredWidth = getDecoratedMeasuredWidth(viewForPosition);
            int decoratedMeasuredHeight = getDecoratedMeasuredHeight(viewForPosition);
            calculateItemDecorationsForChild(viewForPosition, new Rect());
            if (getOrientation() == 0) {
                int i4 = i2 + decoratedMeasuredWidth;
                layoutDecorated(viewForPosition, i2, 0, i4, decoratedMeasuredHeight);
                i2 = i4;
            } else {
                int i5 = decoratedMeasuredHeight + i;
                layoutDecorated(viewForPosition, 0, i, decoratedMeasuredWidth, i5);
                i = i5;
            }
        }
    }
}
