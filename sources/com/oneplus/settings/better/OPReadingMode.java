package com.oneplus.settings.better;

import android.app.AppOpsManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.SwitchPreference;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.Settings;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.oneplus.settings.apploader.OPApplicationLoader;
import com.oneplus.settings.ui.OPTextViewButtonPreference;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OPReadingMode extends DashboardFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.oneplus.settings.better.OPReadingMode.AnonymousClass2 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = C0019R$xml.op_reading_mode;
            return Arrays.asList(searchIndexableResource);
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            return new ArrayList();
        }
    };
    private static ReadingModeEffectManager mReadingModeEffectManager;
    private List<OPAppModel> mAppList = new ArrayList();
    private AppOpsManager mAppOpsManager;
    private PreferenceCategory mAutoTurnOnAppList;
    private SwitchPreference mBlockPeekNotificationsPreference;
    private Context mContext;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        /* class com.oneplus.settings.better.OPReadingMode.AnonymousClass1 */

        public void handleMessage(Message message) {
            super.handleMessage(message);
            OPReadingMode.this.mAutoTurnOnAppList.removeAll();
            OPReadingMode.this.mAppList.clear();
            OPReadingMode.this.mAppList.addAll(OPReadingMode.this.mOPApplicationLoader.getAppListByType(message.what));
            int size = OPReadingMode.this.mAppList.size();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < size; i++) {
                final OPAppModel oPAppModel = (OPAppModel) OPReadingMode.this.mAppList.get(i);
                final OPTextViewButtonPreference oPTextViewButtonPreference = new OPTextViewButtonPreference(OPReadingMode.this.mContext);
                oPTextViewButtonPreference.setIcon(oPAppModel.getAppIcon());
                oPTextViewButtonPreference.setTitle(oPAppModel.getLabel());
                if (OPReadingMode.mReadingModeEffectManager.getAppEffectSelectValue(oPAppModel.getUid() + oPAppModel.getPkgName()) == 0) {
                    oPTextViewButtonPreference.setSummary(OPReadingMode.this.mContext.getString(C0017R$string.oneplus_reading_mode_mono));
                } else {
                    oPTextViewButtonPreference.setSummary(OPReadingMode.this.mContext.getString(C0017R$string.oneplus_reading_mode_chromatic));
                }
                oPTextViewButtonPreference.setSummaryVisible(true);
                oPTextViewButtonPreference.setButtonVisible(false);
                oPTextViewButtonPreference.setRightIconVisible(true);
                oPTextViewButtonPreference.setOnRightIconClickListener(new View.OnClickListener() {
                    /* class com.oneplus.settings.better.OPReadingMode.AnonymousClass1.AnonymousClass1 */

                    public void onClick(View view) {
                        oPTextViewButtonPreference.setButtonEnable(false);
                        OPReadingMode.this.mAutoTurnOnAppList.removePreference(oPTextViewButtonPreference);
                        OPReadingMode.this.mAppOpsManager.setMode(1003, oPAppModel.getUid(), oPAppModel.getPkgName(), 3);
                        OPReadingMode.this.isShowAutoTurnOnAppList();
                    }
                });
                OPReadingMode.this.mAutoTurnOnAppList.addPreference(oPTextViewButtonPreference);
                sb.append(oPAppModel.getPkgName() + ";");
            }
            OPReadingMode.this.isShowAutoTurnOnAppList();
            Settings.System.putString(OPReadingMode.this.getContentResolver(), "read_mode_apps", sb.toString());
            OPUtils.sendAppTrackerForReadingModeApps(sb.toString());
        }
    };
    private OPApplicationLoader mOPApplicationLoader;
    private PackageManager mPackageManager;
    private Preference mReadingModeAddAppsPreference;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "OPReadingMode";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void isShowAutoTurnOnAppList() {
        if (this.mAutoTurnOnAppList.getPreferenceCount() <= 0) {
            this.mAutoTurnOnAppList.setVisible(false);
        } else {
            this.mAutoTurnOnAppList.setVisible(true);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = getActivity();
        this.mAppOpsManager = (AppOpsManager) getSystemService("appops");
        this.mPackageManager = getPackageManager();
        this.mOPApplicationLoader = new OPApplicationLoader(this.mContext, this.mAppOpsManager, this.mPackageManager);
        this.mAutoTurnOnAppList = (PreferenceCategory) findPreference("auto_turn_on_apps");
        Preference findPreference = findPreference("reading_mode_add_apps");
        this.mReadingModeAddAppsPreference = findPreference;
        if (findPreference != null) {
            findPreference.setOnPreferenceClickListener(this);
        }
        SwitchPreference switchPreference = (SwitchPreference) findPreference("block_peek_notifications");
        this.mBlockPeekNotificationsPreference = switchPreference;
        if (switchPreference != null) {
            switchPreference.setOnPreferenceChangeListener(this);
        }
    }

    private void updateListData() {
        if (!this.mOPApplicationLoader.isLoading()) {
            this.mOPApplicationLoader.loadSelectedGameOrReadAppMap(1003);
            this.mOPApplicationLoader.initData(1, this.mHandler);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        ReadingModeEffectManager instance = ReadingModeEffectManager.getInstance(this.mContext);
        mReadingModeEffectManager = instance;
        instance.loadAppMap();
        updateListData();
        boolean z = false;
        int intForUser = Settings.System.getIntForUser(getContentResolver(), "reading_mode_block_notification", 0, -2);
        SwitchPreference switchPreference = this.mBlockPeekNotificationsPreference;
        if (switchPreference != null) {
            if (intForUser != 0) {
                z = true;
            }
            switchPreference.setChecked(z);
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        OPReadingModeTurnOnPreferenceController oPReadingModeTurnOnPreferenceController = new OPReadingModeTurnOnPreferenceController(context, getSettingsLifecycle());
        getSettingsLifecycle().addObserver(oPReadingModeTurnOnPreferenceController);
        arrayList.add(oPReadingModeTurnOnPreferenceController);
        return arrayList;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (!"block_peek_notifications".equals(preference.getKey())) {
            return true;
        }
        Settings.System.putIntForUser(getContentResolver(), "reading_mode_block_notification", ((Boolean) obj).booleanValue() ? 1 : 0, -2);
        OPUtils.sendAppTrackerForReadingModeNotification();
        return true;
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        if (!preference.getKey().equals("reading_mode_add_apps")) {
            return false;
        }
        Intent intent = null;
        try {
            Intent intent2 = new Intent("com.android.settings.action.READINGMODE_EFFECT_SELECT");
            try {
                intent2.putExtra("classname", Settings.ReadingModeAppListActivity.class.getName());
                this.mContext.startActivity(intent2);
                return true;
            } catch (ActivityNotFoundException unused) {
                intent = intent2;
            }
        } catch (ActivityNotFoundException unused2) {
            Log.d("OPReadingMode", "No activity found for " + intent);
            return true;
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.op_reading_mode;
    }
}
