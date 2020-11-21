package com.android.settings.datausage;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.applications.ApplicationsState;

public class UnrestrictedDataAccess extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.unrestricted_data_access_settings);
    private ApplicationsState.AppFilter mFilter;
    private boolean mShowSystem;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "UnrestrictedDataAccess";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 349;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mShowSystem = bundle != null && bundle.getBoolean("show_system");
        ((UnrestrictedDataAccessPreferenceController) use(UnrestrictedDataAccessPreferenceController.class)).setParentFragment(this);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menu.add(0, 43, 0, this.mShowSystem ? C0017R$string.menu_hide_system : C0017R$string.menu_show_system);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        ApplicationsState.AppFilter appFilter;
        if (menuItem.getItemId() == 43) {
            boolean z = !this.mShowSystem;
            this.mShowSystem = z;
            menuItem.setTitle(z ? C0017R$string.menu_hide_system : C0017R$string.menu_show_system);
            if (this.mShowSystem) {
                appFilter = ApplicationsState.FILTER_ALL_ENABLED;
            } else {
                appFilter = ApplicationsState.FILTER_DOWNLOADED_AND_LAUNCHER;
            }
            this.mFilter = appFilter;
            ((UnrestrictedDataAccessPreferenceController) use(UnrestrictedDataAccessPreferenceController.class)).setFilter(this.mFilter);
            ((UnrestrictedDataAccessPreferenceController) use(UnrestrictedDataAccessPreferenceController.class)).rebuild();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("show_system", this.mShowSystem);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        ApplicationsState.AppFilter appFilter;
        super.onAttach(context);
        if (this.mShowSystem) {
            appFilter = ApplicationsState.FILTER_ALL_ENABLED;
        } else {
            appFilter = ApplicationsState.FILTER_DOWNLOADED_AND_LAUNCHER;
        }
        this.mFilter = appFilter;
        ((UnrestrictedDataAccessPreferenceController) use(UnrestrictedDataAccessPreferenceController.class)).setSession(getSettingsLifecycle());
        ((UnrestrictedDataAccessPreferenceController) use(UnrestrictedDataAccessPreferenceController.class)).setFilter(this.mFilter);
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_unrestricted_data_access;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.unrestricted_data_access_settings;
    }
}
