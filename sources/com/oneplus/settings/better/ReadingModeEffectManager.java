package com.oneplus.settings.better;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import com.oneplus.settings.utils.OPUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadingModeEffectManager {
    private static Map<String, OPAppModel> mChromaticAppMap = new HashMap();
    private static ReadingModeEffectManager mDisplaySizeAdaptiongeManager;
    private static Map<String, OPAppModel> mMonoAppMap = new HashMap();
    private static Map<String, OPAppModel> mTmpChromaticAppMap = new HashMap();
    private static Map<String, OPAppModel> mTmpMonoAppMap = new HashMap();
    private AppOpsManager mAppOpsManager;
    private Context mContext;
    private PackageManager mPackageManager = this.mContext.getPackageManager();

    public ReadingModeEffectManager(Context context) {
        this.mContext = context;
        this.mAppOpsManager = (AppOpsManager) context.getSystemService("appops");
        ActivityManager activityManager = (ActivityManager) this.mContext.getSystemService("activity");
    }

    public static ReadingModeEffectManager getInstance(Context context) {
        if (mDisplaySizeAdaptiongeManager == null) {
            mDisplaySizeAdaptiongeManager = new ReadingModeEffectManager(context);
        }
        return mDisplaySizeAdaptiongeManager;
    }

    public Map<String, OPAppModel> loadAppMap() {
        loadChromaticApp();
        loadMonoApp();
        HashMap hashMap = new HashMap();
        try {
            Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
            intent.addCategory("android.intent.category.LAUNCHER");
            List<ResolveInfo> queryIntentActivities = this.mPackageManager.queryIntentActivities(intent, 0);
            if (queryIntentActivities.isEmpty()) {
                return null;
            }
            for (ResolveInfo resolveInfo : queryIntentActivities) {
                String str = resolveInfo.activityInfo.name;
                String str2 = resolveInfo.activityInfo.packageName;
                hashMap.put(str2, new OPAppModel(str2, (String) resolveInfo.loadLabel(this.mPackageManager), "", 0, false));
            }
            return hashMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadClassAppList(int i) {
        HashMap hashMap = new HashMap();
        try {
            List<AppOpsManager.PackageOps> packagesForOps = this.mAppOpsManager.getPackagesForOps(new int[]{1003});
            if (packagesForOps != null) {
                for (AppOpsManager.PackageOps packageOps : packagesForOps) {
                    int uid = packageOps.getUid();
                    for (AppOpsManager.OpEntry opEntry : packageOps.getOps()) {
                        if (opEntry.getOp() == 1003 && opEntry.getMode() == i) {
                            hashMap.put(uid + packageOps.getPackageName(), Integer.valueOf(uid));
                        }
                    }
                    boolean containsKey = hashMap.containsKey(uid + packageOps.getPackageName());
                    if (containsKey) {
                        OPAppModel oPAppModel = new OPAppModel(packageOps.getPackageName(), OPUtils.getAppLabel(this.mContext, packageOps.getPackageName()), "", 0, containsKey);
                        if (i == 0) {
                            Map<String, OPAppModel> map = mTmpMonoAppMap;
                            map.put(uid + packageOps.getPackageName(), oPAppModel);
                            Map<String, OPAppModel> map2 = mTmpChromaticAppMap;
                            map2.remove(uid + packageOps.getPackageName());
                        } else if (i == 2) {
                            Map<String, OPAppModel> map3 = mTmpChromaticAppMap;
                            map3.put(uid + packageOps.getPackageName(), oPAppModel);
                            Map<String, OPAppModel> map4 = mTmpMonoAppMap;
                            map4.remove(uid + packageOps.getPackageName());
                        } else {
                            Map<String, OPAppModel> map5 = mTmpChromaticAppMap;
                            map5.remove(uid + packageOps.getPackageName());
                            Map<String, OPAppModel> map6 = mTmpMonoAppMap;
                            map6.remove(uid + packageOps.getPackageName());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadChromaticApp() {
        loadClassAppList(2);
        mChromaticAppMap.clear();
        mChromaticAppMap = new HashMap(mTmpChromaticAppMap);
        mTmpChromaticAppMap.clear();
    }

    private void loadMonoApp() {
        loadClassAppList(0);
        mMonoAppMap.clear();
        mMonoAppMap = new HashMap(mTmpMonoAppMap);
        mTmpMonoAppMap.clear();
    }

    public void setAppEffectSelect(int i, String str, int i2) {
        this.mAppOpsManager.setMode(1003, i, str, i2);
        removeTask(str);
        try {
            OPAppModel oPAppModel = new OPAppModel(str, this.mPackageManager.getApplicationInfo(str, 0).loadLabel(this.mPackageManager).toString(), "", i, false);
            if (i2 == 2) {
                Map<String, OPAppModel> map = mChromaticAppMap;
                map.put(i + str, oPAppModel);
                Map<String, OPAppModel> map2 = mMonoAppMap;
                map2.remove(i + str);
            } else if (i2 == 0) {
                Map<String, OPAppModel> map3 = mMonoAppMap;
                map3.put(i + str, oPAppModel);
                Map<String, OPAppModel> map4 = mChromaticAppMap;
                map4.remove(i + str);
            } else {
                Map<String, OPAppModel> map5 = mChromaticAppMap;
                map5.remove(i + str);
                Map<String, OPAppModel> map6 = mMonoAppMap;
                map6.remove(i + str);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("ReadingModeEffectManager", e.getMessage());
        }
    }

    public int getAppEffectSelectValue(String str) {
        OPAppModel oPAppModel = mMonoAppMap.get(str);
        OPAppModel oPAppModel2 = mChromaticAppMap.get(str);
        if (oPAppModel != null) {
            return 0;
        }
        return oPAppModel2 != null ? 2 : 3;
    }

    private void removeTask(String str) {
        List<ActivityManager.RecentTaskInfo> list;
        try {
            list = ActivityManager.getService().getRecentTasks(Integer.MAX_VALUE, 2, -2).getList();
        } catch (Exception e) {
            e.printStackTrace();
            list = null;
        }
        if (list != null) {
            for (ActivityManager.RecentTaskInfo recentTaskInfo : list) {
                ComponentName componentName = recentTaskInfo != null ? recentTaskInfo.baseActivity : null;
                if (componentName != null && !TextUtils.isEmpty(str) && str.equals(componentName.getPackageName())) {
                    try {
                        ActivityManager.getService().removeTask(recentTaskInfo.persistentId);
                    } catch (RemoteException e2) {
                        e2.printStackTrace();
                    }
                }
            }
        }
    }
}
