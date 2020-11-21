package com.android.settings.datausage;

import android.app.ActivityManager;
import android.app.usage.NetworkStats;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.NetworkPolicy;
import android.net.NetworkTemplate;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.telephony.SubscriptionInfo;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;
import com.android.settings.C0006R$color;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.Utils;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.datausage.CycleAdapter;
import com.android.settings.network.MobileDataEnabledListener;
import com.android.settings.network.ProxySubscriptionManager;
import com.android.settings.widget.LoadingViewController;
import com.android.settingslib.AppItem;
import com.android.settingslib.net.NetworkCycleChartData;
import com.android.settingslib.net.NetworkCycleChartDataLoader;
import com.android.settingslib.net.NetworkStatsSummaryLoader;
import com.android.settingslib.net.UidDetail;
import com.android.settingslib.net.UidDetailProvider;
import com.oneplus.settings.utils.OPUtils;
import com.oneplus.settings.utils.ProductUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataUsageList extends DataUsageBaseFragment implements MobileDataEnabledListener.Client {
    static final int LOADER_CHART_DATA = 2;
    static final int LOADER_SUMMARY = 3;
    private PreferenceGroup mApps;
    private ChartDataUsagePreference mChart;
    private CycleAdapter mCycleAdapter;
    private List<NetworkCycleChartData> mCycleData;
    private AdapterView.OnItemSelectedListener mCycleListener = new AdapterView.OnItemSelectedListener() {
        /* class com.android.settings.datausage.DataUsageList.AnonymousClass4 */

        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onNothingSelected(AdapterView<?> adapterView) {
        }

        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
            long[] dataUsageSectionTimeMillByAccountDay = OPDataUsageUtils.getDataUsageSectionTimeMillByAccountDay(DataUsageList.this.getPrefContext(), DataUsageList.this.mSubId);
            new CycleAdapter.CycleItem(DataUsageList.this.getPrefContext(), dataUsageSectionTimeMillByAccountDay[0], dataUsageSectionTimeMillByAccountDay[1]);
            DataUsageList.this.mChart.setNetworkCycleData((NetworkCycleChartData) DataUsageList.this.mCycleData.get(i));
            DataUsageList.this.updateDetailData();
        }
    };
    Spinner mCycleSpinner;
    private ArrayList<Long> mCycles;
    MobileDataEnabledListener mDataStateListener;
    private SparseArray<AppItem> mExistedItems = new SparseArray<>();
    private View mHeader;
    LoadingViewController mLoadingViewController;
    final LoaderManager.LoaderCallbacks<List<NetworkCycleChartData>> mNetworkCycleDataCallbacks = new LoaderManager.LoaderCallbacks<List<NetworkCycleChartData>>() {
        /* class com.android.settings.datausage.DataUsageList.AnonymousClass5 */

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public Loader<List<NetworkCycleChartData>> onCreateLoader(int i, Bundle bundle) {
            NetworkCycleChartDataLoader.Builder<?> builder = NetworkCycleChartDataLoader.builder(DataUsageList.this.getContext());
            builder.setNetworkTemplate(DataUsageList.this.mTemplate);
            return builder.build();
        }

        public void onLoadFinished(Loader<List<NetworkCycleChartData>> loader, List<NetworkCycleChartData> list) {
            if (!(list == null || list.size() == 0)) {
                DataUsageList.this.mUsageAmount.setVisible(true);
            }
            DataUsageList.this.mLoadingViewController.showContent(false);
            DataUsageList.this.mCycleData = list;
            DataUsageList.this.updatePolicy();
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoaderReset(Loader<List<NetworkCycleChartData>> loader) {
            DataUsageList.this.mCycleData = null;
        }
    };
    private final LoaderManager.LoaderCallbacks<NetworkStats> mNetworkStatsDetailCallbacks = new LoaderManager.LoaderCallbacks<NetworkStats>() {
        /* class com.android.settings.datausage.DataUsageList.AnonymousClass6 */

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public Loader<NetworkStats> onCreateLoader(int i, Bundle bundle) {
            NetworkStatsSummaryLoader.Builder builder = new NetworkStatsSummaryLoader.Builder(DataUsageList.this.getContext());
            builder.setStartTime(DataUsageList.this.mChart.getInspectStart());
            builder.setEndTime(DataUsageList.this.mChart.getInspectEnd());
            builder.setNetworkTemplate(DataUsageList.this.mTemplate);
            return builder.build();
        }

        public void onLoadFinished(Loader<NetworkStats> loader, NetworkStats networkStats) {
            DataUsageList.this.bindStats(networkStats, DataUsageList.this.services.mPolicyManager.getUidsWithPolicy(1));
            updateEmptyVisible();
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoaderReset(Loader<NetworkStats> loader) {
            DataUsageList.this.bindStats(null, new int[0]);
            updateEmptyVisible();
        }

        private void updateEmptyVisible() {
            boolean z = true;
            boolean z2 = DataUsageList.this.mApps.getPreferenceCount() != 0;
            if (DataUsageList.this.getPreferenceScreen().getPreferenceCount() == 0) {
                z = false;
            }
            if (z2 == z) {
                return;
            }
            if (DataUsageList.this.mApps.getPreferenceCount() != 0) {
                DataUsageList.this.getPreferenceScreen().addPreference(DataUsageList.this.mUsageAmount);
                DataUsageList.this.getPreferenceScreen().addPreference(DataUsageList.this.mApps);
                return;
            }
            DataUsageList.this.getPreferenceScreen().removeAll();
        }
    };
    int mNetworkType;
    int mSubId = -1;
    NetworkTemplate mTemplate;
    private UidDetailProvider mUidDetailProvider;
    private Preference mUsageAmount;
    private TextView tv_filter_datetime;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "DataUsageList";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 341;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.datausage.DataUsageBaseFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        if (!isBandwidthControlEnabled()) {
            Log.w("DataUsageList", "No bandwidth control; leaving");
            activity.finish();
            return;
        }
        this.mUidDetailProvider = new UidDetailProvider(activity);
        this.mUsageAmount = findPreference("usage_amount");
        this.mChart = (ChartDataUsagePreference) findPreference("chart_data");
        this.mApps = (PreferenceGroup) findPreference("apps_group");
        if (!ProductUtils.isUsvMode()) {
            ((PreferenceCategory) findPreference("usage_amount")).removePreference(findPreference("data_usage_disclaimer"));
        }
        processArgument();
        this.mDataStateListener = new MobileDataEnabledListener(activity, this);
        this.mChart.setShowWifi(!this.mTemplate.isMatchRuleMobile());
        this.mChart.setSubId(this.mSubId);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        View pinnedHeaderView = setPinnedHeaderView(C0012R$layout.apps_filter_spinner);
        this.mHeader = pinnedHeaderView;
        pinnedHeaderView.findViewById(C0010R$id.filter_settings).setOnClickListener(new View.OnClickListener() {
            /* class com.android.settings.datausage.$$Lambda$DataUsageList$YolaBauY8HvHsYGl5vfnCCKHiAQ */

            public final void onClick(View view) {
                DataUsageList.this.lambda$onViewCreated$0$DataUsageList(view);
            }
        });
        Spinner spinner = (Spinner) this.mHeader.findViewById(C0010R$id.filter_spinner);
        this.mCycleSpinner = spinner;
        spinner.setVisibility(8);
        this.mHeader.setVisibility(8);
        this.tv_filter_datetime = (TextView) this.mHeader.findViewById(C0010R$id.tv_filter_datetime);
        long[] dataUsageSectionTimeMillByAccountDay = OPDataUsageUtils.getDataUsageSectionTimeMillByAccountDay(getPrefContext(), this.mSubId);
        this.mChart.setVisibleRange(dataUsageSectionTimeMillByAccountDay[0], dataUsageSectionTimeMillByAccountDay[1]);
        this.tv_filter_datetime.setText(Utils.formatDateRange(getPrefContext(), dataUsageSectionTimeMillByAccountDay[0], dataUsageSectionTimeMillByAccountDay[1]));
        ((ImageView) this.mHeader.findViewById(C0010R$id.filter_settings)).setVisibility(8);
        this.mCycleAdapter = new CycleAdapter(this.mCycleSpinner.getContext(), new CycleAdapter.SpinnerInterface() {
            /* class com.android.settings.datausage.DataUsageList.AnonymousClass1 */

            @Override // com.android.settings.datausage.CycleAdapter.SpinnerInterface
            public void setAdapter(CycleAdapter cycleAdapter) {
                DataUsageList.this.mCycleSpinner.setAdapter((SpinnerAdapter) cycleAdapter);
            }

            @Override // com.android.settings.datausage.CycleAdapter.SpinnerInterface
            public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener onItemSelectedListener) {
                DataUsageList.this.mCycleSpinner.setOnItemSelectedListener(onItemSelectedListener);
            }

            @Override // com.android.settings.datausage.CycleAdapter.SpinnerInterface
            public Object getSelectedItem() {
                return DataUsageList.this.mCycleSpinner.getSelectedItem();
            }

            @Override // com.android.settings.datausage.CycleAdapter.SpinnerInterface
            public void setSelection(int i) {
                DataUsageList.this.mCycleSpinner.setSelection(i);
            }
        }, this.mCycleListener);
        this.mCycleSpinner.setAccessibilityDelegate(new View.AccessibilityDelegate(this) {
            /* class com.android.settings.datausage.DataUsageList.AnonymousClass2 */

            public void sendAccessibilityEvent(View view, int i) {
                if (i != 4) {
                    super.sendAccessibilityEvent(view, i);
                }
            }
        });
        LoadingViewController loadingViewController = new LoadingViewController(getView().findViewById(C0010R$id.loading_container), getListView());
        this.mLoadingViewController = loadingViewController;
        loadingViewController.showLoadingViewDelayed();
        this.mUsageAmount.setVisible(false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onViewCreated$0 */
    public /* synthetic */ void lambda$onViewCreated$0$DataUsageList(View view) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("network_template", this.mTemplate);
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(getContext());
        subSettingLauncher.setDestination(BillingCycleSettings.class.getName());
        subSettingLauncher.setTitleRes(C0017R$string.billing_cycle);
        subSettingLauncher.setSourceMetricsCategory(getMetricsCategory());
        subSettingLauncher.setArguments(bundle);
        subSettingLauncher.launch();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.datausage.DataUsageBaseFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        this.mDataStateListener.start(this.mSubId);
        getLoaderManager().restartLoader(2, buildArgs(this.mTemplate), this.mNetworkCycleDataCallbacks);
        updateBody();
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        this.mDataStateListener.stop();
        getLoaderManager().destroyLoader(2);
        getLoaderManager().destroyLoader(3);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onDestroy() {
        this.mUidDetailProvider.clearCache();
        this.mUidDetailProvider = null;
        super.onDestroy();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.data_usage_list;
    }

    /* access modifiers changed from: package-private */
    public void processArgument() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            this.mSubId = arguments.getInt("sub_id", -1);
            this.mTemplate = arguments.getParcelable("network_template");
            this.mNetworkType = arguments.getInt("network_type", 0);
        }
        if (this.mTemplate == null && this.mSubId == -1) {
            Intent intent = getIntent();
            this.mSubId = intent.getIntExtra("sub_id", -1);
            this.mTemplate = intent.getParcelableExtra("network_template");
        }
        if (this.mTemplate == null && this.mSubId == -1) {
            Intent intent2 = getIntent();
            this.mSubId = intent2.getIntExtra("android.provider.extra.SUB_ID", -1);
            this.mTemplate = intent2.getParcelableExtra("network_template");
        }
    }

    @Override // com.android.settings.network.MobileDataEnabledListener.Client
    public void onMobileDataEnabledChange() {
        updatePolicy();
    }

    private void updateBody() {
        SubscriptionInfo activeSubscriptionInfo;
        if (isAdded()) {
            FragmentActivity activity = getActivity();
            getActivity().invalidateOptionsMenu();
            int color = activity.getColor(C0006R$color.sim_noitification);
            if (!(this.mSubId == -1 || (activeSubscriptionInfo = ProxySubscriptionManager.getInstance(activity).getActiveSubscriptionInfo(this.mSubId)) == null)) {
                color = activeSubscriptionInfo.getIconTint();
            }
            this.mChart.setColors(color, Color.argb(127, Color.red(color), Color.green(color), Color.blue(color)));
        }
    }

    private Bundle buildArgs(NetworkTemplate networkTemplate) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("template", networkTemplate);
        bundle.putParcelable("app", null);
        bundle.putInt("fields", 10);
        return bundle;
    }

    /* access modifiers changed from: package-private */
    public void updatePolicy() {
        NetworkPolicy policy = this.services.mPolicyEditor.getPolicy(this.mTemplate);
        this.mHeader.findViewById(C0010R$id.filter_settings);
        if (isNetworkPolicyModifiable(policy, this.mSubId) && isMobileDataAvailable(this.mSubId)) {
            this.mChart.setNetworkPolicy(policy);
        }
        if (this.mCycleAdapter.updateCycleList(this.mCycleData)) {
            updateDetailData();
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateDetailData() {
        getLoaderManager().restartLoader(3, null, this.mNetworkStatsDetailCallbacks);
        List<NetworkCycleChartData> list = this.mCycleData;
        CharSequence formatDataUsage = DataUsageUtils.formatDataUsage(getActivity(), (list == null || list.isEmpty()) ? 0 : this.mCycleData.get(this.mCycleSpinner.getSelectedItemPosition()).getTotalUsage());
        long[] dataUsageSectionTimeMillByAccountDay = OPDataUsageUtils.getDataUsageSectionTimeMillByAccountDay(getPrefContext(), this.mSubId);
        String formatDateRange = Utils.formatDateRange(getPrefContext(), dataUsageSectionTimeMillByAccountDay[0], dataUsageSectionTimeMillByAccountDay[1]);
        String string = getString(C0017R$string.data_used_template, formatDataUsage);
        ChartDataUsagePreference chartDataUsagePreference = this.mChart;
        chartDataUsagePreference.setUsageAmount(string + "(" + formatDateRange + ")");
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void bindStats(NetworkStats networkStats, int[] iArr) {
        NetworkStats.Bucket bucket;
        long j;
        int i;
        int i2;
        int i3;
        NetworkStats networkStats2 = networkStats;
        if (networkStats2 != null) {
            ArrayList arrayList = new ArrayList();
            int currentUser = ActivityManager.getCurrentUser();
            UserManager userManager = UserManager.get(getContext());
            List<UserHandle> userProfiles = userManager.getUserProfiles();
            SparseArray sparseArray = new SparseArray();
            PackageManager packageManager = getContext().getPackageManager();
            NetworkStats.Bucket bucket2 = new NetworkStats.Bucket();
            long j2 = 0;
            while (true) {
                int i4 = 0;
                if (!networkStats.hasNextBucket() || !networkStats2.getNextBucket(bucket2)) {
                    networkStats.close();
                } else {
                    int uid = bucket2.getUid();
                    int userId = UserHandle.getUserId(uid);
                    int i5 = -4;
                    if (!UserHandle.isApp(uid)) {
                        bucket = bucket2;
                        i3 = uid;
                        if (!(i3 == -4 || i3 == -5 || i3 == 1061)) {
                            i3 = 1000;
                        }
                    } else if (!userProfiles.contains(new UserHandle(userId))) {
                        bucket = bucket2;
                        if (userManager.getUserInfo(userId) == null) {
                            i4 = 2;
                        } else {
                            i5 = UidDetailProvider.buildKeyForUser(userId);
                        }
                        i2 = i5;
                        j = j2;
                        i = i4;
                        j2 = accumulate(i2, sparseArray, bucket, i, arrayList, j);
                        networkStats2 = networkStats;
                        bucket2 = bucket;
                    } else if (userId != currentUser) {
                        bucket = bucket2;
                        i3 = uid;
                        j2 = accumulate(UidDetailProvider.buildKeyForUser(userId), sparseArray, bucket2, 0, arrayList, j2);
                    } else {
                        bucket = bucket2;
                        i3 = uid;
                    }
                    i2 = i3;
                    j = j2;
                    i = 2;
                    j2 = accumulate(i2, sparseArray, bucket, i, arrayList, j);
                    networkStats2 = networkStats;
                    bucket2 = bucket;
                }
            }
            networkStats.close();
            for (int i6 : iArr) {
                if (userProfiles.contains(new UserHandle(UserHandle.getUserId(i6)))) {
                    try {
                        ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageManager.getPackagesForUid(i6)[0], 0);
                        if (UserHandle.getUserId(i6) == 999 && applicationInfo != null && (applicationInfo.flags & 1) > 0) {
                        }
                    } catch (Exception e) {
                        Log.d("DataUsageList", "get dialer getApplicationInfo failed " + e);
                    }
                    AppItem appItem = (AppItem) sparseArray.get(i6);
                    if (appItem == null) {
                        appItem = new AppItem(i6);
                        appItem.total = -1;
                        arrayList.add(appItem);
                        sparseArray.put(appItem.key, appItem);
                    }
                    appItem.restricted = true;
                }
            }
            Collections.sort(arrayList);
            OPUtils.getCorpUserInfo(getContext());
            for (int i7 = 0; i7 < arrayList.size(); i7++) {
                AppDataUsagePreference appDataUsagePreference = new AppDataUsagePreference(getContext(), (AppItem) arrayList.get(i7), j2 != 0 ? (int) ((((AppItem) arrayList.get(i7)).total * 100) / j2) : 0, this.mUidDetailProvider);
                if (!String.valueOf(((AppItem) arrayList.get(i7)).key).equals(appDataUsagePreference.getTitle()) && this.mExistedItems.get(((AppItem) arrayList.get(i7)).key) == null) {
                    appDataUsagePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        /* class com.android.settings.datausage.DataUsageList.AnonymousClass3 */

                        @Override // androidx.preference.Preference.OnPreferenceClickListener
                        public boolean onPreferenceClick(Preference preference) {
                            AppItem item = ((AppDataUsagePreference) preference).getItem();
                            int i = item.key;
                            if (i == 1000 || i == -4) {
                                UidDetail uidDetail = new UidDetail();
                                AppDataUsage.OSUidDetail = uidDetail;
                                uidDetail.icon = preference.getIcon();
                                AppDataUsage.OSUidDetail.label = preference.getTitle();
                            }
                            DataUsageList.this.startAppDataUsage(item);
                            return true;
                        }
                    });
                    this.mApps.addPreference(appDataUsagePreference);
                    this.mExistedItems.put(((AppItem) arrayList.get(i7)).key, (AppItem) arrayList.get(i7));
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void startAppDataUsage(AppItem appItem) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("app_item", appItem);
        bundle.putParcelable("network_template", this.mTemplate);
        bundle.putInt("arg_subid", this.mSubId);
        bundle.putInt("uid", appItem.key);
        if (this.mCycles == null) {
            this.mCycles = new ArrayList<>();
            for (NetworkCycleChartData networkCycleChartData : this.mCycleData) {
                if (this.mCycles.isEmpty()) {
                    this.mCycles.add(Long.valueOf(networkCycleChartData.getEndTime()));
                }
                this.mCycles.add(Long.valueOf(networkCycleChartData.getStartTime()));
            }
        }
        bundle.putSerializable("network_cycles", this.mCycles);
        try {
            bundle.putLong("selected_cycle", this.mCycleData.get(this.mCycleSpinner.getSelectedItemPosition()).getEndTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(getContext());
        subSettingLauncher.setDestination(AppDataUsage.class.getName());
        subSettingLauncher.setTitleRes(C0017R$string.data_usage_app_summary_title);
        subSettingLauncher.setArguments(bundle);
        subSettingLauncher.setSourceMetricsCategory(getMetricsCategory());
        subSettingLauncher.launch();
    }

    private static long accumulate(int i, SparseArray<AppItem> sparseArray, NetworkStats.Bucket bucket, int i2, ArrayList<AppItem> arrayList, long j) {
        int uid = bucket.getUid();
        AppItem appItem = sparseArray.get(i);
        if (appItem == null) {
            appItem = new AppItem(i);
            appItem.category = i2;
            arrayList.add(appItem);
            sparseArray.put(appItem.key, appItem);
        }
        appItem.addUid(uid);
        long rxBytes = appItem.total + bucket.getRxBytes() + bucket.getTxBytes();
        appItem.total = rxBytes;
        return Math.max(j, rxBytes);
    }
}
