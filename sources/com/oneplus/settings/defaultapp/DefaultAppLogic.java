package com.oneplus.settings.defaultapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import com.oneplus.settings.defaultapp.apptype.DefaultAppTypeInfo;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class DefaultAppLogic {
    private static final byte[] INIT_LOCK = new byte[0];
    private static volatile DefaultAppLogic instance;
    private final Context mContext;
    private Map<String, List<String>> mExcludedMap;
    private String[] mKeys = DefaultAppConstants.DEFAULTAPP_VALUE_LIST_KEY;
    private String[] mValues = DefaultAppUtils.getDefaultAppValueList();

    private DefaultAppLogic(Context context) {
        this.mContext = context;
        initExcludedMap();
    }

    public static DefaultAppLogic getInstance(Context context) {
        if (instance == null) {
            synchronized (INIT_LOCK) {
                if (instance == null) {
                    instance = new DefaultAppLogic(context);
                }
            }
        }
        return instance;
    }

    private void initExcludedMap() {
        this.mExcludedMap = new HashMap();
        ArrayList arrayList = new ArrayList();
        arrayList.add("com.android.documentsui");
        this.mExcludedMap.put(this.mKeys[1], arrayList);
    }

    public void initDefaultAppSettings() {
        initDefaultAppSettings(false);
    }

    public void initDefaultAppSettings(boolean z) {
        if (z || !DataHelper.isDefaultAppInited(this.mContext)) {
            int i = 0;
            while (true) {
                String[] strArr = this.mKeys;
                if (i < strArr.length) {
                    String str = strArr[i];
                    String pmDefaultAppPackageName = getPmDefaultAppPackageName(str);
                    if (pmDefaultAppPackageName == null || "android".equals(pmDefaultAppPackageName)) {
                        DefaultAppUtils.resetDefaultApp(this.mContext, this.mKeys[i]);
                    } else {
                        List<DefaultAppActivityInfo> appInfoList = getAppInfoList(str);
                        List<String> appPackageNameList = getAppPackageNameList(str, appInfoList);
                        setDefaultAppPosition(str, appInfoList, appPackageNameList, getDefaultAppPosition(appPackageNameList, pmDefaultAppPackageName));
                    }
                    i++;
                } else {
                    DataHelper.setDefaultAppInited(this.mContext);
                    return;
                }
            }
        }
    }

    private boolean isAppExist(String str, List<String> list, String str2) {
        if (TextUtils.isEmpty(str2)) {
            return false;
        }
        List<String> list2 = this.mExcludedMap.get(str);
        if (list2 != null && list2.contains(str2)) {
            return true;
        }
        for (int i = 0; i < list.size(); i++) {
            if (str2.equals(list.get(i))) {
                return true;
            }
        }
        return false;
    }

    public List<DefaultAppActivityInfo> getAppInfoList(String str) {
        ArrayList arrayList = new ArrayList();
        List<Intent> appIntent = DefaultAppUtils.create(this.mContext, str).getAppIntent();
        PackageManager packageManager = this.mContext.getPackageManager();
        for (Intent intent : appIntent) {
            DefaultAppActivityInfo defaultAppActivityInfo = new DefaultAppActivityInfo();
            for (ResolveInfo resolveInfo : packageManager.queryIntentActivities(intent, 131072)) {
                defaultAppActivityInfo.addActivityInfo(resolveInfo.activityInfo);
            }
            arrayList.add(defaultAppActivityInfo);
        }
        return arrayList;
    }

    public List<String> getAppPackageNameList(String str, List<DefaultAppActivityInfo> list) {
        ArrayList arrayList = new ArrayList();
        for (DefaultAppActivityInfo defaultAppActivityInfo : list) {
            for (ActivityInfo activityInfo : defaultAppActivityInfo.getActivityInfo()) {
                if (!isAppExist(str, arrayList, activityInfo.packageName)) {
                    arrayList.add(activityInfo.packageName);
                }
            }
        }
        return arrayList;
    }

    private void updateRelatedDefaultApp(String str, String str2) {
        if (!TextUtils.isEmpty(str2)) {
            int i = 0;
            while (true) {
                String[] strArr = this.mKeys;
                if (i < strArr.length) {
                    if (!strArr[i].equals(str) && str2.equals(this.mValues[i])) {
                        resetDefaultApp(this.mKeys[i]);
                    }
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void resetDefaultApp(String str) {
        List<DefaultAppActivityInfo> appInfoList = getAppInfoList(str);
        List<String> appPackageNameList = getAppPackageNameList(str, appInfoList);
        setDefaultAppPosition(str, appInfoList, appPackageNameList, getSystemDefaultAppPosition(str, appPackageNameList));
    }

    public void setDefaultAppPosition(String str, List<DefaultAppActivityInfo> list, List<String> list2, String str2) {
        if (getDefaultAppPosition(str, list2) == -1) {
            for (String str3 : list2) {
                if (!str2.equals(str3)) {
                    setDefaultApp(str, list, list2, str3);
                }
            }
        }
        setDefaultApp(str, list, list2, str2);
    }

    public void setDefaultApp(String str, List<DefaultAppActivityInfo> list, List<String> list2, String str2) {
        PackageManager packageManager = this.mContext.getPackageManager();
        int defaultAppPosition = getDefaultAppPosition(str, list2);
        DataHelper.setDefaultAppPackageName(this.mContext, str, str2);
        if (defaultAppPosition != -1) {
            packageManager.clearPackagePreferredActivities(list2.get(defaultAppPosition));
            updateRelatedDefaultApp(str, list2.get(defaultAppPosition));
        }
        DefaultAppTypeInfo create = DefaultAppUtils.create(this.mContext, str);
        if (create != null) {
            List<IntentFilter> appFilter = create.getAppFilter();
            List<Integer> appMatchParam = create.getAppMatchParam();
            for (int i = 0; i < appFilter.size(); i++) {
                List<ActivityInfo> activityInfo = list.get(i).getActivityInfo();
                ComponentName[] componentNameArr = new ComponentName[activityInfo.size()];
                ComponentName componentName = null;
                for (int size = activityInfo.size() - 1; size >= 0; size--) {
                    ActivityInfo activityInfo2 = activityInfo.get(size);
                    String str3 = activityInfo2.packageName;
                    String str4 = activityInfo2.name;
                    componentNameArr[size] = new ComponentName(str3, str4);
                    if (!TextUtils.isEmpty(str3) && str3.equals(str2)) {
                        componentName = new ComponentName(str3, str4);
                    }
                }
                if (componentName != null) {
                    packageManager.addPreferredActivity(appFilter.get(i), appMatchParam.get(i).intValue(), componentNameArr, componentName);
                    if (str.equals("op_default_app_browser")) {
                        try {
                            Class<?> cls = Class.forName(UserHandle.class.getName());
                            invoke(packageManager, packageManager.getClass().getDeclaredMethod("setDefaultBrowserPackageName", String.class, Integer.TYPE), componentName.getPackageName(), Integer.valueOf(((Integer) cls.getMethod("getCallingUserId", new Class[0]).invoke(cls, new Object[0])).intValue()));
                        } catch (Exception e) {
                            Log.e("DefaultAppLogic", "setDefaultAppPosition: " + e);
                        }
                    }
                }
            }
        }
    }

    public void setDefaultAppPosition(String str, List<DefaultAppActivityInfo> list, List<String> list2, int i) {
        if (i >= 0 && i < list2.size()) {
            setDefaultAppPosition(str, list, list2, list2.get(i));
        }
    }

    public Object invoke(PackageManager packageManager, Method method, Object... objArr) {
        if (!(packageManager == null || method == null)) {
            try {
                return method.invoke(packageManager, objArr);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e2) {
                e2.printStackTrace();
            } catch (InvocationTargetException e3) {
                e3.printStackTrace();
            }
        }
        return null;
    }

    public int getDefaultAppPosition(String str, List<String> list, String str2) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(str2)) {
                return i;
            }
        }
        return -1;
    }

    public int getDefaultAppPosition(String str, List<String> list) {
        return getDefaultAppPosition(list, DataHelper.getDefaultAppPackageName(this.mContext, str));
    }

    public int getDefaultAppPosition(List<String> list, String str) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(str)) {
                return i;
            }
        }
        return -1;
    }

    public int getSystemDefaultAppPosition(String str, List<String> list) {
        String systemDefaultPackageName = DefaultAppUtils.getSystemDefaultPackageName(this.mContext, str);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(systemDefaultPackageName)) {
                return i;
            }
        }
        return -1;
    }

    public void resetAppByType(String str, String str2) {
        List<DefaultAppActivityInfo> appInfoList = getAppInfoList(str);
        setDefaultApp(str, appInfoList, getAppPackageNameList(str, appInfoList), str2);
    }

    public String getDefaultAppPackageName(String str) {
        return DefaultAppUtils.getDefaultAppPackageName(this.mContext, str);
    }

    public String getPmDefaultAppPackageName(String str) {
        String defaultAppPackageName = DataHelper.getDefaultAppPackageName(this.mContext, str);
        Log.d("DefaultAppLogic", "getDefaultAppPackageName appType:" + str + " pkg name = " + defaultAppPackageName);
        if (!TextUtils.isEmpty(defaultAppPackageName)) {
            return defaultAppPackageName;
        }
        List<Intent> appIntent = DefaultAppUtils.create(this.mContext, str).getAppIntent();
        PackageManager packageManager = this.mContext.getPackageManager();
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        for (Intent intent : appIntent) {
            ResolveInfo resolveActivity = packageManager.resolveActivity(intent, 65536);
            if (resolveActivity != null) {
                linkedHashSet.add(resolveActivity.activityInfo.packageName);
            }
        }
        List<String> list = this.mExcludedMap.get(str);
        linkedHashSet.remove("android");
        Iterator it = linkedHashSet.iterator();
        while (it.hasNext()) {
            String str2 = (String) it.next();
            if (list != null && list.contains(str2)) {
                it.remove();
            }
        }
        if (linkedHashSet.size() < 1) {
            Log.d("DefaultAppLogic", "getDefaultAppPackageName appType:" + str + " error defaultApp.size != 1");
            return null;
        }
        String str3 = (String) linkedHashSet.toArray()[0];
        Log.d("DefaultAppLogic", "getPmDefaultAppPackageName appType:" + str + ", defaultApp pkg:" + str3);
        return str3;
    }
}
