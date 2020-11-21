package com.android.settings;

import android.app.Dialog;

public interface DialogCreatable {
    int getDialogMetricsCategory(int i);

    Dialog onCreateDialog(int i);
}
