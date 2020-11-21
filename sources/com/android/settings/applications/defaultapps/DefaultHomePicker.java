package com.android.settings.applications.defaultapps;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.text.TextUtils;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settingslib.applications.DefaultAppInfo;
import java.util.ArrayList;
import java.util.List;

public class DefaultHomePicker extends DefaultAppPickerFragment {
    private String mPackageName;

    private boolean versionNumberAtLeastL(int i) {
        return i >= 21;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 787;
    }

    @Override // com.android.settings.applications.defaultapps.DefaultAppPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settings.widget.RadioButtonPickerFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mPackageName = context.getPackageName();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.widget.RadioButtonPickerFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.default_home_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public List<DefaultAppInfo> getCandidates() {
        String str;
        boolean hasManagedProfile = hasManagedProfile();
        ArrayList arrayList = new ArrayList();
        ArrayList<ResolveInfo> arrayList2 = new ArrayList();
        Context context = getContext();
        this.mPm.getHomeActivities(arrayList2);
        for (ResolveInfo resolveInfo : arrayList2) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            ComponentName componentName = new ComponentName(activityInfo.packageName, activityInfo.name);
            if (!activityInfo.packageName.equals(this.mPackageName)) {
                boolean z = true;
                if (!hasManagedProfile || launcherHasManagedProfilesFeature(resolveInfo)) {
                    str = null;
                } else {
                    str = getContext().getString(C0017R$string.home_work_profile_not_supported);
                    z = false;
                }
                arrayList.add(new DefaultAppInfo(context, this.mPm, this.mUserId, componentName, str, z));
            }
        }
        return arrayList;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public String getDefaultKey() {
        ComponentName homeActivities = this.mPm.getHomeActivities(new ArrayList());
        if (homeActivities != null) {
            return homeActivities.flattenToString();
        }
        return null;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public boolean setDefaultKey(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        ComponentName unflattenFromString = ComponentName.unflattenFromString(str);
        ArrayList<ResolveInfo> arrayList = new ArrayList();
        this.mPm.getHomeActivities(arrayList);
        ArrayList arrayList2 = new ArrayList();
        for (ResolveInfo resolveInfo : arrayList) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            arrayList2.add(new ComponentName(activityInfo.packageName, activityInfo.name));
        }
        this.mPm.replacePreferredActivity(DefaultHomePreferenceController.HOME_FILTER, 1048576, (ComponentName[]) arrayList2.toArray(new ComponentName[0]), unflattenFromString);
        Context context = getContext();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.setFlags(268435456);
        context.startActivity(intent);
        return true;
    }

    private boolean hasManagedProfile() {
        for (UserInfo userInfo : this.mUserManager.getProfiles(getContext().getUserId())) {
            if (userInfo.isManagedProfile() && userInfo.id != 999) {
                return true;
            }
        }
        return false;
    }

    private boolean launcherHasManagedProfilesFeature(ResolveInfo resolveInfo) {
        try {
            return versionNumberAtLeastL(this.mPm.getApplicationInfo(resolveInfo.activityInfo.packageName, 0).targetSdkVersion);
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }
}
