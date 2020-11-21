package com.android.settings.fuelgauge.batterytip.tips;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.settings.C0017R$string;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;

public class LowBatteryTip extends EarlyWarningTip {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        /* class com.android.settings.fuelgauge.batterytip.tips.LowBatteryTip.AnonymousClass1 */

        @Override // android.os.Parcelable.Creator
        public BatteryTip createFromParcel(Parcel parcel) {
            return new LowBatteryTip(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public BatteryTip[] newArray(int i) {
            return new LowBatteryTip[i];
        }
    };
    private CharSequence mSummary;

    public LowBatteryTip(int i, boolean z, CharSequence charSequence) {
        super(i, z);
        this.mType = 5;
        this.mSummary = charSequence;
    }

    public LowBatteryTip(Parcel parcel) {
        super(parcel);
        this.mSummary = parcel.readCharSequence();
    }

    @Override // com.android.settings.fuelgauge.batterytip.tips.EarlyWarningTip, com.android.settings.fuelgauge.batterytip.tips.BatteryTip
    public CharSequence getSummary(Context context) {
        if (this.mState == 1) {
            return context.getString(C0017R$string.battery_tip_early_heads_up_done_summary);
        }
        return this.mSummary;
    }

    @Override // com.android.settings.fuelgauge.batterytip.tips.EarlyWarningTip, com.android.settings.fuelgauge.batterytip.tips.BatteryTip
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeCharSequence(this.mSummary);
    }

    @Override // com.android.settings.fuelgauge.batterytip.tips.EarlyWarningTip, com.android.settings.fuelgauge.batterytip.tips.BatteryTip
    public void log(Context context, MetricsFeatureProvider metricsFeatureProvider) {
        metricsFeatureProvider.action(context, 1352, this.mState);
    }
}
