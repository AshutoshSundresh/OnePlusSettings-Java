package com.android.settings.datausage;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class MeasurableLinearLayout extends LinearLayout {
    private View mDisposableView;
    private View mFixedView;

    public MeasurableLinearLayout(Context context) {
        super(context, null);
    }

    public MeasurableLinearLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, 0);
    }

    public MeasurableLinearLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i, 0);
    }

    public MeasurableLinearLayout(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.mDisposableView == null || getMeasuredWidth() - this.mFixedView.getMeasuredWidth() >= this.mDisposableView.getMeasuredWidth()) {
            View view = this.mDisposableView;
            if (view != null && view.getVisibility() != 0) {
                this.mDisposableView.setVisibility(0);
                super.onMeasure(i, i2);
                return;
            }
            return;
        }
        this.mDisposableView.setVisibility(8);
        super.onMeasure(i, i2);
    }

    public void setChildren(View view, View view2) {
        this.mFixedView = view;
        this.mDisposableView = view2;
    }
}
