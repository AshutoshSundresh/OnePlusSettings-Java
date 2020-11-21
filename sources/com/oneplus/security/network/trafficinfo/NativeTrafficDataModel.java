package com.oneplus.security.network.trafficinfo;

import android.content.Context;
import android.net.INetworkStatsService;
import android.net.INetworkStatsSession;
import android.net.NetworkStatsHistory;
import android.net.NetworkTemplate;
import android.net.TrafficStats;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.oneplus.security.network.calibrate.AutoCalibrateUtil;
import com.oneplus.security.network.operator.AccountDayLocalCache;
import com.oneplus.security.network.simcard.SimcardDataModel;
import com.oneplus.security.utils.LogUtils;
import com.oneplus.security.utils.OPSNSUtils;
import com.oneplus.settings.SettingsBaseApplication;

public class NativeTrafficDataModel implements TrafficDataModelInterface {
    private static Object lockNetworkTemplate = new byte[0];
    private static int sReferencCount;
    private static NativeTrafficDataModel sTrafficData;
    private long lastForceUpdateTime = 0;
    private Context mContext;
    private Object mLockQueryDataUsage = new byte[0];
    private INetworkStatsService mStatsService;
    private INetworkStatsSession mStatsSession;

    private NativeTrafficDataModel() {
        Context applicationContext = SettingsBaseApplication.getContext().getApplicationContext();
        this.mContext = applicationContext;
        SimcardDataModel.getInstance(applicationContext);
        INetworkStatsService asInterface = INetworkStatsService.Stub.asInterface(ServiceManager.getService("netstats"));
        this.mStatsService = asInterface;
        try {
            this.mStatsSession = asInterface.openSession();
        } catch (Exception e) {
            LogUtils.e("NativeTrafficDataModel", "mStatsService.openSession() error." + e.getMessage());
        }
    }

    public static TrafficDataModelInterface getTrafficModelInstance() {
        if (sTrafficData == null) {
            synchronized (NativeTrafficDataModel.class) {
                if (sTrafficData == null) {
                    sTrafficData = new NativeTrafficDataModel();
                }
            }
        }
        synchronized (NativeTrafficDataModel.class) {
            sReferencCount++;
        }
        return sTrafficData;
    }

    private long getSpecificTimeUsageBySlotId(int i, long j, long j2, boolean z) {
        NetworkTemplate networkTemplate = getNetworkTemplate(OPSNSUtils.findSubIdBySlotId(i));
        long j3 = 0;
        if (this.mStatsSession != null) {
            try {
                synchronized (this.mLockQueryDataUsage) {
                    if (z) {
                        LogUtils.d("NativeTrafficDataModel", "slotId:" + i);
                        forceUpdateNetworkStats();
                    }
                    NetworkStatsHistory.Entry values = this.mStatsSession.getHistoryForNetwork(networkTemplate, 10).getValues(j, j2, System.currentTimeMillis(), (NetworkStatsHistory.Entry) null);
                    if (values != null) {
                        j3 = values.rxBytes + values.txBytes;
                    }
                }
            } catch (Exception e) {
                LogUtils.e("NativeTrafficDataModel", "error when fetching networkStats info." + e.getMessage());
            }
        }
        LogUtils.i("NativeTrafficDataModel", "getSpecificTimeUsageBySlotId from framework slotId=" + i + ",startTime=" + j + ",endTime=" + j2 + "block=" + z + ",totalBytes=" + j3);
        return j3;
    }

    private void forceUpdateNetworkStats() throws Exception {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        if (elapsedRealtime - this.lastForceUpdateTime > 3000) {
            this.lastForceUpdateTime = elapsedRealtime;
            this.mStatsService.forceUpdate();
            LogUtils.w("NativeTrafficDataModel", "update usage");
        }
    }

    private long getSpecificTimeUsageBySlotId(int i, long j, boolean z) {
        return getSpecificTimeUsageBySlotId(i, 0, j, z);
    }

    @Override // com.oneplus.security.network.trafficinfo.TrafficDataModelInterface
    public void clearTrafficData() {
        synchronized (NativeTrafficDataModel.class) {
            LogUtils.d("clear_o", "data before clear traffic data " + sReferencCount);
            int i = sReferencCount + -1;
            sReferencCount = i;
            if (i > 0) {
                LogUtils.d("clear_o", "data clear traffic data part " + sReferencCount);
                return;
            }
            if (sTrafficData != null) {
                TrafficStats.closeQuietly(this.mStatsSession);
                LogUtils.d("clear_o", "data clear traffic data all " + sReferencCount);
                sTrafficData = null;
                sReferencCount = 0;
            }
            if (sReferencCount < 0) {
                sReferencCount = 0;
            }
        }
    }

