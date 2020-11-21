package com.android.settings.applications.defaultapps;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.Utils;
import com.android.settingslib.applications.DefaultAppInfo;
import com.android.settingslib.widget.CandidateInfo;
import java.util.ArrayList;
import java.util.List;

public class DefaultEmergencyPicker extends DefaultAppPickerFragment {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 786;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.widget.RadioButtonPickerFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.default_emergency_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public List<DefaultAppInfo> getCandidates() {
        ArrayList arrayList = new ArrayList();
        List<ResolveInfo> queryIntentActivities = this.mPm.queryIntentActivities(DefaultEmergencyPreferenceController.QUERY_INTENT, 0);
        Context context = getContext();
        PackageInfo packageInfo = null;
        for (ResolveInfo resolveInfo : queryIntentActivities) {
            try {
                PackageInfo packageInfo2 = this.mPm.getPackageInfo(resolveInfo.activityInfo.packageName, 0);
                ApplicationInfo applicationInfo = packageInfo2.applicationInfo;
                arrayList.add(new DefaultAppInfo(context, this.mPm, UserHandle.myUserId(), applicationInfo));
                if (isSystemApp(applicationInfo) && (packageInfo == null || packageInfo.firstInstallTime > packageInfo2.firstInstallTime)) {
                    packageInfo = packageInfo2;
                }
            } catch (PackageManager.NameNotFoundException unused) {
            }
            if (packageInfo != null && TextUtils.isEmpty(getDefaultKey())) {
                setDefaultKey(packageInfo.packageName);
            }
        }
        return arrayList;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.defaultapps.DefaultAppPickerFragment
    public String getConfirmationMessage(CandidateInfo candidateInfo) {
        if (Utils.isPackageDirectBootAware(getContext(), candidateInfo.getKey())) {
            return null;
        }
        return getContext().getString(C0017R$string.direct_boot_unaware_dialog_message);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public String getDefaultKey() {
        return Settings.Secure.getString(getContext().getContentResolver(), "emergency_assistance_application");
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public boolean setDefaultKey(String str) {
        ContentResolver contentResolver = getContext().getContentResolver();
        String string = Settings.Secure.getString(contentResolver, "emergency_assistance_application");
        if (TextUtils.isEmpty(str) || TextUtils.equals(str, string)) {
            return false;
        }
        Settings.Secure.putString(contentResolver, "emergency_assistance_application", str);
        return true;
    }

    private boolean isSystemApp(ApplicationInfo applicationInfo) {
        return (applicationInfo == null || (applicationInfo.flags & 1) == 0) ? false : true;
    }
}
