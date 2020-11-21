package com.android.settings.applications.manageapplications;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.INotificationManager;
import android.app.NotificationChannel;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.NetworkPolicyManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.telecom.DefaultDialerManager;
import android.util.Log;
import android.util.OpFeatures;
import androidx.appcompat.app.AlertDialog;
import com.android.internal.telephony.SmsApplication;
import com.android.settings.C0017R$string;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.defaultapp.DefaultAppLogic;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;

public class ResetAppsHelper implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener {
    private final AppOpsManager mAom;
    private final Context mContext;
    private final IPackageManager mIPm = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
    private final INotificationManager mNm = INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));
    private final NetworkPolicyManager mNpm;
    private final PackageManager mPm;
    private AlertDialog mResetDialog;

    public ResetAppsHelper(Context context) {
        this.mContext = context;
        this.mPm = context.getPackageManager();
        this.mNpm = NetworkPolicyManager.from(context);
        this.mAom = (AppOpsManager) context.getSystemService("appops");
    }

    public void onRestoreInstanceState(Bundle bundle) {
        if (bundle != null && bundle.getBoolean("resetDialog")) {
            buildResetDialog();
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        if (this.mResetDialog != null) {
            bundle.putBoolean("resetDialog", true);
        }
    }

    public void stop() {
        AlertDialog alertDialog = this.mResetDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.mResetDialog = null;
        }
    }

    /* access modifiers changed from: package-private */
    public void buildResetDialog() {
        if (this.mResetDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
            builder.setTitle(C0017R$string.reset_app_preferences_title);
            builder.setMessage(C0017R$string.reset_app_preferences_desc);
            builder.setPositiveButton(C0017R$string.reset_app_preferences_button, this);
            builder.setNegativeButton(C0017R$string.cancel, (DialogInterface.OnClickListener) null);
            builder.setOnDismissListener(this);
            this.mResetDialog = builder.show();
        }
    }

    public void onDismiss(DialogInterface dialogInterface) {
        if (this.mResetDialog == dialogInterface) {
            this.mResetDialog = null;
        }
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (this.mResetDialog == dialogInterface) {
            AsyncTask.execute(new Runnable() {
                /* class com.android.settings.applications.manageapplications.ResetAppsHelper.AnonymousClass1 */

                public void run() {
                    List<ApplicationInfo> installedApplications = ResetAppsHelper.this.mPm.getInstalledApplications(512);
                    for (int i = 0; i < installedApplications.size(); i++) {
                        ApplicationInfo applicationInfo = installedApplications.get(i);
                        try {
                            ResetAppsHelper.this.mNm.clearData(applicationInfo.packageName, applicationInfo.uid, false);
                        } catch (RemoteException unused) {
                        }
                        try {
                            NotificationChannel notificationChannelForPackage = ResetAppsHelper.this.mNm.getNotificationChannelForPackage(applicationInfo.packageName, applicationInfo.uid, "miscellaneous", (String) null, true);
                            if (notificationChannelForPackage != null && (ResetAppsHelper.this.mNm.onlyHasDefaultChannel(applicationInfo.packageName, applicationInfo.uid) || "miscellaneous".equals(notificationChannelForPackage.getId()))) {
                                notificationChannelForPackage.setImportance(-1000);
                                ResetAppsHelper.this.mNm.updateNotificationChannelForPackage(applicationInfo.packageName, applicationInfo.uid, notificationChannelForPackage);
                            }
                            ResetAppsHelper.this.mNm.setNotificationsEnabledForPackage(applicationInfo.packageName, applicationInfo.uid, true);
                            if (OpFeatures.isSupport(new int[]{26}) && ResetAppsHelper.this.mAom.checkOp(1005, applicationInfo.uid, applicationInfo.packageName) == 0) {
                                int uid = UserHandle.getUid(999, applicationInfo.uid);
                                NotificationChannel notificationChannelForPackage2 = ResetAppsHelper.this.mNm.getNotificationChannelForPackage(applicationInfo.packageName, uid, "miscellaneous", (String) null, true);
                                if (notificationChannelForPackage2 != null && (ResetAppsHelper.this.mNm.onlyHasDefaultChannel(applicationInfo.packageName, uid) || "miscellaneous".equals(notificationChannelForPackage2.getId()))) {
                                    notificationChannelForPackage2.setImportance(3);
                                    ResetAppsHelper.this.mNm.updateNotificationChannelForPackage(applicationInfo.packageName, uid, notificationChannelForPackage2);
                                }
                                ResetAppsHelper.this.mNm.setNotificationsEnabledForPackage(applicationInfo.packageName, uid, true);
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        if (!applicationInfo.enabled && ResetAppsHelper.this.mPm.getApplicationEnabledSetting(applicationInfo.packageName) == 3) {
                            ResetAppsHelper.this.mPm.setApplicationEnabledSetting(applicationInfo.packageName, 0, 1);
                        }
                    }
                    try {
                        ResetAppsHelper.this.mIPm.resetApplicationPreferences(UserHandle.myUserId());
                        if (OpFeatures.isSupport(new int[]{26}) && UserHandle.myUserId() == 0) {
                            ResetAppsHelper.this.mIPm.resetApplicationPreferences(999);
                        }
                    } catch (RemoteException unused2) {
                    }
                    ResetAppsHelper resetAppsHelper = ResetAppsHelper.this;
                    resetAppsHelper.resetDefaultApps(resetAppsHelper.mContext);
                    ResetAppsHelper.this.mAom.resetAllModes();
                    int[] uidsWithPolicy = ResetAppsHelper.this.mNpm.getUidsWithPolicy(1);
                    int currentUser = ActivityManager.getCurrentUser();
                    for (int i2 : uidsWithPolicy) {
                        if (UserHandle.getUserId(i2) == currentUser) {
                            ResetAppsHelper.this.mNpm.setUidPolicy(i2, 0);
                        }
                    }
                    if (Build.VERSION.IS_CTA_BUILD) {
                        Intent intent = new Intent("com.oneplus.cta.permission.RESET");
                        intent.setClassName("com.oneplus.permissionutil", "com.oneplus.permissionutil.ResetReceiver");
                        ResetAppsHelper.this.mContext.sendBroadcast(intent);
                    }
                }
            });
        }
    }

    private boolean isAppExist(String str) {
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = this.mContext.getPackageManager().getApplicationInfo(str, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            applicationInfo = null;
        }
        if (applicationInfo != null) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void resetDefaultApps(Context context) {
        try {
            int myUserId = UserHandle.myUserId();
            PackageManager packageManager = context.getPackageManager();
            DefaultAppLogic.getInstance(SettingsBaseApplication.mApplication).initDefaultAppSettings(true);
            SmsApplication.setDefaultApplication("com.android.mms", context);
            DefaultDialerManager.setDefaultDialerApplication(context, "com.android.dialer", myUserId);
            if (isAppExist("com.oneplus.gallery")) {
                Log.d("ResetAppsHelper", "reset op_default_app_gallerycom.oneplus.gallery");
                DefaultAppLogic.getInstance(SettingsBaseApplication.mApplication).resetAppByType("op_default_app_gallery", "com.oneplus.gallery");
            }
            if (isAppExist("com.oneplus.camera")) {
                Log.d("ResetAppsHelper", "reset op_default_app_cameracom.oneplus.camera");
                DefaultAppLogic.getInstance(SettingsBaseApplication.mApplication).resetAppByType("op_default_app_camera", "com.oneplus.camera");
            }
            if (OPUtils.isO2()) {
                if (isAppExist("com.google.android.gm")) {
                    Log.d("ResetAppsHelper", "reset op_default_app_emailcom.google.android.gm");
                    DefaultAppLogic.getInstance(SettingsBaseApplication.mApplication).resetAppByType("op_default_app_email", "com.google.android.gm");
                }
                if (isAppExist("com.android.chrome")) {
                    Log.d("ResetAppsHelper", "reset DEFAULT_BROWSER_OXYGENcom.android.chrome");
                    packageManager.setDefaultBrowserPackageNameAsUser("com.android.chrome", myUserId);
                }
            } else if (isAppExist("com.heytap.browser")) {
                Log.d("ResetAppsHelper", "reset DEFAULT_BROWSER_HYDROGENcom.heytap.browser");
                packageManager.setDefaultBrowserPackageNameAsUser("com.heytap.browser", myUserId);
            }
            IntentFilter intentFilter = new IntentFilter("android.intent.action.MAIN");
            intentFilter.addCategory("android.intent.category.HOME");
            intentFilter.addCategory("android.intent.category.DEFAULT");
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            packageManager.getHomeActivities(arrayList2);
            for (int i = 0; i < arrayList2.size(); i++) {
                ActivityInfo activityInfo = ((ResolveInfo) arrayList2.get(i)).activityInfo;
                arrayList.add(new ComponentName(activityInfo.packageName, activityInfo.name));
            }
            packageManager.replacePreferredActivity(intentFilter, 1048576, (ComponentName[]) arrayList.toArray(new ComponentName[0]), ComponentName.unflattenFromString("net.oneplus.launcher/net.oneplus.launcher.Launcher"));
        } catch (Exception e) {
            Log.e("ResetAppsHelper", "reset default app exception." + e.getMessage());
        }
    }
}
