package com.android.settings.wifi;

import android.content.Context;
import android.util.OpFeatures;
import com.android.settingslib.core.AbstractPreferenceController;

public class OPWapiCertManagePreferenceController extends AbstractPreferenceController {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "wapi_cert_manage";
    }

    public OPWapiCertManagePreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true ^ OpFeatures.isSupport(new int[]{1});
    }
}
