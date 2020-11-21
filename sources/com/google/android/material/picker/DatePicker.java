package com.google.android.material.picker;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.autofill.AutofillManager;
import android.view.autofill.AutofillValue;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import com.google.android.material.R$integer;
import com.google.android.material.R$styleable;
import java.util.Calendar;
import java.util.Locale;

public class DatePicker extends FrameLayout {
    private static final String LOG_TAG = DatePicker.class.getSimpleName();
    private final DatePickerDelegate mDelegate;
    private final int mMode;

    /* access modifiers changed from: package-private */
    public interface DatePickerDelegate {
        CalendarView getCalendarView();

        boolean getCalendarViewShown();

        long getDate();

        int getDayOfMonth();

        int getFirstDayOfWeek();

        Calendar getMaxDate();

        Calendar getMinDate();

        int getMonth();

        boolean getSpinnersShown();

        int getYear();

        void init(int i, int i2, int i3, OnDateChangedListener onDateChangedListener);

        boolean isEnabled();

        boolean isYearPickerIsShow();

        void onConfigurationChanged(Configuration configuration);

        void onRestoreInstanceState(Parcelable parcelable);

        Parcelable onSaveInstanceState(Parcelable parcelable);

        void setAutoFillChangeListener(OnDateChangedListener onDateChangedListener);

        void setCalendarViewShown(boolean z);

        void setCurrentYear();

        void setEnabled(boolean z);

        void setFirstDayOfWeek(int i);

        void setMaxDate(long j);

        void setMinDate(long j);

        void setOnDateChangedListener(OnDateChangedListener onDateChangedListener);

        void setSpinnersShown(boolean z);

        void setValidationCallback(ValidationCallback validationCallback);

        void updateDate(int i, int i2, int i3);

        void updateDate(long j);
    }

    public interface OnDateChangedListener {
        void onDateChanged(DatePicker datePicker, int i, int i2, int i3);
    }

    public interface ValidationCallback {
    }

    public DatePicker(Context context) {
        this(context, null);
    }

