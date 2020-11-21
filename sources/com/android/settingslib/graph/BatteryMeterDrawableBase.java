package com.android.settingslib.graph;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import com.android.settingslib.R$array;
import com.android.settingslib.R$color;
import com.android.settingslib.R$dimen;
import com.android.settingslib.R$fraction;
import com.android.settingslib.R$string;
import com.android.settingslib.Utils;

public class BatteryMeterDrawableBase extends Drawable {
    protected final Paint mBatteryPaint;
    protected final Paint mBoltPaint;
    private final RectF mButtonFrame;
    protected float mButtonHeightFraction;
    private int mChargeColor;
    private boolean mCharging;
    private final int[] mColors;
    protected final Context mContext;
    private final int mCriticalLevel;
    private final RectF mFrame;
    protected final Paint mFramePaint;
    private int mHeight;
    private int mIconTint = -1;
    private final int mIntrinsicHeight;
    private final int mIntrinsicWidth;
    private int mLevel = -1;
    private final Path mOutlinePath;
    private final Rect mPadding;
    private final RectF mPlusFrame;
    protected final Paint mPlusPaint;
    private final Path mPlusPath;
    private final float[] mPlusPoints;
    protected boolean mPowerSaveAsColorError = true;
    private boolean mPowerSaveEnabled;
    protected final Paint mPowersavePaint;
    private final Path mShapePath;
    private boolean mShowPercent;
    private float mTextHeight;
    protected final Paint mTextPaint;
    private final Path mTextPath;
    private String mWarningString;
    private float mWarningTextHeight;
    protected final Paint mWarningTextPaint;
    private int mWidth;

    public int getOpacity() {
        return 0;
    }

    /* access modifiers changed from: protected */
    public float getRadiusRatio() {
        return 0.05882353f;
    }

    public void setAlpha(int i) {
    }

    public BatteryMeterDrawableBase(Context context, int i) {
        new Path();
        this.mPlusPath = new Path();
        this.mPadding = new Rect();
        this.mFrame = new RectF();
        this.mButtonFrame = new RectF();
        new RectF();
        this.mPlusFrame = new RectF();
        this.mShapePath = new Path();
        this.mOutlinePath = new Path();
        this.mTextPath = new Path();
        this.mContext = context;
        Resources resources = context.getResources();
        TypedArray obtainTypedArray = resources.obtainTypedArray(R$array.batterymeter_color_levels);
        TypedArray obtainTypedArray2 = resources.obtainTypedArray(R$array.batterymeter_color_values);
        int length = obtainTypedArray.length();
        this.mColors = new int[(length * 2)];
        for (int i2 = 0; i2 < length; i2++) {
            int i3 = i2 * 2;
            this.mColors[i3] = obtainTypedArray.getInt(i2, 0);
            if (obtainTypedArray2.getType(i2) == 2) {
                this.mColors[i3 + 1] = Utils.getColorAttrDefaultColor(context, obtainTypedArray2.getThemeAttributeId(i2, 0));
            } else {
                this.mColors[i3 + 1] = obtainTypedArray2.getColor(i2, 0);
            }
        }
        obtainTypedArray.recycle();
        obtainTypedArray2.recycle();
        this.mWarningString = context.getString(R$string.battery_meter_very_low_overlay_symbol);
        this.mCriticalLevel = this.mContext.getResources().getInteger(17694767);
        this.mButtonHeightFraction = context.getResources().getFraction(R$fraction.battery_button_height_fraction, 1, 1);
        context.getResources().getFraction(R$fraction.battery_subpixel_smoothing_left, 1, 1);
        context.getResources().getFraction(R$fraction.battery_subpixel_smoothing_right, 1, 1);
        Paint paint = new Paint(1);
        this.mFramePaint = paint;
        paint.setColor(i);
        this.mFramePaint.setDither(true);
        this.mFramePaint.setStrokeWidth(0.0f);
        this.mFramePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        Paint paint2 = new Paint(1);
        this.mBatteryPaint = paint2;
        paint2.setDither(true);
        this.mBatteryPaint.setStrokeWidth(0.0f);
        this.mBatteryPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.mTextPaint = new Paint(1);
        this.mTextPaint.setTypeface(Typeface.create("sans-serif-condensed", 1));
        this.mTextPaint.setTextAlign(Paint.Align.CENTER);
        this.mWarningTextPaint = new Paint(1);
        this.mWarningTextPaint.setTypeface(Typeface.create("sans-serif", 1));
        this.mWarningTextPaint.setTextAlign(Paint.Align.CENTER);
        int[] iArr = this.mColors;
        if (iArr.length > 1) {
            this.mWarningTextPaint.setColor(iArr[1]);
        }
        this.mChargeColor = Utils.getColorStateListDefaultColor(this.mContext, R$color.meter_consumed_color);
        Paint paint3 = new Paint(1);
        this.mBoltPaint = paint3;
        paint3.setColor(Utils.getColorStateListDefaultColor(this.mContext, R$color.batterymeter_bolt_color));
        loadPoints(resources, R$array.batterymeter_bolt_points);
        Paint paint4 = new Paint(1);
        this.mPlusPaint = paint4;
        paint4.setColor(Utils.getColorStateListDefaultColor(this.mContext, R$color.batterymeter_plus_color));
        this.mPlusPoints = loadPoints(resources, R$array.batterymeter_plus_points);
        Paint paint5 = new Paint(1);
        this.mPowersavePaint = paint5;
        paint5.setColor(this.mPlusPaint.getColor());
        this.mPowersavePaint.setStyle(Paint.Style.STROKE);
        this.mPowersavePaint.setStrokeWidth((float) context.getResources().getDimensionPixelSize(R$dimen.battery_powersave_outline_thickness));
        this.mIntrinsicWidth = context.getResources().getDimensionPixelSize(R$dimen.battery_width);
        this.mIntrinsicHeight = context.getResources().getDimensionPixelSize(R$dimen.battery_height);
    }

