package com.oneplus.settings.timer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.google.android.material.picker.TimePicker;
import com.google.android.material.picker.TimePickerDialog;
import java.util.Calendar;

public class AccessibilityTimerShutdownSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private AlarmManager am;
    private Calendar c;
    private Intent intent;
    private SwitchPreference mShutdownPreference;
    private SwitchPreference mStartupPreference;
    private Preference mTimeDownSettingsPreference;
    private Preference mTimeUpSettingsPreference;
    private PendingIntent pIntent;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        return false;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.op_accessibility_timer_shutdown_settings);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        initView();
    }

    private void initView() {
        SwitchPreference switchPreference = (SwitchPreference) findPreference("accessibility_timer_startup_device");
        this.mStartupPreference = switchPreference;
        switchPreference.setOnPreferenceClickListener(this);
        boolean z = true;
        this.mStartupPreference.setChecked(Settings.System.getInt(getActivity().getContentResolver(), "oem_startup_timer", 1) != 0);
        SwitchPreference switchPreference2 = (SwitchPreference) findPreference("accessibility_timer_shutdown_device");
        this.mShutdownPreference = switchPreference2;
        switchPreference2.setOnPreferenceClickListener(this);
        SwitchPreference switchPreference3 = this.mShutdownPreference;
        if (Settings.System.getInt(getActivity().getContentResolver(), "oem_shutdown_timer", 1) == 0) {
            z = false;
        }
        switchPreference3.setChecked(z);
        Preference findPreference = findPreference("accessibility_timer_startup_device_settings");
        this.mTimeUpSettingsPreference = findPreference;
        findPreference.setOnPreferenceClickListener(this);
        Preference findPreference2 = findPreference("accessibility_timer_startup_device_settings");
        this.mTimeDownSettingsPreference = findPreference2;
        findPreference2.setOnPreferenceClickListener(this);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals("accessibility_timer_shutdown_device")) {
            Settings.System.getInt(getActivity().getContentResolver(), "oem_shutdown_timer", this.mStartupPreference.isChecked() ? 1 : 0);
            return false;
        } else if (preference.getKey().equals("accessibility_timer_startup_device")) {
            Settings.System.getInt(getActivity().getContentResolver(), "oem_startup_timer", this.mStartupPreference.isChecked() ? 1 : 0);
            return false;
        } else if (preference.getKey().equals("accessibility_timer_startup_device_settings")) {
            this.c.setTimeInMillis(System.currentTimeMillis());
            new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                /* class com.oneplus.settings.timer.AccessibilityTimerShutdownSettings.AnonymousClass1 */

                @Override // com.google.android.material.picker.TimePickerDialog.OnTimeSetListener
                public void onTimeSet(TimePicker timePicker, int i, int i2) {
                    AccessibilityTimerShutdownSettings.this.c.setTimeInMillis(System.currentTimeMillis());
                    AccessibilityTimerShutdownSettings.this.c.set(11, i);
                    AccessibilityTimerShutdownSettings.this.c.set(12, i2);
                    AccessibilityTimerShutdownSettings.this.c.set(13, 0);
                    AccessibilityTimerShutdownSettings.this.c.set(14, 0);
                    AccessibilityTimerShutdownSettings.this.intent = new Intent("com.android.settings.action.REQUEST_POWER_ON");
                    AccessibilityTimerShutdownSettings accessibilityTimerShutdownSettings = AccessibilityTimerShutdownSettings.this;
                    accessibilityTimerShutdownSettings.pIntent = PendingIntent.getBroadcast(accessibilityTimerShutdownSettings.getActivity(), 0, AccessibilityTimerShutdownSettings.this.intent, 0);
                    AccessibilityTimerShutdownSettings accessibilityTimerShutdownSettings2 = AccessibilityTimerShutdownSettings.this;
                    accessibilityTimerShutdownSettings2.am = (AlarmManager) accessibilityTimerShutdownSettings2.getSystemService("alarm");
                    AccessibilityTimerShutdownSettings.this.am.set(0, AccessibilityTimerShutdownSettings.this.c.getTimeInMillis(), AccessibilityTimerShutdownSettings.this.pIntent);
                    AccessibilityTimerShutdownSettings.this.am.setRepeating(0, AccessibilityTimerShutdownSettings.this.c.getTimeInMillis(), 10000, AccessibilityTimerShutdownSettings.this.pIntent);
                    Preference preference = AccessibilityTimerShutdownSettings.this.mTimeUpSettingsPreference;
                    preference.setSummary("设置的闹钟时间为:" + i + ":" + i2);
                }
            }, this.c.get(11), this.c.get(12), true).show();
            return true;
        } else if (!preference.getKey().equals("accessibility_timer_startup_device_settings")) {
            return false;
        } else {
            this.c.setTimeInMillis(System.currentTimeMillis());
            new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                /* class com.oneplus.settings.timer.AccessibilityTimerShutdownSettings.AnonymousClass2 */

                @Override // com.google.android.material.picker.TimePickerDialog.OnTimeSetListener
                public void onTimeSet(TimePicker timePicker, int i, int i2) {
                    AccessibilityTimerShutdownSettings.this.c.setTimeInMillis(System.currentTimeMillis());
                    AccessibilityTimerShutdownSettings.this.c.set(11, i);
                    AccessibilityTimerShutdownSettings.this.c.set(12, i2);
                    AccessibilityTimerShutdownSettings.this.c.set(13, 0);
                    AccessibilityTimerShutdownSettings.this.c.set(14, 0);
                    AccessibilityTimerShutdownSettings.this.intent = new Intent("com.android.settings.action.REQUEST_POWER_OFF");
                    AccessibilityTimerShutdownSettings.this.intent.addFlags(285212672);
                    AccessibilityTimerShutdownSettings accessibilityTimerShutdownSettings = AccessibilityTimerShutdownSettings.this;
                    accessibilityTimerShutdownSettings.pIntent = PendingIntent.getBroadcast(accessibilityTimerShutdownSettings.getActivity(), 0, AccessibilityTimerShutdownSettings.this.intent, 0);
                    AccessibilityTimerShutdownSettings accessibilityTimerShutdownSettings2 = AccessibilityTimerShutdownSettings.this;
                    accessibilityTimerShutdownSettings2.am = (AlarmManager) accessibilityTimerShutdownSettings2.getSystemService("alarm");
                    AccessibilityTimerShutdownSettings.this.am.set(0, AccessibilityTimerShutdownSettings.this.c.getTimeInMillis(), AccessibilityTimerShutdownSettings.this.pIntent);
                    AccessibilityTimerShutdownSettings.this.am.setRepeating(0, AccessibilityTimerShutdownSettings.this.c.getTimeInMillis(), 10000, AccessibilityTimerShutdownSettings.this.pIntent);
                    Preference preference = AccessibilityTimerShutdownSettings.this.mTimeUpSettingsPreference;
                    preference.setSummary("设置的闹钟时间为:" + i + ":" + i2);
                }
            }, this.c.get(11), this.c.get(12), true).show();
            return true;
        }
    }
}
