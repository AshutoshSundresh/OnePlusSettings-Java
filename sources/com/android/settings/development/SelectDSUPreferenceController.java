package com.android.settings.development;

import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;

/* access modifiers changed from: package-private */
public class SelectDSUPreferenceController extends DeveloperOptionsPreferenceController implements PreferenceControllerMixin {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "dsu_loader";
    }

    SelectDSUPreferenceController(Context context) {
        super(context);
    }

    private boolean isDSURunning() {
        return SystemProperties.getBoolean("ro.gsid.image_running", false);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!"dsu_loader".equals(preference.getKey())) {
            return false;
        }
        if (isDSURunning()) {
            return true;
        }
        this.mContext.startActivity(new Intent(this.mContext, DSULoader.class));
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        preference.setSummary(this.mContext.getResources().getString(isDSURunning() ? C0017R$string.dsu_is_running : C0017R$string.dsu_loader_description));
    }
}
