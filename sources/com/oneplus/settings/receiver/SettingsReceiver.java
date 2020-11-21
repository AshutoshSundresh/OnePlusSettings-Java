package com.oneplus.settings.receiver;

import android.app.AppOpsManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.SystemProperties;
import android.os.UserManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.bluetooth.Utils;
import com.android.settings.datausage.backgrounddata.utils.BackgroundDataUtils;
import com.android.settingslib.bluetooth.LocalBluetoothAdapter;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.utils.OPPrefUtil;
import com.oneplus.settings.utils.OPUtils;
import com.oneplus.settings.utils.OPZenModeUtils;
import com.oneplus.settings.utils.ProductUtils;

public class SettingsReceiver extends BroadcastReceiver {
    private AppOpsManager mAppOpsManager;
    private PackageManager mPackageManager;
    private UserManager mUm;
    private int mZenMode = 0;

    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        this.mZenMode = NotificationManager.from(context).getZenMode();
        Log.d("SettingsReceiver", "action = " + action);
        if ("android.intent.action.PACKAGE_REMOVED".equals(action) || "android.intent.action.PACKAGE_ADDED".equals(action)) {
            OPUtils.setAppUpdated(true);
        }
        if ("oneplus.settings.intent.action.PACKAGE_REMOVED".equals(action)) {
            String stringExtra = intent.getStringExtra("package_name");
            Log.d("SettingsReceiver", "ACTION_PACKAGE_REMOVED pkgName= " + stringExtra);
            if (this.mAppOpsManager == null) {
                this.mAppOpsManager = (AppOpsManager) context.getSystemService("appops");
            }
            if (this.mPackageManager == null) {
                this.mPackageManager = context.getPackageManager();
            }
            try {
                if (!OPUtils.hasMultiApp(context, stringExtra)) {
                    this.mAppOpsManager.setMode(1005, this.mPackageManager.getApplicationInfoAsUser(stringExtra, 1, 0).uid, stringExtra, 1);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if ("com.oneplus.cloud".equals(stringExtra)) {
                OPUtils.mIsExistCloudPackage = null;
            }
        }
        if ("android.intent.action.PACKAGE_ADDED".equals(action) && intent.getData() != null && "com.oneplus.cloud".equals(intent.getData().getSchemeSpecificPart())) {
            OPUtils.mIsExistCloudPackage = null;
        }
        if (action.equals("codeaurora.net.conn.TETHER_AUTO_SHUT_DOWN_SOFTAP")) {
            Log.d("SettingsReceiver", "Auto shutdown wifi ap if no device connected in 5 mins ");
            OPUtils.stopTethering(context);
        }
        if (action.equals("com.oem.intent.action.THREE_KEY_MODE")) {
            OPZenModeUtils.getInstance(context).sendAppTrackerDelay();
        }
        if (action.equals("android.intent.action.BOOT_COMPLETED")) {
            if (context.getSharedPreferences("App_Tracker", 0).getInt("zen_mode", 0) != this.mZenMode) {
                OPZenModeUtils.getInstance(context).sendAppTrackerDelay();
            }
            if (ProductUtils.isUsvMode()) {
                new Handler().postDelayed(new Runnable(this) {
                    /* class com.oneplus.settings.receiver.SettingsReceiver.AnonymousClass1 */

                    public void run() {
                        if (SystemProperties.getBoolean("persist.radio.network_select_vzw", false) && !SettingsReceiver.isSimAbsent(context) && !OPUtils.isGuestMode()) {
                            Intent intent = new Intent("android.settings.NETWORK_OPERATOR_SETTINGS");
                            intent.setFlags(268435456);
                            intent.putExtra("source_boot", true);
                            intent.setPackage("com.qualcomm.qti.networksetting");
                            context.startActivity(intent);
                        }
                    }
                }, 5000);
            }
        }
        if ("android.intent.action.LOCALE_CHANGED".equals(action)) {
            if (!OPUtils.isSupportFontStyleSetting()) {
                Log.i("SettingsReceiver", "! isSupportFontStyleSetting Language change");
                setFontMode(1);
            } else {
                Log.i("SettingsReceiver", " isSupportFontStyleSetting Language change");
                setFontMode(Settings.System.getIntForUser(SettingsBaseApplication.mApplication.getContentResolver(), "oem_font_mode", 1, 0));
            }
        }
        if ("android.bluetooth.adapter.action.STATE_CHANGED".equals(action)) {
            int intExtra = intent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE);
            Log.d("SettingsReceiver", "android.bluetooth.adapter.action.STATE_CHANGED");
            int i = OPPrefUtil.getInt("oneplus_bluetooth_scan_mode_flag", 0);
            if (intExtra == 12 && i == 0) {
                setBluetoothScanMode();
            }
        }
        if (action.equals("com.oem.intent.action.BOOT_COMPLETED")) {
            OPUtils.restoreBackupEntranceInLauncher(context);
            OPUtils.disableCloundServiceApp(context);
            OPUtils.enableAppBgService(context);
            OPUtils.enablePackageInstaller(context);
            OPUtils.disableWirelessAdbDebuging();
            OPUtils.sendAppTrackerForAllSettings();
            OPUtils.setCustomToneDarkModeLocation();
            OPUtils.sendAnalytics("dc_dimming", "status", Settings.System.getInt(context.getContentResolver(), "oneplus_dc_dimming_value", 0) != 0 ? "1" : "0");
            UserManager userManager = (UserManager) context.getSystemService("user");
            this.mUm = userManager;
            if (userManager != null && userManager.isUserRunning(999)) {
                Log.d("SettingsReceiver", "Handle Parallel App Requirement");
                try {
                    Settings.System.putIntForUser(SettingsBaseApplication.mApplication.getContentResolver(), "oem_acc_sensor_three_finger", Settings.System.getIntForUser(SettingsBaseApplication.mApplication.getContentResolver(), "oem_acc_sensor_three_finger", 0), 999);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                new Thread(new Runnable(this) {
                    /* class com.oneplus.settings.receiver.SettingsReceiver.AnonymousClass2 */

                    public void run() {
                        if (OPUtils.isO2()) {
                            OPUtils.installMultiApp(context, "com.google.android.gms", 999);
                        }
                        if (OPUtils.isAppPakExist(context, "org.ifaa.aidl.manager")) {
                            OPUtils.installMultiApp(context, "org.ifaa.aidl.manager", 999);
                        } else {
                            OPUtils.installMultiApp(context, "com.oneplus.ifaaservice", 999);
                        }
                    }
                }).start();
            }
            if (OPUtils.isSupportUss()) {
                BackgroundDataUtils.initAppBackgroundDataType(context);
            }
            if (OPUtils.isSupportUss() || OPUtils.isSupportUstMode()) {
                OPUtils.initHwId();
            }
            if (ProductUtils.isUsvMode()) {
                Settings.System.putInt(context.getContentResolver(), "bluetooth_default_scan_mode", 21);
                Utils.getLocalBtManager(SettingsBaseApplication.mApplication).getBluetoothAdapter().setScanMode(21);
            }
        }
        if ("oneplus.intent.action.otg_auto_shutdown".equals(intent.getAction())) {
            SystemProperties.set("persist.sys.oem.otg_support", "false");
            Settings.Global.putInt(context.getContentResolver(), "oneplus_otg_auto_disable", 0);
            if (Settings.System.getIntForUser(SettingsBaseApplication.mApplication.getContentResolver(), "oneplus_otg_auto_disable_is_first", 0, 0) == 0) {
                NotificationChannel notificationChannel = new NotificationChannel("OTG_INTENT_NOTIFICATION_CHANNEL", context.getResources().getString(C0017R$string.oneplus_otg_title), 3);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "OTG_INTENT_NOTIFICATION_CHANNEL");
                builder.setSmallIcon(C0008R$drawable.op_ic_otg);
                builder.setAutoCancel(true);
                builder.setContentTitle(context.getResources().getString(C0017R$string.oneplus_otg_title));
                Intent intent2 = new Intent("oneplus.intent.action.OTG_SETTINGS");
                intent2.addFlags(268435456);
                builder.setContentIntent(PendingIntent.getActivity(context, 0, intent2, 1073741824));
                NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
                notificationManager.createNotificationChannel(notificationChannel);
                notificationManager.notify(C0017R$string.oneplus_otg_title, builder.build());
                Settings.System.putInt(context.getContentResolver(), "oneplus_otg_auto_disable_is_first", 1);
            }
        }
    }

