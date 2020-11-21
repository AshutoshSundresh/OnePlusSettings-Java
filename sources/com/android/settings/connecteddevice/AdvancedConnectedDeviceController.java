package com.android.settings.connecteddevice;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.nfc.NfcPreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public class AdvancedConnectedDeviceController extends BasePreferenceController {
    private static final String DRIVING_MODE_SETTINGS_ENABLED = "gearhead:driving_mode_settings_enabled";
    private static final String GEARHEAD_PACKAGE = "com.google.android.projection.gearhead";

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
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

    public AdvancedConnectedDeviceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        Context context = this.mContext;
        return context.getText(getConnectedDevicesSummaryResourceId(context));
    }

    public static int getConnectedDevicesSummaryResourceId(Context context) {
        return getConnectedDevicesSummaryResourceId(new NfcPreferenceController(context, NfcPreferenceController.KEY_TOGGLE_NFC), isDrivingModeAvailable(context), isAndroidAutoSettingAvailable(context));
    }

    static boolean isDrivingModeAvailable(Context context) {
        return Settings.System.getInt(context.getContentResolver(), DRIVING_MODE_SETTINGS_ENABLED, 0) == 1;
    }

    static boolean isAndroidAutoSettingAvailable(Context context) {
        Intent intent = new Intent("com.android.settings.action.IA_SETTINGS");
        intent.setPackage(GEARHEAD_PACKAGE);
        return intent.resolveActivity(context.getPackageManager()) != null;
    }

    static int getConnectedDevicesSummaryResourceId(NfcPreferenceController nfcPreferenceController, boolean z, boolean z2) {
        if (z2) {
            if (nfcPreferenceController.isAvailable()) {
                if (z) {
                    return C0017R$string.connected_devices_dashboard_android_auto_summary;
                }
                return C0017R$string.connected_devices_dashboard_android_auto_no_driving_mode_summary;
            } else if (z) {
                return C0017R$string.connected_devices_dashboard_android_auto_no_nfc_summary;
            } else {
                return C0017R$string.connected_devices_dashboard_android_auto_no_nfc_no_driving_mode;
            }
        } else if (nfcPreferenceController.isAvailable()) {
            if (z) {
                return C0017R$string.connected_devices_dashboard_summary;
            }
            return C0017R$string.connected_devices_dashboard_no_driving_mode_summary;
        } else if (z) {
            return C0017R$string.connected_devices_dashboard_no_nfc_summary;
        } else {
            return C0017R$string.connected_devices_dashboard_no_driving_mode_no_nfc_summary;
        }
    }
}
