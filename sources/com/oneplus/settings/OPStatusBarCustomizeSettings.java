package com.oneplus.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.Indexable$SearchIndexProvider;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;

public class OPStatusBarCustomizeSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    public static final Indexable$SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new StatusBarCustomizeIndexProvider();
    private ListPreference mBatteryStylePreference;
    private Context mContext;
    private SwitchPreference mShowBatteryPercentPreference;
    private Preference mStatusBarIconMangerPreference;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.op_statusbar_customize_settings);
        this.mContext = getActivity();
        this.mBatteryStylePreference = (ListPreference) findPreference("battery_style");
        boolean z = false;
        int i = Settings.System.getInt(this.mContext.getContentResolver(), "status_bar_battery_style", 0);
        this.mBatteryStylePreference.setValue(String.valueOf(i));
        updateBatteryStylePreferenceDescription(i);
        this.mBatteryStylePreference.setOnPreferenceChangeListener(this);
        this.mShowBatteryPercentPreference = (SwitchPreference) findPreference("enable_show_statusbar");
        ListPreference listPreference = (ListPreference) findPreference("clock");
        Preference findPreference = findPreference("status_bar_icon_manager");
        this.mStatusBarIconMangerPreference = findPreference;
        findPreference.setOnPreferenceClickListener(this);
        int i2 = Settings.System.getInt(this.mContext.getContentResolver(), "status_bar_show_battery_percent", 0);
        SwitchPreference switchPreference = this.mShowBatteryPercentPreference;
        if (i2 == 1) {
            z = true;
        }
        switchPreference.setChecked(z);
        this.mShowBatteryPercentPreference.setOnPreferenceChangeListener(this);
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("show_power_percent_in_statusbar_title") && !intent.getBooleanExtra("show_power_percent_in_statusbar_title", true)) {
            getPreferenceScreen().removePreference(this.mShowBatteryPercentPreference);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        String key = preference.getKey();
        if ("battery_style".equals(key)) {
            int parseInt = Integer.parseInt((String) obj);
            Settings.System.putInt(this.mContext.getContentResolver(), "status_bar_battery_style", parseInt);
            updateBatteryStylePreferenceDescription(parseInt);
            return true;
        }
        if ("enable_show_statusbar".equals(key)) {
            Settings.System.putInt(this.mContext.getContentResolver(), "status_bar_show_battery_percent", ((Boolean) obj).booleanValue() ? 1 : 0);
        }
        return true;
    }

    private void updateBatteryStylePreferenceDescription(int i) {
        ListPreference listPreference = this.mBatteryStylePreference;
        if (listPreference != null) {
            if (i >= listPreference.getEntries().length) {
                i = this.mBatteryStylePreference.getEntries().length - 1;
            }
            this.mBatteryStylePreference.setSummary(this.mBatteryStylePreference.getEntries()[i]);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        if (!"status_bar_icon_manager".equals(preference.getKey())) {
            return false;
        }
        OPUtils.startFragment(this.mContext, OPStatusBarCustomizeIconSettings.class.getName(), getMetricsCategory());
        return true;
    }

    private static class StatusBarCustomizeIndexProvider extends BaseSearchIndexProvider {
        boolean mIsPrimary;

        public StatusBarCustomizeIndexProvider() {
            this.mIsPrimary = UserHandle.myUserId() == 0;
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            ArrayList arrayList = new ArrayList();
            if (!this.mIsPrimary) {
                return arrayList;
            }
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = C0019R$xml.op_statusbar_customize_settings;
            arrayList.add(searchIndexableResource);
            return arrayList;
        }
    }
}
