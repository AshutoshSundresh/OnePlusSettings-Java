package com.android.settings.deviceinfo.hardwareinfo;

import android.content.Context;
import android.content.IntentFilter;
import com.android.settings.deviceinfo.HardwareInfoPreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public class DeviceModelPreferenceController extends HardwareInfoPreferenceController {
    @Override // com.android.settings.deviceinfo.HardwareInfoPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.deviceinfo.HardwareInfoPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.deviceinfo.HardwareInfoPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.deviceinfo.HardwareInfoPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.deviceinfo.HardwareInfoPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.deviceinfo.HardwareInfoPreferenceController, com.android.settings.slices.Sliceable
    public boolean isPublicSlice() {
        return true;
    }

    @Override // com.android.settings.deviceinfo.HardwareInfoPreferenceController, com.android.settings.slices.Sliceable
    public boolean isSliceable() {
        return true;
    }

    @Override // com.android.settings.deviceinfo.HardwareInfoPreferenceController, com.android.settings.slices.Sliceable
    public boolean useDynamicSliceSummary() {
        return true;
    }

    public DeviceModelPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.deviceinfo.HardwareInfoPreferenceController, com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        int availabilityStatus = super.getAvailabilityStatus();
        if (availabilityStatus == 1) {
            return 0;
        }
        return availabilityStatus;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.deviceinfo.HardwareInfoPreferenceController
    public CharSequence getSummary() {
        return HardwareInfoPreferenceController.getDeviceModel();
    }
}
