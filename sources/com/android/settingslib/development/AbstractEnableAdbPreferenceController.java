package com.android.settingslib.development;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.UserManager;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import androidx.preference.TwoStatePreference;
import com.android.settingslib.core.ConfirmationDialogController;

public abstract class AbstractEnableAdbPreferenceController extends DeveloperOptionsPreferenceController implements ConfirmationDialogController {
    protected SwitchPreference mPreference;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "enable_adb";
    }

    public AbstractEnableAdbPreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (isAvailable()) {
            this.mPreference = (SwitchPreference) preferenceScreen.findPreference("enable_adb");
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settingslib.development.DeveloperOptionsPreferenceController
    public boolean isAvailable() {
        return ((UserManager) this.mContext.getSystemService(UserManager.class)).isAdminUser();
    }

    private boolean isAdbEnabled() {
        if (Settings.Global.getInt(this.mContext.getContentResolver(), "adb_enabled", 0) != 0) {
            return true;
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        ((TwoStatePreference) preference).setChecked(isAdbEnabled());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (isUserAMonkey() || !TextUtils.equals("enable_adb", preference.getKey())) {
            return false;
        }
        if (!isAdbEnabled()) {
            showConfirmationDialog(preference);
            return true;
        }
        writeAdbSetting(false);
        return true;
    }

    /* access modifiers changed from: protected */
    public void writeAdbSetting(boolean z) {
        Settings.Global.putInt(this.mContext.getContentResolver(), "adb_enabled", z ? 1 : 0);
        notifyStateChanged();
    }

    private void notifyStateChanged() {
        LocalBroadcastManager.getInstance(this.mContext).sendBroadcast(new Intent("com.android.settingslib.development.AbstractEnableAdbController.ENABLE_ADB_STATE_CHANGED"));
    }

    /* access modifiers changed from: package-private */
    public boolean isUserAMonkey() {
        return ActivityManager.isUserAMonkey();
    }
}
