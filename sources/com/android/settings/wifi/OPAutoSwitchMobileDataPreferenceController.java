package com.android.settings.wifi;

import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.oneplus.settings.utils.OPUtils;
import com.oneplus.settings.utils.ProductUtils;

public class OPAutoSwitchMobileDataPreferenceController extends BasePreferenceController implements LifecycleObserver, OnResume, OnPause {
    public static final String AVOID_BAD_WIFI_CONNECTION_CICKED = "avoid_bad_wifi_connection_cicked";
    private static final String KEY_AUTO_SWITCH_MOBILE_DATA = "auto_switch_mobile_data";
    public static final String ONEPLUS_SMART_LINK_SELECTION = "oneplus_smart_link_selection";
    public static final String PREFS_WIFISETTINGS = "WifiSettingsPrefsFile";
    public static final String WIFI_AUTO_CHANGE_TO_MOBILE_DATA = "wifi_auto_change_to_mobile_data";
    private SwitchPreference mAvoidBadWifiConnection;
    private SettingObserver mSettingObserver;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public String getPreferenceKey() {
        return KEY_AUTO_SWITCH_MOBILE_DATA;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public OPAutoSwitchMobileDataPreferenceController(Context context, Lifecycle lifecycle) {
        super(context, KEY_AUTO_SWITCH_MOBILE_DATA);
        lifecycle.addObserver(this);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mSettingObserver = new SettingObserver(preferenceScreen.findPreference(KEY_AUTO_SWITCH_MOBILE_DATA));
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

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals(preference.getKey(), KEY_AUTO_SWITCH_MOBILE_DATA) || !(preference instanceof SwitchPreference)) {
            return false;
        }
        SwitchPreference switchPreference = (SwitchPreference) preference;
        Settings.System.putInt(this.mContext.getContentResolver(), WIFI_AUTO_CHANGE_TO_MOBILE_DATA, switchPreference.isChecked() ? 1 : 0);
        if (!ProductUtils.isUsvMode()) {
            Settings.System.putInt(this.mContext.getContentResolver(), ONEPLUS_SMART_LINK_SELECTION, switchPreference.isChecked() ? 1 : 0);
        }
        OPUtils.sendAnalytics("net_switch", "state", switchPreference.isChecked() ? "on" : "off");
        if (ProductUtils.isUsvMode()) {
            this.mAvoidBadWifiConnection = switchPreference;
            if (switchPreference.isChecked()) {
                SharedPreferences.Editor edit = this.mContext.getSharedPreferences(PREFS_WIFISETTINGS, 0).edit();
                edit.putBoolean(AVOID_BAD_WIFI_CONNECTION_CICKED, this.mAvoidBadWifiConnection.isChecked());
                edit.apply();
                OPUtils.sendAppTrackerForDataAutoSwitch();
                return true;
            }
            SharedPreferences.Editor edit2 = this.mContext.getSharedPreferences(PREFS_WIFISETTINGS, 0).edit();
            edit2.putBoolean(AVOID_BAD_WIFI_CONNECTION_CICKED, this.mAvoidBadWifiConnection.isChecked());
            edit2.apply();
            return true;
        }
        OPUtils.sendAppTrackerForDataAutoSwitch();
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (preference instanceof SwitchPreference) {
            SwitchPreference switchPreference = (SwitchPreference) preference;
            boolean z = false;
            if (Settings.System.getInt(this.mContext.getContentResolver(), WIFI_AUTO_CHANGE_TO_MOBILE_DATA, 0) == 1) {
                z = true;
            }
            switchPreference.setChecked(z);
        }
    }

    class SettingObserver extends ContentObserver {
        private final Uri WIFI_AUTO_CHANGE_TO_MOBILE_DATA_URI = Settings.System.getUriFor(OPAutoSwitchMobileDataPreferenceController.WIFI_AUTO_CHANGE_TO_MOBILE_DATA);
        private final Preference mPreference;

        public SettingObserver(Preference preference) {
            super(new Handler());
            this.mPreference = preference;
        }

        public void register(ContentResolver contentResolver, boolean z) {
            if (z) {
                contentResolver.registerContentObserver(this.WIFI_AUTO_CHANGE_TO_MOBILE_DATA_URI, false, this);
            } else {
                contentResolver.unregisterContentObserver(this);
            }
        }

        public void onChange(boolean z, Uri uri) {
            super.onChange(z, uri);
            if (this.WIFI_AUTO_CHANGE_TO_MOBILE_DATA_URI.equals(uri)) {
                OPAutoSwitchMobileDataPreferenceController.this.updateState(this.mPreference);
            }
        }
    }
}
