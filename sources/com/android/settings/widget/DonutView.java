package com.android.settings.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.icu.text.DecimalFormatSymbols;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.view.View;
import com.android.settings.C0006R$color;
import com.android.settings.C0007R$dimen;
import com.android.settings.C0017R$string;
import com.android.settings.R$styleable;
import com.android.settingslib.Utils;

public class DonutView extends View {
    private Paint mBackgroundCircle;
    private TextPaint mBigNumberPaint;
    private Paint mFilledArc;
    private String mFullString;
    private int mMeterBackgroundColor;
    private int mMeterConsumedColor;
    private double mPercent;
    private String mPercentString;
    private boolean mShowPercentString = true;
    private float mStrokeWidth;
    private TextPaint mTextPaint;

    public DonutView(Context context) {
        super(context);
    }

    public DonutView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        boolean z;
        this.mMeterBackgroundColor = context.getColor(C0006R$color.meter_background_color);
        this.mMeterConsumedColor = Utils.getColorStateListDefaultColor(((View) this).mContext, C0006R$color.meter_consumed_color);
        Resources resources = context.getResources();
        this.mStrokeWidth = resources.getDimension(C0007R$dimen.storage_donut_thickness);
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.DonutView);
            this.mMeterBackgroundColor = obtainStyledAttributes.getColor(R$styleable.DonutView_meterBackgroundColor, this.mMeterBackgroundColor);
            this.mMeterConsumedColor = obtainStyledAttributes.getColor(R$styleable.DonutView_meterConsumedColor, this.mMeterConsumedColor);
            z = obtainStyledAttributes.getBoolean(R$styleable.DonutView_applyColorAccent, true);
            this.mShowPercentString = obtainStyledAttributes.getBoolean(R$styleable.DonutView_showPercentString, true);
            this.mStrokeWidth = (float) obtainStyledAttributes.getDimensionPixelSize(R$styleable.DonutView_thickness, (int) this.mStrokeWidth);
            obtainStyledAttributes.recycle();
        } else {
            z = true;
        }
        Paint paint = new Paint();
        this.mBackgroundCircle = paint;
        paint.setAntiAlias(true);
        this.mBackgroundCircle.setStrokeCap(Paint.Cap.BUTT);
        this.mBackgroundCircle.setStyle(Paint.Style.STROKE);
        this.mBackgroundCircle.setStrokeWidth(this.mStrokeWidth);
        this.mBackgroundCircle.setColor(this.mMeterBackgroundColor);
        Paint paint2 = new Paint();
        this.mFilledArc = paint2;
        paint2.setAntiAlias(true);
        this.mFilledArc.setStrokeCap(Paint.Cap.BUTT);
        this.mFilledArc.setStyle(Paint.Style.STROKE);
        this.mFilledArc.setStrokeWidth(this.mStrokeWidth);
        this.mFilledArc.setColor(this.mMeterConsumedColor);
        if (z) {
            PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(Utils.getColorAttrDefaultColor(context, 16843829), PorterDuff.Mode.SRC_IN);
            this.mBackgroundCircle.setColorFilter(porterDuffColorFilter);
            this.mFilledArc.setColorFilter(porterDuffColorFilter);
        }
        int i = TextUtils.getLayoutDirectionFromLocale(resources.getConfiguration().locale) == 0 ? 0 : 1;
        TextPaint textPaint = new TextPaint();
        this.mTextPaint = textPaint;
        textPaint.setAntiAlias(true);
        this.mTextPaint.setColor(getResources().getColor(C0006R$color.op_control_text_color_secondary));
        this.mTextPaint.setTextSize(resources.getDimension(C0007R$dimen.conversation_status_text_size));
        this.mTextPaint.setTypeface(Typeface.create("sans-serif-light", 0));
        this.mTextPaint.setTextAlign(Paint.Align.CENTER);
        this.mTextPaint.setBidiFlags(i);
        TextPaint textPaint2 = new TextPaint();
        this.mBigNumberPaint = textPaint2;
        textPaint2.setColor(getResources().getColor(C0006R$color.op_control_text_color_primary));
        this.mBigNumberPaint.setAntiAlias(true);
        this.mBigNumberPaint.setTextSize(resources.getDimension(C0007R$dimen.conversation_message_text_size));
        this.mBigNumberPaint.setTypeface(Typeface.create("sans-serif-light", 0));
        this.mBigNumberPaint.setBidiFlags(i);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawDonut(canvas);
        if (this.mShowPercentString) {
            drawInnerText(canvas);
        }
    }

    private void drawDonut(Canvas canvas) {
        float f = this.mStrokeWidth / 2.0f;
        float f2 = 0.0f + f;
        canvas.drawArc(f2, f2, ((float) getWidth()) - f, ((float) getHeight()) - f, -90.0f, 360.0f, false, this.mBackgroundCircle);
        canvas.drawArc(f2, f2, ((float) getWidth()) - f, ((float) getHeight()) - f, -90.0f, ((float) this.mPercent) * 360.0f, false, this.mFilledArc);
    }

    private void drawInnerText(Canvas canvas) {
        float textHeight = getTextHeight(this.mTextPaint) + getTextHeight(this.mBigNumberPaint);
        float height = ((float) (getHeight() / 2)) + (textHeight / 2.0f);
        String percentString = new DecimalFormatSymbols().getPercentString();
        canvas.save();
        StaticLayout staticLayout = new StaticLayout(getPercentageStringSpannable(getResources(), this.mPercentString, percentString), this.mBigNumberPaint, getWidth(), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        canvas.translate(0.0f, (((float) getHeight()) - textHeight) / 2.0f);
        staticLayout.draw(canvas);
        canvas.restore();
        canvas.drawText(this.mFullString, (float) (getWidth() / 2), height - this.mTextPaint.descent(), this.mTextPaint);
    }

    public void setPercentage(double d) {
        this.mPercent = d;
        this.mPercentString = Utils.formatPercentage(d);
        String string = getContext().getString(C0017R$string.storage_percent_full);
        this.mFullString = string;
        if (string.length() > 10) {
            this.mTextPaint.setTextSize(getContext().getResources().getDimension(C0007R$dimen.storage_donut_view_shrunken_label_text_size));
        }
        setContentDescription(getContext().getString(C0017R$string.join_two_unrelated_items, this.mPercentString, this.mFullString));
        invalidate();
    }

    public int getMeterBackgroundColor() {
        return this.mMeterBackgroundColor;
    }

    public void setMeterBackgroundColor(int i) {
        this.mMeterBackgroundColor = i;
        this.mBackgroundCircle.setColor(i);
        invalidate();
    }

    public int getMeterConsumedColor() {
        return this.mMeterConsumedColor;
    }

    public void setMeterConsumedColor(int i) {
        this.mMeterConsumedColor = i;
        this.mFilledArc.setColor(i);
        invalidate();
    }

    static Spannable getPercentageStringSpannable(Resources resources, String str, String str2) {
        float dimension = resources.getDimension(C0007R$dimen.storage_donut_view_percent_sign_size) / resources.getDimension(C0007R$dimen.storage_donut_view_percent_text_size);
        SpannableString spannableString = new SpannableString(str);
        int indexOf = str.indexOf(str2);
        int length = str2.length() + indexOf;
        if (indexOf < 0) {
            indexOf = 0;
            length = str.length();
        }
        spannableString.setSpan(new RelativeSizeSpan(dimension), indexOf, length, 34);
        return spannableString;
    }

    private float getTextHeight(TextPaint textPaint) {
        return textPaint.descent() - textPaint.ascent();
    }
}
