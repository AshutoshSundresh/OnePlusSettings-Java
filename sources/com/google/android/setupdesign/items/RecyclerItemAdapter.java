package com.google.android.setupdesign.items;

import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.setupcompat.partnerconfig.PartnerConfig;
import com.google.android.setupcompat.partnerconfig.PartnerConfigHelper;
import com.google.android.setupdesign.R$styleable;
import com.google.android.setupdesign.items.ItemHierarchy;

public class RecyclerItemAdapter extends RecyclerView.Adapter<ItemViewHolder> implements ItemHierarchy.Observer {
    public final boolean applyPartnerHeavyThemeResource;
    private final ItemHierarchy itemHierarchy;
    private OnItemSelectedListener listener;

    public interface OnItemSelectedListener {
        void onItemSelected(IItem iItem);
    }

    public RecyclerItemAdapter(ItemHierarchy itemHierarchy2, boolean z) {
        this.applyPartnerHeavyThemeResource = z;
        this.itemHierarchy = itemHierarchy2;
        itemHierarchy2.registerObserver(this);
    }

    public IItem getItem(int i) {
        return this.itemHierarchy.getItemAt(i);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public long getItemId(int i) {
        int id;
        IItem item = getItem(i);
        if (!(item instanceof AbstractItem) || (id = ((AbstractItem) item).getId()) <= 0) {
            return -1;
        }
        return (long) id;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.itemHierarchy.getCount();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Drawable drawable;
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(i, viewGroup, false);
        final ItemViewHolder itemViewHolder = new ItemViewHolder(inflate);
        if (!"noBackground".equals(inflate.getTag())) {
            TypedArray obtainStyledAttributes = viewGroup.getContext().obtainStyledAttributes(R$styleable.SudRecyclerItemAdapter);
            Drawable drawable2 = obtainStyledAttributes.getDrawable(R$styleable.SudRecyclerItemAdapter_android_selectableItemBackground);
            if (drawable2 == null) {
                drawable2 = obtainStyledAttributes.getDrawable(R$styleable.SudRecyclerItemAdapter_selectableItemBackground);
                drawable = null;
            } else {
                drawable = inflate.getBackground();
                if (drawable == null) {
                    if (this.applyPartnerHeavyThemeResource) {
                        drawable = new ColorDrawable(PartnerConfigHelper.get(inflate.getContext()).getColor(inflate.getContext(), PartnerConfig.CONFIG_LAYOUT_BACKGROUND_COLOR));
                    } else {
                        drawable = obtainStyledAttributes.getDrawable(R$styleable.SudRecyclerItemAdapter_android_colorBackground);
                    }
                }
            }
            if (drawable2 == null || drawable == null) {
                Log.e("RecyclerItemAdapter", "Cannot resolve required attributes. selectableItemBackground=" + drawable2 + " background=" + drawable);
            } else {
                inflate.setBackgroundDrawable(new PatchedLayerDrawable(new Drawable[]{drawable, drawable2}));
            }
            obtainStyledAttributes.recycle();
        }
        inflate.setOnClickListener(new View.OnClickListener() {
            /* class com.google.android.setupdesign.items.RecyclerItemAdapter.AnonymousClass1 */

            public void onClick(View view) {
                IItem item = itemViewHolder.getItem();
                if (RecyclerItemAdapter.this.listener != null && item != null && item.isEnabled()) {
                    RecyclerItemAdapter.this.listener.onItemSelected(item);
                }
            }
        });
        return itemViewHolder;
    }

    public void onBindViewHolder(ItemViewHolder itemViewHolder, int i) {
        IItem item = getItem(i);
        itemViewHolder.setEnabled(item.isEnabled());
        itemViewHolder.setItem(item);
        item.onBindView(itemViewHolder.itemView);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int i) {
        return getItem(i).getLayoutResource();
    }

    @Override // com.google.android.setupdesign.items.ItemHierarchy.Observer
    public void onItemRangeChanged(ItemHierarchy itemHierarchy2, int i, int i2) {
        notifyItemRangeChanged(i, i2);
    }

    @Override // com.google.android.setupdesign.items.ItemHierarchy.Observer
    public void onItemRangeInserted(ItemHierarchy itemHierarchy2, int i, int i2) {
        notifyItemRangeInserted(i, i2);
    }

    /* access modifiers changed from: package-private */
    public static class PatchedLayerDrawable extends LayerDrawable {
        PatchedLayerDrawable(Drawable[] drawableArr) {
            super(drawableArr);
        }

        public boolean getPadding(Rect rect) {
            return super.getPadding(rect) && !(rect.left == 0 && rect.top == 0 && rect.right == 0 && rect.bottom == 0);
        }
    }
}
