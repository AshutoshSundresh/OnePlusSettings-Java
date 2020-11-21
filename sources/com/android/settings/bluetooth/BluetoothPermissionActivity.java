package com.android.settings.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.preference.Preference;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;

public class BluetoothPermissionActivity extends AlertActivity implements DialogInterface.OnClickListener, Preference.OnPreferenceChangeListener {
    private BluetoothDevice mDevice;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        /* class com.android.settings.bluetooth.BluetoothPermissionActivity.AnonymousClass1 */

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.bluetooth.device.action.CONNECTION_ACCESS_CANCEL") && intent.getIntExtra("android.bluetooth.device.extra.ACCESS_REQUEST_TYPE", 2) == BluetoothPermissionActivity.this.mRequestType) {
                if (BluetoothPermissionActivity.this.mDevice.equals((BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE"))) {
                    BluetoothPermissionActivity.this.dismissDialog();
                }
            }
        }
    };
    private boolean mReceiverRegistered = false;
    private int mRequestType = 0;
    private String mReturnClass = null;
    private String mReturnPackage = null;
    private View mView;
    private TextView messageView;

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        return true;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void dismissDialog() {
        dismiss();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        BluetoothPermissionActivity.super.onCreate(bundle);
        Intent intent = getIntent();
        if (!intent.getAction().equals("android.bluetooth.device.action.CONNECTION_ACCESS_REQUEST")) {
            Log.e("BluetoothPermissionActivity", "Error: this activity may be started only with intent ACTION_CONNECTION_ACCESS_REQUEST");
            finish();
            return;
        }
        this.mDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
        this.mReturnPackage = intent.getStringExtra("android.bluetooth.device.extra.PACKAGE_NAME");
        this.mReturnClass = intent.getStringExtra("android.bluetooth.device.extra.CLASS_NAME");
        this.mRequestType = intent.getIntExtra("android.bluetooth.device.extra.ACCESS_REQUEST_TYPE", 2);
        Log.i("BluetoothPermissionActivity", "onCreate() Request type: " + this.mRequestType);
        int i = this.mRequestType;
        if (i == 1) {
            showDialog(getString(C0017R$string.bluetooth_connection_permission_request), this.mRequestType);
        } else if (i == 2) {
            showDialog(getString(C0017R$string.bluetooth_phonebook_request), this.mRequestType);
        } else if (i == 3) {
            showDialog(getString(C0017R$string.bluetooth_map_request), this.mRequestType);
        } else if (i == 4) {
            showDialog(getString(C0017R$string.bluetooth_sap_request), this.mRequestType);
        } else {
            Log.e("BluetoothPermissionActivity", "Error: bad request type: " + this.mRequestType);
            finish();
            return;
        }
        registerReceiver(this.mReceiver, new IntentFilter("android.bluetooth.device.action.CONNECTION_ACCESS_CANCEL"));
        this.mReceiverRegistered = true;
    }

    private void showDialog(String str, int i) {
        AlertController.AlertParams alertParams = ((AlertActivity) this).mAlertParams;
        alertParams.mTitle = str;
        Log.i("BluetoothPermissionActivity", "showDialog() Request type: " + this.mRequestType + " this: " + this);
        if (i == 1) {
            alertParams.mView = createConnectionDialogView();
        } else if (i == 2) {
            alertParams.mView = createPhonebookDialogView();
        } else if (i == 3) {
            alertParams.mView = createMapDialogView();
        } else if (i == 4) {
            alertParams.mView = createSapDialogView();
        }
        alertParams.mPositiveButtonText = getString(C0017R$string.yes);
        alertParams.mPositiveButtonListener = this;
        alertParams.mNegativeButtonText = getString(C0017R$string.no);
        alertParams.mNegativeButtonListener = this;
        ((AlertActivity) this).mAlert.getButton(-1);
        setupAlert();
    }

    public void onBackPressed() {
        Log.i("BluetoothPermissionActivity", "Back button pressed! ignoring");
    }

    /* JADX DEBUG: Multi-variable search result rejected for r5v0, resolved type: com.android.settings.bluetooth.BluetoothPermissionActivity */
    /* JADX WARN: Multi-variable type inference failed */
    private View createConnectionDialogView() {
        String createRemoteName = Utils.createRemoteName(this, this.mDevice);
        View inflate = getLayoutInflater().inflate(C0012R$layout.bluetooth_access, (ViewGroup) null);
        this.mView = inflate;
        TextView textView = (TextView) inflate.findViewById(C0010R$id.message);
        this.messageView = textView;
        textView.setText(getString(C0017R$string.bluetooth_connection_dialog_text, new Object[]{createRemoteName}));
        return this.mView;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r5v0, resolved type: com.android.settings.bluetooth.BluetoothPermissionActivity */
    /* JADX WARN: Multi-variable type inference failed */
    private View createPhonebookDialogView() {
        String createRemoteName = Utils.createRemoteName(this, this.mDevice);
        View inflate = getLayoutInflater().inflate(C0012R$layout.bluetooth_access, (ViewGroup) null);
        this.mView = inflate;
        TextView textView = (TextView) inflate.findViewById(C0010R$id.message);
        this.messageView = textView;
        textView.setText(getString(C0017R$string.bluetooth_pb_acceptance_dialog_text, new Object[]{createRemoteName, createRemoteName}));
        return this.mView;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r5v0, resolved type: com.android.settings.bluetooth.BluetoothPermissionActivity */
    /* JADX WARN: Multi-variable type inference failed */
    private View createMapDialogView() {
        String createRemoteName = Utils.createRemoteName(this, this.mDevice);
        View inflate = getLayoutInflater().inflate(C0012R$layout.bluetooth_access, (ViewGroup) null);
        this.mView = inflate;
        TextView textView = (TextView) inflate.findViewById(C0010R$id.message);
        this.messageView = textView;
        textView.setText(getString(C0017R$string.bluetooth_map_acceptance_dialog_text, new Object[]{createRemoteName, createRemoteName}));
        return this.mView;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r5v0, resolved type: com.android.settings.bluetooth.BluetoothPermissionActivity */
    /* JADX WARN: Multi-variable type inference failed */
    private View createSapDialogView() {
        String createRemoteName = Utils.createRemoteName(this, this.mDevice);
        View inflate = getLayoutInflater().inflate(C0012R$layout.bluetooth_access, (ViewGroup) null);
        this.mView = inflate;
        TextView textView = (TextView) inflate.findViewById(C0010R$id.message);
        this.messageView = textView;
        textView.setText(getString(C0017R$string.bluetooth_sap_acceptance_dialog_text, new Object[]{createRemoteName, createRemoteName}));
        return this.mView;
    }

    private void onPositive() {
        Log.d("BluetoothPermissionActivity", "onPositive");
        sendReplyIntentToReceiver(true, true);
        finish();
    }

    private void onNegative() {
        Log.d("BluetoothPermissionActivity", "onNegative");
        sendReplyIntentToReceiver(false, true);
    }

    private void sendReplyIntentToReceiver(boolean z, boolean z2) {
        String str;
        Intent intent = new Intent("android.bluetooth.device.action.CONNECTION_ACCESS_REPLY");
        String str2 = this.mReturnPackage;
        if (!(str2 == null || (str = this.mReturnClass) == null)) {
            intent.setClassName(str2, str);
        }
        Log.i("BluetoothPermissionActivity", "sendReplyIntentToReceiver() Request type: " + this.mRequestType + " mReturnPackage" + this.mReturnPackage + " mReturnClass" + this.mReturnClass);
        intent.putExtra("android.bluetooth.device.extra.CONNECTION_ACCESS_RESULT", z ? 1 : 2);
        intent.putExtra("android.bluetooth.device.extra.ALWAYS_ALLOWED", z2);
        intent.putExtra("android.bluetooth.device.extra.DEVICE", this.mDevice);
        intent.putExtra("android.bluetooth.device.extra.ACCESS_REQUEST_TYPE", this.mRequestType);
        sendBroadcast(intent, "android.permission.BLUETOOTH_ADMIN");
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -2) {
            onNegative();
        } else if (i == -1) {
            onPositive();
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        BluetoothPermissionActivity.super.onDestroy();
        if (this.mReceiverRegistered) {
            unregisterReceiver(this.mReceiver);
            this.mReceiverRegistered = false;
        }
    }
}
