package com.android.settings.homepage.contextualcards;

import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.face.Face;
import android.hardware.face.FaceManager;
import android.os.Bundle;
import android.util.Log;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController;
import com.android.settings.C0017R$string;
import com.android.settings.Utils;
import com.android.settings.homepage.contextualcards.slices.FaceSetupSlice;

public class FaceReEnrollDialog extends AlertActivity implements DialogInterface.OnClickListener {
    private FaceManager mFaceManager;
    private int mReEnrollType;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        FaceReEnrollDialog.super.onCreate(bundle);
        AlertController.AlertParams alertParams = ((AlertActivity) this).mAlertParams;
        alertParams.mTitle = getText(C0017R$string.security_settings_face_enroll_improve_face_alert_title);
        alertParams.mMessage = getText(C0017R$string.security_settings_face_enroll_improve_face_alert_body);
        alertParams.mPositiveButtonText = getText(C0017R$string.storage_menu_set_up);
        alertParams.mNegativeButtonText = getText(C0017R$string.cancel);
        alertParams.mPositiveButtonListener = this;
        this.mFaceManager = Utils.getFaceManagerOrNull(getApplicationContext());
        this.mReEnrollType = FaceSetupSlice.getReEnrollSetting(getApplicationContext(), getUserId());
        Log.d("FaceReEnrollDialog", "ReEnroll Type : " + this.mReEnrollType);
        int i = this.mReEnrollType;
        if (i == 1) {
            setupAlert();
        } else if (i == 3) {
            removeFaceAndReEnroll();
        } else {
            Log.d("FaceReEnrollDialog", "Error unsupported flow for : " + this.mReEnrollType);
            dismiss();
        }
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        removeFaceAndReEnroll();
    }

    public void removeFaceAndReEnroll() {
        int userId = getUserId();
        FaceManager faceManager = this.mFaceManager;
        if (faceManager == null || !faceManager.hasEnrolledTemplates(userId)) {
            finish();
        }
        this.mFaceManager.remove(new Face("", 0, 0), userId, new FaceManager.RemovalCallback() {
            /* class com.android.settings.homepage.contextualcards.FaceReEnrollDialog.AnonymousClass1 */

            public void onRemovalError(Face face, int i, CharSequence charSequence) {
                FaceReEnrollDialog.super.onRemovalError(face, i, charSequence);
                FaceReEnrollDialog.this.finish();
            }

            public void onRemovalSucceeded(Face face, int i) {
                FaceReEnrollDialog.super.onRemovalSucceeded(face, i);
                if (i == 0) {
                    Intent intent = new Intent("android.settings.BIOMETRIC_ENROLL");
                    FaceReEnrollDialog.this.getApplicationContext();
                    try {
                        FaceReEnrollDialog.this.startActivity(intent);
                    } catch (Exception unused) {
                        Log.e("FaceReEnrollDialog", "Failed to startActivity");
                    }
                    FaceReEnrollDialog.this.finish();
                }
            }
        });
    }
}
