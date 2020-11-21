package com.oneplus.settings.chargingstations;

import android.content.Context;
import android.os.Bundle;
import android.provider.SearchIndexableResource;
import android.util.Log;
import android.view.View;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.location.LocationEnabler;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.SwitchBar;
import com.android.settings.widget.SwitchBarController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.oneplus.settings.utils.OPUtils;
import com.oneplus.settings.widget.OPFooterPreference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class OPChargingStationSettings extends DashboardFragment implements Preference.OnPreferenceChangeListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.oneplus.settings.chargingstations.OPChargingStationSettings.AnonymousClass1 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = C0019R$xml.op_charging_station_settings;
            return Arrays.asList(searchIndexableResource);
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            return new ArrayList();
        }
    };
    private LocationEnabler.LocationModeChangeListener listener = $$Lambda$OPChargingStationSettings$CzXbc1ZhpMClld2i7Uew9d3aTY.INSTANCE;
    private Context mContext;
    private OPFooterPreference mFooterPreference;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "OPChargingStationSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.mContext = getActivity();
        OPFooterPreference createFooterPreference = this.mFooterPreferenceMixin.createFooterPreference();
        this.mFooterPreference = createFooterPreference;
        createFooterPreference.setTitle(C0017R$string.op_charging_footer_text);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        FragmentActivity activity = getActivity();
        SettingsActivity settingsActivity = (SettingsActivity) activity;
        SwitchBar switchBar = settingsActivity.getSwitchBar();
        int i = C0017R$string.op_find_charging_stations;
        switchBar.setSwitchBarText(i, i);
        SwitchPreference switchPreference = (SwitchPreference) findPreference("op_charging_station_mute_notification");
        if (switchPreference != null) {
            switchPreference.setOnPreferenceChangeListener(this);
            updateMuteDescription(switchPreference);
        }
        LifecycleObserver oPChargingStationSettingsController = new OPChargingStationSettingsController(activity, new SwitchBarController(settingsActivity.getSwitchBar()), switchPreference);
        Lifecycle settingsLifecycle = getSettingsLifecycle();
        if (settingsLifecycle != null) {
            settingsLifecycle.addObserver(oPChargingStationSettingsController);
        }
        new LocationEnabler(activity, this.listener, settingsLifecycle);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.op_charging_station_settings;
    }

    private void updateMuteDescription(Preference preference) {
        int intSystemProperty = OPChargingStationUtils.getIntSystemProperty(getActivity(), "op_charging_stations_mute_notification", 0);
        long longSystemProperty = OPChargingStationUtils.getLongSystemProperty(getActivity(), "op_charging_stations_mute_eta", 0);
        if (intSystemProperty == 0 && longSystemProperty == 0) {
            preference.setSummary(getString(C0017R$string.op_charing_station_mute_notifications_description));
            ((SwitchPreference) preference).setChecked(false);
        } else if (longSystemProperty > 0) {
            long abs = Math.abs(System.currentTimeMillis() - longSystemProperty);
            long hours = TimeUnit.MILLISECONDS.toHours(abs);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(abs) - TimeUnit.HOURS.toMinutes(hours);
            preference.setSummary(String.format(getString(C0017R$string.op_charing_stations_mute_dynamic_time), Long.valueOf(hours), Long.valueOf(minutes)));
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (!"op_charging_station_mute_notification".equalsIgnoreCase(preference.getKey())) {
            return false;
        }
        boolean booleanValue = ((Boolean) obj).booleanValue();
        OPChargingStationUtils.putIntSystemProperty(this.mContext, "op_charging_stations_mute_notification", booleanValue ? 1 : 0);
        if (booleanValue) {
            OPChargingStationUtils.putLongSystemProperty(this.mContext, "op_charging_stations_mute_eta", System.currentTimeMillis() + 21600000);
            OPChargingStationUtils.sendBroadcastToApp(this.mContext, "type_mute");
        } else {
            OPChargingStationUtils.putLongSystemProperty(this.mContext, "op_charging_stations_mute_eta", 0);
            OPChargingStationUtils.sendBroadcastToApp(this.mContext, "type_undo");
        }
        OPUtils.sendAnalytics("C22AG9UUDL", "settings_action", "settings_notifications_muted", booleanValue ? "on" : "off");
        updateMuteDescription(preference);
        return true;
    }

    static /* synthetic */ void lambda$new$0(int i, boolean z) {
        Log.d("OPChargingStationSettings", "Location changed" + i + z);
        if (OPChargingStationUtils.getLocationUpdate() != null) {
            OPChargingStationUtils.getLocationUpdate().onOPLocationUpdate();
        }
    }
}