    public int getIntrinsicHeight() {
        return this.mIntrinsicHeight;
    }

    public int getIntrinsicWidth() {
        return this.mIntrinsicWidth;
    }

    public void setShowPercent(boolean z) {
        this.mShowPercent = z;
        postInvalidate();
    }

    public void setCharging(boolean z) {
        this.mCharging = z;
        postInvalidate();
    }

    public boolean getCharging() {
        return this.mCharging;
    }

    public void setBatteryLevel(int i) {
        this.mLevel = i;
        postInvalidate();
    }

    public int getBatteryLevel() {
        return this.mLevel;
    }

    /* access modifiers changed from: protected */
    public void postInvalidate() {
        unscheduleSelf(new Runnable() {
            /* class com.android.settingslib.graph.$$Lambda$ExJ0HHRzS2_LMtcBJqtFiovbn0w */

            public final void run() {
                BatteryMeterDrawableBase.this.invalidateSelf();
            }
        });
        scheduleSelf(new Runnable() {
            /* class com.android.settingslib.graph.$$Lambda$ExJ0HHRzS2_LMtcBJqtFiovbn0w */

            public final void run() {
                BatteryMeterDrawableBase.this.invalidateSelf();
            }
        }, 0);
    }

    private static float[] loadPoints(Resources resources, int i) {
        int[] intArray = resources.getIntArray(i);
        int i2 = 0;
        int i3 = 0;
        for (int i4 = 0; i4 < intArray.length; i4 += 2) {
            i2 = Math.max(i2, intArray[i4]);
            i3 = Math.max(i3, intArray[i4 + 1]);
        }
        float[] fArr = new float[intArray.length];
        for (int i5 = 0; i5 < intArray.length; i5 += 2) {
            fArr[i5] = ((float) intArray[i5]) / ((float) i2);
            int i6 = i5 + 1;
            fArr[i6] = ((float) intArray[i6]) / ((float) i3);
        }
        return fArr;
    }

    public void setBounds(int i, int i2, int i3, int i4) {
        super.setBounds(i, i2, i3, i4);
        updateSize();
    }

    private void updateSize() {
        Rect bounds = getBounds();
        int i = bounds.bottom;
        Rect rect = this.mPadding;
        int i2 = (i - rect.bottom) - (bounds.top + rect.top);
        this.mHeight = i2;
        this.mWidth = (bounds.right - rect.right) - (bounds.left + rect.left);
        this.mWarningTextPaint.setTextSize(((float) i2) * 0.75f);
        this.mWarningTextHeight = -this.mWarningTextPaint.getFontMetrics().ascent;
    }

