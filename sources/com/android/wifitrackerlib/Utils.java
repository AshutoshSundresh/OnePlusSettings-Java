package com.android.wifitrackerlib;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkKey;
import android.net.NetworkScoreManager;
import android.net.ScoredNetwork;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiNetworkScoreCache;
import android.os.PersistableBundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.Annotation;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.style.ClickableSpan;
import android.view.View;
import com.android.settingslib.HelpUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringJoiner;

/* access modifiers changed from: package-private */
public class Utils {
    private static NetworkScoreManager sNetworkScoreManager;

    private static int roundToClosestSpeedEnum(int i) {
        if (i == 0) {
            return 0;
        }
        if (i < 7) {
            return 5;
        }
        if (i < 15) {
            return 10;
        }
        return i < 25 ? 20 : 30;
    }

    private static String getActiveScorerPackage(Context context) {
        if (sNetworkScoreManager == null) {
            sNetworkScoreManager = (NetworkScoreManager) context.getSystemService(NetworkScoreManager.class);
        }
        return sNetworkScoreManager.getActiveScorerPackage();
    }

    static ScanResult getBestScanResultByLevel(List<ScanResult> list) {
        if (list.isEmpty()) {
            return null;
        }
        return (ScanResult) Collections.max(list, Comparator.comparingInt($$Lambda$Utils$wGn2sVTZ5l1wFLkqd7rxKtPh0RU.INSTANCE));
    }

    static List<Integer> getSecurityTypesFromScanResult(ScanResult scanResult) {
        ArrayList arrayList = new ArrayList();
        String str = scanResult.capabilities;
        if (str == null) {
            arrayList.add(0);
        } else if (str.contains("WAPI-PSK")) {
            arrayList.add(8);
        } else if (scanResult.capabilities.contains("WAPI-CERT")) {
            arrayList.add(9);
        } else if (scanResult.capabilities.contains("PSK") && scanResult.capabilities.contains("SAE")) {
            arrayList.add(2);
            arrayList.add(5);
        } else if (scanResult.capabilities.contains("OWE_TRANSITION")) {
            arrayList.add(0);
            arrayList.add(4);
        } else if (scanResult.capabilities.contains("OWE")) {
            arrayList.add(4);
        } else if (scanResult.capabilities.contains("WEP")) {
            arrayList.add(1);
        } else if (scanResult.capabilities.contains("SAE")) {
            arrayList.add(5);
        } else if (scanResult.capabilities.contains("PSK")) {
            arrayList.add(2);
        } else if (scanResult.capabilities.contains("EAP_SUITE_B_192")) {
            arrayList.add(6);
        } else if (scanResult.capabilities.contains("EAP")) {
            arrayList.add(3);
        } else {
            arrayList.add(0);
        }
        return arrayList;
    }

    static int getSecurityTypeFromWifiConfiguration(WifiConfiguration wifiConfiguration) {
        if (wifiConfiguration.allowedKeyManagement.get(8)) {
            return 5;
        }
        if (wifiConfiguration.allowedKeyManagement.get(1)) {
            return 2;
        }
        if (wifiConfiguration.allowedKeyManagement.get(10)) {
            return 6;
        }
        if (wifiConfiguration.allowedKeyManagement.get(2) || wifiConfiguration.allowedKeyManagement.get(3)) {
            return 3;
        }
        if (wifiConfiguration.allowedKeyManagement.get(9)) {
            return 4;
        }
        if (wifiConfiguration.allowedKeyManagement.get(13)) {
            return 8;
        }
        if (wifiConfiguration.allowedKeyManagement.get(14)) {
            return 9;
        }
        if (wifiConfiguration.wepKeys[0] != null) {
            return 1;
        }
        return 0;
    }

