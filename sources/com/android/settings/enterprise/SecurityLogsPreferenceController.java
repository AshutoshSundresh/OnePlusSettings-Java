package com.android.settings.enterprise;

import android.content.Context;
import java.util.Date;

public class SecurityLogsPreferenceController extends AdminActionPreferenceControllerBase {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "security_logs";
    }

    public SecurityLogsPreferenceController(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.enterprise.AdminActionPreferenceControllerBase
    public Date getAdminActionTimestamp() {
        return this.mFeatureProvider.getLastSecurityLogRetrievalTime();
    }

    @Override // com.android.settings.enterprise.AdminActionPreferenceControllerBase, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mFeatureProvider.isSecurityLoggingEnabled() || this.mFeatureProvider.getLastSecurityLogRetrievalTime() != null;
    }
}
