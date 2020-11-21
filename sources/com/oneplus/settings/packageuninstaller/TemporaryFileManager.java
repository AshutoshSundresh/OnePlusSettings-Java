package com.oneplus.settings.packageuninstaller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import java.io.File;

public class TemporaryFileManager extends BroadcastReceiver {
    private static final String LOG_TAG = TemporaryFileManager.class.getSimpleName();

    public static File getUninstallStateFile(Context context) {
        return new File(context.getNoBackupFilesDir(), "uninstall_results.xml");
    }

    public void onReceive(Context context, Intent intent) {
        String str = LOG_TAG;
        long currentTimeMillis = System.currentTimeMillis() - SystemClock.elapsedRealtime();
        File[] listFiles = context.getNoBackupFilesDir().listFiles();
        if (listFiles != null) {
            for (File file : listFiles) {
                if (currentTimeMillis <= file.lastModified()) {
                    Log.w(str, file.getName() + " was created before onBoot broadcast was received");
                } else if (!file.delete()) {
                    Log.w(str, "Could not delete " + file.getName() + " onBoot");
                }
            }
        }
    }
}
