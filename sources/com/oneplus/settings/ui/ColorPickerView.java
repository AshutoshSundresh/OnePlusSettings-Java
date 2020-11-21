package com.oneplus.settings.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.android.settings.C0006R$color;
import com.android.settings.C0007R$dimen;

public class ColorPickerView extends View {
    private RectF mDrawingRect;
    private float mHue;
    private Paint mHueIndicatorPaint;
    private float mHueIndicatorWidth;
    private float mHueInidcatorOffset;
    private Paint mHuePaint;
    private float mHuePanelHeight;
    private RectF mHueRect;
    private Shader mHueShader;
    private RectF mHueTouchRect;
    private float mIndicatorBlurRadius;
    private float mIndicatorBorderWidth;
    private int mIndicatorColor;
    private float mIndicatorCornerRadius;
    private OnColorChangedListener mListener;
    private float mMarginTop;
    private float mMinHeight;
    private float mMinWidth;
    private float mPadding;
    private float mPanelSpacing;
    private float mPanelWidth;
    private Paint mSVIndicatorPaint;
    private float mSVIndicatorWidth;
    private float mSVPanelHeight;
    private float mSat;
    private Paint mSatPaint;
    private RectF mSatRect;
    private Shader mSatShader;
    private RectF mSatTouchRect;
    private RectF mSatValRect;
    private RectF mSatValTouchRect;
    private Point mTouchPoint;
    private float mVal;
    private Paint mValPaint;
    private RectF mValRect;
    private Shader mValShader;
    private RectF mValTouchRect;

    public interface OnColorChangedListener {
        void onColorChanged(int i);
    }

    private static boolean isUnspecified(int i) {
        return (i == 1073741824 || i == Integer.MIN_VALUE) ? false : true;
    }

    public ColorPickerView(Context context) {
        this(context, null);
    }

