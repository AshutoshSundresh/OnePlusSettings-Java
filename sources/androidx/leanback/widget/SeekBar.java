package androidx.leanback.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import androidx.leanback.R$dimen;

public final class SeekBar extends View {
    private AccessibilitySeekListener mAccessibilitySeekListener;
    private int mActiveBarHeight;
    private int mActiveRadius;
    private final Paint mBackgroundPaint = new Paint(1);
    private final RectF mBackgroundRect = new RectF();
    private int mBarHeight;
    private final Paint mKnobPaint = new Paint(1);
    private int mKnobx;
    private int mMax;
    private int mProgress;
    private final Paint mProgressPaint = new Paint(1);
    private final RectF mProgressRect = new RectF();
    private int mSecondProgress;
    private final Paint mSecondProgressPaint = new Paint(1);
    private final RectF mSecondProgressRect = new RectF();

    public static abstract class AccessibilitySeekListener {
        public abstract boolean onAccessibilitySeekBackward();

        public abstract boolean onAccessibilitySeekForward();
    }

    public SeekBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setWillNotDraw(false);
        this.mBackgroundPaint.setColor(-7829368);
        this.mSecondProgressPaint.setColor(-3355444);
        this.mProgressPaint.setColor(-65536);
        this.mKnobPaint.setColor(-1);
        this.mBarHeight = context.getResources().getDimensionPixelSize(R$dimen.lb_playback_transport_progressbar_bar_height);
        this.mActiveBarHeight = context.getResources().getDimensionPixelSize(R$dimen.lb_playback_transport_progressbar_active_bar_height);
        this.mActiveRadius = context.getResources().getDimensionPixelSize(R$dimen.lb_playback_transport_progressbar_active_radius);
    }

    public void setActiveRadius(int i) {
        this.mActiveRadius = i;
        calculate();
    }

    public void setBarHeight(int i) {
        this.mBarHeight = i;
        calculate();
    }

    public void setActiveBarHeight(int i) {
        this.mActiveBarHeight = i;
        calculate();
    }

    /* access modifiers changed from: protected */
    public void onFocusChanged(boolean z, int i, Rect rect) {
        super.onFocusChanged(z, i, rect);
        calculate();
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        calculate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float f = (float) (isFocused() ? this.mActiveRadius : this.mBarHeight / 2);
        canvas.drawRoundRect(this.mBackgroundRect, f, f, this.mBackgroundPaint);
        RectF rectF = this.mSecondProgressRect;
        if (rectF.right > rectF.left) {
            canvas.drawRoundRect(rectF, f, f, this.mSecondProgressPaint);
        }
        canvas.drawRoundRect(this.mProgressRect, f, f, this.mProgressPaint);
        canvas.drawCircle((float) this.mKnobx, (float) (getHeight() / 2), f, this.mKnobPaint);
    }

    public void setProgress(int i) {
        int i2 = this.mMax;
        if (i > i2) {
            i = i2;
        } else if (i < 0) {
            i = 0;
        }
        this.mProgress = i;
        calculate();
    }

    public void setSecondaryProgress(int i) {
        int i2 = this.mMax;
        if (i > i2) {
            i = i2;
        } else if (i < 0) {
            i = 0;
        }
        this.mSecondProgress = i;
        calculate();
    }

    public int getProgress() {
        return this.mProgress;
    }

    public int getSecondProgress() {
        return this.mSecondProgress;
    }

    public int getMax() {
        return this.mMax;
    }

    public void setMax(int i) {
        this.mMax = i;
        calculate();
    }

    public void setProgressColor(int i) {
        this.mProgressPaint.setColor(i);
    }

    public void setSecondaryProgressColor(int i) {
        this.mSecondProgressPaint.setColor(i);
    }

    public int getSecondaryProgressColor() {
        return this.mSecondProgressPaint.getColor();
    }

    private void calculate() {
        int i = isFocused() ? this.mActiveBarHeight : this.mBarHeight;
        int width = getWidth();
        int height = getHeight();
        int i2 = (height - i) / 2;
        RectF rectF = this.mBackgroundRect;
        int i3 = this.mBarHeight;
        float f = (float) i2;
        float f2 = (float) (height - i2);
        rectF.set((float) (i3 / 2), f, (float) (width - (i3 / 2)), f2);
        int i4 = isFocused() ? this.mActiveRadius : this.mBarHeight / 2;
        float f3 = (float) (width - (i4 * 2));
        float f4 = (((float) this.mProgress) / ((float) this.mMax)) * f3;
        RectF rectF2 = this.mProgressRect;
        int i5 = this.mBarHeight;
        rectF2.set((float) (i5 / 2), f, ((float) (i5 / 2)) + f4, f2);
        this.mSecondProgressRect.set(this.mProgressRect.right, f, ((float) (this.mBarHeight / 2)) + ((((float) this.mSecondProgress) / ((float) this.mMax)) * f3), f2);
        this.mKnobx = i4 + ((int) f4);
        invalidate();
    }

    public CharSequence getAccessibilityClassName() {
        return android.widget.SeekBar.class.getName();
    }

    public void setAccessibilitySeekListener(AccessibilitySeekListener accessibilitySeekListener) {
        this.mAccessibilitySeekListener = accessibilitySeekListener;
    }

    public boolean performAccessibilityAction(int i, Bundle bundle) {
        AccessibilitySeekListener accessibilitySeekListener = this.mAccessibilitySeekListener;
        if (accessibilitySeekListener != null) {
            if (i == 4096) {
                return accessibilitySeekListener.onAccessibilitySeekForward();
            }
            if (i == 8192) {
                return accessibilitySeekListener.onAccessibilitySeekBackward();
            }
        }
        return super.performAccessibilityAction(i, bundle);
    }
}
