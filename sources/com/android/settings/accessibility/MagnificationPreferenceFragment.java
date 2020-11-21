package com.android.settings.accessibility;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;

public final class MagnificationPreferenceFragment extends DashboardFragment {
    static final int OFF = 0;
    static final int ON = 1;
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.accessibility_magnification_settings) {
        /* class com.android.settings.accessibility.MagnificationPreferenceFragment.AnonymousClass1 */

        /* access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return MagnificationPreferenceFragment.isApplicable(context.getResources());
        }
    };
    private boolean mLaunchedFromSuw = false;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "MagnificationPreferenceFragment";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 922;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_magnification;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.accessibility_magnification_settings;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("from_suw")) {
            this.mLaunchedFromSuw = arguments.getBoolean("from_suw");
        }
        ((MagnificationGesturesPreferenceController) use(MagnificationGesturesPreferenceController.class)).setIsFromSUW(this.mLaunchedFromSuw);
        ((MagnificationNavbarPreferenceController) use(MagnificationNavbarPreferenceController.class)).setIsFromSUW(this.mLaunchedFromSuw);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener, com.android.settings.dashboard.DashboardFragment
    public boolean onPreferenceTreeClick(Preference preference) {
        if (this.mLaunchedFromSuw) {
            preference.setFragment(ToggleScreenMagnificationPreferenceFragmentForSetupWizard.class.getName());
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        if (!this.mLaunchedFromSuw) {
            super.onCreateOptionsMenu(menu, menuInflater);
        }
    }

    static boolean isChecked(ContentResolver contentResolver, String str) {
        return Settings.Secure.getInt(contentResolver, str, 0) == 1;
    }

    static boolean setChecked(ContentResolver contentResolver, String str, boolean z) {
        return Settings.Secure.putInt(contentResolver, str, z ? 1 : 0);
    }

    static boolean isApplicable(Resources resources) {
        return resources.getBoolean(17891529);
    }
}
