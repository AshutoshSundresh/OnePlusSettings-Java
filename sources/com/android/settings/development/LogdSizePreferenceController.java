package com.android.settings.development;

import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.AbstractLogdSizePreferenceController;

public class LogdSizePreferenceController extends AbstractLogdSizePreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    public LogdSizePreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        updateLogdSizeValues();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        writeLogdSizeOption(null);
    }
}
