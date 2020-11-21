package com.android.settings.notification.zen;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import androidx.appcompat.app.AlertDialog;
import com.android.settingslib.CustomDialogPreferenceCompat;
import com.android.settingslib.notification.ZenDurationDialog;

public class ZenDurationDialogPreference extends CustomDialogPreferenceCompat {
    public ZenDurationDialogPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        setSingleLineTitle(false);
    }

    public ZenDurationDialogPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setSingleLineTitle(false);
    }

    public ZenDurationDialogPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setSingleLineTitle(false);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.CustomDialogPreferenceCompat
    public void onPrepareDialogBuilder(AlertDialog.Builder builder, DialogInterface.OnClickListener onClickListener) {
        super.onPrepareDialogBuilder(builder, onClickListener);
        new ZenDurationDialog(getContext()).setupDialog(builder);
    }
}
