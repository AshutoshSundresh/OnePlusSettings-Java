package com.android.settings.fuelgauge;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.BatteryStats;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import android.util.SparseLongArray;
import com.android.internal.os.BatterySipper;
import com.android.internal.os.BatteryStatsHelper;
import com.android.internal.util.ArrayUtils;
import com.android.settings.fuelgauge.batterytip.AnomalyInfo;
import com.android.settings.fuelgauge.batterytip.BatteryDatabaseManager;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.applications.AppUtils;
import com.android.settingslib.fuelgauge.Estimate;
import com.android.settingslib.fuelgauge.PowerWhitelistBackend;
import com.android.settingslib.utils.PowerUtil;
import com.android.settingslib.utils.ThreadUtils;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BatteryUtils {
    private static BatteryUtils sInstance;
    private AppOpsManager mAppOpsManager;
    private Context mContext;
    private PackageManager mPackageManager;
    PowerUsageFeatureProvider mPowerUsageFeatureProvider;

    public double calculateBatteryPercent(double d, double d2, double d3, int i) {
        if (d2 == 0.0d) {
            return 0.0d;
        }
        return (d / (d2 - d3)) * ((double) i);
    }

    public static BatteryUtils getInstance(Context context) {
        BatteryUtils batteryUtils = sInstance;
        if (batteryUtils == null || batteryUtils.isDataCorrupted()) {
            sInstance = new BatteryUtils(context.getApplicationContext());
        }
        return sInstance;
    }

    BatteryUtils(Context context) {
        this.mContext = context;
        this.mPackageManager = context.getPackageManager();
        this.mAppOpsManager = (AppOpsManager) context.getSystemService("appops");
        this.mPowerUsageFeatureProvider = FeatureFactory.getFactory(context).getPowerUsageFeatureProvider(context);
    }

    public long getProcessTimeMs(int i, BatteryStats.Uid uid, int i2) {
        if (uid == null) {
            return 0;
        }
        if (i == 0) {
            return getScreenUsageTimeMs(uid, i2);
        }
        if (i == 1) {
            return getProcessForegroundTimeMs(uid, i2);
        }
        if (i == 2) {
            return getProcessBackgroundTimeMs(uid, i2);
        }
        if (i != 3) {
            return 0;
        }
        return getProcessForegroundTimeMs(uid, i2) + getProcessBackgroundTimeMs(uid, i2);
    }

    private long getScreenUsageTimeMs(BatteryStats.Uid uid, int i, long j) {
        int[] iArr = {0};
        Log.v("BatteryUtils", "package: " + this.mPackageManager.getNameForUid(uid.getUid()));
        long j2 = 0;
        for (int i2 = 0; i2 < 1; i2++) {
            int i3 = iArr[i2];
            long processStateTime = uid.getProcessStateTime(i3, j, i);
            Log.v("BatteryUtils", "type: " + i3 + " time(us): " + processStateTime);
            j2 += processStateTime;
        }
        Log.v("BatteryUtils", "foreground time(us): " + j2);
        return PowerUtil.convertUsToMs(Math.min(j2, getForegroundActivityTotalTimeUs(uid, j)));
    }

    private long getScreenUsageTimeMs(BatteryStats.Uid uid, int i) {
        return getScreenUsageTimeMs(uid, i, PowerUtil.convertMsToUs(SystemClock.elapsedRealtime()));
    }

    private long getProcessBackgroundTimeMs(BatteryStats.Uid uid, int i) {
        long processStateTime = uid.getProcessStateTime(3, PowerUtil.convertMsToUs(SystemClock.elapsedRealtime()), i);
        Log.v("BatteryUtils", "package: " + this.mPackageManager.getNameForUid(uid.getUid()));
        Log.v("BatteryUtils", "background time(us): " + processStateTime);
        return PowerUtil.convertUsToMs(processStateTime);
    }

    private long getProcessForegroundTimeMs(BatteryStats.Uid uid, int i) {
        long convertMsToUs = PowerUtil.convertMsToUs(SystemClock.elapsedRealtime());
        return getScreenUsageTimeMs(uid, i, convertMsToUs) + PowerUtil.convertUsToMs(getForegroundServiceTotalTimeUs(uid, convertMsToUs));
    }

    public double removeHiddenBatterySippers(List<BatterySipper> list) {
        double d = 0.0d;
        BatterySipper batterySipper = null;
        for (int size = list.size() - 1; size >= 0; size--) {
            BatterySipper batterySipper2 = list.get(size);
            if (shouldHideSipper(batterySipper2)) {
                list.remove(size);
                BatterySipper.DrainType drainType = batterySipper2.drainType;
                if (!(drainType == BatterySipper.DrainType.OVERCOUNTED || drainType == BatterySipper.DrainType.SCREEN || drainType == BatterySipper.DrainType.UNACCOUNTED || drainType == BatterySipper.DrainType.BLUETOOTH || drainType == BatterySipper.DrainType.WIFI || drainType == BatterySipper.DrainType.IDLE || isHiddenSystemModule(batterySipper2))) {
                    d += batterySipper2.totalPowerMah;
                }
            }
            if (batterySipper2.drainType == BatterySipper.DrainType.SCREEN) {
                batterySipper = batterySipper2;
            }
        }
        smearScreenBatterySipper(list, batterySipper);
        return d;
    }

    /* access modifiers changed from: package-private */
    public void smearScreenBatterySipper(List<BatterySipper> list, BatterySipper batterySipper) {
        SparseLongArray sparseLongArray = new SparseLongArray();
        int size = list.size();
        long j = 0;
        int i = 0;
        long j2 = 0;
        for (int i2 = 0; i2 < size; i2++) {
            BatteryStats.Uid uid = list.get(i2).uidObj;
            if (uid != null) {
                long processTimeMs = getProcessTimeMs(0, uid, 0);
                sparseLongArray.put(uid.getUid(), processTimeMs);
                j2 += processTimeMs;
            }
        }
        if (j2 < 600000) {
            return;
        }
        if (batterySipper == null) {
            Log.e("BatteryUtils", "screen sipper is null even when app screen time is not zero");
            return;
        }
        double d = batterySipper.totalPowerMah;
        int size2 = list.size();
        while (i < size2) {
            BatterySipper batterySipper2 = list.get(i);
            batterySipper2.totalPowerMah += (((double) sparseLongArray.get(batterySipper2.getUid(), j)) * d) / ((double) j2);
            i++;
            j = 0;
        }
    }

    public boolean shouldHideSipper(BatterySipper batterySipper) {
        BatterySipper.DrainType drainType = batterySipper.drainType;
        return drainType == BatterySipper.DrainType.IDLE || drainType == BatterySipper.DrainType.CELL || drainType == BatterySipper.DrainType.SCREEN || drainType == BatterySipper.DrainType.UNACCOUNTED || drainType == BatterySipper.DrainType.OVERCOUNTED || drainType == BatterySipper.DrainType.BLUETOOTH || drainType == BatterySipper.DrainType.WIFI || batterySipper.totalPowerMah * 3600.0d < 5.0d || this.mPowerUsageFeatureProvider.isTypeService(batterySipper) || this.mPowerUsageFeatureProvider.isTypeSystem(batterySipper) || isHiddenSystemModule(batterySipper);
    }

    public boolean isHiddenSystemModule(BatterySipper batterySipper) {
        if (batterySipper.uidObj == null) {
            return false;
        }
        String[] packagesForUid = this.mPackageManager.getPackagesForUid(batterySipper.getUid());
        batterySipper.mPackages = packagesForUid;
        if (packagesForUid != null) {
            int length = packagesForUid.length;
            for (int i = 0; i < length; i++) {
                if (AppUtils.isHiddenSystemModule(this.mContext, batterySipper.mPackages[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getPackageName(int i) {
        String[] packagesForUid = this.mPackageManager.getPackagesForUid(i);
        if (ArrayUtils.isEmpty(packagesForUid)) {
            return null;
        }
        return packagesForUid[0];
    }

    public void sortUsageList(List<BatterySipper> list) {
        Collections.sort(list, new Comparator<BatterySipper>(this) {
            /* class com.android.settings.fuelgauge.BatteryUtils.AnonymousClass1 */

            public int compare(BatterySipper batterySipper, BatterySipper batterySipper2) {
                return Double.compare(batterySipper2.totalPowerMah, batterySipper.totalPowerMah);
            }
        });
    }

    public long calculateLastFullChargeTime(BatteryStatsHelper batteryStatsHelper, long j) {
        return j - batteryStatsHelper.getStats().getStartClockTime();
    }

    public long calculateScreenUsageTime(BatteryStatsHelper batteryStatsHelper) {
        BatterySipper findBatterySipperByType = findBatterySipperByType(batteryStatsHelper.getUsageList(), BatterySipper.DrainType.SCREEN);
        if (findBatterySipperByType != null) {
            return findBatterySipperByType.usageTimeMs;
        }
        return 0;
    }

    public static void logRuntime(String str, String str2, long j) {
        Log.d(str, str2 + ": " + (System.currentTimeMillis() - j) + "ms");
    }

    public int getPackageUid(String str) {
        if (str == null) {
            return -1;
        }
        try {
            return this.mPackageManager.getPackageUid(str, 128);
        } catch (PackageManager.NameNotFoundException unused) {
            return -1;
        }
    }

    public void setForceAppStandby(int i, String str, int i2) {
        if (isPreOApp(str)) {
            this.mAppOpsManager.setMode(63, i, str, i2);
        }
        this.mAppOpsManager.setMode(70, i, str, i2);
        ThreadUtils.postOnBackgroundThread(new Runnable(i2, i, str) {
            /* class com.android.settings.fuelgauge.$$Lambda$BatteryUtils$ShxxVGhaRDdL8cjipNso8s_v8Y */
            public final /* synthetic */ int f$1;
            public final /* synthetic */ int f$2;
            public final /* synthetic */ String f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                BatteryUtils.this.lambda$setForceAppStandby$0$BatteryUtils(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setForceAppStandby$0 */
    public /* synthetic */ void lambda$setForceAppStandby$0$BatteryUtils(int i, int i2, String str) {
        BatteryDatabaseManager instance = BatteryDatabaseManager.getInstance(this.mContext);
        if (i == 1) {
            instance.insertAction(0, i2, str, System.currentTimeMillis());
        } else if (i == 0) {
            instance.deleteAction(0, i2, str);
        }
    }

    public boolean isForceAppStandbyEnabled(int i, String str) {
        return this.mAppOpsManager.checkOpNoThrow(70, i, str) == 1;
    }

    public boolean clearForceAppStandby(String str) {
        int packageUid = getPackageUid(str);
        if (packageUid == -1 || !isForceAppStandbyEnabled(packageUid, str)) {
            return false;
        }
        setForceAppStandby(packageUid, str, 0);
        return true;
    }

    public void initBatteryStatsHelper(BatteryStatsHelper batteryStatsHelper, Bundle bundle, UserManager userManager) {
        batteryStatsHelper.create(bundle);
        batteryStatsHelper.clearStats();
        batteryStatsHelper.refreshStats(0, userManager.getUserProfiles());
    }

    public BatteryInfo getBatteryInfo(BatteryStatsHelper batteryStatsHelper, String str) {
        long currentTimeMillis = System.currentTimeMillis();
        Intent registerReceiver = this.mContext.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        long convertMsToUs = PowerUtil.convertMsToUs(SystemClock.elapsedRealtime());
        BatteryStats stats = batteryStatsHelper.getStats();
        Estimate enhancedEstimate = getEnhancedEstimate();
        if (enhancedEstimate == null) {
            enhancedEstimate = new Estimate(PowerUtil.convertUsToMs(stats.computeBatteryTimeRemaining(convertMsToUs)), false, -1);
        }
        logRuntime(str, "BatteryInfoLoader post query", currentTimeMillis);
        BatteryInfo batteryInfo = BatteryInfo.getBatteryInfo(this.mContext, registerReceiver, stats, enhancedEstimate, convertMsToUs, false);
        logRuntime(str, "BatteryInfoLoader.loadInBackground", currentTimeMillis);
        return batteryInfo;
    }

    /* access modifiers changed from: package-private */
    public Estimate getEnhancedEstimate() {
        if (Duration.between(Estimate.getLastCacheUpdateTime(this.mContext), Instant.now()).compareTo(Duration.ofSeconds(10)) < 0) {
            return Estimate.getCachedEstimateIfAvailable(this.mContext);
        }
        PowerUsageFeatureProvider powerUsageFeatureProvider = this.mPowerUsageFeatureProvider;
        if (powerUsageFeatureProvider == null || !powerUsageFeatureProvider.isEnhancedBatteryPredictionEnabled(this.mContext)) {
            return null;
        }
        Estimate enhancedBatteryPrediction = this.mPowerUsageFeatureProvider.getEnhancedBatteryPrediction(this.mContext);
        if (enhancedBatteryPrediction != null) {
            Estimate.storeCachedEstimate(this.mContext, enhancedBatteryPrediction);
        }
        return enhancedBatteryPrediction;
    }

    public BatterySipper findBatterySipperByType(List<BatterySipper> list, BatterySipper.DrainType drainType) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            BatterySipper batterySipper = list.get(i);
            if (batterySipper.drainType == drainType) {
                return batterySipper;
            }
        }
        return null;
    }

    private boolean isDataCorrupted() {
        return this.mPackageManager == null || this.mAppOpsManager == null;
    }

    /* access modifiers changed from: package-private */
    public long getForegroundActivityTotalTimeUs(BatteryStats.Uid uid, long j) {
        BatteryStats.Timer foregroundActivityTimer = uid.getForegroundActivityTimer();
        if (foregroundActivityTimer != null) {
            return foregroundActivityTimer.getTotalTimeLocked(j, 0);
        }
        return 0;
    }

    /* access modifiers changed from: package-private */
    public long getForegroundServiceTotalTimeUs(BatteryStats.Uid uid, long j) {
        BatteryStats.Timer foregroundServiceTimer = uid.getForegroundServiceTimer();
        if (foregroundServiceTimer != null) {
            return foregroundServiceTimer.getTotalTimeLocked(j, 0);
        }
        return 0;
    }

    public boolean isPreOApp(String str) {
        try {
            if (this.mPackageManager.getApplicationInfo(str, 128).targetSdkVersion < 26) {
                return true;
            }
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("BatteryUtils", "Cannot find package: " + str, e);
            return false;
        }
    }

    public boolean isPreOApp(String[] strArr) {
        if (ArrayUtils.isEmpty(strArr)) {
            return false;
        }
        for (String str : strArr) {
            if (isPreOApp(str)) {
                return true;
            }
        }
        return false;
    }

    public boolean shouldHideAnomaly(PowerWhitelistBackend powerWhitelistBackend, int i, AnomalyInfo anomalyInfo) {
        String[] packagesForUid = this.mPackageManager.getPackagesForUid(i);
        if (ArrayUtils.isEmpty(packagesForUid) || isSystemUid(i) || powerWhitelistBackend.isWhitelisted(packagesForUid)) {
            return true;
        }
        if (isSystemApp(this.mPackageManager, packagesForUid) && !hasLauncherEntry(packagesForUid)) {
            return true;
        }
        if (!isExcessiveBackgroundAnomaly(anomalyInfo) || isPreOApp(packagesForUid)) {
            return false;
        }
        return true;
    }

    private boolean isExcessiveBackgroundAnomaly(AnomalyInfo anomalyInfo) {
        return anomalyInfo.anomalyType.intValue() == 4;
    }

    private boolean isSystemUid(int i) {
        int appId = UserHandle.getAppId(i);
        return appId >= 0 && appId < 10000;
    }

    private boolean isSystemApp(PackageManager packageManager, String[] strArr) {
        for (String str : strArr) {
            try {
                if ((packageManager.getApplicationInfo(str, 0).flags & 1) != 0) {
                    return true;
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("BatteryUtils", "Package not found: " + str, e);
            }
        }
        return false;
    }

    private boolean hasLauncherEntry(String[] strArr) {
        Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
        intent.addCategory("android.intent.category.LAUNCHER");
        List<ResolveInfo> queryIntentActivities = this.mPackageManager.queryIntentActivities(intent, 1835520);
        int size = queryIntentActivities.size();
        for (int i = 0; i < size; i++) {
            if (ArrayUtils.contains(strArr, queryIntentActivities.get(i).activityInfo.packageName)) {
                return true;
            }
        }
        return false;
    }

    public long getAppLongVersionCode(String str) {
        try {
            return this.mPackageManager.getPackageInfo(str, 0).getLongVersionCode();
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("BatteryUtils", "Cannot find package: " + str, e);
            return -1;
        }
    }
}
