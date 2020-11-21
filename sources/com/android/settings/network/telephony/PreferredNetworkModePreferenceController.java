package com.android.settings.network.telephony;

import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0003R$array;
import com.android.settings.C0017R$string;
import com.android.settings.slices.SliceBackgroundWorker;

public class PreferredNetworkModePreferenceController extends TelephonyBasePreferenceController implements Preference.OnPreferenceChangeListener, LifecycleObserver {
    private static final String LOG_TAG = "PreferredNetworkMode";
    private CarrierConfigManager mCarrierConfigManager;
    private boolean mIsGlobalCdma;
    boolean mIsPrimaryCardEnabled = false;
    boolean mIsPrimaryCardLWEnabled = false;
    boolean mIsSubsidyLockFeatureEnabled = false;
    private PersistableBundle mPersistableBundle;
    private Preference mPreference;
    private ContentObserver mPreferredNetworkModeObserver;
    private ContentObserver mSubsidySettingsObserver;
    private TelephonyManager mTelephonyManager;

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public PreferredNetworkModePreferenceController(Context context, String str) {
        super(context, str);
        this.mCarrierConfigManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
        this.mSubsidySettingsObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
            /* class com.android.settings.network.telephony.PreferredNetworkModePreferenceController.AnonymousClass1 */

            public void onChange(boolean z) {
                if (PreferredNetworkModePreferenceController.this.mPreference != null) {
                    if (PrimaryCardAndSubsidyLockUtils.DBG) {
                        Log.d(PreferredNetworkModePreferenceController.LOG_TAG, "mSubsidySettingsObserver#onChange");
                    }
                    PreferredNetworkModePreferenceController preferredNetworkModePreferenceController = PreferredNetworkModePreferenceController.this;
                    preferredNetworkModePreferenceController.updateState(preferredNetworkModePreferenceController.mPreference);
                }
            }
        };
        this.mPreferredNetworkModeObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
            /* class com.android.settings.network.telephony.PreferredNetworkModePreferenceController.AnonymousClass2 */

