package com.oneplus.settings.gestures;

import android.content.Context;
import com.android.settings.widget.SwitchWidgetController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;

public class OPQuickTurnOnAssistantAppSwitchPreferenceController implements LifecycleObserver, OnStart, OnStop, SwitchWidgetController.OnSwitchChangeListener {
    private Context mContext;
    private QuickTurnOnAssistantAppSwitchEnabler mQuickTurnOnAssistantAppSwitchEnabler;

    class QuickTurnOnAssistantAppSwitchEnabler implements SwitchWidgetController.OnSwitchChangeListener {
        public abstract void pause();

        public abstract void resume(Context context);
    }

    @Override // com.android.settings.widget.SwitchWidgetController.OnSwitchChangeListener
    public boolean onSwitchToggled(boolean z) {
        return true;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mQuickTurnOnAssistantAppSwitchEnabler.resume(this.mContext);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mQuickTurnOnAssistantAppSwitchEnabler.pause();
    }
}
