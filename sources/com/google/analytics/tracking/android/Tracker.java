package com.google.analytics.tracking.android;

import android.text.TextUtils;
import com.google.analytics.tracking.android.GAUsage;
import java.util.HashMap;
import java.util.Map;

public class Tracker {
    private final TrackerHandler mHandler;
    private final Map<String, String> mParams;
    private RateLimiter mRateLimiter;

    Tracker(String str, String str2, TrackerHandler trackerHandler) {
        this(str, str2, trackerHandler, ClientIdDefaultProvider.getProvider(), ScreenResolutionDefaultProvider.getProvider(), AppFieldsDefaultProvider.getProvider(), new SendHitRateLimiter());
    }

    Tracker(String str, String str2, TrackerHandler trackerHandler, ClientIdDefaultProvider clientIdDefaultProvider, ScreenResolutionDefaultProvider screenResolutionDefaultProvider, AppFieldsDefaultProvider appFieldsDefaultProvider, RateLimiter rateLimiter) {
        this.mParams = new HashMap();
        if (!TextUtils.isEmpty(str)) {
            this.mHandler = trackerHandler;
            this.mParams.put("&tid", str2);
            this.mParams.put("useSecure", "1");
            this.mRateLimiter = rateLimiter;
            return;
        }
        throw new IllegalArgumentException("Tracker name cannot be empty.");
    }

    /* access modifiers changed from: package-private */
    public RateLimiter getRateLimiter() {
        return this.mRateLimiter;
    }

    public void send(Map<String, String> map) {
        GAUsage.getInstance().setUsage(GAUsage.Field.SEND);
        HashMap hashMap = new HashMap();
        hashMap.putAll(this.mParams);
        if (map != null) {
            hashMap.putAll(map);
        }
        if (TextUtils.isEmpty((CharSequence) hashMap.get("&tid"))) {
            Log.w(String.format("Missing tracking id (%s) parameter.", "&tid"));
        }
        String str = (String) hashMap.get("&t");
        if (TextUtils.isEmpty(str)) {
            Log.w(String.format("Missing hit type (%s) parameter.", "&t"));
            str = "";
        }
        if (str.equals("transaction") || str.equals("item") || this.mRateLimiter.tokenAvailable()) {
            this.mHandler.sendHit(hashMap);
        } else {
            Log.w("Too many hits sent too quickly, rate limiting invoked.");
        }
    }

    public void set(String str, String str2) {
        GAUsage.getInstance().setUsage(GAUsage.Field.SET);
        if (str2 == null) {
            this.mParams.remove(str);
        } else {
            this.mParams.put(str, str2);
        }
    }
}
