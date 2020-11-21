package com.android.settings.enterprise;

import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import androidx.appcompat.app.AlertDialog;
import com.android.settingslib.RestrictedLockUtils;

public class ActionDisabledByAdminDialog extends Activity implements DialogInterface.OnDismissListener {
    private ActionDisabledByAdminDialogHelper mDialogHelper;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        RestrictedLockUtils.EnforcedAdmin adminDetailsFromIntent = getAdminDetailsFromIntent(getIntent());
        String restrictionFromIntent = getRestrictionFromIntent(getIntent());
        ActionDisabledByAdminDialogHelper actionDisabledByAdminDialogHelper = new ActionDisabledByAdminDialogHelper(this);
        this.mDialogHelper = actionDisabledByAdminDialogHelper;
        AlertDialog.Builder prepareDialogBuilder = actionDisabledByAdminDialogHelper.prepareDialogBuilder(restrictionFromIntent, adminDetailsFromIntent);
        prepareDialogBuilder.setOnDismissListener(this);
        prepareDialogBuilder.show();
    }

    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        RestrictedLockUtils.EnforcedAdmin adminDetailsFromIntent = getAdminDetailsFromIntent(intent);
        this.mDialogHelper.updateDialog(getRestrictionFromIntent(intent), adminDetailsFromIntent);
    }

    /* access modifiers changed from: package-private */
    public RestrictedLockUtils.EnforcedAdmin getAdminDetailsFromIntent(Intent intent) {
        RestrictedLockUtils.EnforcedAdmin enforcedAdmin = new RestrictedLockUtils.EnforcedAdmin(null, UserHandle.of(UserHandle.myUserId()));
        if (intent == null) {
            return enforcedAdmin;
        }
        enforcedAdmin.component = (ComponentName) intent.getParcelableExtra("android.app.extra.DEVICE_ADMIN");
        if (intent.hasExtra("android.intent.extra.USER")) {
            enforcedAdmin.user = (UserHandle) intent.getParcelableExtra("android.intent.extra.USER");
        } else {
            int intExtra = intent.getIntExtra("android.intent.extra.USER_ID", UserHandle.myUserId());
            if (intExtra == -10000) {
                enforcedAdmin.user = null;
            } else {
                enforcedAdmin.user = UserHandle.of(intExtra);
            }
        }
        return enforcedAdmin;
    }

    /* access modifiers changed from: package-private */
    public String getRestrictionFromIntent(Intent intent) {
        if (intent == null) {
            return null;
        }
        return intent.getStringExtra("android.app.extra.RESTRICTION");
    }

    public void onDismiss(DialogInterface dialogInterface) {
        finish();
    }
}
