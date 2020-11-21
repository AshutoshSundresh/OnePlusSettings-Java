package com.google.android.setupdesign;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListAdapter;
import com.google.android.setupdesign.items.ItemAdapter;

@Deprecated
public class SetupWizardItemsLayout extends SetupWizardListLayout {
    public SetupWizardItemsLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public SetupWizardItemsLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // com.google.android.setupdesign.SetupWizardListLayout
    public ItemAdapter getAdapter() {
        ListAdapter adapter = super.getAdapter();
        if (adapter instanceof ItemAdapter) {
            return (ItemAdapter) adapter;
        }
        return null;
    }
}
