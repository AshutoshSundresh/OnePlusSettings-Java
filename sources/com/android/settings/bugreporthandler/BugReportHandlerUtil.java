package com.android.settings.bugreporthandler;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;
import com.android.settings.C0017R$string;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BugReportHandlerUtil {
    public boolean isBugReportHandlerEnabled(Context context) {
        return context.getResources().getBoolean(17891385);
    }

    public Pair<String, Integer> getCurrentBugReportHandlerAppAndUser(Context context) {
        boolean z;
        String customBugReportHandlerApp = getCustomBugReportHandlerApp(context);
        int customBugReportHandlerUser = getCustomBugReportHandlerUser(context);
        boolean z2 = true;
        int i = 0;
        if (!isBugreportWhitelistedApp(customBugReportHandlerApp)) {
            customBugReportHandlerApp = getDefaultBugReportHandlerApp(context);
            customBugReportHandlerUser = 0;
            z = false;
        } else if (getBugReportHandlerAppReceivers(context, customBugReportHandlerApp, customBugReportHandlerUser).isEmpty()) {
            customBugReportHandlerApp = getDefaultBugReportHandlerApp(context);
            z = true;
            customBugReportHandlerUser = 0;
        } else {
            z = false;
        }
        if (!isBugreportWhitelistedApp(customBugReportHandlerApp) || getBugReportHandlerAppReceivers(context, customBugReportHandlerApp, customBugReportHandlerUser).isEmpty()) {
            customBugReportHandlerApp = "com.android.shell";
        } else {
            i = customBugReportHandlerUser;
            z2 = z;
        }
        if (z2) {
            setBugreportHandlerAppAndUser(context, customBugReportHandlerApp, i);
        }
        return Pair.create(customBugReportHandlerApp, Integer.valueOf(i));
    }

    private String getCustomBugReportHandlerApp(Context context) {
        return Settings.Global.getString(context.getContentResolver(), "custom_bugreport_handler_app");
    }

    private int getCustomBugReportHandlerUser(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "custom_bugreport_handler_user", -10000);
    }

    private String getDefaultBugReportHandlerApp(Context context) {
        return context.getResources().getString(17039872);
    }

    public boolean setCurrentBugReportHandlerAppAndUser(Context context, String str, int i) {
        if (!isBugreportWhitelistedApp(str) || getBugReportHandlerAppReceivers(context, str, i).isEmpty()) {
            return false;
        }
        setBugreportHandlerAppAndUser(context, str, i);
        return true;
    }

    public List<Pair<ApplicationInfo, Integer>> getValidBugReportHandlerInfos(Context context) {
        ArrayList arrayList = new ArrayList();
        try {
            List bugreportWhitelistedPackages = ActivityManager.getService().getBugreportWhitelistedPackages();
            if (bugreportWhitelistedPackages.contains("com.android.shell") && !getBugReportHandlerAppReceivers(context, "com.android.shell", 0).isEmpty()) {
                try {
                    arrayList.add(Pair.create(context.getPackageManager().getApplicationInfo("com.android.shell", 4194304), 0));
                } catch (PackageManager.NameNotFoundException unused) {
                }
            }
            List<UserInfo> profiles = ((UserManager) context.getSystemService(UserManager.class)).getProfiles(UserHandle.getCallingUserId());
            List<String> list = (List) bugreportWhitelistedPackages.stream().filter($$Lambda$BugReportHandlerUtil$ivctevKDF8hpktSTBu58VhPy8tw.INSTANCE).collect(Collectors.toList());
            Collections.sort(list);
            for (String str : list) {
                for (UserInfo userInfo : profiles) {
                    int identifier = userInfo.getUserHandle().getIdentifier();
                    if (!getBugReportHandlerAppReceivers(context, str, identifier).isEmpty()) {
                        try {
                            arrayList.add(Pair.create(context.getPackageManager().getApplicationInfo(str, 4194304), Integer.valueOf(identifier)));
                        } catch (PackageManager.NameNotFoundException unused2) {
                        }
                    }
                }
            }
            return arrayList;
        } catch (RemoteException e) {
            Log.e("BugReportHandlerUtil", "Failed to get bugreportWhitelistedPackages:", e);
            return arrayList;
        }
    }

    static /* synthetic */ boolean lambda$getValidBugReportHandlerInfos$0(String str) {
        return !"com.android.shell".equals(str);
    }

    private boolean isBugreportWhitelistedApp(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        try {
            return ActivityManager.getService().getBugreportWhitelistedPackages().contains(str);
        } catch (RemoteException e) {
            Log.e("BugReportHandlerUtil", "Failed to get bugreportWhitelistedPackages:", e);
            return false;
        }
    }

    private List<ResolveInfo> getBugReportHandlerAppReceivers(Context context, String str, int i) {
        Intent intent = new Intent("com.android.internal.intent.action.BUGREPORT_REQUESTED");
        intent.setPackage(str);
        return context.getPackageManager().queryBroadcastReceiversAsUser(intent, 1048576, i);
    }

    private void setBugreportHandlerAppAndUser(Context context, String str, int i) {
        Settings.Global.putString(context.getContentResolver(), "custom_bugreport_handler_app", str);
        Settings.Global.putInt(context.getContentResolver(), "custom_bugreport_handler_user", i);
    }

    public void showInvalidChoiceToast(Context context) {
        Toast.makeText(context, C0017R$string.select_invalid_bug_report_handler_toast_text, 0).show();
    }
}
