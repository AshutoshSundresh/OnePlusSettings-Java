package com.android.settings.deviceinfo;

import android.app.usage.ExternalStorageStats;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.os.AsyncTask;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.text.format.DateUtils;
import android.text.format.Formatter;
import android.util.Log;
import java.io.IOException;
import java.util.UUID;

public abstract class MigrateEstimateTask extends AsyncTask<Void, Void, Long> {
    private final Context mContext;
    private long mSizeBytes = -1;

    public abstract void onPostExecute(String str, String str2);

    public MigrateEstimateTask(Context context) {
        this.mContext = context;
    }

    public void copyFrom(Intent intent) {
        this.mSizeBytes = intent.getLongExtra("size_bytes", -1);
    }

    /* access modifiers changed from: protected */
    public Long doInBackground(Void... voidArr) {
        long j = this.mSizeBytes;
        if (j != -1) {
            return Long.valueOf(j);
        }
        UserManager userManager = (UserManager) this.mContext.getSystemService(UserManager.class);
        StorageManager storageManager = (StorageManager) this.mContext.getSystemService(StorageManager.class);
        StorageStatsManager storageStatsManager = (StorageStatsManager) this.mContext.getSystemService(StorageStatsManager.class);
        VolumeInfo findEmulatedForPrivate = storageManager.findEmulatedForPrivate(this.mContext.getPackageManager().getPrimaryStorageCurrentVolume());
        if (findEmulatedForPrivate == null) {
            Log.w("StorageSettings", "Failed to find current primary storage");
            return -1L;
        }
        try {
            UUID uuidForPath = storageManager.getUuidForPath(findEmulatedForPrivate.getPath());
            Log.d("StorageSettings", "Measuring size of " + uuidForPath);
            long j2 = 0;
            for (UserInfo userInfo : userManager.getUsers()) {
                ExternalStorageStats queryExternalStatsForUser = storageStatsManager.queryExternalStatsForUser(uuidForPath, UserHandle.of(userInfo.id));
                j2 += queryExternalStatsForUser.getTotalBytes();
                if (userInfo.id == 0) {
                    j2 += queryExternalStatsForUser.getObbBytes();
                }
            }
            return Long.valueOf(j2);
        } catch (IOException e) {
            Log.w("StorageSettings", "Failed to measure", e);
            return -1L;
        }
    }

    /* access modifiers changed from: protected */
    public void onPostExecute(Long l) {
        long longValue = l.longValue();
        this.mSizeBytes = longValue;
        onPostExecute(Formatter.formatFileSize(this.mContext, this.mSizeBytes), DateUtils.formatDuration(Math.max((longValue * 1000) / 10485760, 1000L)).toString());
    }
}
