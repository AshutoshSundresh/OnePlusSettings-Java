package androidx.leanback.widget;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class DetailsOverviewRow extends Row {
    private ObjectAdapter mActionsAdapter;
    private Object mItem;
    private ArrayList<WeakReference<Listener>> mListeners;

    public static class Listener {
    }

    /* access modifiers changed from: package-private */
    public final void addListener(Listener listener) {
        if (this.mListeners == null) {
            this.mListeners = new ArrayList<>();
        } else {
            int i = 0;
            while (i < this.mListeners.size()) {
                Listener listener2 = this.mListeners.get(i).get();
                if (listener2 == null) {
                    this.mListeners.remove(i);
                } else if (listener2 != listener) {
                    i++;
                } else {
                    return;
                }
            }
        }
        this.mListeners.add(new WeakReference<>(listener));
    }

    /* access modifiers changed from: package-private */
    public final void removeListener(Listener listener) {
        if (this.mListeners != null) {
            int i = 0;
            while (i < this.mListeners.size()) {
                Listener listener2 = this.mListeners.get(i).get();
                if (listener2 == null) {
                    this.mListeners.remove(i);
                } else if (listener2 == listener) {
                    this.mListeners.remove(i);
                    return;
                } else {
                    i++;
                }
            }
        }
    }

    public final Object getItem() {
        return this.mItem;
    }

    public final ObjectAdapter getActionsAdapter() {
        return this.mActionsAdapter;
    }
}
