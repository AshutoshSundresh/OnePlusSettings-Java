package com.android.settings;

import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.app.Application;
import android.app.admin.DevicePolicyManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.hardware.display.DisplayManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IPowerManager;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.service.vr.IVrManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.MathUtils;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.DropDownPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.SwitchPreference;
import com.android.internal.view.RotationPolicy;
import com.android.settings.display.TimeoutListPreference;
import com.android.settings.display.ToggleFontSizePreferenceFragment;
import com.android.settings.dream.DreamSettings;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.display.BrightnessUtils;
import com.android.settingslib.search.SearchIndexableRaw;
import com.oneplus.common.ReflectUtil;
import com.oneplus.compat.util.OpThemeNative;
import com.oneplus.custom.utils.OpCustomizeSettings;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.ui.ColorPickerPreference;
import com.oneplus.settings.ui.OPBrightnessSeekbarPreferenceCategory;
import com.oneplus.settings.utils.OPUtils;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DisplaySettings extends SettingsPreferenceFragment implements ColorPickerPreference.CustomColorClickListener, Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener, OPBrightnessSeekbarPreferenceCategory.OPCallbackBrightness {
    private static final boolean DEBUG = Build.DEBUG_ONEPLUS;
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.android.settings.DisplaySettings.AnonymousClass5 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            ArrayList arrayList = new ArrayList();
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = C0019R$xml.display_settings;
            arrayList.add(searchIndexableResource);
            return arrayList;
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableRaw> getRawDataToIndex(Context context, boolean z) {
            ArrayList arrayList = new ArrayList();
            Resources resources = context.getResources();
            if (OPUtils.isSupportScreenCutting() && OPUtils.isSupportScreenDisplayAdaption()) {
                SearchIndexableRaw searchIndexableRaw = new SearchIndexableRaw(context);
                searchIndexableRaw.title = resources.getString(C0017R$string.oneplus_app_display_fullscreen_title);
                searchIndexableRaw.screenTitle = resources.getString(C0017R$string.display_settings);
                arrayList.add(searchIndexableRaw);
            }
            return arrayList;
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            ArrayList arrayList = new ArrayList();
            if (!context.getResources().getBoolean(17891431)) {
                arrayList.add("screensaver");
            }
            if (!DisplaySettings.isAutomaticBrightnessAvailable(context.getResources())) {
                arrayList.add("auto_brightness");
            }
            if (!DisplaySettings.isDozeAvailable(context)) {
                arrayList.add("doze");
                arrayList.add("doze_801");
            }
            if (OPUtils.isSupportCustomFingerprint()) {
                arrayList.add("doze");
            } else {
                arrayList.add("doze_801");
            }
            if (!RotationPolicy.isRotationLockToggleVisible(context)) {
                arrayList.add("auto_rotate");
            }
            if (!DisplaySettings.isTapToWakeAvailable(context.getResources())) {
                arrayList.add("tap_to_wake");
            }
            if (!DisplaySettings.isCameraGestureAvailable(context.getResources())) {
                arrayList.add("camera_gesture");
            }
            if (!DisplaySettings.isCameraDoubleTapPowerGestureAvailable(context.getResources())) {
                arrayList.add("camera_double_tap_power_gesture");
            }
            if (!DisplaySettings.isVrDisplayModeAvailable(context)) {
                arrayList.add("vr_display_pref");
            }
            arrayList.add("lockguard_wallpaper_settings");
            if (!DisplaySettings.isSupportReadingMode) {
                arrayList.add("oneplus_reading_mode");
            }
            if (!OPUtils.isSupportVideoEnhancer()) {
                arrayList.add("video_enhancer");
            }
            if (OPUtils.isGuestMode() || !OPUtils.isSupportCustomBlinkLight()) {
                arrayList.add("led_settings");
                arrayList.add("notify_light_enable");
            }
            if (!OPUtils.isSupportTrueColorMode(context)) {
                arrayList.add("op_true_color_mode");
            }
            if (!OPUtils.isSupportMotionGraphicsCompensation()) {
                arrayList.add("graphics_dynamic_optimization");
                arrayList.add("motion_graphics_compensation");
                arrayList.add("graphics_enhancement_engine");
            } else {
                arrayList.add("video_enhancer");
            }
            return arrayList;
        }
    };
    private static boolean isSupportReadingMode;
    private ContentObserver mAccessibilityDisplayDaltonizerAndInversionContentObserver = new ContentObserver(new Handler()) {
        /* class com.android.settings.DisplaySettings.AnonymousClass2 */
        private final Uri ACCESSIBILITY_DISPLAY_DALTONIZER_ENABLED_URI = Settings.Secure.getUriFor("accessibility_display_daltonizer_enabled");
        private final Uri ACCESSIBILITY_DISPLAY_GRAYSCALE_ENABLED_URI = Settings.System.getUriFor("accessibility_display_grayscale_enabled");
        private final Uri ACCESSIBILITY_DISPLAY_INVERSION_ENABLED_URI = Settings.Secure.getUriFor("accessibility_display_inversion_enabled");

        public void onChange(boolean z, Uri uri) {
            super.onChange(z, uri);
            if (this.ACCESSIBILITY_DISPLAY_DALTONIZER_ENABLED_URI.equals(uri) || this.ACCESSIBILITY_DISPLAY_INVERSION_ENABLED_URI.equals(uri) || this.ACCESSIBILITY_DISPLAY_GRAYSCALE_ENABLED_URI.equals(uri)) {
                boolean z2 = false;
                boolean z3 = Settings.Secure.getInt(DisplaySettings.this.getContentResolver(), "accessibility_display_daltonizer_enabled", 12) == 1;
                boolean z4 = Settings.Secure.getInt(DisplaySettings.this.getContentResolver(), "accessibility_display_inversion_enabled", 0) == 1;
                boolean z5 = Settings.System.getInt(DisplaySettings.this.getContentResolver(), "accessibility_display_grayscale_enabled", 1) == 0;
                Settings.Secure.getIntForUser(DisplaySettings.this.getContentResolver(), "night_display_activated", 0, -2);
                if (DisplaySettings.this.mNightModePreference != null) {
                    DisplaySettings.this.mNightModePreference.setEnabled(!z3 && !z4 && !z5);
                }
                if (DisplaySettings.this.mReadingModePreference != null) {
                    Preference preference = DisplaySettings.this.mReadingModePreference;
                    if (!z3 && !z4 && !z5) {
                        z2 = true;
                    }
                    preference.setEnabled(z2);
                }
            }
        }
    };
    private Preference mAdvancedSettingsPreference;
    private SwitchPreference mAutoBrightnessPreference;
    private boolean mAutomatic;
    private boolean mAutomaticAvailable;
    private String[] mBlackColors;
    private OPBrightnessSeekbarPreferenceCategory mBrightPreference;
    private BrightnessObserver mBrightnessObserver;
    private SwitchPreference mCameraDoubleTapPowerGesturePreference;
    private SwitchPreference mCameraGesturePreference;
    private Context mContext;
    private String mCurrentTempColor;
    private float mDefaultBacklight;
    private float mDefaultBacklightForVr;
    private DisplayManager mDisplayManager;
    private Preference mDoze801Preference;
    private Preference mDozePreference;
    private Preference mFontSizePref;
    private Preference mGraphicsDynamicOptimization;
    private Preference mGraphicsEnhancementEngine;
    private Handler mHandler;
    private boolean mIsSupportIrisSmooth = ReflectUtil.isFeatureSupported("OP_FEATURE_PIXELWORKS_BRIGHTNESS_SMOOTH");
    private long mLastSliderChangeTimeMillis = -1;
    private Preference mLedSettingsPreference;
    private Preference mLockWallPaperPreference;
    private float mMaximumBacklight;
    private float mMaximumBacklightForVr;
    private float mMinimumBacklight;
    private float mMinimumBacklightForVr;
    private Preference mMotionGraphicsCompensation;
    private SwitchPreference mNetworkNameDisplayedPreference = null;
    private Preference mNightModePreference;
    private int mNotifyLightEnable;
    private SwitchPreference mNotifyLightPreference;
    private Preference mReadingModePreference;
    private final RotationPolicy.RotationPolicyListener mRotationPolicyListener = new RotationPolicy.RotationPolicyListener() {
        /* class com.android.settings.DisplaySettings.AnonymousClass4 */

        public void onChange() {
            DisplaySettings.this.updateLockScreenRotation();
        }
    };
    private PreferenceCategory mScreenBrightnessRootPreference;
    private Preference mScreenSaverPreference;
    private TimeoutListPreference mScreenTimeoutPreference;
    private ValueAnimator mSliderAnimator;
    private SwitchPreference mTapToWakePreference;
    private SwitchPreference mToggleLockScreenRotationPreference;
    private SwitchPreference mTrueColorMode;
    private Preference mVideoEnhancerPreference;
    private String[] mWhiteColors;

    /* access modifiers changed from: private */
    public static boolean isAutomaticBrightnessAvailable(Resources resources) {
        return true;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 46;
    }

    @Override // com.oneplus.settings.ui.OPBrightnessSeekbarPreferenceCategory.OPCallbackBrightness
    public void onOPBrightValueStartTrackingTouch(int i) {
    }

    class DefaultHandler extends Handler {
        private final WeakReference<Context> mReference;

        public DefaultHandler(Context context) {
            this.mReference = new WeakReference<>(context);
        }

        public void handleMessage(Message message) {
            Context context = this.mReference.get();
            if (context != null) {
                int i = message.what;
                if (i == 100) {
                    Intent intent = new Intent("android.settings.OEM_THEME_MODE");
                    intent.setPackage("com.oneplus.skin");
                    intent.addFlags(268435456);
                    intent.putExtra("oem_theme_mode", message.arg1);
                    intent.putExtra("special_theme", false);
                    HashMap hashMap = new HashMap();
                    int i2 = message.arg1;
                    if (i2 == 2) {
                        hashMap.put("oneplus_basiccolor", "black");
                        OpThemeNative.enableTheme(SettingsBaseApplication.mApplication, hashMap);
                    } else if (i2 == 1) {
                        hashMap.put("oneplus_basiccolor", "white");
                        OpThemeNative.enableTheme(SettingsBaseApplication.mApplication, hashMap);
                    } else {
                        hashMap.put("oneplus_basiccolor", "black");
                        OpThemeNative.disableTheme(SettingsBaseApplication.mApplication, hashMap);
                        hashMap.put("oneplus_basiccolor", "white");
                        OpThemeNative.disableTheme(SettingsBaseApplication.mApplication, hashMap);
                    }
                    DisplaySettings.this.setAccentColor();
                    context.sendBroadcast(intent);
                } else if (i == 101) {
                    Intent intent2 = new Intent("android.settings.OEM_THEME_MODE");
                    intent2.setPackage("com.oneplus.skin");
                    intent2.addFlags(268435456);
                    intent2.putExtra("oem_theme_mode", message.arg1);
                    intent2.putExtra("special_theme", true);
                    if (OpCustomizeSettings.CUSTOM_TYPE.SW.equals(OpCustomizeSettings.getCustomType())) {
                        Settings.System.putString(DisplaySettings.this.getContentResolver(), "oneplus_accent_color", "#FF2837");
                        SystemProperties.set("persist.sys.theme.accentcolor", "#FF2837".replace("#", ""));
                    } else if (OpCustomizeSettings.CUSTOM_TYPE.AVG.equals(OpCustomizeSettings.getCustomType())) {
                        Settings.System.putString(DisplaySettings.this.getContentResolver(), "oneplus_accent_color", "#FBC02D");
                        SystemProperties.set("persist.sys.theme.accentcolor", "#FBC02D".replace("#", ""));
                    } else if (OpCustomizeSettings.CUSTOM_TYPE.MCL.equals(OpCustomizeSettings.getCustomType())) {
                        Settings.System.putString(DisplaySettings.this.getContentResolver(), "oneplus_accent_color", "#FF9E31");
                        SystemProperties.set("persist.sys.theme.accentcolor", "#FF9E31".replace("#", ""));
                    } else {
                        Settings.System.putString(DisplaySettings.this.getContentResolver(), "oneplus_accent_color", "#2196F3");
                        SystemProperties.set("persist.sys.theme.accentcolor", "#2196F3".replace("#", ""));
                    }
                    context.sendBroadcast(intent2);
                }
            }
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        Preference preference;
        Preference preference2;
        Preference preference3;
        Preference preference4;
        super.onCreate(bundle);
        final FragmentActivity activity = getActivity();
        activity.getContentResolver();
        this.mContext = getActivity();
        new DefaultHandler(activity.getApplication());
        addPreferencesFromResource(C0019R$xml.display_settings);
        initAccentColors(this.mContext.getResources());
        this.mScreenBrightnessRootPreference = (PreferenceCategory) findPreference("screen_brightness");
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("display_system");
        PreferenceCategory preferenceCategory2 = (PreferenceCategory) findPreference("header_category_custom");
        Preference findPreference = findPreference("screensaver");
        this.mScreenSaverPreference = findPreference;
        boolean z = false;
        if (findPreference != null && !getResources().getBoolean(17891431)) {
            this.mScreenSaverPreference.setVisible(false);
        }
        this.mScreenTimeoutPreference = (TimeoutListPreference) findPreference("screen_timeout");
        this.mFontSizePref = findPreference("font_size");
        if (isAutomaticBrightnessAvailable(getResources())) {
            SwitchPreference switchPreference = (SwitchPreference) findPreference("auto_brightness");
            this.mAutoBrightnessPreference = switchPreference;
            switchPreference.setOnPreferenceChangeListener(this);
        } else {
            SwitchPreference switchPreference2 = (SwitchPreference) findPreference("auto_brightness");
            this.mAutoBrightnessPreference = switchPreference2;
            switchPreference2.setVisible(false);
        }
        this.mDozePreference = findPreference("doze");
        this.mDoze801Preference = findPreference("doze_801");
        this.mDozePreference.setOnPreferenceClickListener(this);
        this.mDoze801Preference.setOnPreferenceClickListener(this);
        if (!isDozeAvailable(activity)) {
            Preference preference5 = this.mDoze801Preference;
            if (preference5 != null) {
                preference5.setVisible(false);
            }
            Preference preference6 = this.mDozePreference;
            if (preference6 != null) {
                preference6.setVisible(false);
            }
        } else if (OPUtils.isSupportCustomFingerprint()) {
            Preference preference7 = this.mDozePreference;
            if (preference7 != null) {
                preference7.setVisible(false);
            }
        } else {
            Preference preference8 = this.mDoze801Preference;
            if (preference8 != null) {
                preference8.setVisible(false);
            }
            if (OPUtils.isSupportAlwaysOnDisplay() && (preference4 = this.mDozePreference) != null) {
                preference4.setSummary(C0017R$string.oneplus_hand_up_proximity_title);
            }
        }
        if (isVrDisplayModeAvailable(activity)) {
            DropDownPreference dropDownPreference = (DropDownPreference) findPreference("vr_display_pref");
            dropDownPreference.setEntries(new CharSequence[]{activity.getString(C0017R$string.display_vr_pref_low_persistence), activity.getString(C0017R$string.display_vr_pref_off)});
            dropDownPreference.setEntryValues(new CharSequence[]{"0", "1"});
            dropDownPreference.setValueIndex(Settings.Secure.getIntForUser(activity.getContentResolver(), "vr_display_mode", 0, ActivityManager.getCurrentUser()));
            dropDownPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(this) {
                /* class com.android.settings.DisplaySettings.AnonymousClass1 */

                @Override // androidx.preference.Preference.OnPreferenceChangeListener
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    if (Settings.Secure.putIntForUser(activity.getContentResolver(), "vr_display_mode", Integer.parseInt((String) obj), ActivityManager.getCurrentUser())) {
                        return true;
                    }
                    Log.e("DisplaySettings", "Could not change setting for vr_display_mode");
                    return true;
                }
            });
        } else {
            ((DropDownPreference) findPreference("vr_display_pref")).setVisible(false);
        }
        this.mHandler = new Handler();
        this.mToggleLockScreenRotationPreference = (SwitchPreference) findPreference("toggle_lock_screen_rotation_preference");
        if (!RotationPolicy.isRotationSupported(getActivity())) {
            removePreference("toggle_lock_screen_rotation_preference");
        }
        this.mNotifyLightEnable = Settings.System.getInt(getActivity().getContentResolver(), "oem_acc_breath_light", 0);
        SwitchPreference switchPreference3 = (SwitchPreference) findPreference("notify_light_enable");
        this.mNotifyLightPreference = switchPreference3;
        switchPreference3.setOnPreferenceChangeListener(this);
        this.mNotifyLightPreference.setChecked(this.mNotifyLightEnable != 0);
        Preference findPreference2 = findPreference("lockguard_wallpaper_settings");
        this.mLockWallPaperPreference = findPreference2;
        findPreference2.setOnPreferenceClickListener(this);
        this.mLockWallPaperPreference.setVisible(false);
        PowerManager powerManager = (PowerManager) getActivity().getSystemService(PowerManager.class);
        this.mMinimumBacklight = powerManager.getBrightnessConstraint(0);
        this.mMaximumBacklight = powerManager.getBrightnessConstraint(1);
        this.mDefaultBacklight = powerManager.getBrightnessConstraint(2);
        this.mMinimumBacklightForVr = powerManager.getBrightnessConstraint(5);
        this.mMaximumBacklightForVr = powerManager.getBrightnessConstraint(6);
        this.mDefaultBacklightForVr = powerManager.getBrightnessConstraint(7);
        this.mDisplayManager = (DisplayManager) this.mContext.getSystemService(DisplayManager.class);
        this.mAutomaticAvailable = getActivity().getResources().getBoolean(17891369);
        IPowerManager.Stub.asInterface(ServiceManager.getService("power"));
        this.mContext.getPackageManager().hasSystemFeature("oem.autobrightctl.animation.support");
        OPBrightnessSeekbarPreferenceCategory oPBrightnessSeekbarPreferenceCategory = (OPBrightnessSeekbarPreferenceCategory) findPreference("manual_brightness_displays");
        this.mBrightPreference = oPBrightnessSeekbarPreferenceCategory;
        oPBrightnessSeekbarPreferenceCategory.setCallback(this);
        BrightnessObserver brightnessObserver = new BrightnessObserver(this.mHandler);
        this.mBrightnessObserver = brightnessObserver;
        brightnessObserver.startObserving();
        setCustomAccentColor();
        this.mLedSettingsPreference = findPreference("led_settings");
        updateState();
        this.mNotifyLightPreference.setVisible(false);
        this.mLedSettingsPreference.setVisible(false);
        isSupportReadingMode = this.mContext.getPackageManager().hasSystemFeature("oem.read_mode.support");
        this.mNightModePreference = findPreference("oneplus_night_mode");
        Preference findPreference3 = findPreference("oneplus_reading_mode");
        this.mReadingModePreference = findPreference3;
        if (findPreference3 != null && !isSupportReadingMode) {
            this.mScreenBrightnessRootPreference.removePreference(findPreference3);
        }
        this.mVideoEnhancerPreference = findPreference("video_enhancer");
        if (!OPUtils.isSupportVideoEnhancer() && (preference3 = this.mVideoEnhancerPreference) != null) {
            preference3.setVisible(false);
        }
        ValueAnimator valueAnimator = this.mSliderAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        if (UserHandle.myUserId() != 0) {
            removePreference("status_bar_custom");
        }
        this.mTrueColorMode = (SwitchPreference) findPreference("op_true_color_mode");
        this.mTrueColorMode.setChecked(Settings.Secure.getInt(getContentResolver(), "display_white_balance_enabled", 0) == 1);
        this.mTrueColorMode.setOnPreferenceChangeListener(this);
        if (!OPUtils.isSupportTrueColorMode(this.mContext)) {
            this.mTrueColorMode.setVisible(false);
        }
        this.mGraphicsDynamicOptimization = findPreference("graphics_dynamic_optimization");
        this.mMotionGraphicsCompensation = findPreference("motion_graphics_compensation");
        this.mGraphicsEnhancementEngine = findPreference("graphics_enhancement_engine");
        if (!OPUtils.isSupportMotionGraphicsCompensation() && (preference2 = this.mGraphicsEnhancementEngine) != null) {
            preference2.setVisible(false);
        }
        if (OPUtils.isSupportMotionGraphicsCompensation() && (preference = this.mVideoEnhancerPreference) != null) {
            preference.setVisible(false);
        }
        this.mAdvancedSettingsPreference = findPreference("op_advanced_settings");
        boolean isSupportMultiScreenResolution = OPUtils.isSupportMultiScreenResolution(this.mContext);
        boolean isSupportScreenRefreshRate = OPUtils.isSupportScreenRefreshRate();
        if (OPUtils.isSupportScreenCutting() || (OPUtils.isSupportScreenDisplayAdaption() && OPUtils.isSupportAppsDisplayInFullscreen())) {
            z = true;
        }
        if (OPUtils.isGuestMode()) {
            this.mAdvancedSettingsPreference.setSummary(this.mContext.getString(C0017R$string.op_display_advanced_settings_summary_5));
        } else if (!isSupportMultiScreenResolution || !isSupportScreenRefreshRate || !z) {
            if (!isSupportScreenRefreshRate || !z) {
                if (isSupportMultiScreenResolution && isSupportScreenRefreshRate) {
                    this.mAdvancedSettingsPreference.setSummary(this.mContext.getString(C0017R$string.op_display_advanced_settings_summary_3));
                } else if (z) {
                    this.mAdvancedSettingsPreference.setSummary(this.mContext.getString(C0017R$string.op_display_advanced_settings_summary_4));
                }
            } else if (OPUtils.isSupportHolePunchFrontCam()) {
                this.mAdvancedSettingsPreference.setSummary(this.mContext.getString(C0017R$string.op_display_advanced_settings_summary_7));
            } else {
                this.mAdvancedSettingsPreference.setSummary(this.mContext.getString(C0017R$string.op_display_advanced_settings_summary_2));
            }
        } else if (OPUtils.isSupportHolePunchFrontCam()) {
            this.mAdvancedSettingsPreference.setSummary(this.mContext.getString(C0017R$string.op_display_advanced_settings_summary_6));
        } else {
            this.mAdvancedSettingsPreference.setSummary(this.mContext.getString(C0017R$string.op_display_advanced_settings_summary_1));
        }
    }

    /* access modifiers changed from: private */
    public static boolean isDozeAvailable(Context context) {
        String str = Build.IS_DEBUGGABLE ? SystemProperties.get("debug.doze.component") : null;
        if (TextUtils.isEmpty(str)) {
            str = context.getResources().getString(17039897);
        }
        return !TextUtils.isEmpty(str);
    }

    /* access modifiers changed from: private */
    public static boolean isTapToWakeAvailable(Resources resources) {
        return resources.getBoolean(17891549);
    }

    /* access modifiers changed from: private */
    public static boolean isCameraGestureAvailable(Resources resources) {
        return (resources.getInteger(17694762) != -1) && !SystemProperties.getBoolean("gesture.disable_camera_launch", false);
    }

    /* access modifiers changed from: private */
    public static boolean isCameraDoubleTapPowerGestureAvailable(Resources resources) {
        return resources.getBoolean(17891388);
    }

    /* access modifiers changed from: private */
    public static boolean isVrDisplayModeAvailable(Context context) {
        context.getPackageManager();
        return false;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateTimeoutPreferenceDescription(long j) {
        if (isAdded()) {
            TimeoutListPreference timeoutListPreference = this.mScreenTimeoutPreference;
            String str = "";
            if (timeoutListPreference.isDisabledByAdmin()) {
                str = this.mContext.getResources().getString(C0017R$string.disabled_by_policy_title);
            } else if (j >= 0) {
                CharSequence[] entries = timeoutListPreference.getEntries();
                CharSequence[] entryValues = timeoutListPreference.getEntryValues();
                if (!(entries == null || entries.length == 0)) {
                    int i = 0;
                    for (int i2 = 0; i2 < entryValues.length; i2++) {
                        if (j >= Long.parseLong(entryValues[i2].toString())) {
                            i = i2;
                        }
                    }
                    str = this.mContext.getResources().getString(C0017R$string.screen_timeout_summary, entries[i]);
                }
            }
            timeoutListPreference.setSummary(str);
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStart() {
        super.onStart();
        getContentResolver().registerContentObserver(Settings.Secure.getUriFor("accessibility_display_daltonizer_enabled"), true, this.mAccessibilityDisplayDaltonizerAndInversionContentObserver, -1);
        getContentResolver().registerContentObserver(Settings.Secure.getUriFor("accessibility_display_inversion_enabled"), true, this.mAccessibilityDisplayDaltonizerAndInversionContentObserver, -1);
        getContentResolver().registerContentObserver(Settings.System.getUriFor("accessibility_display_grayscale_enabled"), true, this.mAccessibilityDisplayDaltonizerAndInversionContentObserver, -1);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        updateState();
        long j = Settings.System.getLong(getActivity().getContentResolver(), "screen_off_timeout", 30000);
        this.mScreenTimeoutPreference.setValue(String.valueOf(j));
        this.mScreenTimeoutPreference.setOnPreferenceChangeListener(this);
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getActivity().getSystemService("device_policy");
        if (devicePolicyManager != null) {
            RestrictedLockUtils.EnforcedAdmin checkIfMaximumTimeToLockIsSet = RestrictedLockUtilsInternal.checkIfMaximumTimeToLockIsSet(getActivity());
            this.mScreenTimeoutPreference.removeUnusableTimeouts(devicePolicyManager.getMaximumTimeToLock(null, UserHandle.myUserId()), checkIfMaximumTimeToLockIsSet);
        }
        updateTimeoutPreferenceDescription(j);
        RestrictedLockUtils.EnforcedAdmin checkIfRestrictionEnforced = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mContext, "no_config_screen_timeout", UserHandle.myUserId());
        if (checkIfRestrictionEnforced != null) {
            this.mScreenTimeoutPreference.removeUnusableTimeouts(0, checkIfRestrictionEnforced);
        }
        disablePreferenceIfManaged("wallpaper", "no_set_wallpaper");
        updateLockScreenRotation();
        if (RotationPolicy.isRotationSupported(getActivity())) {
            RotationPolicy.registerRotationPolicyListener(getActivity(), this.mRotationPolicyListener);
        }
        disableEntryForAccessibilityDisplayDaltonizerAndInversion();
    }

    private void disableEntryForAccessibilityDisplayDaltonizerAndInversion() {
        boolean z = false;
        boolean z2 = Settings.Secure.getInt(getContentResolver(), "accessibility_display_daltonizer_enabled", 12) == 1;
        boolean z3 = Settings.Secure.getInt(getContentResolver(), "accessibility_display_inversion_enabled", 0) == 1;
        boolean z4 = Settings.System.getInt(getContentResolver(), "accessibility_display_grayscale_enabled", 1) == 0;
        if (z2 || z3 || z4) {
            Preference preference = this.mNightModePreference;
            if (preference != null) {
                preference.setEnabled(!z2 && !z3 && !z4);
            }
            Preference preference2 = this.mReadingModePreference;
            if (preference2 != null) {
                if (!z2 && !z3 && !z4) {
                    z = true;
                }
                preference2.setEnabled(z);
            }
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        if (RotationPolicy.isRotationSupported(getActivity())) {
            RotationPolicy.unregisterRotationPolicyListener(getActivity(), this.mRotationPolicyListener);
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStop() {
        super.onStop();
        getContentResolver().unregisterContentObserver(this.mAccessibilityDisplayDaltonizerAndInversionContentObserver);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onDestroy() {
        super.onDestroy();
        BrightnessObserver brightnessObserver = this.mBrightnessObserver;
        if (brightnessObserver != null) {
            brightnessObserver.stopObserving();
        }
    }

    private void updateState() {
        updateFontSizeSummary();
        updateScreenSaverSummary();
        int i = Settings.System.getInt(getContentResolver(), "oem_acc_breath_light", 0);
        this.mNotifyLightEnable = i;
        Preference preference = this.mLedSettingsPreference;
        if (preference != null) {
            preference.setEnabled(i == 1);
        }
        if (this.mAutoBrightnessPreference != null) {
            this.mAutoBrightnessPreference.setChecked(Settings.System.getInt(getContentResolver(), "screen_brightness_mode", 0) != 0);
        }
        if (this.mNetworkNameDisplayedPreference != null) {
            this.mNetworkNameDisplayedPreference.setChecked(Settings.System.getInt(getContentResolver(), "show_network_name_mode", 1) != 0);
        }
        if (this.mTapToWakePreference != null) {
            this.mTapToWakePreference.setChecked(Settings.Secure.getInt(getContentResolver(), "double_tap_to_wake", 0) != 0);
        }
        if (this.mCameraGesturePreference != null) {
            this.mCameraGesturePreference.setChecked(Settings.Secure.getInt(getContentResolver(), "camera_gesture_disabled", 0) == 0);
        }
        if (this.mCameraDoubleTapPowerGesturePreference != null) {
            this.mCameraDoubleTapPowerGesturePreference.setChecked(Settings.Secure.getInt(getContentResolver(), "camera_double_tap_power_gesture_disabled", 0) == 0);
        }
        if (this.mVideoEnhancerPreference != null) {
            this.mVideoEnhancerPreference.setSummary(SystemProperties.getBoolean("persist.sys.oem.vendor.media.vpp.enable", false) ? C0017R$string.switch_on_text : C0017R$string.switch_off_text);
        }
        if (this.mGraphicsDynamicOptimization != null) {
            this.mGraphicsDynamicOptimization.setSummary(Settings.System.getInt(this.mContext.getContentResolver(), "op_iris_video_sdr2hdr_status", 0) == 1 ? C0017R$string.switch_on_text : C0017R$string.switch_off_text);
        }
        if (this.mMotionGraphicsCompensation != null) {
            this.mMotionGraphicsCompensation.setSummary(Settings.System.getInt(this.mContext.getContentResolver(), "op_iris_video_memc_status", 0) == 1 ? C0017R$string.switch_on_text : C0017R$string.switch_off_text);
        }
    }

    private void updateScreenSaverSummary() {
        Preference preference = this.mScreenSaverPreference;
        if (preference != null) {
            preference.setSummary(DreamSettings.getSummaryTextWithDreamName(getActivity()));
        }
    }

    private void updateFontSizeSummary() {
        Context context = this.mFontSizePref.getContext();
        float f = Settings.System.getFloat(context.getContentResolver(), "font_scale", 1.0f);
        Resources resources = context.getResources();
        this.mFontSizePref.setSummary(resources.getStringArray(C0003R$array.entries_font_size)[ToggleFontSizePreferenceFragment.fontSizeValueToIndex(f, resources.getStringArray(C0003R$array.entryvalues_font_size))]);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        String key = preference.getKey();
        if ("op_true_color_mode".equals(key)) {
            Settings.Secure.putInt(getContentResolver(), "display_white_balance_enabled", ((Boolean) obj).booleanValue() ? 1 : 0);
            OPUtils.sendAppTrackerForTrueColor();
        } else if ("screen_timeout".equals(key)) {
            try {
                int parseInt = Integer.parseInt((String) obj);
                Settings.System.putInt(getContentResolver(), "screen_off_timeout", parseInt);
                updateTimeoutPreferenceDescription((long) parseInt);
            } catch (NumberFormatException e) {
                Log.e("DisplaySettings", "could not persist screen timeout setting", e);
            }
        }
        if (preference == this.mAutoBrightnessPreference) {
            Settings.System.putInt(getContentResolver(), "screen_brightness_mode", ((Boolean) obj).booleanValue() ? 1 : 0);
            OPUtils.sendAppTrackerForAutoBrightness();
        }
        if (preference == this.mNetworkNameDisplayedPreference) {
            Settings.System.putInt(getContentResolver(), "show_network_name_mode", ((Boolean) obj).booleanValue() ? 1 : 0);
        }
        if (preference == this.mTapToWakePreference) {
            Settings.Secure.putInt(getContentResolver(), "double_tap_to_wake", ((Boolean) obj).booleanValue() ? 1 : 0);
        }
        if (preference == this.mCameraGesturePreference) {
            Settings.Secure.putInt(getContentResolver(), "camera_gesture_disabled", !((Boolean) obj).booleanValue());
        }
        if (preference == this.mCameraDoubleTapPowerGesturePreference) {
            Settings.Secure.putInt(getContentResolver(), "camera_double_tap_power_gesture_disabled", !((Boolean) obj).booleanValue());
        }
        if (preference == this.mNotifyLightPreference) {
            boolean booleanValue = ((Boolean) obj).booleanValue();
            updateNotifyLightStatus(booleanValue ? 1 : 0);
            Preference preference2 = this.mLedSettingsPreference;
            if (preference2 != null) {
                preference2.setEnabled(booleanValue);
            }
            return true;
        }
        if ("video_enhancer".equals(key)) {
            boolean booleanValue2 = ((Boolean) obj).booleanValue();
            SystemProperties.set("persist.sys.oem.vendor.media.vpp.enable", booleanValue2 ? "true" : "false");
            OPUtils.sendAppTracker("video_enhancer", booleanValue2 ? "1" : "0");
        }
        return true;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if (this.mToggleLockScreenRotationPreference == preference) {
            handleLockScreenRotationPreferenceClick();
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals("doze") || preference.getKey().equals("doze_801")) {
            try {
                Intent intent = new Intent();
                intent.setClassName("com.oneplus.aod", "com.oneplus.settings.SettingsActivity");
                this.mContext.startActivity(intent);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            }
        } else if (!preference.getKey().equals("lockguard_wallpaper_settings")) {
            return false;
        } else {
            Intent intent2 = new Intent("android.intent.action.SET_WALLPAPER");
            intent2.setPackage("net.oneplus.launcher");
            if (!OPUtils.isActionExist(this.mContext, intent2, "android.intent.action.SET_WALLPAPER")) {
                return false;
            }
            try {
                startActivity(intent2);
                return false;
            } catch (Exception e2) {
                e2.printStackTrace();
                return false;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public IVrManager safeGetVrManager() {
        return IVrManager.Stub.asInterface(ServiceManager.getService("vrmanager"));
    }

    /* access modifiers changed from: package-private */
    public boolean isInVrMode() {
        IVrManager safeGetVrManager = safeGetVrManager();
        if (safeGetVrManager == null) {
            return false;
        }
        try {
            return safeGetVrManager.getVrModeState();
        } catch (RemoteException e) {
            Log.e("DisplaySettings", "Failed to check vr mode!", e);
            return false;
        }
    }

    private void setBrightness(int i) {
        this.mDisplayManager.setTemporaryBrightness(MathUtils.min(BrightnessUtils.convertGammaToLinearFloat(i, this.mMinimumBacklight, this.mMaximumBacklight), 1.0f));
    }

    @Override // com.oneplus.settings.ui.OPBrightnessSeekbarPreferenceCategory.OPCallbackBrightness
    public void onOPBrightValueChanged(int i, int i2) {
        ValueAnimator valueAnimator = this.mSliderAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        isInVrMode();
        long uptimeMillis = SystemClock.uptimeMillis();
        long j = this.mLastSliderChangeTimeMillis;
        if (j <= 0) {
            if (DEBUG) {
                Log.d("DisplaySettings", "Slider.onChanged denoise init.");
            }
            this.mLastSliderChangeTimeMillis = uptimeMillis;
        } else if (uptimeMillis - j >= 10) {
            setBrightness(i2);
        } else if (DEBUG) {
            Log.d("DisplaySettings", "Slider.onChanged denoise consecutive change.");
        }
    }

    @Override // com.oneplus.settings.ui.OPBrightnessSeekbarPreferenceCategory.OPCallbackBrightness
    public void saveBrightnessDataBase(int i) {
        float f;
        float f2;
        ValueAnimator valueAnimator = this.mSliderAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        if (isInVrMode()) {
            f2 = this.mMinimumBacklightForVr;
            f = this.mMaximumBacklightForVr;
        } else {
            f2 = this.mMinimumBacklight;
            f = this.mMaximumBacklight;
        }
        final float convertGammaToLinearFloat = BrightnessUtils.convertGammaToLinearFloat(i, f2, f);
        AsyncTask.execute(new Runnable() {
            /* class com.android.settings.DisplaySettings.AnonymousClass3 */

            public void run() {
                Settings.System.putFloatForUser(DisplaySettings.this.mContext.getContentResolver(), "screen_brightness_float", convertGammaToLinearFloat, -2);
            }
        });
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (100 == i && i2 == -1 && intent != null) {
            this.mCurrentTempColor = intent.getStringExtra("current_temp_color");
            if (OPUtils.isWhiteModeOn(this.mContext.getContentResolver())) {
                this.mWhiteColors[11] = this.mCurrentTempColor;
            } else if (OPUtils.isBlackModeOn(this.mContext.getContentResolver())) {
                this.mBlackColors[11] = this.mCurrentTempColor;
            }
        }
    }

    @Override // com.oneplus.settings.ui.ColorPickerPreference.CustomColorClickListener
    public void onCustomColorClick() {
        Intent intent = new Intent("oneplus.intent.action.ONEPLUS_COLOR_PICKER");
        if (OPUtils.isWhiteModeOn(this.mContext.getContentResolver())) {
            this.mCurrentTempColor = Settings.System.getString(getActivity().getContentResolver(), "oneplus_white_custom_accent_color");
        } else if (OPUtils.isBlackModeOn(this.mContext.getContentResolver())) {
            this.mCurrentTempColor = Settings.System.getString(getActivity().getContentResolver(), "oneplus_black_custom_accent_color");
        }
        intent.putExtra("current_color", this.mCurrentTempColor);
        startActivityForResult(intent, 100);
    }

    private void initAccentColors(Resources resources) {
        this.mWhiteColors = new String[]{resources.getString(C0006R$color.op_primary_default_light), resources.getString(C0006R$color.op_primary_golden_light), resources.getString(C0006R$color.op_primary_lemon_yellow_light), resources.getString(C0006R$color.op_primary_grass_green_light), resources.getString(C0006R$color.op_primary_charm_purple_light), resources.getString(C0006R$color.op_primary_sky_blue_light), resources.getString(C0006R$color.op_primary_vigour_red_light), resources.getString(C0006R$color.op_primary_fashion_pink_light), resources.getString(C0006R$color.op_primary_red_light), resources.getString(C0006R$color.op_primary_blue_light), resources.getString(C0006R$color.op_primary_green_light), resources.getString(C0006R$color.op_primary_green_custom)};
        this.mBlackColors = new String[]{resources.getString(C0006R$color.op_primary_default_dark), resources.getString(C0006R$color.op_primary_golden_dark), resources.getString(C0006R$color.op_primary_lemon_yellow_dark), resources.getString(C0006R$color.op_primary_grass_green_dark), resources.getString(C0006R$color.op_primary_charm_purple_dark), resources.getString(C0006R$color.op_primary_sky_blue_dark), resources.getString(C0006R$color.op_primary_vigour_red_dark), resources.getString(C0006R$color.op_primary_fashion_pink_dark), resources.getString(C0006R$color.op_primary_red_dark), resources.getString(C0006R$color.op_primary_blue_dark), resources.getString(C0006R$color.op_primary_green_dark), resources.getString(C0006R$color.op_primary_green_custom)};
        if (!OPUtils.isWhiteModeOn(this.mContext.getContentResolver())) {
            OPUtils.isBlackModeOn(this.mContext.getContentResolver());
        }
    }

    private void setCustomAccentColor() {
        String string = Settings.System.getString(getActivity().getContentResolver(), "oneplus_white_custom_accent_color");
        String str = "#FF0000";
        if (TextUtils.isEmpty(string)) {
            string = str;
        }
        this.mWhiteColors[11] = string;
        String string2 = Settings.System.getString(getActivity().getContentResolver(), "oneplus_black_custom_accent_color");
        if (!TextUtils.isEmpty(string2)) {
            str = string2;
        }
        this.mBlackColors[11] = str;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setAccentColor() {
        String str;
        if (OPUtils.isWhiteModeOn(this.mContext.getContentResolver())) {
            str = Settings.System.getString(getActivity().getContentResolver(), "oem_white_mode_accent_color");
        } else if (OPUtils.isBlackModeOn(this.mContext.getContentResolver())) {
            str = Settings.System.getString(getActivity().getContentResolver(), "oem_black_mode_accent_color");
        } else {
            str = this.mContext.getResources().getString(C0006R$color.op_primary_default_light);
        }
        Settings.System.putString(getContentResolver(), "oneplus_accent_color", str);
        if (!TextUtils.isEmpty(str)) {
            str = str.replace("#", "");
        }
        setCustomAccentColor();
        SystemProperties.set("persist.sys.theme.accentcolor", str);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateLockScreenRotation() {
        Application application = SettingsBaseApplication.mApplication;
        if (application != null) {
            this.mToggleLockScreenRotationPreference.setChecked(!RotationPolicy.isRotationLocked(application));
        }
    }

    private void handleLockScreenRotationPreferenceClick() {
        RotationPolicy.setRotationLockForAccessibility(SettingsBaseApplication.mApplication, !this.mToggleLockScreenRotationPreference.isChecked());
    }

    private void updateNotifyLightStatus(int i) {
        Settings.System.putInt(getActivity().getContentResolver(), "oem_acc_breath_light", i);
        Settings.System.putInt(getActivity().getContentResolver(), "notification_light_pulse", i);
        Settings.System.putInt(getActivity().getContentResolver(), "battery_led_low_power", i);
        Settings.System.putInt(getActivity().getContentResolver(), "battery_led_charging", i);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateSlider() {
        int i;
        ValueAnimator valueAnimator = this.mSliderAnimator;
        if (valueAnimator != null && valueAnimator.isStarted()) {
            Log.d("DisplaySettings", "animateSliderTo: cancel anim.");
            this.mSliderAnimator.cancel();
        }
        if (isInVrMode()) {
            i = BrightnessUtils.convertLinearToGammaFloat(Settings.System.getFloatForUser(this.mContext.getContentResolver(), "screen_brightness_for_vr_float", this.mDefaultBacklightForVr, -2), this.mMinimumBacklightForVr, this.mMaximumBacklightForVr);
        } else {
            i = BrightnessUtils.convertLinearToGammaFloat(Settings.System.getFloatForUser(this.mContext.getContentResolver(), "screen_brightness_float", this.mDefaultBacklight, -2), this.mMinimumBacklight, this.mMaximumBacklight);
        }
        if (Settings.System.getInt(getContentResolver(), "screen_brightness_mode", 0) != 0) {
            ValueAnimator valueAnimator2 = new ValueAnimator();
            this.mSliderAnimator = valueAnimator2;
            valueAnimator2.setIntValues(this.mBrightPreference.getBrightness(), i);
            Log.d("DisplaySettings", "animateSliderTo: animating from " + this.mBrightPreference.getBrightness() + " to " + i);
            this.mSliderAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                /* class com.android.settings.$$Lambda$DisplaySettings$qOh46548JQf3cUmLta2I9UEyRo4 */

                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    DisplaySettings.this.lambda$updateSlider$0$DisplaySettings(valueAnimator);
                }
            });
            long abs = (long) ((Math.abs(this.mBrightPreference.getBrightness() - i) * 3000) / 65535);
            if (this.mIsSupportIrisSmooth && abs < 1000) {
                abs = 1000;
            }
            this.mSliderAnimator.setDuration(abs);
            this.mSliderAnimator.start();
            return;
        }
        this.mBrightPreference.setBrightness(i);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateSlider$0 */
    public /* synthetic */ void lambda$updateSlider$0$DisplaySettings(ValueAnimator valueAnimator) {
        this.mBrightPreference.setBrightness(((Integer) this.mSliderAnimator.getAnimatedValue()).intValue());
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateMode() {
        if (this.mAutomaticAvailable) {
            boolean z = false;
            if (Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_brightness_mode", 0, -2) != 0) {
                z = true;
            }
            this.mAutomatic = z;
            this.mAutoBrightnessPreference.setChecked(z);
        }
    }

    private class BrightnessObserver extends ContentObserver {
        private final Uri BRIGHTNESS_ADJ_URI = Settings.System.getUriFor("screen_auto_brightness_adj");
        private final Uri BRIGHTNESS_MODE_URI = Settings.System.getUriFor("screen_brightness_mode");
        private final Uri BRIGHTNESS_URI = Settings.System.getUriFor("screen_brightness_float");
        private final Uri SCREEN_TIMEOUT_URI = Settings.System.getUriFor("screen_off_timeout");

        public BrightnessObserver(Handler handler) {
            super(handler);
        }

        public void onChange(boolean z) {
            onChange(z, null);
        }

        public void onChange(boolean z, Uri uri) {
            if (!z) {
                try {
                    if (this.BRIGHTNESS_MODE_URI.equals(uri)) {
                        DisplaySettings.this.updateMode();
                        return;
                    }
                    if (!this.BRIGHTNESS_URI.equals(uri)) {
                        if (!this.BRIGHTNESS_ADJ_URI.equals(uri)) {
                            if (this.SCREEN_TIMEOUT_URI.equals(uri)) {
                                int i = Settings.System.getInt(DisplaySettings.this.mContext.getContentResolver(), "screen_off_timeout", 30000);
                                DisplaySettings.this.mScreenTimeoutPreference.setValue(String.valueOf(i));
                                DisplaySettings.this.updateTimeoutPreferenceDescription((long) i);
                                return;
                            }
                            return;
                        }
                    }
                    DisplaySettings.this.updateSlider();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void startObserving() {
            ContentResolver contentResolver = DisplaySettings.this.mContext.getContentResolver();
            contentResolver.unregisterContentObserver(this);
            contentResolver.registerContentObserver(this.BRIGHTNESS_MODE_URI, false, this, -1);
            contentResolver.registerContentObserver(this.BRIGHTNESS_URI, false, this, -1);
            contentResolver.registerContentObserver(this.BRIGHTNESS_ADJ_URI, false, this, -1);
            contentResolver.registerContentObserver(this.SCREEN_TIMEOUT_URI, false, this, -1);
        }

        public void stopObserving() {
            DisplaySettings.this.mContext.getContentResolver().unregisterContentObserver(this);
        }
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_uri_display;
    }

    private void disablePreferenceIfManaged(String str, String str2) {
        RestrictedPreference restrictedPreference = (RestrictedPreference) findPreference(str);
        if (restrictedPreference != null) {
            restrictedPreference.setDisabledByAdmin(null);
            if (RestrictedLockUtilsInternal.hasBaseUserRestriction(getActivity(), str2, UserHandle.myUserId())) {
                restrictedPreference.setEnabled(false);
            } else {
                restrictedPreference.checkRestrictionAndSetDisabled(str2);
            }
        }
    }
}
