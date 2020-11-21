package com.google.android.material.picker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.Parcelable;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ViewAnimator;
import com.google.android.material.R$id;
import com.google.android.material.R$layout;
import com.google.android.material.R$string;
import com.google.android.material.R$styleable;
import com.google.android.material.picker.DatePicker;
import com.google.android.material.picker.DayPickerView;
import com.google.android.material.picker.YearPickerView;
import com.google.android.material.picker.calendar.OneplusLunarCalendar;
import com.google.android.material.picker.calendar.OnepulsCalendarUtil;
import com.oneplus.common.OPFeaturesUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/* access modifiers changed from: package-private */
public class DatePickerCalendarDelegate extends DatePicker.AbstractDatePickerDelegate {
    private static final int[] ATTRS_TEXT_COLOR = {16842904};
    private static final AudioAttributes VIBRATION_ATTRIBUTES = new AudioAttributes.Builder().setContentType(4).setUsage(13).build();
    private ViewAnimator mAnimator;
    private ViewGroup mContainer;
    private int mCurrentView = -1;
    private DayPickerView mDayPickerView;
    private int mFirstDayOfWeek = 0;
    private TextView mHeaderLunarMonthDay;
    private TextView mHeaderMonthDay;
    private TextView mHeaderYear;
    private final Calendar mMaxDate;
    private final Calendar mMinDate;
    private SimpleDateFormat mMonthDayFormat;
    private final DayPickerView.OnDaySelectedListener mOnDaySelectedListener = new DayPickerView.OnDaySelectedListener() {
        /* class com.google.android.material.picker.DatePickerCalendarDelegate.AnonymousClass1 */

        @Override // com.google.android.material.picker.DayPickerView.OnDaySelectedListener
        public void onDaySelected(DayPickerView dayPickerView, Calendar calendar) {
            DatePickerCalendarDelegate.this.mCurrentDate.setTimeInMillis(calendar.getTimeInMillis());
            DatePickerCalendarDelegate.this.onDateChanged(true, true);
        }
    };
    private final View.OnClickListener mOnHeaderClickListener = new View.OnClickListener() {
        /* class com.google.android.material.picker.DatePickerCalendarDelegate.AnonymousClass3 */

        public void onClick(View view) {
            DatePickerCalendarDelegate.this.tryVibrate();
            if (view.getId() == R$id.date_picker_header_year) {
                DatePickerCalendarDelegate.this.setCurrentView(1);
            } else if (view.getId() == R$id.date_picker_header_date) {
                DatePickerCalendarDelegate.this.setCurrentView(0);
            }
        }
    };
    private final YearPickerView.OnYearSelectedListener mOnYearSelectedListener = new YearPickerView.OnYearSelectedListener() {
        /* class com.google.android.material.picker.DatePickerCalendarDelegate.AnonymousClass2 */

        @Override // com.google.android.material.picker.YearPickerView.OnYearSelectedListener
        public void onYearChanged(YearPickerView yearPickerView, int i) {
            int i2 = DatePickerCalendarDelegate.this.mCurrentDate.get(5);
            int daysInMonth = DatePickerCalendarDelegate.getDaysInMonth(DatePickerCalendarDelegate.this.mCurrentDate.get(2), i);
            if (i2 > daysInMonth) {
                DatePickerCalendarDelegate.this.mCurrentDate.set(5, daysInMonth);
            }
            DatePickerCalendarDelegate.this.mCurrentDate.set(1, i);
            DatePickerCalendarDelegate.this.onDateChanged(true, true);
            DatePickerCalendarDelegate.this.setCurrentView(0);
            DatePickerCalendarDelegate.this.mHeaderYear.requestFocus();
        }
    };
    private String mSelectDay;
    private String mSelectYear;
    private final Calendar mTempDate;
    private SimpleDateFormat mYearFormat;
    private YearPickerView mYearPickerView;

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public boolean getCalendarViewShown() {
        return false;
    }

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public boolean getSpinnersShown() {
        return false;
    }

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public void setCalendarViewShown(boolean z) {
    }

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public void setSpinnersShown(boolean z) {
    }

