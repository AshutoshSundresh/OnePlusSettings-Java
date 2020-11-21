package com.android.settings.homepage.contextualcards.conditional;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.C0006R$color;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settings.homepage.contextualcards.ContextualCardRenderer;
import com.android.settings.homepage.contextualcards.ControllerRendererPool;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;

public class ConditionContextualCardRenderer implements ContextualCardRenderer {
    public static final int VIEW_TYPE_FULL_WIDTH = C0012R$layout.conditional_card_full_tile;
    public static final int VIEW_TYPE_HALF_WIDTH = C0012R$layout.conditional_card_half_tile;
    private final Context mContext;
    private final ControllerRendererPool mControllerRendererPool;

    public ConditionContextualCardRenderer(Context context, ControllerRendererPool controllerRendererPool) {
        this.mContext = context;
        this.mControllerRendererPool = controllerRendererPool;
    }

    @Override // com.android.settings.homepage.contextualcards.ContextualCardRenderer
    public RecyclerView.ViewHolder createViewHolder(View view, int i) {
        return new ConditionalCardHolder(view);
    }

    @Override // com.android.settings.homepage.contextualcards.ContextualCardRenderer
    public void bindView(RecyclerView.ViewHolder viewHolder, ContextualCard contextualCard) {
        ConditionalCardHolder conditionalCardHolder = (ConditionalCardHolder) viewHolder;
        ConditionalContextualCard conditionalContextualCard = (ConditionalContextualCard) contextualCard;
        MetricsFeatureProvider metricsFeatureProvider = FeatureFactory.getFactory(this.mContext).getMetricsFeatureProvider();
        metricsFeatureProvider.visible(this.mContext, 1502, conditionalContextualCard.getMetricsConstant(), 0);
        initializePrimaryClick(conditionalCardHolder, conditionalContextualCard, metricsFeatureProvider);
        initializeView(conditionalCardHolder, conditionalContextualCard);
        initializeActionButton(conditionalCardHolder, conditionalContextualCard, metricsFeatureProvider);
    }

    private void initializePrimaryClick(ConditionalCardHolder conditionalCardHolder, ConditionalContextualCard conditionalContextualCard, MetricsFeatureProvider metricsFeatureProvider) {
        conditionalCardHolder.itemView.findViewById(C0010R$id.content).setOnClickListener(new View.OnClickListener(metricsFeatureProvider, conditionalContextualCard) {
            /* class com.android.settings.homepage.contextualcards.conditional.$$Lambda$ConditionContextualCardRenderer$mYIJR6MT82XJp0d3MUEsLTgalG0 */
            public final /* synthetic */ MetricsFeatureProvider f$1;
            public final /* synthetic */ ConditionalContextualCard f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void onClick(View view) {
                ConditionContextualCardRenderer.this.lambda$initializePrimaryClick$0$ConditionContextualCardRenderer(this.f$1, this.f$2, view);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$initializePrimaryClick$0 */
    public /* synthetic */ void lambda$initializePrimaryClick$0$ConditionContextualCardRenderer(MetricsFeatureProvider metricsFeatureProvider, ConditionalContextualCard conditionalContextualCard, View view) {
        metricsFeatureProvider.action(this.mContext, 375, conditionalContextualCard.getMetricsConstant());
        this.mControllerRendererPool.getController(this.mContext, conditionalContextualCard.getCardType()).onPrimaryClick(conditionalContextualCard);
    }

    private void initializeView(ConditionalCardHolder conditionalCardHolder, ConditionalContextualCard conditionalContextualCard) {
        LayerDrawable layerDrawable = (LayerDrawable) ContextCompat.getDrawable(conditionalCardHolder.itemView.getContext(), C0008R$drawable.op_ic_homepage_common_card_round_icon);
        Drawable wrap = DrawableCompat.wrap(conditionalContextualCard.getIconDrawable());
        DrawableCompat.setTint(wrap, this.mContext.getResources().getColor(C0006R$color.oneplus_accent_color));
        layerDrawable.setDrawableByLayerId(C0010R$id.icon_view, wrap);
        conditionalCardHolder.icon.setImageDrawable(layerDrawable);
        conditionalCardHolder.title.setText(conditionalContextualCard.getTitleText());
        conditionalCardHolder.summary.setText(conditionalContextualCard.getSummaryText());
    }

    private void initializeActionButton(ConditionalCardHolder conditionalCardHolder, ConditionalContextualCard conditionalContextualCard, MetricsFeatureProvider metricsFeatureProvider) {
        CharSequence actionText = conditionalContextualCard.getActionText();
        boolean z = !TextUtils.isEmpty(actionText);
        TextView textView = (TextView) conditionalCardHolder.itemView.findViewById(C0010R$id.first_action);
        if (z) {
            textView.setVisibility(0);
            textView.setText(actionText);
            textView.setOnClickListener(new View.OnClickListener(metricsFeatureProvider, conditionalContextualCard) {
                /* class com.android.settings.homepage.contextualcards.conditional.$$Lambda$ConditionContextualCardRenderer$pwAlpuPuZxYifx7itzC3y45MNfA */
                public final /* synthetic */ MetricsFeatureProvider f$1;
                public final /* synthetic */ ConditionalContextualCard f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(View view) {
                    ConditionContextualCardRenderer.this.lambda$initializeActionButton$1$ConditionContextualCardRenderer(this.f$1, this.f$2, view);
                }
            });
            return;
        }
        textView.setVisibility(8);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$initializeActionButton$1 */
    public /* synthetic */ void lambda$initializeActionButton$1$ConditionContextualCardRenderer(MetricsFeatureProvider metricsFeatureProvider, ConditionalContextualCard conditionalContextualCard, View view) {
        metricsFeatureProvider.action(view.getContext(), 376, conditionalContextualCard.getMetricsConstant());
        this.mControllerRendererPool.getController(this.mContext, conditionalContextualCard.getCardType()).onActionClick(conditionalContextualCard);
    }

    public static class ConditionalCardHolder extends RecyclerView.ViewHolder {
        public final ImageView icon;
        public final TextView summary;
        public final TextView title;

        public ConditionalCardHolder(View view) {
            super(view);
            this.icon = (ImageView) view.findViewById(16908294);
            this.title = (TextView) view.findViewById(16908310);
            this.summary = (TextView) view.findViewById(16908304);
        }
    }
}
