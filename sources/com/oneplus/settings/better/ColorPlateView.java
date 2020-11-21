package com.oneplus.settings.better;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.android.settings.C0006R$color;
import com.android.settings.C0007R$dimen;
import com.android.settings.C0008R$drawable;

public class ColorPlateView extends View {
    private Bitmap mBmpColorPlate;
    private OnColorChangeListener mColorChangeListener;
    private int mColorPlateMargin;
    private int mCurrentXProgress;
    private int mCurrentYProgress;
    private int mHeightSelectBox;
    private int mIntrinsicHeightColorPlate;
    private int mIntrinsicWidthColorPlate;
    int mLastXProgress;
    int mLastYProgress;
    private int mMaxXProgress = 100;
    private int mMaxYProgress = 100;
    private Paint mPaintColorPlate;
    private Paint mPaintSelectBox;
    private Rect mRectColorPlate = new Rect();
    private RectF mRectSelectBox = new RectF();
    private float mSelectBoxCornerRadius;
    private int mWidthSelectBox;

    public interface OnColorChangeListener {
        void colorChanged(int i, int i2, int i3, int i4);

        void onStartTrackingTouch(int i, int i2, int i3, int i4);

        void onStopTrackingTouch(int i, int i2, int i3, int i4);
    }

    public ColorPlateView(Context context) {
        super(context);
        init();
    }

    public ColorPlateView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public ColorPlateView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private void init() {
        this.mBmpColorPlate = drawableToBitmap(getResources().getDrawable(C0008R$drawable.op_img_color_plate));
        this.mSelectBoxCornerRadius = getResources().getDimension(C0007R$dimen.indicator_corner_radius);
        this.mIntrinsicWidthColorPlate = this.mBmpColorPlate.getWidth();
        this.mIntrinsicHeightColorPlate = this.mBmpColorPlate.getHeight();
        int dimension = (int) getResources().getDimension(C0007R$dimen.hue_indicator_width);
        this.mWidthSelectBox = dimension;
        this.mHeightSelectBox = dimension;
        this.mColorPlateMargin = (int) (((float) dimension) * 0.88f);
        Paint paint = new Paint();
        this.mPaintColorPlate = paint;
        paint.setAntiAlias(true);
        this.mPaintColorPlate.setFilterBitmap(true);
        Paint paint2 = new Paint();
        this.mPaintSelectBox = paint2;
        paint2.setColor(getResources().getColor(C0006R$color.indicator_border_color));
        this.mPaintSelectBox.setStyle(Paint.Style.STROKE);
        this.mPaintSelectBox.setStrokeWidth(getResources().getDimension(C0007R$dimen.indicator_border_width));
        this.mPaintSelectBox.setAntiAlias(true);
        this.mPaintSelectBox.setShadowLayer(getResources().getDimension(C0007R$dimen.indicator_shadow_radius), 0.0f, 0.0f, -7829368);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(this.mBmpColorPlate, new Rect(0, 0, this.mBmpColorPlate.getWidth(), this.mBmpColorPlate.getHeight()), this.mRectColorPlate, this.mPaintColorPlate);
        RectF rectF = this.mRectSelectBox;
        float f = this.mSelectBoxCornerRadius;
        canvas.drawRoundRect(rectF, f, f, this.mPaintSelectBox);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int measureWidth = measureWidth(i);
        int i3 = (this.mIntrinsicHeightColorPlate * measureWidth) / this.mIntrinsicWidthColorPlate;
        setMeasuredDimension(measureWidth, i3);
        Rect rect = this.mRectColorPlate;
        int i4 = this.mColorPlateMargin;
        rect.left = i4;
        rect.top = i4;
        rect.right = measureWidth - i4;
        rect.bottom = i3 - i4;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!isEnabled()) {
            return false;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        int i = this.mRectColorPlate.left;
        if (x < i) {
            x = i;
        }
        int i2 = this.mRectColorPlate.right;
        if (x > i2) {
            x = i2;
        }
        int i3 = this.mRectColorPlate.top;
        if (y < i3) {
            y = i3;
        }
        int i4 = this.mRectColorPlate.bottom;
        if (y > i4) {
            y = i4;
        }
        RectF rectF = this.mRectSelectBox;
        int i5 = this.mWidthSelectBox;
        rectF.left = (float) (x - (i5 / 2));
        int i6 = this.mHeightSelectBox;
        rectF.top = (float) (y - (i6 / 2));
        rectF.right = (float) ((i5 / 2) + x);
        rectF.bottom = (float) ((i6 / 2) + y);
        invalidate();
        this.mLastXProgress = this.mCurrentXProgress;
        this.mLastYProgress = this.mCurrentYProgress;
        this.mCurrentXProgress = calculateXProgress(x);
        this.mCurrentYProgress = calculateYProgress(y);
        int action = motionEvent.getAction();
        if (action == 0) {
            OnColorChangeListener onColorChangeListener = this.mColorChangeListener;
            if (onColorChangeListener != null) {
                onColorChangeListener.onStartTrackingTouch(this.mCurrentXProgress, this.mMaxXProgress, this.mCurrentYProgress, this.mMaxYProgress);
            }
        } else if (action == 1) {
            OnColorChangeListener onColorChangeListener2 = this.mColorChangeListener;
            if (onColorChangeListener2 != null) {
                onColorChangeListener2.onStopTrackingTouch(this.mCurrentXProgress, this.mMaxXProgress, this.mCurrentYProgress, this.mMaxYProgress);
            }
        } else if (action == 2 && this.mColorChangeListener != null && (Math.abs(this.mLastXProgress - this.mCurrentXProgress) >= 1 || Math.abs(this.mLastYProgress - this.mCurrentYProgress) >= 1)) {
            this.mColorChangeListener.colorChanged(this.mCurrentXProgress, this.mMaxXProgress, this.mCurrentYProgress, this.mMaxYProgress);
        }
        return true;
    }