    /* JADX WARNING: Removed duplicated region for block: B:125:0x02d4  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x02f2  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x014e  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x015c  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x016c  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x018c  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x01a7  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x01b5  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x01c4  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x01ef  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static java.util.Map<java.lang.String, java.util.List<android.net.wifi.ScanResult>> mapScanResultsToKey(java.util.List<android.net.wifi.ScanResult> r29, boolean r30, java.util.Map<java.lang.String, android.net.wifi.WifiConfiguration> r31, boolean r32, boolean r33, boolean r34, boolean r35) {
        /*
        // Method dump skipped, instructions count: 815
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.wifitrackerlib.Utils.mapScanResultsToKey(java.util.List, boolean, java.util.Map, boolean, boolean, boolean, boolean):java.util.Map");
    }

    static /* synthetic */ boolean lambda$mapScanResultsToKey$1(ScanResult scanResult) {
        return !TextUtils.isEmpty(scanResult.SSID);
    }

    static int getAverageSpeedFromScanResults(WifiNetworkScoreCache wifiNetworkScoreCache, List<ScanResult> list) {
        int calculateBadge;
        int i = 0;
        int i2 = 0;
        for (ScanResult scanResult : list) {
            ScoredNetwork scoredNetwork = wifiNetworkScoreCache.getScoredNetwork(scanResult);
            if (!(scoredNetwork == null || (calculateBadge = scoredNetwork.calculateBadge(scanResult.level)) == 0)) {
                i++;
                i2 += calculateBadge;
            }
        }
        if (i == 0) {
            return 0;
        }
        return roundToClosestSpeedEnum(i2 / i);
    }

    static int getSpeedFromWifiInfo(WifiNetworkScoreCache wifiNetworkScoreCache, WifiInfo wifiInfo) {
        ScoredNetwork scoredNetwork = wifiNetworkScoreCache.getScoredNetwork(NetworkKey.createFromWifiInfo(wifiInfo));
        if (scoredNetwork == null) {
            return 0;
        }
        return roundToClosestSpeedEnum(scoredNetwork.calculateBadge(wifiInfo.getRssi()));
    }

    static String getAppLabel(Context context, String str) {
        try {
            String string = Settings.Global.getString(context.getContentResolver(), "use_open_wifi_package");
            if (!TextUtils.isEmpty(string) && TextUtils.equals(str, getActiveScorerPackage(context))) {
                str = string;
            }
            return context.getPackageManager().getApplicationInfoAsUser(str, 0, UserHandle.getUserId(-2)).loadLabel(context.getPackageManager()).toString();
        } catch (PackageManager.NameNotFoundException unused) {
            return "";
        }
    }

    static String getDisconnectedStateDescription(Context context, WifiEntry wifiEntry) {
        if (!(context == null || wifiEntry == null)) {
            WifiConfiguration wifiConfiguration = wifiEntry.getWifiConfiguration();
            if (wifiConfiguration == null) {
                return null;
            }
            if (wifiConfiguration.hasNoInternetAccess()) {
                return context.getString(wifiConfiguration.getNetworkSelectionStatus().getNetworkSelectionStatus() == 2 ? R$string.wifi_no_internet_no_reconnect : R$string.wifi_no_internet);
            } else if (wifiConfiguration.getNetworkSelectionStatus().getNetworkSelectionStatus() != 0) {
                int networkSelectionDisableReason = wifiConfiguration.getNetworkSelectionStatus().getNetworkSelectionDisableReason();
                if (networkSelectionDisableReason == 1) {
                    return context.getString(R$string.wifi_disabled_generic);
                }
                if (networkSelectionDisableReason == 2) {
                    return context.getString(R$string.wifi_disabled_password_failure);
                }
                if (networkSelectionDisableReason == 3) {
                    return context.getString(R$string.wifi_disabled_network_failure);
                }
                if (networkSelectionDisableReason == 8) {
                    return context.getString(R$string.wifi_check_password_try_again);
                }
            } else if (wifiEntry.getLevel() != -1 && wifiConfiguration.getRecentFailureReason() == 17) {
                return context.getString(R$string.wifi_ap_unable_to_handle_new_sta);
            }
        }
        return "";
    }

    static String getAutoConnectDescription(Context context, WifiEntry wifiEntry) {
        if (context == null || wifiEntry == null || !wifiEntry.canSetAutoJoinEnabled() || wifiEntry.isAutoJoinEnabled()) {
            return "";
        }
        return context.getString(R$string.auto_connect_disable);
    }

