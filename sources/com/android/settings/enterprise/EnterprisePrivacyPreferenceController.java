package com.android.settings.enterprise;

import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.AbstractPreferenceController;

public class EnterprisePrivacyPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private final EnterprisePrivacyFeatureProvider mFeatureProvider;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "enterprise_privacy";
    }

    public EnterprisePrivacyPreferenceController(Context context) {
        super(context);
        this.mFeatureProvider = FeatureFactory.getFactory(context).getEnterprisePrivacyFeatureProvider(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (preference != null) {
            String deviceOwnerOrganizationName = this.mFeatureProvider.getDeviceOwnerOrganizationName();
            if (deviceOwnerOrganizationName == null) {
                preference.setSummary(C0017R$string.enterprise_privacy_settings_summary_generic);
                return;
            }
            preference.setSummary(this.mContext.getResources().getString(C0017R$string.enterprise_privacy_settings_summary_with_name, deviceOwnerOrganizationName));
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mFeatureProvider.hasDeviceOwner();
    }
}
