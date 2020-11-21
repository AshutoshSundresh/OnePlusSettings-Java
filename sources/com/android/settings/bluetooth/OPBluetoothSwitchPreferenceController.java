package com.android.settings.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.widget.MasterSwitchController;
import com.android.settings.widget.MasterSwitchPreference;
import com.android.settings.widget.SwitchWidgetController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;

public class OPBluetoothSwitchPreferenceController extends BasePreferenceController implements LifecycleObserver, OnResume, OnPause, SwitchWidgetController.OnSwitchChangeListener {
    private static final String KEY_BLUETOOTH_SETTINGS = "bluetooth_settings";
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private long mLastClickTime = 0;
    private BroadcastReceiver mStatusReceive = new BroadcastReceiver() {
        /* class com.android.settings.bluetooth.OPBluetoothSwitchPreferenceController.AnonymousClass1 */

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (((action.hashCode() == -1530327060 && action.equals("android.bluetooth.adapter.action.STATE_CHANGED")) ? (char) 0 : 65535) == 0) {
                int intExtra = intent.getIntExtra("android.bluetooth.adapter.extra.STATE", 0);
                switch (intExtra) {
                    case 10:
                        OPBluetoothSwitchPreferenceController.this.handleStateChanged(intExtra);
                        return;
                    case 11:
                        OPBluetoothSwitchPreferenceController.this.handleStateChanged(intExtra);
                        return;
                    case 12:
                        OPBluetoothSwitchPreferenceController.this.handleStateChanged(intExtra);
                        return;
                    case 13:
                        OPBluetoothSwitchPreferenceController.this.handleStateChanged(intExtra);
                        return;
                    default:
                        return;
                }
            }
        }
    };
    private MasterSwitchPreference mSwitch;
    private MasterSwitchController mSwitchController;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
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
        return KEY_BLUETOOTH_SETTINGS;
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

    public OPBluetoothSwitchPreferenceController(Context context, Lifecycle lifecycle) {
        super(context, KEY_BLUETOOTH_SETTINGS);
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mSwitch = (MasterSwitchPreference) preferenceScreen.findPreference(KEY_BLUETOOTH_SETTINGS);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        if (isAvailable()) {
            MasterSwitchPreference masterSwitchPreference = this.mSwitch;
            if (masterSwitchPreference != null) {
                MasterSwitchController masterSwitchController = new MasterSwitchController(masterSwitchPreference);
                this.mSwitchController = masterSwitchController;
                masterSwitchController.setListener(this);
                this.mSwitchController.startListening();
                handleStateChanged(this.mBluetoothAdapter.getState());
            }
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
            this.mContext.registerReceiver(this.mStatusReceive, intentFilter);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        this.mSwitchController.stopListening();
        this.mContext.unregisterReceiver(this.mStatusReceive);
    }

    /* access modifiers changed from: package-private */
    public void handleStateChanged(int i) {
        switch (i) {
            case 10:
                this.mSwitchController.setEnabled(true);
                this.mSwitch.setEnabled(true);
                setChecked(false);
                return;
            case 11:
                this.mSwitchController.setEnabled(false);
                this.mSwitch.setEnabled(false);
                return;
            case 12:
                this.mSwitchController.setEnabled(true);
                this.mSwitch.setEnabled(true);
                setChecked(true);
                return;
            case 13:
                this.mSwitchController.setEnabled(false);
                this.mSwitch.setEnabled(false);
                return;
            default:
                this.mSwitchController.setEnabled(true);
                this.mSwitch.setEnabled(true);
                setChecked(false);
                return;
        }
    }

    private void setChecked(boolean z) {
        if (z != this.mSwitchController.isChecked()) {
            this.mSwitchController.setChecked(z);
        }
    }

    @Override // com.android.settings.widget.SwitchWidgetController.OnSwitchChangeListener
    public boolean onSwitchToggled(boolean z) {
        if (SystemClock.elapsedRealtime() - this.mLastClickTime < 1000) {
            return false;
        }
        this.mLastClickTime = SystemClock.elapsedRealtime();
        setBluetoothEnabled(z);
        return true;
    }

    private boolean setBluetoothEnabled(boolean z) {
        BluetoothAdapter bluetoothAdapter = this.mBluetoothAdapter;
        return z ? bluetoothAdapter.enable() : bluetoothAdapter.disable();
    }
}
