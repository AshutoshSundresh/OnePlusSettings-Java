package com.android.launcher3.icons;

import android.content.Context;

public class IconFactory extends BaseIconFactory {
    private static IconFactory sPool;
    private static int sPoolId;
    private static final Object sPoolSync = new Object();
    private final int mPoolId;
    private IconFactory next;

    public static IconFactory obtain(Context context) {
        synchronized (sPoolSync) {
            if (sPool != null) {
                IconFactory iconFactory = sPool;
                sPool = iconFactory.next;
                iconFactory.next = null;
                return iconFactory;
            }
            return new IconFactory(context, context.getResources().getConfiguration().densityDpi, context.getResources().getDimensionPixelSize(R$dimen.default_icon_bitmap_size), sPoolId);
        }
    }

    private IconFactory(Context context, int i, int i2, int i3) {
        super(context, i, i2);
        this.mPoolId = i3;
    }

    public void recycle() {
        synchronized (sPoolSync) {
            if (sPoolId == this.mPoolId) {
                clear();
                this.next = sPool;
                sPool = this;
            }
        }
    }

    @Override // com.android.launcher3.icons.BaseIconFactory, java.lang.AutoCloseable
    public void close() {
        recycle();
    }
}
