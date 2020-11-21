package com.oneplus.settings.notification;

import android.content.Context;
import android.provider.Settings;
import com.android.settings.widget.SwitchWidgetController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;

public class OPSystemVibrateSwitchPreferenceController implements LifecycleObserver, OnStart, OnStop, SwitchWidgetController.OnSwitchChangeListener {
    private Context mContext;
    private SwitchWidgetController mSwitch;
    private SystemVibrateEnabler mSystemVibrateEnabler;

    @Override // com.android.settings.widget.SwitchWidgetController.OnSwitchChangeListener
    public boolean onSwitchToggled(boolean z) {
        return true;
    }

    public OPSystemVibrateSwitchPreferenceController(Context context, SwitchWidgetController switchWidgetController) {
        this.mSwitch = switchWidgetController;
        this.mContext = context;
        switchWidgetController.setupView();
        SystemVibrateEnabler systemVibrateEnabler = new SystemVibrateEnabler(this, this.mContext, this.mSwitch);
        this.mSystemVibrateEnabler = systemVibrateEnabler;
        systemVibrateEnabler.setToggleCallback(this);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mSystemVibrateEnabler.resume(this.mContext);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mSystemVibrateEnabler.pause();
    }

    class SystemVibrateEnabler implements SwitchWidgetController.OnSwitchChangeListener {
        private SwitchWidgetController.OnSwitchChangeListener mCallback;
        private Context mContext;
        private final SwitchWidgetController mSwitchController;
        private boolean mValidListener = false;

        SystemVibrateEnabler(OPSystemVibrateSwitchPreferenceController oPSystemVibrateSwitchPreferenceController, Context context, SwitchWidgetController switchWidgetController) {
            this.mContext = context;
            this.mSwitchController = switchWidgetController;
            switchWidgetController.setListener(this);
        }

        public void resume(Context context) {
            if (this.mContext != context) {
                this.mContext = context;
            }
            this.mSwitchController.startListening();
            boolean z = true;
            this.mValidListener = true;
            if (Settings.System.getInt(this.mContext.getContentResolver(), "haptic_feedback_enabled", 0) == 0) {
                z = false;
            }
            this.mSwitchController.setChecked(z);
        }

        public void pause() {
            if (this.mValidListener) {
                this.mSwitchController.stopListening();
                this.mValidListener = false;
            }
        }

        @Override // com.android.settings.widget.SwitchWidgetController.OnSwitchChangeListener
        public boolean onSwitchToggled(boolean z) {
            Settings.System.putInt(this.mContext.getContentResolver(), "haptic_feedback_enabled", z ? 1 : 0);
            triggerParentPreferenceCallback(z);
            return true;
        }

        public void setToggleCallback(SwitchWidgetController.OnSwitchChangeListener onSwitchChangeListener) {
            this.mCallback = onSwitchChangeListener;
        }

        private void triggerParentPreferenceCallback(boolean z) {
            SwitchWidgetController.OnSwitchChangeListener onSwitchChangeListener = this.mCallback;
            if (onSwitchChangeListener != null) {
                onSwitchChangeListener.onSwitchToggled(z);
            }
        }
    }
}
