package com.google.analytics.tracking.android;

/* access modifiers changed from: package-private */
public class SendHitRateLimiter implements RateLimiter {
    private long mLastTrackTime;
    private final int mMaxTokens;
    private final long mMillisecondsPerToken;
    private final Object mTokenLock;
    private double mTokens;

    public SendHitRateLimiter(int i, long j) {
        this.mTokenLock = new Object();
        this.mMaxTokens = i;
        this.mTokens = (double) i;
        this.mMillisecondsPerToken = j;
    }

    public SendHitRateLimiter() {
        this(60, 2000);
    }

    /* access modifiers changed from: package-private */
    public void setLastTrackTime(long j) {
        this.mLastTrackTime = j;
    }

    /* access modifiers changed from: package-private */
    public void setTokensAvailable(long j) {
        this.mTokens = (double) j;
    }

    @Override // com.google.analytics.tracking.android.RateLimiter
    public boolean tokenAvailable() {
        synchronized (this.mTokenLock) {
            long currentTimeMillis = System.currentTimeMillis();
            if (this.mTokens < ((double) this.mMaxTokens)) {
                double d = ((double) (currentTimeMillis - this.mLastTrackTime)) / ((double) this.mMillisecondsPerToken);
                if (d > 0.0d) {
                    this.mTokens = Math.min((double) this.mMaxTokens, this.mTokens + d);
                }
            }
            this.mLastTrackTime = currentTimeMillis;
            if (this.mTokens >= 1.0d) {
                this.mTokens -= 1.0d;
                return true;
            }
            Log.w("Excessive tracking detected.  Tracking call ignored.");
            return false;
        }
    }
}
