package com.google.android.setupdesign;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.google.android.setupdesign.template.ListMixin;
import com.google.android.setupdesign.template.ListViewScrollHandlingDelegate;
import com.google.android.setupdesign.template.RequireScrollMixin;

public class GlifListLayout extends GlifLayout {
    private ListMixin listMixin;

    public GlifListLayout(Context context) {
        this(context, 0, 0);
    }

    public GlifListLayout(Context context, int i, int i2) {
        super(context, i, i2);
        init(null, 0);
    }

    public GlifListLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(attributeSet, 0);
    }

    @TargetApi(11)
    public GlifListLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(attributeSet, i);
    }

    private void init(AttributeSet attributeSet, int i) {
        ListMixin listMixin2 = new ListMixin(this, attributeSet, i);
        this.listMixin = listMixin2;
        registerMixin(ListMixin.class, listMixin2);
        RequireScrollMixin requireScrollMixin = (RequireScrollMixin) getMixin(RequireScrollMixin.class);
        requireScrollMixin.setScrollHandlingDelegate(new ListViewScrollHandlingDelegate(requireScrollMixin, getListView()));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        this.listMixin.onLayout();
    }

    /* access modifiers changed from: protected */
    @Override // com.google.android.setupdesign.GlifLayout, com.google.android.setupcompat.internal.TemplateLayout, com.google.android.setupcompat.PartnerCustomizationLayout
    public View onInflateTemplate(LayoutInflater layoutInflater, int i) {
        if (i == 0) {
            i = R$layout.sud_glif_list_template;
        }
        return super.onInflateTemplate(layoutInflater, i);
    }

    /* access modifiers changed from: protected */
    @Override // com.google.android.setupdesign.GlifLayout, com.google.android.setupcompat.internal.TemplateLayout, com.google.android.setupcompat.PartnerCustomizationLayout
    public ViewGroup findContainer(int i) {
        if (i == 0) {
            i = 16908298;
        }
        return super.findContainer(i);
    }

    public ListView getListView() {
        return this.listMixin.getListView();
    }

    public void setAdapter(ListAdapter listAdapter) {
        this.listMixin.setAdapter(listAdapter);
    }

    public ListAdapter getAdapter() {
        return this.listMixin.getAdapter();
    }

    @Deprecated
    public void setDividerInset(int i) {
        this.listMixin.setDividerInset(i);
    }

    @Deprecated
    public int getDividerInset() {
        return this.listMixin.getDividerInset();
    }

    public int getDividerInsetStart() {
        return this.listMixin.getDividerInsetStart();
    }

    public int getDividerInsetEnd() {
        return this.listMixin.getDividerInsetEnd();
    }

    public Drawable getDivider() {
        return this.listMixin.getDivider();
    }
}
