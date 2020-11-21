package com.android.settings;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;

public class RemoteBugreportActivity extends Activity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        int i;
        super.onCreate(bundle);
        int intExtra = getIntent().getIntExtra("android.app.extra.bugreport_notification_type", -1);
        if (intExtra == 2) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(C0017R$string.sharing_remote_bugreport_dialog_message);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                /* class com.android.settings.RemoteBugreportActivity.AnonymousClass2 */

                public void onDismiss(DialogInterface dialogInterface) {
                    RemoteBugreportActivity.this.finish();
                }
            });
            builder.setNegativeButton(17039370, new DialogInterface.OnClickListener() {
                /* class com.android.settings.RemoteBugreportActivity.AnonymousClass1 */

                public void onClick(DialogInterface dialogInterface, int i) {
                    RemoteBugreportActivity.this.finish();
                }
            });
            builder.create().show();
        } else if (intExtra == 1 || intExtra == 3) {
            AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
            builder2.setTitle(C0017R$string.share_remote_bugreport_dialog_title);
            if (intExtra == 1) {
                i = C0017R$string.share_remote_bugreport_dialog_message;
            } else {
                i = C0017R$string.share_remote_bugreport_dialog_message_finished;
            }
            builder2.setMessage(i);
            builder2.setOnDismissListener(new DialogInterface.OnDismissListener() {
                /* class com.android.settings.RemoteBugreportActivity.AnonymousClass5 */

                public void onDismiss(DialogInterface dialogInterface) {
                    RemoteBugreportActivity.this.finish();
                }
            });
            builder2.setNegativeButton(C0017R$string.decline_remote_bugreport_action, new DialogInterface.OnClickListener() {
                /* class com.android.settings.RemoteBugreportActivity.AnonymousClass4 */

                public void onClick(DialogInterface dialogInterface, int i) {
                    RemoteBugreportActivity.this.sendBroadcastAsUser(new Intent("com.android.server.action.REMOTE_BUGREPORT_SHARING_DECLINED"), UserHandle.SYSTEM, "android.permission.DUMP");
                    RemoteBugreportActivity.this.finish();
                }
            });
            builder2.setPositiveButton(C0017R$string.share_remote_bugreport_action, new DialogInterface.OnClickListener() {
                /* class com.android.settings.RemoteBugreportActivity.AnonymousClass3 */

                public void onClick(DialogInterface dialogInterface, int i) {
                    RemoteBugreportActivity.this.sendBroadcastAsUser(new Intent("com.android.server.action.REMOTE_BUGREPORT_SHARING_ACCEPTED"), UserHandle.SYSTEM, "android.permission.DUMP");
                    RemoteBugreportActivity.this.finish();
                }
            });
            builder2.create().show();
        } else {
            Log.e("RemoteBugreportActivity", "Incorrect dialog type, no dialog shown. Received: " + intExtra);
        }
    }
}
