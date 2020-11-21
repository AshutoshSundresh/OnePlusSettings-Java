package com.android.settings.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.BidiFormatter;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;
import com.android.settings.C0017R$string;
import com.android.settings.dashboard.RestrictedDashboardFragment;
import com.android.settingslib.bluetooth.BluetoothCallback;
import com.android.settingslib.bluetooth.BluetoothDeviceFilter;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class DeviceListPreferenceFragment extends RestrictedDashboardFragment implements BluetoothCallback {
    BluetoothAdapter mBluetoothAdapter;
    PreferenceGroup mDeviceListGroup;
    final HashMap<CachedBluetoothDevice, BluetoothDevicePreference> mDevicePreferenceMap = new HashMap<>();
    private BluetoothDeviceFilter.Filter mFilter = BluetoothDeviceFilter.ALL_FILTER;
    LocalBluetoothManager mLocalManager;
    boolean mScanEnabled;
    BluetoothDevice mSelectedDevice;
    final List<BluetoothDevice> mSelectedList = new ArrayList();
    boolean mShowDevicesWithoutNames;

    public abstract String getDeviceListKey();

    /* access modifiers changed from: protected */
    public void initDevicePreference(BluetoothDevicePreference bluetoothDevicePreference) {
    }

    /* access modifiers changed from: package-private */
    public abstract void initPreferencesFromPreferenceScreen();

    DeviceListPreferenceFragment(String str) {
        super(str);
    }

    /* access modifiers changed from: package-private */
    public final void setFilter(BluetoothDeviceFilter.Filter filter) {
        this.mFilter = filter;
    }

    /* access modifiers changed from: package-private */
    public final void setFilter(int i) {
        this.mFilter = BluetoothDeviceFilter.getFilter(i);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        LocalBluetoothManager localBtManager = Utils.getLocalBtManager(getActivity());
        this.mLocalManager = localBtManager;
        if (localBtManager == null) {
            Log.e("DeviceListPreferenceFragment", "Bluetooth is not supported on this device");
            return;
        }
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mShowDevicesWithoutNames = SystemProperties.getBoolean("persist.bluetooth.showdeviceswithoutnames", true);
        initPreferencesFromPreferenceScreen();
        this.mDeviceListGroup = (PreferenceCategory) findPreference(getDeviceListKey());
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStart() {
        super.onStart();
        if (this.mLocalManager != null && !isUiRestricted()) {
            this.mLocalManager.setForegroundActivity(getActivity());
            this.mLocalManager.getEventManager().registerCallback(this);
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStop() {
        super.onStop();
        if (this.mLocalManager != null && !isUiRestricted()) {
            removeAllDevices();
            this.mLocalManager.setForegroundActivity(null);
            this.mLocalManager.getEventManager().unregisterCallback(this);
        }
    }

    /* access modifiers changed from: package-private */
    public void removeAllDevices() {
        this.mDevicePreferenceMap.clear();
        this.mDeviceListGroup.removeAll();
    }

    /* access modifiers changed from: package-private */
    public void addCachedDevices() {
        for (CachedBluetoothDevice cachedBluetoothDevice : this.mLocalManager.getCachedDeviceManager().getCachedDevicesCopy()) {
            onDeviceAdded(cachedBluetoothDevice);
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if ("bt_scan".equals(preference.getKey())) {
            startScanning();
            return true;
        } else if (!(preference instanceof BluetoothDevicePreference)) {
            return super.onPreferenceTreeClick(preference);
        } else {
            BluetoothDevicePreference bluetoothDevicePreference = (BluetoothDevicePreference) preference;
            BluetoothDevice device = bluetoothDevicePreference.getCachedDevice().getDevice();
            this.mSelectedDevice = device;
            this.mSelectedList.add(device);
            onDevicePreferenceClick(bluetoothDevicePreference);
            return true;
        }
    }

    /* access modifiers changed from: package-private */
    public void onDevicePreferenceClick(BluetoothDevicePreference bluetoothDevicePreference) {
        bluetoothDevicePreference.onClicked();
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onDeviceAdded(CachedBluetoothDevice cachedBluetoothDevice) {
        if (this.mDevicePreferenceMap.get(cachedBluetoothDevice) == null && this.mBluetoothAdapter.getState() == 12 && this.mFilter.matches(cachedBluetoothDevice.getDevice())) {
            createDevicePreference(cachedBluetoothDevice);
        }
    }

    /* access modifiers changed from: package-private */
    public void createDevicePreference(CachedBluetoothDevice cachedBluetoothDevice) {
        if (this.mDeviceListGroup == null) {
            Log.w("DeviceListPreferenceFragment", "Trying to create a device preference before the list group/category exists!");
            return;
        }
        String address = cachedBluetoothDevice.getDevice().getAddress();
        BluetoothDevicePreference bluetoothDevicePreference = (BluetoothDevicePreference) getCachedPreference(address);
        if (bluetoothDevicePreference == null) {
            bluetoothDevicePreference = new BluetoothDevicePreference(getPrefContext(), cachedBluetoothDevice, this.mShowDevicesWithoutNames, 2);
            bluetoothDevicePreference.setKey(address);
            bluetoothDevicePreference.hideSecondTarget(true);
            this.mDeviceListGroup.addPreference(bluetoothDevicePreference);
        }
        initDevicePreference(bluetoothDevicePreference);
        this.mDevicePreferenceMap.put(cachedBluetoothDevice, bluetoothDevicePreference);
    }

    /* access modifiers changed from: package-private */
    public void updateFooterPreference(Preference preference) {
        BidiFormatter instance = BidiFormatter.getInstance();
        preference.setTitle(getString(C0017R$string.bluetooth_footer_mac_message, instance.unicodeWrap(this.mBluetoothAdapter.getAddress())));
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onDeviceDeleted(CachedBluetoothDevice cachedBluetoothDevice) {
        BluetoothDevicePreference remove = this.mDevicePreferenceMap.remove(cachedBluetoothDevice);
        if (remove != null) {
            this.mDeviceListGroup.removePreference(remove);
        }
    }

    /* access modifiers changed from: package-private */
    public void enableScanning() {
        startScanning();
        if (this.mBluetoothAdapter != null) {
            int i = 21;
            int i2 = Settings.System.getInt(getContext().getContentResolver(), "bluetooth_default_scan_mode", 21);
            BluetoothAdapter bluetoothAdapter = this.mBluetoothAdapter;
            if (i2 == 23) {
                i = 23;
            }
            bluetoothAdapter.setScanMode(i);
        }
        this.mScanEnabled = true;
    }

    /* access modifiers changed from: package-private */
    public void disableScanning() {
        stopScanning();
        this.mScanEnabled = false;
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onScanningStateChanged(boolean z) {
        if (!z && this.mScanEnabled) {
            startScanning();
        }
    }

    public void addDeviceCategory(PreferenceGroup preferenceGroup, int i, BluetoothDeviceFilter.Filter filter, boolean z) {
        cacheRemoveAllPrefs(preferenceGroup);
        preferenceGroup.setTitle(i);
        this.mDeviceListGroup = preferenceGroup;
        if (z) {
            setFilter(BluetoothDeviceFilter.UNBONDED_DEVICE_FILTER);
            addCachedDevices();
        }
        setFilter(filter);
        preferenceGroup.setEnabled(true);
        removeCachedPrefs(preferenceGroup);
    }

    /* access modifiers changed from: package-private */
    public void startScanning() {
        if (!this.mBluetoothAdapter.isDiscovering()) {
            this.mBluetoothAdapter.startDiscovery();
        }
    }

    /* access modifiers changed from: package-private */
    public void stopScanning() {
        if (this.mBluetoothAdapter.isDiscovering()) {
            this.mBluetoothAdapter.cancelDiscovery();
        }
    }
}
