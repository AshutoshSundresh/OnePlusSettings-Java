package androidx.leanback.widget;

import android.view.ViewGroup;
import androidx.leanback.widget.Presenter;

public class VerticalGridPresenter extends Presenter {

    public static class ViewHolder extends Presenter.ViewHolder {
        public abstract VerticalGridView getGridView();
    }

    @Override // androidx.leanback.widget.Presenter
    public final ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        throw null;
    }

    public abstract void setEntranceTransitionState(ViewHolder viewHolder, boolean z);
}
