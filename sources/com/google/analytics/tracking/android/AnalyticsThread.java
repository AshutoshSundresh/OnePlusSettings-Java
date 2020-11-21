package com.google.analytics.tracking.android;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

interface AnalyticsThread {
    void dispatch();

    LinkedBlockingQueue<Runnable> getQueue();

    Thread getThread();

    void sendHit(Map<String, String> map);

    void setForceLocalDispatch();
}
