package com.android.settings.homepage.contextualcards.legacysuggestion;

import android.app.PendingIntent;
import android.service.settings.suggestions.Suggestion;
import com.android.settings.homepage.contextualcards.ContextualCard;

public class LegacySuggestionContextualCard extends ContextualCard {
    private final PendingIntent mPendingIntent;
    private final Suggestion mSuggestion;

    @Override // com.android.settings.homepage.contextualcards.ContextualCard
    public int getCardType() {
        return 2;
    }

    public LegacySuggestionContextualCard(Builder builder) {
        super(builder);
        this.mPendingIntent = builder.mPendingIntent;
        this.mSuggestion = builder.mSuggestion;
    }

    public PendingIntent getPendingIntent() {
        return this.mPendingIntent;
    }

    public Suggestion getSuggestion() {
        return this.mSuggestion;
    }

    public static class Builder extends ContextualCard.Builder {
        private PendingIntent mPendingIntent;
        private Suggestion mSuggestion;

        public Builder setPendingIntent(PendingIntent pendingIntent) {
            this.mPendingIntent = pendingIntent;
            return this;
        }

        public Builder setSuggestion(Suggestion suggestion) {
            this.mSuggestion = suggestion;
            return this;
        }

        @Override // com.android.settings.homepage.contextualcards.ContextualCard.Builder
        public Builder setCardType(int i) {
            throw new IllegalArgumentException("Cannot change card type for " + Builder.class.getName());
        }

        @Override // com.android.settings.homepage.contextualcards.ContextualCard.Builder
        public LegacySuggestionContextualCard build() {
            return new LegacySuggestionContextualCard(this);
        }
    }
}
