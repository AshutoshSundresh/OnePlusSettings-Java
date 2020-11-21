package com.android.settings.deviceinfo.storage;

import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.os.storage.VolumeInfo;
import com.android.settingslib.deviceinfo.PrivateStorageInfo;
import com.android.settingslib.deviceinfo.StorageVolumeProvider;
import com.android.settingslib.utils.AsyncLoaderCompat;
import java.io.IOException;

public class VolumeSizesLoader extends AsyncLoaderCompat<PrivateStorageInfo> {
    private StorageStatsManager mStats;
    private VolumeInfo mVolume;
    private StorageVolumeProvider mVolumeProvider;

    /* access modifiers changed from: protected */
    public void onDiscardResult(PrivateStorageInfo privateStorageInfo) {
    }

    public VolumeSizesLoader(Context context, StorageVolumeProvider storageVolumeProvider, StorageStatsManager storageStatsManager, VolumeInfo volumeInfo) {
        super(context);
        this.mVolumeProvider = storageVolumeProvider;
        this.mStats = storageStatsManager;
        this.mVolume = volumeInfo;
    }

    @Override // androidx.loader.content.AsyncTaskLoader
    public PrivateStorageInfo loadInBackground() {
        try {
            return getVolumeSize(this.mVolumeProvider, this.mStats, this.mVolume);
        } catch (IOException unused) {
            return null;
        }
    }

    static PrivateStorageInfo getVolumeSize(StorageVolumeProvider storageVolumeProvider, StorageStatsManager storageStatsManager, VolumeInfo volumeInfo) throws IOException {
        return new PrivateStorageInfo(storageVolumeProvider.getFreeBytes(storageStatsManager, volumeInfo), storageVolumeProvider.getTotalBytes(storageStatsManager, volumeInfo));
    }
}
