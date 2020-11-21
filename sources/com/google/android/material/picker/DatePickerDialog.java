package com.google.android.material.picker;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.R$id;
import com.google.android.material.R$layout;
import com.google.android.material.picker.DatePicker;
import java.util.Calendar;

public class DatePickerDialog extends AlertDialog implements DialogInterface.OnClickListener, DatePicker.OnDateChangedListener {
    private final DatePicker mDatePicker;
    private OnDateSetListener mDateSetListener;
    private final DatePicker.ValidationCallback mValidationCallback;

    public interface OnDateSetListener {
        void onDateSet(DatePicker datePicker, int i, int i2, int i3);
    }

    public DatePickerDialog(Context context, OnDateSetListener onDateSetListener, int i, int i2, int i3) {
        this(context, 0, onDateSetListener, null, i, i2, i3);
    }

    private DatePickerDialog(Context context, int i, OnDateSetListener onDateSetListener, Calendar calendar, int i2, int i3, int i4) {
        super(context, resolveDialogTheme(context, i));
        this.mValidationCallback = new DatePicker.ValidationCallback(this) {
            /* class com.google.android.material.picker.DatePickerDialog.AnonymousClass2 */
        };
        Context context2 = getContext();
        View inflate = LayoutInflater.from(context2).inflate(R$layout.op_control_date_picker_dialog, (ViewGroup) null);
        setShowInBottom(true);
        setView(inflate);
        setButton(-1, context2.getString(17039370), this);
        setButton(-2, context2.getString(17039360), this);
        if (calendar != null) {
            i2 = calendar.get(1);
            i3 = calendar.get(2);
            i4 = calendar.get(5);
        }
        DatePicker datePicker = (DatePicker) inflate.findViewById(R$id.datePicker);
        this.mDatePicker = datePicker;
        datePicker.init(i2, i3, i4, this);
        Calendar instance = Calendar.getInstance();
        instance.set(2049, 11, 31);
        this.mDatePicker.setMaxDate(instance.getTimeInMillis());
        instance.clear();
        instance.set(1901, 0, 1);
        this.mDatePicker.setMinDate(instance.getTimeInMillis());
        this.mDatePicker.setValidationCallback(this.mValidationCallback);
        this.mDateSetListener = onDateSetListener;
    }

    static int resolveDialogTheme(Context context, int i) {
        if (i != 0) {
            return i;
        }
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(16843948, typedValue, true);
        return typedValue.resourceId;
    }

    public void show() {
        super.show();
        getButton(-1).setOnClickListener(new View.OnClickListener() {
            /* class com.google.android.material.picker.DatePickerDialog.AnonymousClass1 */

            public void onClick(View view) {
                if (DatePickerDialog.this.mDateSetListener == null) {
                    return;
                }
                if (DatePickerDialog.this.mDatePicker.getMode() != 2 || !DatePickerDialog.this.mDatePicker.isYearPickerShowing()) {
                    DatePickerDialog.this.mDatePicker.clearFocus();
                    DatePickerDialog.this.mDateSetListener.onDateSet(DatePickerDialog.this.mDatePicker, DatePickerDialog.this.mDatePicker.getYear(), DatePickerDialog.this.mDatePicker.getMonth(), DatePickerDialog.this.mDatePicker.getDayOfMonth());
                    DatePickerDialog.this.dismiss();
                    return;
                }
                DatePickerDialog.this.mDatePicker.setCurrentYear();
            }
        });
    }

    @Override // com.google.android.material.picker.DatePicker.OnDateChangedListener
    public void onDateChanged(DatePicker datePicker, int i, int i2, int i3) {
        this.mDatePicker.init(i, i2, i3, this);
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -2) {
            cancel();
        } else if (i == -1 && this.mDateSetListener != null) {
            this.mDatePicker.clearFocus();
            OnDateSetListener onDateSetListener = this.mDateSetListener;
            DatePicker datePicker = this.mDatePicker;
            onDateSetListener.onDateSet(datePicker, datePicker.getYear(), this.mDatePicker.getMonth(), this.mDatePicker.getDayOfMonth());
        }
    }

    public DatePicker getDatePicker() {
        return this.mDatePicker;
    }

    public Bundle onSaveInstanceState() {
        Bundle onSaveInstanceState = super.onSaveInstanceState();
        onSaveInstanceState.putInt("year", this.mDatePicker.getYear());
        onSaveInstanceState.putInt("month", this.mDatePicker.getMonth());
        onSaveInstanceState.putInt("day", this.mDatePicker.getDayOfMonth());
        return onSaveInstanceState;
    }

    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        this.mDatePicker.init(bundle.getInt("year"), bundle.getInt("month"), bundle.getInt("day"), this);
    }
}
