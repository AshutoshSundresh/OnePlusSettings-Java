package com.android.settingslib;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.UserInfo;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.telephony.NetworkRegistrationInfo;
import android.telephony.ServiceState;
import android.text.format.Formatter;
import com.android.internal.annotations.VisibleForTesting;
import com.android.launcher3.icons.IconFactory;
import com.android.settingslib.drawable.UserIconDrawable;
import com.android.settingslib.drawable.UserIcons;
import com.android.settingslib.fuelgauge.BatteryStatus;
import java.text.NumberFormat;
import java.util.Locale;

public class Utils {
    @VisibleForTesting
    static final String STORAGE_MANAGER_ENABLED_PROPERTY = "ro.storage_manager.enabled";
    public static final String[] UNIT_OF_STORAGE = {"%28?<%21[吉千兆太]%29比特", "%28?<%21[吉千兆太]%29字节", "吉比特", "吉字节", "千比特", "千字节", "兆比特", "兆字节", "太比特", "太字节"};
    public static final String[] UNIT_OF_STORAGE_REPLACE = {"b", "B", "Gb", "GB", "Kb", "KB", "Mb", "MB", "Tb", "TB"};
    static final int[] WIFI_PIE = {R$drawable.op_ic_wifi_signal_0, R$drawable.op_ic_wifi_signal_1, R$drawable.op_ic_wifi_signal_2, R$drawable.op_ic_wifi_signal_3, R$drawable.op_ic_wifi_signal_4};
    private static String sPermissionControllerPackageName;
    private static String sServicesSystemSharedLibPackageName;
    private static String sSharedSystemSharedLibPackageName;
    private static Signature[] sSystemSignature;

    public static void updateLocationEnabled(Context context, boolean z, int i, int i2) {
        Settings.Secure.putIntForUser(context.getContentResolver(), "location_changer", i2, i);
        ((LocationManager) context.getSystemService(LocationManager.class)).setLocationEnabledForUser(z, UserHandle.of(i));
    }

    public static int getTetheringLabel(ConnectivityManager connectivityManager) {
        String[] tetherableUsbRegexs = connectivityManager.getTetherableUsbRegexs();
        String[] tetherableWifiRegexs = connectivityManager.getTetherableWifiRegexs();
        String[] tetherableBluetoothRegexs = connectivityManager.getTetherableBluetoothRegexs();
        boolean z = true;
        boolean z2 = tetherableUsbRegexs.length != 0;
        boolean z3 = tetherableWifiRegexs.length != 0;
        if (tetherableBluetoothRegexs.length == 0) {
            z = false;
        }
        if (z3 && z2 && z) {
            return R$string.tether_settings_title_all;
        }
        if (z3 && z2) {
            return R$string.tether_settings_title_all;
        }
        if (z3 && z) {
            return R$string.tether_settings_title_all;
        }
        if (z3) {
            return R$string.tether_settings_title_wifi;
        }
        if (z2 && z) {
            return R$string.tether_settings_title_usb_bluetooth;
        }
        if (z2) {
            return R$string.tether_settings_title_usb;
        }
        return R$string.tether_settings_title_bluetooth;
    }

    public static String getUserLabel(Context context, UserInfo userInfo) {
        String str = userInfo != null ? userInfo.name : null;
        if (userInfo.id != 999 && userInfo.isManagedProfile()) {
            return context.getString(R$string.managed_user_title);
        }
        if (userInfo.isGuest()) {
            str = context.getString(R$string.user_guest);
        }
        if (str == null && userInfo != null) {
            str = Integer.toString(userInfo.id);
        } else if (userInfo == null) {
            str = context.getString(R$string.unknown);
        }
        return context.getResources().getString(R$string.running_process_item_user_label, str);
    }

    public static Drawable getUserIcon(Context context, UserManager userManager, UserInfo userInfo) {
        Bitmap userIcon;
        int sizeForList = UserIconDrawable.getSizeForList(context);
        if (userInfo.isManagedProfile()) {
            Drawable managedUserDrawable = UserIconDrawable.getManagedUserDrawable(context);
            managedUserDrawable.setBounds(0, 0, sizeForList, sizeForList);
            return managedUserDrawable;
        } else if (userInfo.iconPath == null || (userIcon = userManager.getUserIcon(userInfo.id)) == null) {
            UserIconDrawable userIconDrawable = new UserIconDrawable(sizeForList);
            userIconDrawable.setIconDrawable(UserIcons.getDefaultUserIcon(context.getResources(), userInfo.id, false));
            userIconDrawable.bake();
            return userIconDrawable;
        } else {
            UserIconDrawable userIconDrawable2 = new UserIconDrawable(sizeForList);
            userIconDrawable2.setIcon(userIcon);
            userIconDrawable2.bake();
            return userIconDrawable2;
        }
    }

