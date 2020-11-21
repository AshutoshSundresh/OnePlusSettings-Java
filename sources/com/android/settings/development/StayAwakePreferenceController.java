package com.android.settings.development;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedSwitchPreference;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;

public class StayAwakePreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, LifecycleObserver, OnResume, OnPause, PreferenceControllerMixin {
    static final int SETTING_VALUE_OFF = 0;
    static final int SETTING_VALUE_ON = 7;
    private RestrictedSwitchPreference mPreference;
    SettingsObserver mSettingsObserver;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "keep_screen_on";
    }

    public StayAwakePreferenceController(Context context, Lifecycle lifecycle) {
        super(context);
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (RestrictedSwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        Settings.Global.putInt(this.mContext.getContentResolver(), "stay_on_while_plugged_in", ((Boolean) obj).booleanValue() ? 7 : 0);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        RestrictedLockUtils.EnforcedAdmin checkIfMaximumTimeToLockSetByAdmin = checkIfMaximumTimeToLockSetByAdmin();
        if (checkIfMaximumTimeToLockSetByAdmin != null) {
            this.mPreference.setDisabledByAdmin(checkIfMaximumTimeToLockSetByAdmin);
            return;
        }
        boolean z = false;
        int i = Settings.Global.getInt(this.mContext.getContentResolver(), "stay_on_while_plugged_in", 0);
        RestrictedSwitchPreference restrictedSwitchPreference = this.mPreference;
        if (i != 0) {
            z = true;
        }
        restrictedSwitchPreference.setChecked(z);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        if (this.mPreference != null) {
            if (this.mSettingsObserver == null) {
                this.mSettingsObserver = new SettingsObserver();
            }
            this.mSettingsObserver.register(true);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        SettingsObserver settingsObserver;
        if (this.mPreference != null && (settingsObserver = this.mSettingsObserver) != null) {
            settingsObserver.register(false);
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        Settings.Global.putInt(this.mContext.getContentResolver(), "stay_on_while_plugged_in", 0);
        this.mPreference.setChecked(false);
    }

    /* access modifiers changed from: package-private */
    public RestrictedLockUtils.EnforcedAdmin checkIfMaximumTimeToLockSetByAdmin() {
        return RestrictedLockUtilsInternal.checkIfMaximumTimeToLockIsSet(this.mContext);
    }

    class SettingsObserver extends ContentObserver {
        private final Uri mStayAwakeUri = Settings.Global.getUriFor("stay_on_while_plugged_in");

        public SettingsObserver() {
            super(new Handler());
        }

        public void register(boolean z) {
            ContentResolver contentResolver = ((AbstractPreferenceController) StayAwakePreferenceController.this).mContext.getContentResolver();
            if (z) {
                contentResolver.registerContentObserver(this.mStayAwakeUri, false, this);
            } else {
                contentResolver.unregisterContentObserver(this);
            }
        }

        public void onChange(boolean z, Uri uri) {
            super.onChange(z, uri);
            if (this.mStayAwakeUri.equals(uri)) {
                StayAwakePreferenceController stayAwakePreferenceController = StayAwakePreferenceController.this;
                stayAwakePreferenceController.updateState(stayAwakePreferenceController.mPreference);
            }
        }
    }
}
