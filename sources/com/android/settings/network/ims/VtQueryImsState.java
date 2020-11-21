package com.android.settings.network.ims;

import android.content.Context;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionManager;
import android.telephony.ims.ImsException;
import android.util.Log;

public class VtQueryImsState extends ImsQueryController {
    private Context mContext;
    private int mSubId;

    public VtQueryImsState(Context context, int i) {
        super(2, 0, 1);
        this.mContext = context;
        this.mSubId = i;
    }

    /* access modifiers changed from: package-private */
    public boolean isEnabledByUser(int i) {
        if (!SubscriptionManager.isValidSubscriptionId(i)) {
            return false;
        }
        return new ImsQueryVtUserSetting(i).query();
    }

    public boolean isReadyToVideoCall() {
        if (!isProvisionedOnDevice(this.mSubId)) {
            return false;
        }
        try {
            if (!isEnabledByPlatform(this.mSubId) || !isServiceStateReady(this.mSubId)) {
                return false;
            }
            return true;
        } catch (ImsException | IllegalArgumentException | InterruptedException e) {
            Log.w("VtQueryImsState", "fail to get Vt ready. subId=" + this.mSubId, e);
            return false;
        }
    }

    public boolean isAllowUserControl() {
        if (!SubscriptionManager.isValidSubscriptionId(this.mSubId)) {
            return false;
        }
        if (!isTtyEnabled(this.mContext) || isTtyOnVolteEnabled(this.mSubId)) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean isTtyEnabled(Context context) {
        return ((TelecomManager) context.getSystemService(TelecomManager.class)).getCurrentTtyMode() != 0;
    }

    public boolean isEnabledByUser() {
        if (!SubscriptionManager.isValidSubscriptionId(this.mSubId)) {
            return false;
        }
        return isEnabledByUser(this.mSubId);
    }
}
