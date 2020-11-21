package com.android.settings.security;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.LockscreenCredential;
import com.android.settings.C0017R$string;
import com.android.settings.Utils;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.password.ChooseLockGeneric;
import com.android.settings.password.ChooseLockSettingsHelper;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedSwitchPreference;
import com.android.settingslib.core.AbstractPreferenceController;

public class LockUnificationPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener {
    private static final int MY_USER_ID = UserHandle.myUserId();
    private LockscreenCredential mCurrentDevicePassword = LockscreenCredential.createNone();
    private LockscreenCredential mCurrentProfilePassword = LockscreenCredential.createNone();
    private final DevicePolicyManager mDpm;
    private final SecuritySettings mHost;
    private final LockPatternUtils mLockPatternUtils;
    private final int mProfileUserId = Utils.getManagedProfileId(this.mUm, MY_USER_ID);
    private boolean mRequireNewDevicePassword;
    private final UserManager mUm;
    private RestrictedSwitchPreference mUnifyProfile;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "unification";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mUnifyProfile = (RestrictedSwitchPreference) preferenceScreen.findPreference("unification");
    }

    public LockUnificationPreferenceController(Context context, SecuritySettings securitySettings) {
        super(context);
        this.mHost = securitySettings;
        this.mUm = (UserManager) context.getSystemService(UserManager.class);
        this.mDpm = (DevicePolicyManager) context.getSystemService(DevicePolicyManager.class);
        this.mLockPatternUtils = FeatureFactory.getFactory(context).getSecurityFeatureProvider().getLockPatternUtils(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        int i = this.mProfileUserId;
        return i != -10000 && this.mLockPatternUtils.isSeparateProfileChallengeAllowed(i);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (Utils.startQuietModeDialogIfNecessary(this.mContext, this.mUm, this.mProfileUserId)) {
            return false;
        }
        if (((Boolean) obj).booleanValue()) {
            this.mRequireNewDevicePassword = !this.mDpm.isPasswordSufficientAfterProfileUnification(UserHandle.myUserId(), this.mProfileUserId);
            startUnification();
        } else {
            if (!new ChooseLockSettingsHelper(this.mHost.getActivity(), this.mHost).launchConfirmationActivity(130, this.mContext.getString(C0017R$string.unlock_set_unlock_launch_picker_title), true, MY_USER_ID)) {
                ununifyLocks();
            }
        }
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (this.mUnifyProfile != null) {
            boolean isSeparateProfileChallengeEnabled = this.mLockPatternUtils.isSeparateProfileChallengeEnabled(this.mProfileUserId);
            this.mUnifyProfile.setChecked(!isSeparateProfileChallengeEnabled);
            if (isSeparateProfileChallengeEnabled) {
                this.mUnifyProfile.setDisabledByAdmin(RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mContext, "no_unified_password", this.mProfileUserId));
            }
        }
    }

    public boolean handleActivityResult(int i, int i2, Intent intent) {
        if (i == 130 && i2 == -1) {
            this.mCurrentDevicePassword = intent.getParcelableExtra("password");
            ununifyLocks();
            return true;
        } else if (i != 129 || i2 != -1) {
            return false;
        } else {
            this.mCurrentProfilePassword = intent.getParcelableExtra("password");
            unifyLocks();
            return true;
        }
    }

    private void ununifyLocks() {
        Bundle bundle = new Bundle();
        bundle.putInt("android.intent.extra.USER_ID", this.mProfileUserId);
        bundle.putParcelable("password", this.mCurrentDevicePassword);
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(this.mContext);
        subSettingLauncher.setDestination(ChooseLockGeneric.ChooseLockGenericFragment.class.getName());
        subSettingLauncher.setTitleRes(C0017R$string.lock_settings_picker_title_profile);
        subSettingLauncher.setSourceMetricsCategory(this.mHost.getMetricsCategory());
        subSettingLauncher.setArguments(bundle);
        subSettingLauncher.launch();
    }

    /* access modifiers changed from: package-private */
    public void startUnification() {
        if (!new ChooseLockSettingsHelper(this.mHost.getActivity(), this.mHost).launchConfirmationActivity(129, this.mContext.getString(C0017R$string.unlock_set_unlock_launch_picker_title_profile), true, this.mProfileUserId)) {
            unifyLocks();
        }
    }

    private void unifyLocks() {
        if (this.mRequireNewDevicePassword) {
            promptForNewDeviceLockAndThenUnify();
        } else {
            unifyKeepingDeviceLock();
        }
        LockscreenCredential lockscreenCredential = this.mCurrentDevicePassword;
        if (lockscreenCredential != null) {
            lockscreenCredential.zeroize();
            this.mCurrentDevicePassword = null;
        }
        LockscreenCredential lockscreenCredential2 = this.mCurrentProfilePassword;
        if (lockscreenCredential2 != null) {
            lockscreenCredential2.zeroize();
            this.mCurrentProfilePassword = null;
        }
    }

    private void unifyKeepingDeviceLock() {
        this.mLockPatternUtils.setSeparateProfileChallengeEnabled(this.mProfileUserId, false, this.mCurrentProfilePassword);
    }

    private void promptForNewDeviceLockAndThenUnify() {
        Bundle bundle = new Bundle();
        bundle.putInt("unification_profile_id", this.mProfileUserId);
        bundle.putParcelable("unification_profile_credential", this.mCurrentProfilePassword);
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(this.mContext);
        subSettingLauncher.setDestination(ChooseLockGeneric.ChooseLockGenericFragment.class.getName());
        subSettingLauncher.setTitleRes(C0017R$string.lock_settings_picker_title);
        subSettingLauncher.setSourceMetricsCategory(this.mHost.getMetricsCategory());
        subSettingLauncher.setArguments(bundle);
        subSettingLauncher.launch();
    }
}
