package com.oneplus.settings.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0006R$color;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.R$styleable;
import com.oneplus.settings.utils.OPUtils;
import java.util.Arrays;

public class OPLedColorPickerPreference extends CustomDialogPreference {
    private String mColor;
    private Context mContext;
    private String mDefaultColor;
    private String[] mDefaultColors;
    private int mDisabledCellColor;
    ImageView mImageView;
    private TextView mMessage;
    private CharSequence mMessageText;
    private String[] mPalette;
    private int[] mPaletteNamesResIds;
    private int mRippleEffectColor;
    private String mTmpColor;
    private boolean mUseColorLabelAsSummary;
    private View[] mViews;
    private boolean mVisibility;

    /* access modifiers changed from: protected */
    public void onSetColor(String str) {
    }

    /* access modifiers changed from: protected */
    public void onSetColorPalette(String[] strArr) {
    }

    public OPLedColorPickerPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mDefaultColor = "";
        this.mVisibility = false;
        setLayoutResource(C0012R$layout.op_led_colorpicker_preference);
        this.mContext = context;
        this.mDefaultColors = new String[]{context.getResources().getString(C0006R$color.op_primary_default_light), this.mContext.getResources().getString(C0006R$color.op_primary_default_dark)};
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.ColorPickerPreference, 0, 0);
        this.mRippleEffectColor = obtainStyledAttributes.getColor(R$styleable.ColorPickerPreference_rippleEffectColor, context.getResources().getColor(C0006R$color.colorpicker_ripple_effect_color));
        this.mDisabledCellColor = obtainStyledAttributes.getColor(R$styleable.ColorPickerPreference_disabledCellColor, context.getResources().getColor(C0006R$color.colorpicker_disabled_cell_color));
        obtainStyledAttributes.recycle();
        setNeutralButtonText(C0017R$string.color_picker_default);
        setNegativeButtonText(17039360);
        setPositiveButtonText(17039370);
        setDialogLayoutResource(C0012R$layout.op_led_preference_dialog_colorpicker);
        if (getSummary() == null) {
            this.mUseColorLabelAsSummary = true;
        } else {
            this.mUseColorLabelAsSummary = false;
        }
    }

    public OPLedColorPickerPreference(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public OPLedColorPickerPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public OPLedColorPickerPreference(Context context) {
        this(context, null);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        String str;
        super.onBindViewHolder(preferenceViewHolder);
        ImageView imageView = (ImageView) preferenceViewHolder.findViewById(C0010R$id.secondary_icon);
        this.mImageView = imageView;
        if (imageView != null) {
            imageView.getDrawable();
            String str2 = this.mColor;
            if (str2 == null || TextUtils.isEmpty(str2)) {
                str = this.mDefaultColor;
            } else {
                str = this.mColor;
            }
            this.mImageView.setImageTintList(ColorStateList.valueOf(Color.parseColor(str)));
            if (this.mVisibility) {
                this.mImageView.setVisibility(0);
            }
        }
    }

    public void setImageViewVisibility() {
        this.mVisibility = true;
    }

    public void setMessage(CharSequence charSequence) {
        this.mMessage.setText(charSequence);
    }

    public void setMessage(int i) {
        this.mMessage.setText(getContext().getString(i));
    }

    public void setMessageText(int i) {
        this.mMessageText = getContext().getString(i);
    }

    public void init() {
        String color = getColor();
        this.mColor = color;
        this.mTmpColor = color;
        setSelection(color, 0);
    }

    public void setColor(String str) {
        String str2;
        this.mColor = str;
        updateSummary();
        if (callChangeListener(this.mColor)) {
            onSetColor(this.mColor);
        }
        ImageView imageView = this.mImageView;
        if (imageView != null) {
            imageView.getDrawable();
            String str3 = this.mColor;
            if (str3 == null || TextUtils.isEmpty(str3)) {
                str2 = this.mDefaultColor;
            } else {
                str2 = str;
            }
            this.mImageView.setImageTintList(ColorStateList.valueOf(Color.parseColor(str2)));
        }
        persistString(str);
    }

    public String getColor() {
        String str = this.mColor;
        return str != null ? str : getPersistedString(getDefaultColor());
    }

    public void setDefaultColor(String str) {
        this.mDefaultColor = str;
    }

    public String getDefaultColor() {
        return this.mDefaultColor;
    }

    public void setColorPalette(String[] strArr) {
        this.mPalette = strArr;
    }

    public RippleDrawable createRippleDrawable(String str) {
        return new RippleDrawable(new ColorStateList(new int[][]{new int[0]}, new int[]{this.mRippleEffectColor}), new ColorDrawable(TextUtils.isEmpty(str) ? this.mDisabledCellColor : Color.parseColor(str)), null);
    }

    public void setSelection(String str, int i) {
        if (str != null) {
            String[] strArr = this.mPalette;
            int indexOf = strArr == null ? -1 : Arrays.asList(strArr).indexOf(str);
            if (indexOf >= 0) {
                this.mViews[indexOf].findViewById(new int[]{C0010R$id.check_0, C0010R$id.check_1, C0010R$id.check_2, C0010R$id.check_3, C0010R$id.check_4, C0010R$id.check_5, C0010R$id.check_6, C0010R$id.check_7}[indexOf]).setVisibility(i);
            }
        }
    }

    @Override // androidx.preference.Preference
    public void setEnabled(boolean z) {
        super.setEnabled(z);
    }

    /* access modifiers changed from: protected */
    @Override // com.oneplus.settings.ui.CustomDialogPreference
    public void onBindDialogView(View view) {
        super.onBindDialogView(view);
        onSetColorPalette(this.mPalette);
        updateSummary();
        int length = this.mPalette.length;
        if (length != 0 && length <= 4) {
            view.findViewById(C0010R$id.colors_row_2).setVisibility(8);
        }
        int[] iArr = {C0010R$id.color_0, C0010R$id.color_1, C0010R$id.color_2, C0010R$id.color_3, C0010R$id.color_4, C0010R$id.color_5, C0010R$id.color_6, C0010R$id.color_7};
        this.mViews = new View[this.mPalette.length];
        for (int i = 0; i < this.mPalette.length; i++) {
            this.mViews[i] = view.findViewById(iArr[i]);
            this.mViews[i].setBackground(createRippleDrawable(this.mPalette[i]));
            this.mViews[i].setTag(Integer.valueOf(i));
            this.mViews[i].setOnClickListener(length > 0 ? new View.OnClickListener() {
                /* class com.oneplus.settings.ui.OPLedColorPickerPreference.AnonymousClass1 */

                public void onClick(View view) {
                    int intValue = ((Integer) view.getTag()).intValue();
                    if (!OPLedColorPickerPreference.this.mPalette[intValue].equals(OPLedColorPickerPreference.this.mTmpColor)) {
                        OPLedColorPickerPreference oPLedColorPickerPreference = OPLedColorPickerPreference.this;
                        oPLedColorPickerPreference.setSelection(oPLedColorPickerPreference.mTmpColor, 8);
                        OPLedColorPickerPreference oPLedColorPickerPreference2 = OPLedColorPickerPreference.this;
                        oPLedColorPickerPreference2.setTmpColor(oPLedColorPickerPreference2.mPalette[intValue]);
                    }
                }
            } : null);
        }
        this.mMessage = (TextView) view.findViewById(C0010R$id.message);
        CharSequence charSequence = this.mMessageText;
        if (charSequence != null) {
            setMessage(charSequence);
        } else {
            setMessage(C0017R$string.color_picker_message_default);
        }
        init();
    }

    /* access modifiers changed from: protected */
    @Override // com.oneplus.settings.ui.CustomDialogPreference
    public void onDialogClosed(int i) {
        String str;
        String str2;
        super.onDialogClosed(i);
        boolean z = true;
        boolean z2 = i == -1;
        boolean z3 = i == -3;
        if (z2) {
            if (!(this.mColor == null && this.mTmpColor == null) && ((str2 = this.mColor) == null || !str2.equals(this.mTmpColor))) {
                z = false;
            }
            if (!z) {
                setColor(this.mTmpColor);
            }
        } else if (z3) {
            if (!(this.mColor == null && this.mDefaultColor == null) && ((str = this.mColor) == null || !str.equals(this.mDefaultColor))) {
                z = false;
            }
            if (!z) {
                setColor(this.mDefaultColor);
            }
        }
    }

    /* access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public void onSetInitialValue(boolean z, Object obj) {
        setColor(z ? getPersistedString(this.mDefaultColor) : (String) obj);
    }

    /* access modifiers changed from: protected */
    @Override // com.oneplus.settings.ui.CustomDialogPreference, androidx.preference.Preference
    public Parcelable onSaveInstanceState() {
        Parcelable onSaveInstanceState = super.onSaveInstanceState();
        if (this.mTmpColor == null) {
            return onSaveInstanceState;
        }
        SavedState savedState = new SavedState(onSaveInstanceState);
        savedState.tmpColor = this.mTmpColor;
        return savedState;
    }

    /* access modifiers changed from: protected */
    @Override // com.oneplus.settings.ui.CustomDialogPreference, androidx.preference.Preference
    public void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable == null || !parcelable.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        if (!savedState.tmpColor.equals(this.mTmpColor) && this.mViews != null) {
            setSelection(this.mTmpColor, 8);
            setTmpColor(savedState.tmpColor);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setTmpColor(String str) {
        this.mTmpColor = str;
        setSelection(str, 0);
    }

    private void updateSummary() {
        int[] iArr;
        if (this.mUseColorLabelAsSummary || ((iArr = this.mPaletteNamesResIds) != null && iArr.length >= 0)) {
            String[] strArr = this.mPalette;
            int indexOf = strArr == null ? -1 : Arrays.asList(strArr).indexOf(this.mColor);
            int[] iArr2 = this.mPaletteNamesResIds;
            if (iArr2 == null || indexOf < 0 || indexOf >= iArr2.length) {
                String str = this.mColor;
                if (str != null && !str.equals(this.mDefaultColor) && !TextUtils.isEmpty(this.mColor) && !isDefaultColor()) {
                    setSummary(getContext().getString(C0017R$string.color_picker_unknown_label));
                } else if (OPUtils.isAndroidModeOn(this.mContext.getContentResolver())) {
                    setSummary(getContext().getString(C0017R$string.oneplus_colorful_mode_cannot_change_color_accent));
                    ImageView imageView = this.mImageView;
                    if (imageView != null) {
                        imageView.setVisibility(8);
                    }
                } else {
                    setSummary(getContext().getString(C0017R$string.op_primary_default_light_label));
                }
            } else {
                setSummary(getContext().getString(this.mPaletteNamesResIds[indexOf]));
            }
        }
    }

    private boolean isDefaultColor() {
        int i = 0;
        while (true) {
            String[] strArr = this.mDefaultColors;
            if (i >= strArr.length) {
                return false;
            }
            if (strArr[i].equals(this.mColor)) {
                return true;
            }
            i++;
        }
    }

    /* access modifiers changed from: private */
    public static class SavedState extends Preference.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            /* class com.oneplus.settings.ui.OPLedColorPickerPreference.SavedState.AnonymousClass1 */

            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        String tmpColor;

        public SavedState(Parcel parcel) {
            super(parcel);
            try {
                this.tmpColor = parcel.readString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeString(this.tmpColor);
        }
    }
}
