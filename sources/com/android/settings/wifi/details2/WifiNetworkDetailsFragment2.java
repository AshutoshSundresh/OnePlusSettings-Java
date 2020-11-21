package com.android.settings.wifi.details2;

import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.UserHandle;
import android.os.UserManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.Utils;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.wifi.WifiDialog2;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.wifitrackerlib.NetworkDetailsTracker;
import com.android.wifitrackerlib.WifiEntry;
import java.util.ArrayList;
import java.util.List;

public class WifiNetworkDetailsFragment2 extends DashboardFragment implements WifiDialog2.WifiDialog2Listener {
    private List<AbstractPreferenceController> mControllers;
    private NetworkDetailsTracker mNetworkDetailsTracker;
    private WifiDetailPreferenceController2 mWifiDetailPreferenceController2;
    private List<WifiDialog2.WifiDialog2Listener> mWifiDialogListeners = new ArrayList();
    private HandlerThread mWorkerThread;

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        return i == 1 ? 603 : 0;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "WifiNetworkDetailsFrg2";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 849;
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onDestroy() {
        this.mWorkerThread.quit();
        super.onDestroy();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.wifi_network_details_fragment2;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        if (getActivity() == null || this.mWifiDetailPreferenceController2 == null) {
            return null;
        }
        return WifiDialog2.createModal(getActivity(), this, this.mNetworkDetailsTracker.getWifiEntry(), 2);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        MenuItem add = menu.add(0, 1, 0, C0017R$string.wifi_modify);
        add.setIcon(C0008R$drawable.op_ic_edit);
        add.setShowAsAction(2);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 1) {
            return super.onOptionsItemSelected(menuItem);
        }
        if (!this.mWifiDetailPreferenceController2.canModifyNetwork()) {
            RestrictedLockUtils.EnforcedAdmin deviceOwner = RestrictedLockUtilsInternal.getDeviceOwner(getContext());
            if (deviceOwner == null) {
                DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getContext().getSystemService("device_policy");
                int managedProfileId = Utils.getManagedProfileId((UserManager) getContext().getSystemService("user"), UserHandle.myUserId());
                if (managedProfileId != -10000) {
                    deviceOwner = new RestrictedLockUtils.EnforcedAdmin(devicePolicyManager.getProfileOwnerAsUser(managedProfileId), null, UserHandle.of(managedProfileId));
                } else {
                    deviceOwner = new RestrictedLockUtils.EnforcedAdmin(devicePolicyManager.getProfileOwnerAsUser(UserHandle.myUserId()), null, UserHandle.of(UserHandle.myUserId()));
                }
            }
            RestrictedLockUtils.sendShowAdminSupportDetailsIntent(getContext(), deviceOwner);
        } else {
            showDialog(1);
        }
        return true;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        this.mControllers = new ArrayList();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(ConnectivityManager.class);
        setupNetworksDetailTracker();
        WifiEntry wifiEntry = this.mNetworkDetailsTracker.getWifiEntry();
        WifiSecondSummaryController2 wifiSecondSummaryController2 = new WifiSecondSummaryController2(context);
        wifiSecondSummaryController2.setWifiEntry(wifiEntry);
        this.mControllers.add(wifiSecondSummaryController2);
        WifiDetailPreferenceController2 newInstance = WifiDetailPreferenceController2.newInstance(wifiEntry, connectivityManager, context, this, new Handler(Looper.getMainLooper()), getSettingsLifecycle(), (WifiManager) context.getSystemService(WifiManager.class), this.mMetricsFeatureProvider);
        this.mWifiDetailPreferenceController2 = newInstance;
        this.mControllers.add(newInstance);
        WifiAutoConnectPreferenceController2 wifiAutoConnectPreferenceController2 = new WifiAutoConnectPreferenceController2(context);
        wifiAutoConnectPreferenceController2.setWifiEntry(wifiEntry);
        this.mControllers.add(wifiAutoConnectPreferenceController2);
        AddDevicePreferenceController2 addDevicePreferenceController2 = new AddDevicePreferenceController2(context);
        addDevicePreferenceController2.setWifiEntry(wifiEntry);
        this.mControllers.add(addDevicePreferenceController2);
        WifiMeteredPreferenceController2 wifiMeteredPreferenceController2 = new WifiMeteredPreferenceController2(context, wifiEntry);
        this.mControllers.add(wifiMeteredPreferenceController2);
        WifiPrivacyPreferenceController2 wifiPrivacyPreferenceController2 = new WifiPrivacyPreferenceController2(context);
        wifiPrivacyPreferenceController2.setWifiEntry(wifiEntry);
        this.mControllers.add(wifiPrivacyPreferenceController2);
        WifiSubscriptionDetailPreferenceController2 wifiSubscriptionDetailPreferenceController2 = new WifiSubscriptionDetailPreferenceController2(context);
        wifiSubscriptionDetailPreferenceController2.setWifiEntry(wifiEntry);
        this.mControllers.add(wifiSubscriptionDetailPreferenceController2);
        this.mWifiDialogListeners.add(this.mWifiDetailPreferenceController2);
        this.mWifiDialogListeners.add(wifiPrivacyPreferenceController2);
        this.mWifiDialogListeners.add(wifiMeteredPreferenceController2);
        return this.mControllers;
    }

    @Override // com.android.settings.wifi.WifiDialog2.WifiDialog2Listener
    public void onSubmit(WifiDialog2 wifiDialog2) {
        for (WifiDialog2.WifiDialog2Listener wifiDialog2Listener : this.mWifiDialogListeners) {
            wifiDialog2Listener.onSubmit(wifiDialog2);
        }
    }

    /* JADX WARN: Type inference failed for: r8v0, types: [java.time.Clock, com.android.settings.wifi.details2.WifiNetworkDetailsFragment2$1] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setupNetworksDetailTracker() {
        /*
        // Method dump skipped, instructions count: 126
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.wifi.details2.WifiNetworkDetailsFragment2.setupNetworksDetailTracker():void");
    }

    public void refreshPreferences() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        for (AbstractPreferenceController abstractPreferenceController : this.mControllers) {
            if (!(abstractPreferenceController instanceof WifiDetailPreferenceController2)) {
                abstractPreferenceController.displayPreference(preferenceScreen);
            }
        }
    }
}
