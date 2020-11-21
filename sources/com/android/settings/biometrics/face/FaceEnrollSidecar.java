package com.android.settings.biometrics.face;

import android.app.Activity;
import android.hardware.face.FaceManager;
import com.android.settings.Utils;
import com.android.settings.biometrics.BiometricEnrollSidecar;
import java.util.Arrays;

public class FaceEnrollSidecar extends BiometricEnrollSidecar {
    private final int[] mDisabledFeatures;
    private FaceManager.EnrollmentCallback mEnrollmentCallback = new FaceManager.EnrollmentCallback() {
        /* class com.android.settings.biometrics.face.FaceEnrollSidecar.AnonymousClass1 */

        public void onEnrollmentProgress(int i) {
            FaceEnrollSidecar.super.onEnrollmentProgress(i);
        }

        public void onEnrollmentHelp(int i, CharSequence charSequence) {
            FaceEnrollSidecar.super.onEnrollmentHelp(i, charSequence);
        }

        public void onEnrollmentError(int i, CharSequence charSequence) {
            FaceEnrollSidecar.super.onEnrollmentError(i, charSequence);
        }
    };
    private FaceManager mFaceManager;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1509;
    }

    public FaceEnrollSidecar(int[] iArr) {
        this.mDisabledFeatures = Arrays.copyOf(iArr, iArr.length);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settings.biometrics.BiometricEnrollSidecar
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mFaceManager = Utils.getFaceManagerOrNull(activity);
    }

    @Override // com.android.settings.biometrics.BiometricEnrollSidecar
    public void startEnrollment() {
        super.startEnrollment();
        int i = this.mUserId;
        if (i != -10000) {
            this.mFaceManager.setActiveUser(i);
        }
        this.mFaceManager.enroll(this.mUserId, this.mToken, this.mEnrollmentCancel, this.mEnrollmentCallback, this.mDisabledFeatures);
    }
}
