package com.oneplus.settings.notification;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.ui.RadioButtonPreference;

public class OPSMSNotificationVibrateIntensitySettings extends SettingsPreferenceFragment implements RadioButtonPreference.OnClickListener {
    private Context mContext;
    private RadioButtonPreference mIntenselyPreference;
    private RadioButtonPreference mLightPreference;
    private RadioButtonPreference mModeratePreference;
    private Vibrator mVibrator;
    private long[][] sNoticeVibrateIntensity = {new long[]{-1, 0, 100, 150, 100, 1000, 100, 150, 100}, new long[]{-2, 0, 100, 150, 100, 1000, 100, 150, 100}, new long[]{-3, 0, 100, 150, 100, 1000, 100, 150, 100}};

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return 0;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = getActivity();
        addPreferencesFromResource(C0019R$xml.op_smsnotification_vibrate_intensity);
        Vibrator vibrator = (Vibrator) this.mContext.getSystemService("vibrator");
        this.mVibrator = vibrator;
        if (vibrator != null && !vibrator.hasVibrator()) {
            this.mVibrator = null;
        }
        this.mContext.getPackageManager().hasSystemFeature("oem.linear.motor.support");
        this.mLightPreference = (RadioButtonPreference) findPreference("light");
        this.mModeratePreference = (RadioButtonPreference) findPreference("moderate");
        this.mIntenselyPreference = (RadioButtonPreference) findPreference("intensely");
        this.mLightPreference.setOnClickListener(this);
        this.mModeratePreference.setOnClickListener(this);
        this.mIntenselyPreference.setOnClickListener(this);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        updateSmsNotificationVibarateIntensityStatus(Settings.System.getInt(this.mContext.getContentResolver(), "notice_vibrate_intensity", 0));
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        Vibrator vibrator = this.mVibrator;
        if (vibrator != null) {
            try {
                vibrator.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setSmsNotificationVibarateIntensityValue(int i) {
        Settings.System.putInt(this.mContext.getContentResolver(), "notice_vibrate_intensity", i);
        Vibrator vibrator = this.mVibrator;
        if (vibrator != null) {
            vibrator.cancel();
            this.mVibrator.vibrate(this.sNoticeVibrateIntensity[i], -1);
        }
    }

    private void updateSmsNotificationVibarateIntensityStatus(int i) {
        boolean z = false;
        this.mLightPreference.setChecked(i == 0);
        this.mModeratePreference.setChecked(i == 1);
        RadioButtonPreference radioButtonPreference = this.mIntenselyPreference;
        if (i == 2) {
            z = true;
        }
        radioButtonPreference.setChecked(z);
    }

    @Override // com.android.settings.ui.RadioButtonPreference.OnClickListener
    public void onRadioButtonClicked(RadioButtonPreference radioButtonPreference) {
        String key = radioButtonPreference.getKey();
        if ("light".equals(key)) {
            setSmsNotificationVibarateIntensityValue(0);
            updateSmsNotificationVibarateIntensityStatus(0);
        } else if ("moderate".equals(key)) {
            setSmsNotificationVibarateIntensityValue(1);
            updateSmsNotificationVibarateIntensityStatus(1);
        } else if ("intensely".equals(key)) {
            setSmsNotificationVibarateIntensityValue(2);
            updateSmsNotificationVibarateIntensityStatus(2);
        }
    }
}
