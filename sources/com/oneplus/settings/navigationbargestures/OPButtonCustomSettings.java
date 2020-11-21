package com.oneplus.settings.navigationbargestures;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.C0003R$array;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.oneplus.common.ReflectUtil;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.quickpay.QuickPaySettings;
import com.oneplus.settings.utils.OPUtils;
import com.oneplus.settings.utils.XmlParseUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OPButtonCustomSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.oneplus.settings.navigationbargestures.OPButtonCustomSettings.AnonymousClass1 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = C0019R$xml.op_button_custom_settings;
            return Arrays.asList(searchIndexableResource);
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            ArrayList arrayList = new ArrayList();
            if (!OPNavigationBarGesturesSettings.isCustomSettingsEnable(context) || OPButtonCustomSettings.isSupportHardwareKeys()) {
                arrayList.addAll(XmlParseUtils.parsePreferenceKeyFromResource(C0019R$xml.op_button_custom_settings, context));
            }
            return arrayList;
        }
    };
    private ListPreference mBackDoubleTapAction;
    private ListPreference mBackLongPressAction;
    private Context mContext;
    private SwitchPreference mHideNavkeys;
    private ListPreference mHomeDoubleTapAction;
    private ListPreference mHomeLongPressAction;
    private ListPreference mMenuDoubleTapAction;
    private ListPreference mMenuLongPressAction;
    private SwitchPreference mSwapNavkeys;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.op_button_custom_settings);
        this.mContext = SettingsBaseApplication.mApplication;
        SwitchPreference switchPreference = (SwitchPreference) findPreference("buttons_swap_navkeys");
        this.mSwapNavkeys = switchPreference;
        switchPreference.setOnPreferenceChangeListener(this);
        SwitchPreference switchPreference2 = (SwitchPreference) findPreference("hide_navkeys");
        this.mHideNavkeys = switchPreference2;
        switchPreference2.setOnPreferenceChangeListener(this);
        if (!ReflectUtil.isFeatureSupported("OP_FEATURE_HIDE_NAVBAR")) {
            this.mHideNavkeys.setVisible(false);
        }
    }

    static boolean isSwipeUpEnabled(Context context) {
        if (!isEdgeToEdgeEnabled(context) && 1 == context.getResources().getInteger(17694854)) {
            return true;
        }
        return false;
    }

    static boolean isEdgeToEdgeEnabled(Context context) {
        return 2 == context.getResources().getInteger(17694854);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        initPrefs();
        boolean z = false;
        this.mSwapNavkeys.setChecked(Settings.System.getInt(getContentResolver(), "oem_acc_key_define", 0) != 0);
        Settings.System.getInt(this.mContext.getContentResolver(), "op_navigation_bar_type", 1);
        if (isSwipeUpEnabled(this.mContext)) {
            this.mHideNavkeys.setEnabled(false);
            this.mMenuLongPressAction.setEnabled(false);
            this.mMenuDoubleTapAction.setEnabled(false);
        } else {
            this.mHideNavkeys.setEnabled(true);
            this.mMenuLongPressAction.setEnabled(true);
            this.mMenuDoubleTapAction.setEnabled(true);
        }
        int i = Settings.System.getInt(this.mContext.getContentResolver(), "op_gesture_button_enabled", 0);
        SwitchPreference switchPreference = this.mHideNavkeys;
        if (switchPreference != null) {
            if (i != 0) {
                z = true;
            }
            switchPreference.setChecked(z);
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
            String string = this.mContext.getString(C0017R$string.hardware_keys_action_shelf);
            OPUtils.removeSomeEntryAndValue(this.mHomeLongPressAction, string);
            OPUtils.removeSomeEntryAndValue(this.mHomeDoubleTapAction, string);
            OPUtils.removeSomeEntryAndValue(this.mMenuLongPressAction, string);
            OPUtils.removeSomeEntryAndValue(this.mMenuDoubleTapAction, string);
            OPUtils.removeSomeEntryAndValue(this.mBackLongPressAction, string);
            OPUtils.removeSomeEntryAndValue(this.mBackDoubleTapAction, string);
        }
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

    /* access modifiers changed from: private */
    public static boolean isSupportHardwareKeys() {
        return !SettingsBaseApplication.mApplication.getResources().getBoolean(17891529);
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

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean z;
        boolean z2 = obj instanceof Boolean;
        if (z2) {
            z = ((Boolean) obj).booleanValue();
        } else {
            z = (obj instanceof String) && Integer.valueOf((String) obj).intValue() != 0;
        }
        SwitchPreference switchPreference = this.mSwapNavkeys;
        if (preference == switchPreference) {
            handleChange(switchPreference, obj, "oem_acc_key_define");
            int i = z ? 1 : 0;
            int i2 = z ? 1 : 0;
            int i3 = z ? 1 : 0;
            int i4 = z ? 1 : 0;
            OPUtils.sendAppTracker("swap_button", i);
            return true;
        }
        SwitchPreference switchPreference2 = this.mHideNavkeys;
        if (preference == switchPreference2) {
            handleChange(switchPreference2, obj, "op_gesture_button_enabled");
            if (z && Settings.System.getInt(getContentResolver(), "op_navigation_bar_type", 0) == 1) {
                OPUtils.sendAppTracker("nav&gestures_settings", 4);
            }
            return true;
        }
        if (z2) {
            ((Boolean) obj).booleanValue();
        } else if (obj instanceof String) {
            Integer.valueOf((String) obj).intValue();
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
        if (preference != listPreference6) {
            return false;
        }
        handleChange(listPreference6, obj, "key_back_double_tap_action");
        return true;
    }
}
