package com.android.settings.connecteddevice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.settings.bluetooth.OPBluetoothCarKitDeviceUpdater;
import com.android.settings.bluetooth.OPPairedBluetoothDeviceUpdater;
import com.android.settings.bluetooth.Utils;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.bluetooth.BluetoothCallback;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;

public class OPOtherPairedBluetoothDevicesGroupController extends BasePreferenceController implements LifecycleObserver, OnStart, OnStop, DevicePreferenceCallback, BluetoothCallback {
    private static final String KEY = "other_paired_bluetooth_devices";
    private final LocalBluetoothManager mLocalBluetoothManager = Utils.getLocalBtManager(this.mContext);
    private OPBluetoothCarKitDeviceUpdater mOPBluetoothCarKitDeviceUpdater;
    PreferenceGroup mPreferenceGroup;
    private BroadcastReceiver mStatusReceive = new BroadcastReceiver() {
        /* class com.android.settings.connecteddevice.OPOtherPairedBluetoothDevicesGroupController.AnonymousClass1 */

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (((action.hashCode() == 140676821 && action.equals("oneplus.action.intent.UpdateBluetoothCarkitDevice")) ? (char) 0 : 65535) == 0) {
                OPOtherPairedBluetoothDevicesGroupController.this.mOPBluetoothCarKitDeviceUpdater.forceUpdate();
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

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public String getPreferenceKey() {
        return KEY;
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

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public /* bridge */ /* synthetic */ void onAclConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        super.onAclConnectionStateChanged(cachedBluetoothDevice, i);
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onActiveDeviceChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onAudioModeChanged() {
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onBluetoothStateChanged(int i) {
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onDeviceAdded(CachedBluetoothDevice cachedBluetoothDevice) {
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onDeviceBondStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onDeviceDeleted(CachedBluetoothDevice cachedBluetoothDevice) {
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public /* bridge */ /* synthetic */ void onProfileConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i, int i2) {
        super.onProfileConnectionStateChanged(cachedBluetoothDevice, i, i2);
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onScanningStateChanged(boolean z) {
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public OPOtherPairedBluetoothDevicesGroupController(Context context) {
        super(context, KEY);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mOPBluetoothCarKitDeviceUpdater.registerCallback();
        this.mLocalBluetoothManager.getEventManager().registerCallback(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("oneplus.action.intent.UpdateBluetoothCarkitDevice");
        this.mContext.registerReceiver(this.mStatusReceive, intentFilter);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mOPBluetoothCarKitDeviceUpdater.unregisterCallback();
        this.mLocalBluetoothManager.getEventManager().unregisterCallback(this);
        this.mContext.unregisterReceiver(this.mStatusReceive);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (isAvailable()) {
            this.mPreferenceGroup = (PreferenceGroup) preferenceScreen.findPreference(KEY);
            this.mOPBluetoothCarKitDeviceUpdater.setPrefContext(preferenceScreen.getContext());
            this.mOPBluetoothCarKitDeviceUpdater.forceUpdate();
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mContext.getPackageManager().hasSystemFeature("android.hardware.bluetooth") ? 0 : 3;
    }

    @Override // com.android.settings.connecteddevice.DevicePreferenceCallback
    public void onDeviceAdded(Preference preference) {
        if (this.mPreferenceGroup.getPreferenceCount() == 0) {
            this.mPreferenceGroup.setVisible(true);
        }
        this.mPreferenceGroup.addPreference(preference);
    }

    @Override // com.android.settings.connecteddevice.DevicePreferenceCallback
    public void onDeviceRemoved(Preference preference) {
        this.mPreferenceGroup.removePreference(preference);
        this.mPreferenceGroup.getPreferenceCount();
    }

    public void init(DashboardFragment dashboardFragment) {
        this.mOPBluetoothCarKitDeviceUpdater = new OPPairedBluetoothDeviceUpdater(dashboardFragment.getContext(), dashboardFragment, this);
    }

    public void setBluetoothDeviceUpdater(OPBluetoothCarKitDeviceUpdater oPBluetoothCarKitDeviceUpdater) {
        this.mOPBluetoothCarKitDeviceUpdater = oPBluetoothCarKitDeviceUpdater;
    }
}
