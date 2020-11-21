package com.android.settings.applications;

import android.app.usage.UsageStats;
import android.content.Context;
import android.content.IntentFilter;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.applications.RecentAppStatsMixin;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.List;

public class AllAppsInfoPreferenceController extends BasePreferenceController implements RecentAppStatsMixin.RecentAppStatsListener {
    Preference mPreference;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AllAppsInfoPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Preference findPreference = preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = findPreference;
        findPreference.setVisible(false);
    }

    @Override // com.android.settings.applications.RecentAppStatsMixin.RecentAppStatsListener
    public void onReloadDataCompleted(List<UsageStats> list) {
        if (!list.isEmpty()) {
            this.mPreference.setVisible(false);
            return;
        }
        this.mPreference.setVisible(true);
        Context context = this.mContext;
        new InstalledAppCounter(context, -1, context.getPackageManager()) {
            /* class com.android.settings.applications.AllAppsInfoPreferenceController.AnonymousClass1 */

            /* access modifiers changed from: protected */
            @Override // com.android.settings.applications.AppCounter
            public void onCountComplete(int i) {
                AllAppsInfoPreferenceController allAppsInfoPreferenceController = AllAppsInfoPreferenceController.this;
                allAppsInfoPreferenceController.mPreference.setSummary(((AbstractPreferenceController) allAppsInfoPreferenceController).mContext.getString(C0017R$string.apps_summary, Integer.valueOf(i)));
            }
        }.execute(new Void[0]);
    }
}
