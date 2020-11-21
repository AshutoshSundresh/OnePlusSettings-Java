package com.android.settingslib.deviceinfo;

import android.app.usage.StorageStatsManager;
import android.os.storage.VolumeInfo;
import java.io.IOException;
import java.util.List;

public interface StorageVolumeProvider {
    VolumeInfo findEmulatedForPrivate(VolumeInfo volumeInfo);

    long getFreeBytes(StorageStatsManager storageStatsManager, VolumeInfo volumeInfo) throws IOException;

    long getPrimaryStorageSize();

    long getTotalBytes(StorageStatsManager storageStatsManager, VolumeInfo volumeInfo) throws IOException;

    List<VolumeInfo> getVolumes();
}
