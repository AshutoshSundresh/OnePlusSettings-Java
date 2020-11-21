package com.android.settings.vpn2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.IConnectivityManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.security.KeyStore;
import android.util.Log;
import com.android.internal.net.VpnConfig;

public class VpnUtils {
    public static String getLockdownVpn() {
        byte[] bArr = KeyStore.getInstance().get("LOCKDOWN_VPN", true);
        if (bArr == null) {
            return null;
        }
        return new String(bArr);
    }

    public static void clearLockdownVpn(Context context) {
        KeyStore.getInstance().delete("LOCKDOWN_VPN");
        getConnectivityManager(context).updateLockdownVpn();
    }

    public static void setLockdownVpn(Context context, String str) {
        KeyStore.getInstance().put("LOCKDOWN_VPN", str.getBytes(), -1, 0);
        getConnectivityManager(context).updateLockdownVpn();
    }

    public static boolean isVpnLockdown(String str) {
        return str.equals(getLockdownVpn());
    }

    public static boolean isAnyLockdownActive(Context context) {
        int userId = context.getUserId();
        if (getLockdownVpn() != null) {
            return true;
        }
        if (getConnectivityManager(context).getAlwaysOnVpnPackageForUser(userId) == null || Settings.Secure.getIntForUser(context.getContentResolver(), "always_on_vpn_lockdown", 0, userId) == 0) {
            return false;
        }
        return true;
    }

    public static boolean isVpnActive(Context context) throws RemoteException {
        return getIConnectivityManager().getVpnConfig(context.getUserId()) != null;
    }

    public static String getConnectedPackage(IConnectivityManager iConnectivityManager, int i) throws RemoteException {
        VpnConfig vpnConfig = iConnectivityManager.getVpnConfig(i);
        if (vpnConfig != null) {
            return vpnConfig.user;
        }
        return null;
    }

    private static ConnectivityManager getConnectivityManager(Context context) {
        return (ConnectivityManager) context.getSystemService(ConnectivityManager.class);
    }

    private static IConnectivityManager getIConnectivityManager() {
        return IConnectivityManager.Stub.asInterface(ServiceManager.getService("connectivity"));
    }

    public static boolean isAlwaysOnVpnSet(ConnectivityManager connectivityManager, int i) {
        return connectivityManager.getAlwaysOnVpnPackageForUser(i) != null;
    }

    public static boolean disconnectLegacyVpn(Context context) {
        try {
            int userId = context.getUserId();
            IConnectivityManager iConnectivityManager = getIConnectivityManager();
            if (iConnectivityManager.getLegacyVpnInfo(userId) == null) {
                return false;
            }
            clearLockdownVpn(context);
            iConnectivityManager.prepareVpn((String) null, "[Legacy VPN]", userId);
            return true;
        } catch (RemoteException e) {
            Log.e("VpnUtils", "Legacy VPN could not be disconnected", e);
            return false;
        }
    }
}
