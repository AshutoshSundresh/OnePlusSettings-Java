package com.android.settings.location;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import com.android.settings.C0005R$bool;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public class LocationScanningPreferenceController extends BasePreferenceController {
    private final WifiManager mWifiManager;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public LocationScanningPreferenceController(Context context, String str) {
        super(context, str);
        this.mWifiManager = (WifiManager) context.getSystemService(WifiManager.class);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        int i;
        boolean isScanAlwaysAvailable = this.mWifiManager.isScanAlwaysAvailable();
        boolean z = false;
        if (Settings.Global.getInt(this.mContext.getContentResolver(), "ble_scan_always_enabled", 0) == 1) {
            z = true;
        }
        if (isScanAlwaysAvailable && z) {
            i = C0017R$string.scanning_status_text_wifi_on_ble_on;
        } else if (isScanAlwaysAvailable && !z) {
            i = C0017R$string.scanning_status_text_wifi_on_ble_off;
        } else if (isScanAlwaysAvailable || !z) {
            i = C0017R$string.scanning_status_text_wifi_off_ble_off;
        } else {
            i = C0017R$string.scanning_status_text_wifi_off_ble_on;
        }
        return this.mContext.getString(i);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mContext.getResources().getBoolean(C0005R$bool.config_show_location_scanning) ? 0 : 3;
    }
}
