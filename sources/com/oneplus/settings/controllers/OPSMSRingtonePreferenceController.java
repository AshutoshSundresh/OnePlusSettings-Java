package com.oneplus.settings.controllers;

import android.content.Context;
import com.android.settings.notification.RingtonePreferenceControllerBase;

public class OPSMSRingtonePreferenceController extends RingtonePreferenceControllerBase {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "message_ringtone";
    }

    @Override // com.android.settings.notification.RingtonePreferenceControllerBase
    public int getRingtoneType() {
        return 8;
    }

    public OPSMSRingtonePreferenceController(Context context) {
        super(context);
    }
}
