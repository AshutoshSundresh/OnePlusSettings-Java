package com.google.android.material.picker;

import android.animation.Animator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Parcelable;
import android.text.SpannableStringBuilder;
import android.text.format.DateFormat;
import android.text.style.TtsSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.animation.AnimatorUtils;
import com.google.android.material.R$drawable;
import com.google.android.material.R$id;
import com.google.android.material.R$integer;
import com.google.android.material.R$layout;
import com.google.android.material.R$string;
import com.google.android.material.R$style;
import com.google.android.material.R$styleable;
import com.google.android.material.picker.NumericTextView;
import com.google.android.material.picker.RadialTimePickerView;
import com.google.android.material.picker.TextInputTimePickerView;
import com.google.android.material.picker.TimePicker;
import com.oneplus.common.SystemUtils;
import java.util.Calendar;

/* access modifiers changed from: package-private */
public class TimePickerClockDelegate extends TimePicker.AbstractTimePickerDelegate {
    private static final int[] ATTRS_TEXT_COLOR = {16842904};
    private boolean mAllowAutoAdvance;
    private final RadioButton mAmLabel;
    private final View mAmPmLayout;
    private final View.OnClickListener mClickListener = new View.OnClickListener() {
        /* class com.google.android.material.picker.TimePickerClockDelegate.AnonymousClass10 */

        public void onClick(View view) {
            int id = view.getId();
            if (id == R$id.am_label) {
                TimePickerClockDelegate.this.setAmOrPm(0);
            } else if (id == R$id.pm_label) {
                TimePickerClockDelegate.this.setAmOrPm(1);
            } else if (id == R$id.hours) {
                TimePickerClockDelegate.this.setCurrentItemShowing(0, true, true);
            } else if (id == R$id.minutes) {
                TimePickerClockDelegate.this.setCurrentItemShowing(1, true, true);
            } else {
                return;
            }
            TimePickerClockDelegate.this.tryVibrate();
        }
    };
    private final Runnable mCommitHour = new Runnable() {
        /* class com.google.android.material.picker.TimePickerClockDelegate.AnonymousClass7 */

        public void run() {
            TimePickerClockDelegate timePickerClockDelegate = TimePickerClockDelegate.this;
            timePickerClockDelegate.setHour(timePickerClockDelegate.mHourView.getValue());
        }
    };
    private final Runnable mCommitMinute = new Runnable() {
        /* class com.google.android.material.picker.TimePickerClockDelegate.AnonymousClass8 */

        public void run() {
            TimePickerClockDelegate timePickerClockDelegate = TimePickerClockDelegate.this;
            timePickerClockDelegate.setMinute(timePickerClockDelegate.mMinuteView.getValue());
        }
    };
    private int mCurrentHour;
    private int mCurrentMinute;
    private final NumericTextView.OnValueChangedListener mDigitEnteredListener = new NumericTextView.OnValueChangedListener() {
        /* class com.google.android.material.picker.TimePickerClockDelegate.AnonymousClass6 */

        @Override // com.google.android.material.picker.NumericTextView.OnValueChangedListener
        public void onValueChanged(NumericTextView numericTextView, int i, boolean z, boolean z2) {
            Runnable runnable;
            NumericTextView numericTextView2 = null;
            if (numericTextView == TimePickerClockDelegate.this.mHourView) {
                runnable = TimePickerClockDelegate.this.mCommitHour;
                if (numericTextView.isFocused()) {
                    numericTextView2 = TimePickerClockDelegate.this.mMinuteView;
                }
            } else if (numericTextView == TimePickerClockDelegate.this.mMinuteView) {
                runnable = TimePickerClockDelegate.this.mCommitMinute;
            } else {
                return;
            }
            numericTextView.removeCallbacks(runnable);
            if (!z) {
                return;
            }
            if (z2) {
                runnable.run();
                if (numericTextView2 != null) {
                    numericTextView2.requestFocus();
                    return;
                }
                return;
            }
            numericTextView.postDelayed(runnable, 2000);
        }
    };
    private int mDuration;
    private final View.OnFocusChangeListener mFocusListener = new View.OnFocusChangeListener() {
        /* class com.google.android.material.picker.TimePickerClockDelegate.AnonymousClass9 */

        public void onFocusChange(View view, boolean z) {
            if (z) {
                int id = view.getId();
                if (id == R$id.am_label) {
                    TimePickerClockDelegate.this.setAmOrPm(0);
                } else if (id == R$id.pm_label) {
                    TimePickerClockDelegate.this.setAmOrPm(1);
                } else if (id == R$id.hours) {
                    TimePickerClockDelegate.this.setCurrentItemShowing(0, true, true);
                } else if (id == R$id.minutes) {
                    TimePickerClockDelegate.this.setCurrentItemShowing(1, true, true);
                } else {
                    return;
                }
                TimePickerClockDelegate.this.tryVibrate();
            }
        }
    };
    private int mHeaderOffset;
    private int mHeaderPositionY;
    private boolean mHourFormatShowLeadingZero;
    private boolean mHourFormatStartsAtZero;
    private final NumericTextView mHourView;
    private int mInputBlockPositionY;
    private boolean mIs24Hour;
    private boolean mIsAmPmAtStart;
    private boolean mIsEnabled = true;
    private boolean mIsToggleTimeMode;
    private boolean mLastAnnouncedIsHour;
    private CharSequence mLastAnnouncedText;
    private final NumericTextView mMinuteView;
    private final RadialTimePickerView.OnValueSelectedListener mOnValueSelectedListener = new RadialTimePickerView.OnValueSelectedListener() {
        /* class com.google.android.material.picker.TimePickerClockDelegate.AnonymousClass4 */

        @Override // com.google.android.material.picker.RadialTimePickerView.OnValueSelectedListener
        public void onValueSelected(int i, int i2, boolean z) {
            boolean z2 = false;
            if (i == 0) {
                boolean z3 = TimePickerClockDelegate.this.getHour() != i2;
                boolean z4 = TimePickerClockDelegate.this.mAllowAutoAdvance && z;
                TimePickerClockDelegate.this.setHourInternal(i2, 1, !z4);
                if (z4) {
                    TimePickerClockDelegate.this.setCurrentItemShowing(1, true, false);
                    int localizedHour = TimePickerClockDelegate.this.getLocalizedHour(i2);
                    TimePickerClockDelegate.this.mDelegator.announceForAccessibility(localizedHour + ". " + TimePickerClockDelegate.this.mSelectMinutes);
                }
                z2 = z3;
            } else if (i == 1) {
                if (TimePickerClockDelegate.this.getMinute() != i2) {
                    z2 = true;
                }
                TimePickerClockDelegate.this.setMinuteInternal(i2, 1);
            }
            TimePickerClockDelegate timePickerClockDelegate = TimePickerClockDelegate.this;
            TimePicker.OnTimeChangedListener onTimeChangedListener = timePickerClockDelegate.mOnTimeChangedListener;
            if (onTimeChangedListener != null && z2) {
                onTimeChangedListener.onTimeChanged(timePickerClockDelegate.mDelegator, timePickerClockDelegate.getHour(), TimePickerClockDelegate.this.getMinute());
            }
        }
    };
    private final TextInputTimePickerView.OnValueTypedListener mOnValueTypedListener = new TextInputTimePickerView.OnValueTypedListener() {
        /* class com.google.android.material.picker.TimePickerClockDelegate.AnonymousClass5 */

        @Override // com.google.android.material.picker.TextInputTimePickerView.OnValueTypedListener
        public void onValueChanged(int i, int i2) {
            if (i == 0) {
                TimePickerClockDelegate.this.setHourInternal(i2, 2, false);
            } else if (i == 1) {
                TimePickerClockDelegate.this.setMinuteInternal(i2, 2);
            } else if (i == 2) {
                TimePickerClockDelegate.this.setAmOrPm(i2);
            }
        }
    };
    private final RadioButton mPmLabel;
    private boolean mRadialPickerModeEnabled = true;
    private final LinearLayout mRadialTimePickerHeader;
    private final ImageButton mRadialTimePickerModeButton;
    private final RadialTimePickerView mRadialTimePickerView;
    private float mRadialTimeViewAlpha;
    private float mRadialTimeViewScale;
    private final String mSelectHours;
    private final String mSelectMinutes;
    private final TextView mSeparatorView;
    private final Calendar mTempCalendar;
    private final View mTextInputPickerHeader;
    private final TextInputTimePickerView mTextInputPickerView;

