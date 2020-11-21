package com.android.settings.notification.zen;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.service.notification.ZenModeConfig;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

public class ZenRuleNameDialog extends InstrumentedDialogFragment {
    protected static PositiveClickListener mPositiveClickListener;

    public interface PositiveClickListener {
        void onOk(String str, Fragment fragment);
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1269;
    }

    public static void show(Fragment fragment, String str, Uri uri, PositiveClickListener positiveClickListener) {
        Bundle bundle = new Bundle();
        bundle.putString("zen_rule_name", str);
        bundle.putParcelable("extra_zen_condition_id", uri);
        mPositiveClickListener = positiveClickListener;
        ZenRuleNameDialog zenRuleNameDialog = new ZenRuleNameDialog();
        zenRuleNameDialog.setArguments(bundle);
        zenRuleNameDialog.setTargetFragment(fragment, 0);
        zenRuleNameDialog.show(fragment.getFragmentManager(), "ZenRuleNameDialog");
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        int i;
        Bundle arguments = getArguments();
        Uri uri = (Uri) arguments.getParcelable("extra_zen_condition_id");
        final String string = arguments.getString("zen_rule_name");
        final boolean z = string == null;
        Context context = getContext();
        View inflate = LayoutInflater.from(context).inflate(C0012R$layout.zen_rule_name, (ViewGroup) null, false);
        final EditText editText = (EditText) inflate.findViewById(C0010R$id.zen_mode_rule_name);
        if (!z) {
            editText.setText(string);
            editText.setSelection(editText.getText().length());
        }
        editText.setSelectAllOnFocus(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getTitleResource(uri, z));
        builder.setView(inflate);
        if (z) {
            i = C0017R$string.zen_mode_add;
        } else {
            i = C0017R$string.okay;
        }
        builder.setPositiveButton(i, new DialogInterface.OnClickListener() {
            /* class com.android.settings.notification.zen.ZenRuleNameDialog.AnonymousClass1 */

            public void onClick(DialogInterface dialogInterface, int i) {
                CharSequence charSequence;
                String trimmedText = ZenRuleNameDialog.this.trimmedText(editText);
                if (!TextUtils.isEmpty(trimmedText)) {
                    if (z || (charSequence = string) == null || !charSequence.equals(trimmedText)) {
                        ZenRuleNameDialog.mPositiveClickListener.onOk(trimmedText, ZenRuleNameDialog.this.getTargetFragment());
                    }
                }
            }
        });
        builder.setNegativeButton(C0017R$string.cancel, (DialogInterface.OnClickListener) null);
        return builder.create();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private String trimmedText(EditText editText) {
        if (editText.getText() == null) {
            return null;
        }
        return editText.getText().toString().trim();
    }

    private int getTitleResource(Uri uri, boolean z) {
        boolean isValidEventConditionId = ZenModeConfig.isValidEventConditionId(uri);
        boolean isValidScheduleConditionId = ZenModeConfig.isValidScheduleConditionId(uri);
        int i = C0017R$string.zen_mode_rule_name;
        if (!z) {
            return i;
        }
        if (isValidEventConditionId) {
            return C0017R$string.zen_mode_add_event_rule;
        }
        return isValidScheduleConditionId ? C0017R$string.zen_mode_add_time_rule : i;
    }
}
