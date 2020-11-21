package com.android.settings.homepage.contextualcards.conditional;

import android.text.TextUtils;
import com.android.settings.homepage.contextualcards.ContextualCard;
import java.util.List;
import java.util.Objects;

public class ConditionHeaderContextualCard extends ContextualCard {
    private final List<ContextualCard> mConditionalCards;

    @Override // com.android.settings.homepage.contextualcards.ContextualCard
    public int getCardType() {
        return 4;
    }

    private ConditionHeaderContextualCard(Builder builder) {
        super(builder);
        this.mConditionalCards = builder.mConditionalCards;
    }

    public List<ContextualCard> getConditionalCards() {
        return this.mConditionalCards;
    }

    @Override // com.android.settings.homepage.contextualcards.ContextualCard
    public int hashCode() {
        return Objects.hash(getName(), this.mConditionalCards);
    }

    @Override // com.android.settings.homepage.contextualcards.ContextualCard
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ConditionHeaderContextualCard)) {
            return false;
        }
        ConditionHeaderContextualCard conditionHeaderContextualCard = (ConditionHeaderContextualCard) obj;
        return TextUtils.equals(getName(), conditionHeaderContextualCard.getName()) && this.mConditionalCards.equals(conditionHeaderContextualCard.mConditionalCards);
    }

    public static class Builder extends ContextualCard.Builder {
        private List<ContextualCard> mConditionalCards;

        public Builder setConditionalCards(List<ContextualCard> list) {
            this.mConditionalCards = list;
            return this;
        }

        @Override // com.android.settings.homepage.contextualcards.ContextualCard.Builder
        public Builder setCardType(int i) {
            throw new IllegalArgumentException("Cannot change card type for " + Builder.class.getName());
        }

        @Override // com.android.settings.homepage.contextualcards.ContextualCard.Builder
        public ConditionHeaderContextualCard build() {
            return new ConditionHeaderContextualCard(this);
        }
    }
}
