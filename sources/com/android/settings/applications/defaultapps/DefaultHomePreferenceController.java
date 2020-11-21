package com.android.settings.applications.defaultapps;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.ResolveInfo;
import com.android.settings.C0005R$bool;
import com.android.settingslib.applications.DefaultAppInfo;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;

public class DefaultHomePreferenceController extends DefaultAppPreferenceController {
    static final IntentFilter HOME_FILTER;
    private final String mPackageName = this.mContext.getPackageName();

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "default_home";
    }

    static {
        IntentFilter intentFilter = new IntentFilter("android.intent.action.MAIN");
        HOME_FILTER = intentFilter;
        intentFilter.addCategory("android.intent.category.HOME");
        HOME_FILTER.addCategory("android.intent.category.DEFAULT");
    }

    public DefaultHomePreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mContext.getResources().getBoolean(C0005R$bool.config_show_default_home);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.defaultapps.DefaultAppPreferenceController
    public DefaultAppInfo getDefaultAppInfo() {
        ArrayList arrayList = new ArrayList();
        ComponentName homeActivities = this.mPackageManager.getHomeActivities(arrayList);
        if (homeActivities != null) {
            OPUtils.sendAppTrackerForDefaultHomeAppByComponentName(homeActivities.toString());
        }
        if (homeActivities != null) {
            return new DefaultAppInfo(this.mContext, this.mPackageManager, this.mUserId, homeActivities);
        }
        ActivityInfo onlyAppInfo = getOnlyAppInfo(arrayList);
        if (onlyAppInfo != null) {
            return new DefaultAppInfo(this.mContext, this.mPackageManager, this.mUserId, onlyAppInfo.getComponentName());
        }
        return null;
    }

    private ActivityInfo getOnlyAppInfo(List<ResolveInfo> list) {
        ArrayList arrayList = new ArrayList();
        this.mPackageManager.getHomeActivities(list);
        for (ResolveInfo resolveInfo : list) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            if (!activityInfo.packageName.equals(this.mPackageName)) {
                arrayList.add(activityInfo);
            }
        }
        if (arrayList.size() == 1) {
            return (ActivityInfo) arrayList.get(0);
        }
        return null;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.defaultapps.DefaultAppPreferenceController
    public Intent getSettingIntent(DefaultAppInfo defaultAppInfo) {
        String str;
        if (defaultAppInfo == null) {
            return null;
        }
        ComponentName componentName = defaultAppInfo.componentName;
        if (componentName != null) {
            str = componentName.getPackageName();
        } else {
            PackageItemInfo packageItemInfo = defaultAppInfo.packageItemInfo;
            if (packageItemInfo == null) {
                return null;
            }
            str = packageItemInfo.packageName;
        }
        Intent addFlags = new Intent("android.intent.action.APPLICATION_PREFERENCES").setPackage(str).addFlags(268468224);
        if (this.mPackageManager.queryIntentActivities(addFlags, 0).size() == 1) {
            return addFlags;
        }
        return null;
    }
}
