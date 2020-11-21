package com.oneplus.settings.others;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.util.Log;
import android.util.OpFeatures;
import androidx.constraintlayout.widget.R$styleable;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.password.ChooseLockGeneric;
import com.android.settings.password.ChooseLockSettingsHelper;
import com.android.settings.search.BaseSearchIndexProvider;
import com.oneplus.common.ReflectUtil;
import com.oneplus.settings.OPMemberController;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.quickpay.QuickPaySettings;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OPToolsSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.oneplus.settings.others.OPToolsSettings.AnonymousClass1 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = C0019R$xml.op_tools_settings;
            return Arrays.asList(searchIndexableResource);
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            ArrayList arrayList = new ArrayList();
            if (OPUtils.isGuestMode()) {
                arrayList.add("anti_misoperation_of_the_screen_touch_enable");
                arrayList.add("oneplus_app_locker");
                arrayList.add("oneplus_laboratory_settings");
                arrayList.add("oneplus_multi_app");
                arrayList.add("worklifebalance");
            }
            if (!OPUtils.isSupportPocketMode()) {
                arrayList.add("anti_misoperation_of_the_screen_touch_enable");
            }
            if (!OPUtils.isLaboratoryFeatureExist()) {
                arrayList.add("oneplus_laboratory_settings");
            }
            if (OPUtils.isSupportQuickLaunch()) {
                arrayList.add("oneplus_quick_pay");
            } else {
                arrayList.add("oneplus_quick_launch");
            }
            if (!QuickPaySettings.canShowQuickPay(context) || OPUtils.isGuestMode()) {
                arrayList.add("oneplus_quick_pay");
            }
            if (OPUtils.isGuestMode()) {
                arrayList.add("oneplus_quick_launch");
            }
            if (!OPUtils.isAppPakExist(context, "com.oneplus.clipboard")) {
                arrayList.add("quick_clipboard");
            }
            if (!OPUtils.isAppExist(context, "com.oneplus.backuprestore")) {
                arrayList.add("switch");
            }
            if (OPUtils.isAppExist(context, "com.oneplus.gamespace")) {
                arrayList.add("gaming_mode");
            }
            if (!OPToolsSettings.isNeedShowGameSpace(context)) {
                arrayList.add("game_space");
            }
            if (OPUtils.isGuestMode()) {
                arrayList.add("oneplus_quick_launch");
            }
            if (!OPUtils.isSupportQuickReply() || OPUtils.isGuestMode()) {
                arrayList.add("oneplus_quick_replay");
            }
            if (!OPToolsSettings.isNeedShowWorkLife(context)) {
                arrayList.add("worklifebalance");
            }
            if (ReflectUtil.isFeatureSupported("OP_FEATURE_SECOND_PRIVATE_PASSWORD")) {
                arrayList.add("oneplus_app_locker");
            }
            return arrayList;
        }
    };
    private SwitchPreference mAntiMisOperationTouch;
    private Preference mAppLocker;
    private long mChallenge;
    private Context mContext;
    private Preference mGameSpacePreference;
    private boolean mGotoAppLockerClick = false;
    private Preference mMultiAppPreference;
    private Preference mOneplusLaboratorySettings;
    private Preference mOneplusQuickReply;
    private Preference mQuickLaunchPreference;
    private Preference mQuickPayPreference;
    private Preference mSwitchPreference;
    private Preference mTimerShutdownPreference;
    private WLBFeatureObserver mWlbFeatureObserver;
    private Preference mWorkLifeBalancePreference;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.op_tools_settings);
        this.mContext = getActivity();
        updateView();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        if (this.mGameSpacePreference != null) {
            if (!isNeedShowGameSpace(getContext())) {
                this.mGameSpacePreference.setVisible(false);
            } else {
                this.mGameSpacePreference.setVisible(true);
            }
        }
        updatePreferencesVisibility();
    }

    private void updatePreferencesVisibility() {
        if (this.mWorkLifeBalancePreference == null) {
            return;
        }
        if (!isNeedShowWorkLife(getContext())) {
            this.mWorkLifeBalancePreference.setVisible(false);
        } else {
            this.mWorkLifeBalancePreference.setVisible(true);
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onDestroy() {
        super.onDestroy();
        if (this.mWlbFeatureObserver != null) {
            getContentResolver().unregisterContentObserver(this.mWlbFeatureObserver);
            this.mWlbFeatureObserver = null;
        }
    }

    private void updateView() {
        Preference preference;
        Preference findPreference;
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        Preference findPreference2 = findPreference("oneplus_multi_app");
        this.mMultiAppPreference = findPreference2;
        findPreference2.setOnPreferenceClickListener(this);
        Preference findPreference3 = findPreference("oneplus_app_locker");
        this.mAppLocker = findPreference3;
        findPreference3.setOnPreferenceClickListener(this);
        if (ReflectUtil.isFeatureSupported("OP_FEATURE_SECOND_PRIVATE_PASSWORD")) {
            this.mAppLocker.setVisible(false);
        }
        this.mQuickPayPreference = findPreference("oneplus_quick_pay");
        this.mQuickLaunchPreference = findPreference("oneplus_quick_launch");
        this.mWorkLifeBalancePreference = findPreference("worklifebalance");
        if (OPUtils.isSupportQuickLaunch()) {
            this.mQuickPayPreference.setVisible(false);
        } else {
            this.mQuickLaunchPreference.setVisible(false);
        }
        if (OPUtils.isGuestMode()) {
            this.mQuickLaunchPreference.setVisible(false);
        }
        if (!QuickPaySettings.canShowQuickPay(getContext()) || OPUtils.isGuestMode()) {
            this.mQuickPayPreference.setVisible(false);
        }
        if (OPUtils.isSurportBackFingerprint(SettingsBaseApplication.mApplication)) {
            this.mQuickPayPreference.setSummary(C0017R$string.oneplus_fingerprint_longpress_for_quickpay_summary);
        } else {
            this.mQuickPayPreference.setSummary(C0017R$string.oneplus_quickpay_entry_summary);
        }
        SwitchPreference switchPreference = (SwitchPreference) findPreference("anti_misoperation_of_the_screen_touch_enable");
        this.mAntiMisOperationTouch = switchPreference;
        switchPreference.setOnPreferenceChangeListener(this);
        this.mAntiMisOperationTouch.setChecked(Settings.System.getInt(getContentResolver(), "oem_acc_anti_misoperation_screen", 0) != 0);
        if (!OPUtils.isSupportPocketMode()) {
            this.mAntiMisOperationTouch.setVisible(false);
        }
        if (!OpFeatures.isSupport(new int[]{63})) {
            this.mAntiMisOperationTouch.setSummary(C0017R$string.oneplus_pocket_mode_summary);
        }
        this.mTimerShutdownPreference = findPreference("timer_shutdown_startup_settings");
        if (checkIfNeedPasswordToPowerOn()) {
            this.mTimerShutdownPreference.setEnabled(false);
            this.mTimerShutdownPreference.setSummary(C0017R$string.oneplus_timer_shutdown_disable_summary);
        } else {
            this.mTimerShutdownPreference.setEnabled(true);
        }
        Preference findPreference4 = findPreference("oneplus_laboratory_settings");
        this.mOneplusLaboratorySettings = findPreference4;
        findPreference4.setOnPreferenceClickListener(this);
        this.mOneplusQuickReply = findPreference("oneplus_quick_replay");
        if (!OPUtils.isSupportQuickReply()) {
            preferenceScreen.removePreference(this.mOneplusQuickReply);
        }
        if (OPUtils.isGuestMode()) {
            preferenceScreen.removePreference(this.mTimerShutdownPreference);
            preferenceScreen.removePreference(this.mAntiMisOperationTouch);
            preferenceScreen.removePreference(this.mAppLocker);
            preferenceScreen.removePreference(this.mMultiAppPreference);
            preferenceScreen.removePreference(this.mOneplusLaboratorySettings);
            preferenceScreen.removePreference(this.mOneplusQuickReply);
            preferenceScreen.removePreference(this.mWorkLifeBalancePreference);
        }
        if (!OPUtils.isLaboratoryFeatureExist()) {
            preferenceScreen.removePreference(this.mOneplusLaboratorySettings);
        }
        Preference findPreference5 = findPreference("switch");
        this.mSwitchPreference = findPreference5;
        findPreference5.setOnPreferenceClickListener(this);
        if (!OPUtils.isAppExist(getActivity(), "com.oneplus.backuprestore")) {
            getPreferenceScreen().removePreference(this.mSwitchPreference);
        }
        if (OPUtils.isAppExist(getActivity(), "com.oneplus.gamespace") && (findPreference = findPreference("gaming_mode")) != null) {
            getPreferenceScreen().removePreference(findPreference);
        }
        this.mGameSpacePreference = findPreference("game_space");
        if (!OPUtils.isAppExist(getActivity(), "com.oneplus.opwlb") && (preference = this.mWorkLifeBalancePreference) != null) {
            preferenceScreen.removePreference(preference);
        }
        if (this.mWlbFeatureObserver == null) {
            this.mWlbFeatureObserver = new WLBFeatureObserver(new Handler());
            this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("worklife_feature_enable"), false, this.mWlbFeatureObserver);
        }
        updateWLBPreferenceSummary();
    }

    private void launchChooseOrConfirmLock(int i) {
        Intent intent = new Intent();
        if (!new ChooseLockSettingsHelper(getActivity(), this).launchConfirmationActivity(i, getString(C0017R$string.op_security_lock_settings_title), null, null, this.mChallenge, true)) {
            intent.setClassName(OPMemberController.PACKAGE_NAME, ChooseLockGeneric.class.getName());
            intent.putExtra("minimum_quality", 65536);
            intent.putExtra("hide_disabled_prefs", true);
            intent.putExtra("has_challenge", true);
            intent.putExtra("challenge", this.mChallenge);
            startActivityForResult(intent, R$styleable.Constraint_layout_goneMarginStart);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        if (this.mGotoAppLockerClick) {
            if ((i == 102 || i == 104) && (i2 == 1 || i2 == -1)) {
                gotoAppLockerPage();
            }
            this.mGotoAppLockerClick = false;
        }
    }

    public void gotoAppLockerPage() {
        Intent intent = null;
        try {
            Intent intent2 = new Intent();
            try {
                intent2.setClassName(OPMemberController.PACKAGE_NAME, "com.android.settings.Settings$OPAppLockerActivity");
                getActivity().startActivity(intent2);
            } catch (ActivityNotFoundException unused) {
                intent = intent2;
            }
        } catch (ActivityNotFoundException unused2) {
            Log.d("OPOthersSettings", "No activity found for " + intent);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        if ("oneplus_multi_app".equals(preference.getKey())) {
            try {
                Intent intent = new Intent();
                intent.setAction("oneplus.intent.action.ONEPLUS_MULTI_APP_LIST_ACTION");
                getPrefContext().startActivity(intent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
            return true;
        } else if ("switch".equals(preference.getKey())) {
            try {
                Intent intent2 = new Intent();
                intent2.setClassName("com.oneplus.backuprestore", "com.oneplus.backuprestore.activity.BootActivity");
                intent2.setFlags(268435456);
                getPrefContext().startActivity(intent2);
            } catch (ActivityNotFoundException e2) {
                e2.printStackTrace();
            }
            return true;
        } else if (!"oneplus_app_locker".equals(preference.getKey())) {
            return false;
        } else {
            Log.d("OPOthersSettings", "App -> Locker");
            this.mGotoAppLockerClick = true;
            launchChooseOrConfirmLock(R$styleable.Constraint_motionStagger);
            return true;
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (!preference.getKey().equals("anti_misoperation_of_the_screen_touch_enable")) {
            return false;
        }
        Settings.System.putInt(getContentResolver(), "oem_acc_anti_misoperation_screen", ((Boolean) obj).booleanValue() ? 1 : 0);
        return true;
    }

    public boolean checkIfNeedPasswordToPowerOn() {
        return Settings.Global.getInt(getActivity().getContentResolver(), "require_password_to_decrypt", 0) == 1;
    }

    /* access modifiers changed from: private */
    public static boolean isNeedShowGameSpace(Context context) {
        if (!OPUtils.isAppExist(context, "com.oneplus.gamespace")) {
            return false;
        }
        int intForUser = Settings.System.getIntForUser(context.getContentResolver(), "game_space_hide_icon", 0, -2);
        int componentEnabledSetting = context.getPackageManager().getComponentEnabledSetting(new ComponentName("com.oneplus.gamespace", "com.oneplus.gamespace.ui.main.MainActivity"));
        Log.d("OPOthersSettings", "isNeedShowGameSpace value:" + intForUser + " status:" + componentEnabledSetting);
        if (componentEnabledSetting == 2) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public static boolean isNeedShowWorkLife(Context context) {
        return OPUtils.isAppExist(context, "com.oneplus.opwlb");
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateWLBPreferenceSummary() {
        if (this.mWorkLifeBalancePreference != null) {
            int i = Settings.System.getInt(this.mContext.getContentResolver(), "worklife_feature_enable", 0);
            if (i == 0) {
                this.mWorkLifeBalancePreference.setSummary(C0017R$string.notification_for_work_life);
            } else if (i == 1) {
                this.mWorkLifeBalancePreference.setSummary(C0017R$string.notification_for_work_life_disabled);
            } else {
                Log.d("OPOthersSettings", "WLB feature has been removed");
            }
        }
    }

    public class WLBFeatureObserver extends ContentObserver {
        public WLBFeatureObserver(Handler handler) {
            super(handler);
        }

        public void onChange(boolean z) {
            OPToolsSettings.this.updateWLBPreferenceSummary();
        }
    }
}
