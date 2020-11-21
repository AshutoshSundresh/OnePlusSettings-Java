package com.android.settings.development;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.provider.Settings;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;

public class SimulateColorSpacePreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    static final int SETTING_VALUE_OFF = 0;
    static final int SETTING_VALUE_ON = 1;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "simulate_color_space";
    }

    public SimulateColorSpacePreferenceController(Context context) {
        super(context);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        writeSimulateColorSpace(obj);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        updateSimulateColorSpace();
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsDisabled() {
        super.onDeveloperOptionsDisabled();
        if (usingDevelopmentColorSpace()) {
            writeSimulateColorSpace(-1);
        }
    }

    private void updateSimulateColorSpace() {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        boolean z = Settings.Secure.getInt(contentResolver, "accessibility_display_daltonizer_enabled", 0) != 0;
        ListPreference listPreference = (ListPreference) this.mPreference;
        if (z) {
            String num = Integer.toString(Settings.Secure.getInt(contentResolver, "accessibility_display_daltonizer", -1));
            listPreference.setValue(num);
            if (listPreference.findIndexOfValue(num) < 0) {
                Resources resources = this.mContext.getResources();
                listPreference.setSummary(resources.getString(C0017R$string.daltonizer_type_overridden, resources.getString(C0017R$string.accessibility_display_daltonizer_preference_title)));
                return;
            }
            listPreference.setSummary("%s");
            return;
        }
        listPreference.setValue(Integer.toString(-1));
    }

    private void writeSimulateColorSpace(Object obj) {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        int parseInt = Integer.parseInt(obj.toString());
        if (parseInt < 0) {
            Settings.Secure.putInt(contentResolver, "accessibility_display_daltonizer_enabled", 0);
            return;
        }
        Settings.Secure.putInt(contentResolver, "accessibility_display_daltonizer_enabled", 1);
        Settings.Secure.putInt(contentResolver, "accessibility_display_daltonizer", parseInt);
    }

    private boolean usingDevelopmentColorSpace() {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        if (Settings.Secure.getInt(contentResolver, "accessibility_display_daltonizer_enabled", 0) != 0) {
            if (((ListPreference) this.mPreference).findIndexOfValue(Integer.toString(Settings.Secure.getInt(contentResolver, "accessibility_display_daltonizer", -1))) >= 0) {
                return true;
            }
        }
        return false;
    }
}
