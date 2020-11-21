package androidx.slice;

public class SystemClock implements Clock {
    @Override // androidx.slice.Clock
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}
