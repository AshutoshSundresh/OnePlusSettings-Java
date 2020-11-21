package com.android.settings.password;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.app.admin.PasswordMetrics;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.password.SetNewPasswordController;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.google.android.setupcompat.util.WizardManagerHelper;
import java.util.List;

public class SetNewPasswordActivity extends Activity implements SetNewPasswordController.Ui {
    private String mCallerAppName = null;
    private String mNewPasswordAction;
    private int mRequestedMinComplexity = 0;
    private SetNewPasswordController mSetNewPasswordController;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        String action = getIntent().getAction();
        this.mNewPasswordAction = action;
        if ("android.app.action.SET_NEW_PASSWORD".equals(action) || "android.app.action.SET_NEW_PARENT_PROFILE_PASSWORD".equals(this.mNewPasswordAction)) {
            logSetNewPasswordIntent();
            IBinder activityToken = getActivityToken();
            this.mCallerAppName = (String) PasswordUtils.getCallingAppLabel(this, activityToken);
            if ("android.app.action.SET_NEW_PASSWORD".equals(this.mNewPasswordAction) && getIntent().hasExtra("android.app.extra.PASSWORD_COMPLEXITY")) {
                if (PasswordUtils.isCallingAppPermitted(this, activityToken, "android.permission.REQUEST_PASSWORD_COMPLEXITY")) {
                    this.mRequestedMinComplexity = PasswordMetrics.sanitizeComplexityLevel(getIntent().getIntExtra("android.app.extra.PASSWORD_COMPLEXITY", 0));
                } else {
                    PasswordUtils.crashCallingApplication(activityToken, "Must have permission android.permission.REQUEST_PASSWORD_COMPLEXITY to use extra android.app.extra.PASSWORD_COMPLEXITY");
                    finish();
                    return;
                }
            }
            SetNewPasswordController create = SetNewPasswordController.create(this, this, getIntent(), getActivityToken());
            this.mSetNewPasswordController = create;
            create.dispatchSetNewPasswordIntent();
            return;
        }
        Log.e("SetNewPasswordActivity", "Unexpected action to launch this activity");
        finish();
    }

    @Override // com.android.settings.password.SetNewPasswordController.Ui
    public void launchChooseLock(Bundle bundle) {
        Intent intent;
        if (WizardManagerHelper.isAnySetupWizard(getIntent())) {
            intent = new Intent(this, SetupChooseLockGeneric.class);
        } else {
            intent = new Intent(this, ChooseLockGeneric.class);
        }
        intent.setAction(this.mNewPasswordAction);
        intent.putExtras(bundle);
        String str = this.mCallerAppName;
        if (str != null) {
            intent.putExtra("caller_app_name", str);
        }
        int i = this.mRequestedMinComplexity;
        if (i != 0) {
            intent.putExtra("requested_min_complexity", i);
        }
        if (isCallingAppAdmin()) {
            intent.putExtra("is_calling_app_admin", true);
        }
        startActivity(intent);
        finish();
    }

    private boolean isCallingAppAdmin() {
        String callingAppPackageName = PasswordUtils.getCallingAppPackageName(getActivityToken());
        List<ComponentName> activeAdmins = ((DevicePolicyManager) getSystemService(DevicePolicyManager.class)).getActiveAdmins();
        if (activeAdmins == null) {
            return false;
        }
        for (ComponentName componentName : activeAdmins) {
            if (componentName.getPackageName().equals(callingAppPackageName)) {
                return true;
            }
        }
        return false;
    }

    private void logSetNewPasswordIntent() {
        String callingAppPackageName = PasswordUtils.getCallingAppPackageName(getActivityToken());
        int intExtra = getIntent().hasExtra("android.app.extra.PASSWORD_COMPLEXITY") ? getIntent().getIntExtra("android.app.extra.PASSWORD_COMPLEXITY", 0) : Integer.MIN_VALUE;
        int i = "android.app.action.SET_NEW_PASSWORD".equals(this.mNewPasswordAction) ? 1645 : 1646;
        MetricsFeatureProvider metricsFeatureProvider = FeatureFactory.getFactory(this).getMetricsFeatureProvider();
        metricsFeatureProvider.action(metricsFeatureProvider.getAttribution(this), i, 1644, callingAppPackageName, intExtra);
    }
}
