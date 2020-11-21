package com.oneplus.settings.backgroundoptimize;

import android.content.Context;
import android.os.IDeviceIdleController;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.Log;
import com.android.settings.applications.AppStateBaseBridge;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.fuelgauge.PowerWhitelistBackend;
import com.oneplus.settings.utils.OPUtils;
import com.oneplus.settings.utils.ProductUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class AppBgOptimizeBridge extends AppStateBaseBridge {
    public static final ApplicationsState.AppFilter FILTER_APP_BG_All = new ApplicationsState.AppFilter() {
        /* class com.oneplus.settings.backgroundoptimize.AppBgOptimizeBridge.AnonymousClass1 */

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(ApplicationsState.AppEntry appEntry) {
            if (appEntry == null) {
                return false;
            }
            return AppBgOptimizeBridge.needShown(appEntry.info.packageName, AppBgOptimizeBridge.mPowerWhitelistBackend);
        }
    };
    public static final ApplicationsState.AppFilter FILTER_APP_BG_NOT_OPTIMIZE = new ApplicationsState.AppFilter() {
        /* class com.oneplus.settings.backgroundoptimize.AppBgOptimizeBridge.AnonymousClass2 */

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(ApplicationsState.AppEntry appEntry) {
            Object obj;
            if (appEntry == null || (obj = appEntry.extraInfo) == null || !(obj instanceof AppControlMode) || UserHandle.getUserId(appEntry.info.uid) == 999 || 1 != ((AppControlMode) appEntry.extraInfo).value) {
                return false;
            }
            return true;
        }
    };
    public static final List<String> NOT_SHOWN_LIST;
    public static final String[] NOT_SHOWN_PACKAGES;
    public static HashSet<String> VZW_APPS_SHOWN_DISABLED;
    private static PowerWhitelistBackend mPowerWhitelistBackend;
    private final Context mContext;
    private IDeviceIdleController mDeviceIdleService = IDeviceIdleController.Stub.asInterface(ServiceManager.getService("deviceidle"));
    private final BgOActivityManager mManager;

    static {
        String[] strArr = {"com.oneplus.card", "com.oneplus.cloud", "com.oneplus.appupgrader", "net.oneplus.launcher", "com.oneplus.dirac.simplemanager", "com.oneplus.soundrecorder", "com.oneplus.sound.tuner", "com.oneplus.soundrecorder", "com.android.dialer", "com.oneplus.mms"};
        NOT_SHOWN_PACKAGES = strArr;
        NOT_SHOWN_LIST = Arrays.asList(strArr);
    }

    public AppBgOptimizeBridge(Context context, ApplicationsState applicationsState, AppStateBaseBridge.Callback callback) {
        super(applicationsState, callback);
        try {
            VZW_APPS_SHOWN_DISABLED = new HashSet<>(Arrays.asList(this.mDeviceIdleService.getSystemPowerWhitelist()));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        this.mContext = context;
        context.getPackageManager();
        this.mManager = BgOActivityManager.getInstance(this.mContext);
        mPowerWhitelistBackend = PowerWhitelistBackend.getInstance(context);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.AppStateBaseBridge
    public void loadAllExtraInfo() {
        ArrayList<ApplicationsState.AppEntry> allApps = this.mAppSession.getAllApps();
        int size = allApps.size();
        Map<String, AppControlMode> allAppControlModesMap = this.mManager.getAllAppControlModesMap(0);
        for (int i = 0; i < size; i++) {
            ApplicationsState.AppEntry appEntry = allApps.get(i);
            appEntry.extraInfo = allAppControlModesMap.get(appEntry.info.packageName);
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.AppStateBaseBridge
    public void updateExtraInfo(ApplicationsState.AppEntry appEntry, String str, int i) {
        appEntry.extraInfo = new AppControlMode(str, 0, this.mManager.getAppControlMode(str, 0));
    }

    public static boolean needShown(String str, PowerWhitelistBackend powerWhitelistBackend) {
        if (OPUtils.isMultiAppUser() || OPUtils.isGuestMode() || NOT_SHOWN_LIST.contains(str)) {
            return false;
        }
        HashSet<String> hashSet = VZW_APPS_SHOWN_DISABLED;
        if (hashSet != null && hashSet.contains(str) && ProductUtils.isUsvMode()) {
            return true;
        }
        String name = AppBgOptimizeBridge.class.getName();
        StringBuilder sb = new StringBuilder();
        sb.append("packageName : ");
        sb.append(str);
        sb.append("    isneedShown : ");
        sb.append(!powerWhitelistBackend.isSysWhitelisted(str));
        Log.d(name, sb.toString());
        return !powerWhitelistBackend.isSysWhitelisted(str);
    }
}
