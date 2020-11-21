package com.google.android.setupdesign.items;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.google.android.setupdesign.R$id;
import com.google.android.setupdesign.util.DescriptionStyler;

public class DescriptionItem extends Item {
    private boolean applyPartnerDescriptionStyle = false;

    public DescriptionItem() {
    }

    public DescriptionItem(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public boolean shouldApplyPartnerDescriptionStyle() {
        return this.applyPartnerDescriptionStyle;
    }

    @Override // com.google.android.setupdesign.items.Item, com.google.android.setupdesign.items.IItem
    public void onBindView(View view) {
        super.onBindView(view);
        TextView textView = (TextView) view.findViewById(R$id.sud_items_title);
        if (shouldApplyPartnerDescriptionStyle()) {
            DescriptionStyler.applyPartnerCustomizationStyle(textView);
        }
    }
}
