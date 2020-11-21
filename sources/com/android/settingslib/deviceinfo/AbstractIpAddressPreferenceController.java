package com.android.settingslib.deviceinfo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settingslib.R$string;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.net.InetAddress;
import java.util.Iterator;

public abstract class AbstractIpAddressPreferenceController extends AbstractConnectivityPreferenceController {
    private static final String[] CONNECTIVITY_INTENTS = {"android.net.conn.CONNECTIVITY_CHANGE", "android.net.wifi.LINK_CONFIGURATION_CHANGED", "android.net.wifi.STATE_CHANGE"};
    static final String KEY_IP_ADDRESS = "wifi_ip_address";
    private final ConnectivityManager mCM;
    private Preference mIpAddress;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return KEY_IP_ADDRESS;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public AbstractIpAddressPreferenceController(Context context, Lifecycle lifecycle) {
        super(context, lifecycle);
        this.mCM = (ConnectivityManager) context.getSystemService(ConnectivityManager.class);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mIpAddress = preferenceScreen.findPreference(KEY_IP_ADDRESS);
        updateConnectivity();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.deviceinfo.AbstractConnectivityPreferenceController
    public String[] getConnectivityIntents() {
        return CONNECTIVITY_INTENTS;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.deviceinfo.AbstractConnectivityPreferenceController
    public void updateConnectivity() {
        String defaultIpAddresses = getDefaultIpAddresses(this.mCM);
        if (defaultIpAddresses != null) {
            this.mIpAddress.setSummary(defaultIpAddresses);
        } else {
            this.mIpAddress.setSummary(R$string.status_unavailable);
        }
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
        while (it.hasNext()) {
            sb.append(((InetAddress) it.next()).getHostAddress());
            if (it.hasNext()) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
