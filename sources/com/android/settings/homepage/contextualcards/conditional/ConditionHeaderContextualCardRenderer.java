package com.android.settings.homepage.contextualcards.conditional;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.android.settings.homepage.contextualcards.conditional.ConditionHeaderContextualCardRenderer;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import java.util.function.Consumer;

public class ConditionHeaderContextualCardRenderer implements ContextualCardRenderer {
    public static final int VIEW_TYPE = C0012R$layout.conditional_card_header;
    private final Context mContext;
    private final ControllerRendererPool mControllerRendererPool;

    public ConditionHeaderContextualCardRenderer(Context context, ControllerRendererPool controllerRendererPool) {
        this.mContext = context;
        this.mControllerRendererPool = controllerRendererPool;
    }

    @Override // com.android.settings.homepage.contextualcards.ContextualCardRenderer
    public RecyclerView.ViewHolder createViewHolder(View view, int i) {
        return new ConditionHeaderCardHolder(view);
    }

    @Override // com.android.settings.homepage.contextualcards.ContextualCardRenderer
    public void bindView(RecyclerView.ViewHolder viewHolder, ContextualCard contextualCard) {
        ConditionHeaderContextualCard conditionHeaderContextualCard = (ConditionHeaderContextualCard) contextualCard;
        ConditionHeaderCardHolder conditionHeaderCardHolder = (ConditionHeaderCardHolder) viewHolder;
        MetricsFeatureProvider metricsFeatureProvider = FeatureFactory.getFactory(this.mContext).getMetricsFeatureProvider();
        try {
            conditionHeaderCardHolder.icons.removeAllViews();
            conditionHeaderContextualCard.getConditionalCards().forEach(new Consumer(conditionHeaderCardHolder) {
                /* class com.android.settings.homepage.contextualcards.conditional.$$Lambda$ConditionHeaderContextualCardRenderer$5VFckGWp_oWyQ2332JKhOyycWqM */
                public final /* synthetic */ ConditionHeaderContextualCardRenderer.ConditionHeaderCardHolder f$1;

                {
                    this.f$1 = r2;
                }

                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ConditionHeaderContextualCardRenderer.this.lambda$bindView$0$ConditionHeaderContextualCardRenderer(this.f$1, (ContextualCard) obj);
                }
            });
            conditionHeaderCardHolder.itemView.setOnClickListener(new View.OnClickListener(metricsFeatureProvider) {
                /* class com.android.settings.homepage.contextualcards.conditional.$$Lambda$ConditionHeaderContextualCardRenderer$gudCNLzfoSmQiv2x98xm2QFBbYk */
                public final /* synthetic */ MetricsFeatureProvider f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    ConditionHeaderContextualCardRenderer.this.lambda$bindView$1$ConditionHeaderContextualCardRenderer(this.f$1, view);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$bindView$0 */
    public /* synthetic */ void lambda$bindView$0$ConditionHeaderContextualCardRenderer(ConditionHeaderCardHolder conditionHeaderCardHolder, ContextualCard contextualCard) {
        ImageView imageView = (ImageView) LayoutInflater.from(this.mContext).inflate(C0012R$layout.conditional_card_header_icon, (ViewGroup) conditionHeaderCardHolder.icons, false);
        LayerDrawable layerDrawable = (LayerDrawable) ContextCompat.getDrawable(conditionHeaderCardHolder.itemView.getContext(), C0008R$drawable.op_ic_homepage_common_card_round_icon);
        Drawable wrap = DrawableCompat.wrap(contextualCard.getIconDrawable());
        DrawableCompat.setTint(wrap, this.mContext.getResources().getColor(C0006R$color.oneplus_accent_color));
        layerDrawable.setDrawableByLayerId(C0010R$id.icon_view, wrap);
        imageView.setImageDrawable(layerDrawable);
        conditionHeaderCardHolder.icons.addView(imageView);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$bindView$1 */
    public /* synthetic */ void lambda$bindView$1$ConditionHeaderContextualCardRenderer(MetricsFeatureProvider metricsFeatureProvider, View view) {
        metricsFeatureProvider.action(0, 373, 1502, null, 1);
        ConditionContextualCardController conditionContextualCardController = (ConditionContextualCardController) this.mControllerRendererPool.getController(this.mContext, 4);
        conditionContextualCardController.setIsExpanded(true);
        conditionContextualCardController.onConditionsChanged();
    }

    public static class ConditionHeaderCardHolder extends RecyclerView.ViewHolder {
        public final LinearLayout icons;

        public ConditionHeaderCardHolder(View view) {
            super(view);
            this.icons = (LinearLayout) view.findViewById(C0010R$id.header_icons_container);
            ImageView imageView = (ImageView) view.findViewById(C0010R$id.expand_indicator);
        }
    }
}
