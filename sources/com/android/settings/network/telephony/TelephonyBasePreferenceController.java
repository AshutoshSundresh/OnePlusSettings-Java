package com.android.settings.network.telephony;

import android.content.Context;
import android.content.IntentFilter;
import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionManager;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class TelephonyBasePreferenceController extends BasePreferenceController implements TelephonyAvailabilityCallback, TelephonyAvailabilityHandler {
    private AtomicInteger mAvailabilityStatus = new AtomicInteger(0);
    private AtomicInteger mSetSessionCount = new AtomicInteger(0);
    protected int mSubId = -1;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.network.telephony.TelephonyAvailabilityCallback
    public abstract /* synthetic */ int getAvailabilityStatus(int i);

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public TelephonyBasePreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (this.mSetSessionCount.get() <= 0) {
            this.mAvailabilityStatus.set(MobileNetworkUtils.getAvailability(this.mContext, this.mSubId, new TelephonyAvailabilityCallback() {
                /* class com.android.settings.network.telephony.$$Lambda$ObfukzJxj4OvZ6XClLvNT8fzhCc */

                @Override // com.android.settings.network.telephony.TelephonyAvailabilityCallback
                public final int getAvailabilityStatus(int i) {
                    return TelephonyBasePreferenceController.this.getAvailabilityStatus(i);
                }
            }));
        }
        return this.mAvailabilityStatus.get();
    }

    @Override // com.android.settings.network.telephony.TelephonyAvailabilityHandler
    public void setAvailabilityStatus(int i) {
        this.mAvailabilityStatus.set(i);
        this.mSetSessionCount.getAndIncrement();
    }

    @Override // com.android.settings.network.telephony.TelephonyAvailabilityHandler
    public void unsetAvailabilityStatus() {
        this.mSetSessionCount.getAndDecrement();
    }

    public PersistableBundle getCarrierConfigForSubId(int i) {
        if (!SubscriptionManager.isValidSubscriptionId(i)) {
            return null;
        }
        return ((CarrierConfigManager) this.mContext.getSystemService(CarrierConfigManager.class)).getConfigForSubId(i);
    }
}
