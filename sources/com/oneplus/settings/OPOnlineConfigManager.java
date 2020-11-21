package com.oneplus.settings;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.C0003R$array;
import com.android.settingslib.utils.ThreadUtils;
import com.oneplus.config.ConfigGrabber;
import com.oneplus.config.ConfigObserver;
import com.oneplus.settings.utils.OPPrefUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OPOnlineConfigManager {
    private static boolean isSupportEnable = false;
    private static List<String> localMultiAppWhiteList;
    private static List<String> localSlaDownloadWhiteList;
    private static Object lock = new Object();
    private static List<String> multiAppWhiteList = new ArrayList();
    private static OPOnlineConfigManager onlineConfigManager;
    private static List<String> slaDownloadWhiteList = new ArrayList();
    private ConfigObserver mBackgroundConfigObserver;
    private Context mContext;
    private Handler mHandler = new Handler(this.mHandlerThread.getLooper()) {
        /* class com.oneplus.settings.OPOnlineConfigManager.AnonymousClass1 */

        public void handleMessage(Message message) {
            Log.d("OPOnlineConfigManager", "settings handleMessage....");
            int i = message.what;
            if (i == 1) {
                OPOnlineConfigManager.this.parseConfigFromJson(new ConfigGrabber(OPOnlineConfigManager.this.mContext, "ROM_APP_OPSettings").grabConfig());
            } else if (i == 2) {
                OPOnlineConfigManager.this.parseSlaConfigFromJson(new ConfigGrabber(OPOnlineConfigManager.this.mContext, "SlaOnlineConfig").grabConfig());
            }
        }
    };
    private HandlerThread mHandlerThread;
    OnSupportConfigCompleteParseListener mOnConfigCompleteParseListener;
    private ConfigObserver mSlaBackgroundConfigObserver;

    public interface OnSupportConfigCompleteParseListener {
        void OnSupportConfigParseCompleted();
    }

    private OPOnlineConfigManager(Context context) {
        this.mContext = context;
        HandlerThread handlerThread = new HandlerThread("OPOnlineConfigManager");
        this.mHandlerThread = handlerThread;
        handlerThread.start();
        localMultiAppWhiteList = Arrays.asList(context.getResources().getStringArray(C0003R$array.op_multiapp_white_list));
        localSlaDownloadWhiteList = Arrays.asList(context.getResources().getStringArray(C0003R$array.op_local_sla_down_load_white_list_app));
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void parseSlaConfigFromJson(JSONArray jSONArray) {
        if (jSONArray == null) {
            String string = OPPrefUtil.getString("SlaDownloadWhiteList", null);
            if (Build.DEBUG_ONEPLUS) {
                Log.d("OPOnlineConfigManager", "get Sla Online Config settings parseSlaConfigFromJson jsonArray is null,use old json:" + string);
            }
            if (string != null) {
                try {
                    jSONArray = new JSONArray(string);
                } catch (JSONException e) {
                    Log.e("OPOnlineConfigManager", "get Sla Online Config settings parseSlaConfigFromJson JSONException:" + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                Log.d("OPOnlineConfigManager", "get Sla Online Config settings parseSlaConfigFromJson jsonArray is null, return");
                return;
            }
        }
        if (Build.DEBUG_ONEPLUS && jSONArray != null) {
            Log.d("OPOnlineConfigManager", "get Sla Online Config jsonArray=" + jSONArray.toString());
        }
        for (int i = 0; i < jSONArray.length(); i++) {
            try {
                JSONObject jSONObject = jSONArray.getJSONObject(i);
                if (jSONObject.has("name") && jSONObject.getString("name").equals("SlaDownloadWhiteList")) {
                    JSONArray jSONArray2 = jSONObject.getJSONArray("value");
                    OPPrefUtil.putString("SlaDownloadWhiteList", jSONArray2.toString());
                    synchronized (slaDownloadWhiteList) {
                        slaDownloadWhiteList.clear();
                        for (int i2 = 0; i2 < jSONArray2.length(); i2++) {
                            slaDownloadWhiteList.add(jSONArray2.getString(i2));
                        }
                    }
                }
            } catch (JSONException e2) {
                Log.e("OPOnlineConfigManager", "get Sla Online Config settings parseSlaConfigFromJson JSONException:" + e2.getMessage());
                e2.printStackTrace();
                return;
            } catch (Exception e3) {
                Log.e("OPOnlineConfigManager", "get Sla Online Config settings parseSlaConfigFromJson Exception:" + e3.getMessage());
                e3.printStackTrace();
                return;
            }
        }
        deleteNoWhiteListSlaDownLoadOpenApp();
    }

    public static List<String> getSlaDownloadWhiteList() {
        synchronized (slaDownloadWhiteList) {
            if (!slaDownloadWhiteList.isEmpty()) {
                return slaDownloadWhiteList;
            }
            return localSlaDownloadWhiteList;
        }
    }

    public static boolean isSupportEnable() {
        return isSupportEnable;
    }

    public static List<String> getMultiAppWhiteList() {
        synchronized (multiAppWhiteList) {
            if (!multiAppWhiteList.isEmpty()) {
                return multiAppWhiteList;
            }
            return localMultiAppWhiteList;
        }
    }

    public static synchronized OPOnlineConfigManager getInstence(Context context) {
        OPOnlineConfigManager oPOnlineConfigManager;
        synchronized (OPOnlineConfigManager.class) {
            synchronized (lock) {
                if (onlineConfigManager == null) {
                    onlineConfigManager = new OPOnlineConfigManager(context);
                }
            }
            oPOnlineConfigManager = onlineConfigManager;
        }
        return oPOnlineConfigManager;
    }

    public void init() {
        ConfigObserver configObserver = new ConfigObserver(this.mContext, this.mHandler, new BackgroundConfigUpdater(), "ROM_APP_OPSettings");
        this.mBackgroundConfigObserver = configObserver;
        configObserver.register();
        this.mHandler.sendEmptyMessageDelayed(1, 100);
        ConfigObserver configObserver2 = new ConfigObserver(this.mContext, this.mHandler, new SLABackgroundConfigUpdater(), "SlaOnlineConfig");
        this.mSlaBackgroundConfigObserver = configObserver2;
        configObserver2.register();
        this.mHandler.sendEmptyMessageDelayed(2, 100);
    }

    /* access modifiers changed from: package-private */
    public class SLABackgroundConfigUpdater implements ConfigObserver.ConfigUpdater {
        SLABackgroundConfigUpdater() {
        }

        public void updateConfig(JSONArray jSONArray) {
            Log.d("OPOnlineConfigManager", "SLABackgroundConfigUpdater");
            OPOnlineConfigManager.this.parseSlaConfigFromJson(jSONArray);
        }
    }

    /* access modifiers changed from: package-private */
    public class BackgroundConfigUpdater implements ConfigObserver.ConfigUpdater {
        BackgroundConfigUpdater() {
        }

        public void updateConfig(JSONArray jSONArray) {
            OPOnlineConfigManager.this.parseConfigFromJson(jSONArray);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void parseConfigFromJson(JSONArray jSONArray) {
        if (jSONArray == null) {
            String string = OPPrefUtil.getString("op_multiapp_white_list_p", null);
            if (Build.DEBUG_ONEPLUS) {
                Log.d("OPOnlineConfigManager", "settings parseConfigFromJson jsonArray is null,use old json:" + string);
            }
            if (string != null) {
                try {
                    jSONArray = new JSONArray(string);
                } catch (JSONException e) {
                    Log.e("OPOnlineConfigManager", "settings parseConfigFromJson JSONException:" + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                Log.d("OPOnlineConfigManager", "settings parseConfigFromJson jsonArray is null, return");
                return;
            }
        }
        if (Build.DEBUG_ONEPLUS && jSONArray != null) {
            Log.d("OPOnlineConfigManager", "settings parseConfigFromJson jsonArray=" + jSONArray.toString());
        }
        for (int i = 0; i < jSONArray.length(); i++) {
            try {
                JSONObject jSONObject = jSONArray.getJSONObject(i);
                if (jSONObject.has("name") && jSONObject.getString("name").equals("op_multiapp_white_list_p")) {
                    JSONArray jSONArray2 = jSONObject.getJSONArray("value");
                    OPPrefUtil.putString("op_multiapp_white_list_p", jSONArray2.toString());
                    synchronized (multiAppWhiteList) {
                        multiAppWhiteList.clear();
                        for (int i2 = 0; i2 < jSONArray2.length(); i2++) {
                            multiAppWhiteList.add(jSONArray2.getString(i2));
                        }
                    }
                }
                if (jSONObject.has("need_show")) {
                    OPPrefUtil.putString("need_show", jSONObject.toString());
                    isSupportEnable = jSONObject.getBoolean("need_show");
                    ThreadUtils.postOnMainThread(new Runnable() {
                        /* class com.oneplus.settings.$$Lambda$OPOnlineConfigManager$5pLnhywe5rGpEt08hGrm0Px0kGs */

                        public final void run() {
                            OPOnlineConfigManager.this.lambda$parseConfigFromJson$0$OPOnlineConfigManager();
                        }
                    });
                }
            } catch (JSONException e2) {
                Log.e("OPOnlineConfigManager", "settings parseConfigFromJson JSONException:" + e2.getMessage());
                return;
            } catch (Exception e3) {
                Log.e("OPOnlineConfigManager", "settings parseConfigFromJson Exception:" + e3.getMessage());
                return;
            }
        }
    }

    private String getSlaDownLoadOpenAppsListString() {
        return Settings.System.getString(this.mContext.getContentResolver(), "sla_download_open_apps_list");
    }

    public void saveSlaDownLoadOpenAppsListStrings(String str) {
        Settings.System.putString(this.mContext.getContentResolver(), "sla_download_open_apps_list", str);
    }

    private void deleteNoWhiteListSlaDownLoadOpenApp() {
        String string = Settings.System.getString(this.mContext.getContentResolver(), "sla_download_open_apps_list");
        if (!TextUtils.isEmpty(string)) {
            getSlaDownloadWhiteList();
            String[] split = string.split(";", -1);
            for (String str : split) {
                if (!TextUtils.isEmpty(str) && !slaDownloadWhiteList.contains(str)) {
                    StringBuilder sb = new StringBuilder(getSlaDownLoadOpenAppsListString());
                    String str2 = str + ";";
                    int indexOf = sb.indexOf(str2);
                    sb.delete(indexOf, str2.length() + indexOf);
                    saveSlaDownLoadOpenAppsListStrings(sb.toString());
                }
            }
        }
    }

    public void setOnConfigCompleteParseListener(OnSupportConfigCompleteParseListener onSupportConfigCompleteParseListener) {
        this.mOnConfigCompleteParseListener = onSupportConfigCompleteParseListener;
    }

    /* access modifiers changed from: private */
    /* renamed from: supportConfigParseCompleted */
    public void lambda$parseConfigFromJson$0() {
        OnSupportConfigCompleteParseListener onSupportConfigCompleteParseListener = this.mOnConfigCompleteParseListener;
        if (onSupportConfigCompleteParseListener != null) {
            onSupportConfigCompleteParseListener.OnSupportConfigParseCompleted();
        }
    }
}
