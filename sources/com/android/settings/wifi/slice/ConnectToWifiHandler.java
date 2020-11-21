package com.android.settings.wifi.slice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import com.android.settings.wifi.WifiConnectListener;
import com.android.settings.wifi.WifiUtils;
import com.android.settingslib.wifi.AccessPoint;

public class ConnectToWifiHandler extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (context != null && intent != null) {
            Network network = (Network) intent.getParcelableExtra("android.net.extra.NETWORK");
            Bundle bundleExtra = intent.getBundleExtra("access_point_state");
            if (network != null) {
                WifiScanWorker.clearClickedWifi();
                ((ConnectivityManager) context.getSystemService(ConnectivityManager.class)).startCaptivePortalApp(network);
            } else if (bundleExtra != null) {
                connect(context, new AccessPoint(context, bundleExtra));
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void connect(Context context, AccessPoint accessPoint) {
        ContextualWifiScanWorker.saveSession();
        WifiScanWorker.saveClickedWifi(accessPoint);
        WifiManager.ActionListener wifiConnectListener = new WifiConnectListener(context);
        int connectingType = WifiUtils.getConnectingType(accessPoint);
        if (connectingType == 1) {
            accessPoint.generateOpenNetworkConfig();
        } else if (connectingType != 2) {
            if (connectingType == 3) {
                accessPoint.startOsuProvisioning(wifiConnectListener);
                return;
            }
            return;
        }
        ((WifiManager) context.getSystemService(WifiManager.class)).connect(accessPoint.getConfig(), wifiConnectListener);
    }
}
