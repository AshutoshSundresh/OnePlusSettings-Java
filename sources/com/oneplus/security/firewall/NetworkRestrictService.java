package com.oneplus.security.firewall;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.INetworkManagementService;
import android.os.Looper;
import android.os.Message;
import android.os.ServiceManager;
import android.text.TextUtils;
import android.util.Log;
import com.oneplus.security.receiver.NetworkStateUtils;
import com.oneplus.security.utils.LogUtils;
import com.oneplus.security.utils.Utils;
import java.lang.ref.WeakReference;

public class NetworkRestrictService extends Service {
    private Context mContext;
    private Handler mHandler;
    private HandlerThread mHandlerThread;
    private boolean mIsFirewallEnabled;
    private INetworkManagementService mNetworkManagementService;
    private PackageManager mPackageManager;

    public IBinder onBind(Intent intent) {
        return null;
    }

    static class FirewallRuleHandler extends Handler {
        private final WeakReference<NetworkRestrictService> serviceReference;

        public FirewallRuleHandler(Looper looper, NetworkRestrictService networkRestrictService) {
            super(looper);
            this.serviceReference = new WeakReference<>(networkRestrictService);
        }

        public void handleMessage(Message message) {
            NetworkRestrictService networkRestrictService = this.serviceReference.get();
            if (networkRestrictService != null) {
                int i = message.what;
                if (i != 1) {
                    if (i == 2) {
                        LogUtils.d("NetworkRestrictService", "service.stopSelf()");
                        networkRestrictService.stopSelf();
                    }
                } else if (!NetworkStateUtils.isNetWorkAvailable(networkRestrictService)) {
                    Log.d("NetworkRestrictService", "NetWork is not available");
                } else {
                    networkRestrictService.applyFirewallRule(NetworkStateUtils.currentNetWorkIsWlan(networkRestrictService));
                }
            }
        }
    }

    public void onCreate() {
        super.onCreate();
        this.mContext = this;
        HandlerThread handlerThread = new HandlerThread("NetworkRestrictService");
        this.mHandlerThread = handlerThread;
        handlerThread.start();
        this.mHandler = new FirewallRuleHandler(this.mHandlerThread.getLooper(), this);
        this.mPackageManager = getPackageManager();
        INetworkManagementService asInterface = INetworkManagementService.Stub.asInterface(ServiceManager.getService("network_management"));
        this.mNetworkManagementService = asInterface;
        try {
            this.mIsFirewallEnabled = asInterface.isFirewallEnabled();
            LogUtils.d("NetworkRestrictService", "mIsFirewallEnabled=" + this.mIsFirewallEnabled);
        } catch (Exception e) {
            LogUtils.e("NetworkRestrictService", "onCreate mNetworkManagementService.isFirewallEnabled() error:" + e.getMessage());
        }
    }

    public void onDestroy() {
        HandlerThread handlerThread = this.mHandlerThread;
        if (handlerThread != null) {
            handlerThread.quit();
            this.mHandlerThread = null;
        }
        super.onDestroy();
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        if (intent != null) {
            String action = intent.getAction();
            if ("com.oneplus.security.action.UPDATE_RULES".equals(action)) {
                saveFirewallRule(intent);
            } else if ("com.oneplus.security.action.APPLY_RULE".equals(action)) {
                sendHandlerMessage(1, 2000);
            }
        }
        return super.onStartCommand(intent, i, i2);
    }

    private void sendHandlerMessage(int i, long j) {
        this.mHandler.removeMessages(i);
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(i), j);
    }

    public static void applyRules(Context context) {
        if (Utils.currentUserIsOwner()) {
            Intent intent = new Intent(context, NetworkRestrictService.class);
            intent.setAction("com.oneplus.security.action.APPLY_RULE");
            context.startService(intent);
        }
    }

    private void saveFirewallRule(Intent intent) {
        String str;
        try {
            str = intent.getStringExtra("app_package");
        } catch (Exception e) {
            e.printStackTrace();
            str = "";
        }
        if (!TextUtils.isEmpty(str)) {
            FirewallRule.addOrUpdateRole(this.mContext, new FirewallRule(str, Integer.valueOf(intent.getIntExtra("app_role_wifi", 0)), Integer.valueOf(intent.getIntExtra("app_role_mobile", 0))));
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0075, code lost:
        if (r1.getWlan().intValue() != 0) goto L_0x008a;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void applyFirewallRule(boolean r8) {
        /*
        // Method dump skipped, instructions count: 195
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oneplus.security.firewall.NetworkRestrictService.applyFirewallRule(boolean):void");
    }
}
