package com.android.wifitrackerlib;

import android.net.wifi.ScanResult;
import java.time.Clock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class ScanResultUpdater {
    private final Clock mClock;
    private final Object mLock = new Object();
    private final long mMaxScanAgeMillis;
    private HashMap<String, ScanResult> mScanResultsByBssid = new HashMap<>();

    public ScanResultUpdater(Clock clock, long j) {
        this.mMaxScanAgeMillis = j;
        this.mClock = clock;
    }

    public void update(List<ScanResult> list) {
        synchronized (this.mLock) {
            evictOldScans();
            for (ScanResult scanResult : list) {
                ScanResult scanResult2 = this.mScanResultsByBssid.get(scanResult.BSSID);
                if (scanResult2 == null || scanResult2.timestamp < scanResult.timestamp) {
                    this.mScanResultsByBssid.put(scanResult.BSSID, scanResult);
                }
            }
        }
    }

    public List<ScanResult> getScanResults() {
        return getScanResults(this.mMaxScanAgeMillis);
    }

    public List<ScanResult> getScanResults(long j) throws IllegalArgumentException {
        ArrayList arrayList;
        if (j <= this.mMaxScanAgeMillis) {
            synchronized (this.mLock) {
                arrayList = new ArrayList();
                for (ScanResult scanResult : this.mScanResultsByBssid.values()) {
                    if (this.mClock.millis() - (scanResult.timestamp / 1000) <= j) {
                        arrayList.add(scanResult);
                    }
                }
            }
            return arrayList;
        }
        throw new IllegalArgumentException("maxScanAgeMillis argument cannot be greater than mMaxScanAgeMillis!");
    }

    private void evictOldScans() {
        synchronized (this.mLock) {
            this.mScanResultsByBssid.entrySet().removeIf(new Predicate() {
                /* class com.android.wifitrackerlib.$$Lambda$ScanResultUpdater$0dMeCJ48FAV9jeYCNlP_G5xHfw */

                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return ScanResultUpdater.this.lambda$evictOldScans$0$ScanResultUpdater((Map.Entry) obj);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$evictOldScans$0 */
    public /* synthetic */ boolean lambda$evictOldScans$0$ScanResultUpdater(Map.Entry entry) {
        return this.mClock.millis() - (((ScanResult) entry.getValue()).timestamp / 1000) > this.mMaxScanAgeMillis;
    }
}
