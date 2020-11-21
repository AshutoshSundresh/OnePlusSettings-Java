package com.oneplus.settings.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class OPAdjustWidthLayout extends ViewGroup {
    public OPAdjustWidthLayout(Context context) {
        this(context, null);
    }

    public OPAdjustWidthLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public OPAdjustWidthLayout(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public OPAdjustWidthLayout(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int measuredWidth = (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
        boolean z = false;
        int i3 = 0;
        int i4 = 0;
        for (int i5 = 0; i5 < getChildCount(); i5++) {
            View childAt = getChildAt(i5);
            if (childAt.getVisibility() != 8) {
                measureChildWithMargins(childAt, i, 0, i2, 0);
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) childAt.getLayoutParams();
                int measuredWidth2 = childAt.getMeasuredWidth() + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin;
                int i6 = i4 + measuredWidth2;
                if (i6 > measuredWidth) {
                    z = true;
                } else {
                    i4 = i6;
                }
                if (i5 != 0) {
                    i3 += measuredWidth2;
                }
            }
        }
        if (z) {
            View childAt2 = getChildAt(0);
            ViewGroup.MarginLayoutParams marginLayoutParams2 = (ViewGroup.MarginLayoutParams) childAt2.getLayoutParams();
            int i7 = ((measuredWidth - i3) - marginLayoutParams2.leftMargin) - marginLayoutParams2.rightMargin;
            if (i7 > 0) {
                childAt2.measure(View.MeasureSpec.makeMeasureSpec(i7, 1073741824), ViewGroup.getChildMeasureSpec(i2, getPaddingTop() + getPaddingBottom() + marginLayoutParams2.topMargin + marginLayoutParams2.bottomMargin, marginLayoutParams2.height));
            }
        }
        if (getChildCount() <= 0) {
            return;
        }
        if (View.MeasureSpec.getMode(i2) == Integer.MIN_VALUE || View.MeasureSpec.getMode(i2) == 0) {
            ViewGroup.MarginLayoutParams marginLayoutParams3 = (ViewGroup.MarginLayoutParams) getChildAt(0).getLayoutParams();
            setMeasuredDimension(View.MeasureSpec.getSize(i), getChildAt(0).getMeasuredHeight() + marginLayoutParams3.topMargin + marginLayoutParams3.bottomMargin + getPaddingTop() + getPaddingBottom());
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        int i6;
        int paddingLeft = getPaddingLeft();
        int i7 = 0;
        for (int i8 = 0; i8 < getChildCount(); i8++) {
            View childAt = getChildAt(i8);
            if (childAt.getVisibility() != 8) {
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) childAt.getLayoutParams();
                if (i8 == 0) {
                    i5 = getPaddingTop() + marginLayoutParams.topMargin;
                    i6 = i5;
                } else {
                    i6 = i7;
                    i5 = ((getChildAt(0).getMeasuredHeight() / 2) + i7) - (childAt.getMeasuredHeight() / 2);
                }
                int i9 = marginLayoutParams.leftMargin + paddingLeft;
                childAt.layout(i9, i5, childAt.getMeasuredWidth() + i9, childAt.getMeasuredHeight() + i5);
                paddingLeft += marginLayoutParams.leftMargin + childAt.getMeasuredWidth() + marginLayoutParams.rightMargin;
                i7 = i6;
            }
        }
    }

    @Override // android.view.ViewGroup
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new ViewGroup.MarginLayoutParams(getContext(), attributeSet);
    }

    /* access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return new ViewGroup.MarginLayoutParams(layoutParams);
    }

    /* access modifiers changed from: protected */
    public ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new ViewGroup.MarginLayoutParams(-2, -2);
    }
}
