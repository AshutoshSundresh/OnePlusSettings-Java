package com.android.settings.datetime;

import android.app.Activity;
import android.app.timedetector.TimeDetector;
import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateFormat;
import androidx.preference.Preference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.core.AbstractPreferenceController;
import com.google.android.material.picker.TimePicker;
import com.google.android.material.picker.TimePickerDialog;
import java.util.Calendar;

public class TimePreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, TimePickerDialog.OnTimeSetListener {
    private final AutoTimePreferenceController mAutoTimePreferenceController;
    private final TimePreferenceHost mHost;

    public interface TimePreferenceHost extends UpdateTimeAndDateCallback {
        void showTimePicker();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "time";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public TimePreferenceController(Context context, TimePreferenceHost timePreferenceHost, AutoTimePreferenceController autoTimePreferenceController) {
        super(context);
        this.mHost = timePreferenceHost;
        this.mAutoTimePreferenceController = autoTimePreferenceController;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (preference instanceof RestrictedPreference) {
            preference.setSummary(DateFormat.getTimeFormat(this.mContext).format(Calendar.getInstance().getTime()));
            if (!((RestrictedPreference) preference).isDisabledByAdmin()) {
                preference.setEnabled(!this.mAutoTimePreferenceController.isEnabled());
            }
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals("time", preference.getKey())) {
            return false;
        }
        this.mHost.showTimePicker();
        return true;
    }

    @Override // com.google.android.material.picker.TimePickerDialog.OnTimeSetListener
    public void onTimeSet(TimePicker timePicker, int i, int i2) {
        if (this.mContext != null) {
            setTime(i, i2);
            this.mHost.updateTimeAndDateDisplay(this.mContext);
        }
    }

    public TimePickerDialog buildTimePicker(Activity activity) {
        Calendar instance = Calendar.getInstance();
        return new TimePickerDialog(activity, this, instance.get(11), instance.get(12), DateFormat.is24HourFormat(activity));
    }

    /* access modifiers changed from: package-private */
    public void setTime(int i, int i2) {
        Calendar instance = Calendar.getInstance();
        instance.set(11, i);
        instance.set(12, i2);
        instance.set(13, 0);
        instance.set(14, 0);
        long max = Math.max(instance.getTimeInMillis(), 1194220800000L);
        if (max / 1000 < 2147483647L) {
            ((TimeDetector) this.mContext.getSystemService(TimeDetector.class)).suggestManualTime(TimeDetector.createManualTimeSuggestion(max, "Settings: Set time"));
        }
    }
}
