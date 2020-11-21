package com.android.settingslib.development;

import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settingslib.R$array;

public abstract class AbstractLogdSizePreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener {
    static final String DEFAULT_SNET_TAG = "I";
    static final String LOW_RAM_CONFIG_PROPERTY_KEY = "ro.config.low_ram";
    static final String SELECT_LOGD_DEFAULT_SIZE_VALUE = "262144";
    static final String SELECT_LOGD_MINIMUM_SIZE_VALUE = "65536";
    static final String SELECT_LOGD_SIZE_PROPERTY = "persist.logd.size";
    static final String SELECT_LOGD_SNET_TAG_PROPERTY = "persist.log.tag.snet_event_log";
    private ListPreference mLogdSize;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "select_logd_size";
    }

    public AbstractLogdSizePreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (isAvailable()) {
            this.mLogdSize = (ListPreference) preferenceScreen.findPreference("select_logd_size");
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference != this.mLogdSize) {
            return false;
        }
        writeLogdSizeOption(obj);
        return true;
    }

    private String defaultLogdSizeValue() {
        String str = SystemProperties.get("ro.logd.size");
        if (str == null || str.length() == 0) {
            return SystemProperties.get(LOW_RAM_CONFIG_PROPERTY_KEY).equals("true") ? SELECT_LOGD_MINIMUM_SIZE_VALUE : SELECT_LOGD_DEFAULT_SIZE_VALUE;
        }
        return str;
    }

    public void updateLogdSizeValues() {
        if (this.mLogdSize != null) {
            String str = SystemProperties.get("persist.log.tag");
            String str2 = SystemProperties.get(SELECT_LOGD_SIZE_PROPERTY);
            if (str != null && str.startsWith("Settings")) {
                str2 = "32768";
            }
            LocalBroadcastManager.getInstance(this.mContext).sendBroadcastSync(new Intent("com.android.settingslib.development.AbstractLogdSizePreferenceController.LOGD_SIZE_UPDATED").putExtra("CURRENT_LOGD_VALUE", str2));
            if (str2 == null || str2.length() == 0) {
                str2 = defaultLogdSizeValue();
            }
            String[] stringArray = this.mContext.getResources().getStringArray(R$array.select_logd_size_values);
            String[] stringArray2 = this.mContext.getResources().getStringArray(R$array.select_logd_size_titles);
            int i = 2;
            if (SystemProperties.get(LOW_RAM_CONFIG_PROPERTY_KEY).equals("true")) {
                this.mLogdSize.setEntries(R$array.select_logd_size_lowram_titles);
                stringArray2 = this.mContext.getResources().getStringArray(R$array.select_logd_size_lowram_titles);
                i = 1;
            }
            String[] stringArray3 = this.mContext.getResources().getStringArray(R$array.select_logd_size_summaries);
            int i2 = 0;
            while (true) {
                if (i2 >= stringArray2.length) {
                    break;
                } else if (str2.equals(stringArray[i2]) || str2.equals(stringArray2[i2])) {
                    i = i2;
                } else {
                    i2++;
                }
            }
            i = i2;
            this.mLogdSize.setValue(stringArray[i]);
            this.mLogdSize.setSummary(stringArray3[i]);
        }
    }

    public void writeLogdSizeOption(Object obj) {
        String str;
        boolean z = obj != null && obj.toString().equals("32768");
        String str2 = SystemProperties.get("persist.log.tag");
        String str3 = "";
        if (str2 == null) {
            str2 = str3;
        }
        String replaceFirst = str2.replaceAll(",+Settings", str3).replaceFirst("^Settings,*", str3).replaceAll(",+", ",").replaceFirst(",+$", str3);
        if (z) {
            String str4 = SystemProperties.get(SELECT_LOGD_SNET_TAG_PROPERTY);
            if ((str4 == null || str4.length() == 0) && ((str = SystemProperties.get("log.tag.snet_event_log")) == null || str.length() == 0)) {
                SystemProperties.set(SELECT_LOGD_SNET_TAG_PROPERTY, DEFAULT_SNET_TAG);
            }
            if (replaceFirst.length() != 0) {
                replaceFirst = "," + replaceFirst;
            }
            replaceFirst = "Settings" + replaceFirst;
            obj = SELECT_LOGD_MINIMUM_SIZE_VALUE;
        }
        if (!replaceFirst.equals(str2)) {
            SystemProperties.set("persist.log.tag", replaceFirst);
        }
        String defaultLogdSizeValue = defaultLogdSizeValue();
        String obj2 = (obj == null || obj.toString().length() == 0) ? defaultLogdSizeValue : obj.toString();
        if (!defaultLogdSizeValue.equals(obj2)) {
            str3 = obj2;
        }
        SystemProperties.set(SELECT_LOGD_SIZE_PROPERTY, str3);
        SystemProperties.set("ctl.start", "logd-reinit");
        SystemPropPoker.getInstance().poke();
        updateLogdSizeValues();
    }
}
