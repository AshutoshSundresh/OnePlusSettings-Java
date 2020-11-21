package com.android.settings.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewDebug;
import android.widget.FrameLayout;
import com.android.settings.R$styleable;

public class ChartView extends FrameLayout {
    private Rect mContent;
    ChartAxis mHoriz;
    @ViewDebug.ExportedProperty
    private int mOptimalWidth;
    private float mOptimalWidthWeight;
    ChartAxis mVert;

    public ChartView(Context context) {
        this(context, null, 0);
    }

    public ChartView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ChartView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mOptimalWidth = -1;
        this.mOptimalWidthWeight = 0.0f;
        this.mContent = new Rect();
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.ChartView, i, 0);
        setOptimalWidth(obtainStyledAttributes.getDimensionPixelSize(R$styleable.ChartView_optimalWidth, -1), obtainStyledAttributes.getFloat(R$styleable.ChartView_optimalWidthWeight, 0.0f));
        obtainStyledAttributes.recycle();
        setClipToPadding(false);
        setClipChildren(false);
    }

    public void setOptimalWidth(int i, float f) {
        this.mOptimalWidth = i;
        this.mOptimalWidthWeight = f;
        requestLayout();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int measuredWidth = getMeasuredWidth();
        int i3 = this.mOptimalWidth;
        int i4 = measuredWidth - i3;
        if (i3 > 0 && i4 > 0) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec((int) (((float) i3) + (((float) i4) * this.mOptimalWidthWeight)), 1073741824), i2);
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        this.mContent.set(getPaddingLeft(), getPaddingTop(), (i3 - i) - getPaddingRight(), (i4 - i2) - getPaddingBottom());
        int width = this.mContent.width();
        int height = this.mContent.height();
        this.mHoriz.setSize((float) width);
        this.mVert.setSize((float) height);
        Rect rect = new Rect();
        Rect rect2 = new Rect();
        for (int i5 = 0; i5 < getChildCount(); i5++) {
            View childAt = getChildAt(i5);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) childAt.getLayoutParams();
            rect.set(this.mContent);
            if (childAt instanceof ChartGridView) {
                Gravity.apply(layoutParams.gravity, width, height, rect, rect2);
                childAt.layout(rect2.left, rect2.top, rect2.right, rect2.bottom + childAt.getPaddingBottom());
            } else if (childAt instanceof ChartSweepView) {
                layoutSweep((ChartSweepView) childAt, rect, rect2);
                childAt.layout(rect2.left, rect2.top, rect2.right, rect2.bottom);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void layoutSweep(ChartSweepView chartSweepView, Rect rect, Rect rect2) {
        Rect margins = chartSweepView.getMargins();
        if (chartSweepView.getFollowAxis() == 1) {
            int point = rect.top + margins.top + ((int) chartSweepView.getPoint());
            rect.top = point;
            rect.bottom = point;
            rect.left += margins.left;
            rect.right += margins.right;
            Gravity.apply(8388659, rect.width(), chartSweepView.getMeasuredHeight(), rect, rect2);
            return;
        }
        int point2 = rect.left + margins.left + ((int) chartSweepView.getPoint());
        rect.left = point2;
        rect.right = point2;
        rect.top += margins.top;
        rect.bottom += margins.bottom;
        Gravity.apply(8388659, chartSweepView.getMeasuredWidth(), rect.height(), rect, rect2);
    }
}
