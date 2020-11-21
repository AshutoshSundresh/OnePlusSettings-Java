package com.oneplus.settings.better;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.util.Log;
import android.widget.SeekBar;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.RadioButtonPreference;
import com.android.settingslib.search.Indexable$SearchIndexProvider;
import com.oneplus.settings.OPMemberController;
import com.oneplus.settings.ui.OPScreenColorModeSummary;
import com.oneplus.settings.ui.OPSeekBarPreference;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;

public class OPScreenColorMode extends SettingsPreferenceFragment implements Preference.OnPreferenceClickListener, RadioButtonPreference.OnClickListener, OPSeekBarPreference.OPColorModeSeekBarChangeListener {
    public static final int DEFAULT_COLOR_PROGRESS = (OPUtils.isSupportMMDisplayColorScreenMode() ? 20 : 43);
    public static final Indexable$SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.oneplus.settings.better.OPScreenColorMode.AnonymousClass3 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            ArrayList arrayList = new ArrayList();
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = C0019R$xml.op_screen_color_mode;
            arrayList.add(searchIndexableResource);
            return arrayList;
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            ArrayList arrayList = new ArrayList();
            if (!OPScreenColorMode.isSupportDcip3) {
                arrayList.add("screen_color_mode_dci_p3_settings");
            }
            if (!OPScreenColorMode.isSupportAdaptive) {
                arrayList.add("screen_color_mode_adaptive_model_settings");
            }
            if (!OPScreenColorMode.isSupportSoft) {
                arrayList.add("screen_color_mode_soft_settings");
            }
            if (OPScreenColorMode.isSupportMMDisplayColor) {
                arrayList.add("screen_color_mode_adaptive_model_settings");
                arrayList.add("screen_color_mode_basic_settings");
                arrayList.add("screen_color_mode_dci_p3_settings");
            }
            return arrayList;
        }
    };
    private static boolean isSupportAdaptive;
    private static boolean isSupportDcip3;
    private static boolean isSupportMMDisplayColor = OPUtils.isSupportMMDisplayColorScreenMode();
    private static boolean isSupportSoft;
    private ContentObserver mAccessibilityDisplayDaltonizerAndInversionContentObserver = new ContentObserver(new Handler()) {
        /* class com.oneplus.settings.better.OPScreenColorMode.AnonymousClass2 */
        final Uri accessibilityDisplayDaltonizerEnabledUri = Settings.Secure.getUriFor("accessibility_display_daltonizer_enabled");
        final Uri accessibilityDisplayInversionEnabledUri = Settings.Secure.getUriFor("accessibility_display_inversion_enabled");

        public void onChange(boolean z, Uri uri) {
            super.onChange(z, uri);
            if (this.accessibilityDisplayDaltonizerEnabledUri.equals(uri) || this.accessibilityDisplayInversionEnabledUri.equals(uri)) {
                boolean z2 = false;
                boolean z3 = Settings.Secure.getInt(OPScreenColorMode.this.getContentResolver(), "accessibility_display_daltonizer_enabled", 12) == 1;
                boolean z4 = Settings.Secure.getInt(OPScreenColorMode.this.getContentResolver(), "accessibility_display_inversion_enabled", 0) == 1;
                OPScreenColorMode.this.mScreenColorModeDefaultSettings.setEnabled(!z3 && !z4);
                OPScreenColorMode.this.mScreenColorModeBasicSettings.setEnabled(!z3 && !z4);
                OPScreenColorMode.this.mScreenColorModeDefinedSettings.setEnabled(!z3 && !z4);
                OPScreenColorMode.this.mScreenColorModeDciP3Settings.setEnabled(!z3 && !z4);
                OPScreenColorMode.this.mScreenColorModeAdaptiveModelSettings.setEnabled(!z3 && !z4);
                OPScreenColorMode.this.mScreenColorModeSoftSettings.setEnabled(!z3 && !z4);
                OPScreenColorMode.this.mScreenColorModeAutoSettings.setEnabled(!z3 && !z4);
                OPScreenColorMode.this.mScreenColorModeCustomPreferenceCategory.setEnabled(!z3 && !z4);
                OPSeekBarPreference oPSeekBarPreference = OPScreenColorMode.this.mSeekBarpreference;
                if (!z3 && !z4) {
                    z2 = true;
                }
                oPSeekBarPreference.setEnabled(z2);
            }
        }
    };
    private Context mContext;
    private boolean mDeviceProvision = true;
    private int mEnterAdvancedValue;
    private int mEnterColorModeValue;
    private int mEnterValue;
    private OPScreenColorModeSummary mOPScreenColorModeSummary;
    private RadioButtonPreference mScreenColorModeAdaptiveModelSettings;
    private RadioButtonPreference mScreenColorModeAdvancedSettingsDisplayP3;
    private RadioButtonPreference mScreenColorModeAdvancedSettingsNTSC;
    private RadioButtonPreference mScreenColorModeAdvancedSettingsSRGB;
    private RadioButtonPreference mScreenColorModeAutoSettings;
    private RadioButtonPreference mScreenColorModeBasicSettings;
    private ContentObserver mScreenColorModeContentObserver = new ContentObserver(new Handler()) {
        /* class com.oneplus.settings.better.OPScreenColorMode.AnonymousClass1 */

        public void onChange(boolean z, Uri uri) {
            OPScreenColorMode.this.updatePreferenceStatus();
        }
    };
    private PreferenceCategory mScreenColorModeCustomPreferenceCategory;
    private RadioButtonPreference mScreenColorModeDciP3Settings;
    private RadioButtonPreference mScreenColorModeDefaultSettings;
    private RadioButtonPreference mScreenColorModeDefinedSettings;
    private RadioButtonPreference mScreenColorModeSoftSettings;
    private int mScreenColorModeValue;
    private OPSeekBarPreference mSeekBarpreference;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        return true;
    }

    @Override // com.oneplus.settings.ui.OPSeekBarPreference.OPColorModeSeekBarChangeListener
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.op_screen_color_mode);
        FragmentActivity activity = getActivity();
        this.mContext = activity;
        activity.getPackageManager().hasSystemFeature("oem.read_mode.support");
        this.mScreenColorModeDefaultSettings = (RadioButtonPreference) findPreference("screen_color_mode_default_settings");
        this.mScreenColorModeBasicSettings = (RadioButtonPreference) findPreference("screen_color_mode_basic_settings");
        this.mScreenColorModeDefinedSettings = (RadioButtonPreference) findPreference("screen_color_mode_defined_settings");
        this.mScreenColorModeDciP3Settings = (RadioButtonPreference) findPreference("screen_color_mode_dci_p3_settings");
        this.mScreenColorModeAdaptiveModelSettings = (RadioButtonPreference) findPreference("screen_color_mode_adaptive_model_settings");
        this.mScreenColorModeSoftSettings = (RadioButtonPreference) findPreference("screen_color_mode_soft_settings");
        this.mScreenColorModeAutoSettings = (RadioButtonPreference) findPreference("screen_color_mode_auto_settings");
        this.mScreenColorModeAdvancedSettingsNTSC = (RadioButtonPreference) findPreference("screen_color_mode_advanced_settings_ntsc");
        this.mScreenColorModeAdvancedSettingsSRGB = (RadioButtonPreference) findPreference("screen_color_mode_advanced_settings_srgb");
        this.mScreenColorModeAdvancedSettingsDisplayP3 = (RadioButtonPreference) findPreference("screen_color_mode_advanced_settings_display_p3");
        this.mScreenColorModeCustomPreferenceCategory = (PreferenceCategory) findPreference("screen_color_mode_advanced_settings");
        this.mOPScreenColorModeSummary = (OPScreenColorModeSummary) findPreference("oneplus_screen_color_mode_title_summary");
        OPSeekBarPreference oPSeekBarPreference = (OPSeekBarPreference) findPreference("screen_color_mode_seekbar");
        this.mSeekBarpreference = oPSeekBarPreference;
        oPSeekBarPreference.setOPColorModeSeekBarChangeListener(this);
        this.mScreenColorModeDefaultSettings.setOnClickListener(this);
        this.mScreenColorModeBasicSettings.setOnClickListener(this);
        this.mScreenColorModeDefinedSettings.setOnClickListener(this);
        this.mScreenColorModeDciP3Settings.setOnClickListener(this);
        this.mScreenColorModeAdaptiveModelSettings.setOnClickListener(this);
        this.mScreenColorModeSoftSettings.setOnClickListener(this);
        this.mScreenColorModeAutoSettings.setOnClickListener(this);
        this.mScreenColorModeAdvancedSettingsNTSC.setOnClickListener(this);
        this.mScreenColorModeAdvancedSettingsSRGB.setOnClickListener(this);
        this.mScreenColorModeAdvancedSettingsDisplayP3.setOnClickListener(this);
        if (isSupportMMDisplayColor) {
            removePreference("screen_color_mode_adaptive_model_settings");
            removePreference("screen_color_mode_basic_settings");
            removePreference("screen_color_mode_dci_p3_settings");
        } else {
            this.mScreenColorModeDefaultSettings.setTitle(C0017R$string.oneplus_screen_color_mode_default);
            this.mScreenColorModeDefinedSettings.setTitle(C0017R$string.oneplus_screen_color_mode_defined);
            removePreference("screen_color_mode_auto_settings");
            getPreferenceScreen().removePreference(this.mScreenColorModeCustomPreferenceCategory);
        }
        getPreferenceScreen().removePreference(this.mOPScreenColorModeSummary);
        boolean hasSystemFeature = this.mContext.getPackageManager().hasSystemFeature("oem.dcip3.support");
        isSupportDcip3 = hasSystemFeature;
        if (!hasSystemFeature) {
            removePreference("screen_color_mode_dci_p3_settings");
        }
        boolean hasSystemFeature2 = this.mContext.getPackageManager().hasSystemFeature("oem.display.adaptive.mode.support");
        isSupportAdaptive = hasSystemFeature2;
        if (!hasSystemFeature2) {
            removePreference("screen_color_mode_adaptive_model_settings");
        }
        boolean hasSystemFeature3 = this.mContext.getPackageManager().hasSystemFeature("oem.display.soft.support");
        isSupportSoft = hasSystemFeature3;
        if (!hasSystemFeature3) {
            removePreference("screen_color_mode_soft_settings");
        }
        boolean z = false;
        if (Settings.Global.getInt(getContentResolver(), "device_provisioned", 0) == 1) {
            z = true;
        }
        this.mDeviceProvision = z;
        Log.i("OPScreenColorMode", "mDeviceProvision = " + this.mDeviceProvision);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStop() {
        super.onStop();
        int screenColorModeSettingsValue = getScreenColorModeSettingsValue();
        if (screenColorModeSettingsValue != this.mEnterValue) {
            if (screenColorModeSettingsValue == 1) {
                OPUtils.sendAnalytics("screen_calibration", "status", "1");
            } else if (screenColorModeSettingsValue == 10) {
                OPUtils.sendAnalytics("screen_calibration", "status", "2");
            } else if (screenColorModeSettingsValue == 3) {
                OPUtils.sendAnalytics("screen_calibration", "status", OPMemberController.CLIENT_TYPE);
            }
        }
        int i = Settings.System.getInt(getContentResolver(), "oem_screen_better_value", DEFAULT_COLOR_PROGRESS);
        if (screenColorModeSettingsValue == 3) {
            int intForUser = Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_color_mode_advanced_settings_value", 0, -2);
            if (intForUser != this.mEnterAdvancedValue) {
                if (intForUser == 0) {
                    OPUtils.sendAnalytics("screen_calibration", "advanced", "1");
                } else if (intForUser == 1) {
                    OPUtils.sendAnalytics("screen_calibration", "advanced", "2");
                } else if (intForUser == 2) {
                    OPUtils.sendAnalytics("screen_calibration", "advanced", OPMemberController.CLIENT_TYPE);
                }
            }
            if (i != this.mScreenColorModeValue) {
                double d = (double) i;
                if (d <= ((double) this.mSeekBarpreference.getSeekBarMax()) * 0.33d) {
                    OPUtils.sendAnalytics("screen_calibration", "custom", "1");
                } else if (d <= ((double) this.mSeekBarpreference.getSeekBarMax()) * 0.66d) {
                    OPUtils.sendAnalytics("screen_calibration", "custom", "2");
                } else if (i <= this.mSeekBarpreference.getSeekBarMax()) {
                    OPUtils.sendAnalytics("screen_calibration", "custom", OPMemberController.CLIENT_TYPE);
                }
            }
        }
        if (this.mEnterColorModeValue != i) {
            double d2 = (double) i;
            if (d2 <= ((double) this.mSeekBarpreference.getSeekBarMax()) * 0.33d) {
                OPUtils.sendAnalytics("screen_calibration", "custom", "1");
            } else if (d2 <= ((double) this.mSeekBarpreference.getSeekBarMax()) * 0.66d) {
                OPUtils.sendAnalytics("screen_calibration", "custom", "2");
            } else if (i <= this.mSeekBarpreference.getSeekBarMax()) {
                OPUtils.sendAnalytics("screen_calibration", "custom", OPMemberController.CLIENT_TYPE);
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updatePreferenceStatus() {
        boolean z = false;
        boolean z2 = Settings.Secure.getInt(getContentResolver(), "night_display_activated", 0) != 1;
        boolean z3 = Settings.System.getInt(getContentResolver(), OPReadingModeTurnOnPreferenceController.READING_MODE_STATUS_MANUAL, 0) != 0;
        if (z2 && !z3) {
            z = true;
        }
        this.mScreenColorModeDefaultSettings.setEnabled(z);
        this.mScreenColorModeBasicSettings.setEnabled(z);
        this.mScreenColorModeDefinedSettings.setEnabled(z);
        this.mScreenColorModeDciP3Settings.setEnabled(z);
        this.mScreenColorModeAdaptiveModelSettings.setEnabled(z);
        this.mScreenColorModeSoftSettings.setEnabled(z);
        this.mScreenColorModeAutoSettings.setEnabled(z);
        this.mScreenColorModeCustomPreferenceCategory.setEnabled(z);
        this.mSeekBarpreference.setEnabled(z);
        OPScreenColorModeSummary oPScreenColorModeSummary = this.mOPScreenColorModeSummary;
        if (oPScreenColorModeSummary != null) {
            if (!z2) {
                oPScreenColorModeSummary.setSummary(this.mContext.getText(C0017R$string.oneplus_screen_color_mode_title_summary));
            }
            if (z3) {
                this.mOPScreenColorModeSummary.setSummary(this.mContext.getText(C0017R$string.oneplus_screen_color_mode_reading_mode_on_summary));
            }
            if (!z) {
                getPreferenceScreen().addPreference(this.mOPScreenColorModeSummary);
            } else {
                getPreferenceScreen().removePreference(this.mOPScreenColorModeSummary);
            }
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        updateRadioButtons(getScreenColorModeSettingsValue());
        getContentResolver().registerContentObserver(Settings.Secure.getUriFor("night_display_activated"), true, this.mScreenColorModeContentObserver, -1);
        getContentResolver().registerContentObserver(Settings.System.getUriFor(OPReadingModeTurnOnPreferenceController.READING_MODE_STATUS_MANUAL), true, this.mScreenColorModeContentObserver, -1);
        getContentResolver().registerContentObserver(Settings.Secure.getUriFor("accessibility_display_daltonizer_enabled"), true, this.mAccessibilityDisplayDaltonizerAndInversionContentObserver, -1);
        getContentResolver().registerContentObserver(Settings.Secure.getUriFor("accessibility_display_inversion_enabled"), true, this.mAccessibilityDisplayDaltonizerAndInversionContentObserver, -1);
        updatePreferenceStatus();
        updateAdvancedSettingsRadioButtons();
        this.mScreenColorModeValue = Settings.System.getInt(getContentResolver(), "oem_screen_better_value", DEFAULT_COLOR_PROGRESS);
        this.mEnterValue = getScreenColorModeSettingsValue();
        this.mEnterAdvancedValue = Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_color_mode_advanced_settings_value", 0, -2);
        this.mEnterColorModeValue = Settings.System.getInt(this.mContext.getContentResolver(), "oem_screen_better_value", 0);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        getContentResolver().unregisterContentObserver(this.mScreenColorModeContentObserver);
        getContentResolver().unregisterContentObserver(this.mAccessibilityDisplayDaltonizerAndInversionContentObserver);
    }

    private void updateAdvancedSettingsRadioButtons() {
        boolean z = false;
        int intForUser = Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_color_mode_advanced_settings_value", 0, -2);
        this.mScreenColorModeAdvancedSettingsNTSC.setChecked(intForUser == 0);
        this.mScreenColorModeAdvancedSettingsSRGB.setChecked(intForUser == 1);
        RadioButtonPreference radioButtonPreference = this.mScreenColorModeAdvancedSettingsDisplayP3;
        if (intForUser == 2) {
            z = true;
        }
        radioButtonPreference.setChecked(z);
    }

    private void updateRadioButtons(int i) {
        if (1 == i) {
            this.mScreenColorModeDefaultSettings.setChecked(true);
            this.mScreenColorModeBasicSettings.setChecked(false);
            this.mScreenColorModeDefinedSettings.setChecked(false);
            this.mScreenColorModeDciP3Settings.setChecked(false);
            this.mScreenColorModeAdaptiveModelSettings.setChecked(false);
            this.mScreenColorModeSoftSettings.setChecked(false);
            this.mScreenColorModeAutoSettings.setChecked(false);
            removePreference("screen_color_mode_seekbar");
            if (this.mScreenColorModeCustomPreferenceCategory != null) {
                getPreferenceScreen().removePreference(this.mScreenColorModeCustomPreferenceCategory);
            }
        } else if (2 == i) {
            this.mScreenColorModeDefaultSettings.setChecked(false);
            this.mScreenColorModeBasicSettings.setChecked(true);
            this.mScreenColorModeDefinedSettings.setChecked(false);
            this.mScreenColorModeDciP3Settings.setChecked(false);
            this.mScreenColorModeAdaptiveModelSettings.setChecked(false);
            this.mScreenColorModeSoftSettings.setChecked(false);
            this.mScreenColorModeAutoSettings.setChecked(false);
            removePreference("screen_color_mode_seekbar");
            if (this.mScreenColorModeCustomPreferenceCategory != null) {
                getPreferenceScreen().removePreference(this.mScreenColorModeCustomPreferenceCategory);
            }
        } else if (3 == i) {
            this.mScreenColorModeDefaultSettings.setChecked(false);
            this.mScreenColorModeBasicSettings.setChecked(false);
            this.mScreenColorModeDefinedSettings.setChecked(true);
            this.mScreenColorModeDciP3Settings.setChecked(false);
            this.mScreenColorModeAdaptiveModelSettings.setChecked(false);
            this.mScreenColorModeAutoSettings.setChecked(false);
            getPreferenceScreen().addPreference(this.mSeekBarpreference);
            if (this.mScreenColorModeCustomPreferenceCategory != null && isSupportMMDisplayColor) {
                getPreferenceScreen().addPreference(this.mScreenColorModeCustomPreferenceCategory);
            }
        } else if (4 == i) {
            this.mScreenColorModeDefaultSettings.setChecked(false);
            this.mScreenColorModeBasicSettings.setChecked(false);
            this.mScreenColorModeDefinedSettings.setChecked(false);
            this.mScreenColorModeDciP3Settings.setChecked(true);
            this.mScreenColorModeAdaptiveModelSettings.setChecked(false);
            this.mScreenColorModeSoftSettings.setChecked(false);
            this.mScreenColorModeAutoSettings.setChecked(false);
            if (this.mScreenColorModeCustomPreferenceCategory != null) {
                getPreferenceScreen().removePreference(this.mScreenColorModeCustomPreferenceCategory);
            }
        } else if (5 == i) {
            this.mScreenColorModeDefaultSettings.setChecked(false);
            this.mScreenColorModeBasicSettings.setChecked(false);
            this.mScreenColorModeDefinedSettings.setChecked(false);
            this.mScreenColorModeDciP3Settings.setChecked(false);
            this.mScreenColorModeAdaptiveModelSettings.setChecked(true);
            this.mScreenColorModeSoftSettings.setChecked(false);
            this.mScreenColorModeAutoSettings.setChecked(false);
            removePreference("screen_color_mode_seekbar");
            if (this.mScreenColorModeCustomPreferenceCategory != null) {
                getPreferenceScreen().removePreference(this.mScreenColorModeCustomPreferenceCategory);
            }
        } else if (6 == i) {
            this.mScreenColorModeDefaultSettings.setChecked(false);
            this.mScreenColorModeBasicSettings.setChecked(false);
            this.mScreenColorModeDefinedSettings.setChecked(false);
            this.mScreenColorModeDciP3Settings.setChecked(false);
            this.mScreenColorModeAdaptiveModelSettings.setChecked(false);
            this.mScreenColorModeSoftSettings.setChecked(true);
            this.mScreenColorModeAutoSettings.setChecked(false);
            removePreference("screen_color_mode_seekbar");
            if (this.mScreenColorModeCustomPreferenceCategory != null) {
                getPreferenceScreen().removePreference(this.mScreenColorModeCustomPreferenceCategory);
            }
        } else if (10 == i) {
            this.mScreenColorModeDefaultSettings.setChecked(false);
            this.mScreenColorModeBasicSettings.setChecked(false);
            this.mScreenColorModeDefinedSettings.setChecked(false);
            this.mScreenColorModeDciP3Settings.setChecked(false);
            this.mScreenColorModeAdaptiveModelSettings.setChecked(false);
            this.mScreenColorModeSoftSettings.setChecked(false);
            this.mScreenColorModeAutoSettings.setChecked(true);
            removePreference("screen_color_mode_seekbar");
            if (this.mScreenColorModeCustomPreferenceCategory != null) {
                getPreferenceScreen().removePreference(this.mScreenColorModeCustomPreferenceCategory);
            }
        }
    }

    @Override // com.android.settings.widget.RadioButtonPreference.OnClickListener
    public void onRadioButtonClicked(RadioButtonPreference radioButtonPreference) {
        if (radioButtonPreference == null) {
            this.mScreenColorModeDefaultSettings.setChecked(false);
            this.mScreenColorModeBasicSettings.setChecked(false);
            this.mScreenColorModeDefinedSettings.setChecked(false);
            this.mScreenColorModeDciP3Settings.setChecked(false);
            this.mScreenColorModeAdaptiveModelSettings.setChecked(false);
            this.mScreenColorModeSoftSettings.setChecked(false);
            this.mScreenColorModeAutoSettings.setChecked(false);
        } else {
            RadioButtonPreference radioButtonPreference2 = this.mScreenColorModeDefaultSettings;
            if (radioButtonPreference == radioButtonPreference2) {
                radioButtonPreference2.setChecked(true);
                this.mScreenColorModeBasicSettings.setChecked(false);
                this.mScreenColorModeDefinedSettings.setChecked(false);
                this.mScreenColorModeDciP3Settings.setChecked(false);
                this.mScreenColorModeAdaptiveModelSettings.setChecked(false);
                this.mScreenColorModeSoftSettings.setChecked(false);
                this.mScreenColorModeAutoSettings.setChecked(false);
                if (getScreenColorModeSettingsValue() != 1) {
                    onSaveScreenColorModeSettingsValue(1);
                }
                removePreference("screen_color_mode_seekbar");
                if (this.mScreenColorModeCustomPreferenceCategory != null) {
                    getPreferenceScreen().removePreference(this.mScreenColorModeCustomPreferenceCategory);
                }
            } else if (radioButtonPreference == this.mScreenColorModeBasicSettings) {
                radioButtonPreference2.setChecked(false);
                this.mScreenColorModeBasicSettings.setChecked(true);
                this.mScreenColorModeDefinedSettings.setChecked(false);
                this.mScreenColorModeDciP3Settings.setChecked(false);
                this.mScreenColorModeAdaptiveModelSettings.setChecked(false);
                this.mScreenColorModeSoftSettings.setChecked(false);
                this.mScreenColorModeAutoSettings.setChecked(false);
                if (getScreenColorModeSettingsValue() != 2) {
                    onSaveScreenColorModeSettingsValue(2);
                }
                removePreference("screen_color_mode_seekbar");
                if (this.mScreenColorModeCustomPreferenceCategory != null) {
                    getPreferenceScreen().removePreference(this.mScreenColorModeCustomPreferenceCategory);
                }
            } else if (radioButtonPreference == this.mScreenColorModeDefinedSettings) {
                radioButtonPreference2.setChecked(false);
                this.mScreenColorModeBasicSettings.setChecked(false);
                this.mScreenColorModeDefinedSettings.setChecked(true);
                this.mScreenColorModeDciP3Settings.setChecked(false);
                this.mScreenColorModeAdaptiveModelSettings.setChecked(false);
                this.mScreenColorModeSoftSettings.setChecked(false);
                this.mScreenColorModeAutoSettings.setChecked(false);
                if (getScreenColorModeSettingsValue() != 3) {
                    onSaveScreenColorModeSettingsValue(3);
                }
                getPreferenceScreen().addPreference(this.mSeekBarpreference);
                if (this.mScreenColorModeCustomPreferenceCategory != null && isSupportMMDisplayColor) {
                    getPreferenceScreen().addPreference(this.mScreenColorModeCustomPreferenceCategory);
                }
            } else if (radioButtonPreference == this.mScreenColorModeDciP3Settings) {
                radioButtonPreference2.setChecked(false);
                this.mScreenColorModeBasicSettings.setChecked(false);
                this.mScreenColorModeDefinedSettings.setChecked(false);
                this.mScreenColorModeDciP3Settings.setChecked(true);
                this.mScreenColorModeAdaptiveModelSettings.setChecked(false);
                this.mScreenColorModeSoftSettings.setChecked(false);
                this.mScreenColorModeAutoSettings.setChecked(false);
                if (getScreenColorModeSettingsValue() != 4) {
                    onSaveScreenColorModeSettingsValue(4);
                }
                removePreference("screen_color_mode_seekbar");
                if (this.mScreenColorModeCustomPreferenceCategory != null) {
                    getPreferenceScreen().removePreference(this.mScreenColorModeCustomPreferenceCategory);
                }
            } else if (radioButtonPreference == this.mScreenColorModeAdaptiveModelSettings) {
                radioButtonPreference2.setChecked(false);
                this.mScreenColorModeBasicSettings.setChecked(false);
                this.mScreenColorModeDefinedSettings.setChecked(false);
                this.mScreenColorModeDciP3Settings.setChecked(false);
                this.mScreenColorModeAdaptiveModelSettings.setChecked(true);
                this.mScreenColorModeSoftSettings.setChecked(false);
                this.mScreenColorModeAutoSettings.setChecked(false);
                if (getScreenColorModeSettingsValue() != 5) {
                    onSaveScreenColorModeSettingsValue(5);
                }
                removePreference("screen_color_mode_seekbar");
                if (this.mScreenColorModeCustomPreferenceCategory != null) {
                    getPreferenceScreen().removePreference(this.mScreenColorModeCustomPreferenceCategory);
                }
            } else if (radioButtonPreference == this.mScreenColorModeSoftSettings) {
                radioButtonPreference2.setChecked(false);
                this.mScreenColorModeBasicSettings.setChecked(false);
                this.mScreenColorModeDefinedSettings.setChecked(false);
                this.mScreenColorModeDciP3Settings.setChecked(false);
                this.mScreenColorModeAdaptiveModelSettings.setChecked(false);
                this.mScreenColorModeSoftSettings.setChecked(true);
                this.mScreenColorModeAutoSettings.setChecked(false);
                if (getScreenColorModeSettingsValue() != 6) {
                    onSaveScreenColorModeSettingsValue(6);
                }
                removePreference("screen_color_mode_seekbar");
                if (this.mScreenColorModeCustomPreferenceCategory != null) {
                    getPreferenceScreen().removePreference(this.mScreenColorModeCustomPreferenceCategory);
                }
            } else if (radioButtonPreference == this.mScreenColorModeAutoSettings) {
                radioButtonPreference2.setChecked(false);
                this.mScreenColorModeBasicSettings.setChecked(false);
                this.mScreenColorModeDefinedSettings.setChecked(false);
                this.mScreenColorModeDciP3Settings.setChecked(false);
                this.mScreenColorModeAdaptiveModelSettings.setChecked(false);
                this.mScreenColorModeSoftSettings.setChecked(false);
                this.mScreenColorModeAutoSettings.setChecked(true);
                if (getScreenColorModeSettingsValue() != 10) {
                    onSaveScreenColorModeSettingsValue(10);
                }
                removePreference("screen_color_mode_seekbar");
                if (this.mScreenColorModeCustomPreferenceCategory != null) {
                    getPreferenceScreen().removePreference(this.mScreenColorModeCustomPreferenceCategory);
                }
            } else if (radioButtonPreference == this.mScreenColorModeAdvancedSettingsNTSC) {
                Settings.System.putIntForUser(getContentResolver(), "screen_color_mode_advanced_settings_value", 0, -2);
                this.mScreenColorModeAdvancedSettingsNTSC.setChecked(true);
                this.mScreenColorModeAdvancedSettingsSRGB.setChecked(false);
                this.mScreenColorModeAdvancedSettingsDisplayP3.setChecked(false);
            } else if (radioButtonPreference == this.mScreenColorModeAdvancedSettingsSRGB) {
                Settings.System.putIntForUser(getContentResolver(), "screen_color_mode_advanced_settings_value", 1, -2);
                this.mScreenColorModeAdvancedSettingsNTSC.setChecked(false);
                this.mScreenColorModeAdvancedSettingsSRGB.setChecked(true);
                this.mScreenColorModeAdvancedSettingsDisplayP3.setChecked(false);
            } else if (radioButtonPreference == this.mScreenColorModeAdvancedSettingsDisplayP3) {
                Settings.System.putIntForUser(getContentResolver(), "screen_color_mode_advanced_settings_value", 2, -2);
                this.mScreenColorModeAdvancedSettingsNTSC.setChecked(false);
                this.mScreenColorModeAdvancedSettingsSRGB.setChecked(false);
                this.mScreenColorModeAdvancedSettingsDisplayP3.setChecked(true);
            }
        }
        Log.d("ScreenColorMode", "the screen color mode settings value = " + getScreenColorModeSettingsValue());
    }

    @Override // com.oneplus.settings.ui.OPSeekBarPreference.OPColorModeSeekBarChangeListener
    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        this.mScreenColorModeValue = i;
        Settings.System.putInt(getContentResolver(), "oem_screen_better_value", i);
    }

    @Override // com.oneplus.settings.ui.OPSeekBarPreference.OPColorModeSeekBarChangeListener
    public void onStopTrackingTouch(SeekBar seekBar) {
        onSaveScreenColorModeValue(this.mScreenColorModeValue);
    }

    public int getScreenColorModeSettingsValue() {
        return Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_color_mode_settings_value", 1, -2);
    }

    public void onSaveScreenColorModeSettingsValue(int i) {
        Settings.System.putIntForUser(getContentResolver(), "screen_color_mode_settings_value", i, -2);
    }

    public void onSaveScreenColorModeValue(int i) {
        Settings.System.putInt(getContentResolver(), "oem_screen_better_value", i);
        OPUtils.sendAppTrackerForScreenCustomColorMode();
    }
}
