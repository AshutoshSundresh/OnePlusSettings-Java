package com.android.settings.enterprise;

import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;

public class CaCertsCurrentUserPreferenceController extends CaCertsPreferenceControllerBase {
    static final String CA_CERTS_CURRENT_USER = "ca_certs_current_user";

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return CA_CERTS_CURRENT_USER;
    }

    public CaCertsCurrentUserPreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settings.enterprise.CaCertsPreferenceControllerBase, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        int i;
        super.updateState(preference);
        if (this.mFeatureProvider.isInCompMode()) {
            i = C0017R$string.enterprise_privacy_ca_certs_personal;
        } else {
            i = C0017R$string.enterprise_privacy_ca_certs_device;
        }
        preference.setTitle(i);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.enterprise.CaCertsPreferenceControllerBase
    public int getNumberOfCaCerts() {
        return this.mFeatureProvider.getNumberOfOwnerInstalledCaCertsForCurrentUser();
    }
}
