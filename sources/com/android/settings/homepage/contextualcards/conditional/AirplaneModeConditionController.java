package com.android.settings.homepage.contextualcards.conditional;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settings.homepage.contextualcards.conditional.ConditionalContextualCard;
import com.android.settingslib.WirelessUtils;
import java.util.Objects;

public class AirplaneModeConditionController implements ConditionalCardController {
    private static final IntentFilter AIRPLANE_MODE_FILTER = new IntentFilter("android.intent.action.AIRPLANE_MODE");
    static final int ID = Objects.hash("AirplaneModeConditionController");
    private final Context mAppContext;
    private final ConditionManager mConditionManager;
    private final Receiver mReceiver = new Receiver();

    public AirplaneModeConditionController(Context context, ConditionManager conditionManager) {
        this.mAppContext = context;
        this.mConditionManager = conditionManager;
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public long getId() {
        return (long) ID;
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public boolean isDisplayable() {
        return WirelessUtils.isAirplaneModeOn(this.mAppContext);
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void onPrimaryClick(Context context) {
        context.startActivity(new Intent("android.settings.WIRELESS_SETTINGS"));
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void onActionClick() {
        ConnectivityManager.from(this.mAppContext).setAirplaneMode(false);
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public ContextualCard buildContextualCard() {
        ConditionalContextualCard.Builder builder = new ConditionalContextualCard.Builder();
        builder.setConditionId((long) ID);
        builder.setMetricsConstant(377);
        builder.setActionText(this.mAppContext.getText(C0017R$string.oneplus_condition_airplane_mode_turn_off));
        builder.setName(this.mAppContext.getPackageName() + "/" + ((Object) this.mAppContext.getText(C0017R$string.condition_airplane_title)));
        builder.setTitleText(this.mAppContext.getText(C0017R$string.condition_airplane_title).toString());
        builder.setSummaryText(this.mAppContext.getText(C0017R$string.condition_airplane_summary).toString());
        builder.setIconDrawable(this.mAppContext.getDrawable(C0008R$drawable.ic_airplanemode_active));
        builder.setViewType(ConditionContextualCardRenderer.VIEW_TYPE_FULL_WIDTH);
        return builder.build();
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void startMonitoringStateChange() {
        this.mAppContext.registerReceiver(this.mReceiver, AIRPLANE_MODE_FILTER);
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void stopMonitoringStateChange() {
        this.mAppContext.unregisterReceiver(this.mReceiver);
    }

    public class Receiver extends BroadcastReceiver {
        public Receiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.AIRPLANE_MODE".equals(intent.getAction())) {
                AirplaneModeConditionController.this.mConditionManager.onConditionChanged();
            }
        }
    }
}
