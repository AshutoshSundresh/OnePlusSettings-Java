package com.android.settings.development;

import com.android.settings.Utils;
import com.android.settings.widget.SwitchBar;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.development.DevelopmentSettingsEnabler;

public class DevelopmentSwitchBarController implements LifecycleObserver, OnStart, OnStop {
    private final boolean mIsAvailable;
    private final DevelopmentSettingsDashboardFragment mSettings;
    private final SwitchBar mSwitchBar;

    public DevelopmentSwitchBarController(DevelopmentSettingsDashboardFragment developmentSettingsDashboardFragment, SwitchBar switchBar, boolean z, Lifecycle lifecycle) {
        this.mSwitchBar = switchBar;
        boolean z2 = z && !Utils.isMonkeyRunning();
        this.mIsAvailable = z2;
        this.mSettings = developmentSettingsDashboardFragment;
        if (z2) {
            lifecycle.addObserver(this);
        } else {
            this.mSwitchBar.setEnabled(false);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mSwitchBar.setChecked(DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(this.mSettings.getContext()));
        this.mSwitchBar.addOnSwitchChangeListener(this.mSettings);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mSwitchBar.removeOnSwitchChangeListener(this.mSettings);
    }
}
