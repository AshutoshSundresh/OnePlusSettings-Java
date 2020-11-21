package com.google.android.material.picker;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.autofill.AutofillManager;
import android.view.autofill.AutofillValue;
import android.widget.FrameLayout;
import com.google.android.material.R$attr;
import com.google.android.material.R$integer;
import com.google.android.material.R$styleable;
import com.google.android.material.math.MathUtils;
import java.util.Calendar;
import java.util.Locale;

public class TimePicker extends FrameLayout {
    private static final String LOG_TAG = TimePicker.class.getSimpleName();
    private final TimePickerDelegate mDelegate;
    private final int mMode;

    public interface OnTimeChangedListener {
        void onTimeChanged(TimePicker timePicker, int i, int i2);
    }

    /* access modifiers changed from: package-private */
    public interface TimePickerDelegate {
        View getAmView();

        int getBaseline();

        long getDate();

        int getHour();

        View getHourView();

        int getMinute();

        View getMinuteView();

        View getPmView();

        boolean is24Hour();

        boolean isEnabled();

        void onRestoreInstanceState(Parcelable parcelable);

        Parcelable onSaveInstanceState(Parcelable parcelable);

        void setAutoFillChangeListener(OnTimeChangedListener onTimeChangedListener);

        void setDate(long j);

        void setEnabled(boolean z);

        void setHour(int i);

        void setIs24Hour(boolean z);

        void setMinute(int i);

        void setOnTimeChangedListener(OnTimeChangedListener onTimeChangedListener);

        boolean validateInput();
    }

    public TimePicker(Context context) {
        this(context, null);
    }

