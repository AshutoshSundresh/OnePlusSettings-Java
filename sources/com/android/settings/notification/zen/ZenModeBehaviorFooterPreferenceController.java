package com.android.settings.notification.zen;

import android.content.ComponentName;
import android.content.Context;
import android.net.Uri;
import android.service.notification.ZenModeConfig;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settingslib.core.lifecycle.Lifecycle;

public class ZenModeBehaviorFooterPreferenceController extends AbstractZenModePreferenceController {
    private final int mTitleRes;

    private boolean isDeprecatedZenMode(int i) {
        return i == 2 || i == 3;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenModePreferenceController
    public String getPreferenceKey() {
        return "footer_preference";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public ZenModeBehaviorFooterPreferenceController(Context context, Lifecycle lifecycle, int i) {
        super(context, "footer_preference", lifecycle);
        this.mTitleRes = i;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        preference.setTitle(getFooterText());
    }

    /* access modifiers changed from: protected */
    public String getFooterText() {
        ComponentName componentName;
        if (!isDeprecatedZenMode(getZenMode())) {
            return this.mContext.getString(this.mTitleRes);
        }
        ZenModeConfig zenModeConfig = getZenModeConfig();
        ZenModeConfig.ZenRule zenRule = zenModeConfig.manualRule;
        if (zenRule != null && isDeprecatedZenMode(zenRule.zenMode)) {
            ZenModeConfig.ZenRule zenRule2 = zenModeConfig.manualRule;
            Uri uri = zenRule2.conditionId;
            String str = zenRule2.enabler;
            if (str == null) {
                return this.mContext.getString(C0017R$string.zen_mode_qs_set_behavior);
            }
            String ownerCaption = AbstractZenModePreferenceController.mZenModeConfigWrapper.getOwnerCaption(str);
            if (!ownerCaption.isEmpty()) {
                return this.mContext.getString(C0017R$string.zen_mode_app_set_behavior, ownerCaption);
            }
        }
        for (ZenModeConfig.ZenRule zenRule3 : zenModeConfig.automaticRules.values()) {
            if (zenRule3.isAutomaticActive() && isDeprecatedZenMode(zenRule3.zenMode) && (componentName = zenRule3.component) != null) {
                return this.mContext.getString(C0017R$string.zen_mode_app_set_behavior, componentName.getPackageName());
            }
        }
        return this.mContext.getString(C0017R$string.zen_mode_unknown_app_set_behavior);
    }
}
