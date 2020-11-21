package com.android.wifitrackerlib;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkScoreManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import androidx.lifecycle.Lifecycle;
import java.time.Clock;

public abstract class NetworkDetailsTracker extends BaseWifiTracker {
    public abstract WifiEntry getWifiEntry();

    public static NetworkDetailsTracker createNetworkDetailsTracker(Lifecycle lifecycle, Context context, WifiManager wifiManager, ConnectivityManager connectivityManager, NetworkScoreManager networkScoreManager, Handler handler, Handler handler2, Clock clock, long j, long j2, String str) {
        if (str.startsWith("StandardWifiEntry:") || str.startsWith("NetworkRequestEntry:")) {
            return new StandardNetworkDetailsTracker(lifecycle, context, wifiManager, connectivityManager, networkScoreManager, handler, handler2, clock, j, j2, str);
        }
        if (str.startsWith("PasspointWifiEntry:")) {
            return new PasspointNetworkDetailsTracker(lifecycle, context, wifiManager, connectivityManager, networkScoreManager, handler, handler2, clock, j, j2, str);
        }
        throw new IllegalArgumentException("Key does not contain valid key prefix!");
    }

    NetworkDetailsTracker(Lifecycle lifecycle, Context context, WifiManager wifiManager, ConnectivityManager connectivityManager, NetworkScoreManager networkScoreManager, Handler handler, Handler handler2, Clock clock, long j, long j2, String str) {
        super(lifecycle, context, wifiManager, connectivityManager, networkScoreManager, handler, handler2, clock, j, j2, null, str);
    }
}
