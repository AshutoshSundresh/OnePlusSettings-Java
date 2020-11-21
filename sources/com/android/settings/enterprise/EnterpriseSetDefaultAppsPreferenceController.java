package com.android.settings.enterprise;

import android.content.Context;
import android.os.UserHandle;
import androidx.preference.Preference;
import com.android.settings.C0015R$plurals;
import com.android.settings.applications.ApplicationFeatureProvider;
import com.android.settings.applications.EnterpriseDefaultApps;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.users.UserFeatureProvider;
import com.android.settingslib.core.AbstractPreferenceController;

public class EnterpriseSetDefaultAppsPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private final ApplicationFeatureProvider mApplicationFeatureProvider;
    private final UserFeatureProvider mUserFeatureProvider;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "number_enterprise_set_default_apps";
    }

    public EnterpriseSetDefaultAppsPreferenceController(Context context) {
        super(context);
        FeatureFactory factory = FeatureFactory.getFactory(context);
        this.mApplicationFeatureProvider = factory.getApplicationFeatureProvider(context);
        this.mUserFeatureProvider = factory.getUserFeatureProvider(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        int numberOfEnterpriseSetDefaultApps = getNumberOfEnterpriseSetDefaultApps();
        preference.setSummary(this.mContext.getResources().getQuantityString(C0015R$plurals.enterprise_privacy_number_packages, numberOfEnterpriseSetDefaultApps, Integer.valueOf(numberOfEnterpriseSetDefaultApps)));
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return getNumberOfEnterpriseSetDefaultApps() > 0;
    }

    private int getNumberOfEnterpriseSetDefaultApps() {
        int i = 0;
        for (UserHandle userHandle : this.mUserFeatureProvider.getUserProfiles()) {
            for (EnterpriseDefaultApps enterpriseDefaultApps : EnterpriseDefaultApps.values()) {
                i += this.mApplicationFeatureProvider.findPersistentPreferredActivities(userHandle.getIdentifier(), enterpriseDefaultApps.getIntents()).size();
            }
        }
        return i;
    }
}
