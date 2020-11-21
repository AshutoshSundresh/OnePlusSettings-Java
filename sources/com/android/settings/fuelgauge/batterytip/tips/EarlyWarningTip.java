package com.android.settings.fuelgauge.batterytip.tips;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.settings.C0006R$color;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;

public class EarlyWarningTip extends BatteryTip {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        /* class com.android.settings.fuelgauge.batterytip.tips.EarlyWarningTip.AnonymousClass1 */

        @Override // android.os.Parcelable.Creator
        public BatteryTip createFromParcel(Parcel parcel) {
            return new EarlyWarningTip(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public BatteryTip[] newArray(int i) {
            return new EarlyWarningTip[i];
        }
    };
    private boolean mPowerSaveModeOn;

    public EarlyWarningTip(int i, boolean z) {
        super(3, i, false);
        this.mPowerSaveModeOn = z;
    }

    public EarlyWarningTip(Parcel parcel) {
        super(parcel);
        this.mPowerSaveModeOn = parcel.readBoolean();
    }

    @Override // com.android.settings.fuelgauge.batterytip.tips.BatteryTip
    public CharSequence getTitle(Context context) {
        int i;
        if (this.mState == 1) {
            i = C0017R$string.battery_tip_early_heads_up_done_title;
        } else {
            i = C0017R$string.battery_tip_early_heads_up_title;
        }
        return context.getString(i);
    }

    @Override // com.android.settings.fuelgauge.batterytip.tips.BatteryTip
    public CharSequence getSummary(Context context) {
        int i;
        if (this.mState == 1) {
            i = C0017R$string.battery_tip_early_heads_up_done_summary;
        } else {
            i = C0017R$string.battery_tip_early_heads_up_summary;
        }
        return context.getString(i);
    }

    @Override // com.android.settings.fuelgauge.batterytip.tips.BatteryTip
    public int getIconId() {
        if (this.mState == 1) {
            return C0008R$drawable.ic_battery_status_maybe_24dp;
        }
        return C0008R$drawable.ic_battery_status_bad_24dp;
    }

    @Override // com.android.settings.fuelgauge.batterytip.tips.BatteryTip
    public int getIconTintColorId() {
        if (this.mState == 1) {
            return C0006R$color.battery_maybe_color_light;
        }
        return C0006R$color.battery_bad_color_light;
    }

    @Override // com.android.settings.fuelgauge.batterytip.tips.BatteryTip
    public void updateState(BatteryTip batteryTip) {
        EarlyWarningTip earlyWarningTip = (EarlyWarningTip) batteryTip;
        int i = earlyWarningTip.mState;
        if (i == 0) {
            this.mState = 0;
        } else {
            if (this.mState == 0) {
                int i2 = 2;
                if (i == 2) {
                    if (earlyWarningTip.mPowerSaveModeOn) {
                        i2 = 1;
                    }
                    this.mState = i2;
                }
            }
            this.mState = earlyWarningTip.getState();
        }
        this.mPowerSaveModeOn = earlyWarningTip.mPowerSaveModeOn;
    }

    @Override // com.android.settings.fuelgauge.batterytip.tips.BatteryTip
    public void log(Context context, MetricsFeatureProvider metricsFeatureProvider) {
        metricsFeatureProvider.action(context, 1351, this.mState);
    }

    @Override // com.android.settings.fuelgauge.batterytip.tips.BatteryTip
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeBoolean(this.mPowerSaveModeOn);
    }
}
