package com.android.settings.wifi;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.android.settings.C0017R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.oneplus.settings.BaseAppCompatActivity;

public class WifiScanModeActivity extends BaseAppCompatActivity {
    private String mApp;
    private DialogFragment mDialog;

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        if (bundle != null) {
            this.mApp = bundle.getString("app");
        } else if (intent == null || !"android.net.wifi.action.REQUEST_SCAN_ALWAYS_AVAILABLE".equals(intent.getAction())) {
            finish();
            return;
        } else {
            this.mApp = getCallingPackage();
            try {
                PackageManager packageManager = getPackageManager();
                this.mApp = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(this.mApp, 0));
            } catch (PackageManager.NameNotFoundException unused) {
            }
        }
        createDialog();
    }

    private void createDialog() {
        if (this.mDialog == null) {
            AlertDialogFragment newInstance = AlertDialogFragment.newInstance(this.mApp);
            this.mDialog = newInstance;
            newInstance.show(getSupportFragmentManager(), "dialog");
        }
    }

    private void dismissDialog() {
        DialogFragment dialogFragment = this.mDialog;
        if (dialogFragment != null) {
            dialogFragment.dismiss();
            this.mDialog = null;
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void doPositiveClick() {
        ((WifiManager) getApplicationContext().getSystemService(WifiManager.class)).setScanAlwaysAvailable(true);
        setResult(-1);
        finish();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void doNegativeClick() {
        setResult(0);
        finish();
    }

    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString("app", this.mApp);
    }

    @Override // androidx.fragment.app.FragmentActivity
    public void onPause() {
        super.onPause();
        dismissDialog();
    }

    @Override // androidx.fragment.app.FragmentActivity
    public void onResume() {
        super.onResume();
        createDialog();
    }

    public static class AlertDialogFragment extends InstrumentedDialogFragment {
        private final String mApp;

        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 543;
        }

        static AlertDialogFragment newInstance(String str) {
            return new AlertDialogFragment(str);
        }

        public AlertDialogFragment(String str) {
            this.mApp = str;
        }

        public AlertDialogFragment() {
            this.mApp = null;
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            String str;
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            if (TextUtils.isEmpty(this.mApp)) {
                str = getString(C0017R$string.wifi_scan_always_turn_on_message_unknown);
            } else {
                str = getString(C0017R$string.wifi_scan_always_turnon_message, this.mApp);
            }
            builder.setMessage(str);
            builder.setPositiveButton(C0017R$string.wifi_scan_always_confirm_allow, new DialogInterface.OnClickListener() {
                /* class com.android.settings.wifi.WifiScanModeActivity.AlertDialogFragment.AnonymousClass2 */

                public void onClick(DialogInterface dialogInterface, int i) {
                    ((WifiScanModeActivity) AlertDialogFragment.this.getActivity()).doPositiveClick();
                }
            });
            builder.setNegativeButton(C0017R$string.wifi_scan_always_confirm_deny, new DialogInterface.OnClickListener() {
                /* class com.android.settings.wifi.WifiScanModeActivity.AlertDialogFragment.AnonymousClass1 */

                public void onClick(DialogInterface dialogInterface, int i) {
                    ((WifiScanModeActivity) AlertDialogFragment.this.getActivity()).doNegativeClick();
                }
            });
            return builder.create();
        }

        @Override // androidx.fragment.app.DialogFragment
        public void onCancel(DialogInterface dialogInterface) {
            ((WifiScanModeActivity) getActivity()).doNegativeClick();
        }
    }
}
