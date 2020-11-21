package com.android.settings.backup;

import android.content.Context;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;

public class PrivacySettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.privacy_settings) {
        /* class com.android.settings.backup.PrivacySettings.AnonymousClass1 */

        /* access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            BackupSettingsHelper backupSettingsHelper = new BackupSettingsHelper(context);
            return !backupSettingsHelper.isBackupProvidedByManufacturer() && !backupSettingsHelper.isIntentProvidedByTransport();
        }
    };

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "PrivacySettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 81;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.privacy_settings;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_backup_reset;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        updatePrivacySettingsConfigData(context);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public void updatePreferenceStates() {
        updatePrivacySettingsConfigData(getContext());
        super.updatePreferenceStates();
    }

    private void updatePrivacySettingsConfigData(Context context) {
        if (PrivacySettingsUtils.isAdminUser(context)) {
            PrivacySettingsUtils.updatePrivacyBuffer(context, PrivacySettingsConfigData.getInstance());
        }
    }
}
