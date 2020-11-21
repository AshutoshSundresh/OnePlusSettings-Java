package com.oneplus.settings.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;
import com.android.settings.R$styleable;
import java.util.Arrays;

public class RadiusImageView extends AppCompatImageView {
    private PorterDuffXfermode mDstIn = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    private PorterDuffXfermode mDstOut = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
    private PathExtension mExtension;
    private Paint mPaint;
    private Path mPath;
    private Paint mPathPaint;
    private float mRadius = 0.0f;
    private Shape mShape;
    private Bitmap mShapeBitmap;
    private boolean mShapeChanged;
    private int mShapeMode = 0;
    private Bitmap mStrokeBitmap;
    private int mStrokeColor = 637534208;
    private Paint mStrokePaint;
    private Shape mStrokeShape;
    private float mStrokeWidth = 0.0f;

    public interface PathExtension {
        void onLayout(Path path, int i, int i2);
    }

    public RadiusImageView(Context context) {
        super(context);
        new Paint();
        init(null);
    }

    public RadiusImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        new Paint();
        init(attributeSet);
    }

    public RadiusImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        new Paint();
        init(attributeSet);
    }

    private void init(AttributeSet attributeSet) {
        setLayerType(2, null);
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R$styleable.RadiusImageView);
            this.mShapeMode = obtainStyledAttributes.getInt(R$styleable.RadiusImageView_shape_mode, 0);
            this.mRadius = obtainStyledAttributes.getDimension(R$styleable.RadiusImageView_round_radius, 0.0f);
            this.mStrokeWidth = obtainStyledAttributes.getDimension(R$styleable.RadiusImageView_stroke_width, 0.0f);
            this.mStrokeColor = obtainStyledAttributes.getColor(R$styleable.RadiusImageView_stroke_color, this.mStrokeColor);
            obtainStyledAttributes.recycle();
        }
        Paint paint = new Paint(1);
        this.mPaint = paint;
        paint.setFilterBitmap(true);
        this.mPaint.setColor(-16777216);
        this.mPaint.setXfermode(this.mDstIn);
        Paint paint2 = new Paint(1);
        this.mStrokePaint = paint2;
        paint2.setFilterBitmap(true);
        this.mStrokePaint.setColor(-16777216);
        Paint paint3 = new Paint(1);
        this.mPathPaint = paint3;
        paint3.setFilterBitmap(true);
        this.mPathPaint.setColor(-16777216);
        this.mPathPaint.setXfermode(this.mDstOut);
        this.mPath = new Path();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (z || this.mShapeChanged) {
            this.mShapeChanged = false;
            int measuredWidth = getMeasuredWidth();
            int measuredHeight = getMeasuredHeight();
            if (this.mShapeMode == 2) {
                this.mRadius = ((float) Math.min(measuredWidth, measuredHeight)) / 2.0f;
            }
            if (this.mShape == null || this.mRadius != 0.0f) {
                float[] fArr = new float[8];
                Arrays.fill(fArr, this.mRadius);
                this.mShape = new RoundRectShape(fArr, null, null);
                this.mStrokeShape = new RoundRectShape(fArr, null, null);
            }
            float f = (float) measuredWidth;
            float f2 = (float) measuredHeight;
            this.mShape.resize(f, f2);
            Shape shape = this.mStrokeShape;
            float f3 = this.mStrokeWidth;
            shape.resize(f - (f3 * 2.0f), f2 - (f3 * 2.0f));
            makeStrokeBitmap();
            makeShapeBitmap();
            PathExtension pathExtension = this.mExtension;
            if (pathExtension != null) {
                pathExtension.onLayout(this.mPath, measuredWidth, measuredHeight);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mStrokeWidth > 0.0f && this.mStrokeShape != null) {
            Bitmap bitmap = this.mStrokeBitmap;
            if (bitmap == null || bitmap.isRecycled()) {
                makeStrokeBitmap();
            }
            int saveLayer = canvas.saveLayer(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), null, 31);
            this.mStrokePaint.setXfermode(null);
            canvas.drawBitmap(this.mStrokeBitmap, 0.0f, 0.0f, this.mStrokePaint);
            float f = this.mStrokeWidth;
            canvas.translate(f, f);
            this.mStrokePaint.setXfermode(this.mDstOut);
            this.mStrokeShape.draw(canvas, this.mStrokePaint);
            canvas.restoreToCount(saveLayer);
        }
        if (this.mExtension != null) {
            canvas.drawPath(this.mPath, this.mPathPaint);
        }
        int i = this.mShapeMode;
        if (i == 1 || i == 2) {
            Bitmap bitmap2 = this.mShapeBitmap;
            if (bitmap2 == null || bitmap2.isRecycled()) {
                makeShapeBitmap();
            }
            canvas.drawBitmap(this.mShapeBitmap, 0.0f, 0.0f, this.mPaint);
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        releaseBitmap(this.mShapeBitmap);
        releaseBitmap(this.mStrokeBitmap);
    }

    private void makeStrokeBitmap() {
        if (this.mStrokeWidth > 0.0f) {
            int measuredWidth = getMeasuredWidth();
            int measuredHeight = getMeasuredHeight();
            if (measuredWidth != 0 && measuredHeight != 0) {
                releaseBitmap(this.mStrokeBitmap);
                this.mStrokeBitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(this.mStrokeBitmap);
                Paint paint = new Paint(1);
                paint.setColor(this.mStrokeColor);
                canvas.drawRect(new RectF(0.0f, 0.0f, (float) measuredWidth, (float) measuredHeight), paint);
            }
        }
    }

    private void releaseBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    private void makeShapeBitmap() {
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        if (measuredWidth != 0 && measuredHeight != 0) {
            releaseBitmap(this.mShapeBitmap);
            this.mShapeBitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(this.mShapeBitmap);
            Paint paint = new Paint(1);
            paint.setColor(-16777216);
            this.mShape.draw(canvas, paint);
        }
    }

    public void setExtension(PathExtension pathExtension) {
        this.mExtension = pathExtension;
        requestLayout();
    }

    public void setStroke(int i, float f) {
        float f2 = this.mStrokeWidth;
        if (f2 > 0.0f) {
            if (f2 != f) {
                this.mStrokeWidth = f;
                int measuredWidth = getMeasuredWidth();
                int measuredHeight = getMeasuredHeight();
                Shape shape = this.mStrokeShape;
                float f3 = this.mStrokeWidth;
                shape.resize(((float) measuredWidth) - (f3 * 2.0f), ((float) measuredHeight) - (f3 * 2.0f));
                postInvalidate();
            }
            if (this.mStrokeColor != i) {
                this.mStrokeColor = i;
                makeStrokeBitmap();
                postInvalidate();
            }
        }
    }

    public void setStrokeColor(int i) {
        setStroke(i, this.mStrokeWidth);
    }

    public void setStrokeWidth(float f) {
        setStroke(this.mStrokeColor, f);
    }

    public void setShape(int i, float f) {
        boolean z = (this.mShapeMode == i && this.mRadius == f) ? false : true;
        this.mShapeChanged = z;
        if (z) {
            this.mShapeMode = i;
            this.mRadius = f;
            this.mShape = null;
            this.mStrokeShape = null;
            requestLayout();
        }
    }

    public void setShapeMode(int i) {
        setShape(i, this.mRadius);
    }

    public void setShapeRadius(float f) {
        setShape(this.mShapeMode, f);
    }
}
