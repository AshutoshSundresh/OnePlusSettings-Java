package com.android.settings.deletionhelper;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.android.settings.C0017R$string;

public class ActivationWarningFragment extends DialogFragment {
    public static ActivationWarningFragment newInstance() {
        return new ActivationWarningFragment();
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(C0017R$string.automatic_storage_manager_activation_warning);
        builder.setPositiveButton(17039370, (DialogInterface.OnClickListener) null);
        return builder.create();
    }
}
