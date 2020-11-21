package com.android.settings.applications.defaultapps;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.telecom.DefaultDialerManager;
import android.telephony.TelephonyManager;
import com.android.settingslib.applications.DefaultAppInfo;
import java.util.List;

public class DefaultPhonePreferenceController extends DefaultAppPreferenceController {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "default_phone_app";
    }

    public DefaultPhonePreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        List<String> candidates;
        if (((TelephonyManager) this.mContext.getSystemService("phone")).isVoiceCapable() && !((UserManager) this.mContext.getSystemService("user")).hasUserRestriction("no_outgoing_calls") && (candidates = getCandidates()) != null && !candidates.isEmpty()) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.defaultapps.DefaultAppPreferenceController
    public DefaultAppInfo getDefaultAppInfo() {
        try {
            return new DefaultAppInfo(this.mContext, this.mPackageManager, UserHandle.myUserId(), this.mPackageManager.getApplicationInfo(DefaultDialerManager.getDefaultDialerApplication(this.mContext, this.mUserId), 0));
        } catch (PackageManager.NameNotFoundException unused) {
            return null;
        }
    }

    private List<String> getCandidates() {
        return DefaultDialerManager.getInstalledDialerApplications(this.mContext, this.mUserId);
    }
}
