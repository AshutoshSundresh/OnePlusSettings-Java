package com.oneplus.settings.controllers;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.oneplus.settings.utils.OPUtils;

public class OPTopLevelCloudPreferenceController extends BasePreferenceController implements LifecycleObserver {
    private static final String KEY_TOP_LEVEL_CLOUD = "top_level_cloud";
    private Context mContext;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
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

    public OPTopLevelCloudPreferenceController(Context context, String str) {
        super(context, str);
        this.mContext = context;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return ((OPUtils.isAppExist(this.mContext, "com.oneplus.cloud") || OPUtils.isAppExist(this.mContext, "com.heytap.cloud")) && !OPUtils.isGuestMode()) ? 0 : 3;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Preference findPreference = preferenceScreen.findPreference(getPreferenceKey());
        if (OPUtils.isAppExist(this.mContext, "com.oneplus.cloud")) {
            findPreference.setTitle(getAppName(this.mContext, "com.oneplus.cloud"));
        } else if (OPUtils.isAppExist(this.mContext, "com.heytap.cloud")) {
            findPreference.setTitle(getAppName(this.mContext, "com.heytap.cloud"));
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!KEY_TOP_LEVEL_CLOUD.equals(preference.getKey())) {
            return false;
        }
        try {
            if (OPUtils.isAppExist(this.mContext, "com.oneplus.cloud")) {
                Intent intent = new Intent("android.intent.action.ONEPLUSCLOUD");
                intent.setClassName("com.oneplus.cloud", "com.oneplus.cloud.activity.OPMainActivity");
                this.mContext.startActivity(intent);
                return true;
            } else if (!OPUtils.isAppExist(this.mContext, "com.heytap.cloud")) {
                return true;
            } else {
                Intent intent2 = new Intent("intent.action.ocloud.MAIN");
                intent2.setPackage("com.heytap.cloud");
                this.mContext.startActivity(intent2);
                return true;
            }
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return true;
        }
    }

    public String getAppName(Context context, String str) {
        ApplicationInfo applicationInfo;
        PackageManager packageManager = context.getPackageManager();
        try {
            applicationInfo = packageManager.getApplicationInfo(str, 0);
        } catch (PackageManager.NameNotFoundException unused) {
            applicationInfo = null;
        }
        return (String) (applicationInfo != null ? packageManager.getApplicationLabel(applicationInfo) : "(unknown)");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        if (OPUtils.isO2()) {
            return this.mContext.getString(C0017R$string.oneplus_top_level_cloud_o2_summary);
        }
        return this.mContext.getString(C0017R$string.oneplus_top_level_cloud_h2_summary);
    }
}
