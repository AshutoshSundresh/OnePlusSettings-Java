package com.android.settingslib.drawable;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import com.android.settingslib.R$drawable;

public class UserIcons {
    private static final int[] USER_ICON_COLORS = {Color.parseColor("#FFCC6F4E"), Color.parseColor("#FFEB9413"), Color.parseColor("#FF8BC34A"), Color.parseColor("#FF673AB7"), Color.parseColor("#FF02BCD4"), Color.parseColor("#FFE91E63"), Color.parseColor("#FF9C27B0")};

    public static Bitmap convertToBitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        Bitmap createBitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        drawable.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
        drawable.draw(canvas);
        return createBitmap;
    }

    public static Drawable getDefaultUserIcon(Resources resources, int i, boolean z) {
        int i2;
        Color.parseColor(z ? "#FFFFFFFF" : "#FF9E9E9E");
        if (i == -10000) {
            i2 = Color.parseColor("#FF2196F3");
        } else if (i == 0) {
            i2 = Color.parseColor("#FF2196F3");
        } else {
            int[] iArr = USER_ICON_COLORS;
            i2 = iArr[i % iArr.length];
        }
        Drawable mutate = resources.getDrawable(R$drawable.op_ic_account_circle, null).mutate();
        mutate.setColorFilter(i2, PorterDuff.Mode.SRC_IN);
        mutate.setBounds(0, 0, mutate.getIntrinsicWidth(), mutate.getIntrinsicHeight());
        return mutate;
    }
}
