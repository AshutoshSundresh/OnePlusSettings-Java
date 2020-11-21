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
import com.android.settings.network.PreferredNetworkModeContentObserver;
import com.android.settings.network.telephony.EnabledNetworkModePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class EnabledNetworkModePreferenceController extends TelephonyBasePreferenceController implements Preference.OnPreferenceChangeListener, LifecycleObserver {
    private static final String LOG_TAG = "EnabledNetworkMode";
    private PreferenceEntriesBuilder mBuilder;
    private CarrierConfigManager mCarrierConfigManager;
    boolean mIsPrimaryCardEnabled = false;
    boolean mIsPrimaryCardLWEnabled = false;
    boolean mIsSubsidyLockFeatureEnabled = false;
    private Preference mPreference;
    private PreferenceScreen mPreferenceScreen;
    private PreferredNetworkModeContentObserver mPreferredNetworkModeObserver;
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

    public EnabledNetworkModePreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.network.telephony.TelephonyAvailabilityCallback, com.android.settings.network.telephony.TelephonyBasePreferenceController
    public int getAvailabilityStatus(int i) {
        boolean z;
        PersistableBundle configForSubId = this.mCarrierConfigManager.getConfigForSubId(i);
        if (i != -1 && configForSubId != null && !configForSubId.getBoolean("hide_carrier_network_settings_bool") && !configForSubId.getBoolean("hide_preferred_network_type_bool") && !configForSubId.getBoolean("world_phone_bool")) {
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
        if (this.mPreferredNetworkModeObserver != null && this.mSubsidySettingsObserver != null) {
            loadPrimaryCardAndSubsidyLockValues();
            this.mPreferredNetworkModeObserver.register(this.mContext, this.mSubId);
            if (this.mIsSubsidyLockFeatureEnabled) {
                this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("subsidy_status"), false, this.mSubsidySettingsObserver);
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        PreferredNetworkModeContentObserver preferredNetworkModeContentObserver = this.mPreferredNetworkModeObserver;
        if (preferredNetworkModeContentObserver != null) {
            preferredNetworkModeContentObserver.unregister(this.mContext);
            if (this.mSubsidySettingsObserver != null) {
                this.mContext.getContentResolver().unregisterContentObserver(this.mSubsidySettingsObserver);
            }
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreferenceScreen = preferenceScreen;
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        ListPreference listPreference = (ListPreference) preference;
        this.mBuilder.setPreferenceEntries();
        this.mBuilder.setPreferenceValueAndSummary();
        listPreference.setEntries(this.mBuilder.getEntries());
        listPreference.setEntryValues(this.mBuilder.getEntryValues());
        listPreference.setValue(Integer.toString(this.mBuilder.getSelectedEntryValue()));
        listPreference.setSummary(this.mBuilder.getSummary());
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        int parseInt = Integer.parseInt((String) obj);
        ListPreference listPreference = (ListPreference) preference;
        if (!this.mTelephonyManager.setPreferredNetworkTypeBitmask(MobileNetworkUtils.getRafFromNetworkType(parseInt))) {
            return false;
        }
        this.mBuilder.setPreferenceValueAndSummary(parseInt);
        listPreference.setValue(Integer.toString(this.mBuilder.getSelectedEntryValue()));
        listPreference.setSummary(this.mBuilder.getSummary());
        return true;
    }

    public void init(Lifecycle lifecycle, int i) {
        this.mSubId = i;
        this.mTelephonyManager = ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).createForSubscriptionId(this.mSubId);
        this.mCarrierConfigManager = (CarrierConfigManager) this.mContext.getSystemService(CarrierConfigManager.class);
        this.mBuilder = new PreferenceEntriesBuilder(this.mContext, this.mSubId);
        if (this.mPreferredNetworkModeObserver == null) {
            PreferredNetworkModeContentObserver preferredNetworkModeContentObserver = new PreferredNetworkModeContentObserver(new Handler(Looper.getMainLooper()));
            this.mPreferredNetworkModeObserver = preferredNetworkModeContentObserver;
            preferredNetworkModeContentObserver.setPreferredNetworkModeChangedListener(new PreferredNetworkModeContentObserver.OnPreferredNetworkModeChangedListener() {
                /* class com.android.settings.network.telephony.$$Lambda$EnabledNetworkModePreferenceController$AqObIXCHqum9ECYzo0tKReVeOJY */

                @Override // com.android.settings.network.PreferredNetworkModeContentObserver.OnPreferredNetworkModeChangedListener
                public final void onPreferredNetworkModeChanged() {
                    EnabledNetworkModePreferenceController.this.lambda$init$0$EnabledNetworkModePreferenceController();
                }
            });
        }
        if (this.mSubsidySettingsObserver == null) {
            this.mSubsidySettingsObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
                /* class com.android.settings.network.telephony.EnabledNetworkModePreferenceController.AnonymousClass1 */

                public void onChange(boolean z) {
                    if (EnabledNetworkModePreferenceController.this.mPreference != null) {
                        if (PrimaryCardAndSubsidyLockUtils.DBG) {
                            Log.d(EnabledNetworkModePreferenceController.LOG_TAG, "mSubsidySettingsObserver#onChange");
                        }
                        EnabledNetworkModePreferenceController enabledNetworkModePreferenceController = EnabledNetworkModePreferenceController.this;
                        enabledNetworkModePreferenceController.updateState(enabledNetworkModePreferenceController.mPreference);
                    }
                }
            };
        }
        lifecycle.addObserver(this);
    }

    /* access modifiers changed from: private */
    /* renamed from: updatePreference */
    public void lambda$init$0() {
        PreferenceScreen preferenceScreen = this.mPreferenceScreen;
        if (preferenceScreen != null) {
            displayPreference(preferenceScreen);
        }
        Preference preference = this.mPreference;
        if (preference != null) {
            updateState(preference);
        }
    }

    /* access modifiers changed from: private */
    public static final class PreferenceEntriesBuilder {
        private boolean mAllowed5gNetworkType;
        private CarrierConfigManager mCarrierConfigManager;
        private Context mContext;
        private List<String> mEntries = new ArrayList();
        private List<Integer> mEntriesValue = new ArrayList();
        private boolean mIs5gEntryDisplayed;
        private boolean mIsGlobalCdma;
        private int mSelectedEntry;
        private boolean mShow4gForLTE;
        private int mSubId;
        private String mSummary;
        private boolean mSupported5gRadioAccessFamily;
        private TelephonyManager mTelephonyManager;

        /* access modifiers changed from: package-private */
        public enum EnabledNetworks {
            ENABLED_NETWORKS_UNKNOWN,
            ENABLED_NETWORKS_CDMA_CHOICES,
            ENABLED_NETWORKS_CDMA_NO_LTE_CHOICES,
            ENABLED_NETWORKS_CDMA_ONLY_LTE_CHOICES,
            ENABLED_NETWORKS_TDSCDMA_CHOICES,
            ENABLED_NETWORKS_EXCEPT_GSM_LTE_CHOICES,
            ENABLED_NETWORKS_EXCEPT_GSM_4G_CHOICES,
            ENABLED_NETWORKS_EXCEPT_GSM_CHOICES,
            ENABLED_NETWORKS_EXCEPT_LTE_CHOICES,
            ENABLED_NETWORKS_4G_CHOICES,
            ENABLED_NETWORKS_CHOICES,
            PREFERRED_NETWORK_MODE_CHOICES_WORLD_MODE
        }

        private static int addNrToLteNetworkType(int i) {
            switch (i) {
                case 8:
                    return 25;
                case 9:
                    return 26;
                case 10:
                    return 27;
                case 11:
                    return 24;
                case 12:
                    return 28;
                case 13:
                case 14:
                case 16:
                case 18:
                case 21:
                default:
                    return i;
                case 15:
                    return 29;
                case 17:
                    return 30;
                case 19:
                    return 31;
                case 20:
                    return 32;
                case 22:
                    return 33;
            }
        }

        private boolean checkSupportedRadioBitmask(long j, long j2) {
            return (j2 & j) > 0;
        }

        PreferenceEntriesBuilder(Context context, int i) {
            this.mContext = context;
            this.mSubId = i;
            this.mCarrierConfigManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
            this.mTelephonyManager = ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).createForSubscriptionId(this.mSubId);
            PersistableBundle configForSubId = this.mCarrierConfigManager.getConfigForSubId(this.mSubId);
            boolean z = true;
            boolean z2 = configForSubId != null && configForSubId.getBoolean("nr_enabled_bool");
            this.mAllowed5gNetworkType = checkSupportedRadioBitmask(this.mTelephonyManager.getAllowedNetworkTypes(), 524288);
            this.mSupported5gRadioAccessFamily = z2 && checkSupportedRadioBitmask(this.mTelephonyManager.getSupportedRadioAccessFamily(), 524288);
            this.mIsGlobalCdma = this.mTelephonyManager.isLteCdmaEvdoGsmWcdmaEnabled() && configForSubId != null && configForSubId.getBoolean("show_cdma_choices_bool");
            this.mShow4gForLTE = (configForSubId == null || !configForSubId.getBoolean("show_4g_for_lte_data_icon_bool")) ? false : z;
        }

        /* access modifiers changed from: package-private */
        public void setPreferenceEntries() {
            clearAllEntries();
            switch (AnonymousClass2.$SwitchMap$com$android$settings$network$telephony$EnabledNetworkModePreferenceController$PreferenceEntriesBuilder$EnabledNetworks[getEnabledNetworkType().ordinal()]) {
                case 1:
                    int[] array = Stream.of((Object[]) this.mContext.getResources().getStringArray(C0003R$array.enabled_networks_cdma_values)).mapToInt($$Lambda$wddj3hVVrg0MkscpMtYt3BzY8Y.INSTANCE).toArray();
                    if (array.length >= 4) {
                        add5gEntry(addNrToLteNetworkType(array[0]));
                        addLteEntry(array[0]);
                        add3gEntry(array[1]);
                        add1xEntry(array[2]);
                        addGlobalEntry(array[3]);
                        return;
                    }
                    throw new IllegalArgumentException("ENABLED_NETWORKS_CDMA_CHOICES index error.");
                case 2:
                    int[] array2 = Stream.of((Object[]) this.mContext.getResources().getStringArray(C0003R$array.enabled_networks_cdma_no_lte_values)).mapToInt($$Lambda$wddj3hVVrg0MkscpMtYt3BzY8Y.INSTANCE).toArray();
                    if (array2.length >= 2) {
                        add3gEntry(array2[0]);
                        add1xEntry(array2[1]);
                        return;
                    }
                    throw new IllegalArgumentException("ENABLED_NETWORKS_CDMA_NO_LTE_CHOICES index error.");
                case 3:
                    int[] array3 = Stream.of((Object[]) this.mContext.getResources().getStringArray(C0003R$array.enabled_networks_cdma_only_lte_values)).mapToInt($$Lambda$wddj3hVVrg0MkscpMtYt3BzY8Y.INSTANCE).toArray();
                    if (array3.length >= 2) {
                        addLteEntry(array3[0]);
                        addGlobalEntry(array3[1]);
                        return;
                    }
                    throw new IllegalArgumentException("ENABLED_NETWORKS_CDMA_ONLY_LTE_CHOICES index error.");
                case 4:
                    int[] array4 = Stream.of((Object[]) this.mContext.getResources().getStringArray(C0003R$array.enabled_networks_tdscdma_values)).mapToInt($$Lambda$wddj3hVVrg0MkscpMtYt3BzY8Y.INSTANCE).toArray();
                    if (array4.length >= 3) {
                        add5gEntry(addNrToLteNetworkType(array4[0]));
                        addLteEntry(array4[0]);
                        add3gEntry(array4[1]);
                        add2gEntry(array4[2]);
                        return;
                    }
                    throw new IllegalArgumentException("ENABLED_NETWORKS_TDSCDMA_CHOICES index error.");
                case 5:
                    int[] array5 = Stream.of((Object[]) this.mContext.getResources().getStringArray(C0003R$array.enabled_networks_except_gsm_lte_values)).mapToInt($$Lambda$wddj3hVVrg0MkscpMtYt3BzY8Y.INSTANCE).toArray();
                    if (array5.length >= 1) {
                        add3gEntry(array5[0]);
                        return;
                    }
                    throw new IllegalArgumentException("ENABLED_NETWORKS_EXCEPT_GSM_LTE_CHOICES index error.");
                case 6:
                    int[] array6 = Stream.of((Object[]) this.mContext.getResources().getStringArray(C0003R$array.enabled_networks_except_gsm_values)).mapToInt($$Lambda$wddj3hVVrg0MkscpMtYt3BzY8Y.INSTANCE).toArray();
                    if (array6.length >= 2) {
                        add5gEntry(addNrToLteNetworkType(array6[0]));
                        add4gEntry(array6[0]);
                        add3gEntry(array6[1]);
                        return;
                    }
                    throw new IllegalArgumentException("ENABLED_NETWORKS_EXCEPT_GSM_4G_CHOICES index error.");
                case 7:
                    int[] array7 = Stream.of((Object[]) this.mContext.getResources().getStringArray(C0003R$array.enabled_networks_except_gsm_values)).mapToInt($$Lambda$wddj3hVVrg0MkscpMtYt3BzY8Y.INSTANCE).toArray();
                    if (array7.length >= 2) {
                        add5gEntry(addNrToLteNetworkType(array7[0]));
                        addLteEntry(array7[0]);
                        add3gEntry(array7[1]);
                        return;
                    }
                    throw new IllegalArgumentException("ENABLED_NETWORKS_EXCEPT_GSM_CHOICES index error.");
                case 8:
                    int[] array8 = Stream.of((Object[]) this.mContext.getResources().getStringArray(C0003R$array.enabled_networks_except_lte_values)).mapToInt($$Lambda$wddj3hVVrg0MkscpMtYt3BzY8Y.INSTANCE).toArray();
                    if (array8.length >= 2) {
                        add3gEntry(array8[0]);
                        add2gEntry(array8[1]);
                        return;
                    }
                    throw new IllegalArgumentException("ENABLED_NETWORKS_EXCEPT_LTE_CHOICES index error.");
                case 9:
                    int[] array9 = Stream.of((Object[]) this.mContext.getResources().getStringArray(C0003R$array.enabled_networks_values)).mapToInt($$Lambda$wddj3hVVrg0MkscpMtYt3BzY8Y.INSTANCE).toArray();
                    if (array9.length >= 3) {
                        add5gEntry(addNrToLteNetworkType(array9[0]));
                        add4gEntry(array9[0]);
                        add3gEntry(array9[1]);
                        add2gEntry(array9[2]);
                        return;
                    }
                    throw new IllegalArgumentException("ENABLED_NETWORKS_4G_CHOICES index error.");
                case 10:
                    int[] array10 = Stream.of((Object[]) this.mContext.getResources().getStringArray(C0003R$array.enabled_networks_values)).mapToInt($$Lambda$wddj3hVVrg0MkscpMtYt3BzY8Y.INSTANCE).toArray();
                    if (array10.length >= 3) {
                        add5gEntry(addNrToLteNetworkType(array10[0]));
                        addLteEntry(array10[0]);
                        add3gEntry(array10[1]);
                        add2gEntry(array10[2]);
                        return;
                    }
                    throw new IllegalArgumentException("ENABLED_NETWORKS_CHOICES index error.");
                case 11:
                    int[] array11 = Stream.of((Object[]) this.mContext.getResources().getStringArray(C0003R$array.preferred_network_mode_values_world_mode)).mapToInt($$Lambda$wddj3hVVrg0MkscpMtYt3BzY8Y.INSTANCE).toArray();
                    if (array11.length >= 3) {
                        addGlobalEntry(array11[0]);
                        addCustomEntry(this.mContext.getString(C0017R$string.network_world_mode_cdma_lte), array11[1]);
                        addCustomEntry(this.mContext.getString(C0017R$string.network_world_mode_gsm_lte), array11[2]);
                        return;
                    }
                    throw new IllegalArgumentException("PREFERRED_NETWORK_MODE_CHOICES_WORLD_MODE index error.");
                default:
                    throw new IllegalArgumentException("Not supported enabled network types.");
            }
        }

        private int getPreferredNetworkMode() {
            ContentResolver contentResolver = this.mContext.getContentResolver();
            return Settings.Global.getInt(contentResolver, "preferred_network_mode" + this.mSubId, TelephonyManager.DEFAULT_PREFERRED_NETWORK_MODE);
        }

        private EnabledNetworks getEnabledNetworkType() {
            EnabledNetworks enabledNetworks = EnabledNetworks.ENABLED_NETWORKS_UNKNOWN;
            int phoneType = this.mTelephonyManager.getPhoneType();
            PersistableBundle configForSubId = this.mCarrierConfigManager.getConfigForSubId(this.mSubId);
            if (phoneType == 2) {
                ContentResolver contentResolver = this.mContext.getContentResolver();
                int i = Settings.Global.getInt(contentResolver, "lte_service_forced" + this.mSubId, 0);
                int preferredNetworkMode = getPreferredNetworkMode();
                if (this.mTelephonyManager.isLteCdmaEvdoGsmWcdmaEnabled()) {
                    if (i != 0) {
                        switch (preferredNetworkMode) {
                            case 4:
                            case 5:
                            case 6:
                                enabledNetworks = EnabledNetworks.ENABLED_NETWORKS_CDMA_NO_LTE_CHOICES;
                                break;
                            case 7:
                            case 8:
                            case 10:
                            case 11:
                                enabledNetworks = EnabledNetworks.ENABLED_NETWORKS_CDMA_ONLY_LTE_CHOICES;
                                break;
                            case 9:
                            default:
                                enabledNetworks = EnabledNetworks.ENABLED_NETWORKS_CDMA_CHOICES;
                                break;
                        }
                    } else {
                        enabledNetworks = EnabledNetworks.ENABLED_NETWORKS_CDMA_CHOICES;
                    }
                }
            } else if (phoneType == 1) {
                enabledNetworks = MobileNetworkUtils.isTdscdmaSupported(this.mContext, this.mSubId) ? EnabledNetworks.ENABLED_NETWORKS_TDSCDMA_CHOICES : (configForSubId == null || configForSubId.getBoolean("prefer_2g_bool") || configForSubId.getBoolean("lte_enabled_bool")) ? (configForSubId == null || configForSubId.getBoolean("prefer_2g_bool")) ? (configForSubId == null || configForSubId.getBoolean("lte_enabled_bool")) ? this.mIsGlobalCdma ? EnabledNetworks.ENABLED_NETWORKS_CDMA_CHOICES : this.mShow4gForLTE ? EnabledNetworks.ENABLED_NETWORKS_4G_CHOICES : EnabledNetworks.ENABLED_NETWORKS_CHOICES : EnabledNetworks.ENABLED_NETWORKS_EXCEPT_LTE_CHOICES : this.mShow4gForLTE ? EnabledNetworks.ENABLED_NETWORKS_EXCEPT_GSM_4G_CHOICES : EnabledNetworks.ENABLED_NETWORKS_EXCEPT_GSM_CHOICES : EnabledNetworks.ENABLED_NETWORKS_EXCEPT_GSM_LTE_CHOICES;
            }
            if (MobileNetworkUtils.isWorldMode(this.mContext, this.mSubId)) {
                enabledNetworks = EnabledNetworks.PREFERRED_NETWORK_MODE_CHOICES_WORLD_MODE;
            }
            Log.d(EnabledNetworkModePreferenceController.LOG_TAG, "enabledNetworkType: " + enabledNetworks);
            return enabledNetworks;
        }

        /* JADX INFO: Can't fix incorrect switch cases order, some code will duplicate */
        /* access modifiers changed from: package-private */
        public void setPreferenceValueAndSummary(int i) {
            setSelectedEntry(i);
            switch (i) {
                case 0:
                case 2:
                case 3:
                    if (!this.mIsGlobalCdma) {
                        setSelectedEntry(0);
                        setSummary(C0017R$string.network_3G);
                        return;
                    }
                    setSelectedEntry(10);
                    setSummary(C0017R$string.network_global);
                    return;
                case 1:
                    if (!this.mIsGlobalCdma) {
                        setSelectedEntry(1);
                        setSummary(C0017R$string.network_2G);
                        return;
                    }
                    setSelectedEntry(10);
                    setSummary(C0017R$string.network_global);
                    return;
                case 4:
                case 6:
                case 7:
                    setSelectedEntry(4);
                    setSummary(C0017R$string.network_3G);
                    return;
                case 5:
                    setSelectedEntry(5);
                    setSummary(C0017R$string.network_1x);
                    return;
                case 8:
                    if (MobileNetworkUtils.isWorldMode(this.mContext, this.mSubId)) {
                        setSummary(C0017R$string.preferred_network_mode_lte_cdma_summary);
                        return;
                    }
                    setSelectedEntry(8);
                    setSummary(is5gEntryDisplayed() ? C0017R$string.network_lte_pure : C0017R$string.network_lte);
                    return;
                case 9:
                    if (MobileNetworkUtils.isWorldMode(this.mContext, this.mSubId)) {
                        setSummary(C0017R$string.preferred_network_mode_lte_gsm_umts_summary);
                        return;
                    }
                    break;
                case 10:
                case 15:
                case 17:
                case 19:
                case 20:
                case 22:
                    if (MobileNetworkUtils.isTdscdmaSupported(this.mContext, this.mSubId)) {
                        setSelectedEntry(22);
                        setSummary(is5gEntryDisplayed() ? C0017R$string.network_lte_pure : C0017R$string.network_lte);
                        return;
                    }
                    setSelectedEntry(10);
                    if (this.mTelephonyManager.getPhoneType() == 2 || this.mIsGlobalCdma || MobileNetworkUtils.isWorldMode(this.mContext, this.mSubId)) {
                        setSummary(C0017R$string.network_global);
                        return;
                    } else if (is5gEntryDisplayed()) {
                        setSummary(this.mShow4gForLTE ? C0017R$string.network_4G_pure : C0017R$string.network_lte_pure);
                        return;
                    } else {
                        setSummary(this.mShow4gForLTE ? C0017R$string.network_4G : C0017R$string.network_lte);
                        return;
                    }
                case 11:
                case 12:
                    break;
                case 13:
                    setSelectedEntry(13);
                    setSummary(C0017R$string.network_3G);
                    return;
                case 14:
                case 16:
                case 18:
                    setSelectedEntry(18);
                    setSummary(C0017R$string.network_3G);
                    return;
                case 21:
                    setSelectedEntry(21);
                    setSummary(C0017R$string.network_3G);
                    return;
                case 23:
                case 24:
                case 26:
                case 28:
                    setSelectedEntry(26);
                    setSummary(this.mContext.getString(C0017R$string.network_5G) + this.mContext.getString(C0017R$string.network_recommended));
                    return;
                case 25:
                    setSelectedEntry(25);
                    setSummary(this.mContext.getString(C0017R$string.network_5G) + this.mContext.getString(C0017R$string.network_recommended));
                    return;
                case 27:
                    setSelectedEntry(27);
                    if (this.mTelephonyManager.getPhoneType() == 2 || this.mIsGlobalCdma || MobileNetworkUtils.isWorldMode(this.mContext, this.mSubId)) {
                        setSummary(C0017R$string.network_global);
                        return;
                    }
                    setSummary(this.mContext.getString(C0017R$string.network_5G) + this.mContext.getString(C0017R$string.network_recommended));
                    return;
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                    setSelectedEntry(33);
                    setSummary(this.mContext.getString(C0017R$string.network_5G) + this.mContext.getString(C0017R$string.network_recommended));
                    return;
                default:
                    setSummary(this.mContext.getString(C0017R$string.mobile_network_mode_error, Integer.valueOf(i)));
                    return;
            }
            if (!this.mIsGlobalCdma) {
                setSelectedEntry(9);
                if (is5gEntryDisplayed()) {
                    setSummary(this.mShow4gForLTE ? C0017R$string.network_4G_pure : C0017R$string.network_lte_pure);
                } else {
                    setSummary(this.mShow4gForLTE ? C0017R$string.network_4G : C0017R$string.network_lte);
                }
            } else {
                setSelectedEntry(10);
                setSummary(C0017R$string.network_global);
            }
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private void setPreferenceValueAndSummary() {
            setPreferenceValueAndSummary(getPreferredNetworkMode());
        }

        private void add5gEntry(int i) {
            boolean z = i >= 23;
            if (!showNrList() || !z) {
                this.mIs5gEntryDisplayed = false;
                Log.d(EnabledNetworkModePreferenceController.LOG_TAG, "Hide 5G option.  supported5GRadioAccessFamily: " + this.mSupported5gRadioAccessFamily + " allowed5GNetworkType: " + this.mAllowed5gNetworkType + " isNRValue: " + z);
                return;
            }
            List<String> list = this.mEntries;
            list.add(this.mContext.getString(C0017R$string.network_5G) + this.mContext.getString(C0017R$string.network_recommended));
            this.mEntriesValue.add(Integer.valueOf(i));
            this.mIs5gEntryDisplayed = true;
        }

        private void addGlobalEntry(int i) {
            Log.d(EnabledNetworkModePreferenceController.LOG_TAG, "addGlobalEntry.  supported5GRadioAccessFamily: " + this.mSupported5gRadioAccessFamily + " allowed5GNetworkType: " + this.mAllowed5gNetworkType);
            this.mEntries.add(this.mContext.getString(C0017R$string.network_global));
            if (showNrList()) {
                i = addNrToLteNetworkType(i);
            }
            this.mEntriesValue.add(Integer.valueOf(i));
        }

        private boolean showNrList() {
            return this.mSupported5gRadioAccessFamily && this.mAllowed5gNetworkType;
        }

        private void addLteEntry(int i) {
            if (showNrList()) {
                this.mEntries.add(this.mContext.getString(C0017R$string.network_lte_pure));
            } else {
                this.mEntries.add(this.mContext.getString(C0017R$string.network_lte));
            }
            this.mEntriesValue.add(Integer.valueOf(i));
        }

        private void add4gEntry(int i) {
            if (showNrList()) {
                this.mEntries.add(this.mContext.getString(C0017R$string.network_4G_pure));
            } else {
                this.mEntries.add(this.mContext.getString(C0017R$string.network_4G));
            }
            this.mEntriesValue.add(Integer.valueOf(i));
        }

        private void add3gEntry(int i) {
            this.mEntries.add(this.mContext.getString(C0017R$string.network_3G));
            this.mEntriesValue.add(Integer.valueOf(i));
        }

        private void add2gEntry(int i) {
            this.mEntries.add(this.mContext.getString(C0017R$string.network_2G));
            this.mEntriesValue.add(Integer.valueOf(i));
        }

        private void add1xEntry(int i) {
            this.mEntries.add(this.mContext.getString(C0017R$string.network_1x));
            this.mEntriesValue.add(Integer.valueOf(i));
        }

        private void addCustomEntry(String str, int i) {
            this.mEntries.add(str);
            this.mEntriesValue.add(Integer.valueOf(i));
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private String[] getEntries() {
            return (String[]) this.mEntries.toArray(new String[0]);
        }

        private void clearAllEntries() {
            this.mEntries.clear();
            this.mEntriesValue.clear();
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private String[] getEntryValues() {
            return (String[]) Arrays.stream((Integer[]) this.mEntriesValue.toArray(new Integer[0])).map($$Lambda$znfQj8LqOvyui6ncUHU4komPIHY.INSTANCE).toArray($$Lambda$EnabledNetworkModePreferenceController$PreferenceEntriesBuilder$BDJvykESOUf9NmyYittPU77YH2s.INSTANCE);
        }

        static /* synthetic */ String[] lambda$getEntryValues$0(int i) {
            return new String[i];
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private int getSelectedEntryValue() {
            return this.mSelectedEntry;
        }

        private void setSelectedEntry(int i) {
            if (this.mEntriesValue.stream().anyMatch(new Predicate(i) {
                /* class com.android.settings.network.telephony.$$Lambda$EnabledNetworkModePreferenceController$PreferenceEntriesBuilder$Lc5oUEWz7rX1T5t2QKJKT4BdgmQ */
                public final /* synthetic */ int f$0;

                {
                    this.f$0 = r1;
                }

                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return EnabledNetworkModePreferenceController.PreferenceEntriesBuilder.lambda$setSelectedEntry$1(this.f$0, (Integer) obj);
                }
            })) {
                this.mSelectedEntry = i;
            } else if (this.mEntriesValue.size() > 0) {
                this.mSelectedEntry = this.mEntriesValue.get(0).intValue();
            } else {
                Log.e(EnabledNetworkModePreferenceController.LOG_TAG, "entriesValue is empty");
            }
        }

        static /* synthetic */ boolean lambda$setSelectedEntry$1(int i, Integer num) {
            return num.intValue() == i;
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private String getSummary() {
            return this.mSummary;
        }

        private void setSummary(int i) {
            setSummary(this.mContext.getString(i));
        }

        private void setSummary(String str) {
            this.mSummary = str;
        }

        private boolean is5gEntryDisplayed() {
            return this.mIs5gEntryDisplayed;
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: com.android.settings.network.telephony.EnabledNetworkModePreferenceController$2  reason: invalid class name */
    public static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$android$settings$network$telephony$EnabledNetworkModePreferenceController$PreferenceEntriesBuilder$EnabledNetworks;

        /* JADX WARNING: Can't wrap try/catch for region: R(22:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|20|(3:21|22|24)) */
        /* JADX WARNING: Can't wrap try/catch for region: R(24:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|20|21|22|24) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x003e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:13:0x0049 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:15:0x0054 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:17:0x0060 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:19:0x006c */
        /* JADX WARNING: Missing exception handler attribute for start block: B:21:0x0078 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0033 */
        static {
            /*
            // Method dump skipped, instructions count: 133
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settings.network.telephony.EnabledNetworkModePreferenceController.AnonymousClass2.<clinit>():void");
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
