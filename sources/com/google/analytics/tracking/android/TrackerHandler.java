package com.google.analytics.tracking.android;

import java.util.Map;

/* access modifiers changed from: package-private */
public abstract class TrackerHandler {
    /* access modifiers changed from: package-private */
    public abstract void sendHit(Map<String, String> map);

    TrackerHandler() {
    }
}
