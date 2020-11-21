package androidx.leanback.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.ObjectAdapter;
import androidx.leanback.widget.OnChildViewHolderSelectedListener;
import androidx.leanback.widget.PresenterSelector;
import androidx.leanback.widget.VerticalGridView;
import androidx.recyclerview.widget.RecyclerView;

/* access modifiers changed from: package-private */
public abstract class BaseRowSupportFragment extends Fragment {
    private ObjectAdapter mAdapter;
    final ItemBridgeAdapter mBridgeAdapter = new ItemBridgeAdapter();
    LateSelectionObserver mLateSelectionObserver = new LateSelectionObserver();
    private boolean mPendingTransitionPrepare;
    private PresenterSelector mPresenterSelector;
    private final OnChildViewHolderSelectedListener mRowSelectedListener = new OnChildViewHolderSelectedListener() {
        /* class androidx.leanback.app.BaseRowSupportFragment.AnonymousClass1 */

        @Override // androidx.leanback.widget.OnChildViewHolderSelectedListener
        public void onChildViewHolderSelected(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int i, int i2) {
            BaseRowSupportFragment baseRowSupportFragment = BaseRowSupportFragment.this;
            if (!baseRowSupportFragment.mLateSelectionObserver.mIsLateSelection) {
                baseRowSupportFragment.mSelectedPosition = i;
                baseRowSupportFragment.onRowSelected(recyclerView, viewHolder, i, i2);
            }
        }
    };
    int mSelectedPosition = -1;
    VerticalGridView mVerticalGridView;

    /* access modifiers changed from: package-private */
    public abstract VerticalGridView findGridViewFromRoot(View view);

    /* access modifiers changed from: package-private */
    public abstract int getLayoutResourceId();

    /* access modifiers changed from: package-private */
    public abstract void onRowSelected(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int i, int i2);

    BaseRowSupportFragment() {
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(getLayoutResourceId(), viewGroup, false);
        this.mVerticalGridView = findGridViewFromRoot(inflate);
        if (this.mPendingTransitionPrepare) {
            this.mPendingTransitionPrepare = false;
            onTransitionPrepare();
        }
        return inflate;
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        if (bundle != null) {
            this.mSelectedPosition = bundle.getInt("currentSelectedPosition", -1);
        }
        setAdapterAndSelection();
        this.mVerticalGridView.setOnChildViewHolderSelectedListener(this.mRowSelectedListener);
    }

    /* access modifiers changed from: private */
    public class LateSelectionObserver extends RecyclerView.AdapterDataObserver {
        boolean mIsLateSelection = false;

        LateSelectionObserver() {
        }

