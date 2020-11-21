package com.android.settings;

import android.app.Activity;
import android.app.backup.IBackupManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SetFullBackupPassword extends Activity {
    IBackupManager mBackupManager;
    View.OnClickListener mButtonListener = new View.OnClickListener() {
        /* class com.android.settings.SetFullBackupPassword.AnonymousClass1 */

        public void onClick(View view) {
            SetFullBackupPassword setFullBackupPassword = SetFullBackupPassword.this;
            if (view == setFullBackupPassword.mSet) {
                String charSequence = setFullBackupPassword.mCurrentPw.getText().toString();
                String charSequence2 = SetFullBackupPassword.this.mNewPw.getText().toString();
                if (!charSequence2.equals(SetFullBackupPassword.this.mConfirmNewPw.getText().toString())) {
                    Log.i("SetFullBackupPassword", "password mismatch");
                    Toast.makeText(SetFullBackupPassword.this, C0017R$string.local_backup_password_toast_confirmation_mismatch, 1).show();
                } else if (SetFullBackupPassword.this.setBackupPassword(charSequence, charSequence2)) {
                    Log.i("SetFullBackupPassword", "password set successfully");
                    Toast.makeText(SetFullBackupPassword.this, C0017R$string.local_backup_password_toast_success, 1).show();
                    SetFullBackupPassword.this.finish();
                } else {
                    Log.i("SetFullBackupPassword", "failure; password mismatch?");
                    Toast.makeText(SetFullBackupPassword.this, C0017R$string.local_backup_password_toast_validation_failure, 1).show();
                }
            } else if (view == setFullBackupPassword.mCancel) {
                setFullBackupPassword.finish();
            } else {
                Log.w("SetFullBackupPassword", "Click on unknown view");
            }
        }
    };
    Button mCancel;
    TextView mConfirmNewPw;
    TextView mCurrentPw;
    TextView mNewPw;
    Button mSet;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mBackupManager = IBackupManager.Stub.asInterface(ServiceManager.getService("backup"));
        setContentView(C0012R$layout.set_backup_pw);
        this.mCurrentPw = (TextView) findViewById(C0010R$id.current_backup_pw);
        this.mNewPw = (TextView) findViewById(C0010R$id.new_backup_pw);
        this.mConfirmNewPw = (TextView) findViewById(C0010R$id.confirm_new_backup_pw);
        this.mCancel = (Button) findViewById(C0010R$id.backup_pw_cancel_button);
        this.mSet = (Button) findViewById(C0010R$id.backup_pw_set_button);
        this.mCancel.setOnClickListener(this.mButtonListener);
        this.mSet.setOnClickListener(this.mButtonListener);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private boolean setBackupPassword(String str, String str2) {
        if (TextUtils.isEmpty(str2)) {
            return false;
        }
        try {
            return this.mBackupManager.setBackupPassword(str, str2);
        } catch (RemoteException unused) {
            Log.e("SetFullBackupPassword", "Unable to communicate with backup manager");
            return false;
        }
    }
}
