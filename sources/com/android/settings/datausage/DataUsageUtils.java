package com.android.settings.datausage;

import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkTemplate;
import android.os.RemoteException;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.BidiFormatter;
import android.text.format.Formatter;
import android.util.Log;
import com.android.settings.datausage.lib.DataUsageLib;
import com.android.settings.network.ProxySubscriptionManager;
import java.util.List;

public final class DataUsageUtils extends com.android.settingslib.net.DataUsageUtils {
    public static CharSequence formatDataUsage(Context context, long j) {
        Formatter.BytesResult formatBytes = Formatter.formatBytes(context.getResources(), j, 8);
        return BidiFormatter.getInstance().unicodeWrap(context.getString(17040206, formatBytes.value, formatBytes.units));
    }

    public static boolean hasEthernet(Context context) {
        if (!((ConnectivityManager) context.getSystemService(ConnectivityManager.class)).isNetworkSupported(9)) {
            return false;
        }
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        try {
            NetworkStats.Bucket querySummaryForUser = ((NetworkStatsManager) context.getSystemService(NetworkStatsManager.class)).querySummaryForUser(9, telephonyManager.getSubscriberId(), 0, System.currentTimeMillis());
            if (querySummaryForUser == null) {
                return false;
            }
            if (querySummaryForUser.getRxBytes() > 0 || querySummaryForUser.getTxBytes() > 0) {
                return true;
            }
            return false;
        } catch (RemoteException e) {
            Log.e("DataUsageUtils", "Exception querying network detail.", e);
            return false;
        }
    }

    public static boolean hasMobileData(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(ConnectivityManager.class);
        if (connectivityManager == null || !connectivityManager.isNetworkSupported(0)) {
            return false;
        }
        return true;
    }

    public static boolean hasReadyMobileRadio(Context context) {
        List<SubscriptionInfo> activeSubscriptionsInfo = ProxySubscriptionManager.getInstance(context).getActiveSubscriptionsInfo();
        if (activeSubscriptionsInfo == null) {
            return false;
        }
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        boolean z = true;
        for (SubscriptionInfo subscriptionInfo : activeSubscriptionsInfo) {
            z &= telephonyManager.getSimState(subscriptionInfo.getSimSlotIndex()) == 5;
        }
        if (!((ConnectivityManager) context.getSystemService(ConnectivityManager.class)).isNetworkSupported(0) || !z) {
            return false;
        }
        return true;
    }

    public static boolean hasWifiRadio(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(ConnectivityManager.class);
        if (connectivityManager == null || !connectivityManager.isNetworkSupported(1)) {
            return false;
        }
        return true;
    }

    public static int getDefaultSubscriptionId(Context context) {
        int defaultDataSubscriptionId = SubscriptionManager.getDefaultDataSubscriptionId();
        if (SubscriptionManager.isValidSubscriptionId(defaultDataSubscriptionId)) {
            return defaultDataSubscriptionId;
        }
        ProxySubscriptionManager instance = ProxySubscriptionManager.getInstance(context);
        List<SubscriptionInfo> activeSubscriptionsInfo = instance.getActiveSubscriptionsInfo();
        if (activeSubscriptionsInfo == null || activeSubscriptionsInfo.size() <= 0) {
            activeSubscriptionsInfo = instance.getAccessibleSubscriptionsInfo();
        }
        if (activeSubscriptionsInfo == null || activeSubscriptionsInfo.size() <= 0) {
            return -1;
        }
        return activeSubscriptionsInfo.get(0).getSubscriptionId();
    }

    public static NetworkTemplate getDefaultTemplate(Context context, int i) {
        if (SubscriptionManager.isValidSubscriptionId(i) && hasMobileData(context)) {
            return DataUsageLib.getMobileTemplate(context, i);
        }
        if (hasWifiRadio(context)) {
            return NetworkTemplate.buildTemplateWifiWildcard();
        }
        return NetworkTemplate.buildTemplateEthernet();
    }
}
