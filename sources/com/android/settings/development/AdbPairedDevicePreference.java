package com.android.settings.development;

import android.content.Context;
import android.debug.PairDevice;
import android.os.Bundle;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;

public class AdbPairedDevicePreference extends Preference {
    private PairDevice mPairedDevice;

    public AdbPairedDevicePreference(PairDevice pairDevice, Context context) {
        super(context);
        this.mPairedDevice = pairDevice;
        setWidgetLayoutResource(getWidgetLayoutResourceId());
        refresh();
    }

    /* access modifiers changed from: protected */
    public int getWidgetLayoutResourceId() {
        return C0012R$layout.preference_widget_gear_optional_background;
    }

    public void refresh() {
        setTitle(this, this.mPairedDevice);
    }

    public void setPairedDevice(PairDevice pairDevice) {
        this.mPairedDevice = pairDevice;
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View findViewById = preferenceViewHolder.findViewById(C0010R$id.settings_button);
        View findViewById2 = preferenceViewHolder.findViewById(C0010R$id.settings_button_no_background);
        findViewById.setVisibility(4);
        findViewById2.setVisibility(0);
    }

    static void setTitle(AdbPairedDevicePreference adbPairedDevicePreference, PairDevice pairDevice) {
        adbPairedDevicePreference.setTitle(pairDevice.getDeviceName());
        adbPairedDevicePreference.setSummary(pairDevice.isConnected() ? adbPairedDevicePreference.getContext().getText(C0017R$string.adb_wireless_device_connected_summary) : "");
    }

    public void savePairedDeviceToExtras(Bundle bundle) {
        bundle.putParcelable("paired_device", this.mPairedDevice);
    }
}
