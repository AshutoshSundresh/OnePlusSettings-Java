package com.oneplus.settings;

import android.content.Context;
import android.widget.Switch;
import com.android.settings.widget.SwitchBar;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;

public class OPMEMCSwitchBarController implements SwitchBar.OnSwitchChangeListener, LifecycleObserver, OnStart, OnStop {
    private OnChangeScreen mListener;
    private final SwitchBar mSwitchBar;
    private boolean mValidListener;

    public OPMEMCSwitchBarController(Context context, SwitchBar switchBar, Lifecycle lifecycle, OnChangeScreen onChangeScreen, String str) {
        this.mSwitchBar = switchBar;
        switchBar.getSwitch();
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
        this.mListener = onChangeScreen;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        if (!this.mValidListener) {
            this.mSwitchBar.addOnSwitchChangeListener(this);
            this.mValidListener = true;
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        if (this.mValidListener) {
            this.mSwitchBar.removeOnSwitchChangeListener(this);
            this.mValidListener = false;
        }
    }

    @Override // com.android.settings.widget.SwitchBar.OnSwitchChangeListener
    public void onSwitchChanged(Switch r1, boolean z) {
        this.mListener.onChangeScreen(z);
    }
}
