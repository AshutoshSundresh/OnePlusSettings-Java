package com.oneplus.settings;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import androidx.preference.Preference;
import com.android.settings.C0019R$xml;
import com.oneplus.settings.ui.OPCustomNotificationAnimVideoPreference;
import com.oneplus.settings.utils.OPUtils;

public class OPCustomNotificationAnimSettings extends OPQuitConfirmFragment implements Preference.OnPreferenceClickListener, OnPressListener {
    private Context mContext;
    private OPCustomNotificationAnimVideoPreference mNotificaitonAnimPreference;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "OPCustomNotificationAnimSettings";
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
        setOnPressListener(this);
        OPCustomNotificationAnimVideoPreference oPCustomNotificationAnimVideoPreference = (OPCustomNotificationAnimVideoPreference) getPreferenceScreen().findPreference("op_custom_notification_anim");
        this.mNotificaitonAnimPreference = oPCustomNotificationAnimVideoPreference;
        oPCustomNotificationAnimVideoPreference.setSettingsPreferenceFragment(this);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.op_custom_notification_anim_settings;
    }

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = activity;
    }

    @Override // com.oneplus.settings.OnPressListener
    public void onCancelPressed() {
        finish();
    }

    /* access modifiers changed from: protected */
    @Override // com.oneplus.settings.OPQuitConfirmFragment
    public boolean needShowWarningDialog() {
        OPCustomNotificationAnimVideoPreference oPCustomNotificationAnimVideoPreference = this.mNotificaitonAnimPreference;
        if (oPCustomNotificationAnimVideoPreference != null) {
            return oPCustomNotificationAnimVideoPreference.needShowWarningDialog();
        }
        return false;
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onDestroy() {
        super.onDestroy();
        int intForUser = Settings.System.getIntForUser(this.mContext.getContentResolver(), "op_custom_unlock_animation_style", 0, -2);
        if (intForUser == 0) {
            OPUtils.sendAnalytics("fod_effect", "status", "1");
        } else if (intForUser == 1) {
            OPUtils.sendAnalytics("fod_effect", "status", "2");
        } else if (intForUser == 2) {
            OPUtils.sendAnalytics("fod_effect", "status", OPMemberController.CLIENT_TYPE);
        } else if (intForUser == 9) {
            OPUtils.sendAnalytics("fod_effect", "status", "4");
        }
    }
}
