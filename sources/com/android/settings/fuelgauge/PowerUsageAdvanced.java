package com.android.settings.fuelgauge;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.SearchIndexableResource;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PowerUsageAdvanced extends PowerUsageBase {
    static final int MENU_TOGGLE_APPS = 2;
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.android.settings.fuelgauge.PowerUsageAdvanced.AnonymousClass1 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = C0019R$xml.power_usage_advanced;
            return Arrays.asList(searchIndexableResource);
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(new BatteryAppListPreferenceController(context, "app_list", null, null, null));
            return arrayList;
        }
    };
    private BatteryAppListPreferenceController mBatteryAppListPreferenceController;
    BatteryHistoryPreference mHistPref;
    private PowerUsageFeatureProvider mPowerUsageFeatureProvider;
    boolean mShowAllApps = false;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "AdvancedBatteryUsage";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 51;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.fuelgauge.PowerUsageBase, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Context context = getContext();
        this.mHistPref = (BatteryHistoryPreference) findPreference("battery_graph");
        this.mPowerUsageFeatureProvider = FeatureFactory.getFactory(context).getPowerUsageFeatureProvider(context);
        BatteryUtils.getInstance(context);
        updateHistPrefSummary(context);
        restoreSavedInstance(bundle);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onDestroy() {
        super.onDestroy();
        if (getActivity().isChangingConfigurations()) {
            BatteryEntry.clearUidCache();
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.power_usage_advanced;
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menu.add(0, 2, 0, this.mShowAllApps ? C0017R$string.hide_extra_apps : C0017R$string.show_all_apps);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 2) {
            return super.onOptionsItemSelected(menuItem);
        }
        boolean z = !this.mShowAllApps;
        this.mShowAllApps = z;
        menuItem.setTitle(z ? C0017R$string.hide_extra_apps : C0017R$string.show_all_apps);
        this.mMetricsFeatureProvider.action(getContext(), 852, this.mShowAllApps);
        lambda$onCreate$0(0);
        return true;
    }

    /* access modifiers changed from: package-private */
    public void restoreSavedInstance(Bundle bundle) {
        if (bundle != null) {
            this.mShowAllApps = bundle.getBoolean("show_all_apps", false);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("show_all_apps", this.mShowAllApps);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        BatteryAppListPreferenceController batteryAppListPreferenceController = new BatteryAppListPreferenceController(context, "app_list", getSettingsLifecycle(), (SettingsActivity) getActivity(), this);
        this.mBatteryAppListPreferenceController = batteryAppListPreferenceController;
        arrayList.add(batteryAppListPreferenceController);
        return arrayList;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.fuelgauge.PowerUsageBase
    public void refreshUi(int i) {
        Context context = getContext();
        if (context != null) {
            updatePreference(this.mHistPref);
            updateHistPrefSummary(context);
            this.mBatteryAppListPreferenceController.refreshAppListGroup(this.mStatsHelper, this.mShowAllApps);
        }
    }

    private void updateHistPrefSummary(Context context) {
        boolean z = context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED")).getIntExtra("plugged", -1) != 0;
        if (!this.mPowerUsageFeatureProvider.isEnhancedBatteryPredictionEnabled(context) || z) {
            this.mHistPref.hideBottomSummary();
        } else {
            this.mHistPref.setBottomSummary(this.mPowerUsageFeatureProvider.getAdvancedUsageScreenInfoString());
        }
    }
}
