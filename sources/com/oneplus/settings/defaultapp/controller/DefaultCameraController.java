package com.oneplus.settings.defaultapp.controller;

import android.content.Context;
import com.oneplus.settings.defaultapp.DefaultAppUtils;

public class DefaultCameraController extends DefaultBasePreferenceController {
    public DefaultCameraController(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    @Override // com.oneplus.settings.defaultapp.controller.DefaultBasePreferenceController
    public String getType() {
        return DefaultAppUtils.getKeyTypeString(0);
    }
}
