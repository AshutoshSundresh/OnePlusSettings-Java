package com.oneplus.settings.notification;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.View;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.ui.RadioButtonPreference;
import com.android.settings.widget.SwitchBar;
import com.android.settings.widget.SwitchBarController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.oneplus.settings.utils.OPUtils;

public class OPSystemVbrateSettings extends DashboardFragment implements RadioButtonPreference.OnClickListener {
    private Context mContext;
    private RadioButtonPreference mIntenselyPreference;
    private RadioButtonPreference mLightPreference;
    private RadioButtonPreference mModeratePreference;
    private OPSystemVibrateSwitchPreferenceController mOPSystemVibrateSwitchPreferenceController;
    private SwitchBar mSwitchBar;
    private int mTempTouchTapIntensity;
    private Vibrator mVibrator;
    private long[][] sTouchVibrateIntensity = {new long[]{-1, 0, 10, 1000, 10}, new long[]{-2, 0, 10, 1000, 10}, new long[]{-3, 0, 10, 1000, 10}};

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return 0;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "OPSystemVbrateSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    public OPSystemVbrateSettings() {
        new Handler(Looper.getMainLooper()) {
            /* class com.oneplus.settings.notification.OPSystemVbrateSettings.AnonymousClass1 */

            public void handleMessage(Message message) {
                super.handleMessage(message);
                if (message.what == 10) {
                    new View(OPSystemVbrateSettings.this.mContext).performHapticFeedback(1);
                    Settings.System.putInt(OPSystemVbrateSettings.this.getActivity().getContentResolver(), "vibrate_on_touch_intensity", OPSystemVbrateSettings.this.mTempTouchTapIntensity);
                }
            }
        };
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        int i = Settings.System.getInt(this.mContext.getContentResolver(), "vibrate_on_touch_intensity", 0);
        this.mTempTouchTapIntensity = i;
        updateSystemVibrateStatus(i);
    }

    private void updateSystemVibrateStatus(int i) {
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
        int i = 0;
        if (!"light".equals(key)) {
            if ("moderate".equals(key)) {
                i = 1;
            } else if ("intensely".equals(key)) {
                i = 2;
            }
        }
        if (this.mVibrator != null) {
            Settings.System.putInt(this.mContext.getContentResolver(), "vibrate_on_touch_intensity", i);
            if (OPUtils.isSupportZVibrationMotor()) {
                this.mSwitchBar.performHapticFeedback(1);
            } else {
                this.mVibrator.cancel();
                this.mVibrator.vibrate(this.sTouchVibrateIntensity[i], -1);
            }
        }
        updateSystemVibrateStatus(i);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        SettingsActivity settingsActivity = (SettingsActivity) getActivity();
        SwitchBar switchBar = settingsActivity.getSwitchBar();
        this.mSwitchBar = switchBar;
        this.mOPSystemVibrateSwitchPreferenceController = new OPSystemVibrateSwitchPreferenceController(settingsActivity, new SwitchBarController(switchBar));
        Lifecycle settingsLifecycle = getSettingsLifecycle();
        if (settingsLifecycle != null) {
            settingsLifecycle.addObserver(this.mOPSystemVibrateSwitchPreferenceController);
        }
        FragmentActivity activity = getActivity();
        this.mContext = activity;
        Vibrator vibrator = (Vibrator) activity.getSystemService("vibrator");
        this.mVibrator = vibrator;
        if (vibrator != null && !vibrator.hasVibrator()) {
            this.mVibrator = null;
        }
        this.mLightPreference = (RadioButtonPreference) findPreference("light");
        this.mModeratePreference = (RadioButtonPreference) findPreference("moderate");
        this.mIntenselyPreference = (RadioButtonPreference) findPreference("intensely");
        this.mLightPreference.setOnClickListener(this);
        this.mModeratePreference.setOnClickListener(this);
        this.mIntenselyPreference.setOnClickListener(this);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.op_system_vibrate;
    }
}
