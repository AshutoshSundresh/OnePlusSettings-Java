package com.android.settings.applications.specialaccess.interactacrossprofiles;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.CrossProfileApps;
import androidx.preference.Preference;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase;
import com.android.settings.slices.SliceBackgroundWorker;

public class InteractAcrossProfilesDetailsPreferenceController extends AppInfoPreferenceControllerBase {
    private String mPackageName;

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

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public InteractAcrossProfilesDetailsPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase
    public int getAvailabilityStatus() {
        return canUserAttemptToConfigureInteractAcrossProfiles() ? 0 : 4;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        preference.setSummary(getPreferenceSummary());
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase
    public Class<? extends SettingsPreferenceFragment> getDetailFragmentClass() {
        return InteractAcrossProfilesDetails.class;
    }

    private CharSequence getPreferenceSummary() {
        return InteractAcrossProfilesDetails.getPreferenceSummary(this.mContext, this.mPackageName);
    }

    private boolean canUserAttemptToConfigureInteractAcrossProfiles() {
        return ((CrossProfileApps) this.mContext.getSystemService(CrossProfileApps.class)).canUserAttemptToConfigureInteractAcrossProfiles(this.mPackageName);
    }

    public void setPackageName(String str) {
        this.mPackageName = str;
    }
}
