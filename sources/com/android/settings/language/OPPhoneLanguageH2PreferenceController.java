package com.android.settings.language;

import android.content.Context;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.C0005R$bool;
import com.android.settings.C0017R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.localepicker.OPLocalePicker;
import com.android.settingslib.core.AbstractPreferenceController;
import com.oneplus.settings.utils.OPUtils;
import java.util.List;

public class OPPhoneLanguageH2PreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "phone_language_h2os";
    }

    public OPPhoneLanguageH2PreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        boolean z = this.mContext.getResources().getBoolean(C0005R$bool.config_show_phone_language);
        long length = (long) this.mContext.getAssets().getLocales().length;
        boolean isO2 = OPUtils.isO2();
        Log.d("OPPhoneLanguageH2PreferenceController", "config_show_phone_language:" + z + ", length:" + length + ", isO2:" + isO2);
        return z && length > 1 && !isO2;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (preference != null) {
            preference.setSummary(this.mContext.getString(C0017R$string.language_summary_h2));
        }
    }

    @Override // com.android.settings.core.PreferenceControllerMixin
    public void updateNonIndexableKeys(List<String> list) {
        list.add(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        int i;
        if (!"phone_language_h2os".equals(preference.getKey())) {
            return false;
        }
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(this.mContext);
        subSettingLauncher.setDestination(OPLocalePicker.class.getName());
        subSettingLauncher.setSourceMetricsCategory(750);
        if (OPUtils.isO2()) {
            i = C0017R$string.language_picker_title_o2;
        } else {
            i = C0017R$string.language_picker_title;
        }
        subSettingLauncher.setTitleRes(i);
        subSettingLauncher.launch();
        return true;
    }
}
