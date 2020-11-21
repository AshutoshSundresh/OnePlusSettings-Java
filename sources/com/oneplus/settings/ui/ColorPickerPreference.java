package com.oneplus.settings.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0006R$color;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.R$styleable;
import com.oneplus.custom.utils.OpCustomizeSettings;
import com.oneplus.settings.utils.OPUtils;
import java.util.Arrays;

public class ColorPickerPreference extends CustomDialogPreference {
    private String mColor;
    private Context mContext;
    private CustomColorClickListener mCustomColorClickListener;
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

    public interface CustomColorClickListener {
        void onCustomColorClick();
    }

    /* access modifiers changed from: protected */
    public void onSetColor(String str) {
    }

    /* access modifiers changed from: protected */
    public void onSetColorPalette(String[] strArr) {
    }

    public ColorPickerPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mDefaultColor = "";
        this.mVisibility = false;
        setLayoutResource(C0012R$layout.op_colorpicker_preference);
        this.mContext = context;
        this.mDefaultColors = new String[]{context.getResources().getString(C0006R$color.op_primary_default_light), this.mContext.getResources().getString(C0006R$color.op_primary_default_dark)};
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.ColorPickerPreference, 0, 0);
        this.mRippleEffectColor = obtainStyledAttributes.getColor(R$styleable.ColorPickerPreference_rippleEffectColor, context.getResources().getColor(C0006R$color.colorpicker_ripple_effect_color));
        this.mDisabledCellColor = obtainStyledAttributes.getColor(R$styleable.ColorPickerPreference_disabledCellColor, context.getResources().getColor(C0006R$color.colorpicker_disabled_cell_color));
        obtainStyledAttributes.recycle();
        setNeutralButtonText(C0017R$string.color_picker_default);
        setNegativeButtonText(17039360);
        setPositiveButtonText(17039370);
        setDialogLayoutResource(C0012R$layout.op_preference_dialog_colorpicker);
        if (getSummary() == null) {
            this.mUseColorLabelAsSummary = true;
        } else {
            this.mUseColorLabelAsSummary = false;
        }
    }

    public ColorPickerPreference(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public ColorPickerPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        String str;
        super.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.setDividerAllowedBelow(false);
        preferenceViewHolder.setDividerAllowedAbove(false);
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
            if (!TextUtils.isEmpty(str)) {
                this.mImageView.setImageTintList(ColorStateList.valueOf(OPUtils.parseColor(str)));
            }
            if (this.mVisibility) {
                this.mImageView.setVisibility(0);
            }
        }
    }

    public void setMessage(CharSequence charSequence) {
        this.mMessage.setText(charSequence);
    }

    public void setMessage(int i) {
        this.mMessage.setText(getContext().getString(i));
    }

    public void init() {
        String string = Settings.System.getString(this.mContext.getContentResolver(), "oneplus_accent_color");
        this.mColor = string;
        this.mTmpColor = string;
        updateSummary();
        setSelection(this.mTmpColor, 0);
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
            this.mImageView.setImageTintList(ColorStateList.valueOf(OPUtils.parseColor(str2)));
        }
        persistString(str);
    }

    public RippleDrawable createRippleDrawable(String str) {
        return new RippleDrawable(new ColorStateList(new int[][]{new int[0]}, new int[]{this.mRippleEffectColor}), new ColorDrawable(TextUtils.isEmpty(str) ? this.mDisabledCellColor : OPUtils.parseColor(str)), null);
    }

    public void setSelection(String str, int i) {
        if (str != null) {
            String[] strArr = this.mPalette;
            int indexOf = strArr == null ? -1 : Arrays.asList(strArr).indexOf(str);
            if (indexOf == -1) {
                if (OPUtils.isWhiteModeOn(this.mContext.getContentResolver())) {
                    indexOf = Settings.System.getInt(this.mContext.getContentResolver(), "oem_white_mode_accent_color_index", 0);
                } else if (OPUtils.isBlackModeOn(this.mContext.getContentResolver())) {
                    indexOf = Settings.System.getInt(this.mContext.getContentResolver(), "oem_black_mode_accent_color_index", 0);
                }
            }
            int[] iArr = {C0010R$id.check_0, C0010R$id.check_1, C0010R$id.check_2, C0010R$id.check_3, C0010R$id.check_4, C0010R$id.check_5, C0010R$id.check_6, C0010R$id.check_7, C0010R$id.check_8, C0010R$id.check_9, C0010R$id.check_10, C0010R$id.check_11};
            if (indexOf >= 0) {
                this.mViews[indexOf].findViewById(iArr[indexOf]).setVisibility(i);
            } else {
                this.mViews[11].findViewById(iArr[11]).setVisibility(8);
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
        int[] iArr = {C0010R$id.color_0, C0010R$id.color_1, C0010R$id.color_2, C0010R$id.color_3, C0010R$id.color_4, C0010R$id.color_5, C0010R$id.color_6, C0010R$id.color_7, C0010R$id.color_8, C0010R$id.color_9, C0010R$id.color_10, C0010R$id.color_11};
        this.mViews = new View[this.mPalette.length];
        for (int i = 0; i < this.mPalette.length; i++) {
            this.mViews[i] = view.findViewById(iArr[i]);
            if (i == 11) {
                String string = Settings.System.getString(this.mContext.getContentResolver(), "oneplus_accent_color");
                if (OPUtils.isWhiteModeOn(this.mContext.getContentResolver())) {
                    string = Settings.System.getString(this.mContext.getContentResolver(), "oneplus_white_custom_accent_color");
                } else if (OPUtils.isBlackModeOn(this.mContext.getContentResolver())) {
                    string = Settings.System.getString(this.mContext.getContentResolver(), "oneplus_black_custom_accent_color");
                }
                if (!TextUtils.isEmpty(string)) {
                    this.mViews[i].setBackground(createRippleDrawable(string));
                } else {
                    this.mViews[i].setBackgroundResource(C0008R$drawable.op_custom_color_default_bg);
                }
            } else {
                this.mViews[i].setBackground(createRippleDrawable(this.mPalette[i]));
            }
            this.mViews[i].setTag(Integer.valueOf(i));
            this.mViews[i].setOnClickListener(length > 0 ? new View.OnClickListener() {
                /* class com.oneplus.settings.ui.ColorPickerPreference.AnonymousClass1 */

                public void onClick(View view) {
                    int intValue = ((Integer) view.getTag()).intValue();
                    if (!ColorPickerPreference.this.mPalette[intValue].equals(ColorPickerPreference.this.mTmpColor) || intValue == 11) {
                        ColorPickerPreference colorPickerPreference = ColorPickerPreference.this;
                        colorPickerPreference.setSelection(colorPickerPreference.mTmpColor, 8);
                        ColorPickerPreference colorPickerPreference2 = ColorPickerPreference.this;
                        colorPickerPreference2.setTmpColor(colorPickerPreference2.mPalette[intValue]);
                    }
                    if (intValue == 11 && ColorPickerPreference.this.mCustomColorClickListener != null) {
                        ColorPickerPreference.this.mCustomColorClickListener.onCustomColorClick();
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
        super.onDialogClosed(i);
        boolean z = true;
        boolean z2 = i == -1;
        boolean z3 = i == -2;
        boolean z4 = i == -3;
        if (z2) {
            String string = Settings.System.getString(this.mContext.getContentResolver(), "oneplus_accent_color");
            if (!TextUtils.isEmpty(string) && ((string == null || string.equals(this.mTmpColor)) && (string == null || string.equals(this.mColor)))) {
                z = false;
            }
            if (z) {
                setColor(this.mTmpColor);
            }
        } else if (z3) {
            this.mColor = Settings.System.getString(this.mContext.getContentResolver(), "oneplus_accent_color");
            updateSummary();
        } else if (z4) {
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
            if (indexOf == -1) {
                if (OPUtils.isWhiteModeOn(this.mContext.getContentResolver())) {
                    indexOf = Settings.System.getInt(this.mContext.getContentResolver(), "oem_white_mode_accent_color_index", 0);
                } else if (OPUtils.isBlackModeOn(this.mContext.getContentResolver())) {
                    indexOf = Settings.System.getInt(this.mContext.getContentResolver(), "oem_black_mode_accent_color_index", 0);
                }
            }
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
                } else if (OPUtils.isThemeOn(this.mContext.getContentResolver()) && OpCustomizeSettings.CUSTOM_TYPE.SW.equals(OpCustomizeSettings.getCustomType())) {
                    setSummary(getContext().getString(C0017R$string.op_starwar_mode_cannot_change_color_accent));
                    ImageView imageView2 = this.mImageView;
                    if (imageView2 != null) {
                        imageView2.setVisibility(8);
                    }
                } else if (OPUtils.isThemeOn(this.mContext.getContentResolver()) && OpCustomizeSettings.CUSTOM_TYPE.AVG.equals(OpCustomizeSettings.getCustomType())) {
                    setSummary(getContext().getString(C0017R$string.op_theme_2__cannot_change_color_accent));
                    ImageView imageView3 = this.mImageView;
                    if (imageView3 != null) {
                        imageView3.setVisibility(8);
                    }
                } else if (!OPUtils.isThemeOn(this.mContext.getContentResolver()) || !OpCustomizeSettings.CUSTOM_TYPE.MCL.equals(OpCustomizeSettings.getCustomType())) {
                    setSummary(getContext().getString(C0017R$string.op_primary_default_light_label));
                } else {
                    setSummary(getContext().getString(C0017R$string.op_theme_3_cannot_change_color_accent));
                    ImageView imageView4 = this.mImageView;
                    if (imageView4 != null) {
                        imageView4.setVisibility(8);
                    }
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
            /* class com.oneplus.settings.ui.ColorPickerPreference.SavedState.AnonymousClass1 */

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
