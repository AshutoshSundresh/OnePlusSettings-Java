package com.oneplus.settings;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import androidx.preference.Preference;
import com.android.settings.C0019R$xml;
import com.oneplus.settings.ui.OPCustomFingeprintAnimVideoPreference;
import com.oneplus.settings.utils.OPUtils;

public class OPCustomFingerprintAnimSettings extends OPQuitConfirmFragment implements Preference.OnPreferenceClickListener, OnPressListener {
    private Context mContext;
    private OPCustomFingeprintAnimVideoPreference mFingeprintAnimPreference;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "OPCustomFingerprintAnimSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        return false;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        super.onCreate(bundle);
        setOnPressListener(this);
        OPCustomFingeprintAnimVideoPreference oPCustomFingeprintAnimVideoPreference = (OPCustomFingeprintAnimVideoPreference) getPreferenceScreen().findPreference("op_custom_fingerprint_anim");
        this.mFingeprintAnimPreference = oPCustomFingeprintAnimVideoPreference;
        oPCustomFingeprintAnimVideoPreference.setSettingsPreferenceFragment(this);
    }

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = activity;
    }

    @Override // com.oneplus.settings.OnPressListener
    public void onCancelPressed() {
        if (getActivity() != null) {
            finish();
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.oneplus.settings.OPQuitConfirmFragment
    public boolean needShowWarningDialog() {
        OPCustomFingeprintAnimVideoPreference oPCustomFingeprintAnimVideoPreference = this.mFingeprintAnimPreference;
        if (oPCustomFingeprintAnimVideoPreference != null) {
            return oPCustomFingeprintAnimVideoPreference.needShowWarningDialog();
        }
        return false;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStop() {
        super.onStop();
        int intForUser = Settings.System.getIntForUser(this.mContext.getContentResolver(), "op_custom_unlock_animation_style", 0, -2);
        if (intForUser == 0) {
            OPUtils.sendAnalytics("fod_effect", "status", "1");
        } else if (intForUser == 1) {
            OPUtils.sendAnalytics("fod_effect", "status", "2");
        } else if (intForUser == 2) {
            OPUtils.sendAnalytics("fod_effect", "status", OPMemberController.CLIENT_TYPE);
        } else if (intForUser == 9) {
            OPUtils.sendAnalytics("fod_effect", "status", "4");
        } else if (intForUser == 4) {
            OPUtils.sendAnalytics("fod_effect", "status", "5");
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.op_custom_fingerprint_anim_settings;
    }
}
