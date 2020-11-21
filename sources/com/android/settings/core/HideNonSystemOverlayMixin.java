package com.android.settings.core;

import android.app.Activity;
import android.util.EventLog;
import android.view.Window;
import android.view.WindowManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import com.android.settings.development.OverlaySettingsPreferenceController;

public class HideNonSystemOverlayMixin implements LifecycleObserver {
    private final Activity mActivity;

    /* access modifiers changed from: package-private */
    public boolean isEnabled() {
        return !OverlaySettingsPreferenceController.isOverlaySettingsEnabled(this.mActivity);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        if (this.mActivity != null && isEnabled()) {
            this.mActivity.getWindow().addSystemFlags(524288);
            EventLog.writeEvent(1397638484, "120484087", -1, "");
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        if (this.mActivity != null && isEnabled()) {
            Window window = this.mActivity.getWindow();
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.privateFlags &= -524289;
            window.setAttributes(attributes);
        }
    }
}
