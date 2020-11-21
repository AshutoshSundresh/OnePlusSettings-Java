package com.android.settings.wifi.details;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.wifi.dpp.WifiDppUtils;
import com.android.settingslib.wifi.AccessPoint;

public class AddDevicePreferenceController extends BasePreferenceController {
    private static final String KEY_ADD_DEVICE = "add_device_to_network";
    private static final String TAG = "AddDevicePreferenceController";
    private AccessPoint mAccessPoint;
    private WifiManager mWifiManager;

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

    public AddDevicePreferenceController(Context context) {
        super(context, KEY_ADD_DEVICE);
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
    }

    public AddDevicePreferenceController init(AccessPoint accessPoint) {
        this.mAccessPoint = accessPoint;
        return this;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return WifiDppUtils.isSupportConfiguratorQrCodeScanner(this.mContext, this.mAccessPoint) ? 0 : 2;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!KEY_ADD_DEVICE.equals(preference.getKey())) {
            return false;
        }
        WifiDppUtils.showLockScreen(this.mContext, new Runnable() {
            /* class com.android.settings.wifi.details.$$Lambda$AddDevicePreferenceController$O4Vr5ZnezxjC8N10Otd21gQHFos */

            public final void run() {
                AddDevicePreferenceController.this.lambda$handlePreferenceTreeClick$0$AddDevicePreferenceController();
            }
        });
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: launchWifiDppConfiguratorQrCodeScanner */
    public void lambda$handlePreferenceTreeClick$0() {
        Intent configuratorQrCodeScannerIntentOrNull = WifiDppUtils.getConfiguratorQrCodeScannerIntentOrNull(this.mContext, this.mWifiManager, this.mAccessPoint);
        if (configuratorQrCodeScannerIntentOrNull == null) {
            Log.e(TAG, "Launch Wi-Fi QR code scanner with a wrong Wi-Fi network!");
        } else {
            this.mContext.startActivity(configuratorQrCodeScannerIntentOrNull);
        }
    }
}
