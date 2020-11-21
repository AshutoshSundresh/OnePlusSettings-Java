package com.android.settings.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.android.settings.R$styleable;

public final class AspectRatioFrameLayout extends FrameLayout {
    float mAspectRatio;

    public AspectRatioFrameLayout(Context context) {
        this(context, null);
    }

    public AspectRatioFrameLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AspectRatioFrameLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mAspectRatio = 1.0f;
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.AspectRatioFrameLayout);
            this.mAspectRatio = obtainStyledAttributes.getFloat(R$styleable.AspectRatioFrameLayout_aspectRatio, 1.0f);
            obtainStyledAttributes.recycle();
        }
    }

    public void setAspectRatio(float f) {
        this.mAspectRatio = f;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        if (measuredWidth != 0 && measuredHeight != 0) {
            float f = (float) measuredHeight;
            if (Math.abs(this.mAspectRatio - (((float) measuredWidth) / f)) > 0.01f) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec((int) (f * this.mAspectRatio), 1073741824), View.MeasureSpec.makeMeasureSpec(measuredHeight, 1073741824));
            }
        }
    }
}
