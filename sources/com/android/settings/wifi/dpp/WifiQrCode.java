package com.android.settings.wifi.dpp;

import android.text.TextUtils;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class WifiQrCode {
    private String mInformation;
    private String mPublicKey;
    private String mQrCode;
    private String mScheme;
    private WifiNetworkConfig mWifiNetworkConfig;

    public WifiQrCode(String str) throws IllegalArgumentException {
        if (!TextUtils.isEmpty(str)) {
            this.mQrCode = str;
            if (str.startsWith("DPP:")) {
                this.mScheme = "DPP";
                parseWifiDppQrCode(str);
            } else if (str.startsWith("WIFI:")) {
                this.mScheme = "WIFI";
                parseZxingWifiQrCode(str);
            } else {
                throw new IllegalArgumentException("Invalid scheme");
            }
        } else {
            throw new IllegalArgumentException("Empty QR code");
        }
    }

    private void parseWifiDppQrCode(String str) throws IllegalArgumentException {
        List<String> keyValueList = getKeyValueList(str, "DPP:", ";");
        String valueOrNull = getValueOrNull(keyValueList, "K:");
        if (!TextUtils.isEmpty(valueOrNull)) {
            this.mPublicKey = valueOrNull;
            this.mInformation = getValueOrNull(keyValueList, "I:");
            return;
        }
        throw new IllegalArgumentException("Invalid format");
    }

    private void parseZxingWifiQrCode(String str) throws IllegalArgumentException {
        List<String> keyValueList = getKeyValueList(str, "WIFI:", ";");
        String valueOrNull = getValueOrNull(keyValueList, "T:");
        String valueOrNull2 = getValueOrNull(keyValueList, "S:");
        String valueOrNull3 = getValueOrNull(keyValueList, "P:");
        WifiNetworkConfig validConfigOrNull = WifiNetworkConfig.getValidConfigOrNull(removeBackSlash(valueOrNull), removeBackSlash(valueOrNull2), removeBackSlash(valueOrNull3), "true".equalsIgnoreCase(getValueOrNull(keyValueList, "H:")), -1, false);
        this.mWifiNetworkConfig = validConfigOrNull;
        if (validConfigOrNull == null) {
            throw new IllegalArgumentException("Invalid format");
        }
    }

    private List<String> getKeyValueList(String str, String str2, String str3) {
        String substring = str.substring(str2.length());
        return Arrays.asList(substring.split("(?<!\\\\)" + Pattern.quote(str3)));
    }

    private String getValueOrNull(List<String> list, String str) {
        for (String str2 : list) {
            if (str2.startsWith(str)) {
                return str2.substring(str.length());
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public String removeBackSlash(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        char[] charArray = str.toCharArray();
        boolean z = false;
        for (char c : charArray) {
            if (c != '\\') {
                sb.append(c);
            } else if (z) {
                sb.append(c);
            } else {
                z = true;
            }
            z = false;
        }
        return sb.toString();
    }

    /* access modifiers changed from: package-private */
    public String getQrCode() {
        return this.mQrCode;
    }

    public String getScheme() {
        return this.mScheme;
    }

    /* access modifiers changed from: package-private */
    public String getPublicKey() {
        return this.mPublicKey;
    }

    public String getInformation() {
        return this.mInformation;
    }

    /* access modifiers changed from: package-private */
    public WifiNetworkConfig getWifiNetworkConfig() {
        WifiNetworkConfig wifiNetworkConfig = this.mWifiNetworkConfig;
        if (wifiNetworkConfig == null) {
            return null;
        }
        return new WifiNetworkConfig(wifiNetworkConfig);
    }

    static WifiQrCode getValidWifiDppQrCodeOrNull(String str) {
        try {
            WifiQrCode wifiQrCode = new WifiQrCode(str);
            if ("DPP".equals(wifiQrCode.getScheme())) {
                return wifiQrCode;
            }
            return null;
        } catch (IllegalArgumentException unused) {
        }
    }
}
