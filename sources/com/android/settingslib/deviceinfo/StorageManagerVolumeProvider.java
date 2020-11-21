package com.android.settingslib.deviceinfo;

import android.app.usage.StorageStatsManager;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import java.io.IOException;
import java.util.List;

public class StorageManagerVolumeProvider implements StorageVolumeProvider {
    private StorageManager mStorageManager;

    public StorageManagerVolumeProvider(StorageManager storageManager) {
        this.mStorageManager = storageManager;
    }

    @Override // com.android.settingslib.deviceinfo.StorageVolumeProvider
    public long getPrimaryStorageSize() {
        return this.mStorageManager.getPrimaryStorageSize();
    }

    @Override // com.android.settingslib.deviceinfo.StorageVolumeProvider
    public List<VolumeInfo> getVolumes() {
        return this.mStorageManager.getVolumes();
    }

    @Override // com.android.settingslib.deviceinfo.StorageVolumeProvider
    public VolumeInfo findEmulatedForPrivate(VolumeInfo volumeInfo) {
        return this.mStorageManager.findEmulatedForPrivate(volumeInfo);
    }

    @Override // com.android.settingslib.deviceinfo.StorageVolumeProvider
    public long getTotalBytes(StorageStatsManager storageStatsManager, VolumeInfo volumeInfo) throws IOException {
        return storageStatsManager.getTotalBytes(volumeInfo.getFsUuid());
    }

    @Override // com.android.settingslib.deviceinfo.StorageVolumeProvider
    public long getFreeBytes(StorageStatsManager storageStatsManager, VolumeInfo volumeInfo) throws IOException {
        return storageStatsManager.getFreeBytes(volumeInfo.getFsUuid());
    }
}
