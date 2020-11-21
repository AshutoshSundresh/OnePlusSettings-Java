package com.android.wifitrackerlib;

import android.net.wifi.ScanResult;
import java.util.function.Predicate;

/* renamed from: com.android.wifitrackerlib.-$$Lambda$Utils$YXgA7eQ3EufOS8jlgf9HRQs4bfM  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$Utils$YXgA7eQ3EufOS8jlgf9HRQs4bfM implements Predicate {
    public static final /* synthetic */ $$Lambda$Utils$YXgA7eQ3EufOS8jlgf9HRQs4bfM INSTANCE = new $$Lambda$Utils$YXgA7eQ3EufOS8jlgf9HRQs4bfM();

    private /* synthetic */ $$Lambda$Utils$YXgA7eQ3EufOS8jlgf9HRQs4bfM() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        return Utils.lambda$mapScanResultsToKey$1((ScanResult) obj);
    }
}
