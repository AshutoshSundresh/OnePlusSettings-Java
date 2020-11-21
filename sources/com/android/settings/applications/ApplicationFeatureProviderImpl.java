package com.android.settings.applications;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.UserInfo;
import android.location.LocationManager;
import android.os.RemoteException;
import android.os.UserManager;
import android.telecom.DefaultDialerManager;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.telephony.SmsApplication;
import com.android.settings.C0017R$string;
import com.android.settings.applications.ApplicationFeatureProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ApplicationFeatureProviderImpl implements ApplicationFeatureProvider {
    protected final Context mContext;
    private final DevicePolicyManager mDpm;
    private final PackageManager mPm;
    private final IPackageManager mPms;
    private final UserManager mUm;

    public ApplicationFeatureProviderImpl(Context context, PackageManager packageManager, IPackageManager iPackageManager, DevicePolicyManager devicePolicyManager) {
        Context applicationContext = context.getApplicationContext();
        this.mContext = applicationContext;
        this.mPm = packageManager;
        this.mPms = iPackageManager;
        this.mDpm = devicePolicyManager;
        this.mUm = UserManager.get(applicationContext);
    }

    @Override // com.android.settings.applications.ApplicationFeatureProvider
    public void calculateNumberOfPolicyInstalledApps(boolean z, ApplicationFeatureProvider.NumberOfAppsCallback numberOfAppsCallback) {
        CurrentUserAndManagedProfilePolicyInstalledAppCounter currentUserAndManagedProfilePolicyInstalledAppCounter = new CurrentUserAndManagedProfilePolicyInstalledAppCounter(this.mContext, this.mPm, numberOfAppsCallback);
        if (z) {
            currentUserAndManagedProfilePolicyInstalledAppCounter.execute(new Void[0]);
        } else {
            currentUserAndManagedProfilePolicyInstalledAppCounter.executeInForeground();
        }
    }

    @Override // com.android.settings.applications.ApplicationFeatureProvider
    public void listPolicyInstalledApps(ApplicationFeatureProvider.ListOfAppsCallback listOfAppsCallback) {
        new CurrentUserPolicyInstalledAppLister(this.mPm, this.mUm, listOfAppsCallback).execute(new Void[0]);
    }

    @Override // com.android.settings.applications.ApplicationFeatureProvider
    public void calculateNumberOfAppsWithAdminGrantedPermissions(String[] strArr, boolean z, ApplicationFeatureProvider.NumberOfAppsCallback numberOfAppsCallback) {
        CurrentUserAndManagedProfileAppWithAdminGrantedPermissionsCounter currentUserAndManagedProfileAppWithAdminGrantedPermissionsCounter = new CurrentUserAndManagedProfileAppWithAdminGrantedPermissionsCounter(this.mContext, strArr, this.mPm, this.mPms, this.mDpm, numberOfAppsCallback);
        if (z) {
            currentUserAndManagedProfileAppWithAdminGrantedPermissionsCounter.execute(new Void[0]);
        } else {
            currentUserAndManagedProfileAppWithAdminGrantedPermissionsCounter.executeInForeground();
        }
    }

    @Override // com.android.settings.applications.ApplicationFeatureProvider
    public void listAppsWithAdminGrantedPermissions(String[] strArr, ApplicationFeatureProvider.ListOfAppsCallback listOfAppsCallback) {
        new CurrentUserAppWithAdminGrantedPermissionsLister(strArr, this.mPm, this.mPms, this.mDpm, this.mUm, listOfAppsCallback).execute(new Void[0]);
    }

    @Override // com.android.settings.applications.ApplicationFeatureProvider
    public List<UserAppInfo> findPersistentPreferredActivities(int i, Intent[] intentArr) {
        ArrayList arrayList = new ArrayList();
        ArraySet arraySet = new ArraySet();
        UserInfo userInfo = this.mUm.getUserInfo(i);
        for (Intent intent : intentArr) {
            try {
                ResolveInfo findPersistentPreferredActivity = this.mPms.findPersistentPreferredActivity(intent, i);
                if (findPersistentPreferredActivity != null) {
                    ComponentInfo componentInfo = null;
                    if (findPersistentPreferredActivity.activityInfo != null) {
                        componentInfo = findPersistentPreferredActivity.activityInfo;
                    } else if (findPersistentPreferredActivity.serviceInfo != null) {
                        componentInfo = findPersistentPreferredActivity.serviceInfo;
                    } else if (findPersistentPreferredActivity.providerInfo != null) {
                        componentInfo = findPersistentPreferredActivity.providerInfo;
                    }
                    if (componentInfo != null) {
                        UserAppInfo userAppInfo = new UserAppInfo(userInfo, componentInfo.applicationInfo);
                        if (arraySet.add(userAppInfo)) {
                            arrayList.add(userAppInfo);
                        }
                    }
                }
            } catch (RemoteException unused) {
            }
        }
        return arrayList;
    }

    @Override // com.android.settings.applications.ApplicationFeatureProvider
    public Set<String> getKeepEnabledPackages() {
        ArraySet arraySet = new ArraySet();
        String defaultDialerApplication = DefaultDialerManager.getDefaultDialerApplication(this.mContext);
        if (!TextUtils.isEmpty(defaultDialerApplication)) {
            arraySet.add(defaultDialerApplication);
        }
        ComponentName defaultSmsApplication = SmsApplication.getDefaultSmsApplication(this.mContext, true);
        if (defaultSmsApplication != null) {
            arraySet.add(defaultSmsApplication.getPackageName());
        }
        ComponentInfo findEuiccService = findEuiccService(this.mPm);
        if (findEuiccService != null) {
            arraySet.add(findEuiccService.packageName);
        }
        arraySet.addAll(getEnabledPackageWhitelist());
        String extraLocationControllerPackage = ((LocationManager) this.mContext.getSystemService("location")).getExtraLocationControllerPackage();
        if (extraLocationControllerPackage != null) {
            arraySet.add(extraLocationControllerPackage);
        }
        return arraySet;
    }

    private Set<String> getEnabledPackageWhitelist() {
        ArraySet arraySet = new ArraySet();
        arraySet.add(this.mContext.getString(C0017R$string.config_settingsintelligence_package_name));
        arraySet.add(this.mContext.getString(C0017R$string.config_package_installer_package_name));
        return arraySet;
    }

    private static class CurrentUserAndManagedProfilePolicyInstalledAppCounter extends InstalledAppCounter {
        private ApplicationFeatureProvider.NumberOfAppsCallback mCallback;

        CurrentUserAndManagedProfilePolicyInstalledAppCounter(Context context, PackageManager packageManager, ApplicationFeatureProvider.NumberOfAppsCallback numberOfAppsCallback) {
            super(context, 1, packageManager);
            this.mCallback = numberOfAppsCallback;
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.applications.AppCounter
        public void onCountComplete(int i) {
            this.mCallback.onNumberOfAppsResult(i);
        }
    }

    private static class CurrentUserAndManagedProfileAppWithAdminGrantedPermissionsCounter extends AppWithAdminGrantedPermissionsCounter {
        private ApplicationFeatureProvider.NumberOfAppsCallback mCallback;

        CurrentUserAndManagedProfileAppWithAdminGrantedPermissionsCounter(Context context, String[] strArr, PackageManager packageManager, IPackageManager iPackageManager, DevicePolicyManager devicePolicyManager, ApplicationFeatureProvider.NumberOfAppsCallback numberOfAppsCallback) {
            super(context, strArr, packageManager, iPackageManager, devicePolicyManager);
            this.mCallback = numberOfAppsCallback;
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.applications.AppCounter
        public void onCountComplete(int i) {
            this.mCallback.onNumberOfAppsResult(i);
        }
    }

    private static class CurrentUserPolicyInstalledAppLister extends InstalledAppLister {
        private ApplicationFeatureProvider.ListOfAppsCallback mCallback;

        CurrentUserPolicyInstalledAppLister(PackageManager packageManager, UserManager userManager, ApplicationFeatureProvider.ListOfAppsCallback listOfAppsCallback) {
            super(packageManager, userManager);
            this.mCallback = listOfAppsCallback;
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.applications.AppLister
        public void onAppListBuilt(List<UserAppInfo> list) {
            this.mCallback.onListOfAppsResult(list);
        }
    }

    private static class CurrentUserAppWithAdminGrantedPermissionsLister extends AppWithAdminGrantedPermissionsLister {
        private ApplicationFeatureProvider.ListOfAppsCallback mCallback;

        CurrentUserAppWithAdminGrantedPermissionsLister(String[] strArr, PackageManager packageManager, IPackageManager iPackageManager, DevicePolicyManager devicePolicyManager, UserManager userManager, ApplicationFeatureProvider.ListOfAppsCallback listOfAppsCallback) {
            super(strArr, packageManager, iPackageManager, devicePolicyManager, userManager);
            this.mCallback = listOfAppsCallback;
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.applications.AppLister
        public void onAppListBuilt(List<UserAppInfo> list) {
            this.mCallback.onListOfAppsResult(list);
        }
    }

    /* access modifiers changed from: package-private */
    public ComponentInfo findEuiccService(PackageManager packageManager) {
        ComponentInfo findEuiccService = findEuiccService(packageManager, packageManager.queryIntentServices(new Intent("android.service.euicc.EuiccService"), 269484096));
        if (findEuiccService == null) {
            Log.w("AppFeatureProviderImpl", "No valid EuiccService implementation found");
        }
        return findEuiccService;
    }

    private ComponentInfo findEuiccService(PackageManager packageManager, List<ResolveInfo> list) {
        ComponentInfo componentInfo = null;
        if (list != null) {
            int i = Integer.MIN_VALUE;
            for (ResolveInfo resolveInfo : list) {
                if (isValidEuiccComponent(packageManager, resolveInfo) && resolveInfo.filter.getPriority() > i) {
                    i = resolveInfo.filter.getPriority();
                    componentInfo = getComponentInfo(resolveInfo);
                }
            }
        }
        return componentInfo;
    }

    private boolean isValidEuiccComponent(PackageManager packageManager, ResolveInfo resolveInfo) {
        String str;
        ComponentInfo componentInfo = getComponentInfo(resolveInfo);
        String str2 = componentInfo.packageName;
        if (packageManager.checkPermission("android.permission.WRITE_EMBEDDED_SUBSCRIPTIONS", str2) != 0) {
            Log.e("AppFeatureProviderImpl", "Package " + str2 + " does not declare WRITE_EMBEDDED_SUBSCRIPTIONS");
            return false;
        }
        if (componentInfo instanceof ServiceInfo) {
            str = ((ServiceInfo) componentInfo).permission;
        } else if (componentInfo instanceof ActivityInfo) {
            str = ((ActivityInfo) componentInfo).permission;
        } else {
            throw new IllegalArgumentException("Can only verify services/activities");
        }
        if (!TextUtils.equals(str, "android.permission.BIND_EUICC_SERVICE")) {
            Log.e("AppFeatureProviderImpl", "Package " + str2 + " does not require the BIND_EUICC_SERVICE permission");
            return false;
        }
        IntentFilter intentFilter = resolveInfo.filter;
        if (intentFilter != null && intentFilter.getPriority() != 0) {
            return true;
        }
        Log.e("AppFeatureProviderImpl", "Package " + str2 + " does not specify a priority");
        return false;
    }

    private ComponentInfo getComponentInfo(ResolveInfo resolveInfo) {
        ActivityInfo activityInfo = resolveInfo.activityInfo;
        if (activityInfo != null) {
            return activityInfo;
        }
        ServiceInfo serviceInfo = resolveInfo.serviceInfo;
        if (serviceInfo != null) {
            return serviceInfo;
        }
        ProviderInfo providerInfo = resolveInfo.providerInfo;
        if (providerInfo != null) {
            return providerInfo;
        }
        throw new IllegalStateException("Missing ComponentInfo!");
    }
}
