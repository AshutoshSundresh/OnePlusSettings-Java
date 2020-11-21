package com.android.settingslib.display;

import android.os.AsyncTask;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManagerGlobal;
import com.android.settingslib.R$string;

public class DisplayDensityUtils {
    private static final int[] SUMMARIES_LARGER = {R$string.screen_zoom_summary_large, R$string.screen_zoom_summary_very_large, R$string.screen_zoom_summary_extremely_large};
    private static final int[] SUMMARIES_SMALLER = {R$string.screen_zoom_summary_small};
    private static final int SUMMARY_CUSTOM = R$string.screen_zoom_summary_custom;
    public static final int SUMMARY_DEFAULT = R$string.screen_zoom_summary_default;
    private final int mCurrentIndex;
    private final int mDefaultDensity;

    /* JADX WARNING: Code restructure failed: missing block: B:27:0x00a9, code lost:
        if (r3 == 2) goto L_0x00ba;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00b6, code lost:
        if (r3 == 2) goto L_0x00ba;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00b9, code lost:
        r10 = 0;
     */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00bc  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00bd A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public DisplayDensityUtils(android.content.Context r18) {
        /*
        // Method dump skipped, instructions count: 243
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.display.DisplayDensityUtils.<init>(android.content.Context):void");
    }

    public boolean useOld1080Dpi() {
        String str = SystemProperties.get("ro.sf.lcd_density", "480");
        return !TextUtils.equals("400", str) && !TextUtils.equals("420", str) && !TextUtils.equals("480", str);
    }

    public int getCurrentIndex() {
        return this.mCurrentIndex;
    }

    public int getDefaultDensity() {
        return this.mDefaultDensity;
    }

    private static int getDefaultDisplayDensity(int i) {
        try {
            return WindowManagerGlobal.getWindowManagerService().getInitialDisplayDensity(i);
        } catch (RemoteException unused) {
            return -1;
        }
    }

    public static void setForcedDisplayDensity(int i, int i2) {
        AsyncTask.execute(new Runnable(i, i2, UserHandle.myUserId()) {
            /* class com.android.settingslib.display.$$Lambda$DisplayDensityUtils$jbnNZEy3zYf8rJTNV5wQSa3Z5eQ */
            public final /* synthetic */ int f$0;
            public final /* synthetic */ int f$1;
            public final /* synthetic */ int f$2;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                DisplayDensityUtils.lambda$setForcedDisplayDensity$1(this.f$0, this.f$1, this.f$2);
            }
        });
    }

    static /* synthetic */ void lambda$setForcedDisplayDensity$1(int i, int i2, int i3) {
        try {
            WindowManagerGlobal.getWindowManagerService().setForcedDisplayDensityForUser(i, i2, i3);
        } catch (RemoteException unused) {
            Log.w("DisplayDensityUtils", "Unable to save forced display density setting");
        }
    }
}
