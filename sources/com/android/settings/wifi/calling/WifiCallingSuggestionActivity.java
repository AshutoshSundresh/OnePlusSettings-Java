package com.android.settings.wifi.calling;

import android.content.Context;
import android.telephony.SubscriptionManager;
import com.android.settings.SettingsActivity;
import com.android.settings.network.ims.WifiCallingQueryImsState;

public class WifiCallingSuggestionActivity extends SettingsActivity {
    public static boolean isSuggestionComplete(Context context) {
        WifiCallingQueryImsState wifiCallingQueryImsState = new WifiCallingQueryImsState(context, SubscriptionManager.getDefaultVoiceSubscriptionId());
        return !wifiCallingQueryImsState.isWifiCallingProvisioned() || (wifiCallingQueryImsState.isEnabledByUser() && wifiCallingQueryImsState.isAllowUserControl());
    }
}
