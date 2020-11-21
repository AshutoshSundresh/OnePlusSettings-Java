package androidx.leanback.widget;

import android.view.View;
import android.view.ViewGroup;
import androidx.collection.ArrayMap;
import java.util.List;
import java.util.Map;

public abstract class Presenter implements FacetProvider {
    private Map<Class<?>, Object> mFacets;

    public abstract void onBindViewHolder(ViewHolder viewHolder, Object obj);

    public abstract ViewHolder onCreateViewHolder(ViewGroup viewGroup);

    public abstract void onUnbindViewHolder(ViewHolder viewHolder);

    public void onViewAttachedToWindow(ViewHolder viewHolder) {
    }

    public static class ViewHolder implements FacetProvider {
        private Map<Class<?>, Object> mFacets;
        public final View view;

        public ViewHolder(View view2) {
            this.view = view2;
        }

        @Override // androidx.leanback.widget.FacetProvider
        public final Object getFacet(Class<?> cls) {
            Map<Class<?>, Object> map = this.mFacets;
            if (map == null) {
                return null;
            }
            return map.get(cls);
        }
    }

    public void onBindViewHolder(ViewHolder viewHolder, Object obj, List<Object> list) {
        onBindViewHolder(viewHolder, obj);
    }

    public void onViewDetachedFromWindow(ViewHolder viewHolder) {
        cancelAnimationsRecursive(viewHolder.view);
    }

    protected static void cancelAnimationsRecursive(View view) {
        if (view != null && view.hasTransientState()) {
            view.animate().cancel();
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                int childCount = viewGroup.getChildCount();
                int i = 0;
                while (view.hasTransientState() && i < childCount) {
                    cancelAnimationsRecursive(viewGroup.getChildAt(i));
                    i++;
                }
            }
        }
    }

    public void setOnClickListener(ViewHolder viewHolder, View.OnClickListener onClickListener) {
        viewHolder.view.setOnClickListener(onClickListener);
    }

    @Override // androidx.leanback.widget.FacetProvider
    public final Object getFacet(Class<?> cls) {
        Map<Class<?>, Object> map = this.mFacets;
        if (map == null) {
            return null;
        }
        return map.get(cls);
    }

    public final void setFacet(Class<?> cls, Object obj) {
        if (this.mFacets == null) {
            this.mFacets = new ArrayMap();
        }
        this.mFacets.put(cls, obj);
    }
}
