package com.oneplus.settings.ui;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.oneplus.settings.utils.OPUtils;

public class OPSpaceItemDecoration extends RecyclerView.ItemDecoration {
    private Context mContext;
    private int mSpace;
    private int mTotalSize;

    public OPSpaceItemDecoration(Context context, int i, int i2) {
        this.mContext = context;
        this.mTotalSize = i;
        this.mSpace = i2;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
    public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
        super.getItemOffsets(rect, view, recyclerView, state);
        boolean z = true;
        if (((RecyclerView.LayoutParams) view.getLayoutParams()).getViewAdapterPosition() != this.mTotalSize - 1) {
            z = false;
        }
        if (OPUtils.isLTRLayout(this.mContext)) {
            int i = this.mSpace;
            rect.left = i;
            if (z) {
                rect.right = i;
                return;
            }
            return;
        }
        int i2 = this.mSpace;
        rect.right = i2;
        if (z) {
            rect.left = i2;
        }
    }
}
