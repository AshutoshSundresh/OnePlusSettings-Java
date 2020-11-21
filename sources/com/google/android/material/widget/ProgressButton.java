package com.google.android.material.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import com.google.android.material.R$color;
import com.google.android.material.R$styleable;

public class ProgressButton extends View {
    private int DEFAULT_HEIGHT_DP;
    private Paint bgPaint;
    private RectF bgRectf;
    BitmapShader bitmapShader;
    private int borderWidth;
    private float maxProgress;
    private Bitmap pgBitmap;
    private Canvas pgCanvas;
    private Paint pgPaint;
    private float progress;
    private int progressColor;
    private String progressText;
    private int radius;
    private Paint textPaint;
    private Rect textRect;
    private int textSize;
    private PorterDuffXfermode xfermode;

    public ProgressButton(Context context) {
        this(context, null, 0);
    }

    public ProgressButton(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ProgressButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP);
        this.DEFAULT_HEIGHT_DP = 35;
        this.maxProgress = 100.0f;
        this.progressText = "";
        initAttrs(attributeSet);
    }

    private void initAttrs(AttributeSet attributeSet) {
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R$styleable.ProgressButton);
        try {
            this.textSize = (int) obtainStyledAttributes.getDimension(R$styleable.ProgressButton_progress_textSize, 12.0f);
            this.progressColor = obtainStyledAttributes.getColor(R$styleable.ProgressButton_progress_loadingColor, getResources().getColor(R$color.oneplus_accent_color));
            this.radius = (int) obtainStyledAttributes.getDimension(R$styleable.ProgressButton_progress_radius, 16.0f);
            this.borderWidth = (int) obtainStyledAttributes.getDimension(R$styleable.ProgressButton_progress_borderWidth, 1.0f);
        } finally {
            obtainStyledAttributes.recycle();
        }
    }

    private void init() {
        Paint paint = new Paint(5);
        this.bgPaint = paint;
        paint.setStyle(Paint.Style.STROKE);
        this.bgPaint.setStrokeWidth((float) this.borderWidth);
        Paint paint2 = new Paint(1);
        this.pgPaint = paint2;
        paint2.setStyle(Paint.Style.FILL);
        Paint paint3 = new Paint(1);
        this.textPaint = paint3;
        paint3.setTextSize((float) this.textSize);
        this.textRect = new Rect();
        int i = this.borderWidth;
        this.bgRectf = new RectF((float) i, (float) i, (float) (getMeasuredWidth() - this.borderWidth), (float) (getMeasuredHeight() - this.borderWidth));
        initPgBimap();
    }

    private void initPgBimap() {
        this.pgBitmap = Bitmap.createBitmap(getMeasuredWidth() - this.borderWidth, getMeasuredHeight() - this.borderWidth, Bitmap.Config.ARGB_8888);
        this.pgCanvas = new Canvas(this.pgBitmap);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int size = View.MeasureSpec.getSize(i);
        int mode = View.MeasureSpec.getMode(i2);
        int size2 = View.MeasureSpec.getSize(i2);
        if (mode == Integer.MIN_VALUE) {
            size2 = dp2px(this.DEFAULT_HEIGHT_DP);
        } else if (!(mode == 0 || mode == 1073741824)) {
            size2 = 0;
        }
        setMeasuredDimension(size, size2);
        if (this.pgBitmap == null) {
            init();
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackGround(canvas);
        drawProgress(canvas);
        drawProgressText(canvas);
        drawColorProgressText(canvas);
    }

    private void drawBackGround(Canvas canvas) {
        this.bgPaint.setColor(this.progressColor);
        this.bgPaint.setAlpha(30);
        this.bgPaint.setStyle(Paint.Style.FILL);
        RectF rectF = this.bgRectf;
        int i = this.radius;
        canvas.drawRoundRect(rectF, (float) i, (float) i, this.bgPaint);
    }

    private void drawProgress(Canvas canvas) {
        this.pgPaint.setColor(this.progressColor);
        float measuredWidth = (this.progress / this.maxProgress) * ((float) getMeasuredWidth());
        this.pgCanvas.save();
        this.pgCanvas.clipRect(0.0f, 0.0f, measuredWidth, (float) getMeasuredHeight());
        this.pgCanvas.drawColor(this.progressColor);
        this.pgCanvas.restore();
        this.pgPaint.setXfermode(this.xfermode);
        this.pgPaint.setXfermode(null);
        Bitmap bitmap = this.pgBitmap;
        Shader.TileMode tileMode = Shader.TileMode.CLAMP;
        BitmapShader bitmapShader2 = new BitmapShader(bitmap, tileMode, tileMode);
        this.bitmapShader = bitmapShader2;
        this.pgPaint.setShader(bitmapShader2);
        RectF rectF = this.bgRectf;
        int i = this.radius;
        canvas.drawRoundRect(rectF, (float) i, (float) i, this.pgPaint);
    }

    private void drawProgressText(Canvas canvas) {
        this.textPaint.setColor(this.progressColor);
        Paint paint = this.textPaint;
        String str = this.progressText;
        paint.getTextBounds(str, 0, str.length(), this.textRect);
        int width = this.textRect.width();
        int height = this.textRect.height();
        canvas.drawText(this.progressText, (float) ((getMeasuredWidth() - width) / 2), (float) ((getMeasuredHeight() + height) / 2), this.textPaint);
    }

    private void drawColorProgressText(Canvas canvas) {
        this.textPaint.setColor(-1);
        int width = this.textRect.width();
        int height = this.textRect.height();
        float measuredWidth = (float) ((getMeasuredWidth() - width) / 2);
        float measuredHeight = (float) ((getMeasuredHeight() + height) / 2);
        float measuredWidth2 = (this.progress / this.maxProgress) * ((float) getMeasuredWidth());
        if (measuredWidth2 > measuredWidth) {
            canvas.save();
            canvas.clipRect(measuredWidth, 0.0f, Math.min(measuredWidth2, (((float) width) * 1.1f) + measuredWidth), (float) getMeasuredHeight());
            canvas.drawText(this.progressText, measuredWidth, measuredHeight, this.textPaint);
            canvas.restore();
        }
    }

    public void setProgress(float f) {
        float f2 = this.maxProgress;
        if (f < f2) {
            this.progress = f;
        } else {
            this.progress = f2;
        }
        invalidate();
    }

    public float getProgress() {
        return this.progress;
    }

    public void setText(String str) {
        this.progressText = str;
        invalidate();
    }

    private int dp2px(int i) {
        return (int) (((float) i) * getContext().getResources().getDisplayMetrics().density);
    }
}
