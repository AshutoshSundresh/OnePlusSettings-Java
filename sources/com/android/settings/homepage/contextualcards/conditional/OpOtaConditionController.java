package com.android.settings.homepage.contextualcards.conditional;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settings.homepage.contextualcards.conditional.ConditionalContextualCard;
import java.util.Objects;

public class OpOtaConditionController implements ConditionalCardController {
    static final int ID = Objects.hash("OpOtaConditionController");
    private final Context mAppContext;
    private final ConditionManager mConditionManager;

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void onActionClick() {
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void startMonitoringStateChange() {
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void stopMonitoringStateChange() {
    }

    public OpOtaConditionController(Context context, ConditionManager conditionManager) {
        this.mAppContext = context;
        this.mConditionManager = conditionManager;
        new Receiver();
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public long getId() {
        return (long) ID;
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public boolean isDisplayable() {
        boolean z = false;
        boolean z2 = Settings.System.getInt(this.mAppContext.getContentResolver(), "has_new_version_to_update", 0) == 1;
        if (Settings.System.getInt(this.mAppContext.getContentResolver(), "strong_prompt_ota", 0) == 100) {
            z = true;
        }
        Log.v("OPOTACondition", "systemHasUpdate = " + z2 + "    strongPromptOTA = " + z);
        return z2;
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void onPrimaryClick(Context context) {
        Intent intent = new Intent("oneplus.intent.action.CheckUpdate");
        intent.addFlags(268435456);
        if (isIntentResolvable(intent)) {
            context.startActivity(intent);
        } else {
            Log.e("OPOTACondition", "Not found Activity for: oneplus.intent.action.CheckUpdate");
        }
    }

    private boolean isIntentResolvable(Intent intent) {
        return this.mAppContext.getPackageManager().resolveActivity(intent, 0) != null;
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public ContextualCard buildContextualCard() {
        ConditionalContextualCard.Builder builder = new ConditionalContextualCard.Builder();
        builder.setConditionId((long) ID);
        builder.setMetricsConstant(9999);
        builder.setName(this.mAppContext.getPackageName() + "/" + ((Object) this.mAppContext.getText(C0017R$string.oneplus_ota_available)));
        builder.setTitleText(this.mAppContext.getText(C0017R$string.oneplus_ota_available).toString());
        builder.setSummaryText(this.mAppContext.getText(C0017R$string.oneplus_condition_ota_summary).toString());
        builder.setIconDrawable(this.mAppContext.getDrawable(C0008R$drawable.ic_system_update));
        builder.setViewType(ConditionContextualCardRenderer.VIEW_TYPE_FULL_WIDTH);
        return builder.build();
    }

    public class Receiver extends BroadcastReceiver {
        public Receiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction()) || "com.oem.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
                OpOtaConditionController.this.mConditionManager.onConditionChanged();
            }
        }
    }
}
