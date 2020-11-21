package com.android.settings.development;

import android.content.Context;
import android.os.SystemProperties;
import android.text.TextUtils;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import com.android.settings.C0003R$array;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
import com.android.settingslib.development.SystemPropPoker;

public class DebugGpuOverdrawPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    private final String[] mListSummaries;
    private final String[] mListValues;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "debug_hw_overdraw";
    }

    public DebugGpuOverdrawPreferenceController(Context context) {
        super(context);
        this.mListValues = context.getResources().getStringArray(C0003R$array.debug_hw_overdraw_values);
        this.mListSummaries = context.getResources().getStringArray(C0003R$array.debug_hw_overdraw_entries);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        writeDebugHwOverdrawOptions(obj);
        updateDebugHwOverdrawOptions();
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        updateDebugHwOverdrawOptions();
    }

    private void writeDebugHwOverdrawOptions(Object obj) {
        SystemProperties.set("debug.hwui.overdraw", obj == null ? "" : obj.toString());
        SystemPropPoker.getInstance().poke();
    }

    private void updateDebugHwOverdrawOptions() {
        String str = SystemProperties.get("debug.hwui.overdraw", "");
        int i = 0;
        int i2 = 0;
        while (true) {
            String[] strArr = this.mListValues;
            if (i2 >= strArr.length) {
                break;
            } else if (TextUtils.equals(str, strArr[i2])) {
                i = i2;
                break;
            } else {
                i2++;
            }
        }
        ListPreference listPreference = (ListPreference) this.mPreference;
        listPreference.setValue(this.mListValues[i]);
        listPreference.setSummary(this.mListSummaries[i]);
    }
}
