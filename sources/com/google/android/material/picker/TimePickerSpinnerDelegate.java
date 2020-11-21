package com.google.android.material.picker;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcelable;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.material.R$id;
import com.google.android.material.R$layout;
import com.google.android.material.R$styleable;
import com.google.android.material.picker.NumberPicker;
import com.google.android.material.picker.TimePicker;
import java.util.Calendar;

/* access modifiers changed from: package-private */
public class TimePickerSpinnerDelegate extends TimePicker.AbstractTimePickerDelegate {
    private final Button mAmPmButton;
    private final NumberPicker mAmPmSpinner;
    private final EditText mAmPmSpinnerInput;
    private final String[] mAmPmStrings;
    private final TextView mDivider;
    private char mHourFormat;
    private final NumberPicker mHourSpinner;
    private final EditText mHourSpinnerInput;
    private boolean mHourWithTwoDigit;
    private boolean mIs24HourView;
    private boolean mIsAm;
    private boolean mIsEnabled = true;
    private final NumberPicker mMinuteSpinner;
    private final EditText mMinuteSpinnerInput;
    private final Calendar mTempCalendar;

    @Override // com.google.android.material.picker.TimePicker.TimePickerDelegate
    public boolean validateInput() {
        return true;
    }

    public TimePickerSpinnerDelegate(TimePicker timePicker, Context context, AttributeSet attributeSet, int i, int i2) {
        super(timePicker, context);
        TypedArray obtainStyledAttributes = this.mContext.obtainStyledAttributes(attributeSet, R$styleable.TimePicker, i, i2);
        int resourceId = obtainStyledAttributes.getResourceId(R$styleable.TimePicker_legacyLayout, R$layout.op_time_picker_legacy_material);
        obtainStyledAttributes.recycle();
        LayoutInflater.from(this.mContext).inflate(resourceId, (ViewGroup) this.mDelegator, true).setSaveFromParentEnabled(false);
        NumberPicker numberPicker = (NumberPicker) timePicker.findViewById(R$id.hour);
        this.mHourSpinner = numberPicker;
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            /* class com.google.android.material.picker.TimePickerSpinnerDelegate.AnonymousClass1 */

            @Override // com.google.android.material.picker.NumberPicker.OnValueChangeListener
            public void onValueChange(NumberPicker numberPicker, int i, int i2) {
                TimePickerSpinnerDelegate.this.updateInputState();
                if (!TimePickerSpinnerDelegate.this.is24Hour() && ((i == 11 && i2 == 12) || (i == 12 && i2 == 11))) {
                    TimePickerSpinnerDelegate timePickerSpinnerDelegate = TimePickerSpinnerDelegate.this;
                    timePickerSpinnerDelegate.mIsAm = !timePickerSpinnerDelegate.mIsAm;
                    TimePickerSpinnerDelegate.this.updateAmPmControl();
                }
                TimePickerSpinnerDelegate.this.onTimeChanged();
            }
        });
        EditText editText = (EditText) this.mHourSpinner.findViewById(R$id.numberpicker_input);
        this.mHourSpinnerInput = editText;
        editText.setImeOptions(5);
        TextView textView = (TextView) this.mDelegator.findViewById(R$id.divider);
        this.mDivider = textView;
        if (textView != null) {
            setDividerText();
        }
        NumberPicker numberPicker2 = (NumberPicker) this.mDelegator.findViewById(R$id.minute);
        this.mMinuteSpinner = numberPicker2;
        numberPicker2.setMinValue(0);
        this.mMinuteSpinner.setMaxValue(59);
        this.mMinuteSpinner.setOnLongPressUpdateInterval(100);
        this.mMinuteSpinner.setFormatter(NumberPicker.getTwoDigitFormatter());
        this.mMinuteSpinner.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            /* class com.google.android.material.picker.TimePickerSpinnerDelegate.AnonymousClass2 */

            @Override // com.google.android.material.picker.NumberPicker.OnValueChangeListener
            public void onValueChange(NumberPicker numberPicker, int i, int i2) {
                TimePickerSpinnerDelegate.this.updateInputState();
                int minValue = TimePickerSpinnerDelegate.this.mMinuteSpinner.getMinValue();
                int maxValue = TimePickerSpinnerDelegate.this.mMinuteSpinner.getMaxValue();
                if (i == maxValue && i2 == minValue) {
                    int value = TimePickerSpinnerDelegate.this.mHourSpinner.getValue() + 1;
                    if (!TimePickerSpinnerDelegate.this.is24Hour() && value == 12) {
                        TimePickerSpinnerDelegate timePickerSpinnerDelegate = TimePickerSpinnerDelegate.this;
                        timePickerSpinnerDelegate.mIsAm = !timePickerSpinnerDelegate.mIsAm;
                        TimePickerSpinnerDelegate.this.updateAmPmControl();
                    }
                    TimePickerSpinnerDelegate.this.mHourSpinner.setValue(value);
                } else if (i == minValue && i2 == maxValue) {
                    int value2 = TimePickerSpinnerDelegate.this.mHourSpinner.getValue() - 1;
                    if (!TimePickerSpinnerDelegate.this.is24Hour() && value2 == 11) {
                        TimePickerSpinnerDelegate timePickerSpinnerDelegate2 = TimePickerSpinnerDelegate.this;
                        timePickerSpinnerDelegate2.mIsAm = !timePickerSpinnerDelegate2.mIsAm;
                        TimePickerSpinnerDelegate.this.updateAmPmControl();
                    }
                    TimePickerSpinnerDelegate.this.mHourSpinner.setValue(value2);
                }
                TimePickerSpinnerDelegate.this.onTimeChanged();
            }
        });
        EditText editText2 = (EditText) this.mMinuteSpinner.findViewById(R$id.numberpicker_input);
        this.mMinuteSpinnerInput = editText2;
        editText2.setImeOptions(5);
        this.mAmPmStrings = TimePicker.getAmPmStrings(context);
        View findViewById = this.mDelegator.findViewById(R$id.amPm);
        if (findViewById instanceof Button) {
            this.mAmPmSpinner = null;
            this.mAmPmSpinnerInput = null;
            Button button = (Button) findViewById;
            this.mAmPmButton = button;
            button.setOnClickListener(new View.OnClickListener() {
                /* class com.google.android.material.picker.TimePickerSpinnerDelegate.AnonymousClass3 */

                public void onClick(View view) {
                    view.requestFocus();
                    TimePickerSpinnerDelegate timePickerSpinnerDelegate = TimePickerSpinnerDelegate.this;
                    timePickerSpinnerDelegate.mIsAm = !timePickerSpinnerDelegate.mIsAm;
                    TimePickerSpinnerDelegate.this.updateAmPmControl();
                    TimePickerSpinnerDelegate.this.onTimeChanged();
                }
            });
        } else {
            this.mAmPmButton = null;
            NumberPicker numberPicker3 = (NumberPicker) findViewById;
            this.mAmPmSpinner = numberPicker3;
            numberPicker3.setMinValue(0);
            this.mAmPmSpinner.setMaxValue(1);
            this.mAmPmSpinner.setDisplayedValues(this.mAmPmStrings);
            this.mAmPmSpinner.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                /* class com.google.android.material.picker.TimePickerSpinnerDelegate.AnonymousClass4 */

                @Override // com.google.android.material.picker.NumberPicker.OnValueChangeListener
                public void onValueChange(NumberPicker numberPicker, int i, int i2) {
                    TimePickerSpinnerDelegate.this.updateInputState();
                    numberPicker.requestFocus();
                    TimePickerSpinnerDelegate timePickerSpinnerDelegate = TimePickerSpinnerDelegate.this;
                    timePickerSpinnerDelegate.mIsAm = !timePickerSpinnerDelegate.mIsAm;
                    TimePickerSpinnerDelegate.this.updateAmPmControl();
                    TimePickerSpinnerDelegate.this.onTimeChanged();
                }
            });
            EditText editText3 = (EditText) this.mAmPmSpinner.findViewById(R$id.numberpicker_input);
            this.mAmPmSpinnerInput = editText3;
            editText3.setImeOptions(6);
        }
        if (isAmPmAtStart()) {
            ViewGroup viewGroup = (ViewGroup) timePicker.findViewById(R$id.timePickerLayout);
            viewGroup.removeView(findViewById);
            viewGroup.addView(findViewById, 0);
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) findViewById.getLayoutParams();
            int marginStart = marginLayoutParams.getMarginStart();
            int marginEnd = marginLayoutParams.getMarginEnd();
            if (marginStart != marginEnd) {
                marginLayoutParams.setMarginStart(marginEnd);
                marginLayoutParams.setMarginEnd(marginStart);
            }
        }
        getHourFormatData();
        updateHourControl();
        updateMinuteControl();
        updateAmPmControl();
        Calendar instance = Calendar.getInstance(this.mLocale);
        this.mTempCalendar = instance;
        setHour(instance.get(11));
        setMinute(this.mTempCalendar.get(12));
        if (!isEnabled()) {
            setEnabled(false);
        }
        if (this.mDelegator.getImportantForAccessibility() == 0) {
            this.mDelegator.setImportantForAccessibility(1);
        }
    }

    private void getHourFormatData() {
        String bestDateTimePattern = DateFormat.getBestDateTimePattern(this.mLocale, this.mIs24HourView ? "Hm" : "hm");
        int length = bestDateTimePattern.length();
        this.mHourWithTwoDigit = false;
        for (int i = 0; i < length; i++) {
            char charAt = bestDateTimePattern.charAt(i);
            if (charAt == 'H' || charAt == 'h' || charAt == 'K' || charAt == 'k') {
                this.mHourFormat = charAt;
                int i2 = i + 1;
                if (i2 < length && charAt == bestDateTimePattern.charAt(i2)) {
                    this.mHourWithTwoDigit = true;
                    return;
                }
                return;
            }
        }
    }

    private boolean isAmPmAtStart() {
        return DateFormat.getBestDateTimePattern(this.mLocale, "hm").startsWith("a");
    }

    private void setDividerText() {
        String str;
        String bestDateTimePattern = DateFormat.getBestDateTimePattern(this.mLocale, this.mIs24HourView ? "Hm" : "hm");
        int lastIndexOf = bestDateTimePattern.lastIndexOf(72);
        if (lastIndexOf == -1) {
            lastIndexOf = bestDateTimePattern.lastIndexOf(androidx.constraintlayout.widget.R$styleable.Constraint_motionStagger);
        }
        if (lastIndexOf == -1) {
            str = ":";
        } else {
            int i = lastIndexOf + 1;
            int indexOf = bestDateTimePattern.indexOf(androidx.constraintlayout.widget.R$styleable.Constraint_transitionPathRotate, i);
            if (indexOf == -1) {
                str = Character.toString(bestDateTimePattern.charAt(i));
            } else {
                str = bestDateTimePattern.substring(i, indexOf);
            }
        }
        this.mDivider.setText(str);
    }

    @Override // com.google.android.material.picker.TimePicker.TimePickerDelegate
    public void setHour(int i) {
        setCurrentHour(i, true);
    }

    private void setCurrentHour(int i, boolean z) {
        if (i != getHour()) {
            if (!is24Hour()) {
                if (i >= 12) {
                    this.mIsAm = false;
                    if (i > 12) {
                        i -= 12;
                    }
                } else {
                    this.mIsAm = true;
                    if (i == 0) {
                        i = 12;
                    }
                }
                updateAmPmControl();
            }
            this.mHourSpinner.setValue(i);
            if (z) {
                onTimeChanged();
            }
        }
    }

    @Override // com.google.android.material.picker.TimePicker.TimePickerDelegate
    public int getHour() {
        int value = this.mHourSpinner.getValue();
        if (is24Hour()) {
            return value;
        }
        if (this.mIsAm) {
            return value % 12;
        }
        return (value % 12) + 12;
    }

    @Override // com.google.android.material.picker.TimePicker.TimePickerDelegate
    public void setMinute(int i) {
        if (i != getMinute()) {
            this.mMinuteSpinner.setValue(i);
            onTimeChanged();
        }
    }

    @Override // com.google.android.material.picker.TimePicker.TimePickerDelegate
    public int getMinute() {
        return this.mMinuteSpinner.getValue();
    }

    @Override // com.google.android.material.picker.TimePicker.TimePickerDelegate
    public void setIs24Hour(boolean z) {
        if (this.mIs24HourView != z) {
            int hour = getHour();
            this.mIs24HourView = z;
            getHourFormatData();
            updateHourControl();
            setCurrentHour(hour, false);
            updateMinuteControl();
            updateAmPmControl();
        }
    }

    @Override // com.google.android.material.picker.TimePicker.TimePickerDelegate
    public boolean is24Hour() {
        return this.mIs24HourView;
    }

    @Override // com.google.android.material.picker.TimePicker.TimePickerDelegate
    public void setEnabled(boolean z) {
        this.mMinuteSpinner.setEnabled(z);
        TextView textView = this.mDivider;
        if (textView != null) {
            textView.setEnabled(z);
        }
        this.mHourSpinner.setEnabled(z);
        NumberPicker numberPicker = this.mAmPmSpinner;
        if (numberPicker != null) {
            numberPicker.setEnabled(z);
        } else {
            this.mAmPmButton.setEnabled(z);
        }
        this.mIsEnabled = z;
    }

    @Override // com.google.android.material.picker.TimePicker.TimePickerDelegate
    public boolean isEnabled() {
        return this.mIsEnabled;
    }

    @Override // com.google.android.material.picker.TimePicker.TimePickerDelegate
    public int getBaseline() {
        return this.mHourSpinner.getBaseline();
    }

    @Override // com.google.android.material.picker.TimePicker.TimePickerDelegate
    public Parcelable onSaveInstanceState(Parcelable parcelable) {
        return new TimePicker.AbstractTimePickerDelegate.SavedState(parcelable, getHour(), getMinute(), is24Hour());
    }

    @Override // com.google.android.material.picker.TimePicker.TimePickerDelegate
    public void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable instanceof TimePicker.AbstractTimePickerDelegate.SavedState) {
            TimePicker.AbstractTimePickerDelegate.SavedState savedState = (TimePicker.AbstractTimePickerDelegate.SavedState) parcelable;
            setHour(savedState.getHour());
            setMinute(savedState.getMinute());
        }
    }

    @Override // com.google.android.material.picker.TimePicker.TimePickerDelegate
    public View getHourView() {
        return this.mHourSpinnerInput;
    }

    @Override // com.google.android.material.picker.TimePicker.TimePickerDelegate
    public View getMinuteView() {
        return this.mMinuteSpinnerInput;
    }

    @Override // com.google.android.material.picker.TimePicker.TimePickerDelegate
    public View getAmView() {
        return this.mAmPmSpinnerInput;
    }

    @Override // com.google.android.material.picker.TimePicker.TimePickerDelegate
    public View getPmView() {
        return this.mAmPmSpinnerInput;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateInputState() {
        InputMethodManager inputMethodManager = (InputMethodManager) this.mContext.getSystemService("input_method");
        if (inputMethodManager == null) {
            return;
        }
        if (inputMethodManager.isActive(this.mHourSpinnerInput)) {
            this.mHourSpinnerInput.clearFocus();
            inputMethodManager.hideSoftInputFromWindow(this.mDelegator.getWindowToken(), 0);
        } else if (inputMethodManager.isActive(this.mMinuteSpinnerInput)) {
            this.mMinuteSpinnerInput.clearFocus();
            inputMethodManager.hideSoftInputFromWindow(this.mDelegator.getWindowToken(), 0);
        } else if (inputMethodManager.isActive(this.mAmPmSpinnerInput)) {
            this.mAmPmSpinnerInput.clearFocus();
            inputMethodManager.hideSoftInputFromWindow(this.mDelegator.getWindowToken(), 0);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateAmPmControl() {
        if (is24Hour()) {
            NumberPicker numberPicker = this.mAmPmSpinner;
            if (numberPicker != null) {
                numberPicker.setVisibility(8);
            } else {
                this.mAmPmButton.setVisibility(8);
            }
        } else {
            int i = !this.mIsAm ? 1 : 0;
            NumberPicker numberPicker2 = this.mAmPmSpinner;
            if (numberPicker2 != null) {
                numberPicker2.setValue(i);
                this.mAmPmSpinner.setVisibility(0);
            } else {
                this.mAmPmButton.setText(this.mAmPmStrings[i]);
                this.mAmPmButton.setVisibility(0);
            }
        }
        this.mDelegator.sendAccessibilityEvent(4);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void onTimeChanged() {
        this.mDelegator.sendAccessibilityEvent(4);
        TimePicker.OnTimeChangedListener onTimeChangedListener = this.mOnTimeChangedListener;
        if (onTimeChangedListener != null) {
            onTimeChangedListener.onTimeChanged(this.mDelegator, getHour(), getMinute());
        }
        TimePicker.OnTimeChangedListener onTimeChangedListener2 = this.mAutoFillChangeListener;
        if (onTimeChangedListener2 != null) {
            onTimeChangedListener2.onTimeChanged(this.mDelegator, getHour(), getMinute());
        }
    }

    private void updateHourControl() {
        if (is24Hour()) {
            if (this.mHourFormat == 'k') {
                this.mHourSpinner.setMinValue(1);
                this.mHourSpinner.setMaxValue(24);
            } else {
                this.mHourSpinner.setMinValue(0);
                this.mHourSpinner.setMaxValue(23);
            }
        } else if (this.mHourFormat == 'K') {
            this.mHourSpinner.setMinValue(0);
            this.mHourSpinner.setMaxValue(11);
        } else {
            this.mHourSpinner.setMinValue(1);
            this.mHourSpinner.setMaxValue(12);
        }
        this.mHourSpinner.setFormatter(this.mHourWithTwoDigit ? NumberPicker.getTwoDigitFormatter() : null);
    }

    private void updateMinuteControl() {
        if (is24Hour()) {
            this.mMinuteSpinnerInput.setImeOptions(6);
        } else {
            this.mMinuteSpinnerInput.setImeOptions(5);
        }
    }
}
