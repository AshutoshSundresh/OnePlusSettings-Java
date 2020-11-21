package com.android.settings.applications;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import android.util.Log;
import com.android.internal.util.Preconditions;
import com.android.settingslib.applications.StorageStatsSource;
import com.android.settingslib.utils.AsyncLoaderCompat;
import java.io.IOException;

public class FetchPackageStorageAsyncLoader extends AsyncLoaderCompat<StorageStatsSource.AppStorageStats> {
    private final ApplicationInfo mInfo;
    private final StorageStatsSource mSource;
    private final UserHandle mUser;

    /* access modifiers changed from: protected */
    public void onDiscardResult(StorageStatsSource.AppStorageStats appStorageStats) {
    }

    public FetchPackageStorageAsyncLoader(Context context, StorageStatsSource storageStatsSource, ApplicationInfo applicationInfo, UserHandle userHandle) {
        super(context);
        this.mSource = (StorageStatsSource) Preconditions.checkNotNull(storageStatsSource);
        this.mInfo = applicationInfo;
        this.mUser = userHandle;
    }

    @Override // androidx.loader.content.AsyncTaskLoader
    public StorageStatsSource.AppStorageStats loadInBackground() {
        try {
            return this.mSource.getStatsForPackage(this.mInfo.volumeUuid, this.mInfo.packageName, this.mUser);
        } catch (PackageManager.NameNotFoundException | IOException e) {
            Log.w("FetchPackageStorage", "Package may have been removed during query, failing gracefully", e);
            return null;
        }
    }
}
