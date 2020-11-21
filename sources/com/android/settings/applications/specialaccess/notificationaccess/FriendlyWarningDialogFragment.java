package com.android.settings.applications.specialaccess.notificationaccess;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.android.settings.C0017R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

public class FriendlyWarningDialogFragment extends InstrumentedDialogFragment {
    static /* synthetic */ void lambda$onCreateDialog$0(DialogInterface dialogInterface, int i) {
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 552;
    }

    public FriendlyWarningDialogFragment setServiceInfo(ComponentName componentName, CharSequence charSequence, Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString("c", componentName.flattenToString());
        bundle.putCharSequence("l", charSequence);
        setArguments(bundle);
        setTargetFragment(fragment, 0);
        return this;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Bundle arguments = getArguments();
        CharSequence charSequence = arguments.getCharSequence("l");
        final ComponentName unflattenFromString = ComponentName.unflattenFromString(arguments.getString("c"));
        final NotificationAccessDetails notificationAccessDetails = (NotificationAccessDetails) getTargetFragment();
        String string = getResources().getString(C0017R$string.notification_listener_disable_warning_summary, charSequence);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(string);
        builder.setCancelable(true);
        builder.setPositiveButton(C0017R$string.notification_listener_disable_warning_confirm, new DialogInterface.OnClickListener(this) {
            /* class com.android.settings.applications.specialaccess.notificationaccess.FriendlyWarningDialogFragment.AnonymousClass1 */

            public void onClick(DialogInterface dialogInterface, int i) {
                notificationAccessDetails.disable(unflattenFromString);
            }
        });
        builder.setNegativeButton(C0017R$string.notification_listener_disable_warning_cancel, $$Lambda$FriendlyWarningDialogFragment$gfo2g2TwJnTWYYgkmOE0P_dINeU.INSTANCE);
        return builder.create();
    }
}