            public void onChange(boolean z) {
                if (PreferredNetworkModePreferenceController.this.mPreference != null) {
                    Log.d(PreferredNetworkModePreferenceController.LOG_TAG, "mPreferredNetworkModeObserver#onChange");
                    PreferredNetworkModePreferenceController preferredNetworkModePreferenceController = PreferredNetworkModePreferenceController.this;
                    preferredNetworkModePreferenceController.updateState(preferredNetworkModePreferenceController.mPreference);
                }
            }
        };
    }

    @Override // com.android.settings.network.telephony.TelephonyAvailabilityCallback, com.android.settings.network.telephony.TelephonyBasePreferenceController
    public int getAvailabilityStatus(int i) {
        boolean z;
        PersistableBundle configForSubId = this.mCarrierConfigManager.getConfigForSubId(i);
        if (i != -1 && configForSubId != null && !configForSubId.getBoolean("hide_carrier_network_settings_bool") && !configForSubId.getBoolean("hide_preferred_network_type_bool") && configForSubId.getBoolean("world_phone_bool")) {
            z = true;
        } else {
            z = false;
        }
        if (z) {
            return 0;
        }
        return 2;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        loadPrimaryCardAndSubsidyLockValues();
        if (this.mIsSubsidyLockFeatureEnabled) {
            this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("subsidy_status"), false, this.mSubsidySettingsObserver);
        }
        ContentResolver contentResolver = this.mContext.getContentResolver();
        contentResolver.registerContentObserver(Settings.Global.getUriFor("preferred_network_mode" + this.mSubId), true, this.mPreferredNetworkModeObserver);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        if (this.mSubsidySettingsObserver != null) {
            this.mContext.getContentResolver().unregisterContentObserver(this.mSubsidySettingsObserver);
        }
        if (this.mPreferredNetworkModeObserver != null) {
            this.mContext.getContentResolver().unregisterContentObserver(this.mPreferredNetworkModeObserver);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        ListPreference listPreference = (ListPreference) preference;
        int preferredNetworkMode = getPreferredNetworkMode();
        updatePreferenceEntries(listPreference);
        listPreference.setValue(Integer.toString(preferredNetworkMode));
        listPreference.setSummary(getPreferredNetworkModeSummaryResId(preferredNetworkMode));
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        int parseInt = Integer.parseInt((String) obj);
        if (!this.mTelephonyManager.setPreferredNetworkTypeBitmask(MobileNetworkUtils.getRafFromNetworkType(parseInt))) {
            return false;
        }
        ContentResolver contentResolver = this.mContext.getContentResolver();
        Settings.Global.putInt(contentResolver, "preferred_network_mode" + this.mSubId, parseInt);
        ((ListPreference) preference).setSummary(getPreferredNetworkModeSummaryResId(parseInt));
        return true;
    }

    public void init(Lifecycle lifecycle, int i) {
        this.mSubId = i;
        PersistableBundle configForSubId = this.mCarrierConfigManager.getConfigForSubId(i);
        TelephonyManager createForSubscriptionId = ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).createForSubscriptionId(this.mSubId);
        this.mTelephonyManager = createForSubscriptionId;
        this.mIsGlobalCdma = createForSubscriptionId.isLteCdmaEvdoGsmWcdmaEnabled() && configForSubId.getBoolean("show_cdma_choices_bool");
        lifecycle.addObserver(this);
    }

    private int getPreferredNetworkMode() {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        return Settings.Global.getInt(contentResolver, "preferred_network_mode" + this.mSubId, TelephonyManager.DEFAULT_PREFERRED_NETWORK_MODE);
    }

    private int getPreferredNetworkModeSummaryResId(int i) {
        switch (i) {
            case 0:
                return C0017R$string.preferred_network_mode_wcdma_perf_summary;
            case 1:
                return C0017R$string.preferred_network_mode_gsm_only_summary;
            case 2:
                return C0017R$string.preferred_network_mode_wcdma_only_summary;
            case 3:
                return C0017R$string.preferred_network_mode_gsm_wcdma_summary;
            case 4:
                if (this.mTelephonyManager.isLteCdmaEvdoGsmWcdmaEnabled()) {
                    return C0017R$string.preferred_network_mode_cdma_summary;
                }
                return C0017R$string.preferred_network_mode_cdma_evdo_summary;
            case 5:
                return C0017R$string.preferred_network_mode_cdma_only_summary;
            case 6:
                return C0017R$string.preferred_network_mode_evdo_only_summary;
            case 7:
                return C0017R$string.preferred_network_mode_cdma_evdo_gsm_wcdma_summary;
            case 8:
                return C0017R$string.preferred_network_mode_lte_cdma_evdo_summary;
            case 9:
                return C0017R$string.preferred_network_mode_lte_gsm_wcdma_summary;
            case 10:
                if (this.mTelephonyManager.getPhoneType() == 2 || this.mIsGlobalCdma || MobileNetworkUtils.isWorldMode(this.mContext, this.mSubId)) {
                    return C0017R$string.preferred_network_mode_global_summary;
                }
                return C0017R$string.preferred_network_mode_lte_summary;
            case 11:
                return C0017R$string.preferred_network_mode_lte_summary;
            case 12:
                return C0017R$string.preferred_network_mode_lte_wcdma_summary;
            case 13:
                return C0017R$string.preferred_network_mode_tdscdma_summary;
            case 14:
                return C0017R$string.preferred_network_mode_tdscdma_wcdma_summary;
            case 15:
                return C0017R$string.preferred_network_mode_lte_tdscdma_summary;
            case 16:
                return C0017R$string.preferred_network_mode_tdscdma_gsm_summary;
            case 17:
                return C0017R$string.preferred_network_mode_lte_tdscdma_gsm_summary;
            case 18:
                return C0017R$string.preferred_network_mode_tdscdma_gsm_wcdma_summary;
            case 19:
                return C0017R$string.preferred_network_mode_lte_tdscdma_wcdma_summary;
            case 20:
                return C0017R$string.preferred_network_mode_lte_tdscdma_gsm_wcdma_summary;
            case 21:
                return C0017R$string.preferred_network_mode_tdscdma_cdma_evdo_gsm_wcdma_summary;
            case 22:
                return C0017R$string.preferred_network_mode_lte_tdscdma_cdma_evdo_gsm_wcdma_summary;
            case 23:
                return C0017R$string.preferred_network_mode_nr_only_summary;
            case 24:
                return C0017R$string.preferred_network_mode_nr_lte_summary;
            case 25:
                return C0017R$string.preferred_network_mode_nr_lte_cdma_evdo_summary;
            case 26:
                return C0017R$string.preferred_network_mode_nr_lte_gsm_wcdma_summary;
            case 27:
                return C0017R$string.preferred_network_mode_nr_lte_cdma_evdo_gsm_wcdma_summary;
            case 28:
                return C0017R$string.preferred_network_mode_nr_lte_wcdma_summary;
            case 29:
                return C0017R$string.preferred_network_mode_nr_lte_tdscdma_summary;
            case 30:
                return C0017R$string.preferred_network_mode_nr_lte_tdscdma_gsm_summary;
            case 31:
                return C0017R$string.preferred_network_mode_nr_lte_tdscdma_wcdma_summary;
            case 32:
                return C0017R$string.preferred_network_mode_nr_lte_tdscdma_gsm_wcdma_summary;
            case 33:
                return C0017R$string.preferred_network_mode_nr_lte_tdscdma_cdma_evdo_gsm_wcdma_summary;
            default:
                return C0017R$string.preferred_network_mode_global_summary;
        }
    }

    private void updatePreferenceEntries(ListPreference listPreference) {
        listPreference.setEntries(C0003R$array.preferred_network_mode_choices);
        listPreference.setEntryValues(C0003R$array.preferred_network_mode_values);
        int i = Settings.Global.getInt(this.mContext.getContentResolver(), "config_current_primary_sub", -1);
        boolean z = i >= 0 && i < this.mTelephonyManager.getActiveModemCount();
        int phoneId = SubscriptionManager.getPhoneId(this.mSubId);
        Log.d(LOG_TAG, "currentPrimarySlot: " + i + ", isCurrentPrimarySlotValid: " + z + ", currentPhoneId: " + phoneId);
        if (this.mIsPrimaryCardEnabled) {
            if (PrimaryCardAndSubsidyLockUtils.DBG) {
                Log.d(LOG_TAG, "isPrimaryCardEnabled: true");
            }
            if (z && phoneId != i) {
                if (this.mIsPrimaryCardLWEnabled) {
                    Log.d(LOG_TAG, "Primary card LW is enabled");
                    listPreference.setEntries(C0003R$array.preferred_network_mode_gsm_wcdma_choices);
                    listPreference.setEntryValues(C0003R$array.preferred_network_mode_gsm_wcdma_values);
                } else if (getPreferredNetworkMode() == 1) {
                    Log.d(LOG_TAG, "Network mode is GSM only, disabling the preference");
                    listPreference.setEnabled(false);
                }
            }
        }
        if (PrimaryCardAndSubsidyLockUtils.DBG) {
            Log.d(LOG_TAG, "isSubsidyLockFeatureEnabled: " + this.mIsSubsidyLockFeatureEnabled);
            Log.d(LOG_TAG, "isSubsidyUnlocked: " + PrimaryCardAndSubsidyLockUtils.isSubsidyUnlocked(this.mContext));
        }
        if (this.mIsSubsidyLockFeatureEnabled && PrimaryCardAndSubsidyLockUtils.isSubsidyUnlocked(this.mContext)) {
            if (PrimaryCardAndSubsidyLockUtils.DBG) {
                Log.d(LOG_TAG, "Subsidy is unlocked");
            }
            if (!z) {
                return;
            }
            if (phoneId == i) {
                Log.d(LOG_TAG, "Primary sub, change to subsidy choices");
                listPreference.setEntries(C0003R$array.enabled_networks_subsidy_locked_choices);
                listPreference.setEntryValues(C0003R$array.enabled_networks_subsidy_locked_values);
                return;
            }
            Log.d(LOG_TAG, "Non-primary sub, disable the preference");
            listPreference.setEnabled(false);
        }
    }

    private void loadPrimaryCardAndSubsidyLockValues() {
        Log.d(LOG_TAG, "loadPrimaryCardAndSubsidyLockValues");
        this.mIsPrimaryCardEnabled = PrimaryCardAndSubsidyLockUtils.isPrimaryCardEnabled();
        this.mIsPrimaryCardLWEnabled = PrimaryCardAndSubsidyLockUtils.isPrimaryCardLWEnabled();
        this.mIsSubsidyLockFeatureEnabled = PrimaryCardAndSubsidyLockUtils.isSubsidyLockFeatureEnabled();
        if (PrimaryCardAndSubsidyLockUtils.DBG) {
            Log.d(LOG_TAG, "mIsPrimaryCardEnabled: " + this.mIsPrimaryCardEnabled);
            Log.d(LOG_TAG, "mIsPrimaryCardLWEnabled: " + this.mIsPrimaryCardLWEnabled);
            Log.d(LOG_TAG, "mIsSubsidyLockFeatureEnabled: " + this.mIsSubsidyLockFeatureEnabled);
        }
    }
}
