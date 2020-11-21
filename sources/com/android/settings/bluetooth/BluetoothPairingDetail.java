package com.android.settings.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settingslib.bluetooth.BluetoothDeviceFilter;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.widget.OPFooterPreference;

public class BluetoothPairingDetail extends DeviceListPreferenceFragment {
    static final String KEY_AVAIL_DEVICES = "available_devices";
    static final String KEY_FOOTER_PREF = "footer_preference";
    AlwaysDiscoverable mAlwaysDiscoverable;
    BluetoothProgressCategory mAvailableDevicesCategory;
    OPFooterPreference mFooterPreference;
    private boolean mInitialScanStarted;

    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment
    public String getDeviceListKey() {
        return KEY_AVAIL_DEVICES;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "BluetoothPairingDetail";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1018;
    }

    public BluetoothPairingDetail() {
        super("no_config_bluetooth");
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment, com.android.settings.dashboard.RestrictedDashboardFragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.mInitialScanStarted = false;
        this.mAlwaysDiscoverable = new AlwaysDiscoverable(getContext());
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settings.bluetooth.DeviceListPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStart() {
        super.onStart();
        if (this.mLocalManager == null) {
            Log.e("BluetoothPairingDetail", "Bluetooth is not supported on this device");
            return;
        }
        updateBluetooth();
        this.mAvailableDevicesCategory.setProgress(this.mBluetoothAdapter.isDiscovering());
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        ((BluetoothDeviceRenamePreferenceController) use(BluetoothDeviceRenamePreferenceController.class)).setFragment(this);
    }

    /* access modifiers changed from: package-private */
    public void updateBluetooth() {
        if (this.mBluetoothAdapter.isEnabled()) {
            updateContent(this.mBluetoothAdapter.getState());
        } else {
            this.mBluetoothAdapter.enable();
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settings.bluetooth.DeviceListPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStop() {
        super.onStop();
        if (this.mLocalManager == null) {
            Log.e("BluetoothPairingDetail", "Bluetooth is not supported on this device");
            return;
        }
        this.mAlwaysDiscoverable.stop();
        disableScanning();
    }

    /* access modifiers changed from: package-private */
    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment
    public void initPreferencesFromPreferenceScreen() {
        this.mAvailableDevicesCategory = (BluetoothProgressCategory) findPreference(KEY_AVAIL_DEVICES);
        OPFooterPreference oPFooterPreference = (OPFooterPreference) findPreference(KEY_FOOTER_PREF);
        this.mFooterPreference = oPFooterPreference;
        oPFooterPreference.setSelectable(false);
    }

    /* access modifiers changed from: package-private */
    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment
    public void enableScanning() {
        if (!this.mInitialScanStarted) {
            if (this.mAvailableDevicesCategory != null) {
                removeAllDevices();
            }
            this.mLocalManager.getCachedDeviceManager().clearNonBondedDevices();
            this.mInitialScanStarted = true;
        }
        super.enableScanning();
    }

    /* access modifiers changed from: package-private */
    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment
    public void onDevicePreferenceClick(BluetoothDevicePreference bluetoothDevicePreference) {
        disableScanning();
        super.onDevicePreferenceClick(bluetoothDevicePreference);
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback, com.android.settings.bluetooth.DeviceListPreferenceFragment
    public void onScanningStateChanged(boolean z) {
        super.onScanningStateChanged(z);
        this.mAvailableDevicesCategory.setProgress(z | this.mScanEnabled);
    }

    /* access modifiers changed from: package-private */
    public void updateContent(int i) {
        if (i == 10) {
            finish();
        } else if (i == 12) {
            this.mDevicePreferenceMap.clear();
            this.mBluetoothAdapter.enable();
            addDeviceCategory(this.mAvailableDevicesCategory, C0017R$string.bluetooth_preference_found_media_devices, BluetoothDeviceFilter.ALL_FILTER, this.mInitialScanStarted);
            updateFooterPreference(this.mFooterPreference);
            this.mAlwaysDiscoverable.start();
            enableScanning();
            String string = Settings.System.getString(SettingsBaseApplication.mApplication.getContentResolver(), "oem_oneplus_devicename");
            if (!TextUtils.isEmpty(string)) {
                this.mBluetoothAdapter.setName(string);
            }
        }
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onBluetoothStateChanged(int i) {
        super.onBluetoothStateChanged(i);
        updateContent(i);
        if (i == 12) {
            showBluetoothTurnedOnToast();
        }
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onDeviceBondStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        BluetoothDevice device;
        if (i == 12) {
            finish();
        } else if (this.mSelectedDevice != null && cachedBluetoothDevice != null && (device = cachedBluetoothDevice.getDevice()) != null && this.mSelectedDevice.equals(device) && i == 10) {
            enableScanning();
        }
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onProfileConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i, int i2) {
        if (cachedBluetoothDevice != null && cachedBluetoothDevice.isConnected()) {
            BluetoothDevice device = cachedBluetoothDevice.getDevice();
            if (device != null && this.mSelectedList.contains(device)) {
                finish();
            } else if (this.mDevicePreferenceMap.containsKey(cachedBluetoothDevice)) {
                onDeviceDeleted(cachedBluetoothDevice);
            }
        }
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_bluetooth;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.bluetooth_pairing_detail;
    }

    /* access modifiers changed from: package-private */
    public void showBluetoothTurnedOnToast() {
        Toast.makeText(getContext(), C0017R$string.connected_device_bluetooth_turned_on_toast, 0).show();
    }
}
