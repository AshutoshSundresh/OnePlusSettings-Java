package androidx.leanback.widget;

import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.RowPresenter;

public interface BaseOnItemViewClickedListener<T> {
    void onItemClicked(Presenter.ViewHolder viewHolder, Object obj, RowPresenter.ViewHolder viewHolder2, T t);
}
