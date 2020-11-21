package com.oneplus.settings.security;

import android.content.Context;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.TwoStatePreference;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
import com.oneplus.settings.utils.OPUtils;

public class DisableQuickSettingsWhenLockedPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener {
    private final LockPatternUtils mLockPatternUtils;
    private final int mUserId;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "disable_quick_settings_when_locked";
    }

    public DisableQuickSettingsWhenLockedPreferenceController(Context context, int i, LockPatternUtils lockPatternUtils) {
        super(context);
        this.mUserId = i;
        this.mLockPatternUtils = lockPatternUtils;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        if (!this.mLockPatternUtils.isSecure(this.mUserId)) {
            return false;
        }
        int keyguardStoredPasswordQuality = this.mLockPatternUtils.getKeyguardStoredPasswordQuality(this.mUserId);
        if (keyguardStoredPasswordQuality == 65536 || keyguardStoredPasswordQuality == 131072 || keyguardStoredPasswordQuality == 196608 || keyguardStoredPasswordQuality == 262144 || keyguardStoredPasswordQuality == 327680 || keyguardStoredPasswordQuality == 393216 || keyguardStoredPasswordQuality == 524288) {
            return true;
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        boolean z = false;
        TwoStatePreference twoStatePreference = (TwoStatePreference) preference;
        if (Settings.Secure.getInt(this.mContext.getContentResolver(), "oneplus_disable_qs_when_locked", 0) == 1) {
            z = true;
        }
        twoStatePreference.setChecked(z);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        Boolean bool = (Boolean) obj;
        Settings.Secure.putInt(this.mContext.getContentResolver(), "oneplus_disable_qs_when_locked", bool.booleanValue() ? 1 : 0);
        OPUtils.sendAnalytics("LockScreen_disable", "status", bool.booleanValue() ? "1" : "0");
        return true;
    }
}
