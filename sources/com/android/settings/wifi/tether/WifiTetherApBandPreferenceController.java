package com.android.settings.wifi.tether;

import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.wifi.SoftApConfiguration;
import android.util.FeatureFlagUtils;
import android.util.Log;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import com.android.settings.C0003R$array;
import com.android.settings.C0017R$string;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.wifi.WifiUtils;
import com.android.settings.wifi.tether.WifiTetherBasePreferenceController;

public class WifiTetherApBandPreferenceController extends WifiTetherBasePreferenceController {
    private static final String PREF_KEY = "wifi_tether_network_ap_band";
    private static final String TAG = "WifiTetherApBandPref";
    private boolean isVendorDualApSupported;
    private String[] mBandEntries;
    private int mBandIndex;
    private String[] mBandSummaries;
    private int mSecurityType;

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

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public WifiTetherApBandPreferenceController(Context context, WifiTetherBasePreferenceController.OnTetherConfigUpdateListener onTetherConfigUpdateListener) {
        super(context, onTetherConfigUpdateListener, PREF_KEY);
        SoftApConfiguration softApConfiguration = this.mWifiManager.getSoftApConfiguration();
        this.isVendorDualApSupported = context.getResources().getBoolean(17891599);
        updatePreferenceEntries(softApConfiguration);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settings.wifi.tether.WifiTetherBasePreferenceController
    public int getAvailabilityStatus() {
        if (!WifiUtils.isSupportDualBand()) {
            return 2;
        }
        return super.getAvailabilityStatus();
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController
    public void updateDisplay() {
        SoftApConfiguration softApConfiguration = this.mWifiManager.getSoftApConfiguration();
        if (softApConfiguration == null) {
            this.mBandIndex = 1;
            Log.d(TAG, "Updating band index to BAND_2GHZ because no config");
        } else if (is5GhzBandSupported()) {
            this.mBandIndex = validateSelection(softApConfiguration);
            Log.d(TAG, "Updating band index to " + this.mBandIndex);
        } else {
            this.mWifiManager.setSoftApConfiguration(new SoftApConfiguration.Builder(softApConfiguration).setBand(1).build());
            this.mBandIndex = 1;
            Log.d(TAG, "5Ghz not supported, updating band index to 2GHz");
        }
        ListPreference listPreference = (ListPreference) this.mPreference;
        listPreference.setEntries(this.mBandSummaries);
        listPreference.setEntryValues(this.mBandEntries);
        if (!is5GhzBandSupported()) {
            listPreference.setEnabled(false);
            listPreference.setSummary(C0017R$string.wifi_ap_choose_2G);
            return;
        }
        listPreference.setEnabled(true);
        listPreference.setValue(Integer.toString(softApConfiguration.getBand()));
        listPreference.setSummary(getConfigSummary());
    }

    /* access modifiers changed from: package-private */
    public String getConfigSummary() {
        int i = this.mBandIndex;
        if (i == 1) {
            return this.mBandSummaries[0];
        }
        if (i == 2) {
            return this.mBandSummaries[1];
        }
        if (i != 8) {
            return this.mContext.getString(C0017R$string.wifi_ap_prefer_5G);
        }
        return this.mBandSummaries[2];
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public String getPreferenceKey() {
        return FeatureFlagUtils.isEnabled(this.mContext, "settings_tether_all_in_one") ? "wifi_tether_network_ap_band_2" : PREF_KEY;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener, com.android.settings.wifi.tether.WifiTetherBasePreferenceController
    public boolean onPreferenceChange(Preference preference, Object obj) {
        this.mBandIndex = validateSelection(Integer.parseInt((String) obj));
        Log.d(TAG, "Band preference changed, updating band index to " + this.mBandIndex);
        preference.setSummary(getConfigSummary());
        this.mListener.onTetherConfigUpdated(this);
        return true;
    }

    private int validateSelection(SoftApConfiguration softApConfiguration) {
        if (softApConfiguration.getBand() == 8 && softApConfiguration.getSecurityType() == 4) {
            softApConfiguration = new SoftApConfiguration.Builder(softApConfiguration).setBand(1).build();
            this.mWifiManager.setSoftApConfiguration(softApConfiguration);
            Log.d(TAG, "Dual band not supported for OWE security, updating band index to " + this.mBandIndex);
        }
        return validateSelection(softApConfiguration.getBand());
    }

    private int validateSelection(int i) {
        if (2 == i) {
            return !is5GhzBandSupported() ? 1 : 3;
        }
        return i;
    }

    public void updatePreferenceEntries(SoftApConfiguration softApConfiguration) {
        this.mSecurityType = softApConfiguration == null ? 0 : softApConfiguration.getSecurityType();
        Log.d(TAG, "updating band preferences.");
        updatePreferenceEntries();
    }

    /* access modifiers changed from: package-private */
    public void updatePreferenceEntries() {
        Resources resources = this.mContext.getResources();
        int i = C0003R$array.wifi_ap_band;
        int i2 = C0003R$array.wifi_ap_band_summary;
        if (this.isVendorDualApSupported && this.mSecurityType != 4) {
            i = C0003R$array.wifi_ap_band_vendor_config_full;
            i2 = C0003R$array.wifi_ap_band_vendor_summary_full;
        } else if (this.mSecurityType == 4) {
            i = C0003R$array.wifi_ap_band_vendor_config_no_dual;
            i2 = C0003R$array.wifi_ap_band_vendor_summary_no_dual;
        }
        this.mBandEntries = resources.getStringArray(i);
        this.mBandSummaries = resources.getStringArray(i2);
    }

    private boolean is5GhzBandSupported() {
        return this.mWifiManager.is5GHzBandSupported() && this.mWifiManager.getCountryCode() != null;
    }

    public int getBandIndex() {
        return this.mBandIndex;
    }

    public boolean isVendorDualApSupported() {
        return this.isVendorDualApSupported;
    }

    public boolean isBandEntriesHasDualband() {
        if (this.mBandEntries == null) {
            return false;
        }
        int i = 0;
        while (true) {
            String[] strArr = this.mBandEntries;
            if (i >= strArr.length) {
                return false;
            }
            if (Integer.parseInt(strArr[i]) == 8) {
                return true;
            }
            i++;
        }
    }
}
