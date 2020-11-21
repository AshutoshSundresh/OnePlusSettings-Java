package com.android.settings.network.telephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserManager;
import android.provider.SearchIndexableResource;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import com.android.settings.C0010R$id;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.datausage.BillingCyclePreferenceController;
import com.android.settings.datausage.DataUsageSummaryPreferenceController;
import com.android.settings.network.ActiveSubsciptionsListener;
import com.android.settings.network.telephony.cdma.CdmaSubscriptionPreferenceController;
import com.android.settings.network.telephony.cdma.CdmaSystemSelectPreferenceController;
import com.android.settings.network.telephony.gsm.AutoSelectPreferenceController;
import com.android.settings.network.telephony.gsm.OpenNetworkSelectPagePreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.utils.ThreadUtils;
import java.util.Arrays;
import java.util.List;
import org.codeaurora.internal.IExtTelephony;

public class MobileNetworkSettings extends AbstractMobileNetworkSettings {
    static final String KEY_CLICKED_PREF = "key_clicked_pref";
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.mobile_network_settings) {
        /* class com.android.settings.network.telephony.MobileNetworkSettings.AnonymousClass3 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            return super.getXmlResourcesToIndex(context, z);
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return ((UserManager) context.getSystemService(UserManager.class)).isAdminUser();
        }
    };
    private ActiveSubsciptionsListener mActiveSubsciptionsListener;
    private int mActiveSubsciptionsListenerCount;
    private CdmaSubscriptionPreferenceController mCdmaSubscriptionPreferenceController;
    private CdmaSystemSelectPreferenceController mCdmaSystemSelectPreferenceController;
    private String mClickedPrefKey;
    private boolean mDropFirstSubscriptionChangeNotify;
    private int mPhoneId = -1;
    private final BroadcastReceiver mSimStateReceiver = new BroadcastReceiver() {
        /* class com.android.settings.network.telephony.MobileNetworkSettings.AnonymousClass1 */

        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.SIM_STATE_CHANGED".equals(intent.getAction())) {
                String stringExtra = intent.getStringExtra("ss");
                Log.d("NetworkSettings", "Received ACTION_SIM_STATE_CHANGED: " + stringExtra);
                MobileNetworkSettings.this.setScreenState();
            }
        }
    };
    private int mSubId = -1;
    private TelephonyManager mTelephonyManager;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "NetworkSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1571;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setScreenState() {
        int i;
        boolean z = false;
        boolean z2 = this.mTelephonyManager.getSimState() != 1;
        if (z2) {
            try {
                i = IExtTelephony.Stub.asInterface(ServiceManager.getService("qti.radio.extphone")).getCurrentUiccCardProvisioningStatus(this.mPhoneId);
            } catch (RemoteException | NullPointerException e) {
                Log.e("NetworkSettings", "getUiccCardProvisioningStatus: " + this.mPhoneId + ", Exception: ", e);
                i = 0;
            }
            if (i != 0) {
                z = true;
            }
            Log.d("NetworkSettings", "Provisioning Status: " + i + ", screenState: " + z);
            z2 = z;
        }
        Log.d("NetworkSettings", "Setting screen state to: " + z2);
        getPreferenceScreen().setEnabled(z2);
    }

    public MobileNetworkSettings() {
        super("no_config_mobile_networks");
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if (super.onPreferenceTreeClick(preference)) {
            return true;
        }
        String key = preference.getKey();
        if (!TextUtils.equals(key, "cdma_system_select_key") && !TextUtils.equals(key, "cdma_subscription_key")) {
            return false;
        }
        if (this.mTelephonyManager.getEmergencyCallbackMode()) {
            startActivityForResult(new Intent("android.telephony.action.SHOW_NOTICE_ECM_BLOCK_OTHERS", (Uri) null), 17);
            this.mClickedPrefKey = key;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        int i = getArguments().getInt("android.provider.extra.SUB_ID", MobileNetworkUtils.getSearchableSubscriptionId(context));
        this.mSubId = i;
        this.mPhoneId = SubscriptionManager.getPhoneId(i);
        Log.i("NetworkSettings", "display subId: " + this.mSubId + ", phoneId: " + this.mPhoneId);
        if (!SubscriptionManager.isValidSubscriptionId(this.mSubId)) {
            return Arrays.asList(new AbstractPreferenceController[0]);
        }
        return Arrays.asList(new DataUsageSummaryPreferenceController(getActivity(), getSettingsLifecycle(), this, this.mSubId));
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        DataUsageSummaryPreferenceController dataUsageSummaryPreferenceController = (DataUsageSummaryPreferenceController) use(DataUsageSummaryPreferenceController.class);
        if (dataUsageSummaryPreferenceController != null) {
            dataUsageSummaryPreferenceController.init(this.mSubId);
        }
        ((DataDefaultSubscriptionController) use(DataDefaultSubscriptionController.class)).init(getLifecycle());
        ((CallsDefaultSubscriptionController) use(CallsDefaultSubscriptionController.class)).init(getLifecycle());
        ((SmsDefaultSubscriptionController) use(SmsDefaultSubscriptionController.class)).init(getLifecycle());
        ((MobileNetworkSwitchController) use(MobileNetworkSwitchController.class)).init(getLifecycle(), this.mSubId);
        ((CarrierSettingsVersionPreferenceController) use(CarrierSettingsVersionPreferenceController.class)).init(this.mSubId);
        ((BillingCyclePreferenceController) use(BillingCyclePreferenceController.class)).init(this.mSubId);
        ((MmsMessagePreferenceController) use(MmsMessagePreferenceController.class)).init(this.mSubId);
        ((DataDuringCallsPreferenceController) use(DataDuringCallsPreferenceController.class)).init(getLifecycle(), this.mSubId);
        ((DisabledSubscriptionController) use(DisabledSubscriptionController.class)).init(getLifecycle(), this.mSubId);
        ((DeleteSimProfilePreferenceController) use(DeleteSimProfilePreferenceController.class)).init(this.mSubId, this, 18);
        ((DisableSimFooterPreferenceController) use(DisableSimFooterPreferenceController.class)).init(this.mSubId);
        ((NrDisabledInDsdsFooterPreferenceController) use(NrDisabledInDsdsFooterPreferenceController.class)).init(this.mSubId);
        ((MobileDataPreferenceController) use(MobileDataPreferenceController.class)).init(getFragmentManager(), this.mSubId);
        ((RoamingPreferenceController) use(RoamingPreferenceController.class)).init(getFragmentManager(), this.mSubId);
        ((ApnPreferenceController) use(ApnPreferenceController.class)).init(this.mSubId);
        ((UserPLMNPreferenceController) use(UserPLMNPreferenceController.class)).init(this.mSubId);
        ((CarrierPreferenceController) use(CarrierPreferenceController.class)).init(this.mSubId);
        ((DataUsagePreferenceController) use(DataUsagePreferenceController.class)).init(this.mSubId);
        ((PreferredNetworkModePreferenceController) use(PreferredNetworkModePreferenceController.class)).init(getLifecycle(), this.mSubId);
        ((EnabledNetworkModePreferenceController) use(EnabledNetworkModePreferenceController.class)).init(getLifecycle(), this.mSubId);
        ((DataServiceSetupPreferenceController) use(DataServiceSetupPreferenceController.class)).init(this.mSubId);
        WifiCallingPreferenceController init = ((WifiCallingPreferenceController) use(WifiCallingPreferenceController.class)).init(this.mSubId);
        AutoSelectPreferenceController addListener = ((AutoSelectPreferenceController) use(AutoSelectPreferenceController.class)).init(getLifecycle(), this.mSubId).addListener(((OpenNetworkSelectPagePreferenceController) use(OpenNetworkSelectPagePreferenceController.class)).init(getLifecycle(), this.mSubId));
        ((NetworkPreferenceCategoryController) use(NetworkPreferenceCategoryController.class)).init(getLifecycle(), this.mSubId).setChildren(Arrays.asList(addListener));
        CdmaSystemSelectPreferenceController cdmaSystemSelectPreferenceController = (CdmaSystemSelectPreferenceController) use(CdmaSystemSelectPreferenceController.class);
        this.mCdmaSystemSelectPreferenceController = cdmaSystemSelectPreferenceController;
        cdmaSystemSelectPreferenceController.init(getPreferenceManager(), this.mSubId);
        CdmaSubscriptionPreferenceController cdmaSubscriptionPreferenceController = (CdmaSubscriptionPreferenceController) use(CdmaSubscriptionPreferenceController.class);
        this.mCdmaSubscriptionPreferenceController = cdmaSubscriptionPreferenceController;
        cdmaSubscriptionPreferenceController.init(getPreferenceManager(), this.mSubId);
        VideoCallingPreferenceController init2 = ((VideoCallingPreferenceController) use(VideoCallingPreferenceController.class)).init(this.mSubId);
        ((Enabled5GPreferenceController) use(Enabled5GPreferenceController.class)).init(this.mSubId);
        ((CallingPreferenceCategoryController) use(CallingPreferenceCategoryController.class)).setChildren(Arrays.asList(init, init2));
        ((Enhanced4gLtePreferenceController) use(Enhanced4gLtePreferenceController.class)).init(this.mSubId).addListener(init2);
        ((Enhanced4gCallingPreferenceController) use(Enhanced4gCallingPreferenceController.class)).init(this.mSubId).addListener(init2);
        ((Enhanced4gAdvancedCallingPreferenceController) use(Enhanced4gAdvancedCallingPreferenceController.class)).init(this.mSubId).addListener(init2);
        ((ContactDiscoveryPreferenceController) use(ContactDiscoveryPreferenceController.class)).init(getParentFragmentManager(), this.mSubId, getLifecycle());
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        Log.i("NetworkSettings", "onCreate:+");
        TelephonyStatusControlSession telephonyAvailabilityStatus = setTelephonyAvailabilityStatus(getPreferenceControllersAsList());
        super.onCreate(bundle);
        Context context = getContext();
        UserManager userManager = (UserManager) context.getSystemService("user");
        this.mTelephonyManager = ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(this.mSubId);
        telephonyAvailabilityStatus.close();
        onRestoreInstance(bundle);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        Log.i("NetworkSettings", "onResume:+");
        super.onResume();
        if (this.mActiveSubsciptionsListener == null) {
            this.mActiveSubsciptionsListener = new ActiveSubsciptionsListener(getContext().getMainLooper(), getContext(), this.mSubId) {
                /* class com.android.settings.network.telephony.MobileNetworkSettings.AnonymousClass2 */

                @Override // com.android.settings.network.ActiveSubsciptionsListener
                public void onChanged() {
                    MobileNetworkSettings.this.onSubscriptionDetailChanged();
                }
            };
            this.mDropFirstSubscriptionChangeNotify = true;
        }
        this.mActiveSubsciptionsListener.start();
        Context context = getContext();
        if (context != null) {
            context.registerReceiver(this.mSimStateReceiver, new IntentFilter("android.intent.action.SIM_STATE_CHANGED"));
        } else {
            Log.i("NetworkSettings", "context is null, not registering SimStateReceiver");
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void onSubscriptionDetailChanged() {
        if (this.mDropFirstSubscriptionChangeNotify) {
            this.mDropFirstSubscriptionChangeNotify = false;
            Log.d("NetworkSettings", "Callback during onResume()");
            return;
        }
        int i = this.mActiveSubsciptionsListenerCount + 1;
        this.mActiveSubsciptionsListenerCount = i;
        if (i == 1) {
            ThreadUtils.postOnMainThread(new Runnable() {
                /* class com.android.settings.network.telephony.$$Lambda$MobileNetworkSettings$cNxznoXTF4jojYdlEDFtLzIY */

                public final void run() {
                    MobileNetworkSettings.this.lambda$onSubscriptionDetailChanged$0$MobileNetworkSettings();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onSubscriptionDetailChanged$0 */
    public /* synthetic */ void lambda$onSubscriptionDetailChanged$0$MobileNetworkSettings() {
        this.mActiveSubsciptionsListenerCount = 0;
        redrawPreferenceControllers();
    }

    @Override // androidx.fragment.app.Fragment, com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onDestroy() {
        ActiveSubsciptionsListener activeSubsciptionsListener = this.mActiveSubsciptionsListener;
        if (activeSubsciptionsListener != null) {
            activeSubsciptionsListener.stop();
        }
        super.onDestroy();
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        Log.i("NetworkSettings", "onPause:+");
        super.onPause();
        Context context = getContext();
        if (context != null) {
            context.unregisterReceiver(this.mSimStateReceiver);
        } else {
            Log.i("NetworkSettings", "context already null, not unregistering SimStateReceiver");
        }
    }

    /* access modifiers changed from: package-private */
    public void onRestoreInstance(Bundle bundle) {
        if (bundle != null) {
            this.mClickedPrefKey = bundle.getString(KEY_CLICKED_PREF);
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.mobile_network_settings;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString(KEY_CLICKED_PREF, this.mClickedPrefKey);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settings.dashboard.RestrictedDashboardFragment
    public void onActivityResult(int i, int i2, Intent intent) {
        Preference findPreference;
        FragmentActivity activity;
        if (i != 17) {
            if (i == 18 && i2 != 0 && (activity = getActivity()) != null && !activity.isFinishing()) {
                activity.finish();
            }
        } else if (i2 != 0 && (findPreference = getPreferenceScreen().findPreference(this.mClickedPrefKey)) != null) {
            findPreference.performClick();
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        if (SubscriptionManager.isValidSubscriptionId(this.mSubId)) {
            MenuItem add = menu.add(0, C0010R$id.edit_sim_name, 0, C0017R$string.mobile_network_sim_name);
            add.setIcon(17302751);
            add.setShowAsAction(2);
        }
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (!SubscriptionManager.isValidSubscriptionId(this.mSubId) || menuItem.getItemId() != C0010R$id.edit_sim_name) {
            return super.onOptionsItemSelected(menuItem);
        }
        RenameMobileNetworkDialogFragment.newInstance(this.mSubId).show(getFragmentManager(), "RenameMobileNetwork");
        return true;
    }
}