    @Override // com.oneplus.security.network.trafficinfo.TrafficDataModelInterface
    public long getExtraDataUsage(int i, long j, boolean z) {
        long lastCalibrateTime = AutoCalibrateUtil.getLastCalibrateTime(this.mContext, i);
        LogUtils.i("NativeTrafficDataModel", "requesetDataUsage lastCalibrateTime:" + lastCalibrateTime + ", slot:" + i + ", block:" + z);
        long currentTimeMillis = System.currentTimeMillis();
        long[] dataUsageSectionTimeMillByAccountDay = AccountDayLocalCache.getDataUsageSectionTimeMillByAccountDay(this.mContext, i);
        long j2 = dataUsageSectionTimeMillByAccountDay[1];
        if (j >= 0 && lastCalibrateTime <= currentTimeMillis && lastCalibrateTime >= dataUsageSectionTimeMillByAccountDay[0]) {
            return getDataUsageWithinSpecificTime(i, lastCalibrateTime, j2, z);
        }
        long j3 = dataUsageSectionTimeMillByAccountDay[0];
        LogUtils.d("NativeTrafficDataModel", "originalDataUsed or lastCalibrateTime is invalid slotId:" + i + ",originalDataUsed:" + j);
        return getSpecificTimeUsageBySlotId(i, j3, j2, z);
    }

    public long getDataUsageWithinSpecificTime(int i, long j, long j2, boolean z) {
        long specificTimeUsageBySlotId = getSpecificTimeUsageBySlotId(i, j2, z);
        long nativeTotalUsageWhenLastCalibrated = AutoCalibrateUtil.getNativeTotalUsageWhenLastCalibrated(this.mContext, i);
        LogUtils.d("NativeTrafficDataModel", "slotId:" + i + ", value is " + specificTimeUsageBySlotId + " lastExtraValue is " + nativeTotalUsageWhenLastCalibrated);
        if (specificTimeUsageBySlotId < nativeTotalUsageWhenLastCalibrated || nativeTotalUsageWhenLastCalibrated <= 0) {
            long specificTimeUsageBySlotId2 = getSpecificTimeUsageBySlotId(i, j, j2, z);
            LogUtils.e("NativeTrafficDataModel", "slotId:" + i + ", value < lastExtraValue, re-query datausage by start time,new value:" + specificTimeUsageBySlotId2);
            return specificTimeUsageBySlotId2;
        }
        long j3 = specificTimeUsageBySlotId - nativeTotalUsageWhenLastCalibrated;
        if (j3 <= 1000) {
            return 0;
        }
        return j3;
    }

    @Override // com.oneplus.security.network.trafficinfo.TrafficDataModelInterface
    public long getNativeDataUsageWithinSpecificTime(int i, long j, long j2) {
        return getSpecificTimeUsageBySlotId(i, j, j2, false);
    }

    public static NetworkTemplate getNetworkTemplate(int i) {
        NetworkTemplate mobileTemplate;
        synchronized (lockNetworkTemplate) {
            LogUtils.i("NativeTrafficDataModel", "getNetworkTemplate subId:" + i);
            mobileTemplate = getMobileTemplate(SettingsBaseApplication.getContext(), i);
        }
        return mobileTemplate;
    }

    public static NetworkTemplate getMobileTemplate(Context context, int i) {
        TelephonyManager createForSubscriptionId = ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(i);
        SubscriptionInfo activeSubscriptionInfo = ((SubscriptionManager) context.getSystemService(SubscriptionManager.class)).getActiveSubscriptionInfo(i);
        NetworkTemplate buildTemplateMobileAll = NetworkTemplate.buildTemplateMobileAll(createForSubscriptionId.getSubscriberId(i));
        if (activeSubscriptionInfo != null) {
            return NetworkTemplate.normalize(buildTemplateMobileAll, createForSubscriptionId.getMergedSubscriberIds());
        }
        Log.i("NativeTrafficDataModel", "Subscription is not active: " + i);
        return buildTemplateMobileAll;
    }
}
