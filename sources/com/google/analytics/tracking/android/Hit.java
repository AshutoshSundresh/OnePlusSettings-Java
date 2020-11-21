package com.google.analytics.tracking.android;

import android.text.TextUtils;

/* access modifiers changed from: package-private */
public class Hit {
    private final long mHitId;
    private String mHitString;
    private final long mHitTime;
    private String mHitUrlScheme = "https:";

    /* access modifiers changed from: package-private */
    public String getHitParams() {
        return this.mHitString;
    }

    /* access modifiers changed from: package-private */
    public void setHitString(String str) {
        this.mHitString = str;
    }

    /* access modifiers changed from: package-private */
    public long getHitId() {
        return this.mHitId;
    }

    /* access modifiers changed from: package-private */
    public long getHitTime() {
        return this.mHitTime;
    }

    Hit(String str, long j, long j2) {
        this.mHitString = str;
        this.mHitId = j;
        this.mHitTime = j2;
    }

    /* access modifiers changed from: package-private */
    public String getHitUrlScheme() {
        return this.mHitUrlScheme;
    }

    /* access modifiers changed from: package-private */
    public void setHitUrl(String str) {
        if (str != null && !TextUtils.isEmpty(str.trim()) && str.toLowerCase().startsWith("http:")) {
            this.mHitUrlScheme = "http:";
        }
    }
}
