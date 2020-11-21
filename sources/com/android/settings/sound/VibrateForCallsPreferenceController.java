package com.android.settings.sound;

import android.content.Context;
import android.content.IntentFilter;
import android.provider.DeviceConfig;
import android.provider.Settings;
import com.android.settings.C0017R$string;
import com.android.settings.Utils;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public class VibrateForCallsPreferenceController extends BasePreferenceController {
    private static final int OFF = 0;
    private static final int ON = 1;
    static final String RAMPING_RINGER_ENABLED = "ramping_ringer_enabled";

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

    public VibrateForCallsPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (!Utils.isVoiceCapable(this.mContext) || DeviceConfig.getBoolean("telephony", RAMPING_RINGER_ENABLED, false)) ? 3 : 0;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        if (Settings.Global.getInt(this.mContext.getContentResolver(), "apply_ramping_ringer", 0) == 1) {
            return this.mContext.getText(C0017R$string.vibrate_when_ringing_option_ramping_ringer);
        }
        if (Settings.System.getInt(this.mContext.getContentResolver(), "vibrate_when_ringing", 0) == 1) {
            return this.mContext.getText(C0017R$string.vibrate_when_ringing_option_always_vibrate);
        }
        return this.mContext.getText(C0017R$string.vibrate_when_ringing_option_never_vibrate);
    }
}
