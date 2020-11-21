package com.android.settings.accessibility;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.provider.Settings;
import android.util.AttributeSet;
import android.widget.SeekBar;
import com.android.settings.C0006R$color;
import com.android.settings.C0007R$dimen;

public class BalanceSeekBar extends SeekBar {
    static final float SNAP_TO_PERCENTAGE = 0.03f;
    private int mCenter;
    private final Paint mCenterMarkerPaint;
    private final Rect mCenterMarkerRect;
    private final Context mContext;
    private final Object mListenerLock;
    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener;
    private final SeekBar.OnSeekBarChangeListener mProxySeekBarListener;
    private float mSnapThreshold;

    public BalanceSeekBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 16842875);
    }

    public BalanceSeekBar(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public BalanceSeekBar(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mListenerLock = new Object();
        this.mProxySeekBarListener = new SeekBar.OnSeekBarChangeListener() {
            /* class com.android.settings.accessibility.BalanceSeekBar.AnonymousClass1 */

            public void onStopTrackingTouch(SeekBar seekBar) {
                synchronized (BalanceSeekBar.this.mListenerLock) {
                    if (BalanceSeekBar.this.mOnSeekBarChangeListener != null) {
                        BalanceSeekBar.this.mOnSeekBarChangeListener.onStopTrackingTouch(seekBar);
                    }
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                synchronized (BalanceSeekBar.this.mListenerLock) {
                    if (BalanceSeekBar.this.mOnSeekBarChangeListener != null) {
                        BalanceSeekBar.this.mOnSeekBarChangeListener.onStartTrackingTouch(seekBar);
                    }
                }
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                if (z) {
                    if (i != BalanceSeekBar.this.mCenter) {
                        float f = (float) i;
                        if (f > ((float) BalanceSeekBar.this.mCenter) - BalanceSeekBar.this.mSnapThreshold && f < ((float) BalanceSeekBar.this.mCenter) + BalanceSeekBar.this.mSnapThreshold) {
                            i = BalanceSeekBar.this.mCenter;
                            seekBar.setProgress(i);
                        }
                    }
                    Settings.System.putFloatForUser(BalanceSeekBar.this.mContext.getContentResolver(), "master_balance", ((float) (i - BalanceSeekBar.this.mCenter)) * 0.01f, -2);
                }
                synchronized (BalanceSeekBar.this.mListenerLock) {
                    if (BalanceSeekBar.this.mOnSeekBarChangeListener != null) {
                        BalanceSeekBar.this.mOnSeekBarChangeListener.onProgressChanged(seekBar, i, z);
                    }
                }
            }
        };
        this.mContext = context;
        Resources resources = getResources();
        this.mCenterMarkerRect = new Rect(0, 0, resources.getDimensionPixelSize(C0007R$dimen.balance_seekbar_center_marker_width), resources.getDimensionPixelSize(C0007R$dimen.balance_seekbar_center_marker_height));
        Paint paint = new Paint();
        this.mCenterMarkerPaint = paint;
        paint.setColor(getResources().getColor(C0006R$color.op_control_icon_color_disable));
        this.mCenterMarkerPaint.setStyle(Paint.Style.FILL);
        setProgressTintList(ColorStateList.valueOf(0));
        super.setOnSeekBarChangeListener(this.mProxySeekBarListener);
    }

    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener onSeekBarChangeListener) {
        synchronized (this.mListenerLock) {
            this.mOnSeekBarChangeListener = onSeekBarChangeListener;
        }
    }

    public synchronized void setMax(int i) {
        super.setMax(i);
        this.mCenter = i / 2;
        this.mSnapThreshold = ((float) i) * SNAP_TO_PERCENTAGE;
    }

    /* access modifiers changed from: protected */
    public synchronized void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate((float) ((canvas.getWidth() - this.mCenterMarkerRect.right) / 2), (float) (((canvas.getHeight() - getPaddingBottom()) / 2) - (this.mCenterMarkerRect.bottom / 2)));
        canvas.drawRect(this.mCenterMarkerRect, this.mCenterMarkerPaint);
        canvas.restore();
        super.onDraw(canvas);
    }

    /* access modifiers changed from: package-private */
    public SeekBar.OnSeekBarChangeListener getProxySeekBarListener() {
        return this.mProxySeekBarListener;
    }
}
