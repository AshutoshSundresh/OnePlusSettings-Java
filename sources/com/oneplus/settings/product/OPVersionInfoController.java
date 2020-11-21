package com.oneplus.settings.product;

import android.content.Context;
import android.os.SystemProperties;
import android.util.OpFeatures;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;

public class OPVersionInfoController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private Context mContext;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "oneplus_oos_version";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return false;
    }

    public OPVersionInfoController(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (OpFeatures.isSupport(new int[]{1})) {
            preference.setTitle(this.mContext.getResources().getString(C0017R$string.oneplus_oxygen_version));
            preference.setSummary(SystemProperties.get("ro.oxygen.version", this.mContext.getResources().getString(C0017R$string.device_info_default)).replace("O2", "O₂"));
            return;
        }
        preference.setTitle(this.mContext.getResources().getString(C0017R$string.oneplus_hydrogen_version).replace("H2", "H₂"));
        preference.setSummary(SystemProperties.get("ro.rom.version", this.mContext.getResources().getString(C0017R$string.device_info_default)).replace("H2", "H₂"));
    }
}
