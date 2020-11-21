package com.android.settings.deviceinfo;

import android.content.Context;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.deviceinfo.AbstractBluetoothAddressPreferenceController;

public class BluetoothAddressPreferenceController extends AbstractBluetoothAddressPreferenceController implements PreferenceControllerMixin {
    public BluetoothAddressPreferenceController(Context context, Lifecycle lifecycle) {
        super(context, lifecycle);
    }
}
