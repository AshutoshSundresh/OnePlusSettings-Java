package com.google.android.material.picker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.animation.AnimatorUtils;
import com.google.android.material.R$attr;
import com.google.android.material.R$id;
import com.google.android.material.R$integer;
import com.google.android.material.R$layout;
import com.google.android.material.R$style;
import com.google.android.material.math.MathUtils;
import com.oneplus.common.SystemUtils;

public class TextInputTimePickerView extends RelativeLayout {
    private final RadioButton mAmLabel;
    private final RadioGroup mAmPmGroup;
    private final LinearLayout mAmPmParent;
    private final View.OnClickListener mClickListener;
    private final TextView mErrorLabel;
    private boolean mErrorShowing;
    private final TextView mHeaderLabel;
    private final EditText mHourEditText;
    private boolean mHourFormatStartsAtZero;
    private final TextView mHourLabel;
    private final View mInputBlock;
    private InputMethodManager mInputMethodManager;
    private boolean mIs24Hour;
    private boolean mIsAmPmAtStart;
    private int mLabelAlphaDuration;
    private OnValueTypedListener mListener;
    private final EditText mMinuteEditText;
    private final TextView mMinuteLabel;
    private final RadioButton mPmLabel;
    private int[] mTimeColorStates;
    private int[] mTimeLabelColorStates;

    /* access modifiers changed from: package-private */
    public interface OnValueTypedListener {
        void onValueChanged(int i, int i2);
    }

    /* access modifiers changed from: package-private */
    public void updateSeparator(String str) {
    }

    public TextInputTimePickerView(Context context) {
        this(context, null);
    }

