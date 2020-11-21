package com.android.settings.wifi.dpp;

import android.content.Context;
import android.text.TextUtils;

public class AdbQrCode extends WifiQrCode {
    private WifiNetworkConfig mAdbConfig;

    public AdbQrCode(String str) throws IllegalArgumentException {
        super(str);
        if ("WIFI".equals(getScheme())) {
            WifiNetworkConfig wifiNetworkConfig = getWifiNetworkConfig();
            this.mAdbConfig = wifiNetworkConfig;
            if (!"ADB".equals(wifiNetworkConfig.getSecurity())) {
                throw new IllegalArgumentException("Invalid security type");
            } else if (TextUtils.isEmpty(this.mAdbConfig.getSsid())) {
                throw new IllegalArgumentException("Empty service name");
            } else if (TextUtils.isEmpty(this.mAdbConfig.getPreSharedKey())) {
                throw new IllegalArgumentException("Empty password");
            }
        } else {
            throw new IllegalArgumentException("DPP format not supported for ADB QR code");
        }
    }

    public WifiNetworkConfig getAdbNetworkConfig() {
        return this.mAdbConfig;
    }

    public static void triggerVibrationForQrCodeRecognition(Context context) {
        WifiDppUtils.triggerVibrationForQrCodeRecognition(context);
    }
}
