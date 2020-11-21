package com.android.settings.homepage.contextualcards;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController;
import com.android.settings.C0017R$string;

public class ContextualCardFeedbackDialog extends AlertActivity implements DialogInterface.OnClickListener {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        ContextualCardFeedbackDialog.super.onCreate(bundle);
        AlertController.AlertParams alertParams = ((AlertActivity) this).mAlertParams;
        alertParams.mMessage = getText(C0017R$string.contextual_card_feedback_confirm_message);
        alertParams.mPositiveButtonText = getText(C0017R$string.contextual_card_feedback_send);
        alertParams.mPositiveButtonListener = this;
        alertParams.mNegativeButtonText = getText(C0017R$string.skip_label);
        setupAlert();
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        String stringExtra = getIntent().getStringExtra("card_name");
        String stringExtra2 = getIntent().getStringExtra("feedback_email");
        Intent intent = new Intent("android.intent.action.SENDTO", Uri.parse("mailto:" + stringExtra2));
        intent.putExtra("android.intent.extra.SUBJECT", "Settings Contextual Card Feedback - " + stringExtra);
        intent.addFlags(268435456);
        try {
            startActivity(intent);
        } catch (Exception e) {
            Log.e("CardFeedbackDialog", "Send feedback failed.", e);
        }
        finish();
    }
}
