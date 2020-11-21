package com.android.settings.network;

import android.content.Context;
import android.content.Intent;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.ArrayMap;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.C0017R$string;
import com.android.settings.network.SubscriptionsChangeListener;
import com.android.settings.network.telephony.MobileNetworkActivity;
import com.android.settings.network.telephony.MobileNetworkUtils;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.Map;

public class MobileNetworkListController extends AbstractPreferenceController implements LifecycleObserver, SubscriptionsChangeListener.SubscriptionsChangeListenerClient {
    @VisibleForTesting
    static final String KEY_ADD_MORE = "add_more";
    private SubscriptionsChangeListener mChangeListener;
    private PreferenceScreen mPreferenceScreen;
    private Map<Integer, Preference> mPreferences = new ArrayMap();
    private SubscriptionManager mSubscriptionManager;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return null;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onAirplaneModeChanged(boolean z) {
    }

    public MobileNetworkListController(Context context, Lifecycle lifecycle) {
        super(context);
        this.mSubscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        this.mChangeListener = new SubscriptionsChangeListener(context, this);
        lifecycle.addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        this.mChangeListener.start();
        update();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        this.mChangeListener.stop();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreferenceScreen = preferenceScreen;
        preferenceScreen.findPreference(KEY_ADD_MORE).setVisible(MobileNetworkUtils.showEuiccSettings(this.mContext));
        update();
    }

    private void update() {
        if (this.mPreferenceScreen != null) {
            Map<Integer, Preference> map = this.mPreferences;
            this.mPreferences = new ArrayMap();
            for (SubscriptionInfo subscriptionInfo : SubscriptionUtil.getAvailableSubscriptions(this.mContext)) {
                int subscriptionId = subscriptionInfo.getSubscriptionId();
                Preference remove = map.remove(Integer.valueOf(subscriptionId));
                if (remove == null) {
                    remove = new Preference(this.mPreferenceScreen.getContext());
                    this.mPreferenceScreen.addPreference(remove);
                }
                remove.setTitle(subscriptionInfo.getDisplayName());
                if (subscriptionInfo.isEmbedded()) {
                    if (this.mSubscriptionManager.isActiveSubscriptionId(subscriptionId)) {
                        remove.setSummary(C0017R$string.mobile_network_active_esim);
                    } else {
                        remove.setSummary(C0017R$string.mobile_network_inactive_esim);
                    }
                } else if (this.mSubscriptionManager.isActiveSubscriptionId(subscriptionId)) {
                    remove.setSummary(C0017R$string.mobile_network_active_sim);
                } else if (SubscriptionUtil.showToggleForPhysicalSim(this.mSubscriptionManager)) {
                    remove.setSummary(this.mContext.getString(C0017R$string.mobile_network_inactive_sim));
                } else {
                    remove.setSummary(this.mContext.getString(C0017R$string.mobile_network_tap_to_activate, SubscriptionUtil.getDisplayName(subscriptionInfo)));
                }
                remove.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(subscriptionInfo, subscriptionId) {
                    /* class com.android.settings.network.$$Lambda$MobileNetworkListController$ULBSkyh9kv2XCsmwv2R9WCN6Vc */
                    public final /* synthetic */ SubscriptionInfo f$1;
                    public final /* synthetic */ int f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    @Override // androidx.preference.Preference.OnPreferenceClickListener
                    public final boolean onPreferenceClick(Preference preference) {
                        return MobileNetworkListController.this.lambda$update$0$MobileNetworkListController(this.f$1, this.f$2, preference);
                    }
                });
                this.mPreferences.put(Integer.valueOf(subscriptionId), remove);
            }
            for (Preference preference : map.values()) {
                this.mPreferenceScreen.removePreference(preference);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$update$0 */
    public /* synthetic */ boolean lambda$update$0$MobileNetworkListController(SubscriptionInfo subscriptionInfo, int i, Preference preference) {
        if (subscriptionInfo.isEmbedded() || this.mSubscriptionManager.isActiveSubscriptionId(i) || SubscriptionUtil.showToggleForPhysicalSim(this.mSubscriptionManager)) {
            Intent intent = new Intent(this.mContext, MobileNetworkActivity.class);
            intent.putExtra("android.provider.extra.SUB_ID", subscriptionInfo.getSubscriptionId());
            this.mContext.startActivity(intent);
        } else {
            this.mSubscriptionManager.setSubscriptionEnabled(i, true);
        }
        return true;
    }

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onSubscriptionsChanged() {
        update();
    }
}
