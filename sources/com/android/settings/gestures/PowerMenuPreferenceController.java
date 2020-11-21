package com.android.settings.gestures;

import android.content.Context;
import android.content.IntentFilter;
import android.provider.Settings;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public class PowerMenuPreferenceController extends BasePreferenceController {
    private static final String CARDS_AVAILABLE_SETTING = "global_actions_panel_available";
    private static final String CARDS_ENABLED_SETTING = "global_actions_panel_enabled";
    private static final String CONTROLS_ENABLED_SETTING = "controls_enabled";
    private static final String KEY = "gesture_power_menu_summary";

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

    public PowerMenuPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        boolean z = false;
        boolean z2 = isControlsAvailable() && Settings.Secure.getInt(this.mContext.getContentResolver(), CONTROLS_ENABLED_SETTING, 1) == 1;
        if (isCardsAvailable() && Settings.Secure.getInt(this.mContext.getContentResolver(), CARDS_ENABLED_SETTING, 0) == 1) {
            z = true;
        }
        if (z2 && z) {
            return this.mContext.getText(C0017R$string.power_menu_cards_passes_device_controls);
        }
        if (z2) {
            return this.mContext.getText(C0017R$string.power_menu_device_controls);
        }
        if (z) {
            return this.mContext.getText(C0017R$string.power_menu_cards_passes);
        }
        return this.mContext.getText(C0017R$string.power_menu_none);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (isCardsAvailable() || isControlsAvailable()) ? 0 : 2;
    }

    private boolean isControlsAvailable() {
        return this.mContext.getPackageManager().hasSystemFeature("android.software.controls");
    }

    private boolean isCardsAvailable() {
        return Settings.Secure.getInt(this.mContext.getContentResolver(), CARDS_AVAILABLE_SETTING, 0) == 1;
    }
}
