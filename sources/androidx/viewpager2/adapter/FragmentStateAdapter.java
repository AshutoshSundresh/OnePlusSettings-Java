package androidx.viewpager2.adapter;

import android.os.Handler;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

public abstract class FragmentStateAdapter extends RecyclerView.Adapter<FragmentViewHolder> implements StatefulAdapter {
    /* access modifiers changed from: package-private */
    public abstract void placeFragmentInViewHolder(FragmentViewHolder fragmentViewHolder);

    /* access modifiers changed from: package-private */
    public abstract boolean shouldDelayFragmentTransactions();

    /* renamed from: androidx.viewpager2.adapter.FragmentStateAdapter$2  reason: invalid class name */
    class AnonymousClass2 implements LifecycleEventObserver {
        final /* synthetic */ FragmentStateAdapter this$0;
        final /* synthetic */ FragmentViewHolder val$holder;

        @Override // androidx.lifecycle.LifecycleEventObserver
        public void onStateChanged(LifecycleOwner lifecycleOwner, Lifecycle.Event event) {
            if (!this.this$0.shouldDelayFragmentTransactions()) {
                lifecycleOwner.getLifecycle().removeObserver(this);
                if (ViewCompat.isAttachedToWindow(this.val$holder.getContainer())) {
                    this.this$0.placeFragmentInViewHolder(this.val$holder);
                }
            }
        }
    }

    /* renamed from: androidx.viewpager2.adapter.FragmentStateAdapter$5  reason: invalid class name */
    class AnonymousClass5 implements LifecycleEventObserver {
        final /* synthetic */ Handler val$handler;
        final /* synthetic */ Runnable val$runnable;

        @Override // androidx.lifecycle.LifecycleEventObserver
        public void onStateChanged(LifecycleOwner lifecycleOwner, Lifecycle.Event event) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                this.val$handler.removeCallbacks(this.val$runnable);
                lifecycleOwner.getLifecycle().removeObserver(this);
            }
        }
    }

    class FragmentMaxLifecycleEnforcer {
        /* access modifiers changed from: package-private */
        public abstract void updateFragmentMaxLifecycle(boolean z);

        /* renamed from: androidx.viewpager2.adapter.FragmentStateAdapter$FragmentMaxLifecycleEnforcer$3  reason: invalid class name */
        class AnonymousClass3 implements LifecycleEventObserver {
            final /* synthetic */ FragmentMaxLifecycleEnforcer this$1;

            @Override // androidx.lifecycle.LifecycleEventObserver
            public void onStateChanged(LifecycleOwner lifecycleOwner, Lifecycle.Event event) {
                this.this$1.updateFragmentMaxLifecycle(false);
            }
        }
    }
}
