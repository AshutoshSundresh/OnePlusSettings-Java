package com.android.settings.users;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.UserHandle;
import android.os.UserManager;
import com.android.settings.Utils;

public class ProfileUpdateReceiver extends BroadcastReceiver {
    public void onReceive(final Context context, Intent intent) {
        new Thread(this) {
            /* class com.android.settings.users.ProfileUpdateReceiver.AnonymousClass1 */

            public void run() {
                UserSettings.copyMeProfilePhoto(context, null);
                ProfileUpdateReceiver.copyProfileName(context);
            }
        }.start();
    }

    /* access modifiers changed from: private */
    public static void copyProfileName(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("profile", 0);
        if (!sharedPreferences.contains("name_copied_once")) {
            int myUserId = UserHandle.myUserId();
            UserManager userManager = (UserManager) context.getSystemService("user");
            String meProfileName = Utils.getMeProfileName(context, false);
            if (meProfileName != null && meProfileName.length() > 0) {
                userManager.setUserName(myUserId, meProfileName);
                sharedPreferences.edit().putBoolean("name_copied_once", true).commit();
            }
        }
    }
}
