package com.android.settings;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Process;
import android.os.UserHandle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController;

public class AllowBindAppWidgetActivity extends AlertActivity implements DialogInterface.OnClickListener {
    private CheckBox mAlwaysUse;
    private int mAppWidgetId;
    private AppWidgetManager mAppWidgetManager;
    private Bundle mBindOptions;
    private String mCallingPackage;
    private boolean mClicked;
    private ComponentName mComponentName;
    private UserHandle mProfile;

    public void onClick(DialogInterface dialogInterface, int i) {
        int i2;
        ComponentName componentName;
        this.mClicked = true;
        if (!(i != -1 || (i2 = this.mAppWidgetId) == -1 || (componentName = this.mComponentName) == null || this.mCallingPackage == null)) {
            try {
                if (this.mAppWidgetManager.bindAppWidgetIdIfAllowed(i2, this.mProfile, componentName, this.mBindOptions)) {
                    Intent intent = new Intent();
                    intent.putExtra("appWidgetId", this.mAppWidgetId);
                    setResult(-1, intent);
                }
            } catch (Exception unused) {
                Log.v("BIND_APPWIDGET", "Error binding widget with id " + this.mAppWidgetId + " and component " + this.mComponentName);
            }
            boolean isChecked = this.mAlwaysUse.isChecked();
            if (isChecked != this.mAppWidgetManager.hasBindAppWidgetPermission(this.mCallingPackage)) {
                this.mAppWidgetManager.setBindAppWidgetPermission(this.mCallingPackage, isChecked);
            }
        }
        finish();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        if (!this.mClicked) {
            finish();
        }
        AllowBindAppWidgetActivity.super.onPause();
    }

    /* JADX DEBUG: Multi-variable search result rejected for r6v0, resolved type: com.android.settings.AllowBindAppWidgetActivity */
    /* JADX WARN: Multi-variable type inference failed */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        CharSequence charSequence;
        AllowBindAppWidgetActivity.super.onCreate(bundle);
        setResult(0);
        Intent intent = getIntent();
        if (intent != null) {
            try {
                this.mAppWidgetId = intent.getIntExtra("appWidgetId", -1);
                UserHandle userHandle = (UserHandle) intent.getParcelableExtra("appWidgetProviderProfile");
                this.mProfile = userHandle;
                if (userHandle == null) {
                    this.mProfile = Process.myUserHandle();
                }
                this.mComponentName = (ComponentName) intent.getParcelableExtra("appWidgetProvider");
                this.mBindOptions = (Bundle) intent.getParcelableExtra("appWidgetOptions");
                this.mCallingPackage = getCallingPackage();
                PackageManager packageManager = getPackageManager();
                charSequence = packageManager.getApplicationLabel(packageManager.getApplicationInfo(this.mCallingPackage, 0));
            } catch (Exception unused) {
                this.mAppWidgetId = -1;
                this.mComponentName = null;
                this.mCallingPackage = null;
                Log.v("BIND_APPWIDGET", "Error getting parameters");
                finish();
                return;
            }
        } else {
            charSequence = "";
        }
        AlertController.AlertParams alertParams = ((AlertActivity) this).mAlertParams;
        alertParams.mTitle = getString(C0017R$string.allow_bind_app_widget_activity_allow_bind_title);
        alertParams.mMessage = getString(C0017R$string.allow_bind_app_widget_activity_allow_bind, new Object[]{charSequence});
        alertParams.mPositiveButtonText = getString(C0017R$string.create);
        alertParams.mNegativeButtonText = getString(17039360);
        alertParams.mPositiveButtonListener = this;
        alertParams.mNegativeButtonListener = this;
        View inflate = ((LayoutInflater) getSystemService("layout_inflater")).inflate(17367092, (ViewGroup) null);
        alertParams.mView = inflate;
        CheckBox checkBox = (CheckBox) inflate.findViewById(16908751);
        this.mAlwaysUse = checkBox;
        checkBox.setText(getString(C0017R$string.allow_bind_app_widget_activity_always_allow_bind, new Object[]{charSequence}));
        CheckBox checkBox2 = this.mAlwaysUse;
        checkBox2.setPadding(checkBox2.getPaddingLeft(), this.mAlwaysUse.getPaddingTop(), this.mAlwaysUse.getPaddingRight(), (int) (((float) this.mAlwaysUse.getPaddingBottom()) + getResources().getDimension(C0007R$dimen.bind_app_widget_dialog_checkbox_bottom_padding)));
        AppWidgetManager instance = AppWidgetManager.getInstance(this);
        this.mAppWidgetManager = instance;
        this.mAlwaysUse.setChecked(instance.hasBindAppWidgetPermission(this.mCallingPackage, this.mProfile.getIdentifier()));
        setupAlert();
    }
}
