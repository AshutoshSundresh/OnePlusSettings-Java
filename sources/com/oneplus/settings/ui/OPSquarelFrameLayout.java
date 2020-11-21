package com.oneplus.settings.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class OPSquarelFrameLayout extends FrameLayout {
    public OPSquarelFrameLayout(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public OPSquarelFrameLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i, 0);
    }

    public OPSquarelFrameLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, 0, 0);
    }

    public OPSquarelFrameLayout(Context context) {
        super(context, null, 0, 0);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i2, i2);
    }
}
