package com.android.settings.backup;

import android.content.Context;
import android.content.Intent;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;

public class BackupSettingsPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private Intent mBackupSettingsIntent;
    private String mBackupSettingsSummary;
    private CharSequence mBackupSettingsTitle;
    private Intent mManufacturerIntent;
    private String mManufacturerLabel;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return null;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public BackupSettingsPreferenceController(Context context) {
        super(context);
        BackupSettingsHelper backupSettingsHelper = new BackupSettingsHelper(context);
        this.mBackupSettingsIntent = backupSettingsHelper.getIntentForBackupSettings();
        this.mBackupSettingsTitle = backupSettingsHelper.getLabelForBackupSettings();
        this.mBackupSettingsSummary = backupSettingsHelper.getSummaryForBackupSettings();
        this.mManufacturerIntent = backupSettingsHelper.getIntentProvidedByManufacturer();
        this.mManufacturerLabel = backupSettingsHelper.getLabelProvidedByManufacturer();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        Preference findPreference = preferenceScreen.findPreference("backup_settings");
        Preference findPreference2 = preferenceScreen.findPreference("manufacturer_backup");
        findPreference.setIntent(this.mBackupSettingsIntent);
        findPreference.setTitle(this.mBackupSettingsTitle);
        findPreference.setSummary(this.mBackupSettingsSummary);
        findPreference2.setIntent(this.mManufacturerIntent);
        findPreference2.setTitle(this.mManufacturerLabel);
    }
}
