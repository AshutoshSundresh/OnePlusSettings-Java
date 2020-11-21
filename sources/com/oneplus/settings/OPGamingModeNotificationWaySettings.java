package com.oneplus.settings;

import android.app.Application;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import androidx.preference.Preference;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.ui.RadioButtonPreference;
import com.android.settingslib.utils.ThreadUtils;
import com.oneplus.settings.OPGamingModeNotificationWaySettings;
import com.oneplus.settings.ui.OPGamingModeNotificationWayCategory;
import com.oneplus.settings.utils.OPUtils;

public class OPGamingModeNotificationWaySettings extends SettingsPreferenceFragment implements RadioButtonPreference.OnClickListener, Preference.OnPreferenceChangeListener {
    private Handler mHandler = new Handler();
    private OPGamingModeNotificationWayCategory mOPGamingModeNotificationWayCategory;
    private final SettingsObserver mSettingsObserver = new SettingsObserver();
    private RadioButtonPreference mShieldingNotification;
    private RadioButtonPreference mSuspensionNotice;
    private RadioButtonPreference mWeakTextReminding;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        return false;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.op_gamingmode_notification_way_settings);
        Application application = SettingsBaseApplication.mApplication;
        this.mSuspensionNotice = (RadioButtonPreference) findPreference("suspension_notice");
        this.mWeakTextReminding = (RadioButtonPreference) findPreference("weak_text_reminding");
        this.mShieldingNotification = (RadioButtonPreference) findPreference("shielding_notification");
        this.mSuspensionNotice.setOnClickListener(this);
        this.mWeakTextReminding.setOnClickListener(this);
        this.mShieldingNotification.setOnClickListener(this);
        this.mOPGamingModeNotificationWayCategory = (OPGamingModeNotificationWayCategory) findPreference("oneplus_instrucitons");
    }

    private void setGamingModeNotificationWayValue(int i) {
        Settings.System.putIntForUser(getContentResolver(), "game_mode_block_notification", i, -2);
        this.mOPGamingModeNotificationWayCategory.setAnimTypes(i);
    }

    @Override // com.android.settings.ui.RadioButtonPreference.OnClickListener
    public void onRadioButtonClicked(RadioButtonPreference radioButtonPreference) {
        RadioButtonPreference radioButtonPreference2 = this.mSuspensionNotice;
        if (radioButtonPreference == radioButtonPreference2) {
            radioButtonPreference2.setChecked(true);
            this.mWeakTextReminding.setChecked(false);
            this.mShieldingNotification.setChecked(false);
            setGamingModeNotificationWayValue(0);
        } else if (radioButtonPreference == this.mWeakTextReminding) {
            radioButtonPreference2.setChecked(false);
            this.mWeakTextReminding.setChecked(true);
            this.mShieldingNotification.setChecked(false);
            setGamingModeNotificationWayValue(2);
        } else if (radioButtonPreference == this.mShieldingNotification) {
            radioButtonPreference2.setChecked(false);
            this.mWeakTextReminding.setChecked(false);
            this.mShieldingNotification.setChecked(true);
            setGamingModeNotificationWayValue(1);
        }
        OPUtils.sendAppTrackerForGameModeNotificationShow();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        updateUI();
        super.onResume();
        disableOptionsInEsportsMode();
        this.mSettingsObserver.register(true);
        OPGamingModeNotificationWayCategory oPGamingModeNotificationWayCategory = this.mOPGamingModeNotificationWayCategory;
        if (oPGamingModeNotificationWayCategory != null) {
            oPGamingModeNotificationWayCategory.startAnim();
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        this.mSettingsObserver.register(false);
        OPGamingModeNotificationWayCategory oPGamingModeNotificationWayCategory = this.mOPGamingModeNotificationWayCategory;
        if (oPGamingModeNotificationWayCategory != null) {
            oPGamingModeNotificationWayCategory.stopAnim();
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void disableOptionsInEsportsMode() {
        boolean z = !isEsportsMode();
        RadioButtonPreference radioButtonPreference = this.mSuspensionNotice;
        if (radioButtonPreference != null) {
            radioButtonPreference.setEnabled(z);
        }
        RadioButtonPreference radioButtonPreference2 = this.mWeakTextReminding;
        if (radioButtonPreference2 != null) {
            radioButtonPreference2.setEnabled(z);
        }
        RadioButtonPreference radioButtonPreference3 = this.mShieldingNotification;
        if (radioButtonPreference3 != null) {
            radioButtonPreference3.setEnabled(z);
        }
    }

    private boolean isEsportsMode() {
        return "1".equals(Settings.System.getStringForUser(getContentResolver(), "esport_mode_enabled", -2));
    }

    /* access modifiers changed from: private */
    public final class SettingsObserver extends ContentObserver {
        private final Uri ESPORTSMODE_URI = Settings.System.getUriFor("esport_mode_enabled");

        public SettingsObserver() {
            super(OPGamingModeNotificationWaySettings.this.mHandler);
        }

        public void register(boolean z) {
            ContentResolver contentResolver = OPGamingModeNotificationWaySettings.this.getContentResolver();
            if (z) {
                contentResolver.registerContentObserver(this.ESPORTSMODE_URI, false, this);
            } else {
                contentResolver.unregisterContentObserver(this);
            }
        }

        public void onChange(boolean z, Uri uri) {
            super.onChange(z, uri);
            if (this.ESPORTSMODE_URI.equals(uri)) {
                ThreadUtils.postOnMainThread(new Runnable() {
                    /* class com.oneplus.settings.$$Lambda$OPGamingModeNotificationWaySettings$SettingsObserver$AcDQ8_nycEAymWaebuoz_lQERE */

                    public final void run() {
                        OPGamingModeNotificationWaySettings.SettingsObserver.this.lambda$onChange$0$OPGamingModeNotificationWaySettings$SettingsObserver();
                    }
                });
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onChange$0 */
        public /* synthetic */ void lambda$onChange$0$OPGamingModeNotificationWaySettings$SettingsObserver() {
            OPGamingModeNotificationWaySettings.this.disableOptionsInEsportsMode();
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onDestroy() {
        super.onDestroy();
        OPGamingModeNotificationWayCategory oPGamingModeNotificationWayCategory = this.mOPGamingModeNotificationWayCategory;
        if (oPGamingModeNotificationWayCategory != null) {
            oPGamingModeNotificationWayCategory.releaseAnim();
        }
    }

    private void updateUI() {
        boolean z = false;
        int intForUser = Settings.System.getIntForUser(getContentResolver(), "game_mode_block_notification", 0, -2);
        this.mSuspensionNotice.setChecked(intForUser == 0);
        this.mWeakTextReminding.setChecked(intForUser == 2);
        RadioButtonPreference radioButtonPreference = this.mShieldingNotification;
        if (intForUser == 1) {
            z = true;
        }
        radioButtonPreference.setChecked(z);
        this.mSuspensionNotice.setEnabled(true);
        this.mWeakTextReminding.setEnabled(true);
        this.mShieldingNotification.setEnabled(true);
    }
}
