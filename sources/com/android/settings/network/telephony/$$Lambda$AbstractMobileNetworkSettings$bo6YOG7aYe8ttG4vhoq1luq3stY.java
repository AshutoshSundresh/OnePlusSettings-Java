package com.android.settings.network.telephony;

import com.android.settingslib.core.AbstractPreferenceController;
import java.util.function.Predicate;

/* renamed from: com.android.settings.network.telephony.-$$Lambda$AbstractMobileNetworkSettings$bo6YOG7aYe8ttG4vhoq1luq3stY  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$AbstractMobileNetworkSettings$bo6YOG7aYe8ttG4vhoq1luq3stY implements Predicate {
    public static final /* synthetic */ $$Lambda$AbstractMobileNetworkSettings$bo6YOG7aYe8ttG4vhoq1luq3stY INSTANCE = new $$Lambda$AbstractMobileNetworkSettings$bo6YOG7aYe8ttG4vhoq1luq3stY();

    private /* synthetic */ $$Lambda$AbstractMobileNetworkSettings$bo6YOG7aYe8ttG4vhoq1luq3stY() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        return ((AbstractPreferenceController) obj).isAvailable();
    }
}
