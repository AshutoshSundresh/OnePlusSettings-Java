package com.android.settings.homepage.contextualcards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settings.homepage.contextualcards.conditional.ConditionContextualCardRenderer;
import com.android.settings.homepage.contextualcards.slices.SliceContextualCardRenderer;
import com.android.settings.homepage.contextualcards.slices.SwipeDismissalDelegate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContextualCardsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ContextualCardUpdateListener, SwipeDismissalDelegate.Listener {
    private final Context mContext;
    final List<ContextualCard> mContextualCards = new ArrayList();
    private final ControllerRendererPool mControllerRendererPool;
    private final LifecycleOwner mLifecycleOwner;
    private RecyclerView mRecyclerView;

    public ContextualCardsAdapter(Context context, LifecycleOwner lifecycleOwner, ContextualCardManager contextualCardManager) {
        this.mContext = context;
        this.mControllerRendererPool = contextualCardManager.getControllerRendererPool();
        this.mLifecycleOwner = lifecycleOwner;
        setHasStableIds(true);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public long getItemId(int i) {
        return (long) this.mContextualCards.get(i).hashCode();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int i) {
        return this.mContextualCards.get(i).getViewType();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return this.mControllerRendererPool.getRendererByViewType(this.mContext, this.mLifecycleOwner, i).createViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(i, viewGroup, false), i);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        ContextualCard contextualCard = this.mContextualCards.get(i);
        this.mControllerRendererPool.getRendererByViewType(this.mContext, this.mLifecycleOwner, contextualCard.getViewType()).bindView(viewHolder, contextualCard);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.mContextualCards.size();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.mRecyclerView = recyclerView;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            ((GridLayoutManager) layoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                /* class com.android.settings.homepage.contextualcards.ContextualCardsAdapter.AnonymousClass1 */

                @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
                public int getSpanSize(int i) {
                    int viewType = ContextualCardsAdapter.this.mContextualCards.get(i).getViewType();
                    return (viewType == ConditionContextualCardRenderer.VIEW_TYPE_HALF_WIDTH || viewType == SliceContextualCardRenderer.VIEW_TYPE_HALF_WIDTH) ? 1 : 2;
                }
            });
        }
    }

    @Override // com.android.settings.homepage.contextualcards.ContextualCardUpdateListener
    public void onContextualCardUpdated(Map<Integer, List<ContextualCard>> map) {
        boolean z = false;
        List<ContextualCard> list = map.get(0);
        boolean isEmpty = this.mContextualCards.isEmpty();
        if (list == null || list.isEmpty()) {
            z = true;
        }
        if (list == null) {
            this.mContextualCards.clear();
            notifyDataSetChanged();
        } else {
            DiffUtil.DiffResult calculateDiff = DiffUtil.calculateDiff(new ContextualCardsDiffCallback(this.mContextualCards, list));
            this.mContextualCards.clear();
            this.mContextualCards.addAll(list);
            calculateDiff.dispatchUpdatesTo(this);
        }
        RecyclerView recyclerView = this.mRecyclerView;
        if (recyclerView != null && isEmpty && !z) {
            recyclerView.scheduleLayoutAnimation();
        }
    }

    @Override // com.android.settings.homepage.contextualcards.slices.SwipeDismissalDelegate.Listener
    public void onSwiped(int i) {
        ContextualCard.Builder mutate = this.mContextualCards.get(i).mutate();
        mutate.setIsPendingDismiss(true);
        this.mContextualCards.set(i, mutate.build());
        notifyItemChanged(i);
    }
}
