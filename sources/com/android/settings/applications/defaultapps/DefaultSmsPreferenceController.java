package com.android.settings.applications.defaultapps;

import android.content.ComponentName;
import android.content.Context;
import android.telephony.TelephonyManager;
import com.android.internal.telephony.SmsApplication;
import com.android.settingslib.applications.DefaultAppInfo;

public class DefaultSmsPreferenceController extends DefaultAppPreferenceController {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "default_sms_app";
    }

    public DefaultSmsPreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return !this.mUserManager.getUserInfo(this.mUserId).isRestricted() && ((TelephonyManager) this.mContext.getSystemService("phone")).isSmsCapable();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.defaultapps.DefaultAppPreferenceController
    public DefaultAppInfo getDefaultAppInfo() {
        ComponentName defaultSmsApplication = SmsApplication.getDefaultSmsApplication(this.mContext, true);
        if (defaultSmsApplication != null) {
            return new DefaultAppInfo(this.mContext, this.mPackageManager, this.mUserId, defaultSmsApplication);
        }
        return null;
    }
}
