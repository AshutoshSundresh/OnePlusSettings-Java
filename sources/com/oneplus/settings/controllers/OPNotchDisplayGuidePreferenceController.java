package com.oneplus.settings.controllers;

import android.content.Context;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.oneplus.settings.utils.OPUtils;
import java.util.List;

public class OPNotchDisplayGuidePreferenceController extends BasePreferenceController {
    private static final String TAG = "OPNotchDisplayGuidePreferenceController";

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

    public OPNotchDisplayGuidePreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (!OPUtils.isSupportScreenCutting() || OPUtils.isGuestMode()) ? 3 : 0;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (OPUtils.isSupportHolePunchFrontCam()) {
            preference.setTitle(C0017R$string.oneplus_front_camera_display_title);
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public void updateNonIndexableKeys(List<String> list) {
        super.updateNonIndexableKeys(list);
        if (OPUtils.isSupportHolePunchFrontCam()) {
            String preferenceKey = getPreferenceKey();
            if (TextUtils.isEmpty(preferenceKey)) {
                Log.w(TAG, "Skipping updateNonIndexableKeys due to empty key " + toString());
            } else if (list.contains(preferenceKey)) {
                Log.w(TAG, "Skipping updateNonIndexableKeys, key already in list. " + toString());
            } else {
                list.add(preferenceKey);
            }
        }
    }
}