    @Override // com.google.android.material.picker.TimePicker.TimePickerDelegate
    public int getBaseline() {
        return -1;
    }

    public TimePickerClockDelegate(TimePicker timePicker, Context context, AttributeSet attributeSet, int i, int i2) {
        super(timePicker, context);
        TypedArray obtainStyledAttributes = this.mContext.obtainStyledAttributes(attributeSet, R$styleable.TimePicker, i, i2);
        Resources resources = this.mContext.getResources();
        this.mSelectHours = resources.getString(R$string.select_hours);
        this.mSelectMinutes = resources.getString(R$string.select_minutes);
        View inflate = ((LayoutInflater) this.mContext.getSystemService("layout_inflater")).inflate(obtainStyledAttributes.getResourceId(R$styleable.TimePicker_internalLayout, R$layout.op_time_picker_material), timePicker);
        inflate.setSaveFromParentEnabled(false);
        LinearLayout linearLayout = (LinearLayout) inflate.findViewById(R$id.time_header);
        this.mRadialTimePickerHeader = linearLayout;
        linearLayout.setOnTouchListener(new NearestTouchDelegate());
        NumericTextView numericTextView = (NumericTextView) inflate.findViewById(R$id.hours);
        this.mHourView = numericTextView;
        numericTextView.setOnClickListener(this.mClickListener);
        this.mHourView.setOnFocusChangeListener(this.mFocusListener);
        this.mHourView.setOnDigitEnteredListener(this.mDigitEnteredListener);
        this.mHourView.setAccessibilityDelegate(new ClickActionDelegate(context, R$string.select_hours));
        this.mSeparatorView = (TextView) inflate.findViewById(R$id.separator);
        inflate.findViewById(R$id.separator_shape);
        NumericTextView numericTextView2 = (NumericTextView) inflate.findViewById(R$id.minutes);
        this.mMinuteView = numericTextView2;
        numericTextView2.setOnClickListener(this.mClickListener);
        this.mMinuteView.setOnFocusChangeListener(this.mFocusListener);
        this.mMinuteView.setOnDigitEnteredListener(this.mDigitEnteredListener);
        this.mMinuteView.setAccessibilityDelegate(new ClickActionDelegate(context, R$string.select_minutes));
        this.mMinuteView.setRange(0, 59);
        inflate.findViewById(R$id.separator).setActivated(true);
        ((TextView) inflate.findViewById(R$id.separator)).getPaint().setFakeBoldText(true);
        View findViewById = inflate.findViewById(R$id.ampm_layout);
        this.mAmPmLayout = findViewById;
        findViewById.setOnTouchListener(new NearestTouchDelegate());
        String[] amPmStrings = TimePicker.getAmPmStrings(context);
        RadioButton radioButton = (RadioButton) this.mAmPmLayout.findViewById(R$id.am_label);
        this.mAmLabel = radioButton;
        radioButton.setText(obtainVerbatim(amPmStrings[0]));
        this.mAmLabel.setOnClickListener(this.mClickListener);
        ensureMinimumTextWidth(this.mAmLabel);
        RadioButton radioButton2 = (RadioButton) this.mAmPmLayout.findViewById(R$id.pm_label);
        this.mPmLabel = radioButton2;
        radioButton2.setText(obtainVerbatim(amPmStrings[1]));
        this.mPmLabel.setOnClickListener(this.mClickListener);
        ensureMinimumTextWidth(this.mPmLabel);
        int resourceId = obtainStyledAttributes.getResourceId(R$styleable.TimePicker_android_headerTimeTextAppearance, 0);
        if (resourceId != 0) {
            TypedArray obtainStyledAttributes2 = this.mContext.obtainStyledAttributes(null, ATTRS_TEXT_COLOR, 0, resourceId);
            obtainStyledAttributes2.getColorStateList(0);
            obtainStyledAttributes2.recycle();
        }
        ColorStateList colorStateList = obtainStyledAttributes.getColorStateList(R$styleable.TimePicker_headerTextColor);
        this.mTextInputPickerHeader = inflate.findViewById(R$id.input_header);
        if (colorStateList != null) {
            this.mHourView.setTextColor(colorStateList);
            this.mSeparatorView.setTextColor(colorStateList);
            this.mMinuteView.setTextColor(colorStateList);
            this.mAmLabel.setTextColor(colorStateList);
            this.mPmLabel.setTextColor(colorStateList);
        }
        obtainStyledAttributes.recycle();
        this.mDuration = context.getResources().getInteger(R$integer.op_control_time_325);
        this.mRadialTimeViewScale = 0.0f;
        this.mRadialTimeViewAlpha = 0.0f;
        RadialTimePickerView radialTimePickerView = (RadialTimePickerView) inflate.findViewById(R$id.radial_picker);
        this.mRadialTimePickerView = radialTimePickerView;
        radialTimePickerView.applyAttributes(attributeSet, i, i2);
        this.mRadialTimePickerView.setOnValueSelectedListener(this.mOnValueSelectedListener);
        TextInputTimePickerView textInputTimePickerView = (TextInputTimePickerView) inflate.findViewById(R$id.input_mode);
        this.mTextInputPickerView = textInputTimePickerView;
        textInputTimePickerView.setListener(this.mOnValueTypedListener);
        ImageButton imageButton = (ImageButton) inflate.findViewById(R$id.toggle_mode);
        this.mRadialTimePickerModeButton = imageButton;
        imageButton.setOnClickListener(new View.OnClickListener() {
            /* class com.google.android.material.picker.TimePickerClockDelegate.AnonymousClass1 */

            public void onClick(View view) {
                TimePickerClockDelegate.this.toggleRadialPickerMode();
            }
        });
        this.mAllowAutoAdvance = true;
        updateHourFormat();
        Calendar instance = Calendar.getInstance(this.mLocale);
        this.mTempCalendar = instance;
        initialize(instance.get(11), this.mTempCalendar.get(12), this.mIs24Hour, 0);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void toggleRadialPickerMode() {
        if (!this.mIsToggleTimeMode) {
            if (this.mHeaderPositionY == 0) {
                int[] iArr = new int[2];
                this.mRadialTimePickerHeader.getLocationOnScreen(iArr);
                this.mHeaderPositionY = iArr[1];
                View inputBlock = this.mTextInputPickerView.getInputBlock();
                int abs = Math.abs(((int) this.mTextInputPickerView.getInputBlock().findViewById(R$id.input_hour).getY()) - ((int) this.mRadialTimePickerHeader.findViewById(R$id.hours).getY()));
                this.mHeaderOffset = abs;
                if (abs == 0) {
                    this.mHeaderOffset = Math.round(this.mContext.getResources().getDisplayMetrics().density * 4.0f);
                }
                inputBlock.getLocationOnScreen(iArr);
                this.mInputBlockPositionY = iArr[1];
            }
            if (this.mRadialPickerModeEnabled) {
                animationInInputTimeField();
                this.mRadialTimePickerModeButton.setImageResource(R$drawable.op_btn_clock_material);
                this.mRadialPickerModeEnabled = false;
                return;
            }
            animationOutInputTimeField();
            this.mRadialTimePickerModeButton.setImageResource(R$drawable.op_btn_keyboard_key_material);
            this.mRadialPickerModeEnabled = true;
        }
    }

    private void animationInInputTimeField() {
        this.mTextInputPickerHeader.setVisibility(4);
        this.mTextInputPickerView.setVisibility(4);
        this.mTextInputPickerHeader.setVisibility(0);
        this.mTextInputPickerHeader.animate().alpha(1.0f).setDuration((long) this.mDuration).start();
        this.mRadialTimePickerView.animate().scaleX(this.mRadialTimeViewScale).scaleY(this.mRadialTimeViewScale).alpha(this.mRadialTimeViewAlpha).setDuration((long) this.mDuration).setInterpolator(AnimatorUtils.FastOutSlowInInterpolator).start();
        this.mRadialTimePickerHeader.setTranslationY(0.0f);
        this.mRadialTimePickerHeader.animate().setInterpolator(AnimatorUtils.FastOutSlowInInterpolator).translationY((float) ((this.mInputBlockPositionY - this.mHeaderPositionY) + this.mRadialTimePickerHeader.getPaddingTop() + this.mHeaderOffset)).setDuration((long) this.mDuration).setListener(new Animator.AnimatorListener() {
            /* class com.google.android.material.picker.TimePickerClockDelegate.AnonymousClass2 */

            public void onAnimationCancel(Animator animator) {
            }

            public void onAnimationRepeat(Animator animator) {
            }

            public void onAnimationStart(Animator animator) {
                TimePickerClockDelegate.this.mTextInputPickerView.showInputBlock(true);
                TimePickerClockDelegate.this.mTextInputPickerView.showLabels(true);
                TimePickerClockDelegate.this.mIsToggleTimeMode = true;
            }

            public void onAnimationEnd(Animator animator) {
                TimePickerClockDelegate.this.mRadialTimePickerView.setVisibility(4);
                TimePickerClockDelegate.this.mRadialTimePickerHeader.setVisibility(4);
                TimePickerClockDelegate.this.mTextInputPickerView.setVisibility(0);
                TimePickerClockDelegate.this.mIsToggleTimeMode = false;
            }
        }).start();
    }

    private void animationOutInputTimeField() {
        this.mTextInputPickerView.getInputBlock();
        this.mRadialTimePickerView.setVisibility(0);
        this.mRadialTimePickerHeader.setVisibility(0);
        this.mRadialTimePickerView.setAlpha(0.0f);
        this.mTextInputPickerHeader.animate().alpha(0.0f).setDuration((long) this.mDuration).start();
        this.mTextInputPickerView.showLabels(false);
        this.mRadialTimePickerView.animate().scaleX(1.0f).scaleY(1.0f).alpha(1.0f).setInterpolator(AnimatorUtils.FastOutSlowInInterpolator).setDuration((long) this.mDuration).start();
        this.mRadialTimePickerHeader.animate().translationY(0.0f).setInterpolator(AnimatorUtils.FastOutSlowInInterpolator).setDuration((long) this.mDuration).setListener(new Animator.AnimatorListener() {
            /* class com.google.android.material.picker.TimePickerClockDelegate.AnonymousClass3 */

            public void onAnimationCancel(Animator animator) {
            }

            public void onAnimationRepeat(Animator animator) {
            }

            public void onAnimationStart(Animator animator) {
                TimePickerClockDelegate.this.mTextInputPickerView.showInputBlock(false);
                TimePickerClockDelegate.this.mIsToggleTimeMode = true;
            }

            public void onAnimationEnd(Animator animator) {
                TimePickerClockDelegate.this.mRadialTimePickerView.setVisibility(0);
                TimePickerClockDelegate.this.mRadialTimePickerHeader.setVisibility(0);
                TimePickerClockDelegate.this.mTextInputPickerHeader.setVisibility(4);
                TimePickerClockDelegate.this.mTextInputPickerView.setVisibility(4);
                TimePickerClockDelegate.this.updateTextInputPicker();
                TimePickerClockDelegate.this.mIsToggleTimeMode = false;
            }
        }).start();
    }

    @Override // com.google.android.material.picker.TimePicker.TimePickerDelegate
    public boolean validateInput() {
        return this.mTextInputPickerView.validateInput();
    }

    private static void ensureMinimumTextWidth(TextView textView) {
        textView.measure(0, 0);
        int measuredWidth = textView.getMeasuredWidth();
        textView.setMinWidth(measuredWidth);
        textView.setMinimumWidth(measuredWidth);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0031, code lost:
        if (r3 >= r1) goto L_0x003b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0037, code lost:
        if (r7 != r0.charAt(r3)) goto L_0x003b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0039, code lost:
        r0 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x003b, code lost:
        r0 = false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateHourFormat() {
        /*
        // Method dump skipped, instructions count: 115
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.material.picker.TimePickerClockDelegate.updateHourFormat():void");
    }

    static final CharSequence obtainVerbatim(String str) {
        return new SpannableStringBuilder().append(str, new TtsSpan.VerbatimBuilder(str).build(), 0);
    }

    private static class ClickActionDelegate extends View.AccessibilityDelegate {
        private final AccessibilityNodeInfo.AccessibilityAction mClickAction;

        public ClickActionDelegate(Context context, int i) {
            this.mClickAction = new AccessibilityNodeInfo.AccessibilityAction(16, context.getString(i));
        }

        public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
            accessibilityNodeInfo.addAction(this.mClickAction);
        }
    }

    private void initialize(int i, int i2, boolean z, int i3) {
        this.mCurrentHour = i;
        this.mCurrentMinute = i2;
        this.mIs24Hour = z;
        updateUI(i3);
    }

    private void updateUI(int i) {
        updateHeaderAmPm();
        updateHeaderHour(this.mCurrentHour, false);
        updateHeaderSeparator();
        updateHeaderMinute(this.mCurrentMinute, false);
        updateRadialPicker(i);
        updateTextInputPicker();
        this.mDelegator.invalidate();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateTextInputPicker() {
        this.mTextInputPickerView.updateTextInputValues(getLocalizedHour(this.mCurrentHour), this.mCurrentMinute, this.mCurrentHour < 12 ? 0 : 1, this.mIs24Hour, this.mHourFormatStartsAtZero);
    }

    private void updateRadialPicker(int i) {
        this.mRadialTimePickerView.initialize(this.mCurrentHour, this.mCurrentMinute, this.mIs24Hour);
        setCurrentItemShowing(i, false, true);
    }

    private void updateHeaderAmPm() {
        if (this.mIs24Hour) {
            this.mAmPmLayout.setVisibility(8);
            return;
        }
        boolean startsWith = DateFormat.getBestDateTimePattern(this.mLocale, "hm").startsWith("a");
        setAmPmAtStart(startsWith);
        setInputAmPmAtStart(startsWith);
        updateAmPmLabelStates(this.mCurrentHour < 12 ? 0 : 1);
    }

    private void setInputAmPmAtStart(boolean z) {
        this.mTextInputPickerView.setAmPmAtStart(z);
    }

    private void setAmPmAtStart(boolean z) {
        if (this.mIsAmPmAtStart != z) {
            this.mIsAmPmAtStart = z;
            if (z) {
                this.mRadialTimePickerHeader.removeView(this.mAmPmLayout);
                this.mRadialTimePickerHeader.addView(this.mAmPmLayout, 0);
                return;
            }
            this.mRadialTimePickerHeader.removeView(this.mAmPmLayout);
            this.mRadialTimePickerHeader.addView(this.mAmPmLayout);
        }
    }

    @Override // com.google.android.material.picker.TimePicker.TimePickerDelegate
    public void setHour(int i) {
        setHourInternal(i, 0, true);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setHourInternal(int i, int i2, boolean z) {
        if (this.mCurrentHour != i) {
            this.mCurrentHour = i;
            updateHeaderHour(i, z);
            updateHeaderAmPm();
            int i3 = 1;
            if (i2 != 1) {
                this.mRadialTimePickerView.setCurrentHour(i);
                RadialTimePickerView radialTimePickerView = this.mRadialTimePickerView;
                if (i < 12) {
                    i3 = 0;
                }
                radialTimePickerView.setAmOrPm(i3);
            }
            if (i2 != 2) {
                updateTextInputPicker();
            }
            this.mDelegator.invalidate();
            onTimeChanged();
        }
    }

    @Override // com.google.android.material.picker.TimePicker.TimePickerDelegate
    public int getHour() {
        int currentHour = this.mRadialTimePickerView.getCurrentHour();
        if (this.mIs24Hour) {
            return currentHour;
        }
        if (this.mRadialTimePickerView.getAmOrPm() == 1) {
            return (currentHour % 12) + 12;
        }
        return currentHour % 12;
    }

    @Override // com.google.android.material.picker.TimePicker.TimePickerDelegate
    public void setMinute(int i) {
        setMinuteInternal(i, 0);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setMinuteInternal(int i, int i2) {
        if (this.mCurrentMinute != i) {
            this.mCurrentMinute = i;
            updateHeaderMinute(i, true);
            if (i2 != 1) {
                this.mRadialTimePickerView.setCurrentMinute(i);
            }
            if (i2 != 2) {
                updateTextInputPicker();
            }
            this.mDelegator.invalidate();
            onTimeChanged();
        }
    }

    @Override // com.google.android.material.picker.TimePicker.TimePickerDelegate
    public int getMinute() {
        return this.mRadialTimePickerView.getCurrentMinute();
    }

    @Override // com.google.android.material.picker.TimePicker.TimePickerDelegate
    public void setIs24Hour(boolean z) {
        this.mTextInputPickerView.setIs24Hour(z);
        if (this.mIs24Hour != z) {
            this.mIs24Hour = z;
            this.mCurrentHour = getHour();
            updateHourFormat();
            updateUI(this.mRadialTimePickerView.getCurrentItemShowing());
        }
    }

    @Override // com.google.android.material.picker.TimePicker.TimePickerDelegate
    public boolean is24Hour() {
        return this.mIs24Hour;
    }

    @Override // com.google.android.material.picker.TimePicker.TimePickerDelegate
    public void setEnabled(boolean z) {
        this.mHourView.setEnabled(z);
        this.mMinuteView.setEnabled(z);
        this.mAmLabel.setEnabled(z);
        this.mPmLabel.setEnabled(z);
        this.mRadialTimePickerView.setEnabled(z);
        this.mIsEnabled = z;
    }

    @Override // com.google.android.material.picker.TimePicker.TimePickerDelegate
    public boolean isEnabled() {
        return this.mIsEnabled;
    }

    @Override // com.google.android.material.picker.TimePicker.TimePickerDelegate
    public Parcelable onSaveInstanceState(Parcelable parcelable) {
        return new TimePicker.AbstractTimePickerDelegate.SavedState(parcelable, getHour(), getMinute(), is24Hour(), getCurrentItemShowing());
    }

    @Override // com.google.android.material.picker.TimePicker.TimePickerDelegate
    public void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable instanceof TimePicker.AbstractTimePickerDelegate.SavedState) {
            TimePicker.AbstractTimePickerDelegate.SavedState savedState = (TimePicker.AbstractTimePickerDelegate.SavedState) parcelable;
            initialize(savedState.getHour(), savedState.getMinute(), savedState.is24HourMode(), savedState.getCurrentItemShowing());
            this.mRadialTimePickerView.invalidate();
        }
    }

    @Override // com.google.android.material.picker.TimePicker.TimePickerDelegate
    public View getHourView() {
        return this.mHourView;
    }

    @Override // com.google.android.material.picker.TimePicker.TimePickerDelegate
    public View getMinuteView() {
        return this.mMinuteView;
    }

    @Override // com.google.android.material.picker.TimePicker.TimePickerDelegate
    public View getAmView() {
        return this.mAmLabel;
    }

    @Override // com.google.android.material.picker.TimePicker.TimePickerDelegate
    public View getPmView() {
        return this.mPmLabel;
    }

    private int getCurrentItemShowing() {
        return this.mRadialTimePickerView.getCurrentItemShowing();
    }

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

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void tryVibrate() {
        this.mDelegator.performHapticFeedback(4);
    }

    private void updateAmPmLabelStates(int i) {
        boolean z = false;
        boolean z2 = i == 0;
        this.mAmLabel.setActivated(z2);
        this.mAmLabel.setChecked(z2);
        this.mAmLabel.getPaint().setFakeBoldText(z2);
        if (i == 1) {
            z = true;
        }
        this.mPmLabel.setActivated(z);
        this.mPmLabel.setChecked(z);
        this.mPmLabel.getPaint().setFakeBoldText(z);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private int getLocalizedHour(int i) {
        if (!this.mIs24Hour) {
            i %= 12;
        }
        if (this.mHourFormatStartsAtZero || i != 0) {
            return i;
        }
        return this.mIs24Hour ? 24 : 12;
    }

    private void updateHeaderHour(int i, boolean z) {
        this.mHourView.setValue(getLocalizedHour(i));
        if (z) {
            tryAnnounceForAccessibility(this.mHourView.getText(), true);
        }
    }

    private void updateHeaderMinute(int i, boolean z) {
        this.mMinuteView.setValue(i);
        if (z) {
            tryAnnounceForAccessibility(this.mMinuteView.getText(), false);
        }
    }

    private void updateHeaderSeparator() {
        String str;
        String bestDateTimePattern = DateFormat.getBestDateTimePattern(this.mLocale, this.mIs24Hour ? "Hm" : "hm");
        int lastIndexOfAny = lastIndexOfAny(bestDateTimePattern, new char[]{'H', 'h', 'K', 'k'});
        if (lastIndexOfAny == -1) {
            str = ":";
        } else {
            str = Character.toString(bestDateTimePattern.charAt(lastIndexOfAny + 1));
        }
        this.mTextInputPickerView.updateSeparator(str);
    }

    private static int lastIndexOfAny(String str, char[] cArr) {
        int length = cArr.length;
        if (length <= 0) {
            return -1;
        }
        for (int length2 = str.length() - 1; length2 >= 0; length2--) {
            char charAt = str.charAt(length2);
            for (char c : cArr) {
                if (charAt == c) {
                    return length2;
                }
            }
        }
        return -1;
    }

    private void tryAnnounceForAccessibility(CharSequence charSequence, boolean z) {
        if (this.mLastAnnouncedIsHour != z || !charSequence.equals(this.mLastAnnouncedText)) {
            this.mDelegator.announceForAccessibility(charSequence);
            this.mLastAnnouncedText = charSequence;
            this.mLastAnnouncedIsHour = z;
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setCurrentItemShowing(int i, boolean z, boolean z2) {
        this.mRadialTimePickerView.setCurrentItemShowing(i, z);
        if (i == 0) {
            if (z2) {
                this.mDelegator.announceForAccessibility(this.mSelectHours);
            }
        } else if (z2) {
            this.mDelegator.announceForAccessibility(this.mSelectMinutes);
        }
        boolean z3 = false;
        this.mHourView.setActivated(i == 0);
        NumericTextView numericTextView = this.mMinuteView;
        if (i == 1) {
            z3 = true;
        }
        numericTextView.setActivated(z3);
        if (i == 0) {
            resetInputTimeTextAppearance(R$style.OPTextAppearance_Material_TimePicker_TimeLabel, this.mHourView);
            resetInputTimeTextAppearance(R$style.OPTextAppearance_Material_TimePicker_TimeLabelUnActivated, this.mMinuteView);
            return;
        }
        resetInputTimeTextAppearance(R$style.OPTextAppearance_Material_TimePicker_TimeLabel, this.mMinuteView);
        resetInputTimeTextAppearance(R$style.OPTextAppearance_Material_TimePicker_TimeLabelUnActivated, this.mHourView);
    }

    private void resetInputTimeTextAppearance(int i, TextView textView) {
        if (SystemUtils.isAtLeastM()) {
            textView.setTextAppearance(i);
        } else {
            textView.setTextAppearance(this.mContext, i);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setAmOrPm(int i) {
        updateAmPmLabelStates(i);
        if (this.mRadialTimePickerView.setAmOrPm(i)) {
            this.mCurrentHour = getHour();
            updateTextInputPicker();
            TimePicker.OnTimeChangedListener onTimeChangedListener = this.mOnTimeChangedListener;
            if (onTimeChangedListener != null) {
                onTimeChangedListener.onTimeChanged(this.mDelegator, getHour(), getMinute());
            }
        }
    }

    private static class NearestTouchDelegate implements View.OnTouchListener {
        private View mInitialTouchTarget;

        private NearestTouchDelegate() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked == 0) {
                if (view instanceof ViewGroup) {
                    this.mInitialTouchTarget = findNearestChild((ViewGroup) view, (int) motionEvent.getX(), (int) motionEvent.getY());
                } else {
                    this.mInitialTouchTarget = null;
                }
            }
            View view2 = this.mInitialTouchTarget;
            if (view2 == null) {
                return false;
            }
            float scrollX = (float) (view.getScrollX() - view2.getLeft());
            float scrollY = (float) (view.getScrollY() - view2.getTop());
            motionEvent.offsetLocation(scrollX, scrollY);
            boolean dispatchTouchEvent = view2.dispatchTouchEvent(motionEvent);
            motionEvent.offsetLocation(-scrollX, -scrollY);
            if (actionMasked == 1 || actionMasked == 3) {
                this.mInitialTouchTarget = null;
            }
            return dispatchTouchEvent;
        }

        private View findNearestChild(ViewGroup viewGroup, int i, int i2) {
            int childCount = viewGroup.getChildCount();
            View view = null;
            int i3 = Integer.MAX_VALUE;
            for (int i4 = 0; i4 < childCount; i4++) {
                View childAt = viewGroup.getChildAt(i4);
                int left = i - (childAt.getLeft() + (childAt.getWidth() / 2));
                int top = i2 - (childAt.getTop() + (childAt.getHeight() / 2));
                int i5 = (left * left) + (top * top);
                if (i3 > i5) {
                    view = childAt;
                    i3 = i5;
                }
            }
            return view;
        }
    }
}
