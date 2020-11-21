package com.google.android.material.slider;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.google.android.material.R$attr;
import com.google.android.material.R$dimen;
import com.google.android.material.R$drawable;
import com.google.android.material.R$style;
import com.google.android.material.R$styleable;
import com.google.android.material.internal.ThemeEnforcement;
import com.google.android.material.resources.MaterialResources;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.android.material.theme.overlay.MaterialThemeOverlay;

public class Slider extends View {
    private static final int DEF_STYLE_RES = R$style.Widget_MaterialComponents_Slider;
    private static final String TAG = Slider.class.getSimpleName();
    private ColorStateList activeTrackColor;
    private final Paint activeTrackPaint;
    private LabelFormatter formatter;
    private final Paint haloPaint;
    private int haloRadius;
    private ColorStateList inactiveTrackColor;
    private final Paint inactiveTrackPaint;
    private final Drawable label;
    private int labelHeight;
    private String labelText;
    private final Rect labelTextBounds;
    private final Paint labelTextPaint;
    private float labelTextSize;
    private int labelTextTopOffset;
    private int labelTopOffset;
    private int labelWidth;
    private int lineHeight;
    private OnChangeListener listener;
    private float stepSize;
    private ColorStateList textColor;
    private ColorStateList thumbColor;
    private final MaterialShapeDrawable thumbDrawable;
    private boolean thumbIsPressed;
    private final Paint thumbPaint;
    private float thumbPosition;
    private int thumbRadius;
    private ColorStateList tickColor;
    private float[] ticksCoordinates;
    private final Paint ticksPaint;
    private int trackSidePadding;
    private int trackTop;
    private int trackTopDiscrete;
    private int trackWidth;
    private float valueFrom;
    private float valueTo;
    private int widgetHeight;
    private int widgetHeightDiscrete;

    public interface LabelFormatter {
        String getFormattedValue(float f);
    }

    public interface OnChangeListener {
        void onValueChange(Slider slider, float f);
    }

    public void setOnFocusChangeListener(View.OnFocusChangeListener onFocusChangeListener) {
    }

    public Slider(Context context) {
        this(context, null);
    }

