package com.android.settings.network.telephony;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import android.telephony.CellIdentity;
import android.telephony.CellInfo;
import android.telephony.NetworkRegistrationInfo;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.Utils;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.network.telephony.NetworkScanHelper;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.utils.ThreadUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkSelectSettings extends DashboardFragment {
    private final NetworkScanHelper.NetworkScanCallback mCallback = new NetworkScanHelper.NetworkScanCallback() {
        /* class com.android.settings.network.telephony.NetworkSelectSettings.AnonymousClass2 */

        @Override // com.android.settings.network.telephony.NetworkScanHelper.NetworkScanCallback
        public void onResults(List<CellInfo> list) {
            NetworkSelectSettings.this.mHandler.obtainMessage(2, list).sendToTarget();
        }

        @Override // com.android.settings.network.telephony.NetworkScanHelper.NetworkScanCallback
        public void onComplete() {
            NetworkSelectSettings.this.mHandler.obtainMessage(4).sendToTarget();
        }

        @Override // com.android.settings.network.telephony.NetworkScanHelper.NetworkScanCallback
        public void onError(int i) {
            NetworkSelectSettings.this.mHandler.obtainMessage(3, i, 0).sendToTarget();
        }
    };
    List<CellInfo> mCellInfoList;
    private List<String> mForbiddenPlmns;
    private final Handler mHandler = new Handler() {
        /* class com.android.settings.network.telephony.NetworkSelectSettings.AnonymousClass1 */

        public void handleMessage(Message message) {
            int i;
            Log.d("NetworkSelectSettings", "handleMessage, msg.what: " + message.what);
            int i2 = message.what;
            if (i2 == 1) {
                boolean booleanValue = ((Boolean) message.obj).booleanValue();
                NetworkSelectSettings.this.setProgressBarVisible(false);
                NetworkSelectSettings.this.getPreferenceScreen().setEnabled(true);
                NetworkOperatorPreference networkOperatorPreference = NetworkSelectSettings.this.mSelectedPreference;
                if (booleanValue) {
                    i = C0017R$string.network_connected;
                } else {
                    i = C0017R$string.network_could_not_connect;
                }
                networkOperatorPreference.setSummary(i);
            } else if (i2 == 2) {
                List list = (List) message.obj;
                if (NetworkSelectSettings.this.mRequestIdManualNetworkScan < NetworkSelectSettings.this.mRequestIdManualNetworkSelect) {
                    Log.d("NetworkSelectSettings", "CellInfoList (drop): " + CellInfoUtil.cellInfoListToString(new ArrayList(list)));
                    return;
                }
                NetworkSelectSettings.access$210(NetworkSelectSettings.this);
                if (NetworkSelectSettings.this.mWaitingForNumberOfScanResults <= 0 && !NetworkSelectSettings.this.isResumed()) {
                    NetworkSelectSettings.this.stopNetworkQuery();
                }
                NetworkSelectSettings.this.mCellInfoList = new ArrayList(list);
                Log.d("NetworkSelectSettings", "CellInfoList size: " + NetworkSelectSettings.this.mCellInfoList.size());
                Log.d("NetworkSelectSettings", "CellInfoList: " + CellInfoUtil.cellInfoListToString(NetworkSelectSettings.this.mCellInfoList));
                List<CellInfo> list2 = NetworkSelectSettings.this.mCellInfoList;
                if (list2 != null && list2.size() != 0) {
                    NetworkOperatorPreference updateAllPreferenceCategory = NetworkSelectSettings.this.updateAllPreferenceCategory();
                    if (updateAllPreferenceCategory != null) {
                        NetworkSelectSettings networkSelectSettings = NetworkSelectSettings.this;
                        if (networkSelectSettings.mSelectedPreference != null) {
                            networkSelectSettings.mSelectedPreference = updateAllPreferenceCategory;
                        }
                    } else if (!NetworkSelectSettings.this.getPreferenceScreen().isEnabled() && updateAllPreferenceCategory == null) {
                        NetworkSelectSettings.this.mSelectedPreference.setSummary(C0017R$string.network_connecting);
                    }
                    NetworkSelectSettings.this.getPreferenceScreen().setEnabled(true);
                } else if (NetworkSelectSettings.this.getPreferenceScreen().isEnabled()) {
                    NetworkSelectSettings.this.addMessagePreference(C0017R$string.empty_networks_list);
                    NetworkSelectSettings.this.setProgressBarVisible(true);
                }
            } else if (i2 == 3) {
                NetworkSelectSettings.this.stopNetworkQuery();
                Log.i("NetworkSelectSettings", "Network scan failure " + message.arg1 + ": scan request 0x" + Long.toHexString(NetworkSelectSettings.this.mRequestIdManualNetworkScan) + ", waiting for scan results = " + NetworkSelectSettings.this.mWaitingForNumberOfScanResults + ", select request 0x" + Long.toHexString(NetworkSelectSettings.this.mRequestIdManualNetworkSelect));
                if (NetworkSelectSettings.this.mRequestIdManualNetworkScan >= NetworkSelectSettings.this.mRequestIdManualNetworkSelect) {
                    if (!NetworkSelectSettings.this.getPreferenceScreen().isEnabled()) {
                        NetworkSelectSettings.this.clearPreferenceSummary();
                        NetworkSelectSettings.this.getPreferenceScreen().setEnabled(true);
                        return;
                    }
                    NetworkSelectSettings.this.addMessagePreference(C0017R$string.network_query_error);
                }
            } else if (i2 == 4) {
                NetworkSelectSettings.this.stopNetworkQuery();
                Log.d("NetworkSelectSettings", "Network scan complete: scan request 0x" + Long.toHexString(NetworkSelectSettings.this.mRequestIdManualNetworkScan) + ", waiting for scan results = " + NetworkSelectSettings.this.mWaitingForNumberOfScanResults + ", select request 0x" + Long.toHexString(NetworkSelectSettings.this.mRequestIdManualNetworkSelect));
                if (NetworkSelectSettings.this.mRequestIdManualNetworkScan >= NetworkSelectSettings.this.mRequestIdManualNetworkSelect) {
                    if (!NetworkSelectSettings.this.getPreferenceScreen().isEnabled()) {
                        NetworkSelectSettings.this.clearPreferenceSummary();
                        NetworkSelectSettings.this.getPreferenceScreen().setEnabled(true);
                        return;
                    }
                    NetworkSelectSettings networkSelectSettings2 = NetworkSelectSettings.this;
                    if (networkSelectSettings2.mCellInfoList == null) {
                        networkSelectSettings2.addMessagePreference(C0017R$string.empty_networks_list);
                    }
                }
            }
        }
    };
    private boolean mIsAdvancedScanSupported;
    private MetricsFeatureProvider mMetricsFeatureProvider;
    private final ExecutorService mNetworkScanExecutor = Executors.newFixedThreadPool(1);
    private NetworkScanHelper mNetworkScanHelper;
    PreferenceCategory mPreferenceCategory;
    private View mProgressHeader;
    private long mRequestIdManualNetworkScan;
    private long mRequestIdManualNetworkSelect;
    NetworkOperatorPreference mSelectedPreference;
    private boolean mShow4GForLTE = false;
    private Preference mStatusMessagePreference;
    private int mSubId = -1;
    TelephonyManager mTelephonyManager;
    private long mWaitingForNumberOfScanResults;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "NetworkSelectSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1581;
    }

    static /* synthetic */ long access$210(NetworkSelectSettings networkSelectSettings) {
        long j = networkSelectSettings.mWaitingForNumberOfScanResults;
        networkSelectSettings.mWaitingForNumberOfScanResults = j - 1;
        return j;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mIsAdvancedScanSupported = Utils.isAdvancedPlmnScanSupported();
        Log.d("NetworkSelectSettings", "mIsAdvancedScanSupported: " + this.mIsAdvancedScanSupported);
        this.mSubId = getArguments().getInt("android.provider.extra.SUB_ID");
        this.mPreferenceCategory = (PreferenceCategory) findPreference("network_operators_preference");
        Preference preference = new Preference(getContext());
        this.mStatusMessagePreference = preference;
        preference.setSelectable(false);
        this.mSelectedPreference = null;
        this.mTelephonyManager = ((TelephonyManager) getContext().getSystemService(TelephonyManager.class)).createForSubscriptionId(this.mSubId);
        this.mNetworkScanHelper = new NetworkScanHelper(getContext(), this.mTelephonyManager, this.mCallback, this.mNetworkScanExecutor);
        PersistableBundle configForSubId = ((CarrierConfigManager) getContext().getSystemService("carrier_config")).getConfigForSubId(this.mSubId);
        if (configForSubId != null) {
            this.mShow4GForLTE = configForSubId.getBoolean("show_4g_for_lte_data_icon_bool");
        }
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(getContext()).getMetricsFeatureProvider();
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        if (getActivity() != null) {
            this.mProgressHeader = setPinnedHeaderView(C0012R$layout.op_progress_header).findViewById(C0010R$id.progress_bar_animation);
            setProgressBarVisible(false);
        }
        forceUpdateConnectedPreferenceCategory();
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStart() {
        Log.d("NetworkSelectSettings", "onStart()");
        super.onStart();
        updateForbiddenPlmns();
        if (!isProgressBarVisible() && this.mWaitingForNumberOfScanResults <= 0) {
            this.mSelectedPreference = null;
            startNetworkQuery();
        }
    }

    /* access modifiers changed from: package-private */
    public void updateForbiddenPlmns() {
        List<String> list;
        String[] forbiddenPlmns = this.mTelephonyManager.getForbiddenPlmns();
        if (forbiddenPlmns != null) {
            list = Arrays.asList(forbiddenPlmns);
        } else {
            list = new ArrayList<>();
        }
        this.mForbiddenPlmns = list;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStop() {
        Log.d("NetworkSelectSettings", "onStop() mWaitingForNumberOfScanResults: " + this.mWaitingForNumberOfScanResults);
        super.onStop();
        if (this.mWaitingForNumberOfScanResults <= 0) {
            stopNetworkQuery();
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener, com.android.settings.dashboard.DashboardFragment
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference != this.mSelectedPreference) {
            stopNetworkQuery();
            clearPreferenceSummary();
            NetworkOperatorPreference networkOperatorPreference = this.mSelectedPreference;
            if (networkOperatorPreference != null) {
                networkOperatorPreference.setSummary(C0017R$string.network_disconnected);
            }
            NetworkOperatorPreference networkOperatorPreference2 = (NetworkOperatorPreference) preference;
            this.mSelectedPreference = networkOperatorPreference2;
            networkOperatorPreference2.setSummary(C0017R$string.network_connecting);
            this.mMetricsFeatureProvider.action(getContext(), 1210, new Pair[0]);
            setProgressBarVisible(true);
            getPreferenceScreen().setEnabled(false);
            this.mRequestIdManualNetworkSelect = getNewRequestId();
            this.mWaitingForNumberOfScanResults = 2;
            ThreadUtils.postOnBackgroundThread(new Runnable(this.mSelectedPreference.getOperatorNumeric(), this.mSelectedPreference.getAccessNetworkType()) {
                /* class com.android.settings.network.telephony.$$Lambda$NetworkSelectSettings$KkdAQ1DM4ARIcnvhyGVQoIqTY8 */
                public final /* synthetic */ String f$1;
                public final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    NetworkSelectSettings.this.lambda$onPreferenceTreeClick$0$NetworkSelectSettings(this.f$1, this.f$2);
                }
            });
        }
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onPreferenceTreeClick$0 */
    public /* synthetic */ void lambda$onPreferenceTreeClick$0$NetworkSelectSettings(String str, int i) {
        Message obtainMessage = this.mHandler.obtainMessage(1);
        obtainMessage.obj = Boolean.valueOf(this.mTelephonyManager.setNetworkSelectionModeManual(str, true, i));
        obtainMessage.sendToTarget();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.choose_network;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x006f  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0099  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00a0  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.android.settings.network.telephony.NetworkOperatorPreference updateAllPreferenceCategory() {
        /*
        // Method dump skipped, instructions count: 167
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.network.telephony.NetworkSelectSettings.updateAllPreferenceCategory():com.android.settings.network.telephony.NetworkOperatorPreference");
    }

    private void forceUpdateConnectedPreferenceCategory() {
        List<NetworkRegistrationInfo> networkRegistrationInfoListForTransportType;
        int dataState = this.mTelephonyManager.getDataState();
        TelephonyManager telephonyManager = this.mTelephonyManager;
        if (!(dataState != 2 || (networkRegistrationInfoListForTransportType = telephonyManager.getServiceState().getNetworkRegistrationInfoListForTransportType(1)) == null || networkRegistrationInfoListForTransportType.size() == 0)) {
            HashSet<CellIdentity> hashSet = new HashSet();
            for (NetworkRegistrationInfo networkRegistrationInfo : networkRegistrationInfoListForTransportType) {
                Log.d("NetworkSelectSettings", "regInfo: " + networkRegistrationInfo.toString());
                CellIdentity cellIdentity = networkRegistrationInfo.getCellIdentity();
                if (cellIdentity != null) {
                    hashSet.add(cellIdentity);
                }
            }
            for (CellIdentity cellIdentity2 : hashSet) {
                NetworkOperatorPreference networkOperatorPreference = new NetworkOperatorPreference(getPrefContext(), cellIdentity2, this.mForbiddenPlmns, this.mShow4GForLTE);
                networkOperatorPreference.setSummary(C0017R$string.network_connected);
                networkOperatorPreference.setIcon(SignalStrength.NUM_SIGNAL_STRENGTH_BINS - 1);
                this.mPreferenceCategory.addPreference(networkOperatorPreference);
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void clearPreferenceSummary() {
        int preferenceCount = this.mPreferenceCategory.getPreferenceCount();
        while (preferenceCount > 0) {
            preferenceCount--;
            ((NetworkOperatorPreference) this.mPreferenceCategory.getPreference(preferenceCount)).setSummary((CharSequence) null);
        }
    }

    private long getNewRequestId() {
        return Math.max(this.mRequestIdManualNetworkSelect, this.mRequestIdManualNetworkScan) + 1;
    }

    private boolean isProgressBarVisible() {
        View view = this.mProgressHeader;
        if (view != null && view.getVisibility() == 0) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void setProgressBarVisible(boolean z) {
        View view = this.mProgressHeader;
        if (view != null) {
            view.setVisibility(z ? 0 : 8);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void addMessagePreference(int i) {
        setProgressBarVisible(false);
        this.mStatusMessagePreference.setTitle(i);
        this.mPreferenceCategory.removeAll();
        this.mPreferenceCategory.addPreference(this.mStatusMessagePreference);
    }

    private void startNetworkQuery() {
        int i = 1;
        setProgressBarVisible(true);
        if (this.mNetworkScanHelper != null) {
            this.mRequestIdManualNetworkScan = getNewRequestId();
            this.mWaitingForNumberOfScanResults = 2;
            NetworkScanHelper networkScanHelper = this.mNetworkScanHelper;
            if (!this.mIsAdvancedScanSupported) {
                i = 2;
            }
            networkScanHelper.startNetworkScan(i);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void stopNetworkQuery() {
        setProgressBarVisible(false);
        NetworkScanHelper networkScanHelper = this.mNetworkScanHelper;
        if (networkScanHelper != null) {
            this.mWaitingForNumberOfScanResults = 0;
            networkScanHelper.stopNetworkQuery();
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onDestroy() {
        Log.d("NetworkSelectSettings", "onDestroy()");
        stopNetworkQuery();
        this.mNetworkScanExecutor.shutdown();
        super.onDestroy();
    }
}
