package com.android.settings.homepage.contextualcards.conditional;

import android.content.Context;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settings.homepage.contextualcards.conditional.ConditionalContextualCard;
import java.util.Objects;

public class RingerVibrateConditionController extends AbnormalRingerConditionController {
    static final int ID = Objects.hash("RingerVibrateConditionController");
    private final Context mAppContext;

    public RingerVibrateConditionController(Context context, ConditionManager conditionManager) {
        super(context, conditionManager);
        this.mAppContext = context;
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public long getId() {
        return (long) ID;
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public boolean isDisplayable() {
        return this.mAudioManager.getRingerModeInternal() == 1;
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public ContextualCard buildContextualCard() {
        ConditionalContextualCard.Builder builder = new ConditionalContextualCard.Builder();
        builder.setConditionId((long) ID);
        builder.setMetricsConstant(1369);
        builder.setName(this.mAppContext.getPackageName() + "/" + ((Object) this.mAppContext.getText(C0017R$string.oneplus_phone_vibrate)));
        builder.setTitleText(this.mAppContext.getText(C0017R$string.oneplus_phone_vibrate).toString());
        builder.setSummaryText(this.mAppContext.getText(C0017R$string.oneplus_phone_summary).toString());
        builder.setIconDrawable(this.mAppContext.getDrawable(C0008R$drawable.ic_volume_ringer_vibrate));
        builder.setViewType(ConditionContextualCardRenderer.VIEW_TYPE_FULL_WIDTH);
        return builder.build();
    }
}
