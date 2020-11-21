package com.android.settingslib.drawable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import com.android.settingslib.R$dimen;

public class CircleFramedDrawable extends Drawable {
    private final Bitmap mBitmap;
    private RectF mDstRect;
    private final Paint mPaint;
    private float mScale = 1.0f;
    private final int mSize;
    private Rect mSrcRect;

    public int getOpacity() {
        return -3;
    }

    public void setAlpha(int i) {
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public static CircleFramedDrawable getInstance(Context context, Bitmap bitmap) {
        return new CircleFramedDrawable(bitmap, (int) context.getResources().getDimension(R$dimen.circle_avatar_size));
    }

    public CircleFramedDrawable(Bitmap bitmap, int i) {
        this.mSize = i;
        this.mBitmap = Bitmap.createBitmap(i, i, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(this.mBitmap);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int min = Math.min(width, height);
        Rect rect = new Rect((width - min) / 2, (height - min) / 2, min, min);
        int i2 = this.mSize;
        RectF rectF = new RectF(0.0f, 0.0f, (float) i2, (float) i2);
        Path path = new Path();
        path.addArc(rectF, 0.0f, 360.0f);
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setAntiAlias(true);
        this.mPaint.setColor(-16777216);
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setFlags(1);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, 3));
        canvas.drawPath(path, this.mPaint);
        this.mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rectF, this.mPaint);
        this.mPaint.setXfermode(null);
        int i3 = this.mSize;
        this.mSrcRect = new Rect(0, 0, i3, i3);
        int i4 = this.mSize;
        this.mDstRect = new RectF(0.0f, 0.0f, (float) i4, (float) i4);
    }

    public void draw(Canvas canvas) {
        float f = this.mScale;
        int i = this.mSize;
        float f2 = (((float) i) - (f * ((float) i))) / 2.0f;
        this.mDstRect.set(f2, f2, ((float) i) - f2, ((float) i) - f2);
        canvas.drawBitmap(this.mBitmap, this.mSrcRect, this.mDstRect, (Paint) null);
    }

    public int getIntrinsicWidth() {
        return this.mSize;
    }

    public int getIntrinsicHeight() {
        return this.mSize;
    }
}
