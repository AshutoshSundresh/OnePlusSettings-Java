package com.oneplus.settings.support;

import android.app.Activity;
import android.content.Intent;
import com.android.settings.overlay.SupportFeatureProvider;
import com.oneplus.settings.utils.OPUtils;

public class SupportFeatureProviderImpl implements SupportFeatureProvider {
    @Override // com.android.settings.overlay.SupportFeatureProvider
    public void startSupport(Activity activity) {
        Intent intent = new Intent();
        intent.setClassName("com.oneplus.wifiapsettings", "com.oneplus.wifiapsettings.assistance.OPUserAssistance");
        try {
            activity.startActivity(intent);
            OPUtils.sendAnalytics("help_click", "click", "1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
