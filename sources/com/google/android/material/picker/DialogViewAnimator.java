package com.google.android.material.picker;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ViewAnimator;
import java.util.ArrayList;

public class DialogViewAnimator extends ViewAnimator {
    private final ArrayList<View> mMatchParentChildren = new ArrayList<>(1);

    public DialogViewAnimator(Context context) {
        super(context);
    }

    public DialogViewAnimator(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int i3;
        int i4;
        int i5;
        boolean z = (View.MeasureSpec.getMode(i) == 1073741824 && View.MeasureSpec.getMode(i2) == 1073741824) ? false : true;
        int childCount = getChildCount();
        int i6 = 0;
        int i7 = 0;
        int i8 = 0;
        for (int i9 = 0; i9 < childCount; i9++) {
            View childAt = getChildAt(i9);
            if (getMeasureAllChildren() || childAt.getVisibility() != 8) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) childAt.getLayoutParams();
                boolean z2 = layoutParams.width == -1;
                boolean z3 = layoutParams.height == -1;
                if (z && (z2 || z3)) {
                    this.mMatchParentChildren.add(childAt);
                }
                measureChildWithMargins(childAt, i, 0, i2, 0);
                if (!z || z2) {
                    i5 = 0;
                } else {
                    int max = Math.max(i8, childAt.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin);
                    i5 = (childAt.getMeasuredWidthAndState() & -16777216) | 0;
                    i8 = max;
                }
                if (!z || z3) {
                    i7 = i7;
                } else {
                    int max2 = Math.max(i7, childAt.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin);
                    i5 |= (childAt.getMeasuredHeightAndState() >> 16) & -256;
                    i7 = max2;
                }
                i6 = ViewAnimator.combineMeasuredStates(i6, i5);
            }
        }
        int paddingLeft = i8 + getPaddingLeft() + getPaddingRight();
        int max3 = Math.max(i7 + getPaddingTop() + getPaddingBottom(), getSuggestedMinimumHeight());
        int max4 = Math.max(paddingLeft, getSuggestedMinimumWidth());
        Drawable foreground = getForeground();
        if (foreground != null) {
            max3 = Math.max(max3, foreground.getMinimumHeight());
            max4 = Math.max(max4, foreground.getMinimumWidth());
        }
        setMeasuredDimension(ViewAnimator.resolveSizeAndState(max4, i, i6), ViewAnimator.resolveSizeAndState(max3, i2, i6 << 16));
        int size = this.mMatchParentChildren.size();
        for (int i10 = 0; i10 < size; i10++) {
            View view = this.mMatchParentChildren.get(i10);
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            if (marginLayoutParams.width == -1) {
                i3 = View.MeasureSpec.makeMeasureSpec((((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - marginLayoutParams.leftMargin) - marginLayoutParams.rightMargin, 1073741824);
            } else {
                i3 = ViewAnimator.getChildMeasureSpec(i, getPaddingLeft() + getPaddingRight() + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin, marginLayoutParams.width);
            }
            if (marginLayoutParams.height == -1) {
                i4 = View.MeasureSpec.makeMeasureSpec((((getMeasuredHeight() - getPaddingTop()) - getPaddingBottom()) - marginLayoutParams.topMargin) - marginLayoutParams.bottomMargin, 1073741824);
            } else {
                i4 = ViewAnimator.getChildMeasureSpec(i2, getPaddingTop() + getPaddingBottom() + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin, marginLayoutParams.height);
            }
            view.measure(i3, i4);
        }
        this.mMatchParentChildren.clear();
    }
}
