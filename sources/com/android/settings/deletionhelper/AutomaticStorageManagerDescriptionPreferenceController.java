package com.android.settings.deletionhelper;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.text.format.DateUtils;
import android.text.format.Formatter;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.Utils;
import com.android.settingslib.core.AbstractPreferenceController;

public class AutomaticStorageManagerDescriptionPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "freed_bytes";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public AutomaticStorageManagerDescriptionPreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        Preference findPreference = preferenceScreen.findPreference(getPreferenceKey());
        Context context = findPreference.getContext();
        ContentResolver contentResolver = context.getContentResolver();
        long j = Settings.Secure.getLong(contentResolver, "automatic_storage_manager_bytes_cleared", 0);
        long j2 = Settings.Secure.getLong(contentResolver, "automatic_storage_manager_last_run", 0);
        if (j == 0 || j2 == 0 || !Utils.isStorageManagerEnabled(context)) {
            findPreference.setSummary(C0017R$string.automatic_storage_manager_text);
            return;
        }
        findPreference.setSummary(context.getString(C0017R$string.automatic_storage_manager_freed_bytes, Formatter.formatFileSize(context, j), DateUtils.formatDateTime(context, j2, 16)));
    }
}