        @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
        public void onChanged() {
            performLateSelection();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeInserted(int i, int i2) {
            performLateSelection();
        }

        /* access modifiers changed from: package-private */
        public void startLateSelection() {
            this.mIsLateSelection = true;
            BaseRowSupportFragment.this.mBridgeAdapter.registerAdapterDataObserver(this);
        }

        /* access modifiers changed from: package-private */
        public void performLateSelection() {
            clear();
            BaseRowSupportFragment baseRowSupportFragment = BaseRowSupportFragment.this;
            VerticalGridView verticalGridView = baseRowSupportFragment.mVerticalGridView;
            if (verticalGridView != null) {
                verticalGridView.setSelectedPosition(baseRowSupportFragment.mSelectedPosition);
            }
        }

        /* access modifiers changed from: package-private */
        public void clear() {
            if (this.mIsLateSelection) {
                this.mIsLateSelection = false;
                BaseRowSupportFragment.this.mBridgeAdapter.unregisterAdapterDataObserver(this);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setAdapterAndSelection() {
        if (this.mAdapter != null) {
            RecyclerView.Adapter adapter = this.mVerticalGridView.getAdapter();
            ItemBridgeAdapter itemBridgeAdapter = this.mBridgeAdapter;
            if (adapter != itemBridgeAdapter) {
                this.mVerticalGridView.setAdapter(itemBridgeAdapter);
            }
            if (this.mBridgeAdapter.getItemCount() == 0 && this.mSelectedPosition >= 0) {
                this.mLateSelectionObserver.startLateSelection();
                return;
            }
            int i = this.mSelectedPosition;
            if (i >= 0) {
                this.mVerticalGridView.setSelectedPosition(i);
            }
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        this.mLateSelectionObserver.clear();
        this.mVerticalGridView = null;
    }

    @Override // androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("currentSelectedPosition", this.mSelectedPosition);
    }

    public final void setPresenterSelector(PresenterSelector presenterSelector) {
        if (this.mPresenterSelector != presenterSelector) {
            this.mPresenterSelector = presenterSelector;
            updateAdapter();
        }
    }

    public final void setAdapter(ObjectAdapter objectAdapter) {
        if (this.mAdapter != objectAdapter) {
            this.mAdapter = objectAdapter;
            updateAdapter();
        }
    }

    public final ObjectAdapter getAdapter() {
        return this.mAdapter;
    }

    public final ItemBridgeAdapter getBridgeAdapter() {
        return this.mBridgeAdapter;
    }

    public void setSelectedPosition(int i) {
        setSelectedPosition(i, true);
    }

    public int getSelectedPosition() {
        return this.mSelectedPosition;
    }

    public void setSelectedPosition(int i, boolean z) {
        if (this.mSelectedPosition != i) {
            this.mSelectedPosition = i;
            VerticalGridView verticalGridView = this.mVerticalGridView;
            if (verticalGridView != null && !this.mLateSelectionObserver.mIsLateSelection) {
                if (z) {
                    verticalGridView.setSelectedPositionSmooth(i);
                } else {
                    verticalGridView.setSelectedPosition(i);
                }
            }
        }
    }

    public final VerticalGridView getVerticalGridView() {
        return this.mVerticalGridView;
    }

    /* access modifiers changed from: package-private */
    public void updateAdapter() {
        this.mBridgeAdapter.setAdapter(this.mAdapter);
        this.mBridgeAdapter.setPresenter(this.mPresenterSelector);
        if (this.mVerticalGridView != null) {
            setAdapterAndSelection();
        }
    }

    public boolean onTransitionPrepare() {
        VerticalGridView verticalGridView = this.mVerticalGridView;
        if (verticalGridView != null) {
            verticalGridView.setAnimateChildLayout(false);
            this.mVerticalGridView.setScrollEnabled(false);
            return true;
        }
        this.mPendingTransitionPrepare = true;
        return false;
    }

    public void onTransitionStart() {
        VerticalGridView verticalGridView = this.mVerticalGridView;
        if (verticalGridView != null) {
            verticalGridView.setPruneChild(false);
            this.mVerticalGridView.setLayoutFrozen(true);
            this.mVerticalGridView.setFocusSearchDisabled(true);
        }
    }

    public void onTransitionEnd() {
        VerticalGridView verticalGridView = this.mVerticalGridView;
        if (verticalGridView != null) {
            verticalGridView.setLayoutFrozen(false);
            this.mVerticalGridView.setAnimateChildLayout(true);
            this.mVerticalGridView.setPruneChild(true);
            this.mVerticalGridView.setFocusSearchDisabled(false);
            this.mVerticalGridView.setScrollEnabled(true);
        }
    }

    public void setAlignment(int i) {
        VerticalGridView verticalGridView = this.mVerticalGridView;
        if (verticalGridView != null) {
            verticalGridView.setItemAlignmentOffset(0);
            this.mVerticalGridView.setItemAlignmentOffsetPercent(-1.0f);
            this.mVerticalGridView.setWindowAlignmentOffset(i);
            this.mVerticalGridView.setWindowAlignmentOffsetPercent(-1.0f);
            this.mVerticalGridView.setWindowAlignment(0);
        }
    }
}
