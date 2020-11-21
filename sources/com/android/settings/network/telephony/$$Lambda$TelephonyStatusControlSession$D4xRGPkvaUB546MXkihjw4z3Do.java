package com.android.settings.network.telephony;

import com.android.settings.core.BasePreferenceController;
import java.util.function.Consumer;

/* renamed from: com.android.settings.network.telephony.-$$Lambda$TelephonyStatusControlSession$D4xRGPkvaUB546MXkihjw4z-3Do  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$TelephonyStatusControlSession$D4xRGPkvaUB546MXkihjw4z3Do implements Consumer {
    public static final /* synthetic */ $$Lambda$TelephonyStatusControlSession$D4xRGPkvaUB546MXkihjw4z3Do INSTANCE = new $$Lambda$TelephonyStatusControlSession$D4xRGPkvaUB546MXkihjw4z3Do();

    private /* synthetic */ $$Lambda$TelephonyStatusControlSession$D4xRGPkvaUB546MXkihjw4z3Do() {
    }

    @Override // java.util.function.Consumer
    public final void accept(Object obj) {
        TelephonyAvailabilityHandler telephonyAvailabilityHandler;
        telephonyAvailabilityHandler.setAvailabilityStatus(((BasePreferenceController) ((TelephonyAvailabilityHandler) obj)).getAvailabilityStatus());
    }
}
