package com.android.settings.security;

import android.content.Context;
import android.content.IntentFilter;
import android.provider.Settings;
import androidx.preference.Preference;
import com.android.settings.C0005R$bool;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public class ScreenPinningPreferenceController extends BasePreferenceController {
    private static final String KEY_SCREEN_PINNING = "screen_pinning_settings";

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

    public ScreenPinningPreferenceController(Context context) {
        super(context, KEY_SCREEN_PINNING);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mContext.getResources().getBoolean(C0005R$bool.config_show_screen_pinning_settings) ? 0 : 3;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (preference == null) {
            return;
        }
        if (isEdgeToEdgeEnabled(this.mContext)) {
            preference.setEnabled(false);
            preference.setSummary(this.mContext.getResources().getString(C0017R$string.oneplus_fullscreen_disable_this_feature));
            return;
        }
        preference.setSummary(getSummary());
        preference.setEnabled(true);
    }

    static boolean isEdgeToEdgeEnabled(Context context) {
        return 2 == context.getResources().getInteger(17694854);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        if (Settings.System.getInt(this.mContext.getContentResolver(), "lock_to_app_enabled", 0) != 0) {
            return this.mContext.getText(C0017R$string.switch_on_text);
        }
        return this.mContext.getText(C0017R$string.switch_off_text);
    }
}
