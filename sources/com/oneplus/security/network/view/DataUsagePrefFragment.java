package com.oneplus.security.network.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkTemplate;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import com.android.settings.C0019R$xml;
import com.oneplus.security.network.trafficalarm.TrafficUsageAlarmUtils;
import com.oneplus.security.network.trafficinfo.NativeTrafficDataModel;
import com.oneplus.security.utils.LogUtils;
import com.oneplus.security.utils.OPSNSUtils;
import com.oneplus.security.utils.Utils;

@SuppressLint({"ValidFragment"})
public class DataUsagePrefFragment extends PreferenceFragmentCompat {
    private Context mContext;
    private int mCurrentSlotId;
    private Preference mDataUsagePackageSet;
    private HeaderPreference mDataUsagePreference;
    private Preference mDataUsageRank;
    private Handler mHandler = new Handler();
    private boolean mNeedHeadView = false;
    private boolean mSupportSdk = false;
    private long mWarnValue = -1;
    private Preference mWlanMeteredSettings;
    private int mXmlId = C0019R$xml.data_usage_simcard_prefs;

    @Override // androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
    }

    public DataUsagePrefFragment() {
    }

    public void setSupportSdk(boolean z) {
        this.mSupportSdk = z;
    }

    public int getCurrentSlotId() {
        return this.mCurrentSlotId;
    }

    public DataUsagePrefFragment(Context context, int i) {
        this.mContext = context;
        this.mXmlId = i;
    }

    public DataUsagePrefFragment(Context context, int i, boolean z, int i2) {
        this.mNeedHeadView = z;
        this.mCurrentSlotId = i2;
        this.mContext = context;
        this.mXmlId = i;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        if (bundle != null) {
            bundle.clear();
            bundle.putBoolean("mSupportSdk", this.mSupportSdk);
            bundle.putBoolean("mNeedHeadView", this.mNeedHeadView);
            bundle.putInt("mCurrentSlotId", this.mCurrentSlotId);
        }
        super.onSaveInstanceState(bundle);
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewStateRestored(Bundle bundle) {
        if (bundle != null) {
            this.mSupportSdk = bundle.getBoolean("mSupportSdk");
            this.mNeedHeadView = bundle.getBoolean("mNeedHeadView");
            this.mCurrentSlotId = bundle.getInt("mCurrentSlotId");
        }
        super.onViewStateRestored(bundle);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        LogUtils.d("DataUsagePrefFragment", "onCreate");
        if (bundle != null) {
            this.mSupportSdk = bundle.getBoolean("mSupportSdk");
            this.mNeedHeadView = bundle.getBoolean("mNeedHeadView");
            this.mCurrentSlotId = bundle.getInt("mCurrentSlotId");
        }
        super.onCreate(bundle);
        addPreferencesFromResource(this.mXmlId);
        this.mDataUsageRank = findPreference("op_data_usage_rank");
        this.mWlanMeteredSettings = findPreference("op_wlan_meteredsettings");
        this.mDataUsagePreference = (HeaderPreference) findPreference("header_data_preference");
        Preference findPreference = findPreference("op_data_usage_package_set");
        this.mDataUsagePackageSet = findPreference;
        if (findPreference != null) {
            findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                /* class com.oneplus.security.network.view.$$Lambda$DataUsagePrefFragment$65w9ySy8i4zfDZn6aVV1fXzO0Rw */

                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public final boolean onPreferenceClick(Preference preference) {
                    return DataUsagePrefFragment.this.lambda$onCreate$0$DataUsagePrefFragment(preference);
                }
            });
        }
        Preference preference = this.mDataUsageRank;
        if (preference != null) {
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                /* class com.oneplus.security.network.view.$$Lambda$DataUsagePrefFragment$9EjjHeRcvod7CiqL5Xlya9BFiI */

                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public final boolean onPreferenceClick(Preference preference) {
                    return DataUsagePrefFragment.this.lambda$onCreate$1$DataUsagePrefFragment(preference);
                }
            });
        }
        Preference preference2 = this.mWlanMeteredSettings;
        if (preference2 != null) {
            preference2.setVisible(false);
            this.mWlanMeteredSettings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                /* class com.oneplus.security.network.view.$$Lambda$DataUsagePrefFragment$Fm7v3I5KjNq0VhnSHXuSNkaUvjU */

                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public final boolean onPreferenceClick(Preference preference) {
                    return DataUsagePrefFragment.this.lambda$onCreate$2$DataUsagePrefFragment(preference);
                }
            });
        }
        if (this.mContext == null) {
            this.mContext = getActivity();
        }
        if (this.mContext == null) {
            LogUtils.d("DataUsagePrefFragment", "mContext is null ,Fragment onCreate return .");
        } else {
            initUIState();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreate$0 */
    public /* synthetic */ boolean lambda$onCreate$0$DataUsagePrefFragment(Preference preference) {
        Intent intent = new Intent();
        intent.setAction("com.oneplus.security.action.TrafficUsageSettings");
        intent.putExtra("sim_card_slot", this.mCurrentSlotId);
        startActivity(intent);
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreate$1 */
    public /* synthetic */ boolean lambda$onCreate$1$DataUsagePrefFragment(Preference preference) {
        NetworkTemplate networkTemplate;
        int i;
        if (!Utils.hasSDK24()) {
            Intent intent = new Intent("oneplus.intent.action.DATAUSAGESUMMARY");
            intent.putExtra("show_ethernet", this.mNeedHeadView ? 1 : 0);
            intent.putExtra("show_slotId", this.mCurrentSlotId);
            intent.putExtra(":settings:show_fragment_as_subsetting", true);
            intent.putExtra("settings:from_app", "com.oneplus.security");
            startActivity(intent);
        } else {
            try {
                Intent intent2 = new Intent("android.oneplus.action.DATAUSAGE_DATAUSAGELIS");
                if (!Utils.isIntentReceiverExists(getActivity(), intent2)) {
                    Log.w("DataUsagePrefFragment", "replace a new action");
                    intent2 = new Intent("android.settings.MOBILE_DATA_USAGE");
                }
                if (this.mNeedHeadView) {
                    i = OPSNSUtils.findSubIdBySlotId(this.mCurrentSlotId);
                    networkTemplate = NativeTrafficDataModel.getNetworkTemplate(i);
                } else {
                    i = 0;
                    networkTemplate = NetworkTemplate.buildTemplateWifiWildcard();
                }
                intent2.putExtra(":settings:show_fragment_title", this.mDataUsageRank.getTitle());
                intent2.putExtra("sub_id", i);
                intent2.putExtra("network_template", (Parcelable) networkTemplate);
                startActivity(intent2);
            } catch (Exception e) {
                LogUtils.e("DataUsagePrefFragment", e.getLocalizedMessage());
            }
        }
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreate$2 */
    public /* synthetic */ boolean lambda$onCreate$2$DataUsagePrefFragment(Preference preference) {
        Intent intent = new Intent("android.oneplus.action.DATAUSAGE_METEREDSETTINGS");
        intent.putExtra(":settings:show_fragment_as_subsetting", true);
        intent.putExtra("settings:from_app", "com.oneplus.security");
        startActivity(intent);
        return true;
    }

    public void initUIState() {
        if (this.mNeedHeadView) {
            if (this.mCurrentSlotId == -1) {
                this.mDataUsageRank.setEnabled(false);
                this.mDataUsagePackageSet.setEnabled(false);
            }
            initHeaderView();
            setHasOptionsMenu(true);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        if (TrafficUsageAlarmUtils.getDataWarnState(getContext(), false, this.mCurrentSlotId)) {
            this.mWarnValue = TrafficUsageAlarmUtils.getDataWarnValue(getContext(), this.mCurrentSlotId, -1);
        } else {
            this.mWarnValue = -1;
        }
        LogUtils.d("DataUsagePrefFragment", "onResume");
    }

    /* access modifiers changed from: protected */
    public void initHeaderView() {
        if (!Utils.currentUserIsOwner()) {
            this.mDataUsagePackageSet.setEnabled(false);
            PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("screen_brightness");
            if (preferenceCategory != null) {
                preferenceCategory.removePreference(this.mDataUsagePackageSet);
            }
        }
    }

    public void updateMonthlyRemainingData(final long j, final long j2) {
        this.mHandler.post(new Runnable() {
            /* class com.oneplus.security.network.view.DataUsagePrefFragment.AnonymousClass1 */

            public void run() {
                if (DataUsagePrefFragment.this.mDataUsagePreference != null) {
                    DataUsagePrefFragment.this.mDataUsagePreference.updateData(DataUsagePrefFragment.this.mCurrentSlotId, j, j2, DataUsagePrefFragment.this.mWarnValue);
                } else {
                    LogUtils.d("DataUsagePrefFragment", "mDataUsagePreference is null");
                }
            }
        });
    }

    public void animateUpdateMonthlyRemainingData(final long j, final long j2) {
        if (getActivity() == null || !isAdded()) {
            this.mHandler.post(new Runnable() {
                /* class com.oneplus.security.network.view.DataUsagePrefFragment.AnonymousClass2 */

                public void run() {
                    DataUsagePrefFragment.this.updateMonthlyRemainingData(j, j2);
                }
            });
            LogUtils.d("DataUsagePrefFragment", "activity is null");
            return;
        }
        updateMonthlyRemainingData(j, j2);
    }

    public boolean ismNeedHeadView() {
        return this.mNeedHeadView;
    }
}
