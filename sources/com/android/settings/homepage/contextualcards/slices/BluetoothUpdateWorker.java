package com.android.settings.homepage.contextualcards.slices;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import com.android.settings.homepage.contextualcards.slices.BluetoothUpdateWorker;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.bluetooth.BluetoothCallback;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.LocalBluetoothManager;

public class BluetoothUpdateWorker extends SliceBackgroundWorker implements BluetoothCallback {
    private static LocalBluetoothManager sLocalBluetoothManager;
    private LoadBtManagerHandler mLoadBtManagerHandler;

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
    }

    public BluetoothUpdateWorker(Context context, Uri uri) {
        super(context, uri);
        LoadBtManagerHandler instance = LoadBtManagerHandler.getInstance(context);
        this.mLoadBtManagerHandler = instance;
        if (sLocalBluetoothManager == null) {
            instance.startLoadingBtManager(this);
        }
    }

    public static void initLocalBtManager(Context context) {
        if (sLocalBluetoothManager == null) {
            LoadBtManagerHandler.getInstance(context).startLoadingBtManager();
        }
    }

    static LocalBluetoothManager getLocalBtManager() {
        return sLocalBluetoothManager;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.slices.SliceBackgroundWorker
    public void onSlicePinned() {
        LocalBluetoothManager localBtManager = this.mLoadBtManagerHandler.getLocalBtManager();
        if (localBtManager != null) {
            localBtManager.getEventManager().registerCallback(this);
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.slices.SliceBackgroundWorker
    public void onSliceUnpinned() {
        LocalBluetoothManager localBtManager = this.mLoadBtManagerHandler.getLocalBtManager();
        if (localBtManager != null) {
            localBtManager.getEventManager().unregisterCallback(this);
        }
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onAclConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        notifySliceChange();
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onActiveDeviceChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        notifySliceChange();
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onBluetoothStateChanged(int i) {
        notifySliceChange();
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        notifySliceChange();
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onProfileConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i, int i2) {
        notifySliceChange();
    }

    /* access modifiers changed from: private */
    public static class LoadBtManagerHandler extends Handler {
        private static LoadBtManagerHandler sHandler;
        private final Context mContext;
        private final Runnable mLoadBtManagerTask = new Runnable() {
            /* class com.android.settings.homepage.contextualcards.slices.$$Lambda$BluetoothUpdateWorker$LoadBtManagerHandler$pyKuRKCgkqd93NX99d5mrlQ_kIE */

            public final void run() {
                BluetoothUpdateWorker.LoadBtManagerHandler.this.lambda$new$0$BluetoothUpdateWorker$LoadBtManagerHandler();
            }
        };
        private BluetoothUpdateWorker mWorker;

        /* access modifiers changed from: private */
        public static LoadBtManagerHandler getInstance(Context context) {
            if (sHandler == null) {
                HandlerThread handlerThread = new HandlerThread("BluetoothUpdateWorker", 10);
                handlerThread.start();
                sHandler = new LoadBtManagerHandler(context, handlerThread.getLooper());
            }
            return sHandler;
        }

        private LoadBtManagerHandler(Context context, Looper looper) {
            super(looper);
            this.mContext = context;
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$0 */
        public /* synthetic */ void lambda$new$0$BluetoothUpdateWorker$LoadBtManagerHandler() {
            Log.d("BluetoothUpdateWorker", "LoadBtManagerHandler: start loading...");
            long currentTimeMillis = System.currentTimeMillis();
            LocalBluetoothManager unused = BluetoothUpdateWorker.sLocalBluetoothManager = getLocalBtManager();
            Log.d("BluetoothUpdateWorker", "LoadBtManagerHandler took " + (System.currentTimeMillis() - currentTimeMillis) + " ms");
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private LocalBluetoothManager getLocalBtManager() {
            if (BluetoothUpdateWorker.sLocalBluetoothManager != null) {
                return BluetoothUpdateWorker.sLocalBluetoothManager;
            }
            return LocalBluetoothManager.getInstance(this.mContext, new LocalBluetoothManager.BluetoothManagerCallback() {
                /* class com.android.settings.homepage.contextualcards.slices.$$Lambda$BluetoothUpdateWorker$LoadBtManagerHandler$bFnvBLd7f0Am1rgQotC_UH_flVA */

                @Override // com.android.settingslib.bluetooth.LocalBluetoothManager.BluetoothManagerCallback
                public final void onBluetoothManagerInitialized(Context context, LocalBluetoothManager localBluetoothManager) {
                    BluetoothUpdateWorker.LoadBtManagerHandler.this.lambda$getLocalBtManager$1$BluetoothUpdateWorker$LoadBtManagerHandler(context, localBluetoothManager);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$getLocalBtManager$1 */
        public /* synthetic */ void lambda$getLocalBtManager$1$BluetoothUpdateWorker$LoadBtManagerHandler(Context context, LocalBluetoothManager localBluetoothManager) {
            BluetoothUpdateWorker bluetoothUpdateWorker = this.mWorker;
            if (bluetoothUpdateWorker != null) {
                bluetoothUpdateWorker.notifySliceChange();
            }
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private void startLoadingBtManager() {
            if (!hasCallbacks(this.mLoadBtManagerTask)) {
                post(this.mLoadBtManagerTask);
            }
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private void startLoadingBtManager(BluetoothUpdateWorker bluetoothUpdateWorker) {
            this.mWorker = bluetoothUpdateWorker;
            startLoadingBtManager();
        }
    }
}
