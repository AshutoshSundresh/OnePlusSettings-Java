package com.android.settings.fuelgauge.batterytip.tips;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.settings.fuelgauge.batterytip.AppInfo;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;

public class UnrestrictAppTip extends BatteryTip {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        /* class com.android.settings.fuelgauge.batterytip.tips.UnrestrictAppTip.AnonymousClass1 */

        @Override // android.os.Parcelable.Creator
        public BatteryTip createFromParcel(Parcel parcel) {
            return new UnrestrictAppTip(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public BatteryTip[] newArray(int i) {
            return new UnrestrictAppTip[i];
        }
    };
    private AppInfo mAppInfo;

    @Override // com.android.settings.fuelgauge.batterytip.tips.BatteryTip
    public int getIconId() {
        return 0;
    }

    @Override // com.android.settings.fuelgauge.batterytip.tips.BatteryTip
    public CharSequence getSummary(Context context) {
        return null;
    }

    @Override // com.android.settings.fuelgauge.batterytip.tips.BatteryTip
    public CharSequence getTitle(Context context) {
        return null;
    }

    @Override // com.android.settings.fuelgauge.batterytip.tips.BatteryTip
    public void log(Context context, MetricsFeatureProvider metricsFeatureProvider) {
    }

    public UnrestrictAppTip(int i, AppInfo appInfo) {
        super(7, i, true);
        this.mAppInfo = appInfo;
    }

    UnrestrictAppTip(Parcel parcel) {
        super(parcel);
        this.mAppInfo = (AppInfo) parcel.readParcelable(UnrestrictAppTip.class.getClassLoader());
    }

    public String getPackageName() {
        return this.mAppInfo.packageName;
    }

    @Override // com.android.settings.fuelgauge.batterytip.tips.BatteryTip
    public void updateState(BatteryTip batteryTip) {
        this.mState = batteryTip.mState;
    }

    public AppInfo getUnrestrictAppInfo() {
        return this.mAppInfo;
    }

    @Override // com.android.settings.fuelgauge.batterytip.tips.BatteryTip
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeParcelable(this.mAppInfo, i);
    }
}