    private void setFontMode(int i) {
        Intent intent = new Intent("android.settings.OEM_FONT_MODE");
        intent.putExtra("oem_font_mode", i);
        intent.putExtra("oem_font_dialog", 0);
        intent.addFlags(268435456);
        SettingsBaseApplication.mApplication.sendBroadcast(intent);
    }

    private void setBluetoothScanMode() {
        new Thread(new Runnable(this) {
            /* class com.oneplus.settings.receiver.SettingsReceiver.AnonymousClass3 */

            public void run() {
                try {
                    String string = Settings.System.getString(SettingsBaseApplication.mApplication.getContentResolver(), "oem_oneplus_devicename");
                    LocalBluetoothAdapter bluetoothAdapter = Utils.getLocalBtManager(SettingsBaseApplication.mApplication).getBluetoothAdapter();
                    int i = Settings.System.getInt(SettingsBaseApplication.mApplication.getContentResolver(), "bluetooth_default_scan_mode", 21);
                    if (string != null && bluetoothAdapter != null) {
                        Log.d("SettingsReceiver", "bluetooth scan mode = " + i);
                        bluetoothAdapter.setName(string);
                        bluetoothAdapter.setScanMode(i);
                        OPPrefUtil.putInt("oneplus_bluetooth_scan_mode_flag", 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /* access modifiers changed from: private */
    public static boolean isSimAbsent(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getApplicationContext().getSystemService("phone");
        if (telephonyManager == null) {
            return false;
        }
        int simState = telephonyManager.getSimState();
        if (simState == 1 || simState == 0 || simState == 8 || simState == 6) {
            return true;
        }
        return false;
    }
}
