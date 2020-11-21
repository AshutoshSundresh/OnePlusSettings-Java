package com.android.settings.network.ims;

import android.telephony.ims.ProvisioningManager;
import android.util.Log;

public class ImsQueryProvisioningStat {
    private volatile int mCapability;
    private volatile int mSubId;
    private volatile int mTech;

    public ImsQueryProvisioningStat(int i, int i2, int i3) {
        this.mSubId = i;
        this.mCapability = i2;
        this.mTech = i3;
    }

    public boolean query() {
        try {
            return ProvisioningManager.createForSubscriptionId(this.mSubId).getProvisioningStatusForCapability(this.mCapability, this.mTech);
        } catch (IllegalArgumentException e) {
            Log.w("QueryPrivisioningStat", "fail to get Provisioning stat. subId=" + this.mSubId, e);
            return false;
        }
    }
}
