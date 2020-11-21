package com.oneplus.settings.better;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import com.android.settings.applications.AppStateBaseBridge;
import com.android.settingslib.applications.ApplicationsState;
import com.oneplus.settings.OPMemberController;
import com.oneplus.settings.SettingsBaseApplication;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReadingModeEffectSelectBridge extends AppStateBaseBridge {
    public static final ApplicationsState.AppFilter FILTER_ALL = new ApplicationsState.AppFilter() {
        /* class com.oneplus.settings.better.ReadingModeEffectSelectBridge.AnonymousClass1 */

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(ApplicationsState.AppEntry appEntry) {
            if (appEntry == null || appEntry.extraInfo == null) {
                return false;
            }
            return !ReadingModeEffectSelectBridge.packageExcludeFilter(appEntry.info.packageName);
        }
    };
    public static final ApplicationsState.AppFilter FILTER_AVAILABLE = new ApplicationsState.AppFilter() {
        /* class com.oneplus.settings.better.ReadingModeEffectSelectBridge.AnonymousClass4 */

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(ApplicationsState.AppEntry appEntry) {
            if (appEntry == null || appEntry.extraInfo == null) {
                return false;
            }
            ReadingModeEffectManager readingModeEffectManager = ReadingModeEffectSelectBridge.MMANAGER;
            if (readingModeEffectManager.getAppEffectSelectValue(appEntry.info.uid + appEntry.info.packageName) != 3 || ReadingModeEffectSelectBridge.packageExcludeFilter(appEntry.info.packageName)) {
                return false;
            }
            return true;
        }
    };
    public static final ApplicationsState.AppFilter FILTER_CHROMATIC = new ApplicationsState.AppFilter() {
        /* class com.oneplus.settings.better.ReadingModeEffectSelectBridge.AnonymousClass2 */

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
            ReadingModeEffectSelectBridge.access$102(ReadingModeEffectSelectBridge.getLauncherApp());
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(ApplicationsState.AppEntry appEntry) {
            if (appEntry == null || appEntry.extraInfo == null) {
                return false;
            }
            ReadingModeEffectManager readingModeEffectManager = ReadingModeEffectSelectBridge.MMANAGER;
            if (readingModeEffectManager.getAppEffectSelectValue(appEntry.info.uid + appEntry.info.packageName) != 2 || ReadingModeEffectSelectBridge.packageExcludeFilter(appEntry.info.packageName)) {
                return false;
            }
            return true;
        }
    };
    public static final ApplicationsState.AppFilter FILTER_MONO = new ApplicationsState.AppFilter() {
        /* class com.oneplus.settings.better.ReadingModeEffectSelectBridge.AnonymousClass3 */

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(ApplicationsState.AppEntry appEntry) {
            if (appEntry == null || appEntry.extraInfo == null) {
                return false;
            }
            ReadingModeEffectManager readingModeEffectManager = ReadingModeEffectSelectBridge.MMANAGER;
            if (readingModeEffectManager.getAppEffectSelectValue(appEntry.info.uid + appEntry.info.packageName) != 0 || ReadingModeEffectSelectBridge.packageExcludeFilter(appEntry.info.packageName)) {
                return false;
            }
            return true;
        }
    };
    private static final ReadingModeEffectManager MMANAGER = ReadingModeEffectManager.getInstance(SettingsBaseApplication.mApplication);
    private final Context mContext;
    private final PackageManager mPm;

    static /* synthetic */ List access$102(List list) {
        return list;
    }

    public ReadingModeEffectSelectBridge(Context context, ApplicationsState applicationsState, AppStateBaseBridge.Callback callback) {
        super(applicationsState, callback);
        this.mContext = context;
        this.mPm = context.getPackageManager();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.AppStateBaseBridge
    public void loadAllExtraInfo() {
        ArrayList<ApplicationsState.AppEntry> allApps = this.mAppSession.getAllApps();
        int size = allApps.size();
        Map<String, OPAppModel> loadAppMap = MMANAGER.loadAppMap();
        if (loadAppMap != null) {
            for (int i = 0; i < size; i++) {
                ApplicationsState.AppEntry appEntry = allApps.get(i);
                appEntry.extraInfo = loadAppMap.get(appEntry.info.packageName);
            }
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.AppStateBaseBridge
    public void updateExtraInfo(ApplicationsState.AppEntry appEntry, String str, int i) {
        try {
            appEntry.extraInfo = new OPAppModel(str, this.mPm.getApplicationInfo(str, 0).loadLabel(this.mPm).toString(), "", this.mPm.getApplicationInfo(str, 0).uid, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: private */
    public static List<ResolveInfo> getLauncherApp() {
        Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
        intent.addCategory("android.intent.category.LAUNCHER");
        return SettingsBaseApplication.mApplication.getPackageManager().queryIntentActivities(intent, 0);
    }

    /* access modifiers changed from: private */
    public static boolean packageExcludeFilter(String str) {
        return "com.oneplus.deskclock".equals(str) || "com.oneplus.market".equals(str) || "com.heytap.market".equals(str) || OPMemberController.PACKAGE_NAME.equals(str) || "com.google.android.googlequicksearchbox".equals(str) || "com.android.dialer".equals(str) || "com.oneplus.contacts".equals(str) || "com.oneplus.weather".equals(str) || "net.oneplus.weather".equals(str) || "com.google.android.calendar".equals(str) || "com.oneplus.calendar".equals(str) || "com.oneplus.gallery".equals(str) || "com.oneplus.filemanager".equals(str) || "com.oneplus.calculator".equals(str) || "com.oneplus.card".equals(str) || "com.oneplus.soundrecorder".equals(str) || "com.oneplus.camera".equals(str) || "com.google.android.youtube".equals(str) || "com.netflix.mediaclient".equals(str) || "com.amazon.avod.thirdpartyclient".equals(str) || "com.oneplus.dialer".equals(str) || "com.android.gallery3d".equals(str) || "com.google.android.dialer".equals(str) || "com.google.android.contacts".equals(str);
    }
}
