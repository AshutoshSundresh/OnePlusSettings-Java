package com.android.settings.security;

import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Process;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.security.IKeyChainService;
import android.security.KeyChain;
import android.security.KeyStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.C0017R$string;
import com.android.settings.password.ChooseLockSettingsHelper;
import com.android.settings.vpn2.VpnUtils;
import com.oneplus.settings.BaseAppCompatActivity;

public final class CredentialStorage extends BaseAppCompatActivity {
    private Bundle mInstallBundle;
    private LockPatternUtils mUtils;

    public CredentialStorage() {
        KeyStore.getInstance();
    }

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mUtils = new LockPatternUtils(this);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity
    public void onResume() {
        super.onResume();
        Intent intent = getIntent();
        String action = intent.getAction();
        if (((UserManager) getSystemService("user")).hasUserRestriction("no_config_credentials")) {
            finish();
        } else if ("com.android.credentials.RESET".equals(action)) {
            new ResetDialog();
        } else {
            if ("com.android.credentials.INSTALL".equals(action) && checkCallerIsCertInstallerOrSelfInProfile()) {
                this.mInstallBundle = intent.getExtras();
            }
            handleInstall();
        }
    }

    private void handleInstall() {
        if (!isFinishing() && installIfAvailable()) {
            finish();
        }
    }

    private boolean installIfAvailable() {
        Bundle bundle = this.mInstallBundle;
        if (bundle == null || bundle.isEmpty()) {
            return true;
        }
        Bundle bundle2 = this.mInstallBundle;
        this.mInstallBundle = null;
        int i = bundle2.getInt("install_as_uid", -1);
        if (i == -1 || UserHandle.isSameUser(i, Process.myUid())) {
            String string = bundle2.getString("user_key_pair_name", null);
            if (TextUtils.isEmpty(string)) {
                Log.e("CredentialStorage", "Cannot install key without an alias");
                return true;
            }
            new InstallKeyInKeyChain(string, bundle2.getByteArray("user_private_key_data"), bundle2.getByteArray("user_certificate_data"), bundle2.getByteArray("ca_certificates_data"), i).execute(new Void[0]);
            return false;
        }
        int userId = UserHandle.getUserId(i);
        if (i != 1010) {
            Log.e("CredentialStorage", "Failed to install credentials as uid " + i + ": cross-user installs may only target wifi uids");
            return true;
        }
        startActivityAsUser(new Intent("com.android.credentials.INSTALL").setFlags(33554432).putExtras(bundle2), new UserHandle(userId));
        return true;
    }

    private class ResetDialog implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener {
        private boolean mResetConfirmed;

        private ResetDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(CredentialStorage.this);
            builder.setTitle(17039380);
            builder.setMessage(C0017R$string.credentials_reset_hint);
            builder.setPositiveButton(17039370, this);
            builder.setNegativeButton(17039360, this);
            AlertDialog create = builder.create();
            create.setOnDismissListener(this);
            create.show();
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            this.mResetConfirmed = i == -1;
        }

