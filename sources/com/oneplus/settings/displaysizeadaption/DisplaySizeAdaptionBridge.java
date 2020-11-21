package com.oneplus.settings.displaysizeadaption;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.UserHandle;
import com.android.settings.applications.AppStateBaseBridge;
import com.android.settingslib.applications.ApplicationsState;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.better.OPAppModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DisplaySizeAdaptionBridge extends AppStateBaseBridge {
    private static final DisplaySizeAdaptiongeManager ADAPTION_MANAGER = DisplaySizeAdaptiongeManager.getInstance(SettingsBaseApplication.mApplication);
    public static final ApplicationsState.AppFilter FILTER_APP_ALL_SCREENS = new ApplicationsState.AppFilter() {
        /* class com.oneplus.settings.displaysizeadaption.DisplaySizeAdaptionBridge.AnonymousClass2 */

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
            List unused = DisplaySizeAdaptionBridge.resolveInfoList = DisplaySizeAdaptionBridge.getLauncherApp();
        }

        /* JADX WARNING: Code restructure failed: missing block: B:10:0x0024, code lost:
            if (com.oneplus.settings.displaysizeadaption.DisplaySizeAdaptionBridge.isLauncherApp(r0.packageName) != false) goto L_0x0030;
         */
        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean filterApp(com.android.settingslib.applications.ApplicationsState.AppEntry r4) {
            /*
                r3 = this;
                r3 = 0
                if (r4 != 0) goto L_0x0004
                return r3
            L_0x0004:
                boolean r0 = com.oneplus.settings.utils.OPUtils.isSupportScreenCutting()
                r1 = 999(0x3e7, float:1.4E-42)
                r2 = 1
                if (r0 == 0) goto L_0x003a
                android.content.pm.ApplicationInfo r0 = r4.info
                int r0 = r0.uid
                int r0 = android.os.UserHandle.getUserId(r0)
                if (r0 == r1) goto L_0x0026
                android.content.pm.ApplicationInfo r0 = r4.info
                int r1 = r0.flags
                r1 = r1 & r2
                if (r1 != 0) goto L_0x0026
                java.lang.String r0 = r0.packageName
                boolean r0 = com.oneplus.settings.displaysizeadaption.DisplaySizeAdaptionBridge.access$000(r0)
                if (r0 != 0) goto L_0x0030
            L_0x0026:
                android.content.pm.ApplicationInfo r0 = r4.info
                java.lang.String r0 = r0.packageName
                boolean r0 = com.oneplus.settings.displaysizeadaption.DisplaySizeAdaptionBridge.access$100(r0)
                if (r0 == 0) goto L_0x0039
            L_0x0030:
                android.content.pm.ApplicationInfo r4 = r4.info
                int r4 = r4.targetSdkVersion
                r0 = 28
                if (r4 >= r0) goto L_0x0039
                r3 = r2
            L_0x0039:
                return r3
            L_0x003a:
                android.content.pm.ApplicationInfo r0 = r4.info
                int r0 = r0.uid
                int r0 = android.os.UserHandle.getUserId(r0)
                if (r0 == r1) goto L_0x0054
                android.content.pm.ApplicationInfo r4 = r4.info
                int r0 = r4.flags
                r0 = r0 & r2
                if (r0 != 0) goto L_0x0054
                java.lang.String r4 = r4.packageName
                boolean r4 = com.oneplus.settings.displaysizeadaption.DisplaySizeAdaptionBridge.access$000(r4)
                if (r4 == 0) goto L_0x0054
                r3 = r2
            L_0x0054:
                return r3
            */
            throw new UnsupportedOperationException("Method not decompiled: com.oneplus.settings.displaysizeadaption.DisplaySizeAdaptionBridge.AnonymousClass2.filterApp(com.android.settingslib.applications.ApplicationsState$AppEntry):boolean");
        }
    };
    public static final ApplicationsState.AppFilter FILTER_APP_DEFAULT = new ApplicationsState.AppFilter() {
        /* class com.oneplus.settings.displaysizeadaption.DisplaySizeAdaptionBridge.AnonymousClass1 */

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        /* JADX WARNING: Code restructure failed: missing block: B:11:0x002b, code lost:
            if (com.oneplus.settings.displaysizeadaption.DisplaySizeAdaptionBridge.isLauncherApp(r1.packageName) != false) goto L_0x0037;
         */
        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean filterApp(com.android.settingslib.applications.ApplicationsState.AppEntry r6) {
            /*
            // Method dump skipped, instructions count: 112
            */
            throw new UnsupportedOperationException("Method not decompiled: com.oneplus.settings.displaysizeadaption.DisplaySizeAdaptionBridge.AnonymousClass1.filterApp(com.android.settingslib.applications.ApplicationsState$AppEntry):boolean");
        }
    };
    public static final ApplicationsState.AppFilter FILTER_APP_FULL_SCREEN = new ApplicationsState.AppFilter() {
        /* class com.oneplus.settings.displaysizeadaption.DisplaySizeAdaptionBridge.AnonymousClass3 */

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        /* JADX WARNING: Code restructure failed: missing block: B:11:0x002a, code lost:
            if (com.oneplus.settings.displaysizeadaption.DisplaySizeAdaptionBridge.isLauncherApp(r1.packageName) != false) goto L_0x0036;
         */
        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean filterApp(com.android.settingslib.applications.ApplicationsState.AppEntry r5) {
            /*
            // Method dump skipped, instructions count: 111
            */
            throw new UnsupportedOperationException("Method not decompiled: com.oneplus.settings.displaysizeadaption.DisplaySizeAdaptionBridge.AnonymousClass3.filterApp(com.android.settingslib.applications.ApplicationsState$AppEntry):boolean");
        }
    };
    public static final ApplicationsState.AppFilter FILTER_APP_ORIGINAL_SIZE = new ApplicationsState.AppFilter() {
        /* class com.oneplus.settings.displaysizeadaption.DisplaySizeAdaptionBridge.AnonymousClass4 */

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(ApplicationsState.AppEntry appEntry) {
            Object obj;
            if (appEntry == null || (obj = appEntry.extraInfo) == null) {
                return false;
            }
            OPAppModel oPAppModel = (OPAppModel) obj;
            if (UserHandle.getUserId(appEntry.info.uid) != 999 && (appEntry.info.flags & 1) == 0 && DisplaySizeAdaptionBridge.ADAPTION_MANAGER.getAppTypeValue(oPAppModel.getPkgName()) == 0) {
                return true;
            }
            return false;
        }
    };
    private static List<ResolveInfo> resolveInfoList;
    private final Context mContext;
    private final PackageManager mPm;

    public DisplaySizeAdaptionBridge(Context context, ApplicationsState applicationsState, AppStateBaseBridge.Callback callback) {
        super(applicationsState, callback);
        this.mContext = context;
        this.mPm = context.getPackageManager();
        AppOpsManager appOpsManager = (AppOpsManager) this.mContext.getSystemService("appops");
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.AppStateBaseBridge
    public void loadAllExtraInfo() {
        ArrayList<ApplicationsState.AppEntry> allApps = this.mAppSession.getAllApps();
        int size = allApps.size();
        Map<String, OPAppModel> loadAppMap = ADAPTION_MANAGER.loadAppMap();
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
    public static boolean packageExcludeFilter(String str) {
        return "com.google.android.calendar".equals(str) || "com.android.chrome".equals(str) || "com.android.documentsui".equals(str) || "com.google.android.apps.docs".equals(str) || "com.google.android.apps.tachyon".equals(str) || "com.google.android.gm".equals(str) || "com.google.android.googlequicksearchbox".equals(str) || "com.google.android.apps.walletnfcrel".equals(str) || "com.google.android.apps.maps".equals(str) || "com.google.android.apps.photos".equals(str) || "com.google.android.videos".equals(str) || "com.google.android.music".equals(str) || "com.android.vending".equals(str) || "com.google.android.youtube".equals(str) || "com.android.browser".equals(str) || "com.nearme.browser".equals(str);
    }

    /* access modifiers changed from: private */
    public static List<ResolveInfo> getLauncherApp() {
        Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
        intent.addCategory("android.intent.category.LAUNCHER");
        return SettingsBaseApplication.mApplication.getPackageManager().queryIntentActivities(intent, 0);
    }

    /* access modifiers changed from: private */
    public static boolean isLauncherApp(String str) {
        for (int i = 0; i < resolveInfoList.size(); i++) {
            if (str.equals(resolveInfoList.get(i).activityInfo.packageName)) {
                return true;
            }
        }
        return false;
    }
}
