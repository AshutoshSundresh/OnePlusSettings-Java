package com.android.settings.network.ims;

import android.telephony.SubscriptionManager;
import android.telephony.ims.ImsException;
import android.telephony.ims.ImsMmTelManager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* access modifiers changed from: package-private */
public abstract class ImsQueryController {
    private volatile int mCapability;
    private volatile int mTech;
    private volatile int mTransportType;

    ImsQueryController(int i, int i2, int i3) {
        this.mCapability = i;
        this.mTech = i2;
        this.mTransportType = i3;
    }

    /* access modifiers changed from: package-private */
    public boolean isTtyOnVolteEnabled(int i) {
        return new ImsQueryTtyOnVolteStat(i).query();
    }

    /* access modifiers changed from: package-private */
    public boolean isEnabledByPlatform(int i) throws InterruptedException, ImsException, IllegalArgumentException {
        if (!SubscriptionManager.isValidSubscriptionId(i)) {
            return false;
        }
        ImsMmTelManager createForSubscriptionId = ImsMmTelManager.createForSubscriptionId(i);
        ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();
        BooleanConsumer booleanConsumer = new BooleanConsumer();
        createForSubscriptionId.isSupported(this.mCapability, this.mTransportType, newSingleThreadExecutor, booleanConsumer);
        return booleanConsumer.get(2000);
    }

    /* access modifiers changed from: package-private */
    public boolean isProvisionedOnDevice(int i) {
        if (!SubscriptionManager.isValidSubscriptionId(i)) {
            return false;
        }
        return new ImsQueryProvisioningStat(i, this.mCapability, this.mTech).query();
    }

    /* access modifiers changed from: package-private */
    public boolean isServiceStateReady(int i) throws InterruptedException, ImsException, IllegalArgumentException {
        if (!SubscriptionManager.isValidSubscriptionId(i)) {
            return false;
        }
        ImsMmTelManager createForSubscriptionId = ImsMmTelManager.createForSubscriptionId(i);
        ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();
        IntegerConsumer integerConsumer = new IntegerConsumer();
        createForSubscriptionId.getFeatureState(newSingleThreadExecutor, integerConsumer);
        if (integerConsumer.get(2000) == 2) {
            return true;
        }
        return false;
    }
}
