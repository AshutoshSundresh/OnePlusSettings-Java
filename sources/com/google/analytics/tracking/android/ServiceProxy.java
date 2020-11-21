package com.google.analytics.tracking.android;

import com.google.android.gms.analytics.internal.Command;
import java.util.List;
import java.util.Map;

/* access modifiers changed from: package-private */
public interface ServiceProxy {
    void createService();

    void dispatch();

    void putHit(Map<String, String> map, long j, String str, List<Command> list);

    void setForceLocalDispatch();
}
