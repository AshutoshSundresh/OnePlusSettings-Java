package com.android.settings.applications.managedomainurls;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.IconDrawableFactory;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.applications.AppInfoBase;
import com.android.settings.applications.AppLaunchSettings;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.applications.ApplicationsState;
import java.util.ArrayList;
import java.util.Map;

public class DomainAppPreferenceController extends BasePreferenceController implements ApplicationsState.Callbacks {
    private static final int INSTALLED_APP_DETAILS = 1;
    private ApplicationsState mApplicationsState = ApplicationsState.getInstance((Application) this.mContext.getApplicationContext());
    private PreferenceGroup mDomainAppList;
    private ManageDomainUrls mFragment;
    private int mMetricsCategory;
    private Map<String, Preference> mPreferenceCache;
    private ApplicationsState.Session mSession;

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

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onAllSizesComputed() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onLauncherInfoChanged() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onPackageIconChanged() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onPackageListChanged() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onPackageSizeChanged(String str) {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onRunningStateChanged(boolean z) {
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public DomainAppPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mDomainAppList = (PreferenceGroup) preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!(preference instanceof DomainAppPreference)) {
            return false;
        }
        ApplicationsState.AppEntry entry = ((DomainAppPreference) preference).getEntry();
        int i = C0017R$string.auto_launch_label;
        ApplicationInfo applicationInfo = entry.info;
        AppInfoBase.startAppInfoFragment(AppLaunchSettings.class, i, applicationInfo.packageName, applicationInfo.uid, this.mFragment, 1, this.mMetricsCategory);
        return true;
    }

    public void setFragment(ManageDomainUrls manageDomainUrls) {
        this.mFragment = manageDomainUrls;
        this.mMetricsCategory = manageDomainUrls.getMetricsCategory();
        this.mSession = this.mApplicationsState.newSession(this, this.mFragment.getSettingsLifecycle());
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> arrayList) {
        if (this.mContext != null) {
            rebuildAppList(this.mDomainAppList, arrayList);
        }
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onLoadEntriesCompleted() {
        rebuild();
    }

    private void cacheAllPrefs(PreferenceGroup preferenceGroup) {
        this.mPreferenceCache = new ArrayMap();
        int preferenceCount = preferenceGroup.getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            Preference preference = preferenceGroup.getPreference(i);
            if (!TextUtils.isEmpty(preference.getKey())) {
                this.mPreferenceCache.put(preference.getKey(), preference);
            }
        }
    }

    private Preference getCachedPreference(String str) {
        Map<String, Preference> map = this.mPreferenceCache;
        if (map != null) {
            return map.remove(str);
        }
        return null;
    }

    private void removeCachedPrefs(PreferenceGroup preferenceGroup) {
        for (Preference preference : this.mPreferenceCache.values()) {
            preferenceGroup.removePreference(preference);
        }
        this.mPreferenceCache = null;
    }

    private void rebuild() {
        ArrayList<ApplicationsState.AppEntry> rebuild = this.mSession.rebuild(ApplicationsState.FILTER_WITH_DOMAIN_URLS, ApplicationsState.ALPHA_COMPARATOR);
        if (rebuild != null) {
            onRebuildComplete(rebuild);
        }
    }

    private void rebuildAppList(PreferenceGroup preferenceGroup, ArrayList<ApplicationsState.AppEntry> arrayList) {
        cacheAllPrefs(preferenceGroup);
        int size = arrayList.size();
        Context context = preferenceGroup.getContext();
        IconDrawableFactory newInstance = IconDrawableFactory.newInstance(context);
        for (int i = 0; i < size; i++) {
            ApplicationsState.AppEntry appEntry = arrayList.get(i);
            String str = appEntry.info.packageName + "|" + appEntry.info.uid;
            DomainAppPreference domainAppPreference = (DomainAppPreference) getCachedPreference(str);
            if (domainAppPreference == null) {
                domainAppPreference = new DomainAppPreference(context, newInstance, appEntry);
                domainAppPreference.setKey(str);
                preferenceGroup.addPreference(domainAppPreference);
            } else {
                domainAppPreference.reuse();
            }
            domainAppPreference.setOrder(i);
        }
        removeCachedPrefs(preferenceGroup);
    }
}
