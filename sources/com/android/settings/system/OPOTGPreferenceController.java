package com.android.settings.system;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;

public class OPOTGPreferenceController extends AbstractPreferenceController implements LifecycleObserver, OnResume, OnPause {
    private SettingObserver mSettingObserver;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "otg_read_enable";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mSettingObserver = new SettingObserver(preferenceScreen.findPreference("otg_read_enable"));
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
        if (!TextUtils.equals(preference.getKey(), "otg_read_enable") || !(preference instanceof SwitchPreference)) {
            return false;
        }
        SwitchPreference switchPreference = (SwitchPreference) preference;
        SystemProperties.set("persist.sys.oem.otg_support", switchPreference.isChecked() ? "true" : "false");
        Settings.Global.putInt(this.mContext.getContentResolver(), "oneplus_otg_auto_disable", switchPreference.isChecked() ? 1 : 0);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (preference instanceof SwitchPreference) {
            SwitchPreference switchPreference = (SwitchPreference) preference;
            boolean z = false;
            if (Settings.Global.getInt(this.mContext.getContentResolver(), "oneplus_otg_auto_disable", 0) == 1) {
                z = true;
            }
            switchPreference.setChecked(z);
        }
    }

    class SettingObserver extends ContentObserver {
        private final Uri OTG_AUTO_ENABLED_URI = Settings.Global.getUriFor("oneplus_otg_auto_disable");
        private final Preference mPreference;

        public SettingObserver(Preference preference) {
            super(new Handler());
            this.mPreference = preference;
        }

        public void register(ContentResolver contentResolver, boolean z) {
            if (z) {
                contentResolver.registerContentObserver(this.OTG_AUTO_ENABLED_URI, false, this);
            } else {
                contentResolver.unregisterContentObserver(this);
            }
        }

        public void onChange(boolean z, Uri uri) {
            super.onChange(z, uri);
            if (this.OTG_AUTO_ENABLED_URI.equals(uri)) {
                OPOTGPreferenceController.this.updateState(this.mPreference);
            }
        }
    }
}