    public static String formatPercentage(double d, boolean z) {
        return formatPercentage(z ? Math.round((float) d) : (int) d);
    }

    public static String formatPercentage(long j, long j2) {
        return formatPercentage(((double) j) / ((double) j2));
    }

    public static String formatPercentage(int i) {
        return formatPercentage(((double) i) / 100.0d);
    }

    public static String formatPercentage(double d) {
        return NumberFormat.getPercentInstance().format(d);
    }

    public static int getBatteryLevel(Intent intent) {
        return (intent.getIntExtra("level", 0) * 100) / intent.getIntExtra("scale", 100);
    }

    public static String getBatteryStatus(Context context, Intent intent) {
        int intExtra = intent.getIntExtra("status", 1);
        Resources resources = context.getResources();
        String string = resources.getString(R$string.battery_info_status_unknown);
        BatteryStatus batteryStatus = new BatteryStatus(intent);
        if (batteryStatus.isCharged()) {
            return resources.getString(R$string.battery_info_status_full);
        }
        if (intExtra == 2) {
            int chargingSpeed = batteryStatus.getChargingSpeed(context);
            if (chargingSpeed != 0) {
                return chargingSpeed != 2 ? resources.getString(R$string.battery_info_status_charging) : resources.getString(R$string.battery_info_status_charging_fast);
            }
            return resources.getString(R$string.battery_info_status_charging_slow);
        } else if (intExtra == 3) {
            return resources.getString(R$string.battery_info_status_discharging);
        } else {
            return intExtra == 4 ? resources.getString(R$string.battery_info_status_not_charging) : string;
        }
    }

    public static ColorStateList getColorAccent(Context context) {
        return getColorAttr(context, 16843829);
    }

    public static int getColorAccentDefaultColor(Context context) {
        return getColorAttrDefaultColor(context, 16843829);
    }

    public static int getColorStateListDefaultColor(Context context, int i) {
        return context.getResources().getColorStateList(i, context.getTheme()).getDefaultColor();
    }

    public static int getDisabled(Context context, int i) {
        return applyAlphaAttr(context, 16842803, i);
    }

