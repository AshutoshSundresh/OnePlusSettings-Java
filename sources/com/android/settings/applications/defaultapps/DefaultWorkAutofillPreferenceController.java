package com.android.settings.applications.defaultapps;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import com.android.settings.Utils;
import com.android.settings.applications.defaultapps.DefaultAutofillPicker;
import com.android.settingslib.applications.DefaultAppInfo;

public class DefaultWorkAutofillPreferenceController extends DefaultAutofillPreferenceController {
    private final UserHandle mUserHandle = Utils.getManagedProfile(this.mUserManager);

    @Override // com.android.settings.applications.defaultapps.DefaultAutofillPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "default_autofill_work";
    }

    public DefaultWorkAutofillPreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settings.applications.defaultapps.DefaultAutofillPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        if (this.mUserHandle == null) {
            return false;
        }
        return super.isAvailable();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.defaultapps.DefaultAutofillPreferenceController, com.android.settings.applications.defaultapps.DefaultAppPreferenceController
    public DefaultAppInfo getDefaultAppInfo() {
        String stringForUser = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), "autofill_service", this.mUserHandle.getIdentifier());
        if (!TextUtils.isEmpty(stringForUser)) {
            return new DefaultAppInfo(this.mContext, this.mPackageManager, this.mUserHandle.getIdentifier(), ComponentName.unflattenFromString(stringForUser));
        }
        return null;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.defaultapps.DefaultAutofillPreferenceController, com.android.settings.applications.defaultapps.DefaultAppPreferenceController
    public Intent getSettingIntent(DefaultAppInfo defaultAppInfo) {
        if (defaultAppInfo == null) {
            return null;
        }
        return new DefaultAutofillPicker.AutofillSettingIntentProvider(this.mContext, this.mUserHandle.getIdentifier(), defaultAppInfo.getKey()).getIntent();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.defaultapps.DefaultAppPreferenceController
    public void startActivity(Intent intent) {
        this.mContext.startActivityAsUser(intent, this.mUserHandle);
    }
}
