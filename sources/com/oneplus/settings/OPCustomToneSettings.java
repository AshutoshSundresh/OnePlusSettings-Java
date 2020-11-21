package com.oneplus.settings;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.preference.Preference;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.oneplus.settings.ui.OPCustomTonePreference;

public class OPCustomToneSettings extends OPQuitConfirmFragment implements Preference.OnPreferenceClickListener, OnPressListener {
    private OPCustomTonePreference mCustomTonePreference;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "OPCustomToneSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        return false;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setOnPressListener(this);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        setHasOptionsMenu(true);
        this.mCustomTonePreference = (OPCustomTonePreference) findPreference("op_custom_fingerprint_anim");
    }

    @Override // com.oneplus.settings.OnPressListener
    public void onCancelPressed() {
        finish();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.op_custom_tone_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.oneplus.settings.OPQuitConfirmFragment
    public boolean needShowWarningDialog() {
        OPCustomTonePreference oPCustomTonePreference = this.mCustomTonePreference;
        if (oPCustomTonePreference != null) {
            return oPCustomTonePreference.needShowWarningDialog();
        }
        return false;
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        if (menu != null) {
            MenuItem add = menu.add(0, 0, 0, C0017R$string.oneplus_finger_print_anim_save);
            add.setIcon(C0008R$drawable.op_ic_check);
            add.setShowAsAction(1);
            add.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                /* class com.oneplus.settings.$$Lambda$OPCustomToneSettings$epD9P_6do2KzQcx36E6hd77bAQI */

                public final boolean onMenuItemClick(MenuItem menuItem) {
                    return OPCustomToneSettings.this.lambda$onCreateOptionsMenu$0$OPCustomToneSettings(menuItem);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateOptionsMenu$0 */
    public /* synthetic */ boolean lambda$onCreateOptionsMenu$0$OPCustomToneSettings(MenuItem menuItem) {
        finish();
        new Handler().postDelayed(new Runnable() {
            /* class com.oneplus.settings.OPCustomToneSettings.AnonymousClass1 */

            public void run() {
                OPCustomToneSettings.this.mCustomTonePreference.saveSelectedTone();
            }
        }, 100);
        return true;
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onDestroy() {
        super.onDestroy();
    }
}
