package com.android.settings.network.ims;

import android.telephony.ims.ImsMmTelManager;
import android.util.Log;

public class ImsQueryEnhanced4gLteModeUserSetting {
    private volatile int mSubId;

    public ImsQueryEnhanced4gLteModeUserSetting(int i) {
        this.mSubId = i;
    }

    public boolean query() {
        try {
            return ImsMmTelManager.createForSubscriptionId(this.mSubId).isAdvancedCallingSettingEnabled();
        } catch (IllegalArgumentException e) {
            Log.w("QueryEnhanced4gLteModeUserSetting", "fail to get VoLte settings. subId=" + this.mSubId, e);
            return false;
        }
    }
}
