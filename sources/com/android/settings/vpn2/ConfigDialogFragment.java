package com.android.settings.vpn2;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.IConnectivityManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.security.KeyStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.android.internal.net.LegacyVpnInfo;
import com.android.internal.net.VpnProfile;
import com.android.settings.C0017R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.vpn2.ConfirmLockdownFragment;

public class ConfigDialogFragment extends InstrumentedDialogFragment implements DialogInterface.OnClickListener, DialogInterface.OnShowListener, View.OnClickListener, ConfirmLockdownFragment.ConfirmLockdownListener {
    private Context mContext;
    private final IConnectivityManager mService = IConnectivityManager.Stub.asInterface(ServiceManager.getService("connectivity"));

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 545;
    }

    public static void show(VpnSettings vpnSettings, VpnProfile vpnProfile, boolean z, boolean z2) {
        if (vpnSettings.isAdded()) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("profile", vpnProfile);
            bundle.putBoolean("editing", z);
            bundle.putBoolean("exists", z2);
            ConfigDialogFragment configDialogFragment = new ConfigDialogFragment();
            configDialogFragment.setArguments(bundle);
            configDialogFragment.setTargetFragment(vpnSettings, 0);
            configDialogFragment.show(vpnSettings.getFragmentManager(), "vpnconfigdialog");
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settings.core.instrumentation.InstrumentedDialogFragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Bundle arguments = getArguments();
        boolean z = arguments.getBoolean("editing");
        boolean z2 = arguments.getBoolean("exists");
        ConfigDialog configDialog = new ConfigDialog(getActivity(), this, arguments.getParcelable("profile"), z, z2);
        configDialog.setOnShowListener(this);
        return configDialog;
    }

    public void onShow(DialogInterface dialogInterface) {
        ((AlertDialog) getDialog()).getButton(-1).setOnClickListener(this);
    }

    public void onClick(View view) {
        onClick(getDialog(), -1);
    }

    @Override // com.android.settings.vpn2.ConfirmLockdownFragment.ConfirmLockdownListener
    public void onConfirmLockdown(Bundle bundle, boolean z, boolean z2) {
        connect((VpnProfile) bundle.getParcelable("profile"), z);
        dismiss();
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        ConfigDialog configDialog = (ConfigDialog) getDialog();
        if (configDialog == null) {
            Log.e("ConfigDialogFragment", "ConfigDialog object is null");
            return;
        }
        Parcelable profile = configDialog.getProfile();
        if (i == -1) {
            boolean isVpnAlwaysOn = configDialog.isVpnAlwaysOn();
            boolean z = isVpnAlwaysOn || !configDialog.isEditing();
            boolean isAnyLockdownActive = VpnUtils.isAnyLockdownActive(this.mContext);
            try {
                boolean isVpnActive = VpnUtils.isVpnActive(this.mContext);
                if (z && !isConnected(profile) && ConfirmLockdownFragment.shouldShow(isVpnActive, isAnyLockdownActive, isVpnAlwaysOn)) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("profile", profile);
                    ConfirmLockdownFragment.show(this, isVpnActive, isVpnAlwaysOn, isAnyLockdownActive, isVpnAlwaysOn, bundle);
                } else if (z) {
                    connect(profile, isVpnAlwaysOn);
                } else {
                    save(profile, false);
                }
            } catch (RemoteException e) {
                Log.w("ConfigDialogFragment", "Failed to check active VPN state. Skipping.", e);
            }
        } else if (i == -3) {
            if (!disconnect(profile)) {
                Log.e("ConfigDialogFragment", "Failed to disconnect VPN. Leaving profile in keystore.");
                return;
            }
            KeyStore instance = KeyStore.getInstance();
            instance.delete("VPN_" + ((VpnProfile) profile).key, -1);
            updateLockdownVpn(false, profile);
        }
        dismiss();
    }

    @Override // androidx.fragment.app.DialogFragment
    public void onCancel(DialogInterface dialogInterface) {
        dismiss();
        super.onCancel(dialogInterface);
    }

    private void updateLockdownVpn(boolean z, VpnProfile vpnProfile) {
        if (z) {
            if (!vpnProfile.isValidLockdownProfile()) {
                Toast.makeText(this.mContext, C0017R$string.vpn_lockdown_config_error, 1).show();
                return;
            }
            ConnectivityManager.from(this.mContext).setAlwaysOnVpnPackageForUser(UserHandle.myUserId(), null, false, null);
            VpnUtils.setLockdownVpn(this.mContext, vpnProfile.key);
        } else if (VpnUtils.isVpnLockdown(vpnProfile.key)) {
            VpnUtils.clearLockdownVpn(this.mContext);
        }
    }

    private void save(VpnProfile vpnProfile, boolean z) {
        KeyStore instance = KeyStore.getInstance();
        instance.put("VPN_" + vpnProfile.key, vpnProfile.encode(), -1, 0);
        disconnect(vpnProfile);
        updateLockdownVpn(z, vpnProfile);
    }

    private void connect(VpnProfile vpnProfile, boolean z) {
        save(vpnProfile, z);
        if (!VpnUtils.isVpnLockdown(vpnProfile.key)) {
            VpnUtils.clearLockdownVpn(this.mContext);
            try {
                this.mService.startLegacyVpn(vpnProfile);
            } catch (IllegalStateException unused) {
                Toast.makeText(this.mContext, C0017R$string.vpn_no_network, 1).show();
            } catch (RemoteException e) {
                Log.e("ConfigDialogFragment", "Failed to connect", e);
            }
        }
    }

    private boolean disconnect(VpnProfile vpnProfile) {
        try {
            if (!isConnected(vpnProfile)) {
                return true;
            }
            return VpnUtils.disconnectLegacyVpn(getContext());
        } catch (RemoteException e) {
            Log.e("ConfigDialogFragment", "Failed to disconnect", e);
            return false;
        }
    }

    private boolean isConnected(VpnProfile vpnProfile) throws RemoteException {
        LegacyVpnInfo legacyVpnInfo = this.mService.getLegacyVpnInfo(UserHandle.myUserId());
        return legacyVpnInfo != null && vpnProfile.key.equals(legacyVpnInfo.key);
    }
}
