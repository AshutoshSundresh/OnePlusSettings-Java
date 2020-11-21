package com.android.settings.notification.app;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C0017R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

public class BubbleWarningDialogFragment extends InstrumentedDialogFragment {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1702;
    }

    public BubbleWarningDialogFragment setPkgPrefInfo(String str, int i, int i2) {
        Bundle bundle = new Bundle();
        bundle.putString("p", str);
        bundle.putInt("u", i);
        bundle.putInt("pref", i2);
        setArguments(bundle);
        return this;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        super.onCreate(bundle);
        Bundle arguments = getArguments();
        String string = arguments.getString("p");
        int i = arguments.getInt("u");
        int i2 = arguments.getInt("pref");
        String string2 = getResources().getString(C0017R$string.bubbles_feature_disabled_dialog_title);
        String string3 = getResources().getString(C0017R$string.bubbles_feature_disabled_dialog_text);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(string3);
        builder.setTitle(string2);
        builder.setCancelable(true);
        builder.setPositiveButton(C0017R$string.bubbles_feature_disabled_button_approve, new DialogInterface.OnClickListener(string, i, i2) {
            /* class com.android.settings.notification.app.$$Lambda$BubbleWarningDialogFragment$zbf3DRm8F3MrJafmVGWsb8bXzE */
            public final /* synthetic */ String f$1;
            public final /* synthetic */ int f$2;
            public final /* synthetic */ int f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                BubbleWarningDialogFragment.this.lambda$onCreateDialog$0$BubbleWarningDialogFragment(this.f$1, this.f$2, this.f$3, dialogInterface, i);
            }
        });
        builder.setNegativeButton(C0017R$string.bubbles_feature_disabled_button_cancel, new DialogInterface.OnClickListener(string, i) {
            /* class com.android.settings.notification.app.$$Lambda$BubbleWarningDialogFragment$Y6GP4VEnUIN1W8PjkyuTZEUgXWA */
            public final /* synthetic */ String f$1;
            public final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                BubbleWarningDialogFragment.this.lambda$onCreateDialog$1$BubbleWarningDialogFragment(this.f$1, this.f$2, dialogInterface, i);
            }
        });
        return builder.create();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateDialog$0 */
    public /* synthetic */ void lambda$onCreateDialog$0$BubbleWarningDialogFragment(String str, int i, int i2, DialogInterface dialogInterface, int i3) {
        BubblePreferenceController.applyBubblesApproval(getContext(), str, i, i2);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateDialog$1 */
    public /* synthetic */ void lambda$onCreateDialog$1$BubbleWarningDialogFragment(String str, int i, DialogInterface dialogInterface, int i2) {
        BubblePreferenceController.revertBubblesApproval(getContext(), str, i);
    }
}
