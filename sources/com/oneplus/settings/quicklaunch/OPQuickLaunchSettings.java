package com.oneplus.settings.quicklaunch;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.C0003R$array;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.biometrics.fingerprint.FingerprintEnrollIntroduction;
import com.android.settings.search.BaseSearchIndexProvider;
import com.oneplus.settings.OPMemberController;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.better.OPAppModel;
import com.oneplus.settings.quickpay.QuickPayLottieAnimPreference;
import com.oneplus.settings.ui.OPViewPagerGuideCategory;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OPQuickLaunchSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener, QuickPayLottieAnimPreference.OnPreferenceViewClickListener {
    private static final int MY_USER_ID = UserHandle.myUserId();
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.oneplus.settings.quicklaunch.OPQuickLaunchSettings.AnonymousClass1 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = C0019R$xml.op_quicklaunch_settings;
            return Arrays.asList(searchIndexableResource);
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            return new ArrayList();
        }
    };
    private SettingsActivity mActivity;
    private List<OPAppModel> mDefaultQuickLaunchAppList = new ArrayList();
    private SwitchPreference mEnableQuickLaunch;
    private FingerprintManager mFingerprintManager;
    private boolean mHasFingerprint;
    private String[] mPayWaysName = SettingsBaseApplication.mApplication.getResources().getStringArray(C0003R$array.oneplus_quickpay_ways_name);
    private OPViewPagerGuideCategory mQuickLaunchGuide;
    private Preference mQuickLaunchPreferece;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        return false;
    }

    @Override // com.oneplus.settings.quickpay.QuickPayLottieAnimPreference.OnPreferenceViewClickListener
    public void onPreferenceViewClick(View view) {
    }

    @Override // androidx.fragment.app.Fragment
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        SettingsActivity settingsActivity = (SettingsActivity) getActivity();
        this.mActivity = settingsActivity;
        settingsActivity.getPackageManager();
        this.mFingerprintManager = (FingerprintManager) getActivity().getSystemService("fingerprint");
        initPreference();
        initDefaultData();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.mActivity = (SettingsActivity) getActivity();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        checkFingerPrint();
        updatePreferenceState();
        OPViewPagerGuideCategory oPViewPagerGuideCategory = this.mQuickLaunchGuide;
        if (oPViewPagerGuideCategory != null) {
            oPViewPagerGuideCategory.startAnim();
        }
    }

    private void checkFingerPrint() {
        if (this.mFingerprintManager.getEnrolledFingerprints(MY_USER_ID).size() > 0) {
            this.mHasFingerprint = true;
        } else {
            this.mHasFingerprint = false;
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        OPViewPagerGuideCategory oPViewPagerGuideCategory = this.mQuickLaunchGuide;
        if (oPViewPagerGuideCategory != null) {
            oPViewPagerGuideCategory.stopAnim();
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onDestroy() {
        super.onDestroy();
        OPViewPagerGuideCategory oPViewPagerGuideCategory = this.mQuickLaunchGuide;
        if (oPViewPagerGuideCategory != null) {
            oPViewPagerGuideCategory.releaseAnim();
        }
    }

    private void initPreference() {
        addPreferencesFromResource(C0019R$xml.op_quicklaunch_settings);
        this.mQuickLaunchPreferece = findPreference("op_quick_launcher_settings");
        if (!OPUtils.isSupportQuickLaunch()) {
            this.mQuickLaunchPreferece.setVisible(false);
        }
        SwitchPreference switchPreference = (SwitchPreference) findPreference("key_enable_quick_launch");
        this.mEnableQuickLaunch = switchPreference;
        switchPreference.setOnPreferenceChangeListener(this);
        OPViewPagerGuideCategory oPViewPagerGuideCategory = (OPViewPagerGuideCategory) findPreference("key_quick_launch_instructions");
        this.mQuickLaunchGuide = oPViewPagerGuideCategory;
        oPViewPagerGuideCategory.setAnimationWhiteResources(new String[]{"op_quick_launch_guide_active_white.json", "op_quick_launch_guide_exit_white.json"});
        this.mQuickLaunchGuide.setAnimationDarkResources(new String[]{"op_quick_launch_guide_active_dark.json", "op_quick_launch_guide_exit_dark.json"});
        this.mQuickLaunchGuide.setTitleResources(new int[]{C0017R$string.oneplus_quick_launch_how_to_use_title, C0017R$string.oneplus_quick_launch_how_to_exit_title});
        this.mQuickLaunchGuide.setDescriptionIdResources(new int[]{C0017R$string.oneplus_quick_launch_how_to_use_summary, C0017R$string.oneplus_quick_launch_how_to_exit_summary});
    }

    private void updatePreferenceState() {
        if (Settings.Secure.getInt(getContentResolver(), "op_quickpay_enable", 0) != 1) {
            this.mEnableQuickLaunch.setChecked(false);
        } else if (!this.mHasFingerprint) {
            Settings.Secure.putInt(getContentResolver(), "op_quickpay_enable", 0);
            this.mEnableQuickLaunch.setChecked(false);
        } else {
            this.mEnableQuickLaunch.setChecked(true);
        }
    }

    private void initDefaultData() {
        boolean z = false;
        if (Settings.System.getInt(getContentResolver(), "op_quick_launcher_edited", 0) == 1) {
            z = true;
        }
        if (!z) {
            createDefaultData();
            Settings.System.putInt(getContentResolver(), "op_quick_launcher_edited", 1);
        }
    }

    private void createDefaultData() {
        ResolveInfo resolveInfoByPackageName;
        ResolveInfo resolveInfoByPackageName2;
        ResolveInfo resolveInfoByPackageName3;
        ResolveInfo resolveInfoByPackageName4;
        this.mDefaultQuickLaunchAppList.clear();
        if (OPUtils.isO2()) {
            if (OPUtils.isAppExist(this.mActivity, "net.one97.paytm")) {
                OPAppModel oPAppModel = new OPAppModel("net.one97.paytm", this.mPayWaysName[4], String.valueOf(4), 0, false);
                oPAppModel.setType(2);
                oPAppModel.setAppIcon(OPUtils.getAppIcon(this.mActivity, "net.one97.paytm"));
                this.mDefaultQuickLaunchAppList.add(oPAppModel);
            }
            if (OPUtils.isAppExist(this.mActivity, "com.google.android.googlequicksearchbox") && (resolveInfoByPackageName4 = OPUtils.getResolveInfoByPackageName(this.mActivity, "com.google.android.googlequicksearchbox")) != null) {
                this.mDefaultQuickLaunchAppList.add(OPUtils.loadShortcutByPackageNameAndShortcutId(this.mActivity, "com.google.android.googlequicksearchbox", "voice_shortcut", resolveInfoByPackageName4.activityInfo.applicationInfo.uid));
            }
            if (OPUtils.isAppExist(this.mActivity, "com.oneplus.note") && (resolveInfoByPackageName3 = OPUtils.getResolveInfoByPackageName(this.mActivity, "com.oneplus.note")) != null) {
                this.mDefaultQuickLaunchAppList.add(OPUtils.loadShortcutByPackageNameAndShortcutId(this.mActivity, "com.oneplus.note", "new_note", resolveInfoByPackageName3.activityInfo.applicationInfo.uid));
            }
            if (OPUtils.isAppExist(this.mActivity, "com.google.android.music") && (resolveInfoByPackageName2 = OPUtils.getResolveInfoByPackageName(this.mActivity, "com.google.android.music")) != null) {
                this.mDefaultQuickLaunchAppList.add(OPUtils.loadShortcutByPackageNameAndShortcutId(this.mActivity, "com.google.android.music", "music-mylibrary", resolveInfoByPackageName2.activityInfo.applicationInfo.uid));
            }
            if (OPUtils.isAppExist(this.mActivity, "com.google.android.calendar") && (resolveInfoByPackageName = OPUtils.getResolveInfoByPackageName(this.mActivity, "com.google.android.calendar")) != null) {
                this.mDefaultQuickLaunchAppList.add(OPUtils.loadShortcutByPackageNameAndShortcutId(this.mActivity, "com.google.android.calendar", "launcher_shortcuts_shortcut_new_event", resolveInfoByPackageName.activityInfo.applicationInfo.uid));
            }
            StringBuilder sb = new StringBuilder();
            for (OPAppModel oPAppModel2 : this.mDefaultQuickLaunchAppList) {
                if (oPAppModel2 != null) {
                    String quickPayAppString = OPUtils.getQuickPayAppString(oPAppModel2);
                    if (oPAppModel2.getType() == 0) {
                        quickPayAppString = OPUtils.getQuickLaunchAppString(oPAppModel2);
                    } else if (oPAppModel2.getType() == 1) {
                        quickPayAppString = OPUtils.getQuickLaunchShortcutsString(oPAppModel2);
                    } else if (oPAppModel2.getType() == 2) {
                        quickPayAppString = OPUtils.getQuickPayAppString(oPAppModel2);
                    }
                    sb.append(quickPayAppString);
                    OPUtils.saveQuickLaunchStrings(this.mActivity, sb.toString());
                }
            }
        } else {
            if (OPUtils.isAppExist(this.mActivity, "com.tencent.mm")) {
                OPAppModel oPAppModel3 = new OPAppModel("com.tencent.mm", this.mPayWaysName[0], String.valueOf(0), 0, false);
                oPAppModel3.setType(2);
                oPAppModel3.setAppIcon(OPUtils.getQuickPayIconByType(this.mActivity, 0));
                OPAppModel oPAppModel4 = new OPAppModel("com.tencent.mm", this.mPayWaysName[1], String.valueOf(1), 0, false);
                oPAppModel4.setType(2);
                oPAppModel4.setAppIcon(OPUtils.getQuickPayIconByType(this.mActivity, 1));
                OPAppModel oPAppModel5 = new OPAppModel("com.tencent.mm", this.mPayWaysName[2], String.valueOf(0), 0, false);
                oPAppModel5.setType(3);
                oPAppModel5.setAppIcon(OPUtils.getQuickMiniProgrameconByType(this.mActivity, 0));
                this.mDefaultQuickLaunchAppList.add(oPAppModel3);
                this.mDefaultQuickLaunchAppList.add(oPAppModel4);
                this.mDefaultQuickLaunchAppList.add(oPAppModel5);
            }
            if (OPUtils.isAppExist(this.mActivity, "com.eg.android.AlipayGphone")) {
                OPAppModel oPAppModel6 = new OPAppModel("com.eg.android.AlipayGphone", this.mPayWaysName[3], String.valueOf(2), 0, false);
                oPAppModel6.setType(2);
                oPAppModel6.setAppIcon(OPUtils.getQuickPayIconByType(this.mActivity, 2));
                OPAppModel oPAppModel7 = new OPAppModel("com.eg.android.AlipayGphone", this.mPayWaysName[4], String.valueOf(3), 0, false);
                oPAppModel7.setType(2);
                oPAppModel7.setAppIcon(OPUtils.getQuickPayIconByType(this.mActivity, 3));
                this.mDefaultQuickLaunchAppList.add(oPAppModel6);
                this.mDefaultQuickLaunchAppList.add(oPAppModel7);
            }
            StringBuilder sb2 = new StringBuilder();
            for (OPAppModel oPAppModel8 : this.mDefaultQuickLaunchAppList) {
                if (OPUtils.isQuickPayModel(oPAppModel8)) {
                    sb2.append(OPUtils.getQuickPayAppString(oPAppModel8));
                } else if (OPUtils.isWeChatMiniProgrameModel(oPAppModel8)) {
                    sb2.append(OPUtils.getQuickMiniProgrameString(oPAppModel8));
                }
                OPUtils.saveQuickLaunchStrings(this.mActivity, sb2.toString());
            }
        }
        OPUtils.sendAppTrackerForQuickLaunch();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference != this.mEnableQuickLaunch) {
            return false;
        }
        boolean booleanValue = ((Boolean) obj).booleanValue();
        if (!booleanValue || this.mHasFingerprint) {
            Settings.Secure.putInt(getContentResolver(), "op_quickpay_enable", booleanValue ? 1 : 0);
            OPUtils.sendAppTrackerForQuickLaunchToggle();
            return true;
        }
        gotoFingerprintEnrollIntroduction(1);
        return false;
    }

    public void gotoFingerprintEnrollIntroduction(int i) {
        Intent intent = new Intent();
        intent.setClassName(OPMemberController.PACKAGE_NAME, FingerprintEnrollIntroduction.class.getName());
        startActivityForResult(intent, i);
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 1) {
            checkFingerPrint();
            if (this.mHasFingerprint) {
                Settings.Secure.putInt(getContentResolver(), "op_quickpay_enable", 1);
            }
        }
        super.onActivityResult(i, i2, intent);
    }
}
