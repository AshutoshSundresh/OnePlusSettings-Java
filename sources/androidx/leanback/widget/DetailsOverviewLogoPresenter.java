package androidx.leanback.widget;

import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import androidx.leanback.widget.Presenter;

public class DetailsOverviewLogoPresenter extends Presenter {

    public static class ViewHolder extends Presenter.ViewHolder {
    }

    public abstract boolean isBoundToImage(ViewHolder viewHolder, DetailsOverviewRow detailsOverviewRow);

    public abstract void setContext(ViewHolder viewHolder, FullWidthDetailsOverviewRowPresenter.ViewHolder viewHolder2, FullWidthDetailsOverviewRowPresenter fullWidthDetailsOverviewRowPresenter);
}
