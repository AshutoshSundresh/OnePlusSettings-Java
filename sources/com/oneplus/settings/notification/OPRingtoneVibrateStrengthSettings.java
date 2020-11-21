package com.oneplus.settings.notification;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import androidx.preference.PreferenceCategory;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.ui.RadioButtonPreference;
import com.oneplus.settings.utils.OPUtils;

public class OPRingtoneVibrateStrengthSettings extends SettingsPreferenceFragment implements RadioButtonPreference.OnClickListener {
    private Context mContext;
    private RadioButtonPreference mDynamicModePreference;
    private RadioButtonPreference mIntenselyPreference;
    private RadioButtonPreference mLightPreference;
    private RadioButtonPreference mModeratePreference;
    private RadioButtonPreference mRhythm1Preference;
    private RadioButtonPreference mRhythm2Preference;
    private RadioButtonPreference mRhythm3Preference;
    private RadioButtonPreference mRhythm4Preference;
    private RadioButtonPreference mRhythm5Preference;
    private PreferenceCategory mVibrateRhythmCategory;
    private Vibrator mVibrator;
    private long[][] sVibratePatternrhythm = {new long[]{-2, 0, 1000, 1000, 1000}, new long[]{-2, 0, 500, 250, 10, 1000, 500, 250, 10}, new long[]{-2, 0, 300, 400, 300, 400, 300, 1000, 300, 400, 300, 400, 300}, new long[]{-2, 0, 30, 80, 30, 80, 50, 180, 600, 1000, 30, 80, 30, 80, 50, 180, 600}, new long[]{-2, 0, 80, 200, 600, 150, 10, 1000, 80, 200, 600, 150, 10}};

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
        addPreferencesFromResource(C0019R$xml.op_ringtone_vibrate_strength);
        this.mContext = getActivity();
        Vibrator vibrator = (Vibrator) getActivity().getSystemService("vibrator");
        this.mVibrator = vibrator;
        if (vibrator != null && !vibrator.hasVibrator()) {
            this.mVibrator = null;
        }
        this.mContext.getPackageManager().hasSystemFeature("oem.linear.motor.support");
        this.mVibrateRhythmCategory = (PreferenceCategory) findPreference("vibrate_rhythm");
        this.mDynamicModePreference = (RadioButtonPreference) findPreference("dynamic_mode");
        this.mRhythm1Preference = (RadioButtonPreference) findPreference("rhythm_1");
        this.mRhythm2Preference = (RadioButtonPreference) findPreference("rhythm_2");
        this.mRhythm3Preference = (RadioButtonPreference) findPreference("rhythm_3");
        this.mRhythm4Preference = (RadioButtonPreference) findPreference("rhythm_4");
        this.mRhythm5Preference = (RadioButtonPreference) findPreference("rhythm_5");
        this.mLightPreference = (RadioButtonPreference) findPreference("light");
        this.mModeratePreference = (RadioButtonPreference) findPreference("moderate");
        this.mIntenselyPreference = (RadioButtonPreference) findPreference("intensely");
        this.mDynamicModePreference.setOnClickListener(this);
        this.mRhythm1Preference.setOnClickListener(this);
        this.mRhythm2Preference.setOnClickListener(this);
        this.mRhythm3Preference.setOnClickListener(this);
        this.mRhythm4Preference.setOnClickListener(this);
        this.mRhythm5Preference.setOnClickListener(this);
        this.mLightPreference.setOnClickListener(this);
        this.mModeratePreference.setOnClickListener(this);
        this.mIntenselyPreference.setOnClickListener(this);
        if (!OPUtils.isSupportXVibrate()) {
            this.mVibrateRhythmCategory.removePreference(this.mDynamicModePreference);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        updateIncomingCallVibrateStatus(Settings.System.getInt(getContentResolver(), "incoming_call_vibrate_mode", 0));
        updateIncomingCallVibarateIntensityStatus(Settings.System.getInt(getContentResolver(), "incoming_call_vibrate_intensity", -1));
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        Vibrator vibrator = this.mVibrator;
        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    private void updateIncomingCallVibrateStatus(int i) {
        RadioButtonPreference radioButtonPreference = this.mDynamicModePreference;
        boolean z = false;
        if (radioButtonPreference != null) {
            radioButtonPreference.setChecked(i == 5);
        }
        this.mRhythm1Preference.setChecked(i == 0);
        this.mRhythm2Preference.setChecked(i == 1);
        this.mRhythm3Preference.setChecked(i == 2);
        this.mRhythm4Preference.setChecked(i == 3);
        RadioButtonPreference radioButtonPreference2 = this.mRhythm5Preference;
        if (i == 4) {
            z = true;
        }
        radioButtonPreference2.setChecked(z);
    }

    private void updateIncomingCallVibarateIntensityStatus(int i) {
        boolean z = false;
        this.mLightPreference.setChecked(i == 0);
        this.mModeratePreference.setChecked(i == 1);
        RadioButtonPreference radioButtonPreference = this.mIntenselyPreference;
        if (i == 2) {
            z = true;
        }
        radioButtonPreference.setChecked(z);
    }

    private void setIncomingCallVibrateValue(int i) {
        Settings.System.putInt(getContentResolver(), "incoming_call_vibrate_mode", i);
        int i2 = Settings.System.getInt(getContentResolver(), "incoming_call_vibrate_intensity", -1);
        if (this.mVibrator != null) {
            if (i2 == 0) {
                this.sVibratePatternrhythm[i][0] = -1;
            } else if (i2 == 1) {
                this.sVibratePatternrhythm[i][0] = -2;
            } else if (i2 == 2) {
                this.sVibratePatternrhythm[i][0] = -3;
            }
            this.mVibrator.vibrate(this.sVibratePatternrhythm[i], -1);
        }
    }

    private void setIncomingCallVibarateIntensity(int i) {
        Settings.System.putInt(getContentResolver(), "incoming_call_vibrate_intensity", i);
        if (this.mVibrator != null) {
            int i2 = Settings.System.getInt(getContentResolver(), "incoming_call_vibrate_mode", i);
            int i3 = 0;
            if (i2 > this.sVibratePatternrhythm.length - 1) {
                i2 = 0;
            }
            this.mVibrator.cancel();
            if (i == 0) {
                this.sVibratePatternrhythm[i2][0] = -1;
            } else if (i == 1) {
                this.sVibratePatternrhythm[i2][0] = -2;
            } else if (i == 2) {
                this.sVibratePatternrhythm[i2][0] = -3;
            }
            while (true) {
                long[][] jArr = this.sVibratePatternrhythm;
                if (i3 < jArr[i2].length) {
                    Log.d("OPVibrateStrengthPreferenceFragment", "sVibratePatternrhythm [" + i2 + "][" + i3 + "] = " + this.sVibratePatternrhythm[i2][i3]);
                    i3++;
                } else {
                    this.mVibrator.vibrate(jArr[i2], -1);
                    return;
                }
            }
        }
    }

    @Override // com.android.settings.ui.RadioButtonPreference.OnClickListener
    public void onRadioButtonClicked(RadioButtonPreference radioButtonPreference) {
        String key = radioButtonPreference.getKey();
        if ("dynamic_mode".equals(key)) {
            Settings.System.putInt(getContentResolver(), "incoming_call_vibrate_mode", 5);
            updateIncomingCallVibrateStatus(5);
        } else if ("rhythm_1".equals(key)) {
            setIncomingCallVibrateValue(0);
            updateIncomingCallVibrateStatus(0);
        } else if ("rhythm_2".equals(key)) {
            setIncomingCallVibrateValue(1);
            updateIncomingCallVibrateStatus(1);
        } else if ("rhythm_3".equals(key)) {
            setIncomingCallVibrateValue(2);
            updateIncomingCallVibrateStatus(2);
        } else if ("rhythm_4".equals(key)) {
            setIncomingCallVibrateValue(3);
            updateIncomingCallVibrateStatus(3);
        } else if ("rhythm_5".equals(key)) {
            setIncomingCallVibrateValue(4);
            updateIncomingCallVibrateStatus(4);
        } else if ("light".equals(key)) {
            setIncomingCallVibarateIntensity(0);
            updateIncomingCallVibarateIntensityStatus(0);
        } else if ("moderate".equals(key)) {
            setIncomingCallVibarateIntensity(1);
            updateIncomingCallVibarateIntensityStatus(1);
        } else if ("intensely".equals(key)) {
            setIncomingCallVibarateIntensity(2);
            updateIncomingCallVibarateIntensityStatus(2);
        }
    }
}
