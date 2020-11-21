package com.android.launcher3.icons;

import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.RegionIterator;

public class GraphicsUtils {
    static /* synthetic */ void lambda$static$0() {
    }

    static {
        $$Lambda$GraphicsUtils$W6f4e52z7SPvYCk05ydbedScRFQ r0 = $$Lambda$GraphicsUtils$W6f4e52z7SPvYCk05ydbedScRFQ.INSTANCE;
    }

    public static int getArea(Region region) {
        RegionIterator regionIterator = new RegionIterator(region);
        Rect rect = new Rect();
        int i = 0;
        while (regionIterator.next(rect)) {
            i += rect.width() * rect.height();
        }
        return i;
    }
}
