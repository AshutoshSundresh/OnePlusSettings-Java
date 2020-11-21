package com.android.settings.deviceinfo;

import com.android.settingslib.DeviceInfoUtils;
import java.util.concurrent.Callable;

/* renamed from: com.android.settings.deviceinfo.-$$Lambda$HardwareInfoPreferenceController$2jUJufqqG3kEFPreI5guPIXwUZA  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$HardwareInfoPreferenceController$2jUJufqqG3kEFPreI5guPIXwUZA implements Callable {
    public static final /* synthetic */ $$Lambda$HardwareInfoPreferenceController$2jUJufqqG3kEFPreI5guPIXwUZA INSTANCE = new $$Lambda$HardwareInfoPreferenceController$2jUJufqqG3kEFPreI5guPIXwUZA();

    private /* synthetic */ $$Lambda$HardwareInfoPreferenceController$2jUJufqqG3kEFPreI5guPIXwUZA() {
    }

    @Override // java.util.concurrent.Callable
    public final Object call() {
        return DeviceInfoUtils.getMsvSuffix();
    }
}
