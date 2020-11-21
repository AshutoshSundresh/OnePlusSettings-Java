package com.android.settings.vpn2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C0017R$string;

class AppDialog extends AlertDialog implements DialogInterface.OnClickListener {
    private final String mLabel;
    private final Listener mListener;
    private final PackageInfo mPackageInfo;

    public interface Listener {
        void onForget(DialogInterface dialogInterface);
    }

    AppDialog(Context context, Listener listener, PackageInfo packageInfo, String str) {
        super(context);
        this.mListener = listener;
        this.mPackageInfo = packageInfo;
        this.mLabel = str;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AlertDialog, androidx.appcompat.app.AppCompatDialog
    public void onCreate(Bundle bundle) {
        setTitle(this.mLabel);
        setMessage(getContext().getString(C0017R$string.vpn_version, this.mPackageInfo.versionName));
        createButtons();
        super.onCreate(bundle);
    }

    /* access modifiers changed from: protected */
    public void createButtons() {
        Context context = getContext();
        setButton(-2, context.getString(C0017R$string.vpn_forget), this);
        setButton(-1, context.getString(C0017R$string.vpn_done), this);
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -2) {
            this.mListener.onForget(dialogInterface);
        }
        dismiss();
    }
}
