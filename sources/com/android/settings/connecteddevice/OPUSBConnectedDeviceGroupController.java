package com.android.settings.connecteddevice;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.connecteddevice.usb.ConnectedUsbDeviceUpdater;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;

public class OPUSBConnectedDeviceGroupController extends BasePreferenceController implements PreferenceControllerMixin, LifecycleObserver, OnStart, OnStop, DevicePreferenceCallback {
    private static final String KEY = "usb_connected_device_list";
    private ConnectedUsbDeviceUpdater mConnectedUsbDeviceUpdater;
    private int mOrder = Integer.MAX_VALUE;
    PreferenceScreen mPreferenceScreen;

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

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public OPUSBConnectedDeviceGroupController(Context context) {
        super(context, KEY);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mConnectedUsbDeviceUpdater.registerCallback();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mConnectedUsbDeviceUpdater.unregisterCallback();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreferenceScreen = preferenceScreen;
        Preference findPreference = preferenceScreen.findPreference(KEY);
        if (findPreference != null) {
            this.mOrder = findPreference.getOrder();
            preferenceScreen.removePreference(findPreference);
        }
        if (isAvailable()) {
            this.mConnectedUsbDeviceUpdater.initUsbPreference(preferenceScreen.getContext());
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        PackageManager packageManager = this.mContext.getPackageManager();
        return (packageManager.hasSystemFeature("android.hardware.usb.accessory") || packageManager.hasSystemFeature("android.hardware.usb.host")) ? 1 : 3;
    }

    @Override // com.android.settings.connecteddevice.DevicePreferenceCallback
    public void onDeviceAdded(Preference preference) {
        preference.setOrder(this.mOrder);
        this.mPreferenceScreen.addPreference(preference);
    }

    @Override // com.android.settings.connecteddevice.DevicePreferenceCallback
    public void onDeviceRemoved(Preference preference) {
        this.mPreferenceScreen.removePreference(preference);
    }

    public void init(ConnectedUsbDeviceUpdater connectedUsbDeviceUpdater) {
        this.mConnectedUsbDeviceUpdater = connectedUsbDeviceUpdater;
    }

    public void init(DashboardFragment dashboardFragment) {
        init(new ConnectedUsbDeviceUpdater(dashboardFragment.getContext(), dashboardFragment, this));
    }
}