    public TimePicker(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.timePickerStyle);
    }

    public TimePicker(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public TimePicker(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.TimePicker, i, i2);
        boolean z = obtainStyledAttributes.getBoolean(R$styleable.TimePicker_dialogMode, false);
        int i3 = obtainStyledAttributes.getInt(R$styleable.TimePicker_android_timePickerMode, 2);
        obtainStyledAttributes.recycle();
        if (i3 != 2 || !z) {
            this.mMode = i3;
        } else {
            this.mMode = context.getResources().getInteger(R$integer.time_picker_mode);
        }
        if (this.mMode != 2) {
            this.mDelegate = new TimePickerSpinnerDelegate(this, context, attributeSet, i, i2);
        } else {
            this.mDelegate = new TimePickerClockDelegate(this, context, attributeSet, i, i2);
        }
        this.mDelegate.setAutoFillChangeListener(new OnTimeChangedListener() {
            /* class com.google.android.material.picker.TimePicker.AnonymousClass1 */

            @Override // com.google.android.material.picker.TimePicker.OnTimeChangedListener
            public void onTimeChanged(TimePicker timePicker, int i, int i2) {
                if (Build.VERSION.SDK_INT >= 26) {
                    ((AutofillManager) TimePicker.this.getContext().getSystemService(AutofillManager.class)).notifyValueChanged(TimePicker.this);
                }
            }
        });
    }

    public int getMode() {
        return this.mMode;
    }

    public void setHour(int i) {
        this.mDelegate.setHour(MathUtils.constrain(i, 0, 23));
    }

    public int getHour() {
        return this.mDelegate.getHour();
    }

    public void setMinute(int i) {
        this.mDelegate.setMinute(MathUtils.constrain(i, 0, 59));
    }

    public int getMinute() {
        return this.mDelegate.getMinute();
    }

    @Deprecated
    public void setCurrentHour(Integer num) {
        setHour(num.intValue());
    }

    @Deprecated
    public Integer getCurrentHour() {
        return Integer.valueOf(getHour());
    }

    @Deprecated
    public void setCurrentMinute(Integer num) {
        setMinute(num.intValue());
    }

    @Deprecated
    public Integer getCurrentMinute() {
        return Integer.valueOf(getMinute());
    }

    public void setIs24HourView(Boolean bool) {
        if (bool != null) {
            this.mDelegate.setIs24Hour(bool.booleanValue());
        }
    }

    public boolean is24HourView() {
        return this.mDelegate.is24Hour();
    }

    public void setOnTimeChangedListener(OnTimeChangedListener onTimeChangedListener) {
        this.mDelegate.setOnTimeChangedListener(onTimeChangedListener);
    }

    public void setEnabled(boolean z) {
        super.setEnabled(z);
        this.mDelegate.setEnabled(z);
    }

    public boolean isEnabled() {
        return this.mDelegate.isEnabled();
    }

    public int getBaseline() {
        return this.mDelegate.getBaseline();
    }

    public boolean validateInput() {
        return this.mDelegate.validateInput();
    }

    /* access modifiers changed from: protected */
    public Parcelable onSaveInstanceState() {
        return this.mDelegate.onSaveInstanceState(super.onSaveInstanceState());
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Parcelable parcelable) {
        View.BaseSavedState baseSavedState = (View.BaseSavedState) parcelable;
        super.onRestoreInstanceState(baseSavedState.getSuperState());
        this.mDelegate.onRestoreInstanceState(baseSavedState);
    }

    public CharSequence getAccessibilityClassName() {
        return TimePicker.class.getName();
    }

    public View getHourView() {
        return this.mDelegate.getHourView();
    }

    public View getMinuteView() {
        return this.mDelegate.getMinuteView();
    }

    public View getAmView() {
        return this.mDelegate.getAmView();
    }

    public View getPmView() {
        return this.mDelegate.getPmView();
    }

    public static String[] getAmPmStrings(Context context) {
        return new String[]{DateUtils.getAMPMString(0), DateUtils.getAMPMString(1)};
    }

    static abstract class AbstractTimePickerDelegate implements TimePickerDelegate {
        protected OnTimeChangedListener mAutoFillChangeListener;
        protected final Context mContext;
        protected final TimePicker mDelegator;
        protected final Locale mLocale;
        protected OnTimeChangedListener mOnTimeChangedListener;

        public AbstractTimePickerDelegate(TimePicker timePicker, Context context) {
            this.mDelegator = timePicker;
            this.mContext = context;
            this.mLocale = context.getResources().getConfiguration().locale;
        }

        @Override // com.google.android.material.picker.TimePicker.TimePickerDelegate
        public void setOnTimeChangedListener(OnTimeChangedListener onTimeChangedListener) {
            this.mOnTimeChangedListener = onTimeChangedListener;
        }

        @Override // com.google.android.material.picker.TimePicker.TimePickerDelegate
        public void setAutoFillChangeListener(OnTimeChangedListener onTimeChangedListener) {
            this.mAutoFillChangeListener = onTimeChangedListener;
        }

        @Override // com.google.android.material.picker.TimePicker.TimePickerDelegate
        public void setDate(long j) {
            Calendar instance = Calendar.getInstance();
            instance.setTimeInMillis(j);
            setHour(instance.get(11));
            setMinute(instance.get(12));
        }

        @Override // com.google.android.material.picker.TimePicker.TimePickerDelegate
        public long getDate() {
            Calendar instance = Calendar.getInstance(this.mLocale);
            instance.set(11, getHour());
            instance.set(12, getMinute());
            return instance.getTimeInMillis();
        }

        /* access modifiers changed from: protected */
        public static class SavedState extends View.BaseSavedState {
            public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
                /* class com.google.android.material.picker.TimePicker.AbstractTimePickerDelegate.SavedState.AnonymousClass1 */

                @Override // android.os.Parcelable.Creator
                public SavedState createFromParcel(Parcel parcel) {
                    return new SavedState(parcel);
                }

                @Override // android.os.Parcelable.Creator
                public SavedState[] newArray(int i) {
                    return new SavedState[i];
                }
            };
            private final int mCurrentItemShowing;
            private final int mHour;
            private final boolean mIs24HourMode;
            private final int mMinute;

            public SavedState(Parcelable parcelable, int i, int i2, boolean z) {
                this(parcelable, i, i2, z, 0);
            }

            public SavedState(Parcelable parcelable, int i, int i2, boolean z, int i3) {
                super(parcelable);
                this.mHour = i;
                this.mMinute = i2;
                this.mIs24HourMode = z;
                this.mCurrentItemShowing = i3;
            }

            private SavedState(Parcel parcel) {
                super(parcel);
                this.mHour = parcel.readInt();
                this.mMinute = parcel.readInt();
                this.mIs24HourMode = parcel.readInt() != 1 ? false : true;
                this.mCurrentItemShowing = parcel.readInt();
            }

            public int getHour() {
                return this.mHour;
            }

            public int getMinute() {
                return this.mMinute;
            }

            public boolean is24HourMode() {
                return this.mIs24HourMode;
            }

            public int getCurrentItemShowing() {
                return this.mCurrentItemShowing;
            }

            public void writeToParcel(Parcel parcel, int i) {
                super.writeToParcel(parcel, i);
                parcel.writeInt(this.mHour);
                parcel.writeInt(this.mMinute);
                parcel.writeInt(this.mIs24HourMode ? 1 : 0);
                parcel.writeInt(this.mCurrentItemShowing);
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
        this.mDelegate.setDate(autofillValue.getDateValue());
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
