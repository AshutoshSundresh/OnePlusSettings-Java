package com.oneplus.settings.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import com.android.settings.C0003R$array;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settingslib.CustomDialogPreferenceCompat;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;

public class OPHotspotApUpstreamSelectionPreference extends CustomDialogPreferenceCompat implements DialogInterface.OnShowListener, View.OnClickListener {
    private int mExistingConfigValue = Integer.MIN_VALUE;
    private String[] mNetworkTypeEntries;
    private String[] mNetworkTypeSummaries;
    ArrayList<Integer> mRestoredNetworkTypes;
    boolean mShouldRestore;
    Checkable mUpstreamAuto;
    Checkable mUpstreamCell;
    Checkable mUpstreamWifi;

    public OPHotspotApUpstreamSelectionPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public OPHotspotApUpstreamSelectionPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public OPHotspotApUpstreamSelectionPreference(Context context, AttributeSet attributeSet, int i, int i2) {
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
            this.mRestoredNetworkTypes = arrayList;
            if (savedState.enabledAuto) {
                arrayList.add(0);
            }
            if (savedState.enabledWifi) {
                this.mRestoredNetworkTypes.add(1);
            }
            if (savedState.enabledCell) {
                this.mRestoredNetworkTypes.add(2);
            }
        } else {
            this.mRestoredNetworkTypes = null;
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
        this.mNetworkTypeEntries = context.getResources().getStringArray(C0003R$array.wifi_ap_upstream_type_entry);
        this.mNetworkTypeSummaries = context.getResources().getStringArray(C0003R$array.wifi_ap_upstream_type_summary);
        addNetworkTypeUpstreamGroupViews(view);
        updatePositiveButton();
        this.mRestoredNetworkTypes = null;
        this.mShouldRestore = false;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        boolean z = true;
        savedState.shouldRestore = getDialog() != null;
        Checkable checkable = this.mUpstreamAuto;
        savedState.enabledAuto = checkable != null && checkable.isChecked();
        Checkable checkable2 = this.mUpstreamWifi;
        savedState.enabledWifi = checkable2 != null && checkable2.isChecked();
        Checkable checkable3 = this.mUpstreamCell;
        if (checkable3 == null || !checkable3.isChecked()) {
            z = false;
        }
        savedState.enabledCell = z;
        return savedState;
    }

