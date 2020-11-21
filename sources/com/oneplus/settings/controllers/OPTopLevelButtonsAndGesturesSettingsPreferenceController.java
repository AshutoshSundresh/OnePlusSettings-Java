package com.oneplus.settings.controllers;

import android.content.Context;
import android.content.IntentFilter;
import android.icu.text.ListFormatter;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;

public class OPTopLevelButtonsAndGesturesSettingsPreferenceController extends BasePreferenceController {
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

    public OPTopLevelButtonsAndGesturesSettingsPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return !OPUtils.isGuestMode() ? 0 : 2;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        ArrayList arrayList = new ArrayList();
        if (OPUtils.isSupportSocTriState()) {
            return this.mContext.getString(C0017R$string.oneplus_top_level_buttons_and_gestures_summary);
        }
        String string = this.mContext.getString(C0017R$string.alertslider_settings);
        String lowerCase = this.mContext.getString(C0017R$string.buttons_enable_on_screen_navkeys_title).toLowerCase();
        String lowerCase2 = this.mContext.getString(C0017R$string.oneplus_quick_gestures).toLowerCase();
        arrayList.add(string);
        arrayList.add(lowerCase);
        arrayList.add(lowerCase2);
        return ListFormatter.getInstance().format(arrayList);
    }
}
