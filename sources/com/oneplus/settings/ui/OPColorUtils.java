package com.oneplus.settings.ui;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import com.android.internal.graphics.palette.Palette;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class OPColorUtils {
    private static final String TAG = "OPColorUtils";

    static int getMainColor(Drawable drawable, int i) {
        Bitmap bitmapFromDrawable = OPBitmapUtils.getBitmapFromDrawable(drawable);
        if (bitmapFromDrawable != null) {
            return getMainColor(bitmapFromDrawable, i);
        }
        Log.w(TAG, "cannot create bitmap from drawable");
        return i;
    }

    private static int getMainColor(Bitmap bitmap, int i) {
        ArrayList arrayList = new ArrayList(Palette.from(bitmap).generate().getSwatches());
        Collections.sort(arrayList, new Comparator<Palette.Swatch>() {
            /* class com.oneplus.settings.ui.OPColorUtils.AnonymousClass1 */

            public int compare(Palette.Swatch swatch, Palette.Swatch swatch2) {
                return swatch2.getPopulation() - swatch.getPopulation();
            }
        });
        if (arrayList.isEmpty()) {
            return i;
        }
        return ((Palette.Swatch) arrayList.get(0)).getRgb();
    }
}
