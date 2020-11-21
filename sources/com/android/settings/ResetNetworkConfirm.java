package com.android.settings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkPolicyManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RecoverySystem;
import android.os.UserHandle;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.android.ims.ImsManager;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.enterprise.ActionDisabledByAdminDialogHelper;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.bluetooth.LocalBluetoothManager;

public class ResetNetworkConfirm extends InstrumentedFragment {
    Activity mActivity;
    private AlertDialog mAlertDialog;
    View mContentView;
    boolean mEraseEsim;
    View.OnClickListener mFinalClickListener = new View.OnClickListener() {
        /* class com.android.settings.ResetNetworkConfirm.AnonymousClass1 */

        public void onClick(View view) {
            if (!Utils.isMonkeyRunning()) {
                ResetNetworkConfirm resetNetworkConfirm = ResetNetworkConfirm.this;
                resetNetworkConfirm.mProgressDialog = resetNetworkConfirm.getProgressDialog(resetNetworkConfirm.mActivity);
                ResetNetworkConfirm.this.mProgressDialog.show();
                ResetNetworkConfirm resetNetworkConfirm2 = ResetNetworkConfirm.this;
                ResetNetworkConfirm resetNetworkConfirm3 = ResetNetworkConfirm.this;
                resetNetworkConfirm2.mResetNetworkTask = new ResetNetworkTask(resetNetworkConfirm3.mActivity);
                ResetNetworkConfirm.this.mResetNetworkTask.execute(new Void[0]);
            }
        }
    };
    private ProgressDialog mProgressDialog;
    ResetNetworkTask mResetNetworkTask;
    private int mSubId = -1;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 84;
    }

    private class ResetNetworkTask extends AsyncTask<Void, Void, Boolean> {
        private final Context mContext;
        private final String mPackageName;

        ResetNetworkTask(Context context) {
            this.mContext = context;
            this.mPackageName = context.getPackageName();
        }

        /* access modifiers changed from: protected */
        public Boolean doInBackground(Void... voidArr) {
            BluetoothAdapter adapter;
            ConnectivityManager connectivityManager = (ConnectivityManager) this.mContext.getSystemService("connectivity");
            if (connectivityManager != null) {
                connectivityManager.factoryReset();
            }
            WifiManager wifiManager = (WifiManager) this.mContext.getSystemService("wifi");
            if (wifiManager != null) {
                wifiManager.factoryReset();
            }
            ResetNetworkConfirm.this.p2pFactoryReset(this.mContext);
            TelephonyManager createForSubscriptionId = ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).createForSubscriptionId(ResetNetworkConfirm.this.mSubId);
            NetworkPolicyManager networkPolicyManager = (NetworkPolicyManager) this.mContext.getSystemService("netpolicy");
            if (networkPolicyManager != null) {
                networkPolicyManager.factoryReset(createForSubscriptionId.getSubscriberId());
            }
            BluetoothManager bluetoothManager = (BluetoothManager) this.mContext.getSystemService("bluetooth");
            if (!(bluetoothManager == null || (adapter = bluetoothManager.getAdapter()) == null)) {
                adapter.factoryReset();
                LocalBluetoothManager instance = LocalBluetoothManager.getInstance(this.mContext, null);
                if (instance != null) {
                    instance.getCachedDeviceManager().clearAllDevices();
                }
            }
            ImsManager.getInstance(this.mContext, SubscriptionManager.getPhoneId(ResetNetworkConfirm.this.mSubId)).factoryReset();
            if (createForSubscriptionId != null) {
                createForSubscriptionId.factoryReset(ResetNetworkConfirm.this.mSubId);
            }
            ResetNetworkConfirm.this.restoreDefaultApn(this.mContext);
            if (ResetNetworkConfirm.this.mEraseEsim) {
                return Boolean.valueOf(RecoverySystem.wipeEuiccData(this.mContext, this.mPackageName));
            }
            return Boolean.TRUE;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Boolean bool) {
            ResetNetworkConfirm.this.mProgressDialog.dismiss();
            if (bool.booleanValue()) {
                Toast.makeText(this.mContext, C0017R$string.reset_network_complete_toast, 0).show();
                return;
            }
            ResetNetworkConfirm resetNetworkConfirm = ResetNetworkConfirm.this;
            AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
            builder.setTitle(C0017R$string.reset_esim_error_title);
            builder.setMessage(C0017R$string.reset_esim_error_msg);
            builder.setPositiveButton(17039370, (DialogInterface.OnClickListener) null);
            resetNetworkConfirm.mAlertDialog = builder.show();
        }
    }

    /* access modifiers changed from: package-private */
    public void p2pFactoryReset(Context context) {
        WifiP2pManager.Channel initialize;
        WifiP2pManager wifiP2pManager = (WifiP2pManager) context.getSystemService("wifip2p");
        if (wifiP2pManager != null && (initialize = wifiP2pManager.initialize(context.getApplicationContext(), context.getMainLooper(), null)) != null) {
            wifiP2pManager.factoryReset(initialize, null);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private ProgressDialog getProgressDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getString(C0017R$string.master_clear_progress_text));
        return progressDialog;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void restoreDefaultApn(Context context) {
        Uri parse = Uri.parse("content://telephony/carriers/restore");
        if (SubscriptionManager.isUsableSubscriptionId(this.mSubId)) {
            parse = Uri.withAppendedPath(parse, "subId/" + String.valueOf(this.mSubId));
        }
        context.getContentResolver().delete(parse, null, null);
    }

    private void establishFinalConfirmationState() {
        this.mContentView.findViewById(C0010R$id.execute_reset_network).setOnClickListener(this.mFinalClickListener);
    }

    /* access modifiers changed from: package-private */
    public void setSubtitle() {
        if (this.mEraseEsim) {
            ((TextView) this.mContentView.findViewById(C0010R$id.reset_network_confirm)).setText(C0017R$string.reset_network_final_desc_esim);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        RestrictedLockUtils.EnforcedAdmin checkIfRestrictionEnforced = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mActivity, "no_network_reset", UserHandle.myUserId());
        if (RestrictedLockUtilsInternal.hasBaseUserRestriction(this.mActivity, "no_network_reset", UserHandle.myUserId())) {
            return layoutInflater.inflate(C0012R$layout.network_reset_disallowed_screen, (ViewGroup) null);
        }
        if (checkIfRestrictionEnforced != null) {
            AlertDialog.Builder prepareDialogBuilder = new ActionDisabledByAdminDialogHelper(this.mActivity).prepareDialogBuilder("no_network_reset", checkIfRestrictionEnforced);
            prepareDialogBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                /* class com.android.settings.$$Lambda$ResetNetworkConfirm$YTG2gTxf5vyFkKGLAaR8nzFOxo */

                public final void onDismiss(DialogInterface dialogInterface) {
                    ResetNetworkConfirm.this.lambda$onCreateView$0$ResetNetworkConfirm(dialogInterface);
                }
            });
            prepareDialogBuilder.show();
            return new View(this.mActivity);
        }
        this.mContentView = layoutInflater.inflate(C0012R$layout.reset_network_confirm, (ViewGroup) null);
        establishFinalConfirmationState();
        setSubtitle();
        return this.mContentView;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateView$0 */
    public /* synthetic */ void lambda$onCreateView$0$ResetNetworkConfirm(DialogInterface dialogInterface) {
        this.mActivity.finish();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle arguments = getArguments();
        if (arguments != null) {
            this.mSubId = arguments.getInt("android.telephony.extra.SUBSCRIPTION_INDEX", -1);
            this.mEraseEsim = arguments.getBoolean("erase_esim");
        }
        this.mActivity = getActivity();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        ResetNetworkTask resetNetworkTask = this.mResetNetworkTask;
        if (resetNetworkTask != null) {
            resetNetworkTask.cancel(true);
            this.mResetNetworkTask = null;
        }
        ProgressDialog progressDialog = this.mProgressDialog;
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        AlertDialog alertDialog = this.mAlertDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        super.onDestroy();
    }
}
