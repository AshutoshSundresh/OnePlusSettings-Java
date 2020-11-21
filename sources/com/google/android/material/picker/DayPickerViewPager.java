package com.google.android.material.picker;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import androidx.viewpager.widget.ViewPager;
import java.lang.reflect.Method;
import java.util.ArrayList;

class DayPickerViewPager extends ViewPager {
    private final ArrayList<View> mMatchParentChildren;

    public DayPickerViewPager(Context context) {
        this(context, null);
    }

    public DayPickerViewPager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mMatchParentChildren = new ArrayList<>(1);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.viewpager.widget.ViewPager
    public void onMeasure(int i, int i2) {
        int i3;
        int i4;
        Drawable foreground;
        try {
            Method declaredMethod = ViewPager.class.getDeclaredMethod("populate", new Class[0]);
            if (declaredMethod != null) {
                declaredMethod.setAccessible(true);
                declaredMethod.invoke(this, new Object[0]);
            }
        } catch (Exception unused) {
        }
        int childCount = getChildCount();
        boolean z = (View.MeasureSpec.getMode(i) == 1073741824 && View.MeasureSpec.getMode(i2) == 1073741824) ? false : true;
        int i5 = 0;
        int i6 = 0;
        int i7 = 0;
        for (int i8 = 0; i8 < childCount; i8++) {
            View childAt = getChildAt(i8);
            if (childAt.getVisibility() != 8) {
                measureChild(childAt, i, i2);
                ViewPager.LayoutParams layoutParams = (ViewPager.LayoutParams) childAt.getLayoutParams();
                i5 = Math.max(i5, childAt.getMeasuredWidth());
                i6 = Math.max(i6, childAt.getMeasuredHeight());
                i7 = ViewGroup.combineMeasuredStates(i7, childAt.getMeasuredState());
                if (z && (((ViewGroup.LayoutParams) layoutParams).width == -1 || ((ViewGroup.LayoutParams) layoutParams).height == -1)) {
                    this.mMatchParentChildren.add(childAt);
                }
            }
        }
        int paddingLeft = i5 + getPaddingLeft() + getPaddingRight();
        int max = Math.max(i6 + getPaddingTop() + getPaddingBottom(), getSuggestedMinimumHeight());
        int max2 = Math.max(paddingLeft, getSuggestedMinimumWidth());
        if (Build.VERSION.SDK_INT >= 23 && (foreground = getForeground()) != null) {
            max = Math.max(max, foreground.getMinimumHeight());
            max2 = Math.max(max2, foreground.getMinimumWidth());
        }
        setMeasuredDimension(ViewGroup.resolveSizeAndState(max2, i, i7), ViewGroup.resolveSizeAndState(max, i2, i7 << 16));
        int size = this.mMatchParentChildren.size();
        if (size > 1) {
            for (int i9 = 0; i9 < size; i9++) {
                View view = this.mMatchParentChildren.get(i9);
                ViewPager.LayoutParams layoutParams2 = (ViewPager.LayoutParams) view.getLayoutParams();
                if (((ViewGroup.LayoutParams) layoutParams2).width == -1) {
                    i3 = View.MeasureSpec.makeMeasureSpec((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight(), 1073741824);
                } else {
                    i3 = ViewGroup.getChildMeasureSpec(i, getPaddingLeft() + getPaddingRight(), ((ViewGroup.LayoutParams) layoutParams2).width);
                }
                if (((ViewGroup.LayoutParams) layoutParams2).height == -1) {
                    i4 = View.MeasureSpec.makeMeasureSpec((getMeasuredHeight() - getPaddingTop()) - getPaddingBottom(), 1073741824);
                } else {
                    i4 = ViewGroup.getChildMeasureSpec(i2, getPaddingTop() + getPaddingBottom(), ((ViewGroup.LayoutParams) layoutParams2).height);
                }
                view.measure(i3, i4);
            }
        }
        this.mMatchParentChildren.clear();
    }
}
