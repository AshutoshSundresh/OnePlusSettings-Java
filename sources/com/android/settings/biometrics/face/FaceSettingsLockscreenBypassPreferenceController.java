package com.android.settings.biometrics.face;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.face.FaceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import androidx.preference.Preference;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedSwitchPreference;

public class FaceSettingsLockscreenBypassPreferenceController extends FaceSettingsPreferenceController {
    @VisibleForTesting
    protected FaceManager mFaceManager;
    private UserManager mUserManager;

    @Override // com.android.settings.biometrics.face.FaceSettingsPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.biometrics.face.FaceSettingsPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.biometrics.face.FaceSettingsPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.biometrics.face.FaceSettingsPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.biometrics.face.FaceSettingsPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.biometrics.face.FaceSettingsPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public FaceSettingsLockscreenBypassPreferenceController(Context context, String str) {
        super(context, str);
        if (context.getPackageManager().hasSystemFeature("android.hardware.biometrics.face")) {
            this.mFaceManager = (FaceManager) context.getSystemService(FaceManager.class);
        }
        this.mUserManager = (UserManager) context.getSystemService(UserManager.class);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        if (!FaceSettings.isFaceHardwareDetected(this.mContext) || getRestrictingAdmin() != null) {
            return false;
        }
        boolean z = this.mContext.getResources().getBoolean(17891462);
        if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "face_unlock_dismisses_keyguard", z ? 1 : 0, getUserId()) != 0) {
            return true;
        }
        return false;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        Settings.Secure.putInt(this.mContext.getContentResolver(), "face_unlock_dismisses_keyguard", z ? 1 : 0);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.TogglePreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (!FaceSettings.isFaceHardwareDetected(this.mContext)) {
            preference.setEnabled(false);
            return;
        }
        RestrictedLockUtils.EnforcedAdmin restrictingAdmin = getRestrictingAdmin();
        if (restrictingAdmin != null) {
            ((RestrictedSwitchPreference) preference).setDisabledByAdmin(restrictingAdmin);
        } else if (!this.mFaceManager.hasEnrolledTemplates(getUserId())) {
            preference.setEnabled(false);
        } else {
            preference.setEnabled(true);
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        FaceManager faceManager;
        if (!this.mUserManager.isManagedProfile(UserHandle.myUserId()) && (faceManager = this.mFaceManager) != null && faceManager.isHardwareDetected()) {
            return this.mFaceManager.hasEnrolledTemplates(getUserId()) ? 0 : 5;
        }
        return 3;
    }
}