    static String getMeteredDescription(Context context, WifiEntry wifiEntry) {
        if (context == null || wifiEntry == null) {
            return "";
        }
        if (!wifiEntry.canSetMeteredChoice() && wifiEntry.getMeteredChoice() != 1) {
            return "";
        }
        if (wifiEntry.getMeteredChoice() == 1) {
            return context.getString(R$string.wifi_metered_label);
        }
        if (wifiEntry.getMeteredChoice() == 2) {
            return context.getString(R$string.wifi_unmetered_label);
        }
        if (wifiEntry.isMetered()) {
            return context.getString(R$string.wifi_metered_label);
        }
        return "";
    }

    static String getSpeedDescription(Context context, WifiEntry wifiEntry) {
        if (context == null || wifiEntry == null) {
            return "";
        }
        int speed = wifiEntry.getSpeed();
        if (speed == 5) {
            return context.getString(R$string.speed_label_slow);
        }
        if (speed == 10) {
            return context.getString(R$string.speed_label_okay);
        }
        if (speed == 20) {
            return context.getString(R$string.speed_label_fast);
        }
        if (speed != 30) {
            return "";
        }
        return context.getString(R$string.speed_label_very_fast);
    }

    static String getVerboseLoggingDescription(WifiEntry wifiEntry) {
        if (!BaseWifiTracker.isVerboseLoggingEnabled() || wifiEntry == null) {
            return "";
        }
        StringJoiner stringJoiner = new StringJoiner(" ");
        String wifiInfoDescription = wifiEntry.getWifiInfoDescription();
        if (!TextUtils.isEmpty(wifiInfoDescription)) {
            stringJoiner.add(wifiInfoDescription);
        }
        String scanResultDescription = wifiEntry.getScanResultDescription();
        if (!TextUtils.isEmpty(scanResultDescription)) {
            stringJoiner.add(scanResultDescription);
        }
        String networkSelectionDescription = wifiEntry.getNetworkSelectionDescription();
        if (!TextUtils.isEmpty(networkSelectionDescription)) {
            stringJoiner.add(networkSelectionDescription);
        }
        return stringJoiner.toString();
    }

    static String getNetworkSelectionDescription(WifiConfiguration wifiConfiguration) {
        if (wifiConfiguration == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        WifiConfiguration.NetworkSelectionStatus networkSelectionStatus = wifiConfiguration.getNetworkSelectionStatus();
        if (networkSelectionStatus.getNetworkSelectionStatus() != 0) {
            sb.append(" (" + networkSelectionStatus.getNetworkStatusString());
            if (networkSelectionStatus.getDisableTime() > 0) {
                sb.append(" " + DateUtils.formatElapsedTime((System.currentTimeMillis() - networkSelectionStatus.getDisableTime()) / 1000));
            }
            sb.append(")");
        }
        int maxNetworkSelectionDisableReason = WifiConfiguration.NetworkSelectionStatus.getMaxNetworkSelectionDisableReason();
        for (int i = 0; i <= maxNetworkSelectionDisableReason; i++) {
            int disableReasonCounter = networkSelectionStatus.getDisableReasonCounter(i);
            if (disableReasonCounter != 0) {
                sb.append(" ");
                sb.append(WifiConfiguration.NetworkSelectionStatus.getNetworkSelectionDisableReasonString(i));
                sb.append("=");
                sb.append(disableReasonCounter);
            }
        }
        return sb.toString();
    }

    static String getCurrentNetworkCapabilitiesInformation(Context context, NetworkCapabilities networkCapabilities) {
        if (!(context == null || networkCapabilities == null)) {
            if (networkCapabilities.hasCapability(17)) {
                return context.getString(context.getResources().getIdentifier("network_available_sign_in", "string", "android"));
            }
            if (networkCapabilities.hasCapability(24)) {
                return context.getString(R$string.wifi_limited_connection);
            }
            if (!networkCapabilities.hasCapability(16)) {
                if (networkCapabilities.isPrivateDnsBroken()) {
                    return context.getString(R$string.private_dns_broken);
                }
                return context.getString(R$string.wifi_connected_no_internet);
            }
        }
        return "";
    }

    static String getNetworkDetailedState(Context context, NetworkInfo networkInfo) {
        NetworkInfo.DetailedState detailedState;
        if (context == null || networkInfo == null || (detailedState = networkInfo.getDetailedState()) == null) {
            return "";
        }
        String[] stringArray = context.getResources().getStringArray(R$array.wifi_status);
        int ordinal = detailedState.ordinal();
        if (ordinal >= stringArray.length) {
            return "";
        }
        return stringArray[ordinal];
    }

    static String getCarrierNameForSubId(Context context, int i) {
        TelephonyManager telephonyManager;
        TelephonyManager createForSubscriptionId;
        CharSequence simCarrierIdName;
        if (i == -1 || (telephonyManager = (TelephonyManager) context.getSystemService("phone")) == null || (createForSubscriptionId = telephonyManager.createForSubscriptionId(i)) == null || (simCarrierIdName = createForSubscriptionId.getSimCarrierIdName()) == null) {
            return null;
        }
        return simCarrierIdName.toString();
    }

    static boolean isSimCredential(WifiConfiguration wifiConfiguration) {
        WifiEnterpriseConfig wifiEnterpriseConfig = wifiConfiguration.enterpriseConfig;
        return wifiEnterpriseConfig != null && wifiEnterpriseConfig.isAuthenticationSimBased();
    }

    static int getSubIdForConfig(Context context, WifiConfiguration wifiConfiguration) {
        SubscriptionManager subscriptionManager;
        int i = -1;
        if (wifiConfiguration.carrierId == -1 || (subscriptionManager = (SubscriptionManager) context.getSystemService("telephony_subscription_service")) == null) {
            return -1;
        }
        List<SubscriptionInfo> activeSubscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
        if (activeSubscriptionInfoList != null && !activeSubscriptionInfoList.isEmpty()) {
            int defaultDataSubscriptionId = SubscriptionManager.getDefaultDataSubscriptionId();
            for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
                if (subscriptionInfo.getCarrierId() == wifiConfiguration.carrierId && (i = subscriptionInfo.getSubscriptionId()) == defaultDataSubscriptionId) {
                    break;
                }
            }
        }
        return i;
    }

