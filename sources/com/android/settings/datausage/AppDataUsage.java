package com.android.settings.datausage;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.NetworkTemplate;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserHandle;
import android.telephony.SubscriptionManager;
import android.util.ArraySet;
import android.util.IconDrawableFactory;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.SwitchPreference;
import com.android.settings.C0003R$array;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.Utils;
import com.android.settings.datausage.DataSaverBackend;
import com.android.settings.datausage.backgrounddata.utils.BackgroundDataUtils;
import com.android.settingslib.AppItem;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.RestrictedSwitchPreference;
import com.android.settingslib.net.NetworkCycleDataForUid;
import com.android.settingslib.net.NetworkCycleDataForUidLoader;
import com.android.settingslib.net.UidDetail;
import com.android.settingslib.net.UidDetailProvider;
import com.oneplus.settings.ui.OPProgressDialog;
import com.oneplus.settings.utils.OPFirewallRule;
import com.oneplus.settings.utils.OPFirewallUtils;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AppDataUsage extends DataUsageBaseFragment implements Preference.OnPreferenceChangeListener, DataSaverBackend.Listener {
    public static UidDetail OSUidDetail;
    private String[] items = new String[0];
    private AppItem mAppItem;
    private PreferenceCategory mAppList;
    private final LoaderManager.LoaderCallbacks<ArraySet<Preference>> mAppPrefCallbacks = new LoaderManager.LoaderCallbacks<ArraySet<Preference>>() {
        /* class com.android.settings.datausage.AppDataUsage.AnonymousClass4 */

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoaderReset(Loader<ArraySet<Preference>> loader) {
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public Loader<ArraySet<Preference>> onCreateLoader(int i, Bundle bundle) {
            return new AppPrefLoader(AppDataUsage.this.getPrefContext(), AppDataUsage.this.mPackages, AppDataUsage.this.getPackageManager());
        }

        public void onLoadFinished(Loader<ArraySet<Preference>> loader, ArraySet<Preference> arraySet) {
            if (arraySet != null && AppDataUsage.this.mAppList != null) {
                Iterator<Preference> it = arraySet.iterator();
                while (it.hasNext()) {
                    AppDataUsage.this.mAppList.addPreference(it.next());
                }
            }
        }
    };
    private Preference mAppSettings;
    private Intent mAppSettingsIntent;
    private Preference mBackgroundUsage;
    private Context mContext;
    private SpinnerPreference mCycle;
    private CycleAdapter mCycleAdapter;
    private AdapterView.OnItemSelectedListener mCycleListener = new AdapterView.OnItemSelectedListener() {
        /* class com.android.settings.datausage.AppDataUsage.AnonymousClass2 */

        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onNothingSelected(AdapterView<?> adapterView) {
        }

        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
            AppDataUsage.this.bindData(i);
        }
    };
    private Preference mCycleText;
    private ArrayList<Long> mCycles;
    private DataSaverBackend mDataSaverBackend;
    private SwitchPreference mDisabledData;
    private SwitchPreference mDisabledWifi;
    private long mEnd;
    private Preference mForegroundUsage;
    private Drawable mIcon;
    CharSequence mLabel;
    private PackageManager mPackageManager;
    String mPackageName;
    private final ArraySet<String> mPackages = new ArraySet<>();
    private RestrictedSwitchPreference mRestrictBackground;
    private RestrictedPreference mRestrictBackgroundUss;
    private long mSelectedCycle;
    private long mStart;
    private int mSubId = 0;
    NetworkTemplate mTemplate;
    private Preference mTotalUsage;
    final LoaderManager.LoaderCallbacks<List<NetworkCycleDataForUid>> mUidDataCallbacks = new LoaderManager.LoaderCallbacks<List<NetworkCycleDataForUid>>() {
        /* class com.android.settings.datausage.AppDataUsage.AnonymousClass3 */

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoaderReset(Loader<List<NetworkCycleDataForUid>> loader) {
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public Loader<List<NetworkCycleDataForUid>> onCreateLoader(int i, Bundle bundle) {
            NetworkCycleDataForUidLoader.Builder<?> builder = NetworkCycleDataForUidLoader.builder(AppDataUsage.this.mContext);
            builder.setRetrieveDetail(true);
            builder.setNetworkTemplate(AppDataUsage.this.mTemplate);
            if (AppDataUsage.this.mAppItem.category == 0) {
                for (int i2 = 0; i2 < AppDataUsage.this.mAppItem.uids.size(); i2++) {
                    builder.addUid(AppDataUsage.this.mAppItem.uids.keyAt(i2));
                }
            } else {
                builder.addUid(AppDataUsage.this.mAppItem.key);
            }
            if (AppDataUsage.this.mCycles != null) {
                builder.setCycles(AppDataUsage.this.mCycles);
            }
            return builder.build();
        }

        public void onLoadFinished(Loader<List<NetworkCycleDataForUid>> loader, List<NetworkCycleDataForUid> list) {
            AppDataUsage.this.mUsageData = list;
            AppDataUsage.this.mCycleAdapter.updateCycleList(list);
            int i = 0;
            if (AppDataUsage.this.mSelectedCycle > 0) {
                int size = list.size();
                int i2 = 0;
                while (true) {
                    if (i2 >= size) {
                        break;
                    } else if (list.get(i2).getEndTime() == AppDataUsage.this.mSelectedCycle) {
                        i = i2;
                        break;
                    } else {
                        i2++;
                    }
                }
                if (i > 0) {
                    AppDataUsage.this.mCycle.setSelection(i);
                }
                AppDataUsage.this.bindData(i);
                return;
            }
            AppDataUsage.this.bindData(0);
        }
    };
    private RestrictedSwitchPreference mUnrestrictedData;
    private List<NetworkCycleDataForUid> mUsageData;
    private int restrictBackgroundChooseIndex = 0;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "AppDataUsage";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 343;
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onDataSaverChanged(boolean z) {
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.datausage.DataUsageBaseFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        int i;
        super.onCreate(bundle);
        this.mContext = getContext();
        this.mPackageManager = getPackageManager();
        Bundle arguments = getArguments();
        this.mAppItem = arguments != null ? (AppItem) arguments.getParcelable("app_item") : null;
        this.mTemplate = arguments != null ? (NetworkTemplate) arguments.getParcelable("network_template") : null;
        this.mCycles = arguments != null ? (ArrayList) arguments.getSerializable("network_cycles") : null;
        this.mSelectedCycle = arguments != null ? arguments.getLong("selected_cycle") : 0;
        if (this.mTemplate == null) {
            this.mTemplate = DataUsageUtils.getDefaultTemplate(this.mContext, SubscriptionManager.getDefaultDataSubscriptionId());
        }
        int i2 = -1;
        boolean z = false;
        if (this.mAppItem == null) {
            if (arguments != null) {
                i = arguments.getInt("uid", -1);
            } else {
                i = getActivity().getIntent().getIntExtra("uid", -1);
            }
            if (i == -1) {
                getActivity().finish();
            } else {
                addUid(i);
                AppItem appItem = new AppItem(i);
                this.mAppItem = appItem;
                appItem.addUid(i);
            }
        } else {
            for (int i3 = 0; i3 < this.mAppItem.uids.size(); i3++) {
                addUid(this.mAppItem.uids.keyAt(i3));
            }
            if (OPUtils.isSupportUss() && this.mAppItem.uids.size() == 0) {
                if (arguments != null ? arguments.getBoolean("restricted", false) : false) {
                    addUid(this.mAppItem.key);
                }
            }
        }
        this.mTotalUsage = findPreference("total_usage");
        this.mForegroundUsage = findPreference("foreground_usage");
        this.mBackgroundUsage = findPreference("background_usage");
        this.mSubId = arguments != null ? arguments.getInt("arg_subid") : 0;
        long[] dataUsageSectionTimeMillByAccountDay = OPDataUsageUtils.getDataUsageSectionTimeMillByAccountDay(getPrefContext(), this.mSubId);
        this.mStart = dataUsageSectionTimeMillByAccountDay[0];
        this.mEnd = dataUsageSectionTimeMillByAccountDay[1];
        String formatDateRange = Utils.formatDateRange(getPrefContext(), this.mStart, this.mEnd);
        Preference findPreference = findPreference("pf_cycle");
        this.mCycleText = findPreference;
        findPreference.setTitle(formatDateRange);
        this.mDisabledData = (SwitchPreference) findPreference("disabled_mobile");
        this.mDisabledWifi = (SwitchPreference) findPreference("disabled_wifi");
        AppItem appItem2 = this.mAppItem;
        if (appItem2 != null) {
            i2 = UserHandle.getUserId(appItem2.key);
        }
        if (OPUtils.isGuestMode() || !UserHandle.isApp(this.mAppItem.key) || i2 == 999) {
            this.mDisabledData.setVisible(false);
            this.mDisabledWifi.setVisible(false);
        } else {
            this.mDisabledData.setOnPreferenceChangeListener(this);
            this.mDisabledWifi.setOnPreferenceChangeListener(this);
            updateFireWallState();
        }
        SpinnerPreference spinnerPreference = (SpinnerPreference) findPreference("cycle");
        this.mCycle = spinnerPreference;
        spinnerPreference.setVisible(false);
        this.mCycleAdapter = new CycleAdapter(this.mContext, this.mCycle, this.mCycleListener);
        UidDetailProvider uidDetailProvider = getUidDetailProvider();
        int i4 = this.mAppItem.key;
        if (i4 > 0) {
            if (!UserHandle.isApp(i4)) {
                UidDetail uidDetail = uidDetailProvider.getUidDetail(this.mAppItem.key, true);
                this.mIcon = uidDetail.icon;
                this.mLabel = uidDetail.label;
                removePreference("unrestricted_data_saver");
                removePreference("restrict_background");
                removePreference("restrict_background_uss");
            } else {
                if (this.mPackages.size() != 0) {
                    try {
                        ApplicationInfo applicationInfoAsUser = this.mPackageManager.getApplicationInfoAsUser(this.mPackages.valueAt(0), 0, UserHandle.getUserId(this.mAppItem.key));
                        this.mIcon = IconDrawableFactory.newInstance(getActivity()).getBadgedIcon(applicationInfoAsUser);
                        this.mLabel = applicationInfoAsUser.loadLabel(this.mPackageManager);
                        this.mPackageName = applicationInfoAsUser.packageName;
                    } catch (PackageManager.NameNotFoundException unused) {
                    }
                }
                if (!OPUtils.isSupportUss()) {
                    removePreference("restrict_background_uss");
                    RestrictedSwitchPreference restrictedSwitchPreference = (RestrictedSwitchPreference) findPreference("restrict_background");
                    this.mRestrictBackground = restrictedSwitchPreference;
                    restrictedSwitchPreference.setOnPreferenceChangeListener(this);
                    RestrictedSwitchPreference restrictedSwitchPreference2 = (RestrictedSwitchPreference) findPreference("unrestricted_data_saver");
                    this.mUnrestrictedData = restrictedSwitchPreference2;
                    restrictedSwitchPreference2.setOnPreferenceChangeListener(this);
                } else {
                    this.items = getContext().getResources().getStringArray(C0003R$array.restrict_background_values);
                    removePreference("restrict_background");
                    this.mRestrictBackgroundUss = (RestrictedPreference) findPreference("restrict_background_uss");
                    RestrictedSwitchPreference restrictedSwitchPreference3 = (RestrictedSwitchPreference) findPreference("unrestricted_data_saver");
                    this.mUnrestrictedData = restrictedSwitchPreference3;
                    restrictedSwitchPreference3.setOnPreferenceChangeListener(this);
                    int appType = BackgroundDataUtils.getAppType(getContext(), this.mPackageName, this.mAppItem.key);
                    this.restrictBackgroundChooseIndex = appType;
                    String[] strArr = this.items;
                    if (strArr.length > appType) {
                        this.mRestrictBackgroundUss.setSummary(strArr[appType]);
                    }
                    this.mRestrictBackgroundUss.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        /* class com.android.settings.datausage.AppDataUsage.AnonymousClass1 */

                        @Override // androidx.preference.Preference.OnPreferenceClickListener
                        public boolean onPreferenceClick(Preference preference) {
                            AppDataUsage.this.showRestrictBackgroundDialog();
                            return false;
                        }
                    });
                }
            }
            this.mDataSaverBackend = new DataSaverBackend(this.mContext);
            this.mAppSettings = findPreference("app_settings");
            Intent intent = new Intent("android.intent.action.MANAGE_NETWORK_USAGE");
            this.mAppSettingsIntent = intent;
            intent.addCategory("android.intent.category.DEFAULT");
            PackageManager packageManager = getPackageManager();
            Iterator<String> it = this.mPackages.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                this.mAppSettingsIntent.setPackage(it.next());
                if (packageManager.resolveActivity(this.mAppSettingsIntent, 0) != null) {
                    z = true;
                    break;
                }
            }
            if (!z) {
                removePreference("app_settings");
                this.mAppSettings = null;
            }
            if (this.mPackages.size() > 1) {
                this.mAppList = (PreferenceCategory) findPreference("app_list");
                LoaderManager.getInstance(this).restartLoader(3, Bundle.EMPTY, this.mAppPrefCallbacks);
                return;
            }
            removePreference("app_list");
            return;
        }
        FragmentActivity activity = getActivity();
        UidDetail uidDetail2 = uidDetailProvider.getUidDetail(this.mAppItem.key, true);
        this.mIcon = uidDetail2.icon;
        this.mLabel = uidDetail2.label;
        this.mPackageName = activity.getPackageName();
        removePreference("unrestricted_data_saver");
        removePreference("app_settings");
        removePreference("restrict_background");
        removePreference("app_list");
        removePreference("restrict_background_uss");
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.datausage.DataUsageBaseFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        DataSaverBackend dataSaverBackend = this.mDataSaverBackend;
        if (dataSaverBackend != null) {
            dataSaverBackend.addListener(this);
        }
        LoaderManager.getInstance(this).restartLoader(2, null, this.mUidDataCallbacks);
        updatePrefs();
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        DataSaverBackend dataSaverBackend = this.mDataSaverBackend;
        if (dataSaverBackend != null) {
            dataSaverBackend.remListener(this);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference == this.mRestrictBackground) {
            this.mDataSaverBackend.setIsBlacklisted(this.mAppItem.key, this.mPackageName, !((Boolean) obj).booleanValue());
            updatePrefs();
            return true;
        } else if (preference == this.mUnrestrictedData) {
            this.mDataSaverBackend.setIsWhitelisted(this.mAppItem.key, this.mPackageName, ((Boolean) obj).booleanValue());
            return true;
        } else {
            if (preference == this.mDisabledWifi) {
                new UpdateRuleTask(getPrefContext(), this.mAppItem.key, ((Boolean) obj).booleanValue(), 1).execute(new Void[0]);
            } else if (preference == this.mDisabledData) {
                new UpdateRuleTask(getPrefContext(), this.mAppItem.key, ((Boolean) obj).booleanValue(), 0).execute(new Void[0]);
            }
            return false;
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference != this.mAppSettings) {
            return super.onPreferenceTreeClick(preference);
        }
        getActivity().startActivityAsUser(this.mAppSettingsIntent, new UserHandle(UserHandle.getUserId(this.mAppItem.key)));
        return true;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.app_data_usage;
    }

    /* access modifiers changed from: package-private */
    public void updatePrefs() {
        updatePrefs(getAppRestrictBackground(), getUnrestrictData());
        if (OPUtils.isSupportUss()) {
            updateUssPreference();
        }
    }

    /* access modifiers changed from: package-private */
    public UidDetailProvider getUidDetailProvider() {
        return new UidDetailProvider(this.mContext);
    }

    private void updatePrefs(boolean z, boolean z2) {
        RestrictedLockUtils.EnforcedAdmin checkIfMeteredDataRestricted = RestrictedLockUtilsInternal.checkIfMeteredDataRestricted(this.mContext, this.mPackageName, UserHandle.getUserId(this.mAppItem.key));
        RestrictedSwitchPreference restrictedSwitchPreference = this.mRestrictBackground;
        if (restrictedSwitchPreference != null) {
            restrictedSwitchPreference.setChecked(!z);
            this.mRestrictBackground.setDisabledByAdmin(checkIfMeteredDataRestricted);
        }
        RestrictedSwitchPreference restrictedSwitchPreference2 = this.mUnrestrictedData;
        if (restrictedSwitchPreference2 == null) {
            return;
        }
        if (z) {
            restrictedSwitchPreference2.setVisible(false);
            return;
        }
        restrictedSwitchPreference2.setVisible(true);
        this.mUnrestrictedData.setChecked(z2);
        this.mUnrestrictedData.setDisabledByAdmin(checkIfMeteredDataRestricted);
    }

    private void addUid(int i) {
        String[] packagesForUid = this.mPackageManager.getPackagesForUid(i);
        if (packagesForUid != null) {
            for (String str : packagesForUid) {
                this.mPackages.add(str);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void bindData(int i) {
        long j;
        long j2;
        List<NetworkCycleDataForUid> list = this.mUsageData;
        if (list == null || i >= list.size()) {
            j2 = 0;
            j = 0;
        } else {
            NetworkCycleDataForUid networkCycleDataForUid = this.mUsageData.get(i);
            j2 = networkCycleDataForUid.getBackgroudUsage();
            j = networkCycleDataForUid.getForegroudUsage();
        }
        this.mTotalUsage.setSummary(DataUsageUtils.formatDataUsage(this.mContext, j2 + j));
        this.mForegroundUsage.setSummary(DataUsageUtils.formatDataUsage(this.mContext, j));
        this.mBackgroundUsage.setSummary(DataUsageUtils.formatDataUsage(this.mContext, j2));
    }

    private boolean getAppRestrictBackground() {
        return (this.services.mPolicyManager.getUidPolicy(this.mAppItem.key) & 1) != 0;
    }

    private boolean getUnrestrictData() {
        DataSaverBackend dataSaverBackend = this.mDataSaverBackend;
        if (dataSaverBackend != null) {
            return dataSaverBackend.isWhitelisted(this.mAppItem.key);
        }
        return false;
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x0045  */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0047  */
    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onViewCreated(android.view.View r7, android.os.Bundle r8) {
        /*
        // Method dump skipped, instructions count: 139
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.datausage.AppDataUsage.onViewCreated(android.view.View, android.os.Bundle):void");
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onWhitelistStatusChanged(int i, boolean z) {
        if (this.mAppItem.uids.get(i, false)) {
            updatePrefs(getAppRestrictBackground(), z);
        }
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onBlacklistStatusChanged(int i, boolean z) {
        if (this.mAppItem.uids.get(i, false)) {
            updatePrefs(z, getUnrestrictData());
        }
    }

    private void updateFireWallState() {
        List<ApplicationInfo> applicationInfoByUid = OPDataUsageUtils.getApplicationInfoByUid(getPrefContext(), this.mAppItem.key);
        if (applicationInfoByUid != null && !applicationInfoByUid.isEmpty()) {
            boolean z = false;
            ApplicationInfo applicationInfo = applicationInfoByUid.get(0);
            if (applicationInfo != null) {
                OPFirewallRule selectFirewallRuleByPkg = OPFirewallUtils.selectFirewallRuleByPkg(getContext(), applicationInfo.packageName);
                if (selectFirewallRuleByPkg == null || selectFirewallRuleByPkg.getMobile() == null) {
                    this.mDisabledData.setChecked(false);
                } else {
                    this.mDisabledData.setChecked(selectFirewallRuleByPkg.getMobile().intValue() != 0);
                }
                if (selectFirewallRuleByPkg == null || selectFirewallRuleByPkg.getWlan() == null) {
                    this.mDisabledWifi.setChecked(false);
                    return;
                }
                SwitchPreference switchPreference = this.mDisabledWifi;
                if (selectFirewallRuleByPkg.getWlan().intValue() != 0) {
                    z = true;
                }
                switchPreference.setChecked(z);
            }
        }
    }

    class UpdateRuleTask extends AsyncTask<Void, Integer, Integer> {
        private Context mContext;
        OPProgressDialog progressDialog;
        private boolean state;
        private int type;
        private int uid;

        public UpdateRuleTask(Context context, int i, boolean z, int i2) {
            this.mContext = context;
            this.uid = i;
            this.state = z;
            this.type = i2;
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            super.onPreExecute();
            OPProgressDialog oPProgressDialog = new OPProgressDialog(this.mContext);
            this.progressDialog = oPProgressDialog;
            oPProgressDialog.setMessage(this.mContext.getString(C0017R$string.settings_safetylegal_activity_loading));
            this.progressDialog.setTimeOut(5000, new OPProgressDialog.OnTimeOutListener(this) {
                /* class com.android.settings.datausage.AppDataUsage.UpdateRuleTask.AnonymousClass1 */

                @Override // com.oneplus.settings.ui.OPProgressDialog.OnTimeOutListener
                public void onTimeOut(OPProgressDialog oPProgressDialog) {
                    Log.d("UpdateRuleTask", "UpdateRuleTask onTimeOut");
                }
            });
            this.progressDialog.showDelay(1000);
        }

        /* access modifiers changed from: protected */
        public Integer doInBackground(Void... voidArr) {
            List<ApplicationInfo> applicationInfoByUid = OPDataUsageUtils.getApplicationInfoByUid(this.mContext, this.uid);
            for (ApplicationInfo applicationInfo : applicationInfoByUid) {
                if (applicationInfo != null) {
                    if (this.type == 0) {
                        OPFirewallUtils.addOrUpdateRole(AppDataUsage.this.getContext(), new OPFirewallRule(applicationInfo.packageName, null, Integer.valueOf(this.state ? 1 : 0)));
                    } else {
                        OPFirewallUtils.addOrUpdateRole(AppDataUsage.this.getContext(), new OPFirewallRule(applicationInfo.packageName, Integer.valueOf(this.state ? 1 : 0), null));
                    }
                }
            }
            return Integer.valueOf(applicationInfoByUid.size());
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Integer num) {
            super.onPostExecute((Object) num);
            if (this.type == 0) {
                AppDataUsage.this.mDisabledData.setChecked(this.state);
            } else {
                AppDataUsage.this.mDisabledWifi.setChecked(this.state);
            }
            OPProgressDialog oPProgressDialog = this.progressDialog;
            if (oPProgressDialog != null) {
                oPProgressDialog.dismiss();
            }
        }
    }

    public void showRestrictBackgroundDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(C0017R$string.restrict_background_uss_title);
        builder.setSingleChoiceItems(this.items, this.restrictBackgroundChooseIndex, new DialogInterface.OnClickListener() {
            /* class com.android.settings.datausage.AppDataUsage.AnonymousClass5 */

            public void onClick(DialogInterface dialogInterface, int i) {
                if (AppDataUsage.this.restrictBackgroundChooseIndex != i) {
                    AppDataUsage.this.restrictBackgroundChooseIndex = i;
                    if (AppDataUsage.this.mRestrictBackgroundUss != null && AppDataUsage.this.items.length > AppDataUsage.this.restrictBackgroundChooseIndex) {
                        AppDataUsage.this.mRestrictBackgroundUss.setSummary(AppDataUsage.this.items[AppDataUsage.this.restrictBackgroundChooseIndex]);
                        AppDataUsage appDataUsage = AppDataUsage.this;
                        appDataUsage.changeRestrictBackgroundType(appDataUsage.restrictBackgroundChooseIndex);
                        AppDataUsage.this.updateUssPreference();
                    }
                }
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    public void changeRestrictBackgroundType(int i) {
        BackgroundDataUtils.setAppBackgroundDataType(getContext(), this.mPackageName, this.mAppItem.key, i);
    }

    public void updateUssPreference() {
        RestrictedLockUtils.EnforcedAdmin checkIfMeteredDataRestricted = RestrictedLockUtilsInternal.checkIfMeteredDataRestricted(getContext(), this.mPackageName, UserHandle.getUserId(this.mAppItem.key));
        RestrictedPreference restrictedPreference = this.mRestrictBackgroundUss;
        if (restrictedPreference != null) {
            restrictedPreference.setDisabledByAdmin(checkIfMeteredDataRestricted);
        }
        RestrictedSwitchPreference restrictedSwitchPreference = this.mUnrestrictedData;
        if (restrictedSwitchPreference == null) {
            return;
        }
        if (this.restrictBackgroundChooseIndex == 1) {
            restrictedSwitchPreference.setVisible(false);
            return;
        }
        restrictedSwitchPreference.setVisible(true);
        this.mUnrestrictedData.setChecked(getUnrestrictData());
        this.mUnrestrictedData.setDisabledByAdmin(checkIfMeteredDataRestricted);
    }
}
