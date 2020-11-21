package com.android.settings.password;

import android.app.ActivityManager;
import android.app.IActivityManager;
import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import com.android.settingslib.Utils;
import com.oneplus.settings.OPMemberController;

public final class PasswordUtils extends Utils {
    public static boolean isCallingAppPermitted(Context context, IBinder iBinder, String str) {
        try {
            if (context.checkPermission(str, -1, ActivityManager.getService().getLaunchedFromUid(iBinder)) == 0) {
                return true;
            }
            return false;
        } catch (RemoteException e) {
            Log.v("Settings", "Could not talk to activity manager.", e);
            return false;
        }
    }

    public static CharSequence getCallingAppLabel(Context context, IBinder iBinder) {
        String callingAppPackageName = getCallingAppPackageName(iBinder);
        if (callingAppPackageName == null || callingAppPackageName.equals(OPMemberController.PACKAGE_NAME)) {
            return null;
        }
        return com.android.settings.Utils.getApplicationLabel(context, callingAppPackageName);
    }

    public static String getCallingAppPackageName(IBinder iBinder) {
        try {
            return ActivityManager.getService().getLaunchedFromPackage(iBinder);
        } catch (RemoteException e) {
            Log.v("Settings", "Could not talk to activity manager.", e);
            return null;
        }
    }

    public static void crashCallingApplication(IBinder iBinder, String str) {
        IActivityManager service = ActivityManager.getService();
        try {
            int launchedFromUid = service.getLaunchedFromUid(iBinder);
            service.crashApplication(launchedFromUid, -1, getCallingAppPackageName(iBinder), UserHandle.getUserId(launchedFromUid), str, false);
        } catch (RemoteException e) {
            Log.v("Settings", "Could not talk to activity manager.", e);
        }
    }
}
