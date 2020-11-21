package com.android.settings.enterprise;

import android.content.Context;
import android.text.format.DateUtils;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.Date;

public abstract class AdminActionPreferenceControllerBase extends AbstractPreferenceController implements PreferenceControllerMixin {
    protected final EnterprisePrivacyFeatureProvider mFeatureProvider;

    /* access modifiers changed from: protected */
    public abstract Date getAdminActionTimestamp();

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public AdminActionPreferenceControllerBase(Context context) {
        super(context);
        this.mFeatureProvider = FeatureFactory.getFactory(context).getEnterprisePrivacyFeatureProvider(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        String str;
        Date adminActionTimestamp = getAdminActionTimestamp();
        if (adminActionTimestamp == null) {
            str = this.mContext.getString(C0017R$string.enterprise_privacy_none);
        } else {
            str = DateUtils.formatDateTime(this.mContext, adminActionTimestamp.getTime(), 17);
        }
        preference.setSummary(str);
    }
}
