package com.android.settings.location;

import android.content.Context;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;

public class RecentLocationRequestSeeAllFragment extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.location_recent_requests_see_all);
    private RecentLocationRequestSeeAllPreferenceController mController;
    private MenuItem mHideSystemMenu;
    private boolean mShowSystem = false;
    private MenuItem mShowSystemMenu;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "RecentLocationReqAll";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1325;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        int i = getArguments().getInt("profile");
        RecentLocationRequestSeeAllPreferenceController recentLocationRequestSeeAllPreferenceController = (RecentLocationRequestSeeAllPreferenceController) use(RecentLocationRequestSeeAllPreferenceController.class);
        this.mController = recentLocationRequestSeeAllPreferenceController;
        recentLocationRequestSeeAllPreferenceController.init(this);
        if (i != 0) {
            this.mController.setProfileType(i);
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.location_recent_requests_see_all;
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId != 2 && itemId != 3) {
            return super.onOptionsItemSelected(menuItem);
        }
        this.mShowSystem = menuItem.getItemId() == 2;
        updateMenu();
        RecentLocationRequestSeeAllPreferenceController recentLocationRequestSeeAllPreferenceController = this.mController;
        if (recentLocationRequestSeeAllPreferenceController != null) {
            recentLocationRequestSeeAllPreferenceController.setShowSystem(this.mShowSystem);
        }
        return true;
    }

    private void updateMenu() {
        this.mShowSystemMenu.setVisible(!this.mShowSystem);
        this.mHideSystemMenu.setVisible(this.mShowSystem);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        this.mShowSystemMenu = menu.add(0, 2, 0, C0017R$string.menu_show_system);
        this.mHideSystemMenu = menu.add(0, 3, 0, C0017R$string.menu_hide_system);
        updateMenu();
    }
}
