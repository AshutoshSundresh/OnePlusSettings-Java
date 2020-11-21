package com.android.settings.fuelgauge;

import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import com.android.settings.C0006R$color;
import com.android.settings.C0007R$dimen;
import com.android.settings.C0008R$drawable;
import com.android.settingslib.graph.BatteryMeterDrawableBase;

public class BatteryMeterView extends ImageView {
    ColorFilter mAccentColorFilter;
    BatteryMeterDrawable mDrawable;
    ColorFilter mErrorColorFilter;
    ColorFilter mForegroundColorFilter;

    public BatteryMeterView(Context context) {
        this(context, null, 0);
    }

    public BatteryMeterView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public BatteryMeterView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        int color = context.getColor(C0006R$color.meter_background_color);
        this.mAccentColorFilter = new PorterDuffColorFilter(context.getColor(C0006R$color.op_control_accent_color_green), PorterDuff.Mode.SRC_IN);
        this.mErrorColorFilter = new PorterDuffColorFilter(context.getColor(C0006R$color.battery_icon_color_error), PorterDuff.Mode.SRC_IN);
        BatteryMeterDrawable batteryMeterDrawable = new BatteryMeterDrawable(context, color);
        this.mDrawable = batteryMeterDrawable;
        batteryMeterDrawable.setShowPercent(false);
        this.mDrawable.setBatteryColorFilter(this.mAccentColorFilter);
        this.mDrawable.setWarningColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.SRC_IN));
        setBackground(this.mDrawable);
    }

    public void setBatteryLevel(int i) {
        this.mDrawable.setBatteryLevel(i);
        Log.d("BatteryMeterView", "mDrawable.getCriticalLevel() = " + this.mDrawable.getCriticalLevel());
        if (i < this.mDrawable.getCriticalLevel()) {
            this.mDrawable.setBatteryColorFilter(this.mErrorColorFilter);
        } else {
            this.mDrawable.setBatteryColorFilter(this.mAccentColorFilter);
        }
    }

    public int getBatteryLevel() {
        return this.mDrawable.getBatteryLevel();
    }

    public void setCharging(boolean z) {
        Log.d("BatteryMeterView", "setCharging = " + z);
        if (z) {
            setImageResource(C0008R$drawable.ic_battery_lightning);
        } else {
            setImageResource(0);
        }
        this.mDrawable.setCharging(z);
        postInvalidate();
    }

    public boolean getCharging() {
        return this.mDrawable.getCharging();
    }

    public static class BatteryMeterDrawable extends BatteryMeterDrawableBase {
        private final int mIntrinsicHeight;
        private final int mIntrinsicWidth;

        public BatteryMeterDrawable(Context context, int i) {
            super(context, i);
            this.mIntrinsicWidth = context.getResources().getDimensionPixelSize(C0007R$dimen.battery_meter_width);
            this.mIntrinsicHeight = context.getResources().getDimensionPixelSize(C0007R$dimen.battery_meter_height);
        }

        public BatteryMeterDrawable(Context context, int i, int i2, int i3) {
            super(context, i);
            this.mIntrinsicWidth = i2;
            this.mIntrinsicHeight = i3;
        }

        @Override // com.android.settingslib.graph.BatteryMeterDrawableBase
        public int getIntrinsicWidth() {
            return this.mIntrinsicWidth;
        }

        @Override // com.android.settingslib.graph.BatteryMeterDrawableBase
        public int getIntrinsicHeight() {
            return this.mIntrinsicHeight;
        }

        public void setWarningColorFilter(ColorFilter colorFilter) {
            this.mWarningTextPaint.setColorFilter(colorFilter);
        }

        public void setBatteryColorFilter(ColorFilter colorFilter) {
            this.mFramePaint.setColorFilter(colorFilter);
            this.mBatteryPaint.setColorFilter(colorFilter);
            this.mBoltPaint.setColorFilter(colorFilter);
        }
    }
}
