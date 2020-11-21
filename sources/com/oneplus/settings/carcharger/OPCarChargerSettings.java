package com.oneplus.settings.carcharger;

import android.content.Context;
import android.os.Bundle;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settingslib.core.AbstractPreferenceController;
import com.oneplus.settings.utils.OPUtils;
import com.oneplus.settings.widget.OPFooterPreference;
import java.util.ArrayList;
import java.util.List;

public class OPCarChargerSettings extends DashboardFragment {
    static final String KEY_FOOTER_PREF = "footer_preference";
    OPFooterPreference mFooterPreference;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "OPCarChargerSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initPreferencesFromPreferenceScreen();
    }

    /* access modifiers changed from: package-private */
    public void initPreferencesFromPreferenceScreen() {
        OPFooterPreference oPFooterPreference = (OPFooterPreference) findPreference(KEY_FOOTER_PREF);
        this.mFooterPreference = oPFooterPreference;
        updateFooterPreference(oPFooterPreference);
    }

    /* access modifiers changed from: package-private */
    public void updateFooterPreference(Preference preference) {
        if (OPUtils.isO2()) {
            preference.setTitle(getString(C0017R$string.oneplus_auto_turn_on_car_charger_info_o2));
        } else {
            preference.setTitle(getString(C0017R$string.oneplus_auto_turn_on_car_charger_info_h2));
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new OPCarChargerPreferenceController(context, "car_charger_auto_turn_on"));
        arrayList.add(new OPCarChargerPreferenceController(context, "car_charger_auto_turn_on_dnd"));
        arrayList.add(new OPCarChargerAutoOpenSpecifiedAppPreferenceController(context));
        return arrayList;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.op_car_charger_settings;
    }
}