    public TextInputTimePickerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public TextInputTimePickerView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    @SuppressLint({"ResourceType"})
    public TextInputTimePickerView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mTimeColorStates = new int[2];
        this.mTimeLabelColorStates = new int[2];
        this.mClickListener = new View.OnClickListener() {
            /* class com.google.android.material.picker.TextInputTimePickerView.AnonymousClass5 */

            public void onClick(View view) {
                int id = view.getId();
                if (id == R$id.am_label2) {
                    TextInputTimePickerView.this.updateAmPmLabel(true);
                    TextInputTimePickerView.this.mListener.onValueChanged(2, 0);
                } else if (id == R$id.pm_label2) {
                    TextInputTimePickerView.this.updateAmPmLabel(false);
                    TextInputTimePickerView.this.mListener.onValueChanged(2, 1);
                }
            }
        };
        LayoutInflater.from(context).inflate(R$layout.time_picker_text_input_material, (ViewGroup) this, true);
        this.mAmPmParent = (LinearLayout) findViewById(R$id.input_am_pm_parent);
        this.mInputBlock = findViewById(R$id.input_block);
        this.mHourEditText = (EditText) findViewById(R$id.input_hour);
        this.mMinuteEditText = (EditText) findViewById(R$id.input_minute);
        this.mHeaderLabel = (TextView) findViewById(R$id.top_label);
        this.mErrorLabel = (TextView) findViewById(R$id.label_error);
        this.mHourLabel = (TextView) findViewById(R$id.label_hour);
        this.mMinuteLabel = (TextView) findViewById(R$id.label_minute);
        int i3 = R$attr.pickerColorUnActivated;
        int[] iArr = {R$attr.pickerColorActivated, i3};
        int[] iArr2 = {i3, R$attr.pickerInputLabelUnActivated};
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, iArr);
        this.mLabelAlphaDuration = context.getResources().getInteger(R$integer.op_control_time_325);
        this.mTimeColorStates[0] = obtainStyledAttributes.getColor(0, -16777216);
        this.mTimeColorStates[1] = obtainStyledAttributes.getColor(1, -16777216);
        obtainStyledAttributes.recycle();
        TypedArray obtainStyledAttributes2 = context.obtainStyledAttributes(attributeSet, iArr2);
        this.mTimeLabelColorStates[0] = obtainStyledAttributes2.getColor(0, -16777216);
        this.mTimeLabelColorStates[1] = obtainStyledAttributes2.getColor(1, -16777216);
        obtainStyledAttributes2.recycle();
        this.mHourEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            /* class com.google.android.material.picker.TextInputTimePickerView.AnonymousClass1 */

            public void onFocusChange(View view, boolean z) {
                if (z) {
                    TextInputTimePickerView.this.mHourEditText.setActivated(true);
                    TextInputTimePickerView.this.mMinuteEditText.setActivated(false);
                    TextInputTimePickerView textInputTimePickerView = TextInputTimePickerView.this;
                    textInputTimePickerView.resetInputTimeTextAppearance(R$style.OPTextAppearance_Material_TimePicker_InputField, textInputTimePickerView.mHourEditText);
                    TextInputTimePickerView textInputTimePickerView2 = TextInputTimePickerView.this;
                    textInputTimePickerView2.resetInputTimeTextAppearance(R$style.OPTextAppearance_Material_TimePicker_InputFieldUnActive, textInputTimePickerView2.mMinuteEditText);
                    TextInputTimePickerView.this.mHourEditText.setTextColor(TextInputTimePickerView.this.mTimeColorStates[0]);
                    TextInputTimePickerView.this.mMinuteEditText.setTextColor(TextInputTimePickerView.this.mTimeColorStates[1]);
                    TextInputTimePickerView.this.resetInputTimeLabelState(true);
                    TextInputTimePickerView.this.showSoftInput(view);
                }
            }
        });
        this.mMinuteEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            /* class com.google.android.material.picker.TextInputTimePickerView.AnonymousClass2 */

            public void onFocusChange(View view, boolean z) {
                if (z) {
                    TextInputTimePickerView.this.mMinuteEditText.setActivated(true);
                    TextInputTimePickerView.this.mHourEditText.setActivated(false);
                    TextInputTimePickerView textInputTimePickerView = TextInputTimePickerView.this;
                    textInputTimePickerView.resetInputTimeTextAppearance(R$style.OPTextAppearance_Material_TimePicker_InputFieldUnActive, textInputTimePickerView.mHourEditText);
                    TextInputTimePickerView textInputTimePickerView2 = TextInputTimePickerView.this;
                    textInputTimePickerView2.resetInputTimeTextAppearance(R$style.OPTextAppearance_Material_TimePicker_InputField, textInputTimePickerView2.mMinuteEditText);
                    TextInputTimePickerView.this.mMinuteEditText.setTextColor(TextInputTimePickerView.this.mTimeColorStates[0]);
                    TextInputTimePickerView.this.mHourEditText.setTextColor(TextInputTimePickerView.this.mTimeColorStates[1]);
                    TextInputTimePickerView.this.resetInputTimeLabelState(false);
                    TextInputTimePickerView.this.showSoftInput(view);
                }
            }
        });
        this.mHourEditText.addTextChangedListener(new TextWatcher() {
            /* class com.google.android.material.picker.TextInputTimePickerView.AnonymousClass3 */

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                TextInputTimePickerView.this.validateInput();
            }
        });
        this.mMinuteEditText.addTextChangedListener(new TextWatcher() {
            /* class com.google.android.material.picker.TextInputTimePickerView.AnonymousClass4 */

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                TextInputTimePickerView.this.validateInput();
            }
        });
        String[] amPmStrings = TimePicker.getAmPmStrings(context);
        this.mAmPmGroup = (RadioGroup) findViewById(R$id.am_pm_group);
        RadioButton radioButton = (RadioButton) findViewById(R$id.am_label2);
        this.mAmLabel = radioButton;
        radioButton.setText(TimePickerClockDelegate.obtainVerbatim(amPmStrings[0]));
        this.mAmLabel.setOnClickListener(this.mClickListener);
        ensureMinimumTextWidth(this.mAmLabel);
        RadioButton radioButton2 = (RadioButton) findViewById(R$id.pm_label2);
        this.mPmLabel = radioButton2;
        radioButton2.setText(TimePickerClockDelegate.obtainVerbatim(amPmStrings[1]));
        this.mPmLabel.setOnClickListener(this.mClickListener);
        ensureMinimumTextWidth(this.mPmLabel);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void showSoftInput(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService("input_method");
        this.mInputMethodManager = inputMethodManager;
        if (inputMethodManager != null && inputMethodManager.isActive(view)) {
            this.mInputMethodManager.showSoftInput(view, 0);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void resetInputTimeTextAppearance(int i, TextView textView) {
        if (SystemUtils.isAtLeastM()) {
            textView.setTextAppearance(i);
        } else {
            textView.setTextAppearance(getContext(), i);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void resetInputTimeLabelState(boolean z) {
        TextView textView = this.mMinuteLabel;
        int[] iArr = this.mTimeLabelColorStates;
        textView.setTextColor(z ? iArr[1] : iArr[0]);
        TextView textView2 = this.mHourLabel;
        int[] iArr2 = this.mTimeLabelColorStates;
        textView2.setTextColor(z ? iArr2[0] : iArr2[1]);
    }

    public View getInputBlock() {
        return this.mInputBlock;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateAmPmLabel(boolean z) {
        this.mAmLabel.setActivated(z);
        this.mAmLabel.setChecked(z);
        RadioButton radioButton = this.mAmLabel;
        int[] iArr = this.mTimeColorStates;
        radioButton.setTextColor(z ? iArr[0] : iArr[1]);
        this.mAmLabel.getPaint().setFakeBoldText(z);
        this.mPmLabel.setActivated(!z);
        this.mPmLabel.setChecked(!z);
        this.mPmLabel.setTextColor(z ? this.mTimeColorStates[1] : this.mTimeColorStates[0]);
        this.mPmLabel.getPaint().setFakeBoldText(!z);
    }

    public void showLabels(boolean z) {
        if (z) {
            this.mHourLabel.animate().alpha(1.0f).setDuration((long) this.mLabelAlphaDuration).setInterpolator(AnimatorUtils.FastOutSlowInInterpolator).start();
            this.mMinuteLabel.animate().alpha(1.0f).setDuration((long) this.mLabelAlphaDuration).setInterpolator(AnimatorUtils.FastOutSlowInInterpolator).start();
            this.mHeaderLabel.animate().alpha(1.0f).setDuration((long) this.mLabelAlphaDuration).setInterpolator(AnimatorUtils.FastOutSlowInInterpolator).start();
            this.mHourLabel.animate().alpha(1.0f).setDuration((long) this.mLabelAlphaDuration).setInterpolator(AnimatorUtils.FastOutSlowInInterpolator).start();
            return;
        }
        this.mHourLabel.animate().alpha(0.0f).setDuration((long) this.mLabelAlphaDuration).setInterpolator(AnimatorUtils.FastOutSlowInInterpolator).start();
        this.mMinuteLabel.animate().alpha(0.0f).setDuration((long) this.mLabelAlphaDuration).setInterpolator(AnimatorUtils.FastOutSlowInInterpolator).start();
        this.mHeaderLabel.animate().alpha(0.0f).setDuration((long) this.mLabelAlphaDuration).setInterpolator(AnimatorUtils.FastOutSlowInInterpolator).start();
        this.mHourLabel.animate().alpha(0.0f).setDuration((long) this.mLabelAlphaDuration).setInterpolator(AnimatorUtils.FastOutSlowInInterpolator).start();
    }

    public void showInputBlock(boolean z) {
        int i = 0;
        this.mInputBlock.setVisibility(z ? 0 : 4);
        RadioGroup radioGroup = this.mAmPmGroup;
        if (!z) {
            i = 4;
        }
        radioGroup.setVisibility(i);
    }

    public void setAmPmAtStart(boolean z) {
        if (this.mIs24Hour) {
            this.mAmPmParent.removeView(this.mAmPmGroup);
        } else if (this.mIsAmPmAtStart != z) {
            this.mIsAmPmAtStart = z;
            if (z) {
                this.mAmPmParent.removeView(this.mAmPmGroup);
                this.mAmPmParent.addView(this.mAmPmGroup, 0);
            } else {
                this.mAmPmParent.removeView(this.mAmPmGroup);
                this.mAmPmParent.addView(this.mAmPmGroup);
            }
            this.mAmPmParent.requestLayout();
        }
    }

    public void setIs24Hour(boolean z) {
        if (this.mIs24Hour != z) {
            this.mIs24Hour = z;
            setAmPmAtStart(this.mIsAmPmAtStart);
        }
    }

    private static void ensureMinimumTextWidth(TextView textView) {
        textView.measure(0, 0);
        int measuredWidth = textView.getMeasuredWidth();
        textView.setMinWidth(measuredWidth);
        textView.setMinimumWidth(measuredWidth);
    }

    /* access modifiers changed from: package-private */
    public void setListener(OnValueTypedListener onValueTypedListener) {
        this.mListener = onValueTypedListener;
    }

    /* access modifiers changed from: package-private */
    public void setHourFormat(int i) {
        this.mHourEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(i)});
        this.mMinuteEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(i)});
    }

    /* access modifiers changed from: package-private */
    public boolean validateInput() {
        boolean z = parseAndSetHourInternal(this.mHourEditText.getText().toString()) && parseAndSetMinuteInternal(this.mMinuteEditText.getText().toString());
        setError(!z);
        return z;
    }

    private void setError(boolean z) {
        this.mErrorShowing = z;
        int i = 0;
        this.mErrorLabel.setVisibility(z ? 0 : 4);
        this.mHourLabel.setVisibility(z ? 4 : 0);
        TextView textView = this.mMinuteLabel;
        if (z) {
            i = 4;
        }
        textView.setVisibility(i);
    }

    /* access modifiers changed from: package-private */
    public void updateTextInputValues(int i, int i2, int i3, boolean z, boolean z2) {
        this.mIs24Hour = z;
        this.mHourFormatStartsAtZero = z2;
        this.mAmPmGroup.setVisibility(z ? 8 : 0);
        updateAmPmLabel(i3 == 0);
        this.mHourEditText.setText(String.format("%d", Integer.valueOf(i)));
        this.mMinuteEditText.setText(String.format("%d", Integer.valueOf(i2)));
        EditText editText = this.mHourEditText;
        editText.setSelection(editText.getText().toString().length());
        EditText editText2 = this.mMinuteEditText;
        editText2.setSelection(editText2.getText().toString().length());
        if (this.mErrorShowing) {
            validateInput();
        }
    }

    private boolean parseAndSetHourInternal(String str) {
        try {
            int parseInt = Integer.parseInt(str);
            int i = 1;
            if (!isValidLocalizedHour(parseInt)) {
                if (this.mHourFormatStartsAtZero) {
                    i = 0;
                }
                this.mListener.onValueChanged(0, getHourOfDayFromLocalizedHour(MathUtils.constrain(parseInt, i, this.mIs24Hour ? 23 : i + 11)));
                return false;
            }
            this.mListener.onValueChanged(0, getHourOfDayFromLocalizedHour(parseInt));
            return true;
        } catch (NumberFormatException unused) {
            return false;
        }
    }

    private boolean parseAndSetMinuteInternal(String str) {
        try {
            int parseInt = Integer.parseInt(str);
            if (parseInt >= 0) {
                if (parseInt <= 59) {
                    this.mListener.onValueChanged(1, parseInt);
                    return true;
                }
            }
            this.mListener.onValueChanged(1, MathUtils.constrain(parseInt, 0, 59));
        } catch (NumberFormatException unused) {
        }
        return false;
    }

    private boolean isValidLocalizedHour(int i) {
        int i2 = !this.mHourFormatStartsAtZero ? 1 : 0;
        int i3 = (this.mIs24Hour ? 23 : 11) + i2;
        if (i < i2 || i > i3) {
            return false;
        }
        return true;
    }

    private int getHourOfDayFromLocalizedHour(int i) {
        if (!this.mIs24Hour) {
            if (!this.mHourFormatStartsAtZero && i == 12) {
                i = 0;
            }
            return this.mPmLabel.isChecked() ? i + 12 : i;
        } else if (this.mHourFormatStartsAtZero || i != 24) {
            return i;
        } else {
            return 0;
        }
    }
}
