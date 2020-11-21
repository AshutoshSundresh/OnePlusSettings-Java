package com.android.settings.deviceinfo.aboutphone;

import android.content.Context;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
import com.oneplus.settings.utils.ProductUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class OPLastFactoryResetPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private Context mContext;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "last_factory_reset";
    }

    public OPLastFactoryResetPreferenceController(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return ProductUtils.isUsvMode();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Preference findPreference = preferenceScreen.findPreference(getPreferenceKey());
        String fileContent = getFileContent("/mnt/vendor/op2/last_factory_reset");
        Log.d("OPLastFactoryResetPreferenceController", "lastFRTime = " + fileContent);
        if (fileContent == "") {
            fileContent = this.mContext.getResources().getString(C0017R$string.last_factory_reset_none);
        }
        findPreference.setSummary(fileContent);
    }

    private String getFileContent(String str) {
        File file = new File(str);
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            while (fileInputStream.available() > 0) {
                Log.d("OPLastFactoryResetPreferenceController", "fileInputStream.available");
                sb.append((char) fileInputStream.read());
            }
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            Log.e("OPLastFactoryResetPreferenceController", "Exception ", e);
        } catch (IOException e2) {
            Log.e("OPLastFactoryResetPreferenceController", "Exception ", e2);
        }
        return sb.toString();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
    }
}
