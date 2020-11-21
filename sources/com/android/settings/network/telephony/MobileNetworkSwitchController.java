package com.android.settings.network.telephony;

import android.content.Context;
import android.content.IntentFilter;
import android.telephony.PhoneStateListener;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0010R$id;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.network.SubscriptionsChangeListener;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.widget.SwitchBar;
import com.android.settings.widget.ToggleSwitch;
import com.android.settingslib.widget.LayoutPreference;
import java.util.Iterator;

public class MobileNetworkSwitchController extends BasePreferenceController implements SubscriptionsChangeListener.SubscriptionsChangeListenerClient, LifecycleObserver {
    private static final String TAG = "MobileNetworkSwitchCtrl";
    private int mCallState;
    private SubscriptionsChangeListener mChangeListener;
    private int mPhoneId;
    private PhoneStateListener mPhoneStateListener;
    private int mSubId = -1;
    private SubscriptionInfo mSubInfo = null;
    private SubscriptionManager mSubscriptionManager = ((SubscriptionManager) this.mContext.getSystemService(SubscriptionManager.class));
    private SwitchBar mSwitchBar;
    private TelephonyManager mTelephonyManager;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 1;
    }

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

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onAirplaneModeChanged(boolean z) {
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public MobileNetworkSwitchController(Context context, String str) {
        super(context, str);
        this.mChangeListener = new SubscriptionsChangeListener(context, this);
        this.mTelephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
    }

    public void init(Lifecycle lifecycle, int i) {
        lifecycle.addObserver(this);
        this.mSubId = i;
        this.mPhoneId = SubscriptionManager.getSlotIndex(i);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        this.mChangeListener.start();
        registerPhoneStateListener();
        update();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        this.mChangeListener.stop();
        unRegisterPhoneStateListener();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        SwitchBar switchBar = (SwitchBar) ((LayoutPreference) preferenceScreen.findPreference(this.mPreferenceKey)).findViewById(C0010R$id.switch_bar);
        this.mSwitchBar = switchBar;
        switchBar.setSwitchBarText(C0017R$string.mobile_network_use_sim_on, C0017R$string.mobile_network_use_sim_off);
        this.mSwitchBar.getSwitch().setOnBeforeCheckedChangeListener(new ToggleSwitch.OnBeforeCheckedChangeListener() {
            /* class com.android.settings.network.telephony.$$Lambda$MobileNetworkSwitchController$8gHlqcim1vAQNuqU6a1SRLRJ8DY */

            @Override // com.android.settings.widget.ToggleSwitch.OnBeforeCheckedChangeListener
            public final boolean onBeforeCheckedChanged(ToggleSwitch toggleSwitch, boolean z) {
                return MobileNetworkSwitchController.this.lambda$displayPreference$0$MobileNetworkSwitchController(toggleSwitch, z);
            }
        });
        update();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$displayPreference$0 */
    public /* synthetic */ boolean lambda$displayPreference$0$MobileNetworkSwitchController(ToggleSwitch toggleSwitch, boolean z) {
        int uiccCardProvisioningStatus = PrimaryCardAndSubsidyLockUtils.getUiccCardProvisioningStatus(this.mPhoneId);
        Log.d(TAG, "displayPreference: mSubId=" + this.mSubId + ", mSubInfo=" + this.mSubInfo + ", uiccStatus=" + uiccCardProvisioningStatus);
        if (this.mSubInfo != null) {
            return (uiccCardProvisioningStatus == 1) != z && !this.mSubscriptionManager.setSubscriptionEnabled(this.mSubId, z);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void update() {
        if (this.mSwitchBar != null) {
            Iterator<SubscriptionInfo> it = SubscriptionUtil.getAvailableSubscriptions(this.mContext).iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                SubscriptionInfo next = it.next();
                if (next.getSubscriptionId() == this.mSubId) {
                    this.mSubInfo = next;
                    break;
                }
            }
            boolean z = false;
            if (this.mCallState != 0) {
                Log.d(TAG, "update: disable switchbar, callstate=" + this.mCallState);
                this.mSwitchBar.setEnabled(false);
                return;
            }
            this.mSwitchBar.setEnabled(true);
            if (this.mSubInfo == null) {
                this.mSwitchBar.hide();
                return;
            }
            this.mSwitchBar.show();
            int uiccCardProvisioningStatus = PrimaryCardAndSubsidyLockUtils.getUiccCardProvisioningStatus(this.mPhoneId);
            SwitchBar switchBar = this.mSwitchBar;
            if (uiccCardProvisioningStatus == 1) {
                z = true;
            }
            switchBar.setCheckedInternal(z);
        }
    }

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onSubscriptionsChanged() {
        update();
    }

    private void registerPhoneStateListener() {
        this.mTelephonyManager.createForSubscriptionId(this.mSubId).listen(getPhoneStateListener(), 32);
    }

    private void unRegisterPhoneStateListener() {
        PhoneStateListener phoneStateListener = this.mPhoneStateListener;
        if (phoneStateListener != null) {
            this.mTelephonyManager.listen(phoneStateListener, 0);
            this.mPhoneStateListener = null;
        }
    }

    private PhoneStateListener getPhoneStateListener() {
        AnonymousClass1 r0 = new PhoneStateListener() {
            /* class com.android.settings.network.telephony.MobileNetworkSwitchController.AnonymousClass1 */

            public void onCallStateChanged(int i, String str) {
                MobileNetworkSwitchController.this.mCallState = i;
                MobileNetworkSwitchController.this.update();
            }
        };
        this.mPhoneStateListener = r0;
        return r0;
    }
}
