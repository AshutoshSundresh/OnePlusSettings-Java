package com.android.settings.notification.zen;

import android.content.Context;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.notification.zen.ZenModeSettings;
import com.android.settingslib.core.lifecycle.Lifecycle;

public class ZenModeBlockedEffectsPreferenceController extends AbstractZenModePreferenceController implements PreferenceControllerMixin {
    private final ZenModeSettings.SummaryBuilder mSummaryBuilder;

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenModePreferenceController
    public String getPreferenceKey() {
        return "zen_mode_block_effects_settings";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public ZenModeBlockedEffectsPreferenceController(Context context, Lifecycle lifecycle) {
        super(context, "zen_mode_block_effects_settings", lifecycle);
        this.mSummaryBuilder = new ZenModeSettings.SummaryBuilder(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return this.mSummaryBuilder.getBlockedEffectsSummary(getPolicy());
    }
}
