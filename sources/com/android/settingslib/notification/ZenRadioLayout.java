package com.android.settingslib.notification;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class ZenRadioLayout extends LinearLayout {
    public ZenRadioLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        ViewGroup viewGroup = (ViewGroup) getChildAt(0);
        ViewGroup viewGroup2 = (ViewGroup) getChildAt(1);
        int childCount = viewGroup.getChildCount();
        if (childCount == viewGroup2.getChildCount()) {
            View view = null;
            boolean z = false;
            for (int i3 = 0; i3 < childCount; i3++) {
                View childAt = viewGroup.getChildAt(i3);
                View childAt2 = viewGroup2.getChildAt(i3);
                if (view != null) {
                    childAt.setAccessibilityTraversalAfter(view.getId());
                }
                View findFirstClickable = findFirstClickable(childAt2);
                if (findFirstClickable != null) {
                    findFirstClickable.setAccessibilityTraversalAfter(childAt.getId());
                }
                view = findLastClickable(childAt2);
                if (childAt.getLayoutParams().height != childAt2.getMeasuredHeight()) {
                    childAt.getLayoutParams().height = childAt2.getMeasuredHeight();
                    z = true;
                }
            }
            if (z) {
                super.onMeasure(i, i2);
                return;
            }
            return;
        }
        throw new IllegalStateException("Expected matching children");
    }

    private View findFirstClickable(View view) {
        if (view.isClickable()) {
            return view;
        }
        if (!(view instanceof ViewGroup)) {
            return null;
        }
        ViewGroup viewGroup = (ViewGroup) view;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View findFirstClickable = findFirstClickable(viewGroup.getChildAt(i));
            if (findFirstClickable != null) {
                return findFirstClickable;
            }
        }
        return null;
    }

    private View findLastClickable(View view) {
        if (view.isClickable()) {
            return view;
        }
        if (!(view instanceof ViewGroup)) {
            return null;
        }
        ViewGroup viewGroup = (ViewGroup) view;
        for (int childCount = viewGroup.getChildCount() - 1; childCount >= 0; childCount--) {
            View findLastClickable = findLastClickable(viewGroup.getChildAt(childCount));
            if (findLastClickable != null) {
                return findLastClickable;
            }
        }
        return null;
    }
}
