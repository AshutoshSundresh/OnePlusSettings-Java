package com.android.settings.datetime.timezone;

import android.content.Context;
import android.content.IntentFilter;
import android.icu.text.LocaleDisplayNames;
import com.android.settings.slices.SliceBackgroundWorker;

public class RegionPreferenceController extends BaseTimeZonePreferenceController {
    private static final String PREFERENCE_KEY = "region";
    private final LocaleDisplayNames mLocaleDisplayNames;
    private String mRegionId = "";

    @Override // com.android.settings.datetime.timezone.BaseTimeZonePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.datetime.timezone.BaseTimeZonePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.datetime.timezone.BaseTimeZonePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.datetime.timezone.BaseTimeZonePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.datetime.timezone.BaseTimeZonePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.datetime.timezone.BaseTimeZonePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.datetime.timezone.BaseTimeZonePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.datetime.timezone.BaseTimeZonePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public RegionPreferenceController(Context context) {
        super(context, PREFERENCE_KEY);
        this.mLocaleDisplayNames = LocaleDisplayNames.getInstance(context.getResources().getConfiguration().getLocales().get(0));
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return this.mLocaleDisplayNames.regionDisplayName(this.mRegionId);
    }

    public void setRegionId(String str) {
        this.mRegionId = str;
    }

    public String getRegionId() {
        return this.mRegionId;
    }
}
