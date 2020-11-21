package com.android.settings.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import com.android.settings.C0017R$string;
import com.android.settings.widget.SwitchWidgetController;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.WirelessUtils;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;

public final class BluetoothEnabler implements SwitchWidgetController.OnSwitchChangeListener {
    private final BluetoothAdapter mBluetoothAdapter;
    private SwitchWidgetController.OnSwitchChangeListener mCallback;
    private Context mContext;
    private final IntentFilter mIntentFilter;
    private final int mMetricsEvent;
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        /* class com.android.settings.bluetooth.BluetoothEnabler.AnonymousClass1 */

        public void onReceive(Context context, Intent intent) {
            BluetoothEnabler.this.handleStateChanged(intent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE));
        }
    };
    private final RestrictionUtils mRestrictionUtils;
    private final SwitchWidgetController mSwitchController;
    private boolean mValidListener;

    public BluetoothEnabler(Context context, SwitchWidgetController switchWidgetController, MetricsFeatureProvider metricsFeatureProvider, int i, RestrictionUtils restrictionUtils) {
        this.mContext = context;
        this.mMetricsFeatureProvider = metricsFeatureProvider;
        this.mSwitchController = switchWidgetController;
        switchWidgetController.setListener(this);
        this.mValidListener = false;
        this.mMetricsEvent = i;
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mBluetoothAdapter = defaultAdapter;
        if (defaultAdapter == null) {
            Log.d("BluetoothEnabler", "Bluetooth is not supported");
            this.mSwitchController.setEnabled(false);
        }
        this.mIntentFilter = new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED");
        this.mRestrictionUtils = restrictionUtils;
    }

    public void resume(Context context) {
        if (this.mContext != context) {
            this.mContext = context;
        }
        boolean maybeEnforceRestrictions = maybeEnforceRestrictions();
        BluetoothAdapter bluetoothAdapter = this.mBluetoothAdapter;
        if (bluetoothAdapter == null) {
            this.mSwitchController.setEnabled(false);
            return;
        }
        if (!maybeEnforceRestrictions) {
            handleStateChanged(bluetoothAdapter.getState());
        }
        this.mSwitchController.startListening();
        this.mContext.registerReceiver(this.mReceiver, this.mIntentFilter);
        this.mValidListener = true;
    }

    public void pause() {
        if (this.mBluetoothAdapter != null && this.mValidListener) {
            this.mSwitchController.stopListening();
            this.mContext.unregisterReceiver(this.mReceiver);
            this.mValidListener = false;
        }
    }

    /* access modifiers changed from: package-private */
    public void handleStateChanged(int i) {
        switch (i) {
            case 10:
                this.mSwitchController.setEnabled(true);
                setChecked(false);
                return;
            case 11:
                Log.d("BluetoothEnabler", "STATE_TURNING_ON");
                this.mSwitchController.setEnabled(false);
                return;
            case 12:
                this.mSwitchController.setEnabled(true);
                setChecked(true);
                int i2 = 21;
                int i3 = Settings.System.getInt(this.mContext.getContentResolver(), "bluetooth_default_scan_mode", 21);
                BluetoothAdapter bluetoothAdapter = this.mBluetoothAdapter;
                if (i3 == 23) {
                    i2 = 23;
                }
                bluetoothAdapter.setScanMode(i2);
                return;
            case 13:
                Log.d("BluetoothEnabler", "STATE_TURNING_OFF");
                this.mSwitchController.setEnabled(false);
                return;
            default:
                this.mSwitchController.setEnabled(true);
                setChecked(false);
                return;
        }
    }

    private void setChecked(boolean z) {
        if (z != this.mSwitchController.isChecked()) {
            if (this.mValidListener) {
                this.mSwitchController.stopListening();
            }
            this.mSwitchController.setChecked(z);
            if (this.mValidListener) {
                this.mSwitchController.startListening();
            }
        }
    }

    @Override // com.android.settings.widget.SwitchWidgetController.OnSwitchChangeListener
    public boolean onSwitchToggled(boolean z) {
        if (maybeEnforceRestrictions()) {
            Log.d("BluetoothEnabler", "maybeEnforceRestrictions");
            triggerParentPreferenceCallback(z);
            return true;
        } else if (!z || WirelessUtils.isRadioAllowed(this.mContext, "bluetooth")) {
            this.mMetricsFeatureProvider.action(this.mContext, this.mMetricsEvent, z);
            if (this.mBluetoothAdapter != null) {
                boolean bluetoothEnabled = setBluetoothEnabled(z);
                if (z && !bluetoothEnabled) {
                    this.mSwitchController.setChecked(false);
                    this.mSwitchController.setEnabled(true);
                    this.mSwitchController.updateTitle(false);
                    triggerParentPreferenceCallback(false);
                    return false;
                }
            }
            this.mSwitchController.setEnabled(false);
            triggerParentPreferenceCallback(z);
            return true;
        } else {
            Toast.makeText(this.mContext, C0017R$string.wifi_in_airplane_mode, 0).show();
            this.mSwitchController.setChecked(false);
            triggerParentPreferenceCallback(false);
            return false;
        }
    }

    public void setToggleCallback(SwitchWidgetController.OnSwitchChangeListener onSwitchChangeListener) {
        this.mCallback = onSwitchChangeListener;
    }

    /* access modifiers changed from: package-private */
    public boolean maybeEnforceRestrictions() {
        RestrictedLockUtils.EnforcedAdmin enforcedAdmin = getEnforcedAdmin(this.mRestrictionUtils, this.mContext);
        this.mSwitchController.setDisabledByAdmin(enforcedAdmin);
        if (enforcedAdmin != null) {
            this.mSwitchController.setChecked(false);
            this.mSwitchController.setEnabled(false);
        }
        if (enforcedAdmin != null) {
            return true;
        }
        return false;
    }

    public static RestrictedLockUtils.EnforcedAdmin getEnforcedAdmin(RestrictionUtils restrictionUtils, Context context) {
        RestrictedLockUtils.EnforcedAdmin checkIfRestrictionEnforced = restrictionUtils.checkIfRestrictionEnforced(context, "no_bluetooth");
        return checkIfRestrictionEnforced == null ? restrictionUtils.checkIfRestrictionEnforced(context, "no_config_bluetooth") : checkIfRestrictionEnforced;
    }

    private void triggerParentPreferenceCallback(boolean z) {
        SwitchWidgetController.OnSwitchChangeListener onSwitchChangeListener = this.mCallback;
        if (onSwitchChangeListener != null) {
            onSwitchChangeListener.onSwitchToggled(z);
        }
    }

    private boolean setBluetoothEnabled(boolean z) {
        Log.d("BluetoothEnabler", "setBluetoothEnabled : " + z);
        BluetoothAdapter bluetoothAdapter = this.mBluetoothAdapter;
        return z ? bluetoothAdapter.enable() : bluetoothAdapter.disable();
    }
}
