package com.google.android.setupdesign.items;

import android.content.Context;

public class ItemInflater extends ReflectionInflater<ItemHierarchy> {

    public interface ItemParent {
        void addChild(ItemHierarchy itemHierarchy);
    }

    public ItemInflater(Context context) {
        super(context);
        setDefaultPackage(Item.class.getPackage().getName() + ".");
    }

    /* access modifiers changed from: protected */
    public void onAddChildItem(ItemHierarchy itemHierarchy, ItemHierarchy itemHierarchy2) {
        if (itemHierarchy instanceof ItemParent) {
            ((ItemParent) itemHierarchy).addChild(itemHierarchy2);
            return;
        }
        throw new IllegalArgumentException("Cannot add child item to " + itemHierarchy);
    }
}