    public static int applyAlphaAttr(Context context, int i, int i2) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(new int[]{i});
        float f = obtainStyledAttributes.getFloat(0, 0.0f);
        obtainStyledAttributes.recycle();
        return applyAlpha(f, i2);
    }

    public static int applyAlpha(float f, int i) {
        return Color.argb((int) (f * ((float) Color.alpha(i))), Color.red(i), Color.green(i), Color.blue(i));
    }

    public static int getColorAttrDefaultColor(Context context, int i) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(new int[]{i});
        int color = obtainStyledAttributes.getColor(0, 0);
        obtainStyledAttributes.recycle();
        return color;
    }

    public static ColorStateList getColorAttr(Context context, int i) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(new int[]{i});
        try {
            return obtainStyledAttributes.getColorStateList(0);
        } finally {
            obtainStyledAttributes.recycle();
        }
    }

    public static boolean isSystemPackage(Resources resources, PackageManager packageManager, PackageInfo packageInfo) {
        if (sSystemSignature == null) {
            sSystemSignature = new Signature[]{getSystemSignature(packageManager)};
        }
        if (sPermissionControllerPackageName == null) {
            sPermissionControllerPackageName = packageManager.getPermissionControllerPackageName();
        }
        if (sServicesSystemSharedLibPackageName == null) {
            sServicesSystemSharedLibPackageName = packageManager.getServicesSystemSharedLibraryPackageName();
        }
        if (sSharedSystemSharedLibPackageName == null) {
            sSharedSystemSharedLibPackageName = packageManager.getSharedSystemSharedLibraryPackageName();
        }
        Signature[] signatureArr = sSystemSignature;
        return (signatureArr[0] != null && signatureArr[0].equals(getFirstSignature(packageInfo))) || packageInfo.packageName.equals(sPermissionControllerPackageName) || packageInfo.packageName.equals(sServicesSystemSharedLibPackageName) || packageInfo.packageName.equals(sSharedSystemSharedLibPackageName) || packageInfo.packageName.equals("com.android.printspooler") || isDeviceProvisioningPackage(resources, packageInfo.packageName);
    }

    private static Signature getFirstSignature(PackageInfo packageInfo) {
        Signature[] signatureArr;
        if (packageInfo == null || (signatureArr = packageInfo.signatures) == null || signatureArr.length <= 0) {
            return null;
        }
        return signatureArr[0];
    }

    private static Signature getSystemSignature(PackageManager packageManager) {
        try {
            return getFirstSignature(packageManager.getPackageInfo("android", 64));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isDeviceProvisioningPackage(Resources resources, String str) {
        String string = resources.getString(17039890);
        return string != null && string.equals(str);
    }

    public static int getWifiIconResource(int i) {
        return getWifiIconResource(i, 0, false);
    }

    public static int getWifiIconResource(int i, int i2, boolean z) {
        if (i >= 0) {
            int[] iArr = WIFI_PIE;
            if (i < iArr.length) {
                return iArr[i];
            }
        }
        throw new IllegalArgumentException("No Wifi icon found for level: " + i);
    }

    public static int getDefaultStorageManagerDaysToRetain(Resources resources) {
        try {
            return resources.getInteger(17694912);
        } catch (Resources.NotFoundException unused) {
            return 90;
        }
    }

    public static boolean isWifiOnly(Context context) {
        return !((ConnectivityManager) context.getSystemService(ConnectivityManager.class)).isNetworkSupported(0);
    }

    public static boolean isStorageManagerEnabled(Context context) {
        boolean z;
        try {
            z = SystemProperties.getBoolean(STORAGE_MANAGER_ENABLED_PROPERTY, false);
        } catch (Resources.NotFoundException unused) {
            z = false;
        }
        ContentResolver contentResolver = context.getContentResolver();
        int i = z ? 1 : 0;
        int i2 = z ? 1 : 0;
        int i3 = z ? 1 : 0;
        if (Settings.Secure.getInt(contentResolver, "automatic_storage_manager_enabled", i) != 0) {
            return true;
        }
        return false;
    }

    public static boolean isAudioModeOngoingCall(Context context) {
        int mode = ((AudioManager) context.getSystemService(AudioManager.class)).getMode();
        return mode == 1 || mode == 2 || mode == 3;
    }

    public static boolean isInService(ServiceState serviceState) {
        int combinedServiceState;
        return (serviceState == null || (combinedServiceState = getCombinedServiceState(serviceState)) == 3 || combinedServiceState == 1 || combinedServiceState == 2) ? false : true;
    }

    public static int getCombinedServiceState(ServiceState serviceState) {
        if (serviceState == null) {
            return 1;
        }
        int state = serviceState.getState();
        int dataRegistrationState = serviceState.getDataRegistrationState();
        if ((state == 1 || state == 2) && dataRegistrationState == 0 && isNotInIwlan(serviceState)) {
            return 0;
        }
        return state;
    }

    public static Drawable getBadgedIcon(Context context, Drawable drawable, UserHandle userHandle) {
        IconFactory obtain = IconFactory.obtain(context);
        try {
            BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), obtain.createBadgedIconBitmap(drawable, userHandle, true).icon);
            if (obtain != null) {
                obtain.close();
            }
            return bitmapDrawable;
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        throw th;
    }

    public static Drawable getBadgedIcon(Context context, ApplicationInfo applicationInfo) {
        return getBadgedIcon(context, applicationInfo.loadUnbadgedIcon(context.getPackageManager()), UserHandle.getUserHandleForUid(applicationInfo.uid));
    }

    private static boolean isNotInIwlan(ServiceState serviceState) {
        NetworkRegistrationInfo networkRegistrationInfo = serviceState.getNetworkRegistrationInfo(2, 2);
        if (networkRegistrationInfo == null) {
            return true;
        }
        return !(networkRegistrationInfo.getRegistrationState() == 1 || networkRegistrationInfo.getRegistrationState() == 5);
    }

    public static String formatFileSize(Context context, long j) {
        String formatFileSize = Formatter.formatFileSize(context, j);
        if (Build.VERSION.SDK_INT > 26) {
            Locale locale = Locale.getDefault();
            String language = locale.getLanguage();
            String country = locale.getCountry();
            if (language.equalsIgnoreCase("zh") && country.equalsIgnoreCase("CN")) {
                int i = 0;
                while (true) {
                    String[] strArr = UNIT_OF_STORAGE;
                    if (i >= strArr.length) {
                        break;
                    }
                    formatFileSize = formatFileSize.replaceAll(strArr[i], UNIT_OF_STORAGE_REPLACE[i]);
                    i++;
                }
            }
        }
        return formatFileSize;
    }
}
