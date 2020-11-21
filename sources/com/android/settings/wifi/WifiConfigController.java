package com.android.settings.wifi;

import android.content.Context;
import android.content.res.Resources;
import android.net.IpConfiguration;
import android.net.LinkAddress;
import android.net.NetworkInfo;
import android.net.NetworkUtils;
import android.net.ProxyInfo;
import android.net.StaticIpConfiguration;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.UserManager;
import android.security.KeyStore;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.FeatureFlagUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.android.settings.C0003R$array;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.wifi.details.WifiPrivacyPreferenceController;
import com.android.settings.wifi.details2.WifiPrivacyPreferenceController2;
import com.android.settings.wifi.dpp.WifiDppUtils;
import com.android.settingslib.Utils;
import com.android.settingslib.utils.ThreadUtils;
import com.android.settingslib.wifi.AccessPoint;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;

public class WifiConfigController implements TextWatcher, AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener, TextView.OnEditorActionListener, View.OnKeyListener {
    static final String[] UNDESIRED_CERTIFICATES = {"MacRandSecret", "MacRandSapSecret"};
    private static final int[] WAPI_PSK_TYPE = {0, 1};
    private final AccessPoint mAccessPoint;
    int mAccessPointSecurity;
    private final WifiConfigUiBase mConfigUi;
    private Context mContext;
    private TextView mDns1View;
    private TextView mDns2View;
    private String mDoNotProvideEapUserCertString;
    private String mDoNotValidateEapServerString;
    private TextView mEapAnonymousView;
    private Spinner mEapCaCertSpinner;
    private TextView mEapDomainView;
    private TextView mEapIdentityView;
    private Spinner mEapMethodSpinner;
    private Spinner mEapOcspSpinner;
    private Spinner mEapUserCertSpinner;
    private TextView mGatewayView;
    private boolean mHaveWapiCert;
    private Spinner mHiddenSettingsSpinner;
    private TextView mHiddenWarningView;
    private ProxyInfo mHttpProxy;
    private TextView mIpAddressView;
    private IpConfiguration.IpAssignment mIpAssignment;
    private Spinner mIpSettingsSpinner;
    private String[] mLevels;
    private Spinner mMeteredSettingsSpinner;
    private int mMode;
    private String mMultipleCertSetString;
    private TextView mNetworkPrefixLengthView;
    private TextView mPasswordView;
    private ArrayAdapter<CharSequence> mPhase2Adapter;
    private ArrayAdapter<CharSequence> mPhase2PeapAdapter;
    private Spinner mPhase2Spinner;
    private ArrayAdapter<CharSequence> mPhase2TtlsAdapter;
    private Spinner mPrivacySettingsSpinner;
    private TextView mProxyExclusionListView;
    private TextView mProxyHostView;
    private TextView mProxyPacView;
    private TextView mProxyPortView;
    private IpConfiguration.ProxySettings mProxySettings;
    private Spinner mProxySettingsSpinner;
    private boolean mRequestFocus;
    Integer[] mSecurityInPosition;
    private Spinner mSecuritySpinner;
    private CheckBox mShareThisWifiCheckBox;
    private CheckBox mSharedCheckBox;
    private Spinner mSimCardSpinner;
    private ArrayList<String> mSimDisplayNames;
    private ImageButton mSsidScanButton;
    private TextView mSsidView;
    private StaticIpConfiguration mStaticIpConfiguration;
    private SubscriptionManager mSubscriptionManager;
    private TelephonyManager mTelephonyManager;
    private String mUnspecifiedCertString;
    private String mUseSystemCertsString;
    private final View mView;
    private Spinner mWapiCertSpinner;
    private Spinner mWapiPskTypeSpinner;
    private final WifiManager mWifiManager;
    private int selectedSimCardNumber;

    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public WifiConfigController(WifiConfigUiBase wifiConfigUiBase, View view, AccessPoint accessPoint, int i) {
        this(wifiConfigUiBase, view, accessPoint, i, true);
    }

    public WifiConfigController(WifiConfigUiBase wifiConfigUiBase, View view, AccessPoint accessPoint, int i, boolean z) {
        this.mHaveWapiCert = false;
        this.mIpAssignment = IpConfiguration.IpAssignment.UNASSIGNED;
        this.mProxySettings = IpConfiguration.ProxySettings.UNASSIGNED;
        this.mHttpProxy = null;
        this.mStaticIpConfiguration = null;
        this.mRequestFocus = true;
        this.mSubscriptionManager = null;
        this.mConfigUi = wifiConfigUiBase;
        this.mView = view;
        this.mAccessPoint = accessPoint;
        Context context = wifiConfigUiBase.getContext();
        this.mContext = context;
        this.mRequestFocus = z;
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
        initWifiConfigController(accessPoint, i);
    }

    public WifiConfigController(WifiConfigUiBase wifiConfigUiBase, View view, AccessPoint accessPoint, int i, WifiManager wifiManager) {
        this.mHaveWapiCert = false;
        this.mIpAssignment = IpConfiguration.IpAssignment.UNASSIGNED;
        this.mProxySettings = IpConfiguration.ProxySettings.UNASSIGNED;
        this.mHttpProxy = null;
        this.mStaticIpConfiguration = null;
        this.mRequestFocus = true;
        this.mSubscriptionManager = null;
        this.mConfigUi = wifiConfigUiBase;
        this.mView = view;
        this.mAccessPoint = accessPoint;
        this.mContext = wifiConfigUiBase.getContext();
        this.mWifiManager = wifiManager;
        initWifiConfigController(accessPoint, i);
    }

