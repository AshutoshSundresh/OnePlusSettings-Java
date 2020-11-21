package com.android.settings.network.telephony;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkTemplate;
import android.os.Parcelable;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.C0017R$string;
import com.android.settings.datausage.DataUsageUtils;
import com.android.settings.datausage.lib.DataUsageLib;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.net.DataUsageController;
import com.android.settingslib.utils.ThreadUtils;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

public class DataUsagePreferenceController extends TelephonyBasePreferenceController {
    private static final String LOG_TAG = "DataUsagePreferCtrl";
    private Future<Long> mHistoricalUsageLevel;
    private AtomicReference<NetworkTemplate> mTemplate = new AtomicReference<>();
    private Future<NetworkTemplate> mTemplateFuture;

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public DataUsagePreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.network.telephony.TelephonyAvailabilityCallback, com.android.settings.network.telephony.TelephonyBasePreferenceController
    public int getAvailabilityStatus(int i) {
        return !SubscriptionManager.isValidSubscriptionId(i) ? 1 : 0;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals(preference.getKey(), getPreferenceKey())) {
            return false;
        }
        Intent intent = new Intent("android.settings.MOBILE_DATA_USAGE");
        intent.putExtra("network_template", (Parcelable) getNetworkTemplate());
        intent.putExtra("android.provider.extra.SUB_ID", this.mSubId);
        this.mContext.startActivity(intent);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (!SubscriptionManager.isValidSubscriptionId(this.mSubId)) {
            preference.setEnabled(false);
            return;
        }
        CharSequence dataUsageSummary = getDataUsageSummary(this.mContext, this.mSubId);
        if (dataUsageSummary == null) {
            preference.setEnabled(false);
            return;
        }
        preference.setEnabled(true);
        preference.setSummary(dataUsageSummary);
    }

    public void init(int i) {
        this.mSubId = i;
        this.mTemplate.set(null);
        this.mTemplateFuture = ThreadUtils.postOnBackgroundThread(new Callable() {
            /* class com.android.settings.network.telephony.$$Lambda$DataUsagePreferenceController$CmdXLGeXH_M_q7g6YocgysAQjoo */

            @Override // java.util.concurrent.Callable
            public final Object call() {
                return DataUsagePreferenceController.this.lambda$init$0$DataUsagePreferenceController();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$init$0 */
    public /* synthetic */ Object lambda$init$0$DataUsagePreferenceController() throws Exception {
        return fetchMobileTemplate(this.mContext, this.mSubId);
    }

    private NetworkTemplate fetchMobileTemplate(Context context, int i) {
        if (!SubscriptionManager.isValidSubscriptionId(i)) {
            return null;
        }
        return DataUsageLib.getMobileTemplate(context, i);
    }

    private NetworkTemplate getNetworkTemplate() {
        Throwable e;
        if (!SubscriptionManager.isValidSubscriptionId(this.mSubId)) {
            return null;
        }
        NetworkTemplate networkTemplate = this.mTemplate.get();
        if (networkTemplate != null) {
            return networkTemplate;
        }
        try {
            NetworkTemplate networkTemplate2 = this.mTemplateFuture.get();
            try {
                this.mTemplate.set(networkTemplate2);
                return networkTemplate2;
            } catch (InterruptedException | NullPointerException | ExecutionException e2) {
                e = e2;
                networkTemplate = networkTemplate2;
            }
        } catch (InterruptedException | NullPointerException | ExecutionException e3) {
            e = e3;
            Log.e(LOG_TAG, "Fail to get data usage template", e);
            return networkTemplate;
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public DataUsageController.DataUsageInfo getDataUsageInfo(DataUsageController dataUsageController) {
        return dataUsageController.getDataUsageInfo(getNetworkTemplate());
    }

    private CharSequence getDataUsageSummary(Context context, int i) {
        DataUsageController dataUsageController = new DataUsageController(context);
        dataUsageController.setSubscriptionId(i);
        this.mHistoricalUsageLevel = ThreadUtils.postOnBackgroundThread(new Callable(dataUsageController) {
            /* class com.android.settings.network.telephony.$$Lambda$DataUsagePreferenceController$PTXr8Xt74qtZhUPDryzAo_5Og6Y */
            public final /* synthetic */ DataUsageController f$1;

            {
                this.f$1 = r2;
            }

            @Override // java.util.concurrent.Callable
            public final Object call() {
                return DataUsagePreferenceController.this.lambda$getDataUsageSummary$1$DataUsagePreferenceController(this.f$1);
            }
        });
        DataUsageController.DataUsageInfo dataUsageInfo = getDataUsageInfo(dataUsageController);
        long j = dataUsageInfo.usageLevel;
        if (j <= 0) {
            try {
                j = this.mHistoricalUsageLevel.get().longValue();
            } catch (Exception unused) {
            }
        }
        if (j <= 0) {
            return null;
        }
        return context.getString(C0017R$string.data_usage_template, DataUsageUtils.formatDataUsage(context, j), dataUsageInfo.period);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getDataUsageSummary$1 */
    public /* synthetic */ Object lambda$getDataUsageSummary$1$DataUsagePreferenceController(DataUsageController dataUsageController) throws Exception {
        return Long.valueOf(dataUsageController.getHistoricalUsageLevel(getNetworkTemplate()));
    }
}
