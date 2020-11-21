package com.android.settings.gestures;

import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.provider.Settings;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.SliceBackgroundWorker;

public class PowerMenuPrivacyPreferenceController extends TogglePreferenceController {
    private static final String CARDS_AVAILABLE_KEY = "global_actions_panel_available";
    private static final String CARDS_ENABLED_KEY = "global_actions_panel_enabled";
    private static final String CONTROLS_ENABLED_KEY = "controls_enabled";
    private static final String SETTING_KEY = "power_menu_locked_show_content";

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public PowerMenuPrivacyPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return Settings.Secure.getInt(this.mContext.getContentResolver(), SETTING_KEY, 0) != 0;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        return Settings.Secure.putInt(this.mContext.getContentResolver(), SETTING_KEY, z ? 1 : 0);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        int i;
        boolean z = false;
        if (Settings.Secure.getInt(this.mContext.getContentResolver(), CARDS_AVAILABLE_KEY, 0) != 0) {
            z = true;
        }
        boolean isControlsAvailable = isControlsAvailable();
        if (!isSecure()) {
            i = C0017R$string.power_menu_privacy_not_secure;
        } else if (z && isControlsAvailable) {
            i = C0017R$string.power_menu_privacy_show;
        } else if (!z && isControlsAvailable) {
            i = C0017R$string.power_menu_privacy_show_controls;
        } else if (!z) {
            return "";
        } else {
            i = C0017R$string.power_menu_privacy_show_cards;
        }
        return this.mContext.getText(i);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (!isEnabled() || !isSecure()) ? 5 : 0;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.TogglePreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        preference.setEnabled(getAvailabilityStatus() != 5);
        refreshSummary(preference);
    }

    private boolean isEnabled() {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        boolean z = Settings.Secure.getInt(contentResolver, CARDS_AVAILABLE_KEY, 0) != 0;
        boolean z2 = Settings.Secure.getInt(contentResolver, CARDS_ENABLED_KEY, 0) != 0;
        boolean z3 = Settings.Secure.getInt(contentResolver, CONTROLS_ENABLED_KEY, 1) != 0;
        if ((!z || !z2) && (!isControlsAvailable() || !z3)) {
            return false;
        }
        return true;
    }

    private boolean isSecure() {
        return FeatureFactory.getFactory(this.mContext).getSecurityFeatureProvider().getLockPatternUtils(this.mContext).isSecure(UserHandle.myUserId());
    }

    private boolean isControlsAvailable() {
        return this.mContext.getPackageManager().hasSystemFeature("android.software.controls");
    }
}
