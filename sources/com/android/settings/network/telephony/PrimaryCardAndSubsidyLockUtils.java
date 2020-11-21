package com.android.settings.network.telephony;

import android.content.Context;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.util.Log;
import org.codeaurora.internal.IExtTelephony;

public final class PrimaryCardAndSubsidyLockUtils {
    public static boolean DBG = Log.isLoggable("PrimaryCardAndSubsidyLockUtils", 3);

    public static boolean isPrimaryCardEnabled() {
        return isVendorPropertyEnabled("persist.vendor.radio.primarycard");
    }

    public static boolean isPrimaryCardLWEnabled() {
        return isVendorPropertyEnabled("persist.vendor.radio.lw_enabled");
    }

    public static boolean isSubsidyLockFeatureEnabled() {
        return getVendorPropertyInt("ro.vendor.radio.subsidylock") == 1;
    }

    public static boolean isSubsidyUnlocked(Context context) {
        return getSubsidyStatus(context) == 103;
    }

    private static int getSubsidyStatus(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "subsidy_status", -1);
    }

    private static boolean isVendorPropertyEnabled(String str) {
        try {
            return IExtTelephony.Stub.asInterface(ServiceManager.getService("qti.radio.extphone")).getPropertyValueBool(str, false);
        } catch (RemoteException | NullPointerException e) {
            Log.e("PrimaryCardAndSubsidyLockUtils", "isVendorPropertyEnabled: " + str + ", Exception: ", e);
            return false;
        }
    }

    private static int getVendorPropertyInt(String str) {
        try {
            return IExtTelephony.Stub.asInterface(ServiceManager.getService("qti.radio.extphone")).getPropertyValueInt(str, -1);
        } catch (RemoteException | NullPointerException e) {
            Log.e("PrimaryCardAndSubsidyLockUtils", "getVendorPropertyInt: " + str + ", Exception: ", e);
            return -1;
        }
    }

    public static int getUiccCardProvisioningStatus(int i) {
        try {
            return IExtTelephony.Stub.asInterface(ServiceManager.getService("qti.radio.extphone")).getCurrentUiccCardProvisioningStatus(i);
        } catch (RemoteException | NullPointerException e) {
            Log.e("PrimaryCardAndSubsidyLockUtils", "getUiccCardProvisioningStatus: " + i + ", Exception: ", e);
            return 0;
        }
    }
}
