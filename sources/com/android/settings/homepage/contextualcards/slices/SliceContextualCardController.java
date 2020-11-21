package com.android.settings.homepage.contextualcards.slices;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import com.android.settings.C0017R$string;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settings.homepage.contextualcards.ContextualCardController;
import com.android.settings.homepage.contextualcards.ContextualCardFeedbackDialog;
import com.android.settings.homepage.contextualcards.ContextualCardUpdateListener;
import com.android.settings.homepage.contextualcards.logging.ContextualCardLogUtils;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.utils.ThreadUtils;

public class SliceContextualCardController implements ContextualCardController {
    private final Context mContext;

    @Override // com.android.settings.homepage.contextualcards.ContextualCardController
    public void onActionClick(ContextualCard contextualCard) {
    }

    @Override // com.android.settings.homepage.contextualcards.ContextualCardController
    public void onPrimaryClick(ContextualCard contextualCard) {
    }

    @Override // com.android.settings.homepage.contextualcards.ContextualCardController
    public void setCardUpdateListener(ContextualCardUpdateListener contextualCardUpdateListener) {
    }

    public SliceContextualCardController(Context context) {
        this.mContext = context;
    }

    @Override // com.android.settings.homepage.contextualcards.ContextualCardController
    public void onDismissed(ContextualCard contextualCard) {
        ThreadUtils.postOnBackgroundThread(new Runnable(contextualCard) {
            /* class com.android.settings.homepage.contextualcards.slices.$$Lambda$SliceContextualCardController$P_26mOZ27dbnBRjtYeVdsGDtE8 */
            public final /* synthetic */ ContextualCard f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                SliceContextualCardController.this.lambda$onDismissed$0$SliceContextualCardController(this.f$1);
            }
        });
        showFeedbackDialog(contextualCard);
        FeatureFactory.getFactory(this.mContext).getMetricsFeatureProvider().action(this.mContext, 1665, ContextualCardLogUtils.buildCardDismissLog(contextualCard));
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onDismissed$0 */
    public /* synthetic */ void lambda$onDismissed$0$SliceContextualCardController(ContextualCard contextualCard) {
        FeatureFactory.getFactory(this.mContext).getContextualCardFeatureProvider(this.mContext).markCardAsDismissed(this.mContext, contextualCard.getName());
    }

    /* access modifiers changed from: package-private */
    public void showFeedbackDialog(ContextualCard contextualCard) {
        String string = this.mContext.getString(C0017R$string.config_contextual_card_feedback_email);
        if (isFeedbackEnabled(string)) {
            Intent intent = new Intent(this.mContext, ContextualCardFeedbackDialog.class);
            intent.putExtra("card_name", getSimpleCardName(contextualCard));
            intent.putExtra("feedback_email", string);
            intent.addFlags(268435456);
            this.mContext.startActivity(intent);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isFeedbackEnabled(String str) {
        return !TextUtils.isEmpty(str) && Build.IS_DEBUGGABLE;
    }

    private String getSimpleCardName(ContextualCard contextualCard) {
        String[] split = contextualCard.getName().split("/");
        return split[split.length - 1];
    }
}
