package com.android.settings.network.ims;

import android.telephony.ims.ImsMmTelManager;
import android.util.Log;

public class ImsQueryTtyOnVolteStat {
    private volatile int mSubId;

    public ImsQueryTtyOnVolteStat(int i) {
        this.mSubId = i;
    }

    public boolean query() {
        try {
            return ImsMmTelManager.createForSubscriptionId(this.mSubId).isTtyOverVolteEnabled();
        } catch (IllegalArgumentException e) {
            Log.w("QueryTtyOnVolteStat", "fail to get VoLte Tty Stat. subId=" + this.mSubId, e);
            return false;
        }
    }
}
