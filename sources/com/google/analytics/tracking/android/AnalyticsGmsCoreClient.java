package com.google.analytics.tracking.android;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import com.google.android.gms.analytics.internal.Command;
import com.google.android.gms.analytics.internal.IAnalyticsService;
import java.util.List;
import java.util.Map;

class AnalyticsGmsCoreClient implements AnalyticsClient {
    private ServiceConnection mConnection;
    private Context mContext;
    private OnConnectedListener mOnConnectedListener;
    private OnConnectionFailedListener mOnConnectionFailedListener;
    private IAnalyticsService mService;

    public interface OnConnectedListener {
        void onConnected();

        void onDisconnected();
    }

    public interface OnConnectionFailedListener {
        void onConnectionFailed(int i, Intent intent);
    }

    public AnalyticsGmsCoreClient(Context context, OnConnectedListener onConnectedListener, OnConnectionFailedListener onConnectionFailedListener) {
        this.mContext = context;
        if (onConnectedListener != null) {
            this.mOnConnectedListener = onConnectedListener;
            if (onConnectionFailedListener != null) {
                this.mOnConnectionFailedListener = onConnectionFailedListener;
                return;
            }
            throw new IllegalArgumentException("onConnectionFailedListener cannot be null");
        }
        throw new IllegalArgumentException("onConnectedListener cannot be null");
    }

    @Override // com.google.analytics.tracking.android.AnalyticsClient
    public void connect() {
        Intent intent = new Intent("com.google.android.gms.analytics.service.START");
        intent.setComponent(new ComponentName("com.google.android.gms", "com.google.android.gms.analytics.service.AnalyticsService"));
        intent.putExtra("app_package_name", this.mContext.getPackageName());
        if (this.mConnection != null) {
            Log.e("Calling connect() while still connected, missing disconnect().");
            return;
        }
        AnalyticsServiceConnection analyticsServiceConnection = new AnalyticsServiceConnection();
        this.mConnection = analyticsServiceConnection;
        boolean bindService = this.mContext.bindService(intent, analyticsServiceConnection, 129);
        Log.v("connect: bindService returned " + bindService + " for " + intent);
        if (!bindService) {
            this.mConnection = null;
            this.mOnConnectionFailedListener.onConnectionFailed(1, null);
        }
    }

    @Override // com.google.analytics.tracking.android.AnalyticsClient
    public void disconnect() {
        this.mService = null;
        ServiceConnection serviceConnection = this.mConnection;
        if (serviceConnection != null) {
            try {
                this.mContext.unbindService(serviceConnection);
            } catch (IllegalArgumentException | IllegalStateException unused) {
            }
            this.mConnection = null;
            this.mOnConnectedListener.onDisconnected();
        }
    }

    @Override // com.google.analytics.tracking.android.AnalyticsClient
    public void sendHit(Map<String, String> map, long j, String str, List<Command> list) {
        try {
            getService().sendHit(map, j, str, list);
        } catch (RemoteException e) {
            Log.e("sendHit failed: " + e);
        }
    }

    @Override // com.google.analytics.tracking.android.AnalyticsClient
    public void clearHits() {
        try {
            getService().clearHits();
        } catch (RemoteException e) {
            Log.e("clear hits failed: " + e);
        }
    }

    private IAnalyticsService getService() {
        checkConnected();
        return this.mService;
    }

    /* access modifiers changed from: protected */
    public void checkConnected() {
        if (!isConnected()) {
            throw new IllegalStateException("Not connected. Call connect() and wait for onConnected() to be called.");
        }
    }

    public boolean isConnected() {
        return this.mService != null;
    }

    final class AnalyticsServiceConnection implements ServiceConnection {
        AnalyticsServiceConnection() {
        }

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.v("service connected, binder: " + iBinder);
            try {
                if ("com.google.android.gms.analytics.internal.IAnalyticsService".equals(iBinder.getInterfaceDescriptor())) {
                    Log.v("bound to service");
                    AnalyticsGmsCoreClient.this.mService = IAnalyticsService.Stub.asInterface(iBinder);
                    AnalyticsGmsCoreClient.this.onServiceBound();
                    return;
                }
            } catch (RemoteException unused) {
            }
            AnalyticsGmsCoreClient.this.mContext.unbindService(this);
            AnalyticsGmsCoreClient.this.mConnection = null;
            AnalyticsGmsCoreClient.this.mOnConnectionFailedListener.onConnectionFailed(2, null);
        }

        public void onServiceDisconnected(ComponentName componentName) {
            Log.v("service disconnected: " + componentName);
            AnalyticsGmsCoreClient.this.mConnection = null;
            AnalyticsGmsCoreClient.this.mOnConnectedListener.onDisconnected();
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void onServiceBound() {
        onConnectionSuccess();
    }

    private void onConnectionSuccess() {
        this.mOnConnectedListener.onConnected();
    }
}
