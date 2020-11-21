package androidx.leanback.app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import androidx.leanback.transition.TransitionHelper;
import androidx.leanback.transition.TransitionListener;
import androidx.leanback.util.StateMachine;

public class BaseSupportFragment extends BrandedSupportFragment {
    final StateMachine.Condition COND_TRANSITION_NOT_SUPPORTED = new StateMachine.Condition(this, "EntranceTransitionNotSupport") {
        /* class androidx.leanback.app.BaseSupportFragment.AnonymousClass5 */

        @Override // androidx.leanback.util.StateMachine.Condition
        public boolean canProceed() {
            return !TransitionHelper.systemSupportsEntranceTransitions();
        }
    };
    final StateMachine.Event EVT_ENTRANCE_END = new StateMachine.Event("onEntranceTransitionEnd");
    final StateMachine.Event EVT_ON_CREATE = new StateMachine.Event("onCreate");
    final StateMachine.Event EVT_ON_CREATEVIEW = new StateMachine.Event("onCreateView");
    final StateMachine.Event EVT_PREPARE_ENTRANCE = new StateMachine.Event("prepareEntranceTransition");
    final StateMachine.Event EVT_START_ENTRANCE = new StateMachine.Event("startEntranceTransition");
    final StateMachine.State STATE_ENTRANCE_COMPLETE = new StateMachine.State("ENTRANCE_COMPLETE", true, false);
    final StateMachine.State STATE_ENTRANCE_INIT = new StateMachine.State("ENTRANCE_INIT");
    final StateMachine.State STATE_ENTRANCE_ON_ENDED = new StateMachine.State("ENTRANCE_ON_ENDED") {
        /* class androidx.leanback.app.BaseSupportFragment.AnonymousClass4 */

        @Override // androidx.leanback.util.StateMachine.State
        public void run() {
            BaseSupportFragment.this.onEntranceTransitionEnd();
        }
    };
    final StateMachine.State STATE_ENTRANCE_ON_PREPARED = new StateMachine.State("ENTRANCE_ON_PREPARED", true, false) {
        /* class androidx.leanback.app.BaseSupportFragment.AnonymousClass1 */

        @Override // androidx.leanback.util.StateMachine.State
        public void run() {
            BaseSupportFragment.this.mProgressBarManager.show();
        }
    };
    final StateMachine.State STATE_ENTRANCE_ON_PREPARED_ON_CREATEVIEW = new StateMachine.State("ENTRANCE_ON_PREPARED_ON_CREATEVIEW") {
        /* class androidx.leanback.app.BaseSupportFragment.AnonymousClass2 */

        @Override // androidx.leanback.util.StateMachine.State
        public void run() {
            BaseSupportFragment.this.onEntranceTransitionPrepare();
        }
    };
    final StateMachine.State STATE_ENTRANCE_PERFORM = new StateMachine.State("STATE_ENTRANCE_PERFORM") {
        /* class androidx.leanback.app.BaseSupportFragment.AnonymousClass3 */

        @Override // androidx.leanback.util.StateMachine.State
        public void run() {
            BaseSupportFragment.this.mProgressBarManager.hide();
            BaseSupportFragment.this.onExecuteEntranceTransition();
        }
    };
    final StateMachine.State STATE_START = new StateMachine.State("START", true, false);
    Object mEntranceTransition;
    final ProgressBarManager mProgressBarManager = new ProgressBarManager();
    final StateMachine mStateMachine = new StateMachine();

    /* access modifiers changed from: protected */
    public Object createEntranceTransition() {
        return null;
    }

    /* access modifiers changed from: protected */
    public void onEntranceTransitionEnd() {
    }

    /* access modifiers changed from: protected */
    public void onEntranceTransitionPrepare() {
    }

    /* access modifiers changed from: protected */
    public void onEntranceTransitionStart() {
    }

    /* access modifiers changed from: protected */
    public void runEntranceTransition(Object obj) {
    }

    @SuppressLint({"ValidFragment"})
    BaseSupportFragment() {
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        createStateMachineStates();
        createStateMachineTransitions();
        this.mStateMachine.start();
        super.onCreate(bundle);
        this.mStateMachine.fireEvent(this.EVT_ON_CREATE);
    }

