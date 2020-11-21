package com.android.settingslib.drawer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Parcel;
import android.util.Log;
import java.util.List;

public class ActivityTile extends Tile {
    public ActivityTile(ActivityInfo activityInfo, String str) {
        super(activityInfo, str);
        setMetaData(activityInfo.metaData);
    }

    ActivityTile(Parcel parcel) {
        super(parcel);
    }

    @Override // com.android.settingslib.drawer.Tile
    public String getDescription() {
        return getPackageName() + "/" + getComponentName();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.drawer.Tile
    public ComponentInfo getComponentInfo(Context context) {
        if (this.mComponentInfo == null) {
            PackageManager packageManager = context.getApplicationContext().getPackageManager();
            Intent intent = getIntent();
            List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 128);
            if (queryIntentActivities == null || queryIntentActivities.isEmpty()) {
                Log.e("ActivityTile", "Cannot find package info for " + intent.getComponent().flattenToString());
            } else {
                ActivityInfo activityInfo = queryIntentActivities.get(0).activityInfo;
                this.mComponentInfo = activityInfo;
                setMetaData(((ComponentInfo) activityInfo).metaData);
            }
        }
        return this.mComponentInfo;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.drawer.Tile
    public CharSequence getComponentLabel(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ComponentInfo componentInfo = getComponentInfo(context);
        if (componentInfo == null) {
            return null;
        }
        return componentInfo.loadLabel(packageManager);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.drawer.Tile
    public int getComponentIcon(ComponentInfo componentInfo) {
        return componentInfo.icon;
    }
}
