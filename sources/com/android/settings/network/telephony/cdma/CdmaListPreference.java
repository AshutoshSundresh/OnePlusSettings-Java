package com.android.settings.network.telephony.cdma;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import androidx.preference.ListPreference;

public class CdmaListPreference extends ListPreference {
    private TelephonyManager mTelephonyManager;

    public CdmaListPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.preference.DialogPreference, androidx.preference.Preference
    public void onClick() {
        TelephonyManager telephonyManager = this.mTelephonyManager;
        if (telephonyManager == null || !telephonyManager.getEmergencyCallbackMode()) {
            super.onClick();
        }
    }

    public void setSubId(int i) {
        this.mTelephonyManager = ((TelephonyManager) getContext().getSystemService(TelephonyManager.class)).createForSubscriptionId(i);
    }
}
