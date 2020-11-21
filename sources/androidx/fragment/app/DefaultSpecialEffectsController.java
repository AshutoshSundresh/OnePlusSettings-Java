package androidx.fragment.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import androidx.core.os.CancellationSignal;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewGroupCompat;
import androidx.fragment.app.FragmentAnim;
import androidx.fragment.app.SpecialEffectsController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class DefaultSpecialEffectsController extends SpecialEffectsController {
    private final HashMap<SpecialEffectsController.Operation, HashSet<CancellationSignal>> mRunningOperations = new HashMap<>();

    DefaultSpecialEffectsController(ViewGroup viewGroup) {
        super(viewGroup);
    }

    private void addCancellationSignal(SpecialEffectsController.Operation operation, CancellationSignal cancellationSignal) {
        if (this.mRunningOperations.get(operation) == null) {
            this.mRunningOperations.put(operation, new HashSet<>());
        }
        this.mRunningOperations.get(operation).add(cancellationSignal);
    }

    /* access modifiers changed from: package-private */
    public void removeCancellationSignal(SpecialEffectsController.Operation operation, CancellationSignal cancellationSignal) {
        HashSet<CancellationSignal> hashSet = this.mRunningOperations.get(operation);
        if (hashSet != null && hashSet.remove(cancellationSignal) && hashSet.isEmpty()) {
            this.mRunningOperations.remove(operation);
            operation.complete();
        }
    }

    /* access modifiers changed from: package-private */
    public void cancelAllSpecialEffects(SpecialEffectsController.Operation operation) {
        HashSet<CancellationSignal> remove = this.mRunningOperations.remove(operation);
        if (remove != null) {
            Iterator<CancellationSignal> it = remove.iterator();
            while (it.hasNext()) {
                it.next().cancel();
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: androidx.fragment.app.DefaultSpecialEffectsController$8  reason: invalid class name */
    public static /* synthetic */ class AnonymousClass8 {
        static final /* synthetic */ int[] $SwitchMap$androidx$fragment$app$SpecialEffectsController$Operation$Type;

        /* JADX WARNING: Can't wrap try/catch for region: R(8:0|1|2|3|4|5|6|(3:7|8|10)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        static {
            /*
                androidx.fragment.app.SpecialEffectsController$Operation$Type[] r0 = androidx.fragment.app.SpecialEffectsController.Operation.Type.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                androidx.fragment.app.DefaultSpecialEffectsController.AnonymousClass8.$SwitchMap$androidx$fragment$app$SpecialEffectsController$Operation$Type = r0
                androidx.fragment.app.SpecialEffectsController$Operation$Type r1 = androidx.fragment.app.SpecialEffectsController.Operation.Type.HIDE     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = androidx.fragment.app.DefaultSpecialEffectsController.AnonymousClass8.$SwitchMap$androidx$fragment$app$SpecialEffectsController$Operation$Type     // Catch:{ NoSuchFieldError -> 0x001d }
                androidx.fragment.app.SpecialEffectsController$Operation$Type r1 = androidx.fragment.app.SpecialEffectsController.Operation.Type.REMOVE     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = androidx.fragment.app.DefaultSpecialEffectsController.AnonymousClass8.$SwitchMap$androidx$fragment$app$SpecialEffectsController$Operation$Type     // Catch:{ NoSuchFieldError -> 0x0028 }
                androidx.fragment.app.SpecialEffectsController$Operation$Type r1 = androidx.fragment.app.SpecialEffectsController.Operation.Type.SHOW     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = androidx.fragment.app.DefaultSpecialEffectsController.AnonymousClass8.$SwitchMap$androidx$fragment$app$SpecialEffectsController$Operation$Type     // Catch:{ NoSuchFieldError -> 0x0033 }
                androidx.fragment.app.SpecialEffectsController$Operation$Type r1 = androidx.fragment.app.SpecialEffectsController.Operation.Type.ADD     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: androidx.fragment.app.DefaultSpecialEffectsController.AnonymousClass8.<clinit>():void");
        }
    }

    /* access modifiers changed from: package-private */
    @Override // androidx.fragment.app.SpecialEffectsController
    public void executeOperations(List<SpecialEffectsController.Operation> list, boolean z) {
        SpecialEffectsController.Operation operation = null;
        SpecialEffectsController.Operation operation2 = null;
        for (SpecialEffectsController.Operation operation3 : list) {
            int i = AnonymousClass8.$SwitchMap$androidx$fragment$app$SpecialEffectsController$Operation$Type[operation3.getType().ordinal()];
            if (i == 1 || i == 2) {
                if (operation == null) {
                    operation = operation3;
                }
            } else if (i == 3 || i == 4) {
                operation2 = operation3;
            }
        }
        ArrayList<AnimationInfo> arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        final ArrayList<SpecialEffectsController.Operation> arrayList3 = new ArrayList(list);
        for (final SpecialEffectsController.Operation operation4 : list) {
            CancellationSignal cancellationSignal = new CancellationSignal();
            addCancellationSignal(operation4, cancellationSignal);
            arrayList.add(new AnimationInfo(operation4, cancellationSignal));
            CancellationSignal cancellationSignal2 = new CancellationSignal();
            addCancellationSignal(operation4, cancellationSignal2);
            boolean z2 = false;
            if (z) {
                if (operation4 != operation) {
                    arrayList2.add(new TransitionInfo(operation4, cancellationSignal2, z, z2));
                    operation4.addCompletionListener(new Runnable() {
                        /* class androidx.fragment.app.DefaultSpecialEffectsController.AnonymousClass1 */

                        public void run() {
                            if (arrayList3.contains(operation4)) {
                                arrayList3.remove(operation4);
                                DefaultSpecialEffectsController.this.applyContainerChanges(operation4);
                            }
                        }
                    });
                    operation4.getCancellationSignal().setOnCancelListener(new CancellationSignal.OnCancelListener() {
                        /* class androidx.fragment.app.DefaultSpecialEffectsController.AnonymousClass2 */

                        @Override // androidx.core.os.CancellationSignal.OnCancelListener
                        public void onCancel() {
                            DefaultSpecialEffectsController.this.cancelAllSpecialEffects(operation4);
                        }
                    });
                }
            } else if (operation4 != operation2) {
                arrayList2.add(new TransitionInfo(operation4, cancellationSignal2, z, z2));
                operation4.addCompletionListener(new Runnable() {
                    /* class androidx.fragment.app.DefaultSpecialEffectsController.AnonymousClass1 */

                    public void run() {
                        if (arrayList3.contains(operation4)) {
                            arrayList3.remove(operation4);
                            DefaultSpecialEffectsController.this.applyContainerChanges(operation4);
                        }
                    }
                });
                operation4.getCancellationSignal().setOnCancelListener(new CancellationSignal.OnCancelListener() {
                    /* class androidx.fragment.app.DefaultSpecialEffectsController.AnonymousClass2 */

                    @Override // androidx.core.os.CancellationSignal.OnCancelListener
                    public void onCancel() {
                        DefaultSpecialEffectsController.this.cancelAllSpecialEffects(operation4);
                    }
                });
            }
            z2 = true;
            arrayList2.add(new TransitionInfo(operation4, cancellationSignal2, z, z2));
            operation4.addCompletionListener(new Runnable() {
                /* class androidx.fragment.app.DefaultSpecialEffectsController.AnonymousClass1 */

                public void run() {
                    if (arrayList3.contains(operation4)) {
                        arrayList3.remove(operation4);
                        DefaultSpecialEffectsController.this.applyContainerChanges(operation4);
                    }
                }
            });
            operation4.getCancellationSignal().setOnCancelListener(new CancellationSignal.OnCancelListener() {
                /* class androidx.fragment.app.DefaultSpecialEffectsController.AnonymousClass2 */

                @Override // androidx.core.os.CancellationSignal.OnCancelListener
                public void onCancel() {
                    DefaultSpecialEffectsController.this.cancelAllSpecialEffects(operation4);
                }
            });
        }
        startTransitions(arrayList2, z, operation, operation2);
        for (AnimationInfo animationInfo : arrayList) {
            startAnimation(animationInfo.getOperation(), animationInfo.getSignal());
        }
        for (SpecialEffectsController.Operation operation5 : arrayList3) {
            applyContainerChanges(operation5);
        }
        arrayList3.clear();
    }

    private void startAnimation(final SpecialEffectsController.Operation operation, final CancellationSignal cancellationSignal) {
        Animation animation;
        final ViewGroup container = getContainer();
        Context context = container.getContext();
        Fragment fragment = operation.getFragment();
        final View view = fragment.mView;
        FragmentAnim.AnimationOrAnimator loadAnimation = FragmentAnim.loadAnimation(context, fragment, operation.getType() == SpecialEffectsController.Operation.Type.ADD || operation.getType() == SpecialEffectsController.Operation.Type.SHOW);
        if (loadAnimation == null) {
            removeCancellationSignal(operation, cancellationSignal);
            return;
        }
        container.startViewTransition(view);
        if (loadAnimation.animation != null) {
            if (operation.getType() == SpecialEffectsController.Operation.Type.ADD || operation.getType() == SpecialEffectsController.Operation.Type.SHOW) {
                animation = new FragmentAnim.EnterViewTransitionAnimation(loadAnimation.animation);
            } else {
                animation = new FragmentAnim.EndViewTransitionAnimation(loadAnimation.animation, container, view);
            }
            animation.setAnimationListener(new Animation.AnimationListener() {
                /* class androidx.fragment.app.DefaultSpecialEffectsController.AnonymousClass3 */

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    container.post(new Runnable() {
                        /* class androidx.fragment.app.DefaultSpecialEffectsController.AnonymousClass3.AnonymousClass1 */

                        public void run() {
                            AnonymousClass3 r0 = AnonymousClass3.this;
                            container.endViewTransition(view);
                            AnonymousClass3 r2 = AnonymousClass3.this;
                            DefaultSpecialEffectsController.this.removeCancellationSignal(operation, cancellationSignal);
                        }
                    });
                }
            });
            view.startAnimation(animation);
        } else {
            loadAnimation.animator.addListener(new AnimatorListenerAdapter() {
                /* class androidx.fragment.app.DefaultSpecialEffectsController.AnonymousClass4 */

                public void onAnimationEnd(Animator animator) {
                    container.endViewTransition(view);
                    DefaultSpecialEffectsController.this.removeCancellationSignal(operation, cancellationSignal);
                }
            });
            loadAnimation.animator.setTarget(view);
            loadAnimation.animator.start();
        }
        cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener(this) {
            /* class androidx.fragment.app.DefaultSpecialEffectsController.AnonymousClass5 */

            @Override // androidx.core.os.CancellationSignal.OnCancelListener
            public void onCancel() {
                view.clearAnimation();
            }
        });
    }

    /* JADX WARNING: Code restructure failed: missing block: B:47:0x01ad, code lost:
        r6 = (android.view.View) r7.get(r9.get(0));
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void startTransitions(java.util.List<androidx.fragment.app.DefaultSpecialEffectsController.TransitionInfo> r23, boolean r24, androidx.fragment.app.SpecialEffectsController.Operation r25, androidx.fragment.app.SpecialEffectsController.Operation r26) {
        /*
        // Method dump skipped, instructions count: 735
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.fragment.app.DefaultSpecialEffectsController.startTransitions(java.util.List, boolean, androidx.fragment.app.SpecialEffectsController$Operation, androidx.fragment.app.SpecialEffectsController$Operation):void");
    }

    /* access modifiers changed from: package-private */
    public void captureTransitioningViews(ArrayList<View> arrayList, View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            if (ViewGroupCompat.isTransitionGroup(viewGroup)) {
                arrayList.add(viewGroup);
                return;
            }
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (childAt.getVisibility() == 0) {
                    captureTransitioningViews(arrayList, childAt);
                }
            }
            return;
        }
        arrayList.add(view);
    }

    /* access modifiers changed from: package-private */
    public void findNamedViews(Map<String, View> map, View view) {
        String transitionName = ViewCompat.getTransitionName(view);
        if (transitionName != null) {
            map.put(transitionName, view);
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (childAt.getVisibility() == 0) {
                    findNamedViews(map, childAt);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void applyContainerChanges(SpecialEffectsController.Operation operation) {
        View view = operation.getFragment().mView;
        int i = AnonymousClass8.$SwitchMap$androidx$fragment$app$SpecialEffectsController$Operation$Type[operation.getType().ordinal()];
        if (i == 1) {
            view.setVisibility(8);
        } else if (i == 2) {
            getContainer().removeView(view);
        } else if (i == 3 || i == 4) {
            view.setVisibility(0);
        }
    }

    private static class AnimationInfo {
        private final SpecialEffectsController.Operation mOperation;
        private final CancellationSignal mSignal;

        AnimationInfo(SpecialEffectsController.Operation operation, CancellationSignal cancellationSignal) {
            this.mOperation = operation;
            this.mSignal = cancellationSignal;
        }

        /* access modifiers changed from: package-private */
        public SpecialEffectsController.Operation getOperation() {
            return this.mOperation;
        }

        /* access modifiers changed from: package-private */
        public CancellationSignal getSignal() {
            return this.mSignal;
        }
    }

    /* access modifiers changed from: private */
    public static class TransitionInfo {
        private final SpecialEffectsController.Operation mOperation;
        private final boolean mOverlapAllowed;
        private final Object mSharedElementTransition;
        private final CancellationSignal mSignal;
        private final Object mTransition;

        TransitionInfo(SpecialEffectsController.Operation operation, CancellationSignal cancellationSignal, boolean z, boolean z2) {
            Object obj;
            boolean z3;
            Object obj2;
            this.mOperation = operation;
            this.mSignal = cancellationSignal;
            if (operation.getType() == SpecialEffectsController.Operation.Type.ADD || operation.getType() == SpecialEffectsController.Operation.Type.SHOW) {
                if (z) {
                    obj = operation.getFragment().getReenterTransition();
                } else {
                    obj = operation.getFragment().getEnterTransition();
                }
                this.mTransition = obj;
                if (z) {
                    z3 = operation.getFragment().getAllowEnterTransitionOverlap();
                } else {
                    z3 = operation.getFragment().getAllowReturnTransitionOverlap();
                }
                this.mOverlapAllowed = z3;
            } else {
                if (z) {
                    obj2 = operation.getFragment().getReturnTransition();
                } else {
                    obj2 = operation.getFragment().getExitTransition();
                }
                this.mTransition = obj2;
                this.mOverlapAllowed = true;
            }
            if (!z2) {
                this.mSharedElementTransition = null;
            } else if (z) {
                this.mSharedElementTransition = operation.getFragment().getSharedElementReturnTransition();
            } else {
                this.mSharedElementTransition = operation.getFragment().getSharedElementEnterTransition();
            }
        }

        /* access modifiers changed from: package-private */
        public SpecialEffectsController.Operation getOperation() {
            return this.mOperation;
        }

        /* access modifiers changed from: package-private */
        public CancellationSignal getSignal() {
            return this.mSignal;
        }

        /* access modifiers changed from: package-private */
        public Object getTransition() {
            return this.mTransition;
        }

        /* access modifiers changed from: package-private */
        public boolean isOverlapAllowed() {
            return this.mOverlapAllowed;
        }

        public boolean hasSharedElementTransition() {
            return this.mSharedElementTransition != null;
        }

        public Object getSharedElementTransition() {
            return this.mSharedElementTransition;
        }

        /* access modifiers changed from: package-private */
        public FragmentTransitionImpl getHandlingImpl() {
            FragmentTransitionImpl handlingImpl = getHandlingImpl(this.mTransition);
            FragmentTransitionImpl handlingImpl2 = getHandlingImpl(this.mSharedElementTransition);
            if (handlingImpl == null || handlingImpl2 == null || handlingImpl == handlingImpl2) {
                return handlingImpl != null ? handlingImpl : handlingImpl2;
            }
            throw new IllegalArgumentException("Mixing framework transitions and AndroidX transitions is not allowed. Fragment " + this.mOperation.getFragment() + " returned Transition " + this.mTransition + " which uses a different Transition  type than its shared element transition " + this.mSharedElementTransition);
        }

        private FragmentTransitionImpl getHandlingImpl(Object obj) {
            if (obj == null) {
                return null;
            }
            FragmentTransitionImpl fragmentTransitionImpl = FragmentTransition.PLATFORM_IMPL;
            if (fragmentTransitionImpl != null && fragmentTransitionImpl.canHandle(obj)) {
                return FragmentTransition.PLATFORM_IMPL;
            }
            FragmentTransitionImpl fragmentTransitionImpl2 = FragmentTransition.SUPPORT_IMPL;
            if (fragmentTransitionImpl2 != null && fragmentTransitionImpl2.canHandle(obj)) {
                return FragmentTransition.SUPPORT_IMPL;
            }
            throw new IllegalArgumentException("Transition " + obj + " for fragment " + this.mOperation.getFragment() + " is not a valid framework Transition or AndroidX Transition");
        }
    }
}
