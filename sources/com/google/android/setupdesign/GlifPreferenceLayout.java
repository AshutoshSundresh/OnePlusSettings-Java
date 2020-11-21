package com.google.android.setupdesign;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.setupdesign.template.RecyclerMixin;

public class GlifPreferenceLayout extends GlifRecyclerLayout {
    public GlifPreferenceLayout(Context context) {
        super(context);
    }

    public GlifPreferenceLayout(Context context, int i, int i2) {
        super(context, i, i2);
    }

    public GlifPreferenceLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public GlifPreferenceLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /* access modifiers changed from: protected */
    @Override // com.google.android.setupdesign.GlifLayout, com.google.android.setupcompat.internal.TemplateLayout, com.google.android.setupdesign.GlifRecyclerLayout, com.google.android.setupcompat.PartnerCustomizationLayout
    public ViewGroup findContainer(int i) {
        if (i == 0) {
            i = R$id.sud_layout_content;
        }
        return super.findContainer(i);
    }

    public RecyclerView onCreateRecyclerView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return this.recyclerMixin.getRecyclerView();
    }

    /* access modifiers changed from: protected */
    @Override // com.google.android.setupdesign.GlifLayout, com.google.android.setupcompat.internal.TemplateLayout, com.google.android.setupdesign.GlifRecyclerLayout, com.google.android.setupcompat.PartnerCustomizationLayout
    public View onInflateTemplate(LayoutInflater layoutInflater, int i) {
        if (i == 0) {
            i = R$layout.sud_glif_preference_template;
        }
        return super.onInflateTemplate(layoutInflater, i);
    }

    /* access modifiers changed from: protected */
    @Override // com.google.android.setupcompat.internal.TemplateLayout, com.google.android.setupdesign.GlifRecyclerLayout
    public void onTemplateInflated() {
        this.recyclerMixin = new RecyclerMixin(this, (RecyclerView) LayoutInflater.from(getContext()).inflate(R$layout.sud_glif_preference_recycler_view, (ViewGroup) this, false));
    }
}
