package com.google.android.setupdesign.items;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.google.android.setupdesign.R$layout;
import com.google.android.setupdesign.items.ItemInflater;
import java.util.ArrayList;
import java.util.Iterator;

public class ButtonBarItem extends AbstractItem implements ItemInflater.ItemParent {
    private final ArrayList<ButtonItem> buttons = new ArrayList<>();
    private boolean visible = true;

    @Override // com.google.android.setupdesign.items.IItem
    public boolean isEnabled() {
        return false;
    }

    public ButtonBarItem() {
    }

    public ButtonBarItem(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.google.android.setupdesign.items.AbstractItem, com.google.android.setupdesign.items.ItemHierarchy
    public int getCount() {
        return isVisible() ? 1 : 0;
    }

    @Override // com.google.android.setupdesign.items.IItem
    public int getLayoutResource() {
        return R$layout.sud_items_button_bar;
    }

    public boolean isVisible() {
        return this.visible;
    }

    @Override // com.google.android.setupdesign.items.AbstractItemHierarchy
    public int getViewId() {
        return getId();
    }

    @Override // com.google.android.setupdesign.items.IItem
    public void onBindView(View view) {
        LinearLayout linearLayout = (LinearLayout) view;
        linearLayout.removeAllViews();
        Iterator<ButtonItem> it = this.buttons.iterator();
        while (it.hasNext()) {
            linearLayout.addView(it.next().createButton(linearLayout));
        }
        view.setId(getViewId());
    }

    @Override // com.google.android.setupdesign.items.ItemInflater.ItemParent
    public void addChild(ItemHierarchy itemHierarchy) {
        if (itemHierarchy instanceof ButtonItem) {
            this.buttons.add((ButtonItem) itemHierarchy);
            return;
        }
        throw new UnsupportedOperationException("Cannot add non-button item to Button Bar");
    }
}
