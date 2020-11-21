package com.android.settings.fuelgauge;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.OpFeatures;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.preference.Preference;
import com.android.settings.C0010R$id;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.fuelgauge.batterytip.BatteryTipLoader;
import com.android.settings.fuelgauge.batterytip.BatteryTipPreferenceController;
import com.android.settings.fuelgauge.batterytip.tips.BatteryTip;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.MasterSwitchPreference;
import com.android.settingslib.Utils;
import com.android.settingslib.utils.PowerUtil;
import com.android.settingslib.utils.StringUtil;
import com.android.settingslib.widget.LayoutPreference;
import com.oneplus.settings.chargingstations.OPChargingStationPrefController;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;

public class PowerUsageSummary extends PowerUsageBase implements View.OnLongClickListener, BatteryTipPreferenceController.BatteryTipListener {
    static final int BATTERY_INFO_LOADER = 1;
    static final int BATTERY_TIP_LOADER = 2;
    static final int MENU_ADVANCED_BATTERY = 2;
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.power_usage_summary) {
        /* class com.android.settings.fuelgauge.PowerUsageSummary.AnonymousClass5 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            ArrayList arrayList = new ArrayList();
            if (!OpFeatures.isSupport(new int[]{244}) || OPUtils.isGuestMode()) {
                arrayList.add("wireless_charging_category");
                arrayList.add("reverse_wireless_charging");
                arrayList.add("bed_time_mode_settings");
            }
            return arrayList;
        }
    };
    BatteryHeaderPreferenceController mBatteryHeaderPreferenceController;
    BatteryInfo mBatteryInfo;
    LoaderManager.LoaderCallbacks<List<BatteryInfo>> mBatteryInfoDebugLoaderCallbacks = new LoaderManager.LoaderCallbacks<List<BatteryInfo>>() {
        /* class com.android.settings.fuelgauge.PowerUsageSummary.AnonymousClass3 */

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoaderReset(Loader<List<BatteryInfo>> loader) {
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public Loader<List<BatteryInfo>> onCreateLoader(int i, Bundle bundle) {
            return new DebugEstimatesLoader(PowerUsageSummary.this.getContext(), PowerUsageSummary.this.mStatsHelper);
        }

        public void onLoadFinished(Loader<List<BatteryInfo>> loader, List<BatteryInfo> list) {
            PowerUsageSummary.this.updateViews(list);
        }
    };
    LoaderManager.LoaderCallbacks<BatteryInfo> mBatteryInfoLoaderCallbacks = new LoaderManager.LoaderCallbacks<BatteryInfo>() {
        /* class com.android.settings.fuelgauge.PowerUsageSummary.AnonymousClass2 */

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoaderReset(Loader<BatteryInfo> loader) {
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public Loader<BatteryInfo> onCreateLoader(int i, Bundle bundle) {
            return new BatteryInfoLoader(PowerUsageSummary.this.getContext(), PowerUsageSummary.this.mStatsHelper);
        }

        public void onLoadFinished(Loader<BatteryInfo> loader, BatteryInfo batteryInfo) {
            PowerUsageSummary.this.mBatteryHeaderPreferenceController.updateHeaderPreference(batteryInfo);
            PowerUsageSummary powerUsageSummary = PowerUsageSummary.this;
            powerUsageSummary.mBatteryInfo = batteryInfo;
            powerUsageSummary.updateLastFullChargePreference();
        }
    };
    LayoutPreference mBatteryLayoutPref;
    BatteryTipPreferenceController mBatteryTipPreferenceController;
    private LoaderManager.LoaderCallbacks<List<BatteryTip>> mBatteryTipsCallbacks = new LoaderManager.LoaderCallbacks<List<BatteryTip>>() {
        /* class com.android.settings.fuelgauge.PowerUsageSummary.AnonymousClass4 */

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoaderReset(Loader<List<BatteryTip>> loader) {
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public Loader<List<BatteryTip>> onCreateLoader(int i, Bundle bundle) {
            return new BatteryTipLoader(PowerUsageSummary.this.getContext(), PowerUsageSummary.this.mStatsHelper);
        }

        public void onLoadFinished(Loader<List<BatteryTip>> loader, List<BatteryTip> list) {
            PowerUsageSummary.this.mBatteryTipPreferenceController.updateBatteryTips(list);
        }
    };
    BatteryUtils mBatteryUtils;
    Preference mLastFullChargePref;
    boolean mNeedUpdateBatteryTip;
    PowerUsageFeatureProvider mPowerFeatureProvider;
    Preference mScreenUsagePref;
    final ContentObserver mSettingsObserver = new ContentObserver(new Handler()) {
        /* class com.android.settings.fuelgauge.PowerUsageSummary.AnonymousClass1 */

        public void onChange(boolean z, Uri uri) {
            PowerUsageSummary.this.restartBatteryInfoLoader();
        }
    };

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "PowerUsageSummary";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1263;
    }

    /* access modifiers changed from: protected */
    public void updateViews(List<BatteryInfo> list) {
        BatteryMeterView batteryMeterView = (BatteryMeterView) this.mBatteryLayoutPref.findViewById(C0010R$id.battery_header_icon);
        BatteryInfo batteryInfo = list.get(0);
        ((TextView) this.mBatteryLayoutPref.findViewById(C0010R$id.battery_percent)).setText(Utils.formatPercentage(batteryInfo.batteryLevel));
        String oldEstimateDebugString = this.mPowerFeatureProvider.getOldEstimateDebugString(Formatter.formatShortElapsedTime(getContext(), PowerUtil.convertUsToMs(batteryInfo.remainingTimeUs)));
        String enhancedEstimateDebugString = this.mPowerFeatureProvider.getEnhancedEstimateDebugString(Formatter.formatShortElapsedTime(getContext(), PowerUtil.convertUsToMs(list.get(1).remainingTimeUs)));
        ((TextView) this.mBatteryLayoutPref.findViewById(C0010R$id.summary1)).setText(oldEstimateDebugString + "\n" + enhancedEstimateDebugString);
        batteryMeterView.setBatteryLevel(batteryInfo.batteryLevel);
        batteryMeterView.setCharging(batteryInfo.discharging ^ true);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        SettingsActivity settingsActivity = (SettingsActivity) getActivity();
        BatteryHeaderPreferenceController batteryHeaderPreferenceController = (BatteryHeaderPreferenceController) use(BatteryHeaderPreferenceController.class);
        this.mBatteryHeaderPreferenceController = batteryHeaderPreferenceController;
        batteryHeaderPreferenceController.setActivity(settingsActivity);
        this.mBatteryHeaderPreferenceController.setFragment(this);
        this.mBatteryHeaderPreferenceController.setLifecycle(getSettingsLifecycle());
        BatteryTipPreferenceController batteryTipPreferenceController = (BatteryTipPreferenceController) use(BatteryTipPreferenceController.class);
        this.mBatteryTipPreferenceController = batteryTipPreferenceController;
        batteryTipPreferenceController.setActivity(settingsActivity);
        this.mBatteryTipPreferenceController.setFragment(this);
        this.mBatteryTipPreferenceController.setBatteryTipListener(new BatteryTipPreferenceController.BatteryTipListener() {
            /* class com.android.settings.fuelgauge.$$Lambda$aTvFIqGCrsza8hdemOuQH3mcbRg */

            @Override // com.android.settings.fuelgauge.batterytip.BatteryTipPreferenceController.BatteryTipListener
            public final void onBatteryTipHandled(BatteryTip batteryTip) {
                PowerUsageSummary.this.onBatteryTipHandled(batteryTip);
            }
        });
        ((OPChargingStationPrefController) use(OPChargingStationPrefController.class)).setLifeCycle(getSettingsLifecycle());
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settings.fuelgauge.PowerUsageBase, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setAnimationAllowed(true);
        initFeatureProvider();
        this.mBatteryLayoutPref = (LayoutPreference) findPreference("battery_header");
        this.mScreenUsagePref = findPreference("screen_usage");
        this.mLastFullChargePref = findPreference("last_full_charge");
        this.mBatteryUtils = BatteryUtils.getInstance(getContext());
        restartBatteryInfoLoader();
        this.mBatteryTipPreferenceController.restoreInstanceState(bundle);
        updateBatteryTipFlag(bundle);
        ((TextView) this.mBatteryLayoutPref.findViewById(C0010R$id.btn_show_stats)).setOnClickListener(new View.OnClickListener() {
            /* class com.android.settings.fuelgauge.$$Lambda$PowerUsageSummary$00ln8VbkueS9HRjA2L4UZ9tGr0 */

            public final void onClick(View view) {
                PowerUsageSummary.this.lambda$onCreate$0$PowerUsageSummary(view);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreate$0 */
    public /* synthetic */ void lambda$onCreate$0$PowerUsageSummary(View view) {
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(getContext());
        subSettingLauncher.setDestination(PowerUsageAdvanced.class.getName());
        subSettingLauncher.setSourceMetricsCategory(getMetricsCategory());
        subSettingLauncher.setTitleRes(C0017R$string.advanced_battery_title);
        subSettingLauncher.launch();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        getContentResolver().registerContentObserver(Settings.Global.getUriFor("battery_estimates_last_update_time"), false, this.mSettingsObserver);
        updateChargingStationPref();
    }

    private void updateChargingStationPref() {
        MasterSwitchPreference masterSwitchPreference = (MasterSwitchPreference) findPreference("op_charging_station_setting");
        if (OPUtils.isAppExist(getContext(), "com.oneplus.chargingpilar")) {
            boolean z = false;
            if (Settings.System.getInt(getContentResolver(), "op_charging_stations_feature_on", 0) == 1) {
                z = true;
            }
            masterSwitchPreference.setChecked(z);
            return;
        }
        removePreference("op_charging_station_setting");
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        getContentResolver().unregisterContentObserver(this.mSettingsObserver);
        super.onPause();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.power_usage_summary;
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menu.add(0, 2, 0, C0017R$string.advanced_battery_title);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_battery;
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 2) {
            return super.onOptionsItemSelected(menuItem);
        }
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(getContext());
        subSettingLauncher.setDestination(PowerUsageAdvanced.class.getName());
        subSettingLauncher.setSourceMetricsCategory(getMetricsCategory());
        subSettingLauncher.setTitleRes(C0017R$string.advanced_battery_title);
        subSettingLauncher.launch();
        return true;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.fuelgauge.PowerUsageBase
    public void refreshUi(int i) {
        if (getContext() != null) {
            if (!this.mNeedUpdateBatteryTip || i == 1) {
                this.mNeedUpdateBatteryTip = true;
            } else {
                restartBatteryTipLoader();
            }
            restartBatteryInfoLoader();
            updateLastFullChargePreference();
            this.mScreenUsagePref.setSummary(StringUtil.formatElapsedTime(getContext(), (double) this.mBatteryUtils.calculateScreenUsageTime(this.mStatsHelper), false));
        }
    }

    /* access modifiers changed from: package-private */
    public void restartBatteryTipLoader() {
        getLoaderManager().restartLoader(2, Bundle.EMPTY, this.mBatteryTipsCallbacks);
    }

    /* access modifiers changed from: package-private */
    public void setBatteryLayoutPreference(LayoutPreference layoutPreference) {
        this.mBatteryLayoutPref = layoutPreference;
    }

    /* access modifiers changed from: package-private */
    public void updateLastFullChargePreference() {
        BatteryInfo batteryInfo = this.mBatteryInfo;
        if (batteryInfo == null || batteryInfo.averageTimeToDischarge == -1) {
            long calculateLastFullChargeTime = this.mBatteryUtils.calculateLastFullChargeTime(this.mStatsHelper, System.currentTimeMillis());
            this.mLastFullChargePref.setTitle(C0017R$string.battery_last_full_charge);
            this.mLastFullChargePref.setSummary(StringUtil.formatRelativeTime(getContext(), (double) calculateLastFullChargeTime, false));
            return;
        }
        this.mLastFullChargePref.setTitle(C0017R$string.battery_full_charge_last);
        this.mLastFullChargePref.setSummary(StringUtil.formatElapsedTime(getContext(), (double) this.mBatteryInfo.averageTimeToDischarge, false));
    }

    /* access modifiers changed from: package-private */
    public void showBothEstimates() {
        Context context = getContext();
        if (context != null && this.mPowerFeatureProvider.isEnhancedBatteryPredictionEnabled(context)) {
            getLoaderManager().restartLoader(3, Bundle.EMPTY, this.mBatteryInfoDebugLoaderCallbacks);
        }
    }

    /* access modifiers changed from: package-private */
    public void initFeatureProvider() {
        Context context = getContext();
        this.mPowerFeatureProvider = FeatureFactory.getFactory(context).getPowerUsageFeatureProvider(context);
    }

    /* access modifiers changed from: package-private */
    public void restartBatteryInfoLoader() {
        if (getContext() != null) {
            getLoaderManager().restartLoader(1, Bundle.EMPTY, this.mBatteryInfoLoaderCallbacks);
            if (this.mPowerFeatureProvider.isEstimateDebugEnabled()) {
                this.mBatteryLayoutPref.findViewById(C0010R$id.summary1).setOnLongClickListener(this);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void updateBatteryTipFlag(Bundle bundle) {
        this.mNeedUpdateBatteryTip = bundle == null || this.mBatteryTipPreferenceController.needUpdate();
    }

    public boolean onLongClick(View view) {
        showBothEstimates();
        view.setOnLongClickListener(null);
        return true;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.fuelgauge.PowerUsageBase
    public void restartBatteryStatsLoader(int i) {
        super.lambda$onCreate$0(i);
        this.mBatteryHeaderPreferenceController.quickUpdateHeaderPreference();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        this.mBatteryTipPreferenceController.saveInstanceState(bundle);
    }

    @Override // com.android.settings.fuelgauge.batterytip.BatteryTipPreferenceController.BatteryTipListener
    public void onBatteryTipHandled(BatteryTip batteryTip) {
        restartBatteryTipLoader();
    }
}
