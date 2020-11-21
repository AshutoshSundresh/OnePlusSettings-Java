package com.android.settings.wifi.tether;

import android.net.wifi.WifiClient;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerExecutor;
import java.util.List;

public class WifiTetherSoftApManager {
    private Handler mHandler;
    private WifiManager.SoftApCallback mSoftApCallback = new WifiManager.SoftApCallback() {
        /* class com.android.settings.wifi.tether.WifiTetherSoftApManager.AnonymousClass1 */

        public void onStateChanged(int i, int i2) {
            WifiTetherSoftApManager.this.mWifiTetherSoftApCallback.onStateChanged(i, i2);
        }

        public void onConnectedClientsChanged(List<WifiClient> list) {
            WifiTetherSoftApManager.this.mWifiTetherSoftApCallback.onConnectedClientsChanged(list);
        }
    };
    private WifiManager mWifiManager;
    private WifiTetherSoftApCallback mWifiTetherSoftApCallback;

    public interface WifiTetherSoftApCallback {
        void onConnectedClientsChanged(List<WifiClient> list);

        void onStateChanged(int i, int i2);
    }

    WifiTetherSoftApManager(WifiManager wifiManager, WifiTetherSoftApCallback wifiTetherSoftApCallback) {
        this.mWifiManager = wifiManager;
        this.mWifiTetherSoftApCallback = wifiTetherSoftApCallback;
        this.mHandler = new Handler();
    }

    public void registerSoftApCallback() {
        this.mWifiManager.registerSoftApCallback(new HandlerExecutor(this.mHandler), this.mSoftApCallback);
    }

    public void unRegisterSoftApCallback() {
        this.mWifiManager.unregisterSoftApCallback(this.mSoftApCallback);
    }
}
