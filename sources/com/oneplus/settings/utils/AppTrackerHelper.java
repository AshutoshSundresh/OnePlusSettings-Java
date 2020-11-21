package com.oneplus.settings.utils;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import com.oneplus.settings.SettingsBaseApplication;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class AppTrackerHelper {
    private static Object mAppTrackerClass;
    private static Object mLock = new byte[0];
    private static AppTrackerHelper sInstance;

    private AppTrackerHelper() {
    }

    public static AppTrackerHelper getInstance() {
        if (sInstance == null) {
            synchronized (mLock) {
                if (sInstance == null) {
                    sInstance = new AppTrackerHelper();
                }
            }
        }
        return sInstance;
    }

    public void putAnalytics(String str, String str2, String str3) {
        if (!isAllowSendAppTracker(SettingsBaseApplication.mApplication)) {
            Log.w("AppTrackerHelper", "isAllowSendAppTracker is false.");
        } else {
            SettingsBaseApplication.getHandler().post(new Runnable(str, str2, str3) {
                /* class com.oneplus.settings.utils.$$Lambda$AppTrackerHelper$_s0fFsJWgp1rjFX9ZrkdRRKYemY */
                public final /* synthetic */ String f$0;
                public final /* synthetic */ String f$1;
                public final /* synthetic */ String f$2;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    AppTrackerHelper.lambda$putAnalytics$0(this.f$0, this.f$1, this.f$2);
                }
            });
        }
    }

    public void putAnalytics(String str, String str2, String str3, String str4) {
        if (!isAllowSendAppTracker(SettingsBaseApplication.mApplication)) {
            Log.w("AppTrackerHelper", "isAllowSendAppTracker is false.");
        } else {
            SettingsBaseApplication.getHandler().post(new Runnable(str, str2, str3, str4) {
                /* class com.oneplus.settings.utils.$$Lambda$AppTrackerHelper$jYhu3qdFK7E64eXWTcZYdb9nP0 */
                public final /* synthetic */ String f$0;
                public final /* synthetic */ String f$1;
                public final /* synthetic */ String f$2;
                public final /* synthetic */ String f$3;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    AppTrackerHelper.lambda$putAnalytics$1(this.f$0, this.f$1, this.f$2, this.f$3);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public static void sendAppTracker(Context context, String str, String str2, String str3, String str4) {
        Method declaredMethod;
        if (Build.VERSION.SDK_INT >= 29) {
            if (mAppTrackerClass == null) {
                try {
                    Class<?> cls = Class.forName("net.oneplus.odm.OpDeviceManagerInjector");
                    if (!(cls == null || (declaredMethod = cls.getDeclaredMethod("getInstance", new Class[0])) == null)) {
                        declaredMethod.setAccessible(true);
                        mAppTrackerClass = declaredMethod.invoke(null, new Object[0]);
                    }
                } catch (Exception e) {
                    Log.e("AppTrackerUtil", "New instance AppTracker class exception." + e.getMessage());
                }
            }
            if (mAppTrackerClass != null) {
                HashMap hashMap = new HashMap();
                hashMap.put(str3, str4);
                HashMap hashMap2 = new HashMap();
                hashMap2.put("appid", str);
                try {
                    Method declaredMethod2 = mAppTrackerClass.getClass().getDeclaredMethod("preserveAppData", Context.class, String.class, Map.class, Map.class);
                    if (declaredMethod2 != null) {
                        declaredMethod2.setAccessible(true);
                        declaredMethod2.invoke(mAppTrackerClass, context, str2, hashMap, hashMap2);
                    }
                } catch (Exception e2) {
                    Log.e("AppTrackerUtil", "invoke method onEvent exception." + e2.getMessage());
                }
            }
        } else {
            if (mAppTrackerClass == null) {
                try {
                    Constructor<?> constructor = Class.forName("net.oneplus.odm.insight.tracker.AppTracker").getConstructor(Context.class, String.class);
                    constructor.setAccessible(true);
                    mAppTrackerClass = constructor.newInstance(context, str);
                } catch (Exception e3) {
                    e3.printStackTrace();
                }
            }
            if (mAppTrackerClass != null) {
                HashMap hashMap3 = new HashMap();
                hashMap3.put(str3, str4);
                try {
                    Method declaredMethod3 = mAppTrackerClass.getClass().getDeclaredMethod("onEvent", String.class, Map.class);
                    declaredMethod3.setAccessible(true);
                    declaredMethod3.invoke(mAppTrackerClass, str2, hashMap3);
                } catch (Exception e4) {
                    e4.printStackTrace();
                }
            }
        }
    }

    private static boolean isAllowSendAppTracker(Context context) {
        return context != null && Settings.System.getInt(context.getContentResolver(), "oem_join_user_plan_settings", 0) == 1;
    }
}
