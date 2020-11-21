package com.oneplus.settings.displaysizeadaption;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import androidx.constraintlayout.widget.R$styleable;
import com.oneplus.settings.better.OPAppModel;
import com.oneplus.settings.utils.OPUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisplaySizeAdaptiongeManager {
    private static Map<String, OPAppModel> m17819FullScreenAppMap = new HashMap();
    private static DisplaySizeAdaptiongeManager mDisplaySizeAdaptiongeManager;
    private static Map<String, OPAppModel> mFullScreenAppMap = new HashMap();
    private static Map<String, OPAppModel> mOriginalSizeAppMap = new HashMap();
    private static Map<String, OPAppModel> mTmp17819FullScreenAppMap = new HashMap();
    private static Map<String, OPAppModel> mTmpFullScreenAppMap = new HashMap();
    private static Map<String, OPAppModel> mTmpOriginalSizeAppMap = new HashMap();
    private AppOpsManager mAppOpsManager;
    private Context mContext;
    private PackageManager mPackageManager;
    ApplicationInfo multiAppInfo = null;

    public DisplaySizeAdaptiongeManager(Context context) {
        this.mContext = context;
        this.mAppOpsManager = (AppOpsManager) context.getSystemService("appops");
        this.mPackageManager = this.mContext.getPackageManager();
        ActivityManager activityManager = (ActivityManager) this.mContext.getSystemService("activity");
    }

    public static DisplaySizeAdaptiongeManager getInstance(Context context) {
        if (mDisplaySizeAdaptiongeManager == null) {
            mDisplaySizeAdaptiongeManager = new DisplaySizeAdaptiongeManager(context);
        }
        return mDisplaySizeAdaptiongeManager;
    }

    public Map<String, OPAppModel> loadAppMap() {
        loadFullScreenApp();
        loadOriginalSizeApp();
        HashMap hashMap = new HashMap();
        try {
            Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
            if (!OPUtils.isSupportScreenCutting()) {
                intent.addCategory("android.intent.category.LAUNCHER");
            }
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
            List<AppOpsManager.PackageOps> packagesForOps = this.mAppOpsManager.getPackagesForOps(new int[]{1006});
            if (packagesForOps != null) {
                for (AppOpsManager.PackageOps packageOps : packagesForOps) {
                    int uid = packageOps.getUid();
                    for (AppOpsManager.OpEntry opEntry : packageOps.getOps()) {
                        if (opEntry.getOp() == 1006 && opEntry.getMode() == i) {
                            hashMap.put(packageOps.getPackageName(), Integer.valueOf(uid));
                        }
                    }
                }
            }
            Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
            if (!OPUtils.isSupportScreenCutting()) {
                intent.addCategory("android.intent.category.LAUNCHER");
            }
            List<ResolveInfo> queryIntentActivities = this.mPackageManager.queryIntentActivities(intent, 0);
            if (!queryIntentActivities.isEmpty()) {
                for (ResolveInfo resolveInfo : queryIntentActivities) {
                    String str = resolveInfo.activityInfo.name;
                    String str2 = resolveInfo.activityInfo.packageName;
                    String str3 = (String) resolveInfo.loadLabel(this.mPackageManager);
                    boolean containsKey = hashMap.containsKey(str2);
                    if (containsKey) {
                        OPAppModel oPAppModel = new OPAppModel(str2, str3, "", 0, containsKey);
                        if (i != 0) {
                            if (i != 100 || OPUtils.isSupportScreenCutting()) {
                                if (i != 1) {
                                    if (i != 101) {
                                        if (!OPUtils.isSupportScreenCutting() || !(i == 2 || i == 102)) {
                                            mTmpFullScreenAppMap.remove(str2);
                                            mTmpOriginalSizeAppMap.remove(str2);
                                            mTmp17819FullScreenAppMap.remove(str2);
                                        } else {
                                            mTmpFullScreenAppMap.remove(str2);
                                            mTmpOriginalSizeAppMap.remove(str2);
                                            mTmp17819FullScreenAppMap.put(str2, oPAppModel);
                                        }
                                    }
                                }
                                mTmpFullScreenAppMap.put(str2, oPAppModel);
                                mTmpOriginalSizeAppMap.remove(str2);
                                mTmp17819FullScreenAppMap.remove(str2);
                            }
                        }
                        mTmpOriginalSizeAppMap.put(str2, oPAppModel);
                        mTmpFullScreenAppMap.remove(str2);
                        mTmp17819FullScreenAppMap.remove(str2);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFullScreenApp() {
        loadClassAppList(1);
        loadClassAppList(R$styleable.Constraint_layout_goneMarginRight);
        loadClassAppList(2);
        loadClassAppList(R$styleable.Constraint_layout_goneMarginStart);
        mFullScreenAppMap.clear();
        m17819FullScreenAppMap.clear();
        mFullScreenAppMap = new HashMap(mTmpFullScreenAppMap);
        m17819FullScreenAppMap = new HashMap(mTmp17819FullScreenAppMap);
        mTmpFullScreenAppMap.clear();
        mTmp17819FullScreenAppMap.clear();
    }

    private void loadOriginalSizeApp() {
        loadClassAppList(0);
        loadClassAppList(100);
        mOriginalSizeAppMap.clear();
        mOriginalSizeAppMap = new HashMap(mTmpOriginalSizeAppMap);
        mTmpOriginalSizeAppMap.clear();
    }

    public void setClassApp(int i, String str, int i2) {
        this.mAppOpsManager.setMode(1006, i, str, i2);
        removeTask(str);
        try {
            this.multiAppInfo = this.mPackageManager.getApplicationInfoAsUser(str, 1, 999);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        ApplicationInfo applicationInfo = this.multiAppInfo;
        if (applicationInfo != null) {
            this.mAppOpsManager.setMode(1006, applicationInfo.uid, str, i2);
        }
        try {
            OPAppModel oPAppModel = new OPAppModel(str, this.mPackageManager.getApplicationInfo(str, 0).loadLabel(this.mPackageManager).toString(), "", i, false);
            if (i2 != 1) {
                if (i2 != 101) {
                    if (i2 != 0) {
                        if (i2 != 100) {
                            if (!OPUtils.isSupportScreenCutting() || !(i2 == 102 || i2 == 2)) {
                                mFullScreenAppMap.remove(str);
                                mOriginalSizeAppMap.remove(str);
                                m17819FullScreenAppMap.remove(str);
                                return;
                            }
                            mFullScreenAppMap.remove(str);
                            mOriginalSizeAppMap.remove(str);
                            m17819FullScreenAppMap.put(str, oPAppModel);
                            return;
                        }
                    }
                    mOriginalSizeAppMap.put(str, oPAppModel);
                    mFullScreenAppMap.remove(str);
                    m17819FullScreenAppMap.remove(str);
                    return;
                }
            }
            mFullScreenAppMap.put(str, oPAppModel);
            mOriginalSizeAppMap.remove(str);
            m17819FullScreenAppMap.remove(str);
        } catch (PackageManager.NameNotFoundException e2) {
            Log.e("DisplaySizeAdaptiongeManager", e2.getMessage());
        }
    }

    public int getAppTypeValue(String str) {
        OPAppModel oPAppModel = mOriginalSizeAppMap.get(str);
        OPAppModel oPAppModel2 = mFullScreenAppMap.get(str);
        OPAppModel oPAppModel3 = m17819FullScreenAppMap.get(str);
        if (oPAppModel != null) {
            return 0;
        }
        if (oPAppModel2 == null && OPUtils.isSupportScreenCutting() && oPAppModel3 != null) {
            return 3;
        }
        return 1;
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
                        Log.w("OPNotchDisplayGuideActivity", "Failed to remove task=" + recentTaskInfo.persistentId, e2);
                    }
                }
            }
        }
    }
}
