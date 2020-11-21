package com.android.launcher3.icons;

import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;

public class ShadowGenerator {
    private final Paint mBlurPaint = new Paint(3);
    private final BlurMaskFilter mDefaultBlurMaskFilter = new BlurMaskFilter(((float) this.mIconSize) * 0.010416667f, BlurMaskFilter.Blur.NORMAL);
    private final Paint mDrawPaint = new Paint(3);
    private final int mIconSize;

    public ShadowGenerator(int i) {
        this.mIconSize = i;
    }

    public synchronized void recreateIcon(Bitmap bitmap, Canvas canvas) {
        recreateIcon(bitmap, this.mDefaultBlurMaskFilter, 30, 61, canvas);
    }

    public synchronized void recreateIcon(Bitmap bitmap, BlurMaskFilter blurMaskFilter, int i, int i2, Canvas canvas) {
        int[] iArr = new int[2];
        this.mBlurPaint.setMaskFilter(blurMaskFilter);
        Bitmap extractAlpha = bitmap.extractAlpha(this.mBlurPaint, iArr);
        this.mDrawPaint.setAlpha(i);
        canvas.drawBitmap(extractAlpha, (float) iArr[0], (float) iArr[1], this.mDrawPaint);
        this.mDrawPaint.setAlpha(i2);
        canvas.drawBitmap(extractAlpha, (float) iArr[0], ((float) iArr[1]) + (((float) this.mIconSize) * 0.020833334f), this.mDrawPaint);
        this.mDrawPaint.setAlpha(255);
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, this.mDrawPaint);
    }
}
