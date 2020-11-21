package com.android.settings.deviceinfo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import com.android.settings.R$styleable;
import java.util.Collection;

public class PercentageBarChart extends View {
    private final Paint mEmptyPaint = new Paint();
    private Collection<Entry> mEntries;
    private int mMinTickWidth = 1;

    public static class Entry implements Comparable<Entry> {
        public final int order;
        public final Paint paint;
        public final float percentage;

        public int compareTo(Entry entry) {
            return this.order - entry.order;
        }
    }

    public PercentageBarChart(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.PercentageBarChart);
        this.mMinTickWidth = obtainStyledAttributes.getDimensionPixelSize(R$styleable.PercentageBarChart_minTickWidth, 1);
        int color = obtainStyledAttributes.getColor(R$styleable.PercentageBarChart_emptyColor, -16777216);
        obtainStyledAttributes.recycle();
        this.mEmptyPaint.setColor(color);
        this.mEmptyPaint.setStyle(Paint.Style.FILL);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float f;
        float f2;
        super.onDraw(canvas);
        int paddingLeft = getPaddingLeft();
        int width = getWidth() - getPaddingRight();
        int paddingTop = getPaddingTop();
        int height = getHeight() - getPaddingBottom();
        int i = width - paddingLeft;
        if (isLayoutRtl()) {
            float f3 = (float) width;
            Collection<Entry> collection = this.mEntries;
            if (collection != null) {
                float f4 = f3;
                for (Entry entry : collection) {
                    float f5 = entry.percentage;
                    float max = f4 - (f5 == 0.0f ? 0.0f : Math.max((float) this.mMinTickWidth, ((float) i) * f5));
                    float f6 = (float) paddingLeft;
                    if (max < f6) {
                        canvas.drawRect(f6, (float) paddingTop, f4, (float) height, entry.paint);
                        return;
                    } else {
                        canvas.drawRect(max, (float) paddingTop, f4, (float) height, entry.paint);
                        f4 = max;
                    }
                }
                f2 = f4;
            } else {
                f2 = f3;
            }
            canvas.drawRect((float) paddingLeft, (float) paddingTop, f2, (float) height, this.mEmptyPaint);
            return;
        }
        float f7 = (float) paddingLeft;
        Collection<Entry> collection2 = this.mEntries;
        if (collection2 != null) {
            float f8 = f7;
            for (Entry entry2 : collection2) {
                float f9 = entry2.percentage;
                float max2 = f8 + (f9 == 0.0f ? 0.0f : Math.max((float) this.mMinTickWidth, ((float) i) * f9));
                float f10 = (float) width;
                if (max2 > f10) {
                    canvas.drawRect(f8, (float) paddingTop, f10, (float) height, entry2.paint);
                    return;
                } else {
                    canvas.drawRect(f8, (float) paddingTop, max2, (float) height, entry2.paint);
                    f8 = max2;
                }
            }
            f = f8;
        } else {
            f = f7;
        }
        canvas.drawRect(f, (float) paddingTop, (float) width, (float) height, this.mEmptyPaint);
    }

    public void setBackgroundColor(int i) {
        this.mEmptyPaint.setColor(i);
    }

    public void setEntries(Collection<Entry> collection) {
        this.mEntries = collection;
    }
}
