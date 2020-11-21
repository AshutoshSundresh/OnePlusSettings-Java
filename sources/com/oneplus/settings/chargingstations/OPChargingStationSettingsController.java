package com.oneplus.settings.chargingstations;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import androidx.preference.SwitchPreference;
import com.android.settings.widget.SwitchWidgetController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.oneplus.settings.utils.OPUtils;

public class OPChargingStationSettingsController implements SwitchWidgetController.OnSwitchChangeListener, LifecycleObserver, OnStart, OnStop {
    private Context mContext;
    private ContentObserver mFeatureObserver = new ContentObserver(new Handler()) {
        /* class com.oneplus.settings.chargingstations.OPChargingStationSettingsController.AnonymousClass2 */

        public void onChange(boolean z, Uri uri) {
            SwitchWidgetController switchWidgetController = OPChargingStationSettingsController.this.mSwitchController;
            boolean z2 = false;
            if (OPChargingStationUtils.getIntSystemProperty(OPChargingStationSettingsController.this.mContext, "op_charging_stations_feature_on", 0) == 1) {
                z2 = true;
            }
            switchWidgetController.setChecked(z2);
        }
    };
    private final Uri mFeatureUri;
    private SwitchPreference mMuteNotifications;
    private final Uri mMuteUri;
    private ContentObserver mObserver = new ContentObserver(new Handler()) {
        /* class com.oneplus.settings.chargingstations.OPChargingStationSettingsController.AnonymousClass1 */

        public void onChange(boolean z, Uri uri) {
            SwitchPreference switchPreference = OPChargingStationSettingsController.this.mMuteNotifications;
            boolean z2 = false;
            if (OPChargingStationUtils.getIntSystemProperty(OPChargingStationSettingsController.this.mContext, "op_charging_stations_mute_notification", 0) == 1) {
                z2 = true;
            }
            switchPreference.setChecked(z2);
        }
    };
    private SwitchWidgetController mSwitchController;

    OPChargingStationSettingsController(Context context, SwitchWidgetController switchWidgetController, SwitchPreference switchPreference) {
        this.mContext = context;
        this.mSwitchController = switchWidgetController;
        this.mMuteNotifications = switchPreference;
        switchWidgetController.setupView();
        int intSystemProperty = OPChargingStationUtils.getIntSystemProperty(context, "op_charging_stations_feature_on", 0);
        int intSystemProperty2 = OPChargingStationUtils.getIntSystemProperty(context, "op_charging_stations_mute_notification", 0);
        boolean z = true;
        this.mSwitchController.setChecked(intSystemProperty == 1);
        this.mMuteNotifications.setChecked(intSystemProperty == 1 && intSystemProperty2 == 1);
        this.mMuteNotifications.setEnabled(intSystemProperty != 1 ? false : z);
        this.mSwitchController.setListener(this);
        this.mMuteUri = Settings.System.getUriFor("op_charging_stations_mute_notification");
        this.mFeatureUri = Settings.System.getUriFor("op_charging_stations_feature_on");
        this.mContext.getContentResolver().registerContentObserver(this.mMuteUri, false, this.mObserver);
        this.mContext.getContentResolver().registerContentObserver(this.mFeatureUri, false, this.mFeatureObserver);
    }

    @Override // com.android.settings.widget.SwitchWidgetController.OnSwitchChangeListener
    public boolean onSwitchToggled(boolean z) {
        OPChargingStationUtils.putIntSystemProperty(this.mContext, "op_charging_stations_feature_on", z ? 1 : 0);
        boolean z2 = false;
        int intSystemProperty = OPChargingStationUtils.getIntSystemProperty(this.mContext, "op_charging_stations_mute_notification", 0);
        this.mMuteNotifications.setEnabled(z);
        SwitchPreference switchPreference = this.mMuteNotifications;
        if (z && intSystemProperty == 1) {
            z2 = true;
        }
        switchPreference.setChecked(z2);
        OPChargingStationUtils.sendBroadcastToApp(this.mContext, z ? "type_enabled" : "type_undo");
        OPUtils.sendAnalytics("C22AG9UUDL", "settings_action", "settings_feature_enabled", z ? "on" : "off");
        if (!z) {
            OPChargingStationUtils.putStringSystemProperty(this.mContext, "op_charging_station_beacon_name", "");
        }
        return true;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        SwitchWidgetController switchWidgetController = this.mSwitchController;
        if (switchWidgetController != null) {
            switchWidgetController.startListening();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        SwitchWidgetController switchWidgetController = this.mSwitchController;
        if (switchWidgetController != null) {
            switchWidgetController.stopListening();
        }
    }
}