    public boolean getPadding(Rect rect) {
        Rect rect2 = this.mPadding;
        if (rect2.left == 0 && rect2.top == 0 && rect2.right == 0 && rect2.bottom == 0) {
            return super.getPadding(rect);
        }
        rect.set(this.mPadding);
        return true;
    }

    /* access modifiers changed from: protected */
    public int getColorForLevel(int i) {
        int i2 = 0;
        int i3 = 0;
        while (true) {
            int[] iArr = this.mColors;
            if (i2 >= iArr.length) {
                return i3;
            }
            int i4 = iArr[i2];
            int i5 = iArr[i2 + 1];
            if (i <= i4) {
                return i2 == iArr.length + -2 ? this.mIconTint : i5;
            }
            i2 += 2;
            i3 = i5;
        }
    }

    /* access modifiers changed from: protected */
    public int batteryColorForLevel(int i) {
        if (this.mCharging || this.mPowerSaveEnabled) {
            return this.mChargeColor;
        }
        return getColorForLevel(i);
    }

    public void draw(Canvas canvas) {
        float f;
        float f2;
        float f3;
        float[] fArr;
        int i = this.mLevel;
        Rect bounds = getBounds();
        if (i != -1) {
            float f4 = ((float) i) / 100.0f;
            int i2 = this.mHeight;
            int i3 = this.mWidth;
            float f5 = (float) i2;
            int round = Math.round(this.mButtonHeightFraction * f5);
            Rect rect = this.mPadding;
            int i4 = rect.left + bounds.left;
            int i5 = (bounds.bottom - rect.bottom) - i2;
            float f6 = (float) i4;
            float f7 = (float) i5;
            this.mFrame.set(f6, f7, (float) (i4 + i3), (float) (i2 + i5));
            this.mFrame.offset((float) ((i3 - i3) / 2), 0.0f);
            RectF rectF = this.mButtonFrame;
            float f8 = ((float) i3) * 0.28f;
            float round2 = this.mFrame.left + ((float) Math.round(f8));
            RectF rectF2 = this.mFrame;
            float f9 = (float) round;
            rectF.set(round2, rectF2.top, rectF2.right - ((float) Math.round(f8)), this.mFrame.top + f9);
            this.mFrame.top += f9;
            this.mBatteryPaint.setColor(batteryColorForLevel(i));
            if (i >= 96) {
                f4 = 1.0f;
            } else if (i <= this.mCriticalLevel) {
                f4 = 0.0f;
            }
            if (f4 == 1.0f) {
                f = this.mButtonFrame.top;
            } else {
                RectF rectF3 = this.mFrame;
                f = (rectF3.height() * (1.0f - f4)) + rectF3.top;
            }
            this.mShapePath.reset();
            this.mOutlinePath.reset();
            float radiusRatio = getRadiusRatio() * (this.mFrame.height() + f9);
            this.mShapePath.setFillType(Path.FillType.WINDING);
            this.mShapePath.addRoundRect(this.mFrame, radiusRatio, radiusRatio, Path.Direction.CW);
            this.mShapePath.addRect(this.mButtonFrame, Path.Direction.CW);
            this.mOutlinePath.addRoundRect(this.mFrame, radiusRatio, radiusRatio, Path.Direction.CW);
            Path path = new Path();
            path.addRect(this.mButtonFrame, Path.Direction.CW);
            this.mOutlinePath.op(path, Path.Op.XOR);
            boolean z = true;
            boolean z2 = false;
            if (this.mPowerSaveEnabled) {
                float width = (this.mFrame.width() * 2.0f) / 3.0f;
                RectF rectF4 = this.mFrame;
                float width2 = rectF4.left + ((rectF4.width() - width) / 2.0f);
                RectF rectF5 = this.mFrame;
                float height = rectF5.top + ((rectF5.height() - width) / 2.0f);
                RectF rectF6 = this.mFrame;
                float width3 = rectF6.right - ((rectF6.width() - width) / 2.0f);
                RectF rectF7 = this.mFrame;
                float height2 = rectF7.bottom - ((rectF7.height() - width) / 2.0f);
                RectF rectF8 = this.mPlusFrame;
                if (!(rectF8.left == width2 && rectF8.top == height && rectF8.right == width3 && rectF8.bottom == height2)) {
                    this.mPlusFrame.set(width2, height, width3, height2);
                    this.mPlusPath.reset();
                    Path path2 = this.mPlusPath;
                    RectF rectF9 = this.mPlusFrame;
                    RectF rectF10 = this.mPlusFrame;
                    path2.moveTo(rectF9.left + (this.mPlusPoints[0] * rectF9.width()), rectF10.top + (this.mPlusPoints[1] * rectF10.height()));
                    int i6 = 2;
                    while (true) {
                        fArr = this.mPlusPoints;
                        if (i6 >= fArr.length) {
                            break;
                        }
                        Path path3 = this.mPlusPath;
                        RectF rectF11 = this.mPlusFrame;
                        float width4 = rectF11.left + (fArr[i6] * rectF11.width());
                        RectF rectF12 = this.mPlusFrame;
                        path3.lineTo(width4, rectF12.top + (this.mPlusPoints[i6 + 1] * rectF12.height()));
                        i6 += 2;
                    }
                    Path path4 = this.mPlusPath;
                    RectF rectF13 = this.mPlusFrame;
                    float width5 = rectF13.left + (fArr[0] * rectF13.width());
                    RectF rectF14 = this.mPlusFrame;
                    path4.lineTo(width5, rectF14.top + (this.mPlusPoints[1] * rectF14.height()));
                }
                this.mShapePath.op(this.mPlusPath, Path.Op.DIFFERENCE);
                if (this.mPowerSaveAsColorError) {
                    canvas.drawPath(this.mPlusPath, this.mPlusPaint);
                }
            }
            String str = null;
            if (this.mCharging || this.mPowerSaveEnabled || i <= this.mCriticalLevel || !this.mShowPercent) {
                f3 = 0.0f;
                f2 = 0.0f;
            } else {
                this.mTextPaint.setColor(getColorForLevel(i));
                this.mTextPaint.setTextSize(f5 * (this.mLevel == 100 ? 0.38f : 0.5f));
                this.mTextHeight = -this.mTextPaint.getFontMetrics().ascent;
                str = String.valueOf(i);
                f3 = (((float) this.mWidth) * 0.5f) + f6;
                f2 = ((((float) this.mHeight) + this.mTextHeight) * 0.47f) + f7;
                if (f <= f2) {
                    z = false;
                }
                if (!z) {
                    this.mTextPath.reset();
                    this.mTextPaint.getTextPath(str, 0, str.length(), f3, f2, this.mTextPath);
                    this.mShapePath.op(this.mTextPath, Path.Op.DIFFERENCE);
                }
                z2 = z;
            }
            canvas.drawPath(this.mShapePath, this.mFramePaint);
            this.mFrame.top = f;
            canvas.save();
            canvas.clipRect(this.mFrame);
            canvas.drawPath(this.mShapePath, this.mBatteryPaint);
            canvas.restore();
            if (!this.mCharging && !this.mPowerSaveEnabled) {
                if (i <= this.mCriticalLevel) {
                    canvas.drawText(this.mWarningString, (((float) this.mWidth) * 0.5f) + f6, ((((float) this.mHeight) + this.mWarningTextHeight) * 0.48f) + f7, this.mWarningTextPaint);
                } else if (z2) {
                    canvas.drawText(str, f3, f2, this.mTextPaint);
                }
            }
            if (!this.mCharging && this.mPowerSaveEnabled && this.mPowerSaveAsColorError) {
                canvas.drawPath(this.mOutlinePath, this.mPowersavePaint);
            }
        }
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.mFramePaint.setColorFilter(colorFilter);
        this.mBatteryPaint.setColorFilter(colorFilter);
        this.mWarningTextPaint.setColorFilter(colorFilter);
        this.mBoltPaint.setColorFilter(colorFilter);
        this.mPlusPaint.setColorFilter(colorFilter);
    }

    public int getCriticalLevel() {
        return this.mCriticalLevel;
    }
}
