package com.android.settings.development;

import android.content.Context;
import android.os.SystemProperties;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.C0005R$bool;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;

public class CameraLaserSensorPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    static final String BUILD_TYPE = "ro.build.type";
    static final int DISABLED = 2;
    static final int ENABLED = 0;
    static final String ENG_BUILD = "eng";
    static final String PROPERTY_CAMERA_LASER_SENSOR = "persist.camera.stats.disablehaf";
    static final String USERDEBUG_BUILD = "userdebug";
    static final String USER_BUILD = "user";

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "camera_laser_sensor_switch";
    }

    public CameraLaserSensorPreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settingslib.development.DeveloperOptionsPreferenceController
    public boolean isAvailable() {
        return this.mContext.getResources().getBoolean(C0005R$bool.config_show_camera_laser_sensor);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        SystemProperties.set(PROPERTY_CAMERA_LASER_SENSOR, Integer.toString(((Boolean) obj).booleanValue() ? 0 : 2));
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        ((SwitchPreference) this.mPreference).setChecked(isLaserSensorEnabled());
    }

    private boolean isLaserSensorEnabled() {
        return TextUtils.equals(Integer.toString(0), SystemProperties.get(PROPERTY_CAMERA_LASER_SENSOR, Integer.toString(0)));
    }
}
