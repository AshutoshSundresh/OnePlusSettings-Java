package com.google.android.setupcompat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.google.android.setupcompat.R$id;

public class ButtonBarLayout extends LinearLayout {
    private int originalPaddingLeft;
    private int originalPaddingRight;
    private boolean stacked = false;

    public ButtonBarLayout(Context context) {
        super(context);
    }

    public ButtonBarLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        boolean z;
        int i3;
        int size = View.MeasureSpec.getSize(i);
        setStacked(false);
        boolean z2 = true;
        if (View.MeasureSpec.getMode(i) == 1073741824) {
            i3 = View.MeasureSpec.makeMeasureSpec(0, 0);
            z = true;
        } else {
            z = false;
            i3 = i;
        }
        super.onMeasure(i3, i2);
        if (getMeasuredWidth() > size) {
            setStacked(true);
        } else {
            z2 = z;
        }
        if (z2) {
            super.onMeasure(i, i2);
        }
    }

    private void setStacked(boolean z) {
        if (this.stacked != z) {
            this.stacked = z;
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = getChildAt(i);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) childAt.getLayoutParams();
                if (z) {
                    childAt.setTag(R$id.suc_customization_original_weight, Float.valueOf(layoutParams.weight));
                    layoutParams.weight = 0.0f;
                } else {
                    Float f = (Float) childAt.getTag(R$id.suc_customization_original_weight);
                    if (f != null) {
                        layoutParams.weight = f.floatValue();
                    }
                }
                childAt.setLayoutParams(layoutParams);
            }
            setOrientation(z ? 1 : 0);
            for (int i2 = childCount - 1; i2 >= 0; i2--) {
                bringChildToFront(getChildAt(i2));
            }
            if (z) {
                this.originalPaddingLeft = getPaddingLeft();
                int paddingRight = getPaddingRight();
                this.originalPaddingRight = paddingRight;
                int max = Math.max(this.originalPaddingLeft, paddingRight);
                setPadding(max, getPaddingTop(), max, getPaddingBottom());
                return;
            }
            setPadding(this.originalPaddingLeft, getPaddingTop(), this.originalPaddingRight, getPaddingBottom());
        }
    }
}
