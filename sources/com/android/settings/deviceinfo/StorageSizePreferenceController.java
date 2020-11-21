package com.android.settings.deviceinfo;

import android.content.Context;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.Utils;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;

public class StorageSizePreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "key_storage_total_size";
    }

    public StorageSizePreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return Utils.isSupportCTPA(this.mContext);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Preference findPreference = preferenceScreen.findPreference(getPreferenceKey());
        if (isAvailable() && findPreference != null && findPreference.isVisible()) {
            String string = Utils.getString(this.mContext, "ext_ram_total_size");
            Log.d("StorageSizePreferenceController", "displayPreference: ramSize = " + string);
            if (string == null || string.isEmpty()) {
                findPreference.setSummary(this.mContext.getString(C0017R$string.device_info_default));
            } else {
                findPreference.setSummary(string);
            }
            Preference createNewPreference = createNewPreference(preferenceScreen.getContext());
            createNewPreference.setOrder(findPreference.getOrder() + 1);
            createNewPreference.setKey("key_storage_total_size1");
            preferenceScreen.addPreference(createNewPreference);
            createNewPreference.setVisible(true);
            createNewPreference.setTitle(this.mContext.getResources().getString(C0017R$string.rom_total_size));
            String string2 = Utils.getString(this.mContext, "ext_rom_total_size");
            Log.d("StorageSizePreferenceController", "displayPreference: romSize = " + string2);
            if (string2 == null || string2.isEmpty()) {
                createNewPreference.setSummary(this.mContext.getString(C0017R$string.device_info_default));
            } else {
                createNewPreference.setSummary(string2);
            }
        }
    }

    private Preference createNewPreference(Context context) {
        return new Preference(context);
    }
}
