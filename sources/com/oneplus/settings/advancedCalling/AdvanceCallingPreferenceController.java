package com.oneplus.settings.advancedCalling;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.oneplus.settings.utils.OPUtils;
import com.oneplus.settings.utils.ProductUtils;

public class AdvanceCallingPreferenceController extends BasePreferenceController {
    private static final String KEY_ACTIVATE_ADVANCED_CALLING = "activate_advanced_calling";
    private static final String PACKAGE_NAME_ADVANCED_CALLING = "com.oneplus.vzw.advancedcalling";

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

    public AdvanceCallingPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return ProductUtils.isUsvMode() ? 0 : 3;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return this.mContext.getString(C0017R$string.advanced_calling_summary);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!KEY_ACTIVATE_ADVANCED_CALLING.equals(preference.getKey())) {
            return false;
        }
        try {
            if (!OPUtils.isAppExist(this.mContext, PACKAGE_NAME_ADVANCED_CALLING)) {
                return true;
            }
            Intent intent = new Intent("INTENT.ADVANCEDCALLING");
            intent.setClassName(PACKAGE_NAME_ADVANCED_CALLING, "com.oneplus.vzw.advancedcalling.AdvancedCallingActivity");
            this.mContext.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return true;
        }
    }
}
