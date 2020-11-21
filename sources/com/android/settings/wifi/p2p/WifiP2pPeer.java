package com.android.settings.wifi.p2p;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.text.TextUtils;
import android.widget.ImageView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0003R$array;
import com.android.settings.C0004R$attr;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;

public class WifiP2pPeer extends Preference {
    static final int SIGNAL_LEVELS = 4;
    private static final int[] STATE_SECURED = {C0004R$attr.state_encrypted};
    public WifiP2pDevice device;
    final int mRssi = 60;
    private ImageView mSignal;

    public WifiP2pPeer(Context context, WifiP2pDevice wifiP2pDevice) {
        super(context);
        this.device = wifiP2pDevice;
        setWidgetLayoutResource(C0012R$layout.preference_widget_wifi_signal);
        if (TextUtils.isEmpty(this.device.deviceName)) {
            setTitle(this.device.deviceAddress);
        } else {
            setTitle(this.device.deviceName);
        }
        setSummary(context.getResources().getStringArray(C0003R$array.wifi_p2p_status)[this.device.status]);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        ImageView imageView = (ImageView) preferenceViewHolder.findViewById(C0010R$id.signal);
        this.mSignal = imageView;
        if (this.mRssi == Integer.MAX_VALUE) {
            imageView.setImageDrawable(null);
        } else {
            imageView.setImageResource(C0008R$drawable.wifi_signal);
            this.mSignal.setImageState(STATE_SECURED, true);
        }
        this.mSignal.setImageLevel(getLevel());
    }

    @Override // androidx.preference.Preference
    public int compareTo(Preference preference) {
        if (!(preference instanceof WifiP2pPeer)) {
            return 1;
        }
        WifiP2pDevice wifiP2pDevice = this.device;
        int i = wifiP2pDevice.status;
        WifiP2pDevice wifiP2pDevice2 = ((WifiP2pPeer) preference).device;
        int i2 = wifiP2pDevice2.status;
        if (i == i2) {
            String str = wifiP2pDevice.deviceName;
            if (str != null) {
                return str.compareToIgnoreCase(wifiP2pDevice2.deviceName);
            }
            return wifiP2pDevice.deviceAddress.compareToIgnoreCase(wifiP2pDevice2.deviceAddress);
        } else if (i < i2) {
            return -1;
        } else {
            return 1;
        }
    }

    /* access modifiers changed from: package-private */
    public int getLevel() {
        int i = this.mRssi;
        if (i == Integer.MAX_VALUE) {
            return -1;
        }
        return WifiManager.calculateSignalLevel(i, 4);
    }
}
