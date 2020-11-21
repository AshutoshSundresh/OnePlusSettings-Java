package com.oneplus.settings.product;

import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
import com.oneplus.settings.utils.OPUtils;

public class OPMemoryInfoController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private Context mContext;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "oneplus_memory_capacity";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return false;
    }

    public OPMemoryInfoController(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        try {
            preference.setSummary(OPUtils.getTotalMemory());
        } catch (RuntimeException unused) {
            preference.setSummary(this.mContext.getResources().getString(C0017R$string.device_info_default));
        }
    }
}
