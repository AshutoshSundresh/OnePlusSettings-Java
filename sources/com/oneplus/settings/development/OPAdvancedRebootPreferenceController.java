package com.oneplus.settings.development;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.UserManager;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;

public class OPAdvancedRebootPreferenceController extends DeveloperOptionsPreferenceController implements LifecycleObserver, OnResume, OnPause, PreferenceControllerMixin {
    private SettingObserver mSettingObserver;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "advanced_reboot";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settingslib.development.DeveloperOptionsPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public OPAdvancedRebootPreferenceController(Context context, Lifecycle lifecycle) {
        super(context);
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (this.mPreference != null && !isAdminUser()) {
            this.mPreference.setEnabled(false);
        }
        this.mSettingObserver = new SettingObserver(preferenceScreen.findPreference("advanced_reboot"));
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        SettingObserver settingObserver = this.mSettingObserver;
        if (settingObserver != null) {
            settingObserver.register(this.mContext.getContentResolver(), true);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        SettingObserver settingObserver = this.mSettingObserver;
        if (settingObserver != null) {
            settingObserver.register(this.mContext.getContentResolver(), false);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals(preference.getKey(), "advanced_reboot") || !(preference instanceof SwitchPreference)) {
            return false;
        }
        Settings.Secure.putInt(this.mContext.getContentResolver(), "advanced_reboot", ((SwitchPreference) preference).isChecked() ? 1 : 0);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (preference instanceof SwitchPreference) {
            SwitchPreference switchPreference = (SwitchPreference) preference;
            boolean z = false;
            if (Settings.Secure.getInt(this.mContext.getContentResolver(), "advanced_reboot", 0) != 0) {
                z = true;
            }
            switchPreference.setChecked(z);
        }
    }

    class SettingObserver extends ContentObserver {
        private final Uri ADVANCED_REBOOT_ENABLED_URI = Settings.Secure.getUriFor("advanced_reboot");
        private final Preference mPreference;

        public SettingObserver(Preference preference) {
            super(new Handler());
            this.mPreference = preference;
        }

        public void register(ContentResolver contentResolver, boolean z) {
            if (z) {
                contentResolver.registerContentObserver(this.ADVANCED_REBOOT_ENABLED_URI, false, this);
            } else {
                contentResolver.unregisterContentObserver(this);
            }
        }

        public void onChange(boolean z, Uri uri) {
            super.onChange(z, uri);
            if (this.ADVANCED_REBOOT_ENABLED_URI.equals(uri)) {
                OPAdvancedRebootPreferenceController.this.updateState(this.mPreference);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isAdminUser() {
        return ((UserManager) this.mContext.getSystemService("user")).isAdminUser();
    }
}
