package com.android.settings.applications.defaultapps;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import android.telecom.DefaultDialerManager;
import android.telecom.TelecomManager;
import android.text.TextUtils;
import com.android.settings.C0019R$xml;
import com.android.settingslib.applications.DefaultAppInfo;
import java.util.ArrayList;
import java.util.List;

public class DefaultPhonePicker extends DefaultAppPickerFragment {
    private DefaultKeyUpdater mDefaultKeyUpdater;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 788;
    }

    @Override // com.android.settings.applications.defaultapps.DefaultAppPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settings.widget.RadioButtonPickerFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mDefaultKeyUpdater = new DefaultKeyUpdater((TelecomManager) context.getSystemService("telecom"));
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.widget.RadioButtonPickerFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.default_phone_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public List<DefaultAppInfo> getCandidates() {
        ArrayList arrayList = new ArrayList();
        List<String> installedDialerApplications = DefaultDialerManager.getInstalledDialerApplications(getContext(), this.mUserId);
        Context context = getContext();
        for (String str : installedDialerApplications) {
            try {
                arrayList.add(new DefaultAppInfo(context, this.mPm, UserHandle.myUserId(), this.mPm.getApplicationInfo(str, 0)));
            } catch (PackageManager.NameNotFoundException unused) {
            }
        }
        return arrayList;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public String getDefaultKey() {
        return this.mDefaultKeyUpdater.getDefaultDialerApplication(getContext(), this.mUserId);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public String getSystemDefaultKey() {
        return this.mDefaultKeyUpdater.getSystemDialerPackage();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public boolean setDefaultKey(String str) {
        if (TextUtils.isEmpty(str) || TextUtils.equals(str, getDefaultKey())) {
            return false;
        }
        return this.mDefaultKeyUpdater.setDefaultDialerApplication(getContext(), str, this.mUserId);
    }

    /* access modifiers changed from: package-private */
    public static class DefaultKeyUpdater {
        private final TelecomManager mTelecomManager;

        public DefaultKeyUpdater(TelecomManager telecomManager) {
            this.mTelecomManager = telecomManager;
        }

        public String getSystemDialerPackage() {
            return this.mTelecomManager.getSystemDialerPackage();
        }

        public String getDefaultDialerApplication(Context context, int i) {
            return DefaultDialerManager.getDefaultDialerApplication(context, i);
        }

        public boolean setDefaultDialerApplication(Context context, String str, int i) {
            return DefaultDialerManager.setDefaultDialerApplication(context, str, i);
        }
    }
}
