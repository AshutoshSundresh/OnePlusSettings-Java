package com.google.android.material.indicator.draw.drawer.type;

import android.graphics.Canvas;
import android.graphics.Paint;
import com.google.android.material.indicator.draw.data.Indicator;

public class BasicDrawer extends BaseDrawer {
    private Paint strokePaint;

    public BasicDrawer(Paint paint, Indicator indicator) {
        super(paint, indicator);
        Paint paint2 = new Paint();
        this.strokePaint = paint2;
        paint2.setStyle(Paint.Style.STROKE);
        this.strokePaint.setAntiAlias(true);
        this.strokePaint.setStrokeWidth((float) indicator.getStroke());
    }

    public void draw(Canvas canvas, int i, boolean z, int i2, int i3) {
        float radius = (float) this.indicator.getRadius();
        int selectedColor = this.indicator.getSelectedColor();
        int unselectedColor = this.indicator.getUnselectedColor();
        if (i != this.indicator.getSelectedPosition()) {
            selectedColor = unselectedColor;
        }
        Paint paint = this.paint;
        paint.setColor(selectedColor);
        canvas.drawCircle((float) i2, (float) i3, radius, paint);
    }
}
