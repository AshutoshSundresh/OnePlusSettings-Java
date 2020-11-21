package com.google.android.material.picker;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.customview.widget.ExploreByTouchHelper;
import com.google.android.material.R$attr;
import com.google.android.material.R$color;
import com.google.android.material.R$dimen;
import com.google.android.material.R$styleable;
import com.google.android.material.internal.ViewUtils;
import com.google.android.material.math.MathUtils;
import com.oneplus.common.OPFeaturesUtils;
import com.oneplus.common.VibratorSceneUtils;
import java.lang.reflect.Array;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RadialTimePickerView extends View {
    private static final float[] COS_30 = new float[12];
    private static final int[] HOURS_NUMBERS = {12, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
    private static final int[] HOURS_NUMBERS_24 = {0, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23};
    private static final int[] MINUTES_NUMBERS = {0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55};
    private static final float[] SIN_30 = new float[12];
    private static final int[] SNAP_PREFER_30S_MAP = new int[361];
    private int mAmOrPm;
    private int mCenterDotRadius;
    boolean mChangedDuringTouch;
    private int mCircleRadius;
    private float mDisabledAlpha;
    private int mHalfwayDist;
    private final String[] mHours12Texts;
    private float mHoursToMinutes;
    private ObjectAnimator mHoursToMinutesAnimator;
    private final String[] mInnerHours24Texts;
    private String[] mInnerTextHours;
    private final float[] mInnerTextX;
    private final float[] mInnerTextY;
    private boolean mInputEnabled;
    private boolean mIs24HourMode;
    private boolean mIsOnInnerCircle;
    private OnValueSelectedListener mListener;
    private int mMaxDistForOuterNumber;
    private int mMinDistForInnerNumber;
    private String[] mMinutesText;
    private final String[] mMinutesTexts;
    private final String[] mOuterHours24Texts;
    private String[] mOuterTextHours;
    private final float[][] mOuterTextX;
    private final float[][] mOuterTextY;
    private final Paint[] mPaint;
    private final Paint mPaintBackground;
    private final Paint mPaintCenter;
    private final Paint[] mPaintSelector;
    private final int[] mSelectionDegrees;
    private int mSelectorColor;
    private int mSelectorDotColor;
    private int mSelectorDotRadius;
    private final Path mSelectorPath;
    private int mSelectorRadius;
    private int mSelectorStroke;
    private boolean mShowHours;
    private final ColorStateList[] mTextColor;
    private final int[] mTextInset;
    private final int[] mTextSize;
    private final RadialPickerTouchHelper mTouchHelper;
    private final Typeface mTypeface;
    private long[] mVibratePattern;
    private Vibrator mVibrator;
    private int mXCenter;
    private int mYCenter;
    RectF oval;

    /* access modifiers changed from: package-private */
    public interface OnValueSelectedListener {
        void onValueSelected(int i, int i2, boolean z);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private int getDegreesForMinute(int i) {
        return i * 6;
    }

    static {
        preparePrefer30sMap();
        double d = 1.5707963267948966d;
        for (int i = 0; i < 12; i++) {
            COS_30[i] = (float) Math.cos(d);
            SIN_30[i] = (float) Math.sin(d);
            d += 0.5235987755982988d;
        }
    }

    private static void preparePrefer30sMap() {
        int i = 1;
        int i2 = 8;
        int i3 = 0;
        for (int i4 = 0; i4 < 361; i4++) {
            SNAP_PREFER_30S_MAP[i4] = i3;
            if (i == i2) {
                i3 += 6;
                if (i3 == 360) {
                    i2 = 7;
                } else {
                    i2 = i3 % 30 == 0 ? 14 : 4;
                }
                i = 1;
            } else {
                i++;
            }
        }
    }

    private static int snapPrefer30s(int i) {
        int[] iArr = SNAP_PREFER_30S_MAP;
        if (iArr == null) {
            return -1;
        }
        return iArr[i];
    }

    /* access modifiers changed from: private */
    public static int snapOnly30s(int i, int i2) {
        int i3 = (i / 30) * 30;
        int i4 = i3 + 30;
        if (i2 != 1) {
            if (i2 == -1) {
                return i == i3 ? i3 - 30 : i3;
            }
            if (i - i3 < i4 - i) {
                return i3;
            }
        }
        return i4;
    }

    public RadialTimePickerView(Context context) {
        this(context, null);
    }

    public RadialTimePickerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.timePickerStyle);
    }

    public RadialTimePickerView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public RadialTimePickerView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet);
        this.mHours12Texts = new String[12];
        this.mOuterHours24Texts = new String[12];
        this.mInnerHours24Texts = new String[12];
        this.mMinutesTexts = new String[12];
        this.mPaint = new Paint[2];
        this.mPaintCenter = new Paint();
        this.mPaintSelector = new Paint[3];
        this.mPaintBackground = new Paint();
        this.mTextColor = new ColorStateList[3];
        this.mTextSize = new int[3];
        this.mTextInset = new int[3];
        this.mOuterTextX = (float[][]) Array.newInstance(float.class, 2, 12);
        this.mOuterTextY = (float[][]) Array.newInstance(float.class, 2, 12);
        this.mInnerTextX = new float[12];
        this.mInnerTextY = new float[12];
        this.mSelectionDegrees = new int[2];
        this.mSelectorPath = new Path();
        this.mInputEnabled = true;
        this.oval = new RectF();
        this.mChangedDuringTouch = false;
        applyAttributes(attributeSet, i, i2);
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(16842803, typedValue, true);
        this.mDisabledAlpha = typedValue.getFloat();
        this.mTypeface = Typeface.create("sans-serif", 0);
        this.mPaint[0] = new Paint();
        this.mPaint[0].setAntiAlias(true);
        this.mPaint[0].setTextAlign(Paint.Align.CENTER);
        this.mPaint[1] = new Paint();
        this.mPaint[1].setAntiAlias(true);
        this.mPaint[1].setTextAlign(Paint.Align.CENTER);
        this.mPaintCenter.setAntiAlias(true);
        this.mPaintSelector[0] = new Paint();
        this.mPaintSelector[0].setAntiAlias(true);
        this.mPaintSelector[1] = new Paint();
        this.mPaintSelector[1].setAntiAlias(true);
        this.mPaintSelector[2] = new Paint();
        this.mPaintSelector[2].setAntiAlias(true);
        this.mPaintSelector[2].setStrokeWidth(2.0f);
        this.mPaintBackground.setAntiAlias(true);
        Resources resources = getResources();
        this.mSelectorRadius = resources.getDimensionPixelSize(R$dimen.timepicker_selector_radius);
        this.mSelectorStroke = resources.getDimensionPixelSize(R$dimen.timepicker_selector_stroke);
        this.mSelectorDotRadius = resources.getDimensionPixelSize(R$dimen.timepicker_selector_dot_radius);
        this.mCenterDotRadius = resources.getDimensionPixelSize(R$dimen.timepicker_center_dot_radius);
        this.mTextSize[0] = resources.getDimensionPixelSize(R$dimen.timepicker_text_size_normal);
        this.mTextSize[1] = resources.getDimensionPixelSize(R$dimen.timepicker_text_size_normal);
        this.mTextSize[2] = resources.getDimensionPixelSize(R$dimen.timepicker_text_size_inner);
        this.mTextInset[0] = resources.getDimensionPixelSize(R$dimen.timepicker_text_inset_normal);
        this.mTextInset[1] = resources.getDimensionPixelSize(R$dimen.timepicker_text_inset_normal);
        this.mTextInset[2] = resources.getDimensionPixelSize(R$dimen.timepicker_text_inset_inner);
        this.mShowHours = true;
        this.mHoursToMinutes = 0.0f;
        this.mIs24HourMode = false;
        this.mAmOrPm = 0;
        RadialPickerTouchHelper radialPickerTouchHelper = new RadialPickerTouchHelper();
        this.mTouchHelper = radialPickerTouchHelper;
        ViewCompat.setAccessibilityDelegate(this, radialPickerTouchHelper);
        if (getImportantForAccessibility() == 0) {
            setImportantForAccessibility(1);
        }
        initHoursAndMinutesText();
        initData();
        Calendar instance = Calendar.getInstance(Locale.getDefault());
        int i3 = instance.get(11);
        int i4 = instance.get(12);
        setCurrentHourInternal(i3, false, false);
        setCurrentMinuteInternal(i4, false);
        if (OPFeaturesUtils.isSupportXVibrate()) {
            this.mVibrator = (Vibrator) context.getSystemService("vibrator");
        }
        setHapticFeedbackEnabled(true);
    }

    /* access modifiers changed from: package-private */
    public void applyAttributes(AttributeSet attributeSet, int i, int i2) {
        Context context = getContext();
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R$styleable.TimePicker, i, i2);
        ColorStateList colorStateList = obtainStyledAttributes.getColorStateList(R$styleable.TimePicker_numbersTextColor);
        ColorStateList colorStateList2 = obtainStyledAttributes.getColorStateList(R$styleable.TimePicker_numbersInnerTextColor);
        ColorStateList[] colorStateListArr = this.mTextColor;
        int i3 = -65281;
        if (colorStateList == null) {
            colorStateList = ColorStateList.valueOf(-65281);
        }
        colorStateListArr[0] = colorStateList;
        ColorStateList[] colorStateListArr2 = this.mTextColor;
        if (colorStateList2 == null) {
            colorStateList2 = ColorStateList.valueOf(-65281);
        }
        colorStateListArr2[2] = colorStateList2;
        ColorStateList[] colorStateListArr3 = this.mTextColor;
        colorStateListArr3[1] = colorStateListArr3[0];
        ColorStateList colorStateList3 = obtainStyledAttributes.getColorStateList(R$styleable.TimePicker_android_numbersSelectorColor);
        if (colorStateList3 != null) {
            i3 = colorStateList3.getColorForState(ViewUtils.getViewState(40), 0);
        }
        this.mPaintCenter.setColor(i3);
        int[] viewState = ViewUtils.getViewState(40);
        this.mSelectorColor = i3;
        this.mSelectorDotColor = this.mTextColor[0].getColorForState(viewState, 0);
        this.mPaintBackground.setColor(obtainStyledAttributes.getColor(R$styleable.TimePicker_android_numbersBackgroundColor, context.getResources().getColor(R$color.timepicker_default_numbers_background_color_material)));
        obtainStyledAttributes.recycle();
    }

    public void initialize(int i, int i2, boolean z) {
        if (this.mIs24HourMode != z) {
            this.mIs24HourMode = z;
            initData();
        }
        setCurrentHourInternal(i, false, false);
        setCurrentMinuteInternal(i2, false);
    }

    public void setCurrentItemShowing(int i, boolean z) {
        if (i == 0) {
            showHours(z);
        } else if (i != 1) {
            Log.e("RadialTimePickerView", "ClockView does not support showing item " + i);
        } else {
            showMinutes(z);
        }
    }

    public int getCurrentItemShowing() {
        return !this.mShowHours ? 1 : 0;
    }

    public void setOnValueSelectedListener(OnValueSelectedListener onValueSelectedListener) {
        this.mListener = onValueSelectedListener;
    }

    public void setCurrentHour(int i) {
        setCurrentHourInternal(i, true, false);
    }

    private void setCurrentHourInternal(int i, boolean z, boolean z2) {
        OnValueSelectedListener onValueSelectedListener;
        this.mSelectionDegrees[0] = (i % 12) * 30;
        int i2 = (i == 0 || i % 24 < 12) ? 0 : 1;
        boolean innerCircleForHour = getInnerCircleForHour(i);
        if (!(this.mAmOrPm == i2 && this.mIsOnInnerCircle == innerCircleForHour)) {
            this.mAmOrPm = i2;
            this.mIsOnInnerCircle = innerCircleForHour;
            initData();
            this.mTouchHelper.invalidateRoot();
        }
        invalidate();
        if (z && (onValueSelectedListener = this.mListener) != null) {
            onValueSelectedListener.onValueSelected(0, i, z2);
        }
    }

    public int getCurrentHour() {
        return getHourForDegrees(this.mSelectionDegrees[0], this.mIsOnInnerCircle);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    /* JADX WARNING: Code restructure failed: missing block: B:5:0x0010, code lost:
        if (r3 != 0) goto L_0x0018;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:7:0x0016, code lost:
        if (r2.mAmOrPm == 1) goto L_0x0018;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int getHourForDegrees(int r3, boolean r4) {
        /*
            r2 = this;
            int r3 = r3 / 30
            r0 = 12
            int r3 = r3 % r0
            boolean r1 = r2.mIs24HourMode
            if (r1 == 0) goto L_0x0013
            if (r4 != 0) goto L_0x000e
            if (r3 != 0) goto L_0x000e
            goto L_0x001c
        L_0x000e:
            if (r4 == 0) goto L_0x001b
            if (r3 == 0) goto L_0x001b
            goto L_0x0018
        L_0x0013:
            int r2 = r2.mAmOrPm
            r4 = 1
            if (r2 != r4) goto L_0x001b
        L_0x0018:
            int r0 = r3 + 12
            goto L_0x001c
        L_0x001b:
            r0 = r3
        L_0x001c:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.material.picker.RadialTimePickerView.getHourForDegrees(int, boolean):int");
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private int getDegreesForHour(int i) {
        if (this.mIs24HourMode) {
            if (i >= 12) {
                i -= 12;
            }
        } else if (i == 12) {
            i = 0;
        }
        return i * 30;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private boolean getInnerCircleForHour(int i) {
        return this.mIs24HourMode && (i == 0 || i > 12);
    }

    public void setCurrentMinute(int i) {
        setCurrentMinuteInternal(i, true);
    }

    private void setCurrentMinuteInternal(int i, boolean z) {
        OnValueSelectedListener onValueSelectedListener;
        this.mSelectionDegrees[1] = (i % 60) * 6;
        invalidate();
        if (z && (onValueSelectedListener = this.mListener) != null) {
            onValueSelectedListener.onValueSelected(1, i, false);
        }
    }

    public int getCurrentMinute() {
        return getMinuteForDegrees(this.mSelectionDegrees[1]);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private int getMinuteForDegrees(int i) {
        return i / 6;
    }

    public boolean setAmOrPm(int i) {
        if (this.mAmOrPm == i || this.mIs24HourMode) {
            return false;
        }
        this.mAmOrPm = i;
        invalidate();
        this.mTouchHelper.invalidateRoot();
        return true;
    }

    public int getAmOrPm() {
        return this.mAmOrPm;
    }

    public void showHours(boolean z) {
        showPicker(true, z);
    }

    public void showMinutes(boolean z) {
        showPicker(false, z);
    }

    private void initHoursAndMinutesText() {
        int[] iArr = HOURS_NUMBERS;
        for (int i = 0; i < 12; i++) {
            this.mHours12Texts[i] = String.format("%d", Integer.valueOf(iArr[i]));
            this.mInnerHours24Texts[i] = String.format("%02d", Integer.valueOf(HOURS_NUMBERS_24[i]));
            this.mOuterHours24Texts[i] = String.format("%d", Integer.valueOf(iArr[i]));
            this.mMinutesTexts[i] = String.format("%02d", Integer.valueOf(MINUTES_NUMBERS[i]));
        }
    }

    private void initData() {
        if (this.mIs24HourMode) {
            this.mOuterTextHours = this.mOuterHours24Texts;
            this.mInnerTextHours = this.mInnerHours24Texts;
        } else {
            String[] strArr = this.mHours12Texts;
            this.mOuterTextHours = strArr;
            this.mInnerTextHours = strArr;
        }
        this.mMinutesText = this.mMinutesTexts;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (z) {
            this.mXCenter = getWidth() / 2;
            this.mYCenter = getHeight() / 2;
            int min = Math.min(this.mXCenter - Math.max(getPaddingLeft(), getPaddingRight()), this.mYCenter - Math.max(getPaddingTop(), getPaddingBottom()));
            this.mCircleRadius = min;
            int[] iArr = this.mTextInset;
            int i5 = this.mSelectorRadius;
            this.mMinDistForInnerNumber = (min - iArr[2]) - i5;
            this.mMaxDistForOuterNumber = (min - iArr[0]) + i5;
            this.mHalfwayDist = min - ((iArr[0] + iArr[2]) / 2);
            calculatePositionsHours();
            calculatePositionsMinutes();
            this.mTouchHelper.invalidateRoot();
        }
    }

    public void onDraw(Canvas canvas) {
        float f = this.mInputEnabled ? 1.0f : this.mDisabledAlpha;
        drawCircleBackground(canvas);
        Path path = this.mSelectorPath;
        drawSelector(canvas, path);
        drawHours(canvas, path, f);
        drawMinutes(canvas, path, f);
        drawCenter(canvas, f);
    }

    private void showPicker(boolean z, boolean z2) {
        if (this.mShowHours != z) {
            this.mShowHours = z;
            if (z2) {
                animatePicker(z, 500);
            } else {
                ObjectAnimator objectAnimator = this.mHoursToMinutesAnimator;
                if (objectAnimator != null && objectAnimator.isStarted()) {
                    this.mHoursToMinutesAnimator.cancel();
                    this.mHoursToMinutesAnimator = null;
                }
                this.mHoursToMinutes = z ? 0.0f : 1.0f;
            }
            initData();
            invalidate();
            this.mTouchHelper.invalidateRoot();
        }
    }

    private void animatePicker(boolean z, long j) {
        float f = z ? 0.0f : 1.0f;
        float f2 = this.mHoursToMinutes;
        if (f2 == f) {
            ObjectAnimator objectAnimator = this.mHoursToMinutesAnimator;
            if (objectAnimator != null && objectAnimator.isStarted()) {
                this.mHoursToMinutesAnimator.cancel();
                this.mHoursToMinutesAnimator = null;
                return;
            }
            return;
        }
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "HoursToMinutes", f2, f);
        this.mHoursToMinutesAnimator = ofFloat;
        ofFloat.setAutoCancel(true);
        this.mHoursToMinutesAnimator.setDuration(j);
        this.mHoursToMinutesAnimator.start();
    }

    public void setHoursToMinutes(float f) {
        this.mHoursToMinutes = f;
        invalidate();
    }

    public float getHoursToMinutes() {
        return this.mHoursToMinutes;
    }

    private void drawCircleBackground(Canvas canvas) {
        canvas.drawCircle((float) this.mXCenter, (float) this.mYCenter, (float) this.mCircleRadius, this.mPaintBackground);
    }

    private void drawHours(Canvas canvas, Path path, float f) {
        int i = (int) (((1.0f - this.mHoursToMinutes) * 255.0f * f) + 0.5f);
        if (i > 0) {
            canvas.save();
            canvas.clipPath(path, Region.Op.DIFFERENCE);
            drawHoursClipped(canvas, i, false);
            canvas.restore();
            canvas.save();
            canvas.clipPath(path, Region.Op.INTERSECT);
            drawHoursClipped(canvas, i, true);
            canvas.restore();
        }
    }

    private void drawHoursClipped(Canvas canvas, int i, boolean z) {
        String[] strArr;
        drawTextElements(canvas, (float) this.mTextSize[0], this.mTypeface, this.mTextColor[0], this.mOuterTextHours, this.mOuterTextX[0], this.mOuterTextY[0], this.mPaint[0], i, z && !this.mIsOnInnerCircle, this.mSelectionDegrees[0], z);
        if (this.mIs24HourMode && (strArr = this.mInnerTextHours) != null) {
            drawTextElements(canvas, (float) this.mTextSize[2], this.mTypeface, this.mTextColor[2], strArr, this.mInnerTextX, this.mInnerTextY, this.mPaint[0], i, z && this.mIsOnInnerCircle, this.mSelectionDegrees[0], z);
        }
    }

    private void drawMinutes(Canvas canvas, Path path, float f) {
        int i = (int) ((this.mHoursToMinutes * 255.0f * f) + 0.5f);
        if (i > 0) {
            canvas.save();
            canvas.clipPath(path, Region.Op.DIFFERENCE);
            drawMinutesClipped(canvas, i, false);
            canvas.restore();
            canvas.save();
            canvas.clipPath(path, Region.Op.INTERSECT);
            drawMinutesClipped(canvas, i, true);
            canvas.restore();
        }
    }

    private void drawMinutesClipped(Canvas canvas, int i, boolean z) {
        drawTextElements(canvas, (float) this.mTextSize[1], this.mTypeface, this.mTextColor[1], this.mMinutesText, this.mOuterTextX[1], this.mOuterTextY[1], this.mPaint[1], i, z, this.mSelectionDegrees[1], z);
    }

    private void drawCenter(Canvas canvas, float f) {
        this.mPaintCenter.setAlpha((int) ((f * 255.0f) + 0.5f));
        canvas.drawCircle((float) this.mXCenter, (float) this.mYCenter, (float) this.mCenterDotRadius, this.mPaintCenter);
    }

    private int getMultipliedAlpha(int i, int i2) {
        return (int) ((((double) Color.alpha(i)) * (((double) i2) / 255.0d)) + 0.5d);
    }

    private void drawSelector(Canvas canvas, Path path) {
        int i = this.mIsOnInnerCircle ? 2 : 0;
        int i2 = this.mTextInset[i];
        int[] iArr = this.mSelectionDegrees;
        int i3 = i % 2;
        int i4 = iArr[i3];
        int i5 = iArr[i3] % 30;
        float f = 1.0f;
        float f2 = i5 != 0 ? 1.0f : 0.0f;
        int i6 = this.mTextInset[1];
        int[] iArr2 = this.mSelectionDegrees;
        int i7 = iArr2[1];
        if (iArr2[1] % 30 == 0) {
            f = 0.0f;
        }
        int i8 = this.mSelectorRadius;
        float lerp = ((float) this.mCircleRadius) - MathUtils.lerp((float) i2, (float) i6, this.mHoursToMinutes);
        double radians = Math.toRadians((double) MathUtils.lerpDeg((float) i4, (float) i7, this.mHoursToMinutes));
        float sin = ((float) this.mXCenter) + (((float) Math.sin(radians)) * lerp);
        float cos = ((float) this.mYCenter) - (((float) Math.cos(radians)) * lerp);
        Paint paint = this.mPaintSelector[0];
        paint.setColor(this.mSelectorColor);
        float f3 = (float) i8;
        canvas.drawCircle(sin, cos, f3, paint);
        if (path != null) {
            path.reset();
            path.addCircle(sin, cos, f3, Path.Direction.CCW);
        }
        float lerp2 = MathUtils.lerp(f2, f, this.mHoursToMinutes);
        if (lerp2 > 0.0f) {
            Paint paint2 = this.mPaintSelector[1];
            paint2.setColor(this.mSelectorDotColor);
            canvas.drawCircle(sin, cos, ((float) this.mSelectorDotRadius) * lerp2, paint2);
        }
        double sin2 = Math.sin(radians);
        double cos2 = Math.cos(radians);
        int i9 = this.mXCenter;
        int i10 = this.mCenterDotRadius;
        int i11 = i9 + ((int) (((double) i10) * sin2));
        double d = (double) (lerp - f3);
        float f4 = (float) ((this.mYCenter - ((int) (((double) i10) * cos2))) - ((int) (d * cos2)));
        Paint paint3 = this.mPaintSelector[2];
        paint3.setColor(this.mSelectorColor);
        paint3.setStrokeWidth((float) this.mSelectorStroke);
        canvas.drawLine((float) this.mXCenter, (float) this.mYCenter, (float) (i11 + ((int) (sin2 * d))), f4, paint3);
        if (!this.mShowHours) {
            paint3.setColor(-7829368);
            RectF rectF = this.oval;
            float[][] fArr = this.mOuterTextX;
            rectF.set(fArr[1][9], fArr[1][0], fArr[1][3], fArr[1][6]);
            canvas.drawArc(this.oval, (float) (getDegreesForMinute(getCurrentMinute()) - 45), 90.0f, true, paint3);
        }
    }

    private void calculatePositionsHours() {
        calculatePositions(this.mPaint[0], (float) (this.mCircleRadius - this.mTextInset[0]), (float) this.mXCenter, (float) this.mYCenter, (float) this.mTextSize[0], this.mOuterTextX[0], this.mOuterTextY[0]);
        if (this.mIs24HourMode) {
            calculatePositions(this.mPaint[0], (float) (this.mCircleRadius - this.mTextInset[2]), (float) this.mXCenter, (float) this.mYCenter, (float) this.mTextSize[2], this.mInnerTextX, this.mInnerTextY);
        }
    }

    private void calculatePositionsMinutes() {
        calculatePositions(this.mPaint[1], (float) (this.mCircleRadius - this.mTextInset[1]), (float) this.mXCenter, (float) this.mYCenter, (float) this.mTextSize[1], this.mOuterTextX[1], this.mOuterTextY[1]);
    }

    private static void calculatePositions(Paint paint, float f, float f2, float f3, float f4, float[] fArr, float[] fArr2) {
        paint.setTextSize(f4);
        float descent = f3 - ((paint.descent() + paint.ascent()) / 2.0f);
        for (int i = 0; i < 12; i++) {
            fArr[i] = f2 - (COS_30[i] * f);
            fArr2[i] = descent - (SIN_30[i] * f);
        }
    }

    private void drawTextElements(Canvas canvas, float f, Typeface typeface, ColorStateList colorStateList, String[] strArr, float[] fArr, float[] fArr2, Paint paint, int i, boolean z, int i2, boolean z2) {
        paint.setTextSize(f);
        paint.setTypeface(typeface);
        float f2 = ((float) i2) / 30.0f;
        int i3 = (int) f2;
        int ceil = ((int) Math.ceil((double) f2)) % 12;
        int i4 = 0;
        while (i4 < 12) {
            boolean z3 = i3 == i4 || ceil == i4;
            if (!z2 || z3) {
                int colorForState = colorStateList.getColorForState(ViewUtils.getViewState(((!z || !z3) ? 0 : 32) | 8), 0);
                paint.setColor(colorForState);
                paint.setAlpha(getMultipliedAlpha(colorForState, i));
                canvas.drawText(strArr[i4], fArr[i4], fArr2[i4], paint);
            }
            i4++;
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private int getDegreesFromXY(float f, float f2, boolean z) {
        int i;
        int i2;
        if (!this.mIs24HourMode || !this.mShowHours) {
            int i3 = this.mCircleRadius - this.mTextInset[!this.mShowHours ? 1 : 0];
            int i4 = this.mSelectorRadius;
            int i5 = i3 - i4;
            i = i3 + i4;
            i2 = i5;
        } else {
            i2 = this.mMinDistForInnerNumber;
            i = this.mMaxDistForOuterNumber;
        }
        double d = (double) (f - ((float) this.mXCenter));
        double d2 = (double) (f2 - ((float) this.mYCenter));
        double sqrt = Math.sqrt((d * d) + (d2 * d2));
        if ((sqrt < ((double) i2) || z) && sqrt > ((double) i)) {
            return -1;
        }
        int degrees = (int) (Math.toDegrees(Math.atan2(d2, d) + 1.5707963267948966d) + 0.5d);
        return degrees < 0 ? degrees + 360 : degrees;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private boolean getInnerCircleFromXY(float f, float f2) {
        if (!this.mIs24HourMode || !this.mShowHours) {
            return false;
        }
        double d = (double) (f - ((float) this.mXCenter));
        double d2 = (double) (f2 - ((float) this.mYCenter));
        if (Math.sqrt((d * d) + (d2 * d2)) <= ((double) this.mHalfwayDist)) {
            return true;
        }
        return false;
    }

    private boolean isVisible() {
        return getVisibility() == 0;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean z;
        if (!isVisible()) {
            return super.onTouchEvent(motionEvent);
        }
        if (!this.mInputEnabled) {
            return true;
        }
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 2 || actionMasked == 1 || actionMasked == 0) {
            boolean z2 = false;
            if (actionMasked == 0) {
                this.mChangedDuringTouch = false;
            } else if (actionMasked == 1) {
                if (!this.mChangedDuringTouch) {
                    z = true;
                    z2 = true;
                } else {
                    z = true;
                }
                this.mChangedDuringTouch = handleTouchInput(motionEvent.getX(), motionEvent.getY(), z2, z) | this.mChangedDuringTouch;
            }
            z = false;
            this.mChangedDuringTouch = handleTouchInput(motionEvent.getX(), motionEvent.getY(), z2, z) | this.mChangedDuringTouch;
        }
        return true;
    }

    private boolean handleTouchInput(float f, float f2, boolean z, boolean z2) {
        boolean z3;
        int i;
        int i2;
        boolean innerCircleFromXY = getInnerCircleFromXY(f, f2);
        int degreesFromXY = getDegreesFromXY(f, f2, false);
        if (degreesFromXY == -1) {
            return false;
        }
        animatePicker(this.mShowHours, 60);
        if (this.mShowHours) {
            int snapOnly30s = snapOnly30s(degreesFromXY, 0) % 360;
            z3 = (this.mIsOnInnerCircle == innerCircleFromXY && this.mSelectionDegrees[0] == snapOnly30s) ? false : true;
            this.mIsOnInnerCircle = innerCircleFromXY;
            this.mSelectionDegrees[0] = snapOnly30s;
            i = getCurrentHour();
            i2 = 0;
        } else {
            int snapPrefer30s = snapPrefer30s(degreesFromXY) % 360;
            z3 = this.mSelectionDegrees[1] != snapPrefer30s;
            this.mSelectionDegrees[1] = snapPrefer30s;
            i = getCurrentMinute();
            i2 = 1;
        }
        if (!z3 && !z && !z2) {
            return false;
        }
        OnValueSelectedListener onValueSelectedListener = this.mListener;
        if (onValueSelectedListener != null) {
            onValueSelectedListener.onValueSelected(i2, i, z2);
        }
        if (z3 || z) {
            if (!OPFeaturesUtils.isSupportXVibrate()) {
                performHapticFeedback(4);
            } else if (i2 != 1) {
                performHapticFeedback(4);
            } else if (VibratorSceneUtils.systemVibrateEnabled(getContext())) {
                long[] vibratorScenePattern = VibratorSceneUtils.getVibratorScenePattern(getContext(), this.mVibrator, 1030);
                this.mVibratePattern = vibratorScenePattern;
                VibratorSceneUtils.vibrateIfNeeded(vibratorScenePattern, this.mVibrator);
            }
            invalidate();
        }
        return true;
    }

    public boolean dispatchHoverEvent(MotionEvent motionEvent) {
        if (this.mTouchHelper.dispatchHoverEvent(motionEvent)) {
            return true;
        }
        return super.dispatchHoverEvent(motionEvent);
    }

    public void setInputEnabled(boolean z) {
        this.mInputEnabled = z;
        invalidate();
    }

    public PointerIcon onResolvePointerIcon(MotionEvent motionEvent, int i) {
        if (!isEnabled()) {
            return null;
        }
        if (getDegreesFromXY(motionEvent.getX(), motionEvent.getY(), false) == -1 || Build.VERSION.SDK_INT < 24) {
            return super.onResolvePointerIcon(motionEvent, i);
        }
        return PointerIcon.getSystemIcon(getContext(), 1002);
    }

    /* access modifiers changed from: private */
    public class RadialPickerTouchHelper extends ExploreByTouchHelper {
        private final Rect mTempRect = new Rect();

        private int getTypeFromId(int i) {
            return (i >>> 0) & 15;
        }

        private int getValueFromId(int i) {
            return (i >>> 8) & 255;
        }

        private int hour12To24(int i, int i2) {
            if (i != 12) {
                return i2 == 1 ? i + 12 : i;
            }
            if (i2 == 0) {
                return 0;
            }
            return i;
        }

        private int hour24To12(int i) {
            if (i == 0) {
                return 12;
            }
            return i > 12 ? i - 12 : i;
        }

        private int makeId(int i, int i2) {
            return (i << 0) | (i2 << 8);
        }

        public RadialPickerTouchHelper() {
            super(RadialTimePickerView.this);
        }

        @Override // androidx.customview.widget.ExploreByTouchHelper, androidx.core.view.AccessibilityDelegateCompat
        public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
            onPopulateNodeForHost(accessibilityNodeInfoCompat);
            accessibilityNodeInfoCompat.addAction(4096);
            accessibilityNodeInfoCompat.addAction(8192);
        }

        @Override // androidx.core.view.AccessibilityDelegateCompat
        public boolean performAccessibilityAction(View view, int i, Bundle bundle) {
            if (super.performAccessibilityAction(view, i, bundle)) {
                return true;
            }
            if (i == 4096) {
                adjustPicker(1);
                return true;
            } else if (i != 8192) {
                return false;
            } else {
                adjustPicker(-1);
                return true;
            }
        }

        private void adjustPicker(int i) {
            int i2;
            int i3;
            int i4 = 1;
            int i5 = 0;
            if (RadialTimePickerView.this.mShowHours) {
                i3 = RadialTimePickerView.this.getCurrentHour();
                if (RadialTimePickerView.this.mIs24HourMode) {
                    i2 = 23;
                } else {
                    i3 = hour24To12(i3);
                    i2 = 12;
                    i5 = 1;
                }
            } else {
                i4 = 5;
                i3 = RadialTimePickerView.this.getCurrentMinute() / 5;
                i2 = 55;
            }
            int constrain = MathUtils.constrain((i3 + i) * i4, i5, i2);
            if (RadialTimePickerView.this.mShowHours) {
                RadialTimePickerView.this.setCurrentHour(constrain);
            } else {
                RadialTimePickerView.this.setCurrentMinute(constrain);
            }
        }

        /* access modifiers changed from: protected */
        @Override // androidx.customview.widget.ExploreByTouchHelper
        public int getVirtualViewAt(float f, float f2) {
            int degreesFromXY = RadialTimePickerView.this.getDegreesFromXY(f, f2, true);
            if (degreesFromXY == -1) {
                return Integer.MIN_VALUE;
            }
            int snapOnly30s = RadialTimePickerView.snapOnly30s(degreesFromXY, 0) % 360;
            if (RadialTimePickerView.this.mShowHours) {
                int hourForDegrees = RadialTimePickerView.this.getHourForDegrees(snapOnly30s, RadialTimePickerView.this.getInnerCircleFromXY(f, f2));
                if (!RadialTimePickerView.this.mIs24HourMode) {
                    hourForDegrees = hour24To12(hourForDegrees);
                }
                return makeId(1, hourForDegrees);
            }
            int currentMinute = RadialTimePickerView.this.getCurrentMinute();
            int minuteForDegrees = RadialTimePickerView.this.getMinuteForDegrees(degreesFromXY);
            int minuteForDegrees2 = RadialTimePickerView.this.getMinuteForDegrees(snapOnly30s);
            if (getCircularDiff(currentMinute, minuteForDegrees, 60) >= getCircularDiff(minuteForDegrees2, minuteForDegrees, 60)) {
                currentMinute = minuteForDegrees2;
            }
            return makeId(2, currentMinute);
        }

        /* access modifiers changed from: protected */
        @Override // androidx.customview.widget.ExploreByTouchHelper
        public void getVisibleVirtualViews(List<Integer> list) {
            if (RadialTimePickerView.this.mShowHours) {
                int i = RadialTimePickerView.this.mIs24HourMode ? 23 : 12;
                for (int i2 = !RadialTimePickerView.this.mIs24HourMode ? 1 : 0; i2 <= i; i2++) {
                    list.add(Integer.valueOf(makeId(1, i2)));
                }
                return;
            }
            int currentMinute = RadialTimePickerView.this.getCurrentMinute();
            for (int i3 = 0; i3 < 60; i3 += 5) {
                list.add(Integer.valueOf(makeId(2, i3)));
                if (currentMinute > i3 && currentMinute < i3 + 5) {
                    list.add(Integer.valueOf(makeId(2, currentMinute)));
                }
            }
        }

        private int getCircularDiff(int i, int i2, int i3) {
            int abs = Math.abs(i - i2);
            return abs > i3 / 2 ? i3 - abs : abs;
        }

        /* access modifiers changed from: protected */
        @Override // androidx.customview.widget.ExploreByTouchHelper
        public void onPopulateEventForVirtualView(int i, AccessibilityEvent accessibilityEvent) {
            accessibilityEvent.setClassName(RadialPickerTouchHelper.class.getName());
            accessibilityEvent.setContentDescription(getVirtualViewDescription(getTypeFromId(i), getValueFromId(i)));
        }

        /* access modifiers changed from: protected */
        @Override // androidx.customview.widget.ExploreByTouchHelper
        public void onPopulateNodeForVirtualView(int i, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            accessibilityNodeInfoCompat.setClassName(RadialPickerTouchHelper.class.getName());
            accessibilityNodeInfoCompat.addAction(16);
            int typeFromId = getTypeFromId(i);
            int valueFromId = getValueFromId(i);
            accessibilityNodeInfoCompat.setContentDescription(getVirtualViewDescription(typeFromId, valueFromId));
            getBoundsForVirtualView(i, this.mTempRect);
            accessibilityNodeInfoCompat.setBoundsInParent(this.mTempRect);
            accessibilityNodeInfoCompat.setSelected(isVirtualViewSelected(typeFromId, valueFromId));
            int virtualViewIdAfter = getVirtualViewIdAfter(typeFromId, valueFromId);
            if (virtualViewIdAfter != Integer.MIN_VALUE && Build.VERSION.SDK_INT >= 22) {
                accessibilityNodeInfoCompat.setTraversalBefore(RadialTimePickerView.this, virtualViewIdAfter);
            }
        }

        private int getVirtualViewIdAfter(int i, int i2) {
            if (i == 1) {
                int i3 = i2 + 1;
                if (i3 <= (RadialTimePickerView.this.mIs24HourMode ? 23 : 12)) {
                    return makeId(i, i3);
                }
                return Integer.MIN_VALUE;
            } else if (i != 2) {
                return Integer.MIN_VALUE;
            } else {
                int currentMinute = RadialTimePickerView.this.getCurrentMinute();
                int i4 = (i2 - (i2 % 5)) + 5;
                if (i2 < currentMinute && i4 > currentMinute) {
                    return makeId(i, currentMinute);
                }
                if (i4 < 60) {
                    return makeId(i, i4);
                }
                return Integer.MIN_VALUE;
            }
        }

        /* access modifiers changed from: protected */
        @Override // androidx.customview.widget.ExploreByTouchHelper
        public boolean onPerformActionForVirtualView(int i, int i2, Bundle bundle) {
            if (i2 != 16) {
                return false;
            }
            int typeFromId = getTypeFromId(i);
            int valueFromId = getValueFromId(i);
            if (typeFromId == 1) {
                if (!RadialTimePickerView.this.mIs24HourMode) {
                    valueFromId = hour12To24(valueFromId, RadialTimePickerView.this.mAmOrPm);
                }
                RadialTimePickerView.this.setCurrentHour(valueFromId);
                return true;
            } else if (typeFromId != 2) {
                return false;
            } else {
                RadialTimePickerView.this.setCurrentMinute(valueFromId);
                return true;
            }
        }

        private void getBoundsForVirtualView(int i, Rect rect) {
            float f;
            float f2;
            int i2;
            int typeFromId = getTypeFromId(i);
            int valueFromId = getValueFromId(i);
            float f3 = 0.0f;
            if (typeFromId == 1) {
                if (RadialTimePickerView.this.getInnerCircleForHour(valueFromId)) {
                    f2 = (float) (RadialTimePickerView.this.mCircleRadius - RadialTimePickerView.this.mTextInset[2]);
                    i2 = RadialTimePickerView.this.mSelectorRadius;
                } else {
                    f2 = (float) (RadialTimePickerView.this.mCircleRadius - RadialTimePickerView.this.mTextInset[0]);
                    i2 = RadialTimePickerView.this.mSelectorRadius;
                }
                f3 = (float) RadialTimePickerView.this.getDegreesForHour(valueFromId);
                f = (float) i2;
            } else if (typeFromId == 2) {
                float f4 = (float) (RadialTimePickerView.this.mCircleRadius - RadialTimePickerView.this.mTextInset[1]);
                f3 = (float) RadialTimePickerView.this.getDegreesForMinute(valueFromId);
                f = (float) RadialTimePickerView.this.mSelectorRadius;
                f2 = f4;
            } else {
                f = 0.0f;
                f2 = 0.0f;
            }
            double radians = Math.toRadians((double) f3);
            float sin = ((float) RadialTimePickerView.this.mXCenter) + (((float) Math.sin(radians)) * f2);
            float cos = ((float) RadialTimePickerView.this.mYCenter) - (f2 * ((float) Math.cos(radians)));
            rect.set((int) (sin - f), (int) (cos - f), (int) (sin + f), (int) (cos + f));
        }

        private CharSequence getVirtualViewDescription(int i, int i2) {
            if (i == 1 || i == 2) {
                return Integer.toString(i2);
            }
            return null;
        }

        private boolean isVirtualViewSelected(int i, int i2) {
            if (i == 1) {
                if (RadialTimePickerView.this.getCurrentHour() != i2) {
                    return false;
                }
            } else if (!(i == 2 && RadialTimePickerView.this.getCurrentMinute() == i2)) {
                return false;
            }
            return true;
        }
    }
}
