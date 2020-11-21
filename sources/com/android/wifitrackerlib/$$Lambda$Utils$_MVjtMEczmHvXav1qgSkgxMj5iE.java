package com.android.wifitrackerlib;

import android.net.wifi.ScanResult;
import java.util.function.Function;

/* renamed from: com.android.wifitrackerlib.-$$Lambda$Utils$_MVjtMEczmHvXav1qgSkgxMj5iE  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$Utils$_MVjtMEczmHvXav1qgSkgxMj5iE implements Function {
    public static final /* synthetic */ $$Lambda$Utils$_MVjtMEczmHvXav1qgSkgxMj5iE INSTANCE = new $$Lambda$Utils$_MVjtMEczmHvXav1qgSkgxMj5iE();

    private /* synthetic */ $$Lambda$Utils$_MVjtMEczmHvXav1qgSkgxMj5iE() {
    }

    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        return ((ScanResult) obj).SSID;
    }
}
