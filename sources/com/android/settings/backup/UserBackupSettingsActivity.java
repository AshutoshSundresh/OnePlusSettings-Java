package com.android.settings.backup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchIndexableData;
import android.util.Log;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.android.settings.C0017R$string;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexableRaw;
import com.oneplus.settings.BaseAppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class UserBackupSettingsActivity extends BaseAppCompatActivity {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.android.settings.backup.UserBackupSettingsActivity.AnonymousClass1 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableRaw> getRawDataToIndex(Context context, boolean z) {
            ArrayList arrayList = new ArrayList();
            SearchIndexableRaw searchIndexableRaw = new SearchIndexableRaw(context);
            searchIndexableRaw.title = context.getString(C0017R$string.privacy_settings_title);
            searchIndexableRaw.screenTitle = context.getString(C0017R$string.privacy_settings_title);
            searchIndexableRaw.keywords = context.getString(C0017R$string.keywords_backup);
            ((SearchIndexableData) searchIndexableRaw).intentTargetPackage = context.getPackageName();
            ((SearchIndexableData) searchIndexableRaw).intentTargetClass = UserBackupSettingsActivity.class.getName();
            ((SearchIndexableData) searchIndexableRaw).intentAction = "android.intent.action.MAIN";
            ((SearchIndexableData) searchIndexableRaw).key = "Backup";
            arrayList.add(searchIndexableRaw);
            return arrayList;
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            if (!new BackupSettingsHelper(context).isBackupServiceActive()) {
                nonIndexableKeys.add("Backup");
            }
            return nonIndexableKeys;
        }
    };
    private FragmentManager mFragmentManager;

    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        BackupSettingsHelper backupSettingsHelper = new BackupSettingsHelper(this);
        if (!backupSettingsHelper.isBackupProvidedByManufacturer()) {
            if (Log.isLoggable("BackupSettingsActivity", 3)) {
                Log.d("BackupSettingsActivity", "No manufacturer settings found, launching the backup settings directly");
            }
            Intent intentForBackupSettings = backupSettingsHelper.getIntentForBackupSettings();
            try {
                getPackageManager().setComponentEnabledSetting(intentForBackupSettings.getComponent(), 1, 1);
            } catch (SecurityException e) {
                Log.w("BackupSettingsActivity", "Trying to enable activity " + intentForBackupSettings.getComponent() + " but couldn't: " + e.getMessage());
            }
            startActivityForResult(intentForBackupSettings, 1);
            finish();
            return;
        }
        if (Log.isLoggable("BackupSettingsActivity", 3)) {
            Log.d("BackupSettingsActivity", "Manufacturer provided backup settings, showing the preference screen");
        }
        if (this.mFragmentManager == null) {
            this.mFragmentManager = getSupportFragmentManager();
        }
        FragmentTransaction beginTransaction = this.mFragmentManager.beginTransaction();
        beginTransaction.replace(16908290, new BackupSettingsFragment());
        beginTransaction.commit();
    }

    /* access modifiers changed from: package-private */
    public void setFragmentManager(FragmentManager fragmentManager) {
        this.mFragmentManager = fragmentManager;
    }
}
