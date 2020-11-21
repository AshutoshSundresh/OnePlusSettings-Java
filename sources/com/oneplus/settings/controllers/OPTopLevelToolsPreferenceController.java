package com.oneplus.settings.controllers;

import android.content.Context;
import android.content.IntentFilter;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.oneplus.settings.utils.OPUtils;

public class OPTopLevelToolsPreferenceController extends BasePreferenceController {
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

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public OPTopLevelToolsPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        if (OPUtils.isGuestMode()) {
            if (OPUtils.isAppExist(this.mContext, "com.oneplus.gamespace")) {
                return "";
            }
            return this.mContext.getString(C0017R$string.oneplus_gaming_mode);
        } else if (OPUtils.isSupportQuickLaunch()) {
            if (OPUtils.isAppExist(this.mContext, "com.oneplus.gamespace")) {
                return this.mContext.getString(C0017R$string.oneplus_tools_quicklaunch_summary_no_gamingmode);
            }
            return this.mContext.getString(C0017R$string.oneplus_tools_quicklaunch_summary);
        } else if (OPUtils.isAppExist(this.mContext, "com.oneplus.gamespace")) {
            return this.mContext.getString(C0017R$string.oneplus_tools_summary_no_gamingmode);
        } else {
            return this.mContext.getString(C0017R$string.oneplus_tools_summary);
        }
    }
}
