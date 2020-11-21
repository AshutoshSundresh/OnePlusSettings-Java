package com.android.settings.notification;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Slog;
import android.view.accessibility.AccessibilityEvent;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController;
import com.android.settings.C0017R$string;

public class NotificationAccessConfirmationActivity extends Activity implements DialogInterface {
    private ComponentName mComponentName;
    private NotificationManager mNm;

    public void onBackPressed() {
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mNm = (NotificationManager) getSystemService("notification");
        this.mComponentName = (ComponentName) getIntent().getParcelableExtra("component_name");
        getIntent().getIntExtra("user_id", -10000);
        String stringExtra = getIntent().getStringExtra("package_title");
        AlertController.AlertParams alertParams = new AlertController.AlertParams(this);
        alertParams.mTitle = getString(C0017R$string.notification_listener_security_warning_title, new Object[]{stringExtra});
        alertParams.mMessage = getString(C0017R$string.notification_listener_security_warning_summary, new Object[]{stringExtra});
        alertParams.mPositiveButtonText = getString(C0017R$string.allow);
        alertParams.mPositiveButtonListener = new DialogInterface.OnClickListener() {
            /* class com.android.settings.notification.$$Lambda$NotificationAccessConfirmationActivity$UvveyFMEwlZ6m4ViLmcVExulBE8 */

            public final void onClick(DialogInterface dialogInterface, int i) {
                NotificationAccessConfirmationActivity.this.lambda$onCreate$0$NotificationAccessConfirmationActivity(dialogInterface, i);
            }
        };
        alertParams.mNegativeButtonText = getString(C0017R$string.deny);
        alertParams.mNegativeButtonListener = new DialogInterface.OnClickListener() {
            /* class com.android.settings.notification.$$Lambda$NotificationAccessConfirmationActivity$hd7i7CSD_dVpjvK__hXE8eDM2I0 */

            public final void onClick(DialogInterface dialogInterface, int i) {
                NotificationAccessConfirmationActivity.this.lambda$onCreate$1$NotificationAccessConfirmationActivity(dialogInterface, i);
            }
        };
        AlertController.create(this, this, getWindow()).installContent(alertParams);
        getWindow().setCloseOnTouchOutside(false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreate$0 */
    public /* synthetic */ void lambda$onCreate$0$NotificationAccessConfirmationActivity(DialogInterface dialogInterface, int i) {
        onAllow();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreate$1 */
    public /* synthetic */ void lambda$onCreate$1$NotificationAccessConfirmationActivity(DialogInterface dialogInterface, int i) {
        cancel();
    }

    public void onResume() {
        super.onResume();
        getWindow().addFlags(524288);
    }

    public void onPause() {
        getWindow().clearFlags(524288);
        super.onPause();
    }

    private void onAllow() {
        try {
            if (!"android.permission.BIND_NOTIFICATION_LISTENER_SERVICE".equals(getPackageManager().getServiceInfo(this.mComponentName, 0).permission)) {
                Slog.e("NotificationAccessConfirmationActivity", "Service " + this.mComponentName + " lacks permission android.permission.BIND_NOTIFICATION_LISTENER_SERVICE");
                return;
            }
            this.mNm.setNotificationListenerAccessGranted(this.mComponentName, true);
            finish();
        } catch (PackageManager.NameNotFoundException e) {
            Slog.e("NotificationAccessConfirmationActivity", "Failed to get service info for " + this.mComponentName, e);
        }
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        return AlertActivity.dispatchPopulateAccessibilityEvent(this, accessibilityEvent);
    }

    public void cancel() {
        finish();
    }

    public void dismiss() {
        if (!isFinishing()) {
            finish();
        }
    }
}