    private int calculateXProgress(int i) {
        Rect rect = this.mRectColorPlate;
        return (int) ((((float) (i - rect.left)) / ((float) rect.width())) * ((float) this.mMaxXProgress));
    }

    private int calculateYProgress(int i) {
        Rect rect = this.mRectColorPlate;
        return (int) ((((float) (i - rect.top)) / ((float) rect.height())) * ((float) this.mMaxYProgress));
    }

    private int measureWidth(int i) {
        int mode = View.MeasureSpec.getMode(i);
        int size = View.MeasureSpec.getSize(i);
        if (mode == Integer.MIN_VALUE) {
            return Math.min(864, size);
        }
        if (mode == 0 || mode != 1073741824) {
            return 864;
        }
        return size;
    }

    public OnColorChangeListener getColorChangeListener() {
        return this.mColorChangeListener;
    }

    public void setColorChangeListener(OnColorChangeListener onColorChangeListener) {
        this.mColorChangeListener = onColorChangeListener;
    }

    public int[] getXYProgress() {
        return new int[]{this.mCurrentXProgress, this.mCurrentYProgress};
    }

    public int getMaxXProgress() {
        return this.mMaxXProgress;
    }

    public void setMaxXProgress(int i) {
        this.mMaxXProgress = i;
    }

    public int getMaxYProgress() {
        return this.mMaxYProgress;
    }

    public void setMaxYProgress(int i) {
        this.mMaxYProgress = i;
    }

    private static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap.Config config;
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        if (drawable.getOpacity() != -1) {
            config = Bitmap.Config.ARGB_8888;
        } else {
            config = Bitmap.Config.RGB_565;
        }
        Bitmap createBitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, config);
        Canvas canvas = new Canvas(createBitmap);
        drawable.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
        drawable.draw(canvas);
        return createBitmap;
    }
}