    public DatePickerCalendarDelegate(DatePicker datePicker, Context context, AttributeSet attributeSet, int i, int i2) {
        super(datePicker, context);
        Locale locale = this.mCurrentLocale;
        this.mCurrentDate = Calendar.getInstance(locale);
        this.mTempDate = Calendar.getInstance(locale);
        this.mMinDate = Calendar.getInstance(locale);
        this.mMaxDate = Calendar.getInstance(locale);
        this.mMinDate.set(1900, 0, 1);
        this.mMaxDate.set(2100, 11, 31);
        Resources resources = this.mDelegator.getResources();
        TypedArray obtainStyledAttributes = this.mContext.obtainStyledAttributes(attributeSet, R$styleable.DatePicker, i, i2);
        ViewGroup viewGroup = (ViewGroup) ((LayoutInflater) this.mContext.getSystemService("layout_inflater")).inflate(obtainStyledAttributes.getResourceId(R$styleable.DatePicker_internalLayout, R$layout.op_date_picker_material), (ViewGroup) this.mDelegator, false);
        this.mContainer = viewGroup;
        viewGroup.setSaveFromParentEnabled(false);
        this.mDelegator.addView(this.mContainer);
        ViewGroup viewGroup2 = (ViewGroup) this.mContainer.findViewById(R$id.date_picker_header);
        TextView textView = (TextView) viewGroup2.findViewById(R$id.date_picker_header_year);
        this.mHeaderYear = textView;
        textView.setOnClickListener(this.mOnHeaderClickListener);
        TextView textView2 = (TextView) viewGroup2.findViewById(R$id.date_picker_header_date);
        this.mHeaderMonthDay = textView2;
        textView2.setOnClickListener(this.mOnHeaderClickListener);
        this.mHeaderLunarMonthDay = (TextView) viewGroup2.findViewById(R$id.date_picker_header_lunar);
        int resourceId = obtainStyledAttributes.getResourceId(R$styleable.DatePicker_android_headerMonthTextAppearance, 0);
        if (resourceId != 0) {
            TypedArray obtainStyledAttributes2 = this.mContext.obtainStyledAttributes(null, ATTRS_TEXT_COLOR, 0, resourceId);
            obtainStyledAttributes2.getColorStateList(0);
            obtainStyledAttributes2.recycle();
        }
        ColorStateList colorStateList = obtainStyledAttributes.getColorStateList(R$styleable.DatePicker_headerTextColor);
        if (colorStateList != null) {
            this.mHeaderYear.setTextColor(colorStateList);
            this.mHeaderMonthDay.setTextColor(colorStateList);
        }
        obtainStyledAttributes.recycle();
        ViewAnimator viewAnimator = (ViewAnimator) this.mContainer.findViewById(R$id.animator);
        this.mAnimator = viewAnimator;
        DayPickerView dayPickerView = (DayPickerView) viewAnimator.findViewById(R$id.date_picker_day_picker);
        this.mDayPickerView = dayPickerView;
        dayPickerView.setFirstDayOfWeek(this.mFirstDayOfWeek);
        this.mDayPickerView.setMinDate(this.mMinDate.getTimeInMillis());
        this.mDayPickerView.setMaxDate(this.mMaxDate.getTimeInMillis());
        this.mDayPickerView.setDate(this.mCurrentDate.getTimeInMillis());
        this.mDayPickerView.setOnDaySelectedListener(this.mOnDaySelectedListener);
        YearPickerView yearPickerView = (YearPickerView) this.mAnimator.findViewById(R$id.date_picker_year_picker);
        this.mYearPickerView = yearPickerView;
        yearPickerView.setRange(this.mMinDate, this.mMaxDate);
        this.mYearPickerView.setYear(this.mCurrentDate.get(1));
        this.mYearPickerView.setOnYearSelectedListener(this.mOnYearSelectedListener);
        this.mSelectDay = resources.getString(R$string.select_day);
        this.mSelectYear = resources.getString(R$string.select_year);
        onLocaleChanged(this.mCurrentLocale);
        setCurrentView(0);
    }

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public boolean isYearPickerIsShow() {
        return this.mCurrentView == 1;
    }

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public void setCurrentYear() {
        this.mYearPickerView.setCurrentYear();
    }

    /* access modifiers changed from: protected */
    @Override // com.google.android.material.picker.DatePicker.AbstractDatePickerDelegate
    public void onLocaleChanged(Locale locale) {
        if (this.mHeaderYear != null) {
            this.mMonthDayFormat = new SimpleDateFormat(DateFormat.getBestDateTimePattern(locale, "EMMMd"), locale);
            this.mYearFormat = new SimpleDateFormat("y", locale);
            onCurrentDateChanged(false);
        }
    }

    private void onCurrentDateChanged(boolean z) {
        if (this.mHeaderYear != null) {
            this.mHeaderYear.setText(this.mYearFormat.format(this.mCurrentDate.getTime()));
            this.mHeaderMonthDay.setText(this.mMonthDayFormat.format(this.mCurrentDate.getTime()));
            updateLunarDate();
            if (z) {
                this.mAnimator.announceForAccessibility(getFormattedCurrentDate());
            }
        }
    }

