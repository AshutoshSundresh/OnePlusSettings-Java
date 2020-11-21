package com.oneplus.settings.navigationbargestures;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.om.IOverlayManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.SwitchPreference;
import com.android.settings.C0010R$id;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.RadioButtonPreference;
import com.android.settingslib.widget.LayoutPreference;
import com.oneplus.common.ReflectUtil;
import com.oneplus.settings.OPMemberController;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.ui.OPPhoneControlWayCategory;
import com.oneplus.settings.utils.OPApplicationUtils;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OPNavigationBarGesturesSettings extends SettingsPreferenceFragment implements RadioButtonPreference.OnClickListener, Preference.OnPreferenceChangeListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.oneplus.settings.navigationbargestures.OPNavigationBarGesturesSettings.AnonymousClass1 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            if (!OPUtils.isGuestMode()) {
                searchIndexableResource.xmlResId = C0019R$xml.op_navigation_bar_gestures_settings;
            }
            return Arrays.asList(searchIndexableResource);
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            super.getNonIndexableKeys(context);
            ArrayList arrayList = new ArrayList();
            if (OPNavigationBarGesturesSettings.isSupportHardwareKeys()) {
                arrayList.add("customization");
                arrayList.add("choose_navigation_bar");
                arrayList.add("always_show_navigation_bar");
                arrayList.add("gesture_navigation_bar");
                arrayList.add("phone_control_way");
                arrayList.add("gesture_hidden_bar");
            }
            if (!ReflectUtil.isFeatureSupported("OP_FEATURE_GESTURE_DEPRECATED")) {
                arrayList.add("gesture_navigation_bar_deprecated");
            }
            return arrayList;
        }
    };
    private RadioButtonPreference mAlwaysShowNavigationBar;
    private ActivityManager mAm;
    private Context mContext;
    private PreferenceCategory mCustomSettingsCategory;
    private Preference mCustomization;
    private String mEnterStatus;
    private SwitchPreference mGestureHiddenBar;
    private RadioButtonPreference mGestureNavigationBar;
    private RadioButtonPreference mGestureNavigationBarDeprecated;
    private Handler mHandler;
    private OPPhoneControlWayCategory mOPPhoneControlWayCategory;
    private IOverlayManager mOverlayManager;
    private LayoutPreference mbuttonLayoutPref;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.op_navigation_bar_gestures_settings);
        this.mAm = (ActivityManager) getSystemService("activity");
        this.mContext = SettingsBaseApplication.mApplication;
        this.mCustomization = findPreference("customization");
        this.mAlwaysShowNavigationBar = (RadioButtonPreference) findPreference("always_show_navigation_bar");
        this.mGestureNavigationBar = (RadioButtonPreference) findPreference("gesture_navigation_bar");
        this.mGestureNavigationBarDeprecated = (RadioButtonPreference) findPreference("gesture_navigation_bar_deprecated");
        this.mGestureHiddenBar = (SwitchPreference) findPreference("gesture_hidden_bar");
        this.mOPPhoneControlWayCategory = (OPPhoneControlWayCategory) findPreference("phone_control_way");
        this.mCustomSettingsCategory = (PreferenceCategory) findPreference("custom_settings_category");
        this.mHandler = new Handler();
        if (OPUtils.isO2()) {
            this.mAlwaysShowNavigationBar.setTitle(C0017R$string.oneplus_fixed_navigation_bar_o2);
        }
        if (ReflectUtil.isFeatureSupported("OP_FEATURE_GESTURE_DEPRECATED")) {
            this.mGestureNavigationBar.setTitle(C0017R$string.oneplus_gesture_navigation_bar_title_for_deprecated);
            this.mGestureNavigationBar.setSummary(C0017R$string.oneplus_gesture_navigation_bar_summary_for_deprecated);
            this.mGestureNavigationBarDeprecated.setVisible(true);
        } else {
            this.mGestureNavigationBarDeprecated.setVisible(false);
        }
        if (ReflectUtil.isFeatureSupported("OP_FEATURE_HIDE_NAVBAR")) {
            this.mCustomization.setSummary(C0017R$string.oneplus_customization_summary_hide_navbar);
        }
        this.mAlwaysShowNavigationBar.setOnClickListener(this);
        this.mGestureNavigationBar.setOnClickListener(this);
        this.mGestureNavigationBarDeprecated.setOnClickListener(this);
        this.mGestureHiddenBar.setOnPreferenceChangeListener(this);
        this.mOverlayManager = IOverlayManager.Stub.asInterface(ServiceManager.getService("overlay"));
        LayoutPreference layoutPreference = (LayoutPreference) findPreference("learn_gesture_button_container");
        this.mbuttonLayoutPref = layoutPreference;
        ((Button) layoutPreference.findViewById(C0010R$id.learn_gesture_button)).setOnClickListener(new View.OnClickListener() {
            /* class com.oneplus.settings.navigationbargestures.$$Lambda$OPNavigationBarGesturesSettings$SqMDUtfMPpqX0AIjfoq9awLuak8 */

            public final void onClick(View view) {
                OPNavigationBarGesturesSettings.this.lambda$onCreate$0$OPNavigationBarGesturesSettings(view);
            }
        });
        if (!isGesturalEnabled(this.mContext) && !is2ButtonEnabled(this.mContext)) {
            this.mAlwaysShowNavigationBar.setChecked(true);
            this.mCustomization.setVisible(true);
            this.mGestureHiddenBar.setVisible(false);
            this.mbuttonLayoutPref.setVisible(false);
        } else if (isGesturalEnabled(this.mContext) && isSideEnabled(this.mContext)) {
            this.mGestureNavigationBar.setChecked(true);
            this.mCustomization.setVisible(false);
            this.mGestureHiddenBar.setVisible(true);
            this.mGestureHiddenBar.setChecked(isHideBarEnabled(this.mContext));
            this.mbuttonLayoutPref.setVisible(true);
        } else if (isGesturalEnabled(this.mContext) && !isSideEnabled(this.mContext)) {
            this.mGestureNavigationBarDeprecated.setChecked(true);
            this.mCustomSettingsCategory.setVisible(false);
            this.mbuttonLayoutPref.setVisible(false);
        }
        if (!isGesturalEnabled(this.mContext) && !is2ButtonEnabled(this.mContext)) {
            this.mEnterStatus = "always_show_navigation_bar";
        } else if (isGesturalEnabled(this.mContext) && !isHideBarEnabled(this.mContext)) {
            this.mEnterStatus = "gesture_navigation_bar";
        } else if (!isGesturalEnabled(this.mContext) || !isHideBarEnabled(this.mContext)) {
            this.mEnterStatus = "gesture_navigation_bar_deprecated";
        } else {
            this.mEnterStatus = "gesture_hidden_bar";
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreate$0 */
    public /* synthetic */ void lambda$onCreate$0$OPNavigationBarGesturesSettings(View view) {
        Intent intent = new Intent();
        intent.setClassName("net.oneplus.launcher", "net.oneplus.launcher.gestureGuide.GestureTutorialActivity");
        intent.putExtra("extra_launch_gesture_tutorial_from", 0);
        startActivity(intent);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        Log.d("OPNavigationBarGesturesSettings", "onResume context resDirs:" + Arrays.toString(this.mContext.getApplicationInfo().resourceDirs));
        Log.d("OPNavigationBarGesturesSettings", "onResume getApplicationContext resDirs:" + Arrays.toString(getContext().getApplicationContext().getApplicationInfo().resourceDirs));
        OPPhoneControlWayCategory oPPhoneControlWayCategory = this.mOPPhoneControlWayCategory;
        if (oPPhoneControlWayCategory != null) {
            oPPhoneControlWayCategory.startAnim();
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        Log.d("OPNavigationBarGesturesSettings", "onPause context resDirs:" + Arrays.toString(this.mContext.getApplicationInfo().resourceDirs));
        OPPhoneControlWayCategory oPPhoneControlWayCategory = this.mOPPhoneControlWayCategory;
        if (oPPhoneControlWayCategory != null) {
            oPPhoneControlWayCategory.stopAnim();
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStop() {
        String str;
        super.onStop();
        Log.d("OPNavigationBarGesturesSettings", "onStop context resDirs:" + Arrays.toString(this.mContext.getApplicationInfo().resourceDirs));
        if (is2ButtonEnabled(this.mContext)) {
            if (!this.mEnterStatus.equals("back_home")) {
                OPUtils.sendAnalytics("nav_gesture", "status", "2");
            }
            str = "back_home";
        } else if (is3ButtonEnabled(this.mContext)) {
            if (!"always_show_navigation_bar".equals(this.mEnterStatus)) {
                OPUtils.sendAnalytics("nav_gesture", "status", "1");
            }
            str = "always_show_navigation_bar";
        } else {
            if (isGesturalEnabled(this.mContext) && !"gesture_navigation_bar".equals(this.mEnterStatus)) {
                OPUtils.sendAnalytics("nav_gesture", "status", OPMemberController.CLIENT_TYPE);
            }
            str = "gesture_navigation_bar";
        }
        if (str.equals(this.mEnterStatus)) {
            return;
        }
        if ("always_show_navigation_bar".equalsIgnoreCase(this.mEnterStatus) && "gesture_navigation_bar".equalsIgnoreCase(str)) {
            OPUtils.sendAnalytics("nav_gesture", "resource", "full_screen_default");
        } else if ("back_home".equalsIgnoreCase(this.mEnterStatus) && "gesture_navigation_bar".equalsIgnoreCase(str)) {
            OPUtils.sendAnalytics("nav_gesture", "resource", "full_screen_capsule");
        } else if ("gesture_navigation_bar".equalsIgnoreCase(this.mEnterStatus) && "always_show_navigation_bar".equalsIgnoreCase(str)) {
            OPUtils.sendAnalytics("nav_gesture", "resource", "default_full_screen");
        } else if (!"gesture_navigation_bar".equalsIgnoreCase(this.mEnterStatus) || !"back_home".equalsIgnoreCase(str)) {
            OPUtils.sendAnalytics("nav_gesture", "resource", "others");
        } else {
            OPUtils.sendAnalytics("nav_gesture", "resource", "capsule_full_screen");
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onDestroy() {
        super.onDestroy();
        OPPhoneControlWayCategory oPPhoneControlWayCategory = this.mOPPhoneControlWayCategory;
        if (oPPhoneControlWayCategory != null) {
            oPPhoneControlWayCategory.releaseAnim();
        }
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    public static boolean is3ButtonEnabled(Context context) {
        return context.getResources().getInteger(17694854) == 0;
    }

    public static boolean is2ButtonEnabled(Context context) {
        return 1 == context.getResources().getInteger(17694854);
    }

    public static boolean isGesturalEnabled(Context context) {
        return 2 == context.getResources().getInteger(17694854);
    }

    static boolean isHideBarEnabled(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "oneplus_fullscreen_gesture_type", 0) == 1;
    }

    static boolean isSideEnabled(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "op_gesture_button_side_enabled", 0) == 1;
    }

    public static boolean isCustomSettingsEnable(Context context) {
        return is3ButtonEnabled(context);
    }

    private void resetAppNavigationBarMode(boolean z) {
        getActivity();
    }

    @Override // com.android.settings.widget.RadioButtonPreference.OnClickListener
    public void onRadioButtonClicked(RadioButtonPreference radioButtonPreference) {
        OPApplicationUtils.killProcess(this.mAm, true);
        if (radioButtonPreference == this.mAlwaysShowNavigationBar) {
            if (!is3ButtonEnabled(this.mContext)) {
                resetAppNavigationBarMode(false);
                this.mAlwaysShowNavigationBar.setChecked(true);
                this.mAlwaysShowNavigationBar.setEnabled(false);
                this.mGestureNavigationBar.setChecked(false);
                this.mGestureNavigationBar.setEnabled(false);
                this.mGestureNavigationBarDeprecated.setChecked(false);
                this.mGestureNavigationBarDeprecated.setEnabled(false);
                this.mCustomSettingsCategory.setVisible(true);
                this.mCustomization.setVisible(true);
                this.mCustomization.setEnabled(false);
                this.mGestureHiddenBar.setVisible(false);
                this.mbuttonLayoutPref.setVisible(false);
                this.mOPPhoneControlWayCategory.setVisible(false);
                setNavBarInteractionMode(this.mOverlayManager, "com.android.internal.systemui.navbar.threebutton");
            }
        } else if (radioButtonPreference == this.mGestureNavigationBar) {
            if (!isGesturalEnabled(this.mContext) || !isSideEnabled(this.mContext)) {
                resetAppNavigationBarMode(true);
                this.mAlwaysShowNavigationBar.setChecked(false);
                this.mAlwaysShowNavigationBar.setEnabled(false);
                this.mGestureNavigationBar.setChecked(true);
                this.mGestureNavigationBar.setEnabled(false);
                this.mGestureNavigationBarDeprecated.setChecked(false);
                this.mGestureNavigationBarDeprecated.setEnabled(false);
                this.mCustomSettingsCategory.setVisible(true);
                this.mCustomization.setVisible(false);
                this.mGestureHiddenBar.setVisible(true);
                this.mGestureHiddenBar.setEnabled(false);
                this.mGestureHiddenBar.setChecked(isHideBarEnabled(this.mContext));
                this.mbuttonLayoutPref.setVisible(true);
                if (isGesturalEnabled(this.mContext)) {
                    setSideEnable(this.mContext, true);
                    this.mOPPhoneControlWayCategory.setViewType(isHideBarEnabled(getContext()) ? 4 : 2);
                    this.mHandler.postDelayed(new Runnable() {
                        /* class com.oneplus.settings.navigationbargestures.$$Lambda$OPNavigationBarGesturesSettings$gemNwH7kwFrpIyA9VNh6ACnKCLM */

                        public final void run() {
                            OPNavigationBarGesturesSettings.this.lambda$onRadioButtonClicked$1$OPNavigationBarGesturesSettings();
                        }
                    }, 500);
                    return;
                }
                this.mOPPhoneControlWayCategory.setVisible(false);
                this.mbuttonLayoutPref.setVisible(false);
                setSideEnable(this.mContext, true);
                setNavBarInteractionMode(this.mOverlayManager, "com.android.internal.systemui.navbar.gestural");
            }
        } else if (radioButtonPreference != this.mGestureNavigationBarDeprecated) {
        } else {
            if (!isGesturalEnabled(this.mContext) || isSideEnabled(this.mContext)) {
                resetAppNavigationBarMode(false);
                this.mAlwaysShowNavigationBar.setChecked(false);
                this.mAlwaysShowNavigationBar.setEnabled(false);
                this.mGestureNavigationBar.setChecked(false);
                this.mGestureNavigationBar.setEnabled(false);
                this.mGestureNavigationBarDeprecated.setChecked(true);
                this.mGestureNavigationBarDeprecated.setEnabled(false);
                this.mCustomSettingsCategory.setVisible(false);
                this.mbuttonLayoutPref.setVisible(false);
                if (isGesturalEnabled(this.mContext)) {
                    setSideEnable(this.mContext, false);
                    this.mOPPhoneControlWayCategory.setViewType(3);
                    this.mHandler.postDelayed(new Runnable() {
                        /* class com.oneplus.settings.navigationbargestures.$$Lambda$OPNavigationBarGesturesSettings$owtEVR48lEgyPwANgsMLWgqElWo */

                        public final void run() {
                            OPNavigationBarGesturesSettings.this.lambda$onRadioButtonClicked$2$OPNavigationBarGesturesSettings();
                        }
                    }, 500);
                    return;
                }
                setSideEnable(this.mContext, false);
                this.mOPPhoneControlWayCategory.setVisible(false);
                setNavBarInteractionMode(this.mOverlayManager, "com.android.internal.systemui.navbar.gestural");
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onRadioButtonClicked$1 */
    public /* synthetic */ void lambda$onRadioButtonClicked$1$OPNavigationBarGesturesSettings() {
        this.mAlwaysShowNavigationBar.setEnabled(true);
        this.mGestureNavigationBar.setEnabled(true);
        this.mGestureNavigationBarDeprecated.setEnabled(true);
        this.mGestureHiddenBar.setEnabled(true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onRadioButtonClicked$2 */
    public /* synthetic */ void lambda$onRadioButtonClicked$2$OPNavigationBarGesturesSettings() {
        this.mAlwaysShowNavigationBar.setEnabled(true);
        this.mGestureNavigationBar.setEnabled(true);
        this.mGestureNavigationBarDeprecated.setEnabled(true);
    }

    private static void setSideEnable(Context context, boolean z) {
        Settings.System.putInt(context.getContentResolver(), "op_gesture_button_side_enabled", z ? 1 : 0);
    }

    private static void setNavBarInteractionMode(IOverlayManager iOverlayManager, String str) {
        try {
            iOverlayManager.setEnabledExclusiveInCategory(str, -2);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference != this.mGestureHiddenBar) {
            return false;
        }
        Boolean bool = (Boolean) obj;
        Settings.System.putInt(this.mContext.getContentResolver(), "oneplus_fullscreen_gesture_type", bool.booleanValue() ? 1 : 0);
        if (bool.booleanValue()) {
            this.mOPPhoneControlWayCategory.setViewType(4, 3);
        } else {
            this.mOPPhoneControlWayCategory.setViewType(2);
        }
        this.mGestureHiddenBar.setEnabled(false);
        this.mHandler.postDelayed(new Runnable() {
            /* class com.oneplus.settings.navigationbargestures.$$Lambda$OPNavigationBarGesturesSettings$FBs9QW0KxYbC2etmrPZQQKvglhQ */

            public final void run() {
                OPNavigationBarGesturesSettings.this.lambda$onPreferenceChange$3$OPNavigationBarGesturesSettings();
            }
        }, 1000);
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onPreferenceChange$3 */
    public /* synthetic */ void lambda$onPreferenceChange$3$OPNavigationBarGesturesSettings() {
        this.mGestureHiddenBar.setEnabled(true);
    }

    /* access modifiers changed from: private */
    public static boolean isSupportHardwareKeys() {
        return !SettingsBaseApplication.mApplication.getResources().getBoolean(17891529);
    }
}
