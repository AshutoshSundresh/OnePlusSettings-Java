package com.google.android.material.indicator.draw;

import android.graphics.Canvas;
import android.util.Pair;
import android.view.MotionEvent;
import com.google.android.material.indicator.animation.data.Value;
import com.google.android.material.indicator.draw.controller.DrawController;
import com.google.android.material.indicator.draw.controller.MeasureController;
import com.google.android.material.indicator.draw.data.Indicator;

public class DrawManager {
    private DrawController drawController;
    private Indicator indicator;
    private MeasureController measureController = new MeasureController();

    public DrawManager() {
        Indicator indicator2 = new Indicator();
        this.indicator = indicator2;
        this.drawController = new DrawController(indicator2);
    }

    public Indicator indicator() {
        if (this.indicator == null) {
            this.indicator = new Indicator();
        }
        return this.indicator;
    }

    public void setClickListener(DrawController.ClickListener clickListener) {
        this.drawController.setClickListener(clickListener);
    }

    public void touch(MotionEvent motionEvent) {
        this.drawController.touch(motionEvent);
    }

    public void updateValue(Value value) {
        this.drawController.updateValue(value);
    }

    public void draw(Canvas canvas) {
        this.drawController.draw(canvas);
    }

    public Pair<Integer, Integer> measureViewSize(int i, int i2) {
        return this.measureController.measureViewSize(this.indicator, i, i2);
    }
}
