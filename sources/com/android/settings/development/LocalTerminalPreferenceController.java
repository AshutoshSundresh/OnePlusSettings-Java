package com.android.settings.development;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.UserManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;

public class LocalTerminalPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    static final String TERMINAL_APP_PACKAGE = "com.android.terminal";
    private PackageManager mPackageManager;
    private UserManager mUserManager;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "enable_terminal";
    }

    public LocalTerminalPreferenceController(Context context) {
        super(context);
        this.mUserManager = (UserManager) context.getSystemService("user");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settingslib.development.DeveloperOptionsPreferenceController
    public boolean isAvailable() {
        return isPackageInstalled(TERMINAL_APP_PACKAGE);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPackageManager = getPackageManager();
        if (isAvailable() && !isEnabled()) {
            this.mPreference.setEnabled(false);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        this.mPackageManager.setApplicationEnabledSetting(TERMINAL_APP_PACKAGE, ((Boolean) obj).booleanValue() ? 1 : 0, 0);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        boolean z = true;
        if (this.mPackageManager.getApplicationEnabledSetting(TERMINAL_APP_PACKAGE) != 1) {
            z = false;
        }
        ((SwitchPreference) this.mPreference).setChecked(z);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchEnabled() {
        if (isEnabled()) {
            this.mPreference.setEnabled(true);
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        this.mPackageManager.setApplicationEnabledSetting(TERMINAL_APP_PACKAGE, 0, 0);
        ((SwitchPreference) this.mPreference).setChecked(false);
    }

    /* access modifiers changed from: package-private */
    public PackageManager getPackageManager() {
        return this.mContext.getPackageManager();
    }

    private boolean isPackageInstalled(String str) {
        try {
            return this.mContext.getPackageManager().getPackageInfo(str, 0) != null;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    private boolean isEnabled() {
        return this.mUserManager.isAdminUser();
    }
}
