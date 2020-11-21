package com.android.settings.development.qstile;

import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.hardware.SensorPrivacyManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.provider.Settings;
import android.service.quicksettings.TileService;
import android.sysprop.DisplayProperties;
import android.util.Log;
import android.view.IWindowManager;
import android.view.WindowManagerGlobal;
import android.widget.Toast;
import com.android.internal.app.LocalePicker;
import com.android.internal.statusbar.IStatusBarService;
import com.android.settings.C0017R$string;
import com.android.settings.development.WirelessDebuggingPreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.development.DevelopmentSettingsEnabler;
import com.android.settingslib.development.SystemPropPoker;

public abstract class DevelopmentTiles extends TileService {
    /* access modifiers changed from: protected */
    public abstract boolean isEnabled();

    /* access modifiers changed from: protected */
    public abstract void setIsEnabled(boolean z);

    public void onStartListening() {
        super.onStartListening();
        refresh();
    }

    public void refresh() {
        int i = 0;
        if (!DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(this)) {
            if (isEnabled()) {
                setIsEnabled(false);
                SystemPropPoker.getInstance().poke();
            }
            ComponentName componentName = new ComponentName(getPackageName(), getClass().getName());
            try {
                getPackageManager().setComponentEnabledSetting(componentName, 2, 1);
                IStatusBarService asInterface = IStatusBarService.Stub.asInterface(ServiceManager.checkService("statusbar"));
                if (asInterface != null) {
                    asInterface.remTile(componentName);
                }
            } catch (RemoteException e) {
                Log.e("DevelopmentTiles", "Failed to modify QS tile for component " + componentName.toString(), e);
            }
        } else {
            i = isEnabled() ? 2 : 1;
        }
        getQsTile().setState(i);
        getQsTile().updateTile();
    }

    public void onClick() {
        boolean z = true;
        if (getQsTile().getState() != 1) {
            z = false;
        }
        setIsEnabled(z);
        SystemPropPoker.getInstance().poke();
        refresh();
    }

    public static class ShowLayout extends DevelopmentTiles {
        /* access modifiers changed from: protected */
        @Override // com.android.settings.development.qstile.DevelopmentTiles
        public boolean isEnabled() {
            return ((Boolean) DisplayProperties.debug_layout().orElse(Boolean.FALSE)).booleanValue();
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.development.qstile.DevelopmentTiles
        public void setIsEnabled(boolean z) {
            DisplayProperties.debug_layout(Boolean.valueOf(z));
        }
    }

    public static class GPUProfiling extends DevelopmentTiles {
        /* access modifiers changed from: protected */
        @Override // com.android.settings.development.qstile.DevelopmentTiles
        public boolean isEnabled() {
            return SystemProperties.get("debug.hwui.profile").equals("visual_bars");
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.development.qstile.DevelopmentTiles
        public void setIsEnabled(boolean z) {
            SystemProperties.set("debug.hwui.profile", z ? "visual_bars" : "");
        }
    }

    public static class ForceRTL extends DevelopmentTiles {
        /* access modifiers changed from: protected */
        @Override // com.android.settings.development.qstile.DevelopmentTiles
        public boolean isEnabled() {
            if (Settings.Global.getInt(getContentResolver(), "debug.force_rtl", 0) != 0) {
                return true;
            }
            return false;
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.development.qstile.DevelopmentTiles
        public void setIsEnabled(boolean z) {
            Settings.Global.putInt(getContentResolver(), "debug.force_rtl", z ? 1 : 0);
            DisplayProperties.debug_force_rtl(Boolean.valueOf(z));
            LocalePicker.updateLocales(getResources().getConfiguration().getLocales());
        }
    }

    public static class AnimationSpeed extends DevelopmentTiles {
        /* access modifiers changed from: protected */
        @Override // com.android.settings.development.qstile.DevelopmentTiles
        public boolean isEnabled() {
            try {
                if (WindowManagerGlobal.getWindowManagerService().getAnimationScale(0) != 1.0f) {
                    return true;
                }
                return false;
            } catch (RemoteException unused) {
                return false;
            }
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.development.qstile.DevelopmentTiles
        public void setIsEnabled(boolean z) {
            IWindowManager windowManagerService = WindowManagerGlobal.getWindowManagerService();
            float f = z ? 10.0f : 1.0f;
            try {
                windowManagerService.setAnimationScale(0, f);
                windowManagerService.setAnimationScale(1, f);
                windowManagerService.setAnimationScale(2, f);
            } catch (RemoteException unused) {
            }
        }
    }

