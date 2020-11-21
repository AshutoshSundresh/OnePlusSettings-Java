package com.android.settingslib;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.DialogPreference;
import androidx.preference.PreferenceDialogFragmentCompat;

public class CustomDialogPreferenceCompat extends DialogPreference {
    private CustomPreferenceDialogFragment mFragment;
    private DialogInterface.OnShowListener mOnShowListener;

    /* access modifiers changed from: protected */
    public void onBindDialogView(View view) {
    }

    /* access modifiers changed from: protected */
    public void onClick(DialogInterface dialogInterface, int i) {
    }

    /* access modifiers changed from: protected */
    public void onDialogClosed(boolean z) {
    }

    /* access modifiers changed from: protected */
    public void onPrepareDialogBuilder(AlertDialog.Builder builder, DialogInterface.OnClickListener onClickListener) {
    }

    public CustomDialogPreferenceCompat(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public CustomDialogPreferenceCompat(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public CustomDialogPreferenceCompat(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CustomDialogPreferenceCompat(Context context) {
        super(context);
    }

    public boolean isDialogOpen() {
        return getDialog() != null && getDialog().isShowing();
    }

    public Dialog getDialog() {
        CustomPreferenceDialogFragment customPreferenceDialogFragment = this.mFragment;
        if (customPreferenceDialogFragment != null) {
            return customPreferenceDialogFragment.getDialog();
        }
        return null;
    }

    public void setOnShowListener(DialogInterface.OnShowListener onShowListener) {
        this.mOnShowListener = onShowListener;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setFragment(CustomPreferenceDialogFragment customPreferenceDialogFragment) {
        this.mFragment = customPreferenceDialogFragment;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private DialogInterface.OnShowListener getOnShowListener() {
        return this.mOnShowListener;
    }

    public static class CustomPreferenceDialogFragment extends PreferenceDialogFragmentCompat {
        public static CustomPreferenceDialogFragment newInstance(String str) {
            CustomPreferenceDialogFragment customPreferenceDialogFragment = new CustomPreferenceDialogFragment();
            Bundle bundle = new Bundle(1);
            bundle.putString("key", str);
            customPreferenceDialogFragment.setArguments(bundle);
            return customPreferenceDialogFragment;
        }

        private CustomDialogPreferenceCompat getCustomizablePreference() {
            return (CustomDialogPreferenceCompat) getPreference();
        }

        /* access modifiers changed from: protected */
        @Override // androidx.preference.PreferenceDialogFragmentCompat
        public void onPrepareDialogBuilder(AlertDialog.Builder builder) {
            super.onPrepareDialogBuilder(builder);
            getCustomizablePreference().setFragment((CustomDialogPreferenceCompat) this);
            getCustomizablePreference().onPrepareDialogBuilder(builder, this);
        }

        @Override // androidx.preference.PreferenceDialogFragmentCompat
        public void onDialogClosed(boolean z) {
            getCustomizablePreference().onDialogClosed(z);
        }

        /* access modifiers changed from: protected */
        @Override // androidx.preference.PreferenceDialogFragmentCompat
        public void onBindDialogView(View view) {
            super.onBindDialogView(view);
            getCustomizablePreference().onBindDialogView(view);
        }

        @Override // androidx.preference.PreferenceDialogFragmentCompat, androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            Dialog onCreateDialog = super.onCreateDialog(bundle);
            onCreateDialog.setOnShowListener(getCustomizablePreference().getOnShowListener());
            return onCreateDialog;
        }

        @Override // androidx.preference.PreferenceDialogFragmentCompat
        public void onClick(DialogInterface dialogInterface, int i) {
            super.onClick(dialogInterface, i);
            getCustomizablePreference().onClick(dialogInterface, i);
        }
    }
}
