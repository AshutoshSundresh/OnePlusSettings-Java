package com.android.settings.development;

import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.development.AbstractLogpersistPreferenceController;

public class LogPersistPreferenceController extends AbstractLogpersistPreferenceController implements PreferenceControllerMixin {
    private final DevelopmentSettingsDashboardFragment mFragment;

    public LogPersistPreferenceController(Context context, DevelopmentSettingsDashboardFragment developmentSettingsDashboardFragment, Lifecycle lifecycle) {
        super(context, lifecycle);
        this.mFragment = developmentSettingsDashboardFragment;
    }

    @Override // com.android.settingslib.core.ConfirmationDialogController
    public void showConfirmationDialog(Preference preference) {
        DisableLogPersistWarningDialog.show(this.mFragment);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        updateLogpersistValues();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        writeLogpersistOption(null, true);
    }

    public void onDisableLogPersistDialogConfirmed() {
        setLogpersistOff(true);
        updateLogpersistValues();
    }

    public void onDisableLogPersistDialogRejected() {
        updateLogpersistValues();
    }
}
