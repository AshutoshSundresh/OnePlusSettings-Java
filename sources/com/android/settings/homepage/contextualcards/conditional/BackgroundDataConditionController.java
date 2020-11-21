package com.android.settings.homepage.contextualcards.conditional;

import android.content.Context;
import android.content.Intent;
import android.net.NetworkPolicyManager;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settings.homepage.contextualcards.conditional.ConditionalContextualCard;
import java.util.Objects;

public class BackgroundDataConditionController implements ConditionalCardController {
    static final int ID = Objects.hash("BackgroundDataConditionController");
    private final Context mAppContext;
    private final ConditionManager mConditionManager;
    private final NetworkPolicyManager mNetworkPolicyManager;

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void startMonitoringStateChange() {
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void stopMonitoringStateChange() {
    }

    public BackgroundDataConditionController(Context context, ConditionManager conditionManager) {
        this.mAppContext = context;
        this.mConditionManager = conditionManager;
        this.mNetworkPolicyManager = (NetworkPolicyManager) context.getSystemService("netpolicy");
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public long getId() {
        return (long) ID;
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public boolean isDisplayable() {
        return this.mNetworkPolicyManager.getRestrictBackground();
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void onPrimaryClick(Context context) {
        context.startActivity(new Intent("com.oneplus.action.DATAUSAGE_SAVER").addFlags(268435456));
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void onActionClick() {
        this.mNetworkPolicyManager.setRestrictBackground(false);
        this.mConditionManager.onConditionChanged();
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public ContextualCard buildContextualCard() {
        ConditionalContextualCard.Builder builder = new ConditionalContextualCard.Builder();
        builder.setConditionId((long) ID);
        builder.setMetricsConstant(378);
        builder.setActionText(this.mAppContext.getText(C0017R$string.oneplus_bg_data_condition_turn_off));
        builder.setName(this.mAppContext.getPackageName() + "/" + ((Object) this.mAppContext.getText(C0017R$string.oneplus_condition_bg_data_title)));
        builder.setTitleText(this.mAppContext.getText(C0017R$string.oneplus_condition_bg_data_title).toString());
        builder.setSummaryText(this.mAppContext.getText(C0017R$string.oneplus_condition_bg_data_summary).toString());
        builder.setIconDrawable(this.mAppContext.getDrawable(C0008R$drawable.ic_data_saver));
        builder.setViewType(ConditionContextualCardRenderer.VIEW_TYPE_FULL_WIDTH);
        return builder.build();
    }
}
