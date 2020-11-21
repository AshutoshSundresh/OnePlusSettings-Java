package com.oneplus.settings.better;

import android.os.Bundle;
import android.telephony.TelephonyManager;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.oneplus.settings.SettingsBaseApplication;

public class OPESportsModeIntroduction extends SettingsPreferenceFragment {
    private Preference mNetworkPre;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.op_esport_mode_introduction);
        initNetworkPreference();
    }

    private void initNetworkPreference() {
        Preference findPreference = findPreference("oneplus_e_sports_mode_network_introduction");
        this.mNetworkPre = findPreference;
        if (findPreference != null) {
            if (!isSupportDualLTEProject() || !isDualSimCard()) {
                this.mNetworkPre.setSummary(getActivity().getString(C0017R$string.oneplus_e_sports_mode_network_introduction_summary));
            } else {
                this.mNetworkPre.setSummary(getActivity().getString(C0017R$string.oneplus_e_sports_mode_network_introduction_dual4g_summary));
            }
        }
    }

    private boolean isDualSimCard() {
        return ((TelephonyManager) getActivity().getSystemService("phone")).getPhoneCount() == 2;
    }

    public static boolean isSupportDualLTEProject() {
        return SettingsBaseApplication.mApplication.getResources().getBoolean(17891433);
    }
}
