package com.android.settings.datausage;

import android.content.Context;
import android.net.INetworkStatsService;
import android.net.NetworkPolicy;
import android.net.NetworkPolicyManager;
import android.os.Bundle;
import android.os.INetworkManagementService;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.datausage.TemplatePreference;
import com.android.settingslib.NetworkPolicyEditor;

public abstract class DataUsageBaseFragment extends DashboardFragment {
    protected final TemplatePreference.NetworkServices services = new TemplatePreference.NetworkServices();

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Context context = getContext();
        this.services.mNetworkService = INetworkManagementService.Stub.asInterface(ServiceManager.getService("network_management"));
        this.services.mStatsService = INetworkStatsService.Stub.asInterface(ServiceManager.getService("netstats"));
        this.services.mPolicyManager = (NetworkPolicyManager) context.getSystemService("netpolicy");
        TemplatePreference.NetworkServices networkServices = this.services;
        networkServices.mPolicyEditor = new NetworkPolicyEditor(networkServices.mPolicyManager);
        this.services.mTelephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        this.services.mSubscriptionManager = SubscriptionManager.from(context);
        this.services.mUserManager = UserManager.get(context);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        this.services.mPolicyEditor.read();
    }

    /* access modifiers changed from: protected */
    public boolean isAdmin() {
        return this.services.mUserManager.isAdminUser();
    }

    /* access modifiers changed from: protected */
    public boolean isMobileDataAvailable(int i) {
        return this.services.mSubscriptionManager.getActiveSubscriptionInfo(i) != null;
    }

    /* access modifiers changed from: protected */
    public boolean isNetworkPolicyModifiable(NetworkPolicy networkPolicy, int i) {
        return networkPolicy != null && isBandwidthControlEnabled() && this.services.mUserManager.isAdminUser() && isDataEnabled(i);
    }

    private boolean isDataEnabled(int i) {
        if (i == -1) {
            return true;
        }
        return this.services.mTelephonyManager.getDataEnabled(i);
    }

    /* access modifiers changed from: protected */
    public boolean isBandwidthControlEnabled() {
        try {
            return this.services.mNetworkService.isBandwidthControlEnabled();
        } catch (RemoteException e) {
            Log.w("DataUsageBase", "problem talking with INetworkManagementService: ", e);
            return false;
        }
    }
}
