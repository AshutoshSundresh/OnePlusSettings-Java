package com.android.settings.biometrics;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.C0018R$style;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.oneplus.settings.utils.OPThemeUtils;
import com.oneplus.settings.utils.OPUtils;

public abstract class BiometricErrorDialog extends InstrumentedDialogFragment {
    private AlertDialog mAlertDialog;

    public abstract int getOkButtonTextResId();

    public abstract int getTitleResId();

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder;
        int i;
        if (getArguments().getBoolean("setup_for_back_fingerprint")) {
            if (OPUtils.isO2()) {
                i = C0018R$style.SetupWizardTheme_Dialog_Alert_Oxygen;
            } else {
                i = C0018R$style.SetupWizardTheme_Dialog_Alert_Hydrogen;
            }
            builder = new AlertDialog.Builder(getActivity(), i);
        } else {
            builder = new AlertDialog.Builder(getActivity());
        }
        CharSequence charSequence = getArguments().getCharSequence("error_msg");
        final int i2 = getArguments().getInt("error_id");
        builder.setTitle(getTitleResId());
        builder.setMessage(charSequence);
        builder.setCancelable(false);
        builder.setPositiveButton(getOkButtonTextResId(), new DialogInterface.OnClickListener() {
            /* class com.android.settings.biometrics.BiometricErrorDialog.AnonymousClass1 */

            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                int i2 = 1;
                boolean z = i2 == 3;
                FragmentActivity activity = BiometricErrorDialog.this.getActivity();
                if (z) {
                    i2 = 3;
                }
                activity.setResult(i2);
                activity.finish();
            }
        });
        AlertDialog create = builder.create();
        this.mAlertDialog = create;
        create.setCanceledOnTouchOutside(false);
        return this.mAlertDialog;
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment
    public void onStart() {
        super.onStart();
        AlertDialog alertDialog = this.mAlertDialog;
        if (alertDialog != null) {
            OPThemeUtils.setDialogTextColor(alertDialog);
        }
    }
}
