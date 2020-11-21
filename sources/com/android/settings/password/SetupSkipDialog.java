package com.android.settings.password;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import com.android.settings.C0017R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

public class SetupSkipDialog extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 573;
    }

    public static SetupSkipDialog newInstance(boolean z, boolean z2, boolean z3, boolean z4, boolean z5) {
        SetupSkipDialog setupSkipDialog = new SetupSkipDialog();
        Bundle bundle = new Bundle();
        bundle.putBoolean("frp_supported", z);
        bundle.putBoolean("lock_type_pattern", z2);
        bundle.putBoolean("lock_type_alphanumeric", z3);
        bundle.putBoolean("for_fingerprint", z4);
        bundle.putBoolean("for_face", z5);
        setupSkipDialog.setArguments(bundle);
        return setupSkipDialog;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        return onCreateDialogBuilder().create();
    }

    public AlertDialog.Builder onCreateDialogBuilder() {
        int i;
        int i2;
        int i3;
        Bundle arguments = getArguments();
        boolean z = arguments.getBoolean("for_face");
        boolean z2 = arguments.getBoolean("for_fingerprint");
        if (z || z2) {
            if (arguments.getBoolean("lock_type_pattern")) {
                i = C0017R$string.lock_screen_pattern_skip_title;
            } else {
                i = arguments.getBoolean("lock_type_alphanumeric") ? C0017R$string.lock_screen_password_skip_title : C0017R$string.lock_screen_pin_skip_title;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setPositiveButton(C0017R$string.skip_lock_screen_dialog_button_label, this);
            builder.setNegativeButton(C0017R$string.cancel_lock_screen_dialog_button_label, this);
            builder.setTitle(i);
            if (z) {
                i2 = C0017R$string.face_lock_screen_setup_skip_dialog_text;
            } else {
                i2 = C0017R$string.fingerprint_lock_screen_setup_skip_dialog_text;
            }
            builder.setMessage(i2);
            return builder;
        }
        AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
        builder2.setPositiveButton(C0017R$string.skip_anyway_button_label, this);
        builder2.setNegativeButton(C0017R$string.go_back_button_label, this);
        builder2.setTitle(C0017R$string.lock_screen_intro_skip_title);
        if (arguments.getBoolean("frp_supported")) {
            i3 = C0017R$string.lock_screen_intro_skip_dialog_text_frp;
        } else {
            i3 = C0017R$string.lock_screen_intro_skip_dialog_text;
        }
        builder2.setMessage(i3);
        return builder2;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1) {
            new Handler().postDelayed(new Runnable() {
                /* class com.android.settings.password.SetupSkipDialog.AnonymousClass1 */
                final Activity activity = SetupSkipDialog.this.getActivity();

                public void run() {
                    Activity activity2 = this.activity;
                    if (activity2 != null) {
                        activity2.setResult(11);
                        this.activity.finish();
                    }
                }
            }, 300);
        }
    }

    public void show(FragmentManager fragmentManager) {
        show(fragmentManager, "skip_dialog");
    }
}
