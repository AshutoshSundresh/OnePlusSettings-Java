package com.oneplus.settings.packageuninstaller;

import android.content.BroadcastReceiver;
import android.content.Context;
import com.oneplus.settings.packageuninstaller.EventResultPersister;

public class UninstallEventReceiver extends BroadcastReceiver {
    private static final Object sLock = new Object();
    private static EventResultPersister sReceiver;

    private static EventResultPersister getReceiver(Context context) {
        synchronized (sLock) {
            if (sReceiver == null) {
                sReceiver = new EventResultPersister(TemporaryFileManager.getUninstallStateFile(context));
            }
        }
        return sReceiver;
    }

    static int getNewId(Context context) throws EventResultPersister.OutOfIdsException {
        return getReceiver(context).getNewId();
    }
}
