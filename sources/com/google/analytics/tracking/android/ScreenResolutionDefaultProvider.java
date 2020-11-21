package com.google.analytics.tracking.android;

import android.content.Context;
import android.util.DisplayMetrics;

/* access modifiers changed from: package-private */
public class ScreenResolutionDefaultProvider implements DefaultProvider {
    private static ScreenResolutionDefaultProvider sInstance;
    private static Object sInstanceLock = new Object();
    private final Context mContext;

    public static void initializeProvider(Context context) {
        synchronized (sInstanceLock) {
            if (sInstance == null) {
                sInstance = new ScreenResolutionDefaultProvider(context);
            }
        }
    }

    public static ScreenResolutionDefaultProvider getProvider() {
        ScreenResolutionDefaultProvider screenResolutionDefaultProvider;
        synchronized (sInstanceLock) {
            screenResolutionDefaultProvider = sInstance;
        }
        return screenResolutionDefaultProvider;
    }

    static void dropInstance() {
        synchronized (sInstanceLock) {
            sInstance = null;
        }
    }

    protected ScreenResolutionDefaultProvider(Context context) {
        this.mContext = context;
    }

    @Override // com.google.analytics.tracking.android.DefaultProvider
    public String getValue(String str) {
        if (str != null && str.equals("&sr")) {
            return getScreenResolutionString();
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public String getScreenResolutionString() {
        DisplayMetrics displayMetrics = this.mContext.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels + "x" + displayMetrics.heightPixels;
    }
}
