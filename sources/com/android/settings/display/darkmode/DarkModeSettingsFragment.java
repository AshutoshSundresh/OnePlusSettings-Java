package com.android.settings.display.darkmode;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;

public class DarkModeSettingsFragment extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.dark_mode_settings) {
        /* class com.android.settings.display.darkmode.DarkModeSettingsFragment.AnonymousClass1 */

        /* access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return !((PowerManager) context.getSystemService(PowerManager.class)).isPowerSaveMode();
        }
    };
    private Runnable mCallback = new Runnable() {
        /* class com.android.settings.display.darkmode.$$Lambda$DarkModeSettingsFragment$KkVUTj9kbGrBG4xKtR4voqrbL00 */

        public final void run() {
            DarkModeSettingsFragment.this.lambda$new$0$DarkModeSettingsFragment();
        }
    };
    private DarkModeObserver mContentObserver;
    private DarkModeCustomPreferenceController mCustomEndController;
    private DarkModeCustomPreferenceController mCustomStartController;

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        if (i != 0) {
            return i != 1 ? 0 : 1826;
        }
        return 1825;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "DarkModeSettingsFrag";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1698;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$DarkModeSettingsFragment() {
        updatePreferenceStates();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContentObserver = new DarkModeObserver(getContext());
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStart() {
        super.onStart();
        this.mContentObserver.subscribe(this.mCallback);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList(2);
        this.mCustomStartController = new DarkModeCustomPreferenceController(getContext(), "dark_theme_start_time", this);
        this.mCustomEndController = new DarkModeCustomPreferenceController(getContext(), "dark_theme_end_time", this);
        arrayList.add(this.mCustomStartController);
        arrayList.add(this.mCustomEndController);
        return arrayList;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStop() {
        super.onStop();
        this.mContentObserver.unsubscribe();
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener, com.android.settings.dashboard.DashboardFragment
    public boolean onPreferenceTreeClick(Preference preference) {
        if ("dark_theme_end_time".equals(preference.getKey())) {
            showDialog(1);
            return true;
        } else if (!"dark_theme_start_time".equals(preference.getKey())) {
            return super.onPreferenceTreeClick(preference);
        } else {
            showDialog(0);
            return true;
        }
    }

    public void refresh() {
        updatePreferenceStates();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        if (i != 0 && i != 1) {
            return super.onCreateDialog(i);
        }
        if (i == 0) {
            return this.mCustomStartController.getDialog();
        }
        return this.mCustomEndController.getDialog();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.dark_mode_settings;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_dark_theme;
    }
}
