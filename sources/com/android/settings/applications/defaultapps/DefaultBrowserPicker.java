package com.android.settings.applications.defaultapps;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.UserHandle;
import android.util.ArraySet;
import com.android.settings.C0019R$xml;
import com.android.settingslib.applications.DefaultAppInfo;
import java.util.ArrayList;
import java.util.List;

public class DefaultBrowserPicker extends DefaultAppPickerFragment {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 785;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.widget.RadioButtonPickerFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.default_browser_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public String getDefaultKey() {
        return this.mPm.getDefaultBrowserPackageNameAsUser(this.mUserId);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public boolean setDefaultKey(String str) {
        return this.mPm.setDefaultBrowserPackageNameAsUser(str, this.mUserId);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public List<DefaultAppInfo> getCandidates() {
        ArrayList arrayList = new ArrayList();
        Context context = getContext();
        List queryIntentActivitiesAsUser = this.mPm.queryIntentActivitiesAsUser(DefaultBrowserPreferenceController.BROWSE_PROBE, 131072, this.mUserId);
        int size = queryIntentActivitiesAsUser.size();
        ArraySet arraySet = new ArraySet();
        for (int i = 0; i < size; i++) {
            ResolveInfo resolveInfo = (ResolveInfo) queryIntentActivitiesAsUser.get(i);
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            if (activityInfo != null && resolveInfo.handleAllWebDataURI) {
                String str = activityInfo.packageName;
                if (!arraySet.contains(str)) {
                    try {
                        arrayList.add(new DefaultAppInfo(context, this.mPm, UserHandle.myUserId(), this.mPm.getApplicationInfo(str, 0)));
                        arraySet.add(str);
                    } catch (PackageManager.NameNotFoundException unused) {
                    }
                }
            }
        }
        return arrayList;
    }
}
