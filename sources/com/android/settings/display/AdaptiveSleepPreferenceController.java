package com.android.settings.display;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.provider.Settings;
import android.text.TextUtils;
import com.android.settings.C0017R$string;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public class AdaptiveSleepPreferenceController extends TogglePreferenceController {
    private static final int DEFAULT_VALUE = 0;
    public static final String PREF_NAME = "adaptive_sleep";
    private static final String SYSTEM_KEY = "adaptive_sleep";

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AdaptiveSleepPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return hasSufficientPermission(this.mContext.getPackageManager()) && Settings.Secure.getInt(this.mContext.getContentResolver(), "adaptive_sleep", 0) != 0;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        Settings.Secure.putInt(this.mContext.getContentResolver(), "adaptive_sleep", z ? 1 : 0);
        return true;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return isControllerAvailable(this.mContext);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        int i;
        Context context = this.mContext;
        if (isChecked()) {
            i = C0017R$string.adaptive_sleep_summary_on;
        } else {
            i = C0017R$string.adaptive_sleep_summary_off;
        }
        return context.getText(i);
    }

    public static int isControllerAvailable(Context context) {
        return (!context.getResources().getBoolean(17891339) || !isAttentionServiceAvailable(context)) ? 3 : 1;
    }

    private static boolean isAttentionServiceAvailable(Context context) {
        ResolveInfo resolveService;
        PackageManager packageManager = context.getPackageManager();
        String attentionServicePackageName = packageManager.getAttentionServicePackageName();
        if (TextUtils.isEmpty(attentionServicePackageName) || (resolveService = packageManager.resolveService(new Intent("android.service.attention.AttentionService").setPackage(attentionServicePackageName), 1048576)) == null || resolveService.serviceInfo == null) {
            return false;
        }
        return true;
    }

    static boolean hasSufficientPermission(PackageManager packageManager) {
        String attentionServicePackageName = packageManager.getAttentionServicePackageName();
        return attentionServicePackageName != null && packageManager.checkPermission("android.permission.CAMERA", attentionServicePackageName) == 0;
    }
}
