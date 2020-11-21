package com.android.settings;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import com.android.settings.Settings;
import com.oneplus.settings.OPMemberController;
import java.util.ArrayList;
import java.util.List;

public class SettingsInitialize extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        UserInfo userInfo = ((UserManager) context.getSystemService("user")).getUserInfo(UserHandle.myUserId());
        PackageManager packageManager = context.getPackageManager();
        managedProfileSetup(context, packageManager, intent, userInfo);
        webviewSettingSetup(context, packageManager, userInfo);
        refreshExistingShortcuts(context);
    }

    private void managedProfileSetup(Context context, PackageManager packageManager, Intent intent, UserInfo userInfo) {
        ActivityInfo activityInfo;
        Bundle bundle;
        if (userInfo != null && userInfo.isManagedProfile()) {
            Log.i("Settings", "Received broadcast: " + intent.getAction() + ". Setting up intent forwarding for managed profile.");
            packageManager.clearCrossProfileIntentFilters(userInfo.id);
            Intent intent2 = new Intent();
            intent2.addCategory("android.intent.category.DEFAULT");
            intent2.setPackage(context.getPackageName());
            List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent2, 705);
            int size = queryIntentActivities.size();
            for (int i = 0; i < size; i++) {
                ResolveInfo resolveInfo = queryIntentActivities.get(i);
                if (!(resolveInfo.filter == null || (activityInfo = resolveInfo.activityInfo) == null || (bundle = activityInfo.metaData) == null || !bundle.getBoolean("com.android.settings.PRIMARY_PROFILE_CONTROLLED"))) {
                    packageManager.addCrossProfileIntentFilter(resolveInfo.filter, userInfo.id, userInfo.profileGroupId, 2);
                }
            }
            packageManager.setComponentEnabledSetting(new ComponentName(context, Settings.class), 2, 1);
            packageManager.setComponentEnabledSetting(new ComponentName(context, Settings.CreateShortcutActivity.class), 2, 1);
        }
    }

    private void webviewSettingSetup(Context context, PackageManager packageManager, UserInfo userInfo) {
        if (userInfo != null) {
            packageManager.setComponentEnabledSetting(new ComponentName(OPMemberController.PACKAGE_NAME, "com.android.settings.WebViewImplementation"), userInfo.isAdmin() ? 1 : 2, 1);
        }
    }

    /* access modifiers changed from: package-private */
    public void refreshExistingShortcuts(Context context) {
        ShortcutManager shortcutManager = (ShortcutManager) context.getSystemService(ShortcutManager.class);
        List<ShortcutInfo> pinnedShortcuts = shortcutManager.getPinnedShortcuts();
        ArrayList arrayList = new ArrayList();
        for (ShortcutInfo shortcutInfo : pinnedShortcuts) {
            if (!shortcutInfo.isImmutable()) {
                Intent intent = shortcutInfo.getIntent();
                intent.setFlags(335544320);
                arrayList.add(new ShortcutInfo.Builder(context, shortcutInfo.getId()).setIntent(intent).build());
            }
        }
        shortcutManager.updateShortcuts(arrayList);
    }
}
