package com.android.settings.enterprise;

import android.content.Context;
import java.util.Date;

public class BugReportsPreferenceController extends AdminActionPreferenceControllerBase {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "bug_reports";
    }

    public BugReportsPreferenceController(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.enterprise.AdminActionPreferenceControllerBase
    public Date getAdminActionTimestamp() {
        return this.mFeatureProvider.getLastBugReportRequestTime();
    }
}
