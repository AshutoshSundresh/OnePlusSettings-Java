package com.android.settings.password;

import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.face.FaceManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.UserManager;
import com.android.internal.util.Preconditions;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.Utils;

final class SetNewPasswordController {
    private final DevicePolicyManager mDevicePolicyManager;
    private final FaceManager mFaceManager;
    private final FingerprintManager mFingerprintManager;
    private final PackageManager mPackageManager;
    private final int mTargetUserId;
    private final Ui mUi;

    interface Ui {
        void launchChooseLock(Bundle bundle);
    }

    public static SetNewPasswordController create(Context context, Ui ui, Intent intent, IBinder iBinder) {
        int i;
        int currentUser = ActivityManager.getCurrentUser();
        if ("android.app.action.SET_NEW_PASSWORD".equals(intent.getAction())) {
            int identifier = Utils.getSecureTargetUser(iBinder, UserManager.get(context), null, intent.getExtras()).getIdentifier();
            if (new LockPatternUtils(context).isSeparateProfileChallengeAllowed(identifier)) {
                i = identifier;
                return new SetNewPasswordController(i, context.getPackageManager(), Utils.getFingerprintManagerOrNull(context), Utils.getFaceManagerOrNull(context), (DevicePolicyManager) context.getSystemService("device_policy"), ui);
            }
        }
        i = currentUser;
        return new SetNewPasswordController(i, context.getPackageManager(), Utils.getFingerprintManagerOrNull(context), Utils.getFaceManagerOrNull(context), (DevicePolicyManager) context.getSystemService("device_policy"), ui);
    }

    SetNewPasswordController(int i, PackageManager packageManager, FingerprintManager fingerprintManager, FaceManager faceManager, DevicePolicyManager devicePolicyManager, Ui ui) {
        this.mTargetUserId = i;
        this.mPackageManager = (PackageManager) Preconditions.checkNotNull(packageManager);
        this.mFingerprintManager = fingerprintManager;
        this.mFaceManager = faceManager;
        this.mDevicePolicyManager = (DevicePolicyManager) Preconditions.checkNotNull(devicePolicyManager);
        this.mUi = (Ui) Preconditions.checkNotNull(ui);
    }

    public void dispatchSetNewPasswordIntent() {
        Bundle bundle;
        FingerprintManager fingerprintManager;
        FaceManager faceManager;
        if (this.mPackageManager.hasSystemFeature("android.hardware.biometrics.face") && (faceManager = this.mFaceManager) != null && faceManager.isHardwareDetected() && !this.mFaceManager.hasEnrolledTemplates(this.mTargetUserId) && !isFaceDisabledByAdmin()) {
            bundle = getFaceChooseLockExtras();
        } else if (!this.mPackageManager.hasSystemFeature("android.hardware.fingerprint") || (fingerprintManager = this.mFingerprintManager) == null || !fingerprintManager.isHardwareDetected() || this.mFingerprintManager.hasEnrolledFingerprints(this.mTargetUserId) || isFingerprintDisabledByAdmin()) {
            bundle = new Bundle();
        } else {
            bundle = getFingerprintChooseLockExtras();
        }
        bundle.putInt("android.intent.extra.USER_ID", this.mTargetUserId);
        this.mUi.launchChooseLock(bundle);
    }

    private Bundle getFingerprintChooseLockExtras() {
        Bundle bundle = new Bundle();
        long preEnroll = this.mFingerprintManager.preEnroll();
        bundle.putInt("minimum_quality", 65536);
        bundle.putBoolean("hide_disabled_prefs", true);
        bundle.putBoolean("has_challenge", true);
        bundle.putLong("challenge", preEnroll);
        bundle.putBoolean("for_fingerprint", true);
        return bundle;
    }

    private Bundle getFaceChooseLockExtras() {
        Bundle bundle = new Bundle();
        long generateChallenge = this.mFaceManager.generateChallenge();
        bundle.putInt("minimum_quality", 65536);
        bundle.putBoolean("hide_disabled_prefs", true);
        bundle.putBoolean("has_challenge", true);
        bundle.putLong("challenge", generateChallenge);
        bundle.putBoolean("for_face", true);
        return bundle;
    }

    private boolean isFingerprintDisabledByAdmin() {
        return (this.mDevicePolicyManager.getKeyguardDisabledFeatures(null, this.mTargetUserId) & 32) != 0;
    }

    private boolean isFaceDisabledByAdmin() {
        return (this.mDevicePolicyManager.getKeyguardDisabledFeatures(null, this.mTargetUserId) & 128) != 0;
    }
}
