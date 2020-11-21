package com.oneplus.security.utils;

import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.database.ContentObserver;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.provider.MediaStore;
import java.io.IOException;

public class FileSizeUtil {
    public static void unRegisterStorageDBObserver(Context context, ContentObserver contentObserver) {
        context.getContentResolver().unregisterContentObserver(contentObserver);
    }

    public static void registerStorageDBObserver(Context context, ContentObserver contentObserver) {
        context.getContentResolver().registerContentObserver(MediaStore.Files.getContentUri("external"), true, contentObserver);
        context.getContentResolver().registerContentObserver(MediaStore.Audio.Media.getContentUri("external"), true, contentObserver);
        context.getContentResolver().registerContentObserver(MediaStore.Video.Media.getContentUri("external"), true, contentObserver);
        context.getContentResolver().registerContentObserver(MediaStore.Images.Media.getContentUri("external"), true, contentObserver);
    }

    public static int getAvailableStoragePercentValue(StorageManager storageManager, Context context) {
        StorageStatsManager storageStatsManager = (StorageStatsManager) context.getSystemService(StorageStatsManager.class);
        VolumeInfo findVolumeById = storageManager.findVolumeById("private");
        try {
            long totalBytes = storageStatsManager.getTotalBytes(findVolumeById.getFsUuid());
            long freeBytes = storageStatsManager.getFreeBytes(findVolumeById.getFsUuid());
            double d = ((double) freeBytes) / ((double) totalBytes);
            LogUtils.d("FileSizeUtil", "getAvailableStoragePercentValue totalBytes=" + totalBytes + ",freeBytes=" + freeBytes + ",availableStoragePercent=" + d);
            return (int) Math.rint(d * 100.0d);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
