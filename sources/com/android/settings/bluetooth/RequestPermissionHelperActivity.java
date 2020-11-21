package com.android.settings.bluetooth;

import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Log;
import androidx.appcompat.R$styleable;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController;
import com.android.settings.C0005R$bool;
import com.android.settings.C0017R$string;

public class RequestPermissionHelperActivity extends AlertActivity implements DialogInterface.OnClickListener {
    private CharSequence mAppLabel;
    private BluetoothAdapter mBluetoothAdapter;
    private int mRequest;
    private int mTimeout = -1;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        RequestPermissionHelperActivity.super.onCreate(bundle);
        setResult(0);
        if (!parseIntent()) {
            finish();
            return;
        }
        if (getResources().getBoolean(C0005R$bool.auto_confirm_bluetooth_activation_dialog)) {
            onClick(null, -1);
            dismiss();
        }
        createDialog();
    }

    /* access modifiers changed from: package-private */
    public void createDialog() {
        String str;
        String str2;
        String str3;
        String str4;
        AlertController.AlertParams alertParams = ((AlertActivity) this).mAlertParams;
        int i = this.mRequest;
        if (i == 1) {
            int i2 = this.mTimeout;
            if (i2 < 0) {
                CharSequence charSequence = this.mAppLabel;
                if (charSequence != null) {
                    str3 = getString(C0017R$string.bluetooth_ask_enablement, new Object[]{charSequence});
                } else {
                    str3 = getString(C0017R$string.bluetooth_ask_enablement_no_name);
                }
                alertParams.mMessage = str3;
            } else if (i2 == 0) {
                CharSequence charSequence2 = this.mAppLabel;
                if (charSequence2 != null) {
                    str2 = getString(C0017R$string.bluetooth_ask_enablement_and_lasting_discovery, new Object[]{charSequence2});
                } else {
                    str2 = getString(C0017R$string.bluetooth_ask_enablement_and_lasting_discovery_no_name);
                }
                alertParams.mMessage = str2;
            } else {
                CharSequence charSequence3 = this.mAppLabel;
                if (charSequence3 != null) {
                    str = getString(C0017R$string.bluetooth_ask_enablement_and_discovery, new Object[]{charSequence3, Integer.valueOf(i2)});
                } else {
                    str = getString(C0017R$string.bluetooth_ask_enablement_and_discovery_no_name, new Object[]{Integer.valueOf(i2)});
                }
                alertParams.mMessage = str;
            }
        } else if (i == 3) {
            CharSequence charSequence4 = this.mAppLabel;
            if (charSequence4 != null) {
                str4 = getString(C0017R$string.bluetooth_ask_disablement, new Object[]{charSequence4});
            } else {
                str4 = getString(C0017R$string.bluetooth_ask_disablement_no_name);
            }
            alertParams.mMessage = str4;
        }
        alertParams.mPositiveButtonText = getString(C0017R$string.allow);
        alertParams.mPositiveButtonListener = this;
        alertParams.mNegativeButtonText = getString(C0017R$string.deny);
        setupAlert();
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        int i2 = this.mRequest;
        if (i2 == 1 || i2 == 2) {
            if (((UserManager) getSystemService(UserManager.class)).hasUserRestriction("no_bluetooth")) {
                Intent createAdminSupportIntent = ((DevicePolicyManager) getSystemService(DevicePolicyManager.class)).createAdminSupportIntent("no_bluetooth");
                if (createAdminSupportIntent != null) {
                    startActivity(createAdminSupportIntent);
                    return;
                }
                return;
            }
            this.mBluetoothAdapter.enable();
            setResult(-1);
        } else if (i2 == 3) {
            this.mBluetoothAdapter.disable();
            setResult(-1);
        }
    }

    private boolean parseIntent() {
        Intent intent = getIntent();
        if (intent == null) {
            return false;
        }
        String action = intent.getAction();
        if ("com.android.settings.bluetooth.ACTION_INTERNAL_REQUEST_BT_ON".equals(action)) {
            this.mRequest = 1;
            if (intent.hasExtra("android.bluetooth.adapter.extra.DISCOVERABLE_DURATION")) {
                this.mTimeout = intent.getIntExtra("android.bluetooth.adapter.extra.DISCOVERABLE_DURATION", R$styleable.AppCompatTheme_windowFixedHeightMajor);
            }
        } else if (!"com.android.settings.bluetooth.ACTION_INTERNAL_REQUEST_BT_OFF".equals(action)) {
            return false;
        } else {
            this.mRequest = 3;
        }
        this.mAppLabel = getIntent().getCharSequenceExtra("com.android.settings.bluetooth.extra.APP_LABEL");
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mBluetoothAdapter = defaultAdapter;
        if (defaultAdapter != null) {
            return true;
        }
        Log.e("RequestPermissionHelperActivity", "Error: there's a problem starting Bluetooth");
        return false;
    }
}
