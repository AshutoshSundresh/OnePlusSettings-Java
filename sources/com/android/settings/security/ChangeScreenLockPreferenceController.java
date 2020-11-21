package com.android.settings.security;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.C0005R$bool;
import com.android.settings.C0017R$string;
import com.android.settings.Utils;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.password.ChooseLockGeneric;
import com.android.settings.security.screenlock.ScreenLockSettings;
import com.android.settings.widget.GearPreference;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;

public class ChangeScreenLockPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, GearPreference.OnGearClickListener {
    protected final SecuritySettings mHost;
    protected final LockPatternUtils mLockPatternUtils;
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    protected RestrictedPreference mPreference;
    protected final int mProfileChallengeUserId;
    protected final UserManager mUm;
    protected final int mUserId = UserHandle.myUserId();

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "unlock_set_or_change";
    }

    public ChangeScreenLockPreferenceController(Context context, SecuritySettings securitySettings) {
        super(context);
        this.mUm = (UserManager) context.getSystemService("user");
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService("device_policy");
        this.mLockPatternUtils = FeatureFactory.getFactory(context).getSecurityFeatureProvider().getLockPatternUtils(context);
        this.mHost = securitySettings;
        this.mProfileChallengeUserId = Utils.getManagedProfileId(this.mUm, this.mUserId);
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mContext.getResources().getBoolean(C0005R$bool.config_show_unlock_set_or_change);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (RestrictedPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        RestrictedPreference restrictedPreference = this.mPreference;
        if (restrictedPreference != null && (restrictedPreference instanceof GearPreference)) {
            if (this.mLockPatternUtils.isSecure(this.mUserId) || !this.mLockPatternUtils.isLockScreenDisabled(this.mUserId)) {
                ((GearPreference) this.mPreference).setOnGearClickListener(this);
            } else {
                ((GearPreference) this.mPreference).setOnGearClickListener(null);
            }
        }
        updateSummary(preference, this.mUserId);
        disableIfPasswordQualityManaged(this.mUserId);
        if (!this.mLockPatternUtils.isSeparateProfileChallengeEnabled(this.mProfileChallengeUserId)) {
            disableIfPasswordQualityManaged(this.mProfileChallengeUserId);
        }
    }

    @Override // com.android.settings.widget.GearPreference.OnGearClickListener
    public void onGearClick(GearPreference gearPreference) {
        if (TextUtils.equals(gearPreference.getKey(), getPreferenceKey())) {
            this.mMetricsFeatureProvider.logClickedPreference(gearPreference, gearPreference.getExtras().getInt("category"));
            SubSettingLauncher subSettingLauncher = new SubSettingLauncher(this.mContext);
            subSettingLauncher.setDestination(ScreenLockSettings.class.getName());
            subSettingLauncher.setSourceMetricsCategory(this.mHost.getMetricsCategory());
            subSettingLauncher.launch();
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals(preference.getKey(), getPreferenceKey())) {
            return super.handlePreferenceTreeClick(preference);
        }
        if (((Activity) this.mContext).isInMultiWindowMode()) {
            Toast.makeText(this.mContext, C0017R$string.feature_not_support_split_screen, 0).show();
            return false;
        }
        int i = this.mProfileChallengeUserId;
        if (i != -10000 && !this.mLockPatternUtils.isSeparateProfileChallengeEnabled(i) && StorageManager.isFileEncryptedNativeOnly() && Utils.startQuietModeDialogIfNecessary(this.mContext, this.mUm, this.mProfileChallengeUserId)) {
            return false;
        }
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(this.mContext);
        subSettingLauncher.setDestination(ChooseLockGeneric.ChooseLockGenericFragment.class.getName());
        subSettingLauncher.setTitleRes(C0017R$string.lock_settings_picker_title);
        subSettingLauncher.setSourceMetricsCategory(this.mHost.getMetricsCategory());
        subSettingLauncher.launch();
        return true;
    }

    /* access modifiers changed from: protected */
    public void updateSummary(Preference preference, int i) {
        if (this.mLockPatternUtils.isSecure(i)) {
            int keyguardStoredPasswordQuality = this.mLockPatternUtils.getKeyguardStoredPasswordQuality(i);
            if (keyguardStoredPasswordQuality == 65536) {
                preference.setSummary(C0017R$string.unlock_set_unlock_mode_pattern);
            } else if (keyguardStoredPasswordQuality == 131072 || keyguardStoredPasswordQuality == 196608) {
                preference.setSummary(C0017R$string.unlock_set_unlock_mode_pin);
            } else if (keyguardStoredPasswordQuality == 262144 || keyguardStoredPasswordQuality == 327680 || keyguardStoredPasswordQuality == 393216 || keyguardStoredPasswordQuality == 524288) {
                preference.setSummary(C0017R$string.unlock_set_unlock_mode_password);
            }
        } else if (i == this.mProfileChallengeUserId || this.mLockPatternUtils.isLockScreenDisabled(i)) {
            preference.setSummary(C0017R$string.unlock_set_unlock_mode_off);
        } else {
            preference.setSummary(C0017R$string.unlock_set_unlock_mode_none);
        }
        this.mPreference.setEnabled(true);
    }

    /* access modifiers changed from: package-private */
    public void disableIfPasswordQualityManaged(int i) {
        RestrictedLockUtils.EnforcedAdmin checkIfPasswordQualityIsSet = RestrictedLockUtilsInternal.checkIfPasswordQualityIsSet(this.mContext, i);
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) this.mContext.getSystemService("device_policy");
        if (checkIfPasswordQualityIsSet != null && devicePolicyManager.getPasswordQuality(checkIfPasswordQualityIsSet.component, i) == 524288) {
            this.mPreference.setDisabledByAdmin(checkIfPasswordQualityIsSet);
        }
    }
}
