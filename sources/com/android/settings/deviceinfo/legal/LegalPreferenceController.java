package com.android.settings.deviceinfo.legal;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import java.util.List;

public abstract class LegalPreferenceController extends BasePreferenceController {
    private final PackageManager mPackageManager = this.mContext.getPackageManager();
    private Preference mPreference;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    /* access modifiers changed from: protected */
    public abstract Intent getIntent();

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

    public LegalPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return findMatchingSpecificActivity() != null ? 0 : 3;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
        super.displayPreference(preferenceScreen);
        if (getAvailabilityStatus() == 0) {
            replacePreferenceIntent();
        }
    }

    private ResolveInfo findMatchingSpecificActivity() {
        List<ResolveInfo> queryIntentActivities;
        Intent intent = getIntent();
        if (intent == null || (queryIntentActivities = this.mPackageManager.queryIntentActivities(intent, 0)) == null) {
            return null;
        }
        for (ResolveInfo resolveInfo : queryIntentActivities) {
            if ((resolveInfo.activityInfo.applicationInfo.flags & 1) != 0) {
                return resolveInfo;
            }
        }
        return null;
    }

    private void replacePreferenceIntent() {
        ResolveInfo findMatchingSpecificActivity = findMatchingSpecificActivity();
        if (findMatchingSpecificActivity != null) {
            Preference preference = this.mPreference;
            Intent intent = new Intent();
            ActivityInfo activityInfo = findMatchingSpecificActivity.activityInfo;
            preference.setIntent(intent.setClassName(activityInfo.packageName, activityInfo.name));
            this.mPreference.setTitle(findMatchingSpecificActivity.loadLabel(this.mPackageManager));
        }
    }
}
