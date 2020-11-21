package com.oneplus.settings.packageuninstaller;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.admin.IDevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.UserInfo;
import android.graphics.drawable.Icon;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import android.widget.Toast;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.oneplus.settings.OPMemberController;
import com.oneplus.settings.utils.OPUtils;
import java.util.Iterator;
import java.util.List;

public class OPUninstallFinish extends BroadcastReceiver {
    private static final String LOG_TAG = OPUninstallFinish.class.getSimpleName();

    public void onReceive(Context context, Intent intent) {
        int i;
        String str;
        NotificationManager notificationManager;
        int i2;
        int i3;
        List list;
        RemoteException e;
        UserInfo userInfo;
        String str2 = LOG_TAG;
        int intExtra = intent.getIntExtra("android.content.pm.extra.STATUS", 0);
        Log.i(str2, "Uninstall finished extras=" + intent.getExtras());
        if (intExtra == -1) {
            context.startActivity((Intent) intent.getParcelableExtra("android.intent.extra.INTENT"));
            return;
        }
        int intExtra2 = intent.getIntExtra("com.android.packageinstaller.extra.UNINSTALL_ID", 0);
        ApplicationInfo applicationInfo = (ApplicationInfo) intent.getParcelableExtra("com.android.packageinstaller.applicationInfo");
        String stringExtra = intent.getStringExtra("com.android.packageinstaller.extra.APP_LABEL");
        boolean booleanExtra = intent.getBooleanExtra("android.intent.extra.UNINSTALL_ALL_USERS", false);
        NotificationManager notificationManager2 = (NotificationManager) context.getSystemService(NotificationManager.class);
        UserManager userManager = (UserManager) context.getSystemService(UserManager.class);
        notificationManager2.createNotificationChannel(new NotificationChannel("uninstall failure", context.getString(C0017R$string.uninstall_failure_notification_channel), 3));
        Notification.Builder builder = new Notification.Builder(context, "uninstall failure");
        if (intExtra != 0) {
            if (intExtra != 2) {
                Log.d(str2, "Uninstall failed for " + applicationInfo.packageName + " with code " + intExtra);
            } else {
                int intExtra3 = intent.getIntExtra("android.content.pm.extra.LEGACY_STATUS", 0);
                if (intExtra3 == -4) {
                    IPackageManager asInterface = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
                    i2 = intExtra2;
                    List users = userManager.getUsers();
                    notificationManager = notificationManager2;
                    int i4 = 0;
                    while (true) {
                        str = stringExtra;
                        if (i4 >= users.size()) {
                            i3 = -10000;
                            break;
                        }
                        UserInfo userInfo2 = (UserInfo) users.get(i4);
                        try {
                            list = users;
                            try {
                                if (asInterface.getBlockUninstallForUser(applicationInfo.packageName, userInfo2.id)) {
                                    i3 = userInfo2.id;
                                    break;
                                }
                                i4++;
                                stringExtra = str;
                                users = list;
                            } catch (RemoteException e2) {
                                e = e2;
                                Log.e(str2, "Failed to talk to package manager", e);
                                i4++;
                                stringExtra = str;
                                users = list;
                            }
                        } catch (RemoteException e3) {
                            e = e3;
                            list = users;
                            Log.e(str2, "Failed to talk to package manager", e);
                            i4++;
                            stringExtra = str;
                            users = list;
                        }
                    }
                    if (isProfileOfOrSame(userManager, UserHandle.myUserId(), i3)) {
                        addDeviceManagerButton(context, builder);
                    } else {
                        addManageUsersButton(context, builder);
                    }
                    if (i3 == -10000) {
                        Log.d(str2, "Uninstall failed for " + applicationInfo.packageName + " with code " + intExtra + " no blocking user");
                    } else if (i3 == 0) {
                        setBigText(builder, context.getString(C0017R$string.uninstall_blocked_device_owner));
                    } else if (booleanExtra) {
                        setBigText(builder, context.getString(C0017R$string.uninstall_all_blocked_profile_owner));
                    } else {
                        setBigText(builder, context.getString(C0017R$string.uninstall_blocked_profile_owner));
                    }
                    builder.setContentTitle(context.getString(C0017R$string.uninstall_failed_app, str));
                    builder.setOngoing(false);
                    builder.setSmallIcon(C0008R$drawable.op_ic_error);
                    notificationManager.notify(i2, builder.build());
                    return;
                } else if (intExtra3 != -2) {
                    Log.d(str2, "Uninstall blocked for " + applicationInfo.packageName + " with legacy code " + intExtra3);
                } else {
                    IDevicePolicyManager asInterface2 = IDevicePolicyManager.Stub.asInterface(ServiceManager.getService("device_policy"));
                    int myUserId = UserHandle.myUserId();
                    Iterator it = userManager.getUsers().iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            userInfo = null;
                            break;
                        }
                        UserInfo userInfo3 = (UserInfo) it.next();
                        if (!isProfileOfOrSame(userManager, myUserId, userInfo3.id)) {
                            try {
                                if (asInterface2.packageHasActiveAdmins(applicationInfo.packageName, userInfo3.id)) {
                                    userInfo = userInfo3;
                                    break;
                                }
                            } catch (RemoteException e4) {
                                Log.e(str2, "Failed to talk to package manager", e4);
                            }
                        }
                    }
                    if (userInfo == null) {
                        Log.d(str2, "Uninstall failed because " + applicationInfo.packageName + " is a device admin");
                        addDeviceManagerButton(context, builder);
                        setBigText(builder, context.getString(C0017R$string.uninstall_failed_device_policy_manager));
                    } else {
                        Log.d(str2, "Uninstall failed because " + applicationInfo.packageName + " is a device admin of user " + userInfo);
                        setBigText(builder, String.format(context.getString(C0017R$string.uninstall_failed_device_policy_manager_of_user), userInfo.name));
                    }
                }
            }
            i2 = intExtra2;
            str = stringExtra;
            notificationManager = notificationManager2;
            builder.setContentTitle(context.getString(C0017R$string.uninstall_failed_app, str));
            builder.setOngoing(false);
            builder.setSmallIcon(C0008R$drawable.op_ic_error);
            notificationManager.notify(i2, builder.build());
            return;
        }
        notificationManager2.cancel(intExtra2);
        notifyPackageRemoved(context, applicationInfo.packageName);
        int i5 = applicationInfo.uid;
        if (i5 >= 99910000) {
            i = 1;
            OPUtils.notifyMultiPackageRemoved(context, applicationInfo.packageName, i5, true);
        } else {
            i = 1;
        }
        int i6 = C0017R$string.uninstall_done_app;
        Object[] objArr = new Object[i];
        objArr[0] = stringExtra;
        Toast.makeText(context, context.getString(i6, objArr), i).show();
    }

    private void notifyPackageRemoved(Context context, String str) {
        Intent intent = new Intent("oneplus.settings.intent.action.PACKAGE_REMOVED");
        intent.putExtra("package_name", str);
        intent.setFlags(285212672);
        intent.setPackage(OPMemberController.PACKAGE_NAME);
        context.sendBroadcast(intent);
    }

    private boolean isProfileOfOrSame(UserManager userManager, int i, int i2) {
        if (i == i2) {
            return true;
        }
        UserInfo profileParent = userManager.getProfileParent(i2);
        if (profileParent == null || profileParent.id != i) {
            return false;
        }
        return true;
    }

    private void setBigText(Notification.Builder builder, CharSequence charSequence) {
        builder.setStyle(new Notification.BigTextStyle().bigText(charSequence));
    }

    private void addManageUsersButton(Context context, Notification.Builder builder) {
        Intent intent = new Intent("android.settings.USER_SETTINGS");
        intent.setFlags(1342177280);
        builder.addAction(new Notification.Action.Builder(Icon.createWithResource(context, C0008R$drawable.ic_settings_multiuser), context.getString(C0017R$string.manage_users), PendingIntent.getActivity(context, 0, intent, 134217728)).build());
    }

    private void addDeviceManagerButton(Context context, Notification.Builder builder) {
        Intent intent = new Intent();
        intent.setClassName(OPMemberController.PACKAGE_NAME, "com.android.settings.Settings$DeviceAdminSettingsActivity");
        intent.setFlags(1342177280);
        builder.addAction(new Notification.Action.Builder(Icon.createWithResource(context, C0008R$drawable.op_ic_lock), context.getString(C0017R$string.manage_device_administrators), PendingIntent.getActivity(context, 0, intent, 134217728)).build());
    }
}
