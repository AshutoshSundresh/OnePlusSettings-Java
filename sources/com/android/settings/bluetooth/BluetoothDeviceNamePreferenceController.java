package com.android.settings.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.text.BidiFormatter;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;

public class BluetoothDeviceNamePreferenceController extends BasePreferenceController implements LifecycleObserver, OnStart, OnStop {
    private static final String TAG = "BluetoothNamePrefCtrl";
    protected BluetoothAdapter mBluetoothAdapter;
    Preference mPreference;
    final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        /* class com.android.settings.bluetooth.BluetoothDeviceNamePreferenceController.AnonymousClass1 */

        public void onReceive(Context context, Intent intent) {
            BluetoothAdapter bluetoothAdapter;
            String action = intent.getAction();
            if (TextUtils.equals(action, "android.bluetooth.adapter.action.LOCAL_NAME_CHANGED")) {
                BluetoothDeviceNamePreferenceController bluetoothDeviceNamePreferenceController = BluetoothDeviceNamePreferenceController.this;
                if (bluetoothDeviceNamePreferenceController.mPreference != null && (bluetoothAdapter = bluetoothDeviceNamePreferenceController.mBluetoothAdapter) != null && bluetoothAdapter.isEnabled()) {
                    BluetoothDeviceNamePreferenceController bluetoothDeviceNamePreferenceController2 = BluetoothDeviceNamePreferenceController.this;
                    bluetoothDeviceNamePreferenceController2.updatePreferenceState(bluetoothDeviceNamePreferenceController2.mPreference);
                }
            } else if (TextUtils.equals(action, "android.bluetooth.adapter.action.STATE_CHANGED")) {
                BluetoothDeviceNamePreferenceController bluetoothDeviceNamePreferenceController3 = BluetoothDeviceNamePreferenceController.this;
                bluetoothDeviceNamePreferenceController3.updatePreferenceState(bluetoothDeviceNamePreferenceController3.mPreference);
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

    public BluetoothDeviceNamePreferenceController(Context context, String str) {
        super(context, str);
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mBluetoothAdapter = defaultAdapter;
        if (defaultAdapter == null) {
            Log.e(TAG, "Bluetooth is not supported on this device");
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
        super.displayPreference(preferenceScreen);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.bluetooth.adapter.action.LOCAL_NAME_CHANGED");
        intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        this.mContext.registerReceiver(this.mReceiver, intentFilter);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mContext.unregisterReceiver(this.mReceiver);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mBluetoothAdapter != null ? 0 : 3;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        updatePreferenceState(preference);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        String string = Settings.System.getString(this.mContext.getContentResolver(), "oem_oneplus_devicename");
        if (TextUtils.isEmpty(string)) {
            return super.getSummary();
        }
        return TextUtils.expandTemplate(this.mContext.getText(C0017R$string.bluetooth_device_name_summary), BidiFormatter.getInstance().unicodeWrap(string)).toString();
    }

    public Preference createBluetoothDeviceNamePreference(PreferenceScreen preferenceScreen, int i) {
        Preference preference = new Preference(preferenceScreen.getContext());
        this.mPreference = preference;
        preference.setOrder(i);
        this.mPreference.setKey(getPreferenceKey());
        preferenceScreen.addPreference(this.mPreference);
        return this.mPreference;
    }

    /* access modifiers changed from: protected */
    public void updatePreferenceState(Preference preference) {
        preference.setSelectable(false);
        preference.setSummary(getSummary());
    }

    /* access modifiers changed from: protected */
    public String getDeviceName() {
        return this.mBluetoothAdapter.getName();
    }
}
