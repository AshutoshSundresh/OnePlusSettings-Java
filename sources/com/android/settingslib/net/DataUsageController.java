package com.android.settingslib.net;

import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.INetworkStatsService;
import android.net.NetworkPolicy;
import android.net.NetworkPolicyManager;
import android.net.NetworkTemplate;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.Range;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import java.time.ZonedDateTime;
import java.util.Formatter;
import java.util.Iterator;
import java.util.Locale;

public class DataUsageController {
    private static final StringBuilder PERIOD_BUILDER = new StringBuilder(50);
    private static final Formatter PERIOD_FORMATTER = new Formatter(PERIOD_BUILDER, Locale.getDefault());
    private final Context mContext;
    private NetworkNameProvider mNetworkController;
    private final NetworkStatsManager mNetworkStatsManager;
    private final NetworkPolicyManager mPolicyManager = NetworkPolicyManager.from(this.mContext);
    private int mSubscriptionId;

    public static class DataUsageInfo {
        public String carrier;
        public long cycleEnd;
        public long cycleStart;
        public long limitLevel;
        public String period;
        public long startDate;
        public long usageLevel;
        public long warningLevel;
    }

    public interface NetworkNameProvider {
        String getMobileDataNetworkName();
    }

    static {
        Log.isLoggable("DataUsageController", 3);
    }

    public DataUsageController(Context context) {
        this.mContext = context;
        ConnectivityManager.from(context);
        INetworkStatsService.Stub.asInterface(ServiceManager.getService("netstats"));
        this.mNetworkStatsManager = (NetworkStatsManager) context.getSystemService(NetworkStatsManager.class);
        this.mSubscriptionId = -1;
    }

    public void setSubscriptionId(int i) {
        this.mSubscriptionId = i;
    }

    public long getDefaultWarningLevel() {
        return ((long) this.mContext.getResources().getInteger(17694938)) * 1048576;
    }

    private DataUsageInfo warn(String str) {
        Log.w("DataUsageController", "Failed to get data usage, " + str);
        return null;
    }

    public DataUsageInfo getDataUsageInfo(NetworkTemplate networkTemplate) {
        long j;
        NetworkPolicy findNetworkPolicy = findNetworkPolicy(networkTemplate);
        long currentTimeMillis = System.currentTimeMillis();
        Iterator cycleIterator = findNetworkPolicy != null ? findNetworkPolicy.cycleIterator() : null;
        if (cycleIterator == null || !cycleIterator.hasNext()) {
            j = currentTimeMillis - 2419200000L;
        } else {
            Range range = (Range) cycleIterator.next();
            long epochMilli = ((ZonedDateTime) range.getLower()).toInstant().toEpochMilli();
            currentTimeMillis = ((ZonedDateTime) range.getUpper()).toInstant().toEpochMilli();
            j = epochMilli;
        }
        long usageLevel = getUsageLevel(networkTemplate, j, currentTimeMillis);
        long j2 = 0;
        if (usageLevel < 0) {
            return warn("no entry data");
        }
        DataUsageInfo dataUsageInfo = new DataUsageInfo();
        dataUsageInfo.startDate = j;
        dataUsageInfo.usageLevel = usageLevel;
        dataUsageInfo.period = formatDateRange(j, currentTimeMillis);
        dataUsageInfo.cycleStart = j;
        dataUsageInfo.cycleEnd = currentTimeMillis;
        if (findNetworkPolicy != null) {
            long j3 = findNetworkPolicy.limitBytes;
            if (j3 <= 0) {
                j3 = 0;
            }
            dataUsageInfo.limitLevel = j3;
            long j4 = findNetworkPolicy.warningBytes;
            if (j4 > 0) {
                j2 = j4;
            }
            dataUsageInfo.warningLevel = j2;
        } else {
            dataUsageInfo.warningLevel = getDefaultWarningLevel();
        }
        NetworkNameProvider networkNameProvider = this.mNetworkController;
        if (networkNameProvider != null) {
            dataUsageInfo.carrier = networkNameProvider.getMobileDataNetworkName();
        }
        return dataUsageInfo;
    }

    public long getHistoricalUsageLevel(NetworkTemplate networkTemplate) {
        return getUsageLevel(networkTemplate, 0, System.currentTimeMillis());
    }

    private long getUsageLevel(NetworkTemplate networkTemplate, long j, long j2) {
        try {
            NetworkStats.Bucket querySummaryForDevice = this.mNetworkStatsManager.querySummaryForDevice(networkTemplate, j, j2);
            if (querySummaryForDevice != null) {
                return querySummaryForDevice.getRxBytes() + querySummaryForDevice.getTxBytes();
            }
            Log.w("DataUsageController", "Failed to get data usage, no entry data");
            return -1;
        } catch (RemoteException unused) {
            Log.w("DataUsageController", "Failed to get data usage, remote call failed");
            return -1;
        }
    }

    private NetworkPolicy findNetworkPolicy(NetworkTemplate networkTemplate) {
        NetworkPolicy[] networkPolicies;
        NetworkPolicyManager networkPolicyManager = this.mPolicyManager;
        if (networkPolicyManager == null || networkTemplate == null || (networkPolicies = networkPolicyManager.getNetworkPolicies()) == null) {
            return null;
        }
        for (NetworkPolicy networkPolicy : networkPolicies) {
            if (networkPolicy != null && networkTemplate.equals(networkPolicy.template)) {
                return networkPolicy;
            }
        }
        return null;
    }

    @VisibleForTesting
    public TelephonyManager getTelephonyManager() {
        int i = this.mSubscriptionId;
        if (!SubscriptionManager.isValidSubscriptionId(i)) {
            i = SubscriptionManager.getDefaultDataSubscriptionId();
        }
        if (!SubscriptionManager.isValidSubscriptionId(i)) {
            int[] activeSubscriptionIdList = SubscriptionManager.from(this.mContext).getActiveSubscriptionIdList();
            if (!ArrayUtils.isEmpty(activeSubscriptionIdList)) {
                i = activeSubscriptionIdList[0];
            }
        }
        return ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).createForSubscriptionId(i);
    }

    private String formatDateRange(long j, long j2) {
        String formatter;
        synchronized (PERIOD_BUILDER) {
            PERIOD_BUILDER.setLength(0);
            formatter = DateUtils.formatDateRange(this.mContext, PERIOD_FORMATTER, j, j2, 65552, null).toString();
        }
        return formatter;
    }
}
