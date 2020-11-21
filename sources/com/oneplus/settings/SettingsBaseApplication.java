package com.oneplus.settings;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemProperties;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Logger;
import com.google.analytics.tracking.android.Tracker;
import com.oneplus.security.SecureService;
import com.oneplus.security.firewall.NetworkRestrictManager;
import com.oneplus.security.widget.SecurityWidgetProvider;
import com.oneplus.settings.utils.OPUtils;

public class SettingsBaseApplication extends Application {
    public static final boolean ONEPLUS_DEBUG = SystemProperties.getBoolean("persist.sys.assert.panic", false);
    private static Handler handler;
    private static HandlerThread handlerThread;
    public static Application mApplication;
    private static Object mLock = new byte[0];
    private boolean mIsBeta;
    private Tracker mTracker;

    public static Handler getHandler() {
        if (handler == null) {
            synchronized (mLock) {
                if (handler == null) {
                    if (handlerThread == null) {
                        handlerThread = new HandlerThread(SettingsBaseApplication.class.getCanonicalName());
                    }
                    if (!handlerThread.isAlive()) {
                        handlerThread.start();
                    }
                    handler = new Handler(handlerThread.getLooper());
                }
            }
        }
        return handler;
    }

    public static Context getContext() {
        return mApplication;
    }

    public void onCreate() {
        super.onCreate();
        mApplication = this;
        this.mIsBeta = OPUtils.isBetaRom();
        OPOnlineConfigManager.getInstence(mApplication).init();
        startOtherTask(mApplication);
    }

    private void startOtherTask(Context context) {
        NetworkRestrictManager.getInstance(context).init();
        SecureService.startService(context);
        SecurityWidgetProvider.notifyDataUsage(context);
    }

    public boolean isBetaRom() {
        return this.mIsBeta;
    }

    public Tracker getDefaultTracker() {
        synchronized (SettingsBaseApplication.class) {
            if (this.mTracker == null) {
                GoogleAnalytics instance = GoogleAnalytics.getInstance(this);
                instance.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
                this.mTracker = instance.getTracker("UA-92966593-3");
            }
        }
        return this.mTracker;
    }
}
