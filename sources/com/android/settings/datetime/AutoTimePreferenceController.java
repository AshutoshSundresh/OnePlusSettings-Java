package com.android.settings.datetime;

import android.content.Context;
import android.os.UserHandle;
import android.provider.Settings;
import androidx.preference.Preference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedSwitchPreference;
import com.android.settingslib.core.AbstractPreferenceController;

public class AutoTimePreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener {
    private final UpdateTimeAndDateCallback mCallback;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "auto_time";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public AutoTimePreferenceController(Context context, UpdateTimeAndDateCallback updateTimeAndDateCallback) {
        super(context);
        this.mCallback = updateTimeAndDateCallback;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (preference instanceof RestrictedSwitchPreference) {
            RestrictedSwitchPreference restrictedSwitchPreference = (RestrictedSwitchPreference) preference;
            if (!restrictedSwitchPreference.isDisabledByAdmin()) {
                restrictedSwitchPreference.setDisabledByAdmin(getEnforcedAdminProperty());
            }
            restrictedSwitchPreference.setChecked(isEnabled());
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        Settings.Global.putInt(this.mContext.getContentResolver(), "auto_time", ((Boolean) obj).booleanValue() ? 1 : 0);
        this.mCallback.updateTimeAndDateDisplay(this.mContext);
        return true;
    }

    public boolean isEnabled() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "auto_time", 0) > 0;
    }

    private RestrictedLockUtils.EnforcedAdmin getEnforcedAdminProperty() {
        return RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mContext, "no_config_date_time", UserHandle.myUserId());
    }
}
