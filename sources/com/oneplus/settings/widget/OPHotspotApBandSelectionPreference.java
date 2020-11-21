package com.oneplus.settings.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import com.android.settings.C0003R$array;
import com.android.settings.C0010R$id;
import com.android.settingslib.CustomDialogPreferenceCompat;
import java.util.ArrayList;

public class OPHotspotApBandSelectionPreference extends CustomDialogPreferenceCompat implements DialogInterface.OnShowListener, RadioGroup.OnCheckedChangeListener {
    static final String KEY_CHECKED_BANDS = "checked_bands";
    static final String KEY_HOTSPOT_SUPER_STATE = "hotspot_super_state";
    RadioGroup mApBandRadioGroup;
    private int[] mBandEntries;
    private String[] mBandSummaries;
    private int mExistingConfigValue = Integer.MIN_VALUE;
    RadioButton mRadio2G;
    RadioButton mRadio5G;
    ArrayList<Integer> mRestoredBands;
    boolean mShouldRestore;

    public OPHotspotApBandSelectionPreference(Context context) {
        super(context);
    }

    public OPHotspotApBandSelectionPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public OPHotspotApBandSelectionPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public OPHotspotApBandSelectionPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public void onRestoreInstanceState(Parcelable parcelable) {
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        boolean z = savedState.shouldRestore;
        this.mShouldRestore = z;
        if (z) {
            ArrayList<Integer> arrayList = new ArrayList<>();
            this.mRestoredBands = arrayList;
            if (savedState.enabled2G) {
                arrayList.add(1);
            }
            if (savedState.enabled5G) {
                this.mRestoredBands.add(2);
            }
        } else {
            this.mRestoredBands = null;
        }
        updatePositiveButton();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.CustomDialogPreferenceCompat
    public void onPrepareDialogBuilder(AlertDialog.Builder builder, DialogInterface.OnClickListener onClickListener) {
        super.onPrepareDialogBuilder(builder, onClickListener);
        builder.setBottomShow(true);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.CustomDialogPreferenceCompat
    public void onBindDialogView(View view) {
        super.onBindDialogView(view);
        Context context = getContext();
        setOnShowListener(this);
        this.mBandSummaries = context.getResources().getStringArray(C0003R$array.wifi_ap_band_summary_full);
        this.mBandEntries = context.getResources().getIntArray(C0003R$array.wifi_ap_band_config_full);
        addApBandRadioGroupViews((LinearLayout) view);
        updatePositiveButton();
        this.mRestoredBands = null;
        this.mShouldRestore = false;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        boolean z = true;
        savedState.shouldRestore = getDialog() != null;
        RadioButton radioButton = this.mRadio2G;
        savedState.enabled2G = radioButton != null && radioButton.isChecked();
        RadioButton radioButton2 = this.mRadio5G;
        if (radioButton2 == null || !radioButton2.isChecked()) {
            z = false;
        }
        savedState.enabled5G = z;
        return savedState;
    }

    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        updatePositiveButton();
    }

    /* access modifiers changed from: protected */
    @Override // androidx.preference.DialogPreference, androidx.preference.Preference
    public void onClick() {
        if (!isDialogOpen()) {
            super.onClick();
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.CustomDialogPreferenceCompat
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i != -1) {
            return;
        }
        if (this.mRadio2G.isChecked() || this.mRadio5G.isChecked()) {
            int wifiBand = getWifiBand();
            this.mExistingConfigValue = wifiBand;
            callChangeListener(Integer.valueOf(wifiBand));
        }
    }

    public void setExistingConfigValue(int i) {
        this.mExistingConfigValue = i;
    }

    private void addApBandRadioGroupViews(LinearLayout linearLayout) {
        RadioButton radioButton = (RadioButton) linearLayout.findViewById(C0010R$id.radio_2g);
        this.mRadio2G = radioButton;
        radioButton.setText(this.mBandSummaries[0]);
        this.mRadio2G.setChecked(restoreBandIfNeeded(1));
        RadioButton radioButton2 = (RadioButton) linearLayout.findViewById(C0010R$id.radio_5g);
        this.mRadio5G = radioButton2;
        radioButton2.setText(this.mBandSummaries[1]);
        this.mRadio5G.setChecked(restoreBandIfNeeded(2));
        RadioGroup radioGroup = (RadioGroup) linearLayout.findViewById(C0010R$id.radioGroup_ap_band);
        this.mApBandRadioGroup = radioGroup;
        radioGroup.setOnCheckedChangeListener(this);
    }

    private boolean restoreBandIfNeeded(int i) {
        return (isBandPreviouslySelected(i) && !this.mShouldRestore) || (this.mShouldRestore && this.mRestoredBands.contains(Integer.valueOf(i)));
    }

    private void updatePositiveButton() {
        Button button;
        RadioButton radioButton;
        AlertDialog alertDialog = (AlertDialog) getDialog();
        if (alertDialog == null) {
            button = null;
        } else {
            button = alertDialog.getButton(-1);
        }
        if (button != null && (radioButton = this.mRadio2G) != null && this.mRadio5G != null) {
            button.setEnabled(radioButton.isChecked() || this.mRadio5G.isChecked());
        }
    }

    /* access modifiers changed from: package-private */
    public int getWifiBand() {
        boolean isChecked = this.mRadio2G.isChecked();
        boolean isChecked2 = this.mRadio5G.isChecked();
        if (isChecked) {
            return 1;
        }
        if (isChecked2) {
            return 2;
        }
        throw new IllegalStateException("Wifi Config only supports selecting one or all bands");
    }

    private boolean isBandPreviouslySelected(int i) {
        int i2 = this.mExistingConfigValue;
        if (i2 == 1) {
            return i == this.mBandEntries[0];
        }
        if (i2 != 2) {
            return i2 == 8;
        }
        int[] iArr = this.mBandEntries;
        return i == (iArr.length == 0 ? iArr[0] : iArr[1]);
    }

    public void onShow(DialogInterface dialogInterface) {
        updatePositiveButton();
    }

    /* access modifiers changed from: private */
    public static class SavedState extends Preference.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            /* class com.oneplus.settings.widget.OPHotspotApBandSelectionPreference.SavedState.AnonymousClass1 */

            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        boolean enabled2G;
        boolean enabled5G;
        boolean shouldRestore;

        public SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        private SavedState(Parcel parcel) {
            super(parcel);
            boolean z = false;
            this.shouldRestore = parcel.readByte() == 1;
            this.enabled2G = parcel.readByte() == 1;
            this.enabled5G = parcel.readByte() == 1 ? true : z;
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeByte(this.shouldRestore ? (byte) 1 : 0);
            parcel.writeByte(this.enabled2G ? (byte) 1 : 0);
            parcel.writeByte(this.enabled5G ? (byte) 1 : 0);
        }

        public String toString() {
            return "HotspotApBandSelectionPreference.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " shouldRestore=" + this.shouldRestore + " enabled2G=" + this.enabled2G + " enabled5G=" + this.enabled5G + "}";
        }
    }
}