    static boolean isImsiPrivacyProtectionProvided(Context context, int i) {
        PersistableBundle configForSubId;
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) context.getSystemService("carrier_config");
        if (carrierConfigManager == null || (configForSubId = carrierConfigManager.getConfigForSubId(i)) == null || (configForSubId.getInt("imsi_key_availability_int") & 2) == 0) {
            return false;
        }
        return true;
    }

    static CharSequence getImsiProtectionDescription(Context context, WifiConfiguration wifiConfiguration) {
        int i;
        if (!(context == null || wifiConfiguration == null || !isSimCredential(wifiConfiguration))) {
            if (wifiConfiguration.carrierId == -1) {
                i = SubscriptionManager.getDefaultSubscriptionId();
            } else {
                i = getSubIdForConfig(context, wifiConfiguration);
            }
            if (i != -1 && !isImsiPrivacyProtectionProvided(context, i)) {
                return linkifyAnnotation(context, context.getText(R$string.imsi_protection_warning), "url", context.getString(R$string.help_url_imsi_protection));
            }
        }
        return "";
    }

    static CharSequence linkifyAnnotation(final Context context, CharSequence charSequence, String str, final String str2) {
        SpannableString spannableString = new SpannableString(charSequence);
        Annotation[] annotationArr = (Annotation[]) spannableString.getSpans(0, spannableString.length(), Annotation.class);
        for (Annotation annotation : annotationArr) {
            if (TextUtils.equals(annotation.getValue(), str)) {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(spannableString);
                AnonymousClass1 r8 = new ClickableSpan() {
                    /* class com.android.wifitrackerlib.Utils.AnonymousClass1 */

                    public void onClick(View view) {
                        view.startActivityForResult(HelpUtils.getHelpIntent(context, str2, view.getClass().getName()), 0);
                    }
                };
                spannableStringBuilder.setSpan(r8, spannableString.getSpanStart(annotation), spannableString.getSpanEnd(annotation), spannableString.getSpanFlags(r8));
                return spannableStringBuilder;
            }
        }
        return charSequence;
    }
}
