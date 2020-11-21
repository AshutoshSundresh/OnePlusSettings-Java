package androidx.leanback.widget;

public abstract class PresenterSelector {
    public abstract Presenter getPresenter(Object obj);

    public Presenter[] getPresenters() {
        return null;
    }
}
