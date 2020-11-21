package com.android.settings.notification;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import com.android.settings.DefaultRingtonePreference;

public class DefaultNotificationTonePreference extends DefaultRingtonePreference {
    private Uri mRingtone;

    public DefaultNotificationTonePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.DefaultRingtonePreference, com.android.settings.RingtonePreference
    public Uri onRestoreRingtone() {
        return this.mRingtone;
    }

    @Override // com.android.settings.DefaultRingtonePreference, com.android.settings.RingtonePreference
    public void onPrepareRingtonePickerIntent(Intent intent) {
        super.onPrepareRingtonePickerIntent(intent);
        intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", this.mRingtone);
    }
}
