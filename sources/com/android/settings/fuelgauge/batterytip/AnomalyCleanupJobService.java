package com.android.settings.fuelgauge.batterytip;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;
import com.android.settings.C0011R$integer;
import com.android.settingslib.utils.ThreadUtils;
import java.util.concurrent.TimeUnit;

public class AnomalyCleanupJobService extends JobService {
    static final long CLEAN_UP_FREQUENCY_MS = TimeUnit.DAYS.toMillis(1);

    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    public static void scheduleCleanUp(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JobScheduler.class);
        JobInfo.Builder persisted = new JobInfo.Builder(C0011R$integer.job_anomaly_clean_up, new ComponentName(context, AnomalyCleanupJobService.class)).setPeriodic(CLEAN_UP_FREQUENCY_MS).setRequiresDeviceIdle(true).setRequiresCharging(true).setPersisted(true);
        if (jobScheduler.getPendingJob(C0011R$integer.job_anomaly_clean_up) == null && jobScheduler.schedule(persisted.build()) != 1) {
            Log.i("AnomalyCleanUpJobService", "Anomaly clean up job service schedule failed.");
        }
    }

    public boolean onStartJob(JobParameters jobParameters) {
        ThreadUtils.postOnBackgroundThread(new Runnable(BatteryDatabaseManager.getInstance(this), new BatteryTipPolicy(this), jobParameters) {
            /* class com.android.settings.fuelgauge.batterytip.$$Lambda$AnomalyCleanupJobService$Wvu3W97OjsnNVurAIkZXTma9fMg */
            public final /* synthetic */ BatteryDatabaseManager f$1;
            public final /* synthetic */ BatteryTipPolicy f$2;
            public final /* synthetic */ JobParameters f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                AnomalyCleanupJobService.this.lambda$onStartJob$0$AnomalyCleanupJobService(this.f$1, this.f$2, this.f$3);
            }
        });
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onStartJob$0 */
    public /* synthetic */ void lambda$onStartJob$0$AnomalyCleanupJobService(BatteryDatabaseManager batteryDatabaseManager, BatteryTipPolicy batteryTipPolicy, JobParameters jobParameters) {
        batteryDatabaseManager.deleteAllAnomaliesBeforeTimeStamp(System.currentTimeMillis() - TimeUnit.DAYS.toMillis((long) batteryTipPolicy.dataHistoryRetainDay));
        jobFinished(jobParameters, false);
    }
}
