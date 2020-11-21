package com.android.settings.datausage;

import android.app.AppGlobals;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import com.oneplus.settings.utils.OPSNSUtils;
import java.util.ArrayList;
import java.util.List;

public class OPDataUsageUtils {
    public static long[] getDataUsageSectionTimeMillByAccountDay(Context context, int i) {
        if (i != -1) {
            return getOneplusDataUsageRegion(context, OPSNSUtils.findSlotIdBySubId(i));
        }
        return getOneplusDataUsageRegion(context, -1);
    }

    public static List<ApplicationInfo> getApplicationInfoByUid(Context context, int i) {
        ArrayList arrayList = new ArrayList();
        String[] packagesForUid = context.getPackageManager().getPackagesForUid(i);
        int length = packagesForUid != null ? packagesForUid.length : 0;
        try {
            int userId = UserHandle.getUserId(i);
            IPackageManager packageManager = AppGlobals.getPackageManager();
            for (int i2 = 0; i2 < length; i2++) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packagesForUid[i2], 0, userId);
                if (applicationInfo != null) {
                    arrayList.add(applicationInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    public static long[] getOneplusDataUsageRegion(Context context, int i) {
        Bundle bundle = new Bundle();
        bundle.putInt("oneplus_datausage_slotid", i);
        try {
            Bundle call = context.getContentResolver().call(Uri.parse("content://com.oneplus.security.database.SafeProvider"), "method_query_oneplus_datausage_region", (String) null, bundle);
            if (!(call == null || call.getInt("oneplus_datausage_error_code") == 2)) {
                return new long[]{call.getLong("oneplus_datausage_time_start"), call.getLong("oneplus_datausage_time_end")};
            }
        } catch (Exception e) {
            Log.e("OPDataUsageUtils", "getOneplusDataUsage error");
            e.printStackTrace();
        }
        return new long[]{0, System.currentTimeMillis()};
    }
}
