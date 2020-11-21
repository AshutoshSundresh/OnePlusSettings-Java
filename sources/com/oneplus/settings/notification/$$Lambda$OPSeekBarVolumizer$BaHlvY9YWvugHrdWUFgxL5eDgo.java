package com.oneplus.settings.notification;

import android.media.audiopolicy.AudioProductStrategy;
import java.util.function.Function;

/* renamed from: com.oneplus.settings.notification.-$$Lambda$OPSeekBarVolumizer$BaHlv-Y9YWvugHrdWUFgxL5eDgo  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$OPSeekBarVolumizer$BaHlvY9YWvugHrdWUFgxL5eDgo implements Function {
    public static final /* synthetic */ $$Lambda$OPSeekBarVolumizer$BaHlvY9YWvugHrdWUFgxL5eDgo INSTANCE = new $$Lambda$OPSeekBarVolumizer$BaHlvY9YWvugHrdWUFgxL5eDgo();

    private /* synthetic */ $$Lambda$OPSeekBarVolumizer$BaHlvY9YWvugHrdWUFgxL5eDgo() {
    }

    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        return Integer.valueOf(((AudioProductStrategy) obj).getVolumeGroupIdForAudioAttributes(AudioProductStrategy.sDefaultAttributes));
    }
}
