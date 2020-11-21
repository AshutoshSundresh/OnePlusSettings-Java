package com.oneplus.settings.ringtone;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.util.AttributeSet;
import com.android.settings.RingtonePreference;

public class OPSmsRingtonePreference extends RingtonePreference {
    public OPSmsRingtonePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.android.settings.RingtonePreference
    public void onPrepareRingtonePickerIntent(Intent intent) {
        super.onPrepareRingtonePickerIntent(intent);
        if (intent != null) {
            intent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", false);
            intent.putExtra("android.intent.extra.ringtone.TYPE", 8);
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.RingtonePreference
    public void onSaveRingtone(Uri uri) {
        Settings.System.putString(getContext().getContentResolver(), "mms_notification", uri != null ? uri.toString() : null);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.RingtonePreference
    public Uri onRestoreRingtone() {
        return getDefaultSmsNotificationRingtone(getContext());
    }

    private Uri getDefaultSmsNotificationRingtone(Context context) {
        String string = Settings.System.getString(context.getContentResolver(), "mms_notification");
        if (string != null) {
            return Uri.parse(string);
        }
        return null;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public void onClick() {
        Intent intent = new Intent(getContext(), OPRingtonePickerActivity.class);
        intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", onRestoreRingtone());
        intent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", false);
        intent.putExtra("android.intent.extra.ringtone.SHOW_SILENT", getShowSilent());
        intent.putExtra("android.intent.extra.ringtone.TYPE", 8);
        intent.putExtra("android.intent.extra.ringtone.TITLE", getTitle());
        getContext().startActivity(intent);
    }
}
