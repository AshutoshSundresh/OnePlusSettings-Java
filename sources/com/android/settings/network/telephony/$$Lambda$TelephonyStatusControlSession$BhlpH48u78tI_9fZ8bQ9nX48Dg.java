package com.android.settings.network.telephony;

import java.util.function.Consumer;

/* renamed from: com.android.settings.network.telephony.-$$Lambda$TelephonyStatusControlSession$Bhlp-H48u78tI_9fZ8bQ9nX48Dg  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$TelephonyStatusControlSession$BhlpH48u78tI_9fZ8bQ9nX48Dg implements Consumer {
    public static final /* synthetic */ $$Lambda$TelephonyStatusControlSession$BhlpH48u78tI_9fZ8bQ9nX48Dg INSTANCE = new $$Lambda$TelephonyStatusControlSession$BhlpH48u78tI_9fZ8bQ9nX48Dg();

    private /* synthetic */ $$Lambda$TelephonyStatusControlSession$BhlpH48u78tI_9fZ8bQ9nX48Dg() {
    }

    @Override // java.util.function.Consumer
    public final void accept(Object obj) {
        ((TelephonyAvailabilityHandler) obj).unsetAvailabilityStatus();
    }
}
