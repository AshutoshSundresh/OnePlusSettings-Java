package com.oneplus.settings;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemProperties;
import android.text.BidiFormatter;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.oneplus.accountsdk.auth.OPAuth;
import com.oneplus.settings.ui.OPMemberPreference;
import com.oneplus.settings.utils.OPOkHttpUtils;
import com.oneplus.settings.utils.OPUtils;
import com.oneplus.settings.utils.SignUtils;
import java.io.IOException;
import java.util.HashMap;
import okhttp3.Call;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

public class OPMemberController extends BasePreferenceController implements LifecycleObserver, OnResume, OnPause, OPOkHttpUtils.NetCall {
    private static final String ACCESS_TOKEN = "access_token";
    private static final String ACCESS_TOKEN_API_URL = "https://gateway.oneplus.net/v2/oauth/token";
    private static final String ACCOUNT_PROVIDER_URL = "content://com.oneplus.account.provider.open";
    private static final String AVATAR = "avatar";
    private static final String CLIENT_ID = "a291040ba78042b39d1ab8ba35396478";
    private static final String CLIENT_SECRET = "c56f745f667b449aa6ffff36c4f03dd7";
    public static final String CLIENT_TYPE = "3";
    private static final String CN_ACCESS_TOKEN_API_URL = "https://gateway.oneplus.cn/v2/oauth/token";
    private static final String CN_CLIENT_ID = "4a348e7f51314002b1927030ffa22d16";
    private static final String CN_CLIENT_SECRET = "9b0c4d865d804bd99209e2484bad0084";
    private static final String CN_MEMBER_API_URL = "https://gateway.oneplus.cn/v2/api/router";
    private static final String CONTENT = "content";
    private static final String DATA = "data";
    private static final String ERR_CODE = "errCode";
    private static final String EXTRA_PACKAGE_NAME = "extra_package_name";
    private static final String EXTRA_START_MEMBER_CHANNEL_KEY = "start_member_ship_channel";
    private static final String ICON = "icon";
    private static final String KEY_GET_HT_ACCOUNT_TOKEN = "get_account_o_token";
    private static final String KEY_GET_OP_ACCOUNT_TOKEN = "get_account_oneplus_token";
    private static final String KEY_MEMBER = "member_sign";
    private static final String KEY_METHOD_H2 = "setting.copywriting.get";
    private static final String KEY_METHOD_INDIA = "member.app.copywriting.get";
    private static final String MEMBER_API_URL = "https://gateway.oneplus.net/v2/api/router";
    private static final String NEW_VERSION = "benefitVersion";
    public static final String PACKAGE_NAME = "com.android.settings";
    private static final String RET = "ret";
    private static final String SUCCESS = "1";
    private static final String TAG = "OPMemberController";
    private static final String TITLE = "title";
    private static final String USER_DATA_HT_TOKEN = "oplustoken";
    private static final String USER_DATA_ONEPLUS_TOKEN = "token";
    public static final String VERSION_NAME = "1.1.0";
    private int errCount = 0;
    private long mAccountVersionCode;
    private String mAccountVersionName;
    private Context mContext;
    private OPMemberPreference mOPMemberPreference;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public String getPreferenceKey() {
        return KEY_MEMBER;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public OPMemberController(Context context) {
        super(context, KEY_MEMBER);
        this.mContext = context;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return ((isH2Show() || isIndiaShow()) && !OPUtils.isGuestMode()) ? 0 : 3;
    }

    private boolean isIndiaShow() {
        return OPUtils.isIndia() && OPUtils.isAppExist(this.mContext, "com.oneplus.membership");
    }

    private boolean isH2Show() {
        try {
            PackageInfo packageInfo = this.mContext.getPackageManager().getPackageInfo("com.oneplus.account", 0);
            if (packageInfo == null) {
                return false;
            }
            long longVersionCode = packageInfo.getLongVersionCode();
            this.mAccountVersionCode = longVersionCode;
            this.mAccountVersionName = packageInfo.versionName;
            if (longVersionCode < 1570 || !OPUtils.isH2()) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        if ((isH2Show() || isIndiaShow()) && !OPUtils.isGuestMode()) {
            getData(PreferenceManager.getDefaultSharedPreferences(this.mContext).getString(ACCESS_TOKEN, ""));
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mOPMemberPreference = (OPMemberPreference) preferenceScreen.findPreference(KEY_MEMBER);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!KEY_MEMBER.equals(preference.getKey())) {
            return false;
        }
        try {
            if (isH2Show()) {
                OPAuth.startAccountSettingsActivity(this.mContext);
            } else {
                Intent intent = new Intent();
                intent.setAction("com.oneplus.member.action.main.page");
                intent.putExtra(EXTRA_START_MEMBER_CHANNEL_KEY, "0");
                this.mContext.startActivity(intent);
            }
            this.mOPMemberPreference.clearNew();
            return true;
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return true;
        }
    }

    private void getAccessToken() {
        boolean isH2Show = isH2Show();
        HashMap hashMap = new HashMap();
        hashMap.put("client_id", isH2Show ? CN_CLIENT_ID : CLIENT_ID);
        hashMap.put("client_secret", isH2Show ? CN_CLIENT_SECRET : CLIENT_SECRET);
        hashMap.put("grant_type", "client_credentials");
        OPOkHttpUtils.getInstance().postDataAsyn(isH2Show ? CN_ACCESS_TOKEN_API_URL : ACCESS_TOKEN_API_URL, hashMap, new OPOkHttpUtils.NetCall() {
            /* class com.oneplus.settings.OPMemberController.AnonymousClass1 */

            @Override // com.oneplus.settings.utils.OPOkHttpUtils.NetCall
            public void success(Call call, Response response) throws IOException {
                try {
                    String string = new JSONObject(response.body().string()).getString(OPMemberController.ACCESS_TOKEN);
                    if (!TextUtils.isEmpty(string)) {
                        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(OPMemberController.this.mContext).edit();
                        edit.putString(OPMemberController.ACCESS_TOKEN, string);
                        edit.apply();
                        OPMemberController.this.getData(string);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override // com.oneplus.settings.utils.OPOkHttpUtils.NetCall
            public void failed(Call call, IOException iOException) {
                iOException.printStackTrace();
            }
        });
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void getData(String str) {
        boolean isH2Show = isH2Show();
        Log.d(TAG, "isH2=" + isH2Show);
        String accountTokenForProvider = getAccountTokenForProvider(this.mContext, KEY_GET_OP_ACCOUNT_TOKEN);
        HashMap hashMap = new HashMap();
        hashMap.put(ACCESS_TOKEN, str);
        hashMap.put("client_id", isH2Show ? CN_CLIENT_ID : CLIENT_ID);
        hashMap.put("method", isH2Show ? KEY_METHOD_H2 : KEY_METHOD_INDIA);
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("client", CLIENT_TYPE);
            jSONObject.put("deviceName", OPUtils.getDeviceName());
            jSONObject.put("model", OPUtils.getDeviceModel());
            jSONObject.put("deviceId", OPUtils.getOPSafeUUID(this.mContext));
            jSONObject.put("lang", OPUtils.getLang());
            jSONObject.put("version", VERSION_NAME);
            jSONObject.put("packageName", PACKAGE_NAME);
            jSONObject.put(USER_DATA_ONEPLUS_TOKEN, accountTokenForProvider);
            if (isH2Show) {
                jSONObject.put("heyTapToken", getAccountTokenForProvider(this.mContext, KEY_GET_HT_ACCOUNT_TOKEN));
                jSONObject.put("cloudServiceFlag", OPUtils.isAppExist(this.mContext, "com.heytap.cloud") ? 1 : 0);
                jSONObject.put("versionName", getOSVersion());
                jSONObject.put("buildTime", BidiFormatter.getInstance().unicodeWrap(Build.VERSION.INCREMENTAL));
                jSONObject.put("accountVersionName", this.mAccountVersionName);
                jSONObject.put("accountVersionCode", this.mAccountVersionCode);
            }
            hashMap.put("biz_content", jSONObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        hashMap.put("sign", SignUtils.rsa256Sign(SignUtils.getSignContent(hashMap)));
        OPOkHttpUtils.getInstance().postDataAsyn(isH2Show ? CN_MEMBER_API_URL : MEMBER_API_URL, hashMap, this);
    }

    private String getOSVersion() {
        try {
            String unicodeWrap = BidiFormatter.getInstance().unicodeWrap(Build.DISPLAY);
            if (OPUtils.isSM8X50Products()) {
                return SystemProperties.get("ro.rom.version", this.mContext.getResources().getString(C0017R$string.device_info_default));
            }
            return unicodeWrap;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private String getAccountTokenForProvider(Context context, String str) {
        Bundle bundle;
        String str2;
        Bundle bundle2 = new Bundle();
        bundle2.putString(EXTRA_PACKAGE_NAME, context.getPackageName());
        try {
            bundle = context.getContentResolver().call(Uri.parse(ACCOUNT_PROVIDER_URL), str, "", bundle2);
        } catch (Exception e) {
            e.printStackTrace();
            bundle = null;
        }
        if (bundle == null) {
            return "";
        }
        if (KEY_GET_OP_ACCOUNT_TOKEN.equals(str)) {
            str2 = bundle.getString(USER_DATA_ONEPLUS_TOKEN, "");
            if (TextUtils.isEmpty(str2)) {
                Log.i(TAG, "op token is null");
            }
        } else {
            str2 = bundle.getString(USER_DATA_HT_TOKEN, "");
            if (TextUtils.isEmpty(str2)) {
                Log.i(TAG, "ht token is null");
            }
        }
        TextUtils.isEmpty(str2);
        return str2;
    }

    @Override // com.oneplus.settings.utils.OPOkHttpUtils.NetCall
    public void success(Call call, Response response) throws IOException {
        try {
            JSONObject jSONObject = new JSONObject(response.body().string());
            if (jSONObject.getString(RET).equals(SUCCESS)) {
                Log.d(TAG, "get data success");
                JSONObject jSONObject2 = jSONObject.getJSONObject(DATA);
                String str = "";
                String string = jSONObject2.isNull(TITLE) ? str : jSONObject2.getString(TITLE);
                String string2 = jSONObject2.isNull(CONTENT) ? str : jSONObject2.getString(CONTENT);
                String string3 = jSONObject2.isNull(AVATAR) ? str : jSONObject2.getString(AVATAR);
                String string4 = jSONObject2.isNull(ICON) ? str : jSONObject2.getString(ICON);
                if (!jSONObject2.isNull(NEW_VERSION)) {
                    str = String.valueOf(jSONObject2.getDouble(NEW_VERSION));
                }
                HashMap hashMap = new HashMap();
                hashMap.put("member_title", string);
                hashMap.put("member_content", string2);
                hashMap.put("member_avatar", string3);
                hashMap.put("member_icon", string4);
                hashMap.put("member_new_version", str);
                hashMap.put(USER_DATA_ONEPLUS_TOKEN, getAccountTokenForProvider(this.mContext, KEY_GET_OP_ACCOUNT_TOKEN));
                this.mOPMemberPreference.setData(hashMap);
            } else if (this.errCount == 0) {
                this.errCount++;
                getAccessToken();
            } else {
                Log.i(TAG, jSONObject.getString(ERR_CODE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        response.close();
    }

    @Override // com.oneplus.settings.utils.OPOkHttpUtils.NetCall
    public void failed(Call call, IOException iOException) {
        iOException.printStackTrace();
    }
}
