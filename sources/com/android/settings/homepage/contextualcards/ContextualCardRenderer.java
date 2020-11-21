package com.android.settings.homepage.contextualcards;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public interface ContextualCardRenderer {
    void bindView(RecyclerView.ViewHolder viewHolder, ContextualCard contextualCard);

    RecyclerView.ViewHolder createViewHolder(View view, int i);
}
