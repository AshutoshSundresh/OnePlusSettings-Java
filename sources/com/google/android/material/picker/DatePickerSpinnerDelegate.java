package com.google.android.material.picker;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.R$id;
import com.google.android.material.R$layout;
import com.google.android.material.R$styleable;
import com.google.android.material.picker.DatePicker;
import com.google.android.material.picker.NumberPicker;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

/* access modifiers changed from: package-private */
public class DatePickerSpinnerDelegate extends DatePicker.AbstractDatePickerDelegate {
    private final CalendarView mCalendarView;
    private final DateFormat mDateFormat = new SimpleDateFormat("MM/dd/yyyy");
    private final NumberPicker mDaySpinner;
    private final EditText mDaySpinnerInput;
    private boolean mIsEnabled = true;
    private Calendar mMaxDate;
    private Calendar mMinDate;
    private final NumberPicker mMonthSpinner;
    private final EditText mMonthSpinnerInput;
    private int mNumberOfMonths;
    private String[] mShortMonths;
    private final LinearLayout mSpinners;
    private Calendar mTempDate;
    private final NumberPicker mYearSpinner;
    private final EditText mYearSpinnerInput;

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public boolean isYearPickerIsShow() {
        return false;
    }

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public void setCurrentYear() {
    }

    DatePickerSpinnerDelegate(DatePicker datePicker, Context context, AttributeSet attributeSet, int i, int i2) {
        super(datePicker, context);
        this.mDelegator = datePicker;
        this.mContext = context;
        setCurrentLocale(Locale.getDefault());
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.DatePicker, i, i2);
        boolean z = obtainStyledAttributes.getBoolean(R$styleable.DatePicker_android_spinnersShown, true);
        boolean z2 = obtainStyledAttributes.getBoolean(R$styleable.DatePicker_android_calendarViewShown, true);
        int i3 = obtainStyledAttributes.getInt(R$styleable.DatePicker_android_startYear, 1900);
        int i4 = obtainStyledAttributes.getInt(R$styleable.DatePicker_android_endYear, 2100);
        String string = obtainStyledAttributes.getString(R$styleable.DatePicker_android_minDate);
        String string2 = obtainStyledAttributes.getString(R$styleable.DatePicker_android_maxDate);
        int resourceId = obtainStyledAttributes.getResourceId(R$styleable.DatePicker_legacyLayout, R$layout.op_date_picker_legacy);
        obtainStyledAttributes.recycle();
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(resourceId, (ViewGroup) this.mDelegator, true).setSaveFromParentEnabled(false);
        AnonymousClass1 r10 = new NumberPicker.OnValueChangeListener() {
            /* class com.google.android.material.picker.DatePickerSpinnerDelegate.AnonymousClass1 */

            @Override // com.google.android.material.picker.NumberPicker.OnValueChangeListener
            public void onValueChange(NumberPicker numberPicker, int i, int i2) {
                DatePickerSpinnerDelegate.this.updateInputState();
                DatePickerSpinnerDelegate.this.mTempDate.setTimeInMillis(DatePickerSpinnerDelegate.this.mCurrentDate.getTimeInMillis());
                if (numberPicker == DatePickerSpinnerDelegate.this.mDaySpinner) {
                    int actualMaximum = DatePickerSpinnerDelegate.this.mTempDate.getActualMaximum(5);
                    if (i == actualMaximum && i2 == 1) {
                        DatePickerSpinnerDelegate.this.mTempDate.add(5, 1);
                    } else if (i == 1 && i2 == actualMaximum) {
                        DatePickerSpinnerDelegate.this.mTempDate.add(5, -1);
                    } else {
                        DatePickerSpinnerDelegate.this.mTempDate.add(5, i2 - i);
                    }
                } else if (numberPicker == DatePickerSpinnerDelegate.this.mMonthSpinner) {
                    if (i == 11 && i2 == 0) {
                        DatePickerSpinnerDelegate.this.mTempDate.add(2, 1);
                    } else if (i == 0 && i2 == 11) {
                        DatePickerSpinnerDelegate.this.mTempDate.add(2, -1);
                    } else {
                        DatePickerSpinnerDelegate.this.mTempDate.add(2, i2 - i);
                    }
                } else if (numberPicker == DatePickerSpinnerDelegate.this.mYearSpinner) {
                    DatePickerSpinnerDelegate.this.mTempDate.set(1, i2);
                } else {
                    throw new IllegalArgumentException();
                }
                DatePickerSpinnerDelegate datePickerSpinnerDelegate = DatePickerSpinnerDelegate.this;
                datePickerSpinnerDelegate.setDate(datePickerSpinnerDelegate.mTempDate.get(1), DatePickerSpinnerDelegate.this.mTempDate.get(2), DatePickerSpinnerDelegate.this.mTempDate.get(5));
                DatePickerSpinnerDelegate.this.updateSpinners();
                DatePickerSpinnerDelegate.this.updateCalendarView();
                DatePickerSpinnerDelegate.this.notifyDateChanged();
            }
        };
        this.mSpinners = (LinearLayout) this.mDelegator.findViewById(R$id.pickers);
        CalendarView calendarView = (CalendarView) this.mDelegator.findViewById(R$id.calendar_view);
        this.mCalendarView = calendarView;
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            /* class com.google.android.material.picker.DatePickerSpinnerDelegate.AnonymousClass2 */

