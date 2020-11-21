package com.android.settings.homepage.contextualcards;

public interface ContextualCardController {
    void onActionClick(ContextualCard contextualCard);

    void onDismissed(ContextualCard contextualCard);

    void onPrimaryClick(ContextualCard contextualCard);

    void setCardUpdateListener(ContextualCardUpdateListener contextualCardUpdateListener);
}
