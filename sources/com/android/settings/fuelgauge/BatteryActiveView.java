package com.android.settings.fuelgauge;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.View;

public class BatteryActiveView extends View {
    private final Paint mPaint = new Paint();
    private BatteryActiveProvider mProvider;

    public interface BatteryActiveProvider {
        SparseIntArray getColorArray();

        long getPeriod();
    }

    public BatteryActiveView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void setProvider(BatteryActiveProvider batteryActiveProvider) {
        this.mProvider = batteryActiveProvider;
        if (getWidth() != 0) {
            postInvalidate();
        }
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (getWidth() != 0) {
            postInvalidate();
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        BatteryActiveProvider batteryActiveProvider = this.mProvider;
        if (batteryActiveProvider != null) {
            SparseIntArray colorArray = batteryActiveProvider.getColorArray();
            float period = (float) this.mProvider.getPeriod();
            int i = 0;
            while (i < colorArray.size() - 1) {
                int i2 = i + 1;
                drawColor(canvas, colorArray.keyAt(i), colorArray.keyAt(i2), colorArray.valueAt(i), period);
                i = i2;
            }
        }
    }

    private void drawColor(Canvas canvas, int i, int i2, int i3, float f) {
        if (i3 != 0) {
            this.mPaint.setColor(i3);
            canvas.drawRect((((float) i) / f) * ((float) getWidth()), 0.0f, (((float) i2) / f) * ((float) getWidth()), (float) getHeight(), this.mPaint);
        }
    }
}
