package androidx.slice.widget;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

/* access modifiers changed from: package-private */
public class SliceMetrics {
    /* access modifiers changed from: protected */
    public abstract void logHidden();

    /* access modifiers changed from: protected */
    public abstract void logTouch(int i, Uri uri);

    /* access modifiers changed from: protected */
    public abstract void logVisible();

    SliceMetrics() {
    }

    public static SliceMetrics getInstance(Context context, Uri uri) {
        if (Build.VERSION.SDK_INT >= 28) {
            return new SliceMetricsWrapper(context, uri);
        }
        return null;
    }
}
