package com.android.settings;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.oneplus.settings.utils.OPUtils;

public class OPDeviceNameActivity extends Activity implements DialogInterface.OnDismissListener {
    private AlertDialog mDialog;

    private boolean isNotEmojiCharacter(char c) {
        return c == 0 || c == '\t' || c == '\n' || c == '\r' || (c >= ' ' && c <= 55295) || ((c >= 57344 && c <= 65533) || (c >= 0 && c <= 65535));
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        createDialog();
    }

    private void createDialog() {
        View inflate = LayoutInflater.from(this).inflate(C0012R$layout.op_device_name_dialog, (ViewGroup) null);
        EditText editText = (EditText) inflate.findViewById(C0010R$id.edit_device_name);
        initDeviceName(editText);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(C0017R$string.my_device_info_device_name_preference_title);
        builder.setView(inflate);
        builder.setNegativeButton(17039360, new DialogInterface.OnClickListener() {
            /* class com.android.settings.$$Lambda$OPDeviceNameActivity$R1rs1sdSj5hWOOUiBVQ4cuMHxuc */

            public final void onClick(DialogInterface dialogInterface, int i) {
                OPDeviceNameActivity.this.lambda$createDialog$0$OPDeviceNameActivity(dialogInterface, i);
            }
        });
        builder.setPositiveButton(17039370, new DialogInterface.OnClickListener(editText) {
            /* class com.android.settings.$$Lambda$OPDeviceNameActivity$sF6cmpBdviiclSAD4JGw7YnU0dM */
            public final /* synthetic */ EditText f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                OPDeviceNameActivity.this.lambda$createDialog$1$OPDeviceNameActivity(this.f$1, dialogInterface, i);
            }
        });
        builder.setCancelable(true);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            /* class com.android.settings.$$Lambda$OPDeviceNameActivity$SdH1IjLZudbJ_QjRcAsplCXjyc */

            public final void onCancel(DialogInterface dialogInterface) {
                OPDeviceNameActivity.this.lambda$createDialog$2$OPDeviceNameActivity(dialogInterface);
            }
        });
        AlertDialog create = builder.create();
        this.mDialog = create;
        create.setOnDismissListener(this);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createDialog$0 */
    public /* synthetic */ void lambda$createDialog$0$OPDeviceNameActivity(DialogInterface dialogInterface, int i) {
        this.mDialog.dismiss();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createDialog$1 */
    public /* synthetic */ void lambda$createDialog$1$OPDeviceNameActivity(EditText editText, DialogInterface dialogInterface, int i) {
        String trim = editText.getText().toString().trim();
        if (trim.length() != 0) {
            if (trim.equalsIgnoreCase("null")) {
                Toast.makeText(this, C0017R$string.wifi_p2p_failed_rename_message, 0).show();
                this.mDialog.dismiss();
                return;
            }
            for (int i2 = 0; i2 < trim.length(); i2++) {
                if (!isNotEmojiCharacter(trim.charAt(i2))) {
                    Toast.makeText(this, C0017R$string.wifi_p2p_failed_rename_message, 0).show();
                    this.mDialog.dismiss();
                    return;
                }
            }
            Settings.System.putString(getContentResolver(), "oem_oneplus_devicename", trim);
            Settings.System.putString(getContentResolver(), "oem_oneplus_modified_devicename", "1");
            BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
            if (defaultAdapter != null) {
                defaultAdapter.setName(trim);
            }
            WifiP2pManager wifiP2pManager = (WifiP2pManager) getSystemService("wifip2p");
            if (wifiP2pManager != null) {
                wifiP2pManager.setDeviceName(wifiP2pManager.initialize(this, getMainLooper(), null), trim, null);
            }
        }
        this.mDialog.dismiss();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createDialog$2 */
    public /* synthetic */ void lambda$createDialog$2$OPDeviceNameActivity(DialogInterface dialogInterface) {
        this.mDialog.dismiss();
    }

    private void initDeviceName(final EditText editText) {
        if (editText != null) {
            String string = Settings.System.getString(getContentResolver(), "oem_oneplus_modified_devicename");
            Settings.System.getString(getContentResolver(), "oem_oneplus_devicename");
            String resetDeviceNameIfInvalid = OPUtils.resetDeviceNameIfInvalid(this);
            if (string == null && (resetDeviceNameIfInvalid == null || resetDeviceNameIfInvalid.equals("oneplus") || resetDeviceNameIfInvalid.equals("ONE E1001") || resetDeviceNameIfInvalid.equals("ONE E1003") || resetDeviceNameIfInvalid.equals("ONE E1005"))) {
                resetDeviceNameIfInvalid = SystemProperties.get("ro.display.series");
                Settings.System.putString(getContentResolver(), "oem_oneplus_devicename", resetDeviceNameIfInvalid);
                Settings.System.putString(getContentResolver(), "oem_oneplus_modified_devicename", "1");
            }
            if (resetDeviceNameIfInvalid.length() > 32) {
                resetDeviceNameIfInvalid = resetDeviceNameIfInvalid.substring(0, 31);
                Settings.System.putString(getContentResolver(), "oem_oneplus_devicename", resetDeviceNameIfInvalid);
            }
            editText.setText(resetDeviceNameIfInvalid);
            editText.setSelection(resetDeviceNameIfInvalid.length());
            editText.selectAll();
            editText.addTextChangedListener(new TextWatcher() {
                /* class com.android.settings.OPDeviceNameActivity.AnonymousClass1 */
                String name;
                private String nameTemp;
                int num;

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    if (editText.length() != 0 && editText.getText() != null) {
                        this.nameTemp = editText.getText().toString();
                    }
                }

                public void afterTextChanged(Editable editable) {
                    if (editText.length() != 0) {
                        String obj = editText.getText().toString();
                        this.name = obj;
                        int length = obj.getBytes().length;
                        this.num = length;
                        if (length > 32) {
                            editText.setText(this.nameTemp);
                            Editable text = editText.getText();
                            if (text != null) {
                                Selection.setSelection(text, text.length());
                            }
                        }
                    }
                    OPDeviceNameActivity.this.mDialog.getButton(-1).setEnabled(editable.length() != 0 && !editable.toString().trim().isEmpty());
                }
            });
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        if (!this.mDialog.isShowing()) {
            this.mDialog.setCanceledOnTouchOutside(true);
            this.mDialog.show();
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog != null && alertDialog.isShowing()) {
            this.mDialog.dismiss();
            this.mDialog = null;
        }
    }

    public void onDismiss(DialogInterface dialogInterface) {
        this.mDialog = null;
        finish();
    }
}
