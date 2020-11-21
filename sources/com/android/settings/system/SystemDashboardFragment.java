package com.android.settings.system;

import android.content.Context;
import android.os.Bundle;
import android.provider.SearchIndexableResource;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.oneplus.settings.OPPowerOffControlPreferenceController;
import com.oneplus.settings.backup.OPBackupSettingsActivityPreferenceController;
import com.oneplus.settings.controllers.RamBoostPreferenceController;
import com.oneplus.settings.system.OPOTGPreferenceController;
import com.oneplus.settings.utils.OPPreferenceDividerLine;
import com.oneplus.settings.utils.ProductUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SystemDashboardFragment extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.android.settings.system.SystemDashboardFragment.AnonymousClass1 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = C0019R$xml.system_dashboard_fragment;
            return Arrays.asList(searchIndexableResource);
        }
    };

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "SystemDashboardFrag";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 744;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (getVisiblePreferenceCount(preferenceScreen) == preferenceScreen.getInitialExpandedChildrenCount() + 1) {
            preferenceScreen.setInitialExpandedChildrenCount(Integer.MAX_VALUE);
        }
        showRestrictionDialog();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        if (ProductUtils.isUsvMode()) {
            getPreferenceScreen().removePreference(findPreference("oneplus_system_update_settings"));
        }
        return onCreateView;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new OPOTGPreferenceController(context, getSettingsLifecycle()));
        arrayList.add(new RamBoostPreferenceController(context, getSettingsLifecycle()));
        arrayList.add(new OPPreferenceDividerLine(context));
        arrayList.add(new OPPowerOffControlPreferenceController(context, getSettingsLifecycle()));
        arrayList.add(new OPBackupSettingsActivityPreferenceController(context));
        return arrayList;
    }

    public void showRestrictionDialog() {
        Bundle arguments = getArguments();
        if (arguments != null && arguments.getBoolean("show_aware_dialog_disabled", false)) {
            FeatureFactory.getFactory(getContext()).getAwareFeatureProvider().showRestrictionDialog(this);
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.system_dashboard_fragment;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_system_dashboard;
    }

    private int getVisiblePreferenceCount(PreferenceGroup preferenceGroup) {
        int i = 0;
        for (int i2 = 0; i2 < preferenceGroup.getPreferenceCount(); i2++) {
            Preference preference = preferenceGroup.getPreference(i2);
            if (preference instanceof PreferenceGroup) {
                i += getVisiblePreferenceCount((PreferenceGroup) preference);
            } else if (preference.isVisible()) {
                i++;
            }
        }
        return i;
    }
}
