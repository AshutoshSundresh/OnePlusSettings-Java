package com.android.settings.homepage.contextualcards;

import androidx.recyclerview.widget.DiffUtil;
import java.util.List;

public class ContextualCardsDiffCallback extends DiffUtil.Callback {
    private final List<ContextualCard> mNewCards;
    private final List<ContextualCard> mOldCards;

    public ContextualCardsDiffCallback(List<ContextualCard> list, List<ContextualCard> list2) {
        this.mOldCards = list;
        this.mNewCards = list2;
    }

    @Override // androidx.recyclerview.widget.DiffUtil.Callback
    public int getOldListSize() {
        return this.mOldCards.size();
    }

    @Override // androidx.recyclerview.widget.DiffUtil.Callback
    public int getNewListSize() {
        return this.mNewCards.size();
    }

    @Override // androidx.recyclerview.widget.DiffUtil.Callback
    public boolean areItemsTheSame(int i, int i2) {
        return this.mOldCards.get(i).getName().equals(this.mNewCards.get(i2).getName());
    }

    @Override // androidx.recyclerview.widget.DiffUtil.Callback
    public boolean areContentsTheSame(int i, int i2) {
        ContextualCard contextualCard = this.mNewCards.get(i2);
        if (contextualCard.getCategory() == 6 || contextualCard.getCategory() == 3 || contextualCard.hasInlineAction()) {
            return false;
        }
        return this.mOldCards.get(i).equals(contextualCard);
    }
}
