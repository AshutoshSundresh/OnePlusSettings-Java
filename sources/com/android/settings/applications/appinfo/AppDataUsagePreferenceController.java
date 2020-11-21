package com.android.settings.applications.appinfo;

import android.content.Context;
import android.content.IntentFilter;
import android.net.NetworkTemplate;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.text.format.Formatter;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.datausage.AppDataUsage;
import com.android.settings.datausage.DataUsageUtils;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.AppItem;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.net.NetworkCycleDataForUid;
import com.android.settingslib.net.NetworkCycleDataForUidLoader;
import java.util.List;

public class AppDataUsagePreferenceController extends AppInfoPreferenceControllerBase implements LoaderManager.LoaderCallbacks<List<NetworkCycleDataForUid>>, LifecycleObserver, OnResume, OnPause {
    private List<NetworkCycleDataForUid> mAppUsageData;

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public void onLoaderReset(Loader<List<NetworkCycleDataForUid>> loader) {
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AppDataUsagePreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase
    public int getAvailabilityStatus() {
        return isBandwidthControlEnabled() ? 0 : 2;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController, com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        preference.setSummary(getDataSummary());
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        if (isAvailable()) {
            int i = this.mParent.getAppEntry().info.uid;
            new AppItem(i).addUid(i);
            this.mParent.getLoaderManager().restartLoader(2, null, this);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        if (isAvailable()) {
            this.mParent.getLoaderManager().destroyLoader(2);
        }
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public Loader<List<NetworkCycleDataForUid>> onCreateLoader(int i, Bundle bundle) {
        NetworkTemplate template = getTemplate(this.mContext);
        NetworkCycleDataForUidLoader.Builder<?> builder = NetworkCycleDataForUidLoader.builder(this.mContext);
        builder.addUid(this.mParent.getAppEntry().info.uid);
        builder.setRetrieveDetail(false);
        builder.setNetworkTemplate(template);
        return builder.build();
    }

    public void onLoadFinished(Loader<List<NetworkCycleDataForUid>> loader, List<NetworkCycleDataForUid> list) {
        this.mAppUsageData = list;
        updateState(this.mPreference);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase
    public Class<? extends SettingsPreferenceFragment> getDetailFragmentClass() {
        return AppDataUsage.class;
    }

    private CharSequence getDataSummary() {
        if (this.mAppUsageData == null) {
            return this.mContext.getString(C0017R$string.computing_size);
        }
        long currentTimeMillis = System.currentTimeMillis();
        long j = 0;
        for (NetworkCycleDataForUid networkCycleDataForUid : this.mAppUsageData) {
            j += networkCycleDataForUid.getTotalUsage();
            long startTime = networkCycleDataForUid.getStartTime();
            if (startTime < currentTimeMillis) {
                currentTimeMillis = startTime;
            }
        }
        if (j == 0) {
            return this.mContext.getString(C0017R$string.no_data_usage);
        }
        Context context = this.mContext;
        return context.getString(C0017R$string.data_summary_format, Formatter.formatFileSize(context, j, 8), DateUtils.formatDateTime(this.mContext, currentTimeMillis, 65552));
    }

    private static NetworkTemplate getTemplate(Context context) {
        if (DataUsageUtils.hasReadyMobileRadio(context)) {
            return NetworkTemplate.buildTemplateMobileWildcard();
        }
        if (DataUsageUtils.hasWifiRadio(context)) {
            return NetworkTemplate.buildTemplateWifiWildcard();
        }
        return NetworkTemplate.buildTemplateEthernet();
    }

    /* access modifiers changed from: package-private */
    public boolean isBandwidthControlEnabled() {
        return Utils.isBandwidthControlEnabled();
    }
}
