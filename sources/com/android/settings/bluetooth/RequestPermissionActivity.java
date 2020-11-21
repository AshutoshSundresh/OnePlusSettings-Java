package com.android.settings.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import androidx.appcompat.R$styleable;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C0005R$bool;
import com.android.settings.C0017R$string;
import com.android.settings.bluetooth.RequestPermissionActivity;
import com.android.settingslib.bluetooth.BluetoothDiscoverableTimeoutReceiver;

public class RequestPermissionActivity extends Activity implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener {
    private CharSequence mAppLabel;
    private BluetoothAdapter mBluetoothAdapter;
    private AlertDialog mDialog;
    private BroadcastReceiver mReceiver;
    private int mRequest;
    private int mTimeout = R$styleable.AppCompatTheme_windowFixedHeightMajor;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addSystemFlags(524288);
        setResult(0);
        if (parseIntent()) {
            finish();
            return;
        }
        int state = this.mBluetoothAdapter.getState();
        int i = this.mRequest;
        if (i == 3) {
            switch (state) {
                case 10:
                case 13:
                    proceedAndFinish();
                    return;
                case 11:
                case 12:
                    Intent intent = new Intent(this, RequestPermissionHelperActivity.class);
                    intent.putExtra("com.android.settings.bluetooth.extra.APP_LABEL", this.mAppLabel);
                    intent.setAction("com.android.settings.bluetooth.ACTION_INTERNAL_REQUEST_BT_OFF");
                    startActivityForResult(intent, 0);
                    return;
                default:
                    Log.e("BtRequestPermission", "Unknown adapter state: " + state);
                    cancelAndFinish();
                    return;
            }
        } else {
            switch (state) {
                case 10:
                case 11:
                case 13:
                    Intent intent2 = new Intent(this, RequestPermissionHelperActivity.class);
                    intent2.setAction("com.android.settings.bluetooth.ACTION_INTERNAL_REQUEST_BT_ON");
                    intent2.putExtra("com.android.settings.bluetooth.extra.APP_LABEL", this.mAppLabel);
                    if (this.mRequest == 2) {
                        intent2.putExtra("android.bluetooth.adapter.extra.DISCOVERABLE_DURATION", this.mTimeout);
                    }
                    startActivityForResult(intent2, 0);
                    return;
                case 12:
                    if (i == 1) {
                        proceedAndFinish();
                        return;
                    } else {
                        createDialog();
                        return;
                    }
                default:
                    Log.e("BtRequestPermission", "Unknown adapter state: " + state);
                    cancelAndFinish();
                    return;
            }
        }
    }

    private void createDialog() {
        String str;
        String str2;
        if (getResources().getBoolean(C0005R$bool.auto_confirm_bluetooth_activation_dialog)) {
            onClick(null, -1);
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (this.mReceiver != null) {
            int i = this.mRequest;
            if (i == 1 || i == 2) {
                builder.setMessage(getString(C0017R$string.bluetooth_turning_on));
            } else {
                builder.setMessage(getString(C0017R$string.bluetooth_turning_off));
            }
            builder.setCancelable(false);
        } else {
            int i2 = this.mTimeout;
            if (i2 == 0) {
                CharSequence charSequence = this.mAppLabel;
                if (charSequence != null) {
                    str2 = getString(C0017R$string.bluetooth_ask_lasting_discovery, new Object[]{charSequence});
                } else {
                    str2 = getString(C0017R$string.bluetooth_ask_lasting_discovery_no_name);
                }
                builder.setMessage(str2);
            } else {
                CharSequence charSequence2 = this.mAppLabel;
                if (charSequence2 != null) {
                    str = getString(C0017R$string.bluetooth_ask_discovery, new Object[]{charSequence2, Integer.valueOf(i2)});
                } else {
                    str = getString(C0017R$string.bluetooth_ask_discovery_no_name, new Object[]{Integer.valueOf(i2)});
                }
                builder.setMessage(str);
            }
            builder.setPositiveButton(getString(C0017R$string.allow), this);
            builder.setNegativeButton(getString(C0017R$string.deny), this);
        }
        builder.setOnDismissListener(this);
        AlertDialog create = builder.create();
        this.mDialog = create;
        create.show();
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i2 != -1) {
            cancelAndFinish();
            return;
        }
        int i3 = this.mRequest;
        if (i3 == 1 || i3 == 2) {
            if (this.mBluetoothAdapter.getState() == 12) {
                proceedAndFinish();
                return;
            }
            StateChangeReceiver stateChangeReceiver = new StateChangeReceiver();
            this.mReceiver = stateChangeReceiver;
            registerReceiver(stateChangeReceiver, new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED"));
            createDialog();
        } else if (i3 != 3) {
            cancelAndFinish();
        } else if (this.mBluetoothAdapter.getState() == 10) {
            proceedAndFinish();
        } else {
            StateChangeReceiver stateChangeReceiver2 = new StateChangeReceiver();
            this.mReceiver = stateChangeReceiver2;
            registerReceiver(stateChangeReceiver2, new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED"));
            createDialog();
        }
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -2) {
            cancelAndFinish();
        } else if (i == -1) {
            proceedAndFinish();
        }
    }

    public void onDismiss(DialogInterface dialogInterface) {
        cancelAndFinish();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void proceedAndFinish() {
        int i = this.mRequest;
        int i2 = 1;
        if (i == 1 || i == 3) {
            i2 = -1;
        } else if (this.mBluetoothAdapter.setScanMode(23, (long) this.mTimeout)) {
            long currentTimeMillis = System.currentTimeMillis() + (((long) this.mTimeout) * 1000);
            LocalBluetoothPreferences.persistDiscoverableEndTimestamp(this, currentTimeMillis);
            if (this.mTimeout > 0) {
                BluetoothDiscoverableTimeoutReceiver.setDiscoverableAlarm(this, currentTimeMillis);
            }
            int i3 = this.mTimeout;
            if (i3 >= 1) {
                i2 = i3;
            }
        } else {
            i2 = 0;
        }
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        setResult(i2);
        finish();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void cancelAndFinish() {
        setResult(0);
        finish();
    }

    private boolean parseIntent() {
        Intent intent = getIntent();
        if (intent == null) {
            return true;
        }
        if (intent.getAction().equals("android.bluetooth.adapter.action.REQUEST_ENABLE")) {
            this.mRequest = 1;
        } else if (intent.getAction().equals("android.bluetooth.adapter.action.REQUEST_DISABLE")) {
            this.mRequest = 3;
        } else if (intent.getAction().equals("android.bluetooth.adapter.action.REQUEST_DISCOVERABLE")) {
            this.mRequest = 2;
            this.mTimeout = intent.getIntExtra("android.bluetooth.adapter.extra.DISCOVERABLE_DURATION", R$styleable.AppCompatTheme_windowFixedHeightMajor);
            Log.d("BtRequestPermission", "Setting Bluetooth Discoverable Timeout = " + this.mTimeout);
            int i = this.mTimeout;
            if (i < 1 || i > 3600) {
                this.mTimeout = R$styleable.AppCompatTheme_windowFixedHeightMajor;
            }
        } else {
            Log.e("BtRequestPermission", "Error: this activity may be started only with intent android.bluetooth.adapter.action.REQUEST_ENABLE or android.bluetooth.adapter.action.REQUEST_DISCOVERABLE");
            setResult(0);
            return true;
        }
        String callingPackage = getCallingPackage();
        if (TextUtils.isEmpty(callingPackage)) {
            callingPackage = getIntent().getStringExtra("android.intent.extra.PACKAGE_NAME");
        }
        if (!TextUtils.isEmpty(callingPackage)) {
            try {
                this.mAppLabel = getPackageManager().getApplicationInfo(callingPackage, 0).loadSafeLabel(getPackageManager(), 500.0f, 5);
            } catch (PackageManager.NameNotFoundException unused) {
                Log.e("BtRequestPermission", "Couldn't find app with package name " + callingPackage);
                setResult(0);
                return true;
            }
        }
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mBluetoothAdapter = defaultAdapter;
        if (defaultAdapter != null) {
            return false;
        }
        Log.e("BtRequestPermission", "Error: there's a problem starting Bluetooth");
        setResult(0);
        return true;
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        BroadcastReceiver broadcastReceiver = this.mReceiver;
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
            this.mReceiver = null;
        }
    }

    public void onBackPressed() {
        setResult(0);
        super.onBackPressed();
    }

    /* access modifiers changed from: private */
    public final class StateChangeReceiver extends BroadcastReceiver {
        public StateChangeReceiver() {
            RequestPermissionActivity.this.getWindow().getDecorView().postDelayed(new Runnable() {
                /* class com.android.settings.bluetooth.$$Lambda$RequestPermissionActivity$StateChangeReceiver$q4ZilZjRzY7SLoogXiJIIa__yMA */

                public final void run() {
                    RequestPermissionActivity.StateChangeReceiver.this.lambda$new$0$RequestPermissionActivity$StateChangeReceiver();
                }
            }, 10000);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$0 */
        public /* synthetic */ void lambda$new$0$RequestPermissionActivity$StateChangeReceiver() {
            if (!RequestPermissionActivity.this.isFinishing() && !RequestPermissionActivity.this.isDestroyed()) {
                RequestPermissionActivity.this.cancelAndFinish();
            }
        }

        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                int intExtra = intent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE);
                int i = RequestPermissionActivity.this.mRequest;
                if (i == 1 || i == 2) {
                    if (intExtra == 12) {
                        RequestPermissionActivity.this.proceedAndFinish();
                    }
                } else if (i == 3 && intExtra == 10) {
                    RequestPermissionActivity.this.proceedAndFinish();
                }
            }
        }
    }
}
