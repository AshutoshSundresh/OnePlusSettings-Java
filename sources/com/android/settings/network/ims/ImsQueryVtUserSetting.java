package com.android.settings.network.ims;

import android.telephony.ims.ImsMmTelManager;
import android.util.Log;

public class ImsQueryVtUserSetting {
    private volatile int mSubId;

    public ImsQueryVtUserSetting(int i) {
        this.mSubId = i;
    }

    public boolean query() {
        try {
            return ImsMmTelManager.createForSubscriptionId(this.mSubId).isVtSettingEnabled();
        } catch (IllegalArgumentException e) {
            Log.w("QueryVtUserSetting", "fail to get VT settings. subId=" + this.mSubId, e);
            return false;
        }
    }
}
