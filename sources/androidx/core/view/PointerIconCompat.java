package androidx.core.view;

import android.content.Context;
import android.os.Build;
import android.view.PointerIcon;

public final class PointerIconCompat {
    private Object mPointerIcon;

    private PointerIconCompat(Object obj) {
        this.mPointerIcon = obj;
    }

    public Object getPointerIcon() {
        return this.mPointerIcon;
    }

    public static PointerIconCompat getSystemIcon(Context context, int i) {
        if (Build.VERSION.SDK_INT >= 24) {
            return new PointerIconCompat(PointerIcon.getSystemIcon(context, i));
        }
        return new PointerIconCompat(null);
    }
}
