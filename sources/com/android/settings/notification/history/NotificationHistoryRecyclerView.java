package com.android.settings.notification.history;

import android.content.Context;
import android.util.AttributeSet;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NotificationHistoryRecyclerView extends RecyclerView {
    private OnItemSwipeDeleteListener listener;

    public interface OnItemSwipeDeleteListener {
        void onItemSwipeDeleted(int i);
    }

    public NotificationHistoryRecyclerView(Context context) {
        this(context, null);
    }

    public NotificationHistoryRecyclerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public NotificationHistoryRecyclerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setLayoutManager(new LinearLayoutManager(getContext()));
        addItemDecoration(new DividerItemDecoration(getContext(), 1));
        new ItemTouchHelper(new DismissTouchHelper(0, 48)).attachToRecyclerView(this);
        setNestedScrollingEnabled(false);
    }

    public void setOnItemSwipeDeleteListener(OnItemSwipeDeleteListener onItemSwipeDeleteListener) {
        this.listener = onItemSwipeDeleteListener;
    }

    private class DismissTouchHelper extends ItemTouchHelper.SimpleCallback {
        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
            return false;
        }

        public DismissTouchHelper(int i, int i2) {
            super(i, i2);
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
            if (NotificationHistoryRecyclerView.this.listener != null) {
                NotificationHistoryRecyclerView.this.listener.onItemSwipeDeleted(viewHolder.getAdapterPosition());
            }
        }
    }
}
