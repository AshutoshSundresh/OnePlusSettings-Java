package com.oneplus.security.network.simcard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.oneplus.security.utils.LogUtils;

public class SimcardStateChangeReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        String str;
        if ("android.intent.action.SIM_STATE_CHANGED".equals(intent.getAction())) {
            try {
                str = intent.getStringExtra("ss");
            } catch (Exception e) {
                e.printStackTrace();
                str = "";
            }
            LogUtils.d("SimcardStateChangeReceiver", "action_sim_state_changed state = " + str);
            if ("ABSENT".equals(str)) {
                SimcardStateManager.setShouldAlertSimcardHasPopedOut(context, true, 0);
                SimcardStateManager.setShouldAlertSimcardHasPopedOut(context, true, 1);
            }
        }
    }
}
