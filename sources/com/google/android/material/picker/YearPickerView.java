package com.google.android.material.picker;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.google.android.material.R$dimen;
import com.google.android.material.R$id;
import com.google.android.material.R$layout;
import java.util.Calendar;

/* access modifiers changed from: package-private */
public class YearPickerView extends FrameLayout {
    private static final int ITEM_LAYOUT = R$layout.op_year_label_text_view;
    private OnYearSelectedListener mOnYearSelectedListener;
    private NumberPicker mPicker;

    public interface OnYearSelectedListener {
        void onYearChanged(YearPickerView yearPickerView, int i);
    }

    public YearPickerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 16844068);
    }

    public YearPickerView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public YearPickerView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        Resources resources = context.getResources();
        LayoutInflater.from(context).inflate(ITEM_LAYOUT, (ViewGroup) this, true);
        resources.getDimensionPixelOffset(R$dimen.datepicker_view_animator_height);
        resources.getDimensionPixelOffset(R$dimen.datepicker_year_label_height);
        NumberPicker numberPicker = (NumberPicker) findViewById(R$id.year_picker);
        this.mPicker = numberPicker;
        numberPicker.setSelectNumberCount(5);
    }

    public void setCurrentYear() {
        OnYearSelectedListener onYearSelectedListener = this.mOnYearSelectedListener;
        if (onYearSelectedListener != null) {
            onYearSelectedListener.onYearChanged(this, this.mPicker.getValue());
        }
    }

    public void setOnYearSelectedListener(OnYearSelectedListener onYearSelectedListener) {
        this.mOnYearSelectedListener = onYearSelectedListener;
    }

    public void setYear(int i) {
        this.mPicker.setValue(i);
    }

    public void setRange(Calendar calendar, Calendar calendar2) {
        int i = calendar.get(1);
        int i2 = calendar2.get(1);
        this.mPicker.setMinValue(i);
        this.mPicker.setMaxValue(i2);
    }
}
