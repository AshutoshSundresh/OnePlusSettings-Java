package com.oneplus.loading;

import android.app.Dialog;

public class DialogLoadingHelper extends LoadingHelper {
    Dialog mDialog;

    public DialogLoadingHelper(Dialog dialog) {
        this.mDialog = dialog;
    }

    /* access modifiers changed from: protected */
    @Override // com.oneplus.loading.LoadingHelper
    public Object showProgree() {
        Dialog dialog = this.mDialog;
        if (dialog != null) {
            try {
                dialog.show();
            } catch (Throwable unused) {
            }
        }
        return this.mDialog;
    }

    /* access modifiers changed from: protected */
    @Override // com.oneplus.loading.LoadingHelper
    public void hideProgree(Object obj) {
        Dialog dialog = this.mDialog;
        if (dialog != null) {
            try {
                dialog.dismiss();
            } catch (Throwable unused) {
            }
        }
    }
}
