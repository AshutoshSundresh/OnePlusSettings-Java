package com.android.settings.sim;

import android.content.Context;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import java.util.ArrayList;
import java.util.List;

public class CallsSimListDialogFragment extends SimListDialogFragment {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable, com.android.settings.sim.SimListDialogFragment
    public int getMetricsCategory() {
        return 1708;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.sim.SimListDialogFragment
    public List<SubscriptionInfo> getCurrentSubscriptions() {
        Context context = getContext();
        SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        List<PhoneAccountHandle> callCapablePhoneAccounts = ((TelecomManager) context.getSystemService(TelecomManager.class)).getCallCapablePhoneAccounts();
        ArrayList arrayList = new ArrayList();
        if (callCapablePhoneAccounts == null) {
            return arrayList;
        }
        for (PhoneAccountHandle phoneAccountHandle : callCapablePhoneAccounts) {
            int subscriptionId = telephonyManager.getSubscriptionId(phoneAccountHandle);
            if (subscriptionId != -1) {
                arrayList.add(subscriptionManager.getActiveSubscriptionInfo(subscriptionId));
            }
        }
        return arrayList;
    }
}
