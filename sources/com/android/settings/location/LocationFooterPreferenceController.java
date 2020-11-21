package com.android.settings.location;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.widget.FooterPreference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LocationFooterPreferenceController extends LocationBasePreferenceController {
    private static final Intent INJECT_INTENT = new Intent("com.android.settings.location.DISPLAYED_FOOTER");
    private static final String TAG = "LocationFooter";
    private final PackageManager mPackageManager;

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.location.LocationEnabler.LocationModeChangeListener, com.android.settings.location.LocationBasePreferenceController
    public void onLocationModeChanged(int i, boolean z) {
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public LocationFooterPreferenceController(Context context, String str) {
        super(context, str);
        this.mPackageManager = context.getPackageManager();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        PreferenceCategory preferenceCategory = (PreferenceCategory) preference;
        preferenceCategory.removeAll();
        for (FooterData footerData : getFooterData()) {
            try {
                String string = this.mPackageManager.getResourcesForApplication(footerData.applicationInfo).getString(footerData.footerStringRes);
                FooterPreference footerPreference = new FooterPreference(preference.getContext());
                footerPreference.setTitle(string);
                preferenceCategory.addPreference(footerPreference);
            } catch (PackageManager.NameNotFoundException unused) {
                Log.w(TAG, "Resources not found for application " + footerData.applicationInfo.packageName);
            }
        }
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settings.location.LocationBasePreferenceController
    public int getAvailabilityStatus() {
        return !getFooterData().isEmpty() ? 0 : 3;
    }

    private List<FooterData> getFooterData() {
        List<ResolveInfo> queryBroadcastReceivers = this.mPackageManager.queryBroadcastReceivers(INJECT_INTENT, 128);
        if (queryBroadcastReceivers == null) {
            Log.e(TAG, "Unable to resolve intent " + INJECT_INTENT);
            return Collections.emptyList();
        }
        if (Log.isLoggable(TAG, 3)) {
            Log.d(TAG, "Found broadcast receivers: " + queryBroadcastReceivers);
        }
        ArrayList arrayList = new ArrayList(queryBroadcastReceivers.size());
        for (ResolveInfo resolveInfo : queryBroadcastReceivers) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            ApplicationInfo applicationInfo = activityInfo.applicationInfo;
            if ((applicationInfo.flags & 1) == 0) {
                Log.w(TAG, "Ignoring attempt to inject footer from app not in system image: " + resolveInfo);
            } else {
                Bundle bundle = activityInfo.metaData;
                if (bundle != null) {
                    int i = bundle.getInt("com.android.settings.location.FOOTER_STRING");
                    if (i == 0) {
                        Log.w(TAG, "No mapping of integer exists for com.android.settings.location.FOOTER_STRING");
                    } else {
                        arrayList.add(new FooterData(i, applicationInfo));
                    }
                } else if (Log.isLoggable(TAG, 3)) {
                    Log.d(TAG, "No METADATA in broadcast receiver " + activityInfo.name);
                }
            }
        }
        return arrayList;
    }

    /* access modifiers changed from: private */
    public static class FooterData {
        final ApplicationInfo applicationInfo;
        final int footerStringRes;

        FooterData(int i, ApplicationInfo applicationInfo2) {
            this.footerStringRes = i;
            this.applicationInfo = applicationInfo2;
        }
    }
}
