package com.android.settings.development;

import android.content.Context;
import android.debug.IAdbManager;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.deviceinfo.AbstractConnectivityPreferenceController;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Iterator;

public class AdbIpAddressPreferenceController extends AbstractConnectivityPreferenceController {
    private static final String[] CONNECTIVITY_INTENTS = {"android.net.conn.CONNECTIVITY_CHANGE", "android.net.wifi.LINK_CONFIGURATION_CHANGED", "android.net.wifi.STATE_CHANGE"};
    private Preference mAdbIpAddrPref;
    private IAdbManager mAdbManager = IAdbManager.Stub.asInterface(ServiceManager.getService("adb"));
    private final ConnectivityManager mCM;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "adb_ip_addr_pref";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public AdbIpAddressPreferenceController(Context context, Lifecycle lifecycle) {
        super(context, lifecycle);
        this.mCM = (ConnectivityManager) context.getSystemService(ConnectivityManager.class);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mAdbIpAddrPref = preferenceScreen.findPreference("adb_ip_addr_pref");
        updateConnectivity();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        updateConnectivity();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.deviceinfo.AbstractConnectivityPreferenceController
    public String[] getConnectivityIntents() {
        return CONNECTIVITY_INTENTS;
    }

    /* access modifiers changed from: protected */
    public int getPort() {
        try {
            return this.mAdbManager.getAdbWirelessPort();
        } catch (RemoteException unused) {
            Log.e("AdbIpAddrPrefCtrl", "Unable to get the adbwifi port");
            return 0;
        }
    }

    public String getIpv4Address() {
        return getDefaultIpAddresses(this.mCM);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.deviceinfo.AbstractConnectivityPreferenceController
    public void updateConnectivity() {
        String defaultIpAddresses = getDefaultIpAddresses(this.mCM);
        if (defaultIpAddresses != null) {
            int port = getPort();
            if (port <= 0) {
                this.mAdbIpAddrPref.setSummary(C0017R$string.status_unavailable);
            } else {
                defaultIpAddresses = defaultIpAddresses + ":" + port;
            }
            this.mAdbIpAddrPref.setSummary(defaultIpAddresses);
            return;
        }
        this.mAdbIpAddrPref.setSummary(C0017R$string.status_unavailable);
    }

    private static String getDefaultIpAddresses(ConnectivityManager connectivityManager) {
        return formatIpAddresses(connectivityManager.getActiveLinkProperties());
    }

    private static String formatIpAddresses(LinkProperties linkProperties) {
        if (linkProperties == null) {
            return null;
        }
        Iterator it = linkProperties.getAllAddresses().iterator();
        if (!it.hasNext()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            InetAddress inetAddress = (InetAddress) it.next();
            if (inetAddress instanceof Inet4Address) {
                sb.append(inetAddress.getHostAddress());
                break;
            }
        }
        return sb.toString();
    }
}
