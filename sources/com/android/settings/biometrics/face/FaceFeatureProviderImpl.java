package com.android.settings.biometrics.face;

import android.content.Context;

public class FaceFeatureProviderImpl implements FaceFeatureProvider {
    @Override // com.android.settings.biometrics.face.FaceFeatureProvider
    public boolean isAttentionSupported(Context context) {
        return true;
    }
}
