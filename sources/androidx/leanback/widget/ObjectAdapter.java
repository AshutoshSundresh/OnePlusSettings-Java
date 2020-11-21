package androidx.leanback.widget;

import android.database.Observable;

public abstract class ObjectAdapter {
    private boolean mHasStableIds;
    private final DataObservable mObservable = new DataObservable();
    private PresenterSelector mPresenterSelector;

    public abstract Object get(int i);

    public long getId(int i) {
        return -1;
    }

    public boolean isImmediateNotifySupported() {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onPresenterSelectorChanged() {
    }

    public abstract int size();

    public static abstract class DataObserver {
        public void onChanged() {
        }

        public void onItemRangeChanged(int i, int i2) {
            onChanged();
        }

        public void onItemRangeInserted(int i, int i2) {
            onChanged();
        }

        public void onItemRangeRemoved(int i, int i2) {
            onChanged();
        }
    }

    /* access modifiers changed from: private */
    public static final class DataObservable extends Observable<DataObserver> {
        DataObservable() {
        }

        public void notifyChanged() {
            for (int size = ((Observable) this).mObservers.size() - 1; size >= 0; size--) {
                ((DataObserver) ((Observable) this).mObservers.get(size)).onChanged();
            }
        }

        public void notifyItemRangeChanged(int i, int i2) {
            for (int size = ((Observable) this).mObservers.size() - 1; size >= 0; size--) {
                ((DataObserver) ((Observable) this).mObservers.get(size)).onItemRangeChanged(i, i2);
            }
        }

        public void notifyItemRangeInserted(int i, int i2) {
            for (int size = ((Observable) this).mObservers.size() - 1; size >= 0; size--) {
                ((DataObserver) ((Observable) this).mObservers.get(size)).onItemRangeInserted(i, i2);
            }
        }

        public void notifyItemRangeRemoved(int i, int i2) {
            for (int size = ((Observable) this).mObservers.size() - 1; size >= 0; size--) {
                ((DataObserver) ((Observable) this).mObservers.get(size)).onItemRangeRemoved(i, i2);
            }
        }
    }

    public ObjectAdapter(PresenterSelector presenterSelector) {
        setPresenterSelector(presenterSelector);
    }

    public ObjectAdapter() {
    }

    public final void setPresenterSelector(PresenterSelector presenterSelector) {
        if (presenterSelector != null) {
            boolean z = true;
            boolean z2 = this.mPresenterSelector != null;
            if (!z2 || this.mPresenterSelector == presenterSelector) {
                z = false;
            }
            this.mPresenterSelector = presenterSelector;
            if (z) {
                onPresenterSelectorChanged();
            }
            if (z2) {
                notifyChanged();
                return;
            }
            return;
        }
        throw new IllegalArgumentException("Presenter selector must not be null");
    }

    public final PresenterSelector getPresenterSelector() {
        return this.mPresenterSelector;
    }

    public final void registerObserver(DataObserver dataObserver) {
        this.mObservable.registerObserver(dataObserver);
    }

    public final void unregisterObserver(DataObserver dataObserver) {
        this.mObservable.unregisterObserver(dataObserver);
    }

    public final void notifyItemRangeChanged(int i, int i2) {
        this.mObservable.notifyItemRangeChanged(i, i2);
    }

    /* access modifiers changed from: protected */
    public final void notifyItemRangeInserted(int i, int i2) {
        this.mObservable.notifyItemRangeInserted(i, i2);
    }

    /* access modifiers changed from: protected */
    public final void notifyItemRangeRemoved(int i, int i2) {
        this.mObservable.notifyItemRangeRemoved(i, i2);
    }

    /* access modifiers changed from: protected */
    public final void notifyChanged() {
        this.mObservable.notifyChanged();
    }

    public final boolean hasStableIds() {
        return this.mHasStableIds;
    }
}
