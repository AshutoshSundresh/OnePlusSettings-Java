package com.google.android.setupdesign;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.setupdesign.template.RecyclerMixin;
import com.google.android.setupdesign.template.RecyclerViewScrollHandlingDelegate;
import com.google.android.setupdesign.template.RequireScrollMixin;

public class SetupWizardRecyclerLayout extends SetupWizardLayout {
    protected RecyclerMixin recyclerMixin;

    public SetupWizardRecyclerLayout(Context context) {
        this(context, 0, 0);
    }

    public SetupWizardRecyclerLayout(Context context, int i, int i2) {
        super(context, i, i2);
        init(null, 0);
    }

    public SetupWizardRecyclerLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(attributeSet, 0);
    }

    public SetupWizardRecyclerLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(attributeSet, i);
    }

    private void init(AttributeSet attributeSet, int i) {
        this.recyclerMixin.parseAttributes(attributeSet, i);
        registerMixin(RecyclerMixin.class, this.recyclerMixin);
        RequireScrollMixin requireScrollMixin = (RequireScrollMixin) getMixin(RequireScrollMixin.class);
        requireScrollMixin.setScrollHandlingDelegate(new RecyclerViewScrollHandlingDelegate(requireScrollMixin, getRecyclerView()));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        this.recyclerMixin.onLayout();
    }

    public RecyclerView.Adapter<? extends RecyclerView.ViewHolder> getAdapter() {
        return this.recyclerMixin.getAdapter();
    }

    public void setAdapter(RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter) {
        this.recyclerMixin.setAdapter(adapter);
    }

    public RecyclerView getRecyclerView() {
        return this.recyclerMixin.getRecyclerView();
    }

    /* access modifiers changed from: protected */
    @Override // com.google.android.setupdesign.SetupWizardLayout, com.google.android.setupcompat.internal.TemplateLayout
    public ViewGroup findContainer(int i) {
        if (i == 0) {
            i = R$id.sud_recycler_view;
        }
        return super.findContainer(i);
    }

    /* access modifiers changed from: protected */
    @Override // com.google.android.setupdesign.SetupWizardLayout, com.google.android.setupcompat.internal.TemplateLayout
    public View onInflateTemplate(LayoutInflater layoutInflater, int i) {
        if (i == 0) {
            i = R$layout.sud_recycler_template;
        }
        return super.onInflateTemplate(layoutInflater, i);
    }

    /* access modifiers changed from: protected */
    @Override // com.google.android.setupcompat.internal.TemplateLayout
    public void onTemplateInflated() {
        View findViewById = findViewById(R$id.sud_recycler_view);
        if (findViewById instanceof RecyclerView) {
            this.recyclerMixin = new RecyclerMixin(this, (RecyclerView) findViewById);
            return;
        }
        throw new IllegalStateException("SetupWizardRecyclerLayout should use a template with recycler view");
    }

    @Override // com.google.android.setupcompat.internal.TemplateLayout
    public <T extends View> T findManagedViewById(int i) {
        T t;
        View header = this.recyclerMixin.getHeader();
        return (header == null || (t = (T) header.findViewById(i)) == null) ? (T) super.findViewById(i) : t;
    }

    @Deprecated
    public void setDividerInset(int i) {
        this.recyclerMixin.setDividerInset(i);
    }

    @Deprecated
    public int getDividerInset() {
        return this.recyclerMixin.getDividerInset();
    }

    public int getDividerInsetStart() {
        return this.recyclerMixin.getDividerInsetStart();
    }

    public int getDividerInsetEnd() {
        return this.recyclerMixin.getDividerInsetEnd();
    }

    public Drawable getDivider() {
        return this.recyclerMixin.getDivider();
    }
}
