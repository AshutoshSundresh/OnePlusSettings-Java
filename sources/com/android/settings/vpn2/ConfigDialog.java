package com.android.settings.vpn2;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Proxy;
import android.net.ProxyInfo;
import android.os.Bundle;
import android.os.SystemProperties;
import android.security.KeyStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.android.internal.net.VpnProfile;
import com.android.settings.C0003R$array;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;

class ConfigDialog extends AlertDialog implements TextWatcher, View.OnClickListener, AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {
    private TextView mAlwaysOnInvalidReason;
    private CheckBox mAlwaysOnVpn;
    private TextView mDnsServers;
    private boolean mEditing;
    private boolean mExists;
    private Spinner mIpsecCaCert;
    private TextView mIpsecIdentifier;
    private TextView mIpsecSecret;
    private Spinner mIpsecServerCert;
    private Spinner mIpsecUserCert;
    private final KeyStore mKeyStore = KeyStore.getInstance();
    private TextView mL2tpSecret;
    private final DialogInterface.OnClickListener mListener;
    private CheckBox mMppe;
    private TextView mName;
    private TextView mPassword;
    private final VpnProfile mProfile;
    private TextView mProxyHost;
    private TextView mProxyPort;
    private Spinner mProxySettings;
    private TextView mRoutes;
    private CheckBox mSaveLogin;
    private TextView mSearchDomains;
    private TextView mServer;
    private CheckBox mShowOptions;
    private Spinner mType;
    private TextView mUsername;
    private View mView;

