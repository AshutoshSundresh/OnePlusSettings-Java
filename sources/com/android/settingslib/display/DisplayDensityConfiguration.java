package com.android.settingslib.display;

import android.os.AsyncTask;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import android.view.WindowManagerGlobal;

public class DisplayDensityConfiguration {
    public static void clearForcedDisplayDensity(int i) {
        AsyncTask.execute(new Runnable(i, UserHandle.myUserId()) {
            /* class com.android.settingslib.display.$$Lambda$DisplayDensityConfiguration$dm3fSLc60rEdpFAtD9PwhewynDU */
            public final /* synthetic */ int f$0;
            public final /* synthetic */ int f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void run() {
                DisplayDensityConfiguration.lambda$clearForcedDisplayDensity$0(this.f$0, this.f$1);
            }
        });
    }

    static /* synthetic */ void lambda$clearForcedDisplayDensity$0(int i, int i2) {
        try {
            WindowManagerGlobal.getWindowManagerService().clearForcedDisplayDensityForUser(i, i2);
        } catch (RemoteException unused) {
            Log.w("DisplayDensityConfig", "Unable to clear forced display density setting");
        }
    }

    public static void setForcedDisplayDensity(int i, int i2) {
        AsyncTask.execute(new Runnable(i, i2, UserHandle.myUserId()) {
            /* class com.android.settingslib.display.$$Lambda$DisplayDensityConfiguration$Y1PRkpgOIJnyPn1bdKPH8CzcHNs */
            public final /* synthetic */ int f$0;
            public final /* synthetic */ int f$1;
            public final /* synthetic */ int f$2;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                DisplayDensityConfiguration.lambda$setForcedDisplayDensity$1(this.f$0, this.f$1, this.f$2);
            }
        });
    }

    static /* synthetic */ void lambda$setForcedDisplayDensity$1(int i, int i2, int i3) {
        try {
            WindowManagerGlobal.getWindowManagerService().setForcedDisplayDensityForUser(i, i2, i3);
        } catch (RemoteException unused) {
            Log.w("DisplayDensityConfig", "Unable to save forced display density setting");
        }
    }
}
