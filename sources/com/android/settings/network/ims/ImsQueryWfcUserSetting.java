package com.android.settings.network.ims;

import android.telephony.ims.ImsMmTelManager;
import android.util.Log;

public class ImsQueryWfcUserSetting {
    private volatile int mSubId;

    public ImsQueryWfcUserSetting(int i) {
        this.mSubId = i;
    }

    public boolean query() {
        try {
            return ImsMmTelManager.createForSubscriptionId(this.mSubId).isVoWiFiSettingEnabled();
        } catch (IllegalArgumentException e) {
            Log.w("QueryWfcUserSetting", "fail to get Wfc settings. subId=" + this.mSubId, e);
            return false;
        }
    }
}
