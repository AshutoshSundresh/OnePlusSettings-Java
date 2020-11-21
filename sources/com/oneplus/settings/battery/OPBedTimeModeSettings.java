package com.oneplus.settings.battery;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.google.android.material.picker.TimePicker;
import com.google.android.material.picker.TimePickerDialog;
import java.text.DateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.TimeZone;

public class OPBedTimeModeSettings extends DashboardFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private SwitchPreference mAutoActivate;
    private Context mContext;
    private DateFormat mTimeFormatter;
    private Preference mTurnOffTime;
    private Preference mTurnOnTime;

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        return 9999;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "OPBedTimeModeSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = getActivity();
        this.mAutoActivate = (SwitchPreference) findPreference("auto_activate");
        this.mTurnOnTime = findPreference("turn_on_time");
        this.mTurnOffTime = findPreference("turn_off_time");
        this.mAutoActivate.setOnPreferenceChangeListener(this);
        this.mTurnOnTime.setOnPreferenceClickListener(this);
        this.mTurnOffTime.setOnPreferenceClickListener(this);
        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(this.mContext);
        this.mTimeFormatter = timeFormat;
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        updataState();
    }

    private void updataState() {
        if (this.mAutoActivate != null) {
            if (Settings.Secure.getIntForUser(getContentResolver(), "bedtime_mode_auto_mode", 0, -2) == 1) {
                this.mTurnOnTime.setVisible(true);
                this.mTurnOffTime.setVisible(true);
                this.mAutoActivate.setChecked(true);
            } else {
                this.mTurnOnTime.setVisible(false);
                this.mTurnOffTime.setVisible(false);
                this.mAutoActivate.setChecked(false);
            }
        }
        this.mTurnOnTime.setSummary(getFormattedTimeString(getCustomStartTime()));
        this.mTurnOffTime.setSummary(getFormattedTimeString(getCustomEndTime()));
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private String getFormattedTimeString(LocalTime localTime) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeZone(this.mTimeFormatter.getTimeZone());
        instance.set(11, localTime.getHour());
        instance.set(12, localTime.getMinute());
        instance.set(13, 0);
        instance.set(14, 0);
        return this.mTimeFormatter.format(instance.getTime());
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        updataState();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference != this.mAutoActivate) {
            return true;
        }
        boolean booleanValue = ((Boolean) obj).booleanValue();
        this.mTurnOnTime.setVisible(booleanValue);
        this.mTurnOffTime.setVisible(booleanValue);
        Settings.Secure.putIntForUser(getContentResolver(), "bedtime_mode_auto_mode", booleanValue ? 1 : 0, -2);
        return true;
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        if (preference == this.mTurnOnTime) {
            showDialog(0);
        } else if (preference == this.mTurnOffTime) {
            showDialog(1);
        }
        return true;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.op_bed_time_mode_settings;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private LocalTime getCustomStartTime() {
        return LocalTime.ofSecondOfDay(Settings.Secure.getLongForUser(getContentResolver(), "bedtime_mode_custom_start_time", 0, -2) / 1000);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private LocalTime getCustomEndTime() {
        return LocalTime.ofSecondOfDay(Settings.Secure.getLongForUser(getContentResolver(), "bedtime_mode_custom_end_time", 0, -2) / 1000);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(final int i) {
        LocalTime localTime;
        if (i != 0 && i != 1) {
            return super.onCreateDialog(i);
        }
        if (i == 0) {
            localTime = getCustomStartTime();
        } else {
            localTime = getCustomEndTime();
        }
        Context context = getContext();
        return new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            /* class com.oneplus.settings.battery.OPBedTimeModeSettings.AnonymousClass4 */

            @Override // com.google.android.material.picker.TimePickerDialog.OnTimeSetListener
            public void onTimeSet(TimePicker timePicker, int i, int i2) {
                LocalTime of = LocalTime.of(i, i2);
                if (i == 0) {
                    if (String.valueOf(OPBedTimeModeSettings.this.getCustomEndTime()).equals(String.valueOf(of))) {
                        Toast.makeText(OPBedTimeModeSettings.this.getPrefContext(), C0017R$string.timepower_time_duplicate, 1).show();
                        return;
                    }
                    Settings.Secure.putLongForUser(OPBedTimeModeSettings.this.getContentResolver(), "bedtime_mode_custom_start_time", (long) (of.toSecondOfDay() * 1000), -2);
                    OPBedTimeModeSettings.this.mTurnOnTime.setSummary(OPBedTimeModeSettings.this.getFormattedTimeString(of));
                } else if (String.valueOf(OPBedTimeModeSettings.this.getCustomStartTime()).equals(String.valueOf(of))) {
                    Toast.makeText(OPBedTimeModeSettings.this.getPrefContext(), C0017R$string.timepower_time_duplicate, 1).show();
                } else {
                    Settings.Secure.putLongForUser(OPBedTimeModeSettings.this.getContentResolver(), "bedtime_mode_custom_end_time", (long) (of.toSecondOfDay() * 1000), -2);
                    OPBedTimeModeSettings.this.mTurnOffTime.setSummary(OPBedTimeModeSettings.this.getFormattedTimeString(of));
                }
            }
        }, localTime.getHour(), localTime.getMinute(), android.text.format.DateFormat.is24HourFormat(context));
    }
}
