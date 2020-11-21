package com.google.analytics.tracking.android;

import android.content.Context;
import android.text.TextUtils;
import com.google.analytics.tracking.android.GAUsage;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GoogleAnalytics extends TrackerHandler {
    private static GoogleAnalytics sInstance;
    private volatile Boolean mAppOptOut;
    private Context mContext;
    private Tracker mDefaultTracker;
    private boolean mDryRun;
    private Logger mLogger;
    private AnalyticsThread mThread;
    private final Map<String, Tracker> mTrackers;

    /* access modifiers changed from: package-private */
    public void close() {
    }

    protected GoogleAnalytics(Context context) {
        this(context, GAThread.getInstance(context));
    }

    private GoogleAnalytics(Context context, AnalyticsThread analyticsThread) {
        this.mAppOptOut = Boolean.FALSE;
        this.mTrackers = new HashMap();
        if (context != null) {
            Context applicationContext = context.getApplicationContext();
            this.mContext = applicationContext;
            this.mThread = analyticsThread;
            AppFieldsDefaultProvider.initializeProvider(applicationContext);
            ScreenResolutionDefaultProvider.initializeProvider(this.mContext);
            ClientIdDefaultProvider.initializeProvider(this.mContext);
            this.mLogger = new DefaultLoggerImpl();
            return;
        }
        throw new IllegalArgumentException("context cannot be null");
    }

    public static GoogleAnalytics getInstance(Context context) {
        GoogleAnalytics googleAnalytics;
        synchronized (GoogleAnalytics.class) {
            if (sInstance == null) {
                sInstance = new GoogleAnalytics(context);
            }
            googleAnalytics = sInstance;
        }
        return googleAnalytics;
    }

    static GoogleAnalytics getInstance() {
        GoogleAnalytics googleAnalytics;
        synchronized (GoogleAnalytics.class) {
            googleAnalytics = sInstance;
        }
        return googleAnalytics;
    }

    static GoogleAnalytics getNewInstance(Context context, AnalyticsThread analyticsThread) {
        GoogleAnalytics googleAnalytics;
        synchronized (GoogleAnalytics.class) {
            if (sInstance != null) {
                sInstance.close();
            }
            googleAnalytics = new GoogleAnalytics(context, analyticsThread);
            sInstance = googleAnalytics;
        }
        return googleAnalytics;
    }

    static void clearInstance() {
        synchronized (GoogleAnalytics.class) {
            sInstance = null;
            clearDefaultProviders();
        }
    }

    static void clearDefaultProviders() {
        AppFieldsDefaultProvider.dropInstance();
        ScreenResolutionDefaultProvider.dropInstance();
        ClientIdDefaultProvider.dropInstance();
    }

    public boolean isDryRunEnabled() {
        GAUsage.getInstance().setUsage(GAUsage.Field.GET_DRY_RUN);
        return this.mDryRun;
    }

    public Tracker getTracker(String str, String str2) {
        Tracker tracker;
        synchronized (this) {
            if (!TextUtils.isEmpty(str)) {
                tracker = this.mTrackers.get(str);
                if (tracker == null) {
                    tracker = new Tracker(str, str2, this);
                    this.mTrackers.put(str, tracker);
                    if (this.mDefaultTracker == null) {
                        this.mDefaultTracker = tracker;
                    }
                }
                if (!TextUtils.isEmpty(str2)) {
                    tracker.set("&tid", str2);
                }
                GAUsage.getInstance().setUsage(GAUsage.Field.GET_TRACKER);
            } else {
                throw new IllegalArgumentException("Tracker name cannot be empty");
            }
        }
        return tracker;
    }

    public Tracker getTracker(String str) {
        return getTracker(str, str);
    }

    /* access modifiers changed from: package-private */
    @Override // com.google.analytics.tracking.android.TrackerHandler
    public void sendHit(Map<String, String> map) {
        synchronized (this) {
            if (map != null) {
                try {
                    Utils.putIfAbsent(map, "&ul", Utils.getLanguage(Locale.getDefault()));
                    Utils.putIfAbsent(map, "&sr", ScreenResolutionDefaultProvider.getProvider().getValue("&sr"));
                    map.put("&_u", GAUsage.getInstance().getAndClearSequence());
                    GAUsage.getInstance().getAndClearUsage();
                    this.mThread.sendHit(map);
                } catch (Throwable th) {
                    throw th;
                }
            } else {
                throw new IllegalArgumentException("hit cannot be null");
            }
        }
    }

    public boolean getAppOptOut() {
        GAUsage.getInstance().setUsage(GAUsage.Field.GET_APP_OPT_OUT);
        return this.mAppOptOut.booleanValue();
    }

    public Logger getLogger() {
        return this.mLogger;
    }
}