    public Slider(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.sliderStyle);
    }

    public Slider(Context context, AttributeSet attributeSet, int i) {
        super(MaterialThemeOverlay.wrap(context, attributeSet, i, DEF_STYLE_RES), attributeSet, i);
        this.labelText = "";
        this.thumbIsPressed = false;
        this.thumbPosition = 0.0f;
        this.stepSize = 0.0f;
        this.thumbDrawable = new MaterialShapeDrawable();
        Context context2 = getContext();
        loadResources(context2.getResources());
        processAttributes(context2, attributeSet, i);
        Paint paint = new Paint();
        this.inactiveTrackPaint = paint;
        paint.setStyle(Paint.Style.STROKE);
        this.inactiveTrackPaint.setStrokeWidth((float) this.lineHeight);
        Paint paint2 = new Paint();
        this.activeTrackPaint = paint2;
        paint2.setStyle(Paint.Style.STROKE);
        this.activeTrackPaint.setStrokeWidth((float) this.lineHeight);
        Paint paint3 = new Paint(1);
        this.thumbPaint = paint3;
        paint3.setStyle(Paint.Style.FILL);
        this.thumbPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        Paint paint4 = new Paint(1);
        this.haloPaint = paint4;
        paint4.setStyle(Paint.Style.FILL);
        Paint paint5 = new Paint();
        this.ticksPaint = paint5;
        paint5.setStyle(Paint.Style.STROKE);
        this.ticksPaint.setStrokeWidth((float) this.lineHeight);
        Drawable drawable = context2.getResources().getDrawable(R$drawable.mtrl_slider_label);
        this.label = drawable;
        drawable.setColorFilter(new PorterDuffColorFilter(getColorForState(this.thumbColor), PorterDuff.Mode.MULTIPLY));
        Paint paint6 = new Paint();
        this.labelTextPaint = paint6;
        paint6.setTextSize(this.labelTextSize);
        this.labelTextBounds = new Rect();
        super.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            /* class com.google.android.material.slider.Slider.AnonymousClass1 */

            public void onFocusChange(View view, boolean z) {
                Slider.this.invalidate();
            }
        });
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.thumbDrawable.setShadowCompatibilityMode(2);
    }

    public void setEnabled(boolean z) {
        super.setEnabled(z);
        setLayerType(z ? 0 : 2, null);
    }

    private void loadResources(Resources resources) {
        this.widgetHeight = resources.getDimensionPixelSize(R$dimen.mtrl_slider_widget_height);
        this.widgetHeightDiscrete = resources.getDimensionPixelSize(R$dimen.mtrl_slider_widget_height_discrete);
        this.lineHeight = resources.getDimensionPixelSize(R$dimen.mtrl_slider_line_height);
        this.trackSidePadding = resources.getDimensionPixelOffset(R$dimen.mtrl_slider_track_side_padding);
        this.trackTop = resources.getDimensionPixelOffset(R$dimen.mtrl_slider_track_top);
        this.trackTopDiscrete = resources.getDimensionPixelOffset(R$dimen.mtrl_slider_track_top_discrete);
        this.labelWidth = resources.getDimensionPixelSize(R$dimen.mtrl_slider_label_width);
        this.labelHeight = resources.getDimensionPixelSize(R$dimen.mtrl_slider_label_height);
        this.labelTopOffset = resources.getDimensionPixelSize(R$dimen.mtrl_slider_label_top_offset);
        this.labelTextSize = resources.getDimension(R$dimen.mtrl_slider_label_text_size);
        this.labelTextTopOffset = resources.getDimensionPixelSize(R$dimen.mtrl_slider_label_text_top_offset);
    }

    private void processAttributes(Context context, AttributeSet attributeSet, int i) {
        TypedArray obtainStyledAttributes = ThemeEnforcement.obtainStyledAttributes(context, attributeSet, R$styleable.Slider, i, DEF_STYLE_RES, new int[0]);
        this.valueFrom = obtainStyledAttributes.getFloat(R$styleable.Slider_android_valueFrom, 0.0f);
        this.valueTo = obtainStyledAttributes.getFloat(R$styleable.Slider_android_valueTo, 1.0f);
        setValue(obtainStyledAttributes.getFloat(R$styleable.Slider_android_value, this.valueFrom));
        this.stepSize = obtainStyledAttributes.getFloat(R$styleable.Slider_android_stepSize, 0.0f);
        boolean hasValue = obtainStyledAttributes.hasValue(R$styleable.Slider_trackColor);
        int i2 = hasValue ? R$styleable.Slider_trackColor : R$styleable.Slider_inactiveTrackColor;
        int i3 = hasValue ? R$styleable.Slider_trackColor : R$styleable.Slider_activeTrackColor;
        this.inactiveTrackColor = MaterialResources.getColorStateList(context, obtainStyledAttributes, i2);
        this.activeTrackColor = MaterialResources.getColorStateList(context, obtainStyledAttributes, i3);
        ColorStateList colorStateList = MaterialResources.getColorStateList(context, obtainStyledAttributes, R$styleable.Slider_thumbColor);
        this.thumbColor = colorStateList;
        this.thumbDrawable.setFillColor(colorStateList);
        this.tickColor = MaterialResources.getColorStateList(context, obtainStyledAttributes, R$styleable.Slider_activeTickColor);
        this.textColor = MaterialResources.getColorStateList(context, obtainStyledAttributes, R$styleable.Slider_labelColor);
        setThumbRadius(obtainStyledAttributes.getDimensionPixelSize(R$styleable.Slider_thumbRadius, 0));
        this.haloRadius = obtainStyledAttributes.getDimensionPixelSize(R$styleable.Slider_haloRadius, 0);
        setThumbElevation(obtainStyledAttributes.getDimension(R$styleable.Slider_thumbElevation, 0.0f));
        obtainStyledAttributes.recycle();
        validateValueFrom();
        validateValueTo();
        validateStepSize();
    }

    private void validateValueFrom() {
        if (this.valueFrom >= this.valueTo) {
            Log.e(TAG, "valueFrom must be smaller than valueTo");
            throw new IllegalArgumentException("valueFrom must be smaller than valueTo");
        }
    }

    private void validateValueTo() {
        if (this.valueTo <= this.valueFrom) {
            Log.e(TAG, "valueTo must be greater than valueFrom");
            throw new IllegalArgumentException("valueTo must be greater than valueFrom");
        }
    }

    private void validateStepSize() {
        String str = TAG;
        float f = this.stepSize;
        if (f < 0.0f) {
            Log.e(str, "The stepSize must be 0, or a factor of the valueFrom-valueTo range");
            throw new IllegalArgumentException("The stepSize must be 0, or a factor of the valueFrom-valueTo range");
        } else if (f > 0.0f && (this.valueTo - this.valueFrom) % f != 0.0f) {
            Log.e(str, "The stepSize must be 0, or a factor of the valueFrom-valueTo range");
            throw new IllegalArgumentException("The stepSize must be 0, or a factor of the valueFrom-valueTo range");
        }
    }

    public float getValueFrom() {
        return this.valueFrom;
    }

    public void setValueFrom(float f) {
        this.valueFrom = f;
        validateValueFrom();
    }

    public float getValueTo() {
        return this.valueTo;
    }

    public void setValueTo(float f) {
        this.valueTo = f;
        validateValueTo();
    }

    public float getValue() {
        float f = this.thumbPosition;
        float f2 = this.valueTo;
        float f3 = this.valueFrom;
        return (f * (f2 - f3)) + f3;
    }

    public void setValue(float f) {
        if (isValueValid(f)) {
            float f2 = this.valueFrom;
            this.thumbPosition = (f - f2) / (this.valueTo - f2);
            if (hasOnChangeListener()) {
                this.listener.onValueChange(this, getValue());
            }
        }
    }

    private boolean isValueValid(float f) {
        String str = TAG;
        float f2 = this.valueFrom;
        if (f < f2 || f > this.valueTo) {
            Log.e(str, "Slider value must be greater or equal to valueFrom, and lower or equal to valueTo");
            return false;
        }
        float f3 = this.stepSize;
        if (f3 <= 0.0f || (f2 - f) % f3 == 0.0f) {
            return true;
        }
        Log.e(str, "Value must be equal to valueFrom plus a multiple of stepSize when using stepSize");
        return false;
    }

    public float getStepSize() {
        return this.stepSize;
    }

    public void setStepSize(float f) {
        this.stepSize = f;
        validateStepSize();
        requestLayout();
    }

    public boolean hasOnChangeListener() {
        return this.listener != null;
    }

    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.listener = onChangeListener;
    }

    public boolean hasLabelFormatter() {
        return this.formatter != null;
    }

    public void setLabelFormatter(LabelFormatter labelFormatter) {
        this.formatter = labelFormatter;
    }

    public void setThumbElevation(float f) {
        this.thumbDrawable.setElevation(f);
        postInvalidate();
    }

    public void setThumbElevationResource(int i) {
        setThumbElevation(getResources().getDimension(i));
    }

    public float getThumbElevation() {
        return this.thumbDrawable.getElevation();
    }

    public void setThumbRadius(int i) {
        this.thumbRadius = i;
        MaterialShapeDrawable materialShapeDrawable = this.thumbDrawable;
        ShapeAppearanceModel.Builder builder = ShapeAppearanceModel.builder();
        builder.setAllCorners(0, (float) this.thumbRadius);
        materialShapeDrawable.setShapeAppearanceModel(builder.build());
        MaterialShapeDrawable materialShapeDrawable2 = this.thumbDrawable;
        int i2 = this.thumbRadius;
        materialShapeDrawable2.setBounds(0, 0, i2 * 2, i2 * 2);
        postInvalidate();
    }

    public void setThumbRadiusResource(int i) {
        setThumbRadius(getResources().getDimensionPixelSize(i));
    }

    public int getThumbRadius() {
        return this.thumbRadius;
    }

    public void setHaloRadius(int i) {
        this.haloRadius = i;
        postInvalidate();
    }

    public void setHaloRadiusResource(int i) {
        setHaloRadius(getResources().getDimensionPixelSize(i));
    }

    public int getHaloRadius() {
        return this.haloRadius;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(this.stepSize > 0.0f ? this.widgetHeightDiscrete : this.widgetHeight, 1073741824));
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        updateTrackWidthAndTicksCoordinates(i);
    }

    private void updateTrackWidthAndTicksCoordinates(int i) {
        this.trackWidth = i - (this.trackSidePadding * 2);
        float f = this.stepSize;
        if (f > 0.0f) {
            int i2 = (int) (((this.valueTo - this.valueFrom) / f) + 1.0f);
            float[] fArr = this.ticksCoordinates;
            if (fArr == null || fArr.length != i2 * 2) {
                this.ticksCoordinates = new float[(i2 * 2)];
            }
            float f2 = ((float) this.trackWidth) / ((float) (i2 - 1));
            for (int i3 = 0; i3 < i2 * 2; i3 += 2) {
                float[] fArr2 = this.ticksCoordinates;
                fArr2[i3] = ((float) this.trackSidePadding) + (((float) (i3 / 2)) * f2);
                fArr2[i3 + 1] = (float) this.trackTopDiscrete;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int i = this.stepSize > 0.0f ? this.trackTopDiscrete : this.trackTop;
        drawTrack(canvas, this.trackWidth, i);
        if (this.thumbPosition > 0.0f) {
            drawMarker(canvas, this.trackWidth, i);
        }
        if (this.stepSize > 0.0f) {
            if (!hasFocus() || !isEnabled()) {
                drawThumb(canvas, this.trackWidth, i);
            } else {
                drawLabel(canvas, this.trackWidth, i);
                drawLabelText(canvas, this.trackWidth, i);
            }
            if (this.thumbIsPressed) {
                drawTicks(canvas);
                return;
            }
            return;
        }
        if (!this.thumbIsPressed && hasFocus() && isEnabled()) {
            drawHalo(canvas, this.trackWidth, i);
        }
        drawThumb(canvas, this.trackWidth, i);
    }

    private void drawTrack(Canvas canvas, int i, int i2) {
        int i3 = this.trackSidePadding;
        float f = ((float) i3) + (this.thumbPosition * ((float) i));
        if (f < ((float) (i3 + i))) {
            float f2 = (float) i2;
            canvas.drawLine(f, f2, (float) (i3 + i), f2, this.inactiveTrackPaint);
        }
    }

    private void drawMarker(Canvas canvas, int i, int i2) {
        int i3 = this.trackSidePadding;
        float f = (float) i2;
        canvas.drawLine((float) i3, f, ((float) i3) + (this.thumbPosition * ((float) i)), f, this.activeTrackPaint);
    }

    private void drawTicks(Canvas canvas) {
        canvas.drawPoints(this.ticksCoordinates, this.ticksPaint);
    }

    private void drawLabel(Canvas canvas, int i, int i2) {
        int i3 = this.trackSidePadding + ((int) (this.thumbPosition * ((float) i)));
        int i4 = this.labelWidth;
        int i5 = i3 - (i4 / 2);
        int i6 = i2 - this.labelTopOffset;
        this.label.setBounds(i5, i6, i4 + i5, this.labelHeight + i6);
        this.label.draw(canvas);
    }

    private void drawLabelText(Canvas canvas, int i, int i2) {
        Paint paint = this.labelTextPaint;
        String str = this.labelText;
        paint.getTextBounds(str, 0, str.length(), this.labelTextBounds);
        canvas.drawText(this.labelText, (float) ((this.trackSidePadding + ((int) (this.thumbPosition * ((float) i)))) - (this.labelTextBounds.width() / 2)), (float) (i2 - this.labelTextTopOffset), this.labelTextPaint);
    }

    private void drawThumb(Canvas canvas, int i, int i2) {
        if (!isEnabled()) {
            canvas.drawCircle(((float) this.trackSidePadding) + (this.thumbPosition * ((float) i)), (float) i2, (float) this.thumbRadius, this.thumbPaint);
        }
        canvas.save();
        int i3 = this.trackSidePadding + ((int) (this.thumbPosition * ((float) i)));
        int i4 = this.thumbRadius;
        canvas.translate((float) (i3 - i4), (float) (i2 - i4));
        this.thumbDrawable.draw(canvas);
        canvas.restore();
    }

    private void drawHalo(Canvas canvas, int i, int i2) {
        canvas.drawCircle(((float) this.trackSidePadding) + (this.thumbPosition * ((float) i)), (float) i2, (float) this.haloRadius, this.haloPaint);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!isEnabled()) {
            return false;
        }
        float min = Math.min(1.0f, Math.max(0.0f, (motionEvent.getX() - ((float) this.trackSidePadding)) / ((float) this.trackWidth)));
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            getParent().requestDisallowInterceptTouchEvent(true);
            requestFocus();
            this.thumbIsPressed = true;
            this.thumbPosition = min;
            snapThumbPosition();
            invalidate();
            if (hasOnChangeListener()) {
                this.listener.onValueChange(this, getValue());
            }
        } else if (actionMasked == 1) {
            getParent().requestDisallowInterceptTouchEvent(false);
            this.thumbIsPressed = false;
            this.thumbPosition = min;
            snapThumbPosition();
            invalidate();
        } else if (actionMasked == 2) {
            this.thumbPosition = min;
            snapThumbPosition();
            invalidate();
            if (hasOnChangeListener()) {
                this.listener.onValueChange(this, getValue());
            }
        }
        float value = getValue();
        if (hasLabelFormatter()) {
            this.labelText = this.formatter.getFormattedValue(value);
        } else {
            this.labelText = String.format(((float) ((int) value)) == value ? "%.0f" : "%.2f", Float.valueOf(value));
        }
        return true;
    }

    private void snapThumbPosition() {
        if (this.stepSize > 0.0f) {
            this.thumbPosition = ((float) Math.round(this.thumbPosition * ((float) ((this.ticksCoordinates.length / 2) - 1)))) / ((float) ((this.ticksCoordinates.length / 2) - 1));
        }
    }

    /* access modifiers changed from: protected */
    public void drawableStateChanged() {
        super.drawableStateChanged();
        this.inactiveTrackPaint.setColor(getColorForState(this.inactiveTrackColor));
        this.activeTrackPaint.setColor(getColorForState(this.activeTrackColor));
        this.ticksPaint.setColor(getColorForState(this.tickColor));
        this.labelTextPaint.setColor(getColorForState(this.textColor));
        if (this.thumbDrawable.isStateful()) {
            this.thumbDrawable.setState(getDrawableState());
        }
        this.haloPaint.setColor(getColorForState(this.thumbColor));
        this.haloPaint.setAlpha(63);
    }

    private int getColorForState(ColorStateList colorStateList) {
        return colorStateList.getColorForState(getDrawableState(), colorStateList.getDefaultColor());
    }

    /* access modifiers changed from: protected */
    public Parcelable onSaveInstanceState() {
        SliderState sliderState = new SliderState(super.onSaveInstanceState());
        sliderState.valueFrom = this.valueFrom;
        sliderState.valueTo = this.valueTo;
        sliderState.thumbPosition = this.thumbPosition;
        sliderState.stepSize = this.stepSize;
        sliderState.hasFocus = hasFocus();
        return sliderState;
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Parcelable parcelable) {
        SliderState sliderState = (SliderState) parcelable;
        super.onRestoreInstanceState(sliderState.getSuperState());
        this.valueFrom = sliderState.valueFrom;
        this.valueTo = sliderState.valueTo;
        this.thumbPosition = sliderState.thumbPosition;
        this.stepSize = sliderState.stepSize;
        if (sliderState.hasFocus) {
            requestFocus();
        }
        if (hasOnChangeListener()) {
            this.listener.onValueChange(this, getValue());
        }
    }

    /* access modifiers changed from: package-private */
    public static class SliderState extends View.BaseSavedState {
        public static final Parcelable.Creator<SliderState> CREATOR = new Parcelable.Creator<SliderState>() {
            /* class com.google.android.material.slider.Slider.SliderState.AnonymousClass1 */

            @Override // android.os.Parcelable.Creator
            public SliderState createFromParcel(Parcel parcel) {
                return new SliderState(parcel);
            }

            @Override // android.os.Parcelable.Creator
            public SliderState[] newArray(int i) {
                return new SliderState[i];
            }
        };
        boolean hasFocus;
        float stepSize;
        float thumbPosition;
        float[] ticksCoordinates;
        float valueFrom;
        float valueTo;

        SliderState(Parcelable parcelable) {
            super(parcelable);
        }

        private SliderState(Parcel parcel) {
            super(parcel);
            this.valueFrom = parcel.readFloat();
            this.valueTo = parcel.readFloat();
            this.thumbPosition = parcel.readFloat();
            this.stepSize = parcel.readFloat();
            parcel.readFloatArray(this.ticksCoordinates);
            this.hasFocus = parcel.createBooleanArray()[0];
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeFloat(this.valueFrom);
            parcel.writeFloat(this.valueTo);
            parcel.writeFloat(this.thumbPosition);
            parcel.writeFloat(this.stepSize);
            parcel.writeFloatArray(this.ticksCoordinates);
            parcel.writeBooleanArray(new boolean[]{this.hasFocus});
        }
    }
}
