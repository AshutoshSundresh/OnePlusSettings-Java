package com.android.settings.homepage.contextualcards.conditional;

import com.android.settings.homepage.contextualcards.ContextualCard;

public class ConditionFooterContextualCard extends ContextualCard {
    @Override // com.android.settings.homepage.contextualcards.ContextualCard
    public int getCardType() {
        return 5;
    }

    private ConditionFooterContextualCard(Builder builder) {
        super(builder);
    }

    public static class Builder extends ContextualCard.Builder {
        @Override // com.android.settings.homepage.contextualcards.ContextualCard.Builder
        public Builder setCardType(int i) {
            throw new IllegalArgumentException("Cannot change card type for " + Builder.class.getName());
        }

        @Override // com.android.settings.homepage.contextualcards.ContextualCard.Builder
        public ConditionFooterContextualCard build() {
            return new ConditionFooterContextualCard(this);
        }
    }
}
