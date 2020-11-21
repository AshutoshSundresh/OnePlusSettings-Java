package com.android.settings.applications.appinfo;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.text.format.Formatter;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0005R$bool;
import com.android.settings.C0017R$string;
import com.android.settings.SettingsActivity;
import com.android.settings.applications.ProcStatsData;
import com.android.settings.applications.ProcStatsEntry;
import com.android.settings.applications.ProcStatsPackageEntry;
import com.android.settings.applications.ProcessStatsBase;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.development.DevelopmentSettingsEnabler;
import java.util.Iterator;

public class AppMemoryPreferenceController extends BasePreferenceController implements LifecycleObserver, OnResume {
    private static final String KEY_MEMORY = "memory";
    private final AppInfoDashboardFragment mParent;
    private Preference mPreference;
    private ProcStatsPackageEntry mStats;
    private ProcStatsData mStatsManager;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
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

    private class MemoryUpdater extends AsyncTask<Void, Void, ProcStatsPackageEntry> {
        private MemoryUpdater() {
        }

        /* access modifiers changed from: protected */
        public ProcStatsPackageEntry doInBackground(Void... voidArr) {
            PackageInfo packageInfo;
            FragmentActivity activity = AppMemoryPreferenceController.this.mParent.getActivity();
            if (activity == null || (packageInfo = AppMemoryPreferenceController.this.mParent.getPackageInfo()) == null) {
                return null;
            }
            if (AppMemoryPreferenceController.this.mStatsManager == null) {
                AppMemoryPreferenceController.this.mStatsManager = new ProcStatsData(activity, false);
                AppMemoryPreferenceController.this.mStatsManager.setDuration(ProcessStatsBase.sDurations[0]);
            }
            AppMemoryPreferenceController.this.mStatsManager.refreshStats(true);
            for (ProcStatsPackageEntry procStatsPackageEntry : AppMemoryPreferenceController.this.mStatsManager.getEntries()) {
                Iterator<ProcStatsEntry> it = procStatsPackageEntry.getEntries().iterator();
                while (true) {
                    if (it.hasNext()) {
                        if (it.next().getUid() == packageInfo.applicationInfo.uid) {
                            procStatsPackageEntry.updateMetrics();
                            return procStatsPackageEntry;
                        }
                    }
                }
            }
            return null;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(ProcStatsPackageEntry procStatsPackageEntry) {
            if (AppMemoryPreferenceController.this.mParent.getActivity() != null) {
                if (procStatsPackageEntry != null) {
                    AppMemoryPreferenceController.this.mStats = procStatsPackageEntry;
                    AppMemoryPreferenceController.this.mPreference.setEnabled(true);
                    double max = Math.max(procStatsPackageEntry.getRunWeight(), procStatsPackageEntry.getBgWeight()) * AppMemoryPreferenceController.this.mStatsManager.getMemInfo().getWeightToRam();
                    AppMemoryPreferenceController.this.mPreference.setSummary(((AbstractPreferenceController) AppMemoryPreferenceController.this).mContext.getString(C0017R$string.memory_use_summary, Formatter.formatShortFileSize(((AbstractPreferenceController) AppMemoryPreferenceController.this).mContext, (long) max)));
                    return;
                }
                AppMemoryPreferenceController.this.mPreference.setEnabled(false);
                AppMemoryPreferenceController.this.mPreference.setSummary(((AbstractPreferenceController) AppMemoryPreferenceController.this).mContext.getString(C0017R$string.no_memory_use_summary));
            }
        }
    }

    public AppMemoryPreferenceController(Context context, AppInfoDashboardFragment appInfoDashboardFragment, Lifecycle lifecycle) {
        super(context, KEY_MEMORY);
        this.mParent = appInfoDashboardFragment;
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (!this.mContext.getResources().getBoolean(C0005R$bool.config_show_app_info_settings_memory)) {
            return 3;
        }
        return DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(this.mContext) ? 0 : 2;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!KEY_MEMORY.equals(preference.getKey())) {
            return false;
        }
        ProcessStatsBase.launchMemoryDetail((SettingsActivity) this.mParent.getActivity(), this.mStatsManager.getMemInfo(), this.mStats, false);
        return true;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        if (isAvailable()) {
            new MemoryUpdater().execute(new Void[0]);
        }
    }
}