    private void initWifiConfigController(AccessPoint accessPoint, int i) {
        boolean z;
        int i2;
        LinkAddress linkAddress;
        this.mAccessPointSecurity = accessPoint == null ? 0 : accessPoint.getSecurity();
        this.mMode = i;
        Resources resources = this.mContext.getResources();
        this.mTelephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
        this.mSimDisplayNames = new ArrayList<>();
        this.mLevels = resources.getStringArray(C0003R$array.wifi_signal);
        if (Utils.isWifiOnly(this.mContext) || !this.mContext.getResources().getBoolean(17891436)) {
            this.mPhase2PeapAdapter = getSpinnerAdapter(C0003R$array.wifi_peap_phase2_entries);
        } else {
            this.mPhase2PeapAdapter = getSpinnerAdapterWithEapMethodsTts(C0003R$array.wifi_peap_phase2_entries_with_sim_auth);
        }
        this.mPhase2TtlsAdapter = getSpinnerAdapter(C0003R$array.wifi_ttls_phase2_entries);
        this.mUnspecifiedCertString = this.mContext.getString(C0017R$string.wifi_unspecified);
        this.mMultipleCertSetString = this.mContext.getString(C0017R$string.wifi_multiple_cert_added);
        this.mUseSystemCertsString = this.mContext.getString(C0017R$string.wifi_use_system_certs);
        this.mDoNotProvideEapUserCertString = this.mContext.getString(C0017R$string.wifi_do_not_provide_eap_user_cert);
        this.mDoNotValidateEapServerString = this.mContext.getString(C0017R$string.wifi_do_not_validate_eap_server);
        this.mSsidScanButton = (ImageButton) this.mView.findViewById(C0010R$id.ssid_scanner_button);
        ScrollView scrollView = (ScrollView) this.mView.findViewById(C0010R$id.dialog_scrollview);
        Spinner spinner = (Spinner) this.mView.findViewById(C0010R$id.ip_settings);
        this.mIpSettingsSpinner = spinner;
        spinner.setOnItemSelectedListener(this);
        Spinner spinner2 = (Spinner) this.mView.findViewById(C0010R$id.proxy_settings);
        this.mProxySettingsSpinner = spinner2;
        spinner2.setOnItemSelectedListener(this);
        this.mSharedCheckBox = (CheckBox) this.mView.findViewById(C0010R$id.shared);
        this.mMeteredSettingsSpinner = (Spinner) this.mView.findViewById(C0010R$id.metered_settings);
        this.mHiddenSettingsSpinner = (Spinner) this.mView.findViewById(C0010R$id.hidden_settings);
        this.mPrivacySettingsSpinner = (Spinner) this.mView.findViewById(C0010R$id.privacy_settings);
        if (this.mWifiManager.isConnectedMacRandomizationSupported()) {
            this.mView.findViewById(C0010R$id.privacy_settings_fields).setVisibility(0);
        }
        this.mHiddenSettingsSpinner.setOnItemSelectedListener(this);
        TextView textView = (TextView) this.mView.findViewById(C0010R$id.hidden_settings_warning);
        this.mHiddenWarningView = textView;
        textView.setVisibility(this.mHiddenSettingsSpinner.getSelectedItemPosition() == 0 ? 8 : 0);
        this.mShareThisWifiCheckBox = (CheckBox) this.mView.findViewById(C0010R$id.share_this_wifi);
        this.mSecurityInPosition = new Integer[10];
        if (this.mAccessPoint == null) {
            configureSecuritySpinner();
            this.mConfigUi.setSubmitButton(resources.getString(C0017R$string.wifi_save));
        } else {
            if (!this.mWifiManager.isWifiCoverageExtendFeatureEnabled() || !(this.mAccessPoint.getSecurity() == 0 || this.mAccessPoint.getSecurity() == 2)) {
                this.mShareThisWifiCheckBox.setChecked(false);
                this.mShareThisWifiCheckBox.setVisibility(8);
            }
            this.mConfigUi.setTitle(this.mAccessPoint.getTitle());
            ViewGroup viewGroup = (ViewGroup) this.mView.findViewById(C0010R$id.info);
            if (this.mAccessPoint.isSaved()) {
                WifiConfiguration config = this.mAccessPoint.getConfig();
                this.mMeteredSettingsSpinner.setSelection(config.meteredOverride);
                this.mHiddenSettingsSpinner.setSelection(config.hiddenSSID ? 1 : 0);
                if (FeatureFlagUtils.isEnabled(this.mContext, "settings_wifitracker2")) {
                    i2 = WifiPrivacyPreferenceController2.translateMacRandomizedValueToPrefValue(config.macRandomizationSetting);
                } else {
                    i2 = WifiPrivacyPreferenceController.translateMacRandomizedValueToPrefValue(config.macRandomizationSetting);
                }
                this.mPrivacySettingsSpinner.setSelection(i2);
                if (config.getIpConfiguration().getIpAssignment() == IpConfiguration.IpAssignment.STATIC) {
                    this.mIpSettingsSpinner.setSelection(1);
                    StaticIpConfiguration staticIpConfiguration = config.getIpConfiguration().getStaticIpConfiguration();
                    if (!(staticIpConfiguration == null || (linkAddress = staticIpConfiguration.ipAddress) == null)) {
                        addRow(viewGroup, C0017R$string.wifi_ip_address, linkAddress.getAddress().getHostAddress());
                    }
                    z = true;
                } else {
                    this.mIpSettingsSpinner.setSelection(0);
                    z = false;
                }
                this.mShareThisWifiCheckBox.setChecked(config.shareThisAp);
                this.mSharedCheckBox.setEnabled(config.shared);
                if (!config.shared) {
                    z = true;
                }
                IpConfiguration.ProxySettings proxySettings = config.getIpConfiguration().getProxySettings();
                if (proxySettings == IpConfiguration.ProxySettings.STATIC) {
                    this.mProxySettingsSpinner.setSelection(1);
                } else if (proxySettings == IpConfiguration.ProxySettings.PAC) {
                    this.mProxySettingsSpinner.setSelection(2);
                } else {
                    this.mProxySettingsSpinner.setSelection(0);
                    if (config != null && config.isPasspoint()) {
                        addRow(viewGroup, C0017R$string.passpoint_label, String.format(this.mContext.getString(C0017R$string.passpoint_content), config.providerFriendlyName));
                    }
                }
                z = true;
                addRow(viewGroup, C0017R$string.passpoint_label, String.format(this.mContext.getString(C0017R$string.passpoint_content), config.providerFriendlyName));
            } else {
                z = false;
            }
            if ((!this.mAccessPoint.isSaved() && !this.mAccessPoint.isActive() && !this.mAccessPoint.isPasspointConfig()) || this.mMode != 0) {
                showSecurityFields(true, true);
                showIpConfigFields();
                showProxyFields();
                CheckBox checkBox = (CheckBox) this.mView.findViewById(C0010R$id.wifi_advanced_togglebox);
                if (!z) {
                    this.mView.findViewById(C0010R$id.wifi_advanced_toggle).setVisibility(0);
                    checkBox.setOnCheckedChangeListener(this);
                    checkBox.setChecked(z);
                    setAdvancedOptionAccessibilityString();
                }
                this.mView.findViewById(C0010R$id.wifi_advanced_fields).setVisibility(z ? 0 : 8);
            }
            int i3 = this.mMode;
            if (i3 == 2) {
                this.mConfigUi.setSubmitButton(resources.getString(C0017R$string.wifi_save));
            } else if (i3 == 1) {
                this.mConfigUi.setSubmitButton(resources.getString(C0017R$string.wifi_connect));
            } else {
                NetworkInfo.DetailedState detailedState = this.mAccessPoint.getDetailedState();
                String signalString = getSignalString();
                if ((detailedState == null || detailedState == NetworkInfo.DetailedState.DISCONNECTED) && signalString != null) {
                    this.mConfigUi.setSubmitButton(resources.getString(C0017R$string.wifi_connect));
                } else {
                    String str = null;
                    if (detailedState != null) {
                        boolean isEphemeral = this.mAccessPoint.isEphemeral();
                        WifiConfiguration config2 = this.mAccessPoint.getConfig();
                        if (config2 != null && config2.isPasspoint()) {
                            String str2 = config2.providerFriendlyName;
                        }
                        addRow(viewGroup, C0017R$string.wifi_status, AccessPoint.getSummary(this.mConfigUi.getContext(), null, detailedState, isEphemeral, (config2 == null || (!config2.fromWifiNetworkSpecifier && !config2.fromWifiNetworkSuggestion)) ? null : config2.creatorName));
                    }
                    if (signalString != null) {
                        addRow(viewGroup, C0017R$string.wifi_signal, signalString);
                    }
                    WifiInfo info = this.mAccessPoint.getInfo();
                    if (!(info == null || info.getTxLinkSpeedMbps() == -1)) {
                        addRow(viewGroup, C0017R$string.tx_wifi_speed, String.format(resources.getString(C0017R$string.tx_link_speed), Integer.valueOf(info.getTxLinkSpeedMbps())));
                    }
                    if (!(info == null || info.getRxLinkSpeedMbps() == -1)) {
                        addRow(viewGroup, C0017R$string.rx_wifi_speed, String.format(resources.getString(C0017R$string.rx_link_speed), Integer.valueOf(info.getRxLinkSpeedMbps())));
                    }
                    if (!(info == null || info.getFrequency() == -1)) {
                        int frequency = info.getFrequency();
                        if (frequency >= 2400 && frequency < 2500) {
                            str = resources.getString(C0017R$string.wifi_band_24ghz);
                        } else if (frequency >= 4900 && frequency < 5900) {
                            str = resources.getString(C0017R$string.wifi_band_5ghz);
                        } else if (frequency < 58320 || frequency >= 70200) {
                            Log.e("WifiConfigController", "Unexpected frequency " + frequency);
                        } else {
                            str = resources.getString(C0017R$string.wifi_band_60ghz);
                        }
                        if (str != null) {
                            addRow(viewGroup, C0017R$string.wifi_frequency, str);
                        }
                    }
                    addRow(viewGroup, C0017R$string.wifi_security, this.mAccessPoint.getSecurityString(false));
                    this.mView.findViewById(C0010R$id.ip_fields).setVisibility(8);
                }
                if (this.mAccessPoint.isSaved() || this.mAccessPoint.isActive() || this.mAccessPoint.isPasspointConfig()) {
                    this.mConfigUi.setForgetButton(resources.getString(C0017R$string.wifi_forget));
                }
            }
            this.mSsidScanButton.setVisibility(8);
        }
        if (!isSplitSystemUser()) {
            this.mSharedCheckBox.setVisibility(8);
        }
        this.mConfigUi.setCancelButton(resources.getString(C0017R$string.wifi_cancel));
        if (this.mConfigUi.getSubmitButton() != null) {
            enableSubmitIfAppropriate();
        }
        if (this.mRequestFocus) {
            this.mView.findViewById(C0010R$id.l_wifidialog).requestFocus();
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isSplitSystemUser() {
        UserManager userManager = (UserManager) this.mContext.getSystemService("user");
        return UserManager.isSplitSystemUser();
    }

    private void addRow(ViewGroup viewGroup, int i, String str) {
        View inflate = this.mConfigUi.getLayoutInflater().inflate(C0012R$layout.wifi_dialog_row, viewGroup, false);
        ((TextView) inflate.findViewById(C0010R$id.name)).setText(i);
        ((TextView) inflate.findViewById(C0010R$id.value)).setText(str);
        viewGroup.addView(inflate);
    }

    /* access modifiers changed from: package-private */
    public String getSignalString() {
        int level;
        if (!this.mAccessPoint.isReachable() || (level = this.mAccessPoint.getLevel()) <= -1) {
            return null;
        }
        String[] strArr = this.mLevels;
        if (level < strArr.length) {
            return strArr[level];
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public void hideForgetButton() {
        Button forgetButton = this.mConfigUi.getForgetButton();
        if (forgetButton != null) {
            forgetButton.setVisibility(8);
        }
    }

    /* access modifiers changed from: package-private */
    public void hideSubmitButton() {
        Button submitButton = this.mConfigUi.getSubmitButton();
        if (submitButton != null) {
            submitButton.setVisibility(8);
        }
    }

    /* access modifiers changed from: package-private */
    public void enableSubmitIfAppropriate() {
        Button submitButton = this.mConfigUi.getSubmitButton();
        if (submitButton != null) {
            submitButton.setEnabled(isSubmittable());
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isValidPsk(String str) {
        if (str.length() == 64 && str.matches("[0-9A-Fa-f]{64}")) {
            return true;
        }
        if (str.length() < 8 || str.length() > 63) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean isValidSaePassword(String str) {
        return str.length() >= 1 && str.length() <= 63;
    }

    /* access modifiers changed from: package-private */
    public boolean isSubmittable() {
        AccessPoint accessPoint;
        TextView textView = this.mPasswordView;
        boolean z = true;
        if (textView == null || ((this.mAccessPointSecurity != 1 || isWepPskValid(textView.getText().toString(), this.mPasswordView.length())) && ((this.mAccessPointSecurity != 2 || isValidPsk(this.mPasswordView.getText().toString())) && ((this.mAccessPointSecurity != 5 || isValidSaePassword(this.mPasswordView.getText().toString())) && (this.mAccessPointSecurity != 8 || isWapiPskValid()))))) {
            z = false;
        }
        TextView textView2 = this.mPasswordView;
        if (textView2 != null && textView2.length() == 0 && (accessPoint = this.mAccessPoint) != null && accessPoint.isSaved()) {
            z = false;
        }
        TextView textView3 = this.mSsidView;
        boolean ipAndProxyFieldsAreValid = ((textView3 == null || textView3.length() != 0) && !z) ? ipAndProxyFieldsAreValid() : false;
        int i = this.mAccessPointSecurity;
        if (!((i != 3 && i != 6) || this.mEapCaCertSpinner == null || this.mView.findViewById(C0010R$id.l_ca_cert).getVisibility() == 8)) {
            String str = (String) this.mEapCaCertSpinner.getSelectedItem();
            if (str.equals(this.mUnspecifiedCertString)) {
                ipAndProxyFieldsAreValid = false;
            }
            if (str.equals(this.mUseSystemCertsString) && this.mEapDomainView != null && this.mView.findViewById(C0010R$id.l_domain).getVisibility() != 8 && TextUtils.isEmpty(this.mEapDomainView.getText().toString())) {
                ipAndProxyFieldsAreValid = false;
            }
        }
        int i2 = this.mAccessPointSecurity;
        if ((i2 == 3 || i2 == 6) && this.mEapUserCertSpinner != null && this.mView.findViewById(C0010R$id.l_user_cert).getVisibility() != 8 && this.mEapUserCertSpinner.getSelectedItem().equals(this.mUnspecifiedCertString)) {
            return false;
        }
        return ipAndProxyFieldsAreValid;
    }

    /* access modifiers changed from: package-private */
    public void showWarningMessagesIfAppropriate() {
        this.mView.findViewById(C0010R$id.no_ca_cert_warning).setVisibility(8);
        this.mView.findViewById(C0010R$id.no_user_cert_warning).setVisibility(8);
        this.mView.findViewById(C0010R$id.no_domain_warning).setVisibility(8);
        this.mView.findViewById(C0010R$id.ssid_too_long_warning).setVisibility(8);
        TextView textView = this.mSsidView;
        if (textView != null && WifiUtils.isSSIDTooLong(textView.getText().toString())) {
            this.mView.findViewById(C0010R$id.ssid_too_long_warning).setVisibility(0);
        }
        if (!(this.mEapCaCertSpinner == null || this.mView.findViewById(C0010R$id.l_ca_cert).getVisibility() == 8)) {
            String str = (String) this.mEapCaCertSpinner.getSelectedItem();
            if (str.equals(this.mDoNotValidateEapServerString)) {
                this.mView.findViewById(C0010R$id.no_ca_cert_warning).setVisibility(0);
            }
            if (str.equals(this.mUseSystemCertsString) && this.mEapDomainView != null && this.mView.findViewById(C0010R$id.l_domain).getVisibility() != 8 && TextUtils.isEmpty(this.mEapDomainView.getText().toString())) {
                this.mView.findViewById(C0010R$id.no_domain_warning).setVisibility(0);
            }
        }
        if (this.mAccessPointSecurity == 6 && this.mEapMethodSpinner.getSelectedItemPosition() == 1 && ((String) this.mEapUserCertSpinner.getSelectedItem()).equals(this.mUnspecifiedCertString)) {
            this.mView.findViewById(C0010R$id.no_user_cert_warning).setVisibility(0);
        }
    }

    private boolean isWapiPskValid() {
        if (this.mPasswordView.length() < 8 || this.mPasswordView.length() > 64) {
            return false;
        }
        String charSequence = this.mPasswordView.getText().toString();
        if (WAPI_PSK_TYPE[this.mWapiPskTypeSpinner.getSelectedItemPosition()] != 1 || (this.mPasswordView.length() % 2 == 0 && charSequence.matches("[0-9A-Fa-f]*"))) {
            return true;
        }
        return false;
    }

    public WifiConfiguration getConfig() {
        int i;
        if (this.mMode == 0) {
            return null;
        }
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        AccessPoint accessPoint = this.mAccessPoint;
        if (accessPoint == null) {
            wifiConfiguration.SSID = AccessPoint.convertToQuotedString(this.mSsidView.getText().toString());
            wifiConfiguration.hiddenSSID = this.mHiddenSettingsSpinner.getSelectedItemPosition() == 1;
        } else if (!accessPoint.isSaved()) {
            wifiConfiguration.SSID = AccessPoint.convertToQuotedString(this.mAccessPoint.getSsidStr());
        } else {
            wifiConfiguration.networkId = this.mAccessPoint.getConfig().networkId;
            wifiConfiguration.hiddenSSID = this.mAccessPoint.getConfig().hiddenSSID;
        }
        wifiConfiguration.shared = this.mSharedCheckBox.isChecked();
        wifiConfiguration.shareThisAp = this.mShareThisWifiCheckBox.isChecked();
        int i2 = this.mAccessPointSecurity;
        switch (i2) {
            case 0:
                wifiConfiguration.setSecurityParams(0);
                break;
            case 1:
                wifiConfiguration.setSecurityParams(1);
                if (this.mPasswordView.length() != 0) {
                    int length = this.mPasswordView.length();
                    String charSequence = this.mPasswordView.getText().toString();
                    if ((length != 10 && length != 26 && length != 32) || !charSequence.matches("[0-9A-Fa-f]*")) {
                        String[] strArr = wifiConfiguration.wepKeys;
                        strArr[0] = '\"' + charSequence + '\"';
                        break;
                    } else {
                        wifiConfiguration.wepKeys[0] = charSequence;
                        break;
                    }
                }
                break;
            case 2:
                wifiConfiguration.setSecurityParams(2);
                if (this.mPasswordView.length() != 0) {
                    String charSequence2 = this.mPasswordView.getText().toString();
                    if (!charSequence2.matches("[0-9A-Fa-f]{64}")) {
                        wifiConfiguration.preSharedKey = '\"' + charSequence2 + '\"';
                        break;
                    } else {
                        wifiConfiguration.preSharedKey = charSequence2;
                        break;
                    }
                }
                break;
            case 3:
            case 6:
                if (i2 == 6) {
                    wifiConfiguration.setSecurityParams(5);
                } else {
                    wifiConfiguration.setSecurityParams(3);
                }
                wifiConfiguration.enterpriseConfig = new WifiEnterpriseConfig();
                int selectedItemPosition = this.mEapMethodSpinner.getSelectedItemPosition();
                int selectedItemPosition2 = this.mPhase2Spinner.getSelectedItemPosition();
                wifiConfiguration.enterpriseConfig.setEapMethod(selectedItemPosition);
                if (selectedItemPosition != 0) {
                    if (selectedItemPosition != 2) {
                        if (selectedItemPosition == 4 || selectedItemPosition == 5 || selectedItemPosition == 6) {
                            int selectedItemPosition3 = this.mSimCardSpinner.getSelectedItemPosition() + 1;
                            this.selectedSimCardNumber = selectedItemPosition3;
                            wifiConfiguration.enterpriseConfig.setSimNum(selectedItemPosition3);
                        }
                    } else if (selectedItemPosition2 == 0) {
                        wifiConfiguration.enterpriseConfig.setPhase2Method(1);
                    } else if (selectedItemPosition2 == 1) {
                        wifiConfiguration.enterpriseConfig.setPhase2Method(2);
                    } else if (selectedItemPosition2 == 2) {
                        wifiConfiguration.enterpriseConfig.setPhase2Method(3);
                    } else if (selectedItemPosition2 != 3) {
                        Log.e("WifiConfigController", "Unknown phase2 method" + selectedItemPosition2);
                    } else {
                        wifiConfiguration.enterpriseConfig.setPhase2Method(4);
                    }
                } else if (selectedItemPosition2 == 0) {
                    wifiConfiguration.enterpriseConfig.setPhase2Method(3);
                } else if (selectedItemPosition2 == 1) {
                    wifiConfiguration.enterpriseConfig.setPhase2Method(4);
                } else if (selectedItemPosition2 == 2) {
                    wifiConfiguration.enterpriseConfig.setPhase2Method(5);
                } else if (selectedItemPosition2 == 3) {
                    wifiConfiguration.enterpriseConfig.setPhase2Method(6);
                } else if (selectedItemPosition2 != 4) {
                    Log.e("WifiConfigController", "Unknown phase2 method" + selectedItemPosition2);
                } else {
                    wifiConfiguration.enterpriseConfig.setPhase2Method(7);
                }
                String str = (String) this.mEapCaCertSpinner.getSelectedItem();
                wifiConfiguration.enterpriseConfig.setCaCertificateAliases(null);
                wifiConfiguration.enterpriseConfig.setCaPath(null);
                wifiConfiguration.enterpriseConfig.setDomainSuffixMatch(this.mEapDomainView.getText().toString());
                if (!str.equals(this.mUnspecifiedCertString) && !str.equals(this.mDoNotValidateEapServerString)) {
                    if (str.equals(this.mUseSystemCertsString)) {
                        wifiConfiguration.enterpriseConfig.setCaPath("/system/etc/security/cacerts");
                    } else if (str.equals(this.mMultipleCertSetString)) {
                        AccessPoint accessPoint2 = this.mAccessPoint;
                        if (accessPoint2 != null) {
                            if (!accessPoint2.isSaved()) {
                                Log.e("WifiConfigController", "Multiple certs can only be set when editing saved network");
                            }
                            wifiConfiguration.enterpriseConfig.setCaCertificateAliases(this.mAccessPoint.getConfig().enterpriseConfig.getCaCertificateAliases());
                        }
                    } else {
                        wifiConfiguration.enterpriseConfig.setCaCertificateAliases(new String[]{str});
                    }
                }
                if (!(wifiConfiguration.enterpriseConfig.getCaCertificateAliases() == null || wifiConfiguration.enterpriseConfig.getCaPath() == null)) {
                    Log.e("WifiConfigController", "ca_cert (" + wifiConfiguration.enterpriseConfig.getCaCertificateAliases() + ") and ca_path (" + wifiConfiguration.enterpriseConfig.getCaPath() + ") should not both be non-null");
                }
                if (str.equals(this.mUnspecifiedCertString) || str.equals(this.mDoNotValidateEapServerString)) {
                    wifiConfiguration.enterpriseConfig.setOcsp(0);
                } else {
                    wifiConfiguration.enterpriseConfig.setOcsp(this.mEapOcspSpinner.getSelectedItemPosition());
                }
                String str2 = (String) this.mEapUserCertSpinner.getSelectedItem();
                if (str2.equals(this.mUnspecifiedCertString) || str2.equals(this.mDoNotProvideEapUserCertString)) {
                    str2 = "";
                }
                wifiConfiguration.enterpriseConfig.setClientCertificateAlias(str2);
                if (selectedItemPosition == 4 || selectedItemPosition == 5 || selectedItemPosition == 6) {
                    wifiConfiguration.enterpriseConfig.setIdentity("");
                    wifiConfiguration.enterpriseConfig.setAnonymousIdentity("");
                } else if (selectedItemPosition == 3) {
                    wifiConfiguration.enterpriseConfig.setIdentity(this.mEapIdentityView.getText().toString());
                    wifiConfiguration.enterpriseConfig.setAnonymousIdentity("");
                } else {
                    wifiConfiguration.enterpriseConfig.setIdentity(this.mEapIdentityView.getText().toString());
                    wifiConfiguration.enterpriseConfig.setAnonymousIdentity(this.mEapAnonymousView.getText().toString());
                }
                if (this.mPasswordView.isShown()) {
                    if (this.mPasswordView.length() > 0) {
                        wifiConfiguration.enterpriseConfig.setPassword(this.mPasswordView.getText().toString());
                        break;
                    }
                } else {
                    wifiConfiguration.enterpriseConfig.setPassword(this.mPasswordView.getText().toString());
                    break;
                }
                break;
            case 4:
                wifiConfiguration.setSecurityParams(6);
                break;
            case 5:
                wifiConfiguration.setSecurityParams(4);
                if (this.mPasswordView.length() != 0) {
                    String charSequence3 = this.mPasswordView.getText().toString();
                    wifiConfiguration.preSharedKey = '\"' + charSequence3 + '\"';
                    break;
                }
                break;
            case 7:
                wifiConfiguration.allowedKeyManagement.set(17);
                wifiConfiguration.requirePmf = true;
                break;
            case 8:
                wifiConfiguration.allowedKeyManagement.set(13);
                if (this.mPasswordView.length() != 0) {
                    if (this.mWapiPskTypeSpinner.getSelectedItemPosition() != 0) {
                        wifiConfiguration.preSharedKey = this.mPasswordView.getText().toString();
                        break;
                    } else {
                        wifiConfiguration.preSharedKey = '\"' + this.mPasswordView.getText().toString() + '\"';
                        break;
                    }
                }
                break;
            case 9:
                wifiConfiguration.allowedKeyManagement.set(14);
                wifiConfiguration.enterpriseConfig.setEapMethod(8);
                if (this.mWapiCertSpinner.getSelectedItemPosition() != 0) {
                    wifiConfiguration.enterpriseConfig.setWapiCertSuite((String) this.mWapiCertSpinner.getSelectedItem());
                    break;
                } else {
                    wifiConfiguration.enterpriseConfig.setWapiCertSuite("auto");
                    break;
                }
            default:
                return null;
        }
        wifiConfiguration.setIpConfiguration(new IpConfiguration(this.mIpAssignment, this.mProxySettings, this.mStaticIpConfiguration, this.mHttpProxy));
        Spinner spinner = this.mMeteredSettingsSpinner;
        if (spinner != null) {
            wifiConfiguration.meteredOverride = spinner.getSelectedItemPosition();
        }
        if (this.mPrivacySettingsSpinner != null) {
            if (FeatureFlagUtils.isEnabled(this.mContext, "settings_wifitracker2")) {
                i = WifiPrivacyPreferenceController2.translatePrefValueToMacRandomizedValue(this.mPrivacySettingsSpinner.getSelectedItemPosition());
            } else {
                i = WifiPrivacyPreferenceController.translatePrefValueToMacRandomizedValue(this.mPrivacySettingsSpinner.getSelectedItemPosition());
            }
            wifiConfiguration.macRandomizationSetting = i;
        }
        return wifiConfiguration;
    }

    /* JADX WARNING: Removed duplicated region for block: B:25:0x0066  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x006e A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean ipAndProxyFieldsAreValid() {
        /*
        // Method dump skipped, instructions count: 152
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.wifi.WifiConfigController.ipAndProxyFieldsAreValid():boolean");
    }

    private Inet4Address getIPv4Address(String str) {
        try {
            return (Inet4Address) NetworkUtils.numericToInetAddress(str);
        } catch (ClassCastException | IllegalArgumentException unused) {
            return null;
        }
    }

    private int validateIpConfigFields(StaticIpConfiguration staticIpConfiguration) {
        TextView textView = this.mIpAddressView;
        if (textView == null) {
            return 0;
        }
        String charSequence = textView.getText().toString();
        if (TextUtils.isEmpty(charSequence)) {
            return C0017R$string.wifi_ip_settings_invalid_ip_address;
        }
        Inet4Address iPv4Address = getIPv4Address(charSequence);
        if (iPv4Address == null || iPv4Address.equals(Inet4Address.ANY)) {
            return C0017R$string.wifi_ip_settings_invalid_ip_address;
        }
        int i = -1;
        try {
            i = Integer.parseInt(this.mNetworkPrefixLengthView.getText().toString());
            if (i >= 0) {
                if (i <= 32) {
                    staticIpConfiguration.ipAddress = new LinkAddress(iPv4Address, i);
                    String charSequence2 = this.mGatewayView.getText().toString();
                    if (!TextUtils.isEmpty(charSequence2) || this.mGatewayView.isFocused()) {
                        Inet4Address iPv4Address2 = getIPv4Address(charSequence2);
                        if (iPv4Address2 == null) {
                            return C0017R$string.wifi_ip_settings_invalid_gateway;
                        }
                        if (iPv4Address2.isMulticastAddress()) {
                            return C0017R$string.wifi_ip_settings_invalid_gateway;
                        }
                        staticIpConfiguration.gateway = iPv4Address2;
                    } else {
                        try {
                            byte[] address = NetworkUtils.getNetworkPart(iPv4Address, i).getAddress();
                            address[address.length - 1] = 1;
                            this.mGatewayView.setText(InetAddress.getByAddress(address).getHostAddress());
                        } catch (RuntimeException | UnknownHostException unused) {
                        }
                    }
                    String charSequence3 = this.mDns1View.getText().toString();
                    if (!TextUtils.isEmpty(charSequence3) || this.mDns1View.isFocused()) {
                        Inet4Address iPv4Address3 = getIPv4Address(charSequence3);
                        if (iPv4Address3 == null) {
                            return C0017R$string.wifi_ip_settings_invalid_dns;
                        }
                        staticIpConfiguration.dnsServers.add(iPv4Address3);
                    } else {
                        this.mDns1View.setText(this.mConfigUi.getContext().getString(C0017R$string.wifi_dns1_hint));
                    }
                    if (this.mDns2View.length() > 0) {
                        Inet4Address iPv4Address4 = getIPv4Address(this.mDns2View.getText().toString());
                        if (iPv4Address4 == null) {
                            return C0017R$string.wifi_ip_settings_invalid_dns;
                        }
                        staticIpConfiguration.dnsServers.add(iPv4Address4);
                    }
                    return 0;
                }
            }
            return C0017R$string.wifi_ip_settings_invalid_network_prefix_length;
        } catch (NumberFormatException unused2) {
            if (!this.mNetworkPrefixLengthView.isFocused()) {
                this.mNetworkPrefixLengthView.setText(this.mConfigUi.getContext().getString(C0017R$string.wifi_network_prefix_length_hint));
            }
        } catch (IllegalArgumentException unused3) {
            return C0017R$string.wifi_ip_settings_invalid_ip_address;
        }
    }

    private void showSecurityFields(boolean z, boolean z2) {
        boolean z3;
        AccessPoint accessPoint;
        int i = this.mAccessPointSecurity;
        if (i == 0 || i == 4 || i == 7) {
            this.mView.findViewById(C0010R$id.security_fields).setVisibility(8);
            return;
        }
        this.mView.findViewById(C0010R$id.security_fields).setVisibility(0);
        if (this.mPasswordView == null) {
            TextView textView = (TextView) this.mView.findViewById(C0010R$id.password);
            this.mPasswordView = textView;
            textView.addTextChangedListener(this);
            this.mPasswordView.setOnEditorActionListener(this);
            this.mPasswordView.setOnKeyListener(this);
            ((CheckBox) this.mView.findViewById(C0010R$id.show_password)).setOnCheckedChangeListener(this);
            AccessPoint accessPoint2 = this.mAccessPoint;
            if (accessPoint2 != null && accessPoint2.isSaved()) {
                this.mPasswordView.setHint(C0017R$string.wifi_unchanged);
            }
        }
        if (this.mAccessPointSecurity != 8) {
            this.mView.findViewById(C0010R$id.wapi_psk).setVisibility(8);
        } else {
            this.mView.findViewById(C0010R$id.wapi_psk).setVisibility(0);
            this.mWapiPskTypeSpinner = (Spinner) this.mView.findViewById(C0010R$id.wapi_psk_type);
            AccessPoint accessPoint3 = this.mAccessPoint;
            if (accessPoint3 != null && accessPoint3.isSaved()) {
                this.mAccessPoint.getConfig();
            }
            this.mWapiPskTypeSpinner.setOnItemSelectedListener(this);
        }
        if (this.mAccessPointSecurity != 9) {
            this.mView.findViewById(C0010R$id.wapi_cert).setVisibility(8);
            this.mView.findViewById(C0010R$id.password_layout).setVisibility(0);
            this.mView.findViewById(C0010R$id.show_password_layout).setVisibility(0);
            int i2 = this.mAccessPointSecurity;
            if (i2 == 3 || i2 == 6) {
                this.mView.findViewById(C0010R$id.eap).setVisibility(0);
                if (this.mEapMethodSpinner == null) {
                    getSIMInfo();
                    Spinner spinner = (Spinner) this.mView.findViewById(C0010R$id.method);
                    this.mEapMethodSpinner = spinner;
                    spinner.setOnItemSelectedListener(this);
                    if (this.mAccessPointSecurity == 6) {
                        this.mEapMethodSpinner.setSelection(1);
                        this.mEapMethodSpinner.setEnabled(false);
                    }
                    Spinner spinner2 = (Spinner) this.mView.findViewById(C0010R$id.phase2);
                    this.mPhase2Spinner = spinner2;
                    spinner2.setOnItemSelectedListener(this);
                    Spinner spinner3 = (Spinner) this.mView.findViewById(C0010R$id.ca_cert);
                    this.mEapCaCertSpinner = spinner3;
                    spinner3.setOnItemSelectedListener(this);
                    this.mEapOcspSpinner = (Spinner) this.mView.findViewById(C0010R$id.ocsp);
                    TextView textView2 = (TextView) this.mView.findViewById(C0010R$id.domain);
                    this.mEapDomainView = textView2;
                    textView2.addTextChangedListener(this);
                    Spinner spinner4 = (Spinner) this.mView.findViewById(C0010R$id.user_cert);
                    this.mEapUserCertSpinner = spinner4;
                    spinner4.setOnItemSelectedListener(this);
                    this.mSimCardSpinner = (Spinner) this.mView.findViewById(C0010R$id.sim_card);
                    this.mEapIdentityView = (TextView) this.mView.findViewById(C0010R$id.identity);
                    this.mEapAnonymousView = (TextView) this.mView.findViewById(C0010R$id.anonymous);
                    setAccessibilityDelegateForSecuritySpinners();
                    z3 = true;
                } else {
                    z3 = false;
                }
                if (z) {
                    if (this.mAccessPointSecurity == 6) {
                        this.mEapMethodSpinner.setAdapter((SpinnerAdapter) getSpinnerAdapter(C0003R$array.wifi_eap_method));
                        this.mEapMethodSpinner.setSelection(1);
                        this.mEapMethodSpinner.setEnabled(false);
                    } else if (Utils.isWifiOnly(this.mContext) || !this.mContext.getResources().getBoolean(17891436)) {
                        this.mEapMethodSpinner.setAdapter((SpinnerAdapter) getSpinnerAdapter(C0003R$array.eap_method_without_sim_auth));
                        this.mEapMethodSpinner.setEnabled(true);
                    } else {
                        this.mEapMethodSpinner.setAdapter((SpinnerAdapter) getSpinnerAdapterWithEapMethodsTts(C0003R$array.wifi_eap_method));
                        this.mEapMethodSpinner.setEnabled(true);
                    }
                }
                if (z2) {
                    loadCertificates(this.mEapCaCertSpinner, "CACERT_", this.mDoNotValidateEapServerString, false, true);
                    loadCertificates(this.mEapUserCertSpinner, "USRPKEY_", this.mDoNotProvideEapUserCertString, false, false);
                    setSelection(this.mEapCaCertSpinner, this.mUseSystemCertsString);
                }
                if (!z3 || (accessPoint = this.mAccessPoint) == null || !accessPoint.isSaved()) {
                    if (this.mAccessPointSecurity == 6) {
                        this.mEapMethodSpinner.setSelection(1);
                        this.mEapMethodSpinner.setEnabled(false);
                    } else {
                        this.mEapMethodSpinner.setEnabled(true);
                    }
                    showEapFieldsByMethod(this.mEapMethodSpinner.getSelectedItemPosition());
                    return;
                }
                WifiEnterpriseConfig wifiEnterpriseConfig = this.mAccessPoint.getConfig().enterpriseConfig;
                int eapMethod = wifiEnterpriseConfig.getEapMethod();
                int phase2Method = wifiEnterpriseConfig.getPhase2Method();
                this.mEapMethodSpinner.setSelection(eapMethod);
                showEapFieldsByMethod(eapMethod);
                if (eapMethod != 0) {
                    if (eapMethod != 2) {
                        if (eapMethod == 4 || eapMethod == 5 || eapMethod == 6) {
                            if (wifiEnterpriseConfig.getSimNum() == null || wifiEnterpriseConfig.getSimNum().isEmpty()) {
                                this.mSimCardSpinner.setSelection(0);
                            } else {
                                this.mSimCardSpinner.setSelection(Integer.parseInt(wifiEnterpriseConfig.getSimNum()) - 1);
                            }
                        }
                    } else if (phase2Method == 1) {
                        this.mPhase2Spinner.setSelection(0);
                    } else if (phase2Method == 2) {
                        this.mPhase2Spinner.setSelection(1);
                    } else if (phase2Method == 3) {
                        this.mPhase2Spinner.setSelection(2);
                    } else if (phase2Method != 4) {
                        Log.e("WifiConfigController", "Invalid phase 2 method " + phase2Method);
                    } else {
                        this.mPhase2Spinner.setSelection(3);
                    }
                } else if (phase2Method == 3) {
                    this.mPhase2Spinner.setSelection(0);
                } else if (phase2Method == 4) {
                    this.mPhase2Spinner.setSelection(1);
                } else if (phase2Method == 5) {
                    this.mPhase2Spinner.setSelection(2);
                } else if (phase2Method == 6) {
                    this.mPhase2Spinner.setSelection(3);
                } else if (phase2Method != 7) {
                    Log.e("WifiConfigController", "Invalid phase 2 method " + phase2Method);
                } else {
                    this.mPhase2Spinner.setSelection(4);
                }
                if (!TextUtils.isEmpty(wifiEnterpriseConfig.getCaPath())) {
                    setSelection(this.mEapCaCertSpinner, this.mUseSystemCertsString);
                } else {
                    String[] caCertificateAliases = wifiEnterpriseConfig.getCaCertificateAliases();
                    if (caCertificateAliases == null) {
                        setSelection(this.mEapCaCertSpinner, this.mDoNotValidateEapServerString);
                    } else if (caCertificateAliases.length == 1) {
                        setSelection(this.mEapCaCertSpinner, caCertificateAliases[0]);
                    } else {
                        loadCertificates(this.mEapCaCertSpinner, "CACERT_", this.mDoNotValidateEapServerString, true, true);
                        setSelection(this.mEapCaCertSpinner, this.mMultipleCertSetString);
                    }
                }
                this.mEapOcspSpinner.setSelection(wifiEnterpriseConfig.getOcsp());
                this.mEapDomainView.setText(wifiEnterpriseConfig.getDomainSuffixMatch());
                String clientCertificateAlias = wifiEnterpriseConfig.getClientCertificateAlias();
                if (TextUtils.isEmpty(clientCertificateAlias)) {
                    setSelection(this.mEapUserCertSpinner, this.mDoNotProvideEapUserCertString);
                } else {
                    setSelection(this.mEapUserCertSpinner, clientCertificateAlias);
                }
                this.mEapIdentityView.setText(wifiEnterpriseConfig.getIdentity());
                this.mEapAnonymousView.setText(wifiEnterpriseConfig.getAnonymousIdentity());
                return;
            }
            this.mView.findViewById(C0010R$id.eap).setVisibility(8);
            this.mView.findViewById(C0010R$id.password_layout).setVisibility(0);
            this.mView.findViewById(C0010R$id.show_password_layout).setVisibility(0);
            return;
        }
        this.mView.findViewById(C0010R$id.password_layout).setVisibility(8);
        this.mView.findViewById(C0010R$id.show_password_layout).setVisibility(8);
        this.mView.findViewById(C0010R$id.eap).setVisibility(8);
        this.mView.findViewById(C0010R$id.wapi_cert).setVisibility(0);
        Spinner spinner5 = (Spinner) this.mView.findViewById(C0010R$id.wapi_cert_select);
        this.mWapiCertSpinner = spinner5;
        loadWapiCertificates(spinner5);
        AccessPoint accessPoint4 = this.mAccessPoint;
        if (accessPoint4 != null && accessPoint4.isSaved()) {
            WifiConfiguration config = this.mAccessPoint.getConfig();
            if (config.enterpriseConfig.getWapiCertSuite().equals("auto")) {
                this.mWapiCertSpinner.setSelection(0);
            } else {
                setSelection(this.mWapiCertSpinner, config.enterpriseConfig.getWapiCertSuite());
            }
        }
    }

    private void setAccessibilityDelegateForSecuritySpinners() {
        AnonymousClass1 r0 = new View.AccessibilityDelegate(this) {
            /* class com.android.settings.wifi.WifiConfigController.AnonymousClass1 */

            public void sendAccessibilityEvent(View view, int i) {
                if (i != 4) {
                    super.sendAccessibilityEvent(view, i);
                }
            }
        };
        this.mEapMethodSpinner.setAccessibilityDelegate(r0);
        this.mPhase2Spinner.setAccessibilityDelegate(r0);
        this.mEapCaCertSpinner.setAccessibilityDelegate(r0);
        this.mEapOcspSpinner.setAccessibilityDelegate(r0);
        this.mEapUserCertSpinner.setAccessibilityDelegate(r0);
    }

    private void showEapFieldsByMethod(int i) {
        this.mView.findViewById(C0010R$id.l_method).setVisibility(0);
        this.mView.findViewById(C0010R$id.l_identity).setVisibility(0);
        this.mView.findViewById(C0010R$id.l_domain).setVisibility(0);
        this.mView.findViewById(C0010R$id.l_ca_cert).setVisibility(0);
        this.mView.findViewById(C0010R$id.l_ocsp).setVisibility(0);
        this.mView.findViewById(C0010R$id.password_layout).setVisibility(0);
        this.mView.findViewById(C0010R$id.show_password_layout).setVisibility(0);
        this.mConfigUi.getContext();
        switch (i) {
            case 0:
                ArrayAdapter<CharSequence> arrayAdapter = this.mPhase2Adapter;
                ArrayAdapter<CharSequence> arrayAdapter2 = this.mPhase2PeapAdapter;
                if (arrayAdapter != arrayAdapter2) {
                    this.mPhase2Adapter = arrayAdapter2;
                    this.mPhase2Spinner.setAdapter((SpinnerAdapter) arrayAdapter2);
                }
                this.mView.findViewById(C0010R$id.l_phase2).setVisibility(0);
                this.mView.findViewById(C0010R$id.l_anonymous).setVisibility(0);
                showPeapFields();
                setUserCertInvisible();
                setSimCardInvisible();
                break;
            case 1:
                this.mView.findViewById(C0010R$id.l_user_cert).setVisibility(0);
                setPhase2Invisible();
                setAnonymousIdentInvisible();
                setPasswordInvisible();
                setSimCardInvisible();
                break;
            case 2:
                ArrayAdapter<CharSequence> arrayAdapter3 = this.mPhase2Adapter;
                ArrayAdapter<CharSequence> arrayAdapter4 = this.mPhase2TtlsAdapter;
                if (arrayAdapter3 != arrayAdapter4) {
                    this.mPhase2Adapter = arrayAdapter4;
                    this.mPhase2Spinner.setAdapter((SpinnerAdapter) arrayAdapter4);
                }
                this.mView.findViewById(C0010R$id.l_phase2).setVisibility(0);
                this.mView.findViewById(C0010R$id.l_anonymous).setVisibility(0);
                setUserCertInvisible();
                setSimCardInvisible();
                break;
            case 3:
                setPhase2Invisible();
                setCaCertInvisible();
                setOcspInvisible();
                setDomainInvisible();
                setAnonymousIdentInvisible();
                setUserCertInvisible();
                setSimCardInvisible();
                break;
            case 4:
            case 5:
            case 6:
                WifiConfiguration wifiConfiguration = null;
                AccessPoint accessPoint = this.mAccessPoint;
                if (accessPoint != null) {
                    wifiConfiguration = accessPoint.getConfig();
                }
                Context context = this.mContext;
                ArrayList<String> arrayList = this.mSimDisplayNames;
                ArrayAdapter arrayAdapter5 = new ArrayAdapter(context, 17367048, (String[]) arrayList.toArray(new String[arrayList.size()]));
                arrayAdapter5.setDropDownViewResource(17367049);
                this.mSimCardSpinner.setAdapter((SpinnerAdapter) arrayAdapter5);
                this.mView.findViewById(C0010R$id.l_sim_card).setVisibility(0);
                if (wifiConfiguration != null) {
                    if (wifiConfiguration.enterpriseConfig.getSimNum() == null || wifiConfiguration.enterpriseConfig.getSimNum().isEmpty()) {
                        this.mSimCardSpinner.setSelection(0);
                    } else {
                        this.mSimCardSpinner.setSelection(Integer.parseInt(wifiConfiguration.enterpriseConfig.getSimNum()) - 1);
                    }
                }
                setPhase2Invisible();
                setAnonymousIdentInvisible();
                setCaCertInvisible();
                setOcspInvisible();
                setDomainInvisible();
                setUserCertInvisible();
                setPasswordInvisible();
                setIdentityInvisible();
                break;
        }
        if (this.mView.findViewById(C0010R$id.l_ca_cert).getVisibility() != 8) {
            String str = (String) this.mEapCaCertSpinner.getSelectedItem();
            if (str.equals(this.mDoNotValidateEapServerString) || str.equals(this.mUnspecifiedCertString)) {
                setDomainInvisible();
                setOcspInvisible();
            }
        }
    }

    private void showPeapFields() {
        int selectedItemPosition = this.mPhase2Spinner.getSelectedItemPosition();
        if (selectedItemPosition == 2 || selectedItemPosition == 3 || selectedItemPosition == 4) {
            this.mEapIdentityView.setText("");
            this.mView.findViewById(C0010R$id.l_identity).setVisibility(8);
            setPasswordInvisible();
            return;
        }
        this.mView.findViewById(C0010R$id.l_identity).setVisibility(0);
        this.mView.findViewById(C0010R$id.l_anonymous).setVisibility(0);
        this.mView.findViewById(C0010R$id.password_layout).setVisibility(0);
        this.mView.findViewById(C0010R$id.show_password_layout).setVisibility(0);
    }

    private void setSimCardInvisible() {
        this.mView.findViewById(C0010R$id.l_sim_card).setVisibility(8);
    }

    private void setIdentityInvisible() {
        this.mView.findViewById(C0010R$id.l_identity).setVisibility(8);
    }

    private void setPhase2Invisible() {
        this.mView.findViewById(C0010R$id.l_phase2).setVisibility(8);
    }

    private void setCaCertInvisible() {
        this.mView.findViewById(C0010R$id.l_ca_cert).setVisibility(8);
        setSelection(this.mEapCaCertSpinner, this.mUnspecifiedCertString);
    }

    private void setOcspInvisible() {
        this.mView.findViewById(C0010R$id.l_ocsp).setVisibility(8);
        this.mEapOcspSpinner.setSelection(0);
    }

    private void setDomainInvisible() {
        this.mView.findViewById(C0010R$id.l_domain).setVisibility(8);
        this.mEapDomainView.setText("");
    }

    private void setUserCertInvisible() {
        this.mView.findViewById(C0010R$id.l_user_cert).setVisibility(8);
        setSelection(this.mEapUserCertSpinner, this.mUnspecifiedCertString);
    }

    private void setAnonymousIdentInvisible() {
        this.mView.findViewById(C0010R$id.l_anonymous).setVisibility(8);
        this.mEapAnonymousView.setText("");
    }

    private void setPasswordInvisible() {
        this.mPasswordView.setText("");
        this.mView.findViewById(C0010R$id.password_layout).setVisibility(8);
        this.mView.findViewById(C0010R$id.show_password_layout).setVisibility(8);
    }

    private void showIpConfigFields() {
        StaticIpConfiguration staticIpConfiguration;
        this.mView.findViewById(C0010R$id.ip_fields).setVisibility(0);
        AccessPoint accessPoint = this.mAccessPoint;
        WifiConfiguration config = (accessPoint == null || !accessPoint.isSaved()) ? null : this.mAccessPoint.getConfig();
        if (this.mIpSettingsSpinner.getSelectedItemPosition() == 1) {
            this.mView.findViewById(C0010R$id.staticip).setVisibility(0);
            if (this.mIpAddressView == null) {
                TextView textView = (TextView) this.mView.findViewById(C0010R$id.ipaddress);
                this.mIpAddressView = textView;
                textView.addTextChangedListener(this);
                TextView textView2 = (TextView) this.mView.findViewById(C0010R$id.gateway);
                this.mGatewayView = textView2;
                textView2.addTextChangedListener(this);
                TextView textView3 = (TextView) this.mView.findViewById(C0010R$id.network_prefix_length);
                this.mNetworkPrefixLengthView = textView3;
                textView3.addTextChangedListener(this);
                TextView textView4 = (TextView) this.mView.findViewById(C0010R$id.dns1);
                this.mDns1View = textView4;
                textView4.addTextChangedListener(this);
                TextView textView5 = (TextView) this.mView.findViewById(C0010R$id.dns2);
                this.mDns2View = textView5;
                textView5.addTextChangedListener(this);
            }
            if (config != null && (staticIpConfiguration = config.getIpConfiguration().getStaticIpConfiguration()) != null) {
                LinkAddress linkAddress = staticIpConfiguration.ipAddress;
                if (linkAddress != null) {
                    this.mIpAddressView.setText(linkAddress.getAddress().getHostAddress());
                    this.mNetworkPrefixLengthView.setText(Integer.toString(staticIpConfiguration.ipAddress.getPrefixLength()));
                }
                InetAddress inetAddress = staticIpConfiguration.gateway;
                if (inetAddress != null) {
                    this.mGatewayView.setText(inetAddress.getHostAddress());
                }
                Iterator it = staticIpConfiguration.dnsServers.iterator();
                if (it.hasNext()) {
                    this.mDns1View.setText(((InetAddress) it.next()).getHostAddress());
                }
                if (it.hasNext()) {
                    this.mDns2View.setText(((InetAddress) it.next()).getHostAddress());
                    return;
                }
                return;
            }
            return;
        }
        this.mView.findViewById(C0010R$id.staticip).setVisibility(8);
    }

    private void showProxyFields() {
        ProxyInfo httpProxy;
        ProxyInfo httpProxy2;
        this.mView.findViewById(C0010R$id.proxy_settings_fields).setVisibility(0);
        AccessPoint accessPoint = this.mAccessPoint;
        WifiConfiguration config = (accessPoint == null || !accessPoint.isSaved()) ? null : this.mAccessPoint.getConfig();
        if (this.mProxySettingsSpinner.getSelectedItemPosition() == 1) {
            setVisibility(C0010R$id.proxy_warning_limited_support, 0);
            setVisibility(C0010R$id.proxy_fields, 0);
            setVisibility(C0010R$id.proxy_pac_field, 8);
            if (this.mProxyHostView == null) {
                TextView textView = (TextView) this.mView.findViewById(C0010R$id.proxy_hostname);
                this.mProxyHostView = textView;
                textView.addTextChangedListener(this);
                TextView textView2 = (TextView) this.mView.findViewById(C0010R$id.proxy_port);
                this.mProxyPortView = textView2;
                textView2.addTextChangedListener(this);
                TextView textView3 = (TextView) this.mView.findViewById(C0010R$id.proxy_exclusionlist);
                this.mProxyExclusionListView = textView3;
                textView3.addTextChangedListener(this);
            }
            if (config != null && (httpProxy2 = config.getHttpProxy()) != null) {
                this.mProxyHostView.setText(httpProxy2.getHost());
                this.mProxyPortView.setText(Integer.toString(httpProxy2.getPort()));
                this.mProxyExclusionListView.setText(httpProxy2.getExclusionListAsString());
            }
        } else if (this.mProxySettingsSpinner.getSelectedItemPosition() == 2) {
            setVisibility(C0010R$id.proxy_warning_limited_support, 8);
            setVisibility(C0010R$id.proxy_fields, 8);
            setVisibility(C0010R$id.proxy_pac_field, 0);
            if (this.mProxyPacView == null) {
                TextView textView4 = (TextView) this.mView.findViewById(C0010R$id.proxy_pac);
                this.mProxyPacView = textView4;
                textView4.addTextChangedListener(this);
            }
            if (config != null && (httpProxy = config.getHttpProxy()) != null) {
                this.mProxyPacView.setText(httpProxy.getPacFileUrl().toString());
            }
        } else {
            setVisibility(C0010R$id.proxy_warning_limited_support, 8);
            setVisibility(C0010R$id.proxy_fields, 8);
            setVisibility(C0010R$id.proxy_pac_field, 8);
        }
    }

    private void setVisibility(int i, int i2) {
        View findViewById = this.mView.findViewById(i);
        if (findViewById != null) {
            findViewById.setVisibility(i2);
        }
    }

    /* access modifiers changed from: package-private */
    public KeyStore getKeyStore() {
        return KeyStore.getInstance();
    }

    /* access modifiers changed from: package-private */
    public void loadCertificates(Spinner spinner, String str, String str2, boolean z, boolean z2) {
        this.mConfigUi.getContext();
        ArrayList arrayList = new ArrayList();
        arrayList.add(this.mUnspecifiedCertString);
        if (z) {
            arrayList.add(this.mMultipleCertSetString);
        }
        if (z2) {
            arrayList.add(this.mUseSystemCertsString);
        }
        String[] strArr = null;
        try {
            strArr = getKeyStore().list(str, 1010);
        } catch (Exception unused) {
            Log.e("WifiConfigController", "can't get the certificate list from KeyStore");
        }
        if (!(strArr == null || strArr.length == 0)) {
            arrayList.addAll((Collection) Arrays.stream(strArr).filter($$Lambda$WifiConfigController$aHwAFoUScMrqGBeuDOQscm8s6RE.INSTANCE).collect(Collectors.toList()));
        }
        if (this.mAccessPointSecurity != 6) {
            arrayList.add(str2);
        }
        if (arrayList.size() == 2) {
            arrayList.remove(this.mUnspecifiedCertString);
            spinner.setEnabled(false);
        } else {
            spinner.setEnabled(true);
        }
        spinner.setAdapter((SpinnerAdapter) getSpinnerAdapter((String[]) arrayList.toArray(new String[arrayList.size()])));
    }

    static /* synthetic */ boolean lambda$loadCertificates$0(String str) {
        for (String str2 : UNDESIRED_CERTIFICATES) {
            if (str.startsWith(str2)) {
                return false;
            }
        }
        return true;
    }

    private void loadWapiCertificates(Spinner spinner) {
        Context context = this.mConfigUi.getContext();
        String string = context.getString(C0017R$string.wifi_unspecified);
        String string2 = context.getString(C0017R$string.wapi_auto_sel_cert);
        ArrayList arrayList = new ArrayList();
        String[] list = KeyStore.getInstance().list("WAPI_USER_", 1010);
        if (list == null || list.length <= 0) {
            arrayList.add(string);
        } else {
            arrayList.add(string2);
            for (String str : list) {
                arrayList.add(str);
            }
        }
        if (arrayList.size() > 1) {
            this.mHaveWapiCert = true;
        } else {
            this.mHaveWapiCert = false;
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(context, 17367048, (String[]) arrayList.toArray(new String[0]));
        arrayAdapter.setDropDownViewResource(17367049);
        spinner.setAdapter((SpinnerAdapter) arrayAdapter);
    }

    private void setSelection(Spinner spinner, String str) {
        if (str != null) {
            ArrayAdapter arrayAdapter = (ArrayAdapter) spinner.getAdapter();
            for (int count = arrayAdapter.getCount() - 1; count >= 0; count--) {
                if (str.equals(arrayAdapter.getItem(count))) {
                    spinner.setSelection(count);
                    return;
                }
            }
        }
    }

    public int getMode() {
        return this.mMode;
    }

    public void afterTextChanged(Editable editable) {
        ThreadUtils.postOnMainThread(new Runnable() {
            /* class com.android.settings.wifi.$$Lambda$WifiConfigController$kjpxX7C3P_KOmXXunfifdhfpOuo */

            public final void run() {
                WifiConfigController.this.lambda$afterTextChanged$1$WifiConfigController();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$afterTextChanged$1 */
    public /* synthetic */ void lambda$afterTextChanged$1$WifiConfigController() {
        showWarningMessagesIfAppropriate();
        enableSubmitIfAppropriate();
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (textView != this.mPasswordView || i != 6 || !isSubmittable()) {
            return false;
        }
        this.mConfigUi.dispatchSubmit();
        return true;
    }

    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (view != this.mPasswordView || i != 66 || !isSubmittable()) {
            return false;
        }
        this.mConfigUi.dispatchSubmit();
        return true;
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        if (compoundButton.getId() == C0010R$id.show_password) {
            int selectionEnd = this.mPasswordView.getSelectionEnd();
            this.mPasswordView.setInputType((z ? 144 : 128) | 1);
            if (selectionEnd >= 0) {
                ((EditText) this.mPasswordView).setSelection(selectionEnd);
            }
        } else if (compoundButton.getId() == C0010R$id.wifi_advanced_togglebox) {
            hideSoftKeyboard(this.mView.getWindowToken());
            compoundButton.setVisibility(8);
            this.mView.findViewById(C0010R$id.wifi_advanced_fields).setVisibility(0);
        }
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
        int i2;
        int i3 = 8;
        if (adapterView == this.mSecuritySpinner) {
            this.mAccessPointSecurity = this.mSecurityInPosition[i].intValue();
            if (!this.mWifiManager.isWifiCoverageExtendFeatureEnabled() || !((i2 = this.mAccessPointSecurity) == 0 || i2 == 2)) {
                this.mShareThisWifiCheckBox.setChecked(false);
                this.mShareThisWifiCheckBox.setVisibility(8);
            } else {
                this.mShareThisWifiCheckBox.setVisibility(0);
            }
            showSecurityFields(true, true);
            if (WifiDppUtils.isSupportEnrolleeQrCodeScanner(this.mContext, this.mAccessPointSecurity)) {
                this.mSsidScanButton.setVisibility(0);
            } else {
                this.mSsidScanButton.setVisibility(8);
            }
        } else {
            Spinner spinner = this.mEapMethodSpinner;
            if (adapterView == spinner) {
                showSecurityFields(false, true);
            } else if (adapterView == this.mEapCaCertSpinner) {
                showSecurityFields(false, false);
            } else if (adapterView == this.mPhase2Spinner && spinner.getSelectedItemPosition() == 0) {
                showPeapFields();
            } else if (adapterView == this.mProxySettingsSpinner) {
                showProxyFields();
            } else if (adapterView == this.mHiddenSettingsSpinner) {
                TextView textView = this.mHiddenWarningView;
                if (i != 0) {
                    i3 = 0;
                }
                textView.setVisibility(i3);
            } else {
                showIpConfigFields();
            }
        }
        showWarningMessagesIfAppropriate();
        enableSubmitIfAppropriate();
    }

    public void updatePassword() {
        ((TextView) this.mView.findViewById(C0010R$id.password)).setInputType((((CheckBox) this.mView.findViewById(C0010R$id.show_password)).isChecked() ? 144 : 128) | 1);
    }

    public AccessPoint getAccessPoint() {
        return this.mAccessPoint;
    }

    private boolean isWepPskValid(String str, int i) {
        if (str == null || i <= 0) {
            return false;
        }
        return i == 5 || i == 13 || i == 16 || ((i == 10 || i == 26 || i == 32) && str.matches("[0-9A-Fa-f]*"));
    }

    private void getSIMInfo() {
        String str;
        this.mSubscriptionManager = SubscriptionManager.from(this.mContext);
        for (int i = 0; i < this.mTelephonyManager.getSimCount(); i++) {
            SubscriptionInfo activeSubscriptionInfoForSimSlotIndex = this.mSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(i);
            if (activeSubscriptionInfoForSimSlotIndex != null) {
                str = String.valueOf(activeSubscriptionInfoForSimSlotIndex.getDisplayName());
            } else {
                str = this.mContext.getString(C0017R$string.sim_editor_title, Integer.valueOf(i + 1));
            }
            this.mSimDisplayNames.add(str);
        }
    }

    private void configureSecuritySpinner() {
        int i;
        this.mConfigUi.setTitle(C0017R$string.wifi_add_network);
        TextView textView = (TextView) this.mView.findViewById(C0010R$id.ssid);
        this.mSsidView = textView;
        textView.addTextChangedListener(this);
        Spinner spinner = (Spinner) this.mView.findViewById(C0010R$id.security);
        this.mSecuritySpinner = spinner;
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this.mContext, 17367048, 16908308);
        arrayAdapter.setDropDownViewResource(17367049);
        this.mSecuritySpinner.setAdapter((SpinnerAdapter) arrayAdapter);
        arrayAdapter.add(this.mContext.getString(C0017R$string.wifi_security_none));
        this.mSecurityInPosition[0] = 0;
        if (this.mWifiManager.isEnhancedOpenSupported()) {
            arrayAdapter.add(this.mContext.getString(C0017R$string.wifi_security_owe));
            this.mSecurityInPosition[1] = 4;
            i = 2;
        } else {
            i = 1;
        }
        arrayAdapter.add(this.mContext.getString(C0017R$string.wifi_security_wep));
        int i2 = i + 1;
        this.mSecurityInPosition[i] = 1;
        arrayAdapter.add(this.mContext.getString(C0017R$string.wifi_security_wpa_wpa2));
        int i3 = i2 + 1;
        this.mSecurityInPosition[i2] = 2;
        if (this.mWifiManager.isWpa3SaeSupported()) {
            arrayAdapter.add(this.mContext.getString(C0017R$string.wifi_security_sae));
            this.mSecurityInPosition[i3] = 5;
            i3++;
        }
        arrayAdapter.add(this.mContext.getString(C0017R$string.wifi_security_eap));
        int i4 = i3 + 1;
        this.mSecurityInPosition[i3] = 3;
        if (this.mWifiManager.isWpa3SuiteBSupported()) {
            arrayAdapter.add(this.mContext.getString(C0017R$string.wifi_security_eap_suiteb));
            this.mSecurityInPosition[i4] = 6;
            i4++;
        }
        if (this.mWifiManager.isWapiSupported()) {
            arrayAdapter.add(this.mContext.getString(C0017R$string.wifi_security_wapi_psk));
            this.mSecurityInPosition[i4] = 8;
            arrayAdapter.add(this.mContext.getString(C0017R$string.wifi_security_wapi_cert));
            this.mSecurityInPosition[i4 + 1] = 9;
        }
        arrayAdapter.notifyDataSetChanged();
        this.mView.findViewById(C0010R$id.type).setVisibility(0);
        showIpConfigFields();
        showProxyFields();
        this.mView.findViewById(C0010R$id.wifi_advanced_toggle).setVisibility(0);
        this.mView.findViewById(C0010R$id.hidden_settings_field).setVisibility(0);
        ((CheckBox) this.mView.findViewById(C0010R$id.wifi_advanced_togglebox)).setOnCheckedChangeListener(this);
        setAdvancedOptionAccessibilityString();
    }

    /* access modifiers changed from: package-private */
    public CharSequence[] findAndReplaceTargetStrings(CharSequence[] charSequenceArr, CharSequence[] charSequenceArr2, CharSequence[] charSequenceArr3) {
        if (charSequenceArr2.length != charSequenceArr3.length) {
            return charSequenceArr;
        }
        CharSequence[] charSequenceArr4 = new CharSequence[charSequenceArr.length];
        for (int i = 0; i < charSequenceArr.length; i++) {
            charSequenceArr4[i] = charSequenceArr[i];
            for (int i2 = 0; i2 < charSequenceArr2.length; i2++) {
                if (TextUtils.equals(charSequenceArr[i], charSequenceArr2[i2])) {
                    charSequenceArr4[i] = charSequenceArr3[i2];
                }
            }
        }
        return charSequenceArr4;
    }

    private ArrayAdapter<CharSequence> getSpinnerAdapter(int i) {
        return getSpinnerAdapter(this.mContext.getResources().getStringArray(i));
    }

    private ArrayAdapter<CharSequence> getSpinnerAdapter(String[] strArr) {
        ArrayAdapter<CharSequence> arrayAdapter = new ArrayAdapter<>(this.mContext, 17367048, strArr);
        arrayAdapter.setDropDownViewResource(17367049);
        return arrayAdapter;
    }

    private ArrayAdapter<CharSequence> getSpinnerAdapterWithEapMethodsTts(int i) {
        Resources resources = this.mContext.getResources();
        String[] stringArray = resources.getStringArray(i);
        ArrayAdapter<CharSequence> arrayAdapter = new ArrayAdapter<>(this.mContext, 17367048, createAccessibleEntries(stringArray, findAndReplaceTargetStrings(stringArray, resources.getStringArray(C0003R$array.wifi_eap_method_target_strings), resources.getStringArray(C0003R$array.wifi_eap_method_tts_strings))));
        arrayAdapter.setDropDownViewResource(17367049);
        return arrayAdapter;
    }

    private SpannableString[] createAccessibleEntries(CharSequence[] charSequenceArr, CharSequence[] charSequenceArr2) {
        SpannableString[] spannableStringArr = new SpannableString[charSequenceArr.length];
        for (int i = 0; i < charSequenceArr.length; i++) {
            spannableStringArr[i] = com.android.settings.Utils.createAccessibleSequence(charSequenceArr[i], charSequenceArr2[i].toString());
        }
        return spannableStringArr;
    }

    private void hideSoftKeyboard(IBinder iBinder) {
        ((InputMethodManager) this.mContext.getSystemService(InputMethodManager.class)).hideSoftInputFromWindow(iBinder, 0);
    }

    private void setAdvancedOptionAccessibilityString() {
        ((CheckBox) this.mView.findViewById(C0010R$id.wifi_advanced_togglebox)).setAccessibilityDelegate(new View.AccessibilityDelegate() {
            /* class com.android.settings.wifi.WifiConfigController.AnonymousClass2 */

            public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
                accessibilityNodeInfo.setCheckable(false);
                accessibilityNodeInfo.setClassName(null);
                accessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, WifiConfigController.this.mContext.getString(C0017R$string.wifi_advanced_toggle_description_collapsed)));
            }
        });
    }

    public int getCurSecurity() {
        return this.mAccessPointSecurity;
    }

    public boolean checkWapiParam() {
        String string = this.mConfigUi.getContext().getString(C0017R$string.wifi_unchanged);
        int i = this.mAccessPointSecurity;
        if (i == 9) {
            if (!this.mHaveWapiCert) {
                return false;
            }
        } else if (i == 8) {
            int i2 = WAPI_PSK_TYPE[this.mWapiPskTypeSpinner.getSelectedItemPosition()];
            String charSequence = this.mPasswordView.getText().toString();
            if (string.equals(this.mPasswordView.getHint() != null ? this.mPasswordView.getHint().toString() : "")) {
                return true;
            }
            return checkWapiPassword(charSequence, i2);
        }
        return true;
    }

    private boolean checkWapiPassword(String str, int i) {
        if (str.length() == 0) {
            Toast.makeText(this.mConfigUi.getContext(), C0017R$string.wapi_password_empty, 1).show();
            return false;
        } else if (str.length() < 8 || str.length() > 64) {
            Toast.makeText(this.mConfigUi.getContext(), C0017R$string.wapi_password_length_invalid, 1).show();
            return false;
        } else {
            if (i == 1) {
                int checkHexPasswd = checkHexPasswd(str);
                if (checkHexPasswd == -2) {
                    Toast.makeText(this.mConfigUi.getContext(), C0017R$string.wapi_password_hex_format_invalid, 1).show();
                    return false;
                } else if (checkHexPasswd == -1) {
                    Toast.makeText(this.mConfigUi.getContext(), C0017R$string.wapi_password_hex_length_invalid, 1).show();
                    return false;
                }
            }
            return true;
        }
    }

    private static int checkHexPasswd(String str) {
        if (str.length() % 2 == 1) {
            return -1;
        }
        for (int length = str.length() - 1; length >= 0; length--) {
            char charAt = str.charAt(length);
            if ((charAt < '0' || charAt > '9') && ((charAt < 'A' || charAt > 'F') && (charAt < 'a' || charAt > 'f'))) {
                return -2;
            }
        }
        return 0;
    }
}
