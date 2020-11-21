package com.android.settings.network;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.network.MobilePlanPreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.MasterSwitchPreference;
import com.android.settings.wifi.WifiMasterSwitchPreferenceController;
import com.android.settings.wifi.tether.TetherDataObserver;
import com.android.settings.wifi.tether.utils.TetherUtils;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.oneplus.settings.controllers.OPRoamingControlPreferenceController;
import com.oneplus.settings.controllers.OPWiFiCallingPreferenceController;
import com.oneplus.settings.utils.OPUtils;
import com.oneplus.sprint.callingplus.interfaces.IWifiCallingService;
import java.util.ArrayList;
import java.util.List;

public class NetworkDashboardFragment extends DashboardFragment implements MobilePlanPreferenceController.MobilePlanPreferenceHost, TetherDataObserver.OnTetherDataChangeCallback, Preference.OnPreferenceChangeListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.network_and_internet) {
        /* class com.android.settings.network.NetworkDashboardFragment.AnonymousClass1 */

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return NetworkDashboardFragment.buildPreferenceControllers(context, null, null, null, null);
        }
    };
    boolean isBindWifiCallingPlusSuccess = false;
    private int lastTetherData = 3;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        /* class com.android.settings.network.NetworkDashboardFragment.AnonymousClass3 */

        public void onReceive(Context context, Intent intent) {
            if ("com.oneplus.sprint.callingplus.ui_refresh".equals(intent.getAction())) {
                NetworkDashboardFragment.this.updateUssWfifiCallingPlus();
            }
        }
    };
    private ServiceConnection mCallingPlusConnection = new ServiceConnection() {
        /* class com.android.settings.network.NetworkDashboardFragment.AnonymousClass2 */

        public void onServiceDisconnected(ComponentName componentName) {
        }

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            NetworkDashboardFragment.this.mIWifiCallingService = IWifiCallingService.Stub.asInterface(iBinder);
            NetworkDashboardFragment.this.updateUssWfifiCallingPlus();
        }
    };
    private MasterSwitchPreference mCallingPlusSwitchPreference;
    private IWifiCallingService mIWifiCallingService;
    private TetherDataObserver mTetherDataObserver;
    private RestrictedPreference mTetherSettings;

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        return 1 == i ? 609 : 0;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "NetworkDashboardFrag";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 746;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public boolean isParalleledControllers() {
        return true;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.network_and_internet;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MultiNetworkHeaderController) use(MultiNetworkHeaderController.class)).init(getSettingsLifecycle());
        ((AirplaneModePreferenceController) use(AirplaneModePreferenceController.class)).setFragment(this);
        getSettingsLifecycle().addObserver((LifecycleObserver) use(AllInOneTetherPreferenceController.class));
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public void onCreatePreferences(Bundle bundle, String str) {
        super.onCreatePreferences(bundle, str);
        ((AllInOneTetherPreferenceController) use(AllInOneTetherPreferenceController.class)).initEnabler(getSettingsLifecycle());
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_network_dashboard;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle(), this.mMetricsFeatureProvider, this, this);
    }

    /* access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Lifecycle lifecycle, MetricsFeatureProvider metricsFeatureProvider, Fragment fragment, MobilePlanPreferenceController.MobilePlanPreferenceHost mobilePlanPreferenceHost) {
        MobilePlanPreferenceController mobilePlanPreferenceController = new MobilePlanPreferenceController(context, mobilePlanPreferenceHost);
        WifiMasterSwitchPreferenceController wifiMasterSwitchPreferenceController = new WifiMasterSwitchPreferenceController(context, metricsFeatureProvider);
        OPSimAndNetworkSettingsPreferenceController oPSimAndNetworkSettingsPreferenceController = new OPSimAndNetworkSettingsPreferenceController(context);
        OPWiFiCallingPreferenceController oPWiFiCallingPreferenceController = new OPWiFiCallingPreferenceController(context);
        OPWifiCallingPlusPreferenceController oPWifiCallingPlusPreferenceController = new OPWifiCallingPlusPreferenceController(context);
        OPRoamingControlPreferenceController oPRoamingControlPreferenceController = new OPRoamingControlPreferenceController(context);
        VpnPreferenceController vpnPreferenceController = new VpnPreferenceController(context);
        PrivateDnsPreferenceController privateDnsPreferenceController = new PrivateDnsPreferenceController(context);
        if (lifecycle != null) {
            lifecycle.addObserver(oPSimAndNetworkSettingsPreferenceController);
            lifecycle.addObserver(oPWiFiCallingPreferenceController);
            lifecycle.addObserver(oPWifiCallingPlusPreferenceController);
            lifecycle.addObserver(oPRoamingControlPreferenceController);
            lifecycle.addObserver(mobilePlanPreferenceController);
            lifecycle.addObserver(wifiMasterSwitchPreferenceController);
            lifecycle.addObserver(vpnPreferenceController);
            lifecycle.addObserver(privateDnsPreferenceController);
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(oPSimAndNetworkSettingsPreferenceController);
        arrayList.add(oPWiFiCallingPreferenceController);
        arrayList.add(oPWifiCallingPlusPreferenceController);
        arrayList.add(oPRoamingControlPreferenceController);
        arrayList.add(new MobileNetworkSummaryController(context, lifecycle));
        arrayList.add(new TetherPreferenceController(context, lifecycle));
        arrayList.add(vpnPreferenceController);
        arrayList.add(new ProxyPreferenceController(context));
        arrayList.add(mobilePlanPreferenceController);
        arrayList.add(wifiMasterSwitchPreferenceController);
        arrayList.add(privateDnsPreferenceController);
        return arrayList;
    }

    @Override // com.android.settings.network.MobilePlanPreferenceController.MobilePlanPreferenceHost
    public void showMobilePlanMessageDialog() {
        showDialog(1);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        Log.d("NetworkDashboardFrag", "onCreateDialog: dialogId=" + i);
        if (i != 1) {
            return super.onCreateDialog(i);
        }
        MobilePlanPreferenceController mobilePlanPreferenceController = (MobilePlanPreferenceController) use(MobilePlanPreferenceController.class);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(mobilePlanPreferenceController.getMobilePlanDialogMessage());
        builder.setCancelable(false);
        builder.setPositiveButton(17039370, new DialogInterface.OnClickListener() {
            /* class com.android.settings.network.$$Lambda$NetworkDashboardFragment$ezC2Ol_SOf4CDiS8HjkkdWzGu_s */

            public final void onClick(DialogInterface dialogInterface, int i) {
                MobilePlanPreferenceController.this.setMobilePlanDialogMessage(null);
            }
        });
        return builder.create();
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStart() {
        super.onStart();
        if (OPUtils.isSupportUss()) {
            updateUssWifiTetheringPreference();
            TetherDataObserver tetherDataObserver = new TetherDataObserver(this);
            this.mTetherDataObserver = tetherDataObserver;
            if (tetherDataObserver != null) {
                getContentResolver().registerContentObserver(Settings.Global.getUriFor("TetheredData"), true, this.mTetherDataObserver);
            }
            MasterSwitchPreference masterSwitchPreference = (MasterSwitchPreference) findPreference(OPWifiCallingPlusPreferenceController.KEY_WIFI_CALLING_PLUS);
            this.mCallingPlusSwitchPreference = masterSwitchPreference;
            if (masterSwitchPreference != null) {
                masterSwitchPreference.setVisible(isWfcOMASupported(getPrefContext()));
            }
            if (this.mCallingPlusConnection != null) {
                Intent intent = new Intent();
                intent.setPackage("com.oneplus.sprint.callingplus");
                intent.setAction("com.oneplus.sprint.callingplus.WifiCallingService");
                this.isBindWifiCallingPlusSuccess = getPrefContext().bindService(intent, this.mCallingPlusConnection, 1);
            }
            if (this.mBroadcastReceiver != null) {
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("com.oneplus.sprint.callingplus.ui_refresh");
                getPrefContext().registerReceiver(this.mBroadcastReceiver, intentFilter);
            }
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStop() {
        super.onStop();
        if (OPUtils.isSupportUss()) {
            if (this.mTetherDataObserver != null) {
                getContentResolver().unregisterContentObserver(this.mTetherDataObserver);
                this.mTetherDataObserver = null;
            }
            if (this.mCallingPlusConnection != null && this.isBindWifiCallingPlusSuccess) {
                getPrefContext().unbindService(this.mCallingPlusConnection);
                this.isBindWifiCallingPlusSuccess = false;
            }
            if (this.mBroadcastReceiver != null) {
                getPrefContext().unregisterReceiver(this.mBroadcastReceiver);
            }
        }
    }

    @Override // com.android.settings.wifi.tether.TetherDataObserver.OnTetherDataChangeCallback
    public void onTetherDataChange() {
        updateUssWifiTetheringPreference();
    }

    private void updateUssWifiTetheringPreference() {
        int tetherData = TetherUtils.getTetherData(getPrefContext());
        if (this.lastTetherData != tetherData) {
            this.lastTetherData = tetherData;
            if (this.mTetherSettings == null) {
                this.mTetherSettings = (RestrictedPreference) findPreference("tether_settings");
            }
            RestrictedPreference restrictedPreference = this.mTetherSettings;
            if (restrictedPreference == null) {
                return;
            }
            if (tetherData == 3 || tetherData == 2) {
                this.mTetherSettings.setVisible(true);
            } else {
                restrictedPreference.setVisible(false);
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateUssWfifiCallingPlus() {
        IWifiCallingService iWifiCallingService;
        if (this.isBindWifiCallingPlusSuccess && (iWifiCallingService = this.mIWifiCallingService) != null) {
            try {
                boolean isWifiCallingSwitchNormal = iWifiCallingService.isWifiCallingSwitchNormal();
                updateUssWifiCallingPlusPreference(isWifiCallingSwitchNormal, 0);
                if (isWifiCallingSwitchNormal) {
                    updateUssWifiCallingPlusPreference(this.mIWifiCallingService.isWifiCallingSwitchChecked(), 1);
                    updateUssWifiCallingPlusPreference(this.mIWifiCallingService.isWifiCallingSwitchEnable(), 2);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateUssWifiCallingPlusPreference(boolean z, int i) {
        if (this.mCallingPlusSwitchPreference == null) {
            this.mCallingPlusSwitchPreference = (MasterSwitchPreference) findPreference(OPWifiCallingPlusPreferenceController.KEY_WIFI_CALLING_PLUS);
        }
        MasterSwitchPreference masterSwitchPreference = this.mCallingPlusSwitchPreference;
        if (masterSwitchPreference != null) {
            masterSwitchPreference.setOnPreferenceChangeListener(this);
            if (i == 0) {
                this.mCallingPlusSwitchPreference.setVisible(z);
            } else if (i == 1) {
                this.mCallingPlusSwitchPreference.setChecked(z);
            } else if (i == 2) {
                this.mCallingPlusSwitchPreference.setEnabled(z);
            }
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        IWifiCallingService iWifiCallingService;
        if (!OPWifiCallingPlusPreferenceController.KEY_WIFI_CALLING_PLUS.equals(preference.getKey()) || (iWifiCallingService = this.mIWifiCallingService) == null) {
            return true;
        }
        try {
            iWifiCallingService.setWifiCallingSwitchState(((Boolean) obj).booleanValue());
            return true;
        } catch (RemoteException e) {
            e.printStackTrace();
            return true;
        }
    }

    public static boolean isWfcOMASupported(Context context) {
        return context != null && Settings.Global.getInt(context.getContentResolver(), "oma_wfc_enable", 0) == 1;
    }
}
