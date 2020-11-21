package com.android.settings.datetime.timezone;

import android.content.Context;
import android.content.IntentFilter;
import com.android.settings.C0017R$string;
import com.android.settings.slices.SliceBackgroundWorker;

public class FixedOffsetPreferenceController extends BaseTimeZonePreferenceController {
    private static final String PREFERENCE_KEY = "fixed_offset";
    private TimeZoneInfo mTimeZoneInfo;

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

    public FixedOffsetPreferenceController(Context context) {
        super(context, PREFERENCE_KEY);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        TimeZoneInfo timeZoneInfo = this.mTimeZoneInfo;
        if (timeZoneInfo == null) {
            return "";
        }
        String standardName = timeZoneInfo.getStandardName();
        if (standardName == null) {
            return this.mTimeZoneInfo.getGmtOffset();
        }
        return SpannableUtil.getResourcesText(this.mContext.getResources(), C0017R$string.zone_info_offset_and_name, this.mTimeZoneInfo.getGmtOffset(), standardName);
    }

    public void setTimeZoneInfo(TimeZoneInfo timeZoneInfo) {
        this.mTimeZoneInfo = timeZoneInfo;
    }

    public TimeZoneInfo getTimeZoneInfo() {
        return this.mTimeZoneInfo;
    }
}
