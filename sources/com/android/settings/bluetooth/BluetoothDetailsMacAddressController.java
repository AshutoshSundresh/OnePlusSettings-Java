package com.android.settings.bluetooth;

import android.content.Context;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.widget.FooterPreference;

public class BluetoothDetailsMacAddressController extends BluetoothDetailsController {
    private FooterPreference mFooterPreference;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "device_details_footer";
    }

    public BluetoothDetailsMacAddressController(Context context, PreferenceFragmentCompat preferenceFragmentCompat, CachedBluetoothDevice cachedBluetoothDevice, Lifecycle lifecycle) {
        super(context, preferenceFragmentCompat, cachedBluetoothDevice, lifecycle);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.bluetooth.BluetoothDetailsController
    public void init(PreferenceScreen preferenceScreen) {
        FooterPreference footerPreference = (FooterPreference) preferenceScreen.findPreference("device_details_footer");
        this.mFooterPreference = footerPreference;
        footerPreference.setTitle(((BluetoothDetailsController) this).mContext.getString(C0017R$string.bluetooth_device_mac_address, this.mCachedDevice.getAddress()));
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.bluetooth.BluetoothDetailsController
    public void refresh() {
        this.mFooterPreference.setTitle(((BluetoothDetailsController) this).mContext.getString(C0017R$string.bluetooth_device_mac_address, this.mCachedDevice.getAddress()));
    }
}
