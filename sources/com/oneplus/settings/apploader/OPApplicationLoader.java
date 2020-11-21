package com.oneplus.settings.apploader;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ShortcutInfo;
import android.content.pm.UserInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import com.android.settings.C0003R$array;
import com.oneplus.settings.OPOnlineConfigManager;
import com.oneplus.settings.better.OPAppModel;
import com.oneplus.settings.gestures.OPGestureUtils;
import com.oneplus.settings.highpowerapp.PackageUtils;
import com.oneplus.settings.utils.OPUtils;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OPApplicationLoader {
    public static final Comparator<OPAppModel> ALPHA_COMPARATOR = new Comparator<OPAppModel>() {
        /* class com.oneplus.settings.apploader.OPApplicationLoader.AnonymousClass6 */
        protected final Collator sCollator = Collator.getInstance();

        public int compare(OPAppModel oPAppModel, OPAppModel oPAppModel2) {
            int compare = this.sCollator.compare(oPAppModel.getLabel(), oPAppModel2.getLabel());
            if (compare != 0) {
                return compare;
            }
            int compare2 = this.sCollator.compare(oPAppModel.getPkgName(), oPAppModel2.getPkgName());
            if (compare2 != 0) {
                return compare2;
            }
            return oPAppModel.getUid() - oPAppModel2.getUid();
        }
    };
    public static final Uri APP_CATEGORY_URI = Uri.parse("content://net.oneplus.provider.appcategoryprovider.AppCategoryContentProvider/app_category");
    public static ArrayList<String> mGameAppArrayList = new ArrayList<>();
    protected static ArrayList<String> mGameAppList = new ArrayList<>();
    protected List<OPAppModel> mAllAppList = new ArrayList();
    protected List<OPAppModel> mAllAppSelectedList = new ArrayList();
    protected List<OPAppModel> mAllAppSortBySelectedList = new ArrayList();
    protected List<OPAppModel> mAllAppUnSelectedList = new ArrayList();
    protected List<OPAppModel> mAllQuickLaunchAppList = new ArrayList();
    protected List<OPAppModel> mAllQuickLaunchShortcuts = new ArrayList();
    protected AppOpsManager mAppOpsManager;
    protected int mAppType;
    protected Context mContext;
    protected Handler mHandler1;
    protected boolean mHasShowProgress;
    protected List<OPAppModel> mIsGameUnSelectedAppList = new ArrayList();
    protected boolean mLoading;
    protected View mLoadingContainer;
    protected boolean mNeedLoadWorkProfileApps;
    protected PackageManager mPackageManager;
    protected List<UserHandle> mProfiles;
    protected List<OPAppModel> mSelectedAppList = new ArrayList();
    protected Map<String, String> mSelectedAppMap = new HashMap();
    protected Runnable mShowPromptRunnable;
    protected long mShowPromptTime;
    protected ExecutorService mThreadPool;
    protected List<OPAppModel> mUnSelectedAppList = new ArrayList();
    protected UserManager mUserManager;
    public final Comparator<ResolveInfo> resolveinfoAlphaComparator;

    /* access modifiers changed from: protected */
    public final void onPreExecute() {
        this.mHasShowProgress = false;
        AnonymousClass1 r0 = new Runnable() {
            /* class com.oneplus.settings.apploader.OPApplicationLoader.AnonymousClass1 */

            public void run() {
                OPApplicationLoader oPApplicationLoader = OPApplicationLoader.this;
                oPApplicationLoader.mHasShowProgress = true;
                View view = oPApplicationLoader.mLoadingContainer;
                if (view != null) {
                    view.setVisibility(0);
                }
                OPApplicationLoader.this.mShowPromptTime = System.currentTimeMillis();
            }
        };
        this.mShowPromptRunnable = r0;
        this.mHandler1.postDelayed(r0, 300);
    }

    /* access modifiers changed from: protected */
    public final void onPostExecute() {
        if (this.mHasShowProgress) {
            long currentTimeMillis = 500 - (System.currentTimeMillis() - this.mShowPromptTime);
            if (currentTimeMillis > 0) {
                this.mHandler1.postDelayed(new Runnable() {
                    /* class com.oneplus.settings.apploader.OPApplicationLoader.AnonymousClass2 */

                    public void run() {
                        View view = OPApplicationLoader.this.mLoadingContainer;
                        if (view != null) {
                            view.setVisibility(8);
                        }
                    }
                }, currentTimeMillis);
            } else {
                this.mHandler1.post(new Runnable() {
                    /* class com.oneplus.settings.apploader.OPApplicationLoader.AnonymousClass3 */

                    public void run() {
                        View view = OPApplicationLoader.this.mLoadingContainer;
                        if (view != null) {
                            view.setVisibility(8);
                        }
                    }
                });
            }
        } else {
            this.mHandler1.removeCallbacks(this.mShowPromptRunnable);
        }
    }

    public OPApplicationLoader(Context context, PackageManager packageManager) {
        new HashMap();
        this.mThreadPool = Executors.newCachedThreadPool();
        this.mLoading = false;
        this.mNeedLoadWorkProfileApps = true;
        this.mHandler1 = new Handler(Looper.getMainLooper());
        this.resolveinfoAlphaComparator = new Comparator<ResolveInfo>() {
            /* class com.oneplus.settings.apploader.OPApplicationLoader.AnonymousClass5 */
            protected final Collator sCollator = Collator.getInstance();

            public int compare(ResolveInfo resolveInfo, ResolveInfo resolveInfo2) {
                int compare = this.sCollator.compare(resolveInfo.loadLabel(OPApplicationLoader.this.mPackageManager), resolveInfo2.loadLabel(OPApplicationLoader.this.mPackageManager));
                if (compare != 0) {
                    return compare;
                }
                int compare2 = this.sCollator.compare(resolveInfo.activityInfo.packageName, resolveInfo2.activityInfo.packageName);
                if (compare2 != 0) {
                    return compare2;
                }
                int i = resolveInfo.activityInfo.applicationInfo.uid;
                return i - i;
            }
        };
        this.mContext = context;
        this.mPackageManager = packageManager;
        UserManager userManager = UserManager.get(context);
        this.mUserManager = userManager;
        this.mProfiles = userManager.getUserProfiles();
    }

    public OPApplicationLoader(Context context, AppOpsManager appOpsManager, PackageManager packageManager) {
        new HashMap();
        this.mThreadPool = Executors.newCachedThreadPool();
        this.mLoading = false;
        this.mNeedLoadWorkProfileApps = true;
        this.mHandler1 = new Handler(Looper.getMainLooper());
        this.resolveinfoAlphaComparator = new Comparator<ResolveInfo>() {
            /* class com.oneplus.settings.apploader.OPApplicationLoader.AnonymousClass5 */
            protected final Collator sCollator = Collator.getInstance();

            public int compare(ResolveInfo resolveInfo, ResolveInfo resolveInfo2) {
                int compare = this.sCollator.compare(resolveInfo.loadLabel(OPApplicationLoader.this.mPackageManager), resolveInfo2.loadLabel(OPApplicationLoader.this.mPackageManager));
                if (compare != 0) {
                    return compare;
                }
                int compare2 = this.sCollator.compare(resolveInfo.activityInfo.packageName, resolveInfo2.activityInfo.packageName);
                if (compare2 != 0) {
                    return compare2;
                }
                int i = resolveInfo.activityInfo.applicationInfo.uid;
                return i - i;
            }
        };
        this.mContext = context;
        this.mAppOpsManager = appOpsManager;
        this.mPackageManager = packageManager;
        UserManager userManager = UserManager.get(context);
        this.mUserManager = userManager;
        this.mProfiles = userManager.getUserProfiles();
    }

    public void setNeedLoadWorkProfileApps(boolean z) {
        this.mNeedLoadWorkProfileApps = z;
    }

    public void setmLoadingContainer(View view) {
        this.mLoadingContainer = view;
    }

    public void setAppType(int i) {
        this.mAppType = i;
    }

    public void initData(final int i, final Handler handler) {
        this.mThreadPool.execute(new Runnable() {
            /* class com.oneplus.settings.apploader.OPApplicationLoader.AnonymousClass4 */

            public void run() {
                OPApplicationLoader.this.onPreExecute();
                OPApplicationLoader oPApplicationLoader = OPApplicationLoader.this;
                oPApplicationLoader.mLoading = true;
                if (oPApplicationLoader.mAppType == 1004) {
                    oPApplicationLoader.loadGameApp();
                }
                OPApplicationLoader.this.loadAppListByType(i);
                OPApplicationLoader oPApplicationLoader2 = OPApplicationLoader.this;
                oPApplicationLoader2.mLoading = false;
                oPApplicationLoader2.onPostExecute();
                handler.sendEmptyMessage(i);
            }
        });
    }

    public boolean isLoading() {
        return this.mLoading;
    }

    public Map<String, String> loadSelectedGameOrReadAppMap(int i) {
        List<AppOpsManager.PackageOps> packagesForOps = this.mAppOpsManager.getPackagesForOps(new int[]{i});
        Map<String, String> map = this.mSelectedAppMap;
        if (map != null) {
            map.clear();
        }
        if (packagesForOps != null) {
            for (AppOpsManager.PackageOps packageOps : packagesForOps) {
                int userId = UserHandle.getUserId(packageOps.getUid());
                int uid = packageOps.getUid();
                if (isThisUserAProfileOfCurrentUser(userId)) {
                    if (i == 1003) {
                        for (AppOpsManager.OpEntry opEntry : packageOps.getOps()) {
                            if (opEntry.getOp() == i && (opEntry.getMode() == 0 || opEntry.getMode() == 2)) {
                                Map<String, String> map2 = this.mSelectedAppMap;
                                map2.put(uid + packageOps.getPackageName(), packageOps.getPackageName());
                            }
                        }
                    } else {
                        for (AppOpsManager.OpEntry opEntry2 : packageOps.getOps()) {
                            if (opEntry2.getOp() == i && opEntry2.getMode() == 0) {
                                Map<String, String> map3 = this.mSelectedAppMap;
                                map3.put(uid + packageOps.getPackageName(), packageOps.getPackageName());
                            }
                        }
                    }
                }
            }
        }
        return this.mSelectedAppMap;
    }

    /* access modifiers changed from: protected */
    public boolean isThisUserAProfileOfCurrentUser(int i) {
        int size = this.mProfiles.size();
        for (int i2 = 0; i2 < size; i2++) {
            if (this.mProfiles.get(i2).getIdentifier() == i) {
                return true;
            }
        }
        return false;
    }

    public void loadAppListByType(int i) {
        if (i == 0) {
            loadAllAppList();
        } else if (i == 1) {
            loadSelectedAppList();
        } else if (i == 2) {
            loadUnSelectedAppList();
        } else if (i == 3) {
            loadAllAppListSortBySelected(true);
        } else if (i == 4) {
            loadAllQuickLaunchAppList();
        } else if (i == 5) {
            loadAllQuickLaunchShortcuts();
        }
    }

    public List<OPAppModel> getAppListByType(int i) {
        if (i == 0) {
            return this.mAllAppList;
        }
        if (i == 1) {
            return this.mSelectedAppList;
        }
        if (i == 2) {
            return this.mUnSelectedAppList;
        }
        if (i == 5) {
            return this.mAllQuickLaunchShortcuts;
        }
        if (i == 4) {
            return this.mAllQuickLaunchAppList;
        }
        return this.mAllAppSortBySelectedList;
    }

    public List<OPAppModel> getAllAppList() {
        return this.mAllAppList;
    }

    /* access modifiers changed from: protected */
    public Drawable getBadgedIcon(PackageManager packageManager, ResolveInfo resolveInfo) {
        ApplicationInfo applicationInfo = resolveInfo.activityInfo.applicationInfo;
        return packageManager.getUserBadgedIcon(packageManager.loadUnbadgedItemIcon(applicationInfo, applicationInfo), new UserHandle(UserHandle.getUserId(applicationInfo.uid)));
    }

    public void loadAllAppListSortBySelected(boolean z) {
        try {
            this.mAllAppSortBySelectedList.clear();
            this.mAllAppSelectedList.clear();
            this.mAllAppUnSelectedList.clear();
            Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
            intent.addCategory("android.intent.category.LAUNCHER");
            List<ResolveInfo> queryIntentActivities = this.mPackageManager.queryIntentActivities(intent, 0);
            if (!queryIntentActivities.isEmpty()) {
                for (ResolveInfo resolveInfo : queryIntentActivities) {
                    String str = resolveInfo.activityInfo.name;
                    String str2 = resolveInfo.activityInfo.packageName;
                    String str3 = (String) resolveInfo.loadLabel(this.mPackageManager);
                    if (!z || !PackageUtils.isSystemApplication(this.mContext, str2)) {
                        if (multiAppPackageExcludeFilter(this.mContext, str2)) {
                            int i = resolveInfo.activityInfo.applicationInfo.uid;
                            Map<String, String> map = this.mSelectedAppMap;
                            StringBuilder sb = new StringBuilder();
                            sb.append(i);
                            sb.append(str2);
                            boolean z2 = map.containsKey(sb.toString()) && this.mSelectedAppMap.containsValue(str2);
                            OPAppModel oPAppModel = new OPAppModel(str2, str3, "", i, z2);
                            oPAppModel.setAppIcon(getBadgedIcon(this.mPackageManager, resolveInfo));
                            if (z2) {
                                this.mAllAppSelectedList.add(oPAppModel);
                            } else {
                                this.mAllAppUnSelectedList.add(oPAppModel);
                            }
                        }
                    }
                }
                Collections.sort(this.mAllAppSelectedList, ALPHA_COMPARATOR);
                Collections.sort(this.mAllAppUnSelectedList, ALPHA_COMPARATOR);
                this.mAllAppSortBySelectedList.addAll(this.mAllAppSelectedList);
                this.mAllAppSortBySelectedList.addAll(this.mAllAppUnSelectedList);
            }
        } catch (Exception e) {
            Log.e("AppLockerDataController", "some unknown error happened.");
            e.printStackTrace();
        }
    }

    private ResolveInfo getResolveInfoByPackageName(Context context, String str) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
        intent.setPackage(str);
        List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 0);
        if (queryIntentActivities == null || queryIntentActivities.size() <= 0) {
            return null;
        }
        return queryIntentActivities.get(0);
    }

    public void loadAllAppList() {
        List<ResolveInfo> list;
        boolean z;
        ResolveInfo resolveInfoByPackageName;
        try {
            this.mAllAppList.clear();
            Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
            intent.addCategory("android.intent.category.LAUNCHER");
            if (this.mNeedLoadWorkProfileApps) {
                list = new ArrayList<>();
                for (UserInfo userInfo : this.mUserManager.getProfiles(UserHandle.myUserId())) {
                    list.addAll(this.mPackageManager.queryIntentActivitiesAsUser(intent, 0, userInfo.id));
                }
            } else {
                list = this.mPackageManager.queryIntentActivities(intent, 0);
            }
            if (this.mAppType == 100 && OPUtils.isAppExist(this.mContext, "com.oneplus.opbackup") && (resolveInfoByPackageName = getResolveInfoByPackageName(this.mContext, "com.oneplus.opbackup")) != null) {
                list.add(resolveInfoByPackageName);
            }
            if (!list.isEmpty()) {
                for (ResolveInfo resolveInfo : list) {
                    String str = resolveInfo.activityInfo.name;
                    String str2 = resolveInfo.activityInfo.packageName;
                    String str3 = (String) resolveInfo.loadLabel(this.mPackageManager);
                    if (this.mAppType == 80 || !"com.oneplus.camera".equals(str2)) {
                        if (this.mAppType == 80) {
                            String[] stringArray = this.mContext.getResources().getStringArray(C0003R$array.op_car_mode_recommended_apps);
                            int i = 0;
                            while (true) {
                                if (i >= stringArray.length) {
                                    z = false;
                                    break;
                                } else if (stringArray[i].equals(str2)) {
                                    z = true;
                                    break;
                                } else {
                                    i++;
                                }
                            }
                            if (z) {
                            }
                        }
                        if (this.mAppType != 100 || slaDownloadWhiteListFilter(this.mContext, str2)) {
                            OPAppModel oPAppModel = new OPAppModel(str2, str3, "", resolveInfo.activityInfo.applicationInfo.uid, false);
                            oPAppModel.setAppIcon(getBadgedIcon(this.mPackageManager, resolveInfo));
                            if (this.mAppType == 100 && OPUtils.isInSlaDownLoadOpenAppsListString(this.mContext, oPAppModel)) {
                                oPAppModel.setSelected(true);
                            }
                            this.mAllAppList.add(oPAppModel);
                        }
                    }
                }
                Collections.sort(this.mAllAppList, ALPHA_COMPARATOR);
            }
        } catch (Exception e) {
            Log.e("AppLockerDataController", "some unknown error happened.");
            e.printStackTrace();
        }
    }

    public void loadAllQuickLaunchAppList() {
        List<ResolveInfo> list;
        try {
            this.mAllQuickLaunchAppList.clear();
            Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
            intent.addCategory("android.intent.category.LAUNCHER");
            if (this.mNeedLoadWorkProfileApps) {
                list = new ArrayList<>();
                for (UserInfo userInfo : this.mUserManager.getProfiles(UserHandle.myUserId())) {
                    list.addAll(this.mPackageManager.queryIntentActivitiesAsUser(intent, 0, userInfo.id));
                }
            } else {
                list = this.mPackageManager.queryIntentActivities(intent, 0);
            }
            if (!list.isEmpty()) {
                for (ResolveInfo resolveInfo : list) {
                    String str = resolveInfo.activityInfo.name;
                    OPAppModel oPAppModel = new OPAppModel(resolveInfo.activityInfo.packageName, (String) resolveInfo.loadLabel(this.mPackageManager), "", resolveInfo.activityInfo.applicationInfo.uid, false);
                    oPAppModel.setType(0);
                    oPAppModel.setSelected(OPUtils.isInQuickLaunchList(this.mContext, oPAppModel));
                    oPAppModel.setAppIcon(getBadgedIcon(this.mPackageManager, resolveInfo));
                    this.mAllQuickLaunchAppList.add(oPAppModel);
                }
                Collections.sort(this.mAllQuickLaunchAppList, ALPHA_COMPARATOR);
            }
        } catch (Exception e) {
            Log.e("AppLockerDataController", "some unknown error happened.");
            e.printStackTrace();
        }
    }

    public void loadAllQuickLaunchShortcuts() {
        List<ResolveInfo> list;
        try {
            this.mAllQuickLaunchShortcuts.clear();
            Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
            intent.addCategory("android.intent.category.LAUNCHER");
            if (this.mNeedLoadWorkProfileApps) {
                list = new ArrayList<>();
                for (UserInfo userInfo : this.mUserManager.getProfiles(UserHandle.myUserId())) {
                    list.addAll(this.mPackageManager.queryIntentActivitiesAsUser(intent, 0, userInfo.id));
                }
            } else {
                list = this.mPackageManager.queryIntentActivities(intent, 0);
            }
            if (!list.isEmpty()) {
                Collections.sort(list, this.resolveinfoAlphaComparator);
                for (ResolveInfo resolveInfo : list) {
                    String str = resolveInfo.activityInfo.name;
                    String str2 = resolveInfo.activityInfo.packageName;
                    String str3 = (String) resolveInfo.loadLabel(this.mPackageManager);
                    if (OPGestureUtils.hasShortCuts(this.mContext, str2)) {
                        loadShortcutByPackageName(str2, resolveInfo.activityInfo.applicationInfo.uid);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("AppLockerDataController", "some unknown error happened.");
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: protected */
    public void loadShortcutByPackageName(String str, int i) {
        List<ShortcutInfo> loadShortCuts = OPGestureUtils.loadShortCuts(this.mContext, str);
        if (loadShortCuts != null) {
            int size = loadShortCuts.size();
            LauncherApps launcherApps = (LauncherApps) this.mContext.getSystemService("launcherapps");
            for (int i2 = 0; i2 < size; i2++) {
                ShortcutInfo shortcutInfo = loadShortCuts.get(i2);
                CharSequence longLabel = shortcutInfo.getLongLabel();
                if (TextUtils.isEmpty(longLabel)) {
                    longLabel = shortcutInfo.getShortLabel();
                }
                if (TextUtils.isEmpty(longLabel)) {
                    longLabel = shortcutInfo.getId();
                }
                if (!"com.eg.android.AlipayGphone".equals(str) || (!"1001".equals(shortcutInfo.getId()) && !"1002".equals(shortcutInfo.getId()))) {
                    OPAppModel oPAppModel = new OPAppModel(shortcutInfo.getPackage(), longLabel.toString(), shortcutInfo.getId(), i, false);
                    oPAppModel.setAppLabel(OPUtils.getAppLabel(this.mContext, shortcutInfo.getPackage()));
                    oPAppModel.setType(1);
                    oPAppModel.setSelected(OPUtils.isInQuickLaunchList(this.mContext, oPAppModel));
                    oPAppModel.setAppIcon(OPUtils.getAppIcon(this.mContext, str));
                    try {
                        oPAppModel.setShortCutIcon(launcherApps.getShortcutIconDrawable(shortcutInfo, 0));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    this.mAllQuickLaunchShortcuts.add(oPAppModel);
                }
            }
        }
    }

    public void loadSelectedAppList() {
        List<ResolveInfo> list;
        try {
            this.mSelectedAppList.clear();
            Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
            intent.addCategory("android.intent.category.LAUNCHER");
            if (this.mNeedLoadWorkProfileApps) {
                list = new ArrayList<>();
                for (UserInfo userInfo : this.mUserManager.getProfiles(UserHandle.myUserId())) {
                    list.addAll(this.mPackageManager.queryIntentActivitiesAsUser(intent, 0, userInfo.id));
                }
            } else {
                list = this.mPackageManager.queryIntentActivities(intent, 0);
            }
            if (!list.isEmpty()) {
                for (ResolveInfo resolveInfo : list) {
                    String str = resolveInfo.activityInfo.name;
                    String str2 = resolveInfo.activityInfo.packageName;
                    String str3 = (String) resolveInfo.loadLabel(this.mPackageManager);
                    if (!packageExcludeFilter(str2)) {
                        int i = resolveInfo.activityInfo.applicationInfo.uid;
                        Map<String, String> map = this.mSelectedAppMap;
                        StringBuilder sb = new StringBuilder();
                        sb.append(i);
                        sb.append(str2);
                        boolean z = map.containsKey(sb.toString()) && this.mSelectedAppMap.containsValue(str2);
                        if (z) {
                            OPAppModel oPAppModel = new OPAppModel(str2, str3, "", i, z);
                            oPAppModel.setAppIcon(getBadgedIcon(this.mPackageManager, resolveInfo));
                            this.mSelectedAppList.add(oPAppModel);
                        }
                    }
                }
                Collections.sort(this.mSelectedAppList, ALPHA_COMPARATOR);
            }
        } catch (Exception e) {
            Log.e("AppLockerDataController", "some unknown error happened.");
            e.printStackTrace();
        }
    }

    public void loadUnSelectedAppList() {
        List<ResolveInfo> list;
        try {
            this.mUnSelectedAppList.clear();
            Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
            intent.addCategory("android.intent.category.LAUNCHER");
            if (this.mNeedLoadWorkProfileApps) {
                list = new ArrayList<>();
                for (UserInfo userInfo : this.mUserManager.getProfiles(UserHandle.myUserId())) {
                    list.addAll(this.mPackageManager.queryIntentActivitiesAsUser(intent, 0, userInfo.id));
                }
            } else {
                list = this.mPackageManager.queryIntentActivities(intent, 0);
            }
            if (!list.isEmpty()) {
                for (ResolveInfo resolveInfo : list) {
                    String str = resolveInfo.activityInfo.name;
                    String str2 = resolveInfo.activityInfo.packageName;
                    String str3 = (String) resolveInfo.loadLabel(this.mPackageManager);
                    if (!packageExcludeFilter(str2)) {
                        int i = resolveInfo.activityInfo.applicationInfo.uid;
                        Map<String, String> map = this.mSelectedAppMap;
                        StringBuilder sb = new StringBuilder();
                        sb.append(i);
                        sb.append(str2);
                        boolean z = map.containsKey(sb.toString()) && this.mSelectedAppMap.containsValue(str2);
                        if (!z) {
                            OPAppModel oPAppModel = new OPAppModel(str2, str3, "", i, z);
                            oPAppModel.setAppIcon(getBadgedIcon(this.mPackageManager, resolveInfo));
                            if (this.mAppType != 1004) {
                                oPAppModel.setGameAPP(false);
                                this.mUnSelectedAppList.add(oPAppModel);
                            } else if (mGameAppList.contains(str2)) {
                                oPAppModel.setGameAPP(true);
                                this.mIsGameUnSelectedAppList.add(oPAppModel);
                            } else {
                                oPAppModel.setGameAPP(false);
                                this.mUnSelectedAppList.add(oPAppModel);
                            }
                        }
                    }
                }
                if (this.mAppType == 1004) {
                    Collections.sort(this.mIsGameUnSelectedAppList, ALPHA_COMPARATOR);
                    Collections.sort(this.mUnSelectedAppList, ALPHA_COMPARATOR);
                    this.mIsGameUnSelectedAppList.addAll(this.mUnSelectedAppList);
                    this.mUnSelectedAppList = this.mIsGameUnSelectedAppList;
                    return;
                }
                Collections.sort(this.mUnSelectedAppList, ALPHA_COMPARATOR);
            }
        } catch (Exception e) {
            Log.e("AppLockerDataController", "some unknown error happened.");
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0037, code lost:
        if (r6 == null) goto L_0x0059;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0039, code lost:
        r6.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0056, code lost:
        if (0 == 0) goto L_0x0059;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0059, code lost:
        com.oneplus.settings.apploader.OPApplicationLoader.mGameAppList = com.oneplus.settings.apploader.OPApplicationLoader.mGameAppArrayList;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadGameApp() {
        /*
        // Method dump skipped, instructions count: 101
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oneplus.settings.apploader.OPApplicationLoader.loadGameApp():void");
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x00ce, code lost:
        if ("com.amazon.avod.thirdpartyclient".equals(r8) == false) goto L_0x00fc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x00f9, code lost:
        if ("com.oneplus.gamespace".equals(r8) == false) goto L_0x00fc;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean packageExcludeFilter(java.lang.String r8) {
        /*
        // Method dump skipped, instructions count: 254
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oneplus.settings.apploader.OPApplicationLoader.packageExcludeFilter(java.lang.String):boolean");
    }

    /* access modifiers changed from: protected */
    public boolean multiAppPackageExcludeFilter(Context context, String str) {
        return OPOnlineConfigManager.getMultiAppWhiteList().contains(str);
    }

    /* access modifiers changed from: protected */
    public boolean slaDownloadWhiteListFilter(Context context, String str) {
        return OPOnlineConfigManager.getSlaDownloadWhiteList().contains(str);
    }
}
