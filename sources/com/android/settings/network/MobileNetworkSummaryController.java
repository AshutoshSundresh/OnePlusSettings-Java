package com.android.settings.network;

import android.content.Context;
import android.content.Intent;
import android.os.UserManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0015R$plurals;
import com.android.settings.C0017R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.network.SubscriptionsChangeListener;
import com.android.settings.network.telephony.MobileNetworkActivity;
import com.android.settings.network.telephony.MobileNetworkUtils;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.widget.AddPreference;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import java.util.List;

public class MobileNetworkSummaryController extends AbstractPreferenceController implements SubscriptionsChangeListener.SubscriptionsChangeListenerClient, LifecycleObserver, PreferenceControllerMixin {
    private SubscriptionsChangeListener mChangeListener;
    private final MetricsFeatureProvider mMetricsFeatureProvider = FeatureFactory.getFactory(this.mContext).getMetricsFeatureProvider();
    private AddPreference mPreference;
    private SubscriptionManager mSubscriptionManager;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "mobile_network_list";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return false;
    }

    public MobileNetworkSummaryController(Context context, Lifecycle lifecycle) {
        super(context);
        this.mSubscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        UserManager userManager = (UserManager) context.getSystemService(UserManager.class);
        if (lifecycle != null) {
            this.mChangeListener = new SubscriptionsChangeListener(context, this);
            lifecycle.addObserver(this);
        }
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
        this.mPreference = (AddPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        List<SubscriptionInfo> availableSubscriptions = SubscriptionUtil.getAvailableSubscriptions(this.mContext);
        if (availableSubscriptions.isEmpty()) {
            if (MobileNetworkUtils.showEuiccSettings(this.mContext)) {
                return this.mContext.getResources().getString(C0017R$string.mobile_network_summary_add_a_network);
            }
            return null;
        } else if (availableSubscriptions.size() == 1) {
            SubscriptionInfo subscriptionInfo = availableSubscriptions.get(0);
            int subscriptionId = subscriptionInfo.getSubscriptionId();
            if (subscriptionInfo.isEmbedded() || this.mSubscriptionManager.isActiveSubscriptionId(subscriptionId) || SubscriptionUtil.showToggleForPhysicalSim(this.mSubscriptionManager)) {
                return availableSubscriptions.get(0).getDisplayName();
            }
            return this.mContext.getString(C0017R$string.mobile_network_tap_to_activate, SubscriptionUtil.getDisplayName(subscriptionInfo));
        } else {
            int size = availableSubscriptions.size();
            return this.mContext.getResources().getQuantityString(C0015R$plurals.mobile_network_summary_count, size, Integer.valueOf(size));
        }
    }

    private void startAddSimFlow() {
        Intent intent = new Intent("android.telephony.euicc.action.PROVISION_EMBEDDED_SUBSCRIPTION");
        intent.putExtra("android.telephony.euicc.extra.FORCE_PROVISION", true);
        this.mContext.startActivity(intent);
    }

    private void update() {
        AddPreference addPreference = this.mPreference;
        if (addPreference != null && !addPreference.isDisabledByAdmin()) {
            refreshSummary(this.mPreference);
            this.mPreference.setOnPreferenceClickListener(null);
            this.mPreference.setOnAddClickListener(null);
            this.mPreference.setFragment(null);
            this.mPreference.setEnabled(!this.mChangeListener.isAirplaneModeOn());
            List<SubscriptionInfo> availableSubscriptions = SubscriptionUtil.getAvailableSubscriptions(this.mContext);
            if (!availableSubscriptions.isEmpty()) {
                if (MobileNetworkUtils.showEuiccSettings(this.mContext)) {
                    this.mPreference.setAddWidgetEnabled(!this.mChangeListener.isAirplaneModeOn());
                    this.mPreference.setOnAddClickListener(new AddPreference.OnAddClickListener() {
                        /* class com.android.settings.network.$$Lambda$MobileNetworkSummaryController$Av_fwhcj2R6kcfzdoFvt6boCsjs */

                        @Override // com.android.settings.widget.AddPreference.OnAddClickListener
                        public final void onAddClick(AddPreference addPreference) {
                            MobileNetworkSummaryController.this.lambda$update$1$MobileNetworkSummaryController(addPreference);
                        }
                    });
                }
                if (availableSubscriptions.size() == 1) {
                    this.mPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(availableSubscriptions) {
                        /* class com.android.settings.network.$$Lambda$MobileNetworkSummaryController$SLhrBw_W4Z0fRuohpDblxc8vI6I */
                        public final /* synthetic */ List f$1;

                        {
                            this.f$1 = r2;
                        }

                        @Override // androidx.preference.Preference.OnPreferenceClickListener
                        public final boolean onPreferenceClick(Preference preference) {
                            return MobileNetworkSummaryController.this.lambda$update$2$MobileNetworkSummaryController(this.f$1, preference);
                        }
                    });
                } else {
                    this.mPreference.setFragment(MobileNetworkListFragment.class.getCanonicalName());
                }
            } else if (MobileNetworkUtils.showEuiccSettings(this.mContext)) {
                this.mPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    /* class com.android.settings.network.$$Lambda$MobileNetworkSummaryController$_8dM0TxjKQt1kvFsc7Sm1R3eY */

                    @Override // androidx.preference.Preference.OnPreferenceClickListener
                    public final boolean onPreferenceClick(Preference preference) {
                        return MobileNetworkSummaryController.this.lambda$update$0$MobileNetworkSummaryController(preference);
                    }
                });
            } else {
                this.mPreference.setEnabled(false);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$update$0 */
    public /* synthetic */ boolean lambda$update$0$MobileNetworkSummaryController(Preference preference) {
        this.mMetricsFeatureProvider.logClickedPreference(preference, preference.getExtras().getInt("category"));
        startAddSimFlow();
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$update$1 */
    public /* synthetic */ void lambda$update$1$MobileNetworkSummaryController(AddPreference addPreference) {
        this.mMetricsFeatureProvider.logClickedPreference(addPreference, addPreference.getExtras().getInt("category"));
        startAddSimFlow();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$update$2 */
    public /* synthetic */ boolean lambda$update$2$MobileNetworkSummaryController(List list, Preference preference) {
        this.mMetricsFeatureProvider.logClickedPreference(preference, preference.getExtras().getInt("category"));
        SubscriptionInfo subscriptionInfo = (SubscriptionInfo) list.get(0);
        int subscriptionId = subscriptionInfo.getSubscriptionId();
        if (subscriptionInfo.isEmbedded() || this.mSubscriptionManager.isActiveSubscriptionId(subscriptionId) || SubscriptionUtil.showToggleForPhysicalSim(this.mSubscriptionManager)) {
            Intent intent = new Intent(this.mContext, MobileNetworkActivity.class);
            intent.putExtra("android.provider.extra.SUB_ID", ((SubscriptionInfo) list.get(0)).getSubscriptionId());
            this.mContext.startActivity(intent);
        } else {
            this.mSubscriptionManager.setSubscriptionEnabled(subscriptionId, true);
        }
        return true;
    }

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onAirplaneModeChanged(boolean z) {
        update();
    }

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onSubscriptionsChanged() {
        refreshSummary(this.mPreference);
        update();
    }
}