        public void onDismiss(DialogInterface dialogInterface) {
            if (!this.mResetConfirmed) {
                CredentialStorage.this.finish();
                return;
            }
            this.mResetConfirmed = false;
            if (!CredentialStorage.this.mUtils.isSecure(UserHandle.myUserId())) {
                new ResetKeyStoreAndKeyChain().execute(new Void[0]);
            } else if (!CredentialStorage.this.confirmKeyGuard(1)) {
                Log.w("CredentialStorage", "Failed to launch credential confirmation for a secure user.");
                CredentialStorage.this.finish();
            }
        }
    }

    private class ResetKeyStoreAndKeyChain extends AsyncTask<Void, Void, Boolean> {
        private ResetKeyStoreAndKeyChain() {
        }

        /* access modifiers changed from: protected */
        public Boolean doInBackground(Void... voidArr) {
            Boolean bool = Boolean.FALSE;
            CredentialStorage.this.mUtils.resetKeyStore(UserHandle.myUserId());
            try {
                KeyChain.KeyChainConnection bind = KeyChain.bind(CredentialStorage.this);
                try {
                    return Boolean.valueOf(bind.getService().reset());
                } catch (RemoteException unused) {
                    return bool;
                } finally {
                    bind.close();
                }
            } catch (InterruptedException unused2) {
                Thread.currentThread().interrupt();
                return bool;
            }
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Boolean bool) {
            if (bool.booleanValue()) {
                Toast.makeText(CredentialStorage.this, C0017R$string.credentials_erased, 0).show();
                CredentialStorage.this.clearLegacyVpnIfEstablished();
            } else {
                Toast.makeText(CredentialStorage.this, C0017R$string.credentials_not_erased, 0).show();
            }
            CredentialStorage.this.finish();
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void clearLegacyVpnIfEstablished() {
        if (VpnUtils.disconnectLegacyVpn(getApplicationContext())) {
            Toast.makeText(this, C0017R$string.vpn_disconnected, 0).show();
        }
    }

    /* access modifiers changed from: private */
    public class InstallKeyInKeyChain extends AsyncTask<Void, Void, Boolean> {
        final String mAlias;
        private final byte[] mCaListData;
        private final byte[] mCertData;
        private final byte[] mKeyData;
        private final int mUid;

        InstallKeyInKeyChain(String str, byte[] bArr, byte[] bArr2, byte[] bArr3, int i) {
            this.mAlias = str;
            this.mKeyData = bArr;
            this.mCertData = bArr2;
            this.mCaListData = bArr3;
            this.mUid = i;
        }

        /* access modifiers changed from: protected */
        public Boolean doInBackground(Void... voidArr) {
            Boolean bool = Boolean.FALSE;
            try {
                KeyChain.KeyChainConnection bind = KeyChain.bind(CredentialStorage.this);
                try {
                    IKeyChainService service = bind.getService();
                    if (!service.installKeyPair(this.mKeyData, this.mCertData, this.mCaListData, this.mAlias, this.mUid)) {
                        Log.w("CredentialStorage", String.format("Failed installing key %s", this.mAlias));
                        if (bind != null) {
                            bind.close();
                        }
                        return bool;
                    }
                    if (this.mUid == 1000 || this.mUid == -1) {
                        service.setUserSelectable(this.mAlias, true);
                    }
                    Boolean bool2 = Boolean.TRUE;
                    if (bind != null) {
                        bind.close();
                    }
                    return bool2;
                } catch (Throwable th) {
                    th.addSuppressed(th);
                }
                throw th;
            } catch (RemoteException e) {
                Log.w("CredentialStorage", String.format("Failed to install key %s to uid %d", this.mAlias, Integer.valueOf(this.mUid)), e);
                return bool;
            } catch (InterruptedException e2) {
                Log.w("CredentialStorage", String.format("Interrupted while installing key %s", this.mAlias), e2);
                Thread.currentThread().interrupt();
                return bool;
            }
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Boolean bool) {
            CredentialStorage.this.onKeyInstalled(this.mAlias, this.mUid, bool.booleanValue());
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void onKeyInstalled(String str, int i, boolean z) {
        if (!z) {
            Log.w("CredentialStorage", String.format("Error installing alias %s for uid %d", str, Integer.valueOf(i)));
            finish();
            return;
        }
        Log.i("CredentialStorage", String.format("Successfully installed alias %s to uid %d.", str, Integer.valueOf(i)));
        sendBroadcast(new Intent("android.security.action.KEYCHAIN_CHANGED"));
        setResult(-1);
        finish();
    }

    private boolean checkCallerIsCertInstallerOrSelfInProfile() {
        if (TextUtils.equals("com.android.certinstaller", getCallingPackage())) {
            return getPackageManager().checkSignatures(getCallingPackage(), getPackageName()) == 0;
        }
        try {
            int launchedFromUid = ActivityManager.getService().getLaunchedFromUid(getActivityToken());
            if (launchedFromUid == -1) {
                Log.e("CredentialStorage", "com.android.credentials.INSTALL must be started with startActivityForResult");
                return false;
            } else if (!UserHandle.isSameApp(launchedFromUid, Process.myUid())) {
                return false;
            } else {
                UserInfo profileParent = ((UserManager) getSystemService("user")).getProfileParent(UserHandle.getUserId(launchedFromUid));
                return profileParent != null && profileParent.id == UserHandle.myUserId();
            }
        } catch (RemoteException unused) {
            return false;
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private boolean confirmKeyGuard(int i) {
        return new ChooseLockSettingsHelper(this).launchConfirmationActivity(i, getResources().getText(C0017R$string.credentials_title), true);
    }

    @Override // androidx.activity.ComponentActivity, androidx.fragment.app.FragmentActivity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i != 1) {
            return;
        }
        if (i2 == -1) {
            new ResetKeyStoreAndKeyChain().execute(new Void[0]);
        } else {
            finish();
        }
    }
}
