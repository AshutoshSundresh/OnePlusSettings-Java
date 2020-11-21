package com.android.settings.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.List;

public final class DevicePickerFragment extends DeviceListPreferenceFragment {
    BluetoothProgressCategory mAvailableDevicesCategory;
    private String mLaunchClass;
    private String mLaunchPackage;
    private boolean mNeedAuth;
    private boolean mScanAllowed;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return null;
    }

    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment
    public String getDeviceListKey() {
        return "bt_device_list";
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "DevicePickerFragment";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 25;
    }

    public DevicePickerFragment() {
        super(null);
    }

    /* access modifiers changed from: package-private */
    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment
    public void initPreferencesFromPreferenceScreen() {
        Intent intent = getActivity().getIntent();
        this.mNeedAuth = intent.getBooleanExtra("android.bluetooth.devicepicker.extra.NEED_AUTH", false);
        setFilter(intent.getIntExtra("android.bluetooth.devicepicker.extra.FILTER_TYPE", 0));
        this.mLaunchPackage = intent.getStringExtra("android.bluetooth.devicepicker.extra.LAUNCH_PACKAGE");
        this.mLaunchClass = intent.getStringExtra("android.bluetooth.devicepicker.extra.DEVICE_PICKER_LAUNCH_CLASS");
        this.mAvailableDevicesCategory = (BluetoothProgressCategory) findPreference("bt_device_list");
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settings.bluetooth.DeviceListPreferenceFragment, com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getActivity().setTitle(getString(C0017R$string.device_picker));
        this.mScanAllowed = !((UserManager) getSystemService("user")).hasUserRestriction("no_config_bluetooth");
        setHasOptionsMenu(true);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settings.bluetooth.DeviceListPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStart() {
        super.onStart();
        addCachedDevices();
        this.mSelectedDevice = null;
        if (this.mScanAllowed) {
            enableScanning();
            this.mAvailableDevicesCategory.setProgress(this.mBluetoothAdapter.isDiscovering());
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settings.bluetooth.DeviceListPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStop() {
        disableScanning();
        super.onStop();
    }

    @Override // androidx.fragment.app.Fragment, com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onDestroy() {
        super.onDestroy();
        if (this.mSelectedDevice == null) {
            sendDevicePickedIntent(null);
        }
    }

    /* access modifiers changed from: package-private */
    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment
    public void onDevicePreferenceClick(BluetoothDevicePreference bluetoothDevicePreference) {
        disableScanning();
        LocalBluetoothPreferences.persistSelectedDeviceInPicker(getActivity(), this.mSelectedDevice.getAddress());
        if (bluetoothDevicePreference.getCachedDevice().getBondState() == 12 || !this.mNeedAuth) {
            sendDevicePickedIntent(this.mSelectedDevice);
            finish();
            return;
        }
        super.onDevicePreferenceClick(bluetoothDevicePreference);
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback, com.android.settings.bluetooth.DeviceListPreferenceFragment
    public void onScanningStateChanged(boolean z) {
        super.onScanningStateChanged(z);
        this.mAvailableDevicesCategory.setProgress(z | this.mScanEnabled);
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onDeviceBondStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        BluetoothDevice device = cachedBluetoothDevice.getDevice();
        if (device.equals(this.mSelectedDevice)) {
            if (i == 12) {
                sendDevicePickedIntent(device);
                finish();
            } else if (i == 10) {
                enableScanning();
            }
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment
    public void initDevicePreference(BluetoothDevicePreference bluetoothDevicePreference) {
        super.initDevicePreference(bluetoothDevicePreference);
        bluetoothDevicePreference.setNeedNotifyHierarchyChanged(true);
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onBluetoothStateChanged(int i) {
        super.onBluetoothStateChanged(i);
        if (i == 12) {
            enableScanning();
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.device_picker;
    }

    private void sendDevicePickedIntent(BluetoothDevice bluetoothDevice) {
        String str;
        Log.d("Devicepicker", "sendDevicePickedIntent");
        Intent intent = new Intent("android.bluetooth.devicepicker.action.DEVICE_SELECTED");
        intent.putExtra("android.bluetooth.device.extra.DEVICE", bluetoothDevice);
        String str2 = this.mLaunchPackage;
        if (!(str2 == null || (str = this.mLaunchClass) == null)) {
            intent.setClassName(str2, str);
        }
        intent.addFlags(268435456);
        getActivity().sendBroadcast(intent);
    }
}
