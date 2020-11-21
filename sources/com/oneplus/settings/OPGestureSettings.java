package com.oneplus.settings;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.MasterSwitchPreference;
import com.android.settingslib.search.Indexable$SearchIndexProvider;
import com.oneplus.common.ReflectUtil;
import com.oneplus.settings.gestures.OPGestureUtils;
import com.oneplus.settings.ui.OPGesturePreference;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;

public class OPGestureSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private static final Uri OEM_ACC_SENSOR_ROTATE_SILENT_URI = Settings.System.getUriFor("oem_acc_sensor_rotate_silent");
    public static final Indexable$SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new OPGestureSearchIndexProvider();
    private int isDoubleClickEnable;
    private int isFlashlightEnable;
    private int isMusicControlEnable;
    private int isMusicPlayEnable;
    private int isStartUpCameraEnable;
    private boolean isSupportThreeScrrenShot = false;
    private PreferenceCategory mBlackScreenPrefererce;
    private int mBlackSettingValues;
    private SwitchPreference mCameraPerference;
    private ContentObserver mContentObserver = new ContentObserver(new Handler()) {
        /* class com.oneplus.settings.OPGestureSettings.AnonymousClass1 */

        public void onChange(boolean z, Uri uri) {
            super.onChange(z, uri);
            if (OPGestureSettings.OEM_ACC_SENSOR_ROTATE_SILENT_URI.equals(uri) && OPGestureSettings.this.mRotationSilent != null) {
                boolean z2 = false;
                if (Settings.System.getInt(OPGestureSettings.this.getActivity().getContentResolver(), "oem_acc_sensor_rotate_silent", 0) != 0) {
                    z2 = true;
                }
                OPGestureSettings.this.mRotationSilent.setChecked(z2);
            }
        }
    };
    private Context mContext;
    private SwitchPreference mDoubleLightScreenPreference;
    private OPGesturePreference mDrawMStartAppPreference;
    private OPGesturePreference mDrawOStartAppPreference;
    private OPGesturePreference mDrawSStartAppPreference;
    private OPGesturePreference mDrawVStartAppPreference;
    private OPGesturePreference mDrawWStartAppPreference;
    private PreferenceCategory mFingerprintGestureCategory;
    private SwitchPreference mFlashLightPreference;
    private SwitchPreference mMusicControlPreference;
    private SwitchPreference mMusicNextPreference;
    private SwitchPreference mMusicPausePreference;
    private SwitchPreference mMusicPrevPreference;
    private SwitchPreference mMusicStartPreference;
    private MasterSwitchPreference mOneHandedMode;
    private SwitchPreference mRotationSilent;
    private SwitchPreference mThreeSwipeScreenShot;
    private UserManager mUm;
    private PreferenceScreen root;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mUm = (UserManager) getSystemService("user");
        addPreferencesFromResource(C0019R$xml.op_gesture_settings);
        this.mContext = getActivity();
        initFingerprintGesture();
        initBlackScreenView();
        initGestureViews();
        initSensorView();
    }

    private void initSensorView() {
        this.root = getPreferenceScreen();
        this.isSupportThreeScrrenShot = this.mContext.getPackageManager().hasSystemFeature("oem.threeScreenshot.support");
        SwitchPreference switchPreference = (SwitchPreference) findPreference("three_screenshots_enable");
        this.mThreeSwipeScreenShot = switchPreference;
        switchPreference.setOnPreferenceClickListener(this);
        MasterSwitchPreference masterSwitchPreference = (MasterSwitchPreference) findPreference("one_hand_mode");
        this.mOneHandedMode = masterSwitchPreference;
        masterSwitchPreference.setOnPreferenceChangeListener(this);
        if (!ReflectUtil.isFeatureSupported("OP_FEATURE_ONE_HAND_MODE") || OPUtils.isBeta()) {
            this.mOneHandedMode.setVisible(false);
        }
        SwitchPreference switchPreference2 = (SwitchPreference) findPreference("rotation_silent_enable");
        this.mRotationSilent = switchPreference2;
        switchPreference2.setOnPreferenceClickListener(this);
        boolean z = true;
        this.mThreeSwipeScreenShot.setChecked(Settings.System.getInt(getActivity().getContentResolver(), "oem_acc_sensor_three_finger", 0) != 0);
        SwitchPreference switchPreference3 = this.mRotationSilent;
        if (Settings.System.getInt(getActivity().getContentResolver(), "oem_acc_sensor_rotate_silent", 0) == 0) {
            z = false;
        }
        switchPreference3.setChecked(z);
        if (!this.isSupportThreeScrrenShot) {
            this.root.removePreference(this.mThreeSwipeScreenShot);
        }
        if (!OPUtils.hasOnePlusDialer(this.mContext)) {
            this.mRotationSilent.setVisible(false);
        }
    }

    private void initFingerprintGesture() {
        this.mFingerprintGestureCategory = (PreferenceCategory) findPreference("fingerprint_gesture_control");
        getPreferenceScreen().removePreference(this.mFingerprintGestureCategory);
    }

    private void initGestureViews() {
        this.mDrawOStartAppPreference = (OPGesturePreference) findPreference("oneplus_draw_o_start_app");
        this.mDrawVStartAppPreference = (OPGesturePreference) findPreference("oneplus_draw_v_start_app");
        this.mDrawSStartAppPreference = (OPGesturePreference) findPreference("oneplus_draw_s_start_app");
        this.mDrawMStartAppPreference = (OPGesturePreference) findPreference("oneplus_draw_m_start_app");
        this.mDrawWStartAppPreference = (OPGesturePreference) findPreference("oneplus_draw_w_start_app");
        if (!OPUtils.isSurportGesture20(this.mContext)) {
            this.mBlackScreenPrefererce.removePreference(this.mDrawOStartAppPreference);
            this.mBlackScreenPrefererce.removePreference(this.mDrawVStartAppPreference);
            this.mBlackScreenPrefererce.removePreference(this.mDrawSStartAppPreference);
            this.mBlackScreenPrefererce.removePreference(this.mDrawMStartAppPreference);
            this.mBlackScreenPrefererce.removePreference(this.mDrawWStartAppPreference);
        }
    }

    private void initGestureSummary() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            this.mDrawOStartAppPreference.setSummary(OPGestureUtils.getGestureSummarybyGestureKey(activity, "oneplus_draw_o_start_app"));
            this.mDrawVStartAppPreference.setSummary(OPGestureUtils.getGestureSummarybyGestureKey(activity, "oneplus_draw_v_start_app"));
            this.mDrawSStartAppPreference.setSummary(OPGestureUtils.getGestureSummarybyGestureKey(activity, "oneplus_draw_s_start_app"));
            this.mDrawMStartAppPreference.setSummary(OPGestureUtils.getGestureSummarybyGestureKey(activity, "oneplus_draw_m_start_app"));
            this.mDrawWStartAppPreference.setSummary(OPGestureUtils.getGestureSummarybyGestureKey(activity, "oneplus_draw_w_start_app"));
        }
    }

    private void initBlackScreenView() {
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("motion_sensor__control");
        String str = "gesture_to_answer_call";
        findPreference(str);
        findPreference("gesture_to_control_calls");
        if (!OPUtils.supportGestureAudioRoute()) {
            str = "gesture_to_control_calls";
        }
        removePreference(str);
        if (!OPUtils.hasOnePlusDialer(this.mContext)) {
            removePreference("gesture_to_control_calls");
        }
        PreferenceCategory preferenceCategory2 = (PreferenceCategory) findPreference("quick_startup");
        PreferenceCategory preferenceCategory3 = (PreferenceCategory) findPreference("music_control");
        this.mBlackScreenPrefererce = (PreferenceCategory) findPreference("black_screen_gestures");
        SwitchPreference switchPreference = (SwitchPreference) findPreference("draw_o_start_camera_key");
        this.mCameraPerference = switchPreference;
        switchPreference.setOnPreferenceClickListener(this);
        if (OPUtils.isSurportGesture20(this.mContext)) {
            this.mBlackScreenPrefererce.removePreference(this.mCameraPerference);
        }
        SwitchPreference switchPreference2 = (SwitchPreference) findPreference("double_click_light_screen_key");
        this.mDoubleLightScreenPreference = switchPreference2;
        switchPreference2.setOnPreferenceClickListener(this);
        SwitchPreference switchPreference3 = (SwitchPreference) findPreference("music_control_key");
        this.mMusicControlPreference = switchPreference3;
        switchPreference3.setOnPreferenceClickListener(this);
        SwitchPreference switchPreference4 = (SwitchPreference) findPreference("open_light_device_key");
        this.mFlashLightPreference = switchPreference4;
        switchPreference4.setOnPreferenceClickListener(this);
        if (OPUtils.isSurportGesture20(this.mContext)) {
            this.mBlackScreenPrefererce.removePreference(this.mFlashLightPreference);
        }
        getConfig();
        boolean z = false;
        if (!OPUtils.isSurportGesture20(this.mContext)) {
            this.mCameraPerference.setChecked(this.isStartUpCameraEnable != 0);
        }
        this.mDoubleLightScreenPreference.setChecked(this.isDoubleClickEnable != 0);
        this.mMusicControlPreference.setChecked(this.isMusicControlEnable != 0);
        if (!OPUtils.isSurportGesture20(this.mContext)) {
            SwitchPreference switchPreference5 = this.mFlashLightPreference;
            if (this.isFlashlightEnable != 0) {
                z = true;
            }
            switchPreference5.setChecked(z);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        initGestureSummary();
        boolean z = false;
        int intForUser = Settings.System.getIntForUser(this.mContext.getContentResolver(), "op_one_hand_mode_setting", 0, -2);
        MasterSwitchPreference masterSwitchPreference = this.mOneHandedMode;
        if (intForUser == 1) {
            z = true;
        }
        masterSwitchPreference.setChecked(z);
        getContentResolver().registerContentObserver(Settings.System.getUriFor("oem_acc_sensor_rotate_silent"), true, this.mContentObserver, -1);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        getContentResolver().unregisterContentObserver(this.mContentObserver);
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals("draw_o_start_camera_key")) {
            if (this.mCameraPerference.isChecked()) {
                OPGestureUtils.set1(this.mContext, 6);
            } else {
                OPGestureUtils.set0(this.mContext, 6);
            }
            return true;
        } else if (preference.getKey().equals("double_click_light_screen_key")) {
            if (this.mDoubleLightScreenPreference.isChecked()) {
                if (!OPUtils.isSupportTapCoexist() && OPUtils.isSupportCustomFingerprint() && OPGestureUtils.get(Settings.System.getInt(getContext().getContentResolver(), "oem_acc_blackscreen_gestrue_enable", 0), 11) == 1) {
                    Toast.makeText(getContext(), C0017R$string.oneplus_security_settings_fingerprint_toggle_two_toast_1, 0).show();
                    OPGestureUtils.set0(getContext(), 11);
                    OPUtils.sendAppTracker("tap_screen_show", 0);
                }
                OPGestureUtils.set1(this.mContext, 7);
            } else {
                OPGestureUtils.set0(this.mContext, 7);
            }
            return true;
        } else if (preference.getKey().equals("music_control_key")) {
            toggleMusicController(this.mMusicControlPreference.isChecked());
            return true;
        } else if (preference.getKey().equals("music_control_next_key")) {
            if (this.mMusicNextPreference.isChecked()) {
                OPGestureUtils.set1(this.mContext, 3);
            } else {
                OPGestureUtils.set0(this.mContext, 3);
            }
            return true;
        } else if (preference.getKey().equals("music_control_prev_key")) {
            if (this.mMusicPrevPreference.isChecked()) {
                OPGestureUtils.set1(this.mContext, 4);
            } else {
                OPGestureUtils.set0(this.mContext, 4);
            }
            return true;
        } else if (preference.getKey().equals("open_light_device_key")) {
            if (this.mFlashLightPreference.isChecked()) {
                OPGestureUtils.set1(this.mContext, 0);
            } else {
                OPGestureUtils.set0(this.mContext, 0);
            }
            return true;
        } else if (preference.getKey().equals("music_control_start_key")) {
            if (this.mMusicStartPreference.isChecked()) {
                OPGestureUtils.set1(this.mContext, 1);
            } else {
                OPGestureUtils.set0(this.mContext, 1);
            }
            return true;
        } else if (preference.getKey().equals("music_control_pause_key")) {
            if (this.mMusicPausePreference.isChecked()) {
                OPGestureUtils.set1(this.mContext, 2);
            } else {
                OPGestureUtils.set0(this.mContext, 2);
            }
            return true;
        } else if (preference.getKey().equals("three_screenshots_enable")) {
            Settings.System.putInt(getActivity().getContentResolver(), "oem_acc_sensor_three_finger", this.mThreeSwipeScreenShot.isChecked() ? 1 : 0);
            OPUtils.sendAppTracker("op_three_key_screenshots_enabled", this.mThreeSwipeScreenShot.isChecked() ? 1 : 0);
            UserManager userManager = this.mUm;
            if (userManager != null && userManager.isUserRunning(999)) {
                Settings.System.putIntForUser(getActivity().getContentResolver(), "oem_acc_sensor_three_finger", this.mThreeSwipeScreenShot.isChecked() ? 1 : 0, 999);
            }
            return true;
        } else if (!preference.getKey().equals("rotation_silent_enable")) {
            return false;
        } else {
            Settings.System.putInt(getActivity().getContentResolver(), "oem_acc_sensor_rotate_silent", this.mRotationSilent.isChecked() ? 1 : 0);
            return true;
        }
    }

    private void toggleMusicController(boolean z) {
        if (z) {
            OPGestureUtils.set1(this.mContext, 1);
            OPGestureUtils.set1(this.mContext, 2);
            OPGestureUtils.set1(this.mContext, 3);
            OPGestureUtils.set1(this.mContext, 4);
            return;
        }
        OPGestureUtils.set0(this.mContext, 1);
        OPGestureUtils.set0(this.mContext, 2);
        OPGestureUtils.set0(this.mContext, 3);
        OPGestureUtils.set0(this.mContext, 4);
    }

    /* JADX WARN: Type inference failed for: r4v2, types: [int, boolean] */
    /* JADX WARNING: Unknown variable types count: 1 */
    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onPreferenceChange(androidx.preference.Preference r3, java.lang.Object r4) {
        /*
            r2 = this;
            java.lang.Boolean r4 = (java.lang.Boolean) r4
            boolean r4 = r4.booleanValue()
            java.lang.String r3 = r3.getKey()
            java.lang.String r0 = "op_fingerprint_gesture_swipe_down_up"
            boolean r0 = r0.equals(r3)
            if (r0 == 0) goto L_0x001c
            android.content.ContentResolver r2 = r2.getContentResolver()
            java.lang.String r3 = "system_navigation_keys_enabled"
            android.provider.Settings.Secure.putInt(r2, r3, r4)
            goto L_0x003e
        L_0x001c:
            java.lang.String r0 = "op_fingerprint_long_press_camera_shot"
            boolean r1 = r0.equals(r3)
            if (r1 == 0) goto L_0x002c
            android.content.ContentResolver r2 = r2.getContentResolver()
            android.provider.Settings.System.putInt(r2, r0, r4)
            goto L_0x003e
        L_0x002c:
            java.lang.String r0 = "one_hand_mode"
            boolean r3 = r3.equals(r0)
            if (r3 == 0) goto L_0x003e
            android.content.ContentResolver r2 = r2.getContentResolver()
            r3 = -2
            java.lang.String r0 = "op_one_hand_mode_setting"
            android.provider.Settings.System.putIntForUser(r2, r0, r4, r3)
        L_0x003e:
            r2 = 1
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oneplus.settings.OPGestureSettings.onPreferenceChange(androidx.preference.Preference, java.lang.Object):boolean");
    }

    private void getConfig() {
        int i = 0;
        int i2 = Settings.System.getInt(getActivity().getContentResolver(), "oem_acc_blackscreen_gestrue_enable", 0);
        this.mBlackSettingValues = i2;
        this.isFlashlightEnable = OPGestureUtils.get(i2, 0);
        this.isMusicPlayEnable = OPGestureUtils.get(this.mBlackSettingValues, 1);
        OPGestureUtils.get(this.mBlackSettingValues, 2);
        OPGestureUtils.get(this.mBlackSettingValues, 3);
        OPGestureUtils.get(this.mBlackSettingValues, 4);
        if (this.isMusicPlayEnable == 1) {
            i = 1;
        }
        this.isMusicControlEnable = i;
        this.isStartUpCameraEnable = OPGestureUtils.get(this.mBlackSettingValues, 6);
        this.isDoubleClickEnable = OPGestureUtils.get(this.mBlackSettingValues, 7);
    }

    private static class OPGestureSearchIndexProvider extends BaseSearchIndexProvider {
        boolean mIsPrimary;

        public OPGestureSearchIndexProvider() {
            this.mIsPrimary = UserHandle.myUserId() == 0;
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            ArrayList arrayList = new ArrayList();
            if (!this.mIsPrimary) {
                return arrayList;
            }
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = C0019R$xml.op_gesture_settings;
            arrayList.add(searchIndexableResource);
            return arrayList;
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> arrayList = new ArrayList<>();
            if (!this.mIsPrimary) {
                arrayList = OPGestureSettings.getNonVisibleKeys();
            }
            if (!this.mIsPrimary || OPUtils.isSurportGesture20(context)) {
                arrayList.add("open_light_device_key");
                arrayList.add("draw_o_start_camera_key");
            }
            if (!this.mIsPrimary || !OPUtils.isSurportGesture20(context)) {
                arrayList.add("oneplus_draw_o_start_app");
                arrayList.add("oneplus_draw_v_start_app");
                arrayList.add("oneplus_draw_s_start_app");
                arrayList.add("oneplus_draw_m_start_app");
                arrayList.add("oneplus_draw_w_start_app");
            }
            if (!OPUtils.isSupportGesturePullNotificationBar()) {
                arrayList.add("op_fingerprint_gesture_swipe_down_up");
            }
            arrayList.add(OPUtils.supportGestureAudioRoute() ? "gesture_to_answer_call" : "gesture_to_control_calls");
            arrayList.add("fingerprint_gesture_control");
            arrayList.add("op_fingerprint_long_press_camera_shot");
            if (!ReflectUtil.isFeatureSupported("OP_FEATURE_ONE_HAND_MODE") || OPUtils.isBeta()) {
                arrayList.add("one_hand_mode");
            }
            if (!OPUtils.hasOnePlusDialer(context)) {
                arrayList.add("gesture_to_control_calls");
                arrayList.add("rotation_silent_enable");
            }
            return arrayList;
        }
    }

    /* access modifiers changed from: private */
    public static List<String> getNonVisibleKeys() {
        ArrayList arrayList = new ArrayList();
        arrayList.add("double_click_light_screen_key");
        arrayList.add("music_control_key");
        arrayList.add("rotation_silent_enable");
        arrayList.add("three_screenshots_enable");
        arrayList.add("anti_misoperation_of_the_screen_touch_enable");
        arrayList.add("fingerprint_gesture_control");
        return arrayList;
    }
}
