package com.android.settings.deviceinfo;

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

public class WarrantCodePreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private Context mContext;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "warranty_code";
    }

    public WarrantCodePreferenceController(Context context) {
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
        String fileContent = getFileContent("/mnt/vendor/op2/warrantyTime");
        Log.d("WarrantCodePreferenceController", "lastFRTime = " + fileContent);
        if (fileContent == "") {
            fileContent = this.mContext.getString(C0017R$string.last_factory_reset_none);
        }
        findPreference.setSummary(fileContent);
    }

    private String getFileContent(String str) {
        File file = new File(str);
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            while (fileInputStream.available() > 0) {
                Log.d("WarrantCodePreferenceController", "fileInputStream.available");
                sb.append((char) fileInputStream.read());
            }
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            Log.e("WarrantCodePreferenceController", "Exception ", e);
        } catch (IOException e2) {
            Log.e("WarrantCodePreferenceController", "Exception ", e2);
        }
        return sb.toString();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
    }
}
