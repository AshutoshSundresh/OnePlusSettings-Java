package com.android.settings.notification;

import android.content.Context;

public class AlarmRingtonePreferenceController extends RingtonePreferenceControllerBase {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "alarm_ringtone";
    }

    @Override // com.android.settings.notification.RingtonePreferenceControllerBase
    public int getRingtoneType() {
        return 4;
    }

    public AlarmRingtonePreferenceController(Context context) {
        super(context);
    }
}
