package com.android.launcher3.icons;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import java.nio.ByteBuffer;

public class IconNormalizer {
    private final RectF mAdaptiveIconBounds;
    private float mAdaptiveIconScale;
    private final Bitmap mBitmap;
    private final Rect mBounds;
    private final Canvas mCanvas = new Canvas(this.mBitmap);
    private boolean mEnableShapeDetection;
    private final float[] mLeftBorder;
    private final Matrix mMatrix;
    private final int mMaxSize;
    private final Paint mPaintMaskShape;
    private final Paint mPaintMaskShapeOutline;
    private final byte[] mPixels;
    private final float[] mRightBorder;
    private final Path mShapePath;

    IconNormalizer(Context context, int i, boolean z) {
        int i2 = i * 2;
        this.mMaxSize = i2;
        this.mBitmap = Bitmap.createBitmap(i2, i2, Bitmap.Config.ALPHA_8);
        int i3 = this.mMaxSize;
        this.mPixels = new byte[(i3 * i3)];
        this.mLeftBorder = new float[i3];
        this.mRightBorder = new float[i3];
        this.mBounds = new Rect();
        this.mAdaptiveIconBounds = new RectF();
        Paint paint = new Paint();
        this.mPaintMaskShape = paint;
        paint.setColor(-65536);
        this.mPaintMaskShape.setStyle(Paint.Style.FILL);
        this.mPaintMaskShape.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
        Paint paint2 = new Paint();
        this.mPaintMaskShapeOutline = paint2;
        paint2.setStrokeWidth(context.getResources().getDisplayMetrics().density * 2.0f);
        this.mPaintMaskShapeOutline.setStyle(Paint.Style.STROKE);
        this.mPaintMaskShapeOutline.setColor(-16777216);
        this.mPaintMaskShapeOutline.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        this.mShapePath = new Path();
        this.mMatrix = new Matrix();
        this.mAdaptiveIconScale = 0.0f;
        this.mEnableShapeDetection = z;
    }

    private static float getScale(float f, float f2, float f3) {
        float f4 = f / f2;
        float f5 = f4 < 0.7853982f ? 0.6597222f : ((1.0f - f4) * 0.040449437f) + 0.6510417f;
        float f6 = f / f3;
        if (f6 > f5) {
            return (float) Math.sqrt((double) (f5 / f6));
        }
        return 1.0f;
    }

    @TargetApi(26)
    public static float normalizeAdaptiveIcon(Drawable drawable, int i, RectF rectF) {
        Rect rect = new Rect(drawable.getBounds());
        drawable.setBounds(0, 0, i, i);
        Path iconMask = ((AdaptiveIconDrawable) drawable).getIconMask();
        Region region = new Region();
        region.setPath(iconMask, new Region(0, 0, i, i));
        Rect bounds = region.getBounds();
        int area = GraphicsUtils.getArea(region);
        if (rectF != null) {
            float f = (float) i;
            rectF.set(((float) bounds.left) / f, ((float) bounds.top) / f, 1.0f - (((float) bounds.right) / f), 1.0f - (((float) bounds.bottom) / f));
        }
        drawable.setBounds(rect);
        float f2 = (float) area;
        return getScale(f2, f2, (float) (i * i));
    }

    private boolean isShape(Path path) {
        if (Math.abs((((float) this.mBounds.width()) / ((float) this.mBounds.height())) - 1.0f) > 0.05f) {
            return false;
        }
        this.mMatrix.reset();
        this.mMatrix.setScale((float) this.mBounds.width(), (float) this.mBounds.height());
        Matrix matrix = this.mMatrix;
        Rect rect = this.mBounds;
        matrix.postTranslate((float) rect.left, (float) rect.top);
        path.transform(this.mMatrix, this.mShapePath);
        this.mCanvas.drawPath(this.mShapePath, this.mPaintMaskShape);
        this.mCanvas.drawPath(this.mShapePath, this.mPaintMaskShapeOutline);
        return isTransparentBitmap();
    }

    private boolean isTransparentBitmap() {
        Rect rect;
        ByteBuffer wrap = ByteBuffer.wrap(this.mPixels);
        wrap.rewind();
        this.mBitmap.copyPixelsToBuffer(wrap);
        Rect rect2 = this.mBounds;
        int i = rect2.top;
        int i2 = this.mMaxSize;
        int i3 = i * i2;
        int i4 = i2 - rect2.right;
        int i5 = 0;
        while (true) {
            rect = this.mBounds;
            if (i >= rect.bottom) {
                break;
            }
            int i6 = rect.left;
            int i7 = i3 + i6;
            while (i6 < this.mBounds.right) {
                if ((this.mPixels[i7] & 255) > 40) {
                    i5++;
                }
                i7++;
                i6++;
            }
            i3 = i7 + i4;
            i++;
        }
        if (((float) i5) / ((float) (rect.width() * this.mBounds.height())) < 0.005f) {
            return true;
        }
        return false;
    }

