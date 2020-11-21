package com.android.settingslib.wifi;

import android.content.Context;
import androidx.annotation.Keep;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.wifi.WifiTracker;

public class WifiTrackerFactory {
    private static WifiTracker sTestingWifiTracker;

    @Keep
    public static void setTestingWifiTracker(WifiTracker wifiTracker) {
        sTestingWifiTracker = wifiTracker;
    }

    public static WifiTracker create(Context context, WifiTracker.WifiListener wifiListener, Lifecycle lifecycle, boolean z, boolean z2) {
        WifiTracker wifiTracker = sTestingWifiTracker;
        if (wifiTracker != null) {
            return wifiTracker;
        }
        return new WifiTracker(context, wifiListener, lifecycle, z, z2);
    }
}
