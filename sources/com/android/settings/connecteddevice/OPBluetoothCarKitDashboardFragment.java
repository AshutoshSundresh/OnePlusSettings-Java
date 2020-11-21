package com.android.settings.connecteddevice;

import android.content.Context;
import android.os.Bundle;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.actionbar.SearchMenuController;
import com.oneplus.settings.widget.OPFooterPreference;

public class OPBluetoothCarKitDashboardFragment extends DashboardFragment {
    static final String KEY_FOOTER_PREF = "footer_preference";
    OPFooterPreference mFooterPreference;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "OPBluetoothCarKitDashboardFragment";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initPreferencesFromPreferenceScreen();
        Bundle bundle2 = new Bundle();
        bundle2.putBoolean("need_search_icon_in_action_bar", false);
        setArguments(bundle2);
        SearchMenuController.init(this);
    }

    /* access modifiers changed from: package-private */
    public void initPreferencesFromPreferenceScreen() {
        OPFooterPreference oPFooterPreference = (OPFooterPreference) findPreference(KEY_FOOTER_PREF);
        this.mFooterPreference = oPFooterPreference;
        updateFooterPreference(oPFooterPreference);
    }

    /* access modifiers changed from: package-private */
    public void updateFooterPreference(Preference preference) {
        preference.setTitle(C0017R$string.oneplus_add_bluetooth_car_kit_summary);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.op_bluetooth_car_kit;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        ((OPRecognizedBluetoothCarKitsDeviceGroupController) use(OPRecognizedBluetoothCarKitsDeviceGroupController.class)).init(this);
        ((OPOtherPairedBluetoothDevicesGroupController) use(OPOtherPairedBluetoothDevicesGroupController.class)).init(this);
        ((OPRecognizedBluetoothCarKitNoDevicesPreferenceController) use(OPRecognizedBluetoothCarKitNoDevicesPreferenceController.class)).init(this);
    }
}
