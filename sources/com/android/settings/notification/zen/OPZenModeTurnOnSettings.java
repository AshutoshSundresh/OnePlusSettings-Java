package com.android.settings.notification.zen;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.service.notification.ZenModeConfig;
import android.text.TextUtils;
import android.util.Log;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.C0017R$string;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.notification.EnableZenModeDialog;
import com.oneplus.settings.utils.OPUtils;

public class OPZenModeTurnOnSettings extends AbstractZenModePreferenceController implements LifecycleObserver, OnResume, OnPause {
    private SettingObserver mSettingObserver;
    SwitchPreference mSwitchPreference;
    DialogInterface.OnDismissListener onDismissListener = new DialogInterface.OnDismissListener() {
        /* class com.android.settings.notification.zen.OPZenModeTurnOnSettings.AnonymousClass1 */

        public void onDismiss(DialogInterface dialogInterface) {
            OPZenModeTurnOnSettings oPZenModeTurnOnSettings = OPZenModeTurnOnSettings.this;
            oPZenModeTurnOnSettings.updateState(oPZenModeTurnOnSettings.mSwitchPreference);
        }
    };

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenModePreferenceController
    public String getPreferenceKey() {
        return "zen_turn_on";
    }

    public OPZenModeTurnOnSettings(Context context, Lifecycle lifecycle, FragmentManager fragmentManager) {
        super(context, "zen_turn_on", lifecycle);
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return OPUtils.isSupportSocTriState();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        int zenMode = getZenMode();
        if (zenMode == 0) {
            this.mSwitchPreference.setChecked(false);
            this.mSwitchPreference.setSummary(C0017R$string.oneplus_zen_mode_mode_manual_switch_summary);
        } else if (zenMode == 1 || zenMode == 2 || zenMode == 3) {
            this.mSwitchPreference.setChecked(true);
            this.mSwitchPreference.setSummary(getPreferenceSummary());
        } else {
            this.mSwitchPreference.setChecked(false);
            this.mSwitchPreference.setSummary(getPreferenceSummary());
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenModePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mSwitchPreference = (SwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
        Preference findPreference = preferenceScreen.findPreference("zen_turn_on");
        if (findPreference != null) {
            this.mSettingObserver = new SettingObserver(findPreference);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume, com.android.settings.notification.zen.AbstractZenModePreferenceController
    public void onResume() {
        SettingObserver settingObserver = this.mSettingObserver;
        if (settingObserver != null) {
            settingObserver.register(this.mContext.getContentResolver());
            this.mSettingObserver.onChange(false, null);
        }
        updateState(this.mSwitchPreference);
    }

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        SettingObserver settingObserver = this.mSettingObserver;
        if (settingObserver != null) {
            settingObserver.unregister(this.mContext.getContentResolver());
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals(preference.getKey(), "zen_turn_on") || !(preference instanceof SwitchPreference)) {
            return false;
        }
        if (((SwitchPreference) preference).isChecked()) {
            Log.d("OPZenModeTurnOnSettings", "Click true");
            int zenDuration = getZenDuration();
            if (zenDuration == -1) {
                Dialog createDialog = new EnableZenModeDialog(this.mContext).createDialog();
                createDialog.setOnDismissListener(this.onDismissListener);
                createDialog.show();
            } else if (zenDuration != 0) {
                this.mBackend.setZenModeForDuration(zenDuration);
            } else {
                this.mBackend.setZenMode(1);
            }
        } else {
            Log.d("OPZenModeTurnOnSettings", "Click off");
            this.mBackend.setZenMode(0);
        }
        return true;
    }

    class SettingObserver extends ContentObserver {
        private final Uri ZEN_MODE_CONFIG_ETAG_URI = Settings.Global.getUriFor("zen_mode_config_etag");
        private final Uri ZEN_MODE_DURATION_URI = Settings.Global.getUriFor("zen_duration");
        private final Uri ZEN_MODE_URI = Settings.Global.getUriFor("zen_mode");
        private final Preference mPreference;

        public SettingObserver(Preference preference) {
            super(new Handler());
            this.mPreference = preference;
        }

        public void register(ContentResolver contentResolver) {
            contentResolver.registerContentObserver(this.ZEN_MODE_URI, false, this, -1);
            contentResolver.registerContentObserver(this.ZEN_MODE_CONFIG_ETAG_URI, false, this, -1);
            contentResolver.registerContentObserver(this.ZEN_MODE_DURATION_URI, false, this, -1);
        }

        public void unregister(ContentResolver contentResolver) {
            contentResolver.unregisterContentObserver(this);
        }

        public void onChange(boolean z, Uri uri) {
            super.onChange(z, uri);
            if (uri == null || this.ZEN_MODE_URI.equals(uri) || this.ZEN_MODE_CONFIG_ETAG_URI.equals(uri) || this.ZEN_MODE_DURATION_URI.equals(uri)) {
                OPZenModeTurnOnSettings.this.mBackend.updatePolicy();
                OPZenModeTurnOnSettings.this.mBackend.updateZenMode();
                OPZenModeTurnOnSettings.this.updateState(this.mPreference);
            }
        }
    }

    /* access modifiers changed from: protected */
    public String getPreferenceSummary() {
        ZenModeConfig zenModeConfig = getZenModeConfig();
        ZenModeConfig.ZenRule zenRule = zenModeConfig.manualRule;
        String str = "";
        long j = -1;
        if (zenRule != null) {
            Uri uri = zenRule.conditionId;
            String str2 = zenRule.enabler;
            if (str2 != null) {
                String ownerCaption = AbstractZenModePreferenceController.mZenModeConfigWrapper.getOwnerCaption(str2);
                if (!ownerCaption.isEmpty()) {
                    str = this.mContext.getString(C0017R$string.zen_mode_settings_dnd_automatic_rule_app, ownerCaption);
                }
            } else if (uri == null) {
                return this.mContext.getString(C0017R$string.zen_mode_settings_dnd_manual_indefinite);
            } else {
                j = AbstractZenModePreferenceController.mZenModeConfigWrapper.parseManualRuleTime(uri);
                if (j > 0) {
                    CharSequence formattedTime = AbstractZenModePreferenceController.mZenModeConfigWrapper.getFormattedTime(j, this.mContext.getUserId());
                    str = this.mContext.getString(C0017R$string.zen_mode_settings_dnd_manual_end_time, formattedTime);
                }
            }
        }
        for (ZenModeConfig.ZenRule zenRule2 : zenModeConfig.automaticRules.values()) {
            if (zenRule2.isAutomaticActive()) {
                if (!AbstractZenModePreferenceController.mZenModeConfigWrapper.isTimeRule(zenRule2.conditionId)) {
                    return this.mContext.getString(C0017R$string.zen_mode_settings_dnd_automatic_rule, zenRule2.name);
                }
                long parseAutomaticRuleEndTime = AbstractZenModePreferenceController.mZenModeConfigWrapper.parseAutomaticRuleEndTime(zenRule2.conditionId);
                if (parseAutomaticRuleEndTime > j) {
                    str = this.mContext.getString(C0017R$string.zen_mode_settings_dnd_automatic_rule, zenRule2.name);
                    j = parseAutomaticRuleEndTime;
                }
            }
        }
        return str;
    }
}
