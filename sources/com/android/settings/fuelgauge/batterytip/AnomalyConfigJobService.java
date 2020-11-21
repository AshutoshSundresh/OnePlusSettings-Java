package com.android.settings.fuelgauge.batterytip;

import android.app.StatsManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import com.android.settings.C0011R$integer;
import com.android.settingslib.utils.ThreadUtils;
import java.util.concurrent.TimeUnit;

public class AnomalyConfigJobService extends JobService {
    static final long CONFIG_UPDATE_FREQUENCY_MS = TimeUnit.DAYS.toMillis(1);

    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    public static void scheduleConfigUpdate(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JobScheduler.class);
        JobInfo.Builder persisted = new JobInfo.Builder(C0011R$integer.job_anomaly_config_update, new ComponentName(context, AnomalyConfigJobService.class)).setPeriodic(CONFIG_UPDATE_FREQUENCY_MS).setRequiresDeviceIdle(true).setRequiresCharging(true).setPersisted(true);
        if (jobScheduler.getPendingJob(C0011R$integer.job_anomaly_config_update) == null && jobScheduler.schedule(persisted.build()) != 1) {
            Log.i("AnomalyConfigJobService", "Anomaly config update job service schedule failed.");
        }
    }

    public boolean onStartJob(JobParameters jobParameters) {
        ThreadUtils.postOnBackgroundThread(new Runnable(jobParameters) {
            /* class com.android.settings.fuelgauge.batterytip.$$Lambda$AnomalyConfigJobService$ABo24XwFDn4e3D3k2rc6z5bdU */
            public final /* synthetic */ JobParameters f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                AnomalyConfigJobService.this.lambda$onStartJob$0$AnomalyConfigJobService(this.f$1);
            }
        });
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onStartJob$0 */
    public /* synthetic */ void lambda$onStartJob$0$AnomalyConfigJobService(JobParameters jobParameters) {
        StatsManager statsManager = (StatsManager) getSystemService(StatsManager.class);
        checkAnomalyConfig(statsManager);
        try {
            BatteryTipUtils.uploadAnomalyPendingIntent(this, statsManager);
        } catch (StatsManager.StatsUnavailableException e) {
            Log.w("AnomalyConfigJobService", "Failed to uploadAnomalyPendingIntent.", e);
        }
        jobFinished(jobParameters, false);
    }

    /* access modifiers changed from: package-private */
    public synchronized void checkAnomalyConfig(StatsManager statsManager) {
        SharedPreferences sharedPreferences = getSharedPreferences("anomaly_pref", 0);
        int i = sharedPreferences.getInt("anomaly_config_version", 0);
        int i2 = Settings.Global.getInt(getContentResolver(), "anomaly_config_version", 0);
        String string = Settings.Global.getString(getContentResolver(), "anomaly_config");
        Log.i("AnomalyConfigJobService", "CurrentVersion: " + i + " new version: " + i2);
        if (i2 > i) {
            try {
                statsManager.removeConfig(1);
            } catch (StatsManager.StatsUnavailableException e) {
                Log.i("AnomalyConfigJobService", "When updating anomaly config, failed to first remove the old config 1", e);
            }
            if (!TextUtils.isEmpty(string)) {
                try {
                    statsManager.addConfig(1, Base64.decode(string, 0));
                    Log.i("AnomalyConfigJobService", "Upload the anomaly config. configKey: 1");
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putInt("anomaly_config_version", i2);
                    edit.commit();
                } catch (IllegalArgumentException e2) {
                    Log.e("AnomalyConfigJobService", "Anomaly raw config is in wrong format", e2);
                } catch (StatsManager.StatsUnavailableException e3) {
                    Log.i("AnomalyConfigJobService", "Upload of anomaly config failed for configKey 1", e3);
                }
            }
        }
    }
}
