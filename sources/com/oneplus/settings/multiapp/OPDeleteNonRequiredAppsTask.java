package com.oneplus.settings.multiapp;

import android.app.AppGlobals;
import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.ServiceManager;
import android.util.Log;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.view.IInputMethodManager;
import com.android.settings.C0003R$array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class OPDeleteNonRequiredAppsTask {
    private final Callback mCallback;
    private final Context mContext;
    private final List<String> mDisallowedAppsList;
    private final boolean mLeaveAllSystemAppsEnabled;
    private final PackageManager mPm;
    private final int mProvisioningType;
    private final List<String> mRequiredAppsList;
    private final int mUserId;

    public static abstract class Callback {
        public abstract void onError();

        public abstract void onSuccess();
    }

    public OPDeleteNonRequiredAppsTask(Context context, String str, int i, boolean z, int i2, boolean z2, Callback callback) {
        this(context, AppGlobals.getPackageManager(), getIInputMethodManager(), str, i, z, i2, z2, callback);
    }

    @VisibleForTesting
    OPDeleteNonRequiredAppsTask(Context context, IPackageManager iPackageManager, IInputMethodManager iInputMethodManager, String str, int i, boolean z, int i2, boolean z2, Callback callback) {
        this.mCallback = callback;
        this.mContext = context;
        this.mProvisioningType = i;
        this.mUserId = i2;
        this.mLeaveAllSystemAppsEnabled = z2;
        this.mPm = context.getPackageManager();
        int i3 = C0003R$array.required_apps_managed_profile;
        int i4 = C0003R$array.disallowed_apps_managed_profile;
        Resources resources = this.mContext.getResources();
        this.mRequiredAppsList = Arrays.asList(resources.getStringArray(i3));
        this.mDisallowedAppsList = Arrays.asList(resources.getStringArray(i4));
    }

    public void run() {
        if (this.mLeaveAllSystemAppsEnabled) {
            Log.e("DeleteNonRequiredAppsTask", "Not deleting non-required apps.");
            this.mCallback.onSuccess();
            return;
        }
        Log.e("DeleteNonRequiredAppsTask", "Deleting non required apps.");
        Set<String> packagesToDelete = getPackagesToDelete();
        removeNonInstalledPackages(packagesToDelete);
        if (packagesToDelete.isEmpty()) {
            this.mCallback.onSuccess();
            return;
        }
        IPackageDeleteObserver packageDeleteObserver = new PackageDeleteObserver(packagesToDelete.size());
        for (String str : packagesToDelete) {
            Log.e("DeleteNonRequiredAppsTask", "Deleting package [" + str + "] as user " + this.mUserId);
            this.mPm.deletePackageAsUser(str, packageDeleteObserver, 4, this.mUserId);
        }
    }

    private Set<String> getPackagesToDelete() {
        Set<String> currentAppsWithLauncher = getCurrentAppsWithLauncher();
        currentAppsWithLauncher.removeAll(getRequiredApps());
        int i = this.mProvisioningType;
        if (i == 0 || i == 2) {
            currentAppsWithLauncher.removeAll(getSystemInputMethods());
        }
        currentAppsWithLauncher.addAll(getDisallowedApps());
        return currentAppsWithLauncher;
    }

    private void removeNonInstalledPackages(Set<String> set) {
        HashSet hashSet = new HashSet();
        for (String str : set) {
            try {
                if (this.mPm.getPackageInfoAsUser(str, 0, this.mUserId) == null) {
                    hashSet.add(str);
                }
            } catch (PackageManager.NameNotFoundException unused) {
                hashSet.add(str);
            }
        }
        set.removeAll(hashSet);
    }

    private Set<String> getCurrentAppsWithLauncher() {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        List<ResolveInfo> queryIntentActivitiesAsUser = this.mPm.queryIntentActivitiesAsUser(intent, 1843712, this.mUserId);
        Log.d("DeleteNonRequiredAppsTask", "Oneplus-MATCH_SYSTEM_ONLY");
        HashSet hashSet = new HashSet();
        for (ResolveInfo resolveInfo : queryIntentActivitiesAsUser) {
            hashSet.add(resolveInfo.activityInfo.packageName);
        }
        return hashSet;
    }

    private Set<String> getSystemInputMethods() {
        List<InputMethodInfo> inputMethodList = ((InputMethodManager) this.mContext.getSystemService("input_method")).getInputMethodList();
        HashSet hashSet = new HashSet();
        for (InputMethodInfo inputMethodInfo : inputMethodList) {
            if ((inputMethodInfo.getServiceInfo().applicationInfo.flags & 1) != 0) {
                hashSet.add(inputMethodInfo.getPackageName());
            }
        }
        return hashSet;
    }

    /* access modifiers changed from: protected */
    public Set<String> getRequiredApps() {
        HashSet hashSet = new HashSet();
        hashSet.addAll(this.mRequiredAppsList);
        return hashSet;
    }

    private Set<String> getDisallowedApps() {
        HashSet hashSet = new HashSet();
        hashSet.addAll(this.mDisallowedAppsList);
        return hashSet;
    }

    class PackageDeleteObserver extends IPackageDeleteObserver.Stub {
        private final AtomicInteger mPackageCount;

        public PackageDeleteObserver(int i) {
            AtomicInteger atomicInteger = new AtomicInteger(0);
            this.mPackageCount = atomicInteger;
            atomicInteger.set(i);
        }

        public void packageDeleted(String str, int i) {
            if (i != 1) {
                Log.e("DeleteNonRequiredAppsTask", "Could not finish the provisioning: package deletion failed");
                OPDeleteNonRequiredAppsTask.this.mCallback.onError();
            } else if (this.mPackageCount.decrementAndGet() == 0) {
                Log.e("DeleteNonRequiredAppsTask", "All non-required system apps with launcher icon, and all disallowed apps have been uninstalled.");
                OPDeleteNonRequiredAppsTask.this.mCallback.onSuccess();
            }
        }
    }

    private static IInputMethodManager getIInputMethodManager() {
        return IInputMethodManager.Stub.asInterface(ServiceManager.getService("input_method"));
    }
}
