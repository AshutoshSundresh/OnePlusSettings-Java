package com.android.settings.connecteddevice;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.oneplus.settings.utils.OPUtils;

public class AddDevicePreferenceController extends BasePreferenceController implements LifecycleObserver, OnStart, OnStop {
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private IntentFilter mIntentFilter = new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED");
    private Preference mPreference;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        /* class com.android.settings.connecteddevice.AddDevicePreferenceController.AnonymousClass1 */

        public void onReceive(Context context, Intent intent) {
            AddDevicePreferenceController.this.updateState();
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

    public AddDevicePreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        if (this.mBluetoothAdapter != null) {
            setBluetoothDiscoverableState();
            Settings.System.getString(this.mContext.getContentResolver(), "oem_oneplus_devicename");
            String resetDeviceNameIfInvalid = OPUtils.resetDeviceNameIfInvalid(this.mContext);
            if (!TextUtils.isEmpty(resetDeviceNameIfInvalid)) {
                this.mBluetoothAdapter.setName(resetDeviceNameIfInvalid);
            }
        }
        this.mContext.registerReceiver(this.mReceiver, this.mIntentFilter);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mContext.unregisterReceiver(this.mReceiver);
    }

    private void setBluetoothDiscoverableState() {
        int i = Settings.System.getInt(this.mContext.getContentResolver(), "bluetooth_default_scan_mode", 21);
        if (i == 23) {
            this.mBluetoothAdapter.setScanMode(23);
        } else if (i == 21) {
            this.mBluetoothAdapter.setScanMode(21);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (isAvailable()) {
            this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mContext.getPackageManager().hasSystemFeature("android.hardware.bluetooth") ? 0 : 3;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        BluetoothAdapter bluetoothAdapter = this.mBluetoothAdapter;
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            return this.mContext.getString(C0017R$string.connected_device_add_device_summary);
        }
        return "";
    }

    /* access modifiers changed from: package-private */
    public void updateState() {
        updateState(this.mPreference);
    }
}
