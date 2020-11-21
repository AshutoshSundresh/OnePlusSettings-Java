package com.android.settings;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import com.android.settings.accessibility.AccessibilityGestureNavigationTutorial;

public class SettingsTutorialDialogWrapperActivity extends Activity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        showDialog();
    }

    private void showDialog() {
        AccessibilityGestureNavigationTutorial.showGestureNavigationSettingsTutorialDialog(this, new DialogInterface.OnDismissListener() {
            /* class com.android.settings.$$Lambda$SettingsTutorialDialogWrapperActivity$6ZOKJwWBtDFoOEaf95p3PkTzlPE */

            public final void onDismiss(DialogInterface dialogInterface) {
                SettingsTutorialDialogWrapperActivity.this.lambda$showDialog$0$SettingsTutorialDialogWrapperActivity(dialogInterface);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showDialog$0 */
    public /* synthetic */ void lambda$showDialog$0$SettingsTutorialDialogWrapperActivity(DialogInterface dialogInterface) {
        finish();
    }
}
