package com.android.settings.homepage.contextualcards.legacysuggestion;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settings.homepage.contextualcards.ContextualCardController;
import com.android.settings.homepage.contextualcards.ContextualCardRenderer;
import com.android.settings.homepage.contextualcards.ControllerRendererPool;

public class LegacySuggestionContextualCardRenderer implements ContextualCardRenderer {
    public static final int VIEW_TYPE = C0012R$layout.legacy_suggestion_tile;
    private final Context mContext;
    private final ControllerRendererPool mControllerRendererPool;

    public LegacySuggestionContextualCardRenderer(Context context, ControllerRendererPool controllerRendererPool) {
        this.mContext = context;
        this.mControllerRendererPool = controllerRendererPool;
    }

    @Override // com.android.settings.homepage.contextualcards.ContextualCardRenderer
    public RecyclerView.ViewHolder createViewHolder(View view, int i) {
        return new LegacySuggestionViewHolder(view);
    }

    @Override // com.android.settings.homepage.contextualcards.ContextualCardRenderer
    public void bindView(RecyclerView.ViewHolder viewHolder, ContextualCard contextualCard) {
        LegacySuggestionViewHolder legacySuggestionViewHolder = (LegacySuggestionViewHolder) viewHolder;
        ContextualCardController controller = this.mControllerRendererPool.getController(this.mContext, contextualCard.getCardType());
        legacySuggestionViewHolder.icon.setImageDrawable(contextualCard.getIconDrawable());
        legacySuggestionViewHolder.title.setText(contextualCard.getTitleText());
        legacySuggestionViewHolder.summary.setText(contextualCard.getSummaryText());
        legacySuggestionViewHolder.itemView.setOnClickListener(new View.OnClickListener(contextualCard) {
            /* class com.android.settings.homepage.contextualcards.legacysuggestion.$$Lambda$LegacySuggestionContextualCardRenderer$MDE2zT9pRUgspp8MT5mC_ZK8e1U */
            public final /* synthetic */ ContextualCard f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                ContextualCardController.this.onPrimaryClick(this.f$1);
            }
        });
        legacySuggestionViewHolder.closeButton.setOnClickListener(new View.OnClickListener(contextualCard) {
            /* class com.android.settings.homepage.contextualcards.legacysuggestion.$$Lambda$LegacySuggestionContextualCardRenderer$S8SrB_Rrb0w7W3urNdtTiTVQvMI */
            public final /* synthetic */ ContextualCard f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                ContextualCardController.this.onDismissed(this.f$1);
            }
        });
    }

    private static class LegacySuggestionViewHolder extends RecyclerView.ViewHolder {
        public final View closeButton;
        public final ImageView icon;
        public final TextView summary;
        public final TextView title;

        public LegacySuggestionViewHolder(View view) {
            super(view);
            this.icon = (ImageView) view.findViewById(16908294);
            this.title = (TextView) view.findViewById(16908310);
            this.summary = (TextView) view.findViewById(16908304);
            this.closeButton = view.findViewById(C0010R$id.close_button);
        }
    }
}
