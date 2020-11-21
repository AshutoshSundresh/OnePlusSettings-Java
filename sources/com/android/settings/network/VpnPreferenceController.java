package com.android.settings.network;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.net.ConnectivityManager;
import android.net.IConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseArray;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.net.LegacyVpnInfo;
import com.android.internal.net.VpnConfig;
import com.android.settings.C0017R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.utils.ThreadUtils;

public class VpnPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, LifecycleObserver, OnResume, OnPause {
    private static final NetworkRequest REQUEST = new NetworkRequest.Builder().removeCapability(15).removeCapability(13).removeCapability(14).build();
    private final ConnectivityManager mConnectivityManager;
    private final IConnectivityManager mConnectivityManagerService;
    private final ConnectivityManager.NetworkCallback mNetworkCallback = new ConnectivityManager.NetworkCallback() {
        /* class com.android.settings.network.VpnPreferenceController.AnonymousClass1 */

        public void onAvailable(Network network) {
            VpnPreferenceController.this.updateSummary();
        }

        public void onLost(Network network) {
            VpnPreferenceController.this.updateSummary();
        }
    };
    private Preference mPreference;
    private final String mToggleable;
    private final UserManager mUserManager;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "vpn_settings";
    }

    public VpnPreferenceController(Context context) {
        super(context);
        this.mToggleable = Settings.Global.getString(context.getContentResolver(), "airplane_mode_toggleable_radios");
        this.mUserManager = (UserManager) context.getSystemService("user");
        this.mConnectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        this.mConnectivityManagerService = IConnectivityManager.Stub.asInterface(ServiceManager.getService("connectivity"));
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        Preference preference;
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference("vpn_settings");
        String str = this.mToggleable;
        if ((str == null || !str.contains("wifi")) && (preference = this.mPreference) != null) {
            preference.setDependency("airplane_mode");
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return !RestrictedLockUtilsInternal.hasBaseUserRestriction(this.mContext, "no_config_vpn", UserHandle.myUserId());
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        if (isAvailable()) {
            this.mConnectivityManager.unregisterNetworkCallback(this.mNetworkCallback);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        if (isAvailable()) {
            this.mConnectivityManager.registerNetworkCallback(REQUEST, this.mNetworkCallback);
        }
    }

    /* access modifiers changed from: package-private */
    public void updateSummary() {
        int i;
        String str;
        if (this.mPreference != null) {
            SparseArray sparseArray = new SparseArray();
            try {
                for (UserInfo userInfo : this.mUserManager.getUsers()) {
                    VpnConfig vpnConfig = this.mConnectivityManagerService.getVpnConfig(userInfo.id);
                    if (vpnConfig != null) {
                        if (vpnConfig.legacy) {
                            LegacyVpnInfo legacyVpnInfo = this.mConnectivityManagerService.getLegacyVpnInfo(userInfo.id);
                            if (legacyVpnInfo != null) {
                                if (legacyVpnInfo.state != 3) {
                                }
                            }
                        }
                        sparseArray.put(userInfo.id, vpnConfig);
                    }
                }
                UserInfo userInfo2 = this.mUserManager.getUserInfo(UserHandle.myUserId());
                if (userInfo2.isRestricted()) {
                    i = userInfo2.restrictedProfileParentId;
                } else {
                    i = userInfo2.id;
                }
                VpnConfig vpnConfig2 = (VpnConfig) sparseArray.get(i);
                if (vpnConfig2 == null) {
                    str = this.mContext.getString(C0017R$string.vpn_disconnected_summary);
                } else {
                    str = getNameForVpnConfig(vpnConfig2, UserHandle.of(i));
                }
                ThreadUtils.postOnMainThread(new Runnable(str) {
                    /* class com.android.settings.network.$$Lambda$VpnPreferenceController$iDQ0RgxaDkCLoaHHZ6UO2xSI_c */
                    public final /* synthetic */ String f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        VpnPreferenceController.this.lambda$updateSummary$0$VpnPreferenceController(this.f$1);
                    }
                });
            } catch (RemoteException e) {
                Log.e("VpnPreferenceController", "Unable to list active VPNs", e);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateSummary$0 */
    public /* synthetic */ void lambda$updateSummary$0$VpnPreferenceController(String str) {
        this.mPreference.setSummary(str);
    }

    /* access modifiers changed from: package-private */
    public String getNameForVpnConfig(VpnConfig vpnConfig, UserHandle userHandle) {
        if (vpnConfig.legacy) {
            return this.mContext.getString(C0017R$string.wifi_display_status_connected);
        }
        String str = vpnConfig.user;
        try {
            return VpnConfig.getVpnLabel(this.mContext.createPackageContextAsUser(this.mContext.getPackageName(), 0, userHandle), str).toString();
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("VpnPreferenceController", "Package " + str + " is not present", e);
            return null;
        }
    }
}
