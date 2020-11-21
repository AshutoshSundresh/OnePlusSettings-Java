package com.android.settingslib.applications;

import android.content.Context;
import android.os.Handler;
import android.permission.PermissionControllerManager;
import android.permission.RuntimePermissionPresentationInfo;
import com.android.settingslib.applications.PermissionsSummaryHelper;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PermissionsSummaryHelper {

    public static abstract class PermissionsResultCallback {
        public abstract void onPermissionSummaryResult(int i, int i2, int i3, List<CharSequence> list);
    }

    public static void getPermissionSummary(Context context, String str, PermissionsResultCallback permissionsResultCallback) {
        ((PermissionControllerManager) context.getSystemService(PermissionControllerManager.class)).getAppPermissions(str, new PermissionControllerManager.OnGetAppPermissionResultCallback() {
            /* class com.android.settingslib.applications.$$Lambda$PermissionsSummaryHelper$5KNAuDHouZhJftbqZ0g04ncINrg */

            public final void onGetAppPermissions(List list) {
                PermissionsSummaryHelper.lambda$getPermissionSummary$0(PermissionsSummaryHelper.PermissionsResultCallback.this, list);
            }
        }, (Handler) null);
    }

    static /* synthetic */ void lambda$getPermissionSummary$0(PermissionsResultCallback permissionsResultCallback, List list) {
        int size = list.size();
        ArrayList arrayList = new ArrayList();
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        for (int i4 = 0; i4 < size; i4++) {
            RuntimePermissionPresentationInfo runtimePermissionPresentationInfo = (RuntimePermissionPresentationInfo) list.get(i4);
            i2++;
            if (runtimePermissionPresentationInfo.isGranted()) {
                if (runtimePermissionPresentationInfo.isStandard()) {
                    arrayList.add(runtimePermissionPresentationInfo.getLabel());
                    i++;
                } else {
                    i3++;
                }
            }
        }
        Collator instance = Collator.getInstance();
        instance.setStrength(0);
        Collections.sort(arrayList, instance);
        permissionsResultCallback.onPermissionSummaryResult(i, i2, i3, arrayList);
    }
}
