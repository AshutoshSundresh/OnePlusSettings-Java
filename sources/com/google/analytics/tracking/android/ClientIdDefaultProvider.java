package com.google.analytics.tracking.android;

import android.content.Context;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

class ClientIdDefaultProvider implements DefaultProvider {
    private static ClientIdDefaultProvider sInstance;
    private static final Object sInstanceLock = new Object();
    private String mClientId;
    private boolean mClientIdLoaded = false;
    private final Object mClientIdLock = new Object();
    private final Context mContext;

    public static void initializeProvider(Context context) {
        synchronized (sInstanceLock) {
            if (sInstance == null) {
                sInstance = new ClientIdDefaultProvider(context);
            }
        }
    }

    static void dropInstance() {
        synchronized (sInstanceLock) {
            sInstance = null;
        }
    }

    public static ClientIdDefaultProvider getProvider() {
        ClientIdDefaultProvider clientIdDefaultProvider;
        synchronized (sInstanceLock) {
            clientIdDefaultProvider = sInstance;
        }
        return clientIdDefaultProvider;
    }

    protected ClientIdDefaultProvider(Context context) {
        this.mContext = context;
        asyncInitializeClientId();
    }

    @Override // com.google.analytics.tracking.android.DefaultProvider
    public String getValue(String str) {
        if ("&cid".equals(str)) {
            return blockingGetClientId();
        }
        return null;
    }

    private String blockingGetClientId() {
        if (!this.mClientIdLoaded) {
            synchronized (this.mClientIdLock) {
                if (!this.mClientIdLoaded) {
                    Log.v("Waiting for clientId to load");
                    do {
                        try {
                            this.mClientIdLock.wait();
                        } catch (InterruptedException e) {
                            Log.e("Exception while waiting for clientId: " + e);
                        }
                    } while (!this.mClientIdLoaded);
                }
            }
        }
        Log.v("Loaded clientId");
        return this.mClientId;
    }

    private boolean storeClientId(String str) {
        try {
            Log.v("Storing clientId.");
            FileOutputStream openFileOutput = this.mContext.openFileOutput("gaClientId", 0);
            openFileOutput.write(str.getBytes());
            openFileOutput.close();
            return true;
        } catch (FileNotFoundException unused) {
            Log.e("Error creating clientId file.");
            return false;
        } catch (IOException unused2) {
            Log.e("Error writing to clientId file.");
            return false;
        }
    }

    /* access modifiers changed from: protected */
    public String generateClientId() {
        String lowerCase = UUID.randomUUID().toString().toLowerCase();
        return !storeClientId(lowerCase) ? "0" : lowerCase;
    }

    private void asyncInitializeClientId() {
        new Thread("client_id_fetcher") {
            /* class com.google.analytics.tracking.android.ClientIdDefaultProvider.AnonymousClass1 */

            public void run() {
                synchronized (ClientIdDefaultProvider.this.mClientIdLock) {
                    ClientIdDefaultProvider.this.mClientId = ClientIdDefaultProvider.this.initializeClientId();
                    ClientIdDefaultProvider.this.mClientIdLoaded = true;
                    ClientIdDefaultProvider.this.mClientIdLock.notifyAll();
                }
            }
        }.start();
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x004d  */
    /* JADX WARNING: Removed duplicated region for block: B:20:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String initializeClientId() {
        /*
            r7 = this;
            java.lang.String r0 = "gaClientId"
            r1 = 0
            android.content.Context r2 = r7.mContext     // Catch:{ FileNotFoundException -> 0x004b, IOException -> 0x0041 }
            java.io.FileInputStream r2 = r2.openFileInput(r0)     // Catch:{ FileNotFoundException -> 0x004b, IOException -> 0x0041 }
            r3 = 128(0x80, float:1.794E-43)
            byte[] r4 = new byte[r3]     // Catch:{ FileNotFoundException -> 0x004b, IOException -> 0x0041 }
            r5 = 0
            int r3 = r2.read(r4, r5, r3)     // Catch:{ FileNotFoundException -> 0x004b, IOException -> 0x0041 }
            int r6 = r2.available()     // Catch:{ FileNotFoundException -> 0x004b, IOException -> 0x0041 }
            if (r6 <= 0) goto L_0x0026
            java.lang.String r3 = "clientId file seems corrupted, deleting it."
            com.google.analytics.tracking.android.Log.e(r3)     // Catch:{ FileNotFoundException -> 0x004b, IOException -> 0x0041 }
            r2.close()     // Catch:{ FileNotFoundException -> 0x004b, IOException -> 0x0041 }
            android.content.Context r2 = r7.mContext     // Catch:{ FileNotFoundException -> 0x004b, IOException -> 0x0041 }
            r2.deleteFile(r0)     // Catch:{ FileNotFoundException -> 0x004b, IOException -> 0x0041 }
            goto L_0x004b
        L_0x0026:
            if (r3 > 0) goto L_0x0036
            java.lang.String r3 = "clientId file seems empty, deleting it."
            com.google.analytics.tracking.android.Log.e(r3)     // Catch:{ FileNotFoundException -> 0x004b, IOException -> 0x0041 }
            r2.close()     // Catch:{ FileNotFoundException -> 0x004b, IOException -> 0x0041 }
            android.content.Context r2 = r7.mContext     // Catch:{ FileNotFoundException -> 0x004b, IOException -> 0x0041 }
            r2.deleteFile(r0)     // Catch:{ FileNotFoundException -> 0x004b, IOException -> 0x0041 }
            goto L_0x004b
        L_0x0036:
            java.lang.String r6 = new java.lang.String     // Catch:{ FileNotFoundException -> 0x004b, IOException -> 0x0041 }
            r6.<init>(r4, r5, r3)     // Catch:{ FileNotFoundException -> 0x004b, IOException -> 0x0041 }
            r2.close()     // Catch:{ FileNotFoundException -> 0x003e, IOException -> 0x0040 }
        L_0x003e:
            r1 = r6
            goto L_0x004b
        L_0x0040:
            r1 = r6
        L_0x0041:
            java.lang.String r2 = "Error reading clientId file, deleting it."
            com.google.analytics.tracking.android.Log.e(r2)
            android.content.Context r2 = r7.mContext
            r2.deleteFile(r0)
        L_0x004b:
            if (r1 != 0) goto L_0x0051
            java.lang.String r1 = r7.generateClientId()
        L_0x0051:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.analytics.tracking.android.ClientIdDefaultProvider.initializeClientId():java.lang.String");
    }
}
