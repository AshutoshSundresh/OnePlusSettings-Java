package androidx.fragment.app;

import android.view.ViewGroup;
import androidx.core.os.CancellationSignal;
import androidx.fragment.R$id;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* access modifiers changed from: package-private */
public abstract class SpecialEffectsController {
    final HashMap<Fragment, Operation> mAwaitingCompletionOperations = new HashMap<>();
    private final ViewGroup mContainer;
    boolean mIsContainerPostponed = false;
    boolean mOperationDirectionIsPop = false;
    final ArrayList<Operation> mPendingOperations = new ArrayList<>();

    /* access modifiers changed from: package-private */
    public abstract void executeOperations(List<Operation> list, boolean z);

    static SpecialEffectsController getOrCreateController(ViewGroup viewGroup, FragmentManager fragmentManager) {
        return getOrCreateController(viewGroup, fragmentManager.getSpecialEffectsControllerFactory());
    }

    static SpecialEffectsController getOrCreateController(ViewGroup viewGroup, SpecialEffectsControllerFactory specialEffectsControllerFactory) {
        Object tag = viewGroup.getTag(R$id.special_effects_controller_view_tag);
        if (tag instanceof SpecialEffectsController) {
            return (SpecialEffectsController) tag;
        }
        SpecialEffectsController createController = specialEffectsControllerFactory.createController(viewGroup);
        viewGroup.setTag(R$id.special_effects_controller_view_tag, createController);
        return createController;
    }

    SpecialEffectsController(ViewGroup viewGroup) {
        this.mContainer = viewGroup;
    }

    public ViewGroup getContainer() {
        return this.mContainer;
    }

