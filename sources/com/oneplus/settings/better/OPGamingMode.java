package com.oneplus.settings.better;

import android.app.AppOpsManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
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
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.MasterSwitchPreference;
import com.android.settingslib.utils.ThreadUtils;
import com.oneplus.settings.apploader.OPApplicationLoader;
import com.oneplus.settings.better.OPGamingMode;
import com.oneplus.settings.ui.OPTextViewButtonPreference;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OPGamingMode extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.oneplus.settings.better.OPGamingMode.AnonymousClass2 */

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return !OPUtils.isAppExist(context, "com.oneplus.gamespace");
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = C0019R$xml.op_gaming_mode;
            return Arrays.asList(searchIndexableResource);
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            if (!OPUtils.isSupportGameModePowerSaver()) {
                nonIndexableKeys.add("battery_saver");
            }
            if (!OPUtils.isSupportGameAdMode()) {
                nonIndexableKeys.add("op_game_mode_ad_enable");
            }
            return nonIndexableKeys;
        }
    };
    private SwitchPreference mAdEnable;
    private SwitchPreference mAnswerCallBySpeakerPreference;
    private List<OPAppModel> mAppList = new ArrayList();
    private AppOpsManager mAppOpsManager;
    private PreferenceCategory mAutoTurnOnAppList;
    private Preference mBatterySaverPreference;
    private SwitchPreference mBlockNotificationsPreference;
    private SwitchPreference mCloseAutomaticBrightness;
    private Context mContext;
    private PreferenceCategory mDoNotDisturbSettings;
    private Preference mGamingModeAddAppsPreference;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        /* class com.oneplus.settings.better.OPGamingMode.AnonymousClass1 */

        public void handleMessage(Message message) {
            super.handleMessage(message);
            OPGamingMode.this.mAutoTurnOnAppList.removeAll();
            OPGamingMode.this.mAppList.clear();
            OPGamingMode.this.mAppList.addAll(OPGamingMode.this.mOPApplicationLoader.getAppListByType(message.what));
            int size = OPGamingMode.this.mAppList.size();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < size; i++) {
                final OPAppModel oPAppModel = (OPAppModel) OPGamingMode.this.mAppList.get(i);
                final OPTextViewButtonPreference oPTextViewButtonPreference = new OPTextViewButtonPreference(OPGamingMode.this.mContext);
                oPTextViewButtonPreference.setIcon(oPAppModel.getAppIcon());
                oPTextViewButtonPreference.setTitle(oPAppModel.getLabel());
                oPTextViewButtonPreference.setButtonVisible(false);
                oPTextViewButtonPreference.setRightIconVisible(true);
                oPTextViewButtonPreference.setOnRightIconClickListener(new View.OnClickListener() {
                    /* class com.oneplus.settings.better.OPGamingMode.AnonymousClass1.AnonymousClass1 */

                    public void onClick(View view) {
                        oPTextViewButtonPreference.setButtonEnable(false);
                        OPGamingMode.this.mAutoTurnOnAppList.removePreference(oPTextViewButtonPreference);
                        OPGamingMode.this.mAppOpsManager.setMode(1004, oPAppModel.getUid(), oPAppModel.getPkgName(), 1);
                        StringBuilder sb = new StringBuilder(OPUtils.getGameModeAppListString(OPGamingMode.this.mContext));
                        if (!OPUtils.isInRemovedGameAppListString(OPGamingMode.this.mContext, oPAppModel)) {
                            sb.append(OPUtils.getGameModeAppString(oPAppModel));
                            OPUtils.saveGameModeRemovedAppLisStrings(OPGamingMode.this.mContext, sb.toString());
                            OPUtils.sendAppTrackerForGameModeRemovedApps();
                        }
                    }
                });
                OPGamingMode.this.mAutoTurnOnAppList.addPreference(oPTextViewButtonPreference);
                sb.append(oPAppModel.getPkgName() + ";");
            }
            Settings.System.putString(OPGamingMode.this.getContentResolver(), "game_mode_apps", sb.toString());
            OPUtils.sendAppTrackerForGameModeApps(sb.toString());
        }
    };
    private MasterSwitchPreference mHapticFeedbackPreference;
    private SwitchPreference mLockButtonsPreference;
    private SwitchPreference mNetworkAcceleration;
    private Preference mNotificationWaysPreference;
    private SwitchPreference mNotificationsCalls;
    private OPApplicationLoader mOPApplicationLoader;
    private PackageManager mPackageManager;
    private final SettingsObserver mSettingsObserver = new SettingsObserver();

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        SwitchPreference switchPreference;
        SwitchPreference switchPreference2;
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.op_gaming_mode);
        this.mContext = getActivity();
        this.mAppOpsManager = (AppOpsManager) getSystemService("appops");
        this.mPackageManager = getPackageManager();
        this.mOPApplicationLoader = new OPApplicationLoader(this.mContext, this.mAppOpsManager, this.mPackageManager);
        this.mAnswerCallBySpeakerPreference = (SwitchPreference) findPreference("do_not_disturb_answer_call_by_speaker");
        this.mBlockNotificationsPreference = (SwitchPreference) findPreference("block_notifications");
        this.mLockButtonsPreference = (SwitchPreference) findPreference("lock_buttons");
        SwitchPreference switchPreference3 = this.mAnswerCallBySpeakerPreference;
        if (switchPreference3 != null) {
            switchPreference3.setOnPreferenceChangeListener(this);
        }
        SwitchPreference switchPreference4 = this.mBlockNotificationsPreference;
        if (switchPreference4 != null) {
            switchPreference4.setOnPreferenceChangeListener(this);
        }
        SwitchPreference switchPreference5 = this.mLockButtonsPreference;
        if (switchPreference5 != null) {
            switchPreference5.setOnPreferenceChangeListener(this);
        }
        this.mAutoTurnOnAppList = (PreferenceCategory) findPreference("auto_turn_on_apps");
        Preference findPreference = findPreference("gaming_mode_add_apps");
        this.mGamingModeAddAppsPreference = findPreference;
        if (findPreference != null) {
            findPreference.setOnPreferenceClickListener(this);
        }
        this.mDoNotDisturbSettings = (PreferenceCategory) findPreference("do_not_disturb_settings");
        if (OPUtils.isSurportBackFingerprint(this.mContext) && (switchPreference2 = this.mLockButtonsPreference) != null) {
            this.mDoNotDisturbSettings.removePreference(switchPreference2);
        }
        this.mBatterySaverPreference = findPreference("battery_saver");
        if (!OPUtils.isSupportGameModePowerSaver()) {
            this.mBatterySaverPreference.setVisible(false);
        }
        if (this.mBatterySaverPreference != null) {
            updateBatterySaverData();
        }
        SwitchPreference switchPreference6 = (SwitchPreference) findPreference("close_automatic_brightness");
        this.mCloseAutomaticBrightness = switchPreference6;
        if (switchPreference6 != null) {
            switchPreference6.setOnPreferenceChangeListener(this);
        }
        SwitchPreference switchPreference7 = (SwitchPreference) findPreference("network_acceleration");
        this.mNetworkAcceleration = switchPreference7;
        if (switchPreference7 != null) {
            switchPreference7.setOnPreferenceChangeListener(this);
        }
        if (!OPUtils.isSupportGameModeNetBoost()) {
            this.mDoNotDisturbSettings.removePreference(this.mNetworkAcceleration);
        }
        this.mNotificationWaysPreference = findPreference("notification_ways");
        SwitchPreference switchPreference8 = (SwitchPreference) findPreference("notifications_3rd_calls");
        this.mNotificationsCalls = switchPreference8;
        if (switchPreference8 != null) {
            switchPreference8.setOnPreferenceChangeListener(this);
        }
        SwitchPreference switchPreference9 = (SwitchPreference) findPreference("op_game_mode_ad_enable");
        this.mAdEnable = switchPreference9;
        if (switchPreference9 != null) {
            switchPreference9.setOnPreferenceChangeListener(this);
        }
        if (!OPUtils.isSupportGameAdMode() && (switchPreference = this.mAdEnable) != null) {
            switchPreference.setVisible(false);
        }
        this.mHapticFeedbackPreference = (MasterSwitchPreference) findPreference("op_haptic_feedback");
        if (!OPUtils.isSupportXVibrate()) {
            this.mDoNotDisturbSettings.removePreference(this.mHapticFeedbackPreference);
            return;
        }
        this.mHapticFeedbackPreference.setOnPreferenceChangeListener(this);
        this.mHapticFeedbackPreference.setChecked(OPHapticFeedback.getHapticFeedbackState(this.mContext));
    }

    private void updateNotificationWaysSummary() {
        int intForUser = Settings.System.getIntForUser(getContentResolver(), "game_mode_block_notification", 0, -2);
        if (intForUser == 0) {
            this.mNotificationWaysPreference.setSummary(C0017R$string.oneplus_suspension_notice);
        } else if (2 == intForUser) {
            this.mNotificationWaysPreference.setSummary(C0017R$string.oneplus_weak_text_reminding);
        } else if (1 == intForUser) {
            this.mNotificationWaysPreference.setSummary(C0017R$string.oneplus_shielding_notification);
        }
    }

    private void updateBatterySaverData() {
        String stringForUser = Settings.System.getStringForUser(getContentResolver(), "game_mode_battery_saver", -2);
        if (!"0_0".equalsIgnoreCase(stringForUser) && !TextUtils.isEmpty(stringForUser) && !"56_0".equalsIgnoreCase(stringForUser)) {
            "56_30".equalsIgnoreCase(stringForUser);
        }
    }

    private void updateListData() {
        if (!this.mOPApplicationLoader.isLoading()) {
            this.mOPApplicationLoader.loadSelectedGameOrReadAppMap(1004);
            this.mOPApplicationLoader.initData(1, this.mHandler);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        boolean z = true;
        this.mSettingsObserver.register(true);
        updateListData();
        int intForUser = Settings.System.getIntForUser(getContentResolver(), "game_mode_answer_no_incallui", 0, -2);
        SwitchPreference switchPreference = this.mAnswerCallBySpeakerPreference;
        if (switchPreference != null) {
            switchPreference.setChecked(intForUser != 0);
        }
        int intForUser2 = Settings.System.getIntForUser(getContentResolver(), "game_mode_block_notification", 0, -2);
        this.mDoNotDisturbSettings = (PreferenceCategory) findPreference("do_not_disturb_settings");
        SwitchPreference switchPreference2 = this.mBlockNotificationsPreference;
        if (switchPreference2 != null) {
            switchPreference2.setChecked(intForUser2 != 0);
            this.mDoNotDisturbSettings.removePreference(this.mBlockNotificationsPreference);
        }
        int intForUser3 = Settings.System.getIntForUser(getContentResolver(), "game_mode_lock_buttons", 0, -2);
        SwitchPreference switchPreference3 = this.mLockButtonsPreference;
        if (switchPreference3 != null) {
            switchPreference3.setChecked(intForUser3 != 0);
        }
        int intForUser4 = Settings.System.getIntForUser(getContentResolver(), "game_mode_close_automatic_brightness", 0, -2);
        SwitchPreference switchPreference4 = this.mCloseAutomaticBrightness;
        if (switchPreference4 != null) {
            switchPreference4.setChecked(intForUser4 != 0);
        }
        if (this.mBatterySaverPreference != null) {
            updateBatterySaverData();
        }
        int intForUser5 = Settings.System.getIntForUser(getContentResolver(), "game_mode_network_acceleration", 0, -2);
        SwitchPreference switchPreference5 = this.mNetworkAcceleration;
        if (switchPreference5 != null) {
            switchPreference5.setChecked(intForUser5 != 0);
        }
        if (this.mNotificationWaysPreference != null) {
            updateNotificationWaysSummary();
        }
        int intForUser6 = Settings.System.getIntForUser(getContentResolver(), "game_mode_notifications_3rd_calls", 1, -2);
        SwitchPreference switchPreference6 = this.mNotificationsCalls;
        if (switchPreference6 != null) {
            switchPreference6.setChecked(intForUser6 != 0);
        }
        disableOptionsInEsportsMode();
        int intForUser7 = Settings.System.getIntForUser(getContentResolver(), "op_game_mode_ad_enable", 0, -2);
        SwitchPreference switchPreference7 = this.mAdEnable;
        if (switchPreference7 != null) {
            if (intForUser7 == 0) {
                z = false;
            }
            switchPreference7.setChecked(z);
        }
        if (OPUtils.isSupportXVibrate() && this.mHapticFeedbackPreference != null) {
            this.mHapticFeedbackPreference.setChecked(OPHapticFeedback.getHapticFeedbackState(this.mContext));
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        this.mSettingsObserver.register(false);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void disableOptionsInEsportsMode() {
        boolean z = !isEsportsMode();
        SwitchPreference switchPreference = this.mAnswerCallBySpeakerPreference;
        if (switchPreference != null) {
            switchPreference.setEnabled(z);
        }
        Preference preference = this.mNotificationWaysPreference;
        if (preference != null) {
            preference.setEnabled(z);
        }
        SwitchPreference switchPreference2 = this.mNotificationsCalls;
        if (switchPreference2 != null) {
            switchPreference2.setEnabled(z);
        }
    }

    private boolean isEsportsMode() {
        return "1".equals(Settings.System.getStringForUser(getContentResolver(), "esport_mode_enabled", -2));
    }

    /* access modifiers changed from: private */
    public final class SettingsObserver extends ContentObserver {
        private Uri esportsmodeUri = Settings.System.getUriFor("esport_mode_enabled");

        public SettingsObserver() {
            super(OPGamingMode.this.mHandler);
        }

        public void register(boolean z) {
            ContentResolver contentResolver = OPGamingMode.this.getContentResolver();
            if (z) {
                contentResolver.registerContentObserver(this.esportsmodeUri, false, this);
            } else {
                contentResolver.unregisterContentObserver(this);
            }
        }

        public void onChange(boolean z, Uri uri) {
            super.onChange(z, uri);
            if (this.esportsmodeUri.equals(uri)) {
                ThreadUtils.postOnMainThread(new Runnable() {
                    /* class com.oneplus.settings.better.$$Lambda$OPGamingMode$SettingsObserver$sNzw_XCuNF8n2q9Km_hXz4FJIOA */

                    public final void run() {
                        OPGamingMode.SettingsObserver.this.lambda$onChange$0$OPGamingMode$SettingsObserver();
                    }
                });
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onChange$0 */
        public /* synthetic */ void lambda$onChange$0$OPGamingMode$SettingsObserver() {
            OPGamingMode.this.disableOptionsInEsportsMode();
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        String key = preference.getKey();
        if ("block_notifications".equals(key)) {
            Log.d("OPGamingMode", "KEY_BLOCK_NOTIFICATIONS");
            Settings.System.putIntForUser(getContentResolver(), "game_mode_block_notification", ((Boolean) obj).booleanValue() ? 1 : 0, -2);
            return true;
        } else if ("lock_buttons".equals(key)) {
            Log.d("OPGamingMode", "KEY_LOCK_BUTTONS");
            Settings.System.putIntForUser(getContentResolver(), "game_mode_lock_buttons", ((Boolean) obj).booleanValue() ? 1 : 0, -2);
            return true;
        } else if ("do_not_disturb_answer_call_by_speaker".equals(key)) {
            Log.d("OPGamingMode", "KEY_LOCK_BUTTONS");
            Settings.System.putIntForUser(getContentResolver(), "game_mode_answer_no_incallui", ((Boolean) obj).booleanValue() ? 1 : 0, -2);
            OPUtils.sendAppTrackerForGameModeSpeakerAnswer();
            return true;
        } else if ("close_automatic_brightness".equals(key)) {
            Settings.System.putIntForUser(getContentResolver(), "game_mode_close_automatic_brightness", ((Boolean) obj).booleanValue() ? 1 : 0, -2);
            OPUtils.sendAppTrackerForGameModeBrightness();
            return true;
        } else if ("network_acceleration".equals(key)) {
            Settings.System.putIntForUser(getContentResolver(), "game_mode_network_acceleration", ((Boolean) obj).booleanValue() ? 1 : 0, -2);
            OPUtils.sendAppTrackerForGameModeNetWorkBoost();
            return true;
        } else if ("notifications_3rd_calls".equals(key)) {
            Settings.System.putIntForUser(getContentResolver(), "game_mode_notifications_3rd_calls", ((Boolean) obj).booleanValue() ? 1 : 0, -2);
            OPUtils.sendAppTrackerForGameMode3drPartyCalls();
            return true;
        } else if ("op_game_mode_ad_enable".equals(key)) {
            Settings.System.putIntForUser(getContentResolver(), "op_game_mode_ad_enable", ((Boolean) obj).booleanValue() ? 1 : 0, -2);
            OPUtils.sendAppTrackerForGameModeAdEnable();
            return true;
        } else if (!"op_haptic_feedback".equals(key)) {
            return true;
        } else {
            OPHapticFeedback.setHapticFeedbackState(this.mContext, ((Boolean) obj).booleanValue());
            return true;
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        if (!preference.getKey().equals("gaming_mode_add_apps")) {
            return false;
        }
        Log.d("OPGamingMode", "KEY_GAMING_MODE_ADD_APPS");
        Intent intent = new Intent("oneplus.intent.action.ONEPLUS_GAME_READ_APP_LIST_ACTION");
        intent.setFlags(268435456);
        intent.putExtra("op_load_app_tyep", 1004);
        this.mContext.startActivity(intent);
        return true;
    }
}
