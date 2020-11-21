package com.android.settings.deviceinfo.firmwareversion;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.text.BidiFormatter;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.oneplus.settings.utils.OPUtils;

public class SimpleBuildNumberPreferenceController extends BasePreferenceController {
    private Preference mOSBuildNumber;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 1;
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

    public SimpleBuildNumberPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mOSBuildNumber = preferenceScreen.findPreference("os_build_number");
        if (OPUtils.isO2()) {
            this.mOSBuildNumber.setTitle(C0017R$string.build_number);
        } else {
            this.mOSBuildNumber.setTitle(C0017R$string.op_build_number);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        if (OPUtils.isO2()) {
            return BidiFormatter.getInstance().unicodeWrap(Build.DISPLAY);
        }
        String str = Build.DISPLAY;
        if (TextUtils.isEmpty(str)) {
            return BidiFormatter.getInstance().unicodeWrap(Build.DISPLAY);
        }
        String[] split = str.split("_");
        split[1] = "_15_";
        return split[0] + split[1] + split[2];
    }
}
