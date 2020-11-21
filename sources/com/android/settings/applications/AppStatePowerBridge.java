package com.android.settings.applications;

import android.content.Context;
import com.android.settings.applications.AppStateBaseBridge;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.fuelgauge.PowerWhitelistBackend;
import java.util.ArrayList;

public class AppStatePowerBridge extends AppStateBaseBridge {
    public static final ApplicationsState.AppFilter FILTER_POWER_WHITELISTED = new ApplicationsState.CompoundFilter(ApplicationsState.FILTER_WITHOUT_DISABLED_UNTIL_USED, new ApplicationsState.AppFilter() {
        /* class com.android.settings.applications.AppStatePowerBridge.AnonymousClass1 */

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(ApplicationsState.AppEntry appEntry) {
            return appEntry.extraInfo == Boolean.TRUE;
        }
    });
    private final PowerWhitelistBackend mBackend;

    public AppStatePowerBridge(Context context, ApplicationsState applicationsState, AppStateBaseBridge.Callback callback) {
        super(applicationsState, callback);
        this.mBackend = PowerWhitelistBackend.getInstance(context);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.AppStateBaseBridge
    public void loadAllExtraInfo() {
        ArrayList<ApplicationsState.AppEntry> allApps = this.mAppSession.getAllApps();
        int size = allApps.size();
        for (int i = 0; i < size; i++) {
            ApplicationsState.AppEntry appEntry = allApps.get(i);
            appEntry.extraInfo = this.mBackend.isWhitelisted(appEntry.info.packageName) ? Boolean.TRUE : Boolean.FALSE;
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.AppStateBaseBridge
    public void updateExtraInfo(ApplicationsState.AppEntry appEntry, String str, int i) {
        appEntry.extraInfo = this.mBackend.isWhitelisted(str) ? Boolean.TRUE : Boolean.FALSE;
    }
}
