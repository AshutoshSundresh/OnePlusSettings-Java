package com.android.settings.applications.defaultapps;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.IconDrawableFactory;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settingslib.applications.DefaultAppInfo;
import java.util.ArrayList;
import java.util.List;

public class DefaultBrowserPreferenceController extends DefaultAppPreferenceController {
    static final Intent BROWSE_PROBE = new Intent().setAction("android.intent.action.VIEW").addCategory("android.intent.category.BROWSABLE").setData(Uri.parse("http:")).addFlags(512);

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "default_browser";
    }

    public DefaultBrowserPreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        List<ResolveInfo> candidates = getCandidates(this.mPackageManager, this.mUserId);
        return candidates != null && !candidates.isEmpty();
    }

    @Override // com.android.settings.applications.defaultapps.DefaultAppPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        CharSequence defaultAppLabel = getDefaultAppLabel();
        if (!TextUtils.isEmpty(defaultAppLabel)) {
            preference.setSummary(defaultAppLabel);
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.defaultapps.DefaultAppPreferenceController
    public DefaultAppInfo getDefaultAppInfo() {
        try {
            String defaultBrowserPackageNameAsUser = this.mPackageManager.getDefaultBrowserPackageNameAsUser(this.mUserId);
            Log.d("BrowserPrefCtrl", "Get default browser package: " + defaultBrowserPackageNameAsUser);
            return new DefaultAppInfo(this.mContext, this.mPackageManager, this.mUserId, this.mPackageManager.getApplicationInfoAsUser(defaultBrowserPackageNameAsUser, 0, this.mUserId));
        } catch (PackageManager.NameNotFoundException unused) {
            return null;
        }
    }

    @Override // com.android.settings.applications.defaultapps.DefaultAppPreferenceController
    public CharSequence getDefaultAppLabel() {
        CharSequence charSequence = null;
        if (!isAvailable()) {
            return null;
        }
        DefaultAppInfo defaultAppInfo = getDefaultAppInfo();
        if (defaultAppInfo != null) {
            charSequence = defaultAppInfo.loadLabel();
        }
        if (!TextUtils.isEmpty(charSequence)) {
            return charSequence;
        }
        return getOnlyAppLabel();
    }

    @Override // com.android.settings.applications.defaultapps.DefaultAppPreferenceController
    public Drawable getDefaultAppIcon() {
        if (!isAvailable()) {
            return null;
        }
        DefaultAppInfo defaultAppInfo = getDefaultAppInfo();
        if (defaultAppInfo != null) {
            return defaultAppInfo.loadIcon();
        }
        return getOnlyAppIcon();
    }

    static List<ResolveInfo> getCandidates(PackageManager packageManager, int i) {
        ActivityInfo activityInfo;
        ArrayList arrayList = new ArrayList();
        List<ResolveInfo> queryIntentActivitiesAsUser = packageManager.queryIntentActivitiesAsUser(BROWSE_PROBE, 131072, i);
        if (queryIntentActivitiesAsUser != null) {
            ArraySet arraySet = new ArraySet();
            for (ResolveInfo resolveInfo : queryIntentActivitiesAsUser) {
                if (resolveInfo.handleAllWebDataURI && (activityInfo = resolveInfo.activityInfo) != null && activityInfo.enabled && activityInfo.applicationInfo.enabled) {
                    String str = activityInfo.packageName;
                    if (!arraySet.contains(str)) {
                        arrayList.add(resolveInfo);
                        arraySet.add(str);
                    }
                }
            }
        }
        return arrayList;
    }

    private String getOnlyAppLabel() {
        List<ResolveInfo> candidates = getCandidates(this.mPackageManager, this.mUserId);
        String str = null;
        if (candidates == null || candidates.size() != 1) {
            return null;
        }
        ResolveInfo resolveInfo = candidates.get(0);
        String charSequence = resolveInfo.loadLabel(this.mPackageManager).toString();
        ComponentInfo componentInfo = resolveInfo.getComponentInfo();
        if (componentInfo != null) {
            str = componentInfo.packageName;
        }
        Log.d("BrowserPrefCtrl", "Getting label for the only browser app: " + str + charSequence);
        return charSequence;
    }

    /* access modifiers changed from: package-private */
    public Drawable getOnlyAppIcon() {
        String str;
        List<ResolveInfo> candidates = getCandidates(this.mPackageManager, this.mUserId);
        if (candidates != null && candidates.size() == 1) {
            ComponentInfo componentInfo = candidates.get(0).getComponentInfo();
            if (componentInfo == null) {
                str = null;
            } else {
                str = componentInfo.packageName;
            }
            if (TextUtils.isEmpty(str)) {
                return null;
            }
            try {
                ApplicationInfo applicationInfoAsUser = this.mPackageManager.getApplicationInfoAsUser(str, 0, this.mUserId);
                Log.d("BrowserPrefCtrl", "Getting icon for the only browser app: " + str);
                return IconDrawableFactory.newInstance(this.mContext).getBadgedIcon(componentInfo, applicationInfoAsUser, this.mUserId);
            } catch (PackageManager.NameNotFoundException unused) {
                Log.w("BrowserPrefCtrl", "Error getting app info for " + str);
            }
        }
        return null;
    }
}
