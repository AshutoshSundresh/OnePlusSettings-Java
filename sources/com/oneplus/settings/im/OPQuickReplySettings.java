package com.oneplus.settings.im;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.SwitchPreference;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.oneplus.settings.apploader.OPApplicationLoader;
import com.oneplus.settings.better.OPAppModel;
import com.oneplus.settings.quickpay.QuickPayLottieAnimPreference;
import com.oneplus.settings.ui.OPViewPagerGuideCategory;
import com.oneplus.settings.utils.OPApplicationUtils;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OPQuickReplySettings extends SettingsPreferenceFragment implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener, QuickPayLottieAnimPreference.OnPreferenceViewClickListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.oneplus.settings.im.OPQuickReplySettings.AnonymousClass2 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = C0019R$xml.op_quickreply_settings;
            return Arrays.asList(searchIndexableResource);
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            return new ArrayList();
        }
    };
    private AppOpsManager mAppOpsManager;
    private Context mContext;
    private List<OPAppModel> mDefaultQuickReplyAppList = new ArrayList();
    private SwitchPreference mEnableQuickReply;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        /* class com.oneplus.settings.im.OPQuickReplySettings.AnonymousClass1 */

        public void handleMessage(Message message) {
            super.handleMessage(message);
            OPQuickReplySettings oPQuickReplySettings = OPQuickReplySettings.this;
            if (!(oPQuickReplySettings.mAdapter == null || oPQuickReplySettings.mOPApplicationLoader == null)) {
                OPQuickReplySettings.this.mSupportedApps.removeAll();
                OPQuickReplySettings.this.mDefaultQuickReplyAppList.clear();
                OPQuickReplySettings.this.mDefaultQuickReplyAppList.addAll(OPQuickReplySettings.this.mOPApplicationLoader.getAppListByType(message.what));
                for (final OPAppModel oPAppModel : OPQuickReplySettings.this.mDefaultQuickReplyAppList) {
                    SwitchPreference switchPreference = new SwitchPreference(OPQuickReplySettings.this.mContext);
                    switchPreference.setLayoutResource(C0012R$layout.op_preference_material);
                    switchPreference.setWidgetLayoutResource(C0012R$layout.op_preference_widget_switch);
                    String pkgName = oPAppModel.getPkgName();
                    if (OPApplicationUtils.isIMQuickReplyApps(pkgName)) {
                        switchPreference.setKey(pkgName);
                        switchPreference.setTitle(oPAppModel.getLabel());
                        switchPreference.setIcon(oPAppModel.getAppIcon());
                        switchPreference.setChecked(OPUtils.isQuickReplyAppSelected(oPAppModel));
                        switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                            /* class com.oneplus.settings.im.OPQuickReplySettings.AnonymousClass1.AnonymousClass1 */

                            @Override // androidx.preference.Preference.OnPreferenceChangeListener
                            public boolean onPreferenceChange(Preference preference, Object obj) {
                                boolean booleanValue = ((Boolean) obj).booleanValue();
                                StringBuilder sb = new StringBuilder(OPUtils.getQuickReplyAppListString(OPQuickReplySettings.this.mContext));
                                String quickReplyAppString = OPUtils.getQuickReplyAppString(oPAppModel);
                                if (booleanValue) {
                                    sb.append(quickReplyAppString);
                                } else {
                                    int indexOf = sb.indexOf(quickReplyAppString);
                                    try {
                                        sb.delete(indexOf, quickReplyAppString.length() + indexOf);
                                    } catch (Exception e) {
                                        Log.w("OPQuickReplySettings", "quickReplyApp.delete error for index:" + indexOf + ", replyApp:" + quickReplyAppString + ", quickReplyApp:" + sb.toString());
                                        e.printStackTrace();
                                    }
                                }
                                if (TextUtils.isEmpty(sb)) {
                                    Settings.Global.putInt(OPQuickReplySettings.this.getContentResolver(), "enable_freeform_support", 0);
                                } else {
                                    Settings.Global.putInt(OPQuickReplySettings.this.getContentResolver(), "enable_freeform_support", 1);
                                }
                                OPUtils.saveQuickReplyAppLisStrings(OPQuickReplySettings.this.mContext, sb.toString());
                                OPUtils.sendAppTrackerForQuickReplyIMStatus();
                                return true;
                            }
                        });
                        OPQuickReplySettings.this.mSupportedApps.addPreference(switchPreference);
                    }
                }
                if (OPQuickReplySettings.this.mSupportedApps.getPreferenceCount() == 0) {
                    OPQuickReplySettings.this.mSupportedApps.addPreference(OPQuickReplySettings.this.mNoSupportedApps);
                }
            }
        }
    };
    private Preference mNoSupportedApps;
    private OPApplicationLoader mOPApplicationLoader;
    private PackageManager mPackageManager;
    private OPViewPagerGuideCategory mQuickReplyGuide;
    private PreferenceCategory mSupportedApps;

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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = activity;
    }

    @Override // androidx.fragment.app.Fragment
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        SettingsActivity settingsActivity = (SettingsActivity) getActivity();
        Context context = this.mContext;
        if (context != null) {
            this.mPackageManager = context.getPackageManager();
            this.mAppOpsManager = (AppOpsManager) this.mContext.getSystemService("appops");
            this.mOPApplicationLoader = new OPApplicationLoader(this.mContext, this.mAppOpsManager, this.mPackageManager);
        }
        initPreference();
        initData();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        SettingsActivity settingsActivity = (SettingsActivity) getActivity();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        updatePreferenceState();
        OPViewPagerGuideCategory oPViewPagerGuideCategory = this.mQuickReplyGuide;
        if (oPViewPagerGuideCategory != null) {
            oPViewPagerGuideCategory.startAnim();
        }
    }

    private void initData() {
        this.mOPApplicationLoader.setNeedLoadWorkProfileApps(false);
        this.mOPApplicationLoader.initData(4, this.mHandler);
    }

    private void updatePreferenceState() {
        boolean z = false;
        int i = Settings.System.getInt(getContentResolver(), "op_quickreply_ime_adjust", 0);
        SwitchPreference switchPreference = this.mEnableQuickReply;
        if (i == 1) {
            z = true;
        }
        switchPreference.setChecked(z);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        OPViewPagerGuideCategory oPViewPagerGuideCategory = this.mQuickReplyGuide;
        if (oPViewPagerGuideCategory != null) {
            oPViewPagerGuideCategory.stopAnim();
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onDestroy() {
        super.onDestroy();
        OPViewPagerGuideCategory oPViewPagerGuideCategory = this.mQuickReplyGuide;
        if (oPViewPagerGuideCategory != null) {
            oPViewPagerGuideCategory.releaseAnim();
        }
    }

    private void initPreference() {
        addPreferencesFromResource(C0019R$xml.op_quickreply_settings);
        this.mSupportedApps = (PreferenceCategory) findPreference("oneplus_surpported_apps");
        this.mNoSupportedApps = findPreference("oneplus_no_surpported_apps");
        SwitchPreference switchPreference = (SwitchPreference) findPreference("key_enable_quick_reply");
        this.mEnableQuickReply = switchPreference;
        switchPreference.setOnPreferenceChangeListener(this);
        OPViewPagerGuideCategory oPViewPagerGuideCategory = (OPViewPagerGuideCategory) findPreference("key_quick_reply_instructions");
        this.mQuickReplyGuide = oPViewPagerGuideCategory;
        oPViewPagerGuideCategory.showDotView(false);
        this.mQuickReplyGuide.setType(2);
        this.mQuickReplyGuide.setAnimationWhiteResources(new String[]{"op_quick_reply_guide_light.json"});
        this.mQuickReplyGuide.setAnimationDarkResources(new String[]{"op_quick_reply_guide_dark.json"});
        this.mQuickReplyGuide.setTitleResources(new int[]{C0017R$string.oneplus_quick_reply});
        if (OPUtils.isO2()) {
            this.mQuickReplyGuide.setDescriptionIdResources(new int[]{C0017R$string.oneplus_quick_reply_description_O2});
            return;
        }
        this.mQuickReplyGuide.setDescriptionIdResources(new int[]{C0017R$string.oneplus_quick_reply_description});
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference != this.mEnableQuickReply) {
            return false;
        }
        Settings.System.putInt(getContentResolver(), "op_quickreply_ime_adjust", ((Boolean) obj).booleanValue() ? 1 : 0);
        OPUtils.sendAppTrackerForQuickReplyKeyboardStatus();
        return true;
    }
}
