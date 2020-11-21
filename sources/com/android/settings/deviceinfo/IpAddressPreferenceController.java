package com.android.settings.deviceinfo;

import android.content.Context;
import com.android.settings.C0005R$bool;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.deviceinfo.AbstractIpAddressPreferenceController;

public class IpAddressPreferenceController extends AbstractIpAddressPreferenceController implements PreferenceControllerMixin {
    public IpAddressPreferenceController(Context context, Lifecycle lifecycle) {
        super(context, lifecycle);
    }

    @Override // com.android.settingslib.deviceinfo.AbstractIpAddressPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mContext.getResources().getBoolean(C0005R$bool.config_show_wifi_ip_address);
    }
}