    private void updateViews(int i) {
        boolean z = false;
        this.mUpstreamAuto.setChecked(i == 0);
        this.mUpstreamWifi.setChecked(i == 1);
        Checkable checkable = this.mUpstreamCell;
        if (i == 2) {
            z = true;
        }
        checkable.setChecked(z);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.preference.DialogPreference, androidx.preference.Preference
    public void onClick() {
        if (!isDialogOpen()) {
            super.onClick();
        }
    }

    public void onClick(View view) {
        Checkable checkable = this.mUpstreamAuto;
        boolean z = false;
        if (view == checkable) {
            updateViews(0);
            z = !checkable.isChecked();
        } else {
            Checkable checkable2 = this.mUpstreamWifi;
            if (view == checkable2) {
                z = !checkable2.isChecked();
                updateViews(1);
            } else if (view == this.mUpstreamCell) {
                z = !checkable2.isChecked();
                updateViews(2);
            }
        }
        if (z) {
            updatePositiveButton();
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.CustomDialogPreferenceCompat
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i != -1) {
            return;
        }
        if (this.mUpstreamAuto.isChecked() || this.mUpstreamWifi.isChecked() || this.mUpstreamCell.isChecked()) {
            int upstreamType = getUpstreamType();
            this.mExistingConfigValue = upstreamType;
            callChangeListener(Integer.valueOf(upstreamType));
        }
    }

    public Checkable setup(View view, int i) {
        int i2;
        ((TextView) view.findViewById(16908310)).setText(this.mNetworkTypeEntries[i]);
        ((TextView) view.findViewById(16908304)).setText(this.mNetworkTypeSummaries[i]);
        view.setClickable(true);
        view.setOnClickListener(this);
        if (OPUtils.isBlackModeOn(getContext().getContentResolver())) {
            i2 = C0008R$drawable.op_btn_image_single_choice;
        } else {
            i2 = C0008R$drawable.op_btn_image_single_choice;
        }
        ((ImageView) view.findViewById(C0010R$id.marked)).setImageResource(i2);
        return (Checkable) view;
    }

    private void addNetworkTypeUpstreamGroupViews(View view) {
        Checkable upVar = setup(view.findViewById(C0010R$id.upstream_auto), 0);
        this.mUpstreamAuto = upVar;
        upVar.setChecked(restoreUpstreamTypeIfNeeded(0));
        Checkable upVar2 = setup(view.findViewById(C0010R$id.upstream_wifi), 1);
        this.mUpstreamWifi = upVar2;
        upVar2.setChecked(restoreUpstreamTypeIfNeeded(1));
        Checkable upVar3 = setup(view.findViewById(C0010R$id.upstream_cell), 2);
        this.mUpstreamCell = upVar3;
        upVar3.setChecked(restoreUpstreamTypeIfNeeded(2));
    }

    private boolean restoreUpstreamTypeIfNeeded(int i) {
        return (isUpstreamTypePreviouslySelected(i) && !this.mShouldRestore) || (this.mShouldRestore && this.mRestoredNetworkTypes.contains(Integer.valueOf(i)));
    }

    private void updatePositiveButton() {
        Button button;
        Checkable checkable;
        AlertDialog alertDialog = (AlertDialog) getDialog();
        if (alertDialog == null) {
            button = null;
        } else {
            button = alertDialog.getButton(-1);
        }
        if (button != null && (checkable = this.mUpstreamAuto) != null && this.mUpstreamWifi != null && this.mUpstreamCell != null) {
            button.setEnabled(checkable.isChecked() || this.mUpstreamWifi.isChecked() || this.mUpstreamCell.isChecked());
        }
    }

    /* access modifiers changed from: package-private */
    public int getUpstreamType() {
        boolean isChecked = this.mUpstreamAuto.isChecked();
        boolean isChecked2 = this.mUpstreamWifi.isChecked();
        boolean isChecked3 = this.mUpstreamCell.isChecked();
        if (isChecked) {
            return 0;
        }
        if (isChecked2) {
            return 1;
        }
        if (isChecked3) {
            return 2;
        }
        throw new IllegalStateException("Upstream type only supports selecting one or all network types");
    }

    private boolean isUpstreamTypePreviouslySelected(int i) {
        int i2 = this.mExistingConfigValue;
        return i2 != 0 ? i2 != 1 ? i2 == 2 && i == 2 : i == 1 : i == 0;
    }

    public void onShow(DialogInterface dialogInterface) {
        updatePositiveButton();
    }

    /* access modifiers changed from: private */
    public static class SavedState extends Preference.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            /* class com.oneplus.settings.widget.OPHotspotApUpstreamSelectionPreference.SavedState.AnonymousClass1 */

            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        boolean enabledAuto;
        boolean enabledCell;
        boolean enabledWifi;
        boolean shouldRestore;

        public SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        private SavedState(Parcel parcel) {
            super(parcel);
            boolean z = false;
            this.shouldRestore = parcel.readByte() == 1;
            this.enabledAuto = parcel.readByte() == 1;
            this.enabledWifi = parcel.readByte() == 1;
            this.enabledCell = parcel.readByte() == 1 ? true : z;
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeByte(this.shouldRestore ? (byte) 1 : 0);
            parcel.writeByte(this.enabledAuto ? (byte) 1 : 0);
            parcel.writeByte(this.enabledWifi ? (byte) 1 : 0);
            parcel.writeByte(this.enabledCell ? (byte) 1 : 0);
        }

        public String toString() {
            return "OPHotspotApUpstreamSelectionPreference.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " shouldRestore=" + this.shouldRestore + " enabledAuto=" + this.enabledAuto + " enabledWifi=" + this.enabledWifi + " enabledCell=" + this.enabledCell + "}";
        }
    }
}
