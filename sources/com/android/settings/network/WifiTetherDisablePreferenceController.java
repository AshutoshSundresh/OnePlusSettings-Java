package com.android.settings.network;

import android.content.Context;
import android.content.IntentFilter;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.Utils;
import com.android.settings.slices.SliceBackgroundWorker;

public final class WifiTetherDisablePreferenceController extends TetherBasePreferenceController {
    private static final String TAG = "WifiTetherDisablePreferenceController";
    private PreferenceScreen mScreen;

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.network.TetherBasePreferenceController
    public int getTetherType() {
        return 0;
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.network.TetherBasePreferenceController
    public boolean shouldEnable() {
        return true;
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public WifiTetherDisablePreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return !super.isChecked();
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        return super.setChecked(!z);
    }

    private int getTetheringStateOfOtherInterfaces() {
        return this.mTetheringState & -2;
    }

    @Override // com.android.settings.network.TetherBasePreferenceController
    public boolean shouldShow() {
        String[] tetherableWifiRegexs = this.mCm.getTetherableWifiRegexs();
        return (tetherableWifiRegexs == null || tetherableWifiRegexs.length == 0 || Utils.isMonkeyRunning() || getTetheringStateOfOtherInterfaces() == 0) ? false : true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        int tetheringStateOfOtherInterfaces = getTetheringStateOfOtherInterfaces();
        if (tetheringStateOfOtherInterfaces == 2) {
            return this.mContext.getString(C0017R$string.disable_wifi_hotspot_when_usb_on);
        }
        if (tetheringStateOfOtherInterfaces == 4) {
            return this.mContext.getString(C0017R$string.disable_wifi_hotspot_when_bluetooth_on);
        }
        if (tetheringStateOfOtherInterfaces == 6) {
            return this.mContext.getString(C0017R$string.disable_wifi_hotspot_when_usb_and_bluetooth_on);
        }
        if (tetheringStateOfOtherInterfaces == 32) {
            return this.mContext.getString(C0017R$string.disable_wifi_hotspot_when_ethernet_on);
        }
        if (tetheringStateOfOtherInterfaces == 34) {
            return this.mContext.getString(C0017R$string.disable_wifi_hotspot_when_usb_and_ethernet_on);
        }
        if (tetheringStateOfOtherInterfaces == 36) {
            return this.mContext.getString(C0017R$string.disable_wifi_hotspot_when_bluetooth_and_ethernet_on);
        }
        if (tetheringStateOfOtherInterfaces != 38) {
            return this.mContext.getString(C0017R$string.summary_placeholder);
        }
        return this.mContext.getString(C0017R$string.disable_wifi_hotspot_when_usb_and_bluetooth_and_ethernet_on);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController, com.android.settings.network.TetherBasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mScreen = preferenceScreen;
        Preference preference = this.mPreference;
        if (preference != null) {
            preference.setOnPreferenceChangeListener(this);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        preference.setVisible(isAvailable());
        refreshSummary(preference);
    }
}
