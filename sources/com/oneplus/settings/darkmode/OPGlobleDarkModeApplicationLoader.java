package com.oneplus.settings.darkmode;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import com.oneplus.settings.apploader.OPApplicationLoader;
import com.oneplus.settings.better.OPAppModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OPGlobleDarkModeApplicationLoader extends OPApplicationLoader {
    protected static List<OPAppModel> mAppList = new ArrayList();
    protected Map<String, Integer> mAppopsModeMap = new HashMap();
    protected List<OPAppModel> mGrayAppList = new ArrayList();
    protected List<OPAppModel> mWhiteAppList = new ArrayList();

    public OPGlobleDarkModeApplicationLoader(Context context, AppOpsManager appOpsManager, PackageManager packageManager) {
        super(context, appOpsManager, packageManager);
    }

    public Map<String, Integer> loadAppMode(int i) {
        List<AppOpsManager.PackageOps> packagesForOps = this.mAppOpsManager.getPackagesForOps(new int[]{i});
        Map<String, Integer> map = this.mAppopsModeMap;
        if (map != null) {
            map.clear();
        }
        if (packagesForOps != null) {
            for (AppOpsManager.PackageOps packageOps : packagesForOps) {
                int userId = UserHandle.getUserId(packageOps.getUid());
                int uid = packageOps.getUid();
                if (isThisUserAProfileOfCurrentUser(userId)) {
                    for (AppOpsManager.OpEntry opEntry : packageOps.getOps()) {
                        if (opEntry.getOp() == i) {
                            Map<String, Integer> map2 = this.mAppopsModeMap;
                            map2.put(uid + packageOps.getPackageName(), Integer.valueOf(opEntry.getMode()));
                        }
                    }
                }
            }
        }
        return this.mAppopsModeMap;
    }

    public void releaseAppList() {
        List<OPAppModel> list = mAppList;
        if (list != null) {
            list.clear();
        }
        List<OPAppModel> list2 = this.mWhiteAppList;
        if (list2 != null) {
            list2.clear();
        }
        List<OPAppModel> list3 = this.mGrayAppList;
        if (list3 != null) {
            list3.clear();
        }
    }

    @Override // com.oneplus.settings.apploader.OPApplicationLoader
    public List<OPAppModel> getAppListByType(int i) {
        if (i == 0) {
            return mAppList;
        }
        return this.mAllAppSortBySelectedList;
    }

    /* JADX WARNING: Removed duplicated region for block: B:45:0x011d A[Catch:{ Exception -> 0x01a3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0167 A[Catch:{ Exception -> 0x01a3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x0168 A[Catch:{ Exception -> 0x01a3 }] */
    @Override // com.oneplus.settings.apploader.OPApplicationLoader
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadAllAppList() {
        /*
        // Method dump skipped, instructions count: 429
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oneplus.settings.darkmode.OPGlobleDarkModeApplicationLoader.loadAllAppList():void");
    }
}
