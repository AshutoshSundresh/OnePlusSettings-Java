package com.android.settings.connecteddevice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.bluetooth.OPBluetoothCarKitDeviceUpdater;
import com.android.settings.bluetooth.OPPairedBluetoothDeviceUpdater;
import com.android.settings.bluetooth.OPRecognizedBluetoothCarKitDeviceUpdater;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;

public class OPRecognizedBluetoothCarKitNoDevicesPreferenceController extends BasePreferenceController implements LifecycleObserver, OnStart, OnStop, DevicePreferenceCallback {
    static final String KEY_RECOGNIZED_BLUETOOTH_CAR_KITS_NO_DEVICES = "recognized_bluetooth_car_kits_no_devices";
    private OPBluetoothCarKitDeviceUpdater mOPBluetoothCarKitDeviceUpdater;
    private Preference mPreference;
    private int mPreferenceSize;
    private BroadcastReceiver mStatusReceive = new BroadcastReceiver() {
        /* class com.android.settings.connecteddevice.OPRecognizedBluetoothCarKitNoDevicesPreferenceController.AnonymousClass1 */

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (((action.hashCode() == 140676821 && action.equals("oneplus.action.intent.UpdateBluetoothCarkitDevice")) ? (char) 0 : 65535) == 0) {
                OPRecognizedBluetoothCarKitNoDevicesPreferenceController.this.mOPBluetoothCarKitDeviceUpdater.forceUpdate();
            }
        }
    };

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

    public OPRecognizedBluetoothCarKitNoDevicesPreferenceController(Context context, String str) {
        super(context, KEY_RECOGNIZED_BLUETOOTH_CAR_KITS_NO_DEVICES);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mContext.getPackageManager().hasSystemFeature("android.hardware.bluetooth") ? 0 : 2;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (isAvailable()) {
            this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
            this.mOPBluetoothCarKitDeviceUpdater.setPrefContext(preferenceScreen.getContext());
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mOPBluetoothCarKitDeviceUpdater.registerCallback();
        updatePreferenceOnSizeChanged();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("oneplus.action.intent.UpdateBluetoothCarkitDevice");
        this.mContext.registerReceiver(this.mStatusReceive, intentFilter);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mOPBluetoothCarKitDeviceUpdater.unregisterCallback();
        this.mContext.unregisterReceiver(this.mStatusReceive);
    }

    public void init(DashboardFragment dashboardFragment) {
        this.mOPBluetoothCarKitDeviceUpdater = new OPRecognizedBluetoothCarKitDeviceUpdater(dashboardFragment.getContext(), dashboardFragment, this);
    }

    @Override // com.android.settings.connecteddevice.DevicePreferenceCallback
    public void onDeviceAdded(Preference preference) {
        this.mPreferenceSize++;
        updatePreferenceOnSizeChanged();
    }

    @Override // com.android.settings.connecteddevice.DevicePreferenceCallback
    public void onDeviceRemoved(Preference preference) {
        this.mPreferenceSize--;
        updatePreferenceOnSizeChanged();
    }

    /* access modifiers changed from: package-private */
    public void setBluetoothDeviceUpdater(OPPairedBluetoothDeviceUpdater oPPairedBluetoothDeviceUpdater) {
        this.mOPBluetoothCarKitDeviceUpdater = oPPairedBluetoothDeviceUpdater;
    }

    /* access modifiers changed from: package-private */
    public void setPreferenceSize(int i) {
        this.mPreferenceSize = i;
    }

    /* access modifiers changed from: package-private */
    public void setPreference(Preference preference) {
        this.mPreference = preference;
    }

    private void updatePreferenceOnSizeChanged() {
        if (isAvailable()) {
            this.mPreference.setVisible(this.mPreferenceSize == 0);
        }
    }
}
