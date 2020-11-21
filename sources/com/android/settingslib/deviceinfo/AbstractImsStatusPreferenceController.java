package com.android.settingslib.deviceinfo;

import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionManager;
import android.telephony.ims.ImsMmTelManager;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settingslib.R$string;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public abstract class AbstractImsStatusPreferenceController extends AbstractConnectivityPreferenceController {
    private static final String[] CONNECTIVITY_INTENTS = {"android.bluetooth.adapter.action.STATE_CHANGED", "android.net.conn.CONNECTIVITY_CHANGE", "android.net.wifi.LINK_CONFIGURATION_CHANGED", "android.net.wifi.STATE_CHANGE"};
    static final String KEY_IMS_REGISTRATION_STATE = "ims_reg_state";
    private Preference mImsStatus;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return KEY_IMS_REGISTRATION_STATE;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) this.mContext.getSystemService(CarrierConfigManager.class);
        PersistableBundle configForSubId = carrierConfigManager != null ? carrierConfigManager.getConfigForSubId(SubscriptionManager.getDefaultDataSubscriptionId()) : null;
        return configForSubId != null && configForSubId.getBoolean("show_ims_registration_status_bool");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mImsStatus = preferenceScreen.findPreference(KEY_IMS_REGISTRATION_STATE);
        updateConnectivity();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.deviceinfo.AbstractConnectivityPreferenceController
    public String[] getConnectivityIntents() {
        return CONNECTIVITY_INTENTS;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.deviceinfo.AbstractConnectivityPreferenceController
    public void updateConnectivity() {
        if (this.mImsStatus != null) {
            int defaultDataSubscriptionId = SubscriptionManager.getDefaultDataSubscriptionId();
            if (!SubscriptionManager.isValidSubscriptionId(defaultDataSubscriptionId)) {
                this.mImsStatus.setSummary(R$string.ims_reg_status_not_registered);
                return;
            }
            ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();
            StateCallback stateCallback = new StateCallback();
            try {
                ImsMmTelManager.createForSubscriptionId(defaultDataSubscriptionId).getRegistrationState(newSingleThreadExecutor, stateCallback);
            } catch (Exception unused) {
            }
            this.mImsStatus.setSummary(stateCallback.waitUntilResult() ? R$string.ims_reg_status_registered : R$string.ims_reg_status_not_registered);
            try {
                newSingleThreadExecutor.shutdownNow();
            } catch (Exception unused2) {
            }
        }
    }

    /* access modifiers changed from: private */
    public final class StateCallback extends AtomicBoolean implements Consumer<Integer> {
        private final Semaphore mSemaphore;

        private StateCallback() {
            super(false);
            this.mSemaphore = new Semaphore(0);
        }

        public void accept(Integer num) {
            set(num.intValue() == 2);
            try {
                this.mSemaphore.release();
            } catch (Exception unused) {
            }
        }

        public boolean waitUntilResult() {
            try {
                if (!this.mSemaphore.tryAcquire(2000, TimeUnit.MILLISECONDS)) {
                    Log.w("AbstractImsPrefController", "IMS registration state query timeout");
                    return false;
                }
            } catch (Exception unused) {
            }
            return get();
        }
    }
}
