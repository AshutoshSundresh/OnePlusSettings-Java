package com.android.settings.homepage.contextualcards.slices;

import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import java.util.function.Predicate;

/* renamed from: com.android.settings.homepage.contextualcards.slices.-$$Lambda$BluetoothDevicesSlice$x8kIaCKPgPO7kPapU4rXexqhk78  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$BluetoothDevicesSlice$x8kIaCKPgPO7kPapU4rXexqhk78 implements Predicate {
    public static final /* synthetic */ $$Lambda$BluetoothDevicesSlice$x8kIaCKPgPO7kPapU4rXexqhk78 INSTANCE = new $$Lambda$BluetoothDevicesSlice$x8kIaCKPgPO7kPapU4rXexqhk78();

    private /* synthetic */ $$Lambda$BluetoothDevicesSlice$x8kIaCKPgPO7kPapU4rXexqhk78() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        return BluetoothDevicesSlice.lambda$getPairedBluetoothDevices$1((CachedBluetoothDevice) obj);
    }
}
