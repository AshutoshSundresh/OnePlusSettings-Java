package com.android.settings.development;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.settings.C0017R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

public class ClearAdbKeysWarningDialog extends InstrumentedDialogFragment implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1223;
    }

    public static void show(Fragment fragment) {
        FragmentManager supportFragmentManager = fragment.getActivity().getSupportFragmentManager();
        if (supportFragmentManager.findFragmentByTag("ClearAdbKeysDlg") == null) {
            ClearAdbKeysWarningDialog clearAdbKeysWarningDialog = new ClearAdbKeysWarningDialog();
            clearAdbKeysWarningDialog.setTargetFragment(fragment, 0);
            clearAdbKeysWarningDialog.show(supportFragmentManager, "ClearAdbKeysDlg");
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(C0017R$string.adb_keys_warning_message);
        builder.setPositiveButton(17039370, this);
        builder.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
        return builder.create();
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        AdbClearKeysDialogHost adbClearKeysDialogHost = (AdbClearKeysDialogHost) getTargetFragment();
        if (adbClearKeysDialogHost != null) {
            adbClearKeysDialogHost.onAdbClearKeysDialogConfirmed();
        }
    }
}
