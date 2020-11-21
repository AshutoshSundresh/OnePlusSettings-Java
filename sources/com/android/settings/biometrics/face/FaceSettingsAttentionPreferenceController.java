package com.android.settings.biometrics.face;

import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.hardware.face.FaceManager;
import android.provider.Settings;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.Utils;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.AbstractPreferenceController;

public class FaceSettingsAttentionPreferenceController extends FaceSettingsPreferenceController {
    public static final String KEY = "security_settings_face_require_attention";
    private FaceManager mFaceManager;
    private final FaceManager.GetFeatureCallback mGetFeatureCallback;
    private SwitchPreference mPreference;
    private final FaceManager.SetFeatureCallback mSetFeatureCallback;
    private byte[] mToken;

    @Override // com.android.settings.biometrics.face.FaceSettingsPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
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

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public boolean isSliceable() {
        return false;
    }

    @Override // com.android.settings.biometrics.face.FaceSettingsPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public FaceSettingsAttentionPreferenceController(Context context, String str) {
        super(context, str);
        this.mSetFeatureCallback = new FaceManager.SetFeatureCallback() {
            /* class com.android.settings.biometrics.face.FaceSettingsAttentionPreferenceController.AnonymousClass1 */

            public void onCompleted(boolean z, int i) {
                if (i == 1) {
                    FaceSettingsAttentionPreferenceController.this.mPreference.setEnabled(true);
                    if (!z) {
                        FaceSettingsAttentionPreferenceController.this.mPreference.setChecked(!FaceSettingsAttentionPreferenceController.this.mPreference.isChecked());
                        return;
                    }
                    ContentResolver contentResolver = ((AbstractPreferenceController) FaceSettingsAttentionPreferenceController.this).mContext.getContentResolver();
                    boolean isChecked = FaceSettingsAttentionPreferenceController.this.mPreference.isChecked();
                    Settings.Secure.putIntForUser(contentResolver, "face_unlock_attention_required", isChecked ? 1 : 0, FaceSettingsAttentionPreferenceController.this.getUserId());
                }
            }
        };
        this.mGetFeatureCallback = new FaceManager.GetFeatureCallback() {
            /* class com.android.settings.biometrics.face.FaceSettingsAttentionPreferenceController.AnonymousClass2 */

            public void onCompleted(boolean z, int i, boolean z2) {
                if (i == 1 && z) {
                    if (!FaceSettingsAttentionPreferenceController.this.mFaceManager.hasEnrolledTemplates(FaceSettingsAttentionPreferenceController.this.getUserId())) {
                        FaceSettingsAttentionPreferenceController.this.mPreference.setEnabled(false);
                        return;
                    }
                    FaceSettingsAttentionPreferenceController.this.mPreference.setEnabled(true);
                    FaceSettingsAttentionPreferenceController.this.mPreference.setChecked(z2);
                }
            }
        };
        this.mFaceManager = Utils.getFaceManagerOrNull(context);
    }

    public FaceSettingsAttentionPreferenceController(Context context) {
        this(context, KEY);
    }

    public void setToken(byte[] bArr) {
        this.mToken = bArr;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (SwitchPreference) preferenceScreen.findPreference(KEY);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        if (!FaceSettings.isFaceHardwareDetected(this.mContext)) {
            return true;
        }
        this.mPreference.setEnabled(false);
        this.mFaceManager.getFeature(getUserId(), 1, this.mGetFeatureCallback);
        return true;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        this.mPreference.setEnabled(false);
        this.mPreference.setChecked(z);
        this.mFaceManager.setFeature(getUserId(), 1, z, this.mToken, this.mSetFeatureCallback);
        return true;
    }
}
