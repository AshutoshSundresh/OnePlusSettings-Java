package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.android.settingslib.R$id;

public class BottomLabelLayout extends LinearLayout {
    public BottomLabelLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        boolean z;
        int i3;
        int size = View.MeasureSpec.getSize(i);
        boolean isStacked = isStacked();
        boolean z2 = true;
        if (isStacked || View.MeasureSpec.getMode(i) != 1073741824) {
            z = false;
            i3 = i;
        } else {
            i3 = View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE);
            z = true;
        }
        super.onMeasure(i3, i2);
        if (isStacked || (getMeasuredWidthAndState() & -16777216) != 16777216) {
            z2 = z;
        } else {
            setStacked(true);
        }
        if (z2) {
            super.onMeasure(i, i2);
        }
    }

    /* access modifiers changed from: package-private */
    public void setStacked(boolean z) {
        setOrientation(z ? 1 : 0);
        setGravity(z ? 8388611 : 80);
        View findViewById = findViewById(R$id.spacer);
        if (findViewById != null) {
            findViewById.setVisibility(z ? 8 : 0);
        }
    }

    private boolean isStacked() {
        return getOrientation() == 1;
    }
}
