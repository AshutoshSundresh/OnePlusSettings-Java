package com.google.android.material.indicator.draw.drawer.type;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import com.google.android.material.indicator.animation.data.Value;
import com.google.android.material.indicator.animation.data.WormAnimationValue;
import com.google.android.material.indicator.draw.data.Indicator;
import com.google.android.material.indicator.draw.data.Orientation;

public class WormDrawer extends BaseDrawer {
    public RectF rect = new RectF();

    public WormDrawer(Paint paint, Indicator indicator) {
        super(paint, indicator);
    }

    public void draw(Canvas canvas, Value value, int i, int i2) {
        WormAnimationValue wormAnimationValue = (WormAnimationValue) value;
        if (wormAnimationValue != null) {
            int rectStart = wormAnimationValue.getRectStart();
            int rectEnd = wormAnimationValue.getRectEnd();
            int radius = this.indicator.getRadius();
            int unselectedColor = this.indicator.getUnselectedColor();
            int selectedColor = this.indicator.getSelectedColor();
            if (this.indicator.getOrientation() == Orientation.HORIZONTAL) {
                RectF rectF = this.rect;
                rectF.left = (float) rectStart;
                rectF.right = (float) rectEnd;
                rectF.top = (float) (i2 - radius);
                rectF.bottom = (float) (i2 + radius);
            } else {
                RectF rectF2 = this.rect;
                rectF2.left = (float) (i - radius);
                rectF2.right = (float) (i + radius);
                rectF2.top = (float) rectStart;
                rectF2.bottom = (float) rectEnd;
            }
            this.paint.setColor(unselectedColor);
            float f = (float) i;
            float f2 = (float) i2;
            float f3 = (float) radius;
            canvas.drawCircle(f, f2, f3, this.paint);
            this.paint.setColor(selectedColor);
            canvas.drawRoundRect(this.rect, f3, f3, this.paint);
        }
    }
}
