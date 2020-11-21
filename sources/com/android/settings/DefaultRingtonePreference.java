package com.android.settings;

import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import com.oneplus.settings.ringtone.OPRingtoneManager;
import com.oneplus.settings.ringtone.OPRingtonePickerActivity;

public class DefaultRingtonePreference extends RingtonePreference {
    public DefaultRingtonePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.android.settings.RingtonePreference
    public void onPrepareRingtonePickerIntent(Intent intent) {
        super.onPrepareRingtonePickerIntent(intent);
        if (intent != null) {
            intent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", false);
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.RingtonePreference
    public void onSaveRingtone(Uri uri) {
        RingtoneManager.setActualDefaultRingtoneUri(this.mUserContext, getRingtoneType(), uri);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.RingtonePreference
    public Uri onRestoreRingtone() {
        return OPRingtoneManager.getActualDefaultRingtoneUri(this.mUserContext, getRingtoneType());
    }

    /* access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public void onClick() {
        Log.d("volume", "ringtone click");
        Intent intent = new Intent(getContext(), OPRingtonePickerActivity.class);
        onPrepareRingtonePickerIntent(intent);
        intent.putExtra("CURRENT_USER_ID", getUserId());
        getContext().startActivity(intent);
    }
}
