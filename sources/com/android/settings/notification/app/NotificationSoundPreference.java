package com.android.settings.notification.app;

import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.AttributeSet;
import com.android.settings.C0017R$string;
import com.android.settings.RingtonePreference;
import com.oneplus.settings.ringtone.OPRingtonePickerActivity;

public class NotificationSoundPreference extends RingtonePreference {
    private Uri mRingtone;

    public NotificationSoundPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setIntent(new Intent(context, OPRingtonePickerActivity.class));
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.RingtonePreference
    public Uri onRestoreRingtone() {
        return this.mRingtone;
    }

    public void setRingtone(Uri uri) {
        this.mRingtone = uri;
        setSummary(" ");
        updateRingtoneName(this.mRingtone);
    }

    @Override // com.android.settings.RingtonePreference
    public boolean onActivityResult(int i, int i2, Intent intent) {
        if (intent == null) {
            return true;
        }
        Uri uri = (Uri) intent.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
        setRingtone(uri);
        callChangeListener(uri);
        return true;
    }

    private void updateRingtoneName(final Uri uri) {
        new AsyncTask<Object, Void, CharSequence>() {
            /* class com.android.settings.notification.app.NotificationSoundPreference.AnonymousClass1 */

            /* access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public CharSequence doInBackground(Object... objArr) {
                Uri uri = uri;
                if (uri == null) {
                    return NotificationSoundPreference.this.getContext().getString(17041178);
                }
                if (RingtoneManager.isDefault(uri)) {
                    return NotificationSoundPreference.this.getContext().getString(C0017R$string.notification_sound_default);
                }
                if ("android.resource".equals(uri.getScheme())) {
                    return NotificationSoundPreference.this.getContext().getString(C0017R$string.notification_unknown_sound_title);
                }
                return Ringtone.getTitle(NotificationSoundPreference.this.getContext(), uri, false, true);
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(CharSequence charSequence) {
                NotificationSoundPreference.this.setSummary(charSequence);
            }
        }.execute(new Object[0]);
    }
}