    private boolean requiresUsernamePassword(int i) {
        return (i == 7 || i == 8) ? false : true;
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    ConfigDialog(Context context, DialogInterface.OnClickListener onClickListener, VpnProfile vpnProfile, boolean z, boolean z2) {
        super(context);
        this.mListener = onClickListener;
        this.mProfile = vpnProfile;
        this.mEditing = z;
        this.mExists = z2;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AlertDialog, androidx.appcompat.app.AppCompatDialog
    public void onCreate(Bundle bundle) {
        View inflate = getLayoutInflater().inflate(C0012R$layout.vpn_dialog, (ViewGroup) null);
        this.mView = inflate;
        setView(inflate);
        Context context = getContext();
        this.mName = (TextView) this.mView.findViewById(C0010R$id.name);
        this.mType = (Spinner) this.mView.findViewById(C0010R$id.type);
        this.mServer = (TextView) this.mView.findViewById(C0010R$id.server);
        this.mUsername = (TextView) this.mView.findViewById(C0010R$id.username);
        this.mPassword = (TextView) this.mView.findViewById(C0010R$id.password);
        this.mSearchDomains = (TextView) this.mView.findViewById(C0010R$id.search_domains);
        this.mDnsServers = (TextView) this.mView.findViewById(C0010R$id.dns_servers);
        this.mRoutes = (TextView) this.mView.findViewById(C0010R$id.routes);
        this.mProxySettings = (Spinner) this.mView.findViewById(C0010R$id.vpn_proxy_settings);
        this.mProxyHost = (TextView) this.mView.findViewById(C0010R$id.vpn_proxy_host);
        this.mProxyPort = (TextView) this.mView.findViewById(C0010R$id.vpn_proxy_port);
        this.mMppe = (CheckBox) this.mView.findViewById(C0010R$id.mppe);
        this.mL2tpSecret = (TextView) this.mView.findViewById(C0010R$id.l2tp_secret);
        this.mIpsecIdentifier = (TextView) this.mView.findViewById(C0010R$id.ipsec_identifier);
        this.mIpsecSecret = (TextView) this.mView.findViewById(C0010R$id.ipsec_secret);
        this.mIpsecUserCert = (Spinner) this.mView.findViewById(C0010R$id.ipsec_user_cert);
        this.mIpsecCaCert = (Spinner) this.mView.findViewById(C0010R$id.ipsec_ca_cert);
        this.mIpsecServerCert = (Spinner) this.mView.findViewById(C0010R$id.ipsec_server_cert);
        this.mSaveLogin = (CheckBox) this.mView.findViewById(C0010R$id.save_login);
        this.mShowOptions = (CheckBox) this.mView.findViewById(C0010R$id.show_options);
        this.mAlwaysOnVpn = (CheckBox) this.mView.findViewById(C0010R$id.always_on_vpn);
        this.mAlwaysOnInvalidReason = (TextView) this.mView.findViewById(C0010R$id.always_on_invalid_reason);
        this.mName.setText(this.mProfile.name);
        setTypesByFeature(this.mType);
        this.mType.setSelection(this.mProfile.type);
        this.mServer.setText(this.mProfile.server);
        VpnProfile vpnProfile = this.mProfile;
        if (vpnProfile.saveLogin) {
            this.mUsername.setText(vpnProfile.username);
            this.mPassword.setText(this.mProfile.password);
        }
        this.mSearchDomains.setText(this.mProfile.searchDomains);
        this.mDnsServers.setText(this.mProfile.dnsServers);
        this.mRoutes.setText(this.mProfile.routes);
        ProxyInfo proxyInfo = this.mProfile.proxy;
        if (proxyInfo != null) {
            this.mProxyHost.setText(proxyInfo.getHost());
            int port = this.mProfile.proxy.getPort();
            this.mProxyPort.setText(port == 0 ? "" : Integer.toString(port));
        }
        this.mMppe.setChecked(this.mProfile.mppe);
        this.mL2tpSecret.setText(this.mProfile.l2tpSecret);
        this.mL2tpSecret.setTextAppearance(16974257);
        this.mIpsecIdentifier.setText(this.mProfile.ipsecIdentifier);
        this.mIpsecSecret.setText(this.mProfile.ipsecSecret);
        loadCertificates(this.mIpsecUserCert, "USRPKEY_", 0, this.mProfile.ipsecUserCert);
        loadCertificates(this.mIpsecCaCert, "CACERT_", C0017R$string.vpn_no_ca_cert, this.mProfile.ipsecCaCert);
        loadCertificates(this.mIpsecServerCert, "USRCERT_", C0017R$string.vpn_no_server_cert, this.mProfile.ipsecServerCert);
        this.mSaveLogin.setChecked(this.mProfile.saveLogin);
        this.mAlwaysOnVpn.setChecked(this.mProfile.key.equals(VpnUtils.getLockdownVpn()));
        this.mPassword.setTextAppearance(16974257);
        if (SystemProperties.getBoolean("persist.radio.imsregrequired", false)) {
            this.mAlwaysOnVpn.setVisibility(8);
        }
        this.mName.addTextChangedListener(this);
        this.mType.setOnItemSelectedListener(this);
        this.mServer.addTextChangedListener(this);
        this.mUsername.addTextChangedListener(this);
        this.mPassword.addTextChangedListener(this);
        this.mDnsServers.addTextChangedListener(this);
        this.mRoutes.addTextChangedListener(this);
        this.mProxySettings.setOnItemSelectedListener(this);
        this.mProxyHost.addTextChangedListener(this);
        this.mProxyPort.addTextChangedListener(this);
        this.mIpsecIdentifier.addTextChangedListener(this);
        this.mIpsecSecret.addTextChangedListener(this);
        this.mIpsecUserCert.setOnItemSelectedListener(this);
        this.mShowOptions.setOnClickListener(this);
        this.mAlwaysOnVpn.setOnCheckedChangeListener(this);
        boolean z = this.mEditing || !validate(true);
        this.mEditing = z;
        if (z) {
            setTitle(C0017R$string.vpn_edit);
            this.mView.findViewById(C0010R$id.editor).setVisibility(0);
            changeType(this.mProfile.type);
            this.mSaveLogin.setVisibility(8);
            configureAdvancedOptionsVisibility();
            if (this.mExists) {
                setButton(-3, context.getString(C0017R$string.vpn_forget), this.mListener);
            }
            setButton(-1, context.getString(C0017R$string.vpn_save), this.mListener);
        } else {
            setTitle(context.getString(C0017R$string.vpn_connect_to, this.mProfile.name));
            setUsernamePasswordVisibility(this.mProfile.type);
            setButton(-1, context.getString(C0017R$string.vpn_connect), this.mListener);
        }
        setButton(-2, context.getString(C0017R$string.vpn_cancel), this.mListener);
        super.onCreate(bundle);
        updateUiControls();
        getWindow().setSoftInputMode(20);
    }

    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        configureAdvancedOptionsVisibility();
    }

    public void afterTextChanged(Editable editable) {
        updateUiControls();
    }

    public void onClick(View view) {
        if (view == this.mShowOptions) {
            configureAdvancedOptionsVisibility();
        }
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
        if (adapterView == this.mType) {
            changeType(i);
        } else if (adapterView == this.mProxySettings) {
            updateProxyFieldsVisibility(i);
        }
        updateUiControls();
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        if (compoundButton == this.mAlwaysOnVpn) {
            updateUiControls();
        }
    }

    public boolean isVpnAlwaysOn() {
        return this.mAlwaysOnVpn.isChecked();
    }

    private void updateUiControls() {
        VpnProfile profile = getProfile();
        if (profile.isValidLockdownProfile()) {
            this.mAlwaysOnVpn.setEnabled(true);
            this.mAlwaysOnInvalidReason.setVisibility(8);
        } else {
            this.mAlwaysOnVpn.setChecked(false);
            this.mAlwaysOnVpn.setEnabled(false);
            if (!profile.isTypeValidForLockdown()) {
                this.mAlwaysOnInvalidReason.setText(C0017R$string.vpn_always_on_invalid_reason_type);
            } else if (VpnProfile.isLegacyType(profile.type) && !profile.isServerAddressNumeric()) {
                this.mAlwaysOnInvalidReason.setText(C0017R$string.vpn_always_on_invalid_reason_server);
            } else if (VpnProfile.isLegacyType(profile.type) && !profile.hasDns()) {
                this.mAlwaysOnInvalidReason.setText(C0017R$string.vpn_always_on_invalid_reason_no_dns);
            } else if (!VpnProfile.isLegacyType(profile.type) || profile.areDnsAddressesNumeric()) {
                this.mAlwaysOnInvalidReason.setText(C0017R$string.vpn_always_on_invalid_reason_other);
            } else {
                this.mAlwaysOnInvalidReason.setText(C0017R$string.vpn_always_on_invalid_reason_dns);
            }
            this.mAlwaysOnInvalidReason.setVisibility(0);
        }
        ProxyInfo proxyInfo = this.mProfile.proxy;
        if (proxyInfo != null && (!proxyInfo.getHost().isEmpty() || this.mProfile.proxy.getPort() != 0)) {
            this.mProxySettings.setSelection(1);
            updateProxyFieldsVisibility(1);
        }
        if (this.mAlwaysOnVpn.isChecked()) {
            this.mSaveLogin.setChecked(true);
            this.mSaveLogin.setEnabled(false);
        } else {
            this.mSaveLogin.setChecked(this.mProfile.saveLogin);
            this.mSaveLogin.setEnabled(true);
        }
        getButton(-1).setEnabled(validate(this.mEditing));
    }

    private void updateProxyFieldsVisibility(int i) {
        this.mView.findViewById(C0010R$id.vpn_proxy_fields).setVisibility(i == 1 ? 0 : 8);
    }

    private boolean hasAdvancedOptionsEnabled() {
        return this.mSearchDomains.getText().length() > 0 || this.mDnsServers.getText().length() > 0 || this.mRoutes.getText().length() > 0 || this.mProxyHost.getText().length() > 0 || this.mProxyPort.getText().length() > 0;
    }

    private void configureAdvancedOptionsVisibility() {
        int i = 8;
        if (this.mShowOptions.isChecked() || hasAdvancedOptionsEnabled()) {
            this.mView.findViewById(C0010R$id.options).setVisibility(0);
            this.mShowOptions.setVisibility(8);
            if (VpnProfile.isLegacyType(this.mType.getSelectedItemPosition())) {
                i = 0;
            }
            this.mView.findViewById(C0010R$id.network_options).setVisibility(i);
            return;
        }
        this.mView.findViewById(C0010R$id.options).setVisibility(8);
        this.mShowOptions.setVisibility(0);
    }

    /* JADX INFO: Can't fix incorrect switch cases order, some code will duplicate */
    private void changeType(int i) {
        this.mMppe.setVisibility(8);
        this.mView.findViewById(C0010R$id.l2tp).setVisibility(8);
        this.mView.findViewById(C0010R$id.ipsec_psk).setVisibility(8);
        this.mView.findViewById(C0010R$id.ipsec_user).setVisibility(8);
        this.mView.findViewById(C0010R$id.ipsec_peer).setVisibility(8);
        this.mView.findViewById(C0010R$id.options_ipsec_identity).setVisibility(8);
        setUsernamePasswordVisibility(i);
        if (!VpnProfile.isLegacyType(i)) {
            this.mView.findViewById(C0010R$id.options_ipsec_identity).setVisibility(0);
        }
        switch (i) {
            case 0:
                this.mMppe.setVisibility(0);
                break;
            case 1:
                this.mView.findViewById(C0010R$id.l2tp).setVisibility(0);
                this.mView.findViewById(C0010R$id.ipsec_psk).setVisibility(0);
                this.mView.findViewById(C0010R$id.options_ipsec_identity).setVisibility(0);
                break;
            case 2:
                this.mView.findViewById(C0010R$id.l2tp).setVisibility(0);
                this.mView.findViewById(C0010R$id.ipsec_user).setVisibility(0);
                this.mView.findViewById(C0010R$id.ipsec_peer).setVisibility(0);
                break;
            case 3:
            case 7:
                this.mView.findViewById(C0010R$id.ipsec_psk).setVisibility(0);
                this.mView.findViewById(C0010R$id.options_ipsec_identity).setVisibility(0);
                break;
            case 4:
            case 8:
                this.mView.findViewById(C0010R$id.ipsec_user).setVisibility(0);
                this.mView.findViewById(C0010R$id.ipsec_peer).setVisibility(0);
                break;
            case 5:
            case 6:
                this.mView.findViewById(C0010R$id.ipsec_peer).setVisibility(0);
                break;
        }
        configureAdvancedOptionsVisibility();
    }

    private boolean validate(boolean z) {
        if (this.mAlwaysOnVpn.isChecked() && !getProfile().isValidLockdownProfile()) {
            return false;
        }
        int selectedItemPosition = this.mType.getSelectedItemPosition();
        if (z || !requiresUsernamePassword(selectedItemPosition)) {
            if (this.mName.getText().length() == 0 || this.mServer.getText().length() == 0) {
                return false;
            }
            if (VpnProfile.isLegacyType(this.mProfile.type) && (!validateAddresses(this.mDnsServers.getText().toString(), false) || !validateAddresses(this.mRoutes.getText().toString(), true))) {
                return false;
            }
            if ((!VpnProfile.isLegacyType(this.mProfile.type) && this.mIpsecIdentifier.getText().length() == 0) || !validateProxy()) {
                return false;
            }
            switch (selectedItemPosition) {
                case 0:
                case 5:
                case 6:
                    return true;
                case 1:
                case 3:
                case 7:
                    if (this.mIpsecSecret.getText().length() != 0) {
                        return true;
                    }
                    return false;
                case 2:
                case 4:
                case 8:
                    if (this.mIpsecUserCert.getSelectedItemPosition() != 0) {
                        return true;
                    }
                    return false;
                default:
                    return false;
            }
        } else if (this.mUsername.getText().length() == 0 || this.mPassword.getText().length() == 0) {
            return false;
        } else {
            return true;
        }
    }

    private boolean validateAddresses(String str, boolean z) {
        int i;
        try {
            String[] split = str.split(" ");
            for (String str2 : split) {
                if (!str2.isEmpty()) {
                    if (z) {
                        String[] split2 = str2.split("/", 2);
                        String str3 = split2[0];
                        i = Integer.parseInt(split2[1]);
                        str2 = str3;
                    } else {
                        i = 32;
                    }
                    byte[] address = InetAddress.parseNumericAddress(str2).getAddress();
                    int i2 = ((address[1] & 255) << 16) | ((address[2] & 255) << 8) | (address[3] & 255) | ((address[0] & 255) << 24);
                    if (address.length != 4 || i < 0 || i > 32 || (i < 32 && (i2 << i) != 0)) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    private void setTypesByFeature(Spinner spinner) {
        String[] stringArray = getContext().getResources().getStringArray(C0003R$array.vpn_types);
        if (!getContext().getPackageManager().hasSystemFeature("android.software.ipsec_tunnels")) {
            ArrayList arrayList = new ArrayList(Arrays.asList(stringArray));
            arrayList.remove(8);
            arrayList.remove(7);
            arrayList.remove(6);
            stringArray = (String[]) arrayList.toArray(new String[0]);
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), 17367048, stringArray);
        arrayAdapter.setDropDownViewResource(17367049);
        spinner.setAdapter((SpinnerAdapter) arrayAdapter);
    }

    private void loadCertificates(Spinner spinner, String str, int i, String str2) {
        String str3;
        String[] strArr;
        Context context = getContext();
        if (i == 0) {
            str3 = "";
        } else {
            str3 = context.getString(i);
        }
        String[] list = this.mKeyStore.list(str);
        if (list == null || list.length == 0) {
            strArr = new String[]{str3};
        } else {
            strArr = new String[(list.length + 1)];
            strArr[0] = str3;
            System.arraycopy(list, 0, strArr, 1, list.length);
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(context, 17367048, strArr);
        arrayAdapter.setDropDownViewResource(17367049);
        spinner.setAdapter((SpinnerAdapter) arrayAdapter);
        for (int i2 = 1; i2 < strArr.length; i2++) {
            if (strArr[i2].equals(str2)) {
                spinner.setSelection(i2);
                return;
            }
        }
    }

    private void setUsernamePasswordVisibility(int i) {
        this.mView.findViewById(C0010R$id.userpass).setVisibility(requiresUsernamePassword(i) ? 0 : 8);
    }

    /* access modifiers changed from: package-private */
    public boolean isEditing() {
        return this.mEditing;
    }

    /* access modifiers changed from: package-private */
    public boolean hasProxy() {
        return this.mProxySettings.getSelectedItemPosition() == 1;
    }

    /* JADX INFO: Can't fix incorrect switch cases order, some code will duplicate */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x00de  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x00f0  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0102  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.android.internal.net.VpnProfile getProfile() {
        /*
        // Method dump skipped, instructions count: 376
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.vpn2.ConfigDialog.getProfile():com.android.internal.net.VpnProfile");
    }

    private boolean validateProxy() {
        if (hasProxy() && Proxy.validate(this.mProxyHost.getText().toString().trim(), this.mProxyPort.getText().toString().trim(), "") != 0) {
            return false;
        }
        return true;
    }
}
