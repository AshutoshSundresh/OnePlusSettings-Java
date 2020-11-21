package com.google.android.material.indicator.animation.type;

import android.animation.Animator;
import android.animation.ValueAnimator;
import com.google.android.material.indicator.animation.controller.ValueController;

public abstract class BaseAnimation<T extends Animator> {
    protected long animationDuration = 125;
    protected T animator;
    protected ValueController.UpdateListener listener;

    public abstract T createAnimator();

    public abstract BaseAnimation progress(float f);

    public BaseAnimation(ValueController.UpdateListener updateListener) {
        this.listener = updateListener;
        this.animator = createAnimator();
    }

    public BaseAnimation duration(long j) {
        this.animationDuration = j;
        T t = this.animator;
        if (t instanceof ValueAnimator) {
            t.setDuration(j);
        }
        return this;
    }

    public void start() {
        T t = this.animator;
        if (t != null && !t.isRunning()) {
            this.animator.start();
        }
    }

    public void end() {
        T t = this.animator;
        if (t != null && t.isStarted()) {
            this.animator.end();
        }
    }
}
