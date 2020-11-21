package com.android.settings.applications.defaultapps;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import android.text.TextUtils;
import com.android.internal.telephony.SmsApplication;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.Utils;
import com.android.settingslib.applications.DefaultAppInfo;
import com.android.settingslib.widget.CandidateInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DefaultSmsPicker extends DefaultAppPickerFragment {
    private DefaultKeyUpdater mDefaultKeyUpdater = new DefaultKeyUpdater();

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 789;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.widget.RadioButtonPickerFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.default_sms_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public List<DefaultAppInfo> getCandidates() {
        Context context = getContext();
        Collection<SmsApplication.SmsApplicationData> applicationCollection = SmsApplication.getApplicationCollection(context);
        ArrayList arrayList = new ArrayList(applicationCollection.size());
        for (SmsApplication.SmsApplicationData smsApplicationData : applicationCollection) {
            try {
                arrayList.add(new DefaultAppInfo(context, this.mPm, UserHandle.myUserId(), this.mPm.getApplicationInfoAsUser(smsApplicationData.mPackageName, 0, this.mUserId)));
            } catch (PackageManager.NameNotFoundException unused) {
            }
        }
        return arrayList;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public String getDefaultKey() {
        return this.mDefaultKeyUpdater.getDefaultApplication(getContext());
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public boolean setDefaultKey(String str) {
        if (TextUtils.isEmpty(str) || TextUtils.equals(str, getDefaultKey())) {
            return false;
        }
        this.mDefaultKeyUpdater.setDefaultApplication(getContext(), str);
        return true;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.defaultapps.DefaultAppPickerFragment
    public String getConfirmationMessage(CandidateInfo candidateInfo) {
        if (Utils.isPackageDirectBootAware(getContext(), candidateInfo.getKey())) {
            return null;
        }
        return getContext().getString(C0017R$string.direct_boot_unaware_dialog_message);
    }

    /* access modifiers changed from: package-private */
    public static class DefaultKeyUpdater {
        DefaultKeyUpdater() {
        }

        public String getDefaultApplication(Context context) {
            ComponentName defaultSmsApplication = SmsApplication.getDefaultSmsApplication(context, true);
            if (defaultSmsApplication != null) {
                return defaultSmsApplication.getPackageName();
            }
            return null;
        }

        public void setDefaultApplication(Context context, String str) {
            SmsApplication.setDefaultApplication(str, context);
        }
    }
}
