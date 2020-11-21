package com.android.settings.display;

import android.app.Dialog;
import android.content.Context;
import android.hardware.display.ColorDisplayManager;
import android.hardware.display.NightDisplayListener;
import android.os.Bundle;
import android.text.format.DateFormat;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.google.android.material.picker.TimePicker;
import com.google.android.material.picker.TimePickerDialog;
import java.time.LocalTime;

public class NightDisplaySettings extends DashboardFragment implements NightDisplayListener.Callback {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.night_display_settings) {
        /* class com.android.settings.display.NightDisplaySettings.AnonymousClass1 */

        /* access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return ColorDisplayManager.isNightDisplayAvailable(context);
        }
    };
    private ColorDisplayManager mColorDisplayManager;
    private NightDisplayListener mNightDisplayListener;

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        if (i != 0) {
            return i != 1 ? 0 : 589;
        }
        return 588;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "NightDisplaySettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 488;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Context context = getContext();
        this.mColorDisplayManager = (ColorDisplayManager) context.getSystemService(ColorDisplayManager.class);
        this.mNightDisplayListener = new NightDisplayListener(context);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStart() {
        super.onStart();
        this.mNightDisplayListener.setCallback(this);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStop() {
        super.onStop();
        this.mNightDisplayListener.setCallback((NightDisplayListener.Callback) null);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener, com.android.settings.dashboard.DashboardFragment
    public boolean onPreferenceTreeClick(Preference preference) {
        if ("night_display_end_time".equals(preference.getKey())) {
            writePreferenceClickMetric(preference);
            showDialog(1);
            return true;
        } else if (!"night_display_start_time".equals(preference.getKey())) {
            return super.onPreferenceTreeClick(preference);
        } else {
            writePreferenceClickMetric(preference);
            showDialog(0);
            return true;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateDialog$0 */
    public /* synthetic */ void lambda$onCreateDialog$0$NightDisplaySettings(int i, TimePicker timePicker, int i2, int i3) {
        LocalTime of = LocalTime.of(i2, i3);
        if (i == 0) {
            this.mColorDisplayManager.setNightDisplayCustomStartTime(of);
        } else {
            this.mColorDisplayManager.setNightDisplayCustomEndTime(of);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        LocalTime localTime;
        if (i != 0 && i != 1) {
            return super.onCreateDialog(i);
        }
        if (i == 0) {
            localTime = this.mColorDisplayManager.getNightDisplayCustomStartTime();
        } else {
            localTime = this.mColorDisplayManager.getNightDisplayCustomEndTime();
        }
        Context context = getContext();
        return new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener(i) {
            /* class com.android.settings.display.$$Lambda$NightDisplaySettings$uURjTEkryHsqnZXn9tv34g18Dgs */
            public final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            @Override // com.google.android.material.picker.TimePickerDialog.OnTimeSetListener
            public final void onTimeSet(TimePicker timePicker, int i, int i2) {
                NightDisplaySettings.this.lambda$onCreateDialog$0$NightDisplaySettings(this.f$1, timePicker, i, i2);
            }
        }, localTime.getHour(), localTime.getMinute(), DateFormat.is24HourFormat(context));
    }

    public void onActivated(boolean z) {
        updatePreferenceStates();
    }

    public void onAutoModeChanged(int i) {
        updatePreferenceStates();
    }

    public void onColorTemperatureChanged(int i) {
        updatePreferenceStates();
    }

    public void onCustomStartTimeChanged(LocalTime localTime) {
        updatePreferenceStates();
    }

    public void onCustomEndTimeChanged(LocalTime localTime) {
        updatePreferenceStates();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.night_display_settings;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_night_display;
    }
}
