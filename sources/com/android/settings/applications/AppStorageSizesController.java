package com.android.settings.applications;

import android.content.Context;
import android.text.format.Formatter;
import androidx.preference.Preference;
import com.android.internal.util.Preconditions;
import com.android.settingslib.applications.StorageStatsSource;

public class AppStorageSizesController {
    private final Preference mAppSize;
    private final Preference mCacheSize;
    private boolean mCachedCleared;
    private final int mComputing;
    private boolean mDataCleared;
    private final Preference mDataSize;
    private final int mError;
    private long mLastCacheSize;
    private long mLastCodeSize;
    private long mLastDataSize;
    private StorageStatsSource.AppStorageStats mLastResult;
    private boolean mLastResultFailed;
    private long mLastTotalSize;
    private final Preference mTotalSize;

    private AppStorageSizesController(Preference preference, Preference preference2, Preference preference3, Preference preference4, int i, int i2) {
        this.mLastCodeSize = -1;
        this.mLastDataSize = -1;
        this.mLastCacheSize = -1;
        this.mLastTotalSize = -1;
        this.mTotalSize = preference;
        this.mAppSize = preference2;
        this.mDataSize = preference3;
        this.mCacheSize = preference4;
        this.mComputing = i;
        this.mError = i2;
    }

    public void updateUi(Context context) {
        StorageStatsSource.AppStorageStats appStorageStats = this.mLastResult;
        if (appStorageStats == null) {
            int i = this.mLastResultFailed ? this.mError : this.mComputing;
            this.mAppSize.setSummary(i);
            this.mDataSize.setSummary(i);
            this.mCacheSize.setSummary(i);
            this.mTotalSize.setSummary(i);
            return;
        }
        long codeBytes = appStorageStats.getCodeBytes();
        long j = 0;
        long dataBytes = this.mDataCleared ? 0 : this.mLastResult.getDataBytes() - this.mLastResult.getCacheBytes();
        if (this.mLastCodeSize != codeBytes) {
            this.mLastCodeSize = codeBytes;
            this.mAppSize.setSummary(getSizeStr(context, codeBytes));
        }
        if (this.mLastDataSize != dataBytes) {
            this.mLastDataSize = dataBytes;
            this.mDataSize.setSummary(getSizeStr(context, dataBytes));
        }
        if (!this.mDataCleared && !this.mCachedCleared) {
            j = this.mLastResult.getCacheBytes();
        }
        if (this.mLastCacheSize != j) {
            this.mLastCacheSize = j;
            this.mCacheSize.setSummary(getSizeStr(context, j));
        }
        long j2 = codeBytes + dataBytes + j;
        if (this.mLastTotalSize != j2) {
            this.mLastTotalSize = j2;
            this.mTotalSize.setSummary(getSizeStr(context, j2));
        }
    }

    public void setResult(StorageStatsSource.AppStorageStats appStorageStats) {
        this.mLastResult = appStorageStats;
        this.mLastResultFailed = appStorageStats == null;
    }

    public void setCacheCleared(boolean z) {
        this.mCachedCleared = z;
    }

    public void setDataCleared(boolean z) {
        this.mDataCleared = z;
    }

    public StorageStatsSource.AppStorageStats getLastResult() {
        return this.mLastResult;
    }

    private String getSizeStr(Context context, long j) {
        return Formatter.formatFileSize(context, j);
    }

    public static class Builder {
        private Preference mAppSize;
        private Preference mCacheSize;
        private int mComputing;
        private Preference mDataSize;
        private int mError;
        private Preference mTotalSize;

        public Builder setAppSizePreference(Preference preference) {
            this.mAppSize = preference;
            return this;
        }

        public Builder setDataSizePreference(Preference preference) {
            this.mDataSize = preference;
            return this;
        }

        public Builder setCacheSizePreference(Preference preference) {
            this.mCacheSize = preference;
            return this;
        }

        public Builder setTotalSizePreference(Preference preference) {
            this.mTotalSize = preference;
            return this;
        }

        public Builder setComputingString(int i) {
            this.mComputing = i;
            return this;
        }

        public Builder setErrorString(int i) {
            this.mError = i;
            return this;
        }

        public AppStorageSizesController build() {
            return new AppStorageSizesController((Preference) Preconditions.checkNotNull(this.mTotalSize), (Preference) Preconditions.checkNotNull(this.mAppSize), (Preference) Preconditions.checkNotNull(this.mDataSize), (Preference) Preconditions.checkNotNull(this.mCacheSize), this.mComputing, this.mError);
        }
    }
}
