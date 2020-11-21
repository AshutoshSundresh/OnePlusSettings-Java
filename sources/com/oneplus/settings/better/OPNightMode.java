package com.oneplus.settings.better;

import android.app.Dialog;
import android.content.Context;
import android.database.ContentObserver;
import android.hardware.display.ColorDisplayManager;
import android.hardware.display.DisplayManager;
import android.hardware.display.NightDisplayListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import androidx.constraintlayout.widget.R$styleable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.google.android.material.picker.TimePicker;
import com.google.android.material.picker.TimePickerDialog;
import com.oneplus.settings.OPMemberController;
import com.oneplus.settings.OneplusColorManager;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.ui.OPNightModeLevelPreferenceCategory;
import com.oneplus.settings.utils.OPUtils;
import java.text.DateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class OPNightMode extends SettingsPreferenceFragment implements NightDisplayListener.Callback, Preference.OnPreferenceChangeListener, OPNightModeLevelPreferenceCategory.OPNightModeLevelPreferenceChangeListener {
    public static final int DEFAULT_COLOR_PROGRESS = (OPUtils.isSupportMMDisplayColorScreenMode() ? 29 : R$styleable.Constraint_layout_goneMarginTop);
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.oneplus.settings.better.OPNightMode.AnonymousClass7 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = C0019R$xml.op_night_mode;
            return Arrays.asList(searchIndexableResource);
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            return new ArrayList();
        }
    };
    private ListPreference mAutoActivatePreference;
    private ColorDisplayManager mColorDisplayManager;
    private int mEnterAutoOpenValue;
    private float mEnterBrightnessValue;
    private int mEnterScreenColorValue;
    private NightDisplayListener mNightDisplayListener;
    private SwitchPreference mNightModeEnabledPreference;
    private OPNightModeLevelPreferenceCategory mNightModeLevelPreferenceCategory;
    private ContentObserver mNightModeSeekBarContentObserver = new ContentObserver(new Handler()) {
        /* class com.oneplus.settings.better.OPNightMode.AnonymousClass6 */
        final Uri grayscaleUri = Settings.System.getUriFor("accessibility_display_grayscale_enabled");

        public void onChange(boolean z, Uri uri) {
            Settings.System.getIntForUser(OPNightMode.this.getContentResolver(), "oem_nightmode_brightness_progress", 0, -2);
            if (this.grayscaleUri.equals(uri)) {
                OPNightMode.this.disableEntryForWellbeingGrayscale();
            }
            OPUtils.sendAppTrackerForEffectStrength();
        }
    };
    private DateFormat mTimeFormatter;
    private Preference mTurnOffTimePreference;
    private Preference mTurnOnTimePreference;

    public static int convertAutoMode(int i) {
        if (i == 0) {
            return 0;
        }
        return i == 1 ? 2 : 1;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        return 9999;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // com.oneplus.settings.ui.OPNightModeLevelPreferenceCategory.OPNightModeLevelPreferenceChangeListener
    public void onBrightnessStartTrackingTouch(int i) {
    }

    @Override // com.oneplus.settings.ui.OPNightModeLevelPreferenceCategory.OPNightModeLevelPreferenceChangeListener
    public void onColorStartTrackingTouch(int i) {
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.op_night_mode);
        Context context = getContext();
        context.getPackageManager().hasSystemFeature("oem.read_mode.support");
        this.mColorDisplayManager = (ColorDisplayManager) context.getSystemService(ColorDisplayManager.class);
        this.mNightDisplayListener = new NightDisplayListener(context);
        this.mNightModeEnabledPreference = (SwitchPreference) findPreference("night_mode_enabled");
        this.mAutoActivatePreference = (ListPreference) findPreference("auto_activate");
        this.mTurnOnTimePreference = findPreference("turn_on_time");
        this.mTurnOffTimePreference = findPreference("turn_off_time");
        this.mNightModeLevelPreferenceCategory = (OPNightModeLevelPreferenceCategory) findPreference("night_mode_level_op");
        SwitchPreference switchPreference = this.mNightModeEnabledPreference;
        if (switchPreference != null) {
            switchPreference.setOnPreferenceChangeListener(this);
        }
        OPNightModeLevelPreferenceCategory oPNightModeLevelPreferenceCategory = this.mNightModeLevelPreferenceCategory;
        if (oPNightModeLevelPreferenceCategory != null) {
            oPNightModeLevelPreferenceCategory.setOPNightModeLevelSeekBarChangeListener(this);
        }
        this.mAutoActivatePreference.setValue(String.valueOf(this.mColorDisplayManager.getNightDisplayAutoMode()));
        this.mAutoActivatePreference.setOnPreferenceChangeListener(this);
        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);
        this.mTimeFormatter = timeFormat;
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        updateAutoActivateModePreferenceDescription(convertAutoMode(this.mColorDisplayManager.getNightDisplayAutoMode()));
        new OneplusColorManager(SettingsBaseApplication.mApplication);
        DisplayManager displayManager = (DisplayManager) getSystemService("display");
        Settings.System.getIntForUser(getContentResolver(), OPReadingModeTurnOnPreferenceController.READING_MODE_STATUS, 0, -2);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStart() {
        super.onStart();
        this.mNightDisplayListener.setCallback(this);
        onActivated(this.mColorDisplayManager.isNightDisplayActivated());
        onAutoModeChanged(this.mColorDisplayManager.getNightDisplayAutoMode());
        onCustomStartTimeChanged(this.mColorDisplayManager.getNightDisplayCustomStartTime());
        onCustomEndTimeChanged(this.mColorDisplayManager.getNightDisplayCustomEndTime());
        getContentResolver().registerContentObserver(Settings.System.getUriFor(OPReadingModeTurnOnPreferenceController.READING_MODE_STATUS), true, this.mNightModeSeekBarContentObserver, -2);
        getContentResolver().registerContentObserver(Settings.Secure.getUriFor("night_display_activated"), true, this.mNightModeSeekBarContentObserver, -2);
        getContentResolver().registerContentObserver(Settings.System.getUriFor("accessibility_display_grayscale_enabled"), true, this.mNightModeSeekBarContentObserver, -2);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStop() {
        super.onStop();
        this.mNightDisplayListener.setCallback((NightDisplayListener.Callback) null);
        getContentResolver().unregisterContentObserver(this.mNightModeSeekBarContentObserver);
        if (this.mEnterAutoOpenValue != convertAutoMode(this.mColorDisplayManager.getNightDisplayAutoMode())) {
            OPUtils.sendAnalytics("night_mode", "auto_open", String.valueOf(convertAutoMode(this.mColorDisplayManager.getNightDisplayAutoMode())));
        }
        int intForUser = Settings.System.getIntForUser(getContentResolver(), "oem_nightmode_progress_status", DEFAULT_COLOR_PROGRESS, -2);
        if (this.mEnterScreenColorValue != intForUser) {
            double d = (double) intForUser;
            if (d <= ((double) this.mNightModeLevelPreferenceCategory.getColorProgressMax()) * 0.33d) {
                OPUtils.sendAnalytics("night_mode", "screen_color", "1");
            } else if (d <= ((double) this.mNightModeLevelPreferenceCategory.getColorProgressMax()) * 0.66d) {
                OPUtils.sendAnalytics("night_mode", "screen_color", "2");
            } else if (intForUser <= this.mNightModeLevelPreferenceCategory.getColorProgressMax()) {
                OPUtils.sendAnalytics("night_mode", "screen_color", OPMemberController.CLIENT_TYPE);
            }
        }
        float floatForUser = Settings.System.getFloatForUser(getContentResolver(), "oem_nightmode_brightness_progress", 0.0f, -2);
        if (this.mEnterBrightnessValue != floatForUser) {
            double d2 = (double) floatForUser;
            if (d2 <= ((double) this.mNightModeLevelPreferenceCategory.getBrightnessProgressMax()) * 0.33d) {
                OPUtils.sendAnalytics("night_mode", "brightness", "1");
            } else if (d2 <= ((double) this.mNightModeLevelPreferenceCategory.getBrightnessProgressMax()) * 0.66d) {
                OPUtils.sendAnalytics("night_mode", "brightness", "2");
            } else if (floatForUser <= ((float) this.mNightModeLevelPreferenceCategory.getBrightnessProgressMax())) {
                OPUtils.sendAnalytics("night_mode", "brightness", OPMemberController.CLIENT_TYPE);
            }
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        onActivated(this.mColorDisplayManager.isNightDisplayActivated());
        disableEntryForWellbeingGrayscale();
        this.mEnterAutoOpenValue = convertAutoMode(this.mColorDisplayManager.getNightDisplayAutoMode());
        this.mEnterScreenColorValue = Settings.System.getIntForUser(getContentResolver(), "oem_nightmode_progress_status", DEFAULT_COLOR_PROGRESS, -2);
        this.mEnterBrightnessValue = Settings.System.getFloatForUser(getContentResolver(), "oem_nightmode_brightness_progress", 0.0f, -2);
    }

    public void onActivated(boolean z) {
        this.mNightModeEnabledPreference.setChecked(z);
        disableEntryForWellbeingGrayscale();
    }

    public void onAutoModeChanged(int i) {
        this.mAutoActivatePreference.setValue(String.valueOf(i));
        boolean z = true;
        if (i != 1) {
            z = false;
        }
        this.mTurnOnTimePreference.setVisible(z);
        this.mTurnOffTimePreference.setVisible(z);
    }

    private void updateAutoActivateModePreferenceDescription(int i) {
        ListPreference listPreference = this.mAutoActivatePreference;
        if (listPreference != null) {
            this.mAutoActivatePreference.setSummary(listPreference.getEntries()[i]);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void disableEntryForWellbeingGrayscale() {
        boolean z = true;
        boolean z2 = Settings.System.getInt(getContentResolver(), "accessibility_display_grayscale_enabled", 1) == 0;
        boolean z3 = Settings.Secure.getIntForUser(getContentResolver(), "night_display_activated", 0, -2) == 1;
        SwitchPreference switchPreference = this.mNightModeEnabledPreference;
        if (switchPreference != null) {
            switchPreference.setEnabled(!z2);
        }
        ListPreference listPreference = this.mAutoActivatePreference;
        if (listPreference != null) {
            listPreference.setEnabled(!z2);
        }
        OPNightModeLevelPreferenceCategory oPNightModeLevelPreferenceCategory = this.mNightModeLevelPreferenceCategory;
        if (oPNightModeLevelPreferenceCategory != null) {
            if (!z3 || z2) {
                z = false;
            }
            oPNightModeLevelPreferenceCategory.setEnabled(z);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        String key = preference.getKey();
        if ("night_mode_enabled".equals(key)) {
            boolean booleanValue = ((Boolean) obj).booleanValue();
            OPUtils.sendAppTrackerForNightMode();
            this.mNightModeLevelPreferenceCategory.setEnabled(booleanValue);
            int colorProgress = this.mNightModeLevelPreferenceCategory.getColorProgress();
            int brightnessProgress = this.mNightModeLevelPreferenceCategory.getBrightnessProgress();
            if (booleanValue) {
                Log.d("OPNightMode", "onPreferenceChange colorProgress:" + colorProgress + " brightnessProgress:" + brightnessProgress);
                this.mColorDisplayManager.setNightDisplayActivated(true);
                saveColorTemperatureProgress(colorProgress);
                saveBrightnessProgress(brightnessProgress);
            } else {
                this.mColorDisplayManager.setNightDisplayActivated(false);
            }
        } else if ("auto_activate".equals(key)) {
            this.mColorDisplayManager.setNightDisplayAutoMode(Integer.parseInt((String) obj));
            updateAutoActivateModePreferenceDescription(convertAutoMode(this.mColorDisplayManager.getNightDisplayAutoMode()));
        }
        return true;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        String key = preference.getKey();
        if ("turn_on_time".equals(key)) {
            showDialog(0);
            return true;
        } else if (!"turn_off_time".equals(key)) {
            return super.onPreferenceTreeClick(preference);
        } else {
            showDialog(1);
            return true;
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(final int i) {
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
        return new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            /* class com.oneplus.settings.better.OPNightMode.AnonymousClass1 */

            @Override // com.google.android.material.picker.TimePickerDialog.OnTimeSetListener
            public void onTimeSet(TimePicker timePicker, int i, int i2) {
                LocalTime of = LocalTime.of(i, i2);
                if (i == 0) {
                    if (String.valueOf(OPNightMode.this.mColorDisplayManager.getNightDisplayCustomEndTime()).equals(String.valueOf(of))) {
                        Toast.makeText(OPNightMode.this.getPrefContext(), C0017R$string.timepower_time_duplicate, 1).show();
                    } else {
                        OPNightMode.this.mColorDisplayManager.setNightDisplayCustomStartTime(of);
                    }
                } else if (String.valueOf(OPNightMode.this.mColorDisplayManager.getNightDisplayCustomStartTime()).equals(String.valueOf(of))) {
                    Toast.makeText(OPNightMode.this.getPrefContext(), C0017R$string.timepower_time_duplicate, 1).show();
                } else {
                    OPNightMode.this.mColorDisplayManager.setNightDisplayCustomEndTime(of);
                }
            }
        }, localTime.getHour(), localTime.getMinute(), android.text.format.DateFormat.is24HourFormat(context));
    }

    private String getFormattedTimeString(LocalTime localTime) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeZone(this.mTimeFormatter.getTimeZone());
        instance.set(11, localTime.getHour());
        instance.set(12, localTime.getMinute());
        instance.set(13, 0);
        instance.set(14, 0);
        return this.mTimeFormatter.format(instance.getTime());
    }

    public void onCustomStartTimeChanged(LocalTime localTime) {
        this.mTurnOnTimePreference.setSummary(getFormattedTimeString(localTime));
    }

    public void onCustomEndTimeChanged(LocalTime localTime) {
        this.mTurnOffTimePreference.setSummary(getFormattedTimeString(localTime));
    }

    @Override // com.oneplus.settings.ui.OPNightModeLevelPreferenceCategory.OPNightModeLevelPreferenceChangeListener
    public void onColorProgressChanged(int i, boolean z) {
        if (z) {
            saveColorTemperatureProgress(i);
        }
    }

    @Override // com.oneplus.settings.ui.OPNightModeLevelPreferenceCategory.OPNightModeLevelPreferenceChangeListener
    public void onColorStopTrackingTouch(int i) {
        saveColorTemperatureProgress(i);
    }

    @Override // com.oneplus.settings.ui.OPNightModeLevelPreferenceCategory.OPNightModeLevelPreferenceChangeListener
    public void onBrightnessProgressChanged(int i, boolean z) {
        if (z) {
            saveBrightnessProgress(i);
        }
    }

    @Override // com.oneplus.settings.ui.OPNightModeLevelPreferenceCategory.OPNightModeLevelPreferenceChangeListener
    public void onBrightnessStopTrackingTouch(int i) {
        onBrightnessProgressChanged(i, true);
    }

    private void saveColorTemperatureProgress(int i) {
        Settings.System.putIntForUser(getContentResolver(), "oem_nightmode_progress_status", i, -2);
    }

    private void saveBrightnessProgress(int i) {
        Settings.System.putIntForUser(getContentResolver(), "oem_nightmode_brightness_progress", i, -2);
    }
}
