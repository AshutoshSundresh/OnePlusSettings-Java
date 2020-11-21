package com.oneplus.settings;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.Window;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.C0003R$array;
import com.android.settings.C0011R$integer;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.oneplus.settings.quickpay.QuickPaySettings;
import com.oneplus.settings.utils.OPUtils;

public class OPButtonsSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private ListPreference mBackDoubleTapAction;
    private ListPreference mBackLongPressAction;
    private SwitchPreference mCameraDoubleTapPowerGesturePreference;
    private SwitchPreference mDisableNavKeysBrightness;
    private SwitchPreference mEnableOnScreenNavkeys;
    private SwitchPreference mForceHomeButtonEnabled;
    private Handler mHandler;
    private SwitchPreference mHideNavkeys;
    private ListPreference mHomeDoubleTapAction;
    private ListPreference mHomeLongPressAction;
    private ListPreference mMenuDoubleTapAction;
    private ListPreference mMenuLongPressAction;
    private final SettingsObserver mSettingsObserver = new SettingsObserver(this.mHandler);
    private SwitchPreference mSwapNavkeys;
    private Window mWindow;

    public enum KeyLockMode {
        NORMAL,
        POWER,
        POWER_HOME,
        HOME,
        FOOT,
        BACK_SWITCH,
        BASE
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mWindow = getActivity().getWindow();
        addPreferencesFromResource(C0019R$xml.op_buttons_settings);
        this.mHandler = new Handler() {
            /* class com.oneplus.settings.OPButtonsSettings.AnonymousClass1 */

            public void handleMessage(Message message) {
                super.handleMessage(message);
                if (message.what == 1) {
                    OPButtonsSettings.this.loadPreferenceScreen();
                }
            }
        };
        SwitchPreference switchPreference = (SwitchPreference) findPreference("buttons_enable_on_screen_navkeys");
        this.mEnableOnScreenNavkeys = switchPreference;
        switchPreference.setOnPreferenceChangeListener(this);
        SwitchPreference switchPreference2 = (SwitchPreference) findPreference("buttons_swap_navkeys");
        this.mSwapNavkeys = switchPreference2;
        switchPreference2.setOnPreferenceChangeListener(this);
        SwitchPreference switchPreference3 = (SwitchPreference) findPreference("buttons_brightness");
        this.mDisableNavKeysBrightness = switchPreference3;
        switchPreference3.setOnPreferenceChangeListener(this);
        SwitchPreference switchPreference4 = (SwitchPreference) findPreference("buttons_force_home");
        this.mForceHomeButtonEnabled = switchPreference4;
        switchPreference4.setOnPreferenceChangeListener(this);
        removePreference("camera_double_tap_power_gesture");
        SwitchPreference switchPreference5 = (SwitchPreference) findPreference("hide_navkeys");
        this.mHideNavkeys = switchPreference5;
        switchPreference5.setOnPreferenceChangeListener(this);
    }

    private void initPrefs() {
        initListViewPrefs();
        if (!checkGMS(getContext())) {
            initListViewPrefsnogms();
        } else if (!QuickPaySettings.canShowQuickPay(getContext())) {
            if (isSupportHardwareKeys()) {
                this.mHomeLongPressAction.setEntries(C0003R$array.hardware_keys_action_entries);
                this.mHomeLongPressAction.setEntryValues(C0003R$array.hardware_keys_action_values);
            } else {
                this.mHomeLongPressAction.setEntries(C0003R$array.navigation_bar_keys_action_entries);
                this.mHomeLongPressAction.setEntryValues(C0003R$array.navigation_bar_keys_action_values);
            }
        } else if (isSupportHardwareKeys()) {
            this.mHomeLongPressAction.setEntries(C0003R$array.hardware_keys_action_entries_quickpay);
            this.mHomeLongPressAction.setEntryValues(C0003R$array.hardware_keys_action_values_quickpay);
        } else {
            this.mHomeLongPressAction.setEntries(C0003R$array.navigation_bar_keys_action_entries_quickpay);
            this.mHomeLongPressAction.setEntryValues(C0003R$array.navigation_bar_keys_action_values_quickpay);
        }
        if (!OPUtils.methodIsMigrated(SettingsBaseApplication.mApplication)) {
            String string = getContext().getString(C0017R$string.hardware_keys_action_shelf);
            OPUtils.removeSomeEntryAndValue(this.mHomeLongPressAction, string);
            OPUtils.removeSomeEntryAndValue(this.mHomeDoubleTapAction, string);
            OPUtils.removeSomeEntryAndValue(this.mMenuLongPressAction, string);
            OPUtils.removeSomeEntryAndValue(this.mMenuDoubleTapAction, string);
            OPUtils.removeSomeEntryAndValue(this.mBackLongPressAction, string);
            OPUtils.removeSomeEntryAndValue(this.mBackDoubleTapAction, string);
        }
    }

    public static boolean checkGMS(Context context) {
        try {
            context.getPackageManager().getApplicationInfo("com.google.android.googlequicksearchbox", 8192);
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        initPrefs();
        this.mSettingsObserver.setListening(true);
        loadPreferenceScreen();
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 100 && Settings.System.getInt(getContentResolver(), "op_gesture_guide_completed", 0) == 0) {
            Settings.System.putInt(getContentResolver(), "op_gesture_button_enabled", 0);
            OPUtils.sendAppTracker("op_fullscreen_gesture_enabled", false);
            delayEnableHideNavkey();
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        this.mSettingsObserver.setListening(false);
    }

    private ListPreference initActionList(String str, int i) {
        ListPreference listPreference = (ListPreference) getPreferenceScreen().findPreference(str);
        listPreference.setValue(Integer.toString(i));
        listPreference.setSummary(listPreference.getEntry());
        listPreference.setOnPreferenceChangeListener(this);
        return listPreference;
    }

    private void handleChange(Object obj, Object obj2, String str) {
        if (obj instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) obj;
            String str2 = (String) obj2;
            listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue(str2)]);
            Settings.System.putInt(getContentResolver(), str, Integer.valueOf(str2).intValue());
        } else if (obj instanceof SwitchPreference) {
            boolean z = false;
            z = false;
            if (obj2 instanceof Boolean) {
                z = ((Boolean) obj2).booleanValue();
            } else if ((obj2 instanceof String) && Integer.valueOf((String) obj2).intValue() != 0) {
                z = true;
            }
            ContentResolver contentResolver = getContentResolver();
            int i = z ? 1 : 0;
            int i2 = z ? 1 : 0;
            int i3 = z ? 1 : 0;
            int i4 = z ? 1 : 0;
            int i5 = z ? 1 : 0;
            Settings.System.putInt(contentResolver, str, i);
        }
    }

    private boolean isSupportHardwareKeys() {
        return !SettingsBaseApplication.mApplication.getResources().getBoolean(17891529);
    }

    private void initListViewPrefsnogms() {
        if (!QuickPaySettings.canShowQuickPay(getContext())) {
            if (isSupportHardwareKeys()) {
                this.mHomeLongPressAction.setEntries(C0003R$array.hardware_keys_action_entries_nogms);
                this.mHomeLongPressAction.setEntryValues(C0003R$array.hardware_keys_action_values_nogms);
            } else {
                this.mHomeLongPressAction.setEntries(C0003R$array.navigation_bar_keys_action_entries_nogms);
                this.mHomeLongPressAction.setEntryValues(C0003R$array.navigation_bar_keys_action_values_nogms);
            }
        } else if (isSupportHardwareKeys()) {
            this.mHomeLongPressAction.setEntries(C0003R$array.hardware_keys_action_entries_nogms_quickpay);
            this.mHomeLongPressAction.setEntryValues(C0003R$array.hardware_keys_action_values_nogms_quickpay);
        } else {
            this.mHomeLongPressAction.setEntries(C0003R$array.navigation_bar_keys_action_entries_nogms_quickpay);
            this.mHomeLongPressAction.setEntryValues(C0003R$array.navigation_bar_keys_action_values_nogms_quickpay);
        }
        if (isSupportHardwareKeys()) {
            this.mHomeDoubleTapAction.setEntries(C0003R$array.hardware_keys_action_entries_nogms);
            this.mHomeDoubleTapAction.setEntryValues(C0003R$array.hardware_keys_action_values_nogms);
            this.mMenuLongPressAction.setEntries(C0003R$array.hardware_keys_action_entries_nogms);
            this.mMenuLongPressAction.setEntryValues(C0003R$array.hardware_keys_action_values_nogms);
            this.mMenuDoubleTapAction.setEntries(C0003R$array.hardware_keys_action_entries_nogms);
            this.mMenuDoubleTapAction.setEntryValues(C0003R$array.hardware_keys_action_values_nogms);
            this.mBackLongPressAction.setEntries(C0003R$array.hardware_keys_action_entries_nogms);
            this.mBackLongPressAction.setEntryValues(C0003R$array.hardware_keys_action_values_nogms);
            this.mBackDoubleTapAction.setEntries(C0003R$array.hardware_keys_action_entries_nogms);
            this.mBackDoubleTapAction.setEntryValues(C0003R$array.hardware_keys_action_values_nogms);
            return;
        }
        this.mHomeDoubleTapAction.setEntries(C0003R$array.navigation_bar_keys_action_entries_nogms);
        this.mHomeDoubleTapAction.setEntryValues(C0003R$array.navigation_bar_keys_action_values_nogms);
        this.mMenuLongPressAction.setEntries(C0003R$array.navigation_bar_keys_action_entries_nogms);
        this.mMenuLongPressAction.setEntryValues(C0003R$array.navigation_bar_keys_action_values_nogms);
        this.mMenuDoubleTapAction.setEntries(C0003R$array.navigation_bar_keys_action_entries_nogms);
        this.mMenuDoubleTapAction.setEntryValues(C0003R$array.navigation_bar_keys_action_values_nogms);
        this.mBackLongPressAction.setEntries(C0003R$array.navigation_bar_keys_action_entries_nogms);
        this.mBackLongPressAction.setEntryValues(C0003R$array.navigation_bar_keys_action_values_nogms);
        this.mBackDoubleTapAction.setEntries(C0003R$array.navigation_bar_keys_action_entries_nogms);
        this.mBackDoubleTapAction.setEntryValues(C0003R$array.navigation_bar_keys_action_values_nogms);
        this.mBackLongPressAction.setEntries(C0003R$array.navigation_bar_keys_action_entries_nogms);
        this.mBackLongPressAction.setEntryValues(C0003R$array.navigation_bar_keys_action_values_nogms);
    }

    private void initListViewPrefs() {
        ContentResolver contentResolver = SettingsBaseApplication.mApplication.getContentResolver();
        this.mHomeLongPressAction = initActionList("hardware_keys_home_long_press", Settings.System.getInt(contentResolver, "key_home_long_press_action", getActivity().getResources().getInteger(17694828)));
        this.mHomeDoubleTapAction = initActionList("hardware_keys_home_double_tap", Settings.System.getInt(contentResolver, "key_home_double_tap_action", getActivity().getResources().getInteger(17694803)));
        this.mMenuLongPressAction = initActionList("hardware_keys_menu_long_press", Settings.System.getInt(contentResolver, "key_app_switch_long_press_action", getActivity().getResources().getInteger(84475926)));
        this.mMenuDoubleTapAction = initActionList("hardware_keys_menu_double_tap", Settings.System.getInt(contentResolver, "key_app_switch_double_tap_action", getActivity().getResources().getInteger(84475915)));
        this.mBackLongPressAction = initActionList("hardware_keys_back_long_press", Settings.System.getInt(contentResolver, "key_back_long_press_action", getActivity().getResources().getInteger(84475927)));
        this.mBackDoubleTapAction = initActionList("hardware_keys_back_double_tap", Settings.System.getInt(contentResolver, "key_back_double_tap_action", getActivity().getResources().getInteger(84475916)));
        if (!isSupportHardwareKeys()) {
            ListPreference listPreference = this.mHomeLongPressAction;
            if (listPreference != null) {
                listPreference.setEntries(C0003R$array.navigation_bar_keys_action_entries);
                this.mHomeLongPressAction.setEntryValues(C0003R$array.navigation_bar_keys_action_values);
            }
            ListPreference listPreference2 = this.mHomeDoubleTapAction;
            if (listPreference2 != null) {
                listPreference2.setEntries(C0003R$array.navigation_bar_keys_action_entries);
                this.mHomeDoubleTapAction.setEntryValues(C0003R$array.navigation_bar_keys_action_values);
            }
            ListPreference listPreference3 = this.mMenuLongPressAction;
            if (listPreference3 != null) {
                listPreference3.setEntries(C0003R$array.navigation_bar_keys_action_entries);
                this.mMenuLongPressAction.setEntryValues(C0003R$array.navigation_bar_keys_action_values);
            }
            ListPreference listPreference4 = this.mMenuDoubleTapAction;
            if (listPreference4 != null) {
                listPreference4.setEntries(C0003R$array.navigation_bar_keys_action_entries);
                this.mMenuDoubleTapAction.setEntryValues(C0003R$array.navigation_bar_keys_action_values);
            }
            ListPreference listPreference5 = this.mBackLongPressAction;
            if (listPreference5 != null) {
                listPreference5.setEntries(C0003R$array.navigation_bar_keys_action_entries);
                this.mBackLongPressAction.setEntryValues(C0003R$array.navigation_bar_keys_action_values);
            }
            ListPreference listPreference6 = this.mBackDoubleTapAction;
            if (listPreference6 != null) {
                listPreference6.setEntries(C0003R$array.navigation_bar_keys_action_entries);
                this.mBackDoubleTapAction.setEntryValues(C0003R$array.navigation_bar_keys_action_values);
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void loadPreferenceScreen() {
        Application application = SettingsBaseApplication.mApplication;
        if (application != null) {
            ContentResolver contentResolver = application.getContentResolver();
            boolean z = false;
            boolean z2 = Settings.System.getInt(contentResolver, "buttons_brightness", SettingsBaseApplication.mApplication.getResources().getInteger(C0011R$integer.config_buttonBrightnessSettingDefault)) != 0;
            boolean z3 = Settings.System.getInt(contentResolver, "buttons_show_on_screen_navkeys", 0) != 0;
            boolean z4 = Settings.System.getInt(contentResolver, "buttons_force_home_enabled", 0) != 0;
            this.mSwapNavkeys.setChecked(Settings.System.getInt(getContentResolver(), "oem_acc_key_define", 0) != 0);
            this.mDisableNavKeysBrightness.setChecked(z2);
            this.mEnableOnScreenNavkeys.setChecked(z3);
            this.mEnableOnScreenNavkeys.setEnabled(Settings.System.getInt(contentResolver, "oem_acc_key_define", KeyLockMode.NORMAL.ordinal()) != KeyLockMode.FOOT.ordinal());
            this.mForceHomeButtonEnabled.setChecked(z4);
            PreferenceScreen preferenceScreen = getPreferenceScreen();
            PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference("home_key");
            PreferenceCategory preferenceCategory2 = (PreferenceCategory) preferenceScreen.findPreference("menu_key");
            PreferenceCategory preferenceCategory3 = (PreferenceCategory) preferenceScreen.findPreference("back_key");
            SwitchPreference switchPreference = this.mDisableNavKeysBrightness;
            if (switchPreference != null) {
                switchPreference.setEnabled(!z3);
            }
            if (isSupportHardwareKeys()) {
                SwitchPreference switchPreference2 = this.mForceHomeButtonEnabled;
                if (switchPreference2 != null) {
                    switchPreference2.setEnabled(z3);
                }
                removePreference("hide_navkeys");
            } else {
                removePreference("buttons_brightness");
                removePreference("buttons_enable_on_screen_navkeys");
                removePreference("buttons_force_home");
                int i = Settings.System.getInt(getContentResolver(), "op_navigation_bar_type", 1);
                this.mHideNavkeys.setChecked(i != 0);
                boolean z5 = i == 3;
                SwitchPreference switchPreference3 = this.mSwapNavkeys;
                if (switchPreference3 != null) {
                    switchPreference3.setEnabled(!z5);
                }
                if (preferenceCategory != null) {
                    preferenceCategory.setEnabled(!z5);
                }
                if (preferenceCategory2 != null) {
                    preferenceCategory2.setEnabled(!z5);
                }
                if (preferenceCategory3 != null) {
                    preferenceCategory3.setEnabled(!z5);
                }
            }
            if (OPUtils.isSurportNavigationBarOnly(SettingsBaseApplication.mApplication)) {
                removePreference("buttons_enable_on_screen_navkeys");
                removePreference("hide_navkeys");
            } else {
                removePreference("key_navigation_bar_type");
            }
            if (this.mCameraDoubleTapPowerGesturePreference != null) {
                int i2 = Settings.Secure.getInt(getContentResolver(), "camera_double_tap_power_gesture_disabled", 0);
                SwitchPreference switchPreference4 = this.mCameraDoubleTapPowerGesturePreference;
                if (i2 == 0) {
                    z = true;
                }
                switchPreference4.setChecked(z);
            }
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean z;
        if (obj instanceof Boolean) {
            z = ((Boolean) obj).booleanValue();
        } else {
            z = (obj instanceof String) && Integer.valueOf((String) obj).intValue() != 0;
        }
        SwitchPreference switchPreference = this.mSwapNavkeys;
        if (preference == switchPreference) {
            handleChange(switchPreference, obj, "oem_acc_key_define");
            return true;
        }
        ListPreference listPreference = this.mHomeLongPressAction;
        if (preference == listPreference) {
            handleChange(listPreference, obj, "key_home_long_press_action");
            if ("11".equals((String) obj)) {
                QuickPaySettings.gotoQuickPaySettingsPage(getActivity());
            }
            return true;
        }
        ListPreference listPreference2 = this.mHomeDoubleTapAction;
        if (preference == listPreference2) {
            handleChange(listPreference2, obj, "key_home_double_tap_action");
            return true;
        }
        ListPreference listPreference3 = this.mMenuLongPressAction;
        if (preference == listPreference3) {
            handleChange(listPreference3, obj, "key_app_switch_long_press_action");
            return true;
        }
        ListPreference listPreference4 = this.mMenuDoubleTapAction;
        if (preference == listPreference4) {
            handleChange(listPreference4, obj, "key_app_switch_double_tap_action");
            return true;
        }
        ListPreference listPreference5 = this.mBackLongPressAction;
        if (preference == listPreference5) {
            handleChange(listPreference5, obj, "key_back_long_press_action");
            return true;
        }
        ListPreference listPreference6 = this.mBackDoubleTapAction;
        if (preference == listPreference6) {
            handleChange(listPreference6, obj, "key_back_double_tap_action");
            return true;
        } else if (preference == this.mDisableNavKeysBrightness) {
            Helper.setHWButtonsLightsState(getActivity(), z, false);
            loadPreferenceScreen();
            return true;
        } else if (preference == this.mEnableOnScreenNavkeys) {
            Helper.updateSettings(getActivity(), z);
            OPUtils.sendAppTracker("buttons_enable_on_screen_navkeys", z);
            return true;
        } else {
            SwitchPreference switchPreference2 = this.mForceHomeButtonEnabled;
            if (preference == switchPreference2) {
                handleChange(switchPreference2, obj, "buttons_force_home_enabled");
                loadPreferenceScreen();
                return true;
            } else if (preference == this.mCameraDoubleTapPowerGesturePreference) {
                Settings.Secure.putInt(getContentResolver(), "camera_double_tap_power_gesture_disabled", !((Boolean) obj).booleanValue());
                return true;
            } else if (preference != this.mHideNavkeys) {
                return false;
            } else {
                if (Settings.System.getInt(getContentResolver(), "op_gesture_guide_completed", 0) == 0) {
                    startActivityForResult(new Intent("oneplus.intent.action.ONEPLUS_FULLSCREEN_GESTURE_GUIDE"), 100);
                } else {
                    boolean booleanValue = ((Boolean) obj).booleanValue();
                    Settings.System.putInt(getContentResolver(), "op_gesture_button_enabled", booleanValue ? 1 : 0);
                    OPUtils.sendAppTracker("op_fullscreen_gesture_enabled", booleanValue);
                    delayEnableHideNavkey();
                }
                return true;
            }
        }
    }

    private void delayEnableHideNavkey() {
        this.mHideNavkeys.setEnabled(false);
        this.mHandler.postDelayed(new Runnable() {
            /* class com.oneplus.settings.OPButtonsSettings.AnonymousClass2 */

            public void run() {
                OPButtonsSettings.this.mHideNavkeys.setEnabled(true);
                OPButtonsSettings.this.loadPreferenceScreen();
            }
        }, 1000);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference == this.mEnableOnScreenNavkeys) {
            OPUtils.setLightNavigationBar(this.mWindow, OPUtils.getThemeMode(getContentResolver()));
            this.mEnableOnScreenNavkeys.setEnabled(false);
            this.mHandler.postDelayed(new Runnable() {
                /* class com.oneplus.settings.OPButtonsSettings.AnonymousClass3 */

                public void run() {
                    OPButtonsSettings.this.mEnableOnScreenNavkeys.setEnabled(true);
                }
            }, 1000);
        }
        return super.onPreferenceTreeClick(preference);
    }

    public static class Helper {
        public static void updateSettings(Context context, boolean z) {
            updateSettings(context, z, true, false, false);
        }

        public static void updateSettings(Context context, boolean z, boolean z2, boolean z3, boolean z4) {
            if (!z3) {
                Settings.System.putInt(context.getContentResolver(), "buttons_show_on_screen_navkeys", z ? 1 : 0);
            }
            if (z2) {
                if (!z4) {
                    setHWKeysState(context, z);
                }
                setHWButtonsLightsState(context, !z ? 1 : 0, true);
            }
        }

        public static void setHWButtonsLightsState(Context context, boolean z, boolean z2) {
            int integer = context.getResources().getInteger(C0011R$integer.config_buttonBrightnessSettingDefault);
            if (z2) {
                SharedPreferences sharedPreferences = context.getSharedPreferences("pre_navbar_button_backlight", 0);
                SharedPreferences.Editor edit = sharedPreferences.edit();
                if (!z) {
                    int i = Settings.System.getInt(context.getContentResolver(), "buttons_brightness", integer);
                    if (!sharedPreferences.contains("pre_navbar_button_backlight")) {
                        edit.putInt("pre_navbar_button_backlight", i);
                    }
                    Settings.System.putInt(context.getContentResolver(), "buttons_brightness", 0);
                } else {
                    int i2 = sharedPreferences.getInt("pre_navbar_button_backlight", -1);
                    if (i2 != -1) {
                        Settings.System.putInt(context.getContentResolver(), "buttons_brightness", i2);
                        edit.remove("pre_navbar_button_backlight");
                    }
                }
                edit.commit();
                return;
            }
            ContentResolver contentResolver = context.getContentResolver();
            if (!z) {
                integer = 0;
            }
            Settings.System.putInt(contentResolver, "buttons_brightness", integer);
        }

        private static void setHWKeysState(Context context, boolean z) {
            setHWKeysState(context, z, false);
        }

        private static void setHWKeysState(Context context, boolean z, boolean z2) {
            Settings.System.putInt(context.getContentResolver(), "oem_acc_key_define", z ? z2 ? 4 : 5 : 0);
        }
    }

    private final class SettingsObserver extends ContentObserver {
        private final Uri OEM_EYECARE_ENABLE_URI = Settings.System.getUriFor("oem_acc_key_define");

        public SettingsObserver(Handler handler) {
            super(handler);
        }

        public void onChange(boolean z, Uri uri) {
            if (this.OEM_EYECARE_ENABLE_URI.equals(uri)) {
                OPButtonsSettings.this.mHandler.sendEmptyMessageDelayed(1, 1000);
            }
        }

        public void setListening(boolean z) {
            ContentResolver contentResolver = OPButtonsSettings.this.getContentResolver();
            if (z) {
                contentResolver.registerContentObserver(this.OEM_EYECARE_ENABLE_URI, false, this);
            } else {
                contentResolver.unregisterContentObserver(this);
            }
        }
    }
}
