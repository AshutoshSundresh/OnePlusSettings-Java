package com.google.android.setupdesign.items;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.setupdesign.DividerItemDecoration;

class ItemViewHolder extends RecyclerView.ViewHolder implements DividerItemDecoration.DividedViewHolder {
    private boolean isEnabled;
    private IItem item;

    ItemViewHolder(View view) {
        super(view);
    }

    @Override // com.google.android.setupdesign.DividerItemDecoration.DividedViewHolder
    public boolean isDividerAllowedAbove() {
        IItem iItem = this.item;
        return iItem instanceof Dividable ? ((Dividable) iItem).isDividerAllowedAbove() : this.isEnabled;
    }

    @Override // com.google.android.setupdesign.DividerItemDecoration.DividedViewHolder
    public boolean isDividerAllowedBelow() {
        IItem iItem = this.item;
        return iItem instanceof Dividable ? ((Dividable) iItem).isDividerAllowedBelow() : this.isEnabled;
    }

    public void setEnabled(boolean z) {
        this.isEnabled = z;
        this.itemView.setClickable(z);
        this.itemView.setEnabled(z);
        this.itemView.setFocusable(z);
    }

    public void setItem(IItem iItem) {
        this.item = iItem;
    }

    public IItem getItem() {
        return this.item;
    }
}
