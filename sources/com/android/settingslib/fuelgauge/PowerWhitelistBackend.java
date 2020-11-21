package com.android.settingslib.fuelgauge;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.IDeviceIdleController;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.telecom.DefaultDialerManager;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.telephony.SmsApplication;
import com.android.internal.util.ArrayUtils;

public class PowerWhitelistBackend {
    private static PowerWhitelistBackend sInstance;
    private final Context mAppContext;
    private final ArraySet<String> mDefaultActiveApps;
    private final IDeviceIdleController mDeviceIdleService;
    private final ArraySet<String> mSysWhitelistedApps;
    private final ArraySet<String> mSysWhitelistedAppsExceptIdle;
    private final ArraySet<String> mWhitelistedApps;

    public PowerWhitelistBackend(Context context) {
        this(context, IDeviceIdleController.Stub.asInterface(ServiceManager.getService("deviceidle")));
    }

    PowerWhitelistBackend(Context context, IDeviceIdleController iDeviceIdleController) {
        this.mWhitelistedApps = new ArraySet<>();
        this.mSysWhitelistedApps = new ArraySet<>();
        this.mSysWhitelistedAppsExceptIdle = new ArraySet<>();
        this.mDefaultActiveApps = new ArraySet<>();
        this.mAppContext = context.getApplicationContext();
        this.mDeviceIdleService = iDeviceIdleController;
        refreshList();
    }

    public boolean isSysWhitelisted(String str) {
        return this.mSysWhitelistedApps.contains(str);
    }

    public boolean isWhitelisted(String str) {
        if (!this.mWhitelistedApps.contains(str) && !isDefaultActiveApp(str)) {
            return false;
        }
        return true;
    }

    public boolean isDefaultActiveApp(String str) {
        if (!this.mDefaultActiveApps.contains(str) && !((DevicePolicyManager) this.mAppContext.getSystemService(DevicePolicyManager.class)).packageHasActiveAdmins(str)) {
            return false;
        }
        return true;
    }

    public boolean isWhitelisted(String[] strArr) {
        if (ArrayUtils.isEmpty(strArr)) {
            return false;
        }
        for (String str : strArr) {
            if (isWhitelisted(str)) {
                return true;
            }
        }
        return false;
    }

    public void addApp(String str) {
        try {
            this.mDeviceIdleService.addPowerSaveWhitelistApp(str);
            this.mWhitelistedApps.add(str);
        } catch (RemoteException e) {
            Log.w("PowerWhitelistBackend", "Unable to reach IDeviceIdleController", e);
        }
    }

    public void removeApp(String str) {
        try {
            this.mDeviceIdleService.removePowerSaveWhitelistApp(str);
            this.mWhitelistedApps.remove(str);
        } catch (RemoteException e) {
            Log.w("PowerWhitelistBackend", "Unable to reach IDeviceIdleController", e);
        }
    }

    public void refreshList() {
        this.mSysWhitelistedApps.clear();
        this.mSysWhitelistedAppsExceptIdle.clear();
        this.mWhitelistedApps.clear();
        this.mDefaultActiveApps.clear();
        IDeviceIdleController iDeviceIdleController = this.mDeviceIdleService;
        if (iDeviceIdleController != null) {
            try {
                for (String str : iDeviceIdleController.getFullPowerWhitelist()) {
                    this.mWhitelistedApps.add(str);
                }
                for (String str2 : this.mDeviceIdleService.getSystemPowerWhitelist()) {
                    this.mSysWhitelistedApps.add(str2);
                }
                for (String str3 : this.mDeviceIdleService.getSystemPowerWhitelistExceptIdle()) {
                    this.mSysWhitelistedAppsExceptIdle.add(str3);
                }
                boolean hasSystemFeature = this.mAppContext.getPackageManager().hasSystemFeature("android.hardware.telephony");
                ComponentName defaultSmsApplication = SmsApplication.getDefaultSmsApplication(this.mAppContext, true);
                String defaultDialerApplication = DefaultDialerManager.getDefaultDialerApplication(this.mAppContext);
                if (hasSystemFeature) {
                    if (defaultSmsApplication != null) {
                        this.mDefaultActiveApps.add(defaultSmsApplication.getPackageName());
                    }
                    if (!TextUtils.isEmpty(defaultDialerApplication)) {
                        this.mDefaultActiveApps.add(defaultDialerApplication);
                    }
                }
            } catch (RemoteException e) {
                Log.w("PowerWhitelistBackend", "Unable to reach IDeviceIdleController", e);
            }
        }
    }

    public static PowerWhitelistBackend getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PowerWhitelistBackend(context);
        }
        return sInstance;
    }
}
