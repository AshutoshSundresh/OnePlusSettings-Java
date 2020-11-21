package com.google.android.setupdesign.template;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.setupcompat.internal.TemplateLayout;
import com.google.android.setupcompat.template.Mixin;
import com.google.android.setupdesign.DividerItemDecoration;
import com.google.android.setupdesign.GlifLayout;
import com.google.android.setupdesign.R$styleable;
import com.google.android.setupdesign.items.ItemHierarchy;
import com.google.android.setupdesign.items.ItemInflater;
import com.google.android.setupdesign.items.RecyclerItemAdapter;
import com.google.android.setupdesign.util.DrawableLayoutDirectionHelper;
import com.google.android.setupdesign.view.HeaderRecyclerView;

public class RecyclerMixin implements Mixin {
    private Drawable defaultDivider;
    private Drawable divider;
    private DividerItemDecoration dividerDecoration;
    private int dividerInsetEnd;
    private int dividerInsetStart;
    private View header;
    private final RecyclerView recyclerView;
    private final TemplateLayout templateLayout;

    public RecyclerMixin(TemplateLayout templateLayout2, RecyclerView recyclerView2) {
        this.templateLayout = templateLayout2;
        this.dividerDecoration = new DividerItemDecoration(templateLayout2.getContext());
        this.recyclerView = recyclerView2;
        recyclerView2.setLayoutManager(new LinearLayoutManager(this.templateLayout.getContext()));
        if (recyclerView2 instanceof HeaderRecyclerView) {
            this.header = ((HeaderRecyclerView) recyclerView2).getHeader();
        }
        this.recyclerView.addItemDecoration(this.dividerDecoration);
    }

    public void parseAttributes(AttributeSet attributeSet, int i) {
        Context context = this.templateLayout.getContext();
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.SudRecyclerMixin, i, 0);
        int resourceId = obtainStyledAttributes.getResourceId(R$styleable.SudRecyclerMixin_android_entries, 0);
        if (resourceId != 0) {
            ItemHierarchy itemHierarchy = (ItemHierarchy) new ItemInflater(context).inflate(resourceId);
            TemplateLayout templateLayout2 = this.templateLayout;
            RecyclerItemAdapter recyclerItemAdapter = new RecyclerItemAdapter(itemHierarchy, templateLayout2 instanceof GlifLayout ? ((GlifLayout) templateLayout2).shouldApplyPartnerHeavyThemeResource() : false);
            recyclerItemAdapter.setHasStableIds(obtainStyledAttributes.getBoolean(R$styleable.SudRecyclerMixin_sudHasStableIds, false));
            setAdapter(recyclerItemAdapter);
        }
        int dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(R$styleable.SudRecyclerMixin_sudDividerInset, -1);
        if (dimensionPixelSize != -1) {
            setDividerInset(dimensionPixelSize);
        } else {
            setDividerInsets(obtainStyledAttributes.getDimensionPixelSize(R$styleable.SudRecyclerMixin_sudDividerInsetStart, 0), obtainStyledAttributes.getDimensionPixelSize(R$styleable.SudRecyclerMixin_sudDividerInsetEnd, 0));
        }
        obtainStyledAttributes.recycle();
    }

    public RecyclerView getRecyclerView() {
        return this.recyclerView;
    }

    public View getHeader() {
        return this.header;
    }

    public void onLayout() {
        if (this.divider == null) {
            updateDivider();
        }
    }

    public RecyclerView.Adapter<? extends RecyclerView.ViewHolder> getAdapter() {
        RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter = this.recyclerView.getAdapter();
        return adapter instanceof HeaderRecyclerView.HeaderAdapter ? ((HeaderRecyclerView.HeaderAdapter) adapter).getWrappedAdapter() : adapter;
    }

    public void setAdapter(RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter) {
        this.recyclerView.setAdapter(adapter);
    }

    @Deprecated
    public void setDividerInset(int i) {
        setDividerInsets(i, 0);
    }

    public void setDividerInsets(int i, int i2) {
        this.dividerInsetStart = i;
        this.dividerInsetEnd = i2;
        updateDivider();
    }

    @Deprecated
    public int getDividerInset() {
        return getDividerInsetStart();
    }

    public int getDividerInsetStart() {
        return this.dividerInsetStart;
    }

    public int getDividerInsetEnd() {
        return this.dividerInsetEnd;
    }

    private void updateDivider() {
        if (Build.VERSION.SDK_INT >= 19 ? this.templateLayout.isLayoutDirectionResolved() : true) {
            if (this.defaultDivider == null) {
                this.defaultDivider = this.dividerDecoration.getDivider();
            }
            InsetDrawable createRelativeInsetDrawable = DrawableLayoutDirectionHelper.createRelativeInsetDrawable(this.defaultDivider, this.dividerInsetStart, 0, this.dividerInsetEnd, 0, this.templateLayout);
            this.divider = createRelativeInsetDrawable;
            this.dividerDecoration.setDivider(createRelativeInsetDrawable);
        }
    }

    public Drawable getDivider() {
        return this.divider;
    }

    public void setDividerItemDecoration(DividerItemDecoration dividerItemDecoration) {
        this.recyclerView.removeItemDecoration(this.dividerDecoration);
        this.dividerDecoration = dividerItemDecoration;
        this.recyclerView.addItemDecoration(dividerItemDecoration);
        updateDivider();
    }
}
