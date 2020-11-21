package com.android.settings.wifi.calling;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import android.telephony.PhoneStateListener;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.ims.ImsMmTelManager;
import android.telephony.ims.ProvisioningManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.C0003R$array;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.network.ims.WifiCallingQueryImsState;
import com.android.settings.widget.SwitchBar;

public class WifiCallingSettingsForSub extends SettingsPreferenceFragment implements SwitchBar.OnSwitchChangeListener, Preference.OnPreferenceChangeListener {
    @VisibleForTesting
    static final int REQUEST_CHECK_WFC_DISCLAIMER = 2;
    @VisibleForTesting
    static final int REQUEST_CHECK_WFC_EMERGENCY_ADDRESS = 1;
    private ListWithEntrySummaryPreference mButtonWfcMode;
    private ListWithEntrySummaryPreference mButtonWfcRoamingMode;
    private boolean mEditableWfcMode = true;
    private boolean mEditableWfcRoamingMode = true;
    private TextView mEmptyView;
    private ImsMmTelManager mImsMmTelManager;
    private IntentFilter mIntentFilter;
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        /* class com.android.settings.wifi.calling.WifiCallingSettingsForSub.AnonymousClass3 */

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.telephony.ims.action.WFC_IMS_REGISTRATION_ERROR")) {
                setResultCode(0);
                WifiCallingSettingsForSub.this.showAlert(intent);
            }
        }
    };
    private final PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        /* class com.android.settings.wifi.calling.WifiCallingSettingsForSub.AnonymousClass1 */

        public void onCallStateChanged(int i, String str) {
            boolean z;
            boolean z2;
            PersistableBundle configForSubId;
            SettingsActivity settingsActivity = (SettingsActivity) WifiCallingSettingsForSub.this.getActivity();
            WifiCallingSettingsForSub wifiCallingSettingsForSub = WifiCallingSettingsForSub.this;
            boolean isAllowUserControl = wifiCallingSettingsForSub.queryImsState(wifiCallingSettingsForSub.mSubId).isAllowUserControl();
            boolean z3 = true;
            boolean z4 = WifiCallingSettingsForSub.this.mSwitchBar.isChecked() && isAllowUserControl;
            WifiCallingSettingsForSub wifiCallingSettingsForSub2 = WifiCallingSettingsForSub.this;
            boolean z5 = wifiCallingSettingsForSub2.getTelephonyManagerForSub(wifiCallingSettingsForSub2.mSubId).getCallState() == 0;
            WifiCallingSettingsForSub.this.mSwitchBar.setEnabled(z5 && isAllowUserControl);
            CarrierConfigManager carrierConfigManager = (CarrierConfigManager) settingsActivity.getSystemService("carrier_config");
            if (carrierConfigManager == null || (configForSubId = carrierConfigManager.getConfigForSubId(WifiCallingSettingsForSub.this.mSubId)) == null) {
                z = true;
                z2 = false;
            } else {
                z = configForSubId.getBoolean("editable_wfc_mode_bool");
                z2 = configForSubId.getBoolean("editable_wfc_roaming_mode_bool");
            }
            Preference findPreference = WifiCallingSettingsForSub.this.getPreferenceScreen().findPreference("wifi_calling_mode");
            if (findPreference != null) {
                findPreference.setEnabled(z4 && z && z5);
            }
            Preference findPreference2 = WifiCallingSettingsForSub.this.getPreferenceScreen().findPreference("wifi_calling_roaming_mode");
            if (findPreference2 != null) {
                if (!z4 || !z2 || !z5) {
                    z3 = false;
                }
                findPreference2.setEnabled(z3);
            }
        }
    };
    private final ProvisioningManager.Callback mProvisioningCallback = new ProvisioningManager.Callback() {
        /* class com.android.settings.wifi.calling.WifiCallingSettingsForSub.AnonymousClass2 */

        public void onProvisioningIntChanged(int i, int i2) {
            if (i == 28 || i == 10) {
                WifiCallingSettingsForSub.this.updateBody();
            }
        }
    };
    private ProvisioningManager mProvisioningManager;
    private int mSubId = -1;
    private Switch mSwitch;
    private SwitchBar mSwitchBar;
    private TelephonyManager mTelephonyManager;
    private Preference mUpdateAddress;
    private final Preference.OnPreferenceClickListener mUpdateAddressListener = new Preference.OnPreferenceClickListener() {
        /* class com.android.settings.wifi.calling.$$Lambda$WifiCallingSettingsForSub$VMCgn5mWUXZKFNEKT_jVJJP6Rc */

        @Override // androidx.preference.Preference.OnPreferenceClickListener
        public final boolean onPreferenceClick(Preference preference) {
            return WifiCallingSettingsForSub.this.lambda$new$0$WifiCallingSettingsForSub(preference);
        }
    };
    private boolean mUseWfcHomeModeForRoaming = false;
    private boolean mValidListener = false;

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return 0;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1230;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ boolean lambda$new$0$WifiCallingSettingsForSub(Preference preference) {
        Intent carrierActivityIntent = getCarrierActivityIntent();
        if (carrierActivityIntent != null) {
            carrierActivityIntent.putExtra("EXTRA_LAUNCH_CARRIER_APP", 1);
            startActivity(carrierActivityIntent);
        }
        return true;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        TextView textView = (TextView) getView().findViewById(16908292);
        this.mEmptyView = textView;
        setEmptyView(textView);
        Resources resourcesForSubId = getResourcesForSubId();
        this.mEmptyView.setText(resourcesForSubId.getString(C0017R$string.wifi_calling_off_explanation, resourcesForSubId.getString(C0017R$string.wifi_calling_off_explanation_2)));
        SwitchBar switchBar = (SwitchBar) getView().findViewById(C0010R$id.switch_bar);
        this.mSwitchBar = switchBar;
        switchBar.show();
        this.mSwitch = this.mSwitchBar.getSwitch();
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        this.mSwitchBar.hide();
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void showAlert(Intent intent) {
        FragmentActivity activity = getActivity();
        CharSequence charSequenceExtra = intent.getCharSequenceExtra("android.telephony.ims.extra.WFC_REGISTRATION_FAILURE_TITLE");
        CharSequence charSequenceExtra2 = intent.getCharSequenceExtra("android.telephony.ims.extra.WFC_REGISTRATION_FAILURE_MESSAGE");
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(charSequenceExtra2);
        builder.setTitle(charSequenceExtra);
        builder.setIcon(17301543);
        builder.setPositiveButton(17039370, (DialogInterface.OnClickListener) null);
        builder.create().show();
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public TelephonyManager getTelephonyManagerForSub(int i) {
        if (this.mTelephonyManager == null) {
            this.mTelephonyManager = (TelephonyManager) getContext().getSystemService(TelephonyManager.class);
        }
        return this.mTelephonyManager.createForSubscriptionId(i);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public WifiCallingQueryImsState queryImsState(int i) {
        return new WifiCallingQueryImsState(getContext(), i);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public ProvisioningManager getImsProvisioningManager() {
        if (!SubscriptionManager.isValidSubscriptionId(this.mSubId)) {
            return null;
        }
        return ProvisioningManager.createForSubscriptionId(this.mSubId);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public ImsMmTelManager getImsMmTelManager() {
        if (!SubscriptionManager.isValidSubscriptionId(this.mSubId)) {
            return null;
        }
        return ImsMmTelManager.createForSubscriptionId(this.mSubId);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.wifi_calling_settings);
        if (getArguments() != null && getArguments().containsKey("subId")) {
            this.mSubId = getArguments().getInt("subId");
        } else if (bundle != null) {
            this.mSubId = bundle.getInt("subId", -1);
        }
        this.mProvisioningManager = getImsProvisioningManager();
        this.mImsMmTelManager = getImsMmTelManager();
        ListWithEntrySummaryPreference listWithEntrySummaryPreference = (ListWithEntrySummaryPreference) findPreference("wifi_calling_mode");
        this.mButtonWfcMode = listWithEntrySummaryPreference;
        listWithEntrySummaryPreference.setOnPreferenceChangeListener(this);
        ListWithEntrySummaryPreference listWithEntrySummaryPreference2 = (ListWithEntrySummaryPreference) findPreference("wifi_calling_roaming_mode");
        this.mButtonWfcRoamingMode = listWithEntrySummaryPreference2;
        listWithEntrySummaryPreference2.setOnPreferenceChangeListener(this);
        Preference findPreference = findPreference("emergency_address_key");
        this.mUpdateAddress = findPreference;
        findPreference.setOnPreferenceClickListener(this.mUpdateAddressListener);
        IntentFilter intentFilter = new IntentFilter();
        this.mIntentFilter = intentFilter;
        intentFilter.addAction("android.telephony.ims.action.WFC_IMS_REGISTRATION_ERROR");
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putInt("subId", this.mSubId);
        super.onSaveInstanceState(bundle);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(C0012R$layout.wifi_calling_settings_preferences, viewGroup, false);
        ViewGroup viewGroup2 = (ViewGroup) inflate.findViewById(C0010R$id.prefs_container);
        Utils.prepareCustomPreferencesList(viewGroup, inflate, viewGroup2, false);
        viewGroup2.addView(super.onCreateView(layoutInflater, viewGroup2, bundle));
        return inflate;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean isWfcProvisionedOnDevice() {
        return queryImsState(this.mSubId).isWifiCallingProvisioned();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateBody() {
        boolean z;
        boolean z2;
        PersistableBundle configForSubId;
        if (!isWfcProvisionedOnDevice()) {
            finish();
            return;
        }
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) getSystemService("carrier_config");
        boolean z3 = true;
        if (carrierConfigManager == null || (configForSubId = carrierConfigManager.getConfigForSubId(this.mSubId)) == null) {
            z = true;
            z2 = false;
        } else {
            this.mEditableWfcMode = configForSubId.getBoolean("editable_wfc_mode_bool");
            this.mEditableWfcRoamingMode = configForSubId.getBoolean("editable_wfc_roaming_mode_bool");
            this.mUseWfcHomeModeForRoaming = configForSubId.getBoolean("use_wfc_home_network_mode_in_roaming_network_bool", false);
            z = configForSubId.getBoolean("carrier_wfc_supports_wifi_only_bool", true);
            z2 = configForSubId.getBoolean("carrier_wfc_supports_ims_preferred_bool", false);
        }
        Resources resourcesForSubId = getResourcesForSubId();
        this.mButtonWfcMode.setTitle(resourcesForSubId.getString(C0017R$string.wifi_calling_mode_title));
        this.mButtonWfcMode.setDialogTitle(resourcesForSubId.getString(C0017R$string.wifi_calling_mode_dialog_title));
        this.mButtonWfcRoamingMode.setTitle(resourcesForSubId.getString(C0017R$string.wifi_calling_roaming_mode_title));
        this.mButtonWfcRoamingMode.setDialogTitle(resourcesForSubId.getString(C0017R$string.wifi_calling_roaming_mode_dialog_title));
        Log.d("WifiCallingForSub", "isWifiOnlySupported = " + z + " isImsPreferredSupported = " + z2);
        if (z) {
            if (z2) {
                this.mButtonWfcMode.setEntries(resourcesForSubId.getStringArray(C0003R$array.wifi_calling_mode_choices_with_ims_preferred));
                this.mButtonWfcMode.setEntryValues(resourcesForSubId.getStringArray(C0003R$array.wifi_calling_mode_values_with_ims_preferred));
                this.mButtonWfcMode.setEntrySummaries(resourcesForSubId.getStringArray(C0003R$array.wifi_calling_mode_summaries_with_ims_preferred));
                this.mButtonWfcRoamingMode.setEntries(resourcesForSubId.getStringArray(C0003R$array.wifi_calling_mode_choices_v2_with_ims_preferred));
                this.mButtonWfcRoamingMode.setEntryValues(resourcesForSubId.getStringArray(C0003R$array.wifi_calling_mode_values_with_ims_preferred));
                this.mButtonWfcRoamingMode.setEntrySummaries(resourcesForSubId.getStringArray(C0003R$array.wifi_calling_mode_summaries_with_ims_preferred));
            } else {
                this.mButtonWfcMode.setEntries(resourcesForSubId.getStringArray(C0003R$array.wifi_calling_mode_choices));
                this.mButtonWfcMode.setEntryValues(resourcesForSubId.getStringArray(C0003R$array.wifi_calling_mode_values));
                this.mButtonWfcMode.setEntrySummaries(resourcesForSubId.getStringArray(C0003R$array.wifi_calling_mode_summaries));
                this.mButtonWfcRoamingMode.setEntries(resourcesForSubId.getStringArray(C0003R$array.wifi_calling_mode_choices_v2));
                this.mButtonWfcRoamingMode.setEntryValues(resourcesForSubId.getStringArray(C0003R$array.wifi_calling_mode_values));
                this.mButtonWfcRoamingMode.setEntrySummaries(resourcesForSubId.getStringArray(C0003R$array.wifi_calling_mode_summaries));
            }
        } else if (z2) {
            this.mButtonWfcMode.setEntries(resourcesForSubId.getStringArray(C0003R$array.wifi_calling_mode_choices_without_wifi_only_with_ims_preferred));
            this.mButtonWfcMode.setEntryValues(resourcesForSubId.getStringArray(C0003R$array.wifi_calling_mode_values_without_wifi_only_with_ims_preferred));
            this.mButtonWfcMode.setEntrySummaries(resourcesForSubId.getStringArray(C0003R$array.wifi_calling_mode_summaries_without_wifi_only_with_ims_preferred));
            this.mButtonWfcRoamingMode.setEntries(resourcesForSubId.getStringArray(C0003R$array.wifi_calling_mode_choices_v2_without_wifi_only_with_ims_preferred));
            this.mButtonWfcRoamingMode.setEntryValues(resourcesForSubId.getStringArray(C0003R$array.wifi_calling_mode_values_without_wifi_only_with_ims_preferred));
            this.mButtonWfcRoamingMode.setEntrySummaries(resourcesForSubId.getStringArray(C0003R$array.wifi_calling_mode_summaries_without_wifi_only_with_ims_preferred));
        } else {
            this.mButtonWfcMode.setEntries(resourcesForSubId.getStringArray(C0003R$array.wifi_calling_mode_choices_without_wifi_only));
            this.mButtonWfcMode.setEntryValues(resourcesForSubId.getStringArray(C0003R$array.wifi_calling_mode_values_without_wifi_only));
            this.mButtonWfcMode.setEntrySummaries(resourcesForSubId.getStringArray(C0003R$array.wifi_calling_mode_summaries_without_wifi_only));
            this.mButtonWfcRoamingMode.setEntries(resourcesForSubId.getStringArray(C0003R$array.wifi_calling_mode_choices_v2_without_wifi_only));
            this.mButtonWfcRoamingMode.setEntryValues(resourcesForSubId.getStringArray(C0003R$array.wifi_calling_mode_values_without_wifi_only));
            this.mButtonWfcRoamingMode.setEntrySummaries(resourcesForSubId.getStringArray(C0003R$array.wifi_calling_mode_summaries_without_wifi_only));
        }
        WifiCallingQueryImsState queryImsState = queryImsState(this.mSubId);
        if (!queryImsState.isEnabledByUser() || !queryImsState.isAllowUserControl()) {
            z3 = false;
        }
        this.mSwitch.setChecked(z3);
        int voWiFiModeSetting = this.mImsMmTelManager.getVoWiFiModeSetting();
        int voWiFiRoamingModeSetting = this.mImsMmTelManager.getVoWiFiRoamingModeSetting();
        this.mButtonWfcMode.setValue(Integer.toString(voWiFiModeSetting));
        this.mButtonWfcRoamingMode.setValue(Integer.toString(voWiFiRoamingModeSetting));
        updateButtonWfcMode(z3, voWiFiModeSetting, voWiFiRoamingModeSetting);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        updateBody();
        if (queryImsState(this.mSubId).isWifiCallingSupported()) {
            getTelephonyManagerForSub(this.mSubId).listen(this.mPhoneStateListener, 32);
            this.mSwitchBar.addOnSwitchChangeListener(this);
            this.mValidListener = true;
        }
        getActivity().registerReceiver(this.mIntentReceiver, this.mIntentFilter);
        Intent intent = getActivity().getIntent();
        if (intent.getBooleanExtra("alertShow", false)) {
            showAlert(intent);
        }
        registerProvisioningChangedCallback();
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        FragmentActivity activity = getActivity();
        if (this.mValidListener) {
            this.mValidListener = false;
            getTelephonyManagerForSub(this.mSubId).listen(this.mPhoneStateListener, 0);
            this.mSwitchBar.removeOnSwitchChangeListener(this);
        }
        activity.unregisterReceiver(this.mIntentReceiver);
        unregisterProvisioningChangedCallback();
    }

    @Override // com.android.settings.widget.SwitchBar.OnSwitchChangeListener
    public void onSwitchChanged(Switch r3, boolean z) {
        Log.d("WifiCallingForSub", "onSwitchChanged(" + z + ")");
        if (!z) {
            updateWfcMode(false);
            return;
        }
        FragmentActivity activity = getActivity();
        Bundle bundle = new Bundle();
        bundle.putInt("EXTRA_SUB_ID", this.mSubId);
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(activity);
        subSettingLauncher.setDestination(WifiCallingDisclaimerFragment.class.getName());
        subSettingLauncher.setArguments(bundle);
        subSettingLauncher.setTitleRes(C0017R$string.wifi_calling_settings_title);
        subSettingLauncher.setSourceMetricsCategory(getMetricsCategory());
        subSettingLauncher.setResultListener(this, 2);
        subSettingLauncher.launch();
    }

    private Intent getCarrierActivityIntent() {
        PersistableBundle configForSubId;
        ComponentName unflattenFromString;
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) getActivity().getSystemService(CarrierConfigManager.class);
        if (carrierConfigManager == null || (configForSubId = carrierConfigManager.getConfigForSubId(this.mSubId)) == null) {
            return null;
        }
        String string = configForSubId.getString("wfc_emergency_address_carrier_app_string");
        if (TextUtils.isEmpty(string) || (unflattenFromString = ComponentName.unflattenFromString(string)) == null) {
            return null;
        }
        Intent intent = new Intent();
        intent.setComponent(unflattenFromString);
        intent.putExtra("android.telephony.extra.SUBSCRIPTION_INDEX", this.mSubId);
        return intent;
    }

    private void updateWfcMode(boolean z) {
        Log.i("WifiCallingForSub", "updateWfcMode(" + z + ")");
        this.mImsMmTelManager.setVoWiFiSettingEnabled(z);
        int voWiFiModeSetting = this.mImsMmTelManager.getVoWiFiModeSetting();
        updateButtonWfcMode(z, voWiFiModeSetting, this.mImsMmTelManager.getVoWiFiRoamingModeSetting());
        if (z) {
            this.mMetricsFeatureProvider.action(getActivity(), getMetricsCategory(), voWiFiModeSetting);
        } else {
            this.mMetricsFeatureProvider.action(getActivity(), getMetricsCategory(), -1);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        Log.d("WifiCallingForSub", "WFC activity request = " + i + " result = " + i2);
        if (i != 1) {
            if (i != 2) {
                Log.e("WifiCallingForSub", "Unexpected request: " + i);
            } else if (i2 == -1) {
                Intent carrierActivityIntent = getCarrierActivityIntent();
                if (carrierActivityIntent != null) {
                    carrierActivityIntent.putExtra("EXTRA_LAUNCH_CARRIER_APP", 0);
                    startActivityForResult(carrierActivityIntent, 1);
                    return;
                }
                updateWfcMode(true);
            }
        } else if (i2 == -1) {
            updateWfcMode(true);
        }
    }

    private void updateButtonWfcMode(boolean z, int i, int i2) {
        this.mButtonWfcMode.setSummary(getWfcModeSummary(i));
        boolean z2 = true;
        this.mButtonWfcMode.setEnabled(z && this.mEditableWfcMode);
        this.mButtonWfcRoamingMode.setEnabled(z && this.mEditableWfcRoamingMode);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (getCarrierActivityIntent() == null) {
            z2 = false;
        }
        if (z) {
            if (this.mEditableWfcMode) {
                preferenceScreen.addPreference(this.mButtonWfcMode);
            } else {
                preferenceScreen.removePreference(this.mButtonWfcMode);
            }
            if (!this.mEditableWfcRoamingMode || this.mUseWfcHomeModeForRoaming) {
                preferenceScreen.removePreference(this.mButtonWfcRoamingMode);
            } else {
                preferenceScreen.addPreference(this.mButtonWfcRoamingMode);
            }
            if (z2) {
                preferenceScreen.addPreference(this.mUpdateAddress);
            } else {
                preferenceScreen.removePreference(this.mUpdateAddress);
            }
        } else {
            preferenceScreen.removePreference(this.mButtonWfcMode);
            preferenceScreen.removePreference(this.mButtonWfcRoamingMode);
            preferenceScreen.removePreference(this.mUpdateAddress);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference == this.mButtonWfcMode) {
            Log.d("WifiCallingForSub", "onPreferenceChange mButtonWfcMode " + obj);
            String str = (String) obj;
            this.mButtonWfcMode.setValue(str);
            int intValue = Integer.valueOf(str).intValue();
            if (intValue == this.mImsMmTelManager.getVoWiFiModeSetting()) {
                return true;
            }
            this.mImsMmTelManager.setVoWiFiModeSetting(intValue);
            this.mButtonWfcMode.setSummary(getWfcModeSummary(intValue));
            this.mMetricsFeatureProvider.action(getActivity(), getMetricsCategory(), intValue);
            if (!this.mUseWfcHomeModeForRoaming) {
                return true;
            }
            this.mImsMmTelManager.setVoWiFiRoamingModeSetting(intValue);
            return true;
        }
        ListWithEntrySummaryPreference listWithEntrySummaryPreference = this.mButtonWfcRoamingMode;
        if (preference != listWithEntrySummaryPreference) {
            return true;
        }
        String str2 = (String) obj;
        listWithEntrySummaryPreference.setValue(str2);
        int intValue2 = Integer.valueOf(str2).intValue();
        if (intValue2 == this.mImsMmTelManager.getVoWiFiRoamingModeSetting()) {
            return true;
        }
        this.mImsMmTelManager.setVoWiFiRoamingModeSetting(intValue2);
        this.mMetricsFeatureProvider.action(getActivity(), getMetricsCategory(), intValue2);
        return true;
    }

    private CharSequence getWfcModeSummary(int i) {
        int i2;
        if (queryImsState(this.mSubId).isEnabledByUser()) {
            if (i == 0) {
                i2 = 17041477;
            } else if (i == 1) {
                i2 = 17041475;
            } else if (i == 2) {
                i2 = 17041478;
            } else if (i != 10) {
                Log.e("WifiCallingForSub", "Unexpected WFC mode value: " + i);
            } else {
                i2 = 17041476;
            }
            return getResourcesForSubId().getString(i2);
        }
        i2 = 17041508;
        return getResourcesForSubId().getString(i2);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public Resources getResourcesForSubId() {
        return SubscriptionManager.getResourcesForSubId(getContext(), this.mSubId);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void registerProvisioningChangedCallback() {
        ProvisioningManager provisioningManager = this.mProvisioningManager;
        if (provisioningManager != null) {
            try {
                provisioningManager.registerProvisioningChangedCallback(getContext().getMainExecutor(), this.mProvisioningCallback);
            } catch (Exception unused) {
                Log.w("WifiCallingForSub", "onResume: Unable to register callback for provisioning changes.");
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void unregisterProvisioningChangedCallback() {
        ProvisioningManager provisioningManager = this.mProvisioningManager;
        if (provisioningManager != null) {
            provisioningManager.unregisterProvisioningChangedCallback(this.mProvisioningCallback);
        }
    }
}
