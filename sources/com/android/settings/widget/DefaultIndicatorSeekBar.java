package com.android.settings.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class DefaultIndicatorSeekBar extends SeekBar {
    private int mDefaultProgress = -1;

    public DefaultIndicatorSeekBar(Context context) {
        super(context);
    }

    public DefaultIndicatorSeekBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public DefaultIndicatorSeekBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public DefaultIndicatorSeekBar(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    /* access modifiers changed from: protected */
    public void drawTickMarks(Canvas canvas) {
        if (isEnabled() && this.mDefaultProgress <= getMax() && this.mDefaultProgress >= getMin()) {
            Drawable tickMark = getTickMark();
            int intrinsicWidth = tickMark.getIntrinsicWidth();
            int intrinsicHeight = tickMark.getIntrinsicHeight();
            int i = 1;
            int i2 = intrinsicWidth >= 0 ? intrinsicWidth / 2 : 1;
            if (intrinsicHeight >= 0) {
                i = intrinsicHeight / 2;
            }
            tickMark.setBounds(-i2, -i, i2, i);
            int width = (getWidth() - ((SeekBar) this).mPaddingLeft) - ((SeekBar) this).mPaddingRight;
            float max = (float) (getMax() - getMin());
            float f = 0.0f;
            if (max > 0.0f) {
                f = ((float) this.mDefaultProgress) / max;
            }
            int i3 = (int) ((f * ((float) width)) + 0.5f);
            int i4 = (!isLayoutRtl() || !getMirrorForRtl()) ? ((SeekBar) this).mPaddingLeft + i3 : (width - i3) + ((SeekBar) this).mPaddingRight;
            int save = canvas.save();
            canvas.translate((float) i4, (float) (getHeight() / 2));
            tickMark.draw(canvas);
            canvas.restoreToCount(save);
        }
    }

    public void setDefaultProgress(int i) {
        if (this.mDefaultProgress != i) {
            this.mDefaultProgress = i;
            invalidate();
        }
    }

    public int getDefaultProgress() {
        return this.mDefaultProgress;
    }
}
