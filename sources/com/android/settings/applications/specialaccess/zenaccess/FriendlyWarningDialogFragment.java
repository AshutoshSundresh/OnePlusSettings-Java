package com.android.settings.applications.specialaccess.zenaccess;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C0017R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

public class FriendlyWarningDialogFragment extends InstrumentedDialogFragment {
    static /* synthetic */ void lambda$onCreateDialog$1(DialogInterface dialogInterface, int i) {
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 555;
    }

    public FriendlyWarningDialogFragment setPkgInfo(String str, CharSequence charSequence) {
        Bundle bundle = new Bundle();
        bundle.putString("p", str);
        if (!TextUtils.isEmpty(charSequence)) {
            str = charSequence.toString();
        }
        bundle.putString("l", str);
        setArguments(bundle);
        return this;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        super.onCreate(bundle);
        Bundle arguments = getArguments();
        String string = arguments.getString("p");
        String string2 = arguments.getString("l");
        String string3 = getResources().getString(C0017R$string.zen_access_revoke_warning_dialog_title, string2);
        String string4 = getResources().getString(C0017R$string.zen_access_revoke_warning_dialog_summary);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(string4);
        builder.setTitle(string3);
        builder.setCancelable(true);
        builder.setPositiveButton(C0017R$string.okay, new DialogInterface.OnClickListener(string) {
            /* class com.android.settings.applications.specialaccess.zenaccess.$$Lambda$FriendlyWarningDialogFragment$TJpmwUpJG1FTGf_jqUkr3JatE0 */
            public final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                FriendlyWarningDialogFragment.this.lambda$onCreateDialog$0$FriendlyWarningDialogFragment(this.f$1, dialogInterface, i);
            }
        });
        builder.setNegativeButton(C0017R$string.cancel, $$Lambda$FriendlyWarningDialogFragment$KdT2_3oqRqmf618hTquzzrCOct0.INSTANCE);
        return builder.create();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateDialog$0 */
    public /* synthetic */ void lambda$onCreateDialog$0$FriendlyWarningDialogFragment(String str, DialogInterface dialogInterface, int i) {
        ZenAccessController.deleteRules(getContext(), str);
        ZenAccessController.setAccess(getContext(), str, false);
    }
}
