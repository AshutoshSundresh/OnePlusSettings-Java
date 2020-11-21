package com.android.settings.development;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;
import com.android.settings.C0010R$id;
import com.android.settings.C0017R$string;

public class AdbWirelessDialogController {
    private Context mContext;
    private TextView mFailedMsg;
    private TextView mIpAddr = ((TextView) this.mView.findViewById(C0010R$id.ip_addr));
    private int mMode;
    private TextView mSixDigitCode = ((TextView) this.mView.findViewById(C0010R$id.pairing_code));
    private final AdbWirelessDialogUiBase mUi;
    private final View mView;

    public AdbWirelessDialogController(AdbWirelessDialogUiBase adbWirelessDialogUiBase, View view, int i) {
        this.mUi = adbWirelessDialogUiBase;
        this.mView = view;
        this.mMode = i;
        Context context = adbWirelessDialogUiBase.getContext();
        this.mContext = context;
        Resources resources = context.getResources();
        int i2 = this.mMode;
        if (i2 == 0) {
            this.mUi.setTitle(resources.getString(C0017R$string.adb_pairing_device_dialog_title));
            this.mView.findViewById(C0010R$id.l_pairing_six_digit).setVisibility(0);
            this.mUi.setCancelButton(resources.getString(C0017R$string.cancel));
            this.mUi.setCanceledOnTouchOutside(false);
        } else if (i2 == 2) {
            String string = resources.getString(C0017R$string.adb_pairing_device_dialog_failed_msg);
            this.mUi.setTitle(C0017R$string.adb_pairing_device_dialog_failed_title);
            this.mView.findViewById(C0010R$id.l_pairing_failed).setVisibility(0);
            TextView textView = (TextView) this.mView.findViewById(C0010R$id.pairing_failed_label);
            this.mFailedMsg = textView;
            textView.setText(string);
            this.mUi.setSubmitButton(resources.getString(C0017R$string.okay));
        } else if (i2 == 3) {
            this.mUi.setTitle(C0017R$string.adb_pairing_device_dialog_failed_title);
            this.mView.findViewById(C0010R$id.l_qrcode_pairing_failed).setVisibility(0);
            this.mUi.setSubmitButton(resources.getString(C0017R$string.okay));
        }
        this.mView.findViewById(C0010R$id.l_adbwirelessdialog).requestFocus();
    }

    public void setPairingCode(String str) {
        this.mSixDigitCode.setText(str);
    }

    public void setIpAddr(String str) {
        this.mIpAddr.setText(str);
    }
}
