package com.android.settings.security;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C0017R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

public class UnificationConfirmationDialog extends InstrumentedDialogFragment {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 532;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        int i;
        int i2;
        SecuritySettings securitySettings = (SecuritySettings) getParentFragment();
        boolean z = getArguments().getBoolean("compliant");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(C0017R$string.lock_settings_profile_unification_dialog_title);
        if (z) {
            i = C0017R$string.lock_settings_profile_unification_dialog_body;
        } else {
            i = C0017R$string.lock_settings_profile_unification_dialog_uncompliant_body;
        }
        builder.setMessage(i);
        if (z) {
            i2 = C0017R$string.lock_settings_profile_unification_dialog_confirm;
        } else {
            i2 = C0017R$string.lock_settings_profile_unification_dialog_uncompliant_confirm;
        }
        builder.setPositiveButton(i2, new DialogInterface.OnClickListener() {
            /* class com.android.settings.security.$$Lambda$UnificationConfirmationDialog$X7Z3K6rE64nwQwG_nB1_LYip7Y */

            public final void onClick(DialogInterface dialogInterface, int i) {
                UnificationConfirmationDialog.lambda$onCreateDialog$0(SecuritySettings.this, dialogInterface, i);
            }
        });
        builder.setNegativeButton(C0017R$string.cancel, (DialogInterface.OnClickListener) null);
        return builder.create();
    }

    @Override // androidx.fragment.app.DialogFragment
    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        ((SecuritySettings) getParentFragment()).updateUnificationPreference();
    }
}
