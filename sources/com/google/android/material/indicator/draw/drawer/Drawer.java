package com.google.android.material.indicator.draw.drawer;

import android.graphics.Canvas;
import android.graphics.Paint;
import com.google.android.material.indicator.animation.data.Value;
import com.google.android.material.indicator.draw.data.Indicator;
import com.google.android.material.indicator.draw.drawer.type.BasicDrawer;
import com.google.android.material.indicator.draw.drawer.type.ColorDrawer;
import com.google.android.material.indicator.draw.drawer.type.WormDrawer;

public class Drawer {
    private BasicDrawer basicDrawer;
    private ColorDrawer colorDrawer;
    private int coordinateX;
    private int coordinateY;
    private int position;
    private WormDrawer wormDrawer;

    public Drawer(Indicator indicator) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        this.basicDrawer = new BasicDrawer(paint, indicator);
        this.colorDrawer = new ColorDrawer(paint, indicator);
        this.wormDrawer = new WormDrawer(paint, indicator);
    }

    public void drawBasic(Canvas canvas, boolean z) {
        if (this.colorDrawer != null) {
            this.basicDrawer.draw(canvas, this.position, z, this.coordinateX, this.coordinateY);
        }
    }

    public void setup(int i, int i2, int i3) {
        this.position = i;
        this.coordinateX = i2;
        this.coordinateY = i3;
    }

    public void drawWorm(Canvas canvas, Value value) {
        WormDrawer wormDrawer2 = this.wormDrawer;
        if (wormDrawer2 != null) {
            wormDrawer2.draw(canvas, value, this.coordinateX, this.coordinateY);
        }
    }
}
