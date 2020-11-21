package com.android.settings.security;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.Utils;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.password.ChooseLockGeneric;

public class ChangeProfileScreenLockPreferenceController extends ChangeScreenLockPreferenceController {
    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.security.ChangeScreenLockPreferenceController
    public String getPreferenceKey() {
        return "unlock_set_or_change_profile";
    }

    public ChangeProfileScreenLockPreferenceController(Context context, SecuritySettings securitySettings) {
        super(context, securitySettings);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.security.ChangeScreenLockPreferenceController
    public boolean isAvailable() {
        int keyguardStoredPasswordQuality;
        int i = this.mProfileChallengeUserId;
        if (i == -10000 || !this.mLockPatternUtils.isSeparateProfileChallengeAllowed(i)) {
            return false;
        }
        if (!this.mLockPatternUtils.isSecure(this.mProfileChallengeUserId) || (keyguardStoredPasswordQuality = this.mLockPatternUtils.getKeyguardStoredPasswordQuality(this.mProfileChallengeUserId)) == 65536 || keyguardStoredPasswordQuality == 131072 || keyguardStoredPasswordQuality == 196608 || keyguardStoredPasswordQuality == 262144 || keyguardStoredPasswordQuality == 327680 || keyguardStoredPasswordQuality == 393216 || keyguardStoredPasswordQuality == 524288) {
            return true;
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.security.ChangeScreenLockPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals(preference.getKey(), getPreferenceKey()) || Utils.startQuietModeDialogIfNecessary(this.mContext, this.mUm, this.mProfileChallengeUserId)) {
            return false;
        }
        Bundle bundle = new Bundle();
        bundle.putInt("android.intent.extra.USER_ID", this.mProfileChallengeUserId);
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(this.mContext);
        subSettingLauncher.setDestination(ChooseLockGeneric.ChooseLockGenericFragment.class.getName());
        subSettingLauncher.setTitleRes(C0017R$string.lock_settings_picker_title_profile);
        subSettingLauncher.setSourceMetricsCategory(this.mHost.getMetricsCategory());
        subSettingLauncher.setArguments(bundle);
        subSettingLauncher.launch();
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.security.ChangeScreenLockPreferenceController
    public void updateState(Preference preference) {
        updateSummary(preference, this.mProfileChallengeUserId);
        if (!this.mLockPatternUtils.isSeparateProfileChallengeEnabled(this.mProfileChallengeUserId)) {
            this.mPreference.setSummary(this.mContext.getString(C0017R$string.lock_settings_profile_unified_summary));
            this.mPreference.setEnabled(false);
            return;
        }
        disableIfPasswordQualityManaged(this.mProfileChallengeUserId);
    }
}