    private void updateLunarDate() {
        String str = Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry();
        if (str == null || !str.contains("zh")) {
            this.mHeaderLunarMonthDay.setVisibility(8);
            return;
        }
        OneplusLunarCalendar solarToLunar = OnepulsCalendarUtil.solarToLunar(this.mCurrentDate);
        boolean equals = "zh_CN".equals(str);
        TextView textView = this.mHeaderLunarMonthDay;
        StringBuilder sb = new StringBuilder();
        sb.append(equals ? "农历：" : "農曆：");
        sb.append(solarToLunar.getYYMMDD());
        textView.setText(sb.toString());
        this.mHeaderLunarMonthDay.setVisibility(0);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setCurrentView(int i) {
        if (i == 0) {
            this.mDayPickerView.setDate(this.mCurrentDate.getTimeInMillis());
            if (this.mCurrentView != i) {
                this.mHeaderMonthDay.setActivated(true);
                this.mHeaderMonthDay.getPaint().setFakeBoldText(true);
                this.mHeaderYear.setActivated(false);
                this.mHeaderYear.getPaint().setFakeBoldText(false);
                this.mAnimator.setDisplayedChild(0);
                this.mCurrentView = i;
            }
            this.mAnimator.announceForAccessibility(this.mSelectDay);
        } else if (i == 1) {
            changeYearLayoutParams();
            this.mYearPickerView.setYear(this.mCurrentDate.get(1));
            this.mYearPickerView.post(new Runnable() {
                /* class com.google.android.material.picker.DatePickerCalendarDelegate.AnonymousClass4 */

                public void run() {
                    DatePickerCalendarDelegate.this.mYearPickerView.requestFocus();
                    DatePickerCalendarDelegate.this.mYearPickerView.clearFocus();
                }
            });
            if (this.mCurrentView != i) {
                this.mHeaderMonthDay.setActivated(false);
                this.mHeaderMonthDay.getPaint().setFakeBoldText(false);
                this.mHeaderYear.setActivated(true);
                this.mHeaderYear.getPaint().setFakeBoldText(true);
                this.mAnimator.setDisplayedChild(1);
                this.mCurrentView = i;
            }
            this.mAnimator.announceForAccessibility(this.mSelectYear);
        }
    }

    public void changeYearLayoutParams() {
        if (this.mCurrentView == 1) {
            this.mYearPickerView.setLayoutParams(new FrameLayout.LayoutParams(-1, this.mContext.getResources().getConfiguration().orientation == 2 ? -2 : -1));
        }
    }

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public void init(int i, int i2, int i3, DatePicker.OnDateChangedListener onDateChangedListener) {
        this.mCurrentDate.set(1, i);
        this.mCurrentDate.set(2, i2);
        this.mCurrentDate.set(5, i3);
        onDateChanged(false, false);
        this.mOnDateChangedListener = onDateChangedListener;
    }

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public void updateDate(int i, int i2, int i3) {
        this.mCurrentDate.set(1, i);
        this.mCurrentDate.set(2, i2);
        this.mCurrentDate.set(5, i3);
        onDateChanged(false, true);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void onDateChanged(boolean z, boolean z2) {
        int i = this.mCurrentDate.get(1);
        if (z2 && !(this.mOnDateChangedListener == null && this.mAutoFillChangeListener == null)) {
            int i2 = this.mCurrentDate.get(2);
            int i3 = this.mCurrentDate.get(5);
            DatePicker.OnDateChangedListener onDateChangedListener = this.mOnDateChangedListener;
            if (onDateChangedListener != null) {
                onDateChangedListener.onDateChanged(this.mDelegator, i, i2, i3);
            }
            DatePicker.OnDateChangedListener onDateChangedListener2 = this.mAutoFillChangeListener;
            if (onDateChangedListener2 != null) {
                onDateChangedListener2.onDateChanged(this.mDelegator, i, i2, i3);
            }
        }
        this.mDayPickerView.setDate(this.mCurrentDate.getTimeInMillis());
        this.mYearPickerView.setYear(i);
        onCurrentDateChanged(z);
        if (z) {
            tryVibrate();
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
    public void setMinDate(long j) {
        this.mTempDate.setTimeInMillis(j);
        if (this.mTempDate.get(1) != this.mMinDate.get(1) || this.mTempDate.get(6) != this.mMinDate.get(6)) {
            if (this.mCurrentDate.before(this.mTempDate)) {
                this.mCurrentDate.setTimeInMillis(j);
                onDateChanged(false, true);
            }
            this.mMinDate.setTimeInMillis(j);
            this.mDayPickerView.setMinDate(j);
            this.mYearPickerView.setRange(this.mMinDate, this.mMaxDate);
        }
    }

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public Calendar getMinDate() {
        return this.mMinDate;
    }

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public void setMaxDate(long j) {
        this.mTempDate.setTimeInMillis(j);
        if (this.mTempDate.get(1) != this.mMaxDate.get(1) || this.mTempDate.get(6) != this.mMaxDate.get(6)) {
            if (this.mCurrentDate.after(this.mTempDate)) {
                this.mCurrentDate.setTimeInMillis(j);
                onDateChanged(false, true);
            }
            this.mMaxDate.setTimeInMillis(j);
            this.mDayPickerView.setMaxDate(j);
            this.mYearPickerView.setRange(this.mMinDate, this.mMaxDate);
        }
    }

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public Calendar getMaxDate() {
        return this.mMaxDate;
    }

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public void setFirstDayOfWeek(int i) {
        this.mFirstDayOfWeek = i;
        this.mDayPickerView.setFirstDayOfWeek(i);
    }

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public int getFirstDayOfWeek() {
        int i = this.mFirstDayOfWeek;
        if (i != 0) {
            return i;
        }
        return this.mCurrentDate.getFirstDayOfWeek();
    }

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public void setEnabled(boolean z) {
        this.mContainer.setEnabled(z);
        this.mDayPickerView.setEnabled(z);
        this.mYearPickerView.setEnabled(z);
        this.mHeaderYear.setEnabled(z);
        this.mHeaderMonthDay.setEnabled(z);
    }

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public boolean isEnabled() {
        return this.mContainer.isEnabled();
    }

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public CalendarView getCalendarView() {
        throw new UnsupportedOperationException("Not supported by calendar-mode DatePicker");
    }

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public void onConfigurationChanged(Configuration configuration) {
        setCurrentLocale(configuration.locale);
    }

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public Parcelable onSaveInstanceState(Parcelable parcelable) {
        return new DatePicker.AbstractDatePickerDelegate.SavedState(parcelable, this.mCurrentDate.get(1), this.mCurrentDate.get(2), this.mCurrentDate.get(5), this.mMinDate.getTimeInMillis(), this.mMaxDate.getTimeInMillis(), this.mCurrentView, this.mCurrentView == 0 ? this.mDayPickerView.getMostVisiblePosition() : -1, -1);
    }

    @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
    public void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable instanceof DatePicker.AbstractDatePickerDelegate.SavedState) {
            DatePicker.AbstractDatePickerDelegate.SavedState savedState = (DatePicker.AbstractDatePickerDelegate.SavedState) parcelable;
            this.mCurrentDate.set(savedState.getSelectedYear(), savedState.getSelectedMonth(), savedState.getSelectedDay());
            this.mMinDate.setTimeInMillis(savedState.getMinDate());
            this.mMaxDate.setTimeInMillis(savedState.getMaxDate());
            onCurrentDateChanged(false);
            int currentView = savedState.getCurrentView();
            setCurrentView(currentView);
            int listPosition = savedState.getListPosition();
            if (listPosition == -1) {
                return;
            }
            if (currentView == 0) {
                this.mDayPickerView.setPosition(listPosition);
            } else if (currentView == 1) {
                savedState.getListPositionOffset();
            }
        }
    }

    public static int getDaysInMonth(int i, int i2) {
        switch (i) {
            case 0:
            case 2:
            case 4:
            case 6:
            case 7:
            case 9:
            case 11:
                return 31;
            case 1:
                return i2 % 4 == 0 ? 29 : 28;
            case 3:
            case 5:
            case 8:
            case 10:
                return 30;
            default:
                throw new IllegalArgumentException("Invalid Month");
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    @SuppressLint({"MissingPermission"})
    private void tryVibrate() {
        if (!OPFeaturesUtils.isSupportZVibrate() || Build.VERSION.SDK_INT <= 26) {
            this.mDelegator.performHapticFeedback(5);
            return;
        }
        try {
            Field declaredField = VibrationEffect.class.getDeclaredField("EFFECT_CLICK");
            Method declaredMethod = VibrationEffect.class.getDeclaredMethod("get", Integer.TYPE);
            declaredMethod.setAccessible(true);
            declaredField.setAccessible(true);
            Object[] objArr = {Integer.valueOf(declaredField.getInt(null))};
            ((Vibrator) this.mContext.getSystemService("vibrator")).vibrate((VibrationEffect) declaredMethod.invoke(null, objArr), VIBRATION_ATTRIBUTES);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
