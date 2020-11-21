package com.android.settingslib.deviceinfo;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settingslib.R$string;
import com.android.settingslib.core.lifecycle.Lifecycle;

public abstract class AbstractBluetoothAddressPreferenceController extends AbstractConnectivityPreferenceController {
    private static final String[] CONNECTIVITY_INTENTS = {"android.bluetooth.adapter.action.STATE_CHANGED"};
    static final String KEY_BT_ADDRESS = "bt_address";
    private Preference mBtAddress;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return KEY_BT_ADDRESS;
    }

    public AbstractBluetoothAddressPreferenceController(Context context, Lifecycle lifecycle) {
        super(context, lifecycle);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return BluetoothAdapter.getDefaultAdapter() != null;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mBtAddress = preferenceScreen.findPreference(KEY_BT_ADDRESS);
        updateConnectivity();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.deviceinfo.AbstractConnectivityPreferenceController
    public String[] getConnectivityIntents() {
        return CONNECTIVITY_INTENTS;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.deviceinfo.AbstractConnectivityPreferenceController
    @SuppressLint({"HardwareIds"})
    public void updateConnectivity() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null && this.mBtAddress != null) {
            String address = defaultAdapter.isEnabled() ? defaultAdapter.getAddress() : null;
            if (!TextUtils.isEmpty(address)) {
                this.mBtAddress.setSummary(address.toLowerCase());
            } else {
                this.mBtAddress.setSummary(R$string.status_unavailable);
            }
        }
    }
}
