package com.android.settings.deviceinfo.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.SparseArray;
import com.android.settings.deviceinfo.storage.StorageAsyncLoader;
import com.android.settingslib.applications.StorageStatsSource;
import com.android.settingslib.deviceinfo.PrivateStorageInfo;
import java.util.concurrent.TimeUnit;

public class CachedStorageValuesHelper {
    public static final String SHARED_PREFERENCES_NAME = "CachedStorageValues";
    private final Long mClobberThreshold;
    protected Clock mClock = new Clock();
    private final SharedPreferences mSharedPreferences;
    private final int mUserId;

    public CachedStorageValuesHelper(Context context, int i) {
        this.mSharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
        this.mUserId = i;
        this.mClobberThreshold = Long.valueOf(Settings.Global.getLong(context.getContentResolver(), "storage_settings_clobber_threshold", TimeUnit.MINUTES.toMillis(5)));
    }

    public PrivateStorageInfo getCachedPrivateStorageInfo() {
        if (!isDataValid()) {
            return null;
        }
        long j = this.mSharedPreferences.getLong("free_bytes", -1);
        long j2 = this.mSharedPreferences.getLong("total_bytes", -1);
        if (j < 0 || j2 < 0) {
            return null;
        }
        return new PrivateStorageInfo(j, j2);
    }

    public SparseArray<StorageAsyncLoader.AppsStorageResult> getCachedAppsStorageResult() {
        if (!isDataValid()) {
            return null;
        }
        long j = this.mSharedPreferences.getLong("game_apps_size", -1);
        long j2 = this.mSharedPreferences.getLong("music_apps_size", -1);
        long j3 = this.mSharedPreferences.getLong("video_apps_size", -1);
        long j4 = this.mSharedPreferences.getLong("photo_apps_size", -1);
        long j5 = this.mSharedPreferences.getLong("other_apps_size", -1);
        long j6 = this.mSharedPreferences.getLong("cache_apps_size", -1);
        if (j < 0 || j2 < 0 || j3 < 0 || j4 < 0 || j5 < 0 || j6 < 0) {
            return null;
        }
        long j7 = this.mSharedPreferences.getLong("external_total_bytes", -1);
        long j8 = this.mSharedPreferences.getLong("external_audio_bytes", -1);
        long j9 = this.mSharedPreferences.getLong("external_video_bytes", -1);
        long j10 = this.mSharedPreferences.getLong("external_image_bytes", -1);
        long j11 = this.mSharedPreferences.getLong("external_apps_bytes", -1);
        if (j7 < 0 || j8 < 0 || j9 < 0 || j10 < 0 || j11 < 0) {
            return null;
        }
        StorageStatsSource.ExternalStorageStats externalStorageStats = new StorageStatsSource.ExternalStorageStats(j7, j8, j9, j10, j11);
        StorageAsyncLoader.AppsStorageResult appsStorageResult = new StorageAsyncLoader.AppsStorageResult();
        appsStorageResult.gamesSize = j;
        appsStorageResult.musicAppsSize = j2;
        appsStorageResult.videoAppsSize = j3;
        appsStorageResult.photosAppsSize = j4;
        appsStorageResult.otherAppsSize = j5;
        appsStorageResult.cacheSize = j6;
        appsStorageResult.externalStats = externalStorageStats;
        SparseArray<StorageAsyncLoader.AppsStorageResult> sparseArray = new SparseArray<>();
        sparseArray.append(this.mUserId, appsStorageResult);
        return sparseArray;
    }

    public void cacheResult(PrivateStorageInfo privateStorageInfo, StorageAsyncLoader.AppsStorageResult appsStorageResult) {
        this.mSharedPreferences.edit().putLong("free_bytes", privateStorageInfo.freeBytes).putLong("total_bytes", privateStorageInfo.totalBytes).putLong("game_apps_size", appsStorageResult.gamesSize).putLong("music_apps_size", appsStorageResult.musicAppsSize).putLong("video_apps_size", appsStorageResult.videoAppsSize).putLong("photo_apps_size", appsStorageResult.photosAppsSize).putLong("other_apps_size", appsStorageResult.otherAppsSize).putLong("cache_apps_size", appsStorageResult.cacheSize).putLong("external_total_bytes", appsStorageResult.externalStats.totalBytes).putLong("external_audio_bytes", appsStorageResult.externalStats.audioBytes).putLong("external_video_bytes", appsStorageResult.externalStats.videoBytes).putLong("external_image_bytes", appsStorageResult.externalStats.imageBytes).putLong("external_apps_bytes", appsStorageResult.externalStats.appBytes).putInt("user_id", this.mUserId).putLong("last_query_timestamp", this.mClock.getCurrentTime()).apply();
    }

    private boolean isDataValid() {
        if (this.mSharedPreferences.getInt("user_id", -1) != this.mUserId) {
            return false;
        }
        if (this.mClock.getCurrentTime() - this.mSharedPreferences.getLong("last_query_timestamp", Long.MAX_VALUE) < this.mClobberThreshold.longValue()) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public static class Clock {
        Clock() {
        }

        public long getCurrentTime() {
            return System.currentTimeMillis();
        }
    }
}