            public void onSelectedDayChange(CalendarView calendarView, int i, int i2, int i3) {
                DatePickerSpinnerDelegate.this.setDate(i, i2, i3);
                DatePickerSpinnerDelegate.this.updateSpinners();
                DatePickerSpinnerDelegate.this.notifyDateChanged();
            }
        });
        NumberPicker numberPicker = (NumberPicker) this.mDelegator.findViewById(R$id.day);
        this.mDaySpinner = numberPicker;
        numberPicker.setFormatter(NumberPicker.getTwoDigitFormatter());
        this.mDaySpinner.setOnLongPressUpdateInterval(100);
        this.mDaySpinner.setOnValueChangedListener(r10);
        this.mDaySpinnerInput = (EditText) this.mDaySpinner.findViewById(R$id.numberpicker_input);
        NumberPicker numberPicker2 = (NumberPicker) this.mDelegator.findViewById(R$id.month);
        this.mMonthSpinner = numberPicker2;
        numberPicker2.setMinValue(0);
        this.mMonthSpinner.setMaxValue(this.mNumberOfMonths - 1);
        this.mMonthSpinner.setDisplayedValues(this.mShortMonths);
        this.mMonthSpinner.setOnLongPressUpdateInterval(200);
        this.mMonthSpinner.setOnValueChangedListener(r10);
        this.mMonthSpinnerInput = (EditText) this.mMonthSpinner.findViewById(R$id.numberpicker_input);
        NumberPicker numberPicker3 = (NumberPicker) this.mDelegator.findViewById(R$id.year);
        this.mYearSpinner = numberPicker3;
        numberPicker3.setOnLongPressUpdateInterval(100);
        this.mYearSpinner.setOnValueChangedListener(r10);
        this.mYearSpinnerInput = (EditText) this.mYearSpinner.findViewById(R$id.numberpicker_input);
        if (z || z2) {
            setSpinnersShown(z);
            setCalendarViewShown(z2);
        } else {
            setSpinnersShown(true);
        }
        this.mTempDate.clear();
        if (TextUtils.isEmpty(string)) {
            this.mTempDate.set(i3, 0, 1);
        } else if (!parseDate(string, this.mTempDate)) {
            this.mTempDate.set(i3, 0, 1);
        }
        setMinDate(this.mTempDate.getTimeInMillis());
        this.mTempDate.clear();
        if (TextUtils.isEmpty(string2)) {
            this.mTempDate.set(i4, 11, 31);
        } else if (!parseDate(string2, this.mTempDate)) {
            this.mTempDate.set(i4, 11, 31);
        }
        setMaxDate(this.mTempDate.getTimeInMillis());
        this.mCurrentDate.setTimeInMillis(System.currentTimeMillis());
        init(this.mCurrentDate.get(1), this.mCurrentDate.get(2), this.mCurrentDate.get(5), null);
        reorderSpinners();
        if (this.mDelegator.getImportantForAccessibility() == 0) {
            this.mDelegator.setImportantForAccessibility(1);
        }
    }

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public void init(int i, int i2, int i3, DatePicker.OnDateChangedListener onDateChangedListener) {
        setDate(i, i2, i3);
        updateSpinners();
        updateCalendarView();
        this.mOnDateChangedListener = onDateChangedListener;
    }

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public void updateDate(int i, int i2, int i3) {
        if (isNewDate(i, i2, i3)) {
            setDate(i, i2, i3);
            updateSpinners();
            updateCalendarView();
            notifyDateChanged();
        }
    }

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public int getYear() {
        return this.mCurrentDate.get(1);
    }

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public int getMonth() {
        return this.mCurrentDate.get(2);
    }

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public int getDayOfMonth() {
        return this.mCurrentDate.get(5);
    }

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public void setFirstDayOfWeek(int i) {
        this.mCalendarView.setFirstDayOfWeek(i);
    }

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public int getFirstDayOfWeek() {
        return this.mCalendarView.getFirstDayOfWeek();
    }

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public void setMinDate(long j) {
        this.mTempDate.setTimeInMillis(j);
        if (this.mTempDate.get(1) != this.mMinDate.get(1) || this.mTempDate.get(6) != this.mMinDate.get(6)) {
            this.mMinDate.setTimeInMillis(j);
            this.mCalendarView.setMinDate(j);
            if (this.mCurrentDate.before(this.mMinDate)) {
                this.mCurrentDate.setTimeInMillis(this.mMinDate.getTimeInMillis());
                updateCalendarView();
            }
            updateSpinners();
        }
    }

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public Calendar getMinDate() {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(this.mCalendarView.getMinDate());
        return instance;
    }

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public void setMaxDate(long j) {
        this.mTempDate.setTimeInMillis(j);
        if (this.mTempDate.get(1) != this.mMaxDate.get(1) || this.mTempDate.get(6) != this.mMaxDate.get(6)) {
            this.mMaxDate.setTimeInMillis(j);
            this.mCalendarView.setMaxDate(j);
            if (this.mCurrentDate.after(this.mMaxDate)) {
                this.mCurrentDate.setTimeInMillis(this.mMaxDate.getTimeInMillis());
                updateCalendarView();
            }
            updateSpinners();
        }
    }

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public Calendar getMaxDate() {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(this.mCalendarView.getMaxDate());
        return instance;
    }

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public void setEnabled(boolean z) {
        this.mDaySpinner.setEnabled(z);
        this.mMonthSpinner.setEnabled(z);
        this.mYearSpinner.setEnabled(z);
        this.mCalendarView.setEnabled(z);
        this.mIsEnabled = z;
    }

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public boolean isEnabled() {
        return this.mIsEnabled;
    }

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public CalendarView getCalendarView() {
        return this.mCalendarView;
    }

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public void setCalendarViewShown(boolean z) {
        this.mCalendarView.setVisibility(z ? 0 : 8);
    }

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public boolean getCalendarViewShown() {
        return this.mCalendarView.getVisibility() == 0;
    }

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public void setSpinnersShown(boolean z) {
        this.mSpinners.setVisibility(z ? 0 : 8);
    }

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public boolean getSpinnersShown() {
        return this.mSpinners.isShown();
    }

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public void onConfigurationChanged(Configuration configuration) {
        setCurrentLocale(configuration.locale);
    }

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public Parcelable onSaveInstanceState(Parcelable parcelable) {
        return new DatePicker.AbstractDatePickerDelegate.SavedState(parcelable, getYear(), getMonth(), getDayOfMonth(), getMinDate().getTimeInMillis(), getMaxDate().getTimeInMillis());
    }

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable instanceof DatePicker.AbstractDatePickerDelegate.SavedState) {
            DatePicker.AbstractDatePickerDelegate.SavedState savedState = (DatePicker.AbstractDatePickerDelegate.SavedState) parcelable;
            setDate(savedState.getSelectedYear(), savedState.getSelectedMonth(), savedState.getSelectedDay());
            updateSpinners();
            updateCalendarView();
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.google.android.material.picker.DatePicker.AbstractDatePickerDelegate
    public void setCurrentLocale(Locale locale) {
        super.setCurrentLocale(locale);
        this.mTempDate = getCalendarForLocale(this.mTempDate, locale);
        this.mMinDate = getCalendarForLocale(this.mMinDate, locale);
        this.mMaxDate = getCalendarForLocale(this.mMaxDate, locale);
        this.mCurrentDate = getCalendarForLocale(this.mCurrentDate, locale);
        this.mNumberOfMonths = this.mTempDate.getActualMaximum(2) + 1;
        this.mShortMonths = new DateFormatSymbols().getShortMonths();
        if (usingNumericMonths()) {
            this.mShortMonths = new String[this.mNumberOfMonths];
            int i = 0;
            while (i < this.mNumberOfMonths) {
                int i2 = i + 1;
                this.mShortMonths[i] = String.format("%d", Integer.valueOf(i2));
                i = i2;
            }
        }
    }

    private boolean usingNumericMonths() {
        return Character.isDigit(this.mShortMonths[0].charAt(0));
    }

    private Calendar getCalendarForLocale(Calendar calendar, Locale locale) {
        if (calendar == null) {
            return Calendar.getInstance(locale);
        }
        long timeInMillis = calendar.getTimeInMillis();
        Calendar instance = Calendar.getInstance(locale);
        instance.setTimeInMillis(timeInMillis);
        return instance;
    }

    private void reorderSpinners() {
        this.mSpinners.removeAllViews();
        char[] dateFormatOrder = getDateFormatOrder(android.text.format.DateFormat.getBestDateTimePattern(Locale.getDefault(), "yyyyMMMdd"));
        int length = dateFormatOrder.length;
        for (int i = 0; i < length; i++) {
            char c = dateFormatOrder[i];
            if (c == 'M') {
                this.mSpinners.addView(this.mMonthSpinner);
                setImeOptions(this.mMonthSpinner, length, i);
            } else if (c == 'd') {
                this.mSpinners.addView(this.mDaySpinner);
                setImeOptions(this.mDaySpinner, length, i);
            } else if (c == 'y') {
                this.mSpinners.addView(this.mYearSpinner);
                setImeOptions(this.mYearSpinner, length, i);
            } else {
                throw new IllegalArgumentException(Arrays.toString(dateFormatOrder));
            }
        }
    }

    public char[] getDateFormatOrder(String str) {
        char[] cArr = new char[3];
        int i = 0;
        int i2 = 0;
        boolean z = false;
        boolean z2 = false;
        boolean z3 = false;
        while (i < str.length()) {
            char charAt = str.charAt(i);
            if (charAt == 'd' || charAt == 'L' || charAt == 'M' || charAt == 'y') {
                if (charAt == 'd' && !z) {
                    cArr[i2] = 'd';
                    i2++;
                    z = true;
                } else if ((charAt == 'L' || charAt == 'M') && !z2) {
                    cArr[i2] = 'M';
                    i2++;
                    z2 = true;
                } else if (charAt == 'y' && !z3) {
                    cArr[i2] = 'y';
                    i2++;
                    z3 = true;
                }
            } else if (charAt == 'G') {
                continue;
            } else if ((charAt >= 'a' && charAt <= 'z') || (charAt >= 'A' && charAt <= 'Z')) {
                throw new IllegalArgumentException("Bad pattern character '" + charAt + "' in " + str);
            } else if (charAt != '\'') {
                continue;
            } else {
                if (i < str.length() - 1) {
                    int i3 = i + 1;
                    if (str.charAt(i3) == '\'') {
                        i = i3;
                    }
                }
                int indexOf = str.indexOf(39, i + 1);
                if (indexOf != -1) {
                    i = indexOf + 1;
                } else {
                    throw new IllegalArgumentException("Bad quoting in " + str);
                }
            }
            i++;
        }
        return cArr;
    }

    private boolean parseDate(String str, Calendar calendar) {
        try {
            calendar.setTime(this.mDateFormat.parse(str));
            return true;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isNewDate(int i, int i2, int i3) {
        if (this.mCurrentDate.get(1) == i && this.mCurrentDate.get(2) == i2 && this.mCurrentDate.get(5) == i3) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setDate(int i, int i2, int i3) {
        this.mCurrentDate.set(i, i2, i3);
        if (this.mCurrentDate.before(this.mMinDate)) {
            this.mCurrentDate.setTimeInMillis(this.mMinDate.getTimeInMillis());
        } else if (this.mCurrentDate.after(this.mMaxDate)) {
            this.mCurrentDate.setTimeInMillis(this.mMaxDate.getTimeInMillis());
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateSpinners() {
        if (this.mCurrentDate.equals(this.mMinDate)) {
            this.mDaySpinner.setMinValue(this.mCurrentDate.get(5));
            this.mDaySpinner.setMaxValue(this.mCurrentDate.getActualMaximum(5));
            this.mDaySpinner.setWrapSelectorWheel(false);
            this.mMonthSpinner.setDisplayedValues(null);
            this.mMonthSpinner.setMinValue(this.mCurrentDate.get(2));
            this.mMonthSpinner.setMaxValue(this.mCurrentDate.getActualMaximum(2));
            this.mMonthSpinner.setWrapSelectorWheel(false);
        } else if (this.mCurrentDate.equals(this.mMaxDate)) {
            this.mDaySpinner.setMinValue(this.mCurrentDate.getActualMinimum(5));
            this.mDaySpinner.setMaxValue(this.mCurrentDate.get(5));
            this.mDaySpinner.setWrapSelectorWheel(false);
            this.mMonthSpinner.setDisplayedValues(null);
            this.mMonthSpinner.setMinValue(this.mCurrentDate.getActualMinimum(2));
            this.mMonthSpinner.setMaxValue(this.mCurrentDate.get(2));
            this.mMonthSpinner.setWrapSelectorWheel(false);
        } else {
            this.mDaySpinner.setMinValue(1);
            this.mDaySpinner.setMaxValue(this.mCurrentDate.getActualMaximum(5));
            this.mDaySpinner.setWrapSelectorWheel(true);
            this.mMonthSpinner.setDisplayedValues(null);
            this.mMonthSpinner.setMinValue(0);
            this.mMonthSpinner.setMaxValue(11);
            this.mMonthSpinner.setWrapSelectorWheel(true);
        }
        this.mMonthSpinner.setDisplayedValues((String[]) Arrays.copyOfRange(this.mShortMonths, this.mMonthSpinner.getMinValue(), this.mMonthSpinner.getMaxValue() + 1));
        this.mYearSpinner.setMinValue(this.mMinDate.get(1));
        this.mYearSpinner.setMaxValue(this.mMaxDate.get(1));
        this.mYearSpinner.setWrapSelectorWheel(false);
        this.mYearSpinner.setValue(this.mCurrentDate.get(1));
        this.mMonthSpinner.setValue(this.mCurrentDate.get(2));
        this.mDaySpinner.setValue(this.mCurrentDate.get(5));
        if (usingNumericMonths()) {
            this.mMonthSpinnerInput.setRawInputType(2);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateCalendarView() {
        this.mCalendarView.setDate(this.mCurrentDate.getTimeInMillis(), false, false);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void notifyDateChanged() {
        this.mDelegator.sendAccessibilityEvent(4);
        DatePicker.OnDateChangedListener onDateChangedListener = this.mOnDateChangedListener;
        if (onDateChangedListener != null) {
            onDateChangedListener.onDateChanged(this.mDelegator, getYear(), getMonth(), getDayOfMonth());
        }
        DatePicker.OnDateChangedListener onDateChangedListener2 = this.mAutoFillChangeListener;
        if (onDateChangedListener2 != null) {
            onDateChangedListener2.onDateChanged(this.mDelegator, getYear(), getMonth(), getDayOfMonth());
        }
    }

    private void setImeOptions(NumberPicker numberPicker, int i, int i2) {
        ((TextView) numberPicker.findViewById(R$id.numberpicker_input)).setImeOptions(i2 < i + -1 ? 5 : 6);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateInputState() {
        InputMethodManager inputMethodManager = (InputMethodManager) this.mContext.getSystemService("input_method");
        if (inputMethodManager == null) {
            return;
        }
        if (inputMethodManager.isActive(this.mYearSpinnerInput)) {
            this.mYearSpinnerInput.clearFocus();
            inputMethodManager.hideSoftInputFromWindow(this.mDelegator.getWindowToken(), 0);
        } else if (inputMethodManager.isActive(this.mMonthSpinnerInput)) {
            this.mMonthSpinnerInput.clearFocus();
            inputMethodManager.hideSoftInputFromWindow(this.mDelegator.getWindowToken(), 0);
        } else if (inputMethodManager.isActive(this.mDaySpinnerInput)) {
            this.mDaySpinnerInput.clearFocus();
            inputMethodManager.hideSoftInputFromWindow(this.mDelegator.getWindowToken(), 0);
        }
    }
}
