package com.android.settings.deviceinfo;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.SoftApConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannedString;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.Utils;
import com.android.settings.bluetooth.BluetoothLengthDeviceNameFilter;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.widget.ValidatedEditTextPreference;
import com.android.settings.wifi.tether.WifiDeviceNameTextValidator;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnCreate;
import com.android.settingslib.core.lifecycle.events.OnSaveInstanceState;

public class DeviceNamePreferenceController extends BasePreferenceController implements ValidatedEditTextPreference.Validator, Preference.OnPreferenceChangeListener, LifecycleObserver, OnSaveInstanceState, OnCreate {
    private static final String KEY_PENDING_DEVICE_NAME = "key_pending_device_name";
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private String mDeviceName;
    private DeviceNamePreferenceHost mHost;
    private String mPendingDeviceName;
    private ValidatedEditTextPreference mPreference;
    private final WifiDeviceNameTextValidator mWifiDeviceNameTextValidator = new WifiDeviceNameTextValidator();
    protected WifiManager mWifiManager;

    public interface DeviceNamePreferenceHost {
        void showDeviceNameWarningDialog(String str);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 3;
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

    public DeviceNamePreferenceController(Context context, String str) {
        super(context, str);
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
        initializeDeviceName();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (ValidatedEditTextPreference) preferenceScreen.findPreference(getPreferenceKey());
        CharSequence summary = getSummary();
        this.mPreference.setSummary(summary);
        this.mPreference.setText(summary.toString());
        this.mPreference.setValidator(this);
    }

    private void initializeDeviceName() {
        this.mDeviceName = Settings.Global.getString(this.mContext.getContentResolver(), "device_name");
        if (Utils.isSupportCTPA(this.mContext)) {
            this.mDeviceName = Utils.getString(this.mContext, "ext_device_name");
        }
        if (this.mDeviceName == null) {
            this.mDeviceName = Build.MODEL;
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return this.mDeviceName;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        String str = (String) obj;
        this.mPendingDeviceName = str;
        DeviceNamePreferenceHost deviceNamePreferenceHost = this.mHost;
        if (deviceNamePreferenceHost == null) {
            return true;
        }
        deviceNamePreferenceHost.showDeviceNameWarningDialog(str);
        return true;
    }

    @Override // com.android.settings.widget.ValidatedEditTextPreference.Validator
    public boolean isTextValid(String str) {
        return this.mWifiDeviceNameTextValidator.isTextValid(str);
    }

    public void updateDeviceName(boolean z) {
        String str;
        if (!z || (str = this.mPendingDeviceName) == null) {
            this.mPreference.setText(getSummary().toString());
        } else {
            setDeviceName(str);
        }
    }

    public void setHost(DeviceNamePreferenceHost deviceNamePreferenceHost) {
        this.mHost = deviceNamePreferenceHost;
    }

    private void setDeviceName(String str) {
        this.mDeviceName = str;
        setSettingsGlobalDeviceName(str);
        setBluetoothDeviceName(str);
        setTetherSsidName(str);
        this.mPreference.setSummary(getSummary());
    }

    private void setSettingsGlobalDeviceName(String str) {
        Settings.Global.putString(this.mContext.getContentResolver(), "device_name", str);
        if (Utils.isSupportCTPA(this.mContext)) {
            Settings.Global.putString(this.mContext.getContentResolver(), "ext_device_name", str);
        }
    }

    private void setBluetoothDeviceName(String str) {
        BluetoothAdapter bluetoothAdapter = this.mBluetoothAdapter;
        if (bluetoothAdapter != null) {
            bluetoothAdapter.setName(getFilteredBluetoothString(str));
        }
    }

    private static final String getFilteredBluetoothString(String str) {
        CharSequence filter = new BluetoothLengthDeviceNameFilter().filter(str, 0, str.length(), new SpannedString(""), 0, 0);
        if (filter == null) {
            return str;
        }
        return filter.toString();
    }

    private void setTetherSsidName(String str) {
        this.mWifiManager.setSoftApConfiguration(new SoftApConfiguration.Builder(this.mWifiManager.getSoftApConfiguration()).setSsid(str).build());
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnCreate
    public void onCreate(Bundle bundle) {
        if (bundle != null) {
            this.mPendingDeviceName = bundle.getString(KEY_PENDING_DEVICE_NAME, null);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnSaveInstanceState
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putString(KEY_PENDING_DEVICE_NAME, this.mPendingDeviceName);
    }
}
