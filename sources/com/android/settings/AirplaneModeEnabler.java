package com.android.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.os.UserHandle;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.SubscriptionInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.settings.network.GlobalSettingsChangeListener;
import com.android.settings.network.ProxySubscriptionManager;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.WirelessUtils;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import java.util.List;

public class AirplaneModeEnabler extends GlobalSettingsChangeListener {
    private final Context mContext;
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private OnAirplaneModeChangedListener mOnAirplaneModeChangedListener;
    PhoneStateListener mPhoneStateListener = new PhoneStateListener(Looper.getMainLooper()) {
        /* class com.android.settings.AirplaneModeEnabler.AnonymousClass1 */

        public void onRadioPowerStateChanged(int i) {
            AirplaneModeEnabler.this.onAirplaneModeChanged();
        }
    };
    private TelephonyManager mTelephonyManager;

    public interface OnAirplaneModeChangedListener {
        void onAirplaneModeChanged(boolean z);
    }

    public AirplaneModeEnabler(Context context, OnAirplaneModeChangedListener onAirplaneModeChangedListener) {
        super(context, "airplane_mode_on");
        this.mContext = context;
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
        this.mOnAirplaneModeChangedListener = onAirplaneModeChangedListener;
        this.mTelephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
    }

    @Override // com.android.settings.network.GlobalSettingsChangeListener
    public void onChanged(String str) {
        onAirplaneModeChanged();
    }

    public void start() {
        this.mTelephonyManager.listen(this.mPhoneStateListener, 8388608);
    }

    public void stop() {
        this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
    }

    private void setAirplaneModeOn(boolean z) {
        Settings.Global.putInt(this.mContext.getContentResolver(), "airplane_mode_on", z ? 1 : 0);
        OnAirplaneModeChangedListener onAirplaneModeChangedListener = this.mOnAirplaneModeChangedListener;
        if (onAirplaneModeChangedListener != null) {
            onAirplaneModeChangedListener.onAirplaneModeChanged(z);
        }
        Intent intent = new Intent("android.intent.action.AIRPLANE_MODE");
        intent.putExtra("state", z);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void onAirplaneModeChanged() {
        OnAirplaneModeChangedListener onAirplaneModeChangedListener = this.mOnAirplaneModeChangedListener;
        if (onAirplaneModeChangedListener != null) {
            onAirplaneModeChangedListener.onAirplaneModeChanged(isAirplaneModeOn());
        }
    }

    public boolean isInEcmMode() {
        if (this.mTelephonyManager.getEmergencyCallbackMode()) {
            return true;
        }
        List<SubscriptionInfo> activeSubscriptionsInfo = ProxySubscriptionManager.getInstance(this.mContext).getActiveSubscriptionsInfo();
        if (activeSubscriptionsInfo == null) {
            return false;
        }
        for (SubscriptionInfo subscriptionInfo : activeSubscriptionsInfo) {
            TelephonyManager createForSubscriptionId = this.mTelephonyManager.createForSubscriptionId(subscriptionInfo.getSubscriptionId());
            if (createForSubscriptionId != null && createForSubscriptionId.getEmergencyCallbackMode()) {
                return true;
            }
        }
        return false;
    }

    public void setAirplaneMode(boolean z) {
        if (isInEcmMode()) {
            Log.d("AirplaneModeEnabler", "ECM airplane mode=" + z);
            return;
        }
        this.mMetricsFeatureProvider.action(this.mContext, 177, z);
        setAirplaneModeOn(z);
    }

    public void setAirplaneModeInECM(boolean z, boolean z2) {
        Log.d("AirplaneModeEnabler", "Exist ECM=" + z + ", with airplane mode=" + z2);
        if (z) {
            setAirplaneModeOn(z2);
        } else {
            onAirplaneModeChanged();
        }
    }

    public boolean isAirplaneModeOn() {
        return WirelessUtils.isAirplaneModeOn(this.mContext);
    }
}
