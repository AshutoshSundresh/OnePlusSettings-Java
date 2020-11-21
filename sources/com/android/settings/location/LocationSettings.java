package com.android.settings.location;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.SwitchBar;
import com.android.settingslib.core.AbstractPreferenceController;
import com.oneplus.settings.OPLegalSettingsControlPreferenceController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LocationSettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.location_settings);

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "LocationSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 63;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        SettingsActivity settingsActivity = (SettingsActivity) getActivity();
        SwitchBar switchBar = settingsActivity.getSwitchBar();
        int i = C0017R$string.location_settings_master_switch_title;
        switchBar.setSwitchBarText(i, i);
        new LocationSwitchBarController(settingsActivity, switchBar, getSettingsLifecycle());
        switchBar.show();
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        ((AppLocationPermissionPreferenceController) use(AppLocationPermissionPreferenceController.class)).init(this);
        ((RecentLocationRequestPreferenceController) use(RecentLocationRequestPreferenceController.class)).init(this);
        ((LocationServicePreferenceController) use(LocationServicePreferenceController.class)).init(this);
        ((LocationFooterPreferenceController) use(LocationFooterPreferenceController.class)).init(this);
        ((LocationForWorkPreferenceController) use(LocationForWorkPreferenceController.class)).init(this);
        ((LocationServiceForWorkPreferenceController) use(LocationServiceForWorkPreferenceController.class)).init(this);
        ((AgpsPreferenceController) use(AgpsPreferenceController.class)).init(this);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new OPLegalSettingsControlPreferenceController(context, "op_location_information", 9));
        return arrayList;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.location_settings;
    }

    static void addPreferencesSorted(List<Preference> list, PreferenceGroup preferenceGroup) {
        Collections.sort(list, Comparator.comparing($$Lambda$LocationSettings$b5ICKITzeuDqJ5adUiGbEMZMKw.INSTANCE));
        for (Preference preference : list) {
            preference.setIcon((Drawable) null);
            preferenceGroup.addPreference(preference);
        }
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_location_access;
    }
}
