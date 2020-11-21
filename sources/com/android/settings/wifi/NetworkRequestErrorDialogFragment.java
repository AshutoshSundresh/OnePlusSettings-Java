package com.android.settings.wifi;

import android.app.Dialog;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C0017R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

public class NetworkRequestErrorDialogFragment extends InstrumentedDialogFragment {
    private WifiManager.NetworkRequestUserSelectionCallback mRejectCallback;

    public enum ERROR_DIALOG_TYPE {
        TIME_OUT,
        ABORT
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1373;
    }

    public static NetworkRequestErrorDialogFragment newInstance() {
        return new NetworkRequestErrorDialogFragment();
    }

    @Override // androidx.fragment.app.DialogFragment
    public void onCancel(DialogInterface dialogInterface) {
        super.onCancel(dialogInterface);
        rejectNetworkRequestAndFinish();
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        ERROR_DIALOG_TYPE error_dialog_type = ERROR_DIALOG_TYPE.TIME_OUT;
        if (getArguments() != null) {
            error_dialog_type = (ERROR_DIALOG_TYPE) getArguments().getSerializable("DIALOG_ERROR_TYPE");
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        if (error_dialog_type == ERROR_DIALOG_TYPE.TIME_OUT) {
            builder.setMessage(C0017R$string.network_connection_timeout_dialog_message);
            builder.setPositiveButton(C0017R$string.network_connection_timeout_dialog_ok, new DialogInterface.OnClickListener() {
                /* class com.android.settings.wifi.$$Lambda$NetworkRequestErrorDialogFragment$7_Krzx6JGM4tH4YAfKluuB7V0Y */

                public final void onClick(DialogInterface dialogInterface, int i) {
                    NetworkRequestErrorDialogFragment.this.lambda$onCreateDialog$0$NetworkRequestErrorDialogFragment(dialogInterface, i);
                }
            });
            builder.setNegativeButton(C0017R$string.cancel, new DialogInterface.OnClickListener() {
                /* class com.android.settings.wifi.$$Lambda$NetworkRequestErrorDialogFragment$yqnVunFMc2vfWDswc1vtYoaBvEY */

                public final void onClick(DialogInterface dialogInterface, int i) {
                    NetworkRequestErrorDialogFragment.this.lambda$onCreateDialog$1$NetworkRequestErrorDialogFragment(dialogInterface, i);
                }
            });
        } else {
            builder.setMessage(C0017R$string.network_connection_errorstate_dialog_message);
            builder.setPositiveButton(C0017R$string.okay, new DialogInterface.OnClickListener() {
                /* class com.android.settings.wifi.$$Lambda$NetworkRequestErrorDialogFragment$Ktwt2L5d1HsXIiQ1FheTk_6zL0 */

                public final void onClick(DialogInterface dialogInterface, int i) {
                    NetworkRequestErrorDialogFragment.this.lambda$onCreateDialog$2$NetworkRequestErrorDialogFragment(dialogInterface, i);
                }
            });
        }
        return builder.create();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateDialog$0 */
    public /* synthetic */ void lambda$onCreateDialog$0$NetworkRequestErrorDialogFragment(DialogInterface dialogInterface, int i) {
        onRescanClick();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateDialog$1 */
    public /* synthetic */ void lambda$onCreateDialog$1$NetworkRequestErrorDialogFragment(DialogInterface dialogInterface, int i) {
        rejectNetworkRequestAndFinish();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateDialog$2 */
    public /* synthetic */ void lambda$onCreateDialog$2$NetworkRequestErrorDialogFragment(DialogInterface dialogInterface, int i) {
        rejectNetworkRequestAndFinish();
    }

    public void setRejectCallback(WifiManager.NetworkRequestUserSelectionCallback networkRequestUserSelectionCallback) {
        this.mRejectCallback = networkRequestUserSelectionCallback;
    }

    /* access modifiers changed from: protected */
    public void onRescanClick() {
        if (getActivity() != null) {
            dismiss();
            ((NetworkRequestDialogActivity) getActivity()).onClickRescanButton();
        }
    }

    private void rejectNetworkRequestAndFinish() {
        if (getActivity() != null) {
            WifiManager.NetworkRequestUserSelectionCallback networkRequestUserSelectionCallback = this.mRejectCallback;
            if (networkRequestUserSelectionCallback != null) {
                networkRequestUserSelectionCallback.reject();
            }
            getActivity().finish();
        }
    }
}
