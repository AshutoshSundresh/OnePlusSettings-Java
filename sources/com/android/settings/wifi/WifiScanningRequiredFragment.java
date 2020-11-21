package com.android.settings.wifi;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settingslib.HelpUtils;

public class WifiScanningRequiredFragment extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1373;
    }

    public static WifiScanningRequiredFragment newInstance() {
        return new WifiScanningRequiredFragment();
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(C0017R$string.wifi_settings_scanning_required_title);
        builder.setView(C0012R$layout.wifi_settings_scanning_required_view);
        builder.setPositiveButton(C0017R$string.wifi_settings_scanning_required_turn_on, this);
        builder.setNegativeButton(C0017R$string.cancel, (DialogInterface.OnClickListener) null);
        addButtonIfNeeded(builder);
        return builder.create();
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        Context context = getContext();
        context.getContentResolver();
        if (i == -3) {
            openHelpPage();
        } else if (i == -1) {
            ((WifiManager) context.getSystemService(WifiManager.class)).setScanAlwaysAvailable(true);
            Toast.makeText(context, context.getString(C0017R$string.wifi_settings_scanning_required_enabled), 0).show();
            getTargetFragment().onActivityResult(getTargetRequestCode(), -1, null);
        }
    }

    /* access modifiers changed from: package-private */
    public void addButtonIfNeeded(AlertDialog.Builder builder) {
        if (!TextUtils.isEmpty(getContext().getString(C0017R$string.help_uri_wifi_scanning_required))) {
            builder.setNeutralButton(C0017R$string.learn_more, this);
        }
    }

    private void openHelpPage() {
        Intent helpIntent = getHelpIntent(getContext());
        if (helpIntent != null) {
            try {
                getActivity().startActivityForResult(helpIntent, 0);
            } catch (ActivityNotFoundException unused) {
                Log.e("WifiScanReqFrag", "Activity was not found for intent, " + helpIntent.toString());
            }
        }
    }

    /* access modifiers changed from: package-private */
    public Intent getHelpIntent(Context context) {
        return HelpUtils.getHelpIntent(context, context.getString(C0017R$string.help_uri_wifi_scanning_required), context.getClass().getName());
    }
}