    public ColorPickerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ColorPickerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mTouchPoint = null;
        this.mHue = 360.0f;
        this.mSat = 0.0f;
        this.mVal = 0.0f;
        init();
    }

    private void init() {
        Resources resources = getResources();
        this.mPanelWidth = resources.getDimension(C0007R$dimen.panel_view_width);
        Log.d("ColorPickerView", "panel width 375dp:" + this.mPanelWidth);
        Log.d("ColorPickerView", "device width pixels:" + getResources().getDisplayMetrics().widthPixels);
        this.mPanelSpacing = 0.0f;
        this.mMarginTop = resources.getDimension(C0007R$dimen.margin_top2);
        resources.getDimension(C0007R$dimen.margin_left2);
        this.mHuePanelHeight = resources.getDimension(C0007R$dimen.hue_panel_height);
        float dimension = resources.getDimension(C0007R$dimen.sat_val_panel_height);
        this.mSVPanelHeight = dimension;
        this.mMinWidth = this.mPanelWidth;
        this.mMinHeight = this.mHuePanelHeight + dimension + this.mPanelSpacing;
        this.mIndicatorCornerRadius = resources.getDimension(C0007R$dimen.indicator_corner_radius);
        this.mIndicatorBlurRadius = resources.getDimension(C0007R$dimen.indicator_shadow_radius);
        this.mIndicatorBorderWidth = resources.getDimension(C0007R$dimen.indicator_border_width);
        this.mIndicatorColor = resources.getColor(C0006R$color.indicator_border_color);
        this.mHueIndicatorWidth = resources.getDimension(C0007R$dimen.hue_indicator_width);
        float dimension2 = resources.getDimension(C0007R$dimen.hue_indicator_offset);
        this.mHueInidcatorOffset = dimension2;
        this.mSVIndicatorWidth = dimension2;
        this.mPadding = 0.0f;
        initPaintTools();
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    private void initPaintTools() {
        new Paint();
        this.mHuePaint = new Paint();
        this.mSatPaint = new Paint();
        this.mValPaint = new Paint();
        Paint paint = new Paint();
        this.mHueIndicatorPaint = paint;
        paint.setColor(this.mIndicatorColor);
        this.mHueIndicatorPaint.setStyle(Paint.Style.STROKE);
        this.mHueIndicatorPaint.setStrokeWidth(this.mIndicatorBorderWidth);
        this.mHueIndicatorPaint.setAntiAlias(true);
        this.mHueIndicatorPaint.setShadowLayer(this.mIndicatorBlurRadius, 0.0f, 0.0f, -7829368);
        Paint paint2 = new Paint();
        this.mSVIndicatorPaint = paint2;
        paint2.setStyle(Paint.Style.STROKE);
        this.mSVIndicatorPaint.setColor(this.mIndicatorColor);
        this.mSVIndicatorPaint.setStrokeWidth(this.mIndicatorBorderWidth);
        this.mSVIndicatorPaint.setAntiAlias(true);
        this.mSVIndicatorPaint.setShadowLayer(this.mIndicatorBlurRadius, 0.0f, 0.0f, -7829368);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int mode = View.MeasureSpec.getMode(i);
        View.MeasureSpec.getMode(i2);
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        if (isUnspecified(mode)) {
            size = (int) this.mMinWidth;
        }
        if (isUnspecified(mode)) {
            size2 = (int) this.mMinHeight;
        }
        setMeasuredDimension(size, size2);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.mDrawingRect.width() > 0.0f && this.mDrawingRect.height() > 0.0f) {
            drawHuePanel(canvas);
            drawSatPanel(canvas);
            drawValPanel(canvas);
        }
    }

    private void drawSatPanel(Canvas canvas) {
        int HSVToColor = Color.HSVToColor(new float[]{this.mHue, 1.0f, 1.0f});
        RectF rectF = this.mSatRect;
        float f = rectF.left;
        float f2 = rectF.top;
        LinearGradient linearGradient = new LinearGradient(f, f2, rectF.right, f2, -1, HSVToColor, Shader.TileMode.CLAMP);
        this.mSatShader = linearGradient;
        this.mSatPaint.setShader(linearGradient);
        RectF rectF2 = this.mSatRect;
        float f3 = this.mIndicatorCornerRadius;
        canvas.drawRoundRect(rectF2, f3 * 3.0f, f3 * 3.0f, this.mSatPaint);
        float f4 = this.mSat;
        float width = this.mSatRect.width() - this.mHueIndicatorWidth;
        float f5 = this.mIndicatorBorderWidth;
        float f6 = (f4 * (width - f5)) + this.mSatValRect.left + (f5 / 2.0f);
        RectF rectF3 = new RectF();
        rectF3.left = f6;
        rectF3.right = f6 + this.mHueIndicatorWidth;
        RectF rectF4 = this.mSatRect;
        float f7 = rectF4.top;
        float f8 = this.mHueInidcatorOffset;
        rectF3.top = f7 - f8;
        rectF3.bottom = rectF4.bottom + f8;
        float f9 = this.mIndicatorCornerRadius;
        canvas.drawRoundRect(rectF3, f9, f9, this.mSVIndicatorPaint);
    }

    private void drawValPanel(Canvas canvas) {
        RectF rectF = this.mValRect;
        float f = rectF.left;
        float f2 = rectF.top;
        LinearGradient linearGradient = new LinearGradient(f, f2, rectF.right, f2, -1, -16777216, Shader.TileMode.CLAMP);
        this.mValShader = linearGradient;
        this.mValPaint.setShader(linearGradient);
        RectF rectF2 = this.mValRect;
        float f3 = this.mIndicatorCornerRadius;
        canvas.drawRoundRect(rectF2, f3 * 3.0f, f3 * 3.0f, this.mValPaint);
        float f4 = 1.0f - this.mVal;
        float width = this.mValRect.width() - this.mHueIndicatorWidth;
        float f5 = this.mIndicatorBorderWidth;
        float f6 = (f4 * (width - f5)) + this.mValRect.left + (f5 / 2.0f);
        RectF rectF3 = new RectF();
        rectF3.left = f6;
        rectF3.right = f6 + this.mHueIndicatorWidth;
        RectF rectF4 = this.mValRect;
        float f7 = rectF4.top;
        float f8 = this.mHueInidcatorOffset;
        rectF3.top = f7 - f8;
        rectF3.bottom = rectF4.bottom + f8;
        float f9 = this.mIndicatorCornerRadius;
        canvas.drawRoundRect(rectF3, f9, f9, this.mSVIndicatorPaint);
    }

    private void drawHuePanel(Canvas canvas) {
        RectF rectF = this.mHueRect;
        if (this.mHueShader == null) {
            int[] iArr = new int[361];
            int i = 0;
            int i2 = 0;
            while (i <= 360) {
                iArr[i2] = Color.HSVToColor(new float[]{(float) i, 1.0f, 1.0f});
                i++;
                i2++;
            }
            float f = rectF.left;
            float f2 = rectF.top;
            LinearGradient linearGradient = new LinearGradient(f, f2, rectF.right, f2, iArr, (float[]) null, Shader.TileMode.CLAMP);
            this.mHueShader = linearGradient;
            this.mHuePaint.setShader(linearGradient);
        }
        float f3 = this.mIndicatorCornerRadius;
        canvas.drawRoundRect(rectF, f3 * 3.0f, f3 * 3.0f, this.mHuePaint);
        hueToPoint(this.mHue);
        float f4 = this.mHue;
        float width = this.mHueRect.width() - this.mHueIndicatorWidth;
        float f5 = this.mIndicatorBorderWidth;
        float f6 = ((f4 * (width - f5)) / 360.0f) + this.mHueRect.left + (f5 / 2.0f);
        RectF rectF2 = new RectF();
        rectF2.left = f6;
        rectF2.right = f6 + this.mHueIndicatorWidth;
        float f7 = rectF.top;
        float f8 = this.mHueInidcatorOffset;
        rectF2.top = f7 - f8;
        rectF2.bottom = rectF.bottom + f8;
        float f9 = this.mIndicatorCornerRadius;
        canvas.drawRoundRect(rectF2, f9, f9, this.mHueIndicatorPaint);
    }

    private Point hueToPoint(float f) {
        RectF rectF = this.mHueRect;
        float width = rectF.width();
        Point point = new Point();
        point.y = (int) rectF.top;
        point.x = (int) (rectF.left + ((f * width) / 360.0f));
        return point;
    }

    private float pointToSat(float f, float f2) {
        float f3;
        RectF rectF = this.mSatRect;
        float width = rectF.width();
        float f4 = rectF.left;
        if (f < f4) {
            f3 = 0.0f;
        } else {
            f3 = f > rectF.right ? width : f - f4;
        }
        return (1.0f / width) * f3;
    }

    private float pointToVal(float f, float f2) {
        float f3;
        RectF rectF = this.mValRect;
        float width = rectF.width();
        float f4 = rectF.left;
        if (f < f4) {
            f3 = 0.0f;
        } else {
            f3 = f > rectF.right ? width : f - f4;
        }
        return 1.0f - ((1.0f / width) * f3);
    }

    private float pointToHue(float f) {
        float f2;
        RectF rectF = this.mHueRect;
        float width = rectF.width();
        float f3 = rectF.left;
        if (f < f3) {
            f2 = 0.0f;
        } else {
            f2 = f > rectF.right ? width : f - f3;
        }
        return (f2 * 360.0f) / width;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean z;
        int action = motionEvent.getAction();
        if (action == 0) {
            this.mTouchPoint = new Point((int) motionEvent.getX(), (int) motionEvent.getY());
            z = updateIndicatorIfNeeded(motionEvent);
        } else if (action == 1) {
            this.mTouchPoint = null;
            z = updateIndicatorIfNeeded(motionEvent);
        } else if (action != 2) {
            z = false;
        } else {
            z = updateIndicatorIfNeeded(motionEvent);
        }
        if (!z) {
            return super.onTouchEvent(motionEvent);
        }
        OnColorChangedListener onColorChangedListener = this.mListener;
        if (onColorChangedListener != null) {
            onColorChangedListener.onColorChanged(Color.HSVToColor(new float[]{this.mHue, this.mSat, this.mVal}));
        }
        invalidate();
        return true;
    }

    private boolean updateIndicatorIfNeeded(MotionEvent motionEvent) {
        Point point = this.mTouchPoint;
        if (point == null) {
            return false;
        }
        int i = point.x;
        float f = (float) i;
        float f2 = (float) point.y;
        if (this.mHueTouchRect.contains(f, f2)) {
            this.mHue = pointToHue(motionEvent.getX());
        } else if (this.mSatTouchRect.contains(f, f2)) {
            this.mSat = pointToSat(motionEvent.getX(), motionEvent.getY());
        } else if (!this.mValTouchRect.contains(f, f2)) {
            return false;
        } else {
            this.mVal = pointToVal(motionEvent.getX(), motionEvent.getY());
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        RectF rectF = new RectF();
        this.mDrawingRect = rectF;
        rectF.left = this.mPadding + ((float) getPaddingLeft());
        this.mDrawingRect.right = (((float) i) - this.mPadding) - ((float) getPaddingRight());
        this.mDrawingRect.top = this.mPadding + ((float) getPaddingTop());
        this.mDrawingRect.bottom = (((float) i2) - this.mPadding) - ((float) getPaddingBottom());
        RectF rectF2 = this.mDrawingRect;
        this.mHuePanelHeight = ((((rectF2.bottom - rectF2.top) - (this.mMarginTop * 2.0f)) - (this.mPanelSpacing * 2.0f)) - (this.mHueInidcatorOffset * 2.0f)) / 3.0f;
        setupSatValRect();
        setupHueRect();
    }

    private void setupSatValRect() {
        RectF rectF = this.mDrawingRect;
        float f = rectF.left;
        float f2 = rectF.top + this.mHuePanelHeight;
        float f3 = this.mMarginTop;
        float f4 = f2 + f3 + this.mPanelSpacing;
        float f5 = rectF.bottom - f3;
        float f6 = rectF.right;
        this.mSatValRect = new RectF(f, f4, f6, f5);
        RectF rectF2 = new RectF();
        this.mSatValTouchRect = rectF2;
        RectF rectF3 = this.mSatValRect;
        float f7 = rectF3.left;
        float f8 = this.mSVIndicatorWidth;
        rectF2.left = f7 - (f8 / 2.0f);
        rectF2.right = rectF3.right + (f8 / 2.0f);
        rectF2.top = rectF3.top;
        rectF2.bottom = rectF3.bottom;
        float f9 = rectF.top;
        float f10 = this.mHuePanelHeight;
        float f11 = f9 + f10 + this.mMarginTop + this.mPanelSpacing;
        float f12 = f10 + f11;
        this.mSatRect = new RectF(f, f11, f6, f12);
        RectF rectF4 = new RectF();
        this.mSatTouchRect = rectF4;
        RectF rectF5 = this.mSatRect;
        rectF4.left = rectF5.left;
        rectF4.right = rectF5.right;
        rectF4.top = rectF5.top;
        rectF4.bottom = rectF5.bottom;
        float f13 = f12 + this.mMarginTop + this.mPanelSpacing;
        this.mValRect = new RectF(f, f13, f6, this.mHuePanelHeight + f13);
        RectF rectF6 = new RectF();
        this.mValTouchRect = rectF6;
        RectF rectF7 = this.mValRect;
        rectF6.left = rectF7.left;
        rectF6.right = rectF7.right;
        rectF6.top = rectF7.top;
        rectF6.bottom = rectF7.bottom;
    }

    private void setupHueRect() {
        RectF rectF = this.mDrawingRect;
        float f = rectF.left;
        float f2 = rectF.top + (this.mHueInidcatorOffset * 2.0f);
        this.mHueRect = new RectF(f, f2, rectF.right, this.mHuePanelHeight + f2);
        RectF rectF2 = new RectF();
        this.mHueTouchRect = rectF2;
        RectF rectF3 = this.mHueRect;
        rectF2.left = rectF3.left;
        rectF2.right = rectF3.right;
        rectF2.top = rectF3.top + this.mHueInidcatorOffset;
        rectF2.bottom = rectF3.bottom;
    }

    public void setOnColorChangedListener(OnColorChangedListener onColorChangedListener) {
        this.mListener = onColorChangedListener;
    }

    public int getColor() {
        return Color.HSVToColor(new float[]{this.mHue, this.mSat, this.mVal});
    }

    public void setColor(int i) {
        setColor(i, false);
    }

    public void setColor(int i, boolean z) {
        OnColorChangedListener onColorChangedListener;
        float[] fArr = new float[3];
        Color.colorToHSV(i, fArr);
        float f = fArr[0];
        this.mHue = f;
        float f2 = fArr[1];
        this.mSat = f2;
        float f3 = fArr[2];
        this.mVal = f3;
        if (z && (onColorChangedListener = this.mListener) != null) {
            onColorChangedListener.onColorChanged(Color.HSVToColor(new float[]{f, f2, f3}));
        }
        invalidate();
    }
}