    public DatePicker(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 16843612);
    }

    public DatePicker(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public DatePicker(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.DatePicker, i, i2);
        boolean z = obtainStyledAttributes.getBoolean(R$styleable.DatePicker_dialogMode, false);
        int i3 = obtainStyledAttributes.getInt(R$styleable.DatePicker_android_datePickerMode, 1);
        int i4 = obtainStyledAttributes.getInt(R$styleable.DatePicker_android_firstDayOfWeek, 0);
        obtainStyledAttributes.recycle();
        if (i3 != 2 || !z) {
            this.mMode = i3;
        } else {
            this.mMode = context.getResources().getInteger(R$integer.date_picker_mode);
        }
        if (this.mMode != 2) {
            this.mDelegate = createSpinnerUIDelegate(context, attributeSet, i, i2);
        } else {
            this.mDelegate = createCalendarUIDelegate(context, attributeSet, i, i2);
        }
        if (i4 != 0) {
            setFirstDayOfWeek(i4);
        }
        this.mDelegate.setAutoFillChangeListener(new OnDateChangedListener() {
            /* class com.google.android.material.picker.DatePicker.AnonymousClass1 */

            @Override // com.google.android.material.picker.DatePicker.OnDateChangedListener
            public void onDateChanged(DatePicker datePicker, int i, int i2, int i3) {
                if (Build.VERSION.SDK_INT >= 26) {
                    ((AutofillManager) DatePicker.this.getContext().getSystemService(AutofillManager.class)).notifyValueChanged(DatePicker.this);
                }
            }
        });
    }

    private DatePickerDelegate createSpinnerUIDelegate(Context context, AttributeSet attributeSet, int i, int i2) {
        return new DatePickerSpinnerDelegate(this, context, attributeSet, i, i2);
    }

    private DatePickerDelegate createCalendarUIDelegate(Context context, AttributeSet attributeSet, int i, int i2) {
        return new DatePickerCalendarDelegate(this, context, attributeSet, i, i2);
    }

    public int getMode() {
        return this.mMode;
    }

    public void init(int i, int i2, int i3, OnDateChangedListener onDateChangedListener) {
        this.mDelegate.init(i, i2, i3, onDateChangedListener);
    }

    public void setOnDateChangedListener(OnDateChangedListener onDateChangedListener) {
        this.mDelegate.setOnDateChangedListener(onDateChangedListener);
    }

    public int getYear() {
        return this.mDelegate.getYear();
    }

    public int getMonth() {
        return this.mDelegate.getMonth();
    }

    public int getDayOfMonth() {
        return this.mDelegate.getDayOfMonth();
    }

    public long getMinDate() {
        return this.mDelegate.getMinDate().getTimeInMillis();
    }

    public void setMinDate(long j) {
        this.mDelegate.setMinDate(j);
    }

    public long getMaxDate() {
        return this.mDelegate.getMaxDate().getTimeInMillis();
    }

    public void setMaxDate(long j) {
        this.mDelegate.setMaxDate(j);
    }

    public void setValidationCallback(ValidationCallback validationCallback) {
        this.mDelegate.setValidationCallback(validationCallback);
    }

    public void setEnabled(boolean z) {
        if (this.mDelegate.isEnabled() != z) {
            super.setEnabled(z);
            this.mDelegate.setEnabled(z);
        }
    }

    public boolean isEnabled() {
        return this.mDelegate.isEnabled();
    }

    public boolean isYearPickerShowing() {
        return this.mDelegate.isYearPickerIsShow();
    }

    public void setCurrentYear() {
        this.mDelegate.setCurrentYear();
    }

    public CharSequence getAccessibilityClassName() {
        return DatePicker.class.getName();
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mDelegate.onConfigurationChanged(configuration);
    }

    public void setFirstDayOfWeek(int i) {
        if (i < 1 || i > 7) {
            throw new IllegalArgumentException("firstDayOfWeek must be between 1 and 7");
        }
        this.mDelegate.setFirstDayOfWeek(i);
    }

    public int getFirstDayOfWeek() {
        return this.mDelegate.getFirstDayOfWeek();
    }

    @Deprecated
    public boolean getCalendarViewShown() {
        return this.mDelegate.getCalendarViewShown();
    }

    @Deprecated
    public CalendarView getCalendarView() {
        return this.mDelegate.getCalendarView();
    }

    @Deprecated
    public void setCalendarViewShown(boolean z) {
        this.mDelegate.setCalendarViewShown(z);
    }

    @Deprecated
    public boolean getSpinnersShown() {
        return this.mDelegate.getSpinnersShown();
    }

    @Deprecated
    public void setSpinnersShown(boolean z) {
        this.mDelegate.setSpinnersShown(z);
    }

    /* access modifiers changed from: protected */
    @Override // android.view.View, android.view.ViewGroup
    public void dispatchRestoreInstanceState(SparseArray<Parcelable> sparseArray) {
        dispatchThawSelfOnly(sparseArray);
    }

    /* access modifiers changed from: protected */
    public Parcelable onSaveInstanceState() {
        return this.mDelegate.onSaveInstanceState(super.onSaveInstanceState());
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        DatePickerDelegate datePickerDelegate = this.mDelegate;
        if (datePickerDelegate instanceof DatePickerCalendarDelegate) {
            ((DatePickerCalendarDelegate) datePickerDelegate).changeYearLayoutParams();
        }
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Parcelable parcelable) {
        View.BaseSavedState baseSavedState = (View.BaseSavedState) parcelable;
        super.onRestoreInstanceState(baseSavedState.getSuperState());
        this.mDelegate.onRestoreInstanceState(baseSavedState);
    }

    /* access modifiers changed from: package-private */
    public static abstract class AbstractDatePickerDelegate implements DatePickerDelegate {
        protected OnDateChangedListener mAutoFillChangeListener;
        protected Context mContext;
        protected Calendar mCurrentDate;
        protected Locale mCurrentLocale;
        protected DatePicker mDelegator;
        protected OnDateChangedListener mOnDateChangedListener;

        /* access modifiers changed from: protected */
        public void onLocaleChanged(Locale locale) {
        }

        @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
        public void setValidationCallback(ValidationCallback validationCallback) {
        }

        public AbstractDatePickerDelegate(DatePicker datePicker, Context context) {
            this.mDelegator = datePicker;
            this.mContext = context;
            setCurrentLocale(Locale.getDefault());
        }

        /* access modifiers changed from: protected */
        public void setCurrentLocale(Locale locale) {
            if (!locale.equals(this.mCurrentLocale)) {
                this.mCurrentLocale = locale;
                onLocaleChanged(locale);
            }
        }

        @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
        public void setOnDateChangedListener(OnDateChangedListener onDateChangedListener) {
            this.mOnDateChangedListener = onDateChangedListener;
        }

        @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
        public void setAutoFillChangeListener(OnDateChangedListener onDateChangedListener) {
            this.mAutoFillChangeListener = onDateChangedListener;
        }

        @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
        public void updateDate(long j) {
            Calendar instance = Calendar.getInstance(this.mCurrentLocale);
            instance.setTimeInMillis(j);
            updateDate(instance.get(1), instance.get(2), instance.get(5));
        }

        @Override // com.google.android.material.picker.DatePicker.DatePickerDelegate
        public long getDate() {
            return this.mCurrentDate.getTimeInMillis();
        }

        /* access modifiers changed from: protected */
        public String getFormattedCurrentDate() {
            return DateUtils.formatDateTime(this.mContext, this.mCurrentDate.getTimeInMillis(), 22);
        }

        /* access modifiers changed from: package-private */
        public static class SavedState extends View.BaseSavedState {
            public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
                /* class com.google.android.material.picker.DatePicker.AbstractDatePickerDelegate.SavedState.AnonymousClass1 */

                @Override // android.os.Parcelable.Creator
                public SavedState createFromParcel(Parcel parcel) {
                    return new SavedState(parcel);
                }

                @Override // android.os.Parcelable.Creator
                public SavedState[] newArray(int i) {
                    return new SavedState[i];
                }
            };
            private final int mCurrentView;
            private final int mListPosition;
            private final int mListPositionOffset;
            private final long mMaxDate;
            private final long mMinDate;
            private final int mSelectedDay;
            private final int mSelectedMonth;
            private final int mSelectedYear;

            public SavedState(Parcelable parcelable, int i, int i2, int i3, long j, long j2) {
                this(parcelable, i, i2, i3, j, j2, 0, 0, 0);
            }

            public SavedState(Parcelable parcelable, int i, int i2, int i3, long j, long j2, int i4, int i5, int i6) {
                super(parcelable);
                this.mSelectedYear = i;
                this.mSelectedMonth = i2;
                this.mSelectedDay = i3;
                this.mMinDate = j;
                this.mMaxDate = j2;
                this.mCurrentView = i4;
                this.mListPosition = i5;
                this.mListPositionOffset = i6;
            }

            private SavedState(Parcel parcel) {
                super(parcel);
                this.mSelectedYear = parcel.readInt();
                this.mSelectedMonth = parcel.readInt();
                this.mSelectedDay = parcel.readInt();
                this.mMinDate = parcel.readLong();
                this.mMaxDate = parcel.readLong();
                this.mCurrentView = parcel.readInt();
                this.mListPosition = parcel.readInt();
                this.mListPositionOffset = parcel.readInt();
            }

            public void writeToParcel(Parcel parcel, int i) {
                super.writeToParcel(parcel, i);
                parcel.writeInt(this.mSelectedYear);
                parcel.writeInt(this.mSelectedMonth);
                parcel.writeInt(this.mSelectedDay);
                parcel.writeLong(this.mMinDate);
                parcel.writeLong(this.mMaxDate);
                parcel.writeInt(this.mCurrentView);
                parcel.writeInt(this.mListPosition);
                parcel.writeInt(this.mListPositionOffset);
            }

            public int getSelectedDay() {
                return this.mSelectedDay;
            }

            public int getSelectedMonth() {
                return this.mSelectedMonth;
            }

            public int getSelectedYear() {
                return this.mSelectedYear;
            }

            public long getMinDate() {
                return this.mMinDate;
            }

            public long getMaxDate() {
                return this.mMaxDate;
            }

            public int getCurrentView() {
                return this.mCurrentView;
            }

            public int getListPosition() {
                return this.mListPosition;
            }

            public int getListPositionOffset() {
                return this.mListPositionOffset;
            }
        }
    }

    @Override // android.view.View
    public void autofill(AutofillValue autofillValue) {
        if (!isEnabled() || Build.VERSION.SDK_INT < 26) {
            return;
        }
        if (!autofillValue.isDate()) {
            String str = LOG_TAG;
            Log.w(str, autofillValue + " could not be autofilled into " + this);
            return;
        }
        this.mDelegate.updateDate(autofillValue.getDateValue());
    }

    public int getAutofillType() {
        return isEnabled() ? 4 : 0;
    }

    public AutofillValue getAutofillValue() {
        if (Build.VERSION.SDK_INT < 26) {
            return super.getAutofillValue();
        }
        if (isEnabled()) {
            return AutofillValue.forDate(this.mDelegate.getDate());
        }
        return null;
    }
}
