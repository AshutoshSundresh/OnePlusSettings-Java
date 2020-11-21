package com.android.settings.wifi;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;

public class OPWifiScanAlwaysAvailablePreferenceController extends AbstractPreferenceController implements LifecycleObserver, OnResume, OnPause {
    private SettingObserver mSettingObserver;
    private WifiWakeupPreferenceController mWifiWakeupPreferenceController;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "wifi_scan_always_available";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public OPWifiScanAlwaysAvailablePreferenceController(Context context, Lifecycle lifecycle, WifiWakeupPreferenceController wifiWakeupPreferenceController) {
        super(context);
        lifecycle.addObserver(this);
        this.mWifiWakeupPreferenceController = wifiWakeupPreferenceController;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mSettingObserver = new SettingObserver(preferenceScreen.findPreference("wifi_scan_always_available"));
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
        if (!TextUtils.equals(preference.getKey(), "wifi_scan_always_available") || !(preference instanceof SwitchPreference)) {
            return false;
        }
        SwitchPreference switchPreference = (SwitchPreference) preference;
        Settings.Global.putInt(this.mContext.getContentResolver(), "wifi_scan_always_enabled", switchPreference.isChecked() ? 1 : 0);
        if (!switchPreference.isChecked()) {
            setWifiWakeupEnabled(false);
            this.mWifiWakeupPreferenceController.onActivityResult(600, 1);
        }
        return true;
    }

    private void setWifiWakeupEnabled(boolean z) {
        Settings.Global.putInt(this.mContext.getContentResolver(), "wifi_wakeup_enabled", z ? 1 : 0);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (preference instanceof SwitchPreference) {
            SwitchPreference switchPreference = (SwitchPreference) preference;
            boolean z = false;
            if (Settings.Global.getInt(this.mContext.getContentResolver(), "wifi_scan_always_enabled", 0) == 1) {
                z = true;
            }
            switchPreference.setChecked(z);
        }
    }

    class SettingObserver extends ContentObserver {
        private final Uri WIFI_SCAN_ALWAYS_AVAILABLE_ENABLED_URI = Settings.Global.getUriFor("wifi_scan_always_enabled");
        private final Preference mPreference;

        public SettingObserver(Preference preference) {
            super(new Handler());
            this.mPreference = preference;
        }

        public void register(ContentResolver contentResolver, boolean z) {
            if (z) {
                contentResolver.registerContentObserver(this.WIFI_SCAN_ALWAYS_AVAILABLE_ENABLED_URI, false, this);
            } else {
                contentResolver.unregisterContentObserver(this);
            }
        }

        public void onChange(boolean z, Uri uri) {
            super.onChange(z, uri);
            if (this.WIFI_SCAN_ALWAYS_AVAILABLE_ENABLED_URI.equals(uri)) {
                OPWifiScanAlwaysAvailablePreferenceController.this.updateState(this.mPreference);
            }
        }
    }
}
