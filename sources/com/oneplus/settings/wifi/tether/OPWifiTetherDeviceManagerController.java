package com.oneplus.settings.wifi.tether;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.wifi.tether.WifiTetherBasePreferenceController;
import com.oneplus.settings.utils.OPUtils;

public class OPWifiTetherDeviceManagerController extends WifiTetherBasePreferenceController {
    public static final String PREF_KEY = "connected_device_manager";
    private static final String TAG = "OPWifiTetherDeviceManagerController";

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

    public OPWifiTetherDeviceManagerController(Context context, WifiTetherBasePreferenceController.OnTetherConfigUpdateListener onTetherConfigUpdateListener) {
        super(context, onTetherConfigUpdateListener, PREF_KEY);
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController
    public void updateDisplay() {
        if (this.mPreference != null) {
            Log.d(TAG, "mPreference != null");
            boolean isAvailable = isAvailable();
            Log.d(TAG, "available = " + isAvailable);
            this.mPreference.setVisible(isAvailable);
        }
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settings.wifi.tether.WifiTetherBasePreferenceController
    public int getAvailabilityStatus() {
        boolean isAppPakExist = OPUtils.isAppPakExist(this.mContext, "com.oneplus.wifiapsettings");
        Log.d(TAG, "isAppPakExist = " + isAppPakExist);
        WifiManager wifiManager = this.mWifiManager;
        boolean z = true;
        boolean z2 = wifiManager != null && wifiManager.getWifiApState() == 13;
        Log.d(TAG, "wifiStateEnabled = " + z2);
        if (!isAppPakExist || !z2) {
            z = false;
        }
        if (z) {
            return 0;
        }
        return 2;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!PREF_KEY.equals(preference.getKey())) {
            return false;
        }
        if (!OPUtils.isAppPakExist(this.mContext, "com.oneplus.wifiapsettings")) {
            return true;
        }
        try {
            this.mContext.startActivity(new Intent("android.oem.intent.action.OPWIFIAP_SETTINGS"));
            return true;
        } catch (ActivityNotFoundException e) {
            Log.d(TAG, "Start intent : android.oem.intent.action.OPWIFIAP_SETTINGS fail." + e);
            return true;
        }
    }
}
