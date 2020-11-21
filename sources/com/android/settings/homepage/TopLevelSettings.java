package com.android.settings.homepage;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.android.settings.C0005R$bool;
import com.android.settings.C0019R$xml;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.support.SupportPreferenceController;
import com.android.settingslib.core.instrumentation.Instrumentable;
import com.oneplus.settings.OPOnlineConfigManager;
import com.oneplus.settings.SettingsBaseApplication;

public class TopLevelSettings extends DashboardFragment implements PreferenceFragmentCompat.OnPreferenceStartFragmentCallback, OPOnlineConfigManager.OnSupportConfigCompleteParseListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.top_level_settings) {
        /* class com.android.settings.homepage.TopLevelSettings.AnonymousClass1 */

        /* access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return false;
        }
    };

    @Override // androidx.preference.PreferenceFragmentCompat
    public Fragment getCallbackFragment() {
        return this;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return 0;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "TopLevelSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 35;
    }

    public TopLevelSettings() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("need_search_icon_in_action_bar", false);
        setArguments(bundle);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.top_level_settings;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        ((SupportPreferenceController) use(SupportPreferenceController.class)).setActivity(getActivity());
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        OPOnlineConfigManager.getInstence(SettingsBaseApplication.mApplication).setOnConfigCompleteParseListener(this);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        OPOnlineConfigManager.getInstence(SettingsBaseApplication.mApplication).setOnConfigCompleteParseListener(null);
    }

    @Override // com.oneplus.settings.OPOnlineConfigManager.OnSupportConfigCompleteParseListener
    public void OnSupportConfigParseCompleted() {
        Log.d("TopLevelSettings", "OnSupportConfigParseCompleted-isSupportEnable:" + OPOnlineConfigManager.isSupportEnable());
        Preference findPreference = findPreference(((SupportPreferenceController) use(SupportPreferenceController.class)).getPreferenceKey());
        if (findPreference != null) {
            findPreference.setVisible(OPOnlineConfigManager.isSupportEnable());
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat.OnPreferenceStartFragmentCallback
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat preferenceFragmentCompat, Preference preference) {
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(getActivity());
        subSettingLauncher.setDestination(preference.getFragment());
        subSettingLauncher.setArguments(preference.getExtras());
        subSettingLauncher.setSourceMetricsCategory(preferenceFragmentCompat instanceof Instrumentable ? ((Instrumentable) preferenceFragmentCompat).getMetricsCategory() : 0);
        subSettingLauncher.setTitleRes(-1);
        subSettingLauncher.launch();
        return true;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public boolean shouldForceRoundedIcon() {
        return getContext().getResources().getBoolean(C0005R$bool.config_force_rounded_icon_TopLevelSettings);
    }
}
