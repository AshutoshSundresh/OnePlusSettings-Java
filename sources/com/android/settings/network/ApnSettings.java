package com.android.settings.network;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Telephony;
import android.telephony.CarrierConfigManager;
import android.telephony.PhoneStateListener;
import android.telephony.PreciseDataConnectionState;
import android.telephony.ServiceState;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import com.android.ims.ImsManager;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.RestrictedSettingsFragment;
import com.android.settings.Utils;
import com.android.settingslib.RestrictedLockUtils;
import com.oneplus.settings.utils.OPUtils;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.codeaurora.internal.IExtTelephony;

public class ApnSettings extends RestrictedSettingsFragment implements Preference.OnPreferenceChangeListener {
    private static final String[] CARRIERS_PROJECTION = {"_id", "name", "apn", "type", "mvno_type", "mvno_match_data", "edited", "bearer", "bearer_bitmask"};
    private static final Uri DEFAULTAPN_URI = Uri.parse("content://telephony/carriers/restore");
    private static final Uri PREFERAPN_URI = Uri.parse("content://telephony/carriers/preferapn");
    private static final boolean isVzwSim = SystemProperties.getBoolean("vendor.radio.test.vzw.sim", false);
    private static boolean mRestoreDefaultApnMode;
    private DialogInterface.OnKeyListener keylistener = new DialogInterface.OnKeyListener(this) {
        /* class com.android.settings.network.ApnSettings.AnonymousClass3 */

        public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
            Log.d("ApnSettings", "onKey keyCode = " + i);
            return i == 4 && keyEvent.getRepeatCount() == 0;
        }
    };
    private boolean mAllowAddingApns;
    private boolean mApnEditable = false;
    private boolean mCarrierActived = false;
    private PersistableBundle mHideApnsGroupByIccid;
    private String[] mHideApnsWithIccidRule;
    private String[] mHideApnsWithRule;
    private boolean mHideImsApn;
    private boolean mHidePresetApnDetails;
    private IntentFilter mIntentFilter;
    private String mMvnoMatchData;
    private String mMvnoType;
    private int mPhoneId;
    private final PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        /* class com.android.settings.network.ApnSettings.AnonymousClass1 */

        public void onPreciseDataConnectionStateChanged(PreciseDataConnectionState preciseDataConnectionState) {
            if (preciseDataConnectionState.getState() == 2 && !ApnSettings.mRestoreDefaultApnMode) {
                ApnSettings.this.fillList();
            }
        }
    };
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        /* class com.android.settings.network.ApnSettings.AnonymousClass2 */

        public void onReceive(Context context, Intent intent) {
            Log.d("ApnSettings", "onReceive : " + intent);
            if (intent.getAction().equals("android.telephony.action.SUBSCRIPTION_CARRIER_IDENTITY_CHANGED")) {
                if (!ApnSettings.mRestoreDefaultApnMode) {
                    int intExtra = intent.getIntExtra("android.telephony.extra.SUBSCRIPTION_ID", -1);
                    if (SubscriptionManager.isValidSubscriptionId(intExtra) && ApnSettings.this.mPhoneId == SubscriptionUtil.getPhoneId(context, intExtra) && intExtra != ApnSettings.this.mSubId) {
                        ApnSettings.this.mSubId = intExtra;
                        ApnSettings apnSettings = ApnSettings.this;
                        apnSettings.mSubscriptionInfo = apnSettings.getSubscriptionInfo(apnSettings.mSubId);
                        ApnSettings apnSettings2 = ApnSettings.this;
                        apnSettings2.restartPhoneStateListener(apnSettings2.mSubId);
                    }
                    ApnSettings.this.fillList();
                }
            } else if (intent.getAction().equals("org.codeaurora.intent.action.ACTION_ENHANCE_4G_SWITCH")) {
                if (!ApnSettings.mRestoreDefaultApnMode) {
                    ApnSettings.this.fillList();
                } else {
                    ApnSettings.this.showDialog(1001);
                }
            } else if (intent.getAction().equals("android.intent.action.SIM_STATE_CHANGED")) {
                int intExtra2 = intent.getIntExtra("phone", -1);
                Log.d("ApnSettings", "slotId: " + intExtra2);
                if (intExtra2 != -1) {
                    String stringExtra = intent.getStringExtra("ss");
                    Log.d("ApnSettings", "simStatus: " + stringExtra);
                    if (("ABSENT".equals(stringExtra) || "NOT_READY".equals(stringExtra)) && ApnSettings.this.mSubscriptionInfo != null && ApnSettings.this.mSubscriptionInfo.getSimSlotIndex() == intExtra2) {
                        ApnSettings.this.finish();
                    }
                }
            } else if (intent.getAction().equals("android.intent.action.CLOSE_SYSTEM_DIALOGS")) {
                String stringExtra2 = intent.getStringExtra("reason");
                Log.d("ApnSettings", "ACTION_CLOSE_SYSTEM_DIALOGS reason: " + stringExtra2);
                if (stringExtra2 != null && !ApnSettings.mRestoreDefaultApnMode && stringExtra2.equals("homekey")) {
                    ApnSettings.this.finish();
                }
            } else if (intent.getAction().equals("android.intent.action.restoreDefaultAPN")) {
                Log.d("ApnSettings", "ACTION_RESTORE_DEFAULT_APN mRestoreDefaultApnMode: " + ApnSettings.mRestoreDefaultApnMode);
                if (ApnSettings.mRestoreDefaultApnMode) {
                    if (ApnSettings.this.mRestoreDefaultApnThread != null) {
                        ApnSettings.this.mRestoreDefaultApnThread.quit();
                        ApnSettings.this.mRestoreDefaultApnThread = null;
                        ApnSettings.this.mRestoreApnProcessHandler = null;
                    }
                    ApnSettings.this.fillList();
                    ApnSettings.this.getPreferenceScreen().setEnabled(true);
                    boolean unused = ApnSettings.mRestoreDefaultApnMode = false;
                    ApnSettings.this.removeDialog(1001);
                    Toast.makeText(ApnSettings.this.getActivity(), ApnSettings.this.getResources().getString(C0017R$string.restore_default_apn_completed), 1).show();
                }
            } else if (intent.getAction().equals("android.telephony.action.CARRIER_CONFIG_CHANGED") && SubscriptionManager.isValidSubscriptionId(intent.getIntExtra("subscription", -1))) {
                ApnSettings.this.loadCarrierConfigInfo();
                ApnSettings.this.fillList();
            }
        }
    };
    private RestoreApnProcessHandler mRestoreApnProcessHandler;
    private RestoreApnUiHandler mRestoreApnUiHandler;
    private HandlerThread mRestoreDefaultApnThread;
    private String mSelectedKey;
    private boolean mSimLocked = false;
    private int mSubId;
    private SubscriptionInfo mSubscriptionInfo;
    private TelephonyManager mTelephonyManager;
    private boolean mUnavailable;
    private UserManager mUserManager;

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        return i == 1001 ? 579 : 0;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 12;
    }

    public ApnSettings() {
        super("no_config_mobile_networks");
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void restartPhoneStateListener(int i) {
        if (!mRestoreDefaultApnMode) {
            TelephonyManager createForSubscriptionId = this.mTelephonyManager.createForSubscriptionId(i);
            this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
            this.mTelephonyManager = createForSubscriptionId;
            createForSubscriptionId.listen(this.mPhoneStateListener, 4096);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.RestrictedSettingsFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        int intExtra = activity.getIntent().getIntExtra("sub_id", -1);
        this.mSubId = intExtra;
        this.mPhoneId = SubscriptionUtil.getPhoneId(activity, intExtra);
        this.mIntentFilter = new IntentFilter("android.telephony.action.SUBSCRIPTION_CARRIER_IDENTITY_CHANGED");
        if (Utils.isSupportCTPA(getActivity().getApplicationContext())) {
            this.mIntentFilter.addAction("org.codeaurora.intent.action.ACTION_ENHANCE_4G_SWITCH");
        }
        this.mIntentFilter.addAction("android.intent.action.SIM_STATE_CHANGED");
        this.mIntentFilter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
        this.mIntentFilter.addAction("android.intent.action.restoreDefaultAPN");
        this.mIntentFilter.addAction("android.telephony.action.CARRIER_CONFIG_CHANGED");
        setIfOnlyAvailableForAdmins(true);
        this.mSubscriptionInfo = getSubscriptionInfo(this.mSubId);
        this.mTelephonyManager = (TelephonyManager) activity.getSystemService(TelephonyManager.class);
        PersistableBundle configForSubId = ((CarrierConfigManager) getSystemService("carrier_config")).getConfigForSubId(this.mSubId);
        this.mHideImsApn = configForSubId.getBoolean("hide_ims_apn_bool");
        this.mAllowAddingApns = configForSubId.getBoolean("allow_adding_apns_bool");
        this.mHideApnsWithRule = configForSubId.getStringArray("apn_hide_rule_strings_array");
        this.mHideApnsWithIccidRule = configForSubId.getStringArray("apn_hide_rule_strings_with_iccids_array");
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        if (subscriptionInfo != null) {
            String iccId = subscriptionInfo.getIccId();
            Log.d("ApnSettings", "iccid: " + iccId);
            this.mHideApnsGroupByIccid = configForSubId.getPersistableBundle(iccId);
        }
        if (this.mAllowAddingApns && ApnEditor.hasAllApns(configForSubId.getStringArray("read_only_apn_types_string_array"))) {
            Log.d("ApnSettings", "not allowing adding APN because all APN types are read only");
            this.mAllowAddingApns = false;
        }
        this.mHidePresetApnDetails = configForSubId.getBoolean("hide_preset_apn_details_bool");
        loadCarrierConfigInfo();
        if (OPUtils.isSupportUss()) {
            updateApnEditState();
        }
        this.mUserManager = UserManager.get(activity);
    }

    private void updateApnEditState() {
        boolean z = false;
        this.mCarrierActived = getCarrierActiveState() == 1;
        if (getSimLockState() == 1) {
            z = true;
        }
        this.mSimLocked = z;
        this.mApnEditable = OPUtils.getApnEditable();
        this.mAllowAddingApns = isAllowEditApn();
    }

    private boolean isAllowEditApn() {
        if (this.mApnEditable) {
            return true;
        }
        return this.mCarrierActived && !this.mSimLocked;
    }

    private int getCarrierActiveState() {
        String queryParamstore = queryParamstore("subscriber_carrierid", "value");
        int i = (queryParamstore.isEmpty() || queryParamstore.equals("Chameleon")) ? 0 : 1;
        Log.d("ApnSettings", "getCarrierActiveState: " + i);
        return i;
    }

    private int getSimLockState() {
        int i;
        IExtTelephony asInterface = IExtTelephony.Stub.asInterface(ServiceManager.getService("extphone"));
        try {
            Method declaredMethod = asInterface.getClass().getDeclaredMethod("getSimLockStatus", new Class[0]);
            declaredMethod.setAccessible(true);
            i = ((Integer) declaredMethod.invoke(asInterface, new Object[0])).intValue();
        } catch (Exception e) {
            e.printStackTrace();
            i = -1;
        }
        Log.d("ApnSettings", "getSimLockState: " + i);
        return i;
    }

    private String queryParamstore(String str, String str2) {
        Uri parse = Uri.parse("content://com.redbend.app.provider");
        Cursor query = getContentResolver().query(parse, null, null, new String[]{str, str2, "0"}, null);
        String str3 = "";
        if (query != null) {
            if (query.getCount() == 1 && query.moveToFirst()) {
                str3 = query.getString(0);
            }
            query.close();
        }
        return str3;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.RestrictedSettingsFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (getPreferenceScreen() != null) {
            getPreferenceScreen().removeAll();
        }
        getEmptyTextView().setText(C0017R$string.apn_settings_not_available);
        boolean isUiRestricted = isUiRestricted();
        this.mUnavailable = isUiRestricted;
        setHasOptionsMenu(!isUiRestricted);
        if (this.mUnavailable) {
            addPreferencesFromResource(C0019R$xml.placeholder_prefs);
        } else {
            addPreferencesFromResource(C0019R$xml.apn_settings);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.RestrictedSettingsFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        Log.d("ApnSettings", "onResume: restoreMode = " + mRestoreDefaultApnMode);
        if (this.mRestoreDefaultApnThread != null) {
            removeDialog(1001);
            this.mRestoreDefaultApnThread.quit();
            this.mRestoreDefaultApnThread = null;
            this.mRestoreApnProcessHandler = null;
        }
        if (!this.mUnavailable) {
            getActivity().registerReceiver(this.mReceiver, this.mIntentFilter);
            restartPhoneStateListener(this.mSubId);
            if (!mRestoreDefaultApnMode) {
                fillList();
            }
            if (OPUtils.isSupportUss()) {
                updateApnEditState();
            }
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        if (this.mRestoreDefaultApnThread != null) {
            removeDialog(1001);
            this.mRestoreDefaultApnThread.quit();
            this.mRestoreDefaultApnThread = null;
            this.mRestoreApnProcessHandler = null;
        }
        if (!this.mUnavailable) {
            getActivity().unregisterReceiver(this.mReceiver);
            this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
        }
    }

    @Override // com.android.settings.RestrictedSettingsFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onDestroy() {
        Log.d("ApnSettings", "onDestroy");
        super.onDestroy();
        HandlerThread handlerThread = this.mRestoreDefaultApnThread;
        if (handlerThread != null) {
            handlerThread.quit();
        }
        mRestoreDefaultApnMode = false;
    }

    @Override // com.android.settings.RestrictedSettingsFragment
    public RestrictedLockUtils.EnforcedAdmin getRestrictionEnforcedAdmin() {
        UserHandle of = UserHandle.of(this.mUserManager.getUserHandle());
        if (!this.mUserManager.hasUserRestriction("no_config_mobile_networks", of) || this.mUserManager.hasBaseUserRestriction("no_config_mobile_networks", of)) {
            return null;
        }
        return RestrictedLockUtils.EnforcedAdmin.MULTIPLE_ENFORCED_ADMIN;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private SubscriptionInfo getSubscriptionInfo(int i) {
        return SubscriptionManager.from(getActivity()).getActiveSubscriptionInfo(i);
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

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void fillList() {
        Context context;
        ArrayList arrayList;
        ArrayList arrayList2;
        PreferenceGroup preferenceGroup;
        ArrayList arrayList3;
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        int subscriptionId = subscriptionInfo != null ? subscriptionInfo.getSubscriptionId() : -1;
        Uri withAppendedPath = Uri.withAppendedPath(Telephony.Carriers.SIM_APN_URI, String.valueOf(subscriptionId));
        StringBuilder sb = new StringBuilder("NOT (type='ia' AND (apn=\"\" OR apn IS NULL)) AND user_visible!=0");
        int phoneId = SubscriptionManager.getPhoneId(subscriptionId);
        Context applicationContext = getActivity().getApplicationContext();
        boolean isEnhanced4gLteModeSettingEnabledByUser = ImsManager.getInstance(applicationContext, phoneId).isEnhanced4gLteModeSettingEnabledByUser();
        if (this.mHideImsApn || (Utils.isSupportCTPA(applicationContext) && !isEnhanced4gLteModeSettingEnabledByUser)) {
            sb.append(" AND NOT (type='ims')");
        }
        appendFilter(sb);
        Log.d("ApnSettings", "where = " + sb.toString());
        Cursor query = getContentResolver().query(withAppendedPath, CARRIERS_PROJECTION, sb.toString(), null, "_id");
        if (query != null) {
            PreferenceGroup preferenceGroup2 = (PreferenceGroup) findPreference("apn_list");
            preferenceGroup2.removeAll();
            ArrayList arrayList4 = new ArrayList();
            ArrayList arrayList5 = new ArrayList();
            this.mSelectedKey = getSelectedApnKey();
            Log.d("ApnSettings", "select key = " + this.mSelectedKey);
            ApnPreference.setSelectedKey(this.mSelectedKey);
            query.moveToFirst();
            int i = 0;
            boolean z = false;
            while (!query.isAfterLast()) {
                String string = query.getString(1);
                String string2 = query.getString(2);
                String string3 = query.getString(i);
                String string4 = query.getString(3);
                int i2 = query.getInt(6);
                this.mMvnoType = query.getString(4);
                this.mMvnoMatchData = query.getString(5);
                String simOperator = this.mSubscriptionInfo == null ? "" : ((TelephonyManager) getSystemService("phone")).getSimOperator(subscriptionId);
                if ((!OPUtils.isSupportUss() || !isSprintMccMnc(simOperator) || !"3G_APN".equals(string)) && !"3G_HOT".equals(string)) {
                    preferenceGroup = preferenceGroup2;
                    String localizedName = Utils.getLocalizedName(getActivity(), query.getString(1));
                    if (TextUtils.isEmpty(localizedName)) {
                        localizedName = string;
                    }
                    int i3 = query.getInt(7);
                    int i4 = query.getInt(8);
                    arrayList = arrayList5;
                    int bitmaskForTech = ServiceState.getBitmaskForTech(i3) | i4;
                    arrayList2 = arrayList4;
                    int networkTypeToRilRadioTechnology = ServiceState.networkTypeToRilRadioTechnology(TelephonyManager.getDefault().getDataNetworkType(subscriptionId));
                    if (ServiceState.bitmaskHasTech(bitmaskForTech, networkTypeToRilRadioTechnology) || ((i3 == 0 && i4 == 0) || isVzwSim || isSprintMccMnc(simOperator))) {
                        context = applicationContext;
                    } else {
                        StringBuilder sb2 = new StringBuilder();
                        context = applicationContext;
                        sb2.append("fullBearer = ");
                        sb2.append(bitmaskForTech);
                        sb2.append(", radioTech = ");
                        sb2.append(networkTypeToRilRadioTechnology);
                        Log.d("ApnSettings", sb2.toString());
                        if (networkTypeToRilRadioTechnology != 0 || (i3 == 0 && networkTypeToRilRadioTechnology == 0)) {
                            Log.d("ApnSettings", "filter radio tech not match apn.");
                            query.moveToNext();
                        } else {
                            Log.d("ApnSettings", "Do not remove apn when it has bearer and in no service surrounding");
                        }
                    }
                    ApnPreference apnPreference = new ApnPreference(getPrefContext());
                    apnPreference.setKey(string3);
                    apnPreference.setTitle(localizedName);
                    apnPreference.setPersistent(false);
                    apnPreference.setOnPreferenceChangeListener(this);
                    apnPreference.setSubId(subscriptionId);
                    if (!this.mHidePresetApnDetails || i2 != 0) {
                        apnPreference.setSummary(string2);
                    } else {
                        apnPreference.setHideDetails();
                    }
                    Log.d("ApnSettings", "cursor key = " + string3 + ", type = " + string4 + ", mccmnc = " + simOperator + ", is default type = " + isApnType(string4, "default"));
                    boolean z2 = string4 == null || isApnType(string4, "default");
                    Log.d("ApnSettings", "isVoLTEEnabled = " + isEnhanced4gLteModeSettingEnabledByUser + ", selectable = " + z2 + ", Utils.isSupportCTPA(appContext) = " + Utils.isSupportCTPA(context));
                    if (isEnhanced4gLteModeSettingEnabledByUser && z2 && Utils.isSupportCTPA(context)) {
                        z2 = string4 == null || !string4.equals("ims");
                    }
                    Log.d("ApnSettings", "final selectable = " + z2);
                    apnPreference.setSelectable(z2);
                    if (z2 && OPUtils.isSupportUss() && !isAllowEditApn()) {
                        apnPreference.setRadioButtonEnable(false);
                    }
                    if (z2) {
                        String str = this.mSelectedKey;
                        if (str == null || !str.equals(string3)) {
                            z = z;
                            arrayList3 = arrayList2;
                        } else {
                            apnPreference.setChecked();
                            Log.d("ApnSettings", "find select key = " + this.mSelectedKey + " apn: " + string2);
                            arrayList3 = arrayList2;
                            z = true;
                        }
                        arrayList3.add(apnPreference);
                        arrayList5 = arrayList;
                    } else {
                        arrayList3 = arrayList2;
                        arrayList5 = arrayList;
                        arrayList5.add(apnPreference);
                        z = z;
                    }
                    query.moveToNext();
                    arrayList4 = arrayList3;
                    preferenceGroup2 = preferenceGroup;
                    applicationContext = context;
                    i = 0;
                } else {
                    Log.d("ApnSettings", "skip 3G_APN/3G_HOT!");
                    query.moveToNext();
                    preferenceGroup = preferenceGroup2;
                    arrayList2 = arrayList4;
                    arrayList = arrayList5;
                    context = applicationContext;
                }
                preferenceGroup2 = preferenceGroup;
                z = z;
                arrayList4 = arrayList2;
                arrayList5 = arrayList;
                applicationContext = context;
                i = 0;
            }
            query.close();
            Iterator it = arrayList4.iterator();
            while (it.hasNext()) {
                preferenceGroup2.addPreference((Preference) it.next());
            }
            Iterator it2 = arrayList5.iterator();
            while (it2.hasNext()) {
                preferenceGroup2.addPreference((Preference) it2.next());
            }
            if (!z && preferenceGroup2.getPreferenceCount() > 0) {
                ApnPreference apnPreference2 = (ApnPreference) preferenceGroup2.getPreference(0);
                apnPreference2.setChecked();
                setSelectedApnKey(apnPreference2.getKey());
                Log.d("ApnSettings", "set key to  " + apnPreference2.getKey());
            }
        }
    }

    private boolean isApnType(String str, String str2) {
        if (TextUtils.isEmpty(str)) {
            return true;
        }
        for (String str3 : str.split(",")) {
            String trim = str3.trim();
            if (trim.equals(str2) || trim.equals("*")) {
                return true;
            }
        }
        return false;
    }

    private void appendFilter(StringBuilder sb) {
        boolean z;
        String[] strArr;
        String string;
        PersistableBundle persistableBundle = this.mHideApnsGroupByIccid;
        boolean z2 = true;
        if (persistableBundle == null || persistableBundle.isEmpty()) {
            z = true;
        } else {
            z = this.mHideApnsGroupByIccid.getBoolean("include_common_rules", true);
            Log.d("ApnSettings", "apn hidden rules specified iccid, include common rule: " + z);
            for (String str : this.mHideApnsGroupByIccid.keySet()) {
                if (Utils.carrierTableFieldValidate(str) && (string = this.mHideApnsGroupByIccid.getString(str)) != null) {
                    sb.append(" AND " + str + " <> \"" + string + "\"");
                }
            }
        }
        String[] strArr2 = this.mHideApnsWithIccidRule;
        if (strArr2 != null) {
            HashMap<String, String> apnRuleMap = getApnRuleMap(strArr2);
            SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
            if (isOperatorIccid(apnRuleMap, subscriptionInfo == null ? "" : subscriptionInfo.getIccId())) {
                String str2 = apnRuleMap.get("include_common_rules");
                if (str2 != null && str2.equalsIgnoreCase(String.valueOf(false))) {
                    z2 = false;
                }
                Log.d("ApnSettings", "apn hidden rules in iccids, include common rule: " + z2);
                filterWithKey(apnRuleMap, sb);
                z = z2;
            }
        }
        if (z && (strArr = this.mHideApnsWithRule) != null) {
            filterWithKey(getApnRuleMap(strArr), sb);
        }
    }

    private void filterWithKey(Map<String, String> map, StringBuilder sb) {
        for (String str : map.keySet()) {
            if (Utils.carrierTableFieldValidate(str)) {
                String str2 = map.get(str);
                if (!TextUtils.isEmpty(str2)) {
                    String[] split = str2.split(",");
                    for (String str3 : split) {
                        sb.append(" AND " + str + " <> \"" + str3 + "\"");
                    }
                }
            }
        }
    }

    private HashMap<String, String> getApnRuleMap(String[] strArr) {
        HashMap<String, String> hashMap = new HashMap<>();
        if (strArr != null) {
            int length = strArr.length;
            Log.d("ApnSettings", "ruleArray size = " + length);
            if (length > 0 && length % 2 == 0) {
                for (int i = 0; i < length; i += 2) {
                    hashMap.put(strArr[i].toLowerCase(), strArr[i + 1]);
                }
            }
        }
        return hashMap;
    }

    private boolean isOperatorIccid(HashMap<String, String> hashMap, String str) {
        String str2 = hashMap.get("iccid");
        if (!TextUtils.isEmpty(str2)) {
            for (String str3 : str2.split(",")) {
                if (str.startsWith(str3.trim())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (OPUtils.isSupportUss() && !isAllowEditApn()) {
            menu.findItem(2).setEnabled(false);
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        if (!this.mUnavailable) {
            if (this.mAllowAddingApns && !OPUtils.isSupportUss()) {
                menu.add(0, 1, 0, getResources().getString(C0017R$string.menu_new)).setIcon(C0008R$drawable.ic_add_vpn).setShowAsAction(1);
            }
            if (OPUtils.isSupportUss() && isAllowEditApn()) {
                menu.add(0, 1, 0, getResources().getString(C0017R$string.menu_new)).setIcon(C0008R$drawable.ic_add_vpn).setShowAsAction(1);
            }
            menu.add(0, 2, 0, getResources().getString(C0017R$string.menu_restore)).setIcon(17301589);
        }
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 1) {
            addNewApn();
            return true;
        } else if (itemId != 2) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            restoreDefaultApn();
            return true;
        }
    }

    private void addNewApn() {
        Intent intent = new Intent("android.intent.action.INSERT", Telephony.Carriers.CONTENT_URI);
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        intent.putExtra("sub_id", subscriptionInfo != null ? subscriptionInfo.getSubscriptionId() : -1);
        intent.addFlags(1);
        if (!TextUtils.isEmpty(this.mMvnoType) && !TextUtils.isEmpty(this.mMvnoMatchData)) {
            intent.putExtra("mvno_type", this.mMvnoType);
            intent.putExtra("mvno_match_data", this.mMvnoMatchData);
        }
        startActivity(intent);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        Log.d("ApnSettings", "onPreferenceChange(): Preference - " + preference + ", newValue - " + obj + ", newValue type - " + obj.getClass());
        if (!(obj instanceof String)) {
            return true;
        }
        setSelectedApnKey((String) obj);
        return true;
    }

    private void setSelectedApnKey(String str) {
        this.mSelectedKey = str;
        ContentResolver contentResolver = getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put("apn_id", this.mSelectedKey);
        contentResolver.update(getUriForCurrSubId(PREFERAPN_URI), contentValues, null, null);
    }

    private String getSelectedApnKey() {
        String str;
        Cursor query = getContentResolver().query(getUriForCurrSubId(PREFERAPN_URI), new String[]{"_id"}, null, null, "name ASC");
        if (query.getCount() > 0) {
            query.moveToFirst();
            str = query.getString(0);
        } else {
            str = null;
        }
        query.close();
        return str;
    }

    private boolean restoreDefaultApn() {
        Log.d("ApnSettings", "restoreDefaultApn");
        showDialog(1001);
        mRestoreDefaultApnMode = true;
        if (this.mRestoreApnUiHandler == null) {
            this.mRestoreApnUiHandler = new RestoreApnUiHandler();
        }
        if (this.mRestoreApnProcessHandler == null || this.mRestoreDefaultApnThread == null) {
            HandlerThread handlerThread = new HandlerThread("Restore default APN Handler: Process Thread");
            this.mRestoreDefaultApnThread = handlerThread;
            handlerThread.start();
            this.mRestoreApnProcessHandler = new RestoreApnProcessHandler(this.mRestoreDefaultApnThread.getLooper(), this.mRestoreApnUiHandler);
        }
        this.mRestoreApnProcessHandler.sendEmptyMessage(1);
        return true;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private Uri getUriForCurrSubId(Uri uri) {
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        int subscriptionId = subscriptionInfo != null ? subscriptionInfo.getSubscriptionId() : -1;
        if (!SubscriptionManager.isValidSubscriptionId(subscriptionId)) {
            return uri;
        }
        return Uri.withAppendedPath(uri, "subId/" + String.valueOf(subscriptionId));
    }

    /* access modifiers changed from: private */
    public class RestoreApnUiHandler extends Handler {
        private RestoreApnUiHandler() {
        }

        public void handleMessage(Message message) {
            if (message.what == 2) {
                Log.d("ApnSettings", "EVENT_RESTORE_DEFAULTAPN_COMPLETE mRestoreDefaultApnMode = " + ApnSettings.mRestoreDefaultApnMode);
                FragmentActivity activity = ApnSettings.this.getActivity();
                if (activity == null) {
                    boolean unused = ApnSettings.mRestoreDefaultApnMode = false;
                    ApnSettings.this.removeDialog(1001);
                    Log.d("ApnSettings", "EVENT_RESTORE_DEFAULTAPN_COMPLETE activity is null !");
                } else if (ApnSettings.mRestoreDefaultApnMode) {
                    ApnSettings.this.fillList();
                    ApnSettings.this.getPreferenceScreen().setEnabled(true);
                    boolean unused2 = ApnSettings.mRestoreDefaultApnMode = false;
                    ApnSettings.this.removeDialog(1001);
                    Toast.makeText(activity, ApnSettings.this.getResources().getString(C0017R$string.restore_default_apn_completed), 1).show();
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public class RestoreApnProcessHandler extends Handler {
        private Handler mRestoreApnUiHandler;

        public RestoreApnProcessHandler(Looper looper, Handler handler) {
            super(looper);
            this.mRestoreApnUiHandler = handler;
        }

        public void handleMessage(Message message) {
            if (message.what == 1) {
                ApnSettings.this.getContentResolver().delete(ApnSettings.this.getUriForCurrSubId(ApnSettings.DEFAULTAPN_URI), null, null);
                this.mRestoreApnUiHandler.sendEmptyMessageDelayed(2, 10000);
            }
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        if (i != 1001) {
            return null;
        }
        AnonymousClass4 r3 = new ProgressDialog(this, getActivity()) {
            /* class com.android.settings.network.ApnSettings.AnonymousClass4 */

            public boolean onTouchEvent(MotionEvent motionEvent) {
                return true;
            }
        };
        r3.setMessage(getResources().getString(C0017R$string.restore_default_apn));
        r3.setCancelable(false);
        r3.setOnKeyListener(this.keylistener);
        return r3;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void loadCarrierConfigInfo() {
        PersistableBundle configForSubId = ((CarrierConfigManager) getSystemService("carrier_config")).getConfigForSubId(this.mSubId);
        this.mHideImsApn = configForSubId.getBoolean("hide_ims_apn_bool");
        this.mAllowAddingApns = configForSubId.getBoolean("allow_adding_apns_bool");
        this.mHideApnsWithRule = configForSubId.getStringArray("apn_hide_rule_strings_array");
        this.mHideApnsWithIccidRule = configForSubId.getStringArray("apn_hide_rule_strings_with_iccids_array");
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        if (subscriptionInfo != null) {
            String iccId = subscriptionInfo.getIccId();
            Log.d("ApnSettings", "loadCarrierConfigInfo: iccid = " + iccId);
            this.mHideApnsGroupByIccid = configForSubId.getPersistableBundle(iccId);
        }
        if (this.mAllowAddingApns && ApnEditor.hasAllApns(configForSubId.getStringArray("read_only_apn_types_string_array"))) {
            Log.d("ApnSettings", "loadCarrierConfigInfo: not allowing adding APN because all APN types are read only");
            this.mAllowAddingApns = false;
        }
        this.mHidePresetApnDetails = configForSubId.getBoolean("hide_preset_apn_details_bool");
    }
}
