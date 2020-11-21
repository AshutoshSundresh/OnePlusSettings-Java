package com.android.settings.homepage.contextualcards.conditional;

import com.android.settings.homepage.contextualcards.ContextualCard;

public class ConditionalContextualCard extends ContextualCard {
    static final double UNSUPPORTED_RANKING_SCORE = -100.0d;
    private final CharSequence mActionText;
    private final long mConditionId;
    private final int mMetricsConstant;

    @Override // com.android.settings.homepage.contextualcards.ContextualCard
    public int getCardType() {
        return 3;
    }

    private ConditionalContextualCard(Builder builder) {
        super(builder);
        this.mConditionId = builder.mConditionId;
        this.mMetricsConstant = builder.mMetricsConstant;
        this.mActionText = builder.mActionText;
    }

    public long getConditionId() {
        return this.mConditionId;
    }

    public int getMetricsConstant() {
        return this.mMetricsConstant;
    }

    public CharSequence getActionText() {
        return this.mActionText;
    }

    public static class Builder extends ContextualCard.Builder {
        private CharSequence mActionText;
        private long mConditionId;
        private int mMetricsConstant;

        public Builder setConditionId(long j) {
            this.mConditionId = j;
            return this;
        }

        public Builder setMetricsConstant(int i) {
            this.mMetricsConstant = i;
            return this;
        }

        public Builder setActionText(CharSequence charSequence) {
            this.mActionText = charSequence;
            return this;
        }

        @Override // com.android.settings.homepage.contextualcards.ContextualCard.Builder
        public Builder setCardType(int i) {
            throw new IllegalArgumentException("Cannot change card type for " + Builder.class.getName());
        }

        @Override // com.android.settings.homepage.contextualcards.ContextualCard.Builder
        public ConditionalContextualCard build() {
            setRankingScore(ConditionalContextualCard.UNSUPPORTED_RANKING_SCORE);
            return new ConditionalContextualCard(this);
        }
    }
}
