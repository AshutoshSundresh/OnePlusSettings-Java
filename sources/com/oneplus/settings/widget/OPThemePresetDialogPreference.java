package com.oneplus.settings.widget;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckedTextView;
import androidx.core.content.res.TypedArrayUtils;
import androidx.preference.Preference;
import androidx.preference.R$attr;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0017R$string;
import com.android.settingslib.CustomDialogPreferenceCompat;
import com.oneplus.settings.utils.OPThemeUtils;
import com.oneplus.settings.utils.OPUtils;

public class OPThemePresetDialogPreference extends CustomDialogPreferenceCompat implements View.OnClickListener {
    private OnOPThemePresetDialogClickListener mOnOPThemePresetDialogClickListener;
    private CheckedTextView mThemeColorChecked;
    private CheckedTextView mThemeDarkChecked;
    private CheckedTextView mThemeLightChecked;
    private CheckedTextView mThemeMCLChecked;
    private int themeChooose;

    public interface OnOPThemePresetDialogClickListener {
        void onDialogClickListener(int i);
    }

    public OPThemePresetDialogPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public OPThemePresetDialogPreference(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public OPThemePresetDialogPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, TypedArrayUtils.getAttr(context, R$attr.dialogPreferenceStyle, 16842897));
    }

    public OPThemePresetDialogPreference(Context context) {
        this(context, null);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.CustomDialogPreferenceCompat
    public void onBindDialogView(View view) {
        int i;
        super.onBindDialogView(view);
        CheckedTextView checkedTextView = (CheckedTextView) view.findViewById(C0010R$id.oneplus_theme_preset_mcl);
        this.mThemeMCLChecked = checkedTextView;
        checkedTextView.setOnClickListener(this);
        CheckedTextView checkedTextView2 = (CheckedTextView) view.findViewById(C0010R$id.oneplus_theme_preset_color);
        this.mThemeColorChecked = checkedTextView2;
        checkedTextView2.setOnClickListener(this);
        CheckedTextView checkedTextView3 = (CheckedTextView) view.findViewById(C0010R$id.oneplus_theme_preset_light);
        this.mThemeLightChecked = checkedTextView3;
        checkedTextView3.setOnClickListener(this);
        CheckedTextView checkedTextView4 = (CheckedTextView) view.findViewById(C0010R$id.oneplus_theme_preset_dark);
        this.mThemeDarkChecked = checkedTextView4;
        checkedTextView4.setOnClickListener(this);
        if (OPThemeUtils.isSupportMclTheme()) {
            this.mThemeMCLChecked.setText(C0017R$string.oneplus_theme_preset_mcl);
        } else if (OPThemeUtils.isSupportAVGTheme()) {
            this.mThemeMCLChecked.setText(C0017R$string.op_theme_2__title);
        } else if (OPThemeUtils.isSupportSwTheme()) {
            this.mThemeMCLChecked.setText(C0017R$string.op_starwar_mode_title);
        }
        if (!OPThemeUtils.isSupportCustomeTheme()) {
            this.mThemeMCLChecked.setVisibility(8);
        }
        int currentCustomizationTheme = OPThemeUtils.getCurrentCustomizationTheme(getContext());
        if (OPUtils.isBlackModeOn(getContext().getContentResolver())) {
            i = C0008R$drawable.op_btn_image_single_choice_selector;
        } else {
            i = C0008R$drawable.op_btn_image_single_choice_selector;
        }
        this.mThemeColorChecked.setCheckMarkDrawable(i);
        this.mThemeLightChecked.setCheckMarkDrawable(i);
        this.mThemeDarkChecked.setCheckMarkDrawable(i);
        this.mThemeMCLChecked.setCheckMarkDrawable(i);
        if (currentCustomizationTheme == 2) {
            this.mThemeColorChecked.setChecked(true);
        } else if (currentCustomizationTheme == 0) {
            this.mThemeLightChecked.setChecked(true);
        } else if (currentCustomizationTheme == 1) {
            this.mThemeDarkChecked.setChecked(true);
        } else if (currentCustomizationTheme == 3) {
            this.mThemeMCLChecked.setChecked(true);
        }
    }

    /* access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public Parcelable onSaveInstanceState() {
        Parcelable onSaveInstanceState = super.onSaveInstanceState();
        if (isPersistent()) {
            return onSaveInstanceState;
        }
        SavedState savedState = new SavedState(onSaveInstanceState);
        savedState.value = this.themeChooose;
        return savedState;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable == null || !parcelable.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(parcelable);
        } else {
            super.onRestoreInstanceState(((SavedState) parcelable).getSuperState());
        }
    }

    public void onClick(View view) {
        if (view instanceof CheckedTextView) {
            this.themeChooose = 2;
            if (this.mThemeMCLChecked == view) {
                this.themeChooose = 3;
            }
            if (this.mThemeColorChecked == view) {
                this.themeChooose = 2;
            }
            if (this.mThemeLightChecked == view) {
                this.themeChooose = 0;
            }
            if (this.mThemeDarkChecked == view) {
                this.themeChooose = 1;
            }
            if (this.mOnOPThemePresetDialogClickListener != null) {
                getDialog().dismiss();
                this.mOnOPThemePresetDialogClickListener.onDialogClickListener(this.themeChooose);
            }
        }
    }

    /* access modifiers changed from: private */
    public static class SavedState extends Preference.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            /* class com.oneplus.settings.widget.OPThemePresetDialogPreference.SavedState.AnonymousClass1 */

            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        int value;

        public SavedState(Parcel parcel) {
            super(parcel);
            this.value = parcel.readInt();
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.value);
        }

        public SavedState(Parcelable parcelable) {
            super(parcelable);
        }
    }
}