    /* access modifiers changed from: package-private */
    public Operation.Type getAwaitingCompletionType(FragmentStateManager fragmentStateManager) {
        Operation operation = this.mAwaitingCompletionOperations.get(fragmentStateManager.getFragment());
        if (operation != null) {
            return operation.getType();
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public void enqueueAdd(FragmentStateManager fragmentStateManager, CancellationSignal cancellationSignal) {
        enqueue(Operation.Type.ADD, fragmentStateManager, cancellationSignal);
    }

    /* access modifiers changed from: package-private */
    public void enqueueShow(FragmentStateManager fragmentStateManager, CancellationSignal cancellationSignal) {
        enqueue(Operation.Type.SHOW, fragmentStateManager, cancellationSignal);
    }

    /* access modifiers changed from: package-private */
    public void enqueueHide(FragmentStateManager fragmentStateManager, CancellationSignal cancellationSignal) {
        enqueue(Operation.Type.HIDE, fragmentStateManager, cancellationSignal);
    }

    /* access modifiers changed from: package-private */
    public void enqueueRemove(FragmentStateManager fragmentStateManager, CancellationSignal cancellationSignal) {
        enqueue(Operation.Type.REMOVE, fragmentStateManager, cancellationSignal);
    }

    private void enqueue(Operation.Type type, FragmentStateManager fragmentStateManager, CancellationSignal cancellationSignal) {
        if (!cancellationSignal.isCanceled()) {
            synchronized (this.mPendingOperations) {
                final CancellationSignal cancellationSignal2 = new CancellationSignal();
                final FragmentStateManagerOperation fragmentStateManagerOperation = new FragmentStateManagerOperation(type, fragmentStateManager, cancellationSignal2);
                this.mPendingOperations.add(fragmentStateManagerOperation);
                this.mAwaitingCompletionOperations.put(fragmentStateManagerOperation.getFragment(), fragmentStateManagerOperation);
                cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
                    /* class androidx.fragment.app.SpecialEffectsController.AnonymousClass1 */

                    @Override // androidx.core.os.CancellationSignal.OnCancelListener
                    public void onCancel() {
                        synchronized (SpecialEffectsController.this.mPendingOperations) {
                            SpecialEffectsController.this.mPendingOperations.remove(fragmentStateManagerOperation);
                            SpecialEffectsController.this.mAwaitingCompletionOperations.remove(fragmentStateManagerOperation.getFragment());
                            cancellationSignal2.cancel();
                        }
                    }
                });
                fragmentStateManagerOperation.addCompletionListener(new Runnable() {
                    /* class androidx.fragment.app.SpecialEffectsController.AnonymousClass2 */

                    public void run() {
                        SpecialEffectsController.this.mAwaitingCompletionOperations.remove(fragmentStateManagerOperation.getFragment());
                    }
                });
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void updateOperationDirection(boolean z) {
        this.mOperationDirectionIsPop = z;
    }

    /* access modifiers changed from: package-private */
    public void markPostponedState() {
        Operation operation;
        synchronized (this.mPendingOperations) {
            this.mIsContainerPostponed = false;
            int size = this.mPendingOperations.size() - 1;
            while (true) {
                if (size < 0) {
                    break;
                }
                operation = this.mPendingOperations.get(size);
                if (operation.getType() == Operation.Type.ADD) {
                    break;
                } else if (operation.getType() == Operation.Type.SHOW) {
                    break;
                } else {
                    size--;
                }
            }
            this.mIsContainerPostponed = operation.getFragment().isPostponed();
        }
    }

    /* access modifiers changed from: package-private */
    public void forcePostponedExecutePendingOperations() {
        if (this.mIsContainerPostponed) {
            this.mIsContainerPostponed = false;
            executePendingOperations();
        }
    }

    /* access modifiers changed from: package-private */
    public void executePendingOperations() {
        if (!this.mIsContainerPostponed) {
            synchronized (this.mPendingOperations) {
                executeOperations(new ArrayList(this.mPendingOperations), this.mOperationDirectionIsPop);
                this.mPendingOperations.clear();
                this.mOperationDirectionIsPop = false;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void cancelAllOperations() {
        synchronized (this.mPendingOperations) {
            for (Operation operation : this.mAwaitingCompletionOperations.values()) {
                operation.getCancellationSignal().cancel();
            }
            this.mAwaitingCompletionOperations.clear();
            this.mPendingOperations.clear();
        }
    }

    /* access modifiers changed from: package-private */
    public static class Operation {
        private final CancellationSignal mCancellationSignal;
        private final List<Runnable> mCompletionListeners = new ArrayList();
        private final Fragment mFragment;
        private final Type mType;

        /* access modifiers changed from: package-private */
        public enum Type {
            ADD,
            REMOVE,
            SHOW,
            HIDE
        }

        Operation(Type type, Fragment fragment, CancellationSignal cancellationSignal) {
            this.mType = type;
            this.mFragment = fragment;
            this.mCancellationSignal = cancellationSignal;
        }

        public final Type getType() {
            return this.mType;
        }

        public final Fragment getFragment() {
            return this.mFragment;
        }

        public final CancellationSignal getCancellationSignal() {
            return this.mCancellationSignal;
        }

        /* access modifiers changed from: package-private */
        public final void addCompletionListener(Runnable runnable) {
            this.mCompletionListeners.add(runnable);
        }

        public void complete() {
            for (Runnable runnable : this.mCompletionListeners) {
                runnable.run();
            }
        }
    }

    /* access modifiers changed from: private */
    public static class FragmentStateManagerOperation extends Operation {
        private final FragmentStateManager mFragmentStateManager;

        FragmentStateManagerOperation(Operation.Type type, FragmentStateManager fragmentStateManager, CancellationSignal cancellationSignal) {
            super(type, fragmentStateManager.getFragment(), cancellationSignal);
            this.mFragmentStateManager = fragmentStateManager;
        }

        @Override // androidx.fragment.app.SpecialEffectsController.Operation
        public void complete() {
            super.complete();
            this.mFragmentStateManager.moveToExpectedState();
        }
    }
}
