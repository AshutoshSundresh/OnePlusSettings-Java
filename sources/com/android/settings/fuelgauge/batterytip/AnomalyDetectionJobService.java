package com.android.settings.fuelgauge.batterytip;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.app.job.JobWorkItem;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.StatsDimensionsValue;
import android.os.UserManager;
import android.util.Log;
import com.android.settings.C0011R$integer;
import com.android.settings.fuelgauge.BatteryUtils;
import com.android.settings.fuelgauge.PowerUsageFeatureProvider;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.fuelgauge.PowerWhitelistBackend;
import com.android.settingslib.utils.ThreadUtils;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AnomalyDetectionJobService extends JobService {
    static final long MAX_DELAY_MS = TimeUnit.MINUTES.toMillis(30);
    static final int STATSD_UID_FILED = 1;
    static final int UID_NULL = -1;
    boolean mIsJobCanceled = false;
    private final Object mLock = new Object();

    public static void scheduleAnomalyDetection(Context context, Intent intent) {
        if (((JobScheduler) context.getSystemService(JobScheduler.class)).enqueue(new JobInfo.Builder(C0011R$integer.job_anomaly_detection, new ComponentName(context, AnomalyDetectionJobService.class)).setOverrideDeadline(MAX_DELAY_MS).build(), new JobWorkItem(intent)) != 1) {
            Log.i("AnomalyDetectionService", "Anomaly detection job service enqueue failed.");
        }
    }

    public boolean onStartJob(JobParameters jobParameters) {
        synchronized (this.mLock) {
            this.mIsJobCanceled = false;
        }
        ThreadUtils.postOnBackgroundThread(new Runnable(jobParameters) {
            /* class com.android.settings.fuelgauge.batterytip.$$Lambda$AnomalyDetectionJobService$7JxJe3rza0cCkIc77iCSZKPfL4 */
            public final /* synthetic */ JobParameters f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                AnomalyDetectionJobService.this.lambda$onStartJob$0$AnomalyDetectionJobService(this.f$1);
            }
        });
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onStartJob$0 */
    public /* synthetic */ void lambda$onStartJob$0$AnomalyDetectionJobService(JobParameters jobParameters) {
        BatteryDatabaseManager instance = BatteryDatabaseManager.getInstance(this);
        BatteryTipPolicy batteryTipPolicy = new BatteryTipPolicy(this);
        BatteryUtils instance2 = BatteryUtils.getInstance(this);
        ContentResolver contentResolver = getContentResolver();
        UserManager userManager = (UserManager) getSystemService(UserManager.class);
        PowerWhitelistBackend instance3 = PowerWhitelistBackend.getInstance(this);
        PowerUsageFeatureProvider powerUsageFeatureProvider = FeatureFactory.getFactory(this).getPowerUsageFeatureProvider(this);
        MetricsFeatureProvider metricsFeatureProvider = FeatureFactory.getFactory(this).getMetricsFeatureProvider();
        JobWorkItem dequeueWork = dequeueWork(jobParameters);
        while (dequeueWork != null) {
            saveAnomalyToDatabase(this, userManager, instance, instance2, batteryTipPolicy, instance3, contentResolver, powerUsageFeatureProvider, metricsFeatureProvider, dequeueWork.getIntent().getExtras());
            completeWork(jobParameters, dequeueWork);
            dequeueWork = dequeueWork(jobParameters);
            instance = instance;
        }
    }

    public boolean onStopJob(JobParameters jobParameters) {
        synchronized (this.mLock) {
            this.mIsJobCanceled = true;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0068, code lost:
        if (android.provider.Settings.Global.getInt(r20, "app_auto_restriction_enabled", 1) == 1) goto L_0x006a;
     */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x008f A[Catch:{ IndexOutOfBoundsException | NullPointerException -> 0x00f1 }] */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x00a7 A[Catch:{ IndexOutOfBoundsException | NullPointerException -> 0x00f1 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void saveAnomalyToDatabase(android.content.Context r14, android.os.UserManager r15, com.android.settings.fuelgauge.batterytip.BatteryDatabaseManager r16, com.android.settings.fuelgauge.BatteryUtils r17, com.android.settings.fuelgauge.batterytip.BatteryTipPolicy r18, com.android.settingslib.fuelgauge.PowerWhitelistBackend r19, android.content.ContentResolver r20, com.android.settings.fuelgauge.PowerUsageFeatureProvider r21, com.android.settingslib.core.instrumentation.MetricsFeatureProvider r22, android.os.Bundle r23) {
        /*
        // Method dump skipped, instructions count: 248
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.fuelgauge.batterytip.AnomalyDetectionJobService.saveAnomalyToDatabase(android.content.Context, android.os.UserManager, com.android.settings.fuelgauge.batterytip.BatteryDatabaseManager, com.android.settings.fuelgauge.BatteryUtils, com.android.settings.fuelgauge.batterytip.BatteryTipPolicy, com.android.settingslib.fuelgauge.PowerWhitelistBackend, android.content.ContentResolver, com.android.settings.fuelgauge.PowerUsageFeatureProvider, com.android.settingslib.core.instrumentation.MetricsFeatureProvider, android.os.Bundle):void");
    }

    /* access modifiers changed from: package-private */
    public int extractUidFromStatsDimensionsValue(StatsDimensionsValue statsDimensionsValue) {
        if (statsDimensionsValue == null) {
            return UID_NULL;
        }
        if (statsDimensionsValue.isValueType(3) && statsDimensionsValue.getField() == 1) {
            return statsDimensionsValue.getIntValue();
        }
        if (statsDimensionsValue.isValueType(7)) {
            List tupleValueList = statsDimensionsValue.getTupleValueList();
            int size = tupleValueList.size();
            for (int i = 0; i < size; i++) {
                int extractUidFromStatsDimensionsValue = extractUidFromStatsDimensionsValue((StatsDimensionsValue) tupleValueList.get(i));
                if (extractUidFromStatsDimensionsValue != UID_NULL) {
                    return extractUidFromStatsDimensionsValue;
                }
            }
        }
        return UID_NULL;
    }

    /* access modifiers changed from: package-private */
    public JobWorkItem dequeueWork(JobParameters jobParameters) {
        synchronized (this.mLock) {
            if (this.mIsJobCanceled) {
                return null;
            }
            return jobParameters.dequeueWork();
        }
    }

    /* access modifiers changed from: package-private */
    public void completeWork(JobParameters jobParameters, JobWorkItem jobWorkItem) {
        synchronized (this.mLock) {
            if (!this.mIsJobCanceled) {
                jobParameters.completeWork(jobWorkItem);
            }
        }
    }
}
