package com.android.settings.datetime.timezone;

import android.content.Context;
import android.content.IntentFilter;
import android.icu.text.DateFormat;
import android.icu.text.DisplayContext;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.icu.util.TimeZoneTransition;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import java.util.Date;

public class TimeZoneInfoPreferenceController extends BasePreferenceController {
    Date mDate = new Date();
    private final DateFormat mDateFormat;
    private TimeZoneInfo mTimeZoneInfo;

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

    public TimeZoneInfoPreferenceController(Context context, String str) {
        super(context, str);
        DateFormat dateInstance = DateFormat.getDateInstance(1);
        this.mDateFormat = dateInstance;
        dateInstance.setContext(DisplayContext.CAPITALIZATION_NONE);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mTimeZoneInfo != null ? 1 : 3;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        TimeZoneInfo timeZoneInfo = this.mTimeZoneInfo;
        return timeZoneInfo == null ? "" : formatInfo(timeZoneInfo);
    }

    public void setTimeZoneInfo(TimeZoneInfo timeZoneInfo) {
        this.mTimeZoneInfo = timeZoneInfo;
    }

    private CharSequence formatOffsetAndName(TimeZoneInfo timeZoneInfo) {
        String genericName = timeZoneInfo.getGenericName();
        if (genericName == null) {
            if (timeZoneInfo.getTimeZone().inDaylightTime(this.mDate)) {
                genericName = timeZoneInfo.getDaylightName();
            } else {
                genericName = timeZoneInfo.getStandardName();
            }
        }
        if (genericName == null) {
            return timeZoneInfo.getGmtOffset().toString();
        }
        return SpannableUtil.getResourcesText(this.mContext.getResources(), C0017R$string.zone_info_offset_and_name, timeZoneInfo.getGmtOffset(), genericName);
    }

    private CharSequence formatInfo(TimeZoneInfo timeZoneInfo) {
        CharSequence formatOffsetAndName = formatOffsetAndName(timeZoneInfo);
        TimeZone timeZone = timeZoneInfo.getTimeZone();
        if (!timeZone.observesDaylightTime()) {
            return this.mContext.getString(C0017R$string.zone_info_footer_no_dst, formatOffsetAndName);
        }
        TimeZoneTransition findNextDstTransition = findNextDstTransition(timeZone);
        if (findNextDstTransition == null) {
            return null;
        }
        boolean z = findNextDstTransition.getTo().getDSTSavings() != 0;
        String daylightName = z ? timeZoneInfo.getDaylightName() : timeZoneInfo.getStandardName();
        if (daylightName == null) {
            if (z) {
                daylightName = this.mContext.getString(C0017R$string.zone_time_type_dst);
            } else {
                daylightName = this.mContext.getString(C0017R$string.zone_time_type_standard);
            }
        }
        Calendar instance = Calendar.getInstance(timeZone);
        instance.setTimeInMillis(findNextDstTransition.getTime());
        return SpannableUtil.getResourcesText(this.mContext.getResources(), C0017R$string.zone_info_footer, formatOffsetAndName, daylightName, this.mDateFormat.format(instance));
    }

    /* JADX WARNING: Removed duplicated region for block: B:6:0x0026  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.icu.util.TimeZoneTransition findNextDstTransition(android.icu.util.TimeZone r4) {
        /*
            r3 = this;
            boolean r0 = r4 instanceof android.icu.util.BasicTimeZone
            if (r0 != 0) goto L_0x0006
            r3 = 0
            return r3
        L_0x0006:
            android.icu.util.BasicTimeZone r4 = (android.icu.util.BasicTimeZone) r4
            java.util.Date r3 = r3.mDate
            long r0 = r3.getTime()
            r3 = 0
            android.icu.util.TimeZoneTransition r0 = r4.getNextTransition(r0, r3)
        L_0x0013:
            android.icu.util.TimeZoneRule r1 = r0.getTo()
            int r1 = r1.getDSTSavings()
            android.icu.util.TimeZoneRule r2 = r0.getFrom()
            int r2 = r2.getDSTSavings()
            if (r1 == r2) goto L_0x0026
            goto L_0x0030
        L_0x0026:
            long r0 = r0.getTime()
            android.icu.util.TimeZoneTransition r0 = r4.getNextTransition(r0, r3)
            if (r0 != 0) goto L_0x0013
        L_0x0030:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.datetime.timezone.TimeZoneInfoPreferenceController.findNextDstTransition(android.icu.util.TimeZone):android.icu.util.TimeZoneTransition");
    }
}
