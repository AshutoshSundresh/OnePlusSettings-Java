package androidx.slice.widget;

import android.app.slice.SliceMetrics;
import android.content.Context;
import android.net.Uri;

/* access modifiers changed from: package-private */
public class SliceMetricsWrapper extends SliceMetrics {
    private final SliceMetrics mSliceMetrics;

    SliceMetricsWrapper(Context context, Uri uri) {
        this.mSliceMetrics = new SliceMetrics(context, uri);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.slice.widget.SliceMetrics
    public void logVisible() {
        this.mSliceMetrics.logVisible();
    }

    /* access modifiers changed from: protected */
    @Override // androidx.slice.widget.SliceMetrics
    public void logHidden() {
        this.mSliceMetrics.logHidden();
    }

    /* access modifiers changed from: protected */
    @Override // androidx.slice.widget.SliceMetrics
    public void logTouch(int i, Uri uri) {
        this.mSliceMetrics.logTouch(i, uri);
    }
}
