package com.android.settings.applications;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.telephony.SmsManager;
import com.android.settings.applications.AppStateBaseBridge;
import com.android.settingslib.applications.ApplicationsState;
import java.util.ArrayList;

public class AppStateSmsPremBridge extends AppStateBaseBridge {
    public static final ApplicationsState.AppFilter FILTER_APP_PREMIUM_SMS = new ApplicationsState.AppFilter() {
        /* class com.android.settings.applications.AppStateSmsPremBridge.AnonymousClass1 */

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(ApplicationsState.AppEntry appEntry) {
            Object obj = appEntry.extraInfo;
            return (obj instanceof SmsState) && ((SmsState) obj).smsState != 0;
        }
    };
    private final SmsManager mSmsManager = SmsManager.getDefault();

    public static class SmsState {
        public int smsState;
    }

    public AppStateSmsPremBridge(Context context, ApplicationsState applicationsState, AppStateBaseBridge.Callback callback) {
        super(applicationsState, callback);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.AppStateBaseBridge
    public void loadAllExtraInfo() {
        ArrayList<ApplicationsState.AppEntry> allApps = this.mAppSession.getAllApps();
        int size = allApps.size();
        for (int i = 0; i < size; i++) {
            ApplicationsState.AppEntry appEntry = allApps.get(i);
            ApplicationInfo applicationInfo = appEntry.info;
            updateExtraInfo(appEntry, applicationInfo.packageName, applicationInfo.uid);
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.AppStateBaseBridge
    public void updateExtraInfo(ApplicationsState.AppEntry appEntry, String str, int i) {
        appEntry.extraInfo = getState(str);
    }

    public SmsState getState(String str) {
        SmsState smsState = new SmsState();
        smsState.smsState = getSmsState(str);
        return smsState;
    }

    private int getSmsState(String str) {
        return this.mSmsManager.getPremiumSmsConsent(str);
    }

    public void setSmsState(String str, int i) {
        this.mSmsManager.setPremiumSmsConsent(str, i);
    }
}
