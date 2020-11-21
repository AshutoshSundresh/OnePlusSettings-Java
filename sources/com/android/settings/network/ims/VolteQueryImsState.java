package com.android.settings.network.ims;

import android.content.Context;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionManager;
import android.telephony.ims.ImsException;
import android.util.Log;

public class VolteQueryImsState extends ImsQueryController {
    private Context mContext;
    private int mSubId;

    public VolteQueryImsState(Context context, int i) {
        super(1, 0, 1);
        this.mContext = context;
        this.mSubId = i;
    }

    /* access modifiers changed from: package-private */
    public boolean isEnabledByUser(int i) {
        if (!SubscriptionManager.isValidSubscriptionId(i)) {
            return false;
        }
        return new ImsQueryEnhanced4gLteModeUserSetting(i).query();
    }

    public boolean isVoLteProvisioned() {
        if (!SubscriptionManager.isValidSubscriptionId(this.mSubId) || !isProvisionedOnDevice(this.mSubId)) {
            return false;
        }
        try {
            return isEnabledByPlatform(this.mSubId);
        } catch (ImsException | IllegalArgumentException | InterruptedException e) {
            Log.w("VolteQueryImsState", "fail to get VoLte supporting status. subId=" + this.mSubId, e);
            return false;
        }
    }

    public boolean isReadyToVoLte() {
        if (!SubscriptionManager.isValidSubscriptionId(this.mSubId) || !isVoLteProvisioned()) {
            return false;
        }
        try {
            return isServiceStateReady(this.mSubId);
        } catch (ImsException | IllegalArgumentException | InterruptedException e) {
            Log.w("VolteQueryImsState", "fail to get VoLte service status. subId=" + this.mSubId, e);
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
