package com.oneplus.settings.backgroundoptimize;

import android.content.Context;
import android.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BgOActivityManager {
    private static final String TAG = "BgOActivityManager";
    private static volatile BgOActivityManager instance;
    private Method getAllAppControlModes;
    private Method getAppControlMode;
    private Method setAppControlMode;

    private BgOActivityManager(Context context) {
        try {
            Class<?> cls = Class.forName("com.oneplus.appboot.AppControlModeManager");
            this.getAllAppControlModes = cls.getDeclaredMethod("getAllAppControlModes", Integer.TYPE);
            this.getAppControlMode = cls.getDeclaredMethod("getAppControlMode", String.class, Integer.TYPE);
            this.setAppControlMode = cls.getDeclaredMethod("setAppControlMode", String.class, Integer.TYPE, Integer.TYPE);
            cls.getDeclaredMethod("getAppControlState", Integer.TYPE);
            cls.getDeclaredMethod("setAppControlState", Integer.TYPE, Integer.TYPE);
        } catch (Exception e) {
            String str = TAG;
            Log.e(str, "BgOActivityManager Exception=" + e);
            e.printStackTrace();
        }
    }

    public static BgOActivityManager getInstance(Context context) {
        if (instance == null) {
            synchronized (BgOActivityManager.class) {
                if (instance == null) {
                    instance = new BgOActivityManager(context);
                }
            }
        }
        return instance;
    }

    private List<AppControlMode> convert(List<?> list) {
        ArrayList arrayList = new ArrayList();
        Field field = null;
        Field field2 = null;
        Field field3 = null;
        for (Object obj : list) {
            if (field == null) {
                try {
                    field = obj.getClass().getField("packageName");
                    field.setAccessible(true);
                    field2 = obj.getClass().getField("mode");
                    field2.setAccessible(true);
                    field3 = obj.getClass().getField("value");
                    field3.setAccessible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                String str = (String) field.get(obj);
                int intValue = ((Integer) field2.get(obj)).intValue();
                int intValue2 = ((Integer) field3.get(obj)).intValue();
                Logutil.loge(TAG, "AppControl # convert: " + str + ", mode=" + intValue + ", value=" + intValue2);
                arrayList.add(new AppControlMode(str, intValue, intValue2));
            } catch (IllegalArgumentException e2) {
                e2.printStackTrace();
            } catch (IllegalAccessException e3) {
                e3.printStackTrace();
            } catch (Exception e4) {
                e4.printStackTrace();
            }
        }
        return arrayList;
    }

    public Map<String, AppControlMode> getAllAppControlModesMap(int i) {
        List<AppControlMode> allAppControlModes = getAllAppControlModes(i);
        HashMap hashMap = new HashMap();
        for (AppControlMode appControlMode : allAppControlModes) {
            hashMap.put(appControlMode.packageName, appControlMode);
        }
        return hashMap;
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x0020  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<com.oneplus.settings.backgroundoptimize.AppControlMode> getAllAppControlModes(int r4) {
        /*
            r3 = this;
            java.lang.reflect.Method r0 = r3.getAllAppControlModes
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r2 = 0
            r1[r2] = r4
            java.lang.Object r4 = r3.invoke(r0, r1)
            if (r4 == 0) goto L_0x001d
            java.util.List r4 = (java.util.List) r4     // Catch:{ Exception -> 0x0019 }
            java.util.List r3 = r3.convert(r4)     // Catch:{ Exception -> 0x0019 }
            goto L_0x001e
        L_0x0019:
            r3 = move-exception
            r3.printStackTrace()
        L_0x001d:
            r3 = 0
        L_0x001e:
            if (r3 != 0) goto L_0x0025
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
        L_0x0025:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oneplus.settings.backgroundoptimize.BgOActivityManager.getAllAppControlModes(int):java.util.List");
    }

    public int getAppControlMode(String str, int i) {
        int i2 = 0;
        Object invoke = invoke(this.getAppControlMode, str, Integer.valueOf(i));
        if (invoke != null) {
            i2 = ((Integer) invoke).intValue();
        }
        String str2 = TAG;
        Logutil.loge(str2, "AppControl # getAppControlMode packageName: " + str + ", mode=" + i + ", value=" + i2);
        return i2;
    }

    public int setAppControlMode(String str, int i, int i2) {
        String str2 = TAG;
        Logutil.loge(str2, "AppControl # setAppControlMode packageName: " + str + ", mode=" + i + ", value=" + i2);
        invoke(this.setAppControlMode, str, Integer.valueOf(i), Integer.valueOf(i2));
        return 0;
    }

    public Object invoke(Method method, Object... objArr) {
        if (method == null) {
            return null;
        }
        try {
            return method.invoke(null, objArr);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
            return null;
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
            return null;
        }
    }
}
