package androidx.leanback.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.leanback.R$id;
import androidx.leanback.R$layout;
import androidx.leanback.R$transition;
import androidx.leanback.transition.TransitionHelper;
import androidx.leanback.util.StateMachine;
import androidx.leanback.widget.BrowseFrameLayout;
import androidx.leanback.widget.OnChildLaidOutListener;
import androidx.leanback.widget.VerticalGridPresenter;

public class VerticalGridSupportFragment extends BaseSupportFragment {
    final StateMachine.State STATE_SET_ENTRANCE_START_STATE = new StateMachine.State("SET_ENTRANCE_START_STATE") {
        /* class androidx.leanback.app.VerticalGridSupportFragment.AnonymousClass1 */

        @Override // androidx.leanback.util.StateMachine.State
        public void run() {
            VerticalGridSupportFragment.this.setEntranceTransitionState(false);
        }
    };
    private final OnChildLaidOutListener mChildLaidOutListener = new OnChildLaidOutListener() {
        /* class androidx.leanback.app.VerticalGridSupportFragment.AnonymousClass3 */

        @Override // androidx.leanback.widget.OnChildLaidOutListener
        public void onChildLaidOut(ViewGroup viewGroup, View view, int i, long j) {
            if (i == 0) {
                VerticalGridSupportFragment.this.showOrHideTitle();
            }
        }
    };
    private VerticalGridPresenter mGridPresenter;
    VerticalGridPresenter.ViewHolder mGridViewHolder;
    private Object mSceneAfterEntranceTransition;
    private int mSelectedPosition = -1;

    /* access modifiers changed from: package-private */
    @Override // androidx.leanback.app.BaseSupportFragment
    public void createStateMachineStates() {
        super.createStateMachineStates();
        this.mStateMachine.addState(this.STATE_SET_ENTRANCE_START_STATE);
    }

    /* access modifiers changed from: package-private */
    @Override // androidx.leanback.app.BaseSupportFragment
    public void createStateMachineTransitions() {
        super.createStateMachineTransitions();
        this.mStateMachine.addTransition(this.STATE_ENTRANCE_ON_PREPARED, this.STATE_SET_ENTRANCE_START_STATE, this.EVT_ON_CREATEVIEW);
    }

    /* access modifiers changed from: package-private */
    public void showOrHideTitle() {
        if (this.mGridViewHolder.getGridView().findViewHolderForAdapterPosition(this.mSelectedPosition) != null) {
            if (!this.mGridViewHolder.getGridView().hasPreviousViewInSameRow(this.mSelectedPosition)) {
                showTitle(true);
            } else {
                showTitle(false);
            }
        }
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        ViewGroup viewGroup2 = (ViewGroup) layoutInflater.inflate(R$layout.lb_vertical_grid_fragment, viewGroup, false);
        installTitleView(layoutInflater, (ViewGroup) viewGroup2.findViewById(R$id.grid_frame), bundle);
        getProgressBarManager().setRootView(viewGroup2);
        this.mGridPresenter.onCreateViewHolder((ViewGroup) viewGroup2.findViewById(R$id.browse_grid_dock));
        throw null;
    }

    private void setupFocusSearchListener() {
        ((BrowseFrameLayout) getView().findViewById(R$id.grid_frame)).setOnFocusSearchListener(getTitleHelper().getOnFocusSearchListener());
    }

    @Override // androidx.leanback.app.BrandedSupportFragment, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        setupFocusSearchListener();
    }

    @Override // androidx.leanback.app.BrandedSupportFragment, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        this.mGridViewHolder = null;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.leanback.app.BaseSupportFragment
    public Object createEntranceTransition() {
        return TransitionHelper.loadTransition(getContext(), R$transition.lb_vertical_grid_entrance_transition);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.leanback.app.BaseSupportFragment
    public void runEntranceTransition(Object obj) {
        TransitionHelper.runTransition(this.mSceneAfterEntranceTransition, obj);
    }

    /* access modifiers changed from: package-private */
    public void setEntranceTransitionState(boolean z) {
        this.mGridPresenter.setEntranceTransitionState(this.mGridViewHolder, z);
    }
}
