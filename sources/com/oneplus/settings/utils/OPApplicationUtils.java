package com.oneplus.settings.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.pm.ApplicationInfo;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodInfo;
import com.android.internal.view.IInputMethodManager;
import com.android.settings.C0003R$array;
import com.oneplus.settings.OPMemberController;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.highpowerapp.PackageUtils;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class OPApplicationUtils {
    public static boolean isSystemAndNonUpdate(ApplicationInfo applicationInfo) {
        if (applicationInfo == null) {
            return false;
        }
        int i = applicationInfo.flags;
        return (i & 1) == 1 && (i & 128) == 0;
    }

    public static boolean isSystemUpdateAndOneplus(ApplicationInfo applicationInfo) {
        if (applicationInfo == null) {
            return false;
        }
        int i = applicationInfo.flags;
        return (i & 1) == 1 && (i & 128) == 128 && applicationInfo.packageName.contains("oneplus");
    }

    public static boolean isOnePlusH2UninstallationApp(String str) {
        List asList = Arrays.asList(SettingsBaseApplication.mApplication.getResources().getStringArray(C0003R$array.oneplus_uninstallation_app_list_H2));
        if (asList == null) {
            return false;
        }
        return asList.contains(str);
    }

    public static boolean isOnePlusO2UninstallationApp(String str) {
        List asList = Arrays.asList(SettingsBaseApplication.mApplication.getResources().getStringArray(C0003R$array.oneplus_uninstallation_app_list_O2));
        if (asList == null) {
            return false;
        }
        return asList.contains(str);
    }

    public static boolean isInNotKillAppWhiteList(String str) {
        List asList = Arrays.asList(SettingsBaseApplication.mApplication.getResources().getStringArray(C0003R$array.oneplus_not_kill_app_white_list));
        if (asList == null) {
            return false;
        }
        return asList.contains(str);
    }

    public static boolean isIMQuickReplyApps(String str) {
        List asList = Arrays.asList(SettingsBaseApplication.mApplication.getResources().getStringArray(C0003R$array.oneplus_im_quick_reply_app_lists));
        if (asList == null) {
            return false;
        }
        return asList.contains(str);
    }

    public static void killProcess(ActivityManager activityManager, boolean z) {
        if (activityManager == null) {
            Log.d("OPApplicationUtils", "ActivityManager is null");
            return;
        }
        killSomeProcess(activityManager);
        killRunningProcess(activityManager, z);
        killSystemInputMethods(activityManager);
        removeRunningTask();
    }

    public static void killSomeProcess(ActivityManager activityManager) {
        activityManager.killBackgroundProcesses("com.android.dialer");
        activityManager.killBackgroundProcesses("com.android.contacts");
        activityManager.killBackgroundProcesses("com.oneplus.mms");
    }

    public static void removeRunningTask() {
        List<ActivityManager.RecentTaskInfo> list;
        try {
            list = ActivityManager.getService().getRecentTasks(Integer.MAX_VALUE, 2, -2).getList();
        } catch (Exception e) {
            e.printStackTrace();
            list = null;
        }
        if (list != null) {
            boolean z = false;
            for (ActivityManager.RecentTaskInfo recentTaskInfo : list) {
                if (!z) {
                    ComponentName componentName = recentTaskInfo != null ? recentTaskInfo.topActivity : null;
                    if (componentName != null && OPMemberController.PACKAGE_NAME.equals(componentName.getPackageName())) {
                        z = true;
                    }
                }
                if (!isInNotKillAppWhiteList(recentTaskInfo.baseIntent.getComponent().getPackageName()) && recentTaskInfo != null) {
                    try {
                        ActivityManager.getService().removeTask(recentTaskInfo.persistentId);
                    } catch (RemoteException e2) {
                        Log.w("OPApplicationUtils", "Failed to remove task=" + recentTaskInfo.persistentId, e2);
                    }
                }
            }
        }
    }

    public static void killRunningTargetProcess(ActivityManager activityManager, String str) {
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        if (runningAppProcesses != null) {
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
                if (runningAppProcessInfo != null && TextUtils.equals(str, runningAppProcessInfo.processName)) {
                    activityManager.killUid(runningAppProcessInfo.uid, "remove face unlock");
                    Log.i("OPApplicationUtils", "killRunningTargetProcess-packageName:" + str + " pid:" + runningAppProcessInfo.pid + " uid:" + runningAppProcessInfo.uid);
                }
            }
        }
    }

    public static void killRunningProcess(ActivityManager activityManager, boolean z) {
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        if (runningAppProcesses != null) {
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
                if (runningAppProcessInfo != null && !PackageUtils.isSystemApplication(SettingsBaseApplication.mApplication.getApplicationContext(), runningAppProcessInfo.processName) && runningAppProcessInfo.uid > 10000) {
                    if ((!OPUtils.isO2() || !isOnePlusO2UninstallationApp(runningAppProcessInfo.processName)) && ((OPUtils.isO2() || !isOnePlusH2UninstallationApp(runningAppProcessInfo.processName)) && !isInNotKillAppWhiteList(runningAppProcessInfo.processName))) {
                        Log.d("OPApplicationUtils", "killRunningProcess--processName:" + runningAppProcessInfo.processName + " uid:" + runningAppProcessInfo.uid);
                        activityManager.killUid(runningAppProcessInfo.uid, "change screen resolution");
                        String stringForUser = Settings.Secure.getStringForUser(SettingsBaseApplication.mApplication.getApplicationContext().getContentResolver(), "voice_interaction_service", -2);
                        String stringForUser2 = Settings.Secure.getStringForUser(SettingsBaseApplication.mApplication.getApplicationContext().getContentResolver(), "voice_recognition_service", -2);
                        if (!TextUtils.isEmpty(stringForUser) || !TextUtils.isEmpty(stringForUser2)) {
                            if (!TextUtils.isEmpty(stringForUser)) {
                                String str = stringForUser.split("\\/")[0];
                                if (TextUtils.equals(runningAppProcessInfo.processName, str) || runningAppProcessInfo.processName.contains(str)) {
                                    Log.d("OPScreenResolutionAdjust", "forceStopPackage-curInteractor-PackageName:" + str);
                                    activityManager.forceStopPackage(str);
                                }
                            }
                            if (!TextUtils.isEmpty(stringForUser2)) {
                                String str2 = stringForUser2.split("\\/")[0];
                                if (TextUtils.equals(runningAppProcessInfo.processName, str2) || runningAppProcessInfo.processName.contains(str2)) {
                                    Log.d("OPScreenResolutionAdjust", "forceStopPackage-curRecognizer-PackageName:" + str2);
                                    activityManager.forceStopPackage(str2);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void forceStopPackage(ActivityManager activityManager) {
        for (String str : Arrays.asList(SettingsBaseApplication.mApplication.getResources().getStringArray(C0003R$array.oneplus_need_force_stop_app_list))) {
            Log.d("OPScreenResolutionAdjust", "forceStopPackage-PackageName:" + str);
            activityManager.forceStopPackage(str);
        }
    }

    public static void killSystemInputMethods(ActivityManager activityManager) {
        int i;
        try {
            List<InputMethodInfo> inputMethodList = getIInputMethodManager().getInputMethodList(-2);
            new HashSet();
            for (InputMethodInfo inputMethodInfo : inputMethodList) {
                ApplicationInfo applicationInfo = inputMethodInfo.getServiceInfo().applicationInfo;
                Log.d("OPApplicationUtils", "SystemInputMethods--processName:" + applicationInfo.processName + " uid:" + applicationInfo.uid);
                if (!PackageUtils.isSystemApplication(SettingsBaseApplication.mApplication.getApplicationContext(), applicationInfo.processName) && (i = applicationInfo.uid) > 10000) {
                    activityManager.killUid(i, "change screen resolution");
                }
            }
        } catch (RemoteException e) {
            Log.e("OPApplicationUtils", "Could not communicate with IInputMethodManager", e);
        }
    }

    public static IInputMethodManager getIInputMethodManager() {
        return IInputMethodManager.Stub.asInterface(ServiceManager.getService("input_method"));
    }
}
