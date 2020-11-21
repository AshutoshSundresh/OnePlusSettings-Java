package com.android.settings.notification.zen;

import android.app.NotificationManager;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.UserHandle;
import android.service.notification.Condition;
import android.service.notification.ZenModeConfig;
import android.text.format.DateFormat;
import android.util.Log;
import com.android.settings.C0015R$plurals;
import com.android.settings.C0017R$string;
import com.android.settings.utils.VoiceSettingsActivity;
import java.util.Locale;

public class ZenModeVoiceActivity extends VoiceSettingsActivity {
    /* access modifiers changed from: protected */
    @Override // com.android.settings.utils.VoiceSettingsActivity
    public boolean onVoiceSettingInteraction(Intent intent) {
        int i;
        if (intent.hasExtra("android.settings.extra.do_not_disturb_mode_enabled")) {
            int intExtra = intent.getIntExtra("android.settings.extra.do_not_disturb_mode_minutes", -1);
            Condition condition = null;
            if (intent.getBooleanExtra("android.settings.extra.do_not_disturb_mode_enabled", false)) {
                if (intExtra > 0) {
                    condition = ZenModeConfig.toTimeCondition(this, intExtra, UserHandle.myUserId());
                }
                i = 3;
            } else {
                i = 0;
            }
            setZenModeConfig(i, condition);
            AudioManager audioManager = (AudioManager) getSystemService("audio");
            if (audioManager != null) {
                audioManager.adjustStreamVolume(5, 0, 1);
            }
            notifySuccess(getChangeSummary(i, intExtra));
        } else {
            Log.v("ZenModeVoiceActivity", "Missing extra android.provider.Settings.EXTRA_DO_NOT_DISTURB_MODE_ENABLED");
            finish();
        }
        return false;
    }

    private void setZenModeConfig(int i, Condition condition) {
        if (condition != null) {
            NotificationManager.from(this).setZenMode(i, condition.id, "ZenModeVoiceActivity");
        } else {
            NotificationManager.from(this).setZenMode(i, null, "ZenModeVoiceActivity");
        }
    }

    private CharSequence getChangeSummary(int i, int i2) {
        int i3;
        int i4;
        int i5;
        int i6 = -1;
        if (i == 0) {
            i4 = -1;
            i3 = -1;
            i6 = C0017R$string.zen_mode_summary_always;
            i5 = -1;
        } else if (i != 3) {
            i5 = -1;
            i4 = -1;
            i3 = -1;
        } else {
            i6 = C0017R$string.zen_mode_summary_alarms_only_indefinite;
            i5 = C0015R$plurals.zen_mode_summary_alarms_only_by_minute;
            i4 = C0015R$plurals.zen_mode_summary_alarms_only_by_hour;
            i3 = C0017R$string.zen_mode_summary_alarms_only_by_time;
        }
        if (i2 < 0 || i == 0) {
            return getString(i6);
        }
        CharSequence format = DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), DateFormat.is24HourFormat(this, UserHandle.myUserId()) ? "Hm" : "hma"), System.currentTimeMillis() + ((long) (60000 * i2)));
        Resources resources = getResources();
        if (i2 < 60) {
            return resources.getQuantityString(i5, i2, Integer.valueOf(i2), format);
        } else if (i2 % 60 != 0) {
            return resources.getString(i3, format);
        } else {
            int i7 = i2 / 60;
            return resources.getQuantityString(i4, i7, Integer.valueOf(i7), format);
        }
    }
}
