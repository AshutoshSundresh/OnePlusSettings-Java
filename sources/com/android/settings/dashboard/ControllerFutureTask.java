package com.android.settings.dashboard;

import com.android.settingslib.core.AbstractPreferenceController;
import java.util.concurrent.FutureTask;

public class ControllerFutureTask extends FutureTask<Void> {
    private final AbstractPreferenceController mController;

    public ControllerFutureTask(ControllerTask controllerTask, Void r2) {
        super(controllerTask, r2);
        this.mController = controllerTask.getController();
    }

    /* access modifiers changed from: package-private */
    public AbstractPreferenceController getController() {
        return this.mController;
    }
}
