package com.android.settings.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.SystemProperties;
import androidx.preference.Preference;
import com.android.settings.connecteddevice.DevicePreferenceCallback;
import com.android.settings.connecteddevice.OPBluetoothCarKitDevicePreference;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settingslib.bluetooth.BluetoothCallback;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.bluetooth.LocalBluetoothProfileManager;
import java.util.HashMap;
import java.util.Map;

public abstract class OPBluetoothCarKitDeviceUpdater implements BluetoothCallback, LocalBluetoothProfileManager.ServiceListener {
    protected final DevicePreferenceCallback mDevicePreferenceCallback;
    protected DashboardFragment mFragment;
    protected final LocalBluetoothManager mLocalManager;
    protected Context mPrefContext;
    protected final Map<BluetoothDevice, Preference> mPreferenceMap;
    private final boolean mShowDeviceWithoutNames;

    public abstract boolean isFilterMatched(CachedBluetoothDevice cachedBluetoothDevice);

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onActiveDeviceChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onAudioModeChanged() {
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onProfileConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i, int i2) {
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onScanningStateChanged(boolean z) {
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfileManager.ServiceListener
    public void onServiceDisconnected() {
    }

    public OPBluetoothCarKitDeviceUpdater(Context context, DashboardFragment dashboardFragment, DevicePreferenceCallback devicePreferenceCallback) {
        this(dashboardFragment, devicePreferenceCallback, Utils.getLocalBtManager(context));
    }

    OPBluetoothCarKitDeviceUpdater(DashboardFragment dashboardFragment, DevicePreferenceCallback devicePreferenceCallback, LocalBluetoothManager localBluetoothManager) {
        this.mFragment = dashboardFragment;
        this.mDevicePreferenceCallback = devicePreferenceCallback;
        this.mShowDeviceWithoutNames = SystemProperties.getBoolean("persist.bluetooth.showdeviceswithoutnames", true);
        this.mPreferenceMap = new HashMap();
        this.mLocalManager = localBluetoothManager;
    }

    public void registerCallback() {
        this.mLocalManager.setForegroundActivity(this.mFragment.getContext());
        this.mLocalManager.getEventManager().registerCallback(this);
        this.mLocalManager.getProfileManager().addServiceListener(this);
        forceUpdate();
    }

    public void unregisterCallback() {
        this.mLocalManager.setForegroundActivity(null);
        this.mLocalManager.getEventManager().unregisterCallback(this);
        this.mLocalManager.getProfileManager().removeServiceListener(this);
    }

    public void forceUpdate() {
        for (CachedBluetoothDevice cachedBluetoothDevice : this.mLocalManager.getCachedDeviceManager().getCachedDevicesCopy()) {
            update(cachedBluetoothDevice);
        }
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onBluetoothStateChanged(int i) {
        forceUpdate();
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onDeviceAdded(CachedBluetoothDevice cachedBluetoothDevice) {
        update(cachedBluetoothDevice);
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onDeviceDeleted(CachedBluetoothDevice cachedBluetoothDevice) {
        removePreference(cachedBluetoothDevice);
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onDeviceBondStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        update(cachedBluetoothDevice);
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfileManager.ServiceListener
    public void onServiceConnected() {
        forceUpdate();
    }

    public void setPrefContext(Context context) {
        this.mPrefContext = context;
    }

    /* access modifiers changed from: protected */
    public void update(CachedBluetoothDevice cachedBluetoothDevice) {
        if (isFilterMatched(cachedBluetoothDevice)) {
            addPreference(cachedBluetoothDevice);
        } else {
            removePreference(cachedBluetoothDevice);
        }
    }

    /* access modifiers changed from: protected */
    public void addPreference(CachedBluetoothDevice cachedBluetoothDevice) {
        BluetoothDevice device = cachedBluetoothDevice.getDevice();
        if (!this.mPreferenceMap.containsKey(device)) {
            OPBluetoothCarKitDevicePreference oPBluetoothCarKitDevicePreference = new OPBluetoothCarKitDevicePreference(this.mPrefContext, cachedBluetoothDevice, this.mShowDeviceWithoutNames);
            this.mPreferenceMap.put(device, oPBluetoothCarKitDevicePreference);
            this.mDevicePreferenceCallback.onDeviceAdded(oPBluetoothCarKitDevicePreference);
        }
    }

    /* access modifiers changed from: protected */
    public void removePreference(CachedBluetoothDevice cachedBluetoothDevice) {
        BluetoothDevice device = cachedBluetoothDevice.getDevice();
        if (this.mPreferenceMap.containsKey(device)) {
            this.mDevicePreferenceCallback.onDeviceRemoved(this.mPreferenceMap.get(device));
            this.mPreferenceMap.remove(device);
        }
    }

    public boolean isDeviceConnected(CachedBluetoothDevice cachedBluetoothDevice) {
        if (cachedBluetoothDevice != null && cachedBluetoothDevice.getDevice().getBondState() == 12) {
            return true;
        }
        return false;
    }
}
