package com.android.settings.homepage.contextualcards.conditional;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.telephony.PhoneStateListener;
import android.telephony.PreciseDataConnectionState;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settings.homepage.contextualcards.conditional.ConditionalContextualCard;
import com.android.settings.network.GlobalSettingsChangeListener;
import java.util.Objects;

public class CellularDataConditionController implements ConditionalCardController {
    static final int ID = Objects.hash("CellularDataConditionController");
    private final Context mAppContext;
    private final ConditionManager mConditionManager;
    private final ConnectivityManager mConnectivityManager;
    private boolean mIsListeningConnectionChange;
    private final PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        /* class com.android.settings.homepage.contextualcards.conditional.CellularDataConditionController.AnonymousClass2 */

        public void onPreciseDataConnectionStateChanged(PreciseDataConnectionState preciseDataConnectionState) {
            CellularDataConditionController.this.mConditionManager.onConditionChanged();
        }
    };
    private int mSubId;
    private TelephonyManager mTelephonyManager;

    public CellularDataConditionController(Context context, ConditionManager conditionManager) {
        this.mAppContext = context;
        this.mConditionManager = conditionManager;
        int defaultDataSubscriptionId = getDefaultDataSubscriptionId(context);
        this.mSubId = defaultDataSubscriptionId;
        this.mTelephonyManager = getTelephonyManager(context, defaultDataSubscriptionId);
        new GlobalSettingsChangeListener(context, "multi_sim_data_call") {
            /* class com.android.settings.homepage.contextualcards.conditional.CellularDataConditionController.AnonymousClass1 */

            @Override // com.android.settings.network.GlobalSettingsChangeListener
            public void onChanged(String str) {
                CellularDataConditionController cellularDataConditionController = CellularDataConditionController.this;
                int defaultDataSubscriptionId = cellularDataConditionController.getDefaultDataSubscriptionId(cellularDataConditionController.mAppContext);
                if (defaultDataSubscriptionId != CellularDataConditionController.this.mSubId) {
                    CellularDataConditionController.this.mSubId = defaultDataSubscriptionId;
                    if (CellularDataConditionController.this.mIsListeningConnectionChange) {
                        CellularDataConditionController cellularDataConditionController2 = CellularDataConditionController.this;
                        cellularDataConditionController2.restartPhoneStateListener(cellularDataConditionController2.mAppContext, defaultDataSubscriptionId);
                    }
                }
            }
        };
        this.mConnectivityManager = (ConnectivityManager) context.getSystemService(ConnectivityManager.class);
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public long getId() {
        return (long) ID;
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public boolean isDisplayable() {
        if (!this.mConnectivityManager.isNetworkSupported(0) || this.mTelephonyManager.getSimState() != 5) {
            return false;
        }
        return !this.mTelephonyManager.isDataEnabled();
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void onPrimaryClick(Context context) {
        context.startActivity(new Intent("oneplus.intent.action.SIM_AND_NETWORK_SETTINGS").addFlags(268435456));
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void onActionClick() {
        this.mTelephonyManager.setDataEnabled(true);
        this.mConditionManager.onConditionChanged();
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public ContextualCard buildContextualCard() {
        ConditionalContextualCard.Builder builder = new ConditionalContextualCard.Builder();
        builder.setConditionId((long) ID);
        builder.setMetricsConstant(380);
        builder.setActionText(this.mAppContext.getText(C0017R$string.oneplus_cellular_data_condition_turn_on));
        builder.setName(this.mAppContext.getPackageName() + "/" + ((Object) this.mAppContext.getText(C0017R$string.condition_cellular_title)));
        builder.setTitleText(this.mAppContext.getText(C0017R$string.condition_cellular_title).toString());
        builder.setSummaryText(this.mAppContext.getText(C0017R$string.condition_cellular_summary).toString());
        builder.setIconDrawable(this.mAppContext.getDrawable(C0008R$drawable.ic_cellular_off));
        builder.setViewType(ConditionContextualCardRenderer.VIEW_TYPE_FULL_WIDTH);
        return builder.build();
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void startMonitoringStateChange() {
        restartPhoneStateListener(this.mAppContext, this.mSubId);
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void stopMonitoringStateChange() {
        stopPhoneStateListener();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private int getDefaultDataSubscriptionId(Context context) {
        SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        return SubscriptionManager.getDefaultDataSubscriptionId();
    }

    private TelephonyManager getTelephonyManager(Context context, int i) {
        return ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(i);
    }

    private void stopPhoneStateListener() {
        this.mIsListeningConnectionChange = false;
        this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void restartPhoneStateListener(Context context, int i) {
        stopPhoneStateListener();
        this.mIsListeningConnectionChange = true;
        if (SubscriptionManager.isValidSubscriptionId(i)) {
            this.mTelephonyManager = getTelephonyManager(context, i);
        }
        this.mTelephonyManager.listen(this.mPhoneStateListener, 4096);
    }
}
