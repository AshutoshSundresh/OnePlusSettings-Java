package com.google.analytics.tracking.android;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

class AppFieldsDefaultProvider implements DefaultProvider {
    private static AppFieldsDefaultProvider sInstance;
    private static Object sInstanceLock = new Object();
    protected String mAppId;
    protected String mAppInstallerId;
    protected String mAppName;
    protected String mAppVersion;

    public static void initializeProvider(Context context) {
        synchronized (sInstanceLock) {
            if (sInstance == null) {
                sInstance = new AppFieldsDefaultProvider(context);
            }
        }
    }

    static void dropInstance() {
        synchronized (sInstanceLock) {
            sInstance = null;
        }
    }

    public static AppFieldsDefaultProvider getProvider() {
        return sInstance;
    }

    private AppFieldsDefaultProvider(Context context) {
        PackageManager packageManager = context.getPackageManager();
        String packageName = context.getPackageName();
        this.mAppId = packageName;
        this.mAppInstallerId = packageManager.getInstallerPackageName(packageName);
        String str = this.mAppId;
        String str2 = null;
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            if (packageInfo != null) {
                str = packageManager.getApplicationLabel(packageInfo.applicationInfo).toString();
                str2 = packageInfo.versionName;
            }
        } catch (PackageManager.NameNotFoundException unused) {
            Log.e("Error retrieving package info: appName set to " + str);
        }
        this.mAppName = str;
        this.mAppVersion = str2;
    }

    protected AppFieldsDefaultProvider() {
    }

    @Override // com.google.analytics.tracking.android.DefaultProvider
    public String getValue(String str) {
        if (str == null) {
            return null;
        }
        if (str.equals("&an")) {
            return this.mAppName;
        }
        if (str.equals("&av")) {
            return this.mAppVersion;
        }
        if (str.equals("&aid")) {
            return this.mAppId;
        }
        if (str.equals("&aiid")) {
            return this.mAppInstallerId;
        }
        return null;
    }
}
