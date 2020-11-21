package com.google.android.material.picker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.TextView;

@SuppressLint({"AppCompatCustomView"})
public class NumericTextView extends TextView {
    private static final double LOG_RADIX = Math.log(10.0d);
    private int mCount;
    private OnValueChangedListener mListener;
    private int mMaxCount = 2;
    private int mMaxValue = 99;
    private int mMinValue = 0;
    private int mPreviousValue;
    private boolean mShowLeadingZeroes = true;
    private int mValue;

    public interface OnValueChangedListener {
        void onValueChanged(NumericTextView numericTextView, int i, boolean z, boolean z2);
    }

    private static boolean isKeyCodeNumeric(int i) {
        return i == 7 || i == 8 || i == 9 || i == 10 || i == 11 || i == 12 || i == 13 || i == 14 || i == 15 || i == 16;
    }

    private static int numericKeyCodeToInt(int i) {
        return i - 7;
    }

    public NumericTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setFocusable(true);
    }

    /* access modifiers changed from: protected */
    public void onFocusChanged(boolean z, int i, Rect rect) {
        super.onFocusChanged(z, i, rect);
        if (z) {
            this.mPreviousValue = this.mValue;
            this.mValue = 0;
            this.mCount = 0;
            setHint(getText());
            setText("");
            return;
        }
        if (this.mCount == 0) {
            this.mValue = this.mPreviousValue;
            setText(getHint());
            setHint("");
        }
        int i2 = this.mValue;
        int i3 = this.mMinValue;
        if (i2 < i3) {
            this.mValue = i3;
        }
        setValue(this.mValue);
        OnValueChangedListener onValueChangedListener = this.mListener;
        if (onValueChangedListener != null) {
            onValueChangedListener.onValueChanged(this, this.mValue, true, true);
        }
    }

    public final void setValue(int i) {
        if (this.mValue != i) {
            this.mValue = i;
            updateDisplayedValue();
        }
    }

    public final int getValue() {
        return this.mValue;
    }

    public final void setRange(int i, int i2) {
        if (this.mMinValue != i) {
            this.mMinValue = i;
        }
        if (this.mMaxValue != i2) {
            this.mMaxValue = i2;
            this.mMaxCount = ((int) (Math.log((double) i2) / LOG_RADIX)) + 1;
            updateMinimumWidth();
            updateDisplayedValue();
        }
    }

    public final int getRangeMinimum() {
        return this.mMinValue;
    }

    public final int getRangeMaximum() {
        return this.mMaxValue;
    }

    public final void setShowLeadingZeroes(boolean z) {
        if (this.mShowLeadingZeroes != z) {
            this.mShowLeadingZeroes = z;
            updateDisplayedValue();
        }
    }

    public final boolean getShowLeadingZeroes() {
        return this.mShowLeadingZeroes;
    }

    private void updateDisplayedValue() {
        String str;
        if (this.mShowLeadingZeroes) {
            str = "%0" + this.mMaxCount + "d";
        } else {
            str = "%d";
        }
        setText(String.format(str, Integer.valueOf(this.mValue)));
    }

    private void updateMinimumWidth() {
        CharSequence text = getText();
        int i = 0;
        for (int i2 = 0; i2 < this.mMaxValue; i2++) {
            setText(String.format("%0" + this.mMaxCount + "d", Integer.valueOf(i2)));
            measure(0, 0);
            int measuredWidth = getMeasuredWidth();
            if (measuredWidth > i) {
                i = measuredWidth;
            }
        }
        setText(text);
        setMinWidth(i);
        setMinimumWidth(i);
    }

    public final void setOnDigitEnteredListener(OnValueChangedListener onValueChangedListener) {
        this.mListener = onValueChangedListener;
    }

    public final OnValueChangedListener getOnDigitEnteredListener() {
        return this.mListener;
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        return isKeyCodeNumeric(i) || i == 67 || super.onKeyDown(i, keyEvent);
    }

    public boolean onKeyMultiple(int i, int i2, KeyEvent keyEvent) {
        return isKeyCodeNumeric(i) || i == 67 || super.onKeyMultiple(i, i2, keyEvent);
    }

    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        return handleKeyUp(i) || super.onKeyUp(i, keyEvent);
    }

    private boolean handleKeyUp(int i) {
        String str;
        int numericKeyCodeToInt;
        boolean z = false;
        if (i == 67) {
            int i2 = this.mCount;
            if (i2 > 0) {
                this.mValue /= 10;
                this.mCount = i2 - 1;
            }
        } else if (!isKeyCodeNumeric(i)) {
            return false;
        } else {
            if (this.mCount < this.mMaxCount && (numericKeyCodeToInt = (this.mValue * 10) + numericKeyCodeToInt(i)) <= this.mMaxValue) {
                this.mValue = numericKeyCodeToInt;
                this.mCount++;
            }
        }
        if (this.mCount > 0) {
            str = String.format("%0" + this.mCount + "d", Integer.valueOf(this.mValue));
        } else {
            str = "";
        }
        setText(str);
        if (this.mListener != null) {
            boolean z2 = this.mValue >= this.mMinValue;
            if (this.mCount >= this.mMaxCount || this.mValue * 10 > this.mMaxValue) {
                z = true;
            }
            this.mListener.onValueChanged(this, this.mValue, z2, z);
        }
        return true;
    }
}
