package com.android.settings.wifi.p2p;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pGroup;
import androidx.preference.Preference;

public class WifiP2pPersistentGroup extends Preference {
    public WifiP2pGroup mGroup;

    public WifiP2pPersistentGroup(Context context, WifiP2pGroup wifiP2pGroup) {
        super(context);
        this.mGroup = wifiP2pGroup;
        setTitle(wifiP2pGroup.getNetworkName());
    }

    /* access modifiers changed from: package-private */
    public int getNetworkId() {
        return this.mGroup.getNetworkId();
    }

    /* access modifiers changed from: package-private */
    public String getGroupName() {
        return this.mGroup.getNetworkName();
    }
}
