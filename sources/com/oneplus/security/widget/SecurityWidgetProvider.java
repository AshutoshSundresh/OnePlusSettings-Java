package com.oneplus.security.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.UserManager;
import com.oneplus.security.SecureService;
import com.oneplus.security.utils.LogUtils;
import com.oneplus.security.utils.SharedPreferenceHelper;

public class SecurityWidgetProvider extends AppWidgetProvider {
    public void onReceive(Context context, Intent intent) {
        UserManager userManager = (UserManager) context.getSystemService(UserManager.class);
        if (userManager == null || userManager.isUserUnlocked()) {
            SecureService.startService(context.getApplicationContext());
            String action = intent.getAction();
            LogUtils.d("SecurityWidgetProvider", "--------onReceive-----------action=" + action);
            if ("com.oneplus.security.action.DATAUSAGE_CHANGED".equals(action)) {
                int[] appWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, SecurityWidgetProvider.class));
                if (appWidgetIds == null || appWidgetIds.length <= 0) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Security widgets length is not > 0, widgets.length:");
                    sb.append(appWidgetIds == null ? 0 : appWidgetIds.length);
                    LogUtils.d("SecurityWidgetProvider", sb.toString());
                } else {
                    WidgetViewService.startService(context.getApplicationContext());
                }
            }
            super.onReceive(context, intent);
        }
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] iArr) {
        LogUtils.d("SecurityWidgetProvider", "--------onUpdate-----------");
        WidgetViewService.startService(context.getApplicationContext());
        SecureService.startService(context.getApplicationContext());
        super.onUpdate(context, appWidgetManager, iArr);
    }

    public void onEnabled(Context context) {
        LogUtils.d("SecurityWidgetProvider", "--------onEnabled-----------");
        super.onEnabled(context);
    }

    public void onDeleted(Context context, int[] iArr) {
        LogUtils.d("SecurityWidgetProvider", "--------onDeleted-----------");
        super.onDeleted(context, iArr);
    }

    public void onDisabled(Context context) {
        LogUtils.d("SecurityWidgetProvider", "--------onDisabled-----------");
        WidgetViewService.stoptService(context.getApplicationContext());
        super.onDisabled(context);
    }

    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int i, Bundle bundle) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, i, bundle);
        if (bundle.getBoolean("BOUND_ON_SHELF")) {
            SharedPreferenceHelper.putInt("shelf_widget_id", i);
            LogUtils.i("SecurityWidgetProvider", "onAppWidgetOptionsChanged onShelfWidgetId :" + i);
            WidgetViewService.startService(context.getApplicationContext());
        }
    }

    public static void notifyDataUsage(Context context) {
        LogUtils.d("SecurityWidgetProvider", "--------sendBroadcast-----------");
        Intent intent = new Intent("com.oneplus.security.action.DATAUSAGE_CHANGED");
        if (Build.VERSION.SDK_INT >= 26) {
            intent.addFlags(285212672);
        }
        context.sendBroadcast(intent);
    }
}
