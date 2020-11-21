package com.android.settings.datetime;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.DateFormat;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import androidx.preference.TwoStatePreference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.Calendar;

public class TimeFormatPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private final Calendar mDummyDate = Calendar.getInstance();
    private final boolean mIsFromSUW;
    private final UpdateTimeAndDateCallback mUpdateTimeAndDateCallback;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "24 hour";
    }

    public TimeFormatPreferenceController(Context context, UpdateTimeAndDateCallback updateTimeAndDateCallback, boolean z) {
        super(context);
        this.mIsFromSUW = z;
        this.mUpdateTimeAndDateCallback = updateTimeAndDateCallback;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return !this.mIsFromSUW;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (preference instanceof TwoStatePreference) {
            preference.setEnabled(true);
            ((TwoStatePreference) preference).setChecked(is24Hour());
            Calendar instance = Calendar.getInstance();
            this.mDummyDate.setTimeZone(instance.getTimeZone());
            this.mDummyDate.set(instance.get(1), 11, 31, 13, 0, 0);
            preference.setSummary(DateFormat.getTimeFormat(this.mContext).format(this.mDummyDate.getTime()));
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!(preference instanceof TwoStatePreference) || !TextUtils.equals("24 hour", preference.getKey())) {
            return false;
        }
        update24HourFormat(this.mContext, Boolean.valueOf(((SwitchPreference) preference).isChecked()));
        this.mUpdateTimeAndDateCallback.updateTimeAndDateDisplay(this.mContext);
        return true;
    }

    private boolean is24Hour() {
        return DateFormat.is24HourFormat(this.mContext);
    }

    static void update24HourFormat(Context context, Boolean bool) {
        set24Hour(context, bool);
        timeUpdated(context, bool);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r3v1 */
    /* JADX WARN: Type inference failed for: r3v3 */
    /* JADX WARN: Type inference failed for: r3v5 */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static void timeUpdated(android.content.Context r2, java.lang.Boolean r3) {
        /*
            android.content.Intent r0 = new android.content.Intent
            java.lang.String r1 = "android.intent.action.TIME_SET"
            r0.<init>(r1)
            r1 = 16777216(0x1000000, float:2.3509887E-38)
            r0.addFlags(r1)
            if (r3 != 0) goto L_0x0010
            r3 = 2
            goto L_0x0014
        L_0x0010:
            boolean r3 = r3.booleanValue()
        L_0x0014:
            java.lang.String r1 = "android.intent.extra.TIME_PREF_24_HOUR_FORMAT"
            r0.putExtra(r1, r3)
            r2.sendBroadcast(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.datetime.TimeFormatPreferenceController.timeUpdated(android.content.Context, java.lang.Boolean):void");
    }

    static void set24Hour(Context context, Boolean bool) {
        Settings.System.putString(context.getContentResolver(), "time_12_24", bool == null ? null : bool.booleanValue() ? "24" : "12");
    }
}
