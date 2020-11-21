package com.android.settings.development;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.settings.C0017R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

public class EnableAdbWarningDialog extends InstrumentedDialogFragment implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1222;
    }

    public static void show(Fragment fragment) {
        FragmentManager supportFragmentManager = fragment.getActivity().getSupportFragmentManager();
        if (supportFragmentManager.findFragmentByTag("EnableAdbDialog") == null) {
            EnableAdbWarningDialog enableAdbWarningDialog = new EnableAdbWarningDialog();
            enableAdbWarningDialog.setTargetFragment(fragment, 0);
            enableAdbWarningDialog.show(supportFragmentManager, "EnableAdbDialog");
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(C0017R$string.adb_warning_title);
        builder.setMessage(C0017R$string.adb_warning_message);
        builder.setPositiveButton(17039379, this);
        builder.setNegativeButton(17039369, this);
        return builder.create();
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        AdbDialogHost adbDialogHost = (AdbDialogHost) getTargetFragment();
        if (adbDialogHost != null) {
            if (i == -1) {
                adbDialogHost.onEnableAdbDialogConfirmed();
            } else {
                adbDialogHost.onEnableAdbDialogDismissed();
            }
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        AdbDialogHost adbDialogHost = (AdbDialogHost) getTargetFragment();
        if (adbDialogHost != null) {
            adbDialogHost.onEnableAdbDialogDismissed();
        }
    }
}
