package com.google.android.setupdesign;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable divider;
    private int dividerCondition;
    private int dividerHeight;
    private int dividerIntrinsicHeight;

    public interface DividedViewHolder {
        boolean isDividerAllowedAbove();

        boolean isDividerAllowedBelow();
    }

    public DividerItemDecoration(Context context) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(R$styleable.SudDividerItemDecoration);
        Drawable drawable = obtainStyledAttributes.getDrawable(R$styleable.SudDividerItemDecoration_android_listDivider);
        int dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(R$styleable.SudDividerItemDecoration_android_dividerHeight, 0);
        int i = obtainStyledAttributes.getInt(R$styleable.SudDividerItemDecoration_sudDividerCondition, 0);
        obtainStyledAttributes.recycle();
        setDivider(drawable);
        setDividerHeight(dimensionPixelSize);
        setDividerCondition(i);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
    public void onDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.State state) {
        if (this.divider != null) {
            int childCount = recyclerView.getChildCount();
            int width = recyclerView.getWidth();
            int i = this.dividerHeight;
            if (i == 0) {
                i = this.dividerIntrinsicHeight;
            }
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = recyclerView.getChildAt(i2);
                if (shouldDrawDividerBelow(childAt, recyclerView)) {
                    int y = ((int) ViewCompat.getY(childAt)) + childAt.getHeight();
                    this.divider.setBounds(0, y, width, y + i);
                    this.divider.draw(canvas);
                }
            }
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
    public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
        if (shouldDrawDividerBelow(view, recyclerView)) {
            int i = this.dividerHeight;
            if (i == 0) {
                i = this.dividerIntrinsicHeight;
            }
            rect.bottom = i;
        }
    }

    private boolean shouldDrawDividerBelow(View view, RecyclerView recyclerView) {
        RecyclerView.ViewHolder childViewHolder = recyclerView.getChildViewHolder(view);
        int layoutPosition = childViewHolder.getLayoutPosition();
        int itemCount = recyclerView.getAdapter().getItemCount() - 1;
        if (isDividerAllowedBelow(childViewHolder)) {
            if (this.dividerCondition == 0) {
                return true;
            }
        } else if (this.dividerCondition == 1 || layoutPosition == itemCount) {
            return false;
        }
        if (layoutPosition >= itemCount || isDividerAllowedAbove(recyclerView.findViewHolderForLayoutPosition(layoutPosition + 1))) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean isDividerAllowedAbove(RecyclerView.ViewHolder viewHolder) {
        return !(viewHolder instanceof DividedViewHolder) || ((DividedViewHolder) viewHolder).isDividerAllowedAbove();
    }

    /* access modifiers changed from: protected */
    public boolean isDividerAllowedBelow(RecyclerView.ViewHolder viewHolder) {
        return !(viewHolder instanceof DividedViewHolder) || ((DividedViewHolder) viewHolder).isDividerAllowedBelow();
    }

    public void setDivider(Drawable drawable) {
        if (drawable != null) {
            this.dividerIntrinsicHeight = drawable.getIntrinsicHeight();
        } else {
            this.dividerIntrinsicHeight = 0;
        }
        this.divider = drawable;
    }

    public Drawable getDivider() {
        return this.divider;
    }

    public void setDividerHeight(int i) {
        this.dividerHeight = i;
    }

    public void setDividerCondition(int i) {
        this.dividerCondition = i;
    }
}
