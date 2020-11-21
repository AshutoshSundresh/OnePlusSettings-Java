package com.android.settings.notification.zen;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.BidiFormatter;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.android.settings.C0017R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

public class ZenDeleteRuleDialog extends InstrumentedDialogFragment {
    protected static PositiveClickListener mPositiveClickListener;

    public interface PositiveClickListener {
        void onOk(String str);
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1266;
    }

    public static void show(Fragment fragment, String str, String str2, PositiveClickListener positiveClickListener) {
        BidiFormatter instance = BidiFormatter.getInstance();
        Bundle bundle = new Bundle();
        bundle.putString("zen_rule_name", instance.unicodeWrap(str));
        bundle.putString("zen_rule_id", str2);
        mPositiveClickListener = positiveClickListener;
        ZenDeleteRuleDialog zenDeleteRuleDialog = new ZenDeleteRuleDialog();
        zenDeleteRuleDialog.setArguments(bundle);
        zenDeleteRuleDialog.setTargetFragment(fragment, 0);
        zenDeleteRuleDialog.show(fragment.getFragmentManager(), "ZenDeleteRuleDialog");
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        final Bundle arguments = getArguments();
        String string = arguments.getString("zen_rule_name");
        final String string2 = arguments.getString("zen_rule_id");
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getString(C0017R$string.zen_mode_delete_rule_confirmation, string));
        builder.setNegativeButton(C0017R$string.cancel, (DialogInterface.OnClickListener) null);
        builder.setPositiveButton(C0017R$string.zen_mode_delete_rule_button, new DialogInterface.OnClickListener(this) {
            /* class com.android.settings.notification.zen.ZenDeleteRuleDialog.AnonymousClass1 */

            public void onClick(DialogInterface dialogInterface, int i) {
                if (arguments != null) {
                    ZenDeleteRuleDialog.mPositiveClickListener.onOk(string2);
                }
            }
        });
        return builder.create();
    }
}
