package com.android.settings.network;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.SystemProperties;
import android.provider.Telephony;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.internal.util.ArrayUtils;
import com.android.settings.C0003R$array;
import com.android.settings.C0005R$bool;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settingslib.utils.ThreadUtils;
import com.oneplus.settings.utils.OPUtils;
import com.oneplus.settings.utils.ProductUtils;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ApnEditor extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener, View.OnKeyListener {
    static final int APN_INDEX = 2;
    public static final String[] APN_TYPES = {"default", "mms", "supl", "dun", "hipri", "fota", "ims", "cbs", "ia", "emergency", "mcx", "xcap"};
    static final int CARRIER_ENABLED_INDEX = 17;
    static final int MCC_INDEX = 9;
    static final int MNC_INDEX = 10;
    static final int NAME_INDEX = 1;
    static final int PROTOCOL_INDEX = 16;
    static final int ROAMING_PROTOCOL_INDEX = 20;
    private static final String TAG = ApnEditor.class.getSimpleName();
    static final int TYPE_INDEX = 15;
    static String sNotSet;
    private static final String[] sProjection = {"_id", "name", "apn", "proxy", "port", "user", "server", "password", "mmsc", "mcc", "mnc", "numeric", "mmsproxy", "mmsport", "authtype", "type", "protocol", "carrier_enabled", "bearer", "bearer_bitmask", "roaming_protocol", "mvno_type", "mvno_match_data", "edited", "user_editable", "persistent", "read_only"};
    private static final String[] sUIConfigurableItems = {"name", "apn", "proxy", "port", "user", "server", "password", "mmsc", "mmsproxy", "mmsport", "authtype", "type", "protocol", "carrier_enabled", "bearer", "bearer_bitmask", "roaming_protocol"};
    private String ACTION_FROM = "isFromHM";
    private String ACTION_IS_DATA_CHANGED = "isDataChanged";
    EditTextPreference mApn;
    ApnData mApnData;
    EditTextPreference mApnType;
    ListPreference mAuthType;
    private int mBearerInitialVal = 0;
    MultiSelectListPreference mBearerMulti;
    SwitchPreference mCarrierEnabled;
    private Uri mCarrierUri;
    private String mCurMcc;
    private String mCurMnc;
    String mDefaultApnProtocol;
    String mDefaultApnRoamingProtocol;
    String[] mDefaultApnTypes;
    private boolean mDeletableApn;
    private boolean mIsFromHM = false;
    private boolean mIsPreferenceChanged = false;
    EditTextPreference mMcc;
    EditTextPreference mMmsPort;
    EditTextPreference mMmsProxy;
    EditTextPreference mMmsc;
    EditTextPreference mMnc;
    EditTextPreference mMvnoMatchData;
    private String mMvnoMatchDataStr;
    ListPreference mMvnoType;
    EditTextPreference mName;
    private boolean mNewApn;
    EditTextPreference mPassword;
    EditTextPreference mPort;
    ListPreference mProtocol;
    EditTextPreference mProxy;
    private ProxySubscriptionManager mProxySubscriptionMgr;
    private boolean mReadOnlyApn;
    private String[] mReadOnlyApnFields;
    String[] mReadOnlyApnTypes;
    ListPreference mRoamingProtocol;
    EditTextPreference mServer;
    private int mSubId;
    private TelephonyManager mTelephonyManager;
    EditTextPreference mUser;

    private static boolean bitmaskHasTech(int i, int i2) {
        if (i == 0) {
            return true;
        }
        if (i2 >= 1) {
            return (i & (1 << (i2 - 1))) != 0;
        }
        return false;
    }

    private static int getBitmaskForTech(int i) {
        if (i >= 1) {
            return 1 << (i - 1);
        }
        return 0;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 13;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        Uri uri;
        PersistableBundle configForSubId;
        String str = TAG;
        super.onCreate(bundle);
        ProxySubscriptionManager instance = ProxySubscriptionManager.getInstance(getContext());
        this.mProxySubscriptionMgr = instance;
        instance.setLifecycle(getLifecycle());
        addPreferencesFromResource(C0019R$xml.apn_editor);
        sNotSet = getResources().getString(C0017R$string.apn_not_set);
        this.mName = (EditTextPreference) findPreference("apn_name");
        this.mApn = (EditTextPreference) findPreference("apn_apn");
        this.mProxy = (EditTextPreference) findPreference("apn_http_proxy");
        this.mPort = (EditTextPreference) findPreference("apn_http_port");
        this.mUser = (EditTextPreference) findPreference("apn_user");
        this.mServer = (EditTextPreference) findPreference("apn_server");
        this.mPassword = (EditTextPreference) findPreference("apn_password");
        this.mMmsProxy = (EditTextPreference) findPreference("apn_mms_proxy");
        this.mMmsPort = (EditTextPreference) findPreference("apn_mms_port");
        this.mMmsc = (EditTextPreference) findPreference("apn_mmsc");
        this.mMcc = (EditTextPreference) findPreference("apn_mcc");
        this.mMnc = (EditTextPreference) findPreference("apn_mnc");
        this.mApnType = (EditTextPreference) findPreference("apn_type");
        this.mAuthType = (ListPreference) findPreference("auth_type");
        this.mProtocol = (ListPreference) findPreference("apn_protocol");
        this.mRoamingProtocol = (ListPreference) findPreference("apn_roaming_protocol");
        this.mCarrierEnabled = (SwitchPreference) findPreference("carrier_enabled");
        MultiSelectListPreference multiSelectListPreference = (MultiSelectListPreference) findPreference("bearer_multi");
        this.mBearerMulti = multiSelectListPreference;
        multiSelectListPreference.setPositiveButtonText(C0017R$string.dlg_ok);
        this.mBearerMulti.setNegativeButtonText(C0017R$string.dlg_cancel);
        this.mMvnoType = (ListPreference) findPreference("mvno_type");
        this.mMvnoMatchData = (EditTextPreference) findPreference("mvno_match_data");
        Intent intent = getIntent();
        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            finish();
            return;
        }
        this.mSubId = intent.getIntExtra("sub_id", -1);
        this.mReadOnlyApn = false;
        this.mReadOnlyApnTypes = null;
        this.mReadOnlyApnFields = null;
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) getSystemService("carrier_config");
        if (!(carrierConfigManager == null || (configForSubId = carrierConfigManager.getConfigForSubId(this.mSubId)) == null)) {
            String[] stringArray = configForSubId.getStringArray("read_only_apn_types_string_array");
            this.mReadOnlyApnTypes = stringArray;
            if (!ArrayUtils.isEmpty(stringArray)) {
                Log.d(str, "onCreate: read only APN type: " + Arrays.toString(this.mReadOnlyApnTypes));
            }
            this.mReadOnlyApnFields = configForSubId.getStringArray("read_only_apn_fields_string_array");
            String[] stringArray2 = configForSubId.getStringArray("apn_settings_default_apn_types_string_array");
            this.mDefaultApnTypes = stringArray2;
            if (!ArrayUtils.isEmpty(stringArray2)) {
                Log.d(str, "onCreate: default apn types: " + Arrays.toString(this.mDefaultApnTypes));
            }
            String string = configForSubId.getString("apn.settings_default_protocol_string");
            this.mDefaultApnProtocol = string;
            if (!TextUtils.isEmpty(string)) {
                Log.d(str, "onCreate: default apn protocol: " + this.mDefaultApnProtocol);
            }
            String string2 = configForSubId.getString("apn.settings_default_roaming_protocol_string");
            this.mDefaultApnRoamingProtocol = string2;
            if (!TextUtils.isEmpty(string2)) {
                Log.d(str, "onCreate: default apn roaming protocol: " + this.mDefaultApnRoamingProtocol);
            }
        }
        if (action.equals("android.intent.action.EDIT")) {
            uri = intent.getData();
            if (!uri.isPathPrefixMatch(Telephony.Carriers.CONTENT_URI)) {
                Log.e(str, "Edit request not for carrier table. Uri: " + uri);
                finish();
                return;
            }
        } else if (action.equals("android.intent.action.INSERT")) {
            Uri data = intent.getData();
            this.mCarrierUri = data;
            if (!data.isPathPrefixMatch(Telephony.Carriers.CONTENT_URI)) {
                Log.e(str, "Insert request not for carrier table. Uri: " + this.mCarrierUri);
                finish();
                return;
            }
            this.mNewApn = true;
            intent.getStringExtra("mvno_type");
            this.mMvnoMatchDataStr = intent.getStringExtra("mvno_match_data");
            uri = null;
        } else {
            finish();
            return;
        }
        this.mApnData = null;
        if (uri != null) {
            this.mApnData = getApnDataFromUri(uri);
        }
        if (this.mApnData == null) {
            this.mApnData = new ApnData(sProjection.length);
            if (action.equals("android.intent.action.INSERT")) {
                setDefaultData();
            }
        }
        this.mTelephonyManager = (TelephonyManager) getSystemService("phone");
        boolean z = this.mApnData.getInteger(23, 1).intValue() == 1;
        Log.d(str, "onCreate: EDITED " + z);
        if (!z && (this.mApnData.getInteger(24, 1).intValue() == 0 || apnTypesMatch(this.mReadOnlyApnTypes, this.mApnData.getString(15)) || this.mApnData.getInteger(26).intValue() == 1)) {
            Log.d(str, "onCreate: apnTypesMatch; read-only APN");
            this.mReadOnlyApn = true;
            disableAllFields();
        } else if (!ArrayUtils.isEmpty(this.mReadOnlyApnFields)) {
            disableFields(this.mReadOnlyApnFields);
        }
        this.mDeletableApn = this.mApnData.getInteger(25, 0).intValue() != 1;
        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
            getPreferenceScreen().getPreference(i).setOnPreferenceChangeListener(this);
        }
        if (OPUtils.isSupportUss()) {
            boolean booleanExtra = intent.getBooleanExtra(this.ACTION_FROM, false);
            this.mIsFromHM = booleanExtra;
            if (booleanExtra) {
                this.mReadOnlyApn = false;
                disableAllFields();
                this.mApn.setEnabled(true);
                this.mProtocol.setEnabled(true);
            }
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewStateRestored(Bundle bundle) {
        super.onViewStateRestored(bundle);
        fillUI(bundle == null);
    }

    static String formatInteger(String str) {
        try {
            int parseInt = Integer.parseInt(str);
            return String.format(getCorrectDigitsFormat(str), Integer.valueOf(parseInt));
        } catch (NumberFormatException unused) {
            return str;
        }
    }

    static String getCorrectDigitsFormat(String str) {
        return str.length() == 2 ? "%02d" : "%03d";
    }

    static boolean hasAllApns(String[] strArr) {
        String str = TAG;
        if (ArrayUtils.isEmpty(strArr)) {
            return false;
        }
        List asList = Arrays.asList(strArr);
        if (asList.contains("*")) {
            Log.d(str, "hasAllApns: true because apnList.contains(APN_TYPE_ALL)");
            return true;
        }
        for (String str2 : APN_TYPES) {
            if (!asList.contains(str2)) {
                return false;
            }
        }
        Log.d(str, "hasAllApns: true");
        return true;
    }

    private boolean apnTypesMatch(String[] strArr, String str) {
        String str2 = TAG;
        if (ArrayUtils.isEmpty(strArr)) {
            return false;
        }
        if (hasAllApns(strArr) || TextUtils.isEmpty(str)) {
            return true;
        }
        List asList = Arrays.asList(strArr);
        String[] split = str.split(",");
        for (String str3 : split) {
            if (asList.contains(str3.trim())) {
                Log.d(str2, "apnTypesMatch: true because match found for " + str3.trim());
                return true;
            }
        }
        Log.d(str2, "apnTypesMatch: false");
        return false;
    }

    /* JADX INFO: Can't fix incorrect switch cases order, some code will duplicate */
    private Preference getPreferenceFromFieldName(String str) {
        char c;
        switch (str.hashCode()) {
            case -2135515857:
                if (str.equals("mvno_type")) {
                    c = 19;
                    break;
                }
                c = 65535;
                break;
            case -1954254981:
                if (str.equals("mmsproxy")) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case -1640523526:
                if (str.equals("carrier_enabled")) {
                    c = 16;
                    break;
                }
                c = 65535;
                break;
            case -1393032351:
                if (str.equals("bearer")) {
                    c = 17;
                    break;
                }
                c = 65535;
                break;
            case -1230508389:
                if (str.equals("bearer_bitmask")) {
                    c = 18;
                    break;
                }
                c = 65535;
                break;
            case -1039601666:
                if (str.equals("roaming_protocol")) {
                    c = 15;
                    break;
                }
                c = 65535;
                break;
            case -989163880:
                if (str.equals("protocol")) {
                    c = 14;
                    break;
                }
                c = 65535;
                break;
            case -905826493:
                if (str.equals("server")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case -520149991:
                if (str.equals("mvno_match_data")) {
                    c = 20;
                    break;
                }
                c = 65535;
                break;
            case 96799:
                if (str.equals("apn")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 107917:
                if (str.equals("mcc")) {
                    c = '\n';
                    break;
                }
                c = 65535;
                break;
            case 108258:
                if (str.equals("mnc")) {
                    c = 11;
                    break;
                }
                c = 65535;
                break;
            case 3355632:
                if (str.equals("mmsc")) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            case 3373707:
                if (str.equals("name")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 3446913:
                if (str.equals("port")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 3575610:
                if (str.equals("type")) {
                    c = '\f';
                    break;
                }
                c = 65535;
                break;
            case 3599307:
                if (str.equals("user")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 106941038:
                if (str.equals("proxy")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 1183882708:
                if (str.equals("mmsport")) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case 1216985755:
                if (str.equals("password")) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 1433229538:
                if (str.equals("authtype")) {
                    c = '\r';
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return this.mName;
            case 1:
                return this.mApn;
            case 2:
                return this.mProxy;
            case 3:
                return this.mPort;
            case 4:
                return this.mUser;
            case 5:
                return this.mServer;
            case 6:
                return this.mPassword;
            case 7:
                return this.mMmsProxy;
            case '\b':
                return this.mMmsPort;
            case '\t':
                return this.mMmsc;
            case '\n':
                return this.mMcc;
            case 11:
                return this.mMnc;
            case '\f':
                return this.mApnType;
            case '\r':
                return this.mAuthType;
            case 14:
                return this.mProtocol;
            case 15:
                return this.mRoamingProtocol;
            case 16:
                return this.mCarrierEnabled;
            case 17:
            case 18:
                return this.mBearerMulti;
            case 19:
                return this.mMvnoType;
            case 20:
                return this.mMvnoMatchData;
            default:
                return null;
        }
    }

    private void disableFields(String[] strArr) {
        for (String str : strArr) {
            Preference preferenceFromFieldName = getPreferenceFromFieldName(str);
            if (preferenceFromFieldName != null) {
                preferenceFromFieldName.setEnabled(false);
            }
        }
    }

    private void disableAllFields() {
        this.mName.setEnabled(false);
        this.mApn.setEnabled(false);
        this.mProxy.setEnabled(false);
        this.mPort.setEnabled(false);
        this.mUser.setEnabled(false);
        this.mServer.setEnabled(false);
        this.mPassword.setEnabled(false);
        this.mMmsProxy.setEnabled(false);
        this.mMmsPort.setEnabled(false);
        this.mMmsc.setEnabled(false);
        this.mMcc.setEnabled(false);
        this.mMnc.setEnabled(false);
        this.mApnType.setEnabled(false);
        this.mAuthType.setEnabled(false);
        this.mProtocol.setEnabled(false);
        this.mRoamingProtocol.setEnabled(false);
        this.mCarrierEnabled.setEnabled(false);
        this.mBearerMulti.setEnabled(false);
        this.mMvnoType.setEnabled(false);
        this.mMvnoMatchData.setEnabled(false);
    }

    private boolean isSprintMccMnc(String str) {
        if (str == null) {
            return false;
        }
        if (str.equals("310120") || str.equals("311870") || str.equals("311490") || str.equals("312530") || str.equals("310000")) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void fillUI(boolean z) {
        String str;
        String str2;
        if (z) {
            this.mName.setText(this.mApnData.getString(1));
            this.mApn.setText(this.mApnData.getString(2));
            this.mProxy.setText(this.mApnData.getString(3));
            this.mPort.setText(this.mApnData.getString(4));
            this.mUser.setText(this.mApnData.getString(5));
            this.mServer.setText(this.mApnData.getString(6));
            this.mPassword.setText(this.mApnData.getString(7));
            this.mMmsProxy.setText(this.mApnData.getString(12));
            this.mMmsPort.setText(this.mApnData.getString(13));
            this.mMmsc.setText(this.mApnData.getString(8));
            this.mMcc.setText(this.mApnData.getString(9));
            this.mMnc.setText(this.mApnData.getString(10));
            this.mApnType.setText(this.mApnData.getString(15));
            if (this.mNewApn) {
                SubscriptionInfo accessibleSubscriptionInfo = this.mProxySubscriptionMgr.getAccessibleSubscriptionInfo(this.mSubId);
                if (accessibleSubscriptionInfo == null) {
                    str = null;
                } else {
                    str = accessibleSubscriptionInfo.getMccString();
                }
                if (accessibleSubscriptionInfo == null) {
                    str2 = null;
                } else {
                    str2 = accessibleSubscriptionInfo.getMncString();
                }
                String simOperatorNumeric = this.mTelephonyManager.getSimOperatorNumeric(this.mSubId);
                if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str)) {
                    this.mMcc.setText(str);
                    this.mMnc.setText(str2);
                    this.mCurMnc = str2;
                    this.mCurMcc = str;
                }
                if (simOperatorNumeric == null || simOperatorNumeric.length() <= 4 || !"460".equals(simOperatorNumeric.substring(0, 3))) {
                    this.mApnType.setText("default");
                } else {
                    this.mApnType.setText("default,supl");
                }
            }
            int intValue = this.mApnData.getInteger(14, -1).intValue();
            if (intValue != -1) {
                this.mAuthType.setValueIndex(intValue);
            } else {
                this.mAuthType.setValue(null);
            }
            this.mProtocol.setValue(this.mApnData.getString(16));
            this.mRoamingProtocol.setValue(this.mApnData.getString(20));
            this.mCarrierEnabled.setChecked(this.mApnData.getInteger(17, 1).intValue() == 1);
            this.mBearerInitialVal = this.mApnData.getInteger(18, 0).intValue();
            HashSet hashSet = new HashSet();
            int intValue2 = this.mApnData.getInteger(19, 0).intValue();
            if (intValue2 != 0) {
                int i = 1;
                while (intValue2 != 0) {
                    if ((intValue2 & 1) == 1) {
                        hashSet.add("" + i);
                    }
                    intValue2 >>= 1;
                    i++;
                }
            } else if (this.mBearerInitialVal == 0) {
                hashSet.add("0");
            }
            if (this.mBearerInitialVal != 0) {
                if (!hashSet.contains("" + this.mBearerInitialVal)) {
                    hashSet.add("" + this.mBearerInitialVal);
                }
            }
            this.mBearerMulti.setValues(hashSet);
            this.mMvnoType.setValue(this.mApnData.getString(21));
            this.mMvnoMatchData.setEnabled(false);
            this.mMvnoMatchData.setText(this.mApnData.getString(22));
            String localizedName = Utils.getLocalizedName(getActivity(), this.mApnData.getString(1));
            if (!TextUtils.isEmpty(localizedName)) {
                this.mName.setText(localizedName);
            }
        }
        EditTextPreference editTextPreference = this.mName;
        editTextPreference.setSummary(checkNull(editTextPreference.getText()));
        EditTextPreference editTextPreference2 = this.mApn;
        editTextPreference2.setSummary(checkNull(editTextPreference2.getText()));
        EditTextPreference editTextPreference3 = this.mProxy;
        editTextPreference3.setSummary(checkNull(editTextPreference3.getText()));
        EditTextPreference editTextPreference4 = this.mPort;
        editTextPreference4.setSummary(checkNull(editTextPreference4.getText()));
        EditTextPreference editTextPreference5 = this.mUser;
        editTextPreference5.setSummary(checkNull(editTextPreference5.getText()));
        EditTextPreference editTextPreference6 = this.mServer;
        editTextPreference6.setSummary(checkNull(editTextPreference6.getText()));
        EditTextPreference editTextPreference7 = this.mPassword;
        editTextPreference7.setSummary(starify(editTextPreference7.getText()));
        EditTextPreference editTextPreference8 = this.mMmsProxy;
        editTextPreference8.setSummary(checkNull(editTextPreference8.getText()));
        EditTextPreference editTextPreference9 = this.mMmsPort;
        editTextPreference9.setSummary(checkNull(editTextPreference9.getText()));
        EditTextPreference editTextPreference10 = this.mMmsc;
        editTextPreference10.setSummary(checkNull(editTextPreference10.getText()));
        EditTextPreference editTextPreference11 = this.mMcc;
        editTextPreference11.setSummary(formatInteger(checkNull(editTextPreference11.getText())));
        EditTextPreference editTextPreference12 = this.mMnc;
        editTextPreference12.setSummary(formatInteger(checkNull(editTextPreference12.getText())));
        EditTextPreference editTextPreference13 = this.mApnType;
        editTextPreference13.setSummary(checkApnType(editTextPreference13.getText()));
        String value = this.mAuthType.getValue();
        if (value != null) {
            int parseInt = Integer.parseInt(value);
            this.mAuthType.setValueIndex(parseInt);
            this.mAuthType.setSummary(getResources().getStringArray(C0003R$array.apn_auth_entries)[parseInt]);
        } else {
            this.mAuthType.setSummary(sNotSet);
        }
        ListPreference listPreference = this.mProtocol;
        listPreference.setSummary(checkNull(protocolDescription(listPreference.getValue(), this.mProtocol)));
        ListPreference listPreference2 = this.mRoamingProtocol;
        listPreference2.setSummary(checkNull(protocolDescription(listPreference2.getValue(), this.mRoamingProtocol)));
        MultiSelectListPreference multiSelectListPreference = this.mBearerMulti;
        multiSelectListPreference.setSummary(checkNull(bearerMultiDescription(multiSelectListPreference.getValues())));
        ListPreference listPreference3 = this.mMvnoType;
        listPreference3.setSummary(checkNull(mvnoDescription(listPreference3.getValue())));
        EditTextPreference editTextPreference14 = this.mMvnoMatchData;
        editTextPreference14.setSummary(checkNull(editTextPreference14.getText()));
        if (getResources().getBoolean(C0005R$bool.config_allow_edit_carrier_enabled)) {
            this.mCarrierEnabled.setEnabled(true);
        } else {
            this.mCarrierEnabled.setEnabled(false);
        }
    }

    private String protocolDescription(String str, ListPreference listPreference) {
        int findIndexOfValue = listPreference.findIndexOfValue(str);
        if (findIndexOfValue == -1) {
            return null;
        }
        try {
            return getResources().getStringArray(C0003R$array.apn_protocol_entries)[findIndexOfValue];
        } catch (ArrayIndexOutOfBoundsException unused) {
            return null;
        }
    }

    private String bearerMultiDescription(Set<String> set) {
        String[] stringArray = getResources().getStringArray(C0003R$array.bearer_entries);
        StringBuilder sb = new StringBuilder();
        boolean z = true;
        for (String str : set) {
            int findIndexOfValue = this.mBearerMulti.findIndexOfValue(str);
            if (z) {
                try {
                    sb.append(stringArray[findIndexOfValue]);
                    z = false;
                } catch (ArrayIndexOutOfBoundsException unused) {
                }
            } else {
                sb.append(", " + stringArray[findIndexOfValue]);
            }
        }
        String sb2 = sb.toString();
        if (!TextUtils.isEmpty(sb2)) {
            return sb2;
        }
        return null;
    }

    private String mvnoDescription(String str) {
        String str2;
        String[] strArr;
        int findIndexOfValue = this.mMvnoType.findIndexOfValue(str);
        String value = this.mMvnoType.getValue();
        if (findIndexOfValue == -1) {
            return null;
        }
        String[] stringArray = getResources().getStringArray(C0003R$array.mvno_type_entries);
        boolean z = false;
        boolean z2 = this.mReadOnlyApn || ((strArr = this.mReadOnlyApnFields) != null && Arrays.asList(strArr).contains("mvno_match_data"));
        EditTextPreference editTextPreference = this.mMvnoMatchData;
        if (!z2 && findIndexOfValue != 0) {
            z = true;
        }
        editTextPreference.setEnabled(z);
        if (str != null && !str.equals(value)) {
            if (stringArray[findIndexOfValue].equals("SPN")) {
                TelephonyManager telephonyManager = (TelephonyManager) getContext().getSystemService(TelephonyManager.class);
                TelephonyManager createForSubscriptionId = telephonyManager.createForSubscriptionId(this.mSubId);
                if (createForSubscriptionId != null) {
                    telephonyManager = createForSubscriptionId;
                }
                this.mMvnoMatchData.setText(telephonyManager.getSimOperatorName());
            } else if (stringArray[findIndexOfValue].equals("IMSI")) {
                SubscriptionInfo accessibleSubscriptionInfo = this.mProxySubscriptionMgr.getAccessibleSubscriptionInfo(this.mSubId);
                String str3 = "";
                if (accessibleSubscriptionInfo == null) {
                    str2 = str3;
                } else {
                    str2 = Objects.toString(accessibleSubscriptionInfo.getMccString(), str3);
                }
                if (accessibleSubscriptionInfo != null) {
                    str3 = Objects.toString(accessibleSubscriptionInfo.getMncString(), str3);
                }
                String str4 = str2 + str3;
                if (OPUtils.isSupportUss() && isSprintMccMnc(str4)) {
                    str4 = this.mTelephonyManager.getSimOperator();
                }
                this.mMvnoMatchData.setText(str4 + "x");
            } else if (stringArray[findIndexOfValue].equals("GID")) {
                TelephonyManager telephonyManager2 = (TelephonyManager) getContext().getSystemService(TelephonyManager.class);
                TelephonyManager createForSubscriptionId2 = telephonyManager2.createForSubscriptionId(this.mSubId);
                if (createForSubscriptionId2 != null) {
                    telephonyManager2 = createForSubscriptionId2;
                }
                this.mMvnoMatchData.setText(telephonyManager2.getGroupIdLevel1());
            } else if (stringArray[findIndexOfValue].equals("ICCID") && this.mMvnoMatchDataStr != null) {
                Log.d(TAG, "mMvnoMatchDataStr: " + this.mMvnoMatchDataStr);
                this.mMvnoMatchData.setText(this.mMvnoMatchDataStr);
            }
        }
        try {
            return stringArray[findIndexOfValue];
        } catch (ArrayIndexOutOfBoundsException unused) {
            return null;
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        String key = preference.getKey();
        if ("auth_type".equals(key)) {
            try {
                int parseInt = Integer.parseInt((String) obj);
                this.mAuthType.setValueIndex(parseInt);
                this.mAuthType.setSummary(getResources().getStringArray(C0003R$array.apn_auth_entries)[parseInt]);
                return true;
            } catch (NumberFormatException unused) {
                return false;
            }
        } else if ("apn_protocol".equals(key)) {
            String str = (String) obj;
            String protocolDescription = protocolDescription(str, this.mProtocol);
            if (protocolDescription == null) {
                return false;
            }
            this.mProtocol.setSummary(protocolDescription);
            this.mProtocol.setValue(str);
            return true;
        } else if ("apn_roaming_protocol".equals(key)) {
            String str2 = (String) obj;
            String protocolDescription2 = protocolDescription(str2, this.mRoamingProtocol);
            if (protocolDescription2 == null) {
                return false;
            }
            this.mRoamingProtocol.setSummary(protocolDescription2);
            this.mRoamingProtocol.setValue(str2);
            return true;
        } else if ("bearer_multi".equals(key)) {
            Set<String> set = (Set) obj;
            String bearerMultiDescription = bearerMultiDescription(set);
            if (bearerMultiDescription == null) {
                return false;
            }
            this.mBearerMulti.setValues(set);
            this.mBearerMulti.setSummary(bearerMultiDescription);
            return true;
        } else if ("mvno_type".equals(key)) {
            String str3 = (String) obj;
            String mvnoDescription = mvnoDescription(str3);
            if (mvnoDescription == null) {
                return false;
            }
            this.mMvnoType.setValue(str3);
            this.mMvnoType.setSummary(mvnoDescription);
            return true;
        } else if ("apn_password".equals(key)) {
            this.mPassword.setSummary(starify(obj != null ? String.valueOf(obj) : ""));
            return true;
        } else if ("carrier_enabled".equals(key)) {
            return true;
        } else {
            preference.setSummary(checkNull(obj != null ? String.valueOf(obj) : null));
            return true;
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        if (!this.mNewApn && !this.mReadOnlyApn && this.mDeletableApn && !ProductUtils.isUsvMode()) {
            menu.add(0, 1, 0, C0017R$string.menu_delete).setIcon(C0008R$drawable.ic_delete);
        }
        menu.add(0, 2, 0, C0017R$string.menu_save).setIcon(17301582);
        menu.add(0, 3, 0, C0017R$string.menu_cancel).setIcon(17301560);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 1) {
            deleteApn();
            apnEditorFinish();
            return true;
        } else if (itemId == 2) {
            if (validateAndSaveApnData()) {
                apnEditorFinish();
            }
            return true;
        } else if (itemId != 3) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            finish();
            return true;
        }
    }

    private void apnEditorFinish() {
        if (OPUtils.isSupportUss() && this.mIsFromHM) {
            Intent intent = new Intent();
            intent.putExtra(this.ACTION_IS_DATA_CHANGED, this.mIsPreferenceChanged);
            setResult(0, intent);
        }
        finish();
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        view.setOnKeyListener(this);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
    }

    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (keyEvent.getAction() != 0 || i != 4) {
            return false;
        }
        if (!validateAndSaveApnData()) {
            return true;
        }
        apnEditorFinish();
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean setStringValueAndCheckIfDiff(ContentValues contentValues, String str, String str2, boolean z, int i) {
        String string = this.mApnData.getString(i);
        boolean z2 = z || ((!TextUtils.isEmpty(str2) || !TextUtils.isEmpty(string)) && (str2 == null || !str2.equals(string)));
        if (z2 && str2 != null) {
            contentValues.put(str, str2);
        }
        return z2;
    }

    /* access modifiers changed from: package-private */
    public boolean setIntValueAndCheckIfDiff(ContentValues contentValues, String str, int i, boolean z, int i2) {
        boolean z2 = z || i != this.mApnData.getInteger(i2).intValue();
        if (z2) {
            contentValues.put(str, Integer.valueOf(i));
        }
        return z2;
    }

    /* access modifiers changed from: package-private */
    public boolean validateAndSaveApnData() {
        int i;
        int i2;
        if (this.mReadOnlyApn) {
            return true;
        }
        String checkNotSet = checkNotSet(this.mName.getText());
        String checkNotSet2 = checkNotSet(this.mApn.getText());
        String checkNotSet3 = checkNotSet(this.mMcc.getText());
        String checkNotSet4 = checkNotSet(this.mMnc.getText());
        updateApnTypeWithSameApn();
        if (validateApnData() != null) {
            showError();
            return false;
        }
        ContentValues contentValues = new ContentValues();
        boolean stringValueAndCheckIfDiff = setStringValueAndCheckIfDiff(contentValues, "mmsc", checkNotSet(this.mMmsc.getText()), setStringValueAndCheckIfDiff(contentValues, "password", checkNotSet(this.mPassword.getText()), setStringValueAndCheckIfDiff(contentValues, "server", checkNotSet(this.mServer.getText()), setStringValueAndCheckIfDiff(contentValues, "user", checkNotSet(this.mUser.getText()), setStringValueAndCheckIfDiff(contentValues, "mmsport", checkNotSet(this.mMmsPort.getText()), setStringValueAndCheckIfDiff(contentValues, "mmsproxy", checkNotSet(this.mMmsProxy.getText()), setStringValueAndCheckIfDiff(contentValues, "port", checkNotSet(this.mPort.getText()), setStringValueAndCheckIfDiff(contentValues, "proxy", checkNotSet(this.mProxy.getText()), setStringValueAndCheckIfDiff(contentValues, "apn", checkNotSet2, setStringValueAndCheckIfDiff(contentValues, "name", checkNotSet, this.mNewApn, 1), 2), 3), 4), 12), 13), 5), 6), 7), 8);
        String value = this.mAuthType.getValue();
        if (value != null) {
            stringValueAndCheckIfDiff = setIntValueAndCheckIfDiff(contentValues, "authtype", Integer.parseInt(value), stringValueAndCheckIfDiff, 14);
        }
        boolean stringValueAndCheckIfDiff2 = setStringValueAndCheckIfDiff(contentValues, "mnc", checkNotSet4, setStringValueAndCheckIfDiff(contentValues, "mcc", checkNotSet3, setStringValueAndCheckIfDiff(contentValues, "type", checkApnType(getUserEnteredApnType()), setStringValueAndCheckIfDiff(contentValues, "roaming_protocol", getUserEnteredApnProtocol(this.mRoamingProtocol, this.mDefaultApnRoamingProtocol), setStringValueAndCheckIfDiff(contentValues, "protocol", getUserEnteredApnProtocol(this.mProtocol, this.mDefaultApnProtocol), stringValueAndCheckIfDiff, 16), 20), 15), 9), 10);
        contentValues.put("numeric", checkNotSet3 + checkNotSet4);
        String str = this.mCurMnc;
        if (str != null && this.mCurMcc != null && str.equals(checkNotSet4) && this.mCurMcc.equals(checkNotSet3)) {
            contentValues.put("current", (Integer) 1);
        }
        Iterator<String> it = this.mBearerMulti.getValues().iterator();
        int i3 = 0;
        while (true) {
            if (!it.hasNext()) {
                i = i3;
                break;
            }
            String next = it.next();
            if (Integer.parseInt(next) == 0) {
                i = 0;
                break;
            }
            i3 |= getBitmaskForTech(Integer.parseInt(next));
        }
        boolean intValueAndCheckIfDiff = setIntValueAndCheckIfDiff(contentValues, "carrier_enabled", this.mCarrierEnabled.isChecked() ? 1 : 0, setStringValueAndCheckIfDiff(contentValues, "mvno_match_data", checkNotSet(this.mMvnoMatchData.getText()), setStringValueAndCheckIfDiff(contentValues, "mvno_type", checkNotSet(this.mMvnoType.getValue()), setIntValueAndCheckIfDiff(contentValues, "bearer", (i == 0 || (i2 = this.mBearerInitialVal) == 0 || !bitmaskHasTech(i, i2)) ? 0 : this.mBearerInitialVal, setIntValueAndCheckIfDiff(contentValues, "bearer_bitmask", i, stringValueAndCheckIfDiff2, 19), 18), 21), 22), 17);
        contentValues.put("edited", (Integer) 1);
        if (intValueAndCheckIfDiff) {
            updateApnDataToDatabase(this.mApnData.getUri() == null ? this.mCarrierUri : this.mApnData.getUri(), contentValues);
            if (OPUtils.isSupportUss() && this.mIsFromHM) {
                this.mIsPreferenceChanged = true;
            }
        }
        return true;
    }

    private void updateApnDataToDatabase(Uri uri, ContentValues contentValues) {
        ThreadUtils.postOnBackgroundThread(new Runnable(uri, contentValues) {
            /* class com.android.settings.network.$$Lambda$ApnEditor$1vSLgWOnd4pMuFU2qFaSz0HXNw8 */
            public final /* synthetic */ Uri f$1;
            public final /* synthetic */ ContentValues f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ApnEditor.this.lambda$updateApnDataToDatabase$0$ApnEditor(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateApnDataToDatabase$0 */
    public /* synthetic */ void lambda$updateApnDataToDatabase$0$ApnEditor(Uri uri, ContentValues contentValues) {
        String str = TAG;
        Log.d(str, "postOnBackgroundThread updateApnDataToDatabase start");
        if (!uri.equals(this.mCarrierUri)) {
            getContentResolver().update(uri, contentValues, null, null);
        } else if (getContentResolver().insert(this.mCarrierUri, contentValues) == null) {
            Log.e(str, "Can't add a new apn to database " + this.mCarrierUri);
        }
        Log.d(str, "postOnBackgroundThread updateApnDataToDatabase end");
    }

    private void updateApnTypeWithSameApn() {
        String str = TAG;
        if (this.mReadOnlyApn || SystemProperties.get("ro.build.release_type", "").equals("cta")) {
            Log.d(str, "skip updateApnTypeWithSameApn ");
            return;
        }
        String text = this.mApn.getText();
        String simOperatorNumeric = this.mTelephonyManager.getSimOperatorNumeric(this.mSubId);
        if (text == null || simOperatorNumeric == null) {
            Log.d(str, "updateApnTypeWithSameApn: NULL apn or numeric");
            return;
        }
        String text2 = this.mApnType.getText();
        StringBuilder sb = new StringBuilder(" apn=\"" + text + "\" AND numeric=\"" + simOperatorNumeric + "\"");
        StringBuilder sb2 = new StringBuilder();
        sb2.append("updateApnTypeWithSameApn: apnType = ");
        sb2.append(text2);
        Log.d(str, sb2.toString());
        Log.d(str, "updateApnTypeWithSameApn: where = " + sb.toString());
        try {
            Uri uri = this.mApnData.getUri() == null ? this.mCarrierUri : this.mApnData.getUri();
            Log.d(str, "updateApnTypeWithSameApn: uri = " + uri);
            Cursor query = getContentResolver().query(uri, new String[]{"type"}, sb.toString(), null, "name ASC");
            if (query.getCount() > 0) {
                query.moveToFirst();
                String string = query.getString(0);
                if (!TextUtils.isEmpty(string)) {
                    this.mApnType.setText(string);
                }
                Log.d(str, "updateApnTypeWithSameApn: update apnType to " + string);
            }
            query.close();
        } catch (Exception e) {
            Log.e(str, "updateApnTypeWithSameApn: ex = " + e);
        }
    }

    /* access modifiers changed from: package-private */
    public String validateApnData() {
        String str;
        String checkNotSet = checkNotSet(this.mName.getText());
        String checkNotSet2 = checkNotSet(this.mApn.getText());
        String checkNotSet3 = checkNotSet(this.mMcc.getText());
        String checkNotSet4 = checkNotSet(this.mMnc.getText());
        String checkNotSet5 = checkNotSet(this.mPort.getText());
        String checkNotSet6 = checkNotSet(this.mMmsPort.getText());
        if (TextUtils.isEmpty(checkNotSet)) {
            str = getResources().getString(C0017R$string.error_name_empty);
        } else if (TextUtils.isEmpty(checkNotSet2)) {
            str = getResources().getString(C0017R$string.error_apn_empty);
        } else if (checkNotSet3 == null || checkNotSet3.length() != 3) {
            str = getResources().getString(C0017R$string.error_mcc_not3);
        } else {
            str = (checkNotSet4 == null || (checkNotSet4.length() & 65534) != 2) ? getResources().getString(C0017R$string.error_mnc_not23) : (TextUtils.isEmpty(checkNotSet5) || Integer.parseInt(checkNotSet5) <= 65535) ? (TextUtils.isEmpty(checkNotSet6) || Integer.parseInt(checkNotSet6) <= 65535) ? null : getResources().getString(C0017R$string.oneplus_error_mms_port_exceed) : getResources().getString(C0017R$string.oneplus_error_port_exceed);
        }
        if ((OPUtils.isSupportUss() && this.mIsFromHM) || str != null || ArrayUtils.isEmpty(this.mReadOnlyApnTypes) || !apnTypesMatch(this.mReadOnlyApnTypes, getUserEnteredApnType())) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        String[] strArr = this.mReadOnlyApnTypes;
        for (String str2 : strArr) {
            sb.append(str2);
            sb.append(", ");
            Log.d(TAG, "validateApnData: appending type: " + str2);
        }
        if (sb.length() >= 2) {
            sb.delete(sb.length() - 2, sb.length());
        }
        return String.format(getResources().getString(C0017R$string.error_adding_apn_type), sb);
    }

    /* access modifiers changed from: package-private */
    public void showError() {
        ErrorDialog.showError(this);
    }

    private void deleteApn() {
        if (this.mApnData.getUri() != null) {
            getContentResolver().delete(this.mApnData.getUri(), null, null);
            this.mApnData = new ApnData(sProjection.length);
        }
    }

    private String starify(String str) {
        if (str == null || str.length() == 0) {
            return sNotSet;
        }
        int length = str.length();
        char[] cArr = new char[length];
        for (int i = 0; i < length; i++) {
            cArr[i] = '*';
        }
        return new String(cArr);
    }

    private String checkNull(String str) {
        return TextUtils.isEmpty(str) ? sNotSet : str;
    }

    private String checkNotSet(String str) {
        if (sNotSet.equals(str)) {
            return null;
        }
        return str;
    }

    private String checkApnType(String str) {
        return (str == null || str.length() == 0) ? "default" : str;
    }

    private void setDefaultData() {
        PersistableBundle configForSubId;
        PersistableBundle persistableBundle;
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) getSystemService("carrier_config");
        if (!(carrierConfigManager == null || (configForSubId = carrierConfigManager.getConfigForSubId(this.mSubId)) == null || (persistableBundle = configForSubId.getPersistableBundle("apn_default_values_strings_array")) == null || persistableBundle.isEmpty())) {
            for (String str : persistableBundle.keySet()) {
                if (fieldValidate(str)) {
                    setAppData(str, persistableBundle.get(str));
                }
            }
        }
    }

    private void setAppData(String str, Object obj) {
        int findIndexOfKey = findIndexOfKey(str);
        if (findIndexOfKey >= 0) {
            this.mApnData.setObject(findIndexOfKey, obj);
        }
    }

    private int findIndexOfKey(String str) {
        int i = 0;
        while (true) {
            String[] strArr = sProjection;
            if (i >= strArr.length) {
                return -1;
            }
            if (strArr[i].equals(str)) {
                return i;
            }
            i++;
        }
    }

    private boolean fieldValidate(String str) {
        for (String str2 : sUIConfigurableItems) {
            if (str2.equalsIgnoreCase(str)) {
                return true;
            }
        }
        Log.w(TAG, str + " is not configurable");
        return false;
    }

    /* access modifiers changed from: package-private */
    public String getUserEnteredApnProtocol(ListPreference listPreference, String str) {
        String checkNotSet = checkNotSet(listPreference == null ? null : listPreference.getValue());
        if (TextUtils.isEmpty(checkNotSet)) {
            return str;
        }
        return checkNotSet.trim();
    }

    /* access modifiers changed from: package-private */
    public String getUserEnteredApnType() {
        String text = this.mApnType.getText();
        if (text != null) {
            text = text.trim();
        }
        if (!(TextUtils.isEmpty(text) || "*".equals(text)) || ArrayUtils.isEmpty(this.mReadOnlyApnTypes)) {
            return text;
        }
        String[] strArr = APN_TYPES;
        if (TextUtils.isEmpty(text) && !ArrayUtils.isEmpty(this.mDefaultApnTypes)) {
            strArr = this.mDefaultApnTypes;
        }
        StringBuilder sb = new StringBuilder();
        List asList = Arrays.asList(this.mReadOnlyApnTypes);
        boolean z = true;
        for (String str : strArr) {
            if (!asList.contains(str) && !str.equals("ia") && !str.equals("emergency") && !str.equals("mcx")) {
                if (z) {
                    z = false;
                } else {
                    sb.append(",");
                }
                sb.append(str);
            }
        }
        String sb2 = sb.toString();
        Log.d(TAG, "getUserEnteredApnType: changed apn type to editable apn types: " + sb2);
        return sb2;
    }

    public static class ErrorDialog extends InstrumentedDialogFragment {
        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 530;
        }

        public static void showError(ApnEditor apnEditor) {
            ErrorDialog errorDialog = new ErrorDialog();
            errorDialog.setTargetFragment(apnEditor, 0);
            errorDialog.show(apnEditor.getFragmentManager(), "error");
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            String validateApnData = ((ApnEditor) getTargetFragment()).validateApnData();
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(C0017R$string.error_title);
            builder.setPositiveButton(17039370, (DialogInterface.OnClickListener) null);
            builder.setMessage(validateApnData);
            return builder.create();
        }
    }

    /* access modifiers changed from: package-private */
    public ApnData getApnDataFromUri(Uri uri) {
        ApnData apnData;
        String str = TAG;
        ApnData apnData2 = null;
        try {
            Cursor query = getContentResolver().query(uri, sProjection, null, null, null);
            if (query != null) {
                try {
                    query.moveToFirst();
                    apnData = new ApnData(uri, query);
                } catch (Throwable th) {
                    th.addSuppressed(th);
                }
            } else {
                apnData = null;
            }
            if (query != null) {
                query.close();
            }
            apnData2 = apnData;
        } catch (Exception e) {
            Log.d(str, "getApnDataFromUri exception" + e);
        }
        if (apnData2 == null) {
            Log.d(str, "Can't get apnData from Uri " + uri);
        }
        return apnData2;
        throw th;
    }

    /* access modifiers changed from: package-private */
    public static class ApnData {
        Object[] mData;
        Uri mUri;

        ApnData(int i) {
            this.mData = new Object[i];
        }

        ApnData(Uri uri, Cursor cursor) {
            this.mUri = uri;
            this.mData = new Object[cursor.getColumnCount()];
            for (int i = 0; i < this.mData.length; i++) {
                int type = cursor.getType(i);
                if (type == 1) {
                    this.mData[i] = Integer.valueOf(cursor.getInt(i));
                } else if (type == 2) {
                    this.mData[i] = Float.valueOf(cursor.getFloat(i));
                } else if (type == 3) {
                    this.mData[i] = cursor.getString(i);
                } else if (type != 4) {
                    this.mData[i] = null;
                } else {
                    this.mData[i] = cursor.getBlob(i);
                }
            }
        }

        /* access modifiers changed from: package-private */
        public Uri getUri() {
            return this.mUri;
        }

        /* access modifiers changed from: package-private */
        public Integer getInteger(int i) {
            return (Integer) this.mData[i];
        }

        /* access modifiers changed from: package-private */
        public Integer getInteger(int i, Integer num) {
            Integer integer = getInteger(i);
            return integer == null ? num : integer;
        }

        /* access modifiers changed from: package-private */
        public String getString(int i) {
            return (String) this.mData[i];
        }

        /* access modifiers changed from: package-private */
        public void setObject(int i, Object obj) {
            this.mData[i] = obj;
        }
    }
}
