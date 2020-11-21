package com.oneplus.settings.better;

import android.app.Application;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import androidx.preference.Preference;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.ui.OPSuwPreferenceCategory;
import com.android.settings.ui.RadioButtonPreference;
import com.android.settingslib.search.Indexable$SearchIndexProvider;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.ui.OPScreenColorModeSummary;
import com.oneplus.settings.ui.OPSuwSeekBarPreference;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;

public class OPSuwScreenColorMode extends SettingsPreferenceFragment implements Preference.OnPreferenceClickListener, RadioButtonPreference.OnClickListener, OPSuwSeekBarPreference.OPColorModeSeekBarChangeListener {
    public static final int DEFAULT_COLOR_PROGRESS = (OPUtils.isSupportMMDisplayColorScreenMode() ? 20 : 43);
    public static final Indexable$SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.oneplus.settings.better.OPSuwScreenColorMode.AnonymousClass3 */

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
            if (!OPSuwScreenColorMode.isSupportDcip3) {
                arrayList.add("screen_color_mode_dci_p3_settings");
            }
            if (!OPSuwScreenColorMode.isSupportAdaptive) {
                arrayList.add("screen_color_mode_adaptive_model_settings");
            }
            if (!OPSuwScreenColorMode.isSupportSoft) {
                arrayList.add("screen_color_mode_soft_settings");
            }
            if (OPSuwScreenColorMode.isSupportMMDisplayColor) {
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
    private Context mContext;
    private boolean mDeviceProvision = true;
    private Handler mHandler = new Handler() {
        /* class com.oneplus.settings.better.OPSuwScreenColorMode.AnonymousClass1 */

        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 3) {
                OPSuwScreenColorMode.this.scrollToPreference("screen_color_mode_dci_p3_settings");
            }
        }
    };
    private OPScreenColorModeSummary mOPScreenColorModeSummary;
    private RadioButtonPreference mScreenColorModeAdaptiveModelSettings;
    private RadioButtonPreference mScreenColorModeAdvancedSettingsDisplayP3;
    private RadioButtonPreference mScreenColorModeAdvancedSettingsNTSC;
    private RadioButtonPreference mScreenColorModeAdvancedSettingsSRGB;
    private RadioButtonPreference mScreenColorModeAutoSettings;
    private RadioButtonPreference mScreenColorModeBasicSettings;
    private ContentObserver mScreenColorModeContentObserver = new ContentObserver(new Handler()) {
        /* class com.oneplus.settings.better.OPSuwScreenColorMode.AnonymousClass2 */

        public void onChange(boolean z, Uri uri) {
            boolean z2 = false;
            boolean z3 = Settings.Secure.getInt(OPSuwScreenColorMode.this.getContentResolver(), "night_display_activated", 0) != 1;
            boolean z4 = Settings.System.getInt(OPSuwScreenColorMode.this.getContentResolver(), OPReadingModeTurnOnPreferenceController.READING_MODE_STATUS_MANUAL, 0) != 1;
            if (z3 && z4) {
                z2 = true;
            }
            OPSuwScreenColorMode.this.mScreenColorModeDefaultSettings.setEnabled(z2);
            OPSuwScreenColorMode.this.mScreenColorModeBasicSettings.setEnabled(z2);
            OPSuwScreenColorMode.this.mScreenColorModeDefinedSettings.setEnabled(z2);
            OPSuwScreenColorMode.this.mScreenColorModeDciP3Settings.setEnabled(z2);
            OPSuwScreenColorMode.this.mScreenColorModeAdaptiveModelSettings.setEnabled(z2);
            OPSuwScreenColorMode.this.mScreenColorModeSoftSettings.setEnabled(z2);
            OPSuwScreenColorMode.this.mScreenColorModeAutoSettings.setEnabled(z2);
            OPSuwScreenColorMode.this.mScreenColorModeCustomPreferenceCategory.setEnabled(z2);
            OPSuwScreenColorMode.this.mSeekBarpreference.setEnabled(z2);
            if (OPSuwScreenColorMode.this.mOPScreenColorModeSummary != null) {
                if (!z3) {
                    OPSuwScreenColorMode.this.mOPScreenColorModeSummary.setSummary(SettingsBaseApplication.mApplication.getText(C0017R$string.oneplus_screen_color_mode_title_summary));
                }
                if (!z4) {
                    OPSuwScreenColorMode.this.mOPScreenColorModeSummary.setSummary(SettingsBaseApplication.mApplication.getText(C0017R$string.oneplus_screen_color_mode_reading_mode_on_summary));
                }
                if (!z2) {
                    OPSuwScreenColorMode.this.getPreferenceScreen().addPreference(OPSuwScreenColorMode.this.mOPScreenColorModeSummary);
                } else {
                    OPSuwScreenColorMode.this.getPreferenceScreen().removePreference(OPSuwScreenColorMode.this.mOPScreenColorModeSummary);
                }
            }
        }
    };
    private OPSuwPreferenceCategory mScreenColorModeCustomPreferenceCategory;
    private RadioButtonPreference mScreenColorModeDciP3Settings;
    private RadioButtonPreference mScreenColorModeDefaultSettings;
    private RadioButtonPreference mScreenColorModeDefinedSettings;
    private RadioButtonPreference mScreenColorModeSoftSettings;
    private int mScreenColorModeValue;
    private OPSuwSeekBarPreference mSeekBarpreference;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        return true;
    }

    @Override // com.oneplus.settings.ui.OPSuwSeekBarPreference.OPColorModeSeekBarChangeListener
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.op_suw_screen_color_mode);
        Application application = SettingsBaseApplication.mApplication;
        this.mContext = application;
        application.getPackageManager().hasSystemFeature("oem.read_mode.support");
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
        this.mScreenColorModeCustomPreferenceCategory = (OPSuwPreferenceCategory) findPreference("screen_color_mode_advanced_settings");
        this.mOPScreenColorModeSummary = (OPScreenColorModeSummary) findPreference("oneplus_screen_color_mode_title_summary");
        OPSuwSeekBarPreference oPSuwSeekBarPreference = (OPSuwSeekBarPreference) findPreference("screen_color_mode_seekbar");
        this.mSeekBarpreference = oPSuwSeekBarPreference;
        oPSuwSeekBarPreference.setOPColorModeSeekBarChangeListener(this);
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
            this.mScreenColorModeDefaultSettings.setTitle(C0017R$string.screen_color_mode_vivid);
            this.mScreenColorModeDefinedSettings.setTitle(C0017R$string.screen_color_mode_advanced);
            removePreference("screen_color_mode_adaptive_model_settings");
            removePreference("screen_color_mode_basic_settings");
            removePreference("screen_color_mode_dci_p3_settings");
            removePreference("screen_color_mode_adaptive_model_settings_divider");
            removePreference("screen_color_mode_soft_settings_divider");
            removePreference("oneplus_screen_color_mode_basic_divider");
            removePreference("screen_color_mode_advanced_settings_divider");
            removePreference("screen_color_mode_auto_settings_divider");
        } else {
            removePreference("screen_color_mode_auto_settings");
            removePreference("screen_color_mode_auto_settings_divider");
            getPreferenceScreen().removePreference(this.mScreenColorModeCustomPreferenceCategory);
        }
        getPreferenceScreen().removePreference(this.mOPScreenColorModeSummary);
        boolean hasSystemFeature = this.mContext.getPackageManager().hasSystemFeature("oem.dcip3.support");
        isSupportDcip3 = hasSystemFeature;
        if (!hasSystemFeature) {
            removePreference("screen_color_mode_dci_p3_settings");
            removePreference("oneplus_screen_color_mode_basic_divider");
            removePreference("screen_color_mode_defined_settings_divider");
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
            removePreference("screen_color_mode_soft_settings_divider");
        }
        boolean z = false;
        if (Settings.Global.getInt(getContentResolver(), "device_provisioned", 0) == 1) {
            z = true;
        }
        this.mDeviceProvision = z;
        Log.i("OPScreenColorMode", "mDeviceProvision = " + this.mDeviceProvision);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        setPinnedHeaderView(C0012R$layout.op_suw_screen_color_mode_preview);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        updateRadioButtons(getScreenColorModeSettingsValue());
        getContentResolver().registerContentObserver(Settings.Secure.getUriFor("night_display_activated"), true, this.mScreenColorModeContentObserver, -1);
        getContentResolver().registerContentObserver(Settings.System.getUriFor(OPReadingModeTurnOnPreferenceController.READING_MODE_STATUS_MANUAL), true, this.mScreenColorModeContentObserver, -1);
        updateAdvancedSettingsRadioButtons();
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        getContentResolver().unregisterContentObserver(this.mScreenColorModeContentObserver);
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
            removePreference("screen_color_mode_seekbar");
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

    @Override // com.android.settings.ui.RadioButtonPreference.OnClickListener
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
                    this.mHandler.sendEmptyMessageDelayed(3, 50);
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
        Log.d("OPSuwScreenColorMode", "the screen color mode settings value = " + getScreenColorModeSettingsValue());
    }

    @Override // com.oneplus.settings.ui.OPSuwSeekBarPreference.OPColorModeSeekBarChangeListener
    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        this.mScreenColorModeValue = i;
        Settings.System.putInt(getContentResolver(), "oem_screen_better_value", i);
    }

    @Override // com.oneplus.settings.ui.OPSuwSeekBarPreference.OPColorModeSeekBarChangeListener
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
    }
}
