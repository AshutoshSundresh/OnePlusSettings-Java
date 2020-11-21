package com.android.settings.wifi;

import android.net.NetworkCapabilities;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import com.android.settingslib.wifi.AccessPoint;
import java.nio.charset.StandardCharsets;

public class WifiUtils {
    public static boolean isSSIDTooLong(String str) {
        if (!TextUtils.isEmpty(str) && str.getBytes(StandardCharsets.UTF_8).length > 32) {
            return true;
        }
        return false;
    }

    public static boolean isSSIDTooShort(String str) {
        if (!TextUtils.isEmpty(str) && str.length() >= 1) {
            return false;
        }
        return true;
    }

    public static boolean isHotspotPasswordValid(String str) {
        int length;
        if (!TextUtils.isEmpty(str) && (length = str.length()) >= 8 && length <= 63) {
            return true;
        }
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x003b, code lost:
        if (r2.getPackageUidAsUser(r4.getPackageName(), r1.getDeviceOwnerUserId()) == r7.creatorUid) goto L_0x003d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x005d, code lost:
        if (r2.getPackageUidAsUser(r1.getPackageName(), r3) == r7.creatorUid) goto L_0x003d;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isNetworkLockedDown(android.content.Context r6, android.net.wifi.WifiConfiguration r7) {
        /*
        // Method dump skipped, instructions count: 115
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.wifi.WifiUtils.isNetworkLockedDown(android.content.Context, android.net.wifi.WifiConfiguration):boolean");
    }

    public static boolean canSignIntoNetwork(NetworkCapabilities networkCapabilities) {
        return networkCapabilities != null && networkCapabilities.hasCapability(17);
    }

    public static WifiConfiguration getWifiConfig(AccessPoint accessPoint, ScanResult scanResult, String str) {
        int i;
        if (accessPoint == null && scanResult == null) {
            throw new IllegalArgumentException("At least one of AccessPoint and ScanResult input is required.");
        }
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        if (accessPoint == null) {
            wifiConfiguration.SSID = AccessPoint.convertToQuotedString(scanResult.SSID);
            i = getAccessPointSecurity(scanResult);
        } else {
            if (!accessPoint.isSaved()) {
                wifiConfiguration.SSID = AccessPoint.convertToQuotedString(accessPoint.getSsidStr());
            } else {
                wifiConfiguration.networkId = accessPoint.getConfig().networkId;
                wifiConfiguration.hiddenSSID = accessPoint.getConfig().hiddenSSID;
            }
            i = accessPoint.getSecurity();
        }
        switch (i) {
            case 0:
                wifiConfiguration.setSecurityParams(0);
                break;
            case 1:
                wifiConfiguration.setSecurityParams(1);
                if (!TextUtils.isEmpty(str)) {
                    int length = str.length();
                    if ((length != 10 && length != 26 && length != 58) || !str.matches("[0-9A-Fa-f]*")) {
                        String[] strArr = wifiConfiguration.wepKeys;
                        strArr[0] = '\"' + str + '\"';
                        break;
                    } else {
                        wifiConfiguration.wepKeys[0] = str;
                        break;
                    }
                }
                break;
            case 2:
                wifiConfiguration.setSecurityParams(2);
                if (!TextUtils.isEmpty(str)) {
                    if (!str.matches("[0-9A-Fa-f]{64}")) {
                        wifiConfiguration.preSharedKey = '\"' + str + '\"';
                        break;
                    } else {
                        wifiConfiguration.preSharedKey = str;
                        break;
                    }
                }
                break;
            case 3:
            case 6:
                if (i == 6) {
                    wifiConfiguration.setSecurityParams(5);
                } else {
                    wifiConfiguration.setSecurityParams(3);
                }
                if (!TextUtils.isEmpty(str)) {
                    wifiConfiguration.enterpriseConfig.setPassword(str);
                    break;
                }
                break;
            case 4:
                wifiConfiguration.setSecurityParams(6);
                break;
            case 5:
                wifiConfiguration.setSecurityParams(4);
                if (!TextUtils.isEmpty(str)) {
                    wifiConfiguration.preSharedKey = '\"' + str + '\"';
                    break;
                }
                break;
            case 8:
                wifiConfiguration.allowedKeyManagement.set(13);
                if (!TextUtils.isEmpty(str)) {
                    if (!str.matches("[0-9A-Fa-f]{64}")) {
                        wifiConfiguration.preSharedKey = '\"' + str + '\"';
                        break;
                    } else {
                        wifiConfiguration.preSharedKey = str;
                        break;
                    }
                }
                break;
            case 9:
                wifiConfiguration.allowedKeyManagement.set(14);
                break;
        }
        return wifiConfiguration;
    }

    public static int getAccessPointSecurity(ScanResult scanResult) {
        if (scanResult.capabilities.contains("WEP")) {
            return 1;
        }
        if (scanResult.capabilities.contains("SAE")) {
            return 5;
        }
        if (scanResult.capabilities.contains("WAPI-PSK")) {
            return 8;
        }
        if (scanResult.capabilities.contains("WAPI-CERT")) {
            return 9;
        }
        if (scanResult.capabilities.contains("PSK")) {
            return 2;
        }
        if (scanResult.capabilities.contains("EAP_SUITE_B_192")) {
            return 6;
        }
        if (scanResult.capabilities.contains("EAP")) {
            return 3;
        }
        return scanResult.capabilities.contains("OWE") ? 4 : 0;
    }

    public static int getConnectingType(AccessPoint accessPoint) {
        WifiConfiguration config = accessPoint.getConfig();
        if (accessPoint.isOsuProvider()) {
            return 3;
        }
        if (accessPoint.getSecurity() == 0 || accessPoint.getSecurity() == 4) {
            return 1;
        }
        if ((!accessPoint.isSaved() || config == null || config.getNetworkSelectionStatus() == null || !config.getNetworkSelectionStatus().hasEverConnected()) && !accessPoint.isPasspoint()) {
            return 0;
        }
        return 2;
    }

    public static boolean isSupportDualBand() {
        String str = SystemProperties.get("persist.vendor.wifi.softap.dualband", "0");
        Log.i("WifiUtils", "Dualband:" + str);
        return str.equals("1");
    }
}
