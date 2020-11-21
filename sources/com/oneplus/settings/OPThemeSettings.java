package com.oneplus.settings;

import android.content.Context;
import android.os.Bundle;
import android.provider.SearchIndexableData;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexableRaw;
import com.oneplus.settings.ui.OPThemeIconPreference;
import com.oneplus.settings.utils.OPThemeUtils;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;

public class OPThemeSettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.oneplus.settings.OPThemeSettings.AnonymousClass1 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableRaw> getRawDataToIndex(Context context, boolean z) {
            ArrayList arrayList = new ArrayList();
            if (!OPUtils.isSupportNotificationLight()) {
                SearchIndexableRaw searchIndexableRaw = new SearchIndexableRaw(context);
                searchIndexableRaw.title = context.getString(C0017R$string.oneplus_theme_entrance_title);
                ((SearchIndexableData) searchIndexableRaw).key = "theme_setting_no_notification_light_screen";
                searchIndexableRaw.screenTitle = context.getString(C0017R$string.oneplus_theme_entrance_title);
                searchIndexableRaw.keywords = context.getString(C0017R$string.oneplus_search_keyword_theme_settings_no_notification_light);
                arrayList.add(searchIndexableRaw);
            } else if (OPThemeUtils.isSupportCustomeTheme()) {
                SearchIndexableRaw searchIndexableRaw2 = new SearchIndexableRaw(context);
                searchIndexableRaw2.title = context.getString(C0017R$string.oneplus_theme_entrance_title);
                ((SearchIndexableData) searchIndexableRaw2).key = "theme_setting_mcl_screen";
                searchIndexableRaw2.screenTitle = context.getString(C0017R$string.oneplus_theme_entrance_title);
                searchIndexableRaw2.keywords = context.getString(C0017R$string.oneplus_search_keyword_theme_settings_mcl);
                arrayList.add(searchIndexableRaw2);
            }
            return arrayList;
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            if (!OPUtils.isSupportNotificationLight() || OPThemeUtils.isSupportCustomeTheme()) {
                nonIndexableKeys.add("theme_setting_screen");
            }
            return nonIndexableKeys;
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return !OPUtils.isGuestMode();
        }
    };
    private OPThemeIconPreference mOPThemeIconPreference;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "OPThemeSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mOPThemeIconPreference = (OPThemeIconPreference) findPreference("oneplus_theme_custom_key");
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        OPThemeIconPreference oPThemeIconPreference = this.mOPThemeIconPreference;
        if (oPThemeIconPreference != null) {
            oPThemeIconPreference.refreshUI();
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.op_theme_settings;
    }
}
