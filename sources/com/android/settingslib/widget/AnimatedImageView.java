package com.android.settingslib.widget;

import android.content.Context;
import android.graphics.drawable.AnimatedRotateDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class AnimatedImageView extends ImageView {
    private boolean mAnimating;
    private AnimatedRotateDrawable mDrawable;

    public AnimatedImageView(Context context) {
        super(context);
    }

    public AnimatedImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    private void updateDrawable() {
        AnimatedRotateDrawable animatedRotateDrawable;
        if (isShown() && (animatedRotateDrawable = this.mDrawable) != null) {
            animatedRotateDrawable.stop();
        }
        AnimatedRotateDrawable drawable = getDrawable();
        if (drawable instanceof AnimatedRotateDrawable) {
            AnimatedRotateDrawable animatedRotateDrawable2 = drawable;
            this.mDrawable = animatedRotateDrawable2;
            animatedRotateDrawable2.setFramesCount(56);
            this.mDrawable.setFramesDuration(32);
            if (isShown() && this.mAnimating) {
                this.mDrawable.start();
                return;
            }
            return;
        }
        this.mDrawable = null;
    }

    private void updateAnimating() {
        if (this.mDrawable == null) {
            return;
        }
        if (getVisibility() != 0 || !this.mAnimating) {
            this.mDrawable.stop();
        } else {
            this.mDrawable.start();
        }
    }

    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        updateDrawable();
    }

    public void setImageResource(int i) {
        super.setImageResource(i);
        updateDrawable();
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateAnimating();
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        updateAnimating();
    }

    public void setAnimating(boolean z) {
        this.mAnimating = z;
        updateAnimating();
    }

    /* access modifiers changed from: protected */
    public void onVisibilityChanged(View view, int i) {
        super.onVisibilityChanged(view, i);
        updateAnimating();
    }
}
