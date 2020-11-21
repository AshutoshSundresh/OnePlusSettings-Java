package com.google.android.setupdesign.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.setupdesign.DividerItemDecoration;
import com.google.android.setupdesign.R$styleable;

public class HeaderRecyclerView extends RecyclerView {
    private View header;
    private int headerRes;
    private boolean shouldHandleActionUp = false;

    private static class HeaderViewHolder extends RecyclerView.ViewHolder implements DividerItemDecoration.DividedViewHolder {
        @Override // com.google.android.setupdesign.DividerItemDecoration.DividedViewHolder
        public boolean isDividerAllowedAbove() {
            return false;
        }

        @Override // com.google.android.setupdesign.DividerItemDecoration.DividedViewHolder
        public boolean isDividerAllowedBelow() {
            return false;
        }

        HeaderViewHolder(View view) {
            super(view);
        }
    }

    public static class HeaderAdapter<CVH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final RecyclerView.Adapter<CVH> adapter;
        private View header;
        private final RecyclerView.AdapterDataObserver observer;

        public HeaderAdapter(RecyclerView.Adapter<CVH> adapter2) {
            AnonymousClass1 r0 = new RecyclerView.AdapterDataObserver() {
                /* class com.google.android.setupdesign.view.HeaderRecyclerView.HeaderAdapter.AnonymousClass1 */

                @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
                public void onChanged() {
                    HeaderAdapter.this.notifyDataSetChanged();
                }

                @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
                public void onItemRangeChanged(int i, int i2) {
                    if (HeaderAdapter.this.header != null) {
                        i++;
                    }
                    HeaderAdapter.this.notifyItemRangeChanged(i, i2);
                }

                @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
                public void onItemRangeInserted(int i, int i2) {
                    if (HeaderAdapter.this.header != null) {
                        i++;
                    }
                    HeaderAdapter.this.notifyItemRangeInserted(i, i2);
                }

                @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
                public void onItemRangeMoved(int i, int i2, int i3) {
                    if (HeaderAdapter.this.header != null) {
                        i++;
                        i2++;
                    }
                    for (int i4 = 0; i4 < i3; i4++) {
                        HeaderAdapter.this.notifyItemMoved(i + i4, i2 + i4);
                    }
                }

                @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
                public void onItemRangeRemoved(int i, int i2) {
                    if (HeaderAdapter.this.header != null) {
                        i++;
                    }
                    HeaderAdapter.this.notifyItemRangeRemoved(i, i2);
                }
            };
            this.observer = r0;
            this.adapter = adapter2;
            adapter2.registerAdapterDataObserver(r0);
            setHasStableIds(this.adapter.hasStableIds());
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i != Integer.MAX_VALUE) {
                return this.adapter.onCreateViewHolder(viewGroup, i);
            }
            FrameLayout frameLayout = new FrameLayout(viewGroup.getContext());
            frameLayout.setLayoutParams(new FrameLayout.LayoutParams(-1, -2));
            return new HeaderViewHolder(frameLayout);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (this.header != null) {
                i--;
            }
            if (viewHolder instanceof HeaderViewHolder) {
                View view = this.header;
                if (view != null) {
                    if (view.getParent() != null) {
                        ((ViewGroup) this.header.getParent()).removeView(this.header);
                    }
                    ((FrameLayout) viewHolder.itemView).addView(this.header);
                    return;
                }
                throw new IllegalStateException("HeaderViewHolder cannot find mHeader");
            }
            this.adapter.onBindViewHolder(viewHolder, i);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (this.header != null) {
                i--;
            }
            if (i < 0) {
                return Integer.MAX_VALUE;
            }
            return this.adapter.getItemViewType(i);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            int itemCount = this.adapter.getItemCount();
            return this.header != null ? itemCount + 1 : itemCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public long getItemId(int i) {
            if (this.header != null) {
                i--;
            }
            if (i < 0) {
                return Long.MAX_VALUE;
            }
            return this.adapter.getItemId(i);
        }

        public void setHeader(View view) {
            this.header = view;
        }

        public RecyclerView.Adapter<CVH> getWrappedAdapter() {
            return this.adapter;
        }
    }

    public HeaderRecyclerView(Context context) {
        super(context);
        init(null, 0);
    }

    public HeaderRecyclerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(attributeSet, 0);
    }

    public HeaderRecyclerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(attributeSet, i);
    }

    private void init(AttributeSet attributeSet, int i) {
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R$styleable.SudHeaderRecyclerView, i, 0);
        this.headerRes = obtainStyledAttributes.getResourceId(R$styleable.SudHeaderRecyclerView_sudHeader, 0);
        obtainStyledAttributes.recycle();
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        int i = this.header != null ? 1 : 0;
        accessibilityEvent.setItemCount(accessibilityEvent.getItemCount() - i);
        accessibilityEvent.setFromIndex(Math.max(accessibilityEvent.getFromIndex() - i, 0));
        if (Build.VERSION.SDK_INT >= 14) {
            accessibilityEvent.setToIndex(Math.max(accessibilityEvent.getToIndex() - i, 0));
        }
    }

    private boolean handleDpadDown() {
        View findFocus = findFocus();
        if (findFocus == null) {
            return false;
        }
        int[] iArr = new int[2];
        int[] iArr2 = new int[2];
        findFocus.getLocationInWindow(iArr);
        getLocationInWindow(iArr2);
        int measuredHeight = (iArr[1] + findFocus.getMeasuredHeight()) - (iArr2[1] + getMeasuredHeight());
        if (measuredHeight <= 0) {
            return false;
        }
        smoothScrollBy(0, Math.min((int) (((float) getMeasuredHeight()) * 0.7f), measuredHeight));
        return true;
    }

    private boolean handleDpadUp() {
        View findFocus = findFocus();
        if (findFocus == null) {
            return false;
        }
        int[] iArr = new int[2];
        int[] iArr2 = new int[2];
        findFocus.getLocationInWindow(iArr);
        getLocationInWindow(iArr2);
        int i = iArr[1] - iArr2[1];
        if (i >= 0) {
            return false;
        }
        smoothScrollBy(0, Math.max((int) (((float) getMeasuredHeight()) * -0.7f), i));
        return true;
    }

    private boolean handleKeyEvent(KeyEvent keyEvent) {
        boolean z = false;
        if (!this.shouldHandleActionUp || keyEvent.getAction() != 1) {
            if (keyEvent.getAction() == 0) {
                int keyCode = keyEvent.getKeyCode();
                if (keyCode == 19) {
                    z = handleDpadUp();
                } else if (keyCode == 20) {
                    z = handleDpadDown();
                }
                this.shouldHandleActionUp = z;
            }
            return z;
        }
        this.shouldHandleActionUp = false;
        return true;
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (handleKeyEvent(keyEvent)) {
            return true;
        }
        return super.dispatchKeyEvent(keyEvent);
    }

    public View getHeader() {
        return this.header;
    }

    public void setHeader(View view) {
        this.header = view;
    }

    @Override // androidx.recyclerview.widget.RecyclerView
    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        super.setLayoutManager(layoutManager);
        if (layoutManager != null && this.header == null && this.headerRes != 0) {
            this.header = LayoutInflater.from(getContext()).inflate(this.headerRes, (ViewGroup) this, false);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView
    public void setAdapter(RecyclerView.Adapter adapter) {
        if (!(this.header == null || adapter == null)) {
            HeaderAdapter headerAdapter = new HeaderAdapter(adapter);
            headerAdapter.setHeader(this.header);
            adapter = headerAdapter;
        }
        super.setAdapter(adapter);
    }
}
