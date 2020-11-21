package com.oneplus.settings;

import android.content.Context;
import android.os.Bundle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.oneplus.settings.gestures.OPQuickTurnOnAssistantAppPreferenceController;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OPButtonsAndGesturesSettings extends DashboardFragment implements Preference.OnPreferenceChangeListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.oneplus.settings.OPButtonsAndGesturesSettings.AnonymousClass1 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            if (!OPUtils.isGuestMode()) {
                searchIndexableResource.xmlResId = C0019R$xml.op_buttons_and_gesture_settings;
            }
            return Arrays.asList(searchIndexableResource);
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            super.getNonIndexableKeys(context);
            ArrayList arrayList = new ArrayList();
            if (OPUtils.isSupportCustomFingerprint()) {
                arrayList.add("op_fingerprint_long_press_camera_shot");
            }
            if (OPUtils.isSurportBackFingerprint(context) && !OPUtils.isSupportGesturePullNotificationBar()) {
                arrayList.add("op_fingerprint_gesture_swipe_down_up");
            }
            if (OPButtonsAndGesturesSettings.isSupportHardwareKeys()) {
                arrayList.add("op_buttons_and_fullscreen_gestures");
            } else {
                arrayList.add("buttons_settings");
            }
            return arrayList;
        }
    };
    private Preference mAlertsliderSettingsPreference;
    private Preference mButtonsAndFullscreenGesturesPreference;
    private Preference mButtonsSettingsPreference;
    private Context mContext;
    private SwitchPreference mFingerprintGestureLongpressCamera;
    private SwitchPreference mFingerprintGestureSwipeDownUp;
    private Preference mLongPressPowerButtonPreference;

    /* access modifiers changed from: private */
    public static boolean isSupportHardwareKeys() {
        return false;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "OPOthersSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        Preference preference;
        super.onCreate(bundle);
        this.mContext = SettingsBaseApplication.mApplication;
        SwitchPreference switchPreference = (SwitchPreference) findPreference("op_fingerprint_long_press_camera_shot");
        this.mFingerprintGestureLongpressCamera = switchPreference;
        switchPreference.setOnPreferenceChangeListener(this);
        SwitchPreference switchPreference2 = (SwitchPreference) findPreference("op_fingerprint_gesture_swipe_down_up");
        this.mFingerprintGestureSwipeDownUp = switchPreference2;
        switchPreference2.setOnPreferenceChangeListener(this);
        if (!OPUtils.isSurportBackFingerprint(this.mContext) || OPUtils.isSupportCustomFingerprint()) {
            this.mFingerprintGestureLongpressCamera.setVisible(false);
            if (!OPUtils.isSupportGesturePullNotificationBar()) {
                this.mFingerprintGestureSwipeDownUp.setVisible(false);
            }
        } else if (!OPUtils.isSupportGesturePullNotificationBar()) {
            this.mFingerprintGestureSwipeDownUp.setVisible(false);
        }
        this.mAlertsliderSettingsPreference = findPreference("op_alertslider_settings_soc_tri_state");
        if (!OPUtils.isSupportSocTriState() && (preference = this.mAlertsliderSettingsPreference) != null) {
            preference.setTitle(C0017R$string.alertslider_settings);
        }
        this.mButtonsAndFullscreenGesturesPreference = findPreference("op_buttons_and_fullscreen_gestures");
        this.mButtonsSettingsPreference = findPreference("buttons_settings");
        if (isSupportHardwareKeys()) {
            this.mButtonsAndFullscreenGesturesPreference.setVisible(false);
        } else {
            this.mButtonsSettingsPreference.setVisible(false);
        }
        this.mLongPressPowerButtonPreference = findPreference("long_press_power_button");
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.op_buttons_and_gesture_settings;
    }

    private void loadPreferenceScreen() {
        boolean z = false;
        Settings.Global.getInt(getContentResolver(), "emergency_affordance_needed", 0);
        SwitchPreference switchPreference = this.mFingerprintGestureLongpressCamera;
        if (switchPreference != null) {
            switchPreference.setChecked(isFingerprintLongpressCameraShotEnabled(this.mContext));
        }
        SwitchPreference switchPreference2 = this.mFingerprintGestureSwipeDownUp;
        if (switchPreference2 != null) {
            switchPreference2.setChecked(isSystemUINavigationEnabled(this.mContext));
        }
        if (this.mLongPressPowerButtonPreference != null) {
            if (Settings.System.getInt(this.mContext.getContentResolver(), "quick_turn_on_voice_assistant", 0) == 1) {
                z = true;
            }
            if (z) {
                this.mLongPressPowerButtonPreference.setSummary(C0017R$string.oneplus_voice_assistant);
            } else {
                this.mLongPressPowerButtonPreference.setSummary(C0017R$string.power_menu);
            }
        }
    }

    private static boolean isSystemUINavigationEnabled(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "system_navigation_keys_enabled", 0) == 1;
    }

    private static boolean isFingerprintLongpressCameraShotEnabled(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "op_fingerprint_long_press_camera_shot", 0) == 1;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        loadPreferenceScreen();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (obj instanceof Boolean) {
            ((Boolean) obj).booleanValue();
        } else if (obj instanceof String) {
            Integer.valueOf((String) obj).intValue();
        }
        if (preference == this.mFingerprintGestureLongpressCamera) {
            Settings.System.putInt(getContentResolver(), "op_fingerprint_long_press_camera_shot", ((Boolean) obj).booleanValue() ? 1 : 0);
            return true;
        } else if (preference != this.mFingerprintGestureSwipeDownUp) {
            return false;
        } else {
            Settings.Secure.putInt(getContentResolver(), "system_navigation_keys_enabled", ((Boolean) obj).booleanValue() ? 1 : 0);
            return true;
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onDestroy() {
        super.onDestroy();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        OPQuickTurnOnAssistantAppPreferenceController oPQuickTurnOnAssistantAppPreferenceController = new OPQuickTurnOnAssistantAppPreferenceController(context, getSettingsLifecycle());
        getSettingsLifecycle().addObserver(oPQuickTurnOnAssistantAppPreferenceController);
        arrayList.add(oPQuickTurnOnAssistantAppPreferenceController);
        return arrayList;
    }
}
