package com.android.settings.wifi.qrcode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import com.android.settings.C0006R$color;

public class QrDecorateView extends View {
    private final int mBackgroundColor;
    private final Paint mBackgroundPaint;
    private final int mCornerColor;
    private boolean mFocused;
    private final int mFocusedCornerColor;
    private RectF mInnerFrame;
    private Bitmap mMaskBitmap;
    private Canvas mMaskCanvas;
    private RectF mOuterFrame;
    private final Paint mStrokePaint;
    private final Paint mTransparentPaint;

    public QrDecorateView(Context context) {
        this(context, null);
    }

    public QrDecorateView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public QrDecorateView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public QrDecorateView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mFocused = false;
        TypedValue.applyDimension(1, 16.0f, getResources().getDisplayMetrics());
        TypedValue.applyDimension(1, 14.0f, getResources().getDisplayMetrics());
        this.mCornerColor = context.getResources().getColor(C0006R$color.qr_corner_line_color);
        this.mFocusedCornerColor = context.getResources().getColor(C0006R$color.qr_focused_corner_line_color);
        this.mBackgroundColor = context.getResources().getColor(C0006R$color.qr_background_color);
        Paint paint = new Paint();
        this.mStrokePaint = paint;
        paint.setAntiAlias(true);
        Paint paint2 = new Paint();
        this.mTransparentPaint = paint2;
        paint2.setAntiAlias(true);
        this.mTransparentPaint.setColor(getResources().getColor(17170445));
        this.mTransparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        Paint paint3 = new Paint();
        this.mBackgroundPaint = paint3;
        paint3.setColor(this.mBackgroundColor);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.mMaskBitmap == null) {
            this.mMaskBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            this.mMaskCanvas = new Canvas(this.mMaskBitmap);
        }
        calculateFramePos();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        this.mStrokePaint.setColor(this.mFocused ? this.mFocusedCornerColor : this.mCornerColor);
        this.mMaskCanvas.drawColor(this.mBackgroundColor);
        this.mMaskCanvas.drawRect(this.mOuterFrame, this.mStrokePaint);
        this.mMaskCanvas.drawRect(this.mInnerFrame, this.mTransparentPaint);
        canvas.drawBitmap(this.mMaskBitmap, 0.0f, 0.0f, this.mBackgroundPaint);
        super.onDraw(canvas);
    }

    private void calculateFramePos() {
        float applyDimension = TypedValue.applyDimension(1, 264.0f, getResources().getDisplayMetrics()) / 2.0f;
        float applyDimension2 = TypedValue.applyDimension(1, 2.0f, getResources().getDisplayMetrics());
        float width = (float) (getWidth() / 2);
        float height = (float) (getHeight() / 2);
        this.mOuterFrame = new RectF(width - applyDimension, height - applyDimension, width + applyDimension, height + applyDimension);
        RectF rectF = this.mOuterFrame;
        this.mInnerFrame = new RectF(rectF.left + applyDimension2, rectF.top + applyDimension2, rectF.right - applyDimension2, rectF.bottom - applyDimension2);
    }

    public void setFocused(boolean z) {
        this.mFocused = z;
        invalidate();
    }
}
