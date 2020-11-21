package com.android.settings.enterprise;

import android.content.Context;
import java.util.Date;

public class NetworkLogsPreferenceController extends AdminActionPreferenceControllerBase {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "network_logs";
    }

    public NetworkLogsPreferenceController(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.enterprise.AdminActionPreferenceControllerBase
    public Date getAdminActionTimestamp() {
        return this.mFeatureProvider.getLastNetworkLogRetrievalTime();
    }

    @Override // com.android.settings.enterprise.AdminActionPreferenceControllerBase, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mFeatureProvider.isNetworkLoggingEnabled() || this.mFeatureProvider.getLastNetworkLogRetrievalTime() != null;
    }
}
