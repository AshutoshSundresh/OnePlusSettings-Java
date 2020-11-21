package androidx.leanback.widget;

import java.util.ArrayList;
import java.util.List;

public class ArrayObjectAdapter extends ObjectAdapter {
    private final List<Object> mItems = new ArrayList();

    @Override // androidx.leanback.widget.ObjectAdapter
    public boolean isImmediateNotifySupported() {
        return true;
    }

    public ArrayObjectAdapter(PresenterSelector presenterSelector) {
        super(presenterSelector);
        new ArrayList();
    }

    public ArrayObjectAdapter() {
        new ArrayList();
    }

    @Override // androidx.leanback.widget.ObjectAdapter
    public int size() {
        return this.mItems.size();
    }

    @Override // androidx.leanback.widget.ObjectAdapter
    public Object get(int i) {
        return this.mItems.get(i);
    }

    public void add(Object obj) {
        add(this.mItems.size(), obj);
    }

    public void add(int i, Object obj) {
        this.mItems.add(i, obj);
        notifyItemRangeInserted(i, 1);
    }

    public void replace(int i, Object obj) {
        this.mItems.set(i, obj);
        notifyItemRangeChanged(i, 1);
    }
}
