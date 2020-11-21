package com.android.settings.applications.appinfo;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C0017R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

public class ButtonActionDialogFragment extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
    int mId;

    public interface AppButtonsDialogListener {
        void handleDialogClick(int i);
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 558;
    }

    public static ButtonActionDialogFragment newInstance(int i) {
        ButtonActionDialogFragment buttonActionDialogFragment = new ButtonActionDialogFragment();
        Bundle bundle = new Bundle(1);
        bundle.putInt("id", i);
        buttonActionDialogFragment.setArguments(bundle);
        return buttonActionDialogFragment;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        int i = getArguments().getInt("id");
        this.mId = i;
        AlertDialog createDialog = createDialog(i);
        if (createDialog != null) {
            return createDialog;
        }
        throw new IllegalArgumentException("unknown id " + this.mId);
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        ((AppButtonsDialogListener) getTargetFragment()).handleDialogClick(this.mId);
    }

    private AlertDialog createDialog(int i) {
        Context context = getContext();
        if (i == 0 || i == 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(C0017R$string.app_disable_dlg_text);
            builder.setPositiveButton(C0017R$string.app_disable_dlg_positive, this);
            builder.setNegativeButton(C0017R$string.dlg_cancel, (DialogInterface.OnClickListener) null);
            return builder.create();
        } else if (i != 2) {
            return null;
        } else {
            AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
            builder2.setTitle(C0017R$string.force_stop_dlg_title);
            builder2.setMessage(C0017R$string.force_stop_dlg_text);
            builder2.setPositiveButton(C0017R$string.dlg_ok, this);
            builder2.setNegativeButton(C0017R$string.dlg_cancel, (DialogInterface.OnClickListener) null);
            return builder2.create();
        }
    }
}