    public static class WinscopeTrace extends DevelopmentTiles {
        static final int SURFACE_FLINGER_LAYER_TRACE_CONTROL_CODE = 1025;
        static final int SURFACE_FLINGER_LAYER_TRACE_STATUS_CODE = 1026;
        private IBinder mSurfaceFlinger;
        private Toast mToast;
        private IWindowManager mWindowManager;

        public void onCreate() {
            super.onCreate();
            this.mWindowManager = WindowManagerGlobal.getWindowManagerService();
            this.mSurfaceFlinger = ServiceManager.getService("SurfaceFlinger");
            this.mToast = Toast.makeText(getApplicationContext(), "Trace files written to /data/misc/wmtrace", 1);
        }

        private boolean isWindowTraceEnabled() {
            try {
                return this.mWindowManager.isWindowTraceEnabled();
            } catch (RemoteException e) {
                Log.e("DevelopmentTiles", "Could not get window trace status, defaulting to false." + e.toString());
                return false;
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:26:0x005e  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private boolean isLayerTraceEnabled() {
            /*
            // Method dump skipped, instructions count: 101
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settings.development.qstile.DevelopmentTiles.WinscopeTrace.isLayerTraceEnabled():boolean");
        }

        private boolean isSystemUiTracingEnabled() {
            try {
                IStatusBarService asInterface = IStatusBarService.Stub.asInterface(ServiceManager.checkService("statusbar"));
                if (asInterface != null) {
                    return asInterface.isTracing();
                }
                return false;
            } catch (RemoteException e) {
                Log.e("DevelopmentTiles", "Could not get system ui tracing status." + e.toString());
                return false;
            }
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.development.qstile.DevelopmentTiles
        public boolean isEnabled() {
            return isWindowTraceEnabled() || isLayerTraceEnabled() || isSystemUiTracingEnabled();
        }

        private void setWindowTraceEnabled(boolean z) {
            if (z) {
                try {
                    this.mWindowManager.startWindowTrace();
                } catch (RemoteException e) {
                    Log.e("DevelopmentTiles", "Could not set window trace status." + e.toString());
                }
            } else {
                this.mWindowManager.stopWindowTrace();
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:26:0x004f  */
        /* JADX WARNING: Removed duplicated region for block: B:30:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void setLayerTraceEnabled(boolean r4) {
            /*
                r3 = this;
                r0 = 0
                android.os.IBinder r1 = r3.mSurfaceFlinger     // Catch:{ RemoteException -> 0x002e }
                if (r1 == 0) goto L_0x0026
                android.os.Parcel r1 = android.os.Parcel.obtain()     // Catch:{ RemoteException -> 0x002e }
                java.lang.String r2 = "android.ui.ISurfaceComposer"
                r1.writeInterfaceToken(r2)     // Catch:{ RemoteException -> 0x0023, all -> 0x0020 }
                r2 = 0
                if (r4 == 0) goto L_0x0013
                r4 = 1
                goto L_0x0014
            L_0x0013:
                r4 = r2
            L_0x0014:
                r1.writeInt(r4)     // Catch:{ RemoteException -> 0x0023, all -> 0x0020 }
                android.os.IBinder r3 = r3.mSurfaceFlinger     // Catch:{ RemoteException -> 0x0023, all -> 0x0020 }
                r4 = 1025(0x401, float:1.436E-42)
                r3.transact(r4, r1, r0, r2)     // Catch:{ RemoteException -> 0x0023, all -> 0x0020 }
                r0 = r1
                goto L_0x0026
            L_0x0020:
                r3 = move-exception
                r0 = r1
                goto L_0x004d
            L_0x0023:
                r3 = move-exception
                r0 = r1
                goto L_0x002f
            L_0x0026:
                if (r0 == 0) goto L_0x004c
            L_0x0028:
                r0.recycle()
                goto L_0x004c
            L_0x002c:
                r3 = move-exception
                goto L_0x004d
            L_0x002e:
                r3 = move-exception
            L_0x002f:
                java.lang.String r4 = "DevelopmentTiles"
                java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x002c }
                r1.<init>()     // Catch:{ all -> 0x002c }
                java.lang.String r2 = "Could not set layer tracing."
                r1.append(r2)     // Catch:{ all -> 0x002c }
                java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x002c }
                r1.append(r3)     // Catch:{ all -> 0x002c }
                java.lang.String r3 = r1.toString()     // Catch:{ all -> 0x002c }
                android.util.Log.e(r4, r3)     // Catch:{ all -> 0x002c }
                if (r0 == 0) goto L_0x004c
                goto L_0x0028
            L_0x004c:
                return
            L_0x004d:
                if (r0 == 0) goto L_0x0052
                r0.recycle()
            L_0x0052:
                throw r3
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settings.development.qstile.DevelopmentTiles.WinscopeTrace.setLayerTraceEnabled(boolean):void");
        }

        private void setSystemUiTracing(boolean z) {
            try {
                IStatusBarService asInterface = IStatusBarService.Stub.asInterface(ServiceManager.checkService("statusbar"));
                if (asInterface == null) {
                    return;
                }
                if (z) {
                    asInterface.startTracing();
                } else {
                    asInterface.stopTracing();
                }
            } catch (RemoteException e) {
                Log.e("DevelopmentTiles", "Could not set system ui tracing." + e.toString());
            }
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.development.qstile.DevelopmentTiles
        public void setIsEnabled(boolean z) {
            setWindowTraceEnabled(z);
            setLayerTraceEnabled(z);
            setSystemUiTracing(z);
            if (!z) {
                this.mToast.show();
            }
        }
    }

    public static class SensorsOff extends DevelopmentTiles {
        private Context mContext;
        private boolean mIsEnabled;
        private KeyguardManager mKeyguardManager;
        private MetricsFeatureProvider mMetricsFeatureProvider;
        private SensorPrivacyManager mSensorPrivacyManager;

        public void onCreate() {
            super.onCreate();
            Context applicationContext = getApplicationContext();
            this.mContext = applicationContext;
            SensorPrivacyManager sensorPrivacyManager = (SensorPrivacyManager) applicationContext.getSystemService("sensor_privacy");
            this.mSensorPrivacyManager = sensorPrivacyManager;
            this.mIsEnabled = sensorPrivacyManager.isSensorPrivacyEnabled();
            this.mMetricsFeatureProvider = FeatureFactory.getFactory(this.mContext).getMetricsFeatureProvider();
            this.mKeyguardManager = (KeyguardManager) this.mContext.getSystemService("keyguard");
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.development.qstile.DevelopmentTiles
        public boolean isEnabled() {
            return this.mIsEnabled;
        }

        @Override // com.android.settings.development.qstile.DevelopmentTiles
        public void setIsEnabled(boolean z) {
            if (!this.mIsEnabled || !this.mKeyguardManager.isKeyguardLocked()) {
                this.mMetricsFeatureProvider.action(getApplicationContext(), 1598, z);
                this.mIsEnabled = z;
                this.mSensorPrivacyManager.setSensorPrivacy(z);
            }
        }
    }

    public static class WirelessDebugging extends DevelopmentTiles {
        private Context mContext;
        private final Handler mHandler = new Handler(Looper.getMainLooper());
        private KeyguardManager mKeyguardManager;
        private final ContentObserver mSettingsObserver = new ContentObserver(this.mHandler) {
            /* class com.android.settings.development.qstile.DevelopmentTiles.WirelessDebugging.AnonymousClass1 */

            public void onChange(boolean z, Uri uri) {
                WirelessDebugging.this.refresh();
            }
        };
        private Toast mToast;

        public void onCreate() {
            super.onCreate();
            Context applicationContext = getApplicationContext();
            this.mContext = applicationContext;
            this.mKeyguardManager = (KeyguardManager) applicationContext.getSystemService("keyguard");
            this.mToast = Toast.makeText(this.mContext, C0017R$string.adb_wireless_no_network_msg, 1);
        }

        @Override // com.android.settings.development.qstile.DevelopmentTiles
        public void onStartListening() {
            DevelopmentTiles.super.onStartListening();
            getContentResolver().registerContentObserver(Settings.Global.getUriFor("adb_wifi_enabled"), false, this.mSettingsObserver);
        }

        public void onStopListening() {
            super.onStopListening();
            getContentResolver().unregisterContentObserver(this.mSettingsObserver);
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.development.qstile.DevelopmentTiles
        public boolean isEnabled() {
            return isAdbWifiEnabled();
        }

        @Override // com.android.settings.development.qstile.DevelopmentTiles
        public void setIsEnabled(boolean z) {
            if (z && this.mKeyguardManager.isKeyguardLocked()) {
                return;
            }
            if (!z || WirelessDebuggingPreferenceController.isWifiConnected(this.mContext)) {
                writeAdbWifiSetting(z);
                return;
            }
            sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
            this.mToast.show();
        }

        private boolean isAdbWifiEnabled() {
            return Settings.Global.getInt(getContentResolver(), "adb_wifi_enabled", 0) != 0;
        }

        /* access modifiers changed from: protected */
        public void writeAdbWifiSetting(boolean z) {
            Settings.Global.putInt(getContentResolver(), "adb_wifi_enabled", z ? 1 : 0);
        }
    }
}
