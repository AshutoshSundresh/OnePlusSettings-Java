package androidx.leanback.util;

import java.util.ArrayList;
import java.util.Iterator;

public final class StateMachine {
    final ArrayList<State> mFinishedStates = new ArrayList<>();
    final ArrayList<State> mStates = new ArrayList<>();
    final ArrayList<State> mUnfinishedStates = new ArrayList<>();

    public static class Event {
        final String mName;

        public Event(String str) {
            this.mName = str;
        }
    }

    public static class Condition {
        final String mName;

        public abstract boolean canProceed();

        public Condition(String str) {
            this.mName = str;
        }
    }

    /* access modifiers changed from: package-private */
    public static class Transition {
        final Condition mCondition;
        final Event mEvent;
        final State mFromState;
        int mState = 0;
        final State mToState;

        Transition(State state, State state2, Event event) {
            if (event != null) {
                this.mFromState = state;
                this.mToState = state2;
                this.mEvent = event;
                this.mCondition = null;
                return;
            }
            throw new IllegalArgumentException();
        }

        Transition(State state, State state2) {
            this.mFromState = state;
            this.mToState = state2;
            this.mEvent = null;
            this.mCondition = null;
        }

        Transition(State state, State state2, Condition condition) {
            if (condition != null) {
                this.mFromState = state;
                this.mToState = state2;
                this.mEvent = null;
                this.mCondition = condition;
                return;
            }
            throw new IllegalArgumentException();
        }

        public String toString() {
            String str;
            Event event = this.mEvent;
            if (event != null) {
                str = event.mName;
            } else {
                Condition condition = this.mCondition;
                str = condition != null ? condition.mName : "auto";
            }
            return "[" + this.mFromState.mName + " -> " + this.mToState.mName + " <" + str + ">]";
        }
    }

    public static class State {
        final boolean mBranchEnd;
        final boolean mBranchStart;
        ArrayList<Transition> mIncomings;
        int mInvokedOutTransitions;
        final String mName;
        ArrayList<Transition> mOutgoings;
        int mStatus;

        public void run() {
        }

        public String toString() {
            return "[" + this.mName + " " + this.mStatus + "]";
        }

        public State(String str) {
            this(str, false, true);
        }

        public State(String str, boolean z, boolean z2) {
            this.mStatus = 0;
            this.mInvokedOutTransitions = 0;
            this.mName = str;
            this.mBranchStart = z;
            this.mBranchEnd = z2;
        }

        /* access modifiers changed from: package-private */
        public void addIncoming(Transition transition) {
            if (this.mIncomings == null) {
                this.mIncomings = new ArrayList<>();
            }
            this.mIncomings.add(transition);
        }

        /* access modifiers changed from: package-private */
        public void addOutgoing(Transition transition) {
            if (this.mOutgoings == null) {
                this.mOutgoings = new ArrayList<>();
            }
            this.mOutgoings.add(transition);
        }

        /* access modifiers changed from: package-private */
        public final boolean checkPreCondition() {
            ArrayList<Transition> arrayList = this.mIncomings;
            if (arrayList == null) {
                return true;
            }
            if (this.mBranchEnd) {
                Iterator<Transition> it = arrayList.iterator();
                while (it.hasNext()) {
                    if (it.next().mState != 1) {
                        return false;
                    }
                }
                return true;
            }
            Iterator<Transition> it2 = arrayList.iterator();
            while (it2.hasNext()) {
                if (it2.next().mState == 1) {
                    return true;
                }
            }
            return false;
        }

        /* access modifiers changed from: package-private */
        public final boolean runIfNeeded() {
            if (this.mStatus == 1 || !checkPreCondition()) {
                return false;
            }
            this.mStatus = 1;
            run();
            signalAutoTransitionsAfterRun();
            return true;
        }

        /* access modifiers changed from: package-private */
        public final void signalAutoTransitionsAfterRun() {
            Condition condition;
            ArrayList<Transition> arrayList = this.mOutgoings;
            if (arrayList != null) {
                Iterator<Transition> it = arrayList.iterator();
                while (it.hasNext()) {
                    Transition next = it.next();
                    if (next.mEvent == null && ((condition = next.mCondition) == null || condition.canProceed())) {
                        this.mInvokedOutTransitions++;
                        next.mState = 1;
                        if (!this.mBranchStart) {
                            return;
                        }
                    }
                }
            }
        }
    }

    public void addState(State state) {
        if (!this.mStates.contains(state)) {
            this.mStates.add(state);
        }
    }

    public void addTransition(State state, State state2, Event event) {
        Transition transition = new Transition(state, state2, event);
        state2.addIncoming(transition);
        state.addOutgoing(transition);
    }

    public void addTransition(State state, State state2, Condition condition) {
        Transition transition = new Transition(state, state2, condition);
        state2.addIncoming(transition);
        state.addOutgoing(transition);
    }

    public void addTransition(State state, State state2) {
        Transition transition = new Transition(state, state2);
        state2.addIncoming(transition);
        state.addOutgoing(transition);
    }

    public void start() {
        this.mUnfinishedStates.addAll(this.mStates);
        runUnfinishedStates();
    }

    /* access modifiers changed from: package-private */
    public void runUnfinishedStates() {
        boolean z;
        do {
            z = false;
            for (int size = this.mUnfinishedStates.size() - 1; size >= 0; size--) {
                State state = this.mUnfinishedStates.get(size);
                if (state.runIfNeeded()) {
                    this.mUnfinishedStates.remove(size);
                    this.mFinishedStates.add(state);
                    z = true;
                }
            }
        } while (z);
    }

    public void fireEvent(Event event) {
        for (int i = 0; i < this.mFinishedStates.size(); i++) {
            State state = this.mFinishedStates.get(i);
            if (state.mOutgoings != null && (state.mBranchStart || state.mInvokedOutTransitions <= 0)) {
                Iterator<Transition> it = state.mOutgoings.iterator();
                while (it.hasNext()) {
                    Transition next = it.next();
                    if (next.mState != 1 && next.mEvent == event) {
                        next.mState = 1;
                        state.mInvokedOutTransitions++;
                        if (!state.mBranchStart) {
                            break;
                        }
                    }
                }
            }
        }
        runUnfinishedStates();
    }
}