    /* access modifiers changed from: package-private */
    public void createStateMachineStates() {
        this.mStateMachine.addState(this.STATE_START);
        this.mStateMachine.addState(this.STATE_ENTRANCE_INIT);
        this.mStateMachine.addState(this.STATE_ENTRANCE_ON_PREPARED);
        this.mStateMachine.addState(this.STATE_ENTRANCE_ON_PREPARED_ON_CREATEVIEW);
        this.mStateMachine.addState(this.STATE_ENTRANCE_PERFORM);
        this.mStateMachine.addState(this.STATE_ENTRANCE_ON_ENDED);
        this.mStateMachine.addState(this.STATE_ENTRANCE_COMPLETE);
    }

    /* access modifiers changed from: package-private */
    public void createStateMachineTransitions() {
        this.mStateMachine.addTransition(this.STATE_START, this.STATE_ENTRANCE_INIT, this.EVT_ON_CREATE);
        this.mStateMachine.addTransition(this.STATE_ENTRANCE_INIT, this.STATE_ENTRANCE_COMPLETE, this.COND_TRANSITION_NOT_SUPPORTED);
        this.mStateMachine.addTransition(this.STATE_ENTRANCE_INIT, this.STATE_ENTRANCE_COMPLETE, this.EVT_ON_CREATEVIEW);
        this.mStateMachine.addTransition(this.STATE_ENTRANCE_INIT, this.STATE_ENTRANCE_ON_PREPARED, this.EVT_PREPARE_ENTRANCE);
        this.mStateMachine.addTransition(this.STATE_ENTRANCE_ON_PREPARED, this.STATE_ENTRANCE_ON_PREPARED_ON_CREATEVIEW, this.EVT_ON_CREATEVIEW);
        this.mStateMachine.addTransition(this.STATE_ENTRANCE_ON_PREPARED, this.STATE_ENTRANCE_PERFORM, this.EVT_START_ENTRANCE);
        this.mStateMachine.addTransition(this.STATE_ENTRANCE_ON_PREPARED_ON_CREATEVIEW, this.STATE_ENTRANCE_PERFORM);
        this.mStateMachine.addTransition(this.STATE_ENTRANCE_PERFORM, this.STATE_ENTRANCE_ON_ENDED, this.EVT_ENTRANCE_END);
        this.mStateMachine.addTransition(this.STATE_ENTRANCE_ON_ENDED, this.STATE_ENTRANCE_COMPLETE);
    }

    @Override // androidx.leanback.app.BrandedSupportFragment, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.mStateMachine.fireEvent(this.EVT_ON_CREATEVIEW);
    }

    /* access modifiers changed from: package-private */
    public void onExecuteEntranceTransition() {
        final View view = getView();
        if (view != null) {
            view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                /* class androidx.leanback.app.BaseSupportFragment.AnonymousClass6 */

                public boolean onPreDraw() {
                    view.getViewTreeObserver().removeOnPreDrawListener(this);
                    if (BaseSupportFragment.this.getContext() == null || BaseSupportFragment.this.getView() == null) {
                        return true;
                    }
                    BaseSupportFragment.this.internalCreateEntranceTransition();
                    BaseSupportFragment.this.onEntranceTransitionStart();
                    BaseSupportFragment baseSupportFragment = BaseSupportFragment.this;
                    Object obj = baseSupportFragment.mEntranceTransition;
                    if (obj != null) {
                        baseSupportFragment.runEntranceTransition(obj);
                        return false;
                    }
                    baseSupportFragment.mStateMachine.fireEvent(baseSupportFragment.EVT_ENTRANCE_END);
                    return false;
                }
            });
            view.invalidate();
        }
    }

    /* access modifiers changed from: package-private */
    public void internalCreateEntranceTransition() {
        Object createEntranceTransition = createEntranceTransition();
        this.mEntranceTransition = createEntranceTransition;
        if (createEntranceTransition != null) {
            TransitionHelper.addTransitionListener(createEntranceTransition, new TransitionListener() {
                /* class androidx.leanback.app.BaseSupportFragment.AnonymousClass7 */

                @Override // androidx.leanback.transition.TransitionListener
                public void onTransitionEnd(Object obj) {
                    BaseSupportFragment baseSupportFragment = BaseSupportFragment.this;
                    baseSupportFragment.mEntranceTransition = null;
                    baseSupportFragment.mStateMachine.fireEvent(baseSupportFragment.EVT_ENTRANCE_END);
                }
            });
        }
    }

    public final ProgressBarManager getProgressBarManager() {
        return this.mProgressBarManager;
    }
}
