package com.android.settings.fuelgauge.batterytip.tips;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.settings.C0006R$color;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;

public class SummaryTip extends BatteryTip {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        /* class com.android.settings.fuelgauge.batterytip.tips.SummaryTip.AnonymousClass1 */

        @Override // android.os.Parcelable.Creator
        public BatteryTip createFromParcel(Parcel parcel) {
            return new SummaryTip(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public BatteryTip[] newArray(int i) {
            return new SummaryTip[i];
        }
    };
    private long mAverageTimeMs;

    public SummaryTip(int i, long j) {
        super(6, i, true);
        this.mAverageTimeMs = j;
    }

    SummaryTip(Parcel parcel) {
        super(parcel);
        this.mAverageTimeMs = parcel.readLong();
    }

    @Override // com.android.settings.fuelgauge.batterytip.tips.BatteryTip
    public CharSequence getTitle(Context context) {
        return context.getString(C0017R$string.battery_tip_summary_title);
    }

    @Override // com.android.settings.fuelgauge.batterytip.tips.BatteryTip
    public CharSequence getSummary(Context context) {
        return context.getString(C0017R$string.battery_tip_summary_summary);
    }

    @Override // com.android.settings.fuelgauge.batterytip.tips.BatteryTip
    public int getIconId() {
        return C0008R$drawable.ic_battery_status_good_24dp;
    }

    @Override // com.android.settings.fuelgauge.batterytip.tips.BatteryTip
    public int getIconTintColorId() {
        return C0006R$color.battery_good_color_light;
    }

    @Override // com.android.settings.fuelgauge.batterytip.tips.BatteryTip
    public void updateState(BatteryTip batteryTip) {
        this.mState = batteryTip.mState;
    }

    @Override // com.android.settings.fuelgauge.batterytip.tips.BatteryTip
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeLong(this.mAverageTimeMs);
    }

    @Override // com.android.settings.fuelgauge.batterytip.tips.BatteryTip
    public void log(Context context, MetricsFeatureProvider metricsFeatureProvider) {
        metricsFeatureProvider.action(context, 1349, this.mState);
    }
}
