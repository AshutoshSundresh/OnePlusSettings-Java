package com.android.settingslib.users;

import android.app.AppGlobals;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AppRestrictionsHelper {
    private final Context mContext;
    private final IPackageManager mIPm;
    private final Injector mInjector;
    private boolean mLeanback;
    private final PackageManager mPackageManager;
    private final boolean mRestrictedProfile;
    HashMap<String, Boolean> mSelectedPackages;
    private final UserHandle mUser;
    private final UserManager mUserManager;
    private List<SelectableAppInfo> mVisibleApps;

    public interface OnDisableUiForPackageListener {
        void onDisableUiForPackage(String str);
    }

    public AppRestrictionsHelper(Context context, UserHandle userHandle) {
        this(new Injector(context, userHandle));
    }

    AppRestrictionsHelper(Injector injector) {
        this.mSelectedPackages = new HashMap<>();
        this.mInjector = injector;
        this.mContext = injector.getContext();
        this.mPackageManager = this.mInjector.getPackageManager();
        this.mIPm = this.mInjector.getIPackageManager();
        this.mUser = this.mInjector.getUser();
        UserManager userManager = this.mInjector.getUserManager();
        this.mUserManager = userManager;
        this.mRestrictedProfile = userManager.getUserInfo(this.mUser.getIdentifier()).isRestricted();
    }

    public void setPackageSelected(String str, boolean z) {
        this.mSelectedPackages.put(str, Boolean.valueOf(z));
    }

    public boolean isPackageSelected(String str) {
        return this.mSelectedPackages.get(str).booleanValue();
    }

    public List<SelectableAppInfo> getVisibleApps() {
        return this.mVisibleApps;
    }

    public void applyUserAppsStates(OnDisableUiForPackageListener onDisableUiForPackageListener) {
        if (this.mRestrictedProfile || this.mUser.getIdentifier() == UserHandle.myUserId()) {
            for (Map.Entry<String, Boolean> entry : this.mSelectedPackages.entrySet()) {
                applyUserAppState(entry.getKey(), entry.getValue().booleanValue(), onDisableUiForPackageListener);
            }
            return;
        }
        Log.e("AppRestrictionsHelper", "Cannot apply application restrictions on another user!");
    }

    public void applyUserAppState(String str, boolean z, OnDisableUiForPackageListener onDisableUiForPackageListener) {
        int identifier = this.mUser.getIdentifier();
        if (z) {
            try {
                ApplicationInfo applicationInfo = this.mIPm.getApplicationInfo(str, 4194304, identifier);
                if (applicationInfo == null || !applicationInfo.enabled || (applicationInfo.flags & 8388608) == 0) {
                    this.mIPm.installExistingPackageAsUser(str, this.mUser.getIdentifier(), 4194304, 0, (List) null);
                }
                if (applicationInfo != null && (1 & applicationInfo.privateFlags) != 0 && (applicationInfo.flags & 8388608) != 0) {
                    onDisableUiForPackageListener.onDisableUiForPackage(str);
                    this.mIPm.setApplicationHiddenSettingAsUser(str, false, identifier);
                }
            } catch (RemoteException unused) {
            }
        } else if (this.mIPm.getApplicationInfo(str, 0, identifier) == null) {
        } else {
            if (this.mRestrictedProfile) {
                this.mPackageManager.deletePackageAsUser(str, null, 4, this.mUser.getIdentifier());
                return;
            }
            onDisableUiForPackageListener.onDisableUiForPackage(str);
            this.mIPm.setApplicationHiddenSettingAsUser(str, true, identifier);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:31:0x00b1  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x0106  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0159  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void fetchAndMergeApps() {
        /*
        // Method dump skipped, instructions count: 377
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.users.AppRestrictionsHelper.fetchAndMergeApps():void");
    }

    private void addSystemImes(Set<String> set) {
        for (InputMethodInfo inputMethodInfo : this.mInjector.getInputMethodList()) {
            try {
                if (inputMethodInfo.isDefault(this.mContext) && isSystemPackage(inputMethodInfo.getPackageName())) {
                    set.add(inputMethodInfo.getPackageName());
                }
            } catch (Resources.NotFoundException unused) {
            }
        }
    }

    private void addSystemApps(List<SelectableAppInfo> list, Intent intent, Set<String> set) {
        ApplicationInfo applicationInfo;
        ApplicationInfo appInfoForUser;
        PackageManager packageManager = this.mPackageManager;
        for (ResolveInfo resolveInfo : packageManager.queryIntentActivities(intent, 8704)) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            if (!(activityInfo == null || (applicationInfo = activityInfo.applicationInfo) == null)) {
                String str = activityInfo.packageName;
                int i = applicationInfo.flags;
                if (!((i & 1) == 0 && (i & 128) == 0) && !set.contains(str)) {
                    int applicationEnabledSetting = packageManager.getApplicationEnabledSetting(str);
                    if (!((applicationEnabledSetting == 4 || applicationEnabledSetting == 2) && ((appInfoForUser = getAppInfoForUser(str, 0, this.mUser)) == null || (appInfoForUser.flags & 8388608) == 0))) {
                        SelectableAppInfo selectableAppInfo = new SelectableAppInfo();
                        ActivityInfo activityInfo2 = resolveInfo.activityInfo;
                        selectableAppInfo.packageName = activityInfo2.packageName;
                        selectableAppInfo.appName = activityInfo2.applicationInfo.loadLabel(packageManager);
                        selectableAppInfo.icon = resolveInfo.activityInfo.loadIcon(packageManager);
                        CharSequence loadLabel = resolveInfo.activityInfo.loadLabel(packageManager);
                        selectableAppInfo.activityName = loadLabel;
                        if (loadLabel == null) {
                            selectableAppInfo.activityName = selectableAppInfo.appName;
                        }
                        list.add(selectableAppInfo);
                    }
                }
            }
        }
    }

    private boolean isSystemPackage(String str) {
        try {
            PackageInfo packageInfo = this.mPackageManager.getPackageInfo(str, 0);
            if (packageInfo.applicationInfo == null) {
                return false;
            }
            int i = packageInfo.applicationInfo.flags;
            if ((i & 1) == 0 && (i & 128) == 0) {
                return false;
            }
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
        }
    }

    private ApplicationInfo getAppInfoForUser(String str, int i, UserHandle userHandle) {
        try {
            return this.mIPm.getApplicationInfo(str, i, userHandle.getIdentifier());
        } catch (RemoteException unused) {
            return null;
        }
    }

    public static class SelectableAppInfo {
        public CharSequence activityName;
        public CharSequence appName;
        public Drawable icon;
        public SelectableAppInfo masterEntry;
        public String packageName;

        public String toString() {
            return this.packageName + ": appName=" + ((Object) this.appName) + "; activityName=" + ((Object) this.activityName) + "; icon=" + this.icon + "; masterEntry=" + this.masterEntry;
        }
    }

    private static class AppLabelComparator implements Comparator<SelectableAppInfo> {
        private AppLabelComparator() {
        }

        public int compare(SelectableAppInfo selectableAppInfo, SelectableAppInfo selectableAppInfo2) {
            return selectableAppInfo.activityName.toString().toLowerCase().compareTo(selectableAppInfo2.activityName.toString().toLowerCase());
        }
    }

    /* access modifiers changed from: package-private */
    public static class Injector {
        private Context mContext;
        private UserHandle mUser;

        Injector(Context context, UserHandle userHandle) {
            this.mContext = context;
            this.mUser = userHandle;
        }

        /* access modifiers changed from: package-private */
        public Context getContext() {
            return this.mContext;
        }

        /* access modifiers changed from: package-private */
        public UserHandle getUser() {
            return this.mUser;
        }

        /* access modifiers changed from: package-private */
        public PackageManager getPackageManager() {
            return this.mContext.getPackageManager();
        }

        /* access modifiers changed from: package-private */
        public IPackageManager getIPackageManager() {
            return AppGlobals.getPackageManager();
        }

        /* access modifiers changed from: package-private */
        public UserManager getUserManager() {
            return (UserManager) this.mContext.getSystemService(UserManager.class);
        }

        /* access modifiers changed from: package-private */
        public List<InputMethodInfo> getInputMethodList() {
            return ((InputMethodManager) getContext().getSystemService("input_method")).getInputMethodListAsUser(this.mUser.getIdentifier());
        }
    }
}
