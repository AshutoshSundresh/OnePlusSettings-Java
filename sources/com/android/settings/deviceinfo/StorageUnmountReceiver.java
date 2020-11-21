package com.android.settings.deviceinfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.util.Log;
import com.android.settings.deviceinfo.StorageSettings;

public class StorageUnmountReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        String stringExtra = intent.getStringExtra("android.os.storage.extra.VOLUME_ID");
        VolumeInfo findVolumeById = ((StorageManager) context.getSystemService(StorageManager.class)).findVolumeById(stringExtra);
        if (findVolumeById != null) {
            new StorageSettings.UnmountTask(context, findVolumeById).execute(new Void[0]);
            return;
        }
        Log.w("StorageSettings", "Missing volume " + stringExtra);
    }
}
