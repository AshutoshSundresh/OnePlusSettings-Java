package com.android.settingslib.applications;

import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import java.io.IOException;

public class StorageStatsSource {
    private StorageStatsManager mStorageStatsManager;

    public interface AppStorageStats {
        long getCacheBytes();

        long getCodeBytes();

        long getDataBytes();

        long getTotalBytes();
    }

    public StorageStatsSource(Context context) {
        this.mStorageStatsManager = (StorageStatsManager) context.getSystemService(StorageStatsManager.class);
    }

    public ExternalStorageStats getExternalStorageStats(String str, UserHandle userHandle) throws IOException {
        return new ExternalStorageStats(this.mStorageStatsManager.queryExternalStatsForUser(str, userHandle));
    }

    public AppStorageStats getStatsForPackage(String str, String str2, UserHandle userHandle) throws PackageManager.NameNotFoundException, IOException {
        return new AppStorageStatsImpl(this.mStorageStatsManager.queryStatsForPackage(str, str2, userHandle));
    }

    public long getCacheQuotaBytes(String str, int i) {
        return this.mStorageStatsManager.getCacheQuotaBytes(str, i);
    }

    public static class ExternalStorageStats {
        public long appBytes;
        public long audioBytes;
        public long imageBytes;
        public long totalBytes;
        public long videoBytes;

        public ExternalStorageStats(long j, long j2, long j3, long j4, long j5) {
            this.totalBytes = j;
            this.audioBytes = j2;
            this.videoBytes = j3;
            this.imageBytes = j4;
            this.appBytes = j5;
        }

        public ExternalStorageStats(android.app.usage.ExternalStorageStats externalStorageStats) {
            this.totalBytes = externalStorageStats.getTotalBytes();
            this.audioBytes = externalStorageStats.getAudioBytes();
            this.videoBytes = externalStorageStats.getVideoBytes();
            this.imageBytes = externalStorageStats.getImageBytes();
            this.appBytes = externalStorageStats.getAppBytes();
        }
    }

    public static class AppStorageStatsImpl implements AppStorageStats {
        private StorageStats mStats;

        public AppStorageStatsImpl(StorageStats storageStats) {
            this.mStats = storageStats;
        }

        @Override // com.android.settingslib.applications.StorageStatsSource.AppStorageStats
        public long getCodeBytes() {
            return this.mStats.getCodeBytes();
        }

        @Override // com.android.settingslib.applications.StorageStatsSource.AppStorageStats
        public long getDataBytes() {
            return this.mStats.getDataBytes();
        }

        @Override // com.android.settingslib.applications.StorageStatsSource.AppStorageStats
        public long getCacheBytes() {
            return this.mStats.getCacheBytes();
        }

        @Override // com.android.settingslib.applications.StorageStatsSource.AppStorageStats
        public long getTotalBytes() {
            return this.mStats.getAppBytes() + this.mStats.getDataBytes();
        }
    }
}