    public synchronized float getScale(Drawable drawable, RectF rectF, Path path, boolean[] zArr) {
        if (!BaseIconFactory.ATLEAST_OREO || !(drawable instanceof AdaptiveIconDrawable)) {
            int intrinsicWidth = drawable.getIntrinsicWidth();
            int intrinsicHeight = drawable.getIntrinsicHeight();
            if (intrinsicWidth <= 0 || intrinsicHeight <= 0) {
                if (intrinsicWidth <= 0 || intrinsicWidth > this.mMaxSize) {
                    intrinsicWidth = this.mMaxSize;
                }
                if (intrinsicHeight <= 0 || intrinsicHeight > this.mMaxSize) {
                    intrinsicHeight = this.mMaxSize;
                }
            } else if (intrinsicWidth > this.mMaxSize || intrinsicHeight > this.mMaxSize) {
                int max = Math.max(intrinsicWidth, intrinsicHeight);
                intrinsicWidth = (this.mMaxSize * intrinsicWidth) / max;
                intrinsicHeight = (this.mMaxSize * intrinsicHeight) / max;
            }
            int i = 0;
            this.mBitmap.eraseColor(0);
            drawable.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
            drawable.draw(this.mCanvas);
            ByteBuffer wrap = ByteBuffer.wrap(this.mPixels);
            wrap.rewind();
            this.mBitmap.copyPixelsToBuffer(wrap);
            int i2 = this.mMaxSize + 1;
            int i3 = this.mMaxSize - intrinsicWidth;
            int i4 = 0;
            int i5 = 0;
            int i6 = -1;
            int i7 = -1;
            int i8 = -1;
            while (i4 < intrinsicHeight) {
                int i9 = -1;
                int i10 = -1;
                for (int i11 = i; i11 < intrinsicWidth; i11++) {
                    if ((this.mPixels[i5] & 255) > 40) {
                        if (i10 == -1) {
                            i10 = i11;
                        }
                        i9 = i11;
                    }
                    i5++;
                }
                i5 += i3;
                this.mLeftBorder[i4] = (float) i10;
                this.mRightBorder[i4] = (float) i9;
                if (i10 != -1) {
                    if (i6 == -1) {
                        i6 = i4;
                    }
                    i2 = Math.min(i2, i10);
                    i7 = Math.max(i7, i9);
                    i8 = i4;
                }
                i4++;
                i = 0;
            }
            if (i6 == -1 || i7 == -1) {
                return 1.0f;
            }
            convertToConvexArray(this.mLeftBorder, 1, i6, i8);
            convertToConvexArray(this.mRightBorder, -1, i6, i8);
            float f = 0.0f;
            for (int i12 = 0; i12 < intrinsicHeight; i12++) {
                if (this.mLeftBorder[i12] > -1.0f) {
                    f += (this.mRightBorder[i12] - this.mLeftBorder[i12]) + 1.0f;
                }
            }
            this.mBounds.left = i2;
            this.mBounds.right = i7;
            this.mBounds.top = i6;
            this.mBounds.bottom = i8;
            if (rectF != null) {
                float f2 = (float) intrinsicWidth;
                float f3 = (float) intrinsicHeight;
                rectF.set(((float) this.mBounds.left) / f2, ((float) this.mBounds.top) / f3, 1.0f - (((float) this.mBounds.right) / f2), 1.0f - (((float) this.mBounds.bottom) / f3));
            }
            if (zArr != null && this.mEnableShapeDetection && zArr.length > 0) {
                zArr[0] = isShape(path);
            }
            return getScale(f, (float) (((i8 + 1) - i6) * ((i7 + 1) - i2)), (float) (intrinsicWidth * intrinsicHeight));
        }
        if (this.mAdaptiveIconScale == 0.0f) {
            this.mAdaptiveIconScale = normalizeAdaptiveIcon(drawable, this.mMaxSize, this.mAdaptiveIconBounds);
        }
        if (rectF != null) {
            rectF.set(this.mAdaptiveIconBounds);
        }
        return this.mAdaptiveIconScale;
    }

    private static void convertToConvexArray(float[] fArr, int i, int i2, int i3) {
        float[] fArr2 = new float[(fArr.length - 1)];
        int i4 = -1;
        float f = Float.MAX_VALUE;
        for (int i5 = i2 + 1; i5 <= i3; i5++) {
            if (fArr[i5] > -1.0f) {
                if (f == Float.MAX_VALUE) {
                    i4 = i2;
                } else {
                    float f2 = ((fArr[i5] - fArr[i4]) / ((float) (i5 - i4))) - f;
                    float f3 = (float) i;
                    if (f2 * f3 < 0.0f) {
                        while (i4 > i2) {
                            i4--;
                            if ((((fArr[i5] - fArr[i4]) / ((float) (i5 - i4))) - fArr2[i4]) * f3 >= 0.0f) {
                                break;
                            }
                        }
                    }
                }
                f = (fArr[i5] - fArr[i4]) / ((float) (i5 - i4));
                for (int i6 = i4; i6 < i5; i6++) {
                    fArr2[i6] = f;
                    fArr[i6] = fArr[i4] + (((float) (i6 - i4)) * f);
                }
                i4 = i5;
            }
        }
    }
}
