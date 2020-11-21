package com.android.settings.wifi;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;

public class NetworkRequestSingleSsidDialogFragment extends NetworkRequestDialogBaseFragment {
    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        boolean z;
        int i;
        String str = "";
        if (getArguments() != null) {
            z = getArguments().getBoolean("DIALOG_IS_TRYAGAIN", true);
            str = getArguments().getString("DIALOG_REQUEST_SSID", str);
        } else {
            z = false;
        }
        Context context = getContext();
        View inflate = LayoutInflater.from(context).inflate(C0012R$layout.network_request_dialog_title, (ViewGroup) null);
        ((TextView) inflate.findViewById(C0010R$id.network_request_title_text)).setText(getTitle());
        ((TextView) inflate.findViewById(C0010R$id.network_request_summary_text)).setText(getSummary());
        ((ProgressBar) inflate.findViewById(C0010R$id.network_request_title_progress)).setVisibility(8);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCustomTitle(inflate);
        builder.setMessage(str);
        if (z) {
            i = C0017R$string.network_connection_timeout_dialog_ok;
        } else {
            i = C0017R$string.wifi_connect;
        }
        builder.setPositiveButton(i, new DialogInterface.OnClickListener() {
            /* class com.android.settings.wifi.$$Lambda$NetworkRequestSingleSsidDialogFragment$IBPcGMDs1yw4D97Aiq1mUznLVtI */

            public final void onClick(DialogInterface dialogInterface, int i) {
                NetworkRequestSingleSsidDialogFragment.this.lambda$onCreateDialog$0$NetworkRequestSingleSsidDialogFragment(dialogInterface, i);
            }
        });
        builder.setNeutralButton(C0017R$string.cancel, new DialogInterface.OnClickListener() {
            /* class com.android.settings.wifi.$$Lambda$NetworkRequestSingleSsidDialogFragment$jr1du4M4IXpzAWIxrcICSjgzMg */

            public final void onClick(DialogInterface dialogInterface, int i) {
                NetworkRequestSingleSsidDialogFragment.this.lambda$onCreateDialog$1$NetworkRequestSingleSsidDialogFragment(dialogInterface, i);
            }
        });
        setCancelable(false);
        return builder.create();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateDialog$0 */
    public /* synthetic */ void lambda$onCreateDialog$0$NetworkRequestSingleSsidDialogFragment(DialogInterface dialogInterface, int i) {
        onUserClickConnectButton();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateDialog$1 */
    public /* synthetic */ void lambda$onCreateDialog$1$NetworkRequestSingleSsidDialogFragment(DialogInterface dialogInterface, int i) {
        onCancel(dialogInterface);
    }

    private void onUserClickConnectButton() {
        NetworkRequestDialogActivity networkRequestDialogActivity = this.mActivity;
        if (networkRequestDialogActivity != null) {
            networkRequestDialogActivity.onClickConnectButton();
        }
    }
}
