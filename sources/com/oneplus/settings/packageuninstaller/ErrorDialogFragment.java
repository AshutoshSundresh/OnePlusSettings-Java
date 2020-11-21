package com.oneplus.settings.packageuninstaller;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;

public class ErrorDialogFragment extends DialogFragment {
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getArguments().getInt("com.android.packageinstaller.arg.text"));
        builder.setPositiveButton(17039370, (DialogInterface.OnClickListener) null);
        if (getArguments().containsKey("com.android.packageinstaller.arg.title")) {
            builder.setTitle(getArguments().getInt("com.android.packageinstaller.arg.title"));
        }
        return builder.create();
    }

    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        if (isAdded()) {
            if (getActivity() instanceof UninstallerActivity) {
                ((UninstallerActivity) getActivity()).dispatchAborted();
            }
            getActivity().setResult(1);
            getActivity().finish();
        }
    }
}
