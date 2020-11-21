package com.google.analytics.tracking.android;

import android.content.Context;
import android.text.TextUtils;
import com.google.android.gms.analytics.internal.Command;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/* access modifiers changed from: package-private */
public class GAThread extends Thread implements AnalyticsThread {
    private static GAThread sInstance;
    private volatile String mClientId;
    private volatile boolean mClosed = false;
    private volatile List<Command> mCommands;
    private final Context mContext;
    private volatile boolean mDisabled = false;
    private volatile String mInstallCampaign;
    private volatile ServiceProxy mServiceProxy;
    private final LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();

    @Override // com.google.analytics.tracking.android.AnalyticsThread
    public Thread getThread() {
        return this;
    }

    static GAThread getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new GAThread(context);
        }
        return sInstance;
    }

    private GAThread(Context context) {
        super("GAThread");
        if (context != null) {
            this.mContext = context.getApplicationContext();
        } else {
            this.mContext = context;
        }
        start();
    }

    GAThread(Context context, ServiceProxy serviceProxy) {
        super("GAThread");
        if (context != null) {
            this.mContext = context.getApplicationContext();
        } else {
            this.mContext = context;
        }
        this.mServiceProxy = serviceProxy;
        start();
    }

    /* access modifiers changed from: protected */
    public void init() {
        this.mServiceProxy.createService();
        this.mCommands = new ArrayList();
        this.mCommands.add(new Command("appendVersion", "_v", "ma3.0.2"));
        this.mCommands.add(new Command("appendQueueTime", "qt", null));
        this.mCommands.add(new Command("appendCacheBuster", "z", null));
    }

    @Override // com.google.analytics.tracking.android.AnalyticsThread
    public void sendHit(Map<String, String> map) {
        final HashMap hashMap = new HashMap(map);
        String str = map.get("&ht");
        if (str != null) {
            try {
                Long.valueOf(str).longValue();
            } catch (NumberFormatException unused) {
                str = null;
            }
        }
        if (str == null) {
            hashMap.put("&ht", Long.toString(System.currentTimeMillis()));
        }
        queueToThread(new Runnable() {
            /* class com.google.analytics.tracking.android.GAThread.AnonymousClass1 */

            public void run() {
                if (TextUtils.isEmpty((CharSequence) hashMap.get("&cid"))) {
                    hashMap.put("&cid", GAThread.this.mClientId);
                }
                if (!GoogleAnalytics.getInstance(GAThread.this.mContext).getAppOptOut() && !GAThread.this.isSampledOut(hashMap)) {
                    if (!TextUtils.isEmpty(GAThread.this.mInstallCampaign)) {
                        GAUsage.getInstance().setDisableUsage(true);
                        Map map = hashMap;
                        MapBuilder mapBuilder = new MapBuilder();
                        mapBuilder.setCampaignParamsFromUrl(GAThread.this.mInstallCampaign);
                        map.putAll(mapBuilder.build());
                        GAUsage.getInstance().setDisableUsage(false);
                        GAThread.this.mInstallCampaign = null;
                    }
                    GAThread.this.fillAppParameters(hashMap);
                    GAThread.this.mServiceProxy.putHit(HitBuilder.generateHitParams(hashMap), Long.valueOf((String) hashMap.get("&ht")).longValue(), GAThread.this.getUrlScheme(hashMap), GAThread.this.mCommands);
                }
            }
        });
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private String getUrlScheme(Map<String, String> map) {
        if (!map.containsKey("useSecure") || Utils.safeParseBoolean(map.get("useSecure"), true)) {
            return "https:";
        }
        return "http:";
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private boolean isSampledOut(Map<String, String> map) {
        if (map.get("&sf") == null) {
            return false;
        }
        double safeParseDouble = Utils.safeParseDouble(map.get("&sf"), 100.0d);
        if (safeParseDouble >= 100.0d || ((double) (hashClientIdForSampling(map.get("&cid")) % 10000)) < safeParseDouble * 100.0d) {
            return false;
        }
        Log.v(String.format("%s hit sampled out", map.get("&t") == null ? "unknown" : map.get("&t")));
        return true;
    }

    static int hashClientIdForSampling(String str) {
        int i = 1;
        if (!TextUtils.isEmpty(str)) {
            i = 0;
            for (int length = str.length() - 1; length >= 0; length--) {
                char charAt = str.charAt(length);
                i = ((i << 6) & 268435455) + charAt + (charAt << 14);
                int i2 = 266338304 & i;
                if (i2 != 0) {
                    i = (i2 >> 21) ^ i;
                }
            }
        }
        return i;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void fillAppParameters(Map<String, String> map) {
        AppFieldsDefaultProvider provider = AppFieldsDefaultProvider.getProvider();
        Utils.putIfAbsent(map, "&an", provider.getValue("&an"));
        Utils.putIfAbsent(map, "&av", provider.getValue("&av"));
        Utils.putIfAbsent(map, "&aid", provider.getValue("&aid"));
        Utils.putIfAbsent(map, "&aiid", provider.getValue("&aiid"));
        map.put("&v", "1");
    }

    @Override // com.google.analytics.tracking.android.AnalyticsThread
    public void dispatch() {
        queueToThread(new Runnable() {
            /* class com.google.analytics.tracking.android.GAThread.AnonymousClass2 */

            public void run() {
                GAThread.this.mServiceProxy.dispatch();
            }
        });
    }

    @Override // com.google.analytics.tracking.android.AnalyticsThread
    public void setForceLocalDispatch() {
        queueToThread(new Runnable() {
            /* class com.google.analytics.tracking.android.GAThread.AnonymousClass4 */

            public void run() {
                GAThread.this.mServiceProxy.setForceLocalDispatch();
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void queueToThread(Runnable runnable) {
        this.queue.add(runnable);
    }

    static String getAndClearCampaign(Context context) {
        try {
            FileInputStream openFileInput = context.openFileInput("gaInstallData");
            byte[] bArr = new byte[8192];
            int read = openFileInput.read(bArr, 0, 8192);
            if (openFileInput.available() > 0) {
                Log.e("Too much campaign data, ignoring it.");
                openFileInput.close();
                context.deleteFile("gaInstallData");
                return null;
            }
            openFileInput.close();
            context.deleteFile("gaInstallData");
            if (read <= 0) {
                Log.w("Campaign file is empty.");
                return null;
            }
            String str = new String(bArr, 0, read);
            Log.i("Campaign found: " + str);
            return str;
        } catch (FileNotFoundException unused) {
            Log.i("No campaign data found.");
            return null;
        } catch (IOException unused2) {
            Log.e("Error reading campaign data.");
            context.deleteFile("gaInstallData");
            return null;
        }
    }

    private String printStackTrace(Throwable th) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(byteArrayOutputStream);
        th.printStackTrace(printStream);
        printStream.flush();
        return new String(byteArrayOutputStream.toByteArray());
    }

    public void run() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException unused) {
            Log.w("sleep interrupted in GAThread initialize");
        }
        try {
            if (this.mServiceProxy == null) {
                this.mServiceProxy = new GAServiceProxy(this.mContext, this);
            }
            init();
            this.mClientId = ClientIdDefaultProvider.getProvider().getValue("&cid");
            this.mInstallCampaign = getAndClearCampaign(this.mContext);
        } catch (Throwable th) {
            Log.e("Error initializing the GAThread: " + printStackTrace(th));
            Log.e("Google Analytics will not start up.");
            this.mDisabled = true;
        }
        while (!this.mClosed) {
            try {
                Runnable take = this.queue.take();
                if (!this.mDisabled) {
                    take.run();
                }
            } catch (InterruptedException e) {
                Log.i(e.toString());
            } catch (Throwable th2) {
                Log.e("Error on GAThread: " + printStackTrace(th2));
                Log.e("Google Analytics is shutting down.");
                this.mDisabled = true;
            }
        }
    }

    @Override // com.google.analytics.tracking.android.AnalyticsThread
    public LinkedBlockingQueue<Runnable> getQueue() {
        return this.queue;
    }

    /* access modifiers changed from: package-private */
    public void close() {
        this.mClosed = true;
        interrupt();
    }

    /* access modifiers changed from: package-private */
    public boolean isDisabled() {
        return this.mDisabled;
    }
}
