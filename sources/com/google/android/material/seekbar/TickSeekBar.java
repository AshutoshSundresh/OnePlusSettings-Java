package com.google.android.material.seekbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.SeekBar;
import com.google.android.material.R$color;
import com.google.android.material.R$dimen;
import com.google.android.material.R$styleable;
import com.google.android.material.internal.ViewUtils;
import java.util.ArrayList;
import java.util.List;

public class TickSeekBar extends SeekBar {
    private int mMaxProgress;
    private int mMeasureHeight;
    private int mPaddingLeft;
    private float mSeekBlockLength;
    private Paint mStockPaint;
    private int mTickColor;
    private List<TickData> mTickDataList;
    private int mTickDisabledColor;
    private int mTickInactiveColor;
    private float mTrackY;

    public TickSeekBar(Context context) {
        this(context, null);
    }

    public TickSeekBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initAttrs(context, attributeSet);
        initStrokePaint();
    }

    public TickSeekBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initAttrs(context, attributeSet);
        initStrokePaint();
    }

    private void initAttrs(Context context, AttributeSet attributeSet) {
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.TickSeekBar);
            this.mTickColor = obtainStyledAttributes.getColor(R$styleable.TickSeekBar_seekbarTickColor, getResources().getColor(R$color.op_seek_bar_tick_color_light));
            this.mTickInactiveColor = obtainStyledAttributes.getColor(R$styleable.TickSeekBar_seekbarInActiveTickColor, getResources().getColor(R$color.op_seek_bar_tick_color_dark));
            this.mTickDisabledColor = obtainStyledAttributes.getColor(R$styleable.TickSeekBar_seekbarTickDisabledColor, getResources().getColor(R$color.op_seek_bar_tick_disabled_color_light));
            obtainStyledAttributes.recycle();
        }
    }

    private void initStrokePaint() {
        if (this.mStockPaint == null) {
            this.mStockPaint = new Paint();
        }
        this.mStockPaint.setAntiAlias(true);
    }

    private void initSeekBarInfo() {
        int measuredWidth = getMeasuredWidth();
        this.mMeasureHeight = getMeasuredHeight();
        this.mPaddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        this.mSeekBlockLength = ((float) ((measuredWidth - this.mPaddingLeft) - paddingRight)) / ((float) this.mMaxProgress);
        this.mTrackY = (float) paddingTop;
        this.mMaxProgress = getMax();
        getPaddingLeft();
        getMeasuredWidth();
        getPaddingRight();
    }

    private void initTickLocation(List<TickData> list) {
        List<TickData> list2 = this.mTickDataList;
        if (list2 == null) {
            this.mTickDataList = new ArrayList();
        } else {
            list2.clear();
        }
        this.mTickDataList = list;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        initSeekBarInfo();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawTicks(canvas);
    }

    private void drawTicks(Canvas canvas) {
        List<TickData> list = this.mTickDataList;
        if (!(list == null || list.size() == 0)) {
            for (int i = 0; i < this.mTickDataList.size(); i++) {
                float location = (this.mSeekBlockLength * this.mTickDataList.get(i).getLocation()) + ((float) this.mPaddingLeft);
                if (i == 0) {
                    location += ViewUtils.dpToPx(getContext(), 2);
                } else if (i == this.mTickDataList.size() - 1) {
                    location -= ViewUtils.dpToPx(getContext(), 1);
                }
                float thumbPosOnTick = getThumbPosOnTick();
                if (Math.abs(thumbPosOnTick - location) > 50.0f) {
                    float dimensionPixelOffset = ((float) getResources().getDimensionPixelOffset(R$dimen.oneplus_control_tick_seekbar_radius)) / 2.0f;
                    float f = ((this.mTrackY + (((float) this.mMeasureHeight) / 2.0f)) - dimensionPixelOffset) + 0.2f;
                    if (thumbPosOnTick < location) {
                        this.mStockPaint.setColor(isEnabled() ? this.mTickInactiveColor : this.mTickDisabledColor);
                    } else {
                        this.mStockPaint.setColor(isEnabled() ? this.mTickColor : this.mTickDisabledColor);
                    }
                    canvas.drawCircle(location, f + dimensionPixelOffset, dimensionPixelOffset, this.mStockPaint);
                }
            }
        }
    }

    private float getThumbPosOnTick() {
        return (((float) getProgress()) / ((float) getMax())) * ((float) (getWidth() - getPaddingRight()));
    }

    public void setTickSegmentCount(int i) {
        List<TickData> list = this.mTickDataList;
        if (list == null) {
            this.mTickDataList = new ArrayList();
        } else {
            list.clear();
        }
        for (int i2 = 0; i2 < i + 1; i2++) {
            this.mTickDataList.add(new TickData((float) ((getMax() / i) * i2)));
        }
    }

    public void setTicks(List<TickData> list) {
        initStrokePaint();
        initTickLocation(list);
        invalidate();
    }

    public static class TickData {
        private float location;

        public TickData(float f) {
            this.location = f;
        }

        public float getLocation() {
            return this.location;
        }
    }
}
