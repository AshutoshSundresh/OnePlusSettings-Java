package com.android.settings.wifi.tether;

import android.content.Context;
import android.content.IntentFilter;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.slices.SliceBackgroundWorker;

public class WifiTetherFooterPreferenceController extends WifiTetherBasePreferenceController {
    private static final String PREF_KEY = "tether_prefs_footer_2";

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public String getPreferenceKey() {
        return PREF_KEY;
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener, com.android.settings.wifi.tether.WifiTetherBasePreferenceController
    public boolean onPreferenceChange(Preference preference, Object obj) {
        return true;
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public WifiTetherFooterPreferenceController(Context context) {
        super(context, null, PREF_KEY);
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController
    public void updateDisplay() {
        if (this.mWifiManager.isStaApConcurrencySupported()) {
            this.mPreference.setTitle(C0017R$string.tethering_footer_info_sta_ap_concurrency);
        } else {
            this.mPreference.setTitle(C0017R$string.tethering_footer_info);
        }
    }
}
