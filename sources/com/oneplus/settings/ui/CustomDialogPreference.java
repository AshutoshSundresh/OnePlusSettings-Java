package com.oneplus.settings.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import com.android.internal.R;
import com.android.settings.C0018R$style;

public abstract class CustomDialogPreference extends Preference implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener {
    private AlertDialog.Builder mBuilder;
    private Dialog mDialog;
    private int mDialogLayoutResId;
    private CharSequence mNegativeButtonText;
    private CharSequence mNeutralButtonText;
    private CharSequence mPositiveButtonText;
    private int mWhichButtonClicked;

    /* access modifiers changed from: protected */
    public void onBindDialogView(View view) {
    }

    /* access modifiers changed from: protected */
    public void onDialogClosed(int i) {
    }

    /* access modifiers changed from: protected */
    public void onPrepareDialogBuilder(AlertDialog.Builder builder) {
    }

    public CustomDialogPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.DialogPreference, i, i2);
        this.mPositiveButtonText = obtainStyledAttributes.getString(3);
        this.mNegativeButtonText = obtainStyledAttributes.getString(4);
        this.mDialogLayoutResId = obtainStyledAttributes.getResourceId(5, this.mDialogLayoutResId);
        obtainStyledAttributes.recycle();
    }

    public CustomDialogPreference(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public CustomDialogPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 16842897);
    }

    public CustomDialogPreference(Context context) {
        this(context, null);
    }

    public void setPositiveButtonText(CharSequence charSequence) {
        this.mPositiveButtonText = charSequence;
    }

    public void setPositiveButtonText(int i) {
        setPositiveButtonText(getContext().getString(i));
    }

    public void setNegativeButtonText(CharSequence charSequence) {
        this.mNegativeButtonText = charSequence;
    }

    public void setNegativeButtonText(int i) {
        setNegativeButtonText(getContext().getString(i));
    }

    public void setNeutralButtonText(CharSequence charSequence) {
        this.mNeutralButtonText = charSequence;
    }

    public void setNeutralButtonText(int i) {
        setNeutralButtonText(getContext().getString(i));
    }

    public void setDialogLayoutResource(int i) {
        this.mDialogLayoutResId = i;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public void onClick() {
        Dialog dialog = this.mDialog;
        if (dialog == null || !dialog.isShowing()) {
            showDialog(null);
        }
    }

    /* access modifiers changed from: protected */
    public void showDialog(Bundle bundle) {
        Context context = getContext();
        this.mWhichButtonClicked = -2;
        AlertDialog.Builder builder = new AlertDialog.Builder(context, C0018R$style.Theme_AlertDialog);
        this.mBuilder = builder;
        CharSequence charSequence = this.mPositiveButtonText;
        if (charSequence != null) {
            builder.setPositiveButton(charSequence, this);
        }
        CharSequence charSequence2 = this.mNegativeButtonText;
        if (charSequence2 != null) {
            this.mBuilder.setNegativeButton(charSequence2, this);
        }
        CharSequence charSequence3 = this.mNeutralButtonText;
        if (charSequence3 != null) {
            this.mBuilder.setNeutralButton(charSequence3, this);
        }
        View onCreateDialogView = onCreateDialogView();
        if (onCreateDialogView != null) {
            onBindDialogView(onCreateDialogView);
            this.mBuilder.setView(onCreateDialogView);
        }
        onPrepareDialogBuilder(this.mBuilder);
        AlertDialog create = this.mBuilder.create();
        this.mDialog = create;
        if (bundle != null) {
            create.onRestoreInstanceState(bundle);
        }
        create.setOnDismissListener(this);
        create.show();
    }

    /* access modifiers changed from: protected */
    public View onCreateDialogView() {
        if (this.mDialogLayoutResId == 0) {
            return null;
        }
        return LayoutInflater.from(this.mBuilder.getContext()).inflate(this.mDialogLayoutResId, (ViewGroup) null);
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.mWhichButtonClicked = i;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        this.mDialog = null;
        onDialogClosed(this.mWhichButtonClicked);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public Parcelable onSaveInstanceState() {
        Parcelable onSaveInstanceState = super.onSaveInstanceState();
        Dialog dialog = this.mDialog;
        if (dialog == null || !dialog.isShowing()) {
            return onSaveInstanceState;
        }
        SavedState savedState = new SavedState(onSaveInstanceState);
        savedState.isDialogShowing = true;
        savedState.dialogBundle = this.mDialog.onSaveInstanceState();
        return savedState;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable == null || !parcelable.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        if (savedState.isDialogShowing) {
            showDialog(savedState.dialogBundle);
        }
    }

    /* access modifiers changed from: private */
    public static class SavedState extends Preference.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            /* class com.oneplus.settings.ui.CustomDialogPreference.SavedState.AnonymousClass1 */

            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        Bundle dialogBundle;
        boolean isDialogShowing;

        public SavedState(Parcel parcel) {
            super(parcel);
            this.isDialogShowing = parcel.readInt() != 1 ? false : true;
            this.dialogBundle = parcel.readBundle();
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.isDialogShowing ? 1 : 0);
            parcel.writeBundle(this.dialogBundle);
        }

        public SavedState(Parcelable parcelable) {
            super(parcelable);
        }
    }
}
