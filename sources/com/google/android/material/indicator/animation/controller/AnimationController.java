package com.google.android.material.indicator.animation.controller;

import com.google.android.material.indicator.animation.controller.ValueController;
import com.google.android.material.indicator.animation.type.BaseAnimation;
import com.google.android.material.indicator.animation.type.WormAnimation;
import com.google.android.material.indicator.draw.data.Indicator;
import com.google.android.material.indicator.utils.CoordinatesUtils;

public class AnimationController {
    private Indicator indicator;
    private boolean isInteractive;
    private float progress;
    private BaseAnimation runningAnimation;
    private ValueController valueController;

    public AnimationController(Indicator indicator2, ValueController.UpdateListener updateListener) {
        this.valueController = new ValueController(updateListener);
        this.indicator = indicator2;
    }

    public void interactive(float f) {
        this.isInteractive = true;
        this.progress = f;
        animate();
    }

    public void basic() {
        this.isInteractive = false;
        this.progress = 0.0f;
        animate();
    }

    public void end() {
        BaseAnimation baseAnimation = this.runningAnimation;
        if (baseAnimation != null) {
            baseAnimation.end();
        }
    }

    private void animate() {
        wormAnimation();
    }

    private void wormAnimation() {
        int selectedPosition = this.indicator.isInteractiveAnimation() ? this.indicator.getSelectedPosition() : this.indicator.getLastSelectedPosition();
        int selectingPosition = this.indicator.isInteractiveAnimation() ? this.indicator.getSelectingPosition() : this.indicator.getSelectedPosition();
        int coordinate = CoordinatesUtils.getCoordinate(this.indicator, selectedPosition);
        int coordinate2 = CoordinatesUtils.getCoordinate(this.indicator, selectingPosition);
        boolean z = selectingPosition > selectedPosition;
        int radius = this.indicator.getRadius();
        long animationDuration = this.indicator.getAnimationDuration();
        WormAnimation worm = this.valueController.worm();
        worm.with(coordinate, coordinate2, radius, z);
        worm.duration(animationDuration);
        if (this.isInteractive) {
            worm.progress(this.progress);
        } else {
            worm.start();
        }
        this.runningAnimation = worm;
    }
}
