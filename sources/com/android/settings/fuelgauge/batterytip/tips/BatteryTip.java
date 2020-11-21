package com.android.settings.fuelgauge.batterytip.tips;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseIntArray;
import androidx.preference.Preference;
import com.android.settings.C0012R$layout;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;

public abstract class BatteryTip implements Comparable<BatteryTip>, Parcelable {
    static final SparseIntArray TIP_ORDER;
    protected boolean mNeedUpdate;
    protected boolean mShowDialog;
    protected int mState;
    protected int mType;

    public int describeContents() {
        return 0;
    }

    public abstract int getIconId();

    public int getIconTintColorId() {
        return -1;
    }

    public abstract CharSequence getSummary(Context context);

    public abstract CharSequence getTitle(Context context);

    public abstract void log(Context context, MetricsFeatureProvider metricsFeatureProvider);

    public void sanityCheck(Context context) {
    }

    public abstract void updateState(BatteryTip batteryTip);

    static {
        SparseIntArray sparseIntArray = new SparseIntArray();
        TIP_ORDER = sparseIntArray;
        sparseIntArray.append(1, 0);
        TIP_ORDER.append(3, 1);
        TIP_ORDER.append(2, 2);
        TIP_ORDER.append(5, 3);
        TIP_ORDER.append(6, 4);
        TIP_ORDER.append(0, 5);
        TIP_ORDER.append(4, 6);
        TIP_ORDER.append(7, 7);
    }

    BatteryTip(Parcel parcel) {
        this.mType = parcel.readInt();
        this.mState = parcel.readInt();
        this.mShowDialog = parcel.readBoolean();
        this.mNeedUpdate = parcel.readBoolean();
    }

    BatteryTip(int i, int i2, boolean z) {
        this.mType = i;
        this.mState = i2;
        this.mShowDialog = z;
        this.mNeedUpdate = true;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mType);
        parcel.writeInt(this.mState);
        parcel.writeBoolean(this.mShowDialog);
        parcel.writeBoolean(this.mNeedUpdate);
    }

    public void updatePreference(Preference preference) {
        Context context = preference.getContext();
        preference.setLayoutResource(C0012R$layout.op_battery_tips_preference);
        preference.setTitle(getTitle(context));
        preference.setSummary(getSummary(context));
    }

    public boolean shouldShowDialog() {
        return this.mShowDialog;
    }

    public boolean needUpdate() {
        return this.mNeedUpdate;
    }

    public int getType() {
        return this.mType;
    }

    public int getState() {
        return this.mState;
    }

    public int compareTo(BatteryTip batteryTip) {
        return TIP_ORDER.get(this.mType) - TIP_ORDER.get(batteryTip.mType);
    }

    public String toString() {
        return "type=" + this.mType + " state=" + this.mState;
    }
}
