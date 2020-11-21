package com.android.settings.gestures;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public class SystemNavigationPreferenceController extends BasePreferenceController {
    private static final String ACTION_QUICKSTEP = "android.intent.action.QUICKSTEP_SERVICE";
    static final String PREF_KEY_SYSTEM_NAVIGATION = "gesture_system_navigation";

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 3;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public SystemNavigationPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        if (isGestureNavigationEnabled(this.mContext)) {
            return this.mContext.getText(C0017R$string.edge_to_edge_navigation_title);
        }
        if (is2ButtonNavigationEnabled(this.mContext)) {
            return this.mContext.getText(C0017R$string.swipe_up_to_switch_apps_title);
        }
        return this.mContext.getText(C0017R$string.legacy_navigation_title);
    }

    static boolean isGestureAvailable(Context context) {
        ComponentName unflattenFromString;
        if (!context.getResources().getBoolean(17891564) || (unflattenFromString = ComponentName.unflattenFromString(context.getString(17039953))) == null) {
            return false;
        }
        if (context.getPackageManager().resolveService(new Intent(ACTION_QUICKSTEP).setPackage(unflattenFromString.getPackageName()), 1048576) == null) {
            return false;
        }
        return true;
    }

    static boolean isOverlayPackageAvailable(Context context, String str) {
        try {
            return context.getPackageManager().getPackageInfo(str, 0) != null;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    static boolean is2ButtonNavigationEnabled(Context context) {
        return 1 == context.getResources().getInteger(17694854);
    }

    static boolean isGestureNavigationEnabled(Context context) {
        return 2 == context.getResources().getInteger(17694854);
    }
}
